import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class MyPaymentsScreen extends StatefulWidget {
  const MyPaymentsScreen({super.key});

  @override
  State<MyPaymentsScreen> createState() => _MyPaymentsScreenState();
}

class _MyPaymentsScreenState extends State<MyPaymentsScreen> {
  final _authController = Get.find<AuthController>();
  final _propertiesTaxController = Get.find<PropertiesTaxController>();
  final ptPaymentStreamController = StreamController.broadcast();

  List<Payment>? _paymentListpt;
  PtMyProperties? properties;

  @override
  void initState() {
    init();
    super.initState();
  }

//Get Consumer Code
  String getConsumerIdPt() {
    List fileIds = [];
    for (var connection in properties!.properties!) {
      if (isNotNullOrEmpty(connection.propertyId)) {
        fileIds.add(connection.propertyId);
      }
    }
    return fileIds.join(', ');
  }

  void init() async {
    try {
      final token = _authController.token?.accessToken!;

      properties = await _propertiesTaxController.getPropId(
        token: _authController.token!.accessToken!,
      );

      dPrint("getPropId called in home");

      if (properties?.properties != null) {
        final consumerId = getConsumerIdPt();
        var payments = await _propertiesTaxController.getPtMyPayments(
          token: token!,
          consumerCodes: consumerId,
        );
        _paymentListpt = payments;
      }

      ptPaymentStreamController.add(_paymentListpt);
    } catch (e) {
      ptPaymentStreamController.addError(e);
      dPrint('Error in MyPaymentsScreen: $e');
    }
  }

  @override
  dispose() {
    ptPaymentStreamController.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        title: getLocalizedString(
          i18.common.PROPERTY_TAX_PAYMENT_HEADER,
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Padding(
          padding: EdgeInsets.all(16.w),
          child: StreamBuilder(
            stream: ptPaymentStreamController.stream,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return showCircularIndicator();
              } else if (snapshot.hasError) {
                return networkErrorPage(context, () => init());
              } else if (snapshot.data is String ||
                  snapshot.data == null ||
                  snapshot.hasData == false) {
                return const NoApplicationFoundWidget();
              }

              final paymentList = snapshot.data!;

              if (paymentList.isEmpty) {
                return const NoApplicationFoundWidget();
              }

              return ListView.builder(
                itemCount: paymentList.length,
                itemBuilder: (context, index) {
                  final Payment payment = paymentList[index];

                  final property = properties?.properties?.firstWhereOrNull(
                    (element) =>
                        element.propertyId ==
                        payment.paymentDetails?.firstOrNull?.bill?.consumerCode,
                  );

                  return PaymentCard(payment: payment, property: property)
                      .marginOnly(bottom: 12.h);
                },
              );
            },
          ),
        ),
      ),
    );
  }
}

class PaymentCard extends StatelessWidget {
  PaymentCard({super.key, required this.payment, this.property});

  final Payment payment;
  final Property? property;

  final _authController = Get.find<AuthController>();
  final _fileController = Get.find<FileController>();
  final _downloadController = Get.find<DownloadController>();

  String? getOwnersName(List<Owner>? owners) {
    if (isNotNullOrEmpty(owners)) {
      return null;
    }
    String ownerNames = '';
    for (var owner in owners!) {
      ownerNames += '${owner.name}, ';
    }
    return ownerNames.substring(0, ownerNames.length - 2);
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 3,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12.r),
        side: BorderSide(
          color: BaseConfig.borderColor,
          width: 1.w,
        ),
      ),
      child: Padding(
        padding: EdgeInsets.all(10.w),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                SizedBox(
                  width: Get.width * 0.5,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      BigTextNotoSans(
                        text: getLocalizedString(
                          i18.propertyTax.PAYMENT_AMOUNT_PAID,
                          module: Modules.PT,
                        ),
                        size: 16.sp,
                        fontWeight: FontWeight.w600,
                      ),
                      BigTextNotoSans(
                        text: '₹ ${payment.totalAmountPaid ?? "N/A"}',
                        size: 16.sp,
                        fontWeight: FontWeight.w600,
                      ),
                    ],
                  ),
                ),
                IconButton(
                  onPressed: () async {
                    if (isNotNullOrEmpty(
                      payment.fileStoreId,
                    )) {
                      final newFileStore = await _fileController.getFiles(
                        fileStoreIds: payment.fileStoreId!,
                        tenantId: BaseConfig.STATE_TENANT_ID, //For Test ENV
                        token: _authController.token!.accessToken!,
                      );
                      if (isNotNullOrEmpty(
                        newFileStore?.fileStoreIds,
                      )) {
                        _downloadController.starFileDownload(
                          url: newFileStore!.fileStoreIds!.first.url!,
                          title: getLocalizedString(
                            i18.propertyTax.COMMON_DOWNLOAD_RECEIPT,
                            module: Modules.PT,
                          ),
                        );
                      } else {
                        snackBar(
                          'Not Found',
                          'File not found',
                          BaseConfig.redColor,
                        );
                      }
                    } else {
                      final PdfKey key = payment.paymentDetails?.firstOrNull
                                  ?.businessService ==
                              BusinessService.PT_MUTATION.name
                          ? PdfKey.pt_recept
                          : PdfKey.property_receipt;

                      final pdfFileStoreId =
                          await _fileController.getPdfServiceFile(
                        tenantId: payment.tenantId!,
                        token: _authController.token!.accessToken!,
                        payment: payment,
                        key: key,
                      );
                      if (isNotNullOrEmpty(pdfFileStoreId)) {
                        final newFileStore = await _fileController.getFiles(
                          fileStoreIds: pdfFileStoreId,
                          tenantId: payment.tenantId!,
                          token: _authController.token!.accessToken!,
                        );
                        if (isNotNullOrEmpty(
                          newFileStore?.fileStoreIds,
                        )) {
                          _downloadController.starFileDownload(
                            url: newFileStore!.fileStoreIds!.first.url!,
                            title: getLocalizedString(
                              i18.propertyTax.COMMON_DOWNLOAD_RECEIPT,
                              module: Modules.PT,
                            ),
                          );
                        }
                      }
                    }
                  },
                  icon: Icon(
                    Icons.download_for_offline,
                    color: BaseConfig.appThemeColor1,
                    size: 24.w,
                  ),
                ),
              ],
            ),
            const Divider(
              color: BaseConfig.borderColor,
            ),
            SizedBox(height: 4.h),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.PAYMENT_PROPERTY_ID,
                module: Modules.PT,
              ),
              text: payment.paymentDetails?.first.bill?.consumerCode ?? "N/A",
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.PAYMENT_OWNER_NAME,
                module: Modules.PT,
              ),
              text: getOwnersName(property?.owners) ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.PAYMENT_RECEIPT_DATE,
                module: Modules.PT,
              ),
              text: payment.paymentDetails?.first.receiptDate
                      .toCustomDateFormat(pattern: 'dd/MM/yyyy') ??
                  "N/A",
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.PAYMENT_RECEIPT_NO,
                module: Modules.PT,
              ),
              text: payment.paymentDetails?.first.receiptNumber ?? "N/A",
            ),
            const SizedBox(height: 10),
          ],
        ),
      ),
    );
  }
}
