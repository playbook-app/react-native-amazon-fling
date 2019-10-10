package com.vidangel.reactnative.amazonfling;

import android.util.Log;

import com.amazon.whisperplay.fling.media.controller.DiscoveryController;
import com.amazon.whisperplay.fling.media.controller.RemoteMediaPlayer;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class AmazonFlingModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public AmazonFlingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
//        mController = new DiscoveryController(reactContext);
//        mController.start("amzn.thin.pl", mDiscovery);

    }
    @ReactMethod
    public void search() {
        Log.e("jedsearch", "jedsearch");
        DiscoveryController mController;
        DiscoveryController.IDiscoveryListener mDiscovery = new DiscoveryController.IDiscoveryListener() {
            @Override
            public void playerDiscovered(RemoteMediaPlayer player) {
                Log.e("jedsearch", "playerDiscovered");
                Log.e("jed", "playerDiscovered" + player.toString());
                //add media player to the application’s player list.
            }
            @Override
            public void playerLost(RemoteMediaPlayer player) {
                Log.e("jedsearch", "playerLost");
                Log.e("jed", "playerLost" + player.toString());
                //remove media player from the application’s player list.
            }
            @Override
            public void discoveryFailure() {
                Log.e("jed", "discoveryFailure");
            }
        };
        Log.e("jedsearch", "jedsearch1");
        mController = new DiscoveryController(reactContext);
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


}
