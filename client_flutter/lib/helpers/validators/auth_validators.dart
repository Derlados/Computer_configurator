import 'package:flutter/cupertino.dart';

import '../../constants/regex.dart';
import '../../main.dart';

class AuthValidators {
  static String? emailValidator(String? email) {
    if (email == null || email.isEmpty) {
      return "Please enter email";
    }

    if (!RegExp(Regex.email).hasMatch(email)) {
      return "Please enter valid email";
    }

    return null;
  }

  static String? passwordValidator(String? password) {
    if (password == null || password.isEmpty) {
      return "Please enter email";
    }

    return null;
  }

  static String? confirmPasswordValidator(String? password, String? confirmedPassword) {
    if (confirmedPassword == null || confirmedPassword.isEmpty) {
      return "Please enter email";
    }

    if (confirmedPassword != password) {
      return "Passwords do not match";
    }

    return null;
  }

  static String? usernameValidator(String? username) {
    if (username == null || username.isEmpty) {
      return "Please enter username";
    }

    if (username.length < 3) {
      return "Username must be at least 3 characters long";
    }

    return null;
  }
}