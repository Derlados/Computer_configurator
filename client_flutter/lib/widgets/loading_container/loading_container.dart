import 'package:flutter/material.dart';
import 'package:pc_configurator_client/widgets/general/pcb_loader.dart';
import 'package:pc_configurator_client/widgets/general_screens/pcb_no_content_screen.dart';

import '../general_screens/pcb_failure_screen.dart';

class LoadingContainer extends StatelessWidget {
  const LoadingContainer({Key? key,
    this.isLoading = false,
    this.isFailure = false,
    this.loadingText,
    this.isNoContent = false,
    this.noContentText,
    required this.child
  }) : super(key: key);

  final bool isLoading;
  final String? loadingText;
  final bool isFailure;
  final bool isNoContent;
  final String? noContentText;
  final Widget? child;

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return PCBLoader(text: loadingText);
    }

    if (isNoContent == true) {
      return PCBNoContentScreen(noContentText: noContentText);
    }

    if (isFailure) {
      return const PCBFailureScreen();
    }

    return child ?? Container();
  }
}
