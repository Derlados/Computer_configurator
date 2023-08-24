import 'package:flutter/material.dart';

class PCBLoader extends StatelessWidget {
  const PCBLoader({Key? key, this.text}) : super(key: key);

  final String? text;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        children: [
          CircularProgressIndicator(color: Theme.of(context).colorScheme.primary),
          if (text != null)
            Text(text!, style: Theme.of(context).textTheme.bodyLarge)
        ],
      ),
    );
  }
}
