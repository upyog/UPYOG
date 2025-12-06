// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'emp_challans_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Challans _$ChallansFromJson(Map<String, dynamic> json) => Challans()
  ..countOfServices = (json['countOfServices'] as num?)?.toInt()
  ..totalAmountCollected = (json['totalAmountCollected'] as num?)?.toInt()
  ..challanValidity = (json['challanValidity'] as num?)?.toInt()
  ..responseInfo = json['responseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['responseInfo'] as Map<String, dynamic>)
  ..challans = (json['challans'] as List<dynamic>?)
      ?.map((e) => Challan.fromJson(e as Map<String, dynamic>))
      .toList()
  ..totalCount = (json['totalCount'] as num?)?.toInt();

Map<String, dynamic> _$ChallansToJson(Challans instance) => <String, dynamic>{
      'countOfServices': instance.countOfServices,
      'totalAmountCollected': instance.totalAmountCollected,
      'challanValidity': instance.challanValidity,
      'responseInfo': instance.responseInfo,
      'challans': instance.challans,
      'totalCount': instance.totalCount,
    };

Challan _$ChallanFromJson(Map<String, dynamic> json) => Challan()
  ..citizen = json['citizen'] == null
      ? null
      : Citizen.fromJson(json['citizen'] as Map<String, dynamic>)
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..businessService = json['businessService'] as String?
  ..challanNo = json['challanNo'] as String?
  ..referenceId = json['referenceId']
  ..description = json['description']
  ..accountId = json['accountId']
  ..additionalDetail = json['additionalDetail']
  ..source = json['source']
  ..taxPeriodFrom = (json['taxPeriodFrom'] as num?)?.toInt()
  ..taxPeriodTo = (json['taxPeriodTo'] as num?)?.toInt()
  ..calculation = json['calculation'] == null
      ? null
      : Calculation.fromJson(json['calculation'] as Map<String, dynamic>)
  ..amount = (json['amount'] as List<dynamic>?)
      ?.map((e) => Amount.fromJson(e as Map<String, dynamic>))
      .toList()
  ..address = json['address'] == null
      ? null
      : Address.fromJson(json['address'] as Map<String, dynamic>)
  ..fileStoreId = json['filestoreid']
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..applicationStatus = json['applicationStatus'] as String?
  ..receiptNumber = json['receiptNumber'];

Map<String, dynamic> _$ChallanToJson(Challan instance) => <String, dynamic>{
      'citizen': instance.citizen,
      'id': instance.id,
      'tenantId': instance.tenantId,
      'businessService': instance.businessService,
      'challanNo': instance.challanNo,
      'referenceId': instance.referenceId,
      'description': instance.description,
      'accountId': instance.accountId,
      'additionalDetail': instance.additionalDetail,
      'source': instance.source,
      'taxPeriodFrom': instance.taxPeriodFrom,
      'taxPeriodTo': instance.taxPeriodTo,
      'calculation': instance.calculation,
      'amount': instance.amount,
      'address': instance.address,
      'filestoreid': instance.fileStoreId,
      'auditDetails': instance.auditDetails,
      'applicationStatus': instance.applicationStatus,
      'receiptNumber': instance.receiptNumber,
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
  ..city = json['city']
  ..pinCode = json['pincode'] as String?
  ..detail = json['detail']
  ..buildingName = json['buildingName'] as String?
  ..street = json['street'] as String?
  ..locality = json['locality'] == null
      ? null
      : Locality.fromJson(json['locality'] as Map<String, dynamic>)
  ..plotNo = json['plotNo']
  ..district = json['district']
  ..state = json['state']
  ..country = json['country']
  ..region = json['region'];

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
      'pincode': instance.pinCode,
      'detail': instance.detail,
      'buildingName': instance.buildingName,
      'street': instance.street,
      'locality': instance.locality,
      'plotNo': instance.plotNo,
      'district': instance.district,
      'state': instance.state,
      'country': instance.country,
      'region': instance.region,
    };

Locality _$LocalityFromJson(Map<String, dynamic> json) => Locality()
  ..code = json['code'] as String?
  ..name = json['name']
  ..label = json['label']
  ..latitude = json['latitude']
  ..longitude = json['longitude']
  ..children = json['children']
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

Amount _$AmountFromJson(Map<String, dynamic> json) => Amount()
  ..taxHeadCode = json['taxHeadCode'] as String?
  ..amount = (json['amount'] as num?)?.toInt();

Map<String, dynamic> _$AmountToJson(Amount instance) => <String, dynamic>{
      'taxHeadCode': instance.taxHeadCode,
      'amount': instance.amount,
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

Calculation _$CalculationFromJson(Map<String, dynamic> json) => Calculation()
  ..challanNo = json['challanNo']
  ..challan = json['challan']
  ..tenantId = json['tenantId'] as String?
  ..taxHeadEstimates = (json['taxHeadEstimates'] as List<dynamic>?)
      ?.map((e) => TaxHeadEstimate.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$CalculationToJson(Calculation instance) =>
    <String, dynamic>{
      'challanNo': instance.challanNo,
      'challan': instance.challan,
      'tenantId': instance.tenantId,
      'taxHeadEstimates': instance.taxHeadEstimates,
    };

TaxHeadEstimate _$TaxHeadEstimateFromJson(Map<String, dynamic> json) =>
    TaxHeadEstimate()
      ..taxHeadCode = json['taxHeadCode'] as String?
      ..estimateAmount = (json['estimateAmount'] as num?)?.toInt()
      ..category = json['category'];

Map<String, dynamic> _$TaxHeadEstimateToJson(TaxHeadEstimate instance) =>
    <String, dynamic>{
      'taxHeadCode': instance.taxHeadCode,
      'estimateAmount': instance.estimateAmount,
      'category': instance.category,
    };

Citizen _$CitizenFromJson(Map<String, dynamic> json) => Citizen()
  ..id = (json['id'] as num?)?.toInt()
  ..uuid = json['uuid'] as String?
  ..userName = json['userName'] as String?
  ..password = json['password']
  ..salutation = json['salutation']
  ..name = json['name'] as String?
  ..gender = json['gender']
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
  ..correspondenceAddress = json['correspondenceAddress']
  ..active = json['active'] as bool?
  ..dob = json['dob']
  ..pwdExpiryDate = json['pwdExpiryDate']
  ..locale = json['locale']
  ..type = json['type'] as String?
  ..signature = json['signature']
  ..accountLocked = json['accountLocked']
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Role.fromJson(e as Map<String, dynamic>))
      .toList()
  ..fatherOrHusbandName = json['fatherOrHusbandName']
  ..bloodGroup = json['bloodGroup']
  ..identificationMark = json['identificationMark']
  ..photo = json['photo']
  ..createdBy = json['createdBy'] as String?
  ..createdDate = (json['createdDate'] as num?)?.toInt()
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..lastModifiedDate = (json['lastModifiedDate'] as num?)?.toInt()
  ..otpReference = json['otpReference']
  ..tenantId = json['tenantId'] as String?
  ..idToken = json['idToken']
  ..email = json['email']
  ..primaryRole = json['primaryrole'] as List<dynamic>?
  ..additionalRoles = json['additionalroles'];

Map<String, dynamic> _$CitizenToJson(Citizen instance) => <String, dynamic>{
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
      'idToken': instance.idToken,
      'email': instance.email,
      'primaryrole': instance.primaryRole,
      'additionalroles': instance.additionalRoles,
    };

Role _$RoleFromJson(Map<String, dynamic> json) => Role()
  ..id = json['id']
  ..name = json['name'] as String?
  ..code = json['code'] as String?
  ..tenantId = json['tenantId'];

Map<String, dynamic> _$RoleToJson(Role instance) => <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'code': instance.code,
      'tenantId': instance.tenantId,
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
