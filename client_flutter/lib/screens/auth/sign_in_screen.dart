import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:pc_configurator_client/config/pcb_images.dart';
import 'package:pc_configurator_client/cubits/account/account_cubit.dart';
import 'package:pc_configurator_client/cubits/auth/auth_cubit.dart';
import 'package:pc_configurator_client/helpers/validators/auth_validators.dart';
import 'package:pc_configurator_client/helpers/firebase_helper.dart';
import 'package:pc_configurator_client/helpers/storage.dart';
import 'package:pc_configurator_client/main.dart';
import 'package:pc_configurator_client/models/PCBUser.dart';
import 'package:pc_configurator_client/routes.dart';
import 'package:pc_configurator_client/screens/auth/widgets/auth_nav_text.dart';
import 'package:pc_configurator_client/widgets/general/pcb_input_field.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../config/pcb_icons.dart';
import '../../constants/regex.dart';
import '../../services/api/auth/auth_service.dart';
import '../../services/api/dio.dart';
import '../../widgets/buttons/pcb_rounded_button.dart';
import '../../widgets/buttons/pcb_social_button.dart';

class SignInScreen extends StatefulWidget {
  const SignInScreen({Key? key, @visibleForTesting this.testAuthCubit, @visibleForTesting this.testAccountCubit}) : super(key: key);

  final AuthCubit? testAuthCubit;
  final AccountCubit? testAccountCubit;

  @override
  State<SignInScreen> createState() => _SignInScreenState();
}

class _SignInScreenState extends State<SignInScreen> {
  final _formKey = GlobalKey<FormState>();
  bool _isFailureSubmit = false;
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();

  late final AuthCubit _authCubit;
  late final AccountCubit _accountCubit;

  @override
  initState() {
    _authCubit = widget.testAuthCubit ?? AuthCubit(
        firebaseHelper: FirebaseAuthHelper(),
        authService: AuthService(dio: Api().dio)
    );
    _accountCubit = widget.testAccountCubit ?? AccountCubit(
        storage: Storage()
    );

    super.initState();
  }

  _onFormChanged() async {
    if (_isFailureSubmit) {
      _formKey.currentState!.validate();
    }
  }

  _onResetPasswordPressed(BuildContext context) {
    context.pushNamed(Routes.resetPassword);
  }

  _onEmailSignInPressed(BuildContext context) {
    if (_formKey.currentState!.validate()) {
      _authCubit.onEmailSignInPressed(
        email: _emailController.text,
        password: _passwordController.text,
        onSuccess: (user) => _onSuccess(context: context, user: user),
      );
    } else {
      setState(() {
        _isFailureSubmit = true;
      });
    }
  }

  _onGoogleSighInPressed(BuildContext context) {
    _authCubit.onGoogleSignInPressed(
      onSuccess: (user) => _onSuccess(context: context, user: user),
    );
  }

  _onSuccess({required BuildContext context, required PCBUser user}) {
    _accountCubit.onUserSignedIn(user: user);
    context.goNamed(Routes.root);
  }

  _onSignUpPressed(BuildContext context) {
    context.goNamed(Routes.signUp);
  }

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => _authCubit,
      child: Scaffold(
        body: SafeArea(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            child: Center(
              child: SingleChildScrollView(
                child: Form(
                  key: _formKey,
                  onChanged: _onFormChanged,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Center(
                        child: Image(
                            image: PCBImages.appIcon,
                            width: MediaQuery.of(context).size.width * 0.6
                        ),
                      ),
                      const SizedBox(height: 32.0),
                      PCBInputField(
                        controller: _emailController,
                        validator: AuthValidators.emailValidator,
                        hint: "Enter email",
                        prefixIcon: const Icon(PcBuilderIcons.email, color: Colors.white),
                        onFocusChanged: (_) => _onFormChanged,
                      ),
                      const SizedBox(height: 16.0),
                      PCBInputField(
                        controller: _passwordController,
                        validator: AuthValidators.passwordValidator,
                        hint: "Enter password",
                        prefixIcon: const Icon(PcBuilderIcons.topSecret, color: Colors.white),
                        obscureText: true,
                        onFocusChanged: (_) => _onFormChanged,
                      ),
                      const SizedBox(height: 4.0),
                      InkWell(
                        onTap: () => _onResetPasswordPressed(context),
                        child: Text("Forgot the password ?", style: Theme.of(context).textTheme.bodySmall?.copyWith(color: Theme.of(context).colorScheme.primary)),
                      ),
                      const SizedBox(height: 32.0),
                      PCBRoundedButton(
                          text: "Sign In",
                          onTap: () => _onEmailSignInPressed(context)
                      ),
                      const SizedBox(height: 16.0),
                      PCBSocialButton(
                          title: "Sign in with Google",
                          icon: SocialIcon.google,
                          onPressed: () => _onGoogleSighInPressed(context)
                      ),
                      const SizedBox(height: 16.0),
                      AuthNavText(text: "Doesn't have an account? ", linkText: "Sign Up", onTap: () => _onSignUpPressed(context))
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
