// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_profile.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

UserProfile _$UserProfileFromJson(Map<String, dynamic> json) => UserProfile()
  ..user = (json['user'] as List<dynamic>?)
      ?.map((e) => User.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$UserProfileToJson(UserProfile instance) =>
    <String, dynamic>{
      'user': instance.user,
    };

User _$UserFromJson(Map<String, dynamic> json) => User()
  ..id = (json['id'] as num?)?.toInt()
  ..userName = json['userName'] as String?
  ..salutation = json['salutation'] as String?
  ..name = json['name'] as String?
  ..gender = json['gender'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..emailId = json['emailId'] as String?
  ..altContactNumber = json['altContactNumber'] as String?
  ..pan = json['pan'] as String?
  ..aadhaarNumber = json['aadhaarNumber'] as String?
  ..permanentAddress = json['permanentAddress'] as String?
  ..permanentCity = json['permanentCity'] as String?
  ..permanentPinCode = json['permanentPinCode'] as String?
  ..correspondenceAddress = json['correspondenceAddress'] as String?
  ..correspondenceCity = json['correspondenceCity'] as String?
  ..correspondencePinCode = json['correspondencePinCode'] as String?
  ..alternateMobileNumber = json['alternatemobilenumber'] as String?
  ..active = json['active'] as bool?
  ..locale = json['locale'] as String?
  ..type = json['type'] as String?
  ..accountLocked = json['accountLocked'] as bool?
  ..accountLockedDate = (json['accountLockedDate'] as num?)?.toInt()
  ..fatherOrHusbandName = json['fatherOrHusbandName'] as String?
  ..relationship = json['relationship'] as String?
  ..signature = json['signature'] as String?
  ..bloodGroup = json['bloodGroup'] as String?
  ..photo = json['photo'] as String?
  ..identificationMark = json['identificationMark'] as String?
  ..createdBy = (json['createdBy'] as num?)?.toInt()
  ..lastModifiedBy = (json['lastModifiedBy'] as num?)?.toInt()
  ..tenantId = json['tenantId'] as String?
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Roles.fromJson(e as Map<String, dynamic>))
      .toList()
  ..uuid = json['uuid'] as String?
  ..createdDate = json['createdDate'] as String?
  ..lastModifiedDate = json['lastModifiedDate'] as String?
  ..dob = json['dob'] as String?
  ..pwdExpiryDate = json['pwdExpiryDate'] as String?;

Map<String, dynamic> _$UserToJson(User instance) => <String, dynamic>{
      'id': instance.id,
      'userName': instance.userName,
      'salutation': instance.salutation,
      'name': instance.name,
      'gender': instance.gender,
      'mobileNumber': instance.mobileNumber,
      'emailId': instance.emailId,
      'altContactNumber': instance.altContactNumber,
      'pan': instance.pan,
      'aadhaarNumber': instance.aadhaarNumber,
      'permanentAddress': instance.permanentAddress,
      'permanentCity': instance.permanentCity,
      'permanentPinCode': instance.permanentPinCode,
      'correspondenceAddress': instance.correspondenceAddress,
      'correspondenceCity': instance.correspondenceCity,
      'correspondencePinCode': instance.correspondencePinCode,
      'alternatemobilenumber': instance.alternateMobileNumber,
      'active': instance.active,
      'locale': instance.locale,
      'type': instance.type,
      'accountLocked': instance.accountLocked,
      'accountLockedDate': instance.accountLockedDate,
      'fatherOrHusbandName': instance.fatherOrHusbandName,
      'relationship': instance.relationship,
      'signature': instance.signature,
      'bloodGroup': instance.bloodGroup,
      'photo': instance.photo,
      'identificationMark': instance.identificationMark,
      'createdBy': instance.createdBy,
      'lastModifiedBy': instance.lastModifiedBy,
      'tenantId': instance.tenantId,
      'roles': instance.roles,
      'uuid': instance.uuid,
      'createdDate': instance.createdDate,
      'lastModifiedDate': instance.lastModifiedDate,
      'dob': instance.dob,
      'pwdExpiryDate': instance.pwdExpiryDate,
    };

Roles _$RolesFromJson(Map<String, dynamic> json) => Roles()
  ..name = json['name'] as String?
  ..code = json['code'] as String?
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$RolesToJson(Roles instance) => <String, dynamic>{
      'name': instance.name,
      'code': instance.code,
      'tenantId': instance.tenantId,
    };
