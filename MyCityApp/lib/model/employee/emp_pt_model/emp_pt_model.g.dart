// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'emp_pt_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EmpPtModel _$EmpPtModelFromJson(Map<String, dynamic> json) => EmpPtModel()
  ..responseInfo = json['responseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['responseInfo'] as Map<String, dynamic>)
  ..totalCount = (json['totalCount'] as num?)?.toInt()
  ..nearingSlaCount = (json['nearingSlaCount'] as num?)?.toInt()
  ..statusMap = (json['statusMap'] as List<dynamic>?)
      ?.map((e) => StatusMap.fromJson(e as Map<String, dynamic>))
      .toList()
  ..items = (json['items'] as List<dynamic>?)
      ?.map((e) => Item.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$EmpPtModelToJson(EmpPtModel instance) =>
    <String, dynamic>{
      'responseInfo': instance.responseInfo,
      'totalCount': instance.totalCount,
      'nearingSlaCount': instance.nearingSlaCount,
      'statusMap': instance.statusMap,
      'items': instance.items,
    };

Item _$ItemFromJson(Map<String, dynamic> json) => Item()
  ..processInstance = json['ProcessInstance'] == null
      ? null
      : ProcessInstance.fromJson(
          json['ProcessInstance'] as Map<String, dynamic>)
  ..businessObject = json['businessObject'] == null
      ? null
      : BusinessObject.fromJson(json['businessObject'] as Map<String, dynamic>)
  ..serviceObject = json['serviceObject'];

Map<String, dynamic> _$ItemToJson(Item instance) => <String, dynamic>{
      'ProcessInstance': instance.processInstance,
      'businessObject': instance.businessObject,
      'serviceObject': instance.serviceObject,
    };

BusinessObject _$BusinessObjectFromJson(Map<String, dynamic> json) =>
    BusinessObject()
      ..address = json['address'] == null
          ? null
          : Address.fromJson(json['address'] as Map<String, dynamic>)
      ..documents = (json['documents'] as List<dynamic>?)
          ?.map((e) => Document.fromJson(e as Map<String, dynamic>))
          .toList()
      ..businessService = json['businessService'] as String?
      ..landArea = (json['landArea'] as num?)?.toInt()
      ..channel = json['channel'] as String?
      ..owners = (json['owners'] as List<dynamic>?)
          ?.map((e) => Owner.fromJson(e as Map<String, dynamic>))
          .toList()
      ..source = json['source'] as String?
      ..creationReason = json['creationReason'] as String?
      ..accountId = json['accountId'] as String?
      ..noOfFloors = (json['noOfFloors'] as num?)?.toInt()
      ..ownershipCategory = json['ownershipCategory'] as String?
      ..alternateUpdated = json['AlternateUpdated'] as bool?
      ..propertyType = json['propertyType'] as String?
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
      ..tenantId = json['tenantId'] as String?
      ..id = json['id'] as String?
      ..propertyId = json['propertyId'] as String?
      ..isOldDataEncryptionRequest = json['isOldDataEncryptionRequest'] as bool?
      ..status = json['status'] as String?
      ..acknowledgementNumber = json['acknowldgementNumber'] as String?
      ..usageCategory = json['usageCategory'] as String?;

Map<String, dynamic> _$BusinessObjectToJson(BusinessObject instance) =>
    <String, dynamic>{
      'address': instance.address,
      'documents': instance.documents,
      'businessService': instance.businessService,
      'landArea': instance.landArea,
      'channel': instance.channel,
      'owners': instance.owners,
      'source': instance.source,
      'creationReason': instance.creationReason,
      'accountId': instance.accountId,
      'noOfFloors': instance.noOfFloors,
      'ownershipCategory': instance.ownershipCategory,
      'AlternateUpdated': instance.alternateUpdated,
      'propertyType': instance.propertyType,
      'auditDetails': instance.auditDetails,
      'tenantId': instance.tenantId,
      'id': instance.id,
      'propertyId': instance.propertyId,
      'isOldDataEncryptionRequest': instance.isOldDataEncryptionRequest,
      'status': instance.status,
      'acknowldgementNumber': instance.acknowledgementNumber,
      'usageCategory': instance.usageCategory,
    };

Address _$AddressFromJson(Map<String, dynamic> json) => Address()
  ..city = json['city'] as String?
  ..geoLocation = json['geoLocation'] == null
      ? null
      : GeoLocation.fromJson(json['geoLocation'] as Map<String, dynamic>)
  ..tenantId = json['tenantId'] as String?
  ..locality = json['locality'] == null
      ? null
      : Locality.fromJson(json['locality'] as Map<String, dynamic>)
  ..id = json['id'] as String?;

Map<String, dynamic> _$AddressToJson(Address instance) => <String, dynamic>{
      'city': instance.city,
      'geoLocation': instance.geoLocation,
      'tenantId': instance.tenantId,
      'locality': instance.locality,
      'id': instance.id,
    };

GeoLocation _$GeoLocationFromJson(Map<String, dynamic> json) => GeoLocation()
  ..latitude = (json['latitude'] as num?)?.toInt()
  ..longitude = (json['longitude'] as num?)?.toInt();

Map<String, dynamic> _$GeoLocationToJson(GeoLocation instance) =>
    <String, dynamic>{
      'latitude': instance.latitude,
      'longitude': instance.longitude,
    };

Locality _$LocalityFromJson(Map<String, dynamic> json) => Locality()
  ..area = json['area'] as String?
  ..code = json['code'] as String?
  ..children = json['children'] as List<dynamic>?
  ..name = json['name'] as String?
  ..label = json['label'] as String?;

Map<String, dynamic> _$LocalityToJson(Locality instance) => <String, dynamic>{
      'area': instance.area,
      'code': instance.code,
      'children': instance.children,
      'name': instance.name,
      'label': instance.label,
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

Document _$DocumentFromJson(Map<String, dynamic> json) => Document()
  ..documentType = json['documentType'] as String?
  ..documentUid = json['documentUid'] as String?
  ..id = json['id'] as String?
  ..fileStoreId = json['fileStoreId'] as String?
  ..status = json['status'] as String?;

Map<String, dynamic> _$DocumentToJson(Document instance) => <String, dynamic>{
      'documentType': instance.documentType,
      'documentUid': instance.documentUid,
      'id': instance.id,
      'fileStoreId': instance.fileStoreId,
      'status': instance.status,
    };

Owner _$OwnerFromJson(Map<String, dynamic> json) => Owner()
  ..accountLocked = json['accountLocked'] as bool?
  ..ownerType = json['ownerType'] as String?
  ..pwdExpiryDate = (json['pwdExpiryDate'] as num?)?.toInt()
  ..gender = json['gender'] as String?
  ..lastModifiedDate = (json['lastModifiedDate'] as num?)?.toInt()
  ..documents = (json['documents'] as List<dynamic>?)
      ?.map((e) => Document.fromJson(e as Map<String, dynamic>))
      .toList()
  ..mobileNumber = json['mobileNumber'] as String?
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Role.fromJson(e as Map<String, dynamic>))
      .toList()
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..fatherOrHusbandName = json['fatherOrHusbandName'] as String?
  ..active = json['active'] as bool?
  ..emailId = json['emailId'] as String?
  ..userName = json['userName'] as String?
  ..type = json['type'] as String?
  ..uuid = json['uuid'] as String?
  ..ownerInfoUuid = json['ownerInfoUuid'] as String?
  ..correspondenceAddress = json['correspondenceAddress'] as String?
  ..createdDate = (json['createdDate'] as num?)?.toInt()
  ..createdBy = json['createdBy'] as String?
  ..name = json['name'] as String?
  ..tenantId = json['tenantId'] as String?
  ..permanentAddress = json['permanentAddress'] as String?
  ..relationship = json['relationship'] as String?
  ..status = json['status'] as String?;

Map<String, dynamic> _$OwnerToJson(Owner instance) => <String, dynamic>{
      'accountLocked': instance.accountLocked,
      'ownerType': instance.ownerType,
      'pwdExpiryDate': instance.pwdExpiryDate,
      'gender': instance.gender,
      'lastModifiedDate': instance.lastModifiedDate,
      'documents': instance.documents,
      'mobileNumber': instance.mobileNumber,
      'roles': instance.roles,
      'lastModifiedBy': instance.lastModifiedBy,
      'fatherOrHusbandName': instance.fatherOrHusbandName,
      'active': instance.active,
      'emailId': instance.emailId,
      'userName': instance.userName,
      'type': instance.type,
      'uuid': instance.uuid,
      'ownerInfoUuid': instance.ownerInfoUuid,
      'correspondenceAddress': instance.correspondenceAddress,
      'createdDate': instance.createdDate,
      'createdBy': instance.createdBy,
      'name': instance.name,
      'tenantId': instance.tenantId,
      'permanentAddress': instance.permanentAddress,
      'relationship': instance.relationship,
      'status': instance.status,
    };

ProcessInstance _$ProcessInstanceFromJson(Map<String, dynamic> json) =>
    ProcessInstance()
      ..id = json['id'] as String?
      ..tenantId = json['tenantId'] as String?
      ..businessService = json['businessService'] as String?
      ..businessId = json['businessId'] as String?
      ..action = json['action'] as String?
      ..moduleName = json['moduleName'] as String?
      ..state = json['state'] == null
          ? null
          : State.fromJson(json['state'] as Map<String, dynamic>)
      ..comment = json['comment']
      ..documents = json['documents']
      ..assigner = json['assigner'] == null
          ? null
          : Assigner.fromJson(json['assigner'] as Map<String, dynamic>)
      ..assignes = json['assignes']
      ..nextActions = (json['nextActions'] as List<dynamic>?)
          ?.map((e) => Action.fromJson(e as Map<String, dynamic>))
          .toList()
      ..stateSla = json['stateSla']
      ..businesssServiceSla = (json['businesssServiceSla'] as num?)?.toInt()
      ..previousStatus = json['previousStatus']
      ..entity = json['entity']
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
      ..rating = (json['rating'] as num?)?.toInt()
      ..escalated = json['escalated'] as bool?;

Map<String, dynamic> _$ProcessInstanceToJson(ProcessInstance instance) =>
    <String, dynamic>{
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
      'assignes': instance.assignes,
      'nextActions': instance.nextActions,
      'stateSla': instance.stateSla,
      'businesssServiceSla': instance.businesssServiceSla,
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

Action _$ActionFromJson(Map<String, dynamic> json) => Action()
  ..auditDetails = json['auditDetails']
  ..uuid = json['uuid'] as String?
  ..tenantId = json['tenantId'] as String?
  ..currentState = json['currentState'] as String?
  ..action = json['action'] as String?
  ..nextState = json['nextState'] as String?
  ..roles = (json['roles'] as List<dynamic>?)?.map((e) => e as String).toList()
  ..active = json['active'] as bool?;

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

State _$StateFromJson(Map<String, dynamic> json) => State()
  ..auditDetails = json['auditDetails']
  ..uuid = json['uuid'] as String?
  ..tenantId = json['tenantId'] as String?
  ..businessServiceId = json['businessServiceId'] as String?
  ..sla = json['sla']
  ..state = json['state'] as String?
  ..applicationStatus = json['applicationStatus'] as String?
  ..docUploadRequired = json['docUploadRequired'] as bool?
  ..isStartState = json['isStartState'] as bool?
  ..isTerminateState = json['isTerminateState'] as bool?
  ..isStateUpdatable = json['isStateUpdatable']
  ..actions = (json['actions'] as List<dynamic>?)
      ?.map((e) => Action.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$StateToJson(State instance) => <String, dynamic>{
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

ResponseInfo _$ResponseInfoFromJson(Map<String, dynamic> json) => ResponseInfo()
  ..apiId = json['apiId'] as String?
  ..ver = json['ver']
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
