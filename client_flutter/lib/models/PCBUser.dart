import 'package:equatable/equatable.dart';
import 'package:json_annotation/json_annotation.dart';

part 'PCBUser.g.dart';

@JsonSerializable()
class PCBUser extends Equatable {
  final String id;
  final String username;
  final String? email;
  final String? photo;

  const PCBUser({
    required this.id,
    required this.username,
    this.email,
    this.photo,
  });

  factory PCBUser.fromJson(Map<String, dynamic> json) => _$PCBUserFromJson(json);

  Map<String, dynamic> toJson() => _$PCBUserToJson(this);

  copyWith({
    String? id,
    String? username,
    String? email,
    String? photo,
  }) {
    return PCBUser(
      id: id ?? this.id,
      username: username ?? this.username,
      email: email ?? this.email,
      photo: photo ?? this.photo,
    );
  }

  @override
  List<Object?> get props => [id, username, email, photo];
}