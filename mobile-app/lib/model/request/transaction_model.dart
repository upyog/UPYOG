// To parse this JSON data, do
//
//     final transaction = transactionFromJson(jsonString);
class TransactionRequest {
  String? tenantId;
  String? txnAmount;
  String? module;
  String? billId;
  String? consumerCode;
  String? productInfo;
  String? gateway;
  List<TaxAndPayment>? taxAndPayments;
  User? user;
  String? callbackUrl;
  AdditionalDetails? additionalDetails;

  TransactionRequest({
    this.tenantId,
    this.txnAmount,
    this.module,
    this.billId,
    this.consumerCode,
    this.productInfo,
    this.gateway,
    this.taxAndPayments,
    this.user,
    this.callbackUrl,
    this.additionalDetails,
  });

  factory TransactionRequest.fromJson(Map<String, dynamic> json) =>
      TransactionRequest(
        tenantId: json["tenantId"],
        txnAmount: json["txnAmount"],
        module: json["module"],
        billId: json["billId"],
        consumerCode: json["consumerCode"],
        productInfo: json["productInfo"],
        gateway: json["gateway"],
        taxAndPayments: json["taxAndPayments"] == null
            ? []
            : List<TaxAndPayment>.from(
                json["taxAndPayments"]!.map((x) => TaxAndPayment.fromJson(x)),
              ),
        user: json["user"] == null ? null : User.fromJson(json["user"]),
        callbackUrl: json["callbackUrl"],
        additionalDetails: json["additionalDetails"] == null
            ? null
            : AdditionalDetails.fromJson(json["additionalDetails"]),
      );

  Map<String, dynamic> toJson() => {
        "tenantId": tenantId,
        "txnAmount": txnAmount,
        "module": module,
        "billId": billId,
        "consumerCode": consumerCode,
        "productInfo": productInfo ?? "Common Payment",
        "gateway": gateway ?? 'NTTDATA',
        "taxAndPayments": taxAndPayments == null
            ? []
            : List<dynamic>.from(taxAndPayments!.map((x) => x.toJson())),
        "user": user?.toJson(),
        "callbackUrl": callbackUrl,
        "additionalDetails": additionalDetails?.toJson(),
      };
}

class AdditionalDetails {
  bool? isWhatsapp;

  AdditionalDetails({
    this.isWhatsapp,
  });

  factory AdditionalDetails.fromJson(Map<String, dynamic> json) =>
      AdditionalDetails(
        isWhatsapp: json["isWhatsapp"],
      );

  Map<String, dynamic> toJson() => {
        "isWhatsapp": isWhatsapp ?? false,
      };
}

class TaxAndPayment {
  String? billId;
  String? amountPaid;

  TaxAndPayment({
    this.billId,
    this.amountPaid,
  });

  factory TaxAndPayment.fromJson(Map<String, dynamic> json) => TaxAndPayment(
        billId: json["billId"],
        amountPaid: json["amountPaid"],
      );

  Map<String, dynamic> toJson() => {
        "billId": billId,
        "amountPaid": amountPaid,
      };
}

class User {
  String? name;
  String? mobileNumber;
  String? tenantId;
  String? emailId;

  User({
    this.name,
    this.mobileNumber,
    this.tenantId,
    this.emailId,
  });

  factory User.fromJson(Map<String, dynamic> json) => User(
        name: json["name"],
        mobileNumber: json["mobileNumber"],
        tenantId: json["tenantId"],
        emailId: json["emailId"],
      );

  Map<String, dynamic> toJson() => {
        "name": name,
        "mobileNumber": mobileNumber,
        "tenantId": tenantId,
        "emailId": emailId,
      };
}
