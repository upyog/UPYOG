// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'token.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Token _$TokenFromJson(Map<String, dynamic> json) => Token()
  ..accessToken = json['access_token'] as String?
  ..tokenType = json['token_type'] as String?
  ..refreshToken = json['refresh_token'] as String?
  ..expiresIn = (json['expires_in'] as num?)?.toInt()
  ..scope = json['scope'] as String?
  ..responseInfo = json['ResponseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
  ..userRequest = json['UserRequest'] == null
      ? null
      : UserRequest.fromJson(json['UserRequest'] as Map<String, dynamic>);

Map<String, dynamic> _$TokenToJson(Token instance) => <String, dynamic>{
      'access_token': instance.accessToken,
      'token_type': instance.tokenType,
      'refresh_token': instance.refreshToken,
      'expires_in': instance.expiresIn,
      'scope': instance.scope,
      'ResponseInfo': instance.responseInfo,
      'UserRequest': instance.userRequest,
    };

ResponseInfo _$ResponseInfoFromJson(Map<String, dynamic> json) => ResponseInfo()
  ..apiId = json['api_id'] as String?
  ..ver = json['ver'] as String?
  ..ts = json['ts'] as String?
  ..resMsgId = json['res_msg_id'] as String?
  ..msgId = json['msg_id'] as String?
  ..status = json['status'] as String?;

Map<String, dynamic> _$ResponseInfoToJson(ResponseInfo instance) =>
    <String, dynamic>{
      'api_id': instance.apiId,
      'ver': instance.ver,
      'ts': instance.ts,
      'res_msg_id': instance.resMsgId,
      'msg_id': instance.msgId,
      'status': instance.status,
    };

UserRequest _$UserRequestFromJson(Map<String, dynamic> json) => UserRequest()
  ..id = (json['id'] as num?)?.toInt()
  ..uuid = json['uuid'] as String?
  ..userName = json['userName'] as String?
  ..name = json['name'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..emailId = json['emailId'] as String?
  ..locale = json['locale'] as String?
  ..type = json['type'] as String?
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Role.fromJson(e as Map<String, dynamic>))
      .toList()
  ..active = json['active'] as bool?
  ..tenantId = json['tenantId'] as String?
  ..permanentCity = json['permanentCity'] as String?
  ..photo = json['photo'] as String?
  ..dob = json['dob'] as String?
  ..gender = json['gender'] as String?;

Map<String, dynamic> _$UserRequestToJson(UserRequest instance) =>
    <String, dynamic>{
      'id': instance.id,
      'uuid': instance.uuid,
      'userName': instance.userName,
      'name': instance.name,
      'mobileNumber': instance.mobileNumber,
      'emailId': instance.emailId,
      'locale': instance.locale,
      'type': instance.type,
      'roles': instance.roles,
      'active': instance.active,
      'tenantId': instance.tenantId,
      'permanentCity': instance.permanentCity,
      'photo': instance.photo,
      'dob': instance.dob,
      'gender': instance.gender,
    };

Role _$RoleFromJson(Map<String, dynamic> json) => Role()
  ..name = json['name'] as String?
  ..code = json['code'] as String?
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$RoleToJson(Role instance) => <String, dynamic>{
      'name': instance.name,
      'code': instance.code,
      'tenantId': instance.tenantId,
    };
