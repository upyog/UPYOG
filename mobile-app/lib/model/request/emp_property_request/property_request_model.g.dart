// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'property_request_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PropertyRequest _$PropertyRequestFromJson(Map<String, dynamic> json) =>
    PropertyRequest()
      ..tenantId = json['tenantId'] as String?
      ..address = json['address'] == null
          ? null
          : AddressRequest.fromJson(json['address'] as Map<String, dynamic>)
      ..usageCategory = json['usageCategory'] as String?
      ..usageCategoryMajor = json['usageCategoryMajor'] as String?
      ..usageCategoryMinor = json['usageCategoryMinor'] as String?
      ..landArea = (json['landArea'] as num?)?.toInt()
      ..superBuiltUpArea = (json['superBuiltUpArea'] as num?)?.toInt()
      ..propertyType = json['propertyType'] as String?
      ..noOfFloors = (json['noOfFloors'] as num?)?.toInt()
      ..ownershipCategory = json['ownershipCategory'] as String?
      ..additionalDetails = json['additionalDetails'] == null
          ? null
          : PropertyRequestAdditionalDetails.fromJson(
              json['additionalDetails'] as Map<String, dynamic>)
      ..owners = (json['owners'] as List<dynamic>?)
          ?.map((e) => OwnerRequest.fromJson(e as Map<String, dynamic>))
          .toList()
      ..channel = json['channel'] as String?
      ..creationReason = json['creationReason'] as String?
      ..source = json['source'] as String?
      ..units = (json['units'] as List<dynamic>?)
          ?.map((e) => PropertyRequestUnit.fromJson(e as Map<String, dynamic>))
          .toList()
      ..documents = (json['documents'] as List<dynamic>?)
          ?.map((e) => DocumentRequest.fromJson(e as Map<String, dynamic>))
          .toList()
      ..applicationStatus = json['applicationStatus'] as String?
      ..institution = json['institution'] == null
          ? null
          : InstitutionRequest.fromJson(
              json['institution'] as Map<String, dynamic>);

Map<String, dynamic> _$PropertyRequestToJson(PropertyRequest instance) =>
    <String, dynamic>{
      'tenantId': instance.tenantId,
      'address': instance.address,
      'usageCategory': instance.usageCategory,
      'usageCategoryMajor': instance.usageCategoryMajor,
      'usageCategoryMinor': instance.usageCategoryMinor,
      'landArea': instance.landArea,
      'superBuiltUpArea': instance.superBuiltUpArea,
      'propertyType': instance.propertyType,
      'noOfFloors': instance.noOfFloors,
      'ownershipCategory': instance.ownershipCategory,
      'additionalDetails': instance.additionalDetails,
      'owners': instance.owners,
      'channel': instance.channel,
      'creationReason': instance.creationReason,
      'source': instance.source,
      'units': instance.units,
      'documents': instance.documents,
      'applicationStatus': instance.applicationStatus,
      if (instance.institution case final value?) 'institution': value,
    };

PropertyRequestAdditionalDetails _$PropertyRequestAdditionalDetailsFromJson(
        Map<String, dynamic> json) =>
    PropertyRequestAdditionalDetails()
      ..primaryOwner = json['primaryOwner'] as String?
      ..unit = (json['unit'] as List<dynamic>?)
          ?.map((e) =>
              AdditionalDetailsUnitRequest.fromJson(e as Map<String, dynamic>))
          .toList()
      ..ageOfProperty = json['ageOfProperty'] == null
          ? null
          : AgeOfPropertyRequest.fromJson(
              json['ageOfProperty'] as Map<String, dynamic>)
      ..structureType = json['structureType'] == null
          ? null
          : AgeOfPropertyRequest.fromJson(
              json['structureType'] as Map<String, dynamic>)
      ..electricity = json['electricity'] as String?
      ..uid = json['uid'] as String?
      ..owners = (json['owners'] as List<dynamic>?)
          ?.map((e) => OwnerRequest.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$PropertyRequestAdditionalDetailsToJson(
        PropertyRequestAdditionalDetails instance) =>
    <String, dynamic>{
      'primaryOwner': instance.primaryOwner,
      'unit': instance.unit,
      'ageOfProperty': instance.ageOfProperty,
      'structureType': instance.structureType,
      'electricity': instance.electricity,
      'uid': instance.uid,
      'owners': instance.owners,
    };

AgeOfPropertyRequest _$AgeOfPropertyRequestFromJson(
        Map<String, dynamic> json) =>
    AgeOfPropertyRequest()
      ..i18NKey = json['i18nKey'] as String?
      ..name = json['name'] as String?
      ..code = json['code'] as String?
      ..active = json['active'] as bool?;

Map<String, dynamic> _$AgeOfPropertyRequestToJson(
        AgeOfPropertyRequest instance) =>
    <String, dynamic>{
      'i18nKey': instance.i18NKey,
      'name': instance.name,
      'code': instance.code,
      'active': instance.active,
    };

OwnerRequest _$OwnerRequestFromJson(Map<String, dynamic> json) => OwnerRequest()
  ..name = json['name'] as String?
  ..gender = json['gender'] as String?
  ..fatherOrHusbandName = json['fatherOrHusbandName'] as String?
  ..relationship = json['relationship'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..designation = json['designation'] as String?
  ..altContactNumber = json['altContactNumber'] as String?
  ..emailId = json['emailId'] as String?
  ..correspondenceAddress = json['correspondenceAddress'] as String?
  ..isCorrespondenceAddress = json['isCorrespondenceAddress'] as bool?
  ..ownerType = json['ownerType'] as String?
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : OwnerAdditionalDetailsRequest.fromJson(
          json['additionalDetails'] as Map<String, dynamic>)
  ..documents = (json['documents'] as List<dynamic>?)
      ?.map((e) => DocumentRequest.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$OwnerRequestToJson(OwnerRequest instance) =>
    <String, dynamic>{
      if (instance.name case final value?) 'name': value,
      if (instance.gender case final value?) 'gender': value,
      if (instance.fatherOrHusbandName case final value?)
        'fatherOrHusbandName': value,
      if (instance.relationship case final value?) 'relationship': value,
      if (instance.mobileNumber case final value?) 'mobileNumber': value,
      if (instance.designation case final value?) 'designation': value,
      if (instance.altContactNumber case final value?)
        'altContactNumber': value,
      if (instance.emailId case final value?) 'emailId': value,
      if (instance.correspondenceAddress case final value?)
        'correspondenceAddress': value,
      if (instance.isCorrespondenceAddress case final value?)
        'isCorrespondenceAddress': value,
      if (instance.ownerType case final value?) 'ownerType': value,
      if (instance.additionalDetails case final value?)
        'additionalDetails': value,
      if (instance.documents case final value?) 'documents': value,
    };

OwnerAdditionalDetailsRequest _$OwnerAdditionalDetailsRequestFromJson(
        Map<String, dynamic> json) =>
    OwnerAdditionalDetailsRequest()
      ..ownerSequence = (json['ownerSequence'] as num?)?.toInt()
      ..ownerName = json['ownerName'] as String?;

Map<String, dynamic> _$OwnerAdditionalDetailsRequestToJson(
        OwnerAdditionalDetailsRequest instance) =>
    <String, dynamic>{
      'ownerSequence': instance.ownerSequence,
      'ownerName': instance.ownerName,
    };

DocumentRequest _$DocumentRequestFromJson(Map<String, dynamic> json) =>
    DocumentRequest()
      ..documentType = json['documentType'] as String?
      ..fileStoreId = json['fileStoreId'] as String?
      ..documentUid = json['documentUid'] as String?;

Map<String, dynamic> _$DocumentRequestToJson(DocumentRequest instance) =>
    <String, dynamic>{
      'documentType': instance.documentType,
      'fileStoreId': instance.fileStoreId,
      'documentUid': instance.documentUid,
    };

AdditionalDetailsUnitRequest _$AdditionalDetailsUnitRequestFromJson(
        Map<String, dynamic> json) =>
    AdditionalDetailsUnitRequest(
      rentedMonths: json['RentedMonths'] as String?,
      nonRentedMonthsUsage: json['NonRentedMonthsUsage'],
      floorNo: json['floorNo'],
    );

Map<String, dynamic> _$AdditionalDetailsUnitRequestToJson(
        AdditionalDetailsUnitRequest instance) =>
    <String, dynamic>{
      'RentedMonths': instance.rentedMonths,
      'NonRentedMonthsUsage': instance.nonRentedMonthsUsage,
      'floorNo': instance.floorNo,
    };

AddressRequest _$AddressRequestFromJson(Map<String, dynamic> json) =>
    AddressRequest()
      ..pincode = json['pincode'] as String?
      ..city = json['city'] as String?
      ..locality = json['locality'] == null
          ? null
          : LocalityRequest.fromJson(json['locality'] as Map<String, dynamic>)
      ..street = json['street'] as String?
      ..doorNo = json['doorNo'] as String?;

Map<String, dynamic> _$AddressRequestToJson(AddressRequest instance) =>
    <String, dynamic>{
      if (instance.pincode case final value?) 'pincode': value,
      'city': instance.city,
      'locality': instance.locality,
      if (instance.street case final value?) 'street': value,
      if (instance.doorNo case final value?) 'doorNo': value,
    };

LocalityRequest _$LocalityRequestFromJson(Map<String, dynamic> json) =>
    LocalityRequest()
      ..code = json['code'] as String?
      ..area = json['area'] as String?;

Map<String, dynamic> _$LocalityRequestToJson(LocalityRequest instance) =>
    <String, dynamic>{
      'code': instance.code,
      'area': instance.area,
    };

InstitutionRequest _$InstitutionRequestFromJson(Map<String, dynamic> json) =>
    InstitutionRequest()
      ..name = json['name'] as String?
      ..type = json['type'] as String?
      ..designation = json['designation'] as String?
      ..nameOfAuthorizedPerson = json['nameOfAuthorizedPerson'] as String?
      ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$InstitutionRequestToJson(InstitutionRequest instance) =>
    <String, dynamic>{
      'name': instance.name,
      'type': instance.type,
      'designation': instance.designation,
      'nameOfAuthorizedPerson': instance.nameOfAuthorizedPerson,
      'tenantId': instance.tenantId,
    };

PropertyRequestUnit _$PropertyRequestUnitFromJson(Map<String, dynamic> json) =>
    PropertyRequestUnit()
      ..occupancyType = json['occupancyType'] as String?
      ..rentedMonths = json['rentedMonths'] as String?
      ..floorNo = json['floorNo'] as String?
      ..constructionDetail = json['constructionDetail'] == null
          ? null
          : ConstructionDetailRequest.fromJson(
              json['constructionDetail'] as Map<String, dynamic>)
      ..tenantId = json['tenantId'] as String?
      ..usageCategory = json['usageCategory'] as String?
      ..arv = json['arv'] as String?;

Map<String, dynamic> _$PropertyRequestUnitToJson(
        PropertyRequestUnit instance) =>
    <String, dynamic>{
      if (instance.occupancyType case final value?) 'occupancyType': value,
      if (instance.rentedMonths case final value?) 'rentedMonths': value,
      if (instance.floorNo case final value?) 'floorNo': value,
      if (instance.constructionDetail case final value?)
        'constructionDetail': value,
      if (instance.tenantId case final value?) 'tenantId': value,
      if (instance.usageCategory case final value?) 'usageCategory': value,
      if (instance.arv case final value?) 'arv': value,
    };

ConstructionDetailRequest _$ConstructionDetailRequestFromJson(
        Map<String, dynamic> json) =>
    ConstructionDetailRequest()..builtUpArea = json['builtUpArea'] as String?;

Map<String, dynamic> _$ConstructionDetailRequestToJson(
        ConstructionDetailRequest instance) =>
    <String, dynamic>{
      'builtUpArea': instance.builtUpArea,
    };
