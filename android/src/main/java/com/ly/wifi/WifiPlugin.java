package com.ly.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;

public class WifiPlugin implements FlutterPlugin, MethodCallHandler {
    private WifiDelegate delegate;
    private MethodChannel channel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "plugins.ly.com/wifi");
        WifiManager wifiManager = (WifiManager) flutterPluginBinding.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        delegate = new WifiDelegate((Activity) flutterPluginBinding.getApplicationContext(), wifiManager);
        // support Android O,listen network disconnect event
        // https://stackoverflow.com/questions/50462987/android-o-wifimanager-enablenetwork-cannot-work
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        flutterPluginBinding.getApplicationContext()
                .registerReceiver(delegate.networkReceiver, filter);
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        channel.setMethodCallHandler(null);
    }


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "ssid":
                delegate.getSSID(call, result);
                break;
            case "level":
                delegate.getLevel(call, result);
                break;
            case "is5GHz":
                delegate.getIs5GHz(call, result);
                break;
            case "ip":
                delegate.getIP(call, result);
                break;
            case "list":
                delegate.getWifiList(call, result);
                break;
            case "connection":
                delegate.connection(call, result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

}
