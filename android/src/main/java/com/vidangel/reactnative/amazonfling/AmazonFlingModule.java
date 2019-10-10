package com.vidangel.reactnative.amazonfling;

import android.util.Log;

import com.amazon.whisperplay.fling.media.controller.DiscoveryController;
import com.amazon.whisperplay.fling.media.controller.RemoteMediaPlayer;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class AmazonFlingModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private final ReactApplicationContext reactContext;

    public AmazonFlingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
        mController = new DiscoveryController(reactContext.getApplicationContext());
//        mController = new DiscoveryController(reactContext);
//        mController.start("amzn.thin.pl", mDiscovery);

    }

    private DiscoveryController mController;

    private DiscoveryController.IDiscoveryListener mDiscovery = new DiscoveryController.IDiscoveryListener() {
        @Override
        public void playerDiscovered(RemoteMediaPlayer player) {
            Log.e("jedtest", "jed1" + player.toString());
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

    @ReactMethod
    public void search() {

        Log.e("jedsearch", "jedsearch");

        Log.e("jedsearch", "jedsearch1");
        mController = new DiscoveryController(reactContext.getApplicationContext());
        Log.e("jedsearch", "jedsearch2");
        mController.start("amzn.thin.pl", mDiscovery);
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
        mController.start("amzn.thin.pl", mDiscovery);
    }

    @Override
    public void onHostPause() {
        mController.stop();
    }

    @Override
    public void onHostDestroy() {

    }
}
