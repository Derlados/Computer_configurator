import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:logger/logger.dart';
import 'package:pc_configurator_client/helpers/storage.dart';
import 'package:pc_configurator_client/routes.dart';
import 'package:pc_configurator_client/services/api/dio_config.dart';

import 'config/environment.dart';
import 'config/themes.dart';
import 'cubits/app_settings/app_settings_cubit.dart';

import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

final logger = Logger();

Future<void> main() async {
  // Environment init
  WidgetsFlutterBinding.ensureInitialized(); // Required by FlutterConfig
  await initEnvironment();

  // Api init
  WidgetsFlutterBinding.ensureInitialized();
  initApiConfig();

  // Storage init
  WidgetsFlutterBinding.ensureInitialized();
  await Storage().init();

  //Firebase init
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _appSettingsCubit = AppSettingsCubit();

  @override
  void initState() {
    super.initState();
    _appSettingsCubit.onStarted();
  }

  bool isFirstLaunch() {
    final isFirstLaunch = _appSettingsCubit.state.isFirstLaunch;
    if (!isFirstLaunch) {
      _appSettingsCubit.onFirstLaunched();
    }

    return isFirstLaunch;
  }

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider(
          create: (context) => _appSettingsCubit,
        ),
      ],
      child: BlocBuilder<AppSettingsCubit, AppSettingsState>(
        buildWhen: (previous, current) => previous.status != current.status,
        builder: (context, state) {
          if (state.status == AppSettingsStatus.loading) {
            return const CircularProgressIndicator();
          }

          return MaterialApp.router(
            title: 'PC Builder - Component picker',
            theme: getLightTheme(),
            supportedLocales: const [
              Locale('en'),
              Locale('ru'),
              Locale('uk'),
            ],
            localizationsDelegates: const [
              AppLocalizations.delegate,
              GlobalMaterialLocalizations.delegate,
              GlobalWidgetsLocalizations.delegate,
            ],
            routerConfig: getRouter(
              isFirstLaunch: isFirstLaunch(),
            ),
          );
        },
      ),
    );
  }
}

