import 'dio_config.dart';
import 'package:dio/dio.dart';

class Api {
  static final Api _singleton = Api._internal(config: defaultApiConfig);
  late final Dio _dio;

  factory Api() {
    return _singleton;
  }

  Api._internal({required ApiConfig config}) {
    _dio = Dio(BaseOptions(
      baseUrl: config.baseUrl,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Charset': 'utf-8',
      },
      connectTimeout: config.timeout,
      receiveTimeout: config.timeout,
    ));
  }

  Dio get dio => _dio;
}