import 'dart:convert';

import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:meta/meta.dart';
import 'package:pc_configurator_client/main.dart';

import '../../helpers/firebase_helper.dart';

part 'auth_state.dart';

class AuthCubit extends Cubit<AuthState> {
  final FirebaseAuthHelper firebaseHelper;

  AuthCubit({required this.firebaseHelper}) : super(const AuthState());

  onTermsToggled(bool checked) {
    emit(state.copyWith(termsAccepted: !state.termsAccepted));
  }

  onEmailSignUpPressed({required String email, required String username, required String password, required Function() onSuccess}) async {
    if (state.termsAccepted == false) {
      emit(state.copyWith(status: AuthStatus.termsAreNotAcceptedFailure));
      return;
    }

    emit(state.copyWith(status: AuthStatus.loading));

    try {
      final userCredential = await firebaseHelper.getEmailCredential(method: AuthMethod.signUp, email: email, password: password);
      if (userCredential.user != null) {
        logger.w(userCredential);
        // await client.auth.registerUser(userCredential.user!);
        // await client.auth.createUser();
      }

      emit(state.copyWith(status: AuthStatus.success));
      onSuccess();
    } on FirebaseAuthException catch (e) {
      if (e.code == 'email-already-in-use') {
        emit(state.copyWith(status: AuthStatus.userExistFailure));
      }
    } catch (e) {
      emit(state.copyWith(status: AuthStatus.unexpectedFailure));
    }
  }

  onEmailSignInPressed({required String email, required String password, required Function() onSuccess}) async {
    emit(state.copyWith(status: AuthStatus.loading));

    try {
      final userCredential = await firebaseHelper.getEmailCredential(method: AuthMethod.signIn, email: email, password: password);
      if (userCredential.user != null) {
        // await client.auth.signInUser(
        //     userCredential: userCredential,
        //     onSuccess: event.onSuccess,
        //     onAuthNotCompleted: event.onAuthNotCompleted
        // );
        emit(state.copyWith(status: AuthStatus.success));
      }
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

  onNewPasswordRequested({
    required String email,
    required Function() onSuccess
  }) async {
    emit(state.copyWith(status: AuthStatus.loading));

    try {
      await firebaseHelper.sendPasswordResetEmail(email: email);
      emit(state.copyWith(status: AuthStatus.success));
    } on FirebaseAuthException catch (e) {
      if (e.code == 'user-not-found') {
        emit(state.copyWith(status: AuthStatus.userNotFoundFailure));
      }
    } catch (e) {
      emit(state.copyWith(status: AuthStatus.unexpectedFailure));
    }
  }

  onGoogleSignInPressed({required Function onSuccess}) async {
    emit(state.copyWith(status: AuthStatus.loading));

    try {
      final userCredential = await firebaseHelper.getGoogleCredential();

      if (userCredential.user != null) {
        logger.w(userCredential);
        // await client.auth.signInUser(
        //     userCredential: userCredential,
        //     onSuccess: event.onSuccess,
        //     onAuthNotCompleted: event.onAuthNotCompleted
        // );
        emit(state.copyWith(status: AuthStatus.success));
      } else {
        emit(state.copyWith(status: AuthStatus.unexpectedFailure));
      }
    } catch (e) {
      logger.e(e);
      emit(state.copyWith(status: AuthStatus.unexpectedFailure));
    }
  }
}
