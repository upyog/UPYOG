import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/trade_license_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/trade_license/trade_license.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/empty_box.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';

class TradeLicenseApproved extends StatefulWidget {
  const TradeLicenseApproved({super.key});

  @override
  State<TradeLicenseApproved> createState() => _TradeLicenseApprovedState();
}

class _TradeLicenseApprovedState extends State<TradeLicenseApproved> {
  final _authController = Get.find<AuthController>();
  final _tlController = Get.find<TradeLicenseController>();
  final _fileController = Get.find<FileController>();
  final _downloadController = Get.find<DownloadController>();

  String? tenantCity;

  @override
  void initState() {
    super.initState();
    getTlApplication();
  }

  void getTlApplication() async {
    TenantTenant tenant = await getCityTenant();
    setState(() {
      tenantCity = tenant.code!;
    });
    _tlController.getTlApprovedApplications(
      token: _authController.token!.accessToken!,
      tenantId: tenantCity!,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Navigator.of(context).pop();
        },
        title: 'My Certificates',
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: StreamBuilder(
            stream: _tlController.streamCtrlApproved.stream,
            builder: (context, AsyncSnapshot snapshot) {
              if (snapshot.hasData) {
                var tl = snapshot.data;

                if (snapshot.data is String || !isNotNullOrEmpty(tl.licenses)) {
                  return const NoApplicationFoundWidget();
                }
                return _buildTlApplication(tl as TradeLicense);
              } else if (snapshot.hasError) {
                return networkErrorPage(
                  context,
                  () => getTlApplication(),
                );
              } else {
                switch (snapshot.connectionState) {
                  case ConnectionState.waiting:
                    return SizedBox(
                      height: Get.height * 0.8,
                      width: Get.width,
                      child: showCircularIndicator(),
                    );
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
    );
  }

  Widget _buildTlApplication(TradeLicense tradeLicense) {
    final o = MediaQuery.of(context).orientation;
    return tradeLicense.licenses!.isEmpty
        ? const EmptyBox(text: 'My application not found')
        : ListView.builder(
            itemCount: tradeLicense.licenses!.length,
            shrinkWrap: true,
            physics: AppPlatforms.platformPhysics(),
            itemBuilder: (context, index) {
              final license = tradeLicense.licenses![index];
              if (license.status == 'APPROVED') {
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
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                SizedBox(
                                  width: Get.width * 0.5,
                                  child: MediumTextNotoSans(
                                    text: 'TL Certificate',
                                    fontWeight: FontWeight.w700,
                                    size: o == Orientation.portrait
                                        ? 14.sp
                                        : 8.sp,
                                    maxLine: 2,
                                    textOverflow: TextOverflow.ellipsis,
                                  ),
                                ),
                                TextButtonNotoSans(
                                  text: '',
                                  padding: EdgeInsets.symmetric(
                                    horizontal: 4.w,
                                    vertical: 2.h,
                                  ),
                                  fontSize:
                                      o == Orientation.portrait ? 12.sp : 7.sp,
                                  onPressed: () async {
                                    _tlController.isLoading.value = true;
                                    if (license.fileStoreId != null) {
                                      final fileStore =
                                          await _fileController.getFiles(
                                        fileStoreIds: license.fileStoreId,
                                        tenantId: license.tenantId!,
                                        token:
                                            _authController.token!.accessToken!,
                                      );

                                      if (isNotNullOrEmpty(
                                        fileStore?.fileStoreIds,
                                      )) {
                                        _downloadController.starFileDownload(
                                          url: fileStore!
                                              .fileStoreIds!.first.url!,
                                          title: 'TL Certificate',
                                        );
                                      }
                                    } else {
                                      final pdfFileStoreId =
                                          await _fileController
                                              .getPdfServiceFile(
                                        tenantId: license.tenantId!,
                                        token:
                                            _authController.token!.accessToken!,
                                        license: license,
                                        key: PdfKey.tlCertificate,
                                      );
                                      if (pdfFileStoreId != null) {
                                        var newFileStore =
                                            await _fileController.getFiles(
                                          fileStoreIds: pdfFileStoreId,
                                          tenantId: license.tenantId!,
                                          token: _authController
                                              .token!.accessToken!,
                                        );
                                        if (isNotNullOrEmpty(newFileStore)) {
                                          _downloadController.starFileDownload(
                                            url: newFileStore!
                                                .fileStoreIds!.first.url!,
                                            title: 'TL Certificate',
                                          );
                                        }
                                      }
                                    }
                                    _tlController.isLoading.value = false;
                                  },
                                  icon: const Icon(
                                    Icons.download,
                                    color: BaseConfig.appThemeColor1,
                                  ),
                                ),
                              ],
                            ),
                            const Divider(
                              color: BaseConfig.borderColor,
                            ),
                            SizedBox(height: 4.h),
                            SmallSelectableTextNotoSans(
                              text: license.applicationNumber ?? "N/A",
                              fontWeight: FontWeight.w400,
                              size: o == Orientation.portrait ? 12.sp : 7.sp,
                            ),
                          ],
                        ).paddingAll(
                          o == Orientation.portrait ? 16.w : 8.w,
                        ),
                      ],
                    ),
                  ),
                );
              } else {
                return Container();
              }
            },
          );
  }
}
