// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'emp_bpa_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EmpBpaModel _$EmpBpaModelFromJson(Map<String, dynamic> json) => EmpBpaModel()
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

Map<String, dynamic> _$EmpBpaModelToJson(EmpBpaModel instance) =>
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
      ..approvalDate = (json['approvalDate'] as num?)?.toInt()
      ..documents = (json['documents'] as List<dynamic>?)
          ?.map((e) => Document.fromJson(e as Map<String, dynamic>))
          .toList()
      ..applicationNo = json['applicationNo'] as String?
      ..additionalDetails = json['additionalDetails'] == null
          ? null
          : AdditionalDetails.fromJson(
              json['additionalDetails'] as Map<String, dynamic>)
      ..landInfo = json['landInfo'] == null
          ? null
          : LandInfo.fromJson(json['landInfo'] as Map<String, dynamic>)
      ..accountId = json['accountId'] as String?
      ..landId = json['landId'] as String?
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
      ..tenantId = json['tenantId'] as String?
      ..edcrNumber = json['edcrNumber'] as String?
      ..id = json['id'] as String?
      ..applicationDate = (json['applicationDate'] as num?)?.toInt()
      ..status = json['status'] as String?;

Map<String, dynamic> _$BusinessObjectToJson(BusinessObject instance) =>
    <String, dynamic>{
      'businessService': instance.businessService,
      'approvalDate': instance.approvalDate,
      'documents': instance.documents,
      'applicationNo': instance.applicationNo,
      'additionalDetails': instance.additionalDetails,
      'landInfo': instance.landInfo,
      'accountId': instance.accountId,
      'landId': instance.landId,
      'auditDetails': instance.auditDetails,
      'tenantId': instance.tenantId,
      'edcrNumber': instance.edcrNumber,
      'id': instance.id,
      'applicationDate': instance.applicationDate,
      'status': instance.status,
    };

AdditionalDetails _$AdditionalDetailsFromJson(Map<String, dynamic> json) =>
    AdditionalDetails()
      ..serviceType = json['serviceType'] as String?
      ..typeOfArchitect = json['typeOfArchitect'] as String?
      ..applicationType = json['applicationType'] as String?
      ..propertyAcknowledgementNumber =
          json['propertyAcknowldgementNumber'] as String?
      ..fieldInspectionPending = (json['fieldinspection_pending']
              as List<dynamic>?)
          ?.map(
              (e) => FieldInspectionPending.fromJson(e as Map<String, dynamic>))
          .toList()
      ..propertyId = json['propertyID'] as String?;

Map<String, dynamic> _$AdditionalDetailsToJson(AdditionalDetails instance) =>
    <String, dynamic>{
      'serviceType': instance.serviceType,
      'typeOfArchitect': instance.typeOfArchitect,
      'applicationType': instance.applicationType,
      'propertyAcknowldgementNumber': instance.propertyAcknowledgementNumber,
      'fieldinspection_pending': instance.fieldInspectionPending,
      'propertyID': instance.propertyId,
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
  ..id = json['id'] as String?
  ..fileStoreId = json['fileStoreId'] as String?;

Map<String, dynamic> _$DocumentToJson(Document instance) => <String, dynamic>{
      'documentType': instance.documentType,
      'id': instance.id,
      'fileStoreId': instance.fileStoreId,
    };

LandInfo _$LandInfoFromJson(Map<String, dynamic> json) => LandInfo()
  ..unit = (json['unit'] as List<dynamic>?)
      ?.map((e) => Unit.fromJson(e as Map<String, dynamic>))
      .toList()
  ..address = json['address'] == null
      ? null
      : Address.fromJson(json['address'] as Map<String, dynamic>)
  ..ownershipCategory = json['ownershipCategory'] as String?
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..tenantId = json['tenantId'] as String?
  ..channel = json['channel'] as String?
  ..owners = (json['owners'] as List<dynamic>?)
      ?.map((e) => Owner.fromJson(e as Map<String, dynamic>))
      .toList()
  ..id = json['id'] as String?
  ..source = json['source'] as String?;

Map<String, dynamic> _$LandInfoToJson(LandInfo instance) => <String, dynamic>{
      'unit': instance.unit,
      'address': instance.address,
      'ownershipCategory': instance.ownershipCategory,
      'auditDetails': instance.auditDetails,
      'tenantId': instance.tenantId,
      'channel': instance.channel,
      'owners': instance.owners,
      'id': instance.id,
      'source': instance.source,
    };

Address _$AddressFromJson(Map<String, dynamic> json) => Address()
  ..city = json['city'] as String?
  ..geoLocation = json['geoLocation'] == null
      ? null
      : GeoLocation.fromJson(json['geoLocation'] as Map<String, dynamic>)
  ..street = json['street'] as String?
  ..tenantId = json['tenantId'] as String?
  ..locality = json['locality'] == null
      ? null
      : Locality.fromJson(json['locality'] as Map<String, dynamic>)
  ..id = json['id'] as String?
  ..landmark = json['landmark'] as String?;

Map<String, dynamic> _$AddressToJson(Address instance) => <String, dynamic>{
      'city': instance.city,
      'geoLocation': instance.geoLocation,
      'street': instance.street,
      'tenantId': instance.tenantId,
      'locality': instance.locality,
      'id': instance.id,
      'landmark': instance.landmark,
    };

GeoLocation _$GeoLocationFromJson(Map<String, dynamic> json) => GeoLocation();

Map<String, dynamic> _$GeoLocationToJson(GeoLocation instance) =>
    <String, dynamic>{};

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

Owner _$OwnerFromJson(Map<String, dynamic> json) => Owner()
  ..accountLocked = json['accountLocked'] as bool?
  ..pwdExpiryDate = (json['pwdExpiryDate'] as num?)?.toInt()
  ..gender = json['gender'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => OwnerRole.fromJson(e as Map<String, dynamic>))
      .toList()
  ..fatherOrHusbandName = json['fatherOrHusbandName'] as String?
  ..active = json['active'] as bool?
  ..emailId = json['emailId'] as String?
  ..ownerId = json['ownerId'] as String?
  ..userName = json['userName'] as String?
  ..type = json['type'] as String?
  ..uuid = json['uuid'] as String?
  ..isPrimaryOwner = json['isPrimaryOwner'] as bool?
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..tenantId = json['tenantId'] as String?
  ..name = json['name'] as String?
  ..id = (json['id'] as num?)?.toInt()
  ..status = json['status'] as bool?;

Map<String, dynamic> _$OwnerToJson(Owner instance) => <String, dynamic>{
      'accountLocked': instance.accountLocked,
      'pwdExpiryDate': instance.pwdExpiryDate,
      'gender': instance.gender,
      'mobileNumber': instance.mobileNumber,
      'roles': instance.roles,
      'fatherOrHusbandName': instance.fatherOrHusbandName,
      'active': instance.active,
      'emailId': instance.emailId,
      'ownerId': instance.ownerId,
      'userName': instance.userName,
      'type': instance.type,
      'uuid': instance.uuid,
      'isPrimaryOwner': instance.isPrimaryOwner,
      'auditDetails': instance.auditDetails,
      'tenantId': instance.tenantId,
      'name': instance.name,
      'id': instance.id,
      'status': instance.status,
    };

OwnerRole _$OwnerRoleFromJson(Map<String, dynamic> json) => OwnerRole()
  ..code = json['code'] as String?
  ..name = json['name'] as String?;

Map<String, dynamic> _$OwnerRoleToJson(OwnerRole instance) => <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
    };

Unit _$UnitFromJson(Map<String, dynamic> json) => Unit()
  ..unitType = json['unitType'] as String?
  ..occupancyType = json['occupancyType'] as String?
  ..occupancyDate = (json['occupancyDate'] as num?)?.toInt()
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..tenantId = json['tenantId'] as String?
  ..floorNo = json['floorNo'] as String?
  ..id = json['id'] as String?
  ..usageCategory = json['usageCategory'] as String?;

Map<String, dynamic> _$UnitToJson(Unit instance) => <String, dynamic>{
      'unitType': instance.unitType,
      'occupancyType': instance.occupancyType,
      'occupancyDate': instance.occupancyDate,
      'auditDetails': instance.auditDetails,
      'tenantId': instance.tenantId,
      'floorNo': instance.floorNo,
      'id': instance.id,
      'usageCategory': instance.usageCategory,
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
      ?.map((e) => AssignerRole.fromJson(e as Map<String, dynamic>))
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

AssignerRole _$AssignerRoleFromJson(Map<String, dynamic> json) => AssignerRole()
  ..id = json['id']
  ..name = json['name'] as String?
  ..code = json['code'] as String?
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$AssignerRoleToJson(AssignerRole instance) =>
    <String, dynamic>{
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

FieldInspectionPending _$FieldInspectionPendingFromJson(
        Map<String, dynamic> json) =>
    FieldInspectionPending()
      ..docs = (json['docs'] as List<dynamic>?)
          ?.map((e) => Doc.fromJson(e as Map<String, dynamic>))
          .toList()
      ..date =
          json['date'] == null ? null : DateTime.parse(json['date'] as String)
      ..questions = (json['questions'] as List<dynamic>?)
          ?.map((e) => Question.fromJson(e as Map<String, dynamic>))
          .toList()
      ..time = json['time'] as String?;

Map<String, dynamic> _$FieldInspectionPendingToJson(
        FieldInspectionPending instance) =>
    <String, dynamic>{
      'docs': instance.docs,
      'date': instance.date?.toIso8601String(),
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

Map<String, dynamic> _$QuestionToJson(Question instance) => <String, dynamic>{
      'remarks': instance.remarks,
      'question': instance.question,
      'value': instance.value,
    };
