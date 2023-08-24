import 'package:flutter/material.dart';
import 'package:pc_configurator_client/config/pcb_images.dart';
import 'package:pc_configurator_client/widgets/general/pcb_input_field.dart';

class SignInScreen extends StatefulWidget {
  const SignInScreen({Key? key}) : super(key: key);

  @override
  State<SignInScreen> createState() => _SignInScreenState();
}

class _SignInScreenState extends State<SignInScreen> {
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  final TextEditingController usernameController = TextEditingController();


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Image(image: PCBImages.appIcon),
              const SizedBox(height: 32.0),
              PCBInputField(controller: usernameController, hint: "Enter username"),
              const SizedBox(height: 16.0),
              PCBInputField(controller: emailController, hint: "Enter email"),
              const SizedBox(height: 16.0),
              PCBInputField(controller: passwordController, hint: "Enter password"),
            ],
          ),
        )
      ),
    );
  }
}
