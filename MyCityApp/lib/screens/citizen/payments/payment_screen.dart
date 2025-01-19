import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/env/ntt_payment_config.dart';
import 'package:mobile_app/model/citizen/transaction/transaction.dart';
import 'package:mobile_app/model/request/payment_request_model.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:nuts_activity_indicator/nuts_activity_indicator.dart';

class PaymentScreen extends StatelessWidget {
  final String token;
  final String consumerCode;
  final BusinessService businessService;
  final String cityTenantId;
  final String text1;
  final String text2;
  final String module;
  final String? billId;
  final String? totalAmount;

  PaymentScreen({
    super.key,
    required this.token,
    required this.consumerCode,
    required this.businessService,
    required this.cityTenantId,
    this.text1 = 'Receipt',
    this.text2 = 'TL Certificate',
    required this.module,
    required this.billId,
    required this.totalAmount,
  });

  final _paymentController = Get.find<PaymentController>();
  final _editProfileController = Get.find<EditProfileController>();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Navigator.of(context).pop();
        },
        title: 'Payment',
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          child: Obx(
            () => Column(
              children: [
                _buildPaymentCard(),
                if (_paymentController.isPaymentConditionEnabled1.value)
                  _buildPaymentCondition1(),
                if (_paymentController.isPaymentConditionEnabled2.value)
                  _buildPaymentCondition2(),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildPaymentCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const MediumTextNotoSans(text: 'Total Amount Due: '),
                // _buildPrice(),
                Wrap(
                  children: [
                    const MediumTextNotoSans(
                      text: '₹',
                      fontWeight: FontWeight.bold,
                    ),
                    const SizedBox(width: 5),
                    MediumTextNotoSans(
                      text:
                          isNotNullOrEmpty(totalAmount) ? totalAmount! : 'N/A',
                      fontWeight: FontWeight.bold,
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(
              height: 40,
            ),
            const MediumTextNotoSans(text: 'Select Payment Method'),
            const SizedBox(height: 10),
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                SizedBox(
                  height: 20,
                  width: 20,
                  child: Radio<String>(
                    activeColor: BaseConfig.appThemeColor2,
                    value: 'NTTDATA',
                    groupValue: 'NTTDATA',
                    onChanged: (String? value) {
                      print(value);
                    },
                  ),
                ),
                const SizedBox(width: 10),
                const MediumTextNotoSans(text: 'NTTDATA'),
              ],
            ),
            const SizedBox(height: 30),
            if (_paymentController.isPaymentButtonEnabled.value)
              Obx(() {
                return FilledButtonApp(
                  text: 'Pay',
                  width: Get.width,
                  isLoading: _paymentController.isLoading.value,
                  circularColor: BaseConfig.fillAppBtnCircularColor,
                  onPressed: _paymentController.isLoading.value
                      ? null
                      : () async {
                          try {
                            if (!isNotNullOrEmpty(
                              _editProfileController.user.emailId,
                            )) {
                              snackBar(
                                'Error',
                                'Email required!',
                                BaseConfig.redColor,
                              );
                              return;
                            }

                            _paymentController.text1 = text1;
                            _paymentController.text2 = text2;

                            _paymentController.isLoading.value = true;

                            final data = {
                              'token': token,
                              'tenantId': cityTenantId,
                              'consumerCode': consumerCode,
                              'businessService': businessService.name,
                            };

                            String login =
                                BaseConfig.NTT_MERCHANT_ID; //mandatory
                            String password =
                                BaseConfig.NTT_MERCHANT_PASSWORD; //mandatory
                            String prodId = BaseConfig.NTT_PROD_ID; //mandatory
                            String requestHashKey =
                                BaseConfig.NTT_REQUEST_HASH_KEY; //mandatory
                            String responseHashKey =
                                BaseConfig.NTT_RESPONSE_HASH_KEY; //mandatory
                            String requestEncryptionKey =
                                BaseConfig.NTT_REQUEST_RESPONSE_KEY; //mandatory
                            String responseDecryptionKey = BaseConfig
                                .NTT_REQUEST_DECRYPTION_KEY; //mandatory
                            // String txnid = 'test240223'; // mandatory // this should be unique each time
                            String clientCode =
                                BaseConfig.NTT_CLIENT_CODE; //mandatory
                            String txncurr =
                                BaseConfig.NTT_TXN_CURRENCY; //mandatory
                            String mccCode =
                                BaseConfig.NTT_MCC_CODE; //mandatory
                            String merchType =
                                BaseConfig.NTT_MERCHANT_TYPE; //mandatory
                            // String amount = "1.00"; //mandatory
                            String mode = paymentMode;

                            // String custFirstName = 'test'; //optional
                            // String custLastName = 'user'; //optional
                            // String mobile = '8888888888'; //optional
                            // String email = 'test@gmail.com'; //optional
                            // String address = 'mumbai'; //optional
                            // String custacc = '639827'; //optional

                            await HiveService.setData(
                              HiveConstants.MODULES,
                              module,
                            );

                            (Transaction?, bool isError) transactionRes =
                                await _paymentController.createPayment(
                              token: token,
                              cityTenantId: cityTenantId,
                              consumerCode: consumerCode,
                              businessService: businessService.name,
                              totalAmount: double.parse(totalAmount!),
                              billId: billId!,
                            );

                            // If true prevent ntt payment call
                            if (transactionRes.$2) {
                              _paymentController.isLoading.value = false;
                              return;
                            }

                            _paymentController
                                .nttModelResponse = PaymentRequestModel()
                              ..login = login
                              ..password = password
                              ..prodid = prodId
                              ..requestHashKey = requestHashKey
                              ..responseHashKey = responseHashKey
                              ..requestEncryptionKey = requestEncryptionKey
                              ..responseDecryptionKey = responseDecryptionKey
                              ..txnid = transactionRes.$1!.txnId
                              ..clientcode = clientCode
                              ..txncurr = txncurr
                              ..mccCode = mccCode
                              ..merchType = merchType
                              ..amount = totalAmount
                              ..mode = mode
                              ..custFirstName = _editProfileController.user.name
                              ..custLastName = ''
                              ..mobile =
                                  _editProfileController.user.mobileNumber
                              ..email = _editProfileController.user.emailId
                              ..address =
                                  _editProfileController.user.permanentAddress
                              ..custacc = _editProfileController.user.uuid;

                            _paymentController.transactionResponse =
                                transactionRes.$1;

                            log('-------------transactionResponse-------------');
                            dPrint('${transactionRes.$1?.toJson()}');

                            if (transactionRes.$1 != null) {
                              data['txnid'] =
                                  _paymentController.nttModelResponse!.txnid!;
                              _paymentController.initNdpsPayment(
                                responseHashKey,
                                responseDecryptionKey,
                                nttRequest:
                                    _paymentController.nttModelResponse!,
                                data: data,
                              );
                            }
                          } catch (e) {
                            _paymentController.isLoading.value = false;
                            dPrint('Payment init error: ${e.toString()}');
                          }
                          //_paymentController.isLoading.value = false;
                        },
                );
              }),
          ],
        ),
      ),
    ).marginAll(20.0);
  }

  // Widget _buildPrice() {
  //   return FutureBuilder<BillInfo?>(
  //     future: _paymentController.getPayment(
  //       token: token,
  //       consumerCode: consumerCode,
  //       businessService: businessService,
  //       tenantId: cityTenantId,
  //     ),
  //     builder: (context, snapshot) {
  //       if (snapshot.hasData && !snapshot.hasError) {
  //         final BillInfo billInfo = snapshot.data!;
  //         _paymentController.billInfo = billInfo;
  //         return Wrap(
  //           children: [
  //             const MediumTextNotoSans(
  //               text: '₹',
  //               fontWeight: FontWeight.bold,
  //             ),
  //             const SizedBox(width: 5),
  //             MediumTextNotoSans(
  //               text: getTotal(
  //                 billInfo.bill?.firstOrNull?.totalAmount,
  //               ),
  //               fontWeight: FontWeight.bold,
  //             ),
  //           ],
  //         );
  //       } else {
  //         if (snapshot.connectionState == ConnectionState.waiting ||
  //             snapshot.connectionState == ConnectionState.active) {
  //           return showCircularIndicator();
  //         } else {
  //           return const MediumTextNotoSans(
  //             text: 'N/A',
  //             fontWeight: FontWeight.bold,
  //           );
  //         }
  //       }
  //     },
  //   );
  // }

  Widget _buildPaymentCondition1() {
    return const Padding(
      padding: EdgeInsets.all(40.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              NutsActivityIndicator(
                relativeWidth: 0.5,
                tickCount: 20,
                radius: 30,
                activeColor: BaseConfig.appThemeColor1,
                inactiveColor: BaseConfig.shadeAmber,
              ),
              SizedBox(
                width: 20,
              ),
              MediumTextNotoSans(
                text: 'Processing...',
                fontWeight: FontWeight.bold,
                size: 12,
              ),
            ],
          ),
          SizedBox(height: 30),
          MediumTextNotoSans(
            text: 'You will be redirected to the ',
            fontWeight: FontWeight.bold,
          ),
          MediumTextNotoSans(
            text: 'Gateway. It may take few seconds.',
            fontWeight: FontWeight.bold,
          ),
          SizedBox(height: 10),
          MediumTextNotoSans(
            text: 'Please do not refresh page or',
            fontWeight: FontWeight.bold,
          ),
          MediumTextNotoSans(
            text: 'click "Back" or "Close" button.',
            fontWeight: FontWeight.bold,
          ),
          SizedBox(height: 10),
        ],
      ),
    );
  }

  Widget _buildPaymentCondition2() {
    return const Padding(
      padding: EdgeInsets.all(40.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              NutsActivityIndicator(
                relativeWidth: 0.5,
                tickCount: 20,
                radius: 30,
                activeColor: BaseConfig.appThemeColor1,
                inactiveColor: BaseConfig.shadeAmber,
              ),
              SizedBox(
                width: 20,
              ),
              MediumTextNotoSans(
                text: 'Processing...',
                fontWeight: FontWeight.bold,
                size: 12,
              ),
            ],
          ),
          SizedBox(height: 20),
          MediumTextNotoSans(
            text: 'Please wait while we verify payment',
            fontWeight: FontWeight.bold,
          ),
          MediumTextNotoSans(
            text: 'from Gateway. It may take few seconds.',
            fontWeight: FontWeight.bold,
          ),
          SizedBox(height: 10),
          MediumTextNotoSans(
            text: 'Please do not refresh page or',
            fontWeight: FontWeight.bold,
          ),
          MediumTextNotoSans(
            text: 'click "Back" or "Close" button.',
            fontWeight: FontWeight.bold,
          ),
          SizedBox(height: 10),
        ],
      ),
    );
  }
}

// String getTotal(double? price) {
//   return isNotNullOrEmpty(price) ? '$price' : 'N/A';
// }
