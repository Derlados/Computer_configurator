import 'package:flutter_config_plus/flutter_config_plus.dart';

import '../../config/environment.dart';

class ApiConfig {
  final String baseUrl;
  final Duration? timeout;

  const ApiConfig({required this.baseUrl, this.timeout});
}

late final ApiConfig defaultApiConfig;

initApiConfig() {
  defaultApiConfig = ApiConfig(
      baseUrl: FlutterConfigPlus.get(ConfigEnvVariables.apiUrl),
  );
}