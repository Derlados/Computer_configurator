import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:pc_configurator_client/main.dart';
import 'package:pc_configurator_client/models/PCBUser.dart';
import 'package:pc_configurator_client/services/api/service.dart';

class AuthService extends Service {

  final Dio dio;

  AuthService({required this.dio});

  Future<PCBUser> signIn({required UserCredential credential, String? username}) async {

    final body = {
      'idToken': await credential.user!.getIdToken(),
      'username': username,
    };

    try {
      final response = await dio.post(
        '/auth/sign-in',
        data: body
      );

      return PCBUser.fromJson(response.data);
    } on DioException catch (e) {
      logger.e(e.message);
      rethrow;
    }
  }
}