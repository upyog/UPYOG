import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/misc_controller.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class MyChallans extends StatefulWidget {
  const MyChallans({super.key});

  @override
  State<MyChallans> createState() => _MyChallansState();
}

class _MyChallansState extends State<MyChallans> {
  final _authController = Get.find<AuthController>();
  final _miscController = Get.put(MiscController());
  final _profileController = Get.find<EditProfileController>();
  final _commonController = Get.find<CommonController>();

  var ucModuleLoaded = false.obs;

  @override
  void initState() {
    _miscController.setDefaultLimit();
    super.initState();
    init();
  }

  void init() async {
    await _commonController.fetchLabels(modules: Modules.UC).then((_) {
      ucModuleLoaded.value = true;
    });
    getChallans();
  }

  Future<void> getChallans() async {
    try {
      _miscController.getChallans(
        token: _authController.token!.accessToken!,
        mobileNo: _profileController.user.mobileNumber,
        tenantId: BaseConfig.STATE_TENANT_ID,
      );
    } catch (e) {
      dPrint('getChallans Screen Error: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, o) {
        return Scaffold(
          appBar: HeaderTop(
            orientation: o,
            onPressed: () {
              Navigator.of(context).pop();
            },
            titleWidget: Obx(
              () => ucModuleLoaded.value
                  ? Wrap(
                      children: [
                        Text(
                          getLocalizedString(
                            i18.challans.MY_CHALLAN,
                            module: Modules.UC,
                          ),
                        ),
                        Obx(
                          () =>
                              Text(' (${_miscController.totalChallans.value})'),
                        ),
                      ],
                    )
                  : const SizedBox.shrink(),
            ),
          ),
          body: SizedBox(
            height: Get.height,
            width: Get.width,
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: StreamBuilder(
                stream: _miscController.streamCtrl.stream,
                builder: (context, AsyncSnapshot snapshot) {
                  if (snapshot.hasData) {
                    if (snapshot.data is String || snapshot.data == null) {
                      return const NoApplicationFoundWidget();
                    }

                    final BillInfo billInfo = snapshot.data;

                    if (isNotNullOrEmpty(billInfo.bills)) {
                      return Obx(
                        () => !ucModuleLoaded.value
                            ? showCircularIndicator()
                            : SingleChildScrollView(
                                physics: AppPlatforms.platformPhysics(),
                                child: ListView.builder(
                                  itemCount: billInfo.bills!.length >= 10
                                      ? billInfo.bills!.length + 1
                                      : billInfo.bills!.length,
                                  shrinkWrap: true,
                                  physics: const NeverScrollableScrollPhysics(),
                                  itemBuilder: (context, index) {
                                    if (index == billInfo.bills!.length &&
                                        billInfo.bills!.length >= 10) {
                                      return Obx(() {
                                        if (_miscController.isLoading.value) {
                                          return showCircularIndicator();
                                        } else {
                                          return IconButton(
                                            onPressed: () async {
                                              await _miscController.loadMore(
                                                token: _authController
                                                    .token!.accessToken!,
                                                mobileNo: _profileController
                                                    .user.mobileNumber,
                                                tenantId:
                                                    BaseConfig.STATE_TENANT_ID,
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
                                      final bill = billInfo.bills![index];
                                      return ListTile(
                                        contentPadding: EdgeInsets.zero,
                                        subtitle: _subTitleBuildCard(
                                          bill: bill,
                                        ),
                                      );
                                    }
                                  },
                                ),
                              ),
                      );
                    } else {
                      return const NoApplicationFoundWidget();
                    }
                    //return _buildTlApplication(tlLicense);
                  } else if (snapshot.hasError) {
                    return networkErrorPage(
                      context,
                      () => getChallans(),
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
      },
    );
  }

  Widget _subTitleBuildCard({
    required Bill? bill,
  }) {
    final bizTitle = bill?.businessService?.split('.').lastOrNull ?? '';
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        ComplainCard(
          title:
              '₹ ${bill?.totalAmount ?? 0}', // getLocalizedString(bizTitle, module: Modules.UC),
          id: '${getLocalizedString(i18.challans.CHALLAN_NO, module: Modules.UC)} : ${bill?.consumerCode ?? 'N/A'}',
          subtext:
              '${getLocalizedString(i18.challans.SERVICE_CATEGORY, module: Modules.UC)}: ${getLocalizedString(bizTitle, module: Modules.UC)}',
          name:
              '${getLocalizedString(i18.challans.OWNER_NAME, module: Modules.UC)}: ${bill?.payerName?.capitalize ?? 'N/A'}',

          date:
              '${getLocalizedString(i18.challans.DUE_DATE, module: Modules.UC)}: ${bill?.billDate?.toCustomDateFormat(pattern: 'd/MM/yyyy')}',
          date2:
              '${getLocalizedString(i18.challans.BILLING_PERIOD, module: Modules.UC)}: ${bill?.billDetails?.firstOrNull?.fromPeriod.toCustomDateFormat()} - ${bill?.billDetails?.firstOrNull?.toPeriod.toCustomDateFormat()}',
          onTap: () => Get.toNamed(
            AppRoutes.BILL_DETAIL_SCREEN,
            arguments: {'billData': bill, 'module': Modules.UC},
          ),
          isShowViewDetails: bill?.status == ApplicationStatus.ACTIVE.name,
          status: isNotNullOrEmpty(bill?.status)
              ? bill!.status!
              : getLocalizedString(i18.common.NA),
          statusColor: getStatusColor('${bill?.status}'),
          statusBackColor: getStatusBackColor('${bill?.status}'),
        ),
      ],
    );
  }
}
