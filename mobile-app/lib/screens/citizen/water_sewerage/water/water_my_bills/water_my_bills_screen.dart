import 'dart:async';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/controller/water_controller.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class WsMyBillsScreen extends StatefulWidget {
  const WsMyBillsScreen({super.key});

  @override
  State<WsMyBillsScreen> createState() => _WsMyBillsScreenState();
}

class _WsMyBillsScreenState extends State<WsMyBillsScreen> {
  final _authController = Get.find<AuthController>();
  final _waterController = Get.find<WaterController>();
  final _editProfileController = Get.find<EditProfileController>();
  final _paymentController = Get.find<PaymentController>();

  final billStreamController = StreamController.broadcast();

  BillInfo? wsBillInfo;
  List<Bill>? filteredBills;

  var billLength = 0.obs;

  @override
  void initState() {
    super.initState();
    init();
  }

  void init() async {
    try {
      billLength.value = 0;
      final TenantTenant tenant = await getCityTenant();
      final tenantId = tenant.code!;
      final token = _authController.token?.accessToken!;

      await _waterController.getWaterMyApplications(
        tenantId: tenant.code!,
        mobileNumber: _editProfileController.user.mobileNumber!,
        token: token!,
        searchType: SearchType.CONNECTION.name,
      );

      if (isNotNullOrEmpty(_waterController.water?.waterConnection)) {
        _paymentController.isLoading.value = true;
        final consumerIds = getConsumerIds();

        wsBillInfo = await _paymentController.getPayment(
          token: token,
          consumerCode: consumerIds,
          businessService: BusinessService.WS,
          tenantId: tenantId,
        );
      }

      filteredBills = wsBillInfo?.bill?.where((bill) {
        return _waterController.water?.waterConnection?.any(
              (connection) =>
                  connection.connectionNo == bill.consumerCode &&
                  isNotNullOrEmpty(bill.totalAmount),
            ) ??
            false;
      }).toList();

      billLength.value = filteredBills?.length ?? 0;

      if (!billStreamController.isClosed) {
        billStreamController.add(filteredBills);
      }

      _paymentController.isLoading.value = false;
    } catch (e) {
      print("WS Error: $e");
    }
  }

  String getConsumerIds() {
    List fileIds = [];
    for (var connection in _waterController.water!.waterConnection!) {
      if (connection.connectionNo != null) {
        fileIds.add(connection.connectionNo);
      }
    }
    return fileIds.join(', ');
  }

  @override
  void dispose() {
    super.dispose();
    billStreamController.close();
  }

  void goPayment(Bill bill) async {
    Get.toNamed(
      AppRoutes.BILL_DETAIL_SCREEN,
      arguments: {
        'billData': bill,
        'module': Modules.WS,
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Wrap(
          children: [
            Text(
              getLocalizedString(
                i18.waterSewerage.MY_BILLS,
                module: Modules.WS,
              ),
            ),
            Obx(
              () => Text(' (${billLength.value})'),
            ),
          ],
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: isNotNullOrEmpty(_waterController.water?.waterConnection)
          ? SizedBox(
              height: Get.height,
              width: Get.width,
              child: SingleChildScrollView(
                child: Padding(
                  padding: const EdgeInsets.all(20.0),
                  child: StreamBuilder(
                    stream: billStreamController.stream,
                    builder: (context, snapshots) {
                      if (snapshots.hasData && snapshots.data != null) {
                        if (snapshots.data is String ||
                            snapshots.data == null) {
                          return const NoApplicationFoundWidget();
                        }
                        final List<Bill>? bills = snapshots.data;

                        if (!isNotNullOrEmpty(bills)) {
                          return const NoApplicationFoundWidget();
                        }

                        bills?.sort(
                          (a, b) => DateTime.fromMillisecondsSinceEpoch(
                            b.auditDetails!.lastModifiedTime!,
                          ).compareTo(
                            DateTime.fromMillisecondsSinceEpoch(
                              a.auditDetails!.lastModifiedTime!,
                            ),
                          ),
                        );

                        return Obx(
                          () => _paymentController.isLoading.value
                              ? SizedBox(
                                  height: Get.height * 0.8,
                                  width: Get.width,
                                  child: showCircularIndicator(),
                                )
                              : ListView.builder(
                                  itemCount: bills!.length,
                                  shrinkWrap: true,
                                  physics: const NeverScrollableScrollPhysics(),
                                  itemBuilder: (context, index) {
                                    if (bills.isNotEmpty) {
                                      final bill = bills[index];
                                      return _wsMyBillsCard(bill);
                                    } else {
                                      return const NoApplicationFoundWidget();
                                    }
                                  },
                                ),
                        );
                      } else {
                        if (snapshots.connectionState ==
                            ConnectionState.waiting) {
                          return SizedBox(
                            height: Get.height * 0.8,
                            width: Get.width,
                            child: showCircularIndicator(),
                          );
                        } else if (snapshots.connectionState ==
                            ConnectionState.active) {
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
            )
          : const NoApplicationFoundWidget(),
    );
  }

  Widget _wsMyBillsCard(
    Bill bill,
  ) =>
      Card(
        child: Padding(
          padding: const EdgeInsets.all(10),
          child: ListTile(
            contentPadding: EdgeInsets.zero,
            subtitle: _subTitleBuildCard(
              bill: bill,
              onPressed: () {
                //TODO: Navigate to Bill Details Screen
                goPayment(bill);
              },
            ),
          ),
        ),
      ).paddingOnly(bottom: 10);

  Widget _subTitleBuildCard({
    required Bill bill,
    required VoidCallback onPressed,
  }) =>
      Padding(
        padding: const EdgeInsets.all(10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WNS_AMOUNT_DUE,
                module: Modules.WS,
              ),
              text: bill.totalAmount != null
                  ? '₹ ${bill.totalAmount?.toInt()}'
                  : 'N/A',
              fontWeight: FontWeight.bold,
              textSize: 16,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WNS_SERVICE,
                module: Modules.WS,
              ),
              text: bill.businessService != null
                  ? getLocalizedString(
                      '${i18.waterSewerage.WNS_SERVICE_TYPE}${bill.businessService!}',
                      module: Modules.WS,
                    )
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WNS_CONSUMER_NO,
                module: Modules.WS,
              ),
              text: bill.consumerCode ?? 'N/A',
            ),
            const SizedBox(height: 10),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                MediumText(
                  text: getLocalizedString(
                    i18.waterSewerage.WNS_BILLING_PERIOD,
                    module: Modules.WS,
                  ),
                  fontWeight: FontWeight.bold,
                ),
                Wrap(
                  children: [
                    MediumText(
                      text: bill.billDetails?.first.fromPeriod != null
                          ? 'FY ${bill.billDetails!.first.fromPeriod.toCustomDateFormat(pattern: 'yyyy')!}'
                          : '',
                    ),
                    const MediumText(
                      text: '-',
                    ),
                    MediumText(
                      text: bill.billDetails?.first.fromPeriod
                              .toCustomDateFormat(pattern: 'yyyy')
                              .toString() ??
                          '',
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WNS_CONSUMER_NAME,
                module: Modules.WS,
              ),
              text: bill.payerName ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WNS_ADDRESS,
                module: Modules.WS,
              ),
              text: bill.payerAddress ?? "N/A",
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WNS_BILL_DUE_DATE,
                module: Modules.WS,
              ),
              text: bill.billDate.toCustomDateFormat(pattern: 'dd/MM/yyyy') ??
                  'N/A', //'24/5/2024',
            ),
            const SizedBox(height: 20),
            FilledButtonApp(
              width: Get.width,
              text: getLocalizedString(
                i18.waterSewerage.WNS_VIEW_DETAILS_AND_PAY,
                module: Modules.WS,
              ),
              onPressed: onPressed,
            ),
          ],
        ),
      );
}
