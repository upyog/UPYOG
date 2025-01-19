// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:convert';

class PaymentRequestModel {
  String? login;
  String? password;
  String? prodid;
  String? requestHashKey;
  String? responseHashKey;
  String? requestEncryptionKey;
  String? responseDecryptionKey;
  String? txnid;
  String? clientcode;
  String? txncurr;
  String? mccCode;
  String? merchType;
  String? amount;

  String? mode;

  String? custFirstName;
  String? custLastName;
  String? mobile;
  String? email;
  String? address;
  String? custacc;
  String? udf1;
  String? udf2;
  String? udf3;
  String? udf4;
  String? udf5;
  PaymentRequestModel({
    this.login,
    this.password,
    this.prodid,
    this.requestHashKey,
    this.responseHashKey,
    this.requestEncryptionKey,
    this.responseDecryptionKey,
    this.txnid,
    this.clientcode,
    this.txncurr,
    this.mccCode,
    this.merchType,
    this.amount,
    this.mode,
    this.custFirstName,
    this.custLastName,
    this.mobile,
    this.email,
    this.address,
    this.custacc,
    this.udf1,
    this.udf2,
    this.udf3,
    this.udf4,
    this.udf5,
  });

  Map<String, dynamic> toMap() => <String, dynamic>{
        'login': login,
        'password': password,
        'prodid': prodid,
        'requestHashKey': requestHashKey,
        'responseHashKey': responseHashKey,
        'requestEncryptionKey': requestEncryptionKey,
        'responseDecryptionKey': responseDecryptionKey,
        'txnid': txnid,
        'clientcode': clientcode,
        'txncurr': txncurr,
        'mccCode': mccCode,
        'merchType': merchType,
        'amount': amount,
        'mode': mode,
        'custFirstName': custFirstName,
        'custLastName': custLastName,
        'mobile': mobile,
        'email': email,
        'address': address,
        'custacc': custacc,
        'udf1': udf1 ?? 'udf1',
        'udf2': udf2 ?? 'udf2',
        'udf3': udf3 ?? 'udf3',
        'udf4': udf4 ?? 'udf4',
        'udf5': udf5 ?? 'udf5',
      };

  factory PaymentRequestModel.fromMap(Map<String, dynamic> map) {
    return PaymentRequestModel(
      login: map['login'] as String,
      password: map['password'] as String,
      prodid: map['prodid'] as String,
      requestHashKey: map['requestHashKey'] as String,
      responseHashKey: map['responseHashKey'] as String,
      requestEncryptionKey: map['requestEncryptionKey'] as String,
      responseDecryptionKey: map['responseDecryptionKey'] as String,
      txnid: map['txnid'] as String,
      clientcode: map['clientcode'] as String,
      txncurr: map['txncurr'] as String,
      mccCode: map['mccCode'] as String,
      merchType: map['merchType'] as String,
      amount: map['amount'] as String,
      mode: map['mode'] as String,
      custFirstName:
          map['custFirstName'] != null ? map['custFirstName'] as String : null,
      custLastName:
          map['custLastName'] != null ? map['custLastName'] as String : null,
      mobile: map['mobile'] != null ? map['mobile'] as String : null,
      email: map['email'] != null ? map['email'] as String : null,
      address: map['address'] != null ? map['address'] as String : null,
      custacc: map['custacc'] != null ? map['custacc'] as String : null,
      udf1: map['udf1'] != null ? map['udf1'] as String : null,
      udf2: map['udf2'] != null ? map['udf2'] as String : null,
      udf3: map['udf3'] != null ? map['udf3'] as String : null,
      udf4: map['udf4'] != null ? map['udf4'] as String : null,
      udf5: map['udf5'] != null ? map['udf5'] as String : null,
    );
  }

  String toJson() => json.encode(toMap());

  factory PaymentRequestModel.fromJson(String source) =>
      PaymentRequestModel.fromMap(json.decode(source) as Map<String, dynamic>);

  PaymentRequestModel copyWith({
    String? login,
    String? password,
    String? prodid,
    String? requestHashKey,
    String? responseHashKey,
    String? requestEncryptionKey,
    String? responseDecryptionKey,
    String? txnid,
    String? clientcode,
    String? txncurr,
    String? mccCode,
    String? merchType,
    String? amount,
    String? mode,
    String? custFirstName,
    String? custLastName,
    String? mobile,
    String? email,
    String? address,
    String? custacc,
    String? udf1,
    String? udf2,
    String? udf3,
    String? udf4,
    String? udf5,
  }) {
    return PaymentRequestModel(
      login: login ?? this.login,
      password: password ?? this.password,
      prodid: prodid ?? this.prodid,
      requestHashKey: requestHashKey ?? this.requestHashKey,
      responseHashKey: responseHashKey ?? this.responseHashKey,
      requestEncryptionKey: requestEncryptionKey ?? this.requestEncryptionKey,
      responseDecryptionKey:
          responseDecryptionKey ?? this.responseDecryptionKey,
      txnid: txnid ?? this.txnid,
      clientcode: clientcode ?? this.clientcode,
      txncurr: txncurr ?? this.txncurr,
      mccCode: mccCode ?? this.mccCode,
      merchType: merchType ?? this.merchType,
      amount: amount ?? this.amount,
      mode: mode ?? this.mode,
      custFirstName: custFirstName ?? this.custFirstName,
      custLastName: custLastName ?? this.custLastName,
      mobile: mobile ?? this.mobile,
      email: email ?? this.email,
      address: address ?? this.address,
      custacc: custacc ?? this.custacc,
      udf1: udf1 ?? this.udf1,
      udf2: udf2 ?? this.udf2,
      udf3: udf3 ?? this.udf3,
      udf4: udf4 ?? this.udf4,
      udf5: udf5 ?? this.udf5,
    );
  }

  @override
  String toString() {
    return 'PaymentRequestModel(login: $login, password: $password, prodid: $prodid, requestHashKey: $requestHashKey, responseHashKey: $responseHashKey, requestEncryptionKey: $requestEncryptionKey, responseDecryptionKey: $responseDecryptionKey, txnid: $txnid, clientcode: $clientcode, txncurr: $txncurr, mccCode: $mccCode, merchType: $merchType, amount: $amount, mode: $mode, custFirstName: $custFirstName, custLastName: $custLastName, mobile: $mobile, email: $email, address: $address, custacc: $custacc, udf1: $udf1, udf2: $udf2, udf3: $udf3, udf4: $udf4, udf5: $udf5)';
  }

  @override
  bool operator ==(covariant PaymentRequestModel other) {
    if (identical(this, other)) return true;

    return other.login == login &&
        other.password == password &&
        other.prodid == prodid &&
        other.requestHashKey == requestHashKey &&
        other.responseHashKey == responseHashKey &&
        other.requestEncryptionKey == requestEncryptionKey &&
        other.responseDecryptionKey == responseDecryptionKey &&
        other.txnid == txnid &&
        other.clientcode == clientcode &&
        other.txncurr == txncurr &&
        other.mccCode == mccCode &&
        other.merchType == merchType &&
        other.amount == amount &&
        other.mode == mode &&
        other.custFirstName == custFirstName &&
        other.custLastName == custLastName &&
        other.mobile == mobile &&
        other.email == email &&
        other.address == address &&
        other.custacc == custacc &&
        other.udf1 == udf1 &&
        other.udf2 == udf2 &&
        other.udf3 == udf3 &&
        other.udf4 == udf4 &&
        other.udf5 == udf5;
  }

  @override
  int get hashCode {
    return login.hashCode ^
        password.hashCode ^
        prodid.hashCode ^
        requestHashKey.hashCode ^
        responseHashKey.hashCode ^
        requestEncryptionKey.hashCode ^
        responseDecryptionKey.hashCode ^
        txnid.hashCode ^
        clientcode.hashCode ^
        txncurr.hashCode ^
        mccCode.hashCode ^
        merchType.hashCode ^
        amount.hashCode ^
        mode.hashCode ^
        custFirstName.hashCode ^
        custLastName.hashCode ^
        mobile.hashCode ^
        email.hashCode ^
        address.hashCode ^
        custacc.hashCode ^
        udf1.hashCode ^
        udf2.hashCode ^
        udf3.hashCode ^
        udf4.hashCode ^
        udf5.hashCode;
  }
}
