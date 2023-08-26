import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:pc_configurator_client/config/pcb_icons.dart';
import 'package:pc_configurator_client/screens/auth/widgets/auth_nav_text.dart';
import 'package:pc_configurator_client/widgets/buttons/pcb_rounded_button.dart';
import 'package:pc_configurator_client/widgets/buttons/pcb_social_button.dart';
import 'package:pc_configurator_client/widgets/general/pcb_checkbox.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../config/pcb_images.dart';
import '../../cubits/auth/auth_cubit.dart';
import '../../helpers/firebase_helper.dart';
import '../../routes.dart';
import '../../widgets/general/pcb_input_field.dart';

class SignUpScreen extends StatefulWidget {
  const SignUpScreen({Key? key, @visibleForTesting this.testAuthCubit}) : super(key: key);

  final AuthCubit? testAuthCubit;

  @override
  State<SignUpScreen> createState() => _SignUpScreenState();
}

class _SignUpScreenState extends State<SignUpScreen> {
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  final TextEditingController usernameController = TextEditingController();

  late final AuthCubit _authCubit;

  @override
  initState() {
    _authCubit = widget.testAuthCubit ?? AuthCubit(firebaseHelper: FirebaseAuthHelper());

    super.initState();
  }

  _onEmailSignUpPressed(BuildContext context) {
    _authCubit.onEmailSignUpPressed(
      username: usernameController.text,
      email: emailController.text,
      password: passwordController.text,
      onSuccess: () => _onSuccess(context),
    );
  }

  _onTermsToggled(bool checked) {
    _authCubit.onTermsToggled(checked);
  }

  _onSuccess(BuildContext context) {
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
    return Scaffold(
      body: SafeArea(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Center(
              child: SingleChildScrollView(
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
                        controller: usernameController,
                        hint: "Enter username",
                      prefixIcon: const Icon(PcBuilderIcons.user, color: Colors.white),
                    ),
                    const SizedBox(height: 16.0),
                    PCBInputField(
                        controller: emailController,
                        hint: "Enter email",
                      prefixIcon: const Icon(PcBuilderIcons.email, color: Colors.white),
                    ),
                    const SizedBox(height: 16.0),
                    PCBInputField(
                        controller: passwordController,
                        hint: "Enter password",
                        prefixIcon: const Icon(PcBuilderIcons.topSecret, color: Colors.white),
                      obscureText: true,
                    ),
                    const SizedBox(height: 16.0),
                    PCBRoundedButton(
                      text: "Sign Up",
                      onTap: () => _onEmailSignUpPressed(context)
                    ),
                    const SizedBox(height: 16.0),
                    PCBCheckBox(
                      isAccepted: true,
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
                    ),
                    const SizedBox(height: 32.0),
                    AuthNavText(text: "Already have an account? ", linkText: "Sign In", onTap: () => _onSignInPressed(context)),
                  ],
                ),
              ),
            ),
          )
      ),
    );
  }
}
