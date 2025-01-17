class UserRequestModel {
  int? id;
  String? uuid;
  String? userName;
  String? name;
  String? mobileNumber;
  String? emailId;
  dynamic locale;
  String? type;
  List<Role>? roles;
  bool? active;
  String? tenantId;
  dynamic permanentCity;
  String? photo;
  String? dob;
  String? gender;

  UserRequestModel({
    this.id,
    this.uuid,
    this.userName,
    this.name,
    this.mobileNumber,
    this.emailId,
    this.locale,
    this.type,
    this.roles,
    this.active,
    this.tenantId,
    this.permanentCity,
    this.photo,
    this.dob,
    this.gender,
  });

  Map<String, dynamic> toJson() => {
        "id": id,
        "uuid": uuid,
        "userName": userName,
        "name": name,
        "mobileNumber": mobileNumber,
        "emailId": emailId,
        "locale": locale,
        "type": type,
        "roles": roles == null
            ? []
            : List<dynamic>.from(roles!.map((x) => x.toJson())),
        "active": active,
        "tenantId": tenantId,
        "permanentCity": permanentCity,
        "photo": photo,
        "dob": dob,
        "gender": gender,
      };

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'id': id,
      'uuid': uuid,
      'userName': userName,
      'name': name,
      'mobileNumber': mobileNumber,
      'emailId': emailId,
      'locale': locale,
      'type': type,
      'roles': roles?.map((x) => x.toMap()).toList(),
      'active': active,
      'tenantId': tenantId,
      'permanentCity': permanentCity,
    };
  }

  @override
  String toString() {
    return 'UserRequest(id: $id, uuid: $uuid, userName: $userName, name: $name, mobileNumber: $mobileNumber, emailId: $emailId, locale: $locale, type: $type, roles: $roles, active: $active, tenantId: $tenantId, permanentCity: $permanentCity)';
  }
}

class Role {
  final String? name;
  final String? code;
  final String? tenantId;

  Role({
    this.name,
    this.code,
    this.tenantId,
  });


  Map<String, dynamic> toJson() => {
        "name": name,
        "code": code,
        "tenantId": tenantId,
      };

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'name': name,
      'code': code,
      'tenantId': tenantId,
    };
  }

  factory Role.fromMap(Map<String, dynamic> map) {
    return Role(
      name: map['name'] != null ? map['name'] as String : null,
      code: map['code'] != null ? map['code'] as String : null,
      tenantId: map['tenantId'] != null ? map['tenantId'] as String : null,
    );
  }

  @override
  String toString() => 'Role(name: $name, code: $code, tenantId: $tenantId)';
}