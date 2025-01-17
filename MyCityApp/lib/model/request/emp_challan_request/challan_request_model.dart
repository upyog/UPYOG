class ChallanRequestModel {
  CitizenRequestModel citizen;
  String businessService;
  String consumerType;
  int taxPeriodFrom;
  int taxPeriodTo;
  String tenantId;
  AddressRequestModel address;
  List<AmountRequestModel> amount;

  ChallanRequestModel({
    required this.citizen,
    required this.businessService,
    required this.consumerType,
    required this.taxPeriodFrom,
    required this.taxPeriodTo,
    required this.tenantId,
    required this.address,
    required this.amount,
  });

  Map<String, dynamic> toJson() {
    return {
      "citizen": citizen.toJson(),
      "businessService": businessService,
      "consumerType": consumerType,
      "taxPeriodFrom": taxPeriodFrom,
      "taxPeriodTo": taxPeriodTo,
      "tenantId": tenantId,
      "address": address.toJson(),
      "amount": amount.map((e) => e.toJson()).toList(),
    };
  }
}

class CitizenRequestModel {
  String name;
  String mobileNumber;
  String emailId;

  CitizenRequestModel({
    required this.name,
    required this.mobileNumber,
    required this.emailId,
  });

  Map<String, dynamic> toJson() {
    return {
      "name": name,
      "mobileNumber": mobileNumber,
      "emailId": emailId,
    };
  }
}

class AddressRequestModel {
  String buildingName;
  String doorNo;
  String street;
  LocalityRequestModel locality;
  String pincode;

  AddressRequestModel({
    required this.buildingName,
    required this.doorNo,
    required this.street,
    required this.locality,
    required this.pincode,
  });

  Map<String, dynamic> toJson() {
    return {
      "buildingName": buildingName,
      "doorNo": doorNo,
      "street": street,
      "locality": locality.toJson(),
      "pincode": pincode,
    };
  }
}

class LocalityRequestModel {
  String code;

  LocalityRequestModel({
    required this.code,
  });

  Map<String, dynamic> toJson() {
    return {
      "code": code,
    };
  }
}

class AmountRequestModel {
  String taxHeadCode;
  int amount;

  AmountRequestModel({
    required this.taxHeadCode,
    required this.amount,
  });

  Map<String, dynamic> toJson() {
    return {
      "taxHeadCode": taxHeadCode,
      "amount": amount,
    };
  }
}
