import 'dart:io';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_auth_mocks/firebase_auth_mocks.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_facebook_auth/flutter_facebook_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';

enum AuthMethod {
  signIn,
  signUp,
}

class FirebaseAuthHelper {
  late final FirebaseAuth auth;
  FirebaseAuthHelper({FirebaseAuth? auth}) {
    if (!kIsWeb && Platform.environment.containsKey('FLUTTER_TEST')) {
      this.auth = auth ?? MockFirebaseAuth();
    } else {
      this.auth = FirebaseAuth.instance;
    }
  }

  Future<UserCredential> getEmailCredential({required AuthMethod method, required String email, required String password}) async {
    if (method == AuthMethod.signUp) {
      return auth.createUserWithEmailAndPassword(email: email, password: password);
    } else {
      return auth.signInWithEmailAndPassword(email: email, password: password);
    }
  }

  Future<UserCredential> getGoogleCredential() async {
    final GoogleSignInAccount? gUser = await GoogleSignIn().signIn();
    final GoogleSignInAuthentication gAuth = await gUser!.authentication;

    final credential = GoogleAuthProvider.credential(
        accessToken: gAuth.accessToken,
        idToken: gAuth.idToken
    );

    return FirebaseAuth.instance.signInWithCredential(credential);
  }

  Future<void> sendPasswordResetEmail({required String email}) {
    return auth.sendPasswordResetEmail(email: email);
  }
}
