
// ignore_for_file: prefer_interpolation_to_compose_strings

import 'dart:convert';

import 'package:crypto/crypto.dart';
import 'package:intl/intl.dart';

// Warning: Do not change anything in this file

getRequestJsonData(Map data) {
  String datetime = DateFormat("yyyy-MM-dd HH:mm:ss").format(DateTime.now());
  String jsonPayLoadForAIPAY =
      '${'${'{"payInstrument": { "headDetails": { "version": "OTSv1.1", "api": "AUTH", "platform": "FLASH" }, "merchDetails": { "merchId": "' +
          data['login'] +
          '", "userId": "", "password": "' +
          data['password'] +
          '", "merchTxnDate": "' +
          datetime +
          '", "merchTxnId": "' +
          data['txnid'] +
          '" }, "payDetails": { "amount": "' +
          data['amount'] +
          '", "product": "' +
          data['prodid'] +
          '", "custAccNo": "' +
          data['custacc'] +
          '", "txnCurrency": "' +
          data['txncurr'] +
          '" }, "custDetails": { "custEmail": "' +
          data['email'] +
          '", "custMobile": "' +
          data['mobile'] +
          '" }, "extras": { "udf1": "' +
          data['udf1'] +
          '", "udf2": "' +
          data['udf2'] +
          '", "udf3": "' +
          data['udf3'] +
          '", "udf4": "' +
          data['udf4']}", "udf5": "' +
          data['udf5']}"}}}';
  return jsonPayLoadForAIPAY;
}

createSignature(Map data) {
  String signatureString = data['login'] +
      data['password'] +
      data['txnid'] +
      'RD' +
      data['amount'] +
      data['txncurr'] +
      '1';
  var bytes = utf8.encode(signatureString);
  var key = utf8.encode(data['requestHashKey']);
  var hmacSha512 = Hmac(sha512, key);
  var digest = hmacSha512.convert(bytes);
  return (digest.toString());
}

validateSignature(Map data, resHashKey) {
  String signatureString = data["payInstrument"]["merchDetails"]["merchId"]
          .toString() +
      data["payInstrument"]["payDetails"]["atomTxnId"].toString() +
      data['payInstrument']['merchDetails']['merchTxnId'].toString() +
      data['payInstrument']['payDetails']['totalAmount'].toStringAsFixed(2) +
      data['payInstrument']['responseDetails']['statusCode'].toString() +
      data['payInstrument']['payModeSpecificData']['subChannel'][0].toString() +
      data['payInstrument']['payModeSpecificData']['bankDetails']['bankTxnId']
          .toString();
  var bytes = utf8.encode(signatureString);
  var key = utf8.encode(resHashKey);
  var hmacSha512 = Hmac(sha512, key);
  var digest = hmacSha512.convert(bytes);
  var genSig = digest.toString();
  if (data['payInstrument']['payDetails']['signature'] == genSig) {
    return true;
  } else {
    return false;
  }
}
