// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:async';
import 'dart:convert';
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:http/http.dart' as http;
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/env/ntt_payment_config.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart' as b;
import 'package:mobile_app/model/citizen/bill/demands.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/model/citizen/trade_license/trade_license.dart';
import 'package:mobile_app/model/citizen/transaction/transaction.dart';
import 'package:mobile_app/model/request/payment_request_model.dart';
import 'package:mobile_app/model/request/transaction_model.dart' as reqTran;
import 'package:mobile_app/repository/payment_repository.dart';
import 'package:mobile_app/screens/citizen/ntt_data_aipay_payment/atom_pay_helper.dart';
import 'package:mobile_app/screens/citizen/ntt_data_aipay_payment/web_view_container.dart';
import 'package:mobile_app/services/secure_storage_service.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/ntt_payment_enum.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/payments/payment_failure_widget.dart';
import 'package:mobile_app/widgets/payments/payment_success_widget.dart';

class PaymentController extends GetxController {
  final _editProfileController = Get.find<EditProfileController>();

  b.BillInfo? billInfo;
  b.Bill? bill;
  Transaction? transaction;
  PaymentModel? paymentModel;

  // Selected Response
  License? tlLicenseSelected;
  Transaction? transactionResponse;
  PaymentRequestModel? nttModelResponse;
  DemandsModel? demandsModel;

  RxBool isLoading = false.obs,
      isPaymentConditionEnabled1 = false.obs,
      isPaymentConditionEnabled2 = false.obs,
      isPaymentButtonEnabled = true.obs;

  String text1 = 'Receipt';
  String text2 = 'Receipt';

  RxInt length = 0.obs;

  void hidePaymentLoader() {
    isLoading.value = false;
    isPaymentConditionEnabled1.value = false;
    isPaymentConditionEnabled2.value = false;
    isPaymentButtonEnabled.value = true;
  }

  Future<List<Payment>?> getPaymentDetails({
    required String token,
    required String consumerCodes,
    required String tenantId,
    required String businessService,
    bool isEmployee = false,
  }) async {
    try {
      Map<String, dynamic> query = {};
      if (isEmployee) {
        query = {
          'tenantId': tenantId,
          'consumerCodes': consumerCodes,
          'isEmployee': isEmployee.toString(),
        };
      } else {
        query = {
          'tenantId': tenantId,
          'consumerCodes': consumerCodes,
        };
      }

      final paymentRes = await PaymentRepository.verifyPayment(
        token: token,
        businessService: businessService,
        query: query,
      );
      paymentModel = PaymentModel.fromJson(paymentRes);
    } catch (e, s) {
      dPrint('WS/SW Payment Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return paymentModel?.payments;
  }

  Future<b.BillInfo?> searchBillById({
    required String tenantId,
    required String token,
    required String service,
    required String consumerCode,
  }) async {
    try {
      final billRes = await PaymentRepository.searchBill(
        token: token,
        query: {
          'tenantId': tenantId,
          'Service': service,
          'consumerCode': consumerCode,
        },
      );
      billInfo = b.BillInfo.fromJson(billRes);
      bill = billInfo?.bill?.first;
      length.value = billInfo?.bill?.length ?? 0;
    } catch (e, s) {
      dPrint('SearchBillById Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return billInfo;
  }

  // Get demands bill
  Future<DemandsModel?> searchDemandById({
    required String tenantId,
    required String token,
    required String businessService,
    required String consumerCode,
  }) async {
    try {
      final demandRes = await PaymentRepository.searchBillDemand(
        token: token,
        query: {
          'tenantId': tenantId,
          'businessService': businessService,
          'consumerCode': consumerCode,
        },
      );
      demandsModel = demandRes;
      length.value = demandsModel?.demands?.length ?? 0;
    } catch (e, s) {
      dPrint('searchDemandById Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return demandsModel;
  }

  //Get bill information by consumer code
  Future<b.BillInfo?> getPayment({
    required String token,
    required String tenantId,
    required String consumerCode,
    required BusinessService businessService,
  }) async {
    try {
      final query = {
        'tenantId': tenantId,
        'consumerCode': isNotNullOrEmpty(consumerCode) ? consumerCode : "",
        'businessService': businessService.name,
      };
      final data = await PaymentRepository.fetchBill(
        token: token,
        query: query,
      );
      billInfo = b.BillInfo.fromJson(data);
      bill = billInfo?.bill?.firstOrNull;
    } catch (e, s) {
      dPrint('GetPaymentError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return billInfo;
  }

  ///Create payment
  Future<(Transaction?, bool hasError)> createPayment({
    required String token,
    required String cityTenantId, //pg.citya
    required String consumerCode,
    required String businessService,
    required String billId,
    required double totalAmount,
  }) async {
    bool isError = false;
    try {
      isLoading.value = true;
      isPaymentConditionEnabled1.value = true;
      isPaymentButtonEnabled.value = false;
      // final billId = bill!.id!;
      // final totalAmount = bill!.totalAmount!.toInt();

      var additionalDetails = reqTran.AdditionalDetails()..isWhatsapp = false;

      var taxPayments = reqTran.TaxAndPayment()
        ..billId = billId
        ..amountPaid = '${totalAmount.toInt()}';

      var user = reqTran.User()
        ..name = _editProfileController.user.name
        ..emailId = _editProfileController.user.emailId
        ..mobileNumber = _editProfileController.user.mobileNumber
        ..tenantId = cityTenantId;

      final String module =
          await storage.getString(SecureStorageConstants.MODULES) ?? '';

      // Get call back url
      final callbackUrl = getPaymentCallbackUrl(
        tenantId: cityTenantId,
        businessService: businessService,
        consumerCode: consumerCode,
        module: module,
      );
      reqTran.TransactionRequest transactionRequest =
          reqTran.TransactionRequest()
            ..additionalDetails = additionalDetails
            ..tenantId = cityTenantId
            ..txnAmount = '$totalAmount'
            ..module = businessService
            ..billId = billId
            ..consumerCode = consumerCode
            ..taxAndPayments = [taxPayments]
            ..user = user
            ..callbackUrl = callbackUrl;

      final body = {
        'Transaction': transactionRequest.toJson(),
      };

      final query = {
        'tenantId': cityTenantId,
      };
      final response = await PaymentRepository.createBill(
        token: token,
        body: body,
        query: query,
      );
      dPrint('CreatePayment Data: ${response['Transaction']}');
      transaction = Transaction.fromJson(response['Transaction']);
      //isLoading.value = false;
    } catch (e, s) {
      isLoading.value = false;
      isPaymentConditionEnabled1.value = false;
      isPaymentButtonEnabled.value = true;
      dPrint('CreatePaymentError: $e');
      isError = await ErrorHandler.allExceptionsHandler(e, s);
    }
    return (transaction, isError);
  }

  /// Update payment - Complete payment intent
  Future<Transaction?> updatePayment({
    required String token,
    required String transactionId,
  }) async {
    try {
      final updateRes = await PaymentRepository.updatePayment(
        token: token,
        query: {'transactionId': transactionId},
      );
      dPrint('UpdatePayment Data: $updateRes');
      return Transaction.fromJson(updateRes['Transaction'][0]);
    } catch (e, s) {
      dPrint('UpdatePaymentError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return transaction;
  }

  /// Verify payment - Check payment completion
  Future<Payment?> verifyPayment({
    required String token,
    required String businessService,
    String? tenantId,
    String? consumerCodes,
  }) async {
    Payment? paymentRes;
    try {
      final response = await PaymentRepository.verifyPayment(
        token: token,
        query: {
          'tenantId': tenantId ?? transaction!.tenantId,
          'consumerCodes': consumerCodes ?? transaction!.consumerCode,
        },
        businessService: businessService,
      );
      // dPrint('VerifyPayment Data: ${response['Payments'][0]}');
      final verifyingResponse = response['Payments'];
      if (isNotNullOrEmpty(verifyingResponse)) {
        paymentRes = Payment.fromJson(verifyingResponse[0]);
      }
    } catch (e, s) {
      dPrint('VerifyPaymentError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return paymentRes;
  }

  /// Verify payment list
  Future<PaymentModel?> verifyPaymentPT({
    required String token,
    required String businessService,
    String? tenantId,
    String? consumerCodes,
  }) async {
    PaymentModel? paymentRes;
    try {
      final response = await PaymentRepository.verifyPayment(
        token: token,
        query: {
          'tenantId': tenantId ?? transaction!.tenantId,
          'consumerCodes': consumerCodes ?? transaction!.consumerCode,
        },
        businessService: businessService,
      );

      paymentRes = PaymentModel.fromJson(response);
    } catch (e, s) {
      dPrint('VerifyPayment_PT_Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return paymentRes;
  }

  /// Get my payment
  Future<List<Payment>?> getMyPayment({
    required String token,
    required String businessService,
    String? tenantId,
    String? consumerCodes,
  }) async {
    List<Payment> paymentRes = [];
    try {
      final response = await PaymentRepository.verifyPayment(
        token: token,
        query: {
          'tenantId': tenantId ?? transaction!.tenantId,
          'consumerCodes': consumerCodes ?? transaction!.consumerCode,
        },
        businessService: businessService,
      );
      // dPrint('VerifyPayment Data: ${response['Payments'][0]}');
      final getPayments = response['Payments'];
      if (getPayments != []) {
        for (var payment in getPayments) {
          final data = Payment.fromJson(payment);
          paymentRes.add(data);
        }
      }
    } catch (e, s) {
      paymentRes = [];
      dPrint('GetMyPaymentsError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return paymentRes;
  }

// ------------------------------------------------------ //
// ------------------- NTT Payment ---------------------- //
// ------------------------------------------------------ //

  void initNdpsPayment(
    // BuildContext context,
    String responseHashKey,
    String responseDecryptionKey, {
    required PaymentRequestModel nttRequest,
    required Map<String, dynamic> data,
  }) {
    _getEncryptedPayUrl(
      responseHashKey,
      responseDecryptionKey,
      payments: nttRequest,
      data: data,
    );
  }

  _getEncryptedPayUrl(
    responseHashKey,
    responseDecryptionKey, {
    required PaymentRequestModel payments,
    required Map<String, dynamic> data,
  }) async {
    String reqJsonData = _getJsonPayloadData(payments);
    dPrint(reqJsonData);
    const platform = MethodChannel('flutter.dev/NDPSAESLibrary');
    try {
      isLoading.value = true;
      final String result = await platform.invokeMethod('NDPSAESInit', {
        'AES_Method': 'encrypt',
        'text': reqJsonData,
        'encKey': payments.requestEncryptionKey,
      });
      String authEncryptedString = result.toString();
      _getAtomTokenId(authEncryptedString, payments, data: data);
    } on PlatformException catch (e) {
      dPrint("Failed to get encryption string: '${e.message}'.");
    }
  }

  _getAtomTokenId(
    authEncryptedString,
    PaymentRequestModel ntt, {
    required Map<String, dynamic> data,
  }) async {
    var request = http.Request(
      'POST',
      Uri.parse(nttAuthBaseUrl),
    );
    request.bodyFields = {
      'encData': authEncryptedString,
      'merchId': ntt.login!,
    };

    http.StreamedResponse response = await request.send();
    if (response.statusCode == 200) {
      var authApiResponse = await response.stream.bytesToString();
      final split = authApiResponse.trim().split('&');
      final Map<int, String> values = {
        for (int i = 0; i < split.length; i++) i: split[i],
      };
      final splitTwo = values[1]!.split('=');
      if (splitTwo[0] == 'encData') {
        const platform = MethodChannel('flutter.dev/NDPSAESLibrary');
        try {
          isLoading.value = true;
          final String result = await platform.invokeMethod('NDPSAESInit', {
            'AES_Method': 'decrypt',
            'text': splitTwo[1].toString(),
            'encKey': ntt.responseDecryptionKey,
          });
          dPrint(result.toString()); // to read full response
          var respJsonStr = result.toString();
          Map<String, dynamic> jsonInput = jsonDecode(respJsonStr);
          if (jsonInput["responseDetails"]["txnStatusCode"] == 'OTS0000') {
            final atomTokenId = jsonInput["atomTokenId"].toString();
            dPrint("atomTokenId: $atomTokenId");
            final String payDetails =
                '{"atomTokenId" : "$atomTokenId","merchId": "${ntt.login}","emailId": "${ntt.email}","mobileNumber":"${ntt.mobile}", "returnUrl":"$paymentReturnUrl"}';

            Get.to(
              () => WebViewContainer(
                ntt.mode,
                payDetails,
                ntt.responseHashKey,
                ntt.responseDecryptionKey,
                data: data,
              ),
            );
          } else {
            dPrint("Problem in auth API response");
          }
        } on PlatformException catch (e) {
          dPrint("Failed to decrypt: '${e.message}'.");
        }
      }
    }
  }

  _getJsonPayloadData(PaymentRequestModel payments) {
    var payDetails = payments.toMap();
    String jsonPayLoadData = getRequestJsonData(payDetails);
    return jsonPayLoadData;
  }

  // Check NTT payment status
  Future<void> checkTransactionPayment({
    required NttTransactionResult nttTransactionResult,
    dynamic response,
    dynamic transactionResult,
    required String token,
    required Map<String, dynamic> ntData,
  }) async {
    isLoading.value = false;
    if (nttTransactionResult == NttTransactionResult.SUCCESS) {
      isPaymentConditionEnabled1.value = false;
      isPaymentConditionEnabled2.value = true;
      isPaymentButtonEnabled.value = false;
      final updateTransactionResponse = await updatePayment(
        token: token,
        transactionId: nttModelResponse!.txnid!,
      );

      log('----------------updateTransactionResponse--------------------');
      dPrint('${updateTransactionResponse?.toJson()}');

      if (updateTransactionResponse != null) {
        transaction = updateTransactionResponse;

        // Verify payment update
        final paymentRes = await verifyPayment(
          token: token,
          businessService: ntData['businessService'],
        );

        if (paymentRes != null) {
          if (paymentRes.paymentStatus == PaymentStatus.DEPOSITED.name) {
            final String module =
                await storage.getString(SecureStorageConstants.MODULES) ?? '';

            snackBar('Success', 'Payment complete', Colors.green);
            isPaymentConditionEnabled1.value = false;
            isPaymentConditionEnabled2.value = false;
            isPaymentButtonEnabled.value = true;

            Get.off(
              () => PaymentSuccessWidget(
                payment: paymentRes,
                text2: text2,
                text1: text1,
                module: module,
              ),
            );
          }
        } else {
          snackBar(
            'Failed',
            'Transaction failed!',
            Colors.red,
          );

          Get.off(
            () => PaymentFailureWidget(txnId: transactionResponse?.txnId),
          );
        }

        dPrint(
          'Payment Verified: ${paymentRes?.paymentStatus}',
          enableLog: true,
        );
      } else {
        snackBar(
          nttTransactionResult.name,
          'Transaction $transactionResult"',
          Colors.red,
        );

        Get.off(
          () => PaymentFailureWidget(txnId: transactionResponse?.txnId),
        );
      }
    } else if (transactionResult == NttTransactionResult.FAILED) {
      isPaymentConditionEnabled1.value = false;
      isPaymentConditionEnabled2.value = false;
      isPaymentButtonEnabled.value = true;
      // Payment status update
      Transaction? updateTransactionResponse = await updatePayment(
        token: token,
        transactionId: nttModelResponse!.txnid!,
      );

      log('----------------updateTransactionResponse--------------------');
      dPrint('${updateTransactionResponse?.toJson()}');

      if (updateTransactionResponse == null) {
        isPaymentConditionEnabled1.value = false;
        isPaymentConditionEnabled2.value = false;
        isPaymentButtonEnabled.value = true;
        snackBar(
          nttTransactionResult.name,
          'Transaction $transactionResult"',
          Colors.red,
        );

        Get.off(() => PaymentFailureWidget(txnId: transactionResponse?.txnId));
      }
    } else if (transactionResult == NttTransactionResult.CANCELLED) {
      isPaymentConditionEnabled1.value = false;
      isPaymentConditionEnabled2.value = false;
      isPaymentButtonEnabled.value = true;
      snackBar(
        nttTransactionResult.name,
        'Transaction $transactionResult"',
        Colors.red,
      );
    } else {
      isPaymentConditionEnabled1.value = false;
      isPaymentConditionEnabled2.value = false;
      isPaymentButtonEnabled.value = true;
      snackBar(
        nttTransactionResult.name,
        'Transaction $transactionResult"',
        Colors.red,
      );
    }
  }
}
