import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/documents_not_found/documents_not_found.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
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
import 'package:mobile_app/widgets/owner_card/owner_card_widget.dart';
import 'package:mobile_app/widgets/small_text.dart';

class MyPropertyDetailsScreen extends StatefulWidget {
  const MyPropertyDetailsScreen({super.key});

  @override
  State<MyPropertyDetailsScreen> createState() =>
      _MyPropertyDetailsScreenState();
}

class _MyPropertyDetailsScreenState extends State<MyPropertyDetailsScreen>
    with SingleTickerProviderStateMixin {
  final propertyTaxController = Get.find<PropertiesTaxController>();

  final Property? property = Get.arguments;
  // var _isOtpSent = false;

  final _fileController = Get.find<FileController>();
  final _authController = Get.find<AuthController>();
  // final _paymentController = Get.find<PaymentController>();

  // final _otpEditingController = OtpFieldController();
  // late AnimationController _animationController;
  final Completer<FileStore?> fileStoreFuture = Completer<FileStore?>();

  // String _otp = '';
  // int _timerSeconds = 30;
  // Timer? _timer;
  // bool _resendOtp = false;
  late TenantTenant tenant;

  final _isLoading = false.obs;

  var isDownloading = <bool>[].obs;

  String getFileStoreIds() {
    if (!isNotNullOrEmpty(property?.documents)) return '';

    List fileIds = [];
    for (var element in property!.documents!) {
      fileIds.add(element.fileStoreId);
    }
    return fileIds.join(', ');
  }

  // Future<void> sendCode(BuildContext context) async {
  //   _authController.isLoading.value = true;

  //   FocusScope.of(context).unfocus();

  //   _isOtpSent = await _authController.sendOtp(
  //     mobile: _authController.mobileNoController.value.text,
  //   );

  //   _authController.isLoading.value = false;

  //   setState(() {});
  // }

  @override
  void initState() {
    super.initState();

    _init();
    // _listenCode();
    // _animationController = AnimationController(
    //   vsync: this,
    //   duration: Duration(seconds: _timerSeconds),
    // )..forward();
    // _startTimer();
  }

  void _init() async {
    _isLoading.value = true;
    tenant = await getCityTenant();
    await getFiles();
    _isLoading.value = false;
  }

  Future<void> getFiles() async {
    fileStoreFuture.complete(
      await _fileController.getFiles(
        tenantId: BaseConfig.STATE_TENANT_ID, //property?.tenantId!,
        token: _authController.token!.accessToken!,
        fileStoreIds: getFileStoreIds(),
      ),
    );
  }

  // void _listenCode() async {
  //   final sig = await SmsAutoFill().getAppSignature;
  //   dPrint('App Signature: $sig');

  //   SmsAutoFill().listenForCode();
  // }

  // void _startTimer() {
  //   _timer = Timer.periodic(const Duration(seconds: 1), (_) {
  //     if (_timerSeconds == 0) {
  //       if (_timer!.isActive) {
  //         _timer?.cancel();
  //       }
  //     } else {
  //       setState(() {
  //         _timerSeconds--;
  //         _animationController.duration = Duration(seconds: _timerSeconds);
  //       });
  //     }
  //   });
  // }

  // void _resendOTP() {
  //   // Reset timer
  //   _timerSeconds = 30;
  //   _resendOtp = true;
  //   _animationController.duration = Duration(seconds: _timerSeconds);
  //   _animationController.forward(from: 0);
  //   _startTimer();
  //   _isOtpSent = true;
  //   // _otpEditingController.clear();
  //   setState(() {});
  // }

  // void _validateOtp() async {
  //   FocusScope.of(context).unfocus();
  //   _authController.isLoading.value = true;
  //   await _authController.otpValidate(
  //     phoneNo: _authController.mobileNoController.value.text,
  //     otp: _otp,
  //     isSignUp: false,
  //     userType: UserType.CITIZEN,
  //   );
  //   if (_timer!.isActive) {
  //     _timerSeconds = 0;
  //     _timer?.cancel();
  //     _resendOtp = false;
  //   }
  //   _authController.isLoading.value = false;
  // }

  @override
  void dispose() {
    // SmsAutoFill().unregisterListener();
    // // _otpEditingController.dispose();
    // _timer?.cancel();
    // _resendOtp = false;
    // _animationController.dispose();
    super.dispose();
  }

  // Future<BillInfo?> _fetchMyBills() async {
  //   final isPtMutation = (properties.workflow?.businessService ==
  //     BusinessService.PT_MUTATION.name &&
  // properties.creationReason == CreationReason.MUTATION.name);
  //   return _paymentController.searchBillById(
  //     tenantId: property?.tenantId!,
  //     token: _authController.token!.accessToken!,
  //     consumerCode: property?.propertyId!,
  //     service: isPtMutation
  //         ? BusinessService.PT_MUTATION.name
  //         : BusinessService.PT.name,
  //   );
  // }

  // void goPayment() async {
  //   final bill = await _fetchMyBills();

  //   if (!isNotNullOrEmpty(bill?.bill)) return;

  //   Get.toNamed(
  //     AppRoutes.BILL_DETAIL_SCREEN,
  //     arguments: {
  //       'billData': bill?.bill?.first,
  //       'module': Modules.PT,
  //     },
  //   );
  // }

  // Widget _makePayment() {
  //   return FilledButtonApp(
  //     radius: 0,
  //     width: Get.width,
  //     text: getLocalizedString(
  //       i18.common.MAKE_PAYMENT,
  //     ),
  //     onPressed: () => goPayment(),
  //     circularColor: BaseConfig.fillAppBtnCircularColor,
  //     backgroundColor: BaseConfig.appThemeColor1,
  //   );
  // }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        title: getLocalizedString(
          i18.propertyTax.PROPERTY_INFORMATION,
          module: Modules.PT,
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      // bottomNavigationBar:
      //    (properties.creationReason == CreationReason.MUTATION.name &&
      // properties.additionalDetails?.applicationStatus ==
      //     'FIELDVERIFIED')
      //         ? _makePayment()
      //         : null,
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Obx(
          () => _isLoading.value
              ? showCircularIndicator()
              : SingleChildScrollView(
                  physics: AppPlatforms.platformPhysics(),
                  child: Padding(
                    padding: EdgeInsets.all(16.w),
                    child: Column(
                      children: [
                        _buildDetails(context, property),
                        SizedBox(height: 10.h),
                      ],
                    ),
                  ),
                ),
        ),
      ),
    );
  }

  Widget _buildDetails(BuildContext context, Property? property) => BuildCard(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.PROPERTY_UID,
                module: Modules.PT,
              ),
              text: property?.propertyId ?? 'N/A',
            ).paddingOnly(left: 7.w),
            SizedBox(height: 10.h),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.TOTAL_AMOUNT_DUE,
                module: Modules.PT,
              ),
              text: property?.dueAmount == null
                  ? '₹0'
                  : '₹${property?.dueAmount}',
            ).paddingOnly(left: 7.w),
            Align(
              alignment: Alignment.centerLeft,
              child: TextButton(
                onPressed: () async {
                  final paymentController = Get.find<PaymentController>();
                  paymentController.isLoading.value = true;
                  final payments = await paymentController.verifyPaymentPT(
                    token: _authController.token!.accessToken!,
                    businessService: BusinessService.PT.name,
                    consumerCodes: property?.propertyId,
                    tenantId: BaseConfig.STATE_TENANT_ID,
                  );
                  paymentController.isLoading.value = false;
                  if (!isNotNullOrEmpty(payments?.payments)) {
                    snackBar(
                      'Error',
                      getLocalizedString(
                        i18.propertyTax.NO_PAYMENTS_HISTORY,
                        module: Modules.PT,
                      ),
                      BaseConfig.redColor,
                    );
                    return;
                  }

                  isDownloading.assignAll(
                    List<bool>.filled(payments!.payments!.length, false),
                  );
                  _showPaymentHistoryDialogue(payments: payments.payments!);
                },
                child: Row(
                  children: [
                    MediumTextNotoSans(
                      text: getLocalizedString(
                        i18.propertyTax.VIEW_PAYMENT,
                        module: Modules.PT,
                      ),
                      color: BaseConfig.appThemeColor1,
                    ),
                    const SizedBox(width: 10),
                    const Icon(
                      Icons.arrow_right_alt_outlined,
                      color: BaseConfig.appThemeColor1,
                    ),
                  ],
                ),
              ),
            ),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.tlProperty.ADDRESS_HEADER,
                module: Modules.PT,
              ),
              size: 16.sp,
              fontWeight: FontWeight.w600,
            ).paddingOnly(left: 7.w),
            SizedBox(height: 10.h),
            _buildAddress(address: property?.address).paddingOnly(left: 7.w),
            SizedBox(height: 10.h),
            TextButton(
              onPressed: () {
                _showOwnerHistoryDialogue(context, property);
              },
              child: Row(
                children: [
                  MediumText(
                    text: getLocalizedString(
                      i18.propertyTax.OWNER_HISTORY,
                      module: Modules.PT,
                    ).capitalize!,
                    color: Colors.red,
                  ),
                  SizedBox(width: 10.h),
                  const Icon(
                    Icons.arrow_right_alt_outlined,
                    color: Colors.red,
                  ),
                ],
              ),
            ).paddingOnly(left: 7.w),
            BuildExpansion(
              title: getLocalizedString(
                i18.propertyTax.ASSESSMENT_DETAILS_HEADER,
                module: Modules.PT,
              ),
              children: [
                _buildPropertyAssessment(property).paddingOnly(left: 7.w),
              ],
            ),
            if (property?.units != null) ...[
              BuildExpansion(
                title: getLocalizedString(
                  i18.propertyTax.GROUND_FLOOR,
                  module: Modules.PT,
                ),
                children: [
                  ListView.builder(
                    itemCount: property?.units?.length,
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemBuilder: (context, index) {
                      final unit = property?.units?[index];
                      return _buildGroundFloorCard(unit!, index + 1)
                          .paddingOnly(left: 7.w, right: 7.w, bottom: 10.h);
                    },
                  ),
                ],
              ),
            ],
            if (isNotNullOrEmpty(property?.owners))
              BuildExpansion(
                title: getLocalizedString(
                  i18.propertyTax.OWNERSHIP_DETAILS_HEADER,
                  module: Modules.PT,
                ),
                children: [
                  ListView.builder(
                    itemCount: property!.owners!.length,
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemBuilder: (context, index) {
                      return OwnerCardWidget(
                        property: property,
                        owner: property.owners![index],
                        index: index,
                      );
                    },
                  ),
                ],
              ),
            BuildExpansion(
              title: getLocalizedString(i18.common.DOCUMENTS),
              children: [
                FutureBuilder(
                  future: fileStoreFuture.future,
                  builder: (context, snapshot) {
                    if (snapshot.hasData) {
                      var fileData = snapshot.data!;
                      return _buildDocuments(fileData);
                    } else if (snapshot.hasError) {
                      return networkErrorPage(
                        context,
                        () => _fileController.getFiles(
                          tenantId: property!.tenantId!,
                          token: _authController.token!.accessToken!,
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
              ],
            ),
          ],
        ),
      );

  Widget _buildDocuments(FileStore fileStore) {
    return (!isNotNullOrEmpty(fileStore.fileStoreIds))
        ? const DocumentsNotFound(module: Modules.PT)
        : Padding(
            padding: EdgeInsets.all(10.w),
            child: GridView.builder(
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
                    fileStore.fileStoreIds?[index].url?.split(',').firstOrNull;
                final docType = property?.documents?.firstWhereOrNull(
                  (element) =>
                      element.fileStoreId == fileStore.fileStoreIds?[index].id,
                );

                return isNotNullOrEmpty(docType)
                    ? Tooltip(
                        message: getLocalizedString(
                          docType!.documentType,
                          module: Modules.PT,
                        ),
                        child: Column(
                          children: [
                            Container(
                              width: Get.width,
                              decoration: BoxDecoration(
                                color: BaseConfig.greyColor2,
                                borderRadius: BorderRadius.circular(10.r),
                              ),
                              child: Padding(
                                padding: EdgeInsets.all(8.w),
                                child: Icon(
                                  _fileController.getFileType(fileUrl!).$1,
                                  size: 40.sp,
                                  color: Colors.grey.shade600,
                                ),
                              ),
                            ),
                            SizedBox(height: 10.h),
                            SmallTextNotoSans(
                              text: getLocalizedString(
                                docType.documentType,
                                module: Modules.PT,
                              ),
                              fontWeight: FontWeight.w400,
                              color: Colors.grey.shade600,
                              maxLine: 2,
                              textOverflow: TextOverflow.ellipsis,
                            ),
                          ],
                        ).ripple(() {
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
                                module: Modules.PT,
                              ),
                            );
                          } else {
                            showTypeDialogue(
                              context,
                              url: fileUrl,
                              title: getLocalizedString(
                                docType.documentType,
                                module: Modules.PT,
                              ),
                            );
                          }
                        }),
                      )
                    : const SizedBox.shrink();
              },
            ),
          );
  }

  _showPaymentHistoryDialogue({required List<Payment> payments}) {
    return Get.dialog(
      AlertDialog(
        backgroundColor: BaseConfig.mainBackgroundColor,
        surfaceTintColor: BaseConfig.mainBackgroundColor,
        titlePadding: EdgeInsets.symmetric(horizontal: 10.h),
        insetPadding: EdgeInsets.symmetric(horizontal: 10.h),
        scrollable: false,
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Align(
              alignment: Alignment.topRight,
              child: IconButton(
                icon: const Icon(
                  Icons.cancel_presentation_outlined,
                  size: 30,
                  color: BaseConfig.appThemeColor1,
                ),
                onPressed: () => Get.back(),
              ),
            ),
            MediumTextNotoSans(
              text: getLocalizedString(
                i18.propertyTax.PT_PAYMENT_HISTORY,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
          ],
        ),
        content: SizedBox(
          height: Get.height * 0.6,
          width: Get.width,
          child: ListView.builder(
            itemCount: payments.length,
            shrinkWrap: true,
            itemBuilder: (context, index) {
              return paymentCard(payments[index], index);
            },
          ),
        ),
      ),
    );
  }

  Widget paymentCard(Payment payment, int index) {
    return BuildCard(
      child: Column(
        children: [
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.PROPERTY_UID,
              module: Modules.PT,
            ),
            text: payment.paymentDetails?.first.bill?.consumerCode ?? 'NA',
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.PT_HISTORY_BILL_PERIOD,
              module: Modules.PT,
            ),
            text:
                '${payment.paymentDetails?.first.bill?.billDetails?.first.fromPeriod.toCustomDateFormat(pattern: 'dd-MMM-yyyy')} to ${payment.paymentDetails?.first.bill?.billDetails?.first.toPeriod.toCustomDateFormat(pattern: 'dd-MMM-yyyy')}', //'01-Apr-2022 to 01-Apr-2024',
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.PT_HISTORY_BILL_NO,
              module: Modules.PT,
            ),
            text: payment.paymentDetails?.first.bill?.billNumber ?? 'NA',
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.PT_HISTORY_RECEIPT_NO,
              module: Modules.PT,
            ),
            text: payment.paymentDetails?.first.receiptNumber ?? 'NA',
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.PT_HISTORY_PAYMENT_DATE,
              module: Modules.PT,
            ),
            text: payment.paymentDetails?.first.bill?.billDate
                    .toCustomDateFormat(pattern: 'dd-MMM-yyyy') ??
                'NA',
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.PT_HISTORY_AMOUNT_PAID,
              module: Modules.PT,
            ),
            text: payment.paymentDetails?.first.bill?.billDetails?.first
                        .amount !=
                    null
                ? '₹ ${payment.paymentDetails!.first.bill!.billDetails!.first.amount!.toInt().toString()}'
                : 'NA',
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.PT_HISTORY_PAYMENT_STATUS,
              module: Modules.PT,
            ),
            text: payment.paymentStatus ?? 'NA',
          ),
          SizedBox(height: 20.h),
          Obx(
            () => FilledButtonApp(
              text: getLocalizedString(
                i18.propertyTax.PAYMENT_DOWNLOAD_RECEIPT,
                module: Modules.PT,
              ),
              isLoading: isDownloading[index],
              circularColor: BaseConfig.fillAppBtnCircularColor,
              onPressed: () async {
                final downloadController = Get.find<DownloadController>();

                isDownloading[index] = true;

                if (isNotNullOrEmpty(payment.fileStoreId)) {
                  final newFileStore = await _fileController.getFiles(
                    fileStoreIds: payment.fileStoreId!,
                    tenantId: payment.tenantId!,
                    token: _authController.token!.accessToken!,
                  );

                  if (isNotNullOrEmpty(newFileStore?.fileStoreIds)) {
                    downloadController.starFileDownload(
                      url: newFileStore!.fileStoreIds![0].url!,
                      title: getLocalizedString(
                        i18.propertyTax.PAYMENT_DOWNLOAD_RECEIPT,
                        module: Modules.PT,
                      ),
                    );
                  } else {
                    snackBar(
                      'Error',
                      'No file found!',
                      BaseConfig.redColor,
                    );
                  }
                } else {
                  final pdfFileStoreId =
                      await _fileController.getPdfServiceFile(
                    tenantId: tenant.code!,
                    token: _authController.token!.accessToken!,
                    payment: payment,
                    key:
                        property?.creationReason == CreationReason.MUTATION.name
                            ? PdfKey.pt_recept
                            : PdfKey.property_receipt,
                  );
                  if (pdfFileStoreId != null) {
                    var newFileStore = await _fileController.getFiles(
                      fileStoreIds: pdfFileStoreId,
                      tenantId: tenant.code!,
                      token: _authController.token!.accessToken!,
                    );
                    if (isNotNullOrEmpty(
                      newFileStore,
                    )) {
                      await downloadController.starFileDownload(
                        url: newFileStore!.fileStoreIds![0].url!,
                        title: getLocalizedString(
                          i18.propertyTax.PAYMENT_DOWNLOAD_RECEIPT,
                          module: Modules.PT,
                        ),
                      );
                    }
                  } else {
                    snackBar(
                      'Error',
                      'No file found!',
                      BaseConfig.redColor,
                    );
                  }
                }
                isDownloading[index] = false;
              },
            ),
          ),
          SizedBox(height: 10.h),
        ],
      ),
    );
  }

  // Widget _resendAnotherOtp() {
  //   return Center(
  //     child: Column(
  //       children: [
  //         const BigText(text: 'Resend another otp'),
  //         SizedBox(height: 10.h),
  //         AnimatedBuilder(
  //           animation: _animationController,
  //           builder: (context, child) {
  //             return Stack(
  //               alignment: Alignment.center,
  //               children: [
  //                 CustomPaint(
  //                   size: const Size(50, 50),
  //                   painter: ProgressPainter(
  //                     progress: _animationController.value,
  //                     gradient: const LinearGradient(
  //                       colors: [
  //                         BaseConfig.appThemeColor1,
  //                         BaseConfig.appThemeColor2,
  //                       ],
  //                       begin: Alignment.topCenter,
  //                       end: Alignment.bottomCenter,
  //                     ),
  //                   ),
  //                 ),
  //                 BigText(
  //                   text: '$_timerSeconds',
  //                 ),
  //               ],
  //             );
  //           },
  //         ),
  //       ],
  //     ),
  //   );
  // }

  _showOwnerHistoryDialogue(BuildContext context, Property? property) {
    final addressParts = [
      property?.address?.doorNo,
      property?.address?.street,
      property?.address?.locality?.name,
      property?.address?.city,
      property?.address?.pincode,
    ];

    final filteredAddressParts =
        addressParts.where((part) => isNotNullOrEmpty(part)).toList();
    final finalAddress = filteredAddressParts.join(', ');
    return showAdaptiveDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: BaseConfig.mainBackgroundColor,
        surfaceTintColor: BaseConfig.mainBackgroundColor,
        scrollable: false,
        title: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            MediumTextNotoSans(
              text: getLocalizedString(
                i18.propertyTax.OWNER_HISTORY,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            IconButton(
              icon: const Icon(
                Icons.close,
                color: BaseConfig.appThemeColor1,
              ),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        ),
        content: SizedBox(
          width: Get.width,
          height: Get.height * 0.6,
          child: SingleChildScrollView(
            child: Container(
              decoration: BoxDecoration(
                border: Border.all(color: BaseConfig.borderColor),
                borderRadius: BorderRadius.circular(10.r),
              ),
              child: Padding(
                padding: EdgeInsets.all(10.w),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.PT_DATE_OF_TRANSFER,
                        module: Modules.PT,
                      ),
                      text: property?.auditDetails?.createdTime
                              .toCustomDateFormat(pattern: 'dd/MMM/yyyy') ??
                          'N/A',
                    ),
                    const Divider(),
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.OWNER_NAME,
                      ),
                      text: property?.owners?.first.name ?? 'N/A',
                    ),
                    const Divider(),
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.OWNER_GENDER,
                        module: Modules.PT,
                      ),
                      text: property?.owners?.first.gender ?? 'N/A',
                    ),
                    const Divider(),
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.MOBILE_NUMBER,
                        module: Modules.PT,
                      ),
                      text: property?.owners?.first.mobileNumber ?? 'N/A',
                    ),
                    const Divider(),
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.GUARDIAN_NAME,
                        module: Modules.PT,
                      ),
                      text:
                          property?.owners?.first.fatherOrHusbandName ?? 'N/A',
                    ),
                    const Divider(),
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.RELATIONSHIP,
                        module: Modules.PT,
                      ),
                      text: property?.owners?.first.relationship ?? 'N/A',
                    ),
                    const Divider(),
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.PT_SPECIAL_OWNER_CATEGORY,
                        module: Modules.PT,
                      ),
                      text: property?.owners?.first.ownerType ?? 'N/A',
                    ),
                    const Divider(),
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.OWNER_EMAIL,
                        module: Modules.PT,
                      ),
                      text: (isNotNullOrEmpty(
                        property?.owners?.firstOrNull?.emailId,
                      ))
                          ? property!.owners!.first.emailId!
                          : 'N/A',
                    ),
                    const Divider(),
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.tlProperty.OWNERSHIP_ADDRESS,
                        module: Modules.PT,
                      ),
                      text: finalAddress,
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  // Widget _buildOwnerDetails(
  //   BuildContext context,
  //   Owner? owner,
  //   Property? property,
  // ) =>
  //     Column(
  //       children: [
  //         ColumnHeaderText(
  //           label: getLocalizedString(
  //             i18.propertyTax.OWNER_NAME,
  //             module: Modules.PT,
  //           ),
  //           text: owner?.name?.capitalize ?? 'N/A',
  //         ),
  //         SizedBox(height: 10.h),
  //         ColumnHeaderText(
  //           label: getLocalizedString(
  //             i18.propertyTax.OWNER_GENDER,
  //             module: Modules.PT,
  //           ),
  //           text: owner?.gender?.capitalize ?? 'N/A',
  //         ),
  //         SizedBox(height: 10.h),
  //         Row(
  //           children: [
  //             Expanded(
  //               child: ColumnHeaderText(
  //                 label: getLocalizedString(
  //                   i18.propertyTax.MOBILE_NUMBER,
  //                   module: Modules.PT,
  //                 ),
  //                 text: owner?.mobileNumber ?? 'N/A',
  //               ),
  //             ),
  //             // SizedBox(
  //             //   // width: 10,
  //             //   child: IconButton(
  //             //     onPressed: () {
  //             //       _editPhoneNoDialogue(context, owner);
  //             //     },
  //             //     icon: const Icon(Icons.edit),
  //             //   ),
  //             // ),
  //           ],
  //         ),
  //         SizedBox(height: 10.h),
  //         ColumnHeaderText(
  //           label: getLocalizedString(
  //             i18.propertyTax.GUARDIAN_NAME,
  //             module: Modules.PT,
  //           ),
  //           text: owner?.fatherOrHusbandName?.capitalize ?? 'N/A',
  //         ),
  //         SizedBox(height: 10.h),
  //         ColumnHeaderText(
  //           label: getLocalizedString(
  //             i18.tlProperty.OWNERSHIP_TYPE,
  //             module: Modules.PT,
  //           ),
  //           text: getLocalizedString(
  //             '${i18.propertyTax.PT_OWNERSHIP}${property?.ownershipCategory}',
  //             module: Modules.PT,
  //           ),
  //         ),
  //         SizedBox(height: 10.h),
  //         ColumnHeaderText(
  //           label: getLocalizedString(
  //             i18.propertyTax.RELATIONSHIP,
  //             module: Modules.PT,
  //           ),
  //           text: owner?.relationship?.capitalize ?? 'N/A',
  //         ),
  //         SizedBox(height: 10.h),
  //         ColumnHeaderText(
  //           label: getLocalizedString(
  //             i18.propertyTax.OWNER_EMAIL,
  //             module: Modules.PT,
  //           ),
  //           text: (owner?.emailId != null && owner!.emailId!.isNotEmpty)
  //               ? owner.emailId!
  //               : 'N/A',
  //         ),
  //         SizedBox(height: 10.h),
  //         ColumnHeaderText(
  //           label: getLocalizedString(
  //             i18.tlProperty.OWNERSHIP_ADDRESS,
  //             module: Modules.PT,
  //           ),
  //           text: owner?.permanentAddress ?? 'N/A',
  //         ),
  //         SizedBox(height: 10.h),
  //         ColumnHeaderText(
  //           label: getLocalizedString(
  //             i18.propertyTax.SPECIAL_CATEGORY,
  //             module: Modules.PT,
  //           ),
  //           text: owner?.ownerType ?? 'N/A',
  //         ),
  //         SizedBox(height: 10.h),
  //       ],
  //     );

  // _editPhoneNoDialogue(BuildContext context, Owner? owner) {
  //   return showAdaptiveDialog(
  //     context: context,
  //     builder: (context) => AlertDialog(
  //       backgroundColor: Theme.of(context).colorScheme.surface,
  //       surfaceTintColor: Theme.of(context).colorScheme.surface,
  //       scrollable: false,
  //       content: StatefulBuilder(
  //         builder: (context, setState) {
  //           return SizedBox(
  //             width: Get.width,
  //             height: Get.height * 0.6,
  //             child: SingleChildScrollView(
  //               child: Column(
  //                 crossAxisAlignment: CrossAxisAlignment.start,
  //                 children: [
  //                   SizedBox(height: 10.h),
  //                   Row(
  //                     children: [
  //                       Expanded(
  //                         child: MediumTextNotoSans(
  //                           text: getLocalizedString(
  //                             i18.propertyTax.PTUPNO_HEADER,
  //                             module: Modules.PT,
  //                           ),
  //                           fontWeight: FontWeight.w600,
  //                           size: 16.sp,
  //                         ),
  //                       ),
  //                       IconButton(
  //                         icon: const Icon(
  //                           Icons.close,
  //                           color: BaseConfig.appThemeColor1,
  //                         ),
  //                         onPressed: () {
  //                           Navigator.of(context).pop();
  //                         },
  //                       ),
  //                     ],
  //                   ),
  //                   SizedBox(height: 10.h),
  //                   SmallTextNotoSans(
  //                     text: getLocalizedString(
  //                       i18.propertyTax.PTUPNO_OWNER_NAME,
  //                       module: Modules.PT,
  //                     ),
  //                     fontWeight: FontWeight.w600,
  //                   ),
  //                   SmallTextNotoSans(text: owner?.name ?? 'N?A'),
  //                   const Divider(),
  //                   SizedBox(height: 10.h),
  //                   SmallTextNotoSans(
  //                     text: getLocalizedString(
  //                       i18.propertyTax.PTUPNO_CURR_NO,
  //                       module: Modules.PT,
  //                     ),
  //                     fontWeight: FontWeight.w600,
  //                   ),
  //                   SmallTextNotoSans(text: owner?.mobileNumber ?? 'N/A'),
  //                   const Divider(),
  //                   SizedBox(height: 10.h),
  //                   SmallTextNotoSans(
  //                     text: getLocalizedString(
  //                       i18.propertyTax.PT_UPDATE_NEWNO,
  //                       module: Modules.PT,
  //                     ),
  //                     fontWeight: FontWeight.w600,
  //                   ),
  //                   SizedBox(height: 10.h),
  //                   textFormFieldNormal(
  //                     context,
  //                     '91490xxxxx',
  //                     controller: _authController.mobileNoController.value,
  //                     prefixIcon: SizedBox(
  //                       width: 10.w,
  //                       child: Center(
  //                         child: Padding(
  //                           padding: EdgeInsets.all(8..w),
  //                           child: const SmallTextNotoSans(
  //                             text: '+91',
  //                             fontWeight: FontWeight.w600,
  //                           ),
  //                         ),
  //                       ),
  //                     ),
  //                     keyboardType: TextInputType.phone,
  //                     textInputAction: TextInputAction.done,
  //                     inputFormatters: [
  //                       FilteringTextInputFormatter.allow(
  //                         RegExp("[0-9]"),
  //                       ),
  //                     ],
  //                     validator: (value) {
  //                       if (value!.trim().isEmpty) {
  //                         return getLocalizedString(i18.login.ERROR_MOBILE);
  //                       }
  //                       if (!value.isValidPhone()) {
  //                         return 'Enter valid mobile number';
  //                       }
  //                       if (value.trim().length != 10) {
  //                         return 'Mobile number should be 10 digit';
  //                       }
  //                       return null;
  //                     },
  //                   ).paddingOnly(top: 4.h),
  //                   SizedBox(height: 30.h),
  //                   _isOtpSent
  //                       ? OTPTextField(
  //                           controller: _otpEditingController,
  //                           length: 6,
  //                           width: MediaQuery.of(context).size.width,
  //                           fieldWidth: 35.w,
  //                           style: GoogleFonts.notoSans().copyWith(
  //                                 fontSize: 16.sp,
  //                                 fontWeight: FontWeight.w500,
  //                               ),
  //                           textFieldAlignment: MainAxisAlignment.spaceEvenly,
  //                           fieldStyle: FieldStyle.box,
  //                           keyboardType: AppPlatforms.platformKeyboardType(),
  //                           inputFormatter: [
  //                             FilteringTextInputFormatter.allow(
  //                               RegExp(r'[0-9]'),
  //                             ),
  //                           ],
  //                           onChanged: (code) {
  //                             _otp = code.trim();
  //                             if (_otp.trim().length == 6) {
  //                               _validateOtp();
  //                             }
  //                             setState(() {});
  //                           },
  //                         )
  //                       : const SizedBox.shrink(),
  //                   _timerSeconds > 0
  //                       ? _resendAnotherOtp()
  //                       : Obx(
  //                           () => gradientBtn(
  //                             onPressed: _authController.isLoading.value
  //                                 ? null
  //                                 : _resendOTP,
  //                             text: _resendOtp
  //                                 ? 'Resend OTP'
  //                                 : getLocalizedString(
  //                                     i18.propertyTax.PTUPNO_SENDOTP,
  //                                     module: Modules.PT,
  //                                   ),
  //                             width: Get.width,
  //                             horizonPadding: 16.w,
  //                           ).paddingOnly(top: 30.h),
  //                         ),
  //                   Obx(
  //                     () => _authController.isLoading.value
  //                         ? const Center(
  //                             child: CircularProgressIndicator(),
  //                           ).paddingOnly(top: 20.h)
  //                         : const SizedBox.shrink(),
  //                   ),
  //                 ],
  //               ),
  //             ),
  //           );
  //         },
  //       ),
  //     ),
  //   );
  // }

  Widget _buildPropertyAssessment(Property? property) => Column(
        children: [
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.PROPERTY_TYPE,
              module: Modules.PT,
            ),
            text: getLocalizedString(
              '${i18.propertyTax.SUB_TYPE_RES}${property?.propertyType!.replaceAll('.', '_')}',
              module: Modules.PT,
            ),
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(i18.propertyTax.AREA, module: Modules.PT),
            text: '${property?.landArea?.toInt()} sq.ft',
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tlProperty.NO_OF_FLOOR,
              module: Modules.PT,
            ),
            text: parseNoOfFloors(property?.noOfFloors),
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.ELECTRICITY_ID,
              module: Modules.PT,
            ),
            text: property?.additionalDetails?.electricity ?? 'N/A',
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.ASSESSMENT_UID,
              module: Modules.PT,
            ),
            text: property?.additionalDetails?.uid ?? 'N/A',
          ),
          SizedBox(height: 10.h),
        ],
      );

  Widget _buildAddress({Address? address}) => Column(
        children: [
          ColumnHeaderText(
            label:
                getLocalizedString(i18.tlProperty.PINCODE, module: Modules.PT),
            text: address?.pincode ?? 'N/A',
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(i18.propertyTax.CITY, module: Modules.PT),
            text: getCityName(address?.tenantId ?? 'N/A'),
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.LOCALITY_OR_MOHALLA,
              module: Modules.PT,
            ),
            text: getLocalizedString(
              getLocality(
                tenant,
                address?.locality?.code ?? 'N/A',
              ),
            ),
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tlProperty.STREET_NAME,
              module: Modules.PT,
            ),
            text: address?.street ?? 'N/A',
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.HOUSE_NO,
              module: Modules.PT,
            ),
            text: address?.doorNo ?? 'N/A',
          ),
          SizedBox(height: 10.h),
        ],
      );

  Widget _buildGroundFloorCard(Unit? unit, int index) {
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
                MediumTextNotoSans(
                  text: getLocalizedString(
                    i18.propertyTax.UNIT,
                    module: Modules.PT,
                  ),
                  fontWeight: FontWeight.w600,
                  size: 16.sp,
                  color: BaseConfig.greyColor1,
                ),
                MediumTextNotoSans(
                  text: ' $index',
                  fontWeight: FontWeight.w600,
                  size: 16.sp,
                  color: BaseConfig.greyColor1,
                ),
              ],
            ),
            SizedBox(height: 10.h),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.UNIT_USAGE_TYPE,
                module: Modules.PT,
              ),
              text: unit?.usageCategory ?? 'N/A',
            ),
            SizedBox(height: 10.h),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.UNIT_OCCUPANY_TYPE,
                module: Modules.PT,
              ),
              text: unit?.occupancyType ?? 'N/A',
            ),
            SizedBox(height: 10.h),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.UNIT_BUILTUP_AREA,
                module: Modules.PT,
              ),
              text: '${unit?.constructionDetail?.builtUpArea} sq.ft',
            ),
            SizedBox(height: 10.h),
          ],
        ),
      ),
    );
  }
}
