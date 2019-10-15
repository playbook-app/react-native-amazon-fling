package com.vidangel.reactnative.amazonfling;

import android.util.Log;

import com.amazon.whisperplay.fling.media.controller.DiscoveryController;
import com.amazon.whisperplay.fling.media.controller.RemoteMediaPlayer;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AmazonFlingModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private final ReactApplicationContext reactContext;
    private static final long MONITOR_INTERVAL = 1000L;
    private List<RemoteMediaPlayer> mDeviceList = new LinkedList<>();

    private DiscoveryController mController;

    final String TAG = "AmazonFlingModule";

    private DiscoveryController.IDiscoveryListener mDiscovery = new DiscoveryController.IDiscoveryListener() {
        @Override
        public void playerDiscovered(RemoteMediaPlayer player) {
            Log.v(TAG, "playerDiscovered" + player.toString());
//                fling(player, "https://video.thechosen.tv/The_Chosen_S01E01_patch50219.m3u8", "Episode 1");
            //add media player to the application’s player list.
            updateDeviceList(player);
        }

        @Override
        public void playerLost(RemoteMediaPlayer player) {
            Log.v(TAG, "jed2");
            //remove media player from the application’s player list.
        }

        @Override
        public void discoveryFailure() {
            Log.v(TAG, "jed3");
        }
    };

    public AmazonFlingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Log.v(TAG, "AmazonFlingModule5599");
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
        mController = new DiscoveryController(reactContext.getBaseContext());
//        mController = new DiscoveryController(reactContext);
//        mController.start("amzn.thin.pl", mDiscovery);

    }

    @ReactMethod
    public void startSearch() {
        Log.v(TAG, "startSearch");
        mController.start("amzn.thin.pl", mDiscovery);
    }

    @ReactMethod
    public void stopSearch() {
        Log.v(TAG, "stopSearch");
        mController.stop();
    }

    @Override
    public String getName() {
        return "AmazonFling";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        Log.v(TAG, "sampleMethod");
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    public void updateDeviceList(RemoteMediaPlayer device) {
        Log.v(TAG, "updateDeviceList");
        if (mDeviceList.contains(device)) {
            mDeviceList.remove(device);
        }
        mDeviceList.add(device);
        String jsonInString = new Gson().toJson(mDeviceList);
        WritableMap params = Arguments.createMap();
        params.putString("devices", jsonInString);
        Log.v(TAG, jsonInString);
        sendEvent("device_list", params);
    }

    private void sendEvent(String eventName, WritableMap params) {
        this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    private RemoteMediaPlayer getRemoteMediaPlayerFromUUID(String targetUuid) {
        RemoteMediaPlayer target = null;
        for (RemoteMediaPlayer device : mDeviceList) {
            Log.v(TAG, device.toString());
            if (device.getUniqueIdentifier().equals(targetUuid)) {
                target = device;
                Log.v(TAG, device.toString());
            }
        }
        return target;
    }

    @ReactMethod
    private void fling(final String targetUuid, final String name, final String title) {
        RemoteMediaPlayer target = getRemoteMediaPlayerFromUUID(targetUuid);

        Log.v(TAG, target.toString());
        if (target != null) {
            Log.i(TAG, "try setPositionUpdateInterval: " + MONITOR_INTERVAL);
            target.setPositionUpdateInterval(MONITOR_INTERVAL).getAsync(
                    new ErrorResultHandler("setPositionUpdateInterval",
                            "Error attempting set update interval, ignoring", true));
            Log.i(TAG, "try setMediaSource: url - " + name + " title - " + title);
            target.setMediaSource(name, title, true, false).getAsync(
                    new ErrorResultHandler("setMediaSource", "Error attempting to Play:", true));
        }
    }

    @ReactMethod
    private void doPlay(final String targetUuid) {
        RemoteMediaPlayer target = getRemoteMediaPlayerFromUUID(targetUuid);
        if (target != null) {
            Log.i(TAG, "try doPlay...");
            target.play().getAsync(new ErrorResultHandler("doPlay", "Error Playing"));
        }
    }

    @ReactMethod
    private void doPause(final String targetUuid) {
        RemoteMediaPlayer target = getRemoteMediaPlayerFromUUID(targetUuid);
        if (target != null) {
            Log.i(TAG, "try doPause...");
            target.pause().getAsync(new ErrorResultHandler("doPause", "Error Pausing"));
        }
    }

    @ReactMethod
    private void doStop(final String targetUuid) {
        RemoteMediaPlayer target = getRemoteMediaPlayerFromUUID(targetUuid);
        if (target != null) {
            Log.i(TAG, "try doStop...");
            target.stop().getAsync(new ErrorResultHandler("doStop", "Error Stopping"));
        }
    }

    private class ErrorResultHandler implements RemoteMediaPlayer.FutureListener<Void> {
        private String mCommand;
        private String mMsg;
        private boolean mExtend;

        ErrorResultHandler(String command, String msg) {
            this(command, msg, false);
        }

        ErrorResultHandler(String command, String msg, boolean extend) {
            mCommand = command;
            mMsg = msg;
            mExtend = extend;
        }

        @Override
        public void futureIsNow(Future<Void> result) {
            try {
                result.get();
//                showToast(mCommand);
//                mErrorCount = 0;
                Log.i(TAG, mCommand + ": successful");
            } catch (ExecutionException e) {
//                handleFailure(e.getCause(), mMsg, mExtend);
            } catch (Exception e) {
//                handleFailure(e, mMsg, mExtend);
            }
        }
    }

    @Override
    public void onHostDestroy() {
        stopSearch();
    }

    @Override
    public void onHostResume() {
        //do nothing

    }

    @Override
    public void onHostPause() {
        //do nothing
    }
}
