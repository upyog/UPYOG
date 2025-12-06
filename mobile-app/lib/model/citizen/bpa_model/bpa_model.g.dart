// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bpa_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Bpa _$BpaFromJson(Map<String, dynamic> json) => Bpa()
  ..responseInfo = json['ResponseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
  ..bpaele = (json['BPA'] as List<dynamic>?)
      ?.map((e) => BpaElement.fromJson(e as Map<String, dynamic>))
      .toList()
  ..count = (json['Count'] as num?)?.toInt();

Map<String, dynamic> _$BpaToJson(Bpa instance) => <String, dynamic>{
      'ResponseInfo': instance.responseInfo,
      'BPA': instance.bpaele,
      'Count': instance.count,
    };

BpaElement _$BpaElementFromJson(Map<String, dynamic> json) => BpaElement()
  ..id = json['id'] as String?
  ..applicationNo = json['applicationNo'] as String?
  ..approvalNo = json['approvalNo'] as String?
  ..accountId = json['accountId'] as String?
  ..edcrNumber = json['edcrNumber'] as String?
  ..riskType = json['riskType'] as String?
  ..businessService = json['businessService'] as String?
  ..landId = json['landId'] as String?
  ..tenantId = json['tenantId'] as String?
  ..approvalDate = (json['approvalDate'] as num?)?.toInt()
  ..applicationDate = (json['applicationDate'] as num?)?.toInt()
  ..status = json['status'] as String?
  ..documents = (json['documents'] as List<dynamic>?)
      ?.map((e) => Document.fromJson(e as Map<String, dynamic>))
      .toList()
  ..landInfo = json['landInfo'] == null
      ? null
      : LandInfo.fromJson(json['landInfo'] as Map<String, dynamic>)
  ..workflow = json['workflow'] == null
      ? null
      : Workflow.fromJson(json['workflow'] as Map<String, dynamic>)
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : BpaAdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>)
  ..comment = json['comment'] as String?
  ..action = json['action'] as String?
  ..assignee =
      (json['assignee'] as List<dynamic>?)?.map((e) => e as String).toList()
  ..wfDocuments = (json['wfDocuments'] as List<dynamic>?)
      ?.map((e) => WfDocument.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$BpaElementToJson(BpaElement instance) {
  final val = <String, dynamic>{
    'id': instance.id,
    'applicationNo': instance.applicationNo,
    'approvalNo': instance.approvalNo,
    'accountId': instance.accountId,
    'edcrNumber': instance.edcrNumber,
    'riskType': instance.riskType,
    'businessService': instance.businessService,
    'landId': instance.landId,
    'tenantId': instance.tenantId,
    'approvalDate': instance.approvalDate,
    'applicationDate': instance.applicationDate,
    'status': instance.status,
    'documents': instance.documents,
    'landInfo': instance.landInfo,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('workflow', instance.workflow);
  val['auditDetails'] = instance.auditDetails;
  val['additionalDetails'] = instance.additionalDetails;
  writeNotNull('comment', instance.comment);
  writeNotNull('action', instance.action);
  writeNotNull('assignee', instance.assignee);
  writeNotNull('wfDocuments', instance.wfDocuments);
  return val;
}

Workflow _$WorkflowFromJson(Map<String, dynamic> json) => Workflow()
  ..comments = json['comments'] as String?
  ..comment = json['comment'] as String?
  ..assignee = json['assignee']
  ..assignes = json['assignes']
  ..action = json['action'] as String?
  ..verificationDocuments = (json['varificationDocuments'] as List<dynamic>?)
      ?.map((e) => VerificationDocument.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$WorkflowToJson(Workflow instance) {
  final val = <String, dynamic>{
    'comments': instance.comments,
    'comment': instance.comment,
    'assignee': instance.assignee,
    'assignes': instance.assignes,
    'action': instance.action,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('varificationDocuments', instance.verificationDocuments);
  return val;
}

VerificationDocument _$VerificationDocumentFromJson(
        Map<String, dynamic> json) =>
    VerificationDocument()
      ..documentType = json['documentType'] as String?
      ..fileName = json['fileName'] as String?
      ..fileStoreId = json['fileStoreId'] as String?;

Map<String, dynamic> _$VerificationDocumentToJson(
        VerificationDocument instance) =>
    <String, dynamic>{
      'documentType': instance.documentType,
      'fileName': instance.fileName,
      'fileStoreId': instance.fileStoreId,
    };

BpaAdditionalDetails _$BpaAdditionalDetailsFromJson(
        Map<String, dynamic> json) =>
    BpaAdditionalDetails()
      ..propertyId = json['propertyID'] as String?
      ..serviceType = json['serviceType'] as String?
      ..gisPlaceName = json['GISPlaceName'] as String?
      ..applicationType = json['applicationType'] as String?
      ..typeOfArchitect = json['typeOfArchitect'] as String?
      ..fieldInspectionPending = (json['fieldinspection_pending']
              as List<dynamic>?)
          ?.map(
              (e) => FieldInspectionPending.fromJson(e as Map<String, dynamic>))
          .toList()
      ..propertyAcknowledgementNumber =
          json['propertyAcknowldgementNumber'] as String?
      ..holdingNo = json['holdingNo'] as String?
      ..registrationDetails = json['registrationDetails'] as String?
      ..validityDate = (json['validityDate'] as num?)?.toInt()
      ..pendingApproval = (json['pendingapproval'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList()
      ..landId = json['landId'] as String?
      ..permitNumber = json['permitNumber'] as String?
      ..remarks = json['remarks'] as String?
      ..boundaryWallLength = json['boundaryWallLength'] as String?;

Map<String, dynamic> _$BpaAdditionalDetailsToJson(
    BpaAdditionalDetails instance) {
  final val = <String, dynamic>{};

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('propertyID', instance.propertyId);
  writeNotNull('serviceType', instance.serviceType);
  writeNotNull('GISPlaceName', instance.gisPlaceName);
  writeNotNull('applicationType', instance.applicationType);
  writeNotNull('typeOfArchitect', instance.typeOfArchitect);
  writeNotNull('fieldinspection_pending', instance.fieldInspectionPending);
  writeNotNull(
      'propertyAcknowldgementNumber', instance.propertyAcknowledgementNumber);
  writeNotNull('holdingNo', instance.holdingNo);
  writeNotNull('registrationDetails', instance.registrationDetails);
  writeNotNull('validityDate', instance.validityDate);
  writeNotNull('pendingapproval', instance.pendingApproval);
  writeNotNull('landId', instance.landId);
  writeNotNull('permitNumber', instance.permitNumber);
  writeNotNull('remarks', instance.remarks);
  writeNotNull('boundaryWallLength', instance.boundaryWallLength);
  return val;
}

FieldInspectionPending _$FieldInspectionPendingFromJson(
        Map<String, dynamic> json) =>
    FieldInspectionPending()
      ..docs = (json['docs'] as List<dynamic>?)
          ?.map((e) => Doc.fromJson(e as Map<String, dynamic>))
          .toList()
      ..date = json['date'] as String?
      ..questions = (json['questions'] as List<dynamic>?)
          ?.map((e) => Question.fromJson(e as Map<String, dynamic>))
          .toList()
      ..time = json['time'] as String?;

Map<String, dynamic> _$FieldInspectionPendingToJson(
        FieldInspectionPending instance) =>
    <String, dynamic>{
      'docs': instance.docs,
      'date': instance.date,
      'questions': instance.questions,
      'time': instance.time,
    };

Doc _$DocFromJson(Map<String, dynamic> json) => Doc()
  ..documentType = json['documentType'] as String?
  ..fileStoreId = json['fileStoreId'] as String?
  ..fileStore = json['fileStore'] as String?
  ..fileName = json['fileName'] as String?
  ..dropDownValues = json['dropDownValues'] == null
      ? null
      : DropDownValues.fromJson(json['dropDownValues'] as Map<String, dynamic>);

Map<String, dynamic> _$DocToJson(Doc instance) => <String, dynamic>{
      'documentType': instance.documentType,
      'fileStoreId': instance.fileStoreId,
      'fileStore': instance.fileStore,
      'fileName': instance.fileName,
      'dropDownValues': instance.dropDownValues,
    };

DropDownValues _$DropDownValuesFromJson(Map<String, dynamic> json) =>
    DropDownValues()..value = json['value'] as String?;

Map<String, dynamic> _$DropDownValuesToJson(DropDownValues instance) =>
    <String, dynamic>{
      'value': instance.value,
    };

Question _$QuestionFromJson(Map<String, dynamic> json) => Question()
  ..remarks = json['remarks'] as String?
  ..question = json['question'] as String?
  ..value = json['value'] as String?;

Map<String, dynamic> _$QuestionToJson(Question instance) {
  final val = <String, dynamic>{};

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('remarks', instance.remarks);
  val['question'] = instance.question;
  val['value'] = instance.value;
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

Document _$DocumentFromJson(Map<String, dynamic> json) => Document()
  ..id = json['id'] as String?
  ..title = json['title'] as String?
  ..documentType = json['documentType'] as String?
  ..fileStoreId = json['fileStoreId'] as String?
  ..documentUid = json['documentUid'] as String?
  ..url = json['url'] as String?
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : DocumentAdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>)
  ..auditDetails = json['auditDetails'] as String?;

Map<String, dynamic> _$DocumentToJson(Document instance) {
  final val = <String, dynamic>{
    'id': instance.id,
    'title': instance.title,
    'documentType': instance.documentType,
    'fileStoreId': instance.fileStoreId,
    'documentUid': instance.documentUid,
    'url': instance.url,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('additionalDetails', instance.additionalDetails);
  writeNotNull('auditDetails', instance.auditDetails);
  return val;
}

DocumentAdditionalDetails _$DocumentAdditionalDetailsFromJson(
        Map<String, dynamic> json) =>
    DocumentAdditionalDetails()
      ..uploadedBy = json['uploadedBy'] as String?
      ..uploadedTime = (json['uploadedTime'] as num?)?.toDouble();

Map<String, dynamic> _$DocumentAdditionalDetailsToJson(
        DocumentAdditionalDetails instance) =>
    <String, dynamic>{
      'uploadedBy': instance.uploadedBy,
      'uploadedTime': instance.uploadedTime,
    };

LandInfo _$LandInfoFromJson(Map<String, dynamic> json) => LandInfo()
  ..id = json['id'] as String?
  ..landUId = json['landUId'] as String?
  ..landUniqueRegNo = json['landUniqueRegNo'] as String?
  ..tenantId = json['tenantId'] as String?
  ..status = json['status'] as String?
  ..address = json['address'] == null
      ? null
      : Address.fromJson(json['address'] as Map<String, dynamic>)
  ..ownershipCategory = json['ownershipCategory'] as String?
  ..owners = (json['owners'] as List<dynamic>?)
      ?.map((e) => Owner.fromJson(e as Map<String, dynamic>))
      .toList()
  ..institution = json['institution'] as String?
  ..source = json['source'] as String?
  ..channel = json['channel'] as String?
  ..documents = json['documents'] as String?
  ..unit = (json['unit'] as List<dynamic>?)
      ?.map((e) => Unit.fromJson(e as Map<String, dynamic>))
      .toList()
  ..additionalDetails = json['additionalDetails'] as String?
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$LandInfoToJson(LandInfo instance) {
  final val = <String, dynamic>{
    'id': instance.id,
    'landUId': instance.landUId,
    'landUniqueRegNo': instance.landUniqueRegNo,
    'tenantId': instance.tenantId,
    'status': instance.status,
    'address': instance.address,
    'ownershipCategory': instance.ownershipCategory,
    'owners': instance.owners,
    'institution': instance.institution,
    'source': instance.source,
    'channel': instance.channel,
    'documents': instance.documents,
    'unit': instance.unit,
    'additionalDetails': instance.additionalDetails,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('auditDetails', instance.auditDetails);
  return val;
}

Address _$AddressFromJson(Map<String, dynamic> json) => Address()
  ..tenantId = json['tenantId'] as String?
  ..doorNo = json['doorNo'] as String?
  ..plotNo = json['plotNo'] as String?
  ..id = json['id'] as String?
  ..landmark = json['landmark'] as String?
  ..city = json['city'] as String?
  ..district = json['district'] as String?
  ..region = json['region'] as String?
  ..state = json['state'] as String?
  ..country = json['country'] as String?
  ..pincode = json['pincode'] as String?
  ..additionDetails = json['additionDetails'] as String?
  ..buildingName = json['buildingName'] as String?
  ..street = json['street'] as String?
  ..locality = json['locality'] == null
      ? null
      : Locality.fromJson(json['locality'] as Map<String, dynamic>)
  ..geoLocation = json['geoLocation'] == null
      ? null
      : GeoLocation.fromJson(json['geoLocation'] as Map<String, dynamic>)
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$AddressToJson(Address instance) {
  final val = <String, dynamic>{
    'tenantId': instance.tenantId,
    'doorNo': instance.doorNo,
    'plotNo': instance.plotNo,
    'id': instance.id,
    'landmark': instance.landmark,
    'city': instance.city,
    'district': instance.district,
    'region': instance.region,
    'state': instance.state,
    'country': instance.country,
    'pincode': instance.pincode,
    'additionDetails': instance.additionDetails,
    'buildingName': instance.buildingName,
    'street': instance.street,
    'locality': instance.locality,
    'geoLocation': instance.geoLocation,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('auditDetails', instance.auditDetails);
  return val;
}

WfDocument _$WfDocumentFromJson(Map<String, dynamic> json) => WfDocument()
  ..documentType = json['documentType'] as String?
  ..fileName = json['fileName'] as String?
  ..fileStoreId = json['fileStoreId'] as String?;

Map<String, dynamic> _$WfDocumentToJson(WfDocument instance) =>
    <String, dynamic>{
      'documentType': instance.documentType,
      'fileName': instance.fileName,
      'fileStoreId': instance.fileStoreId,
    };

GeoLocation _$GeoLocationFromJson(Map<String, dynamic> json) => GeoLocation()
  ..latitude = (json['latitude'] as num?)?.toDouble()
  ..longitude = (json['longitude'] as num?)?.toDouble()
  ..additionalDetails = (json['additionalDetails'] as num?)?.toDouble();

Map<String, dynamic> _$GeoLocationToJson(GeoLocation instance) =>
    <String, dynamic>{
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'additionalDetails': instance.additionalDetails,
    };

Locality _$LocalityFromJson(Map<String, dynamic> json) => Locality()
  ..code = json['code'] as String?
  ..name = json['name'] as String?
  ..label = json['label'] as String?
  ..latitude = json['latitude'] as String?
  ..longitude = json['longitude'] as String?
  ..children = json['children'] as List<dynamic>?
  ..materializedPath = json['materializedPath'] as String?;

Map<String, dynamic> _$LocalityToJson(Locality instance) => <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'label': instance.label,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'children': instance.children,
      'materializedPath': instance.materializedPath,
    };

Owner _$OwnerFromJson(Map<String, dynamic> json) => Owner()
  ..tenantId = json['tenantId'] as String?
  ..name = json['name'] as String?
  ..ownerId = json['ownerId'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..gender = json['gender'] as String?
  ..fatherOrHusbandName = json['fatherOrHusbandName'] as String?
  ..correspondenceAddress = json['correspondenceAddress'] as String?
  ..isPrimaryOwner = _stringToBool(json['isPrimaryOwner'])
  ..ownerShipPercentage = json['ownerShipPercentage'] as String?
  ..ownerType = json['ownerType'] as String?
  ..status = json['status'] as bool?
  ..institutionId = json['institutionId'] as String?
  ..documents = json['documents'] as String?
  ..relationship = json['relationship'] as String?
  ..additionalDetails = json['additionalDetails'] as String?
  ..id = (json['id'] as num?)?.toInt()
  ..uuid = json['uuid'] as String?
  ..userName = json['userName'] as String?
  ..password = json['password'] as String?
  ..salutation = json['salutation'] as String?
  ..emailId = json['emailId'] as String?
  ..altContactNumber = json['altContactNumber'] as String?
  ..pan = json['pan'] as String?
  ..aadhaarNumber = json['aadhaarNumber'] as String?
  ..permanentAddress = json['permanentAddress'] as String?
  ..permanentCity = json['permanentCity'] as String?
  ..permanentPinCode = json['permanentPinCode'] as String?
  ..correspondenceCity = json['correspondenceCity'] as String?
  ..correspondencePinCode = json['correspondencePinCode'] as String?
  ..active = json['active'] as bool?
  ..dob = (json['dob'] as num?)?.toInt()
  ..pwdExpiryDate = (json['pwdExpiryDate'] as num?)?.toInt()
  ..locale = json['locale'] as String?
  ..type = json['type'] as String?
  ..signature = json['signature'] as String?
  ..accountLocked = json['accountLocked'] as bool?
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Role.fromJson(e as Map<String, dynamic>))
      .toList()
  ..bloodGroup = json['bloodGroup'] as String?
  ..identificationMark = json['identificationMark'] as String?
  ..photo = json['photo'] as String?
  ..createdBy = json['createdBy'] as String?
  ..createdDate = json['createdDate'] as String?
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..lastModifiedDate = json['lastModifiedDate'] as String?
  ..otpReference = json['otpReference'] as String?
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$OwnerToJson(Owner instance) => <String, dynamic>{
      'tenantId': instance.tenantId,
      'name': instance.name,
      'ownerId': instance.ownerId,
      'mobileNumber': instance.mobileNumber,
      'gender': instance.gender,
      'fatherOrHusbandName': instance.fatherOrHusbandName,
      'correspondenceAddress': instance.correspondenceAddress,
      'isPrimaryOwner': _boolToString(instance.isPrimaryOwner),
      'ownerShipPercentage': instance.ownerShipPercentage,
      'ownerType': instance.ownerType,
      'status': instance.status,
      'institutionId': instance.institutionId,
      'documents': instance.documents,
      'relationship': instance.relationship,
      'additionalDetails': instance.additionalDetails,
      'id': instance.id,
      'uuid': instance.uuid,
      'userName': instance.userName,
      'password': instance.password,
      'salutation': instance.salutation,
      'emailId': instance.emailId,
      'altContactNumber': instance.altContactNumber,
      'pan': instance.pan,
      'aadhaarNumber': instance.aadhaarNumber,
      'permanentAddress': instance.permanentAddress,
      'permanentCity': instance.permanentCity,
      'permanentPinCode': instance.permanentPinCode,
      'correspondenceCity': instance.correspondenceCity,
      'correspondencePinCode': instance.correspondencePinCode,
      'active': instance.active,
      'dob': instance.dob,
      'pwdExpiryDate': instance.pwdExpiryDate,
      'locale': instance.locale,
      'type': instance.type,
      'signature': instance.signature,
      'accountLocked': instance.accountLocked,
      'roles': instance.roles,
      'bloodGroup': instance.bloodGroup,
      'identificationMark': instance.identificationMark,
      'photo': instance.photo,
      'createdBy': instance.createdBy,
      'createdDate': instance.createdDate,
      'lastModifiedBy': instance.lastModifiedBy,
      'lastModifiedDate': instance.lastModifiedDate,
      'otpReference': instance.otpReference,
      'auditDetails': instance.auditDetails,
    };

Role _$RoleFromJson(Map<String, dynamic> json) => Role()
  ..id = json['id'] as String?
  ..name = json['name'] as String?
  ..code = json['code'] as String?
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$RoleToJson(Role instance) => <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'code': instance.code,
      'tenantId': instance.tenantId,
    };

Unit _$UnitFromJson(Map<String, dynamic> json) => Unit()
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..floorNo = json['floorNo'] as String?
  ..unitType = json['unitType'] as String?
  ..usageCategory = json['usageCategory'] as String?
  ..occupancyType = json['occupancyType'] as String?
  ..occupancyDate = (json['occupancyDate'] as num?)?.toInt()
  ..additionalDetails = json['additionalDetails'] as String?
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$UnitToJson(Unit instance) => <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'floorNo': instance.floorNo,
      'unitType': instance.unitType,
      'usageCategory': instance.usageCategory,
      'occupancyType': instance.occupancyType,
      'occupancyDate': instance.occupancyDate,
      'additionalDetails': instance.additionalDetails,
      'auditDetails': instance.auditDetails,
    };

ResponseInfo _$ResponseInfoFromJson(Map<String, dynamic> json) => ResponseInfo()
  ..apiId = json['apiId'] as String?
  ..ver = json['ver'] as String?
  ..ts = json['ts'] as String?
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
