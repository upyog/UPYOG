import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/controller/water_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/water_sewerage/sewerage.dart';
import 'package:mobile_app/model/citizen/water_sewerage/water.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/screens/citizen/payments/payment_screen.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class WsViewAllScreen extends StatefulWidget {
  const WsViewAllScreen({super.key});

  @override
  State<WsViewAllScreen> createState() => _WsViewAllScreenState();
}

class _WsViewAllScreenState extends State<WsViewAllScreen> {
  final _authController = Get.find<AuthController>();
  final _waterController = Get.find<WaterController>();
  final _editProfileController = Get.find<EditProfileController>();
  final _paymentController = Get.find<PaymentController>();

  var totalLength = 0.obs;
  final List<dynamic> _swList = <dynamic>[];

  @override
  void initState() {
    super.initState();
    init();
  }

  void init() async {
    TenantTenant tenant = await getCityTenant();
    _waterController.isLoading.value = true;

    // Fetch water applications
    var ws = await _waterController.getWaterMyApplicationsFuture(
      tenantId: tenant.code!,
      mobileNumber:
          _editProfileController.userProfile.user!.first.mobileNumber!,
      token: _authController.token!.accessToken!,
    );

    // Fetch sewerage applications
    var sw = await _waterController.getSewerageMyApplicationsFuture(
      tenantId: tenant.code!,
      mobileNumber:
          _editProfileController.userProfile.user!.first.mobileNumber!,
      token: _authController.token!.accessToken!,
    );

    // Combine both lists into one
    _swList.addAll(ws?.waterConnection ?? []);
    _swList.addAll(sw?.sewerageConnections ?? []);

    // Update the total length
    totalLength.value = (ws?.totalCount ?? 0) + (sw?.totalCount ?? 0);

    _waterController.isLoading.value = false;
  }

  void goPayment(WaterConnection waterConnection) async {
    if (!_authController.isValidUser) return;

    final billInfo = await _paymentController.getPayment(
      token: _authController.token!.accessToken!,
      consumerCode: waterConnection.applicationNo!,
      businessService: BusinessService.WS_ONE_TIME_FEE,
      tenantId: waterConnection.tenantId!,
    );

    Get.to(
      () => PaymentScreen(
        token: _authController.token!.accessToken!,
        consumerCode: waterConnection.applicationNo!,
        businessService: BusinessService.WS_ONE_TIME_FEE,
        cityTenantId: waterConnection.tenantId!,
        module: Modules.WS.name,
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
        titleWidget: Wrap(
          children: [
            Text(
              getLocalizedString(
                i18.waterSewerage.HOME_MY_APPLICATION,
              ),
            ),
            Obx(
              () => Text(' (${totalLength.value})'),
            ),
          ],
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: SingleChildScrollView(
            child: Obx(
              () => _waterController.isLoading.value
                  ? SizedBox(
                      height: Get.height * 0.8,
                      child: showCircularIndicator(),
                    )
                  : ListView.builder(
                      itemCount: _swList.length,
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemBuilder: (context, index) {
                        final item = _swList[index];

                        if (item is WaterConnection) {
                          return ComplainCard(
                            title: getLocalizedString(
                              '${i18.waterSewerage.WS_APPLICATION_TYPE_}${item.applicationType}',
                              module: Modules.WS,
                            ),
                            id: 'Application No: ${item.applicationNo}',
                            date:
                                'Date: ${item.auditDetails!.createdTime.toCustomDateFormat()}',
                            onTap: () {
                              Get.toNamed(
                                AppRoutes.WATER_MY_APPLICATIONS_DETAILS,
                                arguments: {
                                  'water': item,
                                },
                              );
                            },
                            status: item.status ?? 'N/A',
                            statusColor: getStatusColor('${item.status}'),
                            statusBackColor:
                                getStatusBackColor('${item.status}'),
                          );
                        } else if (item is SewerageConnection) {
                          return ComplainCard(
                            title: getLocalizedString(
                              '${i18.waterSewerage.WS_APPLICATION_TYPE_}${item.applicationType}',
                              module: Modules.WS,
                            ),
                            id: '${getLocalizedString(i18.waterSewerage.APPLICATION_NO, module: Modules.WS)}: ${item.applicationNo}',
                            date:
                                'Date: ${item.auditDetails!.createdTime!.toCustomDateFormat()}',
                            onTap: () {
                              Get.toNamed(
                                AppRoutes.SEWERAGE_MY_APPLICATIONS_DETAILS,
                                arguments: {
                                  'sewerage': item,
                                },
                              );
                            },
                            status: item.status ??
                                getLocalizedString(i18.common.NA),
                            statusColor: getStatusColor('${item.status}'),
                            statusBackColor:
                                getStatusBackColor('${item.status}'),
                          );
                        } else {
                          return const Center(
                            child: NoApplicationFoundWidget(),
                          );
                        }
                      },
                    ),
            ),
          ),
        ),
      ),
    );
  }
}
