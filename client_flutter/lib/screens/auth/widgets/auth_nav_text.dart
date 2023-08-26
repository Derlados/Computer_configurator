import 'package:flutter/material.dart';

class AuthNavText extends StatelessWidget {
  const AuthNavText({Key? key, required this.text, required this.linkText, required this.onTap}) : super(key: key);

  final String text;
  final String linkText;
  final Function onTap;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text(text, style: Theme.of(context).textTheme.bodyMedium),
        GestureDetector(
          onTap: () => onTap(),
          child: Text(linkText, style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: Theme.of(context).colorScheme.primary, decoration: TextDecoration.underline)),
        )
      ],
    );
  }
}
