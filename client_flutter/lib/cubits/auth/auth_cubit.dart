import 'dart:convert';

import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:meta/meta.dart';
import 'package:pc_configurator_client/main.dart';
import 'package:pc_configurator_client/models/PCBUser.dart';

import '../../helpers/firebase_helper.dart';
import '../../services/api/auth/auth_service.dart';

part 'auth_state.dart';

class AuthCubit extends Cubit<AuthState> {
  final FirebaseAuthHelper firebaseHelper;
  final AuthService authService;

  AuthCubit({required this.firebaseHelper, required this.authService}) : super(const AuthState());

  onTermsToggled(bool checked) {
    emit(state.copyWith(termsAccepted: !state.termsAccepted));
  }

  onEmailSignUpPressed({required String email, required String username, required String password, required Function(PCBUser) onSuccess}) async {
    if (state.termsAccepted == false) {
      emit(state.copyWith(status: AuthStatus.termsAreNotAcceptedFailure));
      return;
    }

    emit(state.copyWith(status: AuthStatus.loading));

    try {
      final userCredential = await firebaseHelper.getEmailCredential(method: AuthMethod.signIn, email: email, password: password);
      _signInWithCredential(userCredential: userCredential, onSuccess: onSuccess);
      emit(state.copyWith(status: AuthStatus.success));
    } on FirebaseAuthException catch (e) {
      if (e.code == 'email-already-in-use') {
        emit(state.copyWith(status: AuthStatus.userExistFailure));
      }
    } catch (e) {
      emit(state.copyWith(status: AuthStatus.unexpectedFailure));
    }
  }

  onEmailSignInPressed({required String email, required String password, required Function(PCBUser) onSuccess}) async {
    emit(state.copyWith(status: AuthStatus.loading));

    try {
      final userCredential = await firebaseHelper.getEmailCredential(method: AuthMethod.signIn, email: email, password: password);
      _signInWithCredential(userCredential: userCredential, onSuccess: onSuccess);
      emit(state.copyWith(status: AuthStatus.success));
    } on FirebaseAuthException catch (e) {
      logger.e(e);
      if (e.code == 'user-not-found') {
        emit(state.copyWith(status: AuthStatus.userNotFoundFailure));
      } else if (e.code == 'wrong-password') {
        emit(state.copyWith(status: AuthStatus.wrongPasswordFailure));
      }
    } catch (e) {
      logger.e(e);
      emit(state.copyWith(status: AuthStatus.unexpectedFailure));
    }
  }

  onNewPasswordRequested({required String email, required Function() onSuccess}) async {
    emit(state.copyWith(status: AuthStatus.loading));

    try {
      await firebaseHelper.sendPasswordResetEmail(email: email);
      emit(state.copyWith(status: AuthStatus.success));
    } on FirebaseAuthException catch (e) {
      if (e.code == 'user-not-found') {
        emit(state.copyWith(status: AuthStatus.userNotFoundFailure));
      }
    } catch (e) {
      logger.e(e);
      emit(state.copyWith(status: AuthStatus.unexpectedFailure));
    }
  }

  onGoogleSignInPressed({required Function(PCBUser) onSuccess}) async {
    emit(state.copyWith(status: AuthStatus.loading));

    try {
      final userCredential = await firebaseHelper.getGoogleCredential();
      _signInWithCredential(userCredential: userCredential, onSuccess: onSuccess);
      emit(state.copyWith(status: AuthStatus.success));
    } catch (e) {
      logger.e(e);
      emit(state.copyWith(status: AuthStatus.unexpectedFailure));
    }
  }

  _signInWithCredential({required UserCredential userCredential, String? username, required Function(PCBUser) onSuccess }) async {
    if (userCredential.user != null) {
      final user = await authService.signIn(credential: userCredential, username: username ?? userCredential.user!.displayName!);
      onSuccess(user);
    } else {
      throw Exception('UserCredentials: user is null');
    }
  }
}
