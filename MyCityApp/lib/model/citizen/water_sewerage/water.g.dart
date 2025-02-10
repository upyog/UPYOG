// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'water.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Water _$WaterFromJson(Map<String, dynamic> json) => Water()
  ..responseInfo = json['ResponseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
  ..waterConnection = (json['WaterConnection'] as List<dynamic>?)
      ?.map((e) => WaterConnection.fromJson(e as Map<String, dynamic>))
      .toList()
  ..totalCount = (json['TotalCount'] as num?)?.toInt();

Map<String, dynamic> _$WaterToJson(Water instance) => <String, dynamic>{
      'ResponseInfo': instance.responseInfo,
      'WaterConnection': instance.waterConnection,
      'TotalCount': instance.totalCount,
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

WaterConnection _$WaterConnectionFromJson(Map<String, dynamic> json) =>
    WaterConnection()
      ..id = json['id'] as String?
      ..tenantId = json['tenantId'] as String?
      ..propertyId = json['propertyId'] as String?
      ..applicationNo = json['applicationNo'] as String?
      ..applicationStatus = json['applicationStatus'] as String?
      ..status = json['status'] as String?
      ..connectionNo = json['connectionNo']
      ..oldConnectionNo = json['oldConnectionNo']
      ..documents = (json['documents'] as List<dynamic>?)
          ?.map((e) => WDocument.fromJson(e as Map<String, dynamic>))
          .toList()
      ..plumberInfo = (json['plumberInfo'] as List<dynamic>?)
          ?.map((e) => PlumberInfo.fromJson(e as Map<String, dynamic>))
          .toList()
      ..roadType = json['roadType']
      ..roadCuttingArea = (json['roadCuttingArea'] as num?)?.toInt()
      ..roadCuttingInfo = (json['roadCuttingInfo'] as List<dynamic>?)
          ?.map((e) => RoadCuttingInfo.fromJson(e as Map<String, dynamic>))
          .toList()
      ..connectionExecutionDate =
          (json['connectionExecutionDate'] as num?)?.toInt()
      ..connectionCategory = json['connectionCategory']
      ..connectionType = json['connectionType'] as String?
      ..additionalDetails = json['additionalDetails'] == null
          ? null
          : AdditionalDetails.fromJson(
              json['additionalDetails'] as Map<String, dynamic>)
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
      ..processInstance = json['processInstance'] == null
          ? null
          : ProcessInstance.fromJson(
              json['processInstance'] as Map<String, dynamic>)
      ..applicationType = json['applicationType'] as String?
      ..dateEffectiveFrom = (json['dateEffectiveFrom'] as num?)?.toInt()
      ..connectionHolders = (json['connectionHolders'] as List<dynamic>?)
          ?.map((e) => ConnectionHolder.fromJson(e as Map<String, dynamic>))
          .toList()
      ..oldApplication = json['oldApplication'] as bool?
      ..channel = json['channel'] as String?
      ..disconnectionExecutionDate =
          (json['disconnectionExecutionDate'] as num?)?.toInt()
      ..waterSource = json['waterSource'] as String?
      ..meterId = json['meterId'] as String?
      ..meterInstallationDate = (json['meterInstallationDate'] as num?)?.toInt()
      ..proposedPipeSize = (json['proposedPipeSize'] as num?)?.toDouble()
      ..proposedTaps = (json['proposedTaps'] as num?)?.toInt()
      ..noOfWaterClosets = (json['noOfWaterClosets'] as num?)?.toInt()
      ..noOfToilets = (json['noOfToilets'] as num?)?.toInt()
      ..pipeSize = (json['pipeSize'] as num?)?.toDouble()
      ..noOfTaps = (json['noOfTaps'] as num?)?.toInt()
      ..isDisconnectionTemporary = json['isDisconnectionTemporary'] as bool?
      ..disconnectionReason = json['disconnectionReason']
      ..comment = json['comment'] as String?
      ..action = json['action'] as String?
      ..assignes = (json['assignes'] as List<dynamic>?)
          ?.map((e) => Assignes.fromJson(e as Map<String, dynamic>))
          .toList()
      ..assignee =
          (json['assignee'] as List<dynamic>?)?.map((e) => e as String).toList()
      ..wfDocuments = (json['wfDocuments'] as List<dynamic>?)
          ?.map((e) => WfDocument.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$WaterConnectionToJson(WaterConnection instance) {
  final val = <String, dynamic>{
    'id': instance.id,
    'tenantId': instance.tenantId,
    'propertyId': instance.propertyId,
    'applicationNo': instance.applicationNo,
    'applicationStatus': instance.applicationStatus,
    'status': instance.status,
    'connectionNo': instance.connectionNo,
    'oldConnectionNo': instance.oldConnectionNo,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('documents', instance.documents);
  writeNotNull('plumberInfo', instance.plumberInfo);
  val['roadType'] = instance.roadType;
  val['roadCuttingArea'] = instance.roadCuttingArea;
  writeNotNull('roadCuttingInfo', instance.roadCuttingInfo);
  val['connectionExecutionDate'] = instance.connectionExecutionDate;
  val['connectionCategory'] = instance.connectionCategory;
  val['connectionType'] = instance.connectionType;
  val['additionalDetails'] = instance.additionalDetails;
  val['auditDetails'] = instance.auditDetails;
  val['processInstance'] = instance.processInstance;
  val['applicationType'] = instance.applicationType;
  val['dateEffectiveFrom'] = instance.dateEffectiveFrom;
  val['connectionHolders'] = instance.connectionHolders;
  val['oldApplication'] = instance.oldApplication;
  val['channel'] = instance.channel;
  val['disconnectionExecutionDate'] = instance.disconnectionExecutionDate;
  val['waterSource'] = instance.waterSource;
  val['meterId'] = instance.meterId;
  val['meterInstallationDate'] = instance.meterInstallationDate;
  val['proposedPipeSize'] = instance.proposedPipeSize;
  val['proposedTaps'] = instance.proposedTaps;
  val['noOfWaterClosets'] = instance.noOfWaterClosets;
  val['noOfToilets'] = instance.noOfToilets;
  val['pipeSize'] = instance.pipeSize;
  val['noOfTaps'] = instance.noOfTaps;
  val['isDisconnectionTemporary'] = instance.isDisconnectionTemporary;
  val['disconnectionReason'] = instance.disconnectionReason;
  writeNotNull('comment', instance.comment);
  writeNotNull('action', instance.action);
  writeNotNull('assignes', instance.assignes);
  writeNotNull('assignee', instance.assignee);
  writeNotNull('wfDocuments', instance.wfDocuments);
  return val;
}

Assignes _$AssignesFromJson(Map<String, dynamic> json) =>
    Assignes()..uuid = json['uuid'] as String?;

Map<String, dynamic> _$AssignesToJson(Assignes instance) => <String, dynamic>{
      'uuid': instance.uuid,
    };

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

WDocument _$WDocumentFromJson(Map<String, dynamic> json) => WDocument()
  ..id = json['id'] as String?
  ..documentType = json['documentType'] as String?
  ..fileStoreId = json['fileStoreId'] as String?
  ..documentUid = json['documentUid'] as String?
  ..auditDetails = json['auditDetails']
  ..status = json['status'] as String?;

Map<String, dynamic> _$WDocumentToJson(WDocument instance) => <String, dynamic>{
      'id': instance.id,
      'documentType': instance.documentType,
      'fileStoreId': instance.fileStoreId,
      'documentUid': instance.documentUid,
      'auditDetails': instance.auditDetails,
      'status': instance.status,
    };

PlumberInfo _$PlumberInfoFromJson(Map<String, dynamic> json) => PlumberInfo()
  ..id = json['id'] as String?
  ..name = json['name'] as String?
  ..licenseNo = json['licenseNo'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..gender = json['gender']
  ..fatherOrHusbandName = json['fatherOrHusbandName']
  ..correspondenceAddress = json['correspondenceAddress']
  ..relationship = json['relationship']
  ..additionalDetails = json['additionalDetails']
  ..auditDetails = json['auditDetails'];

Map<String, dynamic> _$PlumberInfoToJson(PlumberInfo instance) {
  final val = <String, dynamic>{};

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('id', instance.id);
  val['name'] = instance.name;
  val['licenseNo'] = instance.licenseNo;
  val['mobileNumber'] = instance.mobileNumber;
  writeNotNull('gender', instance.gender);
  writeNotNull('fatherOrHusbandName', instance.fatherOrHusbandName);
  writeNotNull('correspondenceAddress', instance.correspondenceAddress);
  writeNotNull('relationship', instance.relationship);
  writeNotNull('additionalDetails', instance.additionalDetails);
  writeNotNull('auditDetails', instance.auditDetails);
  return val;
}

RoadCuttingInfo _$RoadCuttingInfoFromJson(Map<String, dynamic> json) =>
    RoadCuttingInfo()
      ..id = json['id'] as String?
      ..roadType = json['roadType'] as String?
      ..roadCuttingArea = (json['roadCuttingArea'] as num?)?.toInt()
      ..auditDetails = json['auditDetails']
      ..status = json['status'] as String?;

Map<String, dynamic> _$RoadCuttingInfoToJson(RoadCuttingInfo instance) {
  final val = <String, dynamic>{};

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('id', instance.id);
  val['roadType'] = instance.roadType;
  val['roadCuttingArea'] = instance.roadCuttingArea;
  writeNotNull('auditDetails', instance.auditDetails);
  writeNotNull('status', instance.status);
  return val;
}

AdditionalDetails _$AdditionalDetailsFromJson(Map<String, dynamic> json) =>
    AdditionalDetails()
      ..locality = json['locality'] as String?
      ..ownerName = json['ownerName'] as String?
      ..appCreatedDate = (json['appCreatedDate'] as num?)?.toInt()
      ..detailsProvidedBy = json['detailsProvidedBy'] as String?
      ..initialMeterReading = (json['initialMeterReading'] as num?)?.toInt()
      ..adhocPenalty = json['adhocPenalty']
      ..adhocRebate = json['adhocRebate']
      ..adhocPenaltyReason = json['adhocPenaltyReason']
      ..adhocPenaltyComment = json['adhocPenaltyComment']
      ..adhocRebateReason = json['adhocRebateReason']
      ..adhocRebateComment = json['adhocRebateComment']
      ..estimationFileStoreId = json['estimationFileStoreId']
      ..sanctionFileStoreId = json['sanctionFileStoreId']
      ..estimationLetterDate = json['estimationLetterDate'];

Map<String, dynamic> _$AdditionalDetailsToJson(AdditionalDetails instance) =>
    <String, dynamic>{
      'locality': instance.locality,
      'ownerName': instance.ownerName,
      'appCreatedDate': instance.appCreatedDate,
      'detailsProvidedBy': instance.detailsProvidedBy,
      'initialMeterReading': instance.initialMeterReading,
      'adhocPenalty': instance.adhocPenalty,
      'adhocRebate': instance.adhocRebate,
      'adhocPenaltyReason': instance.adhocPenaltyReason,
      'adhocPenaltyComment': instance.adhocPenaltyComment,
      'adhocRebateReason': instance.adhocRebateReason,
      'adhocRebateComment': instance.adhocRebateComment,
      'estimationFileStoreId': instance.estimationFileStoreId,
      'sanctionFileStoreId': instance.sanctionFileStoreId,
      'estimationLetterDate': instance.estimationLetterDate,
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

ProcessInstance _$ProcessInstanceFromJson(Map<String, dynamic> json) =>
    ProcessInstance()
      ..id = json['id']
      ..tenantId = json['tenantId']
      ..businessService = json['businessService'] as String?
      ..businessId = json['businessId']
      ..action = json['action'] as String?
      ..moduleName = json['moduleName'] as String?
      ..state = json['state']
      ..comment = json['comment']
      ..documents = (json['documents'] as List<dynamic>?)
          ?.map((e) => WsDocument.fromJson(e as Map<String, dynamic>))
          .toList()
      ..assignes = (json['assignes'] as List<dynamic>?)
          ?.map((e) => Assignes.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$ProcessInstanceToJson(ProcessInstance instance) {
  final val = <String, dynamic>{};

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('id', instance.id);
  writeNotNull('tenantId', instance.tenantId);
  writeNotNull('businessService', instance.businessService);
  writeNotNull('businessId', instance.businessId);
  val['action'] = instance.action;
  writeNotNull('moduleName', instance.moduleName);
  writeNotNull('state', instance.state);
  val['comment'] = instance.comment;
  val['documents'] = instance.documents;
  writeNotNull('assignes', instance.assignes);
  return val;
}

ConnectionHolder _$ConnectionHolderFromJson(Map<String, dynamic> json) =>
    ConnectionHolder()
      ..id = json['id']
      ..uuid = json['uuid'] as String?
      ..userName = json['userName'] as String?
      ..password = json['password']
      ..salutation = json['salutation']
      ..name = json['name'] as String?
      ..gender = json['gender'] as String?
      ..mobileNumber = json['mobileNumber'] as String?
      ..emailId = json['emailId'] as String?
      ..altContactNumber = json['altContactNumber']
      ..pan = json['pan']
      ..aadhaarNumber = json['aadhaarNumber']
      ..permanentAddress = json['permanentAddress']
      ..permanentCity = json['permanentCity']
      ..permanentPinCode = json['permanentPinCode']
      ..correspondenceCity = json['correspondenceCity']
      ..correspondencePinCode = json['correspondencePinCode']
      ..correspondenceAddress = json['correspondenceAddress'] as String?
      ..active = json['active'] as bool?
      ..dob = json['dob']
      ..pwdExpiryDate = (json['pwdExpiryDate'] as num?)?.toInt()
      ..locale = json['locale']
      ..type = json['type'] as String?
      ..signature = json['signature']
      ..accountLocked = json['accountLocked'] as bool?
      ..roles = (json['roles'] as List<dynamic>?)
          ?.map((e) => Role.fromJson(e as Map<String, dynamic>))
          .toList()
      ..fatherOrHusbandName = json['fatherOrHusbandName'] as String?
      ..bloodGroup = json['bloodGroup']
      ..identificationMark = json['identificationMark']
      ..photo = json['photo']
      ..createdBy = json['createdBy'] as String?
      ..createdDate = (json['createdDate'] as num?)?.toInt()
      ..lastModifiedBy = json['lastModifiedBy'] as String?
      ..lastModifiedDate = (json['lastModifiedDate'] as num?)?.toInt()
      ..tenantId = json['tenantId'] as String?
      ..ownerInfoUuid = json['ownerInfoUuid']
      ..isPrimaryOwner = json['isPrimaryOwner']
      ..ownerShipPercentage = json['ownerShipPercentage']
      ..ownerType = json['ownerType'] as String?
      ..institutionId = json['institutionId']
      ..status = json['status'] as String?
      ..documents = json['documents']
      ..relationship = json['relationship'] as String?;

Map<String, dynamic> _$ConnectionHolderToJson(ConnectionHolder instance) =>
    <String, dynamic>{
      'id': instance.id,
      'uuid': instance.uuid,
      'userName': instance.userName,
      'password': instance.password,
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
      'correspondenceCity': instance.correspondenceCity,
      'correspondencePinCode': instance.correspondencePinCode,
      'correspondenceAddress': instance.correspondenceAddress,
      'active': instance.active,
      'dob': instance.dob,
      'pwdExpiryDate': instance.pwdExpiryDate,
      'locale': instance.locale,
      'type': instance.type,
      'signature': instance.signature,
      'accountLocked': instance.accountLocked,
      'roles': instance.roles,
      'fatherOrHusbandName': instance.fatherOrHusbandName,
      'bloodGroup': instance.bloodGroup,
      'identificationMark': instance.identificationMark,
      'photo': instance.photo,
      'createdBy': instance.createdBy,
      'createdDate': instance.createdDate,
      'lastModifiedBy': instance.lastModifiedBy,
      'lastModifiedDate': instance.lastModifiedDate,
      'tenantId': instance.tenantId,
      'ownerInfoUuid': instance.ownerInfoUuid,
      'isPrimaryOwner': instance.isPrimaryOwner,
      'ownerShipPercentage': instance.ownerShipPercentage,
      'ownerType': instance.ownerType,
      'institutionId': instance.institutionId,
      'status': instance.status,
      'documents': instance.documents,
      'relationship': instance.relationship,
    };

Role _$RoleFromJson(Map<String, dynamic> json) => Role()
  ..id = json['id']
  ..name = json['name'] as String?
  ..code = json['code'] as String?
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$RoleToJson(Role instance) => <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'code': instance.code,
      'tenantId': instance.tenantId,
    };

WsDocument _$WsDocumentFromJson(Map<String, dynamic> json) => WsDocument()
  ..documentType = json['documentType'] as String?
  ..fileName = json['fileName'] as String?
  ..fileStoreId = json['fileStoreId'] as String?;

Map<String, dynamic> _$WsDocumentToJson(WsDocument instance) =>
    <String, dynamic>{
      'documentType': instance.documentType,
      'fileName': instance.fileName,
      'fileStoreId': instance.fileStoreId,
    };
