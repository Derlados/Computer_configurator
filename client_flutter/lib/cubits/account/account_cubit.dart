import 'package:bloc/bloc.dart';
import 'package:meta/meta.dart';
import 'package:pc_configurator_client/models/PCBUser.dart';

import '../../helpers/storage.dart';
import '../../utils/property_wrapper.dart';

part 'account_state.dart';

class AccountCubit extends Cubit<AccountState> {
  final Storage storage;

  AccountCubit({required this.storage}) : super(const AccountState());

  onStarted() {
    final userInfo = storage.loadFromJson(StorageKeys.userInfo);
    if (userInfo != null) {
      emit(state.copyWith(user: Wrapped(PCBUser.fromJson(userInfo))));
    }
  }

  onUserSignedIn({required PCBUser user}) {
    emit(state.copyWith(user: Wrapped(user)));
    storage.saveAsJson(StorageKeys.userInfo, user.toJson());
  }

  onUserSignedOut() {
    storage.prefs.remove(StorageKeys.userInfo.name);
    emit(state.copyWith(user: const Wrapped(null)));
  }
}
