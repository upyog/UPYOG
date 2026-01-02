import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
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
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class SewerageConnectionApplications extends StatefulWidget {
  const SewerageConnectionApplications({super.key});

  @override
  State<SewerageConnectionApplications> createState() =>
      _SewerageConnectionApplicationsState();
}

class _SewerageConnectionApplicationsState
    extends State<SewerageConnectionApplications> {
  final _authController = Get.find<AuthController>();
  final _waterController = Get.find<WaterController>();
  final _editProfileController = Get.find<EditProfileController>();
  final _paymentController = Get.find<PaymentController>();

  late TenantTenant tenant;

  final _isLoading = false.obs;

  @override
  void initState() {
    super.initState();
    _waterController.setDefaultLimit();
    init();
  }

  init() async {
    _isLoading.value = true;
    _waterController.length.value = 0;
    tenant = await getCityTenant();
    _waterController.getSewerageMyApplications(
      tenantId: tenant.code!,
      mobileNumber: _editProfileController.userProfile.user?.first.mobileNumber,
      token: _authController.token!.accessToken!,
      searchType: "CONNECTION",
    );
    _isLoading.value = false;
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
        businessService: BusinessService.SW_ONE_TIME_FEE,
        cityTenantId: waterConnection.tenantId!,
        module: Modules.SW.name,
        billId: billInfo?.bill?.first.id ?? '',
        totalAmount: '${billInfo?.bill?.first.totalAmount}',
      ),
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
                i18.waterSewerage.MYCONNECTIONS_HEADER,
                module: Modules.WS,
              ),
            ),
            Obx(
              () => Text(' (${_waterController.length})'),
            ),
          ],
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Obx(
          () => _isLoading.value
              ? showCircularIndicator()
              : SingleChildScrollView(
                  child: Padding(
                    padding: const EdgeInsets.all(20.0),
                    child: StreamBuilder(
                      stream: _waterController.sewerageStreamCtrl.stream,
                      builder: (context, snapshots) {
                        if (snapshots.hasData) {
                          if (snapshots.data is String ||
                              snapshots.data == null) {
                            return const NoApplicationFoundWidget();
                          }

                          final Sewerage sewerage = snapshots.data!;

                          sewerage.sewerageConnections?.sort(
                            (a, b) => DateTime.fromMillisecondsSinceEpoch(
                              b.auditDetails?.createdTime ?? 0,
                            ).compareTo(
                              DateTime.fromMillisecondsSinceEpoch(
                                a.auditDetails?.createdTime ?? 0,
                              ),
                            ),
                          );
                          if (isNotNullOrEmpty(sewerage.sewerageConnections)) {
                            return ListView.builder(
                              itemCount:
                                  sewerage.sewerageConnections!.length >= 10
                                      ? sewerage.sewerageConnections!.length + 1
                                      : sewerage.sewerageConnections?.length,
                              shrinkWrap: true,
                              physics: const NeverScrollableScrollPhysics(),
                              itemBuilder: (context, index) {
                                if (index ==
                                        sewerage.sewerageConnections?.length &&
                                    sewerage.sewerageConnections!.length >=
                                        10) {
                                  return Obx(
                                    () {
                                      if (_waterController.isLoading.value) {
                                        return showCircularIndicator();
                                      } else {
                                        return IconButton(
                                          onPressed: () {
                                            _waterController.loadMoreWaterApp(
                                              token: _authController
                                                  .token!.accessToken!,
                                              tenantId: tenant.code!,
                                              mobileNumber:
                                                  _editProfileController
                                                      .userProfile
                                                      .user!
                                                      .first
                                                      .mobileNumber!,
                                              searchType: "CONNECTION",
                                              isSewerage: true,
                                            );
                                          },
                                          icon: const Icon(
                                            Icons.expand_circle_down_outlined,
                                            size: 30,
                                            color: BaseConfig.appThemeColor1,
                                          ),
                                        );
                                      }
                                    },
                                  );
                                } else {
                                  final item =
                                      sewerage.sewerageConnections![index];
                                  return ComplainCard(
                                    title: getLocalizedString(
                                      '${i18.waterSewerage.WS_APPLICATION_TYPE_}${item.applicationType}',
                                      module: Modules.WS,
                                    ),
                                    id: '${getLocalizedString(i18.waterSewerage.MYCONNECTIONS_CONSUMER_NO, module: Modules.WS)}: ${item.connectionNo}',
                                    onTap: () {
                                      Get.toNamed(
                                        AppRoutes
                                            .SEWERAGE_MY_CONNECTION_DETAILS,
                                        arguments: {
                                          'sewerage': item,
                                        },
                                      );
                                    },
                                    status: item.status ?? 'N/A',
                                    statusColor: getStatusColor(
                                      '${item.status}',
                                    ),
                                    statusBackColor: getStatusBackColor(
                                      '${item.status}',
                                    ),
                                  ).paddingOnly(
                                    bottom: 16,
                                  );
                                }
                              },
                            );
                          } else {
                            return const NoApplicationFoundWidget();
                          }
                        } else {
                          if (snapshots.connectionState ==
                                  ConnectionState.waiting ||
                              snapshots.connectionState ==
                                  ConnectionState.active) {
                            return SizedBox(
                              height: Get.height / 2,
                              width: Get.width,
                              child: showCircularIndicator(),
                            );
                          } else if (snapshots.hasError) {
                            return networkErrorPage(
                              context,
                              () => Get.offAndToNamed(
                                AppRoutes.SEWERAGE_MY_CONNECTION,
                              ),
                            );
                          } else {
                            return const SizedBox.shrink();
                          }
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
