import 'dart:convert';

import 'package:json_annotation/json_annotation.dart';
import 'package:shared_preferences/shared_preferences.dart';

enum StorageKeys {
  firstLaunch, tourCompleted, userInfo
}

class Storage {
  static final Storage _instance = Storage._internal();
  late final SharedPreferences prefs;

  factory Storage() {
    return _instance;
  }

  Storage._internal();

  /// for init SharedPreferences, we need get instance of SharedPreferences
  init() async {
    prefs = await SharedPreferences.getInstance();
  }

  dynamic loadFromJson(StorageKeys key) {
    return jsonDecode(prefs.getString(key.name) ?? "");
  }

  Future<bool> saveAsJson(StorageKeys key, dynamic value) {
    return prefs.setString(key.name, jsonEncode(value));
  }
}