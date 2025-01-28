import 'dart:async';

import 'package:flutter/material.dart';
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
import 'package:mobile_app/model/citizen/water_sewerage/water.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class WaterMyPayments extends StatefulWidget {
  const WaterMyPayments({super.key});

  @override
  State<WaterMyPayments> createState() => _WaterMyPaymentsState();
}

class _WaterMyPaymentsState extends State<WaterMyPayments> {
  final _authController = Get.find<AuthController>();
  final _waterController = Get.find<WaterController>();
  final _editProfileController = Get.find<EditProfileController>();
  final _paymentController = Get.find<PaymentController>();
  final _propertyTaxController = Get.find<PropertiesTaxController>();
  final _downloadController = Get.find<DownloadController>();
  final _fileController = Get.find<FileController>();

  var paymentStreamController = StreamController.broadcast();

  List<Payment>? _paymentListWs;

  var waterLength = 0.obs;

  var isDownloading = <bool>[].obs;

  @override
  void initState() {
    super.initState();
    init();
  }

  void init() async {
    try {
      waterLength.value = 0;
      TenantTenant tenant = await getCityTenant();
      final tenantId = tenant.code!;
      final token = _authController.token?.accessToken!;

      await _waterController.getWaterMyApplications(
        tenantId: tenant.code!,
        mobileNumber: _editProfileController.user.mobileNumber!,
        token: _authController.token!.accessToken!,
        searchType: SearchType.CONNECTION.name,
      );

      if (isNotNullOrEmpty(_waterController.water?.waterConnection)) {
        _paymentController.isLoading.value = true;
        final consumerIds = getConsumerIdsWs();

        final payment = await _paymentController.getMyPayment(
          tenantId: tenantId,
          token: token!,
          businessService: BusinessService.WS.name,
          consumerCodes: consumerIds,
        );

        _paymentListWs = payment;

        final propertyIds = getPropertiesIdsWs();
        await _propertyTaxController.fetchMyProperties(
          token: token,
          propertyIds: propertyIds,
        );
      }

      waterLength.value = _paymentListWs?.length ?? 0;

      if (!paymentStreamController.isClosed) {
        paymentStreamController.add(_paymentListWs);
      } else {
        paymentStreamController = StreamController.broadcast();
        paymentStreamController.add(_paymentListWs);
      }

      _paymentController.isLoading.value = false;
    } catch (e) {
      dPrint('WaterMyPayments Error: $e');
      _paymentController.isLoading.value = false;
    }
  }

  String getConsumerIdsWs() {
    List fileIds = [];
    for (var connection in _waterController.water!.waterConnection!) {
      if (connection.connectionNo != null) {
        fileIds.add(connection.connectionNo);
      }
    }
    return fileIds.join(', ');
  }

  String getPropertiesIdsWs() {
    List fileIds = [];
    for (var connection in _waterController.water!.waterConnection!) {
      if (connection.propertyId != null) {
        fileIds.add(connection.propertyId);
      }
    }
    return fileIds.join(', ');
  }

  /// Merge all owners name
  String? getOwnersName(List<Owner>? owners) {
    if (owners == null || owners.isEmpty) {
      return null;
    }
    String ownerNames = '';
    for (var owner in owners) {
      ownerNames += '${owner.name}, ';
    }
    return ownerNames.substring(0, ownerNames.length - 2);
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
              () => Text(' (${waterLength.value})'),
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
            padding: const EdgeInsets.all(16.0),
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

                  return ListView.builder(
                    itemCount: paymentList.length,
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemBuilder: (context, index) {
                      final payment = paymentList[index];
                      final waterConnection = _waterController
                          .water?.waterConnection
                          ?.firstWhereOrNull(
                        (element) =>
                            element.connectionNo ==
                            payment.paymentDetails?.firstOrNull?.bill
                                ?.consumerCode,
                      );

                      final property = _propertyTaxController
                          .myProperties?.properties
                          ?.firstWhereOrNull(
                        (element) =>
                            element.propertyId == waterConnection?.propertyId,
                      );

                      return _waterMyPaymentsCard(
                        waterConnection,
                        payment,
                        property,
                        index,
                      );
                    },
                  );
                } else {
                  if (snapshots.connectionState == ConnectionState.waiting ||
                      snapshots.connectionState == ConnectionState.active) {
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

  Widget _waterMyPaymentsCard(
    WaterConnection? waterConnection,
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
                waterConnection: waterConnection,
                payment: payment,
                property: property,
                isLoading: isDownloading[index],
                onPressed: () async {
                  isDownloading[index] = true;

                  var newFileStore = await _fileController.getFiles(
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
                  }
                  isDownloading[index] = false;
                },
              ),
            ),
          ),
        ),
      ).paddingOnly(bottom: 10);

  Widget _subTitleBuildCard({
    required WaterConnection? waterConnection,
    required Payment? payment,
    required Property? property,
    required VoidCallback onPressed,
    required bool isLoading,
  }) =>
      Padding(
        padding: const EdgeInsets.all(10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.CS_PAYMENT_AMOUNT_DUE_WITHOUT_SYMBOL,
                module: Modules.WS,
              ),
              text: payment?.totalAmountPaid != null
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
              text: payment?.paymentDetails?.firstOrNull != null
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
              text: getOwnersName(
                    _propertyTaxController
                        .myProperties?.properties?.first.owners,
                  ) ??
                  'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WS_RECEIPT_NO_LABEL,
                module: Modules.WS,
              ),
              text: payment?.paymentDetails?.first.receiptNumber ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WS_RECEIPT_DATE_LABEL,
                module: Modules.WS,
              ),
              text: payment?.paymentDetails?.first.bill?.billDate
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
            const SizedBox(height: 16),
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
