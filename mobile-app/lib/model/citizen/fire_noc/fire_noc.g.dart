// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'fire_noc.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FireNocModel _$FireNocModelFromJson(Map<String, dynamic> json) => FireNocModel()
  ..responseInfo = json['ResponseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
  ..fireNoCs = (json['FireNOCs'] as List<dynamic>?)
      ?.map((e) => FireNoc.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$FireNocModelToJson(FireNocModel instance) =>
    <String, dynamic>{
      'ResponseInfo': instance.responseInfo,
      'FireNOCs': instance.fireNoCs,
    };

FireNoc _$FireNocFromJson(Map<String, dynamic> json) => FireNoc()
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..fireNocNumber = json['fireNOCNumber'] as String?
  ..fireNocDetails = json['fireNOCDetails'] == null
      ? null
      : FireNocDetails.fromJson(json['fireNOCDetails'] as Map<String, dynamic>)
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..provisionFireNocNumber = json['provisionFireNOCNumber'] as String?;

Map<String, dynamic> _$FireNocToJson(FireNoc instance) => <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      if (instance.fireNocNumber case final value?) 'fireNOCNumber': value,
      'fireNOCDetails': instance.fireNocDetails,
      'auditDetails': instance.auditDetails,
      if (instance.provisionFireNocNumber case final value?)
        'provisionFireNOCNumber': value,
    };

AuditDetails _$AuditDetailsFromJson(Map<String, dynamic> json) => AuditDetails()
  ..createdBy = json['createdBy'] as String?
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..createdTime = (json['createdTime'] as num?)?.toInt()
  ..lastModifiedTime = (json['lastModifiedTime'] as num?)?.toInt();

Map<String, dynamic> _$AuditDetailsToJson(AuditDetails instance) =>
    <String, dynamic>{
      'createdBy': instance.createdBy,
      'lastModifiedBy': instance.lastModifiedBy,
      'createdTime': instance.createdTime,
      'lastModifiedTime': instance.lastModifiedTime,
    };

FireNocDetails _$FireNocDetailsFromJson(Map<String, dynamic> json) =>
    FireNocDetails()
      ..id = json['id'] as String?
      ..applicationNumber = json['applicationNumber'] as String?
      ..status = json['status'] as String?
      ..fireNocType = json['fireNOCType'] as String?
      ..firestationId = json['firestationId'] as String?
      ..applicationDate = (json['applicationDate'] as num?)?.toInt()
      ..financialYear = json['financialYear'] as String?
      ..issuedDate = (json['issuedDate'] as num?)?.toInt()
      ..validFrom = (json['validFrom'] as num?)?.toInt()
      ..validTo = (json['validTo'] as num?)?.toInt()
      ..action = json['action'] as String?
      ..channel = json['channel'] as String?
      ..noOfBuildings = json['noOfBuildings'] as String?
      ..buildings = (json['buildings'] as List<dynamic>?)
          ?.map((e) => Building.fromJson(e as Map<String, dynamic>))
          .toList()
      ..propertyDetails = json['propertyDetails'] == null
          ? null
          : PropertyDetails.fromJson(
              json['propertyDetails'] as Map<String, dynamic>)
      ..applicantDetails = json['applicantDetails'] == null
          ? null
          : ApplicantDetails.fromJson(
              json['applicantDetails'] as Map<String, dynamic>)
      ..additionalDetail = json['additionalDetail'] == null
          ? null
          : AdditionalDetail.fromJson(
              json['additionalDetail'] as Map<String, dynamic>)
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$FireNocDetailsToJson(FireNocDetails instance) =>
    <String, dynamic>{
      'id': instance.id,
      'applicationNumber': instance.applicationNumber,
      'status': instance.status,
      'fireNOCType': instance.fireNocType,
      'firestationId': instance.firestationId,
      'applicationDate': instance.applicationDate,
      'financialYear': instance.financialYear,
      if (instance.issuedDate case final value?) 'issuedDate': value,
      if (instance.validFrom case final value?) 'validFrom': value,
      if (instance.validTo case final value?) 'validTo': value,
      'action': instance.action,
      'channel': instance.channel,
      'noOfBuildings': instance.noOfBuildings,
      'buildings': instance.buildings,
      'propertyDetails': instance.propertyDetails,
      'applicantDetails': instance.applicantDetails,
      'additionalDetail': instance.additionalDetail,
      'auditDetails': instance.auditDetails,
    };

AdditionalDetail _$AdditionalDetailFromJson(Map<String, dynamic> json) =>
    AdditionalDetail()
      ..comment = json['comment'] as String?
      ..assignee =
          (json['assignee'] as List<dynamic>?)?.map((e) => e as String).toList()
      ..documents = (json['documents'] as List<dynamic>?)
          ?.map((e) => PurpleDocument.fromJson(e as Map<String, dynamic>))
          .toList()
      ..wfDocuments = (json['wfDocuments'] as List<dynamic>?)
          ?.map((e) => WfDocument.fromJson(e as Map<String, dynamic>))
          .toList()
      ..ownerAuditionalDetail = json['ownerAuditionalDetail'] == null
          ? null
          : DitionalDetail.fromJson(
              json['ownerAuditionalDetail'] as Map<String, dynamic>);

Map<String, dynamic> _$AdditionalDetailToJson(AdditionalDetail instance) =>
    <String, dynamic>{
      'comment': instance.comment,
      if (instance.assignee case final value?) 'assignee': value,
      'documents': instance.documents,
      'wfDocuments': instance.wfDocuments,
      'ownerAuditionalDetail': instance.ownerAuditionalDetail,
    };

PurpleDocument _$PurpleDocumentFromJson(Map<String, dynamic> json) =>
    PurpleDocument()
      ..link = json['link'] as String?
      ..name = json['name'] as String?
      ..title = json['title'] as String?
      ..linkText = json['linkText'] as String?
      ..fileStoreId = json['fileStoreId'] as String?;

Map<String, dynamic> _$PurpleDocumentToJson(PurpleDocument instance) =>
    <String, dynamic>{
      'link': instance.link,
      'name': instance.name,
      'title': instance.title,
      'linkText': instance.linkText,
      'fileStoreId': instance.fileStoreId,
    };

DitionalDetail _$DitionalDetailFromJson(Map<String, dynamic> json) =>
    DitionalDetail()
      ..id = json['id'] as String?
      ..documents = (json['documents'] as List<dynamic>?)
          ?.map((e) =>
              OwnerAuditionalDetailDocument.fromJson(e as Map<String, dynamic>))
          .toList()
      ..institutionName = json['institutionName'] as String?
      ..telephoneNumber = json['telephoneNumber'] as String?
      ..institutionDesignation = json['institutionDesignation'] as String?;

Map<String, dynamic> _$DitionalDetailToJson(DitionalDetail instance) =>
    <String, dynamic>{
      'id': instance.id,
      'documents': instance.documents,
      if (instance.institutionName case final value?) 'institutionName': value,
      if (instance.telephoneNumber case final value?) 'telephoneNumber': value,
      if (instance.institutionDesignation case final value?)
        'institutionDesignation': value,
    };

OwnerAuditionalDetailDocument _$OwnerAuditionalDetailDocumentFromJson(
        Map<String, dynamic> json) =>
    OwnerAuditionalDetailDocument()
      ..tenantId = json['tenantId'] as String?
      ..fileStoreId = json['fileStoreId'] as String?
      ..documentType = json['documentType'] as String?;

Map<String, dynamic> _$OwnerAuditionalDetailDocumentToJson(
        OwnerAuditionalDetailDocument instance) =>
    <String, dynamic>{
      'tenantId': instance.tenantId,
      'fileStoreId': instance.fileStoreId,
      'documentType': instance.documentType,
    };

ApplicantDetails _$ApplicantDetailsFromJson(Map<String, dynamic> json) =>
    ApplicantDetails()
      ..ownerShipType = json['ownerShipType'] as String?
      ..owners = (json['owners'] as List<dynamic>?)
          ?.map((e) => Owner.fromJson(e as Map<String, dynamic>))
          .toList()
      ..additionalDetail = json['additionalDetail'] == null
          ? null
          : DitionalDetail.fromJson(
              json['additionalDetail'] as Map<String, dynamic>);

Map<String, dynamic> _$ApplicantDetailsToJson(ApplicantDetails instance) =>
    <String, dynamic>{
      'ownerShipType': instance.ownerShipType,
      'owners': instance.owners,
      'additionalDetail': instance.additionalDetail,
    };

Owner _$OwnerFromJson(Map<String, dynamic> json) => Owner()
  ..id = (json['id'] as num?)?.toInt()
  ..userName = json['userName'] as String?
  ..useruuid = json['useruuid'] as String?
  ..active = json['active'] as bool?
  ..ownerType = json['ownerType'] as String?
  ..tenantId = json['tenantId'] as String?
  ..name = json['name'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..emailId = json['emailId'] as String?
  ..type = json['type'] as String?
  ..accountLocked = json['accountLocked'] as bool?
  ..accountLockedDate = (json['accountLockedDate'] as num?)?.toInt()
  ..createdBy = (json['createdBy'] as num?)?.toInt()
  ..lastModifiedBy = (json['lastModifiedBy'] as num?)?.toInt()
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Role.fromJson(e as Map<String, dynamic>))
      .toList()
  ..uuid = json['uuid'] as String?
  ..createdDate = (json['createdDate'] as num?)?.toInt()
  ..lastModifiedDate = (json['lastModifiedDate'] as num?)?.toInt()
  ..dob = (json['dob'] as num?)?.toInt()
  ..pwdExpiryDate = (json['pwdExpiryDate'] as num?)?.toInt()
  ..relationship = json['relationship'] as String?
  ..fatherOrHusbandName = json['fatherOrHusbandName'] as String?
  ..gender = json['gender'] as String?
  ..correspondenceAddress = json['correspondenceAddress'] as String?
  ..pan = json['pan'] as String?
  ..permanentAddress = json['permanentAddress'] as String?;

Map<String, dynamic> _$OwnerToJson(Owner instance) => <String, dynamic>{
      'id': instance.id,
      'userName': instance.userName,
      'useruuid': instance.useruuid,
      'active': instance.active,
      'ownerType': instance.ownerType,
      'tenantId': instance.tenantId,
      'name': instance.name,
      'mobileNumber': instance.mobileNumber,
      'emailId': instance.emailId,
      'type': instance.type,
      'accountLocked': instance.accountLocked,
      'accountLockedDate': instance.accountLockedDate,
      'createdBy': instance.createdBy,
      'lastModifiedBy': instance.lastModifiedBy,
      'roles': instance.roles,
      'uuid': instance.uuid,
      'createdDate': instance.createdDate,
      'lastModifiedDate': instance.lastModifiedDate,
      'dob': instance.dob,
      'pwdExpiryDate': instance.pwdExpiryDate,
      'relationship': instance.relationship,
      'fatherOrHusbandName': instance.fatherOrHusbandName,
      'gender': instance.gender,
      'correspondenceAddress': instance.correspondenceAddress,
      if (instance.pan case final value?) 'pan': value,
      if (instance.permanentAddress case final value?)
        'permanentAddress': value,
    };

Role _$RoleFromJson(Map<String, dynamic> json) => Role()
  ..code = json['code'] as String?
  ..name = json['name'] as String?
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$RoleToJson(Role instance) => <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'tenantId': instance.tenantId,
    };

Building _$BuildingFromJson(Map<String, dynamic> json) => Building()
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..name = json['name'] as String?
  ..usageType = json['usageType'] as String?
  ..uoms = (json['uoms'] as List<dynamic>?)
      ?.map((e) => Uom.fromJson(e as Map<String, dynamic>))
      .toList()
  ..uomsMap = json['uomsMap'] as Map<String, dynamic>?
  ..applicationDocuments = json['applicationDocuments'] as List<dynamic>?;

Map<String, dynamic> _$BuildingToJson(Building instance) => <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'name': instance.name,
      'usageType': instance.usageType,
      'uoms': instance.uoms,
      if (instance.uomsMap case final value?) 'uomsMap': value,
      'applicationDocuments': instance.applicationDocuments,
    };

Uom _$UomFromJson(Map<String, dynamic> json) => Uom()
  ..id = json['id'] as String?
  ..code = json['code'] as String?
  ..value = (json['value'] as num?)?.toInt()
  ..isActiveUom = json['isActiveUom'] as bool?
  ..active = json['active'] as bool?;

Map<String, dynamic> _$UomToJson(Uom instance) => <String, dynamic>{
      'id': instance.id,
      'code': instance.code,
      'value': instance.value,
      'isActiveUom': instance.isActiveUom,
      'active': instance.active,
    };

PropertyDetails _$PropertyDetailsFromJson(Map<String, dynamic> json) =>
    PropertyDetails()
      ..id = json['id'] as String?
      ..address = json['address'] == null
          ? null
          : Address.fromJson(json['address'] as Map<String, dynamic>)
      ..propertyId = json['propertyId'] as String?;

Map<String, dynamic> _$PropertyDetailsToJson(PropertyDetails instance) =>
    <String, dynamic>{
      'id': instance.id,
      'address': instance.address,
      if (instance.propertyId case final value?) 'propertyId': value,
    };

Address _$AddressFromJson(Map<String, dynamic> json) => Address()
  ..tenantId = json['tenantId'] as String?
  ..city = json['city'] as String?
  ..locality = json['locality'] == null
      ? null
      : Locality.fromJson(json['locality'] as Map<String, dynamic>)
  ..doorNo = json['doorNo'] as String?
  ..buildingName = json['buildingName'] as String?
  ..street = json['street'] as String?
  ..pincode = json['pincode'] as String?;

Map<String, dynamic> _$AddressToJson(Address instance) => <String, dynamic>{
      'tenantId': instance.tenantId,
      'city': instance.city,
      'locality': instance.locality,
      if (instance.doorNo case final value?) 'doorNo': value,
      if (instance.buildingName case final value?) 'buildingName': value,
      if (instance.street case final value?) 'street': value,
      if (instance.pincode case final value?) 'pincode': value,
    };

Locality _$LocalityFromJson(Map<String, dynamic> json) =>
    Locality()..code = json['code'] as String?;

Map<String, dynamic> _$LocalityToJson(Locality instance) => <String, dynamic>{
      'code': instance.code,
    };

ResponseInfo _$ResponseInfoFromJson(Map<String, dynamic> json) => ResponseInfo()
  ..apiId = json['apiId'] as String?
  ..ver = json['ver'] as String?
  ..ts = json['ts']
  ..resMsgId = json['resMsgId'] as String?
  ..msgId = json['msgId'] as String?
  ..status = json['status'] as String?;

Map<String, dynamic> _$ResponseInfoToJson(ResponseInfo instance) =>
    <String, dynamic>{
      'apiId': instance.apiId,
      'ver': instance.ver,
      'ts': instance.ts,
      'resMsgId': instance.resMsgId,
      'msgId': instance.msgId,
      'status': instance.status,
    };

WfDocument _$WfDocumentFromJson(Map<String, dynamic> json) => WfDocument()
  ..fileName = json['fileName'] as String?
  ..fileStoreId = json['fileStoreId'] as String?
  ..documentType = json['documentType'] as String?;

Map<String, dynamic> _$WfDocumentToJson(WfDocument instance) =>
    <String, dynamic>{
      'fileName': instance.fileName,
      'fileStoreId': instance.fileStoreId,
      'documentType': instance.documentType,
    };
