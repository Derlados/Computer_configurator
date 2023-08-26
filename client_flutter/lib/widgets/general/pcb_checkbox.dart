import 'package:flutter/material.dart';

class PCBCheckBox extends StatelessWidget {
  const PCBCheckBox({Key? key, required this.isAccepted, required this.child, required this.onChanged}) : super(key: key);

  final bool isAccepted;
  final Widget child;
  final Function(bool) onChanged;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Padding(
          padding: const EdgeInsets.only(right: 8.0, top: 4.0),
          child: Container(
              height: 18.0,
              width: 18.0,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(4.0),
                color: Theme.of(context).colorScheme.primaryContainer,
              ),
              child: Checkbox(value: isAccepted, onChanged: (checked) => checked != null ? onChanged(checked) : null)
          ),
        ),
        child
      ],
    );
  }
}
