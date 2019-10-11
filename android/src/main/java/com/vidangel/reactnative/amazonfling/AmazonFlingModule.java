package com.vidangel.reactnative.amazonfling;

import android.util.Log;
import android.widget.Toast;

import com.amazon.whisperplay.fling.media.controller.DiscoveryController;
import com.amazon.whisperplay.fling.media.controller.RemoteMediaPlayer;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class AmazonFlingModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private final ReactApplicationContext reactContext;
    private static final long MONITOR_INTERVAL = 1000L;

    private DiscoveryController mController;

    public AmazonFlingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Log.e("jedsearch", "AmazonFlingModule");
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
//        mController = new DiscoveryController(reactContext);
//        mController.start("amzn.thin.pl", mDiscovery);

    }

    @ReactMethod
    public void search() {

        Log.e("jedsearch", "jedsearch");

        Log.e("jedsearch", "jedsearch1");
//        mController = new DiscoveryController(reactContext.getApplicationContext());
        Log.e("jedsearch", "jedsearch2");
//        mController.start("amzn.thin.pl", mDiscovery);
    }

    @Override
    public String getName() {
        return "AmazonFling";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }


    @Override
    public void onHostResume() {
        Log.e("jedsearch", "onHostResume");
        mController = new DiscoveryController(reactContext.getBaseContext());
        DiscoveryController.IDiscoveryListener mDiscovery = new DiscoveryController.IDiscoveryListener() {
            @Override
            public void playerDiscovered(RemoteMediaPlayer player) {
                Log.e("jedtest", "jed1" + player.toString());
                fling(player, "https://video.thechosen.tv/The_Chosen_S01E01_patch50219.m3u8", "Episode 1");
                //add media player to the application’s player list.
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
        mController.start("amzn.thin.pl", mDiscovery);
    }

    private void fling(final RemoteMediaPlayer target, final String name, final String title) {
//        initializeFling(target);
        Log.i("jed", "try setPositionUpdateInterval: " + MONITOR_INTERVAL);
        target.setPositionUpdateInterval(MONITOR_INTERVAL).getAsync(
                new ErrorResultHandler("setPositionUpdateInterval",
                        "Error attempting set update interval, ignoring", true));
        Log.i("jed", "try setMediaSource: url - " + name + " title - " + title);
        target.setMediaSource(name, title, true, false).getAsync(
                new ErrorResultHandler("setMediaSource", "Error attempting to Play:", true));
        showToast("try Flinging...");
    }

    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(reactContext.getApplicationContext(), msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
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
                showToast(mCommand);
//                mErrorCount = 0;
                Log.i("jed", mCommand + ": successful");
            } catch(ExecutionException e) {
//                handleFailure(e.getCause(), mMsg, mExtend);
            } catch(Exception e) {
//                handleFailure(e, mMsg, mExtend);
            }
        }
    }

    @Override
    public void onHostPause() {
        Log.e("jedsearch", "onHostPause");
        mController.stop();
    }

    @Override
    public void onHostDestroy() {

    }
}
