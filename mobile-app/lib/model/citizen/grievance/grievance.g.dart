// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'grievance.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Grievance _$GrievanceFromJson(Map<String, dynamic> json) => Grievance()
  ..responseInfo = json['responseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['responseInfo'] as Map<String, dynamic>)
  ..serviceWrappers = (json['ServiceWrappers'] as List<dynamic>?)
      ?.map((e) => ServiceWrapper.fromJson(e as Map<String, dynamic>))
      .toList()
  ..complaintsResolved = (json['complaintsResolved'] as num?)?.toInt()
  ..averageResolutionTime = (json['averageResolutionTime'] as num?)?.toInt()
  ..complaintTypes = (json['complaintTypes'] as num?)?.toInt();

Map<String, dynamic> _$GrievanceToJson(Grievance instance) => <String, dynamic>{
      'responseInfo': instance.responseInfo,
      'ServiceWrappers': instance.serviceWrappers,
      'complaintsResolved': instance.complaintsResolved,
      'averageResolutionTime': instance.averageResolutionTime,
      'complaintTypes': instance.complaintTypes,
    };

ServiceWrapper _$ServiceWrapperFromJson(Map<String, dynamic> json) =>
    ServiceWrapper()
      ..service = json['service'] == null
          ? null
          : Service.fromJson(json['service'] as Map<String, dynamic>)
      ..workflow = json['workflow'] == null
          ? null
          : Workflow.fromJson(json['workflow'] as Map<String, dynamic>);

Map<String, dynamic> _$ServiceWrapperToJson(ServiceWrapper instance) =>
    <String, dynamic>{
      'service': instance.service,
      'workflow': instance.workflow,
    };

Service _$ServiceFromJson(Map<String, dynamic> json) => Service()
  ..active = json['active'] as bool?
  ..citizen = json['citizen'] == null
      ? null
      : Citizen.fromJson(json['citizen'] as Map<String, dynamic>)
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..serviceCode = json['serviceCode'] as String?
  ..serviceRequestId = json['serviceRequestId'] as String?
  ..description = json['description'] as String?
  ..accountId = json['accountId'] as String?
  ..rating = (json['rating'] as num?)?.toInt()
  ..additionalDetail = json['additionalDetail']
  ..applicationStatus = json['applicationStatus'] as String?
  ..source = json['source'] as String?
  ..address = json['address'] == null
      ? null
      : Address.fromJson(json['address'] as Map<String, dynamic>)
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..priority = json['priority'] as String?;

Map<String, dynamic> _$ServiceToJson(Service instance) => <String, dynamic>{
      'active': instance.active,
      'citizen': instance.citizen,
      'id': instance.id,
      'tenantId': instance.tenantId,
      'serviceCode': instance.serviceCode,
      'serviceRequestId': instance.serviceRequestId,
      'description': instance.description,
      'accountId': instance.accountId,
      'rating': instance.rating,
      'additionalDetail': instance.additionalDetail,
      'applicationStatus': instance.applicationStatus,
      'source': instance.source,
      'address': instance.address,
      'auditDetails': instance.auditDetails,
      'priority': instance.priority,
    };

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
  ..pinCode = json['pincode'] as String?
  ..additionDetails = json['additionDetails'] as String?
  ..buildingName = json['buildingName'] as String?
  ..street = json['street'] as String?
  ..locality = json['locality'] == null
      ? null
      : Locality.fromJson(json['locality'] as Map<String, dynamic>)
  ..geoLocation = json['geoLocation'] == null
      ? null
      : GeoLocation.fromJson(json['geoLocation'] as Map<String, dynamic>);

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
      'pincode': instance.pinCode,
      'additionDetails': instance.additionDetails,
      'buildingName': instance.buildingName,
      'street': instance.street,
      'locality': instance.locality,
      'geoLocation': instance.geoLocation,
    };

GeoLocation _$GeoLocationFromJson(Map<String, dynamic> json) => GeoLocation()
  ..latitude = (json['latitude'] as num?)?.toDouble()
  ..longitude = (json['longitude'] as num?)?.toDouble()
  ..additionalDetails = json['additionalDetails'];

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
  ..latitude = (json['latitude'] as num?)?.toDouble()
  ..longitude = (json['longitude'] as num?)?.toDouble()
  ..children = json['children'] as String?
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

Citizen _$CitizenFromJson(Map<String, dynamic> json) => Citizen()
  ..id = (json['id'] as num?)?.toInt()
  ..userName = json['userName'] as String?
  ..name = json['name'] as String?
  ..type = json['type'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..emailId = json['emailId'] as String?
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Role.fromJson(e as Map<String, dynamic>))
      .toList()
  ..tenantId = json['tenantId'] as String?
  ..uuid = json['uuid'] as String?
  ..active = json['active'] as bool?;

Map<String, dynamic> _$CitizenToJson(Citizen instance) => <String, dynamic>{
      'id': instance.id,
      'userName': instance.userName,
      'name': instance.name,
      'type': instance.type,
      'mobileNumber': instance.mobileNumber,
      'emailId': instance.emailId,
      'roles': instance.roles,
      'tenantId': instance.tenantId,
      'uuid': instance.uuid,
      'active': instance.active,
    };

Roles _$RolesFromJson(Map<String, dynamic> json) => Roles()
  ..id = (json['id'] as num?)?.toInt()
  ..name = json['name'] as String?
  ..code = json['code'] as String?
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$RolesToJson(Roles instance) => <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'code': instance.code,
      'tenantId': instance.tenantId,
    };

Workflow _$WorkflowFromJson(Map<String, dynamic> json) => Workflow()
  ..action = json['action'] as String?
  ..assignes = json['assignes']
  ..comments = json['comments']
  ..verificationDocuments = (json['verificationDocuments'] as List<dynamic>?)
      ?.map((e) => VerificationDocument.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$WorkflowToJson(Workflow instance) => <String, dynamic>{
      'action': instance.action,
      'assignes': instance.assignes,
      'comments': instance.comments,
      'verificationDocuments': instance.verificationDocuments,
    };

VerificationDocument _$VerificationDocumentFromJson(
        Map<String, dynamic> json) =>
    VerificationDocument()
      ..id = json['id'] as String?
      ..documentType = json['documentType'] as String?
      ..fileStoreId = json['fileStoreId'] as String?
      ..documentUid = json['documentUid'] as String?
      ..additionalDetails = json['additionalDetails'];

Map<String, dynamic> _$VerificationDocumentToJson(
        VerificationDocument instance) =>
    <String, dynamic>{
      'id': instance.id,
      'documentType': instance.documentType,
      'fileStoreId': instance.fileStoreId,
      'documentUid': instance.documentUid,
      'additionalDetails': instance.additionalDetails,
    };
