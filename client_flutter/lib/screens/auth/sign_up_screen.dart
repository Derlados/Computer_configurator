import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:pc_configurator_client/config/pcb_icons.dart';
import 'package:pc_configurator_client/helpers/validators/auth_validators.dart';
import 'package:pc_configurator_client/models/PCBUser.dart';
import 'package:pc_configurator_client/screens/auth/widgets/auth_nav_text.dart';
import 'package:pc_configurator_client/services/api/auth/auth_service.dart';
import 'package:pc_configurator_client/widgets/buttons/pcb_rounded_button.dart';
import 'package:pc_configurator_client/widgets/general/pcb_checkbox.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../config/pcb_images.dart';
import '../../cubits/account/account_cubit.dart';
import '../../cubits/auth/auth_cubit.dart';
import '../../helpers/firebase_helper.dart';
import '../../helpers/storage.dart';
import '../../routes.dart';
import '../../services/api/dio.dart';
import '../../widgets/general/pcb_input_field.dart';

class SignUpScreen extends StatefulWidget {
  const SignUpScreen({Key? key, @visibleForTesting this.testAuthCubit, @visibleForTesting this.testAccountCubit}) : super(key: key);

  final AuthCubit? testAuthCubit;
  final AccountCubit? testAccountCubit;

  @override
  State<SignUpScreen> createState() => _SignUpScreenState();
}

class _SignUpScreenState extends State<SignUpScreen> {
  final _formKey = GlobalKey<FormState>();
  bool _isFailureSubmit = false;
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _confirmPasswordController = TextEditingController();
  final TextEditingController _usernameController = TextEditingController();

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

  _onEmailSignUpPressed(BuildContext context) {
    if (_formKey.currentState!.validate()) {
      _authCubit.onEmailSignUpPressed(
          username: _usernameController.text,
          email: _emailController.text,
          password: _passwordController.text,
          onSuccess: (user) => _onSuccess(context: context, user: user)
      );
      return;
    } else {
      setState(() {
        _isFailureSubmit = true;
      });
    }
  }

  _onTermsToggled(bool checked) {
    _authCubit.onTermsToggled(checked);
  }

  _onSuccess({required BuildContext context, required PCBUser user}) {
    _accountCubit.onUserSignedIn(user: user);
    context.goNamed(Routes.root);
  }

  _onOpenLink({required String link}) async {
    final Uri url = Uri.parse(link);
    if (await canLaunchUrl(url)) {
      await launchUrl(url);
    }
  }

  _onSignInPressed(BuildContext context) {
    context.goNamed(Routes.signIn);
  }

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => _authCubit,
      child: Scaffold(
        body: SafeArea(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
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
                            controller: _usernameController,
                            validator: AuthValidators.usernameValidator,
                            hint: "Enter username",
                            prefixIcon: const Icon(PcBuilderIcons.user, color: Colors.white),
                          ),
                          const SizedBox(height: 16.0),
                          PCBInputField(
                            controller: _emailController,
                            validator: AuthValidators.emailValidator,
                            hint: "Enter email",
                            prefixIcon: const Icon(PcBuilderIcons.email, color: Colors.white),
                          ),
                          const SizedBox(height: 16.0),
                          PCBInputField(
                            controller: _passwordController,
                            validator: AuthValidators.passwordValidator,
                            hint: "Enter password",
                            prefixIcon: const Icon(PcBuilderIcons.topSecret, color: Colors.white),
                            obscureText: true,
                          ),
                          const SizedBox(height: 16.0),
                          PCBInputField(
                            controller: _confirmPasswordController,
                            validator: (confirmedPassword) => AuthValidators.confirmPasswordValidator(_passwordController.text, confirmedPassword),
                            hint: "Repeat password",
                            prefixIcon: const Icon(PcBuilderIcons.topSecret, color: Colors.white),
                            obscureText: true,
                          ),
                          const SizedBox(height: 16.0),
                          PCBRoundedButton(
                              text: "Sign Up",
                              onTap: () => _onEmailSignUpPressed(context)
                          ),
                          const SizedBox(height: 16.0),
                          BlocBuilder<AuthCubit, AuthState>(
                            builder: (context, state) {
                              return PCBCheckBox(
                                isAccepted: state.termsAccepted,
                                onChanged: _onTermsToggled,
                                child: RichText(
                                  text: TextSpan(
                                      text: "I agree to the ",
                                      children: [
                                        TextSpan(
                                            text: "Privacy policy",
                                            style: TextStyle(
                                              decoration: TextDecoration.underline,
                                              color: Theme.of(context).colorScheme.primary,
                                            ),
                                            recognizer: TapGestureRecognizer()..onTap = () => _onOpenLink(link: "https://www.google.com")
                                        ),
                                        const TextSpan(text: " and "),
                                        TextSpan(
                                            text: "Terms of service",
                                            style: TextStyle(
                                                decoration: TextDecoration.underline,
                                                color: Theme.of(context).colorScheme.primary
                                            ),
                                            recognizer: TapGestureRecognizer()..onTap = () => _onOpenLink(link: "https://www.google.com")
                                        )
                                      ]
                                  ),

                                ),
                              );
                            },
                          ),
                          const SizedBox(height: 32.0),
                          AuthNavText(text: "Already have an account? ", linkText: "Sign In", onTap: () => _onSignInPressed(context)),
                        ],
                      ),
                    )
                ),
              ),
            )
        ),
      ),
    );
  }
}
