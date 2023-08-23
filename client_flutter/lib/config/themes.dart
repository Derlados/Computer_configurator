import 'package:flutter/material.dart';
import 'package:flex_color_scheme/flex_color_scheme.dart';
import 'package:pc_configurator_client/config/pcb_pallete.dart';

ThemeData getLightTheme() {
  ThemeData themeData = FlexThemeData.light(
    colors: FlexSchemeColor.from(
      primary: PCBPalette.primary,
      primaryContainer: PCBPalette.primaryContainer,
      secondaryContainer: PCBPalette.secondaryContainer,
      tertiaryContainer: PCBPalette.tertiaryContainer,
    ),
    subThemesData: const FlexSubThemesData(
      defaultRadius: 6,
      bottomNavigationBarSelectedLabelSize: 12,
      bottomNavigationBarUnselectedLabelSize: 12,
    ),
  );

  return themeData.copyWith(
    textTheme: themeData.textTheme.apply(
      fontFamily: 'Arial',
      bodyColor: PCBPalette.primaryText,
      displayColor: PCBPalette.primaryText,
    ).merge(
        const TextTheme(
          titleLarge: TextStyle(fontSize: 32, color: PCBPalette.primaryText),
          titleMedium: TextStyle(fontSize: 28, color: PCBPalette.primaryText),
          titleSmall: TextStyle(fontSize: 24, color: PCBPalette.primaryText),
          bodyLarge: TextStyle(fontSize: 18, color: PCBPalette.primaryText),
          bodyMedium: TextStyle(fontSize: 16, color: PCBPalette.primaryText),
          bodySmall: TextStyle(fontSize: 12, color: PCBPalette.primaryText),
        )
    ),
  );
}