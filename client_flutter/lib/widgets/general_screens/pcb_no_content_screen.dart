import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class PCBNoContentScreen extends StatelessWidget {
  const PCBNoContentScreen({Key? key, this.noContentText}) : super(key: key);

  final String? noContentText;

  _onBack(BuildContext context) => context.pop();

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Text("404", style: Theme.of(context).textTheme.titleLarge?.copyWith(fontSize: 64.0)),
          const SizedBox(height: 16.0),
          Text(noContentText ?? "Nothing found :(", style: Theme.of(context).textTheme.titleLarge),
          GestureDetector(
            onTap: () => _onBack(context),
            child: Container(
              width: 150,
              height: 50,
              decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.primary,
                  borderRadius: BorderRadius.circular(32.0)
              ),
              child: Center(child: Text("Back", style: Theme.of(context).textTheme.titleLarge?.copyWith(color: Colors.white))),
            ),
          )
        ],
      ),
    );
  }
}
