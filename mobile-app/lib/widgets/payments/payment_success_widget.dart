// ignore_for_file: use_build_context_synchronously

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/text_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class PaymentSuccessWidget extends StatelessWidget {
  const PaymentSuccessWidget({
    super.key,
    required this.payment,
    required this.text1,
    required this.text2,
    required this.module,
  });

  final Payment? payment;
  final String text1;
  final String text2;
  final String? module;

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
              height: Get.height / 1.25,
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(20.0),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      _buildCard(payment?.paymentDetails?.first.receiptNumber),
                      const SizedBox(
                        height: 20,
                      ),
                      _BuildButton(
                        payment: payment,
                        text1: text1,
                        text2: text2,
                        module: module,
                      ),
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

  Widget _buildCard(String? receiptNo) => Container(
        height: 220,
        width: Get.width,
        padding: const EdgeInsets.all(10),
        decoration: BoxDecoration(
          color: const Color(0xFF00703C),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const BigText(
              text: 'Payment Complete',
              fontWeight: FontWeight.bold,
              textAlign: TextAlign.center,
              color: Colors.white,
              size: 22,
            ),
            const SizedBox(
              height: 10,
            ),
            const Icon(
              Icons.check_circle,
              color: Colors.white,
              size: 50,
            ),
            const SizedBox(
              height: 10,
            ),
            const MediumText(
              text: 'Receipt No.',
              textAlign: TextAlign.center,
              color: Colors.white,
            ),
            const SizedBox(
              height: 10,
            ),
            MediumText(
              text: receiptNo ?? 'N/A',
              textAlign: TextAlign.center,
              color: Colors.white,
              fontWeight: FontWeight.bold,
            ),
          ],
        ),
      );
}

class _BuildButton extends StatelessWidget {
  const _BuildButton({
    required this.payment,
    required this.text1,
    required this.text2,
    required this.module,
  });

  final Payment? payment;
  final String text1;
  final String text2;
  final String? module;

  @override
  Widget build(BuildContext context) {
    final downloadController = Get.find<DownloadController>();
    final fileController = Get.find<FileController>();
    final authController = Get.find<AuthController>();
    // final paymentController = Get.find<PaymentController>();

    return Column(
      children: [
        ColumnHeaderText(
          label: 'Reference No.',
          text: payment?.paymentDetails?.first.bill?.consumerCode ?? 'N/A',
        ),
        const SizedBox(height: 10),
        ColumnHeaderText(
          label: 'Transaction ID',
          text: payment?.transactionNumber ?? 'N/A',
        ),
        const SizedBox(height: 10),
        ColumnHeaderText(
          label: 'Amount Paid',
          text: payment?.totalAmountPaid != null
              ? '${payment!.totalAmountPaid}'
              : 'N/A',
        ),
        const SizedBox(height: 10),
        ColumnHeaderText(
          label: 'Payment Date',
          text: payment?.transactionDate
                  ?.toCustomDateFormat(pattern: 'dd/MM/yyyy') ??
              'N/A', //'20/5/2024',
        ),
        SizedBox(height: 10.h),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Obx(
              () => TextButtonApp(
                isLoading: fileController.isLoading.value,
                circularColor: BaseConfig.appThemeColor1,
                onPressed: () async {
                  //TODO: Download Reciept
                  fileController.isLoading.value = true;
                  try {
                    final token = authController.token?.accessToken!;
                    if (isNotNullOrEmpty(payment?.fileStoreId)) {
                      final newFileStore = await fileController.getFiles(
                        fileStoreIds: payment!.fileStoreId!,
                        tenantId: BaseConfig.STATE_TENANT_ID,
                        token: token!,
                      );
                      if (isNotNullOrEmpty(
                        newFileStore?.fileStoreIds,
                      )) {
                        downloadController.starFileDownload(
                          url: newFileStore!.fileStoreIds!.first.url!,
                          title: getLocalizedString(
                            i18.common.DOWNLOAD_RECEIPT,
                          ),
                        );
                      }
                    } else {
                      final paymentReq = paymentRequestObject(
                        payment: payment,
                        module: module!,
                      );

                      final moduleToPdfKeyMap = {
                        Modules.TL.name: PdfKey.tlReceipt,
                        Modules.FSM.name: PdfKey.fsm_receipt,
                        Modules.UC.name: PdfKey.consolidated_receipt,
                        Modules.NOC.name: PdfKey.consolidated_receipt,
                        Modules.PT.name: paymentReq.firstOrNull?.paymentDetails
                                    ?.firstOrNull?.businessService ==
                                BusinessService.PT_MUTATION.name
                            ? PdfKey.pt_recept
                            : PdfKey.property_receipt,
                      };

                      final PdfKey key = moduleToPdfKeyMap[module]!;

                      final fileStoreId =
                          await fileController.getPdfServiceFile(
                        tenantId: BaseConfig.STATE_TENANT_ID,
                        token: token!,
                        key: key,
                        payments: paymentReq,
                      );

                      final fileStore = await fileController.getFiles(
                        tenantId:
                            BaseConfig.STATE_TENANT_ID, //payment!.tenantId!,
                        token: token,
                        fileStoreIds: fileStoreId,
                      );

                      if (isNotNullOrEmpty(fileStore?.fileStoreIds)) {
                        final url = fileStore!.fileStoreIds!.first.url!
                            .split(',')
                            .first;

                        await downloadController.starFileDownload(
                          url: url,
                          title: getLocalizedString(
                            i18.common.DOWNLOAD_RECEIPT,
                          ),
                        );
                      }
                    }
                  } catch (e) {
                    dPrint('Receipt Error: ${e.toString()}');
                    snackBar(
                      'Error',
                      'Receipt Error - $e',
                      BaseConfig.redColor,
                    );
                  }

                  fileController.isLoading.value = false;
                },
                child: Wrap(
                  children: [
                    const Icon(
                      Icons.download,
                      color: BaseConfig.appThemeColor1,
                    ),
                    const SizedBox(width: 10),
                    MediumText(text: text1),
                  ],
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 20),
        FilledButtonApp(
          width: Get.width,
          text: getLocalizedString(i18.common.GO_TO_HOME),
          onPressed: () {
            Get.offAllNamed(AppRoutes.BOTTOM_NAV);
          },
        ),
      ],
    );
  }
}

List<Payment?> paymentRequestObject({
  Payment? payment,
  required String module,
}) {
  if (module == Modules.PT.name) {
    final payments = printReceiptNew(payment);
    return payments;
  } else {
    return [payment];
  }
}

List<Payment?> printReceiptNew(Payment? payment) {
  List<Array> arrearArray = [];
  List<Array> taxArray = [];
  List<Payment?> paymentArray = [];
  String assessmentYear = "";
  String assessmentYearForReceipt = "";

  try {
    // int count = 0;
    // double totalT = 0;

    if (payment!.paymentDetails!.first.businessService ==
            BusinessService.PT.name ||
        payment.paymentDetails!.first.businessService ==
            BusinessService.PT_MUTATION.name) {
      double roundoffT = 0,
          taxT = 0,
          firecessT = 0,
          cancercessT = 0,
          penaltyT = 0,
          rebateT = 0,
          interestT = 0,
          usageExemptionT = 0,
          specialCategoryExemptionT = 0,
          adhocPenaltyT = 0,
          adhocRebateT = 0;

      var billDetails = payment.paymentDetails!.first.bill!.billDetails;

      for (var element in billDetails!) {
        int elementAmount = element.amount ?? 0;
        int elementAmountPaid = element.amountPaid ?? 0;

        if (elementAmount > 0 || elementAmountPaid > 0) {
          // count++;
          String toDate = convertEpochToDate(element.toPeriod!).split("/")[2];
          String fromDate =
              convertEpochToDate(element.fromPeriod!).split("/")[2];

          assessmentYear = assessmentYear.isEmpty
              ? "$fromDate-$toDate(Rs.$elementAmountPaid)"
              : "$assessmentYear, $fromDate-$toDate(Rs.$elementAmountPaid)";
          assessmentYearForReceipt = "$fromDate-$toDate";

          double tax = 0,
              firecess = 0,
              cancercess = 0,
              penalty = 0,
              rebate = 0,
              interest = 0,
              usageExemption = 0,
              specialCategoryExemption = 0,
              adhocPenalty = 0,
              adhocRebate = 0,
              roundoff = 0;

          for (var ele in element.billAccountDetails!) {
            String taxHeadCode = ele.taxHeadCode ?? "";
            dynamic adjustedAmount = ele.adjustedAmount ?? 0;
            dynamic amount = ele.amount ?? 0;

            if (taxHeadCode == "PT_TAX") {
              tax = double.parse(adjustedAmount.toString());
              taxT += amount;
            } else if (taxHeadCode == "PT_TIME_REBATE") {
              rebate = double.parse(adjustedAmount.toString());
              rebateT += amount;
            } else if (taxHeadCode == "PT_CANCER_CESS") {
              cancercess = double.parse(adjustedAmount.toString());
              cancercessT += amount;
            } else if (taxHeadCode == "PT_FIRE_CESS") {
              firecess = double.parse(adjustedAmount.toString());
              firecessT += amount;
            } else if (taxHeadCode == "PT_TIME_INTEREST") {
              interest = double.parse(adjustedAmount.toString());
              interestT += amount;
            } else if (taxHeadCode == "PT_TIME_PENALTY") {
              penalty = double.parse(adjustedAmount.toString());
              penaltyT += amount;
            } else if (taxHeadCode == "PT_OWNER_EXEMPTION") {
              specialCategoryExemption =
                  double.parse(adjustedAmount.toString());
              specialCategoryExemptionT += amount;
            } else if (taxHeadCode == "PT_ROUNDOFF") {
              roundoff = double.parse(adjustedAmount.toString());
              roundoffT += amount;
            } else if (taxHeadCode == "PT_UNIT_USAGE_EXEMPTION") {
              usageExemption = double.parse(adjustedAmount.toString());
              usageExemptionT += amount;
            } else if (taxHeadCode == "PT_ADHOC_PENALTY") {
              adhocPenalty = double.parse(adjustedAmount.toString());
              adhocPenaltyT += amount;
            } else if (taxHeadCode == "PT_ADHOC_REBATE") {
              adhocRebate = double.parse(adjustedAmount.toString());
              adhocRebateT += amount;
            }

            // totalT += amount;
          }

          final arr = Array()
            ..year = assessmentYearForReceipt
            ..tax = tax.toInt()
            ..firecess = firecess
            ..cancercess = cancercess
            ..penalty = penalty
            ..rebate = rebate
            ..interest = interest
            ..usageExemption = usageExemption
            ..specialCategoryExemption = specialCategoryExemption
            ..adhocPenalty = adhocPenalty
            ..adhocRebate = adhocRebate
            ..roundOff = roundoff
            ..total = elementAmountPaid;

          arrearArray.add(arr);

          final taxAr = Array()
            ..year = assessmentYearForReceipt
            ..tax = taxT.toInt()
            ..firecess = firecessT
            ..cancercess = cancercessT
            ..penalty = penaltyT
            ..rebate = rebateT
            ..interest = interestT
            ..usageExemption = usageExemptionT
            ..specialCategoryExemption = specialCategoryExemptionT
            ..adhocPenalty = adhocPenaltyT
            ..adhocRebate = adhocRebateT
            ..roundOff = roundoffT
            ..total = elementAmount;

          taxArray.add(taxAr);
        }
      }

      final additional = AdditionalDetailsClass()
        ..assessmentYears = assessmentYear
        ..arrearArray = arrearArray
        ..taxArray = taxArray;

      payment.paymentDetails!.first.additionalDetails = additional;

      paymentArray.add(payment);
    }
  } catch (e) {
    dPrint('printReceiptNew Error: ${e.toString()}');
  }
  return paymentArray;
}

String convertEpochToDate(int epoch) {
  final date = DateTime.fromMillisecondsSinceEpoch(epoch);
  return "${date.day}/${date.month}/${date.year}";
}
