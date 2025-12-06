import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class PaymentFailureWidget extends StatelessWidget {
  const PaymentFailureWidget({super.key, required this.txnId});

  final String? txnId;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        height: Get.height,
        width: Get.width,
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [
              BaseConfig.appThemeColor1,
              BaseConfig.appThemeColor2,
            ],
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Center(
            child: SizedBox(
              height: Get.height / 1.8,
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(20.0),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      _buildCard(txnId),
                      const SizedBox(
                        height: 20,
                      ),
                      _buildButton(),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildCard(String? txnId) => Container(
        height: 220,
        width: Get.width,
        padding: const EdgeInsets.all(10),
        decoration: BoxDecoration(
          color: Colors.red,
          borderRadius: BorderRadius.circular(20),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const BigText(
              text: 'Payment Failed!',
              fontWeight: FontWeight.bold,
              textAlign: TextAlign.center,
              color: Colors.white,
              size: 22,
            ),
            const SizedBox(
              height: 10,
            ),
            const Icon(
              Icons.error,
              color: Colors.white,
              size: 50,
            ),
            const SizedBox(
              height: 10,
            ),
            const MediumText(
              text: 'Transaction ID',
              textAlign: TextAlign.center,
              color: Colors.white,
            ),
            const SizedBox(
              height: 10,
            ),
            MediumText(
              text: txnId ?? 'N/A',
              textAlign: TextAlign.center,
              color: Colors.white,
            ),
          ],
        ),
      );

  Widget _buildButton() => Column(
        children: [
          const BigText(
            text: 'Any amount deducted will be proceed back 24 hrs',
            color: BaseConfig.textColor,
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 20),
          FilledButtonApp(
            text: getLocalizedString(i18.common.GO_TO_HOME),
            onPressed: () => Get.offAllNamed(AppRoutes.BOTTOM_NAV),
          ),
        ],
      );
}
