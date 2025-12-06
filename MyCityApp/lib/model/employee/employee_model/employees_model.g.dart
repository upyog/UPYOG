// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'employees_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EmployeeModel _$EmployeeModelFromJson(Map<String, dynamic> json) =>
    EmployeeModel()
      ..employees = (json['Employees'] as List<dynamic>?)
          ?.map((e) => Employee.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$EmployeeModelToJson(EmployeeModel instance) =>
    <String, dynamic>{
      'Employees': instance.employees,
    };

Employee _$EmployeeFromJson(Map<String, dynamic> json) => Employee()
  ..id = (json['id'] as num?)?.toInt()
  ..uuid = json['uuid'] as String?
  ..code = json['code'] as String?
  ..employeeStatus = json['employeeStatus'] as String?
  ..employeeType = json['employeeType'] as String?
  ..dateOfAppointment = json['dateOfAppointment']
  ..jurisdictions = (json['jurisdictions'] as List<dynamic>?)
      ?.map((e) => Jurisdiction.fromJson(e as Map<String, dynamic>))
      .toList()
  ..assignments = (json['assignments'] as List<dynamic>?)
      ?.map((e) => Assignment.fromJson(e as Map<String, dynamic>))
      .toList()
  ..serviceHistory = json['serviceHistory'] as List<dynamic>?
  ..education = json['education'] as List<dynamic>?
  ..tests = json['tests'] as List<dynamic>?
  ..tenantId = json['tenantId'] as String?
  ..documents = json['documents'] as List<dynamic>?
  ..deactivationDetails = json['deactivationDetails'] as List<dynamic>?
  ..reactivationDetails = json['reactivationDetails'] as List<dynamic>?
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..reActivateEmployee = json['reActivateEmployee'] as bool?
  ..user = json['user'] == null
      ? null
      : User.fromJson(json['user'] as Map<String, dynamic>)
  ..isActive = json['isActive'] as bool?;

Map<String, dynamic> _$EmployeeToJson(Employee instance) => <String, dynamic>{
      'id': instance.id,
      'uuid': instance.uuid,
      'code': instance.code,
      'employeeStatus': instance.employeeStatus,
      'employeeType': instance.employeeType,
      'dateOfAppointment': instance.dateOfAppointment,
      'jurisdictions': instance.jurisdictions,
      'assignments': instance.assignments,
      'serviceHistory': instance.serviceHistory,
      'education': instance.education,
      'tests': instance.tests,
      'tenantId': instance.tenantId,
      'documents': instance.documents,
      'deactivationDetails': instance.deactivationDetails,
      'reactivationDetails': instance.reactivationDetails,
      'auditDetails': instance.auditDetails,
      'reActivateEmployee': instance.reActivateEmployee,
      'user': instance.user,
      'isActive': instance.isActive,
    };

Assignment _$AssignmentFromJson(Map<String, dynamic> json) => Assignment()
  ..id = json['id'] as String?
  ..position = (json['position'] as num?)?.toInt()
  ..designation = json['designation'] as String?
  ..department = json['department'] as String?
  ..fromDate = (json['fromDate'] as num?)?.toInt()
  ..toDate = json['toDate']
  ..govtOrderNumber = json['govtOrderNumber']
  ..tenantid = json['tenantid'] as String?
  ..reportingTo = json['reportingTo']
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..isHod = json['isHOD'] as bool?
  ..isCurrentAssignment = json['isCurrentAssignment'] as bool?;

Map<String, dynamic> _$AssignmentToJson(Assignment instance) =>
    <String, dynamic>{
      'id': instance.id,
      'position': instance.position,
      'designation': instance.designation,
      'department': instance.department,
      'fromDate': instance.fromDate,
      'toDate': instance.toDate,
      'govtOrderNumber': instance.govtOrderNumber,
      'tenantid': instance.tenantid,
      'reportingTo': instance.reportingTo,
      'auditDetails': instance.auditDetails,
      'isHOD': instance.isHod,
      'isCurrentAssignment': instance.isCurrentAssignment,
    };

AuditDetails _$AuditDetailsFromJson(Map<String, dynamic> json) => AuditDetails()
  ..createdBy = json['createdBy'] as String?
  ..createdDate = (json['createdDate'] as num?)?.toInt()
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..lastModifiedDate = (json['lastModifiedDate'] as num?)?.toInt();

Map<String, dynamic> _$AuditDetailsToJson(AuditDetails instance) =>
    <String, dynamic>{
      'createdBy': instance.createdBy,
      'createdDate': instance.createdDate,
      'lastModifiedBy': instance.lastModifiedBy,
      'lastModifiedDate': instance.lastModifiedDate,
    };

Jurisdiction _$JurisdictionFromJson(Map<String, dynamic> json) => Jurisdiction()
  ..id = json['id'] as String?
  ..hierarchy = json['hierarchy'] as String?
  ..boundary = json['boundary'] as String?
  ..boundaryType = json['boundaryType'] as String?
  ..tenantId = json['tenantId'] as String?
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..isActive = json['isActive'] as bool?;

Map<String, dynamic> _$JurisdictionToJson(Jurisdiction instance) =>
    <String, dynamic>{
      'id': instance.id,
      'hierarchy': instance.hierarchy,
      'boundary': instance.boundary,
      'boundaryType': instance.boundaryType,
      'tenantId': instance.tenantId,
      'auditDetails': instance.auditDetails,
      'isActive': instance.isActive,
    };

User _$UserFromJson(Map<String, dynamic> json) => User()
  ..id = (json['id'] as num?)?.toInt()
  ..uuid = json['uuid'] as String?
  ..userName = json['userName'] as String?
  ..password = json['password']
  ..salutation = json['salutation']
  ..name = json['name'] as String?
  ..gender = json['gender'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..emailId = json['emailId']
  ..altContactNumber = json['altContactNumber']
  ..pan = json['pan']
  ..aadhaarNumber = json['aadhaarNumber']
  ..permanentAddress = json['permanentAddress']
  ..permanentCity = json['permanentCity']
  ..permanentPinCode = json['permanentPinCode']
  ..correspondenceCity = json['correspondenceCity']
  ..correspondencePinCode = json['correspondencePinCode']
  ..correspondenceAddress = json['correspondenceAddress'] as String?
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
  ..fatherOrHusbandName = json['fatherOrHusbandName'] as String?
  ..relationship = json['relationship']
  ..bloodGroup = json['bloodGroup']
  ..identificationMark = json['identificationMark']
  ..photo = json['photo']
  ..createdBy = json['createdBy'] as String?
  ..createdDate = (json['createdDate'] as num?)?.toInt()
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..lastModifiedDate = (json['lastModifiedDate'] as num?)?.toInt()
  ..otpReference = json['otpReference']
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$UserToJson(User instance) => <String, dynamic>{
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
      'relationship': instance.relationship,
      'bloodGroup': instance.bloodGroup,
      'identificationMark': instance.identificationMark,
      'photo': instance.photo,
      'createdBy': instance.createdBy,
      'createdDate': instance.createdDate,
      'lastModifiedBy': instance.lastModifiedBy,
      'lastModifiedDate': instance.lastModifiedDate,
      'otpReference': instance.otpReference,
      'tenantId': instance.tenantId,
    };

Role _$RoleFromJson(Map<String, dynamic> json) => Role()
  ..name = json['name'] as String?
  ..code = json['code'] as String?
  ..description = json['description']
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$RoleToJson(Role instance) => <String, dynamic>{
      'name': instance.name,
      'code': instance.code,
      'description': instance.description,
      'tenantId': instance.tenantId,
    };
