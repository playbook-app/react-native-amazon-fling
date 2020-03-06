package com.vidangel.reactnative.amazonfling;

import android.util.Log;

import com.amazon.whisperplay.fling.media.controller.DiscoveryController;
import com.amazon.whisperplay.fling.media.controller.RemoteMediaPlayer;
import com.amazon.whisperplay.fling.media.service.CustomMediaPlayer;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    }

    @ReactMethod
    public void startSearch() {
        Log.v(TAG, "startSearch");
        mController.start("amzn.thin.pl", mDiscovery);
    }

    @ReactMethod
    public void stopSearch() {
        Log.v(TAG, "stopSearch");
        if (mController != null){
            mController.stop();
        }

    }

    @Override
    public String getName() {
        return "AmazonFling";
    }

    public void updateDeviceList(RemoteMediaPlayer device) {
        Log.v(TAG, "updateDeviceList");
        if (mDeviceList.contains(device)) {
            mDeviceList.remove(device);
        }
        mDeviceList.add(device);
        JSONArray arrOfDevices = new JSONArray();
        for (RemoteMediaPlayer dev : mDeviceList) {
            JSONObject json = new JSONObject();
            try {
                json.put("name", dev.getName());
                json.put("uuid", dev.getUniqueIdentifier());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            arrOfDevices.put(json);
        }


        WritableMap params = Arguments.createMap();
        params.putString("devices", arrOfDevices.toString());
        Log.v(TAG, arrOfDevices.toString());
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

    @ReactMethod
    private void doSeek(final String targetUuid, final String position) {
        RemoteMediaPlayer target = getRemoteMediaPlayerFromUUID(targetUuid);
        if (target != null) {
            Log.i(TAG, "try doSeek...");
            long positionLong = Long.parseLong(position);
            target.seek(CustomMediaPlayer.PlayerSeekMode.Absolute, positionLong).getAsync(new ErrorResultHandler("doStop", "Error Stopping"));
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
        //do nothing
//        stopSearch();
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
