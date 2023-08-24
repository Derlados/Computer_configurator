import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class PCBFailureScreen extends StatelessWidget {
  const PCBFailureScreen({Key? key}) : super(key: key);

  _onBack(BuildContext context) => context.pop();

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Padding(
            padding: const EdgeInsets.only(bottom: 16.0),
            child: Text("Something went wrong :(", style: Theme.of(context).textTheme.titleLarge),
          ),
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
