import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/controller/water_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/model/citizen/water_sewerage/sewerage.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class SewerageMyPayments extends StatefulWidget {
  const SewerageMyPayments({super.key});

  @override
  State<SewerageMyPayments> createState() => _SewerageMyPaymentsState();
}

class _SewerageMyPaymentsState extends State<SewerageMyPayments> {
  final _authController = Get.find<AuthController>();
  final _waterController = Get.find<WaterController>();
  final _editProfileController = Get.find<EditProfileController>();
  final _paymentController = Get.find<PaymentController>();
  final _propertyTaxController = Get.find<PropertiesTaxController>();
  final _downloadController = Get.find<DownloadController>();
  final _fileController = Get.find<FileController>();
  var paymentStreamController = StreamController.broadcast();

  List<Payment>? _paymentListSw;

  var sewerageLength = 0.obs;

  var isDownloading = <bool>[].obs;

  @override
  void initState() {
    super.initState();
    init();
  }

  void init() async {
    sewerageLength.value = 0;
    TenantTenant tenant = await getCityTenant();
    final tenantId = tenant.code!;
    final token = _authController.token?.accessToken!;

    await _waterController.getSewerageMyApplicationsFuture(
      tenantId: tenant.code!,
      mobileNumber: _editProfileController.user.mobileNumber!,
      token: _authController.token!.accessToken!,
      searchType: SearchType.CONNECTION.name,
    );

    if (_waterController.sewerage?.sewerageConnections != null) {
      final consumerIds = getConsumerIds();
      final paymentSw = await _paymentController.getMyPayment(
        tenantId: tenantId,
        token: token!,
        businessService: BusinessService.SW.name,
        consumerCodes: consumerIds,
      );
      _paymentListSw = paymentSw;
    }

    final propertyIds = getPropertiesIds();
    await _propertyTaxController.fetchMyProperties(
      token: token!,
      propertyIds: propertyIds,
    );

    sewerageLength.value = _paymentListSw?.length ?? 0;

    if (!paymentStreamController.isClosed) {
      paymentStreamController.add(_paymentListSw);
    } else {
      paymentStreamController = StreamController.broadcast();
      paymentStreamController.add(_paymentListSw);
    }

    _paymentController.isLoading.value = false;
  }

  String getConsumerIds() {
    List fileIds = [];
    if (_waterController.sewerage?.sewerageConnections == null) {
      return '';
    }
    for (var connection in _waterController.sewerage!.sewerageConnections!) {
      if (connection.connectionNo != null) {
        fileIds.add(connection.connectionNo);
      }
    }
    return fileIds.join(', ');
  }

  String getPropertiesIds() {
    List fileIds = [];
    if (_waterController.sewerage?.sewerageConnections == null) {
      return '';
    }
    for (var connection in _waterController.sewerage!.sewerageConnections!) {
      if (connection.propertyId != null) {
        fileIds.add(connection.propertyId);
      }
    }
    return fileIds.join(', ');
  }

  /// Merge all owners name
  String? getOwnersName(List<Owner>? owners) {
    if (isNotNullOrEmpty(owners)) {
      String ownerNames = '';
      for (var owner in owners!) {
        ownerNames += '${owner.name}, ';
      }
      return ownerNames.substring(0, ownerNames.length - 2);
    } else {
      return null;
    }
  }

  @override
  void dispose() {
    super.dispose();
    paymentStreamController.close();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Wrap(
          children: [
            Text(
              getLocalizedString(
                i18.waterSewerage.WS_MY_PAYMENTS_HEADER,
                module: Modules.WS,
              ),
            ),
            Obx(
              () => Text(' (${sewerageLength.value})'),
            ),
          ],
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: StreamBuilder(
              stream: paymentStreamController.stream,
              builder: (context, snapshots) {
                if (snapshots.hasData && snapshots.data != null) {
                  if (snapshots.data is String || snapshots.data == null) {
                    return const NoApplicationFoundWidget();
                  }
                  final List<Payment>? paymentList = snapshots.data;

                  if (paymentList == null || paymentList.isEmpty) {
                    return const NoApplicationFoundWidget();
                  }

                  isDownloading
                      .assignAll(List<bool>.filled(paymentList.length, false));

                  return Obx(
                    () => _paymentController.isLoading.value
                        ? SizedBox(
                            height: Get.height,
                            width: Get.width,
                            child: showCircularIndicator(),
                          )
                        : ListView.builder(
                            itemCount: paymentList.length,
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            itemBuilder: (context, index) {
                              final payment = paymentList[index];
                              final sewerageConnection = _waterController
                                  .sewerage!.sewerageConnections!
                                  .firstWhereOrNull(
                                (element) =>
                                    element.connectionNo ==
                                    payment.paymentDetails?.firstOrNull?.bill
                                        ?.consumerCode,
                              );

                              final property = _propertyTaxController
                                  .myProperties?.properties
                                  ?.firstWhereOrNull(
                                (element) =>
                                    element.propertyId ==
                                    sewerageConnection?.propertyId,
                              );

                              return _sewerageMyPaymentsCard(
                                sewerageConnection,
                                payment,
                                property,
                                index,
                              );
                            },
                          ),
                  );
                } else {
                  if (snapshots.connectionState == ConnectionState.active ||
                      snapshots.connectionState == ConnectionState.waiting) {
                    return SizedBox(
                      height: Get.height * 0.8,
                      width: Get.width,
                      child: showCircularIndicator(),
                    );
                  } else if (snapshots.hasError) {
                    return networkErrorPage(context, () => init());
                  } else {
                    return const NoApplicationFoundWidget();
                  }
                }
              },
            ),
          ),
        ),
      ),
    );
  }

  Widget _sewerageMyPaymentsCard(
    SewerageConnection? sewerage,
    Payment? payment,
    property,
    int index,
  ) =>
      Card(
        child: Padding(
          padding: const EdgeInsets.all(10),
          child: ListTile(
            contentPadding: EdgeInsets.zero,
            subtitle: Obx(
              () => _subTitleBuildCard(
                sewerageConnection: sewerage,
                payment: payment,
                property: property,
                isLoading: isDownloading[index],
                onPressed: () async {
                  isDownloading[index] = true;

                  if (isNotNullOrEmpty(payment?.fileStoreId)) {
                    final newFileStore = await _fileController.getFiles(
                      fileStoreIds: payment!.fileStoreId!,
                      tenantId: payment.tenantId!,
                      token: _authController.token!.accessToken!,
                    );
                    if (newFileStore != null &&
                        newFileStore.fileStoreIds != null &&
                        newFileStore.fileStoreIds!.isNotEmpty) {
                      _downloadController.starFileDownload(
                        url: newFileStore.fileStoreIds![0].url!,
                        title: getLocalizedString(
                          i18.waterSewerage.WS_DOWNLOAD_RECEIPT,
                          module: Modules.WS,
                        ),
                      );
                    } else {
                      snackBar('Error', 'File not found!', BaseConfig.redColor);
                    }
                  } else {
                    snackBar('Error', 'File not found!', BaseConfig.redColor);
                  }
                  isDownloading[index] = false;
                },
              ),
            ),
          ),
        ),
      ).paddingOnly(bottom: 10);

  Widget _subTitleBuildCard({
    required SewerageConnection? sewerageConnection,
    required Payment? payment,
    required Property? property,
    required VoidCallback onPressed,
    required bool isLoading,
  }) =>
      Padding(
        padding: EdgeInsets.all(10.w),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.CS_PAYMENT_AMOUNT_DUE_WITHOUT_SYMBOL,
                module: Modules.WS,
              ),
              text: isNotNullOrEmpty(payment?.totalAmountPaid)
                  ? '₹ ${payment?.totalAmountPaid?.toInt()}'
                  : 'N/A',
              fontWeight: FontWeight.bold,
              textSize: 16,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.SERVICE_NAME_LABEL,
                module: Modules.WS,
              ),
              text: isNotNullOrEmpty(payment?.paymentDetails?.firstOrNull)
                  ? getLocalizedString(
                      '${i18.waterSewerage.WNS_SERVICE_TYPE}${payment!.paymentDetails!.first.businessService!}',
                      module: Modules.WS,
                    )
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WS_CONSUMER_CODE,
                module: Modules.WS,
              ),
              text: payment?.paymentDetails?.firstOrNull?.bill?.consumerCode ??
                  'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WNS_CONSUMER_NAME,
                module: Modules.WS,
              ),
              text: getOwnersName(property?.owners) ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WS_RECEIPT_NO_LABEL,
                module: Modules.WS,
              ),
              text:
                  payment?.paymentDetails?.firstOrNull?.receiptNumber ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WS_RECEIPT_DATE_LABEL,
                module: Modules.WS,
              ),
              text: payment?.paymentDetails?.firstOrNull?.bill?.billDate
                      ?.toCustomDateFormat(pattern: 'dd/MM/yyyy') ??
                  'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WS_PAYMENT_STATUS,
                module: Modules.WS,
              ),
              text: payment?.paymentStatus ?? 'N/A',
            ),
            const SizedBox(height: 20),
            FilledButtonApp(
              width: Get.width,
              text: getLocalizedString(
                i18.waterSewerage.WS_DOWNLOAD_RECEIPT,
                module: Modules.WS,
              ),
              onPressed: onPressed,
              isLoading: isLoading,
              circularColor: BaseConfig.fillAppBtnCircularColor,
            ),
          ],
        ),
      );
}
