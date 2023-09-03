part of 'account_cubit.dart';

enum AccountStatus {
  initial,
  loading,
  success,
  unexpectedFailure,
}

@immutable
class AccountState {
  final AccountStatus status;
  final PCBUser? user;

  const AccountState({
    this.status = AccountStatus.initial,
    this.user,
  });

  AccountState copyWith({
    AccountStatus? status,
    Wrapped<PCBUser?>? user,
  }) {
    return AccountState(
      status: status ?? this.status,
      user: user != null ? user.value : this.user,
    );
  }
}

