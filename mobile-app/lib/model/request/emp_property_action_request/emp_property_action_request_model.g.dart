// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'emp_property_action_request_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PropertyActionRequest _$PropertyActionRequestFromJson(
        Map<String, dynamic> json) =>
    PropertyActionRequest()
      ..id = json['id'] as String?
      ..propertyId = json['propertyId'] as String?
      ..surveyId = json['surveyId']
      ..linkedProperties = json['linkedProperties']
      ..tenantId = json['tenantId'] as String?
      ..accountId = json['accountId'] as String?
      ..oldPropertyId = json['oldPropertyId']
      ..status = json['status'] as String?
      ..address = json['address'] == null
          ? null
          : Address.fromJson(json['address'] as Map<String, dynamic>)
      ..acknowldgementNumber = json['acknowldgementNumber'] as String?
      ..propertyType = json['propertyType'] as String?
      ..ownershipCategory = json['ownershipCategory'] as String?
      ..owners = (json['owners'] as List<dynamic>?)
          ?.map((e) => Owner.fromJson(e as Map<String, dynamic>))
          .toList()
      ..institution = json['institution']
      ..creationReason = json['creationReason'] as String?
      ..usageCategory = json['usageCategory'] as String?
      ..noOfFloors = (json['noOfFloors'] as num?)?.toInt()
      ..landArea = (json['landArea'] as num?)?.toInt()
      ..superBuiltUpArea = json['superBuiltUpArea']
      ..source = json['source'] as String?
      ..channel = json['channel'] as String?
      ..documents = (json['documents'] as List<dynamic>?)
          ?.map((e) => PropertyDocument.fromJson(e as Map<String, dynamic>))
          .toList()
      ..units = (json['units'] as List<dynamic>?)
          ?.map((e) => Unit.fromJson(e as Map<String, dynamic>))
          .toList()
      ..dueAmount = json['dueAmount']
      ..dueAmountYear = json['dueAmountYear']
      ..additionalDetails = json['additionalDetails'] == null
          ? null
          : PropertyAdditionalDetails.fromJson(
              json['additionalDetails'] as Map<String, dynamic>)
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
      ..workflow = json['workflow'] == null
          ? null
          : Workflow.fromJson(json['workflow'] as Map<String, dynamic>)
      ..alternateUpdated = json['AlternateUpdated'] as bool?
      ..isOldDataEncryptionRequest =
          json['isOldDataEncryptionRequest'] as bool?;

Map<String, dynamic> _$PropertyActionRequestToJson(
        PropertyActionRequest instance) =>
    <String, dynamic>{
      'id': instance.id,
      'propertyId': instance.propertyId,
      'surveyId': instance.surveyId,
      'linkedProperties': instance.linkedProperties,
      'tenantId': instance.tenantId,
      'accountId': instance.accountId,
      'oldPropertyId': instance.oldPropertyId,
      'status': instance.status,
      'address': instance.address,
      'acknowldgementNumber': instance.acknowldgementNumber,
      'propertyType': instance.propertyType,
      'ownershipCategory': instance.ownershipCategory,
      'owners': instance.owners,
      'institution': instance.institution,
      'creationReason': instance.creationReason,
      'usageCategory': instance.usageCategory,
      'noOfFloors': instance.noOfFloors,
      'landArea': instance.landArea,
      'superBuiltUpArea': instance.superBuiltUpArea,
      'source': instance.source,
      'channel': instance.channel,
      'documents': instance.documents,
      'units': instance.units,
      'dueAmount': instance.dueAmount,
      'dueAmountYear': instance.dueAmountYear,
      'additionalDetails': instance.additionalDetails,
      'auditDetails': instance.auditDetails,
      if (instance.workflow case final value?) 'workflow': value,
      'AlternateUpdated': instance.alternateUpdated,
      'isOldDataEncryptionRequest': instance.isOldDataEncryptionRequest,
    };

PropertyAdditionalDetails _$PropertyAdditionalDetailsFromJson(
        Map<String, dynamic> json) =>
    PropertyAdditionalDetails()
      ..uid = json['uid'] as String?
      ..remarks = json['remarks'] as String?
      ..basement1 = json['basement1']
      ..basement2 = json['basement2']
      ..noOfFloors = json['noOfFloors'] == null
          ? null
          : NoO.fromJson(json['noOfFloors'] as Map<String, dynamic>)
      ..builtUpArea = json['builtUpArea']
      ..caseDetails = json['caseDetails'] as String?
      ..electricity = json['electricity'] as String?
      ..inflammable = json['inflammable'] as bool?
      ..marketValue = (json['marketValue'] as num?)?.toInt()
      ..documentDate = (json['documentDate'] as num?)?.toInt()
      ..propertyType = json['propertyType'] == null
          ? null
          : PropertyType.fromJson(json['propertyType'] as Map<String, dynamic>)
      ..subusagetype = json['subusagetype']
      ..ageOfProperty = json['ageOfProperty'] == null
          ? null
          : AgeOfProperty.fromJson(
              json['ageOfProperty'] as Map<String, dynamic>)
      ..documentValue = json['documentValue'] as String?
      ..structureType = json['structureType'] == null
          ? null
          : AgeOfProperty.fromJson(
              json['structureType'] as Map<String, dynamic>)
      ..documentNumber = json['documentNumber'] as String?
      ..noOofBasements = json['noOofBasements'] == null
          ? null
          : NoO.fromJson(json['noOofBasements'] as Map<String, dynamic>)
      ..applicationStatus = json['applicationStatus'] as String?
      ..heightAbove36Feet = json['heightAbove36Feet'] as bool?
      ..isMutationInCourt = json['isMutationInCourt'] as String?
      ..reasonForTransfer = json['reasonForTransfer'] as String?
      ..previousPropertyUuid = json['previousPropertyUuid'] as String?
      ..subusagetypeofrentedarea = json['Subusagetypeofrentedarea']
      ..isPropertyUnderGovtPossession =
          json['isPropertyUnderGovtPossession'] as String?
      ..isAnyPartOfThisFloorUnOccupied = json['IsAnyPartOfThisFloorUnOccupied'];

Map<String, dynamic> _$PropertyAdditionalDetailsToJson(
        PropertyAdditionalDetails instance) =>
    <String, dynamic>{
      'uid': instance.uid,
      'remarks': instance.remarks,
      'basement1': instance.basement1,
      'basement2': instance.basement2,
      'noOfFloors': instance.noOfFloors,
      'builtUpArea': instance.builtUpArea,
      'caseDetails': instance.caseDetails,
      'electricity': instance.electricity,
      'inflammable': instance.inflammable,
      'marketValue': instance.marketValue,
      'documentDate': instance.documentDate,
      'propertyType': instance.propertyType,
      'subusagetype': instance.subusagetype,
      'ageOfProperty': instance.ageOfProperty,
      'documentValue': instance.documentValue,
      'structureType': instance.structureType,
      'documentNumber': instance.documentNumber,
      'noOofBasements': instance.noOofBasements,
      'applicationStatus': instance.applicationStatus,
      'heightAbove36Feet': instance.heightAbove36Feet,
      'isMutationInCourt': instance.isMutationInCourt,
      'reasonForTransfer': instance.reasonForTransfer,
      'previousPropertyUuid': instance.previousPropertyUuid,
      'Subusagetypeofrentedarea': instance.subusagetypeofrentedarea,
      'isPropertyUnderGovtPossession': instance.isPropertyUnderGovtPossession,
      'IsAnyPartOfThisFloorUnOccupied': instance.isAnyPartOfThisFloorUnOccupied,
    };

AgeOfProperty _$AgeOfPropertyFromJson(Map<String, dynamic> json) =>
    AgeOfProperty()
      ..code = json['code'] as String?
      ..name = json['name'] as String?
      ..active = json['active'] as bool?
      ..i18NKey = json['i18nKey'] as String?;

Map<String, dynamic> _$AgeOfPropertyToJson(AgeOfProperty instance) =>
    <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'active': instance.active,
      'i18nKey': instance.i18NKey,
    };

NoO _$NoOFromJson(Map<String, dynamic> json) => NoO()
  ..code = (json['code'] as num?)?.toInt()
  ..i18NKey = json['i18nKey'] as String?;

Map<String, dynamic> _$NoOToJson(NoO instance) => <String, dynamic>{
      'code': instance.code,
      'i18nKey': instance.i18NKey,
    };

PropertyType _$PropertyTypeFromJson(Map<String, dynamic> json) => PropertyType()
  ..code = json['code'] as String?
  ..i18NKey = json['i18nKey'] as String?;

Map<String, dynamic> _$PropertyTypeToJson(PropertyType instance) =>
    <String, dynamic>{
      'code': instance.code,
      'i18nKey': instance.i18NKey,
    };

Address _$AddressFromJson(Map<String, dynamic> json) => Address()
  ..tenantId = json['tenantId'] as String?
  ..doorNo = json['doorNo'] as String?
  ..plotNo = json['plotNo']
  ..id = json['id'] as String?
  ..landmark = json['landmark'] as String?
  ..city = json['city'] as String?
  ..district = json['district']
  ..region = json['region']
  ..state = json['state']
  ..country = json['country']
  ..pincode = json['pincode'] as String?
  ..buildingName = json['buildingName']
  ..street = json['street'] as String?
  ..locality = json['locality'] == null
      ? null
      : Locality.fromJson(json['locality'] as Map<String, dynamic>)
  ..geoLocation = json['geoLocation'] == null
      ? null
      : GeoLocation.fromJson(json['geoLocation'] as Map<String, dynamic>)
  ..additionalDetails = json['additionalDetails'];

Map<String, dynamic> _$AddressToJson(Address instance) => <String, dynamic>{
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
      'buildingName': instance.buildingName,
      'street': instance.street,
      'locality': instance.locality,
      'geoLocation': instance.geoLocation,
      'additionalDetails': instance.additionalDetails,
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
  ..code = json['code'] as String?
  ..name = json['name'] as String?
  ..label = json['label'] as String?
  ..latitude = json['latitude']
  ..longitude = json['longitude']
  ..area = json['area'] as String?
  ..children = json['children'] as List<dynamic>?
  ..materializedPath = json['materializedPath'];

Map<String, dynamic> _$LocalityToJson(Locality instance) => <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'label': instance.label,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'area': instance.area,
      'children': instance.children,
      'materializedPath': instance.materializedPath,
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

PropertyDocument _$PropertyDocumentFromJson(Map<String, dynamic> json) =>
    PropertyDocument()
      ..id = json['id'] as String?
      ..documentType = json['documentType'] as String?
      ..fileStoreId = json['fileStoreId'] as String?
      ..documentUid = json['documentUid']
      ..auditDetails = json['auditDetails']
      ..status = json['status'] as String?;

Map<String, dynamic> _$PropertyDocumentToJson(PropertyDocument instance) =>
    <String, dynamic>{
      'id': instance.id,
      'documentType': instance.documentType,
      'fileStoreId': instance.fileStoreId,
      'documentUid': instance.documentUid,
      'auditDetails': instance.auditDetails,
      'status': instance.status,
    };

Owner _$OwnerFromJson(Map<String, dynamic> json) => Owner()
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
  ..permanentAddress = json['permanentAddress'] as String?
  ..permanentCity = json['permanentCity']
  ..permanentPinCode = json['permanentPinCode']
  ..correspondenceCity = json['correspondenceCity']
  ..correspondencePinCode = json['correspondencePinCode']
  ..correspondenceAddress = json['correspondenceAddress']
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
  ..alternatemobilenumber = json['alternatemobilenumber']
  ..ownerInfoUuid = json['ownerInfoUuid'] as String?
  ..isPrimaryOwner = json['isPrimaryOwner']
  ..ownerShipPercentage = json['ownerShipPercentage']
  ..ownerType = json['ownerType'] as String?
  ..institutionId = json['institutionId']
  ..status = json['status'] as String?
  ..documents = (json['documents'] as List<dynamic>?)
      ?.map((e) => PropertyDocument.fromJson(e as Map<String, dynamic>))
      .toList()
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : OwnerAdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>)
  ..relationship = json['relationship'] as String?;

Map<String, dynamic> _$OwnerToJson(Owner instance) => <String, dynamic>{
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
      'alternatemobilenumber': instance.alternatemobilenumber,
      'ownerInfoUuid': instance.ownerInfoUuid,
      'isPrimaryOwner': instance.isPrimaryOwner,
      'ownerShipPercentage': instance.ownerShipPercentage,
      'ownerType': instance.ownerType,
      'institutionId': instance.institutionId,
      'status': instance.status,
      'documents': instance.documents,
      'additionalDetails': instance.additionalDetails,
      'relationship': instance.relationship,
    };

OwnerAdditionalDetails _$OwnerAdditionalDetailsFromJson(
        Map<String, dynamic> json) =>
    OwnerAdditionalDetails()
      ..ownerName = json['ownerName'] as String?
      ..ownerSequence = (json['ownerSequence'] as num?)?.toInt();

Map<String, dynamic> _$OwnerAdditionalDetailsToJson(
        OwnerAdditionalDetails instance) =>
    <String, dynamic>{
      'ownerName': instance.ownerName,
      'ownerSequence': instance.ownerSequence,
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

Unit _$UnitFromJson(Map<String, dynamic> json) => Unit()
  ..id = json['id'] as String?
  ..tenantId = json['tenantId']
  ..floorNo = (json['floorNo'] as num?)?.toInt()
  ..unitType = json['unitType']
  ..usageCategory = json['usageCategory'] as String?
  ..occupancyType = json['occupancyType'] as String?
  ..active = json['active'] as bool?
  ..occupancyDate = (json['occupancyDate'] as num?)?.toInt()
  ..constructionDetail = json['constructionDetail'] == null
      ? null
      : ConstructionDetail.fromJson(
          json['constructionDetail'] as Map<String, dynamic>)
  ..additionalDetails = json['additionalDetails']
  ..auditDetails = json['auditDetails']
  ..arv = json['arv'];

Map<String, dynamic> _$UnitToJson(Unit instance) => <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'floorNo': instance.floorNo,
      'unitType': instance.unitType,
      'usageCategory': instance.usageCategory,
      'occupancyType': instance.occupancyType,
      'active': instance.active,
      'occupancyDate': instance.occupancyDate,
      'constructionDetail': instance.constructionDetail,
      'additionalDetails': instance.additionalDetails,
      'auditDetails': instance.auditDetails,
      'arv': instance.arv,
    };

ConstructionDetail _$ConstructionDetailFromJson(Map<String, dynamic> json) =>
    ConstructionDetail()
      ..carpetArea = json['carpetArea']
      ..builtUpArea = (json['builtUpArea'] as num?)?.toInt()
      ..plinthArea = json['plinthArea']
      ..superBuiltUpArea = json['superBuiltUpArea']
      ..constructionType = json['constructionType']
      ..constructionDate = json['constructionDate']
      ..dimensions = json['dimensions'];

Map<String, dynamic> _$ConstructionDetailToJson(ConstructionDetail instance) =>
    <String, dynamic>{
      'carpetArea': instance.carpetArea,
      'builtUpArea': instance.builtUpArea,
      'plinthArea': instance.plinthArea,
      'superBuiltUpArea': instance.superBuiltUpArea,
      'constructionType': instance.constructionType,
      'constructionDate': instance.constructionDate,
      'dimensions': instance.dimensions,
    };

Workflow _$WorkflowFromJson(Map<String, dynamic> json) => Workflow()
  ..action = json['action'] as String?
  ..comment = json['comment'] as String?
  ..businessService = json['businessService'] as String?
  ..moduleName = json['moduleName'] as String?
  ..assignes = (json['assignes'] as List<dynamic>?)
      ?.map((e) => Assignee.fromJson(e as Map<String, dynamic>))
      .toList()
  ..documents = (json['documents'] as List<dynamic>?)
      ?.map((e) => WorkflowDocument.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$WorkflowToJson(Workflow instance) => <String, dynamic>{
      'action': instance.action,
      'comment': instance.comment,
      'businessService': instance.businessService,
      'moduleName': instance.moduleName,
      'assignes': instance.assignes,
      'documents': instance.documents,
    };

Assignee _$AssigneeFromJson(Map<String, dynamic> json) => Assignee()
  ..uuid = json['uuid'] as String?
  ..name = json['name'] as String?;

Map<String, dynamic> _$AssigneeToJson(Assignee instance) => <String, dynamic>{
      'uuid': instance.uuid,
      'name': instance.name,
    };

WorkflowDocument _$WorkflowDocumentFromJson(Map<String, dynamic> json) =>
    WorkflowDocument()
      ..documentType = json['documentType'] as String?
      ..fileName = json['fileName'] as String?
      ..fileStoreId = json['fileStoreId'] as String?;

Map<String, dynamic> _$WorkflowDocumentToJson(WorkflowDocument instance) =>
    <String, dynamic>{
      'documentType': instance.documentType,
      'fileName': instance.fileName,
      'fileStoreId': instance.fileStoreId,
    };
