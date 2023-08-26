import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:pc_configurator_client/helpers/firebase_helper.dart';
import 'package:pc_configurator_client/widgets/buttons/pcb_rounded_button.dart';

import '../../config/pcb_icons.dart';
import '../../config/pcb_images.dart';
import '../../cubits/auth/auth_cubit.dart';
import '../../widgets/general/pcb_input_field.dart';

class ResetPasswordScreen extends StatefulWidget {
  const ResetPasswordScreen({Key? key, @visibleForTesting this.testAuthCubit}) : super(key: key);

  final AuthCubit? testAuthCubit;

  @override
  State<ResetPasswordScreen> createState() => _ResetPasswordScreenState();
}

class _ResetPasswordScreenState extends State<ResetPasswordScreen> {
  final TextEditingController emailController = TextEditingController();

  late final AuthCubit _authCubit;

  @override
  initState() {
    _authCubit = widget.testAuthCubit ?? AuthCubit(firebaseHelper: FirebaseAuthHelper());

    super.initState();
  }

  _onResetPassword() {
    _authCubit.onNewPasswordRequested(email: emailController.text, onSuccess: () {  });
  }

  _onBack(BuildContext context) {
    context.pop();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: Stack(
            children: [
              Padding(
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
                        const SizedBox(height: 24),
                        Text(
                            "Enter your email address and app will send you a link to reset your password",
                            textAlign: TextAlign.center,
                            style: Theme.of(context).textTheme.bodyMedium
                        ),
                        const SizedBox(height: 24),
                        PCBInputField(
                            controller: emailController,
                            hint: "Enter email",
                            prefixIcon: const Icon(PcBuilderIcons.email, color: Colors.white),
                        ),
                        const SizedBox(height: 16),
                        PCBRoundedButton(
                          text: "Reset password",
                          onTap: () => _onResetPassword(),
                        )
                      ],
                    ),
                  ),
                ),
              ),
              Positioned(
                left: 8,
                top: 8,
                child: TextButton(
                  child: Text("< Back", style: Theme.of(context).textTheme.bodyMedium?.copyWith(fontWeight: FontWeight.bold)),
                  onPressed: () => _onBack(context),
                )
              )
            ],
          ),
        ),
      ),
    );
  }
}
