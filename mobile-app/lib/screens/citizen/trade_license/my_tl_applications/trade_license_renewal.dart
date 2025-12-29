import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/controller/trade_license_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/trade_license/trade_license.dart'
    as tl;
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/screens/citizen/payments/payment_screen.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class TradeLicenseRenewal extends StatefulWidget {
  const TradeLicenseRenewal({super.key});

  @override
  State<TradeLicenseRenewal> createState() => _TradeLicenseRenewalState();
}

class _TradeLicenseRenewalState extends State<TradeLicenseRenewal> {
  final _authController = Get.find<AuthController>();
  final _tlController = Get.find<TradeLicenseController>();
  final _paymentController = Get.find<PaymentController>();
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _tlController.setDefaultLimit();
    getTlApplication();
  }

  void getTlApplication() async {
    if (!_isLoading) {
      setState(() => _isLoading = true);
    }

    TenantTenant tenant = await getCityTenant();

    await _tlController.getTlApplications(
      token: _authController.token!.accessToken!,
      tenantId: tenant.code,
      renewalTlApp: TradeAppType.RENEWAL.name,
    );

    setState(() => _isLoading = false);
  }

  void goPayment(tl.License license) async {
    if (!_authController.isValidUser) return;
    Get.find<PaymentController>().tlLicenseSelected = license;

    final billInfo = await _paymentController.getPayment(
      token: _authController.token!.accessToken!,
      consumerCode: license.applicationNumber!,
      businessService: BusinessService.TL,
      tenantId: license.tenantId!,
    );

    Get.to(
      () => PaymentScreen(
        token: _authController.token!.accessToken!,
        consumerCode: license.applicationNumber!,
        businessService: BusinessService.TL,
        cityTenantId: license.tenantId!,
        module: Modules.TL.name,
        billId: billInfo?.bill?.first.id ?? '',
        totalAmount: '${billInfo?.bill?.first.totalAmount}',
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        orientation: o,
        onPressed: () {
          Navigator.of(context).pop();
        },
        titleWidget: Wrap(
          children: [
            Text(getLocalizedString(i18.common.TRADE_LICENSE)),
            Obx(
              () => Text(' (${_tlController.length})'),
            ),
          ],
        ),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: StreamBuilder(
            stream: _tlController.streamCtrl.stream,
            builder: (context, AsyncSnapshot snapshot) {
              if (_isLoading) {
                return showCircularIndicator();
              }

              if (snapshot.hasData) {
                if (snapshot.data is String || snapshot.data == null) {
                  return const NoApplicationFoundWidget();
                }

                final tl.TradeLicense tlLicense = snapshot.data;

                tlLicense.licenses?.sort(
                  (a, b) => DateTime.fromMillisecondsSinceEpoch(
                    b.auditDetails?.createdTime ?? 0,
                  ).compareTo(
                    DateTime.fromMillisecondsSinceEpoch(
                      a.auditDetails?.createdTime ?? 0,
                    ),
                  ),
                );

                if (isNotNullOrEmpty(tlLicense.licenses)) {
                  return SingleChildScrollView(
                    physics: const AlwaysScrollableScrollPhysics(),
                    child: ListView.builder(
                      itemCount: tlLicense.licenses!.length >= 10
                          ? tlLicense.licenses!.length + 1
                          : tlLicense.licenses?.length,
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemBuilder: (context, index) {
                        if (index == tlLicense.licenses?.length &&
                            tlLicense.licenses!.length >= 10) {
                          return Obx(() {
                            if (_tlController.isLoading.value) {
                              return showCircularIndicator();
                            } else {
                              return IconButton(
                                onPressed: () async {
                                  TenantTenant tenant = await getCityTenant();
                                  await _tlController.loadMoreTlApp(
                                    token: _authController.token!.accessToken!,
                                    tenantId: tenant.code!,
                                    renewalTlApp: TradeAppType.RENEWAL.name,
                                  );
                                },
                                icon: const Icon(
                                  Icons.expand_circle_down_outlined,
                                  size: 30,
                                  color: BaseConfig.appThemeColor1,
                                ),
                              );
                            }
                          });
                        } else {
                          final license = tlLicense.licenses![index];
                          return ListTile(
                            contentPadding: EdgeInsets.zero,
                            subtitle: _subTitleBuildCard(
                              license: license,
                            ),
                          );
                        }
                      },
                    ),
                  );
                } else {
                  return const NoApplicationFoundWidget();
                }
                //return _buildTlApplication(tlLicense);
              } else if (snapshot.hasError) {
                return networkErrorPage(
                  context,
                  () => getTlApplication(),
                );
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
          ),
        ),
      ),
    );
  }

  Widget _subTitleBuildCard({
    required tl.License license,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        ComplainCard(
          title: license.tradeName?.capitalizeFirst ??
              getLocalizedString(i18.common.NA),
          id: '${getLocalizedString(i18.tradeLicense.APPLICATION_NO, module: Modules.TL)}: ${license.applicationNumber}',
          date: 'Date: ${license.applicationDate.toCustomDateFormat()}',
          onTap: () => Get.toNamed(AppRoutes.TL_DETAILS, arguments: license),
          status: isNotNullOrEmpty(license.status)
              ? getLocalizedString(
                  'WF_${license.workflowCode}_${license.status}'.toUpperCase(),
                  module: Modules.TL,
                )
              : getLocalizedString(i18.common.NA),
          statusColor: getStatusColor('${license.status}'),
          statusBackColor: getStatusBackColor('${license.status}'),
        ),
      ],
    );
  }
}
