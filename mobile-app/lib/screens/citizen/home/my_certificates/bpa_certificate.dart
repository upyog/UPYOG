import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/bpa_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/model/citizen/bpa_model/bpa_model.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/screens/citizen/payments/payment_screen.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';

class BpaCertificate extends StatefulWidget {
  const BpaCertificate({super.key});

  @override
  State<BpaCertificate> createState() => _BpaCertificateState();
}

class _BpaCertificateState extends State<BpaCertificate> {
  final _commonController = Get.find<CommonController>();
  final _authController = Get.find<AuthController>();
  final _bpaController = Get.find<BpaController>();
  final _editProfileController = Get.find<EditProfileController>();
  final _downloadController = Get.find<DownloadController>();
  final _fileController = Get.find<FileController>();
  final _paymentController = Get.find<PaymentController>();

  late TenantTenant tenant;

  @override
  void initState() {
    _bpaController.setDefaultLimit();
    super.initState();
    _fetchLabelsAsync();
    getBpaAppFun();
  }

  Future<void> _fetchLabelsAsync() async {
    await _commonController.fetchLabels(modules: Modules.BPA);
  }

  Future<void> getBpaAppFun() async {
    tenant = await getCityTenant();
    final accessToken = _authController.token?.accessToken;

    if (accessToken != null) {
      await _bpaController.getBpaApplications(
        token: accessToken,
        applicationType: BpaAppType.BUILDING_PLAN_SCRUTINY.name,
        tenantId: tenant.code!,
        mobileNumber:
            _editProfileController.userProfile.user?.first.mobileNumber,
        isCertificate: true,
      );
    } else {
      dPrint("AccessToken or MobileNumber is null");
    }
  }

  void goPayment(BpaElement bpaEle) async {
    if (!_authController.isValidUser) return;

    final billInfo = await _paymentController.getPayment(
      token: _authController.token!.accessToken!,
      consumerCode: bpaEle.applicationNo!,
      businessService: getBpaServiceStatus(bpaEle.status!),
      tenantId: bpaEle.tenantId!,
    );

    Get.to(
      () => PaymentScreen(
        token: _authController.token!.accessToken!,
        consumerCode: bpaEle.applicationNo!,
        businessService: getBpaServiceStatus(bpaEle.status!),
        cityTenantId: bpaEle.tenantId!,
        module: Modules.BPA.name,
        billId: billInfo?.bill?.first.id ?? '',
        totalAmount: '${billInfo?.bill?.first.totalAmount}',
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, o) {
        return Scaffold(
          appBar: HeaderTop(
            orientation: o,
            titleWidget: Wrap(
              children: [
                const Text("My Certificates"),
                Obx(
                  () => Text(' (${_bpaController.lengthBpa})'),
                ),
              ],
            ),
            onPressed: () {
              Navigator.of(context).pop();
            },
          ),
          body: SizedBox(
            height: Get.height,
            width: Get.width,
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: SingleChildScrollView(
                child: StreamBuilder(
                  stream: _bpaController.streamCtrl.stream,
                  builder: (context, AsyncSnapshot snapshot) {
                    if (snapshot.hasData) {
                      if (snapshot.data is String || snapshot.data == null) {
                        return const NoApplicationFoundWidget();
                      }
                      Bpa bpaData = snapshot.data;

                      if (!isNotNullOrEmpty(bpaData.bpaele)) {
                        return const NoApplicationFoundWidget();
                      }

                      if (bpaData.bpaele!.isNotEmpty) {
                        return ListView.builder(
                          itemCount: bpaData.bpaele!.length,
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          itemBuilder: (context, index) {
                            final bpaItem = bpaData.bpaele![index];
                            return Container(
                              width: Get.width,
                              padding: EdgeInsets.only(bottom: 16.h),
                              child: Card(
                                elevation: 3,
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(12.r),
                                  side: BorderSide(
                                    color: BaseConfig.borderColor,
                                    width: 1.w,
                                  ),
                                ),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            SizedBox(
                                              width: Get.width * 0.5,
                                              child: MediumTextNotoSans(
                                                text: 'Permit Order',
                                                fontWeight: FontWeight.w700,
                                                size: o == Orientation.portrait
                                                    ? 14.sp
                                                    : 8.sp,
                                                maxLine: 2,
                                                textOverflow:
                                                    TextOverflow.ellipsis,
                                              ),
                                            ),
                                            TextButtonNotoSans(
                                              text: '',
                                              padding: EdgeInsets.symmetric(
                                                horizontal: 4.w,
                                                vertical: 2.h,
                                              ),
                                              fontSize:
                                                  o == Orientation.portrait
                                                      ? 12.sp
                                                      : 7.sp,
                                              onPressed: () async {
                                                final pdfFileStoreId =
                                                    await _fileController
                                                        .getPdfServiceFile(
                                                  tenantId: tenant.code!,
                                                  token: _authController
                                                      .token!.accessToken!,
                                                  bpaElement: bpaItem,
                                                  key: getPdfKeyByServiceBPA(
                                                    service: bpaItem
                                                        .businessService!,
                                                  ),
                                                );
                                                if (pdfFileStoreId != null) {
                                                  var newFileStore =
                                                      await _fileController
                                                          .getFiles(
                                                    fileStoreIds:
                                                        pdfFileStoreId,
                                                    tenantId: tenant.code!,
                                                    token: _authController
                                                        .token!.accessToken!,
                                                  );
                                                  if (isNotNullOrEmpty(
                                                    newFileStore,
                                                  )) {
                                                    await _downloadController
                                                        .starFileDownload(
                                                      url: newFileStore!
                                                          .fileStoreIds![0]
                                                          .url!,
                                                      title: 'Permit Order',
                                                    );
                                                  }
                                                }
                                              },
                                              icon: const Icon(
                                                Icons.download,
                                                color:
                                                    BaseConfig.appThemeColor1,
                                              ),
                                            ),
                                          ],
                                        ),
                                        const Divider(
                                          color: BaseConfig.borderColor,
                                        ),
                                        SizedBox(height: 4.h),
                                        SmallSelectableTextNotoSans(
                                          text: bpaItem.applicationNo ?? "N/A",
                                          fontWeight: FontWeight.w400,
                                          size: o == Orientation.portrait
                                              ? 12.sp
                                              : 7.sp,
                                        ),
                                      ],
                                    ).paddingAll(
                                      o == Orientation.portrait ? 16.w : 8.w,
                                    ),
                                  ],
                                ),
                              ),
                            );
                          },
                        );
                      } else {
                        return const NoApplicationFoundWidget();
                      }
                    } else if (snapshot.hasError) {
                      return networkErrorPage(
                        context,
                        () => getBpaAppFun(),
                      );
                    } else {
                      switch (snapshot.connectionState) {
                        case ConnectionState.waiting:
                        case ConnectionState.active:
                          return SizedBox(
                            height: Get.height * 0.8,
                            width: Get.width,
                            child: showCircularIndicator(),
                          );
                        default:
                          return const SizedBox.shrink();
                      }
                    }
                  },
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}
