import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/bpa_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/model/citizen/bpa_model/bpa_model.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class BpaPermitApplications extends StatefulWidget {
  const BpaPermitApplications({super.key});

  @override
  State<BpaPermitApplications> createState() => _BpaPermitApplicationsState();
}

class _BpaPermitApplicationsState extends State<BpaPermitApplications> {
  final _authController = Get.find<AuthController>();
  final _bpaController = Get.find<BpaController>();
  final _editProfileController = Get.find<EditProfileController>();

  @override
  void initState() {
    super.initState();
    _bpaController.setDefaultLimit();
    getBpaAppFun();
  }

  Future<void> getBpaAppFun() async {
    final tenant = await getCityTenant();
    final accessToken = _authController.token?.accessToken;

    if (accessToken != null) {
      await _bpaController.getBpaApplications(
        token: accessToken,
        applicationType: BpaAppType.BUILDING_PLAN_SCRUTINY.name,
        tenantId: tenant.code!,
        mobileNumber:
            _editProfileController.userProfile.user?.first.mobileNumber,
      );
    } else {
      dPrint("AccessToken or MobileNumber is null");
    }
  }

  //   void goPayment(BpaElement bpaEle) async {
//     if (!_authController.isValidUser) return;

//     final billInfo = await _paymentController.getPayment(
//       token: _authController.token!.accessToken!,
//       consumerCode: bpaEle.applicationNo!,
//       businessService: getBpaServiceStatus(bpaEle.status!),
//       tenantId: bpaEle.tenantId!,
//     );

//     Get.to(
//       () => PaymentScreen(
//         token: _authController.token!.accessToken!,
//         consumerCode: bpaEle.applicationNo!,
//         businessService: getBpaServiceStatus(bpaEle.status!),
//         cityTenantId: bpaEle.tenantId!,
//         module: Modules.BPA.name,
//         billId: billInfo?.bill?.first.id ?? '',
//         totalAmount: '${billInfo?.bill?.first.totalAmount}',
//       ),
//     );
//   }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: StreamBuilder(
          stream: _bpaController.streamCtrl.stream,
          builder: (context, AsyncSnapshot snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return showCircularIndicator();
            } else if (snapshot.hasError) {
              return networkErrorPage(context, () => getBpaAppFun());
            } else if (!snapshot.hasData || snapshot.data is String) {
              return const NoApplicationFoundWidget();
            } else {
              final Bpa bpaData = snapshot.data;
              return isNotNullOrEmpty(bpaData.bpaele)
                  ? BpaListView(
                      bpaData: bpaData,
                      bpaController: _bpaController,
                      token: _authController.token!.accessToken!,
                      mobileNumber: _editProfileController
                              .userProfile.user?.first.mobileNumber ??
                          '',
                    )
                  : const NoApplicationFoundWidget();
            }
          },
        ),
      ),
    );
  }

  HeaderTop _buildAppBar() {
    return HeaderTop(
      titleWidget: Wrap(
        children: [
          const Text("Permit Applications"),
          Obx(() => Text(' (${_bpaController.lengthBpa})')),
        ],
      ),
      onPressed: () => Navigator.of(context).pop(),
    );
  }
}

class BpaListView extends StatelessWidget {
  final Bpa bpaData;
  final BpaController bpaController;
  final String token, mobileNumber;

  const BpaListView({
    super.key,
    required this.bpaData,
    required this.bpaController,
    required this.token,
    required this.mobileNumber,
  });

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: bpaData.bpaele!.length >= 10
          ? bpaData.bpaele!.length + 1
          : bpaData.bpaele!.length,
      shrinkWrap: true,
      physics: const BouncingScrollPhysics(),
      itemBuilder: (context, index) {
        if (index == bpaData.bpaele!.length && bpaData.bpaele!.length >= 10) {
          return Obx(() {
            return bpaController.isLoading.value
                ? showCircularIndicator()
                : IconButton(
                    onPressed: () async {
                      final tenant = await getCityTenant();
                      await bpaController.loadMoreBpaApp(
                        token: token,
                        applicationType: BpaAppType.BUILDING_PLAN_SCRUTINY.name,
                        tenantId: tenant.code!,
                        mobileNumber: mobileNumber,
                      );
                    },
                    icon: const Icon(
                      Icons.expand_circle_down_outlined,
                      size: 30,
                      color: BaseConfig.appThemeColor1,
                    ),
                  );
          });
        } else {
          final newBpaData = bpaData.bpaele![index];
          return BpaApplicationCard(newBpaData: newBpaData);
        }
      },
    );
  }
}

class BpaApplicationCard extends StatelessWidget {
  final BpaElement newBpaData;

  const BpaApplicationCard({super.key, required this.newBpaData});

  @override
  Widget build(BuildContext context) {
    return ComplainCard(
      title: getLocalizedString(
        newBpaData.additionalDetails?.applicationType ?? "N/A",
      ),
      id: 'Application ID: ${newBpaData.applicationNo}',
      date:
          'Application Date: ${newBpaData.auditDetails?.createdTime.toCustomDateFormat()}',
      onTap: () {
        Get.toNamed(
          AppRoutes.BUILDING_APPLICATION_DETAILS,
          arguments: {'newBpaData': newBpaData},
        );
      },
      status: getLocalizedString(
        '${i18.building.BPA_APP_STATUS_PREF}${newBpaData.status}',
        module: Modules.BPA,
      ),
      statusColor: getStatusColor(newBpaData.status!),
      statusBackColor: getStatusBackColor(newBpaData.status!),
    ).paddingOnly(bottom: 16);
  }
}
