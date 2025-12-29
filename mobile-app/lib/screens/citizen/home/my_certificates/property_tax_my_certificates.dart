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
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';

class PropertyMyCertificates extends StatefulWidget {
  const PropertyMyCertificates({super.key});

  @override
  State<PropertyMyCertificates> createState() => _PropertyMyCertificatesState();
}

class _PropertyMyCertificatesState extends State<PropertyMyCertificates> {
  final _authController = Get.find<AuthController>();
  final _editProfileController = Get.find<EditProfileController>();
  final _fileController = Get.find<FileController>();
  final _downloadController = Get.find<DownloadController>();
  final _propertiesTaxController = Get.find<PropertiesTaxController>();

  @override
  void initState() {
    init();
    super.initState();
  }

  void init() async {
    _propertiesTaxController.isLoading.value = false;
    await _propertiesTaxController.getMyPropertiesStream(
      isCertificate: true,
      token: _authController.token!.accessToken!,
      mobileNumber:
          _editProfileController.userProfile.user!.first.mobileNumber!,
    );
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
                  stream: _propertiesTaxController.streamCtrl.stream,
                  builder: (context, snapshots) {
                    if (snapshots.hasData) {
                      if (snapshots.data is String || snapshots.data == null) {
                        return Center(
                          child: Text(
                            getLocalizedString(i18.inbox.NO_APPLICATION),
                          ),
                        );
                      }
                      final PtMyProperties ptMyProperties = snapshots.data;

                      if (!isNotNullOrEmpty(ptMyProperties.properties)) {
                        return const NoApplicationFoundWidget();
                      }

                      ptMyProperties.properties?.sort(
                        (a, b) => DateTime.fromMillisecondsSinceEpoch(
                          b.auditDetails!.createdTime!,
                        ).compareTo(
                          DateTime.fromMillisecondsSinceEpoch(
                            a.auditDetails!.createdTime!,
                          ),
                        ),
                      );

                      return Obx(
                        () => _propertiesTaxController.isLoading.value
                            ? SizedBox(
                                height: Get.height * 0.8,
                                width: Get.width,
                                child: showCircularIndicator(),
                              )
                            : ListView.builder(
                                itemCount: ptMyProperties.properties!.length,
                                shrinkWrap: true,
                                physics: const NeverScrollableScrollPhysics(),
                                itemBuilder: (context, index) {
                                  final propertyData =
                                      ptMyProperties.properties![index];
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
                                                      text:
                                                          "Mutation Certificate",
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
                                                      _propertiesTaxController
                                                          .isLoading
                                                          .value = true;

                                                      final pdfFileStoreId =
                                                          await _fileController
                                                              .getPdfServiceFile(
                                                        tenantId: propertyData
                                                            .tenantId!,
                                                        token: _authController
                                                            .token!
                                                            .accessToken!,
                                                        property: propertyData,
                                                        key: PdfKey
                                                            .ptmutationCertificate,
                                                      );
                                                      if (pdfFileStoreId !=
                                                          null) {
                                                        var newFileStore =
                                                            await _fileController
                                                                .getFiles(
                                                          fileStoreIds:
                                                              pdfFileStoreId,
                                                          tenantId: propertyData
                                                              .tenantId!,
                                                          token: _authController
                                                              .token!
                                                              .accessToken!,
                                                        );
                                                        if (isNotNullOrEmpty(
                                                          newFileStore,
                                                        )) {
                                                          await _downloadController
                                                              .starFileDownload(
                                                            url: newFileStore!
                                                                .fileStoreIds![
                                                                    0]
                                                                .url!,
                                                            title:
                                                                'Mutation Certificate',
                                                            //     getLocalizedString(
                                                            //   i18.propertyTax
                                                            //       .MT_APPLICATION,
                                                            //   module:
                                                            //       Modules.PT,
                                                            // ),
                                                          );
                                                        }
                                                      }

                                                      _propertiesTaxController
                                                          .isLoading
                                                          .value = false;
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
                                                text: propertyData.propertyId ??
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
