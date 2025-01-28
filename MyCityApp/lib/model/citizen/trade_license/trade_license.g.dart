// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'trade_license.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

TradeLicense _$TradeLicenseFromJson(Map<String, dynamic> json) => TradeLicense()
  ..responseInfo = json['ResponseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
  ..licenses = (json['Licenses'] as List<dynamic>?)
      ?.map((e) => License.fromJson(e as Map<String, dynamic>))
      .toList()
  ..count = (json['Count'] as num?)?.toInt()
  ..applicationsIssued = (json['applicationsIssued'] as num?)?.toInt()
  ..applicationsRenewed = (json['applicationsRenewed'] as num?)?.toInt()
  ..applicationValidity = (json['applicationValidity'] as num?)?.toInt();

Map<String, dynamic> _$TradeLicenseToJson(TradeLicense instance) =>
    <String, dynamic>{
      'ResponseInfo': instance.responseInfo,
      'Licenses': instance.licenses,
      'Count': instance.count,
      'applicationsIssued': instance.applicationsIssued,
      'applicationsRenewed': instance.applicationsRenewed,
      'applicationValidity': instance.applicationValidity,
    };

License _$LicenseFromJson(Map<String, dynamic> json) => License()
  ..comment = json['comment'] as String?
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..businessService = json['businessService'] as String?
  ..licenseType = json['licenseType'] as String?
  ..applicationType = json['applicationType'] as String?
  ..workflowCode = json['workflowCode'] as String?
  ..licenseNumber = json['licenseNumber']
  ..applicationNumber = json['applicationNumber'] as String?
  ..oldLicenseNumber = json['oldLicenseNumber']
  ..propertyId = json['propertyId']
  ..oldPropertyId = json['oldPropertyId']
  ..accountId = json['accountId']
  ..tradeName = json['tradeName'] as String?
  ..applicationDate = (json['applicationDate'] as num?)?.toInt()
  ..commencementDate = (json['commencementDate'] as num?)?.toInt()
  ..issuedDate = json['issuedDate']
  ..financialYear = json['financialYear'] as String?
  ..validFrom = (json['validFrom'] as num?)?.toInt()
  ..validTo = (json['validTo'] as num?)?.toInt()
  ..action = json['action'] as String?
  ..assignee =
      (json['assignee'] as List<dynamic>?)?.map((e) => e as String).toList()
  ..wfDocuments = (json['wfDocuments'] as List<dynamic>?)
      ?.map((e) => WfDocument.fromJson(e as Map<String, dynamic>))
      .toList()
  ..status = json['status'] as String?
  ..tradeLicenseDetail = json['tradeLicenseDetail'] == null
      ? null
      : TradeLicenseDetail.fromJson(
          json['tradeLicenseDetail'] as Map<String, dynamic>)
  ..calculation = json['calculation']
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..fileStoreId = json['fileStoreId'];

Map<String, dynamic> _$LicenseToJson(License instance) => <String, dynamic>{
      'comment': instance.comment,
      'id': instance.id,
      'tenantId': instance.tenantId,
      'businessService': instance.businessService,
      'licenseType': instance.licenseType,
      'applicationType': instance.applicationType,
      'workflowCode': instance.workflowCode,
      'licenseNumber': instance.licenseNumber,
      'applicationNumber': instance.applicationNumber,
      'oldLicenseNumber': instance.oldLicenseNumber,
      'propertyId': instance.propertyId,
      'oldPropertyId': instance.oldPropertyId,
      'accountId': instance.accountId,
      'tradeName': instance.tradeName,
      'applicationDate': instance.applicationDate,
      'commencementDate': instance.commencementDate,
      'issuedDate': instance.issuedDate,
      'financialYear': instance.financialYear,
      'validFrom': instance.validFrom,
      'validTo': instance.validTo,
      'action': instance.action,
      'assignee': instance.assignee,
      'wfDocuments': instance.wfDocuments,
      'status': instance.status,
      'tradeLicenseDetail': instance.tradeLicenseDetail,
      'calculation': instance.calculation,
      'auditDetails': instance.auditDetails,
      'fileStoreId': instance.fileStoreId,
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
      ..surveyNo = json['surveyNo']
      ..subOwnerShipCategory = json['subOwnerShipCategory'] as String?
      ..structureType = json['structureType'] as String?
      ..operationalArea = (json['operationalArea'] as num?)?.toDouble()
      ..noOfEmployees = (json['noOfEmployees'] as num?)?.toInt()
      ..adhocExemption = json['adhocExemption']
      ..adhocPenalty = json['adhocPenalty']
      ..adhocExemptionReason = json['adhocExemptionReason']
      ..adhocPenaltyReason = json['adhocPenaltyReason']
      ..owners = (json['owners'] as List<dynamic>?)
          ?.map((e) => Owner.fromJson(e as Map<String, dynamic>))
          .toList()
      ..channel = json['channel'] as String?
      ..address = json['address'] == null
          ? null
          : Address.fromJson(json['address'] as Map<String, dynamic>)
      ..tradeUnits = (json['tradeUnits'] as List<dynamic>?)
          ?.map((e) => TradeUnit.fromJson(e as Map<String, dynamic>))
          .toList()
      ..accessories = (json['accessories'] as List<dynamic>?)
          ?.map((e) => e == null
              ? null
              : Accessories.fromJson(e as Map<String, dynamic>))
          .toList()
      ..applicationDocuments = (json['applicationDocuments'] as List<dynamic>?)
          ?.map((e) => ApplicationDocument.fromJson(e as Map<String, dynamic>))
          .toList()
      ..verificationDocuments = json['verificationDocuments']
      ..additionalDetail = json['additionalDetail'] == null
          ? null
          : AdditionalDetail.fromJson(
              json['additionalDetail'] as Map<String, dynamic>)
      ..institution = json['institution']
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$TradeLicenseDetailToJson(TradeLicenseDetail instance) =>
    <String, dynamic>{
      'id': instance.id,
      'surveyNo': instance.surveyNo,
      'subOwnerShipCategory': instance.subOwnerShipCategory,
      'structureType': instance.structureType,
      'operationalArea': instance.operationalArea,
      'noOfEmployees': instance.noOfEmployees,
      'adhocExemption': instance.adhocExemption,
      'adhocPenalty': instance.adhocPenalty,
      'adhocExemptionReason': instance.adhocExemptionReason,
      'adhocPenaltyReason': instance.adhocPenaltyReason,
      'owners': instance.owners,
      'channel': instance.channel,
      'address': instance.address,
      'tradeUnits': instance.tradeUnits,
      'accessories': instance.accessories,
      'applicationDocuments': instance.applicationDocuments,
      'verificationDocuments': instance.verificationDocuments,
      'additionalDetail': instance.additionalDetail,
      'institution': instance.institution,
      'auditDetails': instance.auditDetails,
    };

Accessories _$AccessoriesFromJson(Map<String, dynamic> json) => Accessories()
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..active = json['active'] as bool?
  ..accessoryCategory = json['accessoryCategory'] as String?
  ..uom = json['uom'] as String?
  ..uomValue = json['uomValue'] as String?
  ..count = (json['count'] as num?)?.toInt()
  ..auditDetails = json['auditDetails'];

Map<String, dynamic> _$AccessoriesToJson(Accessories instance) =>
    <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'active': instance.active,
      'accessoryCategory': instance.accessoryCategory,
      'uom': instance.uom,
      'uomValue': instance.uomValue,
      'count': instance.count,
      'auditDetails': instance.auditDetails,
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
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..doorNo = json['doorNo'] as String?
  ..latitude = json['latitude']
  ..longitude = json['longitude']
  ..addressId = json['addressId']
  ..addressNumber = json['addressNumber']
  ..type = json['type']
  ..addressLine1 = json['addressLine1']
  ..addressLine2 = json['addressLine2']
  ..landmark = json['landmark']
  ..city = json['city'] as String?
  ..pincode = json['pincode']
  ..detail = json['detail']
  ..buildingName = json['buildingName']
  ..street = json['street'] as String?
  ..locality = json['locality'] == null
      ? null
      : Locality.fromJson(json['locality'] as Map<String, dynamic>);

Map<String, dynamic> _$AddressToJson(Address instance) => <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'doorNo': instance.doorNo,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'addressId': instance.addressId,
      'addressNumber': instance.addressNumber,
      'type': instance.type,
      'addressLine1': instance.addressLine1,
      'addressLine2': instance.addressLine2,
      'landmark': instance.landmark,
      'city': instance.city,
      'pincode': instance.pincode,
      'detail': instance.detail,
      'buildingName': instance.buildingName,
      'street': instance.street,
      'locality': instance.locality,
    };

Locality _$LocalityFromJson(Map<String, dynamic> json) => Locality()
  ..code = json['code'] as String?
  ..name = json['name'] as String?
  ..label = json['label'] as String?
  ..latitude = json['latitude']
  ..longitude = json['longitude']
  ..children = json['children'] as List<dynamic>?
  ..materializedPath = json['materializedPath'];

Map<String, dynamic> _$LocalityToJson(Locality instance) => <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'label': instance.label,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'children': instance.children,
      'materializedPath': instance.materializedPath,
    };

ApplicationDocument _$ApplicationDocumentFromJson(Map<String, dynamic> json) =>
    ApplicationDocument()
      ..id = json['id'] as String?
      ..active = json['active'] as bool?
      ..tenantId = json['tenantId'] as String?
      ..documentType = json['documentType'] as String?
      ..fileStoreId = json['fileStoreId'] as String?
      ..documentUid = json['documentUid']
      ..auditDetails = json['auditDetails'];

Map<String, dynamic> _$ApplicationDocumentToJson(
        ApplicationDocument instance) =>
    <String, dynamic>{
      'id': instance.id,
      'active': instance.active,
      'tenantId': instance.tenantId,
      'documentType': instance.documentType,
      'fileStoreId': instance.fileStoreId,
      'documentUid': instance.documentUid,
      'auditDetails': instance.auditDetails,
    };

Owner _$OwnerFromJson(Map<String, dynamic> json) => Owner()
  ..id = (json['id'] as num?)?.toInt()
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
      ?.map((e) => Roles.fromJson(e as Map<String, dynamic>))
      .toList()
  ..fatherOrHusbandName = json['fatherOrHusbandName'] as String?
  ..bloodGroup = json['bloodGroup']
  ..identificationMark = json['identificationMark']
  ..photo = json['photo']
  ..createdBy = json['createdBy'] as String?
  ..createdDate = (json['createdDate'] as num?)?.toInt()
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..lastModifiedDate = (json['lastModifiedDate'] as num?)?.toInt()
  ..otpReference = json['otpReference']
  ..tenantId = json['tenantId'] as String?
  ..isPrimaryOwner = json['isPrimaryOwner']
  ..ownerShipPercentage = json['ownerShipPercentage']
  ..ownerType = json['ownerType'] as String?
  ..institutionId = json['institutionId']
  ..additionalDetails = json['additionalDetails']
  ..documents = json['documents']
  ..userActive = json['userActive'] as bool?
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
      'otpReference': instance.otpReference,
      'tenantId': instance.tenantId,
      'isPrimaryOwner': instance.isPrimaryOwner,
      'ownerShipPercentage': instance.ownerShipPercentage,
      'ownerType': instance.ownerType,
      'institutionId': instance.institutionId,
      'additionalDetails': instance.additionalDetails,
      'documents': instance.documents,
      'userActive': instance.userActive,
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
  ..tradeType = json['tradeType'] as String?
  ..uom = json['uom'] as String?
  ..uomValue = json['uomValue'] as String?
  ..auditDetails = json['auditDetails'];

Map<String, dynamic> _$TradeUnitToJson(TradeUnit instance) => <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'active': instance.active,
      'tradeType': instance.tradeType,
      'uom': instance.uom,
      'uomValue': instance.uomValue,
      'auditDetails': instance.auditDetails,
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
