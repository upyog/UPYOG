import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';

class MutationPriceWidget extends StatelessWidget {
  const MutationPriceWidget({
    super.key,
    required this.property,
    required this.token,
    required this.service,
  });

  final Property property;
  final String token;
  final String service;

  @override
  Widget build(BuildContext context) {
    final paymentController = Get.find<PaymentController>();
    return FutureBuilder(
      future: paymentController.searchBillById(
        tenantId: property.tenantId!,
        token: token,
        service: service,
        consumerCode: property.acknowledgementNumber!,
      ),
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          final billData = snapshot.data!;

          final bill = billData.bill?.firstOrNull;

          return isNotNullOrEmpty(bill)
              ? Column(
                  children: [
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.PT_FEE_AMOUNT,
                        module: Modules.PT,
                      ),
                      text: isNotNullOrEmpty(bill?.totalAmount)
                          ? '₹ ${bill!.totalAmount}'
                          : 'N/A',
                      fontWeight: FontWeight.w600,
                    ).paddingOnly(left: 7),
                    const SizedBox(height: 10),
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.PT_PAYMENT_STATUS,
                        module: Modules.PT,
                      ),
                      text: getLocalizedString(
                        '${i18.propertyTax.PT_MUT_BILL}${bill?.status}',
                        module: Modules.PT,
                      ),
                    ).paddingOnly(left: 7),
                    const SizedBox(height: 10),
                  ],
                )
              : const SizedBox.shrink();
        } else if (snapshot.hasError) {
          return networkErrorPage(context, () => build(context));
        } else {
          switch (snapshot.connectionState) {
            case ConnectionState.waiting:
              return showCircularIndicator();
            case ConnectionState.active:
              return showCircularIndicator();
            default:
              return const SizedBox.shrink();
          }
        }
      },
    );
  }
}
