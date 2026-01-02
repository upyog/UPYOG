// ignore_for_file: use_build_context_synchronously

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/controller/property_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/controller/trade_license_controller.dart';
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/property/property.dart';
import 'package:mobile_app/model/citizen/trade_license/trade_license.dart'
    as tl;
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/screens/citizen/payments/payment_screen.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/build_card.dart';
import 'package:mobile_app/widgets/build_expansion.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/file_dialogue/file_dilaogue.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/timeline_widget.dart/timeline_wdget.dart';

class TLDetailsScreen extends StatefulWidget {
  const TLDetailsScreen({super.key});

  @override
  State<TLDetailsScreen> createState() => _TLDetailsScreenState();
}

class _TLDetailsScreenState extends State<TLDetailsScreen> {
  final _tlController = Get.find<TradeLicenseController>();
  final _authController = Get.find<AuthController>();
  final _propertyController = Get.find<PropertyController>();
  final _fileController = Get.find<FileController>();
  final _timelineHistoryController = Get.find<TimelineController>();
  final _downloadController = Get.find<DownloadController>();
  final _commonController = Get.find<CommonController>();
  final _paymentController = Get.find<PaymentController>();

  final propertiesFuture = Completer<Properties?>();

  late tl.License license;
  late tl.TradeLicenseDetail _tradeLicenseDetail;

  List<tl.ApplicationDocument> appDocuments = [];

  bool _dataFetched = false;
  bool _isTimelineFetch = false;
  bool _isEvidenceFetch = false;
  bool _isOwnerFetch = false;

  @override
  void initState() {
    super.initState();
    _getArgs();
    _fetchLabelsAsync();
  }

  Future<void> _fetchLabelsAsync() async {
    await _commonController.fetchLabels(modules: Modules.TL);
    await _commonController.fetchLabels(modules: Modules.PT);
  }

  void _getArgs() {
    final licArgs = Get.arguments as tl.License;
    license = licArgs;

    if (license.tradeLicenseDetail?.applicationDocuments == null) return;
    for (var doc in license.tradeLicenseDetail!.applicationDocuments!) {
      appDocuments.add(doc);
    }
  }

  Future<void> _getProperties() async {
    try {
      if (_dataFetched) return;
      TenantTenant tenantCity = await getCityTenant();
      final properties = await _propertyController.getPropertiesByID(
        token: _authController.token!.accessToken!,
        propertyId: _tlController.propertyId,
        tenantCity: tenantCity.code!,
      );

      _dataFetched = true;

      propertiesFuture.complete(properties);
    } catch (e, s) {
      propertiesFuture.completeError(e, s);
      _dataFetched = false;
      dPrint('Error: $e');
    }
    setState(() {});
  }

  String getFileStoreIds() {
    if (!isNotNullOrEmpty(appDocuments)) return '';

    List fileIds = [];
    for (var element in appDocuments) {
      fileIds.add(element.fileStoreId);
    }
    return fileIds.join(', ');
  }

  Future<void> _getTimeline() async {
    await _timelineHistoryController
        .getTimelineHistory(
      token: _authController.token!.accessToken!,
      tenantId: license.tenantId!,
      businessIds: license.applicationNumber!,
    )
        .then((val) {
      setState(() {
        _isTimelineFetch = true;
      });
    });
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
        billId: billInfo!.bill!.first.id!,
        totalAmount: '${billInfo.bill!.first.totalAmount}',
      ),
    );
  }

  @override
  dispose() {
    _dataFetched = false;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Navigator.of(context).pop();
        },
        title: getLocalizedString(
          i18.tradeLicense.APPLICATION_DETAILS,
          module: Modules.TL,
        ),
      ),
      bottomNavigationBar:
          license.status == TradeLicenseStatus.PENDING_PAYMENT.name
              ? FilledButtonApp(
                  width: Get.width,
                  radius: 0,
                  text: getLocalizedString(
                    i18.common.MAKE_PAYMENT,
                  ),
                  onPressed: () => goPayment(license),
                  circularColor: BaseConfig.fillAppBtnCircularColor,
                )
              : const SizedBox.shrink(),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Obx(
          () => _tlController.isLoading.value
              ? showCircularIndicator()
              : SingleChildScrollView(
                  physics: AppPlatforms.platformPhysics(),
                  child: Padding(
                    padding: EdgeInsets.all(16.w),
                    child: Column(
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            TextButton(
                              onPressed: () async {
                                if (!_isTimelineFetch) {
                                  _getTimeline();
                                }

                                TimelineHistoryApp.buildTimelineDialogue(
                                  context,
                                  tenantId: license.tenantId!,
                                );
                              },
                              child: MediumText(
                                text: getLocalizedString(i18.common.TIMELINE),
                                color: BaseConfig.redColor1,
                              ),
                            ),
                            const SizedBox(width: 10),
                            if (license.status ==
                                TradeLicenseStatus.APPROVED.name)
                              PopupMenuButton(
                                icon: Row(
                                  crossAxisAlignment: CrossAxisAlignment.center,
                                  children: [
                                    const Icon(
                                      Icons.download,
                                      color: BaseConfig.redColor1,
                                    ),
                                    const SizedBox(width: 5),
                                    MediumText(
                                      text: getLocalizedString(
                                        i18.common.DOWNLOAD,
                                      ),
                                      color: BaseConfig.redColor1,
                                      size: 14.0,
                                    ),
                                  ],
                                ),
                                itemBuilder: (context) =>
                                    <PopupMenuItem<String>>[
                                  PopupMenuItem<String>(
                                    value: getLocalizedString(
                                      i18.tradeLicense.TL_CERTIFICATE,
                                      module: Modules.TL,
                                    ),
                                    child: MediumText(
                                      text: getLocalizedString(
                                        i18.tradeLicense.TL_CERTIFICATE,
                                        module: Modules.TL,
                                      ),
                                    ),
                                    onTap: () async {
                                      final pdfFileStoreId =
                                          await _fileController
                                              .getPdfServiceFile(
                                        tenantId: license.tenantId!,
                                        token:
                                            _authController.token!.accessToken!,
                                        license: license,
                                        key: PdfKey.tlCertificate,
                                      );

                                      if (isNotNullOrEmpty(pdfFileStoreId)) {
                                        final newFileStore =
                                            await _fileController.getFiles(
                                          fileStoreIds: pdfFileStoreId,
                                          tenantId: license.tenantId!,
                                          token: _authController
                                              .token!.accessToken!,
                                        );
                                        if (isNotNullOrEmpty(
                                          newFileStore?.fileStoreIds,
                                        )) {
                                          _downloadController.starFileDownload(
                                            url: newFileStore!
                                                .fileStoreIds!.first.url!,
                                            title: getLocalizedString(
                                              i18.tradeLicense.TL_APPLICATION,
                                              module: Modules.TL,
                                            ),
                                          );
                                        }
                                      }
                                    },
                                  ),
                                  PopupMenuItem<String>(
                                    value: getLocalizedString(
                                      i18.common.PAYMENT_RECEIPT,
                                    ),
                                    child: MediumText(
                                      text: getLocalizedString(
                                        i18.common.PAYMENT_RECEIPT,
                                        module: Modules.TL,
                                      ),
                                    ),
                                    onTap: () async {
                                      final payment = await _paymentController
                                          .verifyPayment(
                                        tenantId: license.tenantId!,
                                        token:
                                            _authController.token!.accessToken!,
                                        businessService:
                                            BusinessService.TL.name,
                                        consumerCodes:
                                            license.applicationNumber!,
                                      );

                                      if (!isNotNullOrEmpty(payment)) {
                                        _tlController.isLoading.value = false;
                                        snackBar(
                                          'Error',
                                          'Receipt not found!',
                                          BaseConfig.redColor1,
                                        );
                                        return;
                                      }

                                      if (isNotNullOrEmpty(
                                        payment?.fileStoreId,
                                      )) {
                                        final newFileStore =
                                            await _fileController.getFiles(
                                          fileStoreIds: payment!.fileStoreId!,
                                          tenantId: BaseConfig
                                              .STATE_TENANT_ID, //For Test ENV
                                          token: _authController
                                              .token!.accessToken!,
                                        );
                                        if (isNotNullOrEmpty(
                                          newFileStore?.fileStoreIds,
                                        )) {
                                          _downloadController.starFileDownload(
                                            url: newFileStore!
                                                .fileStoreIds!.first.url!,
                                            title: getLocalizedString(
                                              i18.common.PAYMENT_RECEIPT,
                                              module: Modules.TL,
                                            ),
                                          );
                                        }
                                      } else {
                                        final pdfFileStoreId =
                                            await _fileController
                                                .getPdfServiceFile(
                                          tenantId: license.tenantId!,
                                          token: _authController
                                              .token!.accessToken!,
                                          payment: payment,
                                          key: PdfKey.tlReceipt,
                                        );
                                        if (isNotNullOrEmpty(pdfFileStoreId)) {
                                          final newFileStore =
                                              await _fileController.getFiles(
                                            fileStoreIds: pdfFileStoreId,
                                            tenantId: license.tenantId!,
                                            token: _authController
                                                .token!.accessToken!,
                                          );
                                          if (isNotNullOrEmpty(
                                            newFileStore?.fileStoreIds,
                                          )) {
                                            _downloadController
                                                .starFileDownload(
                                              url: newFileStore!
                                                  .fileStoreIds!.first.url!,
                                              title: getLocalizedString(
                                                i18.common.PAYMENT_RECEIPT,
                                                module: Modules.TL,
                                              ),
                                            );
                                          }
                                        }
                                      }
                                    },
                                  ),
                                ],
                              ),
                          ],
                        ),
                        _buildDetails(),
                        const SizedBox(height: 10),
                        BuildCard(
                          padding: 5,
                          child: Column(
                            children: [
                              if (license.tradeLicenseDetail?.owners != null)
                                BuildExpansion(
                                  title: getLocalizedString(
                                    i18.tradeLicense.OWNERSHIP_DETAILS,
                                    module: Modules.TL,
                                  ),
                                  onExpansionChanged: (value) async {
                                    if (value && !_isOwnerFetch) {
                                      _isOwnerFetch = true;
                                      await _tlController
                                          .getOwnersLicenseByAppID(
                                        token: _authController
                                                .token?.accessToken ??
                                            '',
                                        applicationNo:
                                            license.applicationNumber!,
                                      );
                                    }
                                    setState(() {});
                                  },
                                  children: [
                                    StreamBuilder(
                                      stream: _tlController
                                          .licenseStreamCtrl.stream,
                                      builder: (context, snapshot) {
                                        if (snapshot.hasData &&
                                            !snapshot.hasError &&
                                            snapshot.data!.isNotEmpty) {
                                          final snapData = snapshot.data!.first;

                                          _tradeLicenseDetail =
                                              snapData.tradeLicenseDetail!;

                                          return snapData.tradeLicenseDetail
                                                      ?.owners ==
                                                  null
                                              ? const SizedBox.shrink()
                                              : ListView.builder(
                                                  shrinkWrap: true,
                                                  itemCount: snapData
                                                      .tradeLicenseDetail!
                                                      .owners!
                                                      .length,
                                                  physics:
                                                      const NeverScrollableScrollPhysics(),
                                                  itemBuilder:
                                                      (context, index) {
                                                    final owner = snapData
                                                        .tradeLicenseDetail!
                                                        .owners![index];

                                                    return _buildOwnerCard(
                                                      owner,
                                                      index + 1,
                                                    ).paddingOnly(bottom: 10);
                                                  },
                                                );
                                        } else if (snapshot.hasError) {
                                          return networkErrorPage(
                                            context,
                                            () => _tlController
                                                .getOwnersLicenseByAppID(
                                              token: _authController
                                                      .token?.accessToken ??
                                                  '',
                                              applicationNo:
                                                  license.applicationNumber!,
                                            ),
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
                                    const SizedBox(height: 20),
                                  ],
                                ),
                              if (license.tradeLicenseDetail?.tradeUnits !=
                                  null)
                                BuildExpansion(
                                  title: getLocalizedString(
                                    i18.tradeLicense.TRADE_UNITS,
                                    module: Modules.TL,
                                  ),
                                  children: [
                                    ListView.builder(
                                      shrinkWrap: true,
                                      itemCount: license.tradeLicenseDetail
                                          ?.tradeUnits?.length,
                                      physics: AppPlatforms.platformPhysics(),
                                      itemBuilder: (context, index) {
                                        var tradeUnit = license
                                            .tradeLicenseDetail
                                            ?.tradeUnits?[index];
                                        return _buildTradeUnitCard(
                                          tradeUnit!,
                                          index + 1,
                                        );
                                      },
                                    ),
                                    const SizedBox(height: 20),
                                  ],
                                ),
                              if (isNotNullOrEmpty(
                                license.tradeLicenseDetail?.accessories,
                              ))
                                BuildExpansion(
                                  title: getLocalizedString(
                                    i18.tradeLicense.ACCESSORY,
                                    module: Modules.TL,
                                  ),
                                  children: [
                                    ListView.builder(
                                      shrinkWrap: true,
                                      itemCount: license.tradeLicenseDetail!
                                          .accessories!.length,
                                      physics: AppPlatforms.platformPhysics(),
                                      itemBuilder: (context, index) {
                                        final accessory = license
                                            .tradeLicenseDetail!
                                            .accessories?[index];

                                        return _buildAccessories(
                                          accessory!,
                                          index + 1,
                                        );
                                      },
                                    ),
                                    const SizedBox(height: 20),
                                  ],
                                ),

                              // Property Details
                              BuildExpansion(
                                title: getLocalizedString(
                                  i18.tlProperty.PROPERTY_DETAILS,
                                ),
                                onExpansionChanged: (expanded) async {
                                  if (expanded && !_dataFetched) {
                                    _getProperties();
                                  }
                                },
                                children: [
                                  FutureBuilder(
                                    future: propertiesFuture.future,
                                    builder: (context, snapshot) {
                                      if (!snapshot.hasError &&
                                          snapshot.hasData) {
                                        if (snapshot.data is String ||
                                            snapshot.data == null) {
                                          return Center(
                                            child: Text(
                                              getLocalizedString(
                                                i18.inbox.NO_APPLICATION,
                                              ),
                                            ),
                                          );
                                        }

                                        Properties? property = snapshot.data;

                                        if (!isNotNullOrEmpty(
                                          property?.properties,
                                        )) {
                                          return Center(
                                            child: Text(
                                              getLocalizedString(
                                                i18.inbox.NO_APPLICATION,
                                              ),
                                            ),
                                          );
                                        }

                                        return Column(
                                          children: [
                                            ColumnHeaderText(
                                              label: getLocalizedString(
                                                i18.tlProperty.ID,
                                              ),
                                              text: property!.properties!.first
                                                      .propertyId ??
                                                  'N/A',
                                            ).paddingOnly(left: 7),
                                            const SizedBox(height: 10),
                                            ColumnHeaderText(
                                              label: getLocalizedString(
                                                i18.tlProperty.NAME,
                                              ),
                                              text: property.properties!.first
                                                      .owners?.first.name ??
                                                  'N/A',
                                            ).paddingOnly(left: 7),
                                            const SizedBox(height: 10),
                                            ColumnHeaderText(
                                              label: getLocalizedString(
                                                i18.tlProperty.ADDRESS,
                                              ),
                                              text: property
                                                      .properties!
                                                      .first
                                                      .owners
                                                      ?.first
                                                      .permanentAddress ??
                                                  'N/A',
                                            ).paddingOnly(left: 7),
                                            const SizedBox(height: 10),
                                            Align(
                                              alignment: Alignment.centerLeft,
                                              child: TextButton(
                                                onPressed: () {
                                                  Get.toNamed(
                                                    AppRoutes.PROPERTY_INFO,
                                                    arguments: {
                                                      "license": license,
                                                      "properties": property,
                                                      "tlDetails":
                                                          _tradeLicenseDetail,
                                                    },
                                                  );
                                                },
                                                child: Row(
                                                  children: [
                                                    MediumText(
                                                      text: getLocalizedString(
                                                        i18.tlProperty
                                                            .VIEW_DETAILS_BTN,
                                                      ),
                                                      color: Colors.red,
                                                    ),
                                                    const SizedBox(width: 10),
                                                    const Icon(
                                                      Icons
                                                          .arrow_right_alt_outlined,
                                                      color: Colors.red,
                                                    ),
                                                  ],
                                                ),
                                              ),
                                            ),
                                            const SizedBox(height: 10),
                                          ],
                                        );
                                      } else if (snapshot.hasError) {
                                        return Obx(
                                          () => _propertyController
                                                  .isLoading.value
                                              ? showCircularIndicator()
                                              : networkErrorPage(
                                                  context,
                                                  errorText:
                                                      'Unable to get property details',
                                                  () => _getProperties(),
                                                ),
                                        );
                                      } else {
                                        switch (snapshot.connectionState) {
                                          case ConnectionState.waiting ||
                                                ConnectionState.active:
                                            return showCircularIndicator()
                                                .paddingAll(4);

                                          default:
                                            return const SizedBox.shrink();
                                        }
                                      }
                                    },
                                  ),
                                ],
                              ),
                              BuildExpansion(
                                title: getLocalizedString(
                                  i18.tradeLicense.EVIDENCE,
                                  // module: Modules.TL,
                                ),
                                onExpansionChanged: (expanded) async {
                                  if (expanded && !_isEvidenceFetch) {
                                    _isEvidenceFetch = true;
                                    await _fileController.getFiles(
                                      tenantId: BaseConfig.STATE_TENANT_ID,
                                      token:
                                          _authController.token!.accessToken!,
                                      fileStoreIds: getFileStoreIds(),
                                    );
                                  }
                                  setState(() {});
                                },
                                children: [
                                  StreamBuilder(
                                    stream: _fileController
                                        .fileStoreStreamCtrl.stream,
                                    builder: (context, snapshot) {
                                      if (snapshot.hasData) {
                                        final fileData = snapshot.data!;
                                        return _buildEvidenceCard(fileData);
                                      } else if (snapshot.hasError) {
                                        return networkErrorPage(
                                          context,
                                          () => _fileController.getFiles(
                                            tenantId:
                                                BaseConfig.STATE_TENANT_ID,
                                            token: _authController
                                                .token!.accessToken!,
                                            fileStoreIds: getFileStoreIds(),
                                          ),
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
                                  const SizedBox(height: 10),
                                ],
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
        ),
      ),
    );
  }

  Widget _buildAccessories(tl.Accessories accessories, index) {
    return Container(
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey),
        borderRadius: BorderRadius.circular(10.r),
      ),
      child: Padding(
        padding: EdgeInsets.all(10.w),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Wrap(
              children: [
                BigTextNotoSans(
                  text: getLocalizedString(
                    i18.tradeLicense.ACCESSORY_LABEL,
                    module: Modules.TL,
                  ),
                  size: 16.sp,
                  fontWeight: FontWeight.w600,
                  color: const Color.fromRGBO(80, 90, 95, 1.0),
                ),
                BigTextNotoSans(
                  text: ' - $index',
                  size: 16.sp,
                  fontWeight: FontWeight.w600,
                  color: const Color.fromRGBO(80, 90, 95, 1.0),
                ),
              ],
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.ACCESSORY_TYPE,
                module: Modules.TL,
              ),
              text: accessories.accessoryCategory ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.ACCESSORY_COUNT,
                module: Modules.TL,
              ),
              text: accessories.count != null
                  ? accessories.count.toString()
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.ACCESSORY_UOM,
                module: Modules.TL,
              ),
              text: accessories.uom ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.ACCESSORY_UOMVALUE,
                module: Modules.TL,
              ),
              text: accessories.uomValue ?? 'N/A',
            ),
            const SizedBox(height: 10),
          ],
        ),
      ),
    );
  }

  Widget _buildEvidenceCard(FileStore fileStore) {
    return Padding(
      padding: const EdgeInsets.all(10.0),
      child: isNotNullOrEmpty(fileStore.fileStoreIds)
          ? GridView.builder(
              itemCount: fileStore.fileStoreIds!.length,
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3,
                crossAxisSpacing: 10.w,
                mainAxisSpacing: 10.h,
                mainAxisExtent: 110.0, //110
              ),
              shrinkWrap: true,
              itemBuilder: (context, index) {
                final fileUrl =
                    fileStore.fileStoreIds![index].url!.split(',').first;
                final docType = appDocuments
                    .where(
                      (element) =>
                          element.fileStoreId ==
                          fileStore.fileStoreIds![index].id,
                    )
                    .toList()
                    .firstOrNull;
                return isNotNullOrEmpty(docType)
                    ? Column(
                        children: [
                          Container(
                            width: Get.width,
                            decoration: BoxDecoration(
                              color: BaseConfig.greyColor2,
                              // border: Border.all(color: Colors.grey),
                              borderRadius: BorderRadius.circular(10),
                            ),
                            child: Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: Icon(
                                _fileController.getFileType(fileUrl).$1,
                                size: 40,
                                color: Colors.grey.shade600,
                              ),
                            ),
                          ),
                          const SizedBox(height: 8),
                          Tooltip(
                            message: getLocalizedString(
                              docType!.documentType,
                              module: Modules.TL,
                            ),
                            child: Padding(
                              padding: const EdgeInsets.all(4.0),
                              child: SmallTextNotoSans(
                                text: getLocalizedString(
                                  docType.documentType,
                                  module: Modules.TL,
                                ),
                                color: Colors.grey.shade600,
                                maxLine: 2,
                                textOverflow: TextOverflow.ellipsis,
                              ),
                            ),
                          ),
                        ],
                      ).ripple(
                        () {
                          final fileType =
                              _fileController.getFileType(fileUrl).$2;
                          dPrint('FileType: ${fileType.name}');
                          if (fileType.name == FileExtType.pdf.name) {
                            showTypeDialogue(
                              context,
                              url: fileUrl,
                              isPdf: true,
                              title: getLocalizedString(
                                docType.documentType,
                                module: Modules.TL,
                              ),
                            );
                          } else {
                            showTypeDialogue(
                              context,
                              url: fileUrl,
                              title: getLocalizedString(
                                docType.documentType,
                                module: Modules.TL,
                              ),
                            );
                          }
                        },
                        customBorder: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10),
                        ),
                      )
                    : const SizedBox.shrink();
              },
            )
          : const SizedBox.shrink(),
    );
  }

  Widget _buildTradeUnitCard(tl.TradeUnit tradeUnit, index) {
    return Container(
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Wrap(
              children: [
                BigTextNotoSans(
                  text: getLocalizedString(
                    i18.tradeLicense.UNIT,
                    module: Modules.TL,
                  ),
                  size: 16.sp,
                  fontWeight: FontWeight.w600,
                  color: const Color.fromRGBO(80, 90, 95, 1.0),
                ),
                BigTextNotoSans(
                  text: ' - $index',
                  size: 16.sp,
                  fontWeight: FontWeight.w600,
                  color: const Color.fromRGBO(80, 90, 95, 1.0),
                ),
              ],
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.INDUSTRY_TYPE,
                module: Modules.TL,
              ),
              text: getLocalizedString(
                '${i18.tradeLicense.TRADE_TYPE_RES}${tradeUnit.tradeType!.split('.').first}',
                module: Modules.TL,
              ),
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.TRADE_DESC,
                module: Modules.TL,
              ),
              text: getLocalizedString(
                '${i18.tradeLicense.TRADE_TYPE_RES}${tradeUnit.tradeType!.split('.')[1]}',
                module: Modules.TL,
              ),
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.TRADE_SUB_TYPE,
                module: Modules.TL,
              ),
              text: getLocalizedString(
                '${i18.tradeLicense.TRADE_DESC_RES}${tradeUnit.tradeType}',
                module: Modules.TL,
              ),
            ),
            const SizedBox(height: 10),
          ],
        ),
      ),
    );
  }

  Widget _buildOwnerCard(tl.Owner owner, int index) {
    return Container(
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Wrap(
              children: [
                BigTextNotoSans(
                  text: getLocalizedString(
                    i18.tradeLicense.TRADE_OWNER,
                    module: Modules.TL,
                  ),
                  size: 16.sp,
                  fontWeight: FontWeight.w600,
                  color: const Color.fromRGBO(80, 90, 95, 1.0),
                ),
                BigTextNotoSans(
                  text: ' - $index',
                  size: 16.sp,
                  fontWeight: FontWeight.w600,
                  color: const Color.fromRGBO(80, 90, 95, 1.0),
                ),
              ],
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.OWNER_NAME,
                // module: Modules.TL,
              ),
              text: owner.name ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.GENDER,
                // module: Modules.TL,
              ),
              text: owner.gender ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.MOBILE,
                module: Modules.TL,
              ),
              text: owner.mobileNumber ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.EMAIL,
                module: Modules.TL,
              ),
              text: (owner.emailId != null && owner.emailId!.isNotEmpty)
                  ? owner.emailId!
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.GUARDIAN,
                module: Modules.TL,
              ),
              text: owner.fatherOrHusbandName ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tradeLicense.RELATIONSHIP_WITH_GUARDIAN,
                module: Modules.TL,
              ),
              text: owner.relationship ?? 'N/A',
            ),
            const SizedBox(height: 10),
          ],
        ),
      ),
    );
  }

  Widget _buildDetails() {
    return BuildCard(
      child: Column(
        children: [
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.APPLICATION_NO,
              // module: Modules.TL,
            ),
            text: license.applicationNumber ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.LICENSE_NO,
              module: Modules.TL,
            ),
            text: license.licenseNumber ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.APPLICATION_CATEGORY,
              module: Modules.TL,
            ),
            text: getLocalizedString(i18.tradeLicense.TEST_TRADE_LICENSE),
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.common.APPLICATION_STATUS,
              // module: Modules.TL,
            ),
            text: getLocalizedString(
              'WF_${license.workflowCode}_${license.status}'.toUpperCase(),
              module: Modules.TL,
            ),
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(i18.tradeLicense.SLA, module: Modules.TL),
            text: '0 Days',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.BUSINESS_NAME,
              //module: Modules.TL,
            ),
            text: license.tradeName ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.GST_NO,
              module: Modules.TL,
            ),
            text: license.tradeLicenseDetail?.additionalDetail?.tradeGstNo ??
                'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.OPERATIONAL_AREA,
              module: Modules.TL,
            ),
            text: license.tradeLicenseDetail?.operationalArea != null
                ? license.tradeLicenseDetail!.operationalArea!.toString()
                : 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.NO_OF_EMPLOYEES,
              module: Modules.TL,
            ),
            text: license.tradeLicenseDetail?.noOfEmployees != null
                ? license.tradeLicenseDetail!.noOfEmployees.toString()
                : 'N/A',
          ),
        ],
      ),
    );
  }
}
