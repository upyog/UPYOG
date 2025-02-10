// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'emp_ws_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EmpWsModel _$EmpWsModelFromJson(Map<String, dynamic> json) => EmpWsModel()
  ..responseInfo = json['responseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['responseInfo'] as Map<String, dynamic>)
  ..totalCount = (json['totalCount'] as num?)?.toInt()
  ..nearingSlaCount = (json['nearingSlaCount'] as num?)?.toInt()
  ..statusMap = (json['statusMap'] as List<dynamic>?)
      ?.map((e) => StatusMap.fromJson(e as Map<String, dynamic>))
      .toList()
  ..wsItems = (json['items'] as List<dynamic>?)
      ?.map((e) => WsItem.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$EmpWsModelToJson(EmpWsModel instance) =>
    <String, dynamic>{
      'responseInfo': instance.responseInfo,
      'totalCount': instance.totalCount,
      'nearingSlaCount': instance.nearingSlaCount,
      'statusMap': instance.statusMap,
      'items': instance.wsItems,
    };

WsItem _$WsItemFromJson(Map<String, dynamic> json) => WsItem()
  ..businessObject = json['businessObject'] == null
      ? null
      : BusinessObject.fromJson(json['businessObject'] as Map<String, dynamic>);

Map<String, dynamic> _$WsItemToJson(WsItem instance) => <String, dynamic>{
      'businessObject': instance.businessObject,
    };

BusinessObject _$BusinessObjectFromJson(Map<String, dynamic> json) =>
    BusinessObject()
      ..serviceSla = (json['serviceSLA'] as num?)?.toInt()
      ..data = json['Data'] == null
          ? null
          : Data.fromJson(json['Data'] as Map<String, dynamic>);

Map<String, dynamic> _$BusinessObjectToJson(BusinessObject instance) =>
    <String, dynamic>{
      'serviceSLA': instance.serviceSla,
      'Data': instance.data,
    };

Data _$DataFromJson(Map<String, dynamic> json) => Data()
  ..workflow = json['workflow'] == null
      ? null
      : Workflow.fromJson(json['workflow'] as Map<String, dynamic>)
  ..applicationNo = json['applicationNo'] as String?
  ..applicationStatus = json['applicationStatus'] as String?
  ..status = json['status'] as String?
  ..connectionNo = json['connectionNo']
  ..oldConnectionNo = json['oldConnectionNo']
  ..plumberInfo = (json['plumberInfo'] as List<dynamic>?)
      ?.map((e) => PlumberInfo.fromJson(e as Map<String, dynamic>))
      .toList()
  ..roadCuttingInfo = (json['roadCuttingInfo'] as List<dynamic>?)
      ?.map((e) => RoadCuttingInfo.fromJson(e as Map<String, dynamic>))
      .toList()
  ..connectionHolders = json['connectionHolders']
  ..roadType = json['roadType']
  ..roadCuttingArea = (json['roadCuttingArea'] as num?)?.toInt()
  ..connectionExecutionDate = (json['connectionExecutionDate'] as num?)?.toInt()
  ..connectionCategory = json['connectionCategory']
  ..connectionType = json['connectionType'] as String?
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : AdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>)
  ..id = json['id'] as String?
  ..propertyId = json['propertyId'] as String?
  ..propertyUsageType = json['propertyUsageType'] as String?
  ..rainWaterHarvesting = json['rainWaterHarvesting'] as String?
  ..waterSource = json['waterSource'] as String?
  ..tenantId = json['tenantId'] as String?
  ..meterId = json['meterId'] as String?
  ..meterInstallationDate = (json['meterInstallationDate'] as num?)?.toInt()
  ..proposedPipeSize = (json['proposedPipeSize'] as num?)?.toDouble()
  ..proposedTaps = (json['proposedTaps'] as num?)?.toInt()
  ..pipeSize = (json['pipeSize'] as num?)?.toDouble()
  ..noOfTaps = (json['noOfTaps'] as num?)?.toInt()
  ..applicationType = json['applicationType'] as String?
  ..dateEffectiveFrom = (json['dateEffectiveFrom'] as num?)?.toInt()
  ..channel = json['channel'] as String?
  ..timestamp = json['timestamp'] == null
      ? null
      : DateTime.parse(json['timestamp'] as String)
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..history = (json['history'] as List<dynamic>?)
      ?.map((e) => History.fromJson(e as Map<String, dynamic>))
      .toList()
  ..ownerMobileNumbers = (json['ownerMobileNumbers'] as List<dynamic>?)
      ?.map((e) => e as String)
      .toList()
  ..ward = json['ward'] == null
      ? null
      : Ward.fromJson(json['ward'] as Map<String, dynamic>)
  ..tenantData = json['tenantData'] == null
      ? null
      : TenantData.fromJson(json['tenantData'] as Map<String, dynamic>);

Map<String, dynamic> _$DataToJson(Data instance) => <String, dynamic>{
      'workflow': instance.workflow,
      'applicationNo': instance.applicationNo,
      'applicationStatus': instance.applicationStatus,
      'status': instance.status,
      'connectionNo': instance.connectionNo,
      'oldConnectionNo': instance.oldConnectionNo,
      'plumberInfo': instance.plumberInfo,
      'roadCuttingInfo': instance.roadCuttingInfo,
      'connectionHolders': instance.connectionHolders,
      'roadType': instance.roadType,
      'roadCuttingArea': instance.roadCuttingArea,
      'connectionExecutionDate': instance.connectionExecutionDate,
      'connectionCategory': instance.connectionCategory,
      'connectionType': instance.connectionType,
      'additionalDetails': instance.additionalDetails,
      'id': instance.id,
      'propertyId': instance.propertyId,
      'propertyUsageType': instance.propertyUsageType,
      'rainWaterHarvesting': instance.rainWaterHarvesting,
      'waterSource': instance.waterSource,
      'tenantId': instance.tenantId,
      'meterId': instance.meterId,
      'meterInstallationDate': instance.meterInstallationDate,
      'proposedPipeSize': instance.proposedPipeSize,
      'proposedTaps': instance.proposedTaps,
      'pipeSize': instance.pipeSize,
      'noOfTaps': instance.noOfTaps,
      'applicationType': instance.applicationType,
      'dateEffectiveFrom': instance.dateEffectiveFrom,
      'channel': instance.channel,
      'timestamp': instance.timestamp?.toIso8601String(),
      'auditDetails': instance.auditDetails,
      'history': instance.history,
      'ownerMobileNumbers': instance.ownerMobileNumbers,
      'ward': instance.ward,
      'tenantData': instance.tenantData,
    };

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

AdditionalDetails _$AdditionalDetailsFromJson(Map<String, dynamic> json) =>
    AdditionalDetails()
      ..adhocPenaltyComment = json['adhocPenaltyComment']
      ..adhocPenaltyReason = json['adhocPenaltyReason']
      ..adhocRebateReason = json['adhocRebateReason']
      ..estimationLetterDate = json['estimationLetterDate']
      ..adhocRebate = json['adhocRebate']
      ..detailsProvidedBy = json['detailsProvidedBy']
      ..locality = json['locality'] as String?
      ..estimationFileStoreId = json['estimationFileStoreId']
      ..sanctionFileStoreId = json['sanctionFileStoreId']
      ..adhocRebateComment = json['adhocRebateComment']
      ..adhocPenalty = json['adhocPenalty']
      ..ownerName = json['ownerName'] as String?
      ..appCreatedDate = (json['appCreatedDate'] as num?)?.toInt()
      ..initialMeterReading = (json['initialMeterReading'] as num?)?.toInt();

Map<String, dynamic> _$AdditionalDetailsToJson(AdditionalDetails instance) =>
    <String, dynamic>{
      'adhocPenaltyComment': instance.adhocPenaltyComment,
      'adhocPenaltyReason': instance.adhocPenaltyReason,
      'adhocRebateReason': instance.adhocRebateReason,
      'estimationLetterDate': instance.estimationLetterDate,
      'adhocRebate': instance.adhocRebate,
      'detailsProvidedBy': instance.detailsProvidedBy,
      'locality': instance.locality,
      'estimationFileStoreId': instance.estimationFileStoreId,
      'sanctionFileStoreId': instance.sanctionFileStoreId,
      'adhocRebateComment': instance.adhocRebateComment,
      'adhocPenalty': instance.adhocPenalty,
      'ownerName': instance.ownerName,
      'appCreatedDate': instance.appCreatedDate,
      'initialMeterReading': instance.initialMeterReading,
    };

AuditDetails _$AuditDetailsFromJson(Map<String, dynamic> json) => AuditDetails()
  ..lastModifiedTime = (json['lastModifiedTime'] as num?)?.toInt()
  ..createdBy = json['createdBy'] as String?
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..createdTime = (json['createdTime'] as num?)?.toInt();

Map<String, dynamic> _$AuditDetailsToJson(AuditDetails instance) =>
    <String, dynamic>{
      'lastModifiedTime': instance.lastModifiedTime,
      'createdBy': instance.createdBy,
      'lastModifiedBy': instance.lastModifiedBy,
      'createdTime': instance.createdTime,
    };

History _$HistoryFromJson(Map<String, dynamic> json) => History()
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..businessService = json['businessService'] as String?
  ..businessId = json['businessId'] as String?
  ..action = json['action'] as String?
  ..moduleName = json['moduleName'] as String?
  ..state = json['state'] == null
      ? null
      : HistoryState.fromJson(json['state'] as Map<String, dynamic>)
  ..comment = json['comment'] as String?
  ..documents = json['documents']
  ..assigner = json['assigner'] == null
      ? null
      : Assigner.fromJson(json['assigner'] as Map<String, dynamic>)
  ..assignees = json['assignes']
  ..nextActions = json['nextActions'] as List<dynamic>?
  ..stateSla = (json['stateSla'] as num?)?.toInt()
  ..businessServiceSla = (json['businesssServiceSla'] as num?)?.toInt()
  ..previousStatus = json['previousStatus']
  ..entity = json['entity']
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..rating = (json['rating'] as num?)?.toInt()
  ..escalated = json['escalated'] as bool?;

Map<String, dynamic> _$HistoryToJson(History instance) => <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'businessService': instance.businessService,
      'businessId': instance.businessId,
      'action': instance.action,
      'moduleName': instance.moduleName,
      'state': instance.state,
      'comment': instance.comment,
      'documents': instance.documents,
      'assigner': instance.assigner,
      'assignes': instance.assignees,
      'nextActions': instance.nextActions,
      'stateSla': instance.stateSla,
      'businesssServiceSla': instance.businessServiceSla,
      'previousStatus': instance.previousStatus,
      'entity': instance.entity,
      'auditDetails': instance.auditDetails,
      'rating': instance.rating,
      'escalated': instance.escalated,
    };

Assigner _$AssignerFromJson(Map<String, dynamic> json) => Assigner()
  ..id = (json['id'] as num?)?.toInt()
  ..userName = json['userName'] as String?
  ..name = json['name'] as String?
  ..type = json['type'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..emailId = json['emailId']
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Role.fromJson(e as Map<String, dynamic>))
      .toList()
  ..tenantId = json['tenantId'] as String?
  ..uuid = json['uuid'] as String?;

Map<String, dynamic> _$AssignerToJson(Assigner instance) => <String, dynamic>{
      'id': instance.id,
      'userName': instance.userName,
      'name': instance.name,
      'type': instance.type,
      'mobileNumber': instance.mobileNumber,
      'emailId': instance.emailId,
      'roles': instance.roles,
      'tenantId': instance.tenantId,
      'uuid': instance.uuid,
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

HistoryState _$HistoryStateFromJson(Map<String, dynamic> json) => HistoryState()
  ..auditDetails = json['auditDetails']
  ..uuid = json['uuid'] as String?
  ..tenantId = json['tenantId'] as String?
  ..businessServiceId = json['businessServiceId'] as String?
  ..sla = (json['sla'] as num?)?.toInt()
  ..state = json['state'] as String?
  ..applicationStatus = json['applicationStatus'] as String?
  ..docUploadRequired = json['docUploadRequired'] as bool?
  ..isStartState = json['isStartState'] as bool?
  ..isTerminateState = json['isTerminateState'] as bool?
  ..isStateUpdatable = json['isStateUpdatable']
  ..actions = (json['actions'] as List<dynamic>?)
      ?.map((e) => Action.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$HistoryStateToJson(HistoryState instance) =>
    <String, dynamic>{
      'auditDetails': instance.auditDetails,
      'uuid': instance.uuid,
      'tenantId': instance.tenantId,
      'businessServiceId': instance.businessServiceId,
      'sla': instance.sla,
      'state': instance.state,
      'applicationStatus': instance.applicationStatus,
      'docUploadRequired': instance.docUploadRequired,
      'isStartState': instance.isStartState,
      'isTerminateState': instance.isTerminateState,
      'isStateUpdatable': instance.isStateUpdatable,
      'actions': instance.actions,
    };

Action _$ActionFromJson(Map<String, dynamic> json) => Action()
  ..auditDetails = json['auditDetails']
  ..uuid = json['uuid'] as String?
  ..tenantId = json['tenantId'] as String?
  ..currentState = json['currentState'] as String?
  ..action = json['action'] as String?
  ..nextState = json['nextState'] as String?
  ..roles = (json['roles'] as List<dynamic>?)?.map((e) => e as String).toList()
  ..active = json['active'];

Map<String, dynamic> _$ActionToJson(Action instance) => <String, dynamic>{
      'auditDetails': instance.auditDetails,
      'uuid': instance.uuid,
      'tenantId': instance.tenantId,
      'currentState': instance.currentState,
      'action': instance.action,
      'nextState': instance.nextState,
      'roles': instance.roles,
      'active': instance.active,
    };

TenantData _$TenantDataFromJson(Map<String, dynamic> json) => TenantData()
  ..code = json['code'] as String?
  ..name = json['name'] as String?
  ..description = json['description'] as String?
  ..pincode = (json['pincode'] as List<dynamic>?)
      ?.map((e) => (e as num).toInt())
      .toList()
  ..logoId = json['logoId'] as String?
  ..imageId = json['imageId']
  ..domainUrl = json['domainUrl'] as String?
  ..type = json['type'] as String?
  ..twitterUrl = json['twitterUrl']
  ..facebookUrl = json['facebookUrl']
  ..emailId = json['emailId'] as String?
  ..officeTimings = json['officeTimings'] == null
      ? null
      : OfficeTimings.fromJson(json['officeTimings'] as Map<String, dynamic>)
  ..city = json['city'] == null
      ? null
      : City.fromJson(json['city'] as Map<String, dynamic>)
  ..address = json['address'] as String?
  ..contactNumber = json['contactNumber'] as String?;

Map<String, dynamic> _$TenantDataToJson(TenantData instance) =>
    <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'description': instance.description,
      'pincode': instance.pincode,
      'logoId': instance.logoId,
      'imageId': instance.imageId,
      'domainUrl': instance.domainUrl,
      'type': instance.type,
      'twitterUrl': instance.twitterUrl,
      'facebookUrl': instance.facebookUrl,
      'emailId': instance.emailId,
      'officeTimings': instance.officeTimings,
      'city': instance.city,
      'address': instance.address,
      'contactNumber': instance.contactNumber,
    };

City _$CityFromJson(Map<String, dynamic> json) => City()
  ..name = json['name'] as String?
  ..localName = json['localName']
  ..districtCode = json['districtCode'] as String?
  ..districtName = json['districtName']
  ..districtTenantCode = json['districtTenantCode'] as String?
  ..regionName = json['regionName']
  ..ulbGrade = json['ulbGrade'] as String?
  ..longitude = (json['longitude'] as num?)?.toDouble()
  ..latitude = (json['latitude'] as num?)?.toDouble()
  ..shapeFileLocation = json['shapeFileLocation']
  ..captcha = json['captcha']
  ..code = json['code'] as String?
  ..ddrName = json['ddrName'] as String?;

Map<String, dynamic> _$CityToJson(City instance) => <String, dynamic>{
      'name': instance.name,
      'localName': instance.localName,
      'districtCode': instance.districtCode,
      'districtName': instance.districtName,
      'districtTenantCode': instance.districtTenantCode,
      'regionName': instance.regionName,
      'ulbGrade': instance.ulbGrade,
      'longitude': instance.longitude,
      'latitude': instance.latitude,
      'shapeFileLocation': instance.shapeFileLocation,
      'captcha': instance.captcha,
      'code': instance.code,
      'ddrName': instance.ddrName,
    };

OfficeTimings _$OfficeTimingsFromJson(Map<String, dynamic> json) =>
    OfficeTimings()..monFri = json['Mon - Fri'] as String?;

Map<String, dynamic> _$OfficeTimingsToJson(OfficeTimings instance) =>
    <String, dynamic>{
      'Mon - Fri': instance.monFri,
    };

Ward _$WardFromJson(Map<String, dynamic> json) => Ward()
  ..code = json['code'] as String?
  ..name = json['name'] as String?
  ..label = json['label'] as String?
  ..latitude = json['latitude']
  ..longitude = json['longitude']
  ..area = json['area'] as String?
  ..pinCode = json['pincode']
  ..boundaryNum = (json['boundaryNum'] as num?)?.toInt()
  ..children = (json['children'] as List<dynamic>?)
      ?.map((e) => Ward.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$WardToJson(Ward instance) => <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'label': instance.label,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'area': instance.area,
      'pincode': instance.pinCode,
      'boundaryNum': instance.boundaryNum,
      'children': instance.children,
    };

Workflow _$WorkflowFromJson(Map<String, dynamic> json) => Workflow()
  ..state = json['state'] == null
      ? null
      : WorkflowState.fromJson(json['state'] as Map<String, dynamic>)
  ..action = json['action'] as String?
  ..assignes = json['assignes'] as List<dynamic>?;

Map<String, dynamic> _$WorkflowToJson(Workflow instance) => <String, dynamic>{
      'state': instance.state,
      'action': instance.action,
      'assignes': instance.assignes,
    };

WorkflowState _$WorkflowStateFromJson(Map<String, dynamic> json) =>
    WorkflowState();

Map<String, dynamic> _$WorkflowStateToJson(WorkflowState instance) =>
    <String, dynamic>{};

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
