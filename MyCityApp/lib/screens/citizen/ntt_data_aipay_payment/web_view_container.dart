import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/screens/citizen/ntt_data_aipay_payment/atom_pay_helper.dart';
import 'package:mobile_app/utils/enums/ntt_payment_enum.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:url_launcher/url_launcher.dart';

class WebViewContainer extends StatefulWidget {
  final mode;
  final payDetails;
  final responsehashKey;
  final responseDecryptionKey;
  final Map<String, dynamic> data;

  const WebViewContainer(
    this.mode,
    this.payDetails,
    this.responsehashKey,
    this.responseDecryptionKey, {
    super.key,
    required this.data,
  });

  @override
  createState() => _WebViewContainerState(
        mode,
        payDetails,
        responsehashKey,
        responseDecryptionKey,
      );
}

class _WebViewContainerState extends State<WebViewContainer> {
  final _paymentController = Get.find<PaymentController>();
  final _authController = Get.find<AuthController>();
  // final _editProfileController = Get.find<EditProfileController>();

  final mode;
  final payDetails;
  final _responsehashKey;
  final _responseDecryptionKey;
  Map<String, dynamic> get _data => widget.data;
  // final _key = UniqueKey();
  late InAppWebViewController _controller;

  final Completer<InAppWebViewController> _controllerCompleter =
      Completer<InAppWebViewController>();

  _WebViewContainerState(
    this.mode,
    this.payDetails,
    this._responsehashKey,
    this._responseDecryptionKey,
  );

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvoked: (popResult) {
        if (popResult) return;
        _handleBackButtonAction(context);
      },
      child: Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.transparent,
          automaticallyImplyLeading: false,
          elevation: 0,
          toolbarHeight: 2,
        ),
        body: SafeArea(
          child: InAppWebView(
            // initialUrl: 'about:blank',
            key: UniqueKey(),
            onWebViewCreated: (InAppWebViewController inAppWebViewController) {
              _controllerCompleter.future.then((value) => _controller = value);
              _controllerCompleter.complete(inAppWebViewController);

              dPrint("payDetails from webview $payDetails");
              _loadHtmlFromAssets(mode);
            },
            shouldOverrideUrlLoading: (controller, navigationAction) async {
              String url = navigationAction.request.url as String;
              var uri = navigationAction.request.url!;
              if (url.startsWith("upi://")) {
                dPrint("upi url started loading");
                try {
                  await launchUrl(uri);
                } catch (e) {
                  _closeWebView(
                    context,
                    "Transaction Status = cannot open UPI applications",
                    nttTransactionResult: NttTransactionResult.ERROR,
                  );
                  throw 'custom error for UPI Intent';
                }
                return NavigationActionPolicy.CANCEL;
              }
              return NavigationActionPolicy.ALLOW;
            },

            onLoadStop: (controller, url) async {
              dPrint("onloadstop_url: $url");

              if (url.toString().contains("AIPAYLocalFile")) {
                dPrint(" AIPAYLocalFile Now url loaded: $url");
                await _controller.evaluateJavascript(
                  source: "${"openPay('" + payDetails}')",
                );
              }

              if (url.toString().contains('/mobilesdk/param')) {
                final String response = await _controller.evaluateJavascript(
                  source: "document.getElementsByTagName('h5')[0].innerHTML",
                );
                dPrint("HTML response : $response");

                var transactionResult = "";
                NttTransactionResult nttTransactionResult;
                dynamic nttResponse;

                if (response.trim().contains("cancelTransaction")) {
                  transactionResult = "Transaction Cancelled!";
                  nttTransactionResult = NttTransactionResult.CANCELLED;
                } else {
                  final split = response.trim().split('|');
                  final Map<int, String> values = {
                    for (int i = 0; i < split.length; i++) i: split[i],
                  };

                  final splitTwo = values[1]!.split('=');
                  const platform = MethodChannel('flutter.dev/NDPSAESLibrary');

                  try {
                    final String result =
                        await platform.invokeMethod('NDPSAESInit', {
                      'AES_Method': 'decrypt',
                      'text': splitTwo[1].toString(),
                      'encKey': _responseDecryptionKey,
                    });
                    var respJsonStr = result.toString();
                    Map<String, dynamic> jsonInput = jsonDecode(respJsonStr);
                    dPrint("read full respone : $jsonInput");

                    //calling validateSignature function from atom_pay_helper file
                    var checkFinalTransaction =
                        validateSignature(jsonInput, _responsehashKey);

                    if (checkFinalTransaction) {
                      if (jsonInput["payInstrument"]["responseDetails"]
                                  ["statusCode"] ==
                              'OTS0000' ||
                          jsonInput["payInstrument"]["responseDetails"]
                                  ["statusCode"] ==
                              'OTS0551') {
                        dPrint("Transaction success");
                        transactionResult = "Transaction Success";
                        nttTransactionResult = NttTransactionResult.SUCCESS;
                      } else {
                        dPrint("Transaction failed");
                        transactionResult = "Transaction Failed";
                        nttTransactionResult = NttTransactionResult.FAILED;
                      }
                    } else {
                      dPrint("signature mismatched");
                      transactionResult = "failed";
                      nttTransactionResult = NttTransactionResult.FAILED;
                    }
                    dPrint("Transaction Response : $jsonInput");
                    nttResponse = jsonInput;
                  } on PlatformException catch (e) {
                    dPrint("Failed to decrypt: '${e.message}'.");
                    nttTransactionResult = NttTransactionResult.ERROR;
                    nttResponse = e.message;
                  }
                }
                _closeWebView(
                  context,
                  transactionResult,
                  nttTransactionResult: nttTransactionResult,
                  response: nttResponse,
                );
              }
            },
          ),
        ),
      ),
    );
  }

  _loadHtmlFromAssets(mode) async {
    final localUrl = mode == 'uat'
        ? "assets/aipay/aipay_uat.html"
        : "assets/aipay/aipay_prod.html";
    String fileText = await rootBundle.loadString(localUrl);
    _controller.loadUrl(
      urlRequest: URLRequest(
        url: WebUri.uri(
          Uri.dataFromString(
            fileText,
            mimeType: 'text/html',
            encoding: Encoding.getByName('utf-8'),
          ),
        ),
      ),
    );
  }

  _closeWebView(
    context,
    transactionResult, {
    required NttTransactionResult nttTransactionResult,
    dynamic response,
  }) {
    _paymentController.checkTransactionPayment(
      nttTransactionResult: nttTransactionResult,
      response: response,
      transactionResult: transactionResult,
      token: _authController.token!.accessToken!,
      ntData: _data,
    );
    Navigator.pop(context);
  }

  Future<void> _handleBackButtonAction(BuildContext context) async {
    dPrint("_handleBackButtonAction called");
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Do you want to exit the payment ?'),
        actions: <Widget>[
          // ignore: deprecated_member_use
          ElevatedButton(
            onPressed: () {
              Navigator.of(context).pop();
            },
            child: const Text('No'),
          ),

          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              Navigator.of(context).pop(); // Close current window
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                  content: Text(
                    "Transaction Status = Transaction cancelled",
                  ),
                ),
              );
            },
            child: const Text('Yes'),
          ),
        ],
      ),
    );
  }
}
