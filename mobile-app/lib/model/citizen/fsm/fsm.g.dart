// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'fsm.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FsmModel _$FsmModelFromJson(Map<String, dynamic> json) => FsmModel()
  ..totalCount = (json['totalCount'] as num?)?.toInt()
  ..responseInfo = json['responseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['responseInfo'] as Map<String, dynamic>)
  ..fsm = (json['fsm'] as List<dynamic>?)
      ?.map((e) => Fsm.fromJson(e as Map<String, dynamic>))
      .toList()
  ..workflow = json['workflow'];

Map<String, dynamic> _$FsmModelToJson(FsmModel instance) => <String, dynamic>{
      'totalCount': instance.totalCount,
      'responseInfo': instance.responseInfo,
      'fsm': instance.fsm,
      'workflow': instance.workflow,
    };

Fsm _$FsmFromJson(Map<String, dynamic> json) => Fsm()
  ..citizen = json['citizen'] == null
      ? null
      : Citizen.fromJson(json['citizen'] as Map<String, dynamic>)
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..applicationNo = json['applicationNo'] as String?
  ..description = json['description']
  ..accountId = json['accountId'] as String?
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : FsmAdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>)
  ..applicationStatus = json['applicationStatus'] as String?
  ..source = json['source'] as String?
  ..sanitationtype = json['sanitationtype'] as String?
  ..propertyUsage = json['propertyUsage'] as String?
  ..vehicleType = json['vehicleType']
  ..noOfTrips = (json['noOfTrips'] as num?)?.toInt()
  ..vehicleCapacity = json['vehicleCapacity'] as String?
  ..status = json['status']
  ..vehicleId = json['vehicleId'] as String?
  ..vehicle = json['vehicle']
  ..dsoId = json['dsoId'] as String?
  ..dso = json['dso']
  ..possibleServiceDate = (json['possibleServiceDate'] as num?)?.toInt()
  ..pitDetail = json['pitDetail'] == null
      ? null
      : PitDetail.fromJson(json['pitDetail'] as Map<String, dynamic>)
  ..address = json['address'] == null
      ? null
      : Address.fromJson(json['address'] as Map<String, dynamic>)
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..wasteCollected = json['wasteCollected']
  ..completedOn = (json['completedOn'] as num?)?.toInt()
  ..advanceAmount = (json['advanceAmount'] as num?)?.toInt()
  ..applicationType = json['applicationType'] as String?
  ..oldApplicationNo = json['oldApplicationNo']
  ..paymentPreference = json['paymentPreference'];

Map<String, dynamic> _$FsmToJson(Fsm instance) => <String, dynamic>{
      'citizen': instance.citizen,
      'id': instance.id,
      'tenantId': instance.tenantId,
      'applicationNo': instance.applicationNo,
      'description': instance.description,
      'accountId': instance.accountId,
      'additionalDetails': instance.additionalDetails,
      'applicationStatus': instance.applicationStatus,
      'source': instance.source,
      'sanitationtype': instance.sanitationtype,
      'propertyUsage': instance.propertyUsage,
      'vehicleType': instance.vehicleType,
      'noOfTrips': instance.noOfTrips,
      'vehicleCapacity': instance.vehicleCapacity,
      'status': instance.status,
      'vehicleId': instance.vehicleId,
      'vehicle': instance.vehicle,
      'dsoId': instance.dsoId,
      'dso': instance.dso,
      'possibleServiceDate': instance.possibleServiceDate,
      'pitDetail': instance.pitDetail,
      'address': instance.address,
      'auditDetails': instance.auditDetails,
      'wasteCollected': instance.wasteCollected,
      'completedOn': instance.completedOn,
      'advanceAmount': instance.advanceAmount,
      'applicationType': instance.applicationType,
      'oldApplicationNo': instance.oldApplicationNo,
      'paymentPreference': instance.paymentPreference,
    };

FsmAdditionalDetails _$FsmAdditionalDetailsFromJson(
        Map<String, dynamic> json) =>
    FsmAdditionalDetails()
      ..roadWidth = json['roadWidth'] as String?
      ..propertyId = json['propertyID'] as String?
      ..tripAmount = json['tripAmount']
      ..totalAmount = json['totalAmount']
      ..distancefromroad = json['distancefromroad'] as String?
      ..checkList = (json['CheckList'] as List<dynamic>?)
          ?.map((e) => CheckList.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$FsmAdditionalDetailsToJson(
        FsmAdditionalDetails instance) =>
    <String, dynamic>{
      'roadWidth': instance.roadWidth,
      'propertyID': instance.propertyId,
      'tripAmount': instance.tripAmount,
      'totalAmount': instance.totalAmount,
      'distancefromroad': instance.distancefromroad,
      'CheckList': instance.checkList,
    };

CheckList _$CheckListFromJson(Map<String, dynamic> json) => CheckList()
  ..code = json['code'] as String?
  ..value = json['value'] as String?;

Map<String, dynamic> _$CheckListToJson(CheckList instance) => <String, dynamic>{
      'code': instance.code,
      'value': instance.value,
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
  ..additionalDetails = checkAdditionalDetails(json['additionalDetails'])
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..buildingName = json['buildingName']
  ..street = json['street'] as String?
  ..slumName = json['slumName'] as String?
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
      'pincode': instance.pincode,
      'additionalDetails': instance.additionalDetails,
      'auditDetails': instance.auditDetails,
      'buildingName': instance.buildingName,
      'street': instance.street,
      'slumName': instance.slumName,
      'locality': instance.locality,
      'geoLocation': instance.geoLocation,
    };

AddressAdditionalDetails _$AddressAdditionalDetailsFromJson(
        Map<String, dynamic> json) =>
    AddressAdditionalDetails()
      ..boundaryType = json['boundaryType'] as String?
      ..gramPanchayat = json['gramPanchayat'] == null
          ? null
          : GramPanchayat.fromJson(
              json['gramPanchayat'] as Map<String, dynamic>)
      ..village = checkVillageFromJson(json['village'])
      ..newGramPanchayat = json['newGramPanchayat'] as String?;

Map<String, dynamic> _$AddressAdditionalDetailsToJson(
        AddressAdditionalDetails instance) =>
    <String, dynamic>{
      'boundaryType': instance.boundaryType,
      'gramPanchayat': instance.gramPanchayat,
      'village': checkVillageToJson(instance.village),
      'newGramPanchayat': instance.newGramPanchayat,
    };

GramPanchayat _$GramPanchayatFromJson(Map<String, dynamic> json) =>
    GramPanchayat()
      ..code = json['code'] as String?
      ..name = json['name'] as String?;

Map<String, dynamic> _$GramPanchayatToJson(GramPanchayat instance) =>
    <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
    };

Village _$VillageFromJson(Map<String, dynamic> json) => Village()
  ..code = json['code'] as String?
  ..name = json['name'] as String?;

Map<String, dynamic> _$VillageToJson(Village instance) => <String, dynamic>{
      'code': instance.code,
      if (instance.name case final value?) 'name': value,
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

GeoLocation _$GeoLocationFromJson(Map<String, dynamic> json) => GeoLocation()
  ..id = json['id'] as String?
  ..latitude = (json['latitude'] as num?)?.toInt()
  ..longitude = (json['longitude'] as num?)?.toInt()
  ..additionalDetails = json['additionalDetails'];

Map<String, dynamic> _$GeoLocationToJson(GeoLocation instance) =>
    <String, dynamic>{
      'id': instance.id,
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
  ..children = (json['children'] as List<dynamic>?)
      ?.map((e) => Locality.fromJson(e as Map<String, dynamic>))
      .toList()
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

Citizen _$CitizenFromJson(Map<String, dynamic> json) => Citizen()
  ..id = (json['id'] as num?)?.toInt()
  ..uuid = json['uuid'] as String?
  ..userName = json['userName'] as String?
  ..name = json['name'] as String?
  ..password = json['password']
  ..mobileNumber = json['mobileNumber'] as String?
  ..tenantId = json['tenantId'] as String?
  ..salutation = json['salutation']
  ..emailId = json['emailId'] as String?
  ..altContactNumber = json['altContactNumber']
  ..pan = json['pan']
  ..aadhaarNumber = json['aadhaarNumber']
  ..permanentAddress = json['permanentAddress'] as String?
  ..permanentCity = json['permanentCity']
  ..permanentPinCode = json['permanentPinCode']
  ..correspondenceCity = json['correspondenceCity']
  ..correspondencePinCode = json['correspondencePinCode']
  ..active = json['active'] as bool?
  ..dob = (json['dob'] as num?)?.toInt()
  ..pwdExpiryDate = (json['pwdExpiryDate'] as num?)?.toInt()
  ..locale = json['locale']
  ..type = json['type'] as String?
  ..signature = json['signature']
  ..accountLocked = json['accountLocked'] as bool?
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Role.fromJson(e as Map<String, dynamic>))
      .toList()
  ..bloodGroup = json['bloodGroup']
  ..identificationMark = json['identificationMark']
  ..photo = json['photo']
  ..createdBy = json['createdBy'] as String?
  ..createdDate = (json['createdDate'] as num?)?.toInt()
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..lastModifiedDate = (json['lastModifiedDate'] as num?)?.toInt()
  ..otpReference = json['otpReference']
  ..gender = json['gender'] as String?;

Map<String, dynamic> _$CitizenToJson(Citizen instance) => <String, dynamic>{
      'id': instance.id,
      'uuid': instance.uuid,
      'userName': instance.userName,
      'name': instance.name,
      'password': instance.password,
      'mobileNumber': instance.mobileNumber,
      'tenantId': instance.tenantId,
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
      'gender': instance.gender,
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

PitDetail _$PitDetailFromJson(Map<String, dynamic> json) => PitDetail()
  ..type = json['type']
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..height = (json['height'] as num?)?.toDouble()
  ..length = (json['length'] as num?)?.toInt()
  ..width = (json['width'] as num?)?.toInt()
  ..diameter = (json['diameter'] as num?)?.toInt()
  ..distanceFromRoad = (json['distanceFromRoad'] as num?)?.toInt()
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : PitDetailAdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$PitDetailToJson(PitDetail instance) => <String, dynamic>{
      'type': instance.type,
      'id': instance.id,
      'tenantId': instance.tenantId,
      'height': instance.height,
      'length': instance.length,
      'width': instance.width,
      'diameter': instance.diameter,
      'distanceFromRoad': instance.distanceFromRoad,
      'auditDetails': instance.auditDetails,
      'additionalDetails': instance.additionalDetails,
    };

PitDetailAdditionalDetails _$PitDetailAdditionalDetailsFromJson(
        Map<String, dynamic> json) =>
    PitDetailAdditionalDetails()
      ..fileStoreId = json['fileStoreId'] == null
          ? null
          : FileStoreId.fromJson(json['fileStoreId'] as Map<String, dynamic>);

Map<String, dynamic> _$PitDetailAdditionalDetailsToJson(
        PitDetailAdditionalDetails instance) =>
    <String, dynamic>{
      'fileStoreId': instance.fileStoreId,
    };

FileStoreId _$FileStoreIdFromJson(Map<String, dynamic> json) => FileStoreId()
  ..citizen =
      (json['CITIZEN'] as List<dynamic>?)?.map((e) => e as String).toList();

Map<String, dynamic> _$FileStoreIdToJson(FileStoreId instance) =>
    <String, dynamic>{
      'CITIZEN': instance.citizen,
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
