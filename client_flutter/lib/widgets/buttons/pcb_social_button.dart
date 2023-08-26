import 'package:flutter/material.dart';

enum SocialIcon {
  google,
  facebook,
  apple
}

class PCBSocialButton extends StatelessWidget {
  const PCBSocialButton({
    Key? key,
    required this.title,
    required this.icon,
    required this.onPressed,
    this.isLoading = false,
    this.width = double.infinity,
    this.borderRadius = 24.0,
    this.height = 48
  }) : super(key: key);

  final String title;
  final SocialIcon icon;
  final Function() onPressed;
  final bool isLoading;
  final double width;
  final double height;
  final double borderRadius;

  _getIcoPath(SocialIcon icon) {
    switch (icon) {
      case SocialIcon.google:
        return 'assets/images/google_icon.png';
      case SocialIcon.facebook:
        return 'assets/images/facebook_icon.png';
      case SocialIcon.apple:
        return 'assets/images/apple_icon.png';
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
              color: Colors.white,
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
              Text(title, style: Theme.of(context).textTheme.bodyLarge?.copyWith(fontWeight: FontWeight.bold, color: Colors.black))
            ],
          )
      ),
    );
  }
}
