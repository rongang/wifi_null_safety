import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

enum WifiState { error, success, already }

class Wifi {
  static const MethodChannel _channel =
      const MethodChannel('plugins.ly.com/wifi');

  static Future<WifiResult> get currentWifi async {
    if (Platform.isAndroid)
      return WifiResult(
        await ssid,
        await level,
        await bssid,
        is5GHz: await is5GHz,
      );
    else if (Platform.isIOS)
      return WifiResult(
        await ssid,
        3,
        await bssid,
      );
    throw NoSuchMethodError;
  }

  static Future<String> get ssid async {
    return await _channel.invokeMethod('ssid');
  }

  static Future<bool> get is5GHz async {
    if (Platform.isAndroid) return await _channel.invokeMethod('is5GHz');
    throw NoSuchMethodError;
  }

  static Future<String> get bssid async {
    return await _channel.invokeMethod('bssid');
  }

  static Future<int> get level async {
    if (Platform.isAndroid) return await _channel.invokeMethod('level');
    throw NoSuchMethodError;
  }

  static Future<String> get ip async {
    return await _channel.invokeMethod('ip');
  }

  static Future<List<WifiResult>> list(String key) async {
    final Map<String, dynamic> params = {
      'key': key,
    };
    var results = await _channel.invokeMethod('list', params);
    List<WifiResult> resultList = [];
    for (int i = 0; i < results.length; i++) {
      resultList.add(WifiResult(
          results[i]['ssid'], results[i]['level'], results[i]['bssid'],
          is5GHz: results[i]['is5GHz']));
    }
    return resultList;
  }

  static Future<WifiState> connection(String ssid, String password) async {
    final Map<String, dynamic> params = {
      'ssid': ssid,
      'password': password,
    };
    int state = await _channel.invokeMethod('connection', params);
    switch (state) {
      case 0:
        return WifiState.error;
      case 1:
        return WifiState.success;
      case 2:
        return WifiState.already;
      default:
        return WifiState.error;
    }
  }
}

class WifiResult {
  String ssid;
  int level;
  String bssid;
  bool is5GHz;

  WifiResult(this.ssid, this.level, this.bssid, {this.is5GHz = false});

  @override
  String toString() {
    return 'WifiResult{ssid: $ssid, level: $level, bssid:$bssid,is5GHz:$is5GHz},';
  }
}
