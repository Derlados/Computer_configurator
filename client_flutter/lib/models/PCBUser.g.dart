// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'PCBUser.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PCBUser _$PCBUserFromJson(Map<String, dynamic> json) => PCBUser(
      id: json['id'] as String,
      username: json['username'] as String,
      email: json['email'] as String?,
      photo: json['photo'] as String?,
    );

Map<String, dynamic> _$PCBUserToJson(PCBUser instance) => <String, dynamic>{
      'id': instance.id,
      'username': instance.username,
      'email': instance.email,
      'photo': instance.photo,
    };
