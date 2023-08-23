part of 'auth_cubit.dart';

enum AuthStatus {
  initial,
  loading,
  success,
  termsAreNotAcceptedFailure,
  userExistFailure,
  userNotFoundFailure,
  wrongPasswordFailure,
  unexpectedFailure
}

@immutable
class AuthState extends Equatable {
  final AuthStatus status;
  final bool termsAccepted;

  const AuthState({this.termsAccepted = false, this.status = AuthStatus.initial});

  AuthState copyWith({
    AuthStatus? status,
    bool? termsAccepted
  }) {
    return AuthState(
      status: status ?? this.status,
      termsAccepted: termsAccepted ?? this.termsAccepted
    );
  }

  @override
  List<Object?> get props => [status, termsAccepted];
}
