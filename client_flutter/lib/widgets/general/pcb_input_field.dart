import 'package:flutter/material.dart';
import 'package:pc_configurator_client/config/pcb_pallete.dart';

class PCBInputField extends StatefulWidget {
  const PCBInputField({Key? key, required this.controller, required this.hint, this.focusNode, this.prefixIcon, this.suffixIcon, this.obscureText}) : super(key: key);

  final TextEditingController controller;
  final String hint;
  final FocusNode? focusNode;
  final Icon? prefixIcon;
  final Icon? suffixIcon;
  final bool? obscureText;

  @override
  State<PCBInputField> createState() => _PCBInputFieldState();
}

class _PCBInputFieldState extends State<PCBInputField> {
  late final FocusNode _focusNode;
  bool _isFocused = false;

  @override
  void initState() {
    super.initState();
    _focusNode = widget.focusNode ?? FocusNode();
    _focusNode.addListener(_onFocusChange);
  }

  @override
  void dispose() {
    _focusNode.removeListener(_onFocusChange);
    _focusNode.dispose();
    super.dispose();
  }

  void _onFocusChange() {
    setState(() {
      _isFocused = _focusNode.hasFocus;
    });
  }

  @override
  Widget build(BuildContext context) {
    return TextFormField(
        focusNode: _focusNode,
        controller: widget.controller,
        obscureText: widget.obscureText ?? false,
        decoration: InputDecoration(
          contentPadding: const EdgeInsets.symmetric(vertical: 16, horizontal: 24),
          filled: true,
          fillColor: _isFocused ? PCBPalette.tertiaryContainer : PCBPalette.tertiaryContainer,
          hintText: widget.hint,
          hintStyle: Theme.of(context).textTheme.bodySmall?.copyWith(color: PCBPalette.hintTextColor),
          enabledBorder: OutlineInputBorder( // Граница при неактивном состоянии
            borderRadius: BorderRadius.circular(60.0), // Радиус закругления краев
          ),
          focusedBorder: OutlineInputBorder(
            borderSide: BorderSide(width: 1.0, color: Theme.of(context).colorScheme.primary), // Граница при неактивном состоянии
            borderRadius: BorderRadius.circular(60.0), // Радиус закругления краев
          ),
          prefixIcon: widget.prefixIcon != null ? Padding(
            padding: const EdgeInsets.only(left: 24.0, right: 12, top: 12, bottom: 12),
            child: widget.prefixIcon,
          ) : null,
          suffixIcon: widget.suffixIcon != null ? Padding(
            padding: const EdgeInsets.only(left: 12.0, right: 24, top: 12, bottom: 12),
            child: widget.suffixIcon,
          ) : null,
        ),
        style: const TextStyle(color: Colors.white, fontSize: 18)
    );
  }
}
