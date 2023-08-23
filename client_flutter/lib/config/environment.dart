import 'package:flutter/foundation.dart';
import 'package:flutter_config_plus/flutter_config_plus.dart';

class ConfigEnvVariables {
  static String apiUrl = 'API_URL';
}

initEnvironment() async {
  try {
    if (!kIsWeb) {
      await FlutterConfigPlus.loadEnvVariables();
    }
  } catch (e) {
    debugPrint('Environment initializing error: $e');
  }
}