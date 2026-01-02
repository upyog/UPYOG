import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/web_view/web_view_widget.dart';

class MeriPehchanLoginScreen extends StatelessWidget {
  const MeriPehchanLoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final url = Get.arguments as String? ?? '';

    RxDouble progressVal = 0.0.obs;

    return Scaffold(
      appBar: HeaderTop(
        title: 'Login via MeriPehchaan',
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Stack(
          children: [
            WebViewWidget(
              url: url,
              onLoadStop: (controller, uri) async {
                final uriParams = Uri.parse(uri.toString()).queryParameters;
                final error = uriParams['error'];
                final code = uriParams['code'];

                dPrint('onLoadStop: ${uri.toString()}');
                dPrint('Error: $error');
                dPrint('Code: $code');

                progressVal.value = 1.0;

                if (error == 'access_denied') {
                  Get.offAllNamed(AppRoutes.LOGIN);
                }

                if (code != null) {
                  // Get.offAllNamed(AppRoutes.BOTTOM_NAV);
                  await verifyUrl(uri.toString(), code, controller);
                }
              },
              onUpdateVisitedHistory: (controller, uri, isReload) async {
                final uriParams = Uri.parse(uri.toString()).queryParameters;
                final error = uriParams['error'];
                final code = uriParams['code'];

                dPrint('onUpdateVisitedHistory: ${uri.toString()}');
                dPrint('Error: $error');
                dPrint('Code: $code');

                if (error == 'access_denied') {
                  Get.offAllNamed(AppRoutes.LOGIN);
                }

                if (code != null) {
                  // Get.offAllNamed(AppRoutes.BOTTOM_NAV);
                  await verifyUrl(uri.toString(), code, controller);
                }
              },
            ),
            // WebViewWidget(
            //   url: url,
            //   onLoadStop: (controller, uri) async {
            //     final code = Uri.parse(uri.toString()).queryParameters['code'];
            //
            //     dPrint('onLoadStop: ${uri.toString()}');
            //     dPrint('code: $code');
            //     dPrint('Url: ${Uri.parse(uri.toString()).path}');
            //
            //     progressVal.value = 1.0;
            //
            //     await verifyUrl(uri.toString(), code ?? '', controller);
            //   },
            //   onUpdateVisitedHistory: (controller, uri, isReload) async {
            //     final code = Uri.parse(uri.toString()).queryParameters['code'];
            //
            //     dPrint('onUpdateVisitedHistory: ${uri.toString()}');
            //     dPrint('code: $code');
            //
            //     await verifyUrl(uri.toString(), code ?? '', controller);
            //   },
            // ),
            Obx(
              () => progressVal.value != 1.0
                  ? Center(child: showCircularIndicator())
                  : const SizedBox.shrink(),
            ),
          ],
        ),
      ),
    );
  }

  loginVerify(String code) async {
    final authController = Get.find<AuthController>();

    await authController.otpValidate(
      otp: code,
      isMeripehchaanLogin: true,
      userType: UserType.CITIZEN,
    );
  }

  Future<void> verifyUrl(
    String url,
    String code,
    InAppWebViewController controller,
  ) async {
    Uri uri = Uri.parse(url);
    String baseUrl = "${uri.scheme}://${uri.host}/";
    dPrint('Base URL: $baseUrl');
    if (isNotNullOrEmpty(code) && baseUrl != BaseConfig.BACKEND_URL) {
      showErrorDialog(
        'DigiLocker Redirect URL does not match with configured App Backend URL. Please connect with Backend support team.',
        url,
      );
      controller.stopLoading();
      return;
    } else {
      if (isNotNullOrEmpty(code)) {
        await loginVerify(code);
      }
    }
  }
}
