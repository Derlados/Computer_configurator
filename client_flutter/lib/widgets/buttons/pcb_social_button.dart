import 'package:flutter/material.dart';

enum SocialIcon {
  google,
  facebook,
  apple
}

class PCBSocialButton extends StatelessWidget {
  const PCBSocialButton({Key? key, required this.onPressed, required this.title, this.isLoading = false, this.width = double.infinity, this.borderRadius = 8.0, this.height, required this.icon}) : super(key: key);
  final String title;
  final SocialIcon icon;
  final Function() onPressed;
  final bool isLoading;
  final double width;
  final double? height;
  final double borderRadius;

  _getIcoPath(SocialIcon icon) {
    switch (icon) {
      case SocialIcon.google:
        return 'assets/google_icon.png';
      case SocialIcon.facebook:
        return 'assets/facebook_icon.png';
      case SocialIcon.apple:
        return 'assets/apple_icon.png';
    }
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      behavior: HitTestBehavior.translucent,
      onTap: onPressed,
      child: Container(
          height: height,
          width: width,
          decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.primary,
              borderRadius: BorderRadius.circular(borderRadius)
          ),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Padding(
                padding: const EdgeInsets.only(right: 8.0),
                child: Image(image: AssetImage(_getIcoPath(icon)), height: 24, width: 24),
              ),
              Text(title, style: Theme.of(context).textTheme.bodyLarge?.copyWith(fontWeight: FontWeight.bold))
            ],
          )
      ),
    );
  }
}
