// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'emp_tl_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EmpTlModel _$EmpTlModelFromJson(Map<String, dynamic> json) => EmpTlModel()
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

Map<String, dynamic> _$EmpTlModelToJson(EmpTlModel instance) =>
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
      : BusinessObject.fromJson(json['businessObject'] as Map<String, dynamic>);

Map<String, dynamic> _$ItemToJson(Item instance) => <String, dynamic>{
      'ProcessInstance': instance.processInstance,
      'businessObject': instance.businessObject,
    };

BusinessObject _$BusinessObjectFromJson(Map<String, dynamic> json) =>
    BusinessObject()
      ..businessService = json['businessService'] as String?
      ..applicationType = json['applicationType'] as String?
      ..applicationNumber = json['applicationNumber'] as String?
      ..validFrom = (json['validFrom'] as num?)?.toInt()
      ..workflowCode = json['workflowCode'] as String?
      ..commencementDate = (json['commencementDate'] as num?)?.toInt()
      ..financialYear = json['financialYear'] as String?
      ..licenseType = json['licenseType'] as String?
      ..accountId = json['accountId'] as String?
      ..tradeName = json['tradeName'] as String?
      ..tradeLicenseDetail = json['tradeLicenseDetail'] == null
          ? null
          : TradeLicenseDetail.fromJson(
              json['tradeLicenseDetail'] as Map<String, dynamic>)
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
      ..tenantId = json['tenantId'] as String?
      ..action = json['action'] as String?
      ..id = json['id'] as String?
      ..applicationDate = (json['applicationDate'] as num?)?.toInt()
      ..validTo = (json['validTo'] as num?)?.toInt()
      ..status = json['status'] as String?;

Map<String, dynamic> _$BusinessObjectToJson(BusinessObject instance) =>
    <String, dynamic>{
      'businessService': instance.businessService,
      'applicationType': instance.applicationType,
      'applicationNumber': instance.applicationNumber,
      'validFrom': instance.validFrom,
      'workflowCode': instance.workflowCode,
      'commencementDate': instance.commencementDate,
      'financialYear': instance.financialYear,
      'licenseType': instance.licenseType,
      'accountId': instance.accountId,
      'tradeName': instance.tradeName,
      'tradeLicenseDetail': instance.tradeLicenseDetail,
      'auditDetails': instance.auditDetails,
      'tenantId': instance.tenantId,
      'action': instance.action,
      'id': instance.id,
      'applicationDate': instance.applicationDate,
      'validTo': instance.validTo,
      'status': instance.status,
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

TradeLicenseDetail _$TradeLicenseDetailFromJson(Map<String, dynamic> json) =>
    TradeLicenseDetail()
      ..id = json['id'] as String?
      ..additionalDetail = json['additionalDetail'] == null
          ? null
          : AdditionalDetail.fromJson(
              json['additionalDetail'] as Map<String, dynamic>)
      ..address = json['address'] == null
          ? null
          : Address.fromJson(json['address'] as Map<String, dynamic>)
      ..structureType = json['structureType'] as String?
      ..operationalArea = (json['operationalArea'] as num?)?.toInt()
      ..noOfEmployees = (json['noOfEmployees'] as num?)?.toInt()
      ..tradeUnits = (json['tradeUnits'] as List<dynamic>?)
          ?.map((e) => TradeUnit.fromJson(e as Map<String, dynamic>))
          .toList()
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
      ..channel = json['channel'] as String?
      ..owners = (json['owners'] as List<dynamic>?)
          ?.map((e) => Owner.fromJson(e as Map<String, dynamic>))
          .toList()
      ..applicationDocuments = (json['applicationDocuments'] as List<dynamic>?)
          ?.map((e) => ApplicationDocument.fromJson(e as Map<String, dynamic>))
          .toList()
      ..subOwnerShipCategory = json['subOwnerShipCategory'] as String?;

Map<String, dynamic> _$TradeLicenseDetailToJson(TradeLicenseDetail instance) =>
    <String, dynamic>{
      'id': instance.id,
      'additionalDetail': instance.additionalDetail,
      'address': instance.address,
      'structureType': instance.structureType,
      'operationalArea': instance.operationalArea,
      'noOfEmployees': instance.noOfEmployees,
      'tradeUnits': instance.tradeUnits,
      'auditDetails': instance.auditDetails,
      'channel': instance.channel,
      'owners': instance.owners,
      'applicationDocuments': instance.applicationDocuments,
      'subOwnerShipCategory': instance.subOwnerShipCategory,
    };

AdditionalDetail _$AdditionalDetailFromJson(Map<String, dynamic> json) =>
    AdditionalDetail()
      ..propertyId = json['propertyId'] as String?
      ..tradeGstNo = json['tradeGstNo'] as String?
      ..isSameAsPropertyOwner = json['isSameAsPropertyOwner'] as String?;

Map<String, dynamic> _$AdditionalDetailToJson(AdditionalDetail instance) =>
    <String, dynamic>{
      'propertyId': instance.propertyId,
      'tradeGstNo': instance.tradeGstNo,
      'isSameAsPropertyOwner': instance.isSameAsPropertyOwner,
    };

Address _$AddressFromJson(Map<String, dynamic> json) => Address()
  ..street = json['street'] as String?
  ..tenantId = json['tenantId'] as String?
  ..locality = json['locality'] == null
      ? null
      : Locality.fromJson(json['locality'] as Map<String, dynamic>)
  ..id = json['id'] as String?
  ..doorNo = json['doorNo'] as String?
  ..landmark = json['landmark'] as String?
  ..city = json['city'] as String?;

Map<String, dynamic> _$AddressToJson(Address instance) => <String, dynamic>{
      'street': instance.street,
      'tenantId': instance.tenantId,
      'locality': instance.locality,
      'id': instance.id,
      'doorNo': instance.doorNo,
      'landmark': instance.landmark,
      'city': instance.city,
    };

Locality _$LocalityFromJson(Map<String, dynamic> json) => Locality()
  ..code = json['code'] as String?
  ..children = json['children'] as List<dynamic>?
  ..name = json['name'] as String?
  ..label = json['label'] as String?;

Map<String, dynamic> _$LocalityToJson(Locality instance) => <String, dynamic>{
      'code': instance.code,
      'children': instance.children,
      'name': instance.name,
      'label': instance.label,
    };

ApplicationDocument _$ApplicationDocumentFromJson(Map<String, dynamic> json) =>
    ApplicationDocument()
      ..id = json['id'] as String?
      ..active = json['active'] as bool?
      ..tenantId = json['tenantId'] as String?
      ..documentType = json['documentType'] as String?
      ..fileStoreId = json['fileStoreId'] as String?;

Map<String, dynamic> _$ApplicationDocumentToJson(
        ApplicationDocument instance) =>
    <String, dynamic>{
      'id': instance.id,
      'active': instance.active,
      'tenantId': instance.tenantId,
      'documentType': instance.documentType,
      'fileStoreId': instance.fileStoreId,
    };

Owner _$OwnerFromJson(Map<String, dynamic> json) => Owner()
  ..accountLocked = json['accountLocked'] as bool?
  ..pwdExpiryDate = (json['pwdExpiryDate'] as num?)?.toInt()
  ..gender = json['gender'] as String?
  ..lastModifiedDate = (json['lastModifiedDate'] as num?)?.toInt()
  ..mobileNumber = json['mobileNumber'] as String?
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Roles.fromJson(e as Map<String, dynamic>))
      .toList()
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..fatherOrHusbandName = json['fatherOrHusbandName'] as String?
  ..active = json['active'] as bool?
  ..emailId = json['emailId'] as String?
  ..userName = json['userName'] as String?
  ..type = json['type'] as String?
  ..uuid = json['uuid'] as String?
  ..userActive = json['userActive'] as bool?
  ..createdDate = (json['createdDate'] as num?)?.toInt()
  ..createdBy = json['createdBy'] as String?
  ..name = json['name'] as String?
  ..tenantId = json['tenantId'] as String?
  ..id = (json['id'] as num?)?.toInt()
  ..permanentAddress = json['permanentAddress'] as String?
  ..relationship = json['relationship'] as String?;

Map<String, dynamic> _$OwnerToJson(Owner instance) => <String, dynamic>{
      'accountLocked': instance.accountLocked,
      'pwdExpiryDate': instance.pwdExpiryDate,
      'gender': instance.gender,
      'lastModifiedDate': instance.lastModifiedDate,
      'mobileNumber': instance.mobileNumber,
      'roles': instance.roles,
      'lastModifiedBy': instance.lastModifiedBy,
      'fatherOrHusbandName': instance.fatherOrHusbandName,
      'active': instance.active,
      'emailId': instance.emailId,
      'userName': instance.userName,
      'type': instance.type,
      'uuid': instance.uuid,
      'userActive': instance.userActive,
      'createdDate': instance.createdDate,
      'createdBy': instance.createdBy,
      'name': instance.name,
      'tenantId': instance.tenantId,
      'id': instance.id,
      'permanentAddress': instance.permanentAddress,
      'relationship': instance.relationship,
    };

Roles _$RolesFromJson(Map<String, dynamic> json) => Roles()
  ..id = json['id']
  ..name = json['name'] as String?
  ..code = json['code'] as String?
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$RolesToJson(Roles instance) => <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'code': instance.code,
      'tenantId': instance.tenantId,
    };

TradeUnit _$TradeUnitFromJson(Map<String, dynamic> json) => TradeUnit()
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..active = json['active'] as bool?
  ..tradeType = json['tradeType'] as String?;

Map<String, dynamic> _$TradeUnitToJson(TradeUnit instance) => <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'active': instance.active,
      'tradeType': instance.tradeType,
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
      ..comment = json['comment'] as String?
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
  ..emailId = json['emailId'] as String?
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Roles.fromJson(e as Map<String, dynamic>))
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
