import 'package:flutter/material.dart';

class PCBRoundedButton extends StatelessWidget {
  const PCBRoundedButton({Key? key, required this.onTap, required this.text, this.height, this.width, this.color}) : super(key: key);

  static const double _basicHeight = 48;

  final Function() onTap;
  final String text;
  final double? height;
  final double? width;
  final Color? color;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      child: Container(
        width: width ?? double.infinity,
        height: height ?? _basicHeight,
        decoration: BoxDecoration(
          color: color ?? Theme.of(context).colorScheme.primary,
          borderRadius: BorderRadius.circular(height != null ? height! / 2 : _basicHeight / 2)
        ),
        child: Center(
            child: Text(text, style: Theme.of(context).textTheme.bodyLarge?.copyWith(fontWeight: FontWeight.bold))
        ),
      ),
    );
  }
}
