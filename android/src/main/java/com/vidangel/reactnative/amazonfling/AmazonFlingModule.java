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

    private DiscoveryController.IDiscoveryListener mDiscovery = new DiscoveryController.IDiscoveryListener() {
        @Override
        public void playerDiscovered(RemoteMediaPlayer player) {
            Log.e("jedtest", "playerDiscovered" + player.toString());
//                fling(player, "https://video.thechosen.tv/The_Chosen_S01E01_patch50219.m3u8", "Episode 1");
            //add media player to the application’s player list.
            updateDeviceList(player);
        }

        @Override
        public void playerLost(RemoteMediaPlayer player) {
            Log.e("jedtest", "jed2");
            //remove media player from the application’s player list.
        }

        @Override
        public void discoveryFailure() {
            Log.e("jedtest", "jed3");
        }
    };

    public AmazonFlingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Log.e("jedsearch", "AmazonFlingModule5599");
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
        mController = new DiscoveryController(reactContext.getBaseContext());
//        mController = new DiscoveryController(reactContext);
//        mController.start("amzn.thin.pl", mDiscovery);

    }

    @ReactMethod
    public void startSearch() {
        Log.e("jedsearch55", "startSearch");
        mController.start("amzn.thin.pl", mDiscovery);
    }

    @ReactMethod
    public void stopSearch() {
        Log.e("jedsearch", "stopSearch");
        mController.stop();
    }

    @Override
    public String getName() {
        return "AmazonFling";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        Log.e("jedsearch", "sampleMethod");
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    @ReactMethod
    public void test() {
        Log.e("jedsearch", "test");
        // TODO: Implement some actually useful functionality
    }

    public void updateDeviceList(RemoteMediaPlayer device){
        Log.e("jedsearch55", "updateDeviceList");
        if (mDeviceList.contains(device)) {
            mDeviceList.remove(device);
        }
        mDeviceList.add(device);
        String jsonInString = new Gson().toJson(mDeviceList);
        WritableMap params = Arguments.createMap();
        params.putString("devices", jsonInString);
        Log.e("devices json", jsonInString);
        sendEvent("device_list", params);
    }

    private void sendEvent(String eventName, WritableMap params) {
        this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @ReactMethod
    private void fling(final String targetUuid, final String name, final String title) {
        RemoteMediaPlayer target = null;
        for (RemoteMediaPlayer device : mDeviceList) {
            Log.e("Jed", device.toString());
            if (device.getUniqueIdentifier().equals(targetUuid)){
                target = device;
                Log.e("Jed found device", device.toString());
            }
        }
        Log.e("jed fling device", target.toString());
        if (target != null){
            Log.i("jed", "try setPositionUpdateInterval: " + MONITOR_INTERVAL);
            target.setPositionUpdateInterval(MONITOR_INTERVAL).getAsync(
                    new ErrorResultHandler("setPositionUpdateInterval",
                            "Error attempting set update interval, ignoring", true));
            Log.i("jed", "try setMediaSource: url - " + name + " title - " + title);
            target.setMediaSource(name, title, true, false).getAsync(
                    new ErrorResultHandler("setMediaSource", "Error attempting to Play:", true));
        }
    }

//    private void showToast(final String msg) {
//        runOnUiThread(new Runnable() {
//            public void run() {
//                Toast toast = Toast.makeText(reactContext.getApplicationContext(), msg, Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        });
//    }

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
                Log.i("jed", mCommand + ": successful");
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
