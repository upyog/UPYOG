import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/water_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/water_sewerage/sewerage.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';

class SewerageMyCertificates extends StatefulWidget {
  const SewerageMyCertificates({super.key});

  @override
  State<SewerageMyCertificates> createState() => _SewerageMyCertificatesState();
}

class _SewerageMyCertificatesState extends State<SewerageMyCertificates> {
  final _wtController = Get.find<WaterController>();
  final _authController = Get.find<AuthController>();
  final _editProfileController = Get.find<EditProfileController>();
  final _fileController = Get.find<FileController>();
  final _downloadController = Get.find<DownloadController>();

  late TenantTenant tenant;

  @override
  void initState() {
    init();
    super.initState();
  }

  void init() async {
    tenant = await getCityTenant();
    _wtController.isLoading.value = true;
    await _wtController.getSewerageMyApplicationsFuture(
      isCertificate: true,
      tenantId: tenant.code!,
      token: _authController.token!.accessToken!,
      mobileNumber:
          _editProfileController.userProfile.user!.first.mobileNumber!,
    );
    _wtController.isLoading.value = false;
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
            title: "My Certificates",
          ),
          body: SizedBox(
            height: Get.height,
            width: Get.width,
            child: SingleChildScrollView(
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: StreamBuilder(
                  stream: _wtController.sewerageStreamCtrl.stream,
                  builder: (context, snapshots) {
                    if (snapshots.hasData) {
                      if (snapshots.data is String || snapshots.data == null) {
                        return Center(
                          child: Text(
                            getLocalizedString(i18.inbox.NO_APPLICATION),
                          ),
                        );
                      }
                      final Sewerage sewerage = snapshots.data;

                      if (!isNotNullOrEmpty(sewerage.sewerageConnections)) {
                        return const NoApplicationFoundWidget();
                      }

                      sewerage.sewerageConnections?.sort(
                        (a, b) => DateTime.fromMillisecondsSinceEpoch(
                          b.auditDetails!.createdTime!,
                        ).compareTo(
                          DateTime.fromMillisecondsSinceEpoch(
                            a.auditDetails!.createdTime!,
                          ),
                        ),
                      );

                      return Obx(
                        () => _wtController.isLoading.value
                            ? SizedBox(
                                height: Get.height * 0.8,
                                width: Get.width,
                                child: showCircularIndicator(),
                              )
                            : ListView.builder(
                                itemCount: sewerage.sewerageConnections!.length,
                                shrinkWrap: true,
                                physics: const NeverScrollableScrollPhysics(),
                                itemBuilder: (context, index) {
                                  final sewerageConnection =
                                      sewerage.sewerageConnections![index];
                                  return Container(
                                    width: Get.width,
                                    padding: EdgeInsets.only(bottom: 16.h),
                                    child: Card(
                                      elevation: 3,
                                      shape: RoundedRectangleBorder(
                                        borderRadius:
                                            BorderRadius.circular(12.r),
                                        side: BorderSide(
                                          color: BaseConfig.borderColor,
                                          width: 1.w,
                                        ),
                                      ),
                                      child: Column(
                                        crossAxisAlignment:
                                            CrossAxisAlignment.start,
                                        children: [
                                          Column(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.start,
                                            children: [
                                              Row(
                                                mainAxisAlignment:
                                                    MainAxisAlignment
                                                        .spaceBetween,
                                                children: [
                                                  SizedBox(
                                                    width: Get.width * 0.5,
                                                    child: MediumTextNotoSans(
                                                      text: getLocalizedString(
                                                        i18.waterSewerage
                                                            .SANCTION_LETTER,
                                                        module: Modules.WS,
                                                      ),
                                                      fontWeight:
                                                          FontWeight.w700,
                                                      size: o ==
                                                              Orientation
                                                                  .portrait
                                                          ? 14.sp
                                                          : 8.sp,
                                                      maxLine: 2,
                                                      textOverflow:
                                                          TextOverflow.ellipsis,
                                                    ),
                                                  ),
                                                  TextButtonNotoSans(
                                                    text: '',
                                                    padding:
                                                        EdgeInsets.symmetric(
                                                      horizontal: 4.w,
                                                      vertical: 2.h,
                                                    ),
                                                    fontSize: o ==
                                                            Orientation.portrait
                                                        ? 12.sp
                                                        : 7.sp,
                                                    onPressed: () async {
                                                      if (!isNotNullOrEmpty(
                                                        sewerageConnection
                                                            .additionalDetails!
                                                            .sanctionFileStoreId,
                                                      )) {
                                                        return snackBar(
                                                          "Error",
                                                          "File Not Found",
                                                          BaseConfig.redColor,
                                                        );
                                                      }
                                                      final ids =
                                                          await _fileController
                                                              .getFiles(
                                                        tenantId: tenant.code!,
                                                        token: _authController
                                                            .token!
                                                            .accessToken!,
                                                        fileStoreIds:
                                                            sewerageConnection
                                                                .additionalDetails!
                                                                .sanctionFileStoreId!,
                                                      );
                                                      var fileId = ids
                                                          ?.fileStoreIds
                                                          ?.firstOrNull
                                                          ?.url;

                                                      if (!isNotNullOrEmpty(
                                                        ids?.fileStoreIds,
                                                      )) {
                                                        return snackBar(
                                                          "Error",
                                                          "File Not Found",
                                                          BaseConfig.redColor,
                                                        );
                                                      }
                                                      if (fileId != null &&
                                                          context.mounted) {
                                                        _downloadController
                                                            .starFileDownload(
                                                          url: fileId,
                                                          title:
                                                              getLocalizedString(
                                                            i18.waterSewerage
                                                                .SANCTION_LETTER,
                                                            module: Modules.WS,
                                                          ),
                                                        );
                                                      }
                                                    },
                                                    icon: const Icon(
                                                      Icons.download,
                                                      color: BaseConfig
                                                          .appThemeColor1,
                                                    ),
                                                  ),
                                                ],
                                              ),
                                              const Divider(
                                                color: BaseConfig.borderColor,
                                              ),
                                              SizedBox(height: 4.h),
                                              SmallSelectableTextNotoSans(
                                                text: sewerageConnection
                                                        .applicationNo ??
                                                    "N/A",
                                                fontWeight: FontWeight.w400,
                                                size: o == Orientation.portrait
                                                    ? 12.sp
                                                    : 7.sp,
                                              ),
                                            ],
                                          ).paddingAll(
                                            o == Orientation.portrait
                                                ? 16.w
                                                : 8.w,
                                          ),
                                        ],
                                      ),
                                    ),
                                  );
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
