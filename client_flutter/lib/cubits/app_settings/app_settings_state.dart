part of 'app_settings_cubit.dart';

enum AppSettingsStatus {
  initial, loading, success, failure
}

@immutable
class AppSettingsState {
  final AppSettingsStatus status;
  final bool isFirstLaunch;
  final bool isTourCompleted;

  const AppSettingsState({
    this.status = AppSettingsStatus.initial,
    this.isFirstLaunch = false,
    this.isTourCompleted = false,
  });

  copyWith({
    AppSettingsStatus? status,
    bool? isFirstLaunch,
    bool? isTourCompleted,
  }) {
    return AppSettingsState(
        status: status ?? this.status,
        isFirstLaunch: isFirstLaunch ?? this.isFirstLaunch,
        isTourCompleted: isTourCompleted ?? this.isTourCompleted
    );
  }
}


