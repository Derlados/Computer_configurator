import 'package:go_router/go_router.dart';
import 'package:pc_configurator_client/screens/auth/reset_password_screen.dart';
import 'package:pc_configurator_client/screens/auth/sign_in_screen.dart';
import 'package:pc_configurator_client/screens/auth/sign_up_screen.dart';
import 'package:pc_configurator_client/screens/main/main_screen.dart';

class Routes {
  static String signIn = 'sign-in';
  static String signUp = 'sign-up';
  static String resetPassword = 'reset-password';
  static String root = 'root';
}

getRouter({required isFirstLaunch}) {
  String getInitialRoute() {
    if (!isFirstLaunch) {
      return '/${Routes.signIn}';
    }

    return '/${Routes.signIn}';
  }

  return GoRouter(
    initialLocation: getInitialRoute(),
    routes: [
      GoRoute(
        name: Routes.signIn,
        path: '/${Routes.signIn}',
        builder: (context, state) => const SignInScreen(),
      ),
      GoRoute(
        name: Routes.signUp,
        path: '/${Routes.signUp}',
        builder: (context, state) => const SignUpScreen(),
      ),
      GoRoute(
        name: Routes.resetPassword,
        path: '/${Routes.resetPassword}',
        builder: (context, state) => const ResetPasswordScreen(),
      ),
      GoRoute(
        name: Routes.root,
        path: '/',
        builder: (context, state) => const MainScreen(),
      ),
    ],
  );
}