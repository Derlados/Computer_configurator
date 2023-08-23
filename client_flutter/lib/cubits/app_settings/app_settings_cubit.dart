import 'package:bloc/bloc.dart';
import 'package:meta/meta.dart';

import '../../helpers/storage.dart';
import '../../main.dart';

part 'app_settings_state.dart';

class AppSettingsCubit extends Cubit<AppSettingsState> {
  AppSettingsCubit() : super(const AppSettingsState());

  onStarted() {
    if (state.status != AppSettingsStatus.initial) {
      return;
    }

    emit(state.copyWith(status: AppSettingsStatus.loading));

    try {
      final isFirstLaunch = Storage().prefs.getBool(StorageKeys.firstLaunch.name) ?? false;
      final isTourCompleted = Storage().prefs.getBool(StorageKeys.tourCompleted.name) ?? false;

      emit(state.copyWith(
        status: AppSettingsStatus.success,
        isFirstLaunch: isFirstLaunch,
        isTourCompleted: isTourCompleted
      ));
    } catch (e) {
      logger.e("Storage error: ${e.toString()}");
      emit(state.copyWith(status: AppSettingsStatus.failure));
    }
  }

  onFirstLaunched() async {
    await Storage().prefs.setBool(StorageKeys.firstLaunch.name, true);
    emit(state.copyWith(isFirstLaunch: true));
  }

  onTourCompleted() async {
    try {
      await Storage().prefs.setBool(StorageKeys.tourCompleted.name, true);
      emit(state.copyWith(isTourCompleted: true));
    } catch (e) {
      logger.e("Storage error, key '${StorageKeys.tourCompleted.name}': ${e.toString()}");
    }
  }
}
