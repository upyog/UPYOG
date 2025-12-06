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

Map<String, dynamic> _$FireNocToJson(FireNoc instance) {
  final val = <String, dynamic>{
    'id': instance.id,
    'tenantId': instance.tenantId,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('fireNOCNumber', instance.fireNocNumber);
  val['fireNOCDetails'] = instance.fireNocDetails;
  val['auditDetails'] = instance.auditDetails;
  writeNotNull('provisionFireNOCNumber', instance.provisionFireNocNumber);
  return val;
}

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

Map<String, dynamic> _$FireNocDetailsToJson(FireNocDetails instance) {
  final val = <String, dynamic>{
    'id': instance.id,
    'applicationNumber': instance.applicationNumber,
    'status': instance.status,
    'fireNOCType': instance.fireNocType,
    'firestationId': instance.firestationId,
    'applicationDate': instance.applicationDate,
    'financialYear': instance.financialYear,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('issuedDate', instance.issuedDate);
  writeNotNull('validFrom', instance.validFrom);
  writeNotNull('validTo', instance.validTo);
  val['action'] = instance.action;
  val['channel'] = instance.channel;
  val['noOfBuildings'] = instance.noOfBuildings;
  val['buildings'] = instance.buildings;
  val['propertyDetails'] = instance.propertyDetails;
  val['applicantDetails'] = instance.applicantDetails;
  val['additionalDetail'] = instance.additionalDetail;
  val['auditDetails'] = instance.auditDetails;
  return val;
}

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

Map<String, dynamic> _$AdditionalDetailToJson(AdditionalDetail instance) {
  final val = <String, dynamic>{
    'comment': instance.comment,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('assignee', instance.assignee);
  val['documents'] = instance.documents;
  val['wfDocuments'] = instance.wfDocuments;
  val['ownerAuditionalDetail'] = instance.ownerAuditionalDetail;
  return val;
}

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

Map<String, dynamic> _$DitionalDetailToJson(DitionalDetail instance) {
  final val = <String, dynamic>{
    'id': instance.id,
    'documents': instance.documents,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('institutionName', instance.institutionName);
  writeNotNull('telephoneNumber', instance.telephoneNumber);
  writeNotNull('institutionDesignation', instance.institutionDesignation);
  return val;
}

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

Map<String, dynamic> _$OwnerToJson(Owner instance) {
  final val = <String, dynamic>{
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
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('pan', instance.pan);
  writeNotNull('permanentAddress', instance.permanentAddress);
  return val;
}

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

Map<String, dynamic> _$BuildingToJson(Building instance) {
  final val = <String, dynamic>{
    'id': instance.id,
    'tenantId': instance.tenantId,
    'name': instance.name,
    'usageType': instance.usageType,
    'uoms': instance.uoms,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('uomsMap', instance.uomsMap);
  val['applicationDocuments'] = instance.applicationDocuments;
  return val;
}

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

Map<String, dynamic> _$PropertyDetailsToJson(PropertyDetails instance) {
  final val = <String, dynamic>{
    'id': instance.id,
    'address': instance.address,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('propertyId', instance.propertyId);
  return val;
}

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

Map<String, dynamic> _$AddressToJson(Address instance) {
  final val = <String, dynamic>{
    'tenantId': instance.tenantId,
    'city': instance.city,
    'locality': instance.locality,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('doorNo', instance.doorNo);
  writeNotNull('buildingName', instance.buildingName);
  writeNotNull('street', instance.street);
  writeNotNull('pincode', instance.pincode);
  return val;
}

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
