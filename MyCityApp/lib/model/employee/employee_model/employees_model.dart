import 'package:json_annotation/json_annotation.dart';

part 'employees_model.g.dart';

@JsonSerializable()
class EmployeeModel {
  @JsonKey(name: 'Employees')
  List<Employee>? employees;

  EmployeeModel();

  factory EmployeeModel.fromJson(Map<String, dynamic> json) =>
      _$EmployeeModelFromJson(json);

  Map<String, dynamic> toJson() => _$EmployeeModelToJson(this);
}

@JsonSerializable()
class Employee {
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'employeeStatus')
  String? employeeStatus;
  @JsonKey(name: 'employeeType')
  String? employeeType;
  @JsonKey(name: 'dateOfAppointment')
  dynamic dateOfAppointment;
  @JsonKey(name: 'jurisdictions')
  List<Jurisdiction>? jurisdictions;
  @JsonKey(name: 'assignments')
  List<Assignment>? assignments;
  @JsonKey(name: 'serviceHistory')
  List<dynamic>? serviceHistory;
  @JsonKey(name: 'education')
  List<dynamic>? education;
  @JsonKey(name: 'tests')
  List<dynamic>? tests;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'documents')
  List<dynamic>? documents;
  @JsonKey(name: 'deactivationDetails')
  List<dynamic>? deactivationDetails;
  @JsonKey(name: 'reactivationDetails')
  List<dynamic>? reactivationDetails;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'reActivateEmployee')
  bool? reActivateEmployee;
  @JsonKey(name: 'user')
  User? user;
  @JsonKey(name: 'isActive')
  bool? isActive;

  Employee();

  factory Employee.fromJson(Map<String, dynamic> json) =>
      _$EmployeeFromJson(json);

  Map<String, dynamic> toJson() => _$EmployeeToJson(this);
}

@JsonSerializable()
class Assignment {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'position')
  int? position;
  @JsonKey(name: 'designation')
  String? designation;
  @JsonKey(name: 'department')
  String? department;
  @JsonKey(name: 'fromDate')
  int? fromDate;
  @JsonKey(name: 'toDate')
  dynamic toDate;
  @JsonKey(name: 'govtOrderNumber')
  dynamic govtOrderNumber;
  @JsonKey(name: 'tenantid')
  String? tenantid;
  @JsonKey(name: 'reportingTo')
  dynamic reportingTo;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'isHOD')
  bool? isHod;
  @JsonKey(name: 'isCurrentAssignment')
  bool? isCurrentAssignment;

  Assignment();

  factory Assignment.fromJson(Map<String, dynamic> json) =>
      _$AssignmentFromJson(json);

  Map<String, dynamic> toJson() => _$AssignmentToJson(this);
}

@JsonSerializable()
class AuditDetails {
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'createdDate')
  int? createdDate;
  @JsonKey(name: 'lastModifiedBy')
  String? lastModifiedBy;
  @JsonKey(name: 'lastModifiedDate')
  int? lastModifiedDate;

  AuditDetails();

  factory AuditDetails.fromJson(Map<String, dynamic> json) =>
      _$AuditDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AuditDetailsToJson(this);
}

@JsonSerializable()
class Jurisdiction {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'hierarchy')
  String? hierarchy;
  @JsonKey(name: 'boundary')
  String? boundary;
  @JsonKey(name: 'boundaryType')
  String? boundaryType;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'isActive')
  bool? isActive;

  Jurisdiction();

  factory Jurisdiction.fromJson(Map<String, dynamic> json) =>
      _$JurisdictionFromJson(json);

  Map<String, dynamic> toJson() => _$JurisdictionToJson(this);
}

@JsonSerializable()
class User {
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'userName')
  String? userName;
  @JsonKey(name: 'password')
  dynamic password;
  @JsonKey(name: 'salutation')
  dynamic salutation;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'gender')
  String? gender;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'emailId')
  dynamic emailId;
  @JsonKey(name: 'altContactNumber')
  dynamic altContactNumber;
  @JsonKey(name: 'pan')
  dynamic pan;
  @JsonKey(name: 'aadhaarNumber')
  dynamic aadhaarNumber;
  @JsonKey(name: 'permanentAddress')
  dynamic permanentAddress;
  @JsonKey(name: 'permanentCity')
  dynamic permanentCity;
  @JsonKey(name: 'permanentPinCode')
  dynamic permanentPinCode;
  @JsonKey(name: 'correspondenceCity')
  dynamic correspondenceCity;
  @JsonKey(name: 'correspondencePinCode')
  dynamic correspondencePinCode;
  @JsonKey(name: 'correspondenceAddress')
  String? correspondenceAddress;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'dob')
  int? dob;
  @JsonKey(name: 'pwdExpiryDate')
  int? pwdExpiryDate;
  @JsonKey(name: 'locale')
  dynamic locale;
  @JsonKey(name: 'type')
  String? type;
  @JsonKey(name: 'signature')
  dynamic signature;
  @JsonKey(name: 'accountLocked')
  bool? accountLocked;
  @JsonKey(name: 'roles')
  List<Role>? roles;
  @JsonKey(name: 'fatherOrHusbandName')
  String? fatherOrHusbandName;
  @JsonKey(name: 'relationship')
  dynamic relationship;
  @JsonKey(name: 'bloodGroup')
  dynamic bloodGroup;
  @JsonKey(name: 'identificationMark')
  dynamic identificationMark;
  @JsonKey(name: 'photo')
  dynamic photo;
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'createdDate')
  int? createdDate;
  @JsonKey(name: 'lastModifiedBy')
  String? lastModifiedBy;
  @JsonKey(name: 'lastModifiedDate')
  int? lastModifiedDate;
  @JsonKey(name: 'otpReference')
  dynamic otpReference;
  @JsonKey(name: 'tenantId')
  String? tenantId;

  User();

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);

  Map<String, dynamic> toJson() => _$UserToJson(this);
}

@JsonSerializable()
class Role {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'description')
  dynamic description;
  @JsonKey(name: 'tenantId')
  String? tenantId;

  Role();

  factory Role.fromJson(Map<String, dynamic> json) => _$RoleFromJson(json);

  Map<String, dynamic> toJson() => _$RoleToJson(this);
}
