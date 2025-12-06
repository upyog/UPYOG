import 'dart:async';

import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/bpa_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/inbox_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/model/citizen/bpa_model/bpa_model.dart';
import 'package:mobile_app/model/citizen/bpa_model/noc/noc_model.dart';
import 'package:mobile_app/model/citizen/bpa_model/scrutiny_model.dart';
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/screens/citizen/payments/payment_screen.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/localization/localize_utils.dart';
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
import 'package:rxdart/rxdart.dart';
import 'package:url_launcher/url_launcher.dart';

class BuildingDetailScreen extends StatefulWidget {
  const BuildingDetailScreen({super.key});

  @override
  State<BuildingDetailScreen> createState() => _BuildingDetailScreenState();
}

class _BuildingDetailScreenState extends State<BuildingDetailScreen> {
  final _authController = Get.find<AuthController>();
  final _bpaController = Get.find<BpaController>();
  final _timelineController = Get.find<TimelineController>();
  final _fileController = Get.find<FileController>();
  final _inboxController = Get.find<InboxController>();
  final _downloadController = Get.find<DownloadController>();
  final _paymentController = Get.find<PaymentController>();

  var inspectionDocsRx = BehaviorSubject<FileStore?>();
  var nocDocsRx = BehaviorSubject<FileStore?>();

  final _isLoading = false.obs;
  bool _isTimelineFetch = false;
  bool _isNocFetch = false;
  bool _isFeeFetch = false;
  bool _isFileFetch = false;

  late BpaElement newBpaData;
  late TenantTenant tenantCity;

  // Get Edrc by edrcNo
  EdcrDetail? get _edrcData =>
      _bpaController.getEdcrList(edcrNo: newBpaData.edcrNumber);

  @override
  void initState() {
    super.initState();
    final args = Get.arguments;
    newBpaData = args['newBpaData'];

    _bpaController.bpaElement = newBpaData;
    _init();
  }

  @override
  dispose() {
    inspectionDocsRx.close();
    nocDocsRx.close();
    super.dispose();
  }

  Future<void> _init() async {
    if (_authController.isValidUser) {
      _isLoading.value = true;
      tenantCity = await getCityTenant();

      // await _bpaController.getIndividualBpaApplications(
      //   token: _authController.token?.accessToken,
      //   applicationNo: newBpaData.applicationNo,
      //   tenantId: tenantCity.code!,
      // );
      await _bpaController.getIndividualEdcrApplications(
        token: _authController.token?.accessToken.toString(),
        edcrNo: newBpaData.edcrNumber,
        tenantId: newBpaData.tenantId!,
      );

      if (newBpaData.status == BpaStatus.CITIZEN_APPROVAL_INPROCESS.name) {
        await _getTimeline();
        await _getWorkflow();
      }

      _isLoading.value = false;
    }
  }

  Future<void> getFiles() async {
    _fileController
        .getFiles(
      tenantId: BaseConfig.STATE_TENANT_ID,
      token: _authController.token!.accessToken!,
      fileStoreIds: getFileStoreIds(),
    )
        .then((value) {
      setState(() {
        _isFileFetch = true;
      });
      return value;
    });
  }

  String getFileStoreIds() {
    if (newBpaData.documents == null) {
      return '';
    }
    List fileIds = [];
    for (var element in newBpaData.documents!) {
      fileIds.add(element.fileStoreId);
    }
    return fileIds.join(', ');
  }

  Future<void> _getNoc() async {
    await _bpaController
        .getBpaNocDetail(
      token: _authController.token!.accessToken!,
      sourceRefId: newBpaData.applicationNo!,
      tenantId: newBpaData.tenantId!,
    )
        .then((value) {
      setState(() {
        _isNocFetch = true;
      });
    });
  }

  Future<void> getInspectionDocs(String ids) async {
    try {
      final fileStore = await _fileController.getFiles(
        tenantId: BaseConfig.STATE_TENANT_ID,
        token: _authController.token!.accessToken!,
        fileStoreIds: ids,
      );

      inspectionDocsRx.sink.add(fileStore);
    } catch (e) {
      dPrint('Error in getting inspection docs: $e');
    }
  }

  Future<void> getNocDocs(fileId) async {
    try {
      final fileStore = await _fileController.getFiles(
        tenantId: BaseConfig.STATE_TENANT_ID,
        token: _authController.token!.accessToken!,
        fileStoreIds: fileId,
      );
      nocDocsRx.sink.add(fileStore);
    } catch (e) {
      dPrint('Error in getting noc docs: $e');
    }
  }

  Future<void> _getFee() async {
    await _bpaController
        .getBpaFeeDetail(
      token: _authController.token?.accessToken,
      consumerCodes: newBpaData.applicationNo,
      isEmployee: true,
      tenantId: newBpaData.tenantId!,
      businessService: getBusinessServiceByType(newBpaData.businessService!),
    )
        .then((value) {
      setState(() {
        _isFeeFetch = true;
      });
    });
  }

  Future<void> _getTimeline() async {
    await _timelineController
        .getTimelineHistory(
      token: _authController.token!.accessToken!,
      tenantId: newBpaData.tenantId.toString(),
      businessIds: newBpaData.applicationNo!,
    )
        .then((value) {
      setState(() {
        _isTimelineFetch = true;
      });
    });
  }

  Future<void> _getWorkflow() async {
    try {
      await _timelineController.getWorkFlow(
        token: _authController.token!.accessToken!,
        tenantId: newBpaData.tenantId.toString(),
        workFlow: newBpaData.businessService!,
      );
    } catch (e) {
      dPrint('Error in getting workflow: $e');
    }
  }

  bool isPaymentAvailable() {
    var status = {
      BpaStatus.PENDING_FEE.name,
      BpaStatus.PENDING_APPL_FEE_PAYMENT.name,
      BpaStatus.PENDING_SANC_FEE_PAYMENT.name,
      BpaStatus.PENDING_APPL_FEE.name,
      BpaStatus.PENDING_SANC_FEE.name,
    };

    return status.contains(newBpaData.status);
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

  Widget _makePayment(status) {
    final isBPA = newBpaData.businessService == BusinessServicesEmp.BPA.name;
    return FilledButtonApp(
      radius: 0,
      width: Get.width,
      text: (isBPA && status == BpaStatus.PENDING_APPL_FEE.name)
          ? 'Pay Application Fee'
          : (isBPA && status == BpaStatus.PENDING_SANC_FEE_PAYMENT.name)
              ? 'Pay Sanction Fee'
              : getLocalizedString(
                  i18.common.MAKE_PAYMENT,
                ),
      onPressed: () => goPayment(newBpaData),
      circularColor: BaseConfig.fillAppBtnCircularColor,
    );
  }

  Widget _buildBottomBarTakeAction({required Orientation o}) {
    return Obx(
      () => Container(
        color: BaseConfig.shadeAmberColor.withValues(alpha: 0.3),
        height:
            _timelineController.isTermsConditionsAccepted.value ? 120.h : 60.h,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Obx(
                  () => Checkbox.adaptive(
                    activeColor: BaseConfig.appThemeColor1,
                    value: _timelineController.isTermsConditionsAccepted.value,
                    onChanged: (value) {
                      _timelineController.isTermsConditionsAccepted.value =
                          value!;
                    },
                  ),
                ),
                Expanded(
                  child: RichText(
                    text: TextSpan(
                      children: <TextSpan>[
                        TextSpan(
                          text: '${getLocalizedString(
                            i18.building.I_AGREE,
                            module: Modules.BPA,
                          )} ',
                          style: TextStyle(
                            color: BaseConfig.textColor,
                            fontSize: o == Orientation.portrait ? 16.sp : 12.sp,
                            fontWeight: FontWeight.w400,
                          ),
                        ),
                        TextSpan(
                          text: getLocalizedString(
                            i18.building.TERMS_AND_CONDITIONS,
                            module: Modules.BPA,
                          ),
                          style: TextStyle(
                            color: BaseConfig.appThemeColor1,
                            decoration: TextDecoration.none,
                            fontSize: o == Orientation.portrait ? 16.sp : 12.sp,
                            fontWeight: FontWeight.w500,
                          ),
                          recognizer: TapGestureRecognizer()
                            ..onTap = () {
                              showCustomDialogue(
                                context,
                                height: 0.55,
                                child: SingleChildScrollView(
                                  child: Column(
                                    children: [
                                      MediumTextNotoSans(
                                        text: getLocalizedString(
                                          i18.building.TERMS_AND_CONDITIONS,
                                          module: Modules.BPA,
                                        ),
                                        fontWeight: FontWeight.bold,
                                      ),
                                      const Divider(),
                                      SmallTextNotoSans(
                                        text: '${getLocalizedString(
                                          i18.building
                                              .CITIZEN_DECLARATION_LABEL_1,
                                          module: Modules.BPA,
                                        )}${getLocalizedString(
                                          i18.building
                                              .CITIZEN_DECLARATION_LABEL_2,
                                          module: Modules.BPA,
                                        )}',
                                      ),
                                      SizedBox(height: 20.h),
                                      FilledButtonApp(
                                        height: 40.h,
                                        verticalPadding: 8.h,
                                        text: getLocalizedString(
                                          i18.building.GO_BACK,
                                          module: Modules.BPA,
                                        ),
                                        onPressed: () => Get.back(),
                                      ),
                                    ],
                                  ),
                                ),
                              );
                            },
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
            Obx(
              () => _timelineController.isTermsConditionsAccepted.value
                  ? Container(
                      height: 44.h,
                      width: Get.width,
                      margin: EdgeInsets.symmetric(
                        horizontal: o == Orientation.portrait ? 12.w : 4.w,
                        vertical: 8.h,
                      ),
                      child: PopupMenuButton(
                        style: FilledButton.styleFrom(
                          backgroundColor: BaseConfig.appThemeColor1,
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(
                              o == Orientation.portrait ? 12.w : 6.w,
                            ),
                          ),
                        ),
                        icon: MediumTextNotoSans(
                          text: getLocalizedString(i18.common.TAKE_ACTION),
                          size: o == Orientation.portrait ? 14.sp : 8.sp,
                          color: BaseConfig.mainBackgroundColor,
                          fontWeight: FontWeight.w600,
                        ),
                        itemBuilder: (context) => _timelineController
                            .workflowBusinessServices
                            .businessServices!
                            .first
                            .states!
                            .where(
                              (s) => s.applicationStatus == newBpaData.status,
                            )
                            .first
                            .actions!
                            .map(
                              (action) => PopupMenuItem<String>(
                                value: action.action,
                                child: SmallTextNotoSans(
                                  text: LocalizeUtils.getTakeActionLocal(
                                    action.action,
                                    workflowCode: newBpaData.businessService!
                                            .contains('_')
                                        ? newBpaData.businessService!
                                            .split('_')
                                            .first
                                        : newBpaData.businessService!,
                                    module: Modules.BPA,
                                    isCitizen: true,
                                  ),
                                  color: BaseConfig.textColor,
                                  fontWeight: FontWeight.w600,
                                  size:
                                      o == Orientation.portrait ? 14.sp : 8.sp,
                                ),
                              ),
                            )
                            .toList(),
                        onSelected: (value) async {
                          //TODO: Take Action
                          dPrint(value);

                          // Get the next state of the action
                          // String uuid = _timelineController
                          //         .workflowBusinessServices.businessServices?.first.states
                          //         ?.where((s) => s.uuid == statusMap.statusId)
                          //         .first
                          //         .actions
                          //         ?.where((a) => a.action == value)
                          //         .first
                          //         .nextState ??
                          //     '';

                          // dPrint('UUID: $uuid');

                          // if (uuid.isEmpty) {
                          //   snackBar(
                          //     'InComplete',
                          //     'Next State is Empty',
                          //     Colors.green,
                          //   );
                          //   return;
                          // }

                          // if (value != BaseAction.reject.name &&
                          //     value != BaseAction.sendBackToCitizen.name) {
                          //   await _timelineController.getEmployees(
                          //     token: _authController.token!.accessToken!,
                          //     tenantId: _item!.businessObject!.tenantId!,
                          //     uuid: uuid,
                          //   );
                          // }

                          if (!context.mounted) return;

                          _inboxController.actionDialogue(
                            context,
                            workFlowId: newBpaData.businessService!,
                            action: value,
                            module: Modules.BPA,
                            sectionType: ModulesEmp.BPA_SERVICES,
                            tenantId: newBpaData.tenantId!,
                            businessService: BusinessService.OBPS,
                            isCitizen: true,
                          );
                        },
                      ),
                    )
                  : const SizedBox.shrink(),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        title: getLocalizedString(
          i18.common.APPLICATION_DETAILS,
        ),
        onPressed: () {
          Navigator.of(context).pop();
        },
      ),
      bottomNavigationBar: Obx(
        () => _isLoading.value
            ? showCircularIndicator()
            : newBpaData.status == BpaStatus.CITIZEN_APPROVAL_INPROCESS.name
                ? _buildBottomBarTakeAction(o: o)
                : isPaymentAvailable()
                    ? _makePayment(newBpaData.status)
                    : const SizedBox.shrink(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Obx(
          () => _isLoading.value
              ? showCircularIndicator()
              : SingleChildScrollView(
                  physics: AppPlatforms.platformPhysics(),
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            TextButton(
                              onPressed: () async {
                                if (!_isTimelineFetch) {
                                  await _getTimeline();
                                }
                                TimelineHistoryApp.buildTimelineDialogue(
                                  context,
                                  tenantId: newBpaData.tenantId!,
                                );
                              },
                              child: MediumText(
                                text: getLocalizedString(
                                  i18.common.TIMELINE,
                                  module: Modules.BPA,
                                ),
                                color: BaseConfig.redColor1,
                              ),
                            ),
                            SizedBox(width: 10.w),
                            if (_bpaController.isDownloadAvailable(
                              newBpaData.status,
                              newBpaData.businessService,
                            ))
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
                                  if (newBpaData.businessService ==
                                          BusinessServicesEmp.BPA.name &&
                                      (newBpaData.status == BpaStatus.APPROVED.name ||
                                          newBpaData.status ==
                                              BpaStatus
                                                  .DOC_VERIFICATION_INPROGRESS
                                                  .name ||
                                          newBpaData.status ==
                                              BpaStatus
                                                  .FIELD_INSPECTION_INPROGRESS
                                                  .name ||
                                          newBpaData.status ==
                                              BpaStatus
                                                  .NOC_VERIFICATION_INPROGRESS
                                                  .name))
                                    PopupMenuItem<String>(
                                      value: getLocalizedString(
                                        i18.common.BPA_APP_FEE_RECEIPT,
                                      ),
                                      child: MediumText(
                                        text: getLocalizedString(
                                          i18.common.BPA_APP_FEE_RECEIPT,
                                        ),
                                      ),
                                      onTap: () async {
                                        final payments =
                                            await _paymentController
                                                .getPaymentDetails(
                                          tenantId: newBpaData.tenantId!,
                                          consumerCodes:
                                              newBpaData.applicationNo!,
                                          token: _authController
                                              .token!.accessToken!,
                                          businessService: BusinessService
                                              .BPA_NC_SAN_FEE.name,
                                          isEmployee: true,
                                        );

                                        final payment = payments?.firstOrNull;

                                        final pdfFileStoreId =
                                            await _fileController
                                                .getPdfServiceFile(
                                          tenantId: BaseConfig
                                              .STATE_TENANT_ID, // For Test ENV
                                          token: _authController
                                              .token!.accessToken!,
                                          payment: payment,
                                          key: PdfKey.bpa_receipt,
                                        );

                                        if (isNotNullOrEmpty(
                                          pdfFileStoreId,
                                        )) {
                                          final newFileStore =
                                              await _fileController.getFiles(
                                            fileStoreIds: pdfFileStoreId,
                                            tenantId:
                                                BaseConfig.STATE_TENANT_ID,
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
                                                i18.common.BPA_APP_FEE_RECEIPT,
                                              ),
                                            );
                                          }
                                        }
                                      },
                                    ),

                                  //FOR BPA_LOW & BPA_OC
                                  if ((newBpaData.businessService ==
                                              BusinessServicesEmp
                                                  .BPA_LOW.name ||
                                          newBpaData.businessService ==
                                              BusinessServicesEmp
                                                  .BPA_OC.name) &&
                                      (newBpaData.status ==
                                              BpaStatus
                                                  .DOC_VERIFICATION_INPROGRESS.name ||
                                          newBpaData.status ==
                                              BpaStatus
                                                  .FIELD_INSPECTION_INPROGRESS.name ||
                                          newBpaData.status ==
                                              BpaStatus
                                                  .NOC_VERIFICATION_INPROGRESS
                                                  .name ||
                                          newBpaData.status ==
                                              BpaStatus.APPROVED.name ||
                                          newBpaData.status ==
                                              BpaStatus
                                                  .APPROVAL_INPROGRESS.name))
                                    PopupMenuItem<String>(
                                      value: getLocalizedString(
                                        i18.common.BPA_APP_FEE_RECEIPT,
                                      ),
                                      child: MediumText(
                                        text: getLocalizedString(
                                          i18.common.BPA_APP_FEE_RECEIPT,
                                        ),
                                      ),
                                      onTap: () async {
                                        final payments =
                                            await _paymentController
                                                .getPaymentDetails(
                                          tenantId: newBpaData.tenantId!,
                                          consumerCodes:
                                              newBpaData.applicationNo!,
                                          token: _authController
                                              .token!.accessToken!,
                                          businessService:
                                              newBpaData.businessService ==
                                                      BusinessServicesEmp
                                                          .BPA_LOW.name
                                                  ? BusinessService
                                                      .BPA_LOW_RISK_PERMIT_FEE
                                                      .name
                                                  : BusinessService
                                                      .BPA_NC_OC_APP_FEE.name,
                                          isEmployee: true,
                                        );

                                        final payment = payments?.firstOrNull;

                                        // For BPA_OC
                                        if (newBpaData.businessService ==
                                                BusinessServicesEmp
                                                    .BPA_OC.name &&
                                            isNotNullOrEmpty(
                                              payment?.fileStoreId,
                                            )) {
                                          print('BPA_OC');
                                          final newFileStore =
                                              await _fileController.getFiles(
                                            fileStoreIds: payment!.fileStoreId!,
                                            tenantId:
                                                BaseConfig.STATE_TENANT_ID,
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
                                                i18.common.BPA_APP_FEE_RECEIPT,
                                              ),
                                            );
                                          }
                                        }

                                        if (newBpaData.businessService ==
                                                BusinessServicesEmp
                                                    .BPA_LOW.name &&
                                            isNotNullOrEmpty(
                                              payment?.fileStoreId,
                                            )) {
                                          //For BPA_LOW
                                          final pdfFileStoreId =
                                              await _fileController
                                                  .getPdfServiceFile(
                                            tenantId: BaseConfig
                                                .STATE_TENANT_ID, // For Test ENV
                                            token: _authController
                                                .token!.accessToken!,
                                            payment: payment,
                                            key: PdfKey.bpa_receipt,
                                          );

                                          if (isNotNullOrEmpty(
                                            pdfFileStoreId,
                                          )) {
                                            print('BPA_LOW');
                                            final newFileStore =
                                                await _fileController.getFiles(
                                              fileStoreIds: pdfFileStoreId,
                                              tenantId:
                                                  BaseConfig.STATE_TENANT_ID,
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
                                                  i18.common
                                                      .BPA_APP_FEE_RECEIPT,
                                                ),
                                              );
                                            }
                                          }
                                        }
                                      },
                                    ),

                                  //BPA_OC Comparison report
                                  if (newBpaData.businessService ==
                                          BusinessServicesEmp.BPA_OC.name &&
                                      (newBpaData.status ==
                                              BpaStatus
                                                  .CITIZEN_APPROVAL_INPROCESS
                                                  .name ||
                                          newBpaData.status ==
                                              BpaStatus.INPROGRESS.name ||
                                          newBpaData.status ==
                                              BpaStatus.PENDING_APPL_FEE.name ||
                                          newBpaData.status ==
                                              BpaStatus.APPROVED.name ||
                                          newBpaData.status ==
                                              BpaStatus
                                                  .DOC_VERIFICATION_INPROGRESS
                                                  .name ||
                                          newBpaData.status ==
                                              BpaStatus
                                                  .FIELD_INSPECTION_INPROGRESS
                                                  .name ||
                                          newBpaData.status ==
                                              BpaStatus
                                                  .NOC_VERIFICATION_INPROGRESS
                                                  .name ||
                                          newBpaData.status ==
                                              BpaStatus
                                                  .APPROVAL_INPROGRESS.name ||
                                          newBpaData.status ==
                                              BpaStatus.APPROVED.name))
                                    PopupMenuItem<String>(
                                      value: 'Comparison Report',
                                      child: const MediumText(
                                        text: 'Comparison Report',
                                      ),
                                      onTap: () async {
                                        final bpaDCR = await _bpaController
                                            .getIndividualBpaAppDCR(
                                          tenantId: newBpaData.tenantId!,
                                          approvalNo: newBpaData
                                              .additionalDetails!.permitNumber!,
                                          token: _authController
                                              .token!.accessToken!,
                                        );

                                        if (!isNotNullOrEmpty(
                                          bpaDCR?.bpaele?.firstOrNull,
                                        )) {
                                          snackBar(
                                            'Error',
                                            'Something went wrong!',
                                            BaseConfig.redColor,
                                          );
                                          return;
                                        }

                                        final comparison = await _bpaController
                                            .getComparisonData(
                                          token: _authController
                                              .token!.accessToken!,
                                          tenantId: newBpaData.tenantId!,
                                          ocdcrNumber: newBpaData.edcrNumber!,
                                          edcrNumber:
                                              bpaDCR!.bpaele!.first.edcrNumber!,
                                          propertyId: bpaDCR.bpaele!.first
                                              .additionalDetails!.propertyId!,
                                        );

                                        if (isNotNullOrEmpty(
                                          comparison?.comparisonDetail
                                              ?.comparisonReport,
                                        )) {
                                          await launchURL(
                                            comparison!.comparisonDetail!
                                                .comparisonReport!,
                                            mode:
                                                LaunchMode.externalApplication,
                                          );
                                        }
                                      },
                                    ),

                                  if (newBpaData.businessService ==
                                          BusinessServicesEmp.BPA.name &&
                                      newBpaData.status ==
                                          BpaStatus.APPROVED.name)
                                    PopupMenuItem<String>(
                                      value: getLocalizedString(
                                        i18.common.BPA_PERMIT_ORDER,
                                      ),
                                      child: MediumText(
                                        text: getLocalizedString(
                                          i18.common.BPA_PERMIT_ORDER,
                                        ),
                                      ),
                                      onTap: () async {
                                        final pdfFileStoreId =
                                            await _fileController
                                                .getPdfServiceFile(
                                          tenantId: newBpaData.tenantId!,
                                          token: _authController
                                              .token!.accessToken!,
                                          bpaElement: newBpaData,
                                          key: PdfKey.building_permit,
                                        );

                                        if (isNotNullOrEmpty(pdfFileStoreId)) {
                                          final newFileStore =
                                              await _fileController.getFiles(
                                            fileStoreIds: pdfFileStoreId,
                                            tenantId: newBpaData.tenantId!,
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
                                                i18.common.BPA_PERMIT_ORDER,
                                              ),
                                            );
                                          }
                                        }
                                      },
                                    ),

                                  if (newBpaData.businessService ==
                                          BusinessServicesEmp.BPA.name &&
                                      newBpaData.status ==
                                          BpaStatus.APPROVED.name)
                                    PopupMenuItem<String>(
                                      value: getLocalizedString(
                                        i18.building.SAN_FEE_RECEIPT,
                                        module: Modules.BPA,
                                      ),
                                      child: MediumText(
                                        text: getLocalizedString(
                                          i18.building.SAN_FEE_RECEIPT,
                                          module: Modules.BPA,
                                        ),
                                      ),
                                      onTap: () async {
                                        final payments =
                                            await _paymentController
                                                .getPaymentDetails(
                                          tenantId: newBpaData.tenantId!,
                                          consumerCodes:
                                              newBpaData.applicationNo!,
                                          token: _authController
                                              .token!.accessToken!,
                                          businessService: BusinessService
                                              .BPA_NC_SAN_FEE.name,
                                          isEmployee: true,
                                        );

                                        final payment = payments?.firstOrNull;

                                        if (isNotNullOrEmpty(
                                          payment?.fileStoreId,
                                        )) {
                                          final newFileStore =
                                              await _fileController.getFiles(
                                            fileStoreIds: payment!.fileStoreId!,
                                            tenantId:
                                                BaseConfig.STATE_TENANT_ID,
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
                                                i18.building.SAN_FEE_RECEIPT,
                                                module: Modules.BPA,
                                              ),
                                            );
                                          }
                                        }
                                      },
                                    ),

                                  //Occupancy Certificate
                                  if (newBpaData.businessService ==
                                          BusinessServicesEmp.BPA_OC.name &&
                                      newBpaData.status ==
                                          BpaStatus.APPROVED.name)
                                    PopupMenuItem<String>(
                                      value: 'Occupancy Certificate',
                                      child: const MediumText(
                                        text: 'Occupancy Certificate',
                                      ),
                                      onTap: () async {
                                        final pdfFileStoreId =
                                            await _fileController
                                                .getPdfServiceFile(
                                          tenantId: newBpaData.tenantId!,
                                          token: _authController
                                              .token!.accessToken!,
                                          bpaElement: newBpaData,
                                          key: PdfKey.occupancy_certificate,
                                        );

                                        if (isNotNullOrEmpty(pdfFileStoreId)) {
                                          final newFileStore =
                                              await _fileController.getFiles(
                                            fileStoreIds: pdfFileStoreId,
                                            tenantId: newBpaData.tenantId!,
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
                                              title: 'Occupancy Certificate',
                                            );
                                          }
                                        }
                                      },
                                    ),
                                ],
                              ),
                          ],
                        ),
                        _buildAppNo(),
                        const SizedBox(
                          height: 10,
                        ),
                        _buildLicenseDetail(),
                        const SizedBox(
                          height: 10,
                        ),
                        BuildCard(
                          padding: 0,
                          child: Column(
                            children: [
                              BuildExpansion(
                                title: getLocalizedString(
                                  i18.building.PLOT_DETAILS,
                                  module: Modules.BPA,
                                ),
                                children: [
                                  ColumnHeaderText(
                                    label: getLocalizedString(
                                      i18.building.PLOT_AREA,
                                      module: Modules.BPA,
                                    ),
                                    text: _edrcData
                                                ?.planDetail
                                                ?.planInfoProperties
                                                ?.plotAreaM2 !=
                                            null
                                        ? '${_edrcData?.planDetail?.planInfoProperties!.plotAreaM2} sq.ft'
                                        : "N/A",
                                  ),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                  ColumnHeaderText(
                                    label: getLocalizedString(
                                      i18.building.PLOT_NUMBER,
                                      module: Modules.BPA,
                                    ),
                                    text: _edrcData?.planDetail
                                            ?.planInfoProperties!.plotNo ??
                                        "N/A",
                                  ),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                  ColumnHeaderText(
                                    label: getLocalizedString(
                                      i18.building.KHATA_NUMBER,
                                      module: Modules.BPA,
                                    ),
                                    text: _edrcData?.planDetail
                                            ?.planInfoProperties?.khataNo ??
                                        "N/A",
                                  ),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                  ColumnHeaderText(
                                    label: getLocalizedString(
                                      i18.building.HOLDING_NUM,
                                      module: Modules.BPA,
                                    ),
                                    text: newBpaData
                                            .additionalDetails?.holdingNo ??
                                        "N/A",
                                  ),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                  ColumnHeaderText(
                                    label: getLocalizedString(
                                      i18.building.LAND_REGIS_DETAIL,
                                      module: Modules.BPA,
                                    ),
                                    text: newBpaData.additionalDetails
                                            ?.registrationDetails ??
                                        "N/A",
                                  ),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                  ColumnHeaderText(
                                    label: getLocalizedString(
                                      i18.building.BOUNDARY_WALL_LEN,
                                      module: Modules.BPA,
                                    ),
                                    text: newBpaData.additionalDetails
                                            ?.boundaryWallLength ??
                                        "N/A",
                                  ),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                ],
                              ),
                              BuildExpansion(
                                title: getLocalizedString(
                                  i18.building.SCRUTINY_DETAILS,
                                  module: Modules.BPA,
                                ),
                                children: [
                                  _scrutinyDetails(),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                ],
                              ),
                              BuildExpansion(
                                title: getLocalizedString(
                                  i18.building.PROPOSED_BUILDING_ABS,
                                  module: Modules.BPA,
                                ),
                                children: [
                                  _buildingProposedAbstractWidget(),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                ],
                              ),
                              BuildExpansion(
                                title: getLocalizedString(
                                  i18.building.BLOCKWISE_OCCUPANCY,
                                  module: Modules.BPA,
                                ),
                                children: [
                                  _blockOccupancy(),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                ],
                              ),
                              //---------------------Location Details-------------------------------
                              BuildExpansion(
                                title: getLocalizedString(
                                  i18.building.LOCATION_DETAILS,
                                  module: Modules.BPA,
                                ),
                                children: [
                                  ColumnHeaderText(
                                    label: getLocalizedString(
                                      i18.building.PINCODE,
                                      module: Modules.BPA,
                                    ),
                                    text:
                                        newBpaData.landInfo?.address?.pincode ??
                                            "N/A",
                                  ),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                  ColumnHeaderText(
                                    label: getLocalizedString(
                                      i18.building.CITY,
                                      module: Modules.BPA,
                                    ),
                                    text: isNotNullOrEmpty(
                                      newBpaData.landInfo?.address?.tenantId,
                                    )
                                        ? getCityName(
                                            newBpaData
                                                .landInfo!.address!.tenantId!,
                                          )
                                        : "N/A",
                                  ),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                  ColumnHeaderText(
                                    label: getLocalizedString(
                                      i18.building.LOCALITY,
                                      module: Modules.BPA,
                                    ),
                                    text: getLocalizedString(
                                      getLocality(
                                        tenantCity,
                                        newBpaData.landInfo?.address?.locality
                                                ?.code ??
                                            "",
                                      ),
                                    ),
                                  ),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                  ColumnHeaderText(
                                    label: getLocalizedString(
                                      i18.building.STREET,
                                      module: Modules.BPA,
                                    ),
                                    text:
                                        newBpaData.landInfo?.address?.street ??
                                            "N/A",
                                  ),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                  ColumnHeaderText(
                                    label: getLocalizedString(
                                      i18.building.LANDMARK,
                                      // module: Modules.BPA,
                                    ),
                                    text: newBpaData
                                            .landInfo?.address?.landmark ??
                                        "N/A",
                                  ),
                                  SizedBox(
                                    height: 10.h,
                                  ),
                                ],
                              ),
                              //---------------------Owner Details-------------------------------
                              if (isNotNullOrEmpty(newBpaData.landInfo?.owners))
                                BuildExpansion(
                                  title: getLocalizedString(
                                    i18.building.OWNER_DETAILS,
                                    module: Modules.BPA,
                                  ),
                                  children: [
                                    // SizedBox(
                                    //   height: Get.height * 0.2,
                                    //   child:
                                    ListView.builder(
                                      shrinkWrap: true,
                                      physics:
                                          const AlwaysScrollableScrollPhysics(),
                                      itemCount:
                                          newBpaData.landInfo?.owners?.length,
                                      itemBuilder: (context, i) {
                                        final owner =
                                            newBpaData.landInfo?.owners?[i];
                                        return Column(
                                          children: [
                                            ColumnHeaderText(
                                              label: getLocalizedString(
                                                i18.common.NAME,
                                              ),
                                              text: owner?.name ?? "N/A",
                                            ),
                                            const SizedBox(
                                              height: 10,
                                            ),
                                            ColumnHeaderText(
                                              label: getLocalizedString(
                                                i18.building.GENDER,
                                                module: Modules.BPA,
                                              ),
                                              text: owner?.gender ?? "N/A",
                                            ),
                                            const SizedBox(
                                              height: 10,
                                            ),
                                            ColumnHeaderText(
                                              label: getLocalizedString(
                                                i18.common.MOBILE_NUMBER,
                                              ),
                                              text:
                                                  owner?.mobileNumber ?? "N/A",
                                            ),
                                            const SizedBox(
                                              height: 10,
                                            ),
                                            ColumnHeaderText(
                                              label: getLocalizedString(
                                                i18.common.EMAIL_ID,
                                              ),
                                              text: isNotNullOrEmpty(
                                                owner?.emailId,
                                              )
                                                  ? owner!.emailId!
                                                  : "N/A",
                                            ),
                                            const SizedBox(
                                              height: 10,
                                            ),
                                            ColumnHeaderText(
                                              label: getLocalizedString(
                                                i18.building.PRIMARY_OWNER,
                                                module: Modules.BPA,
                                              ),
                                              text: owner?.isPrimaryOwner
                                                      .toString() ??
                                                  "N/A",
                                            ),
                                            const Divider(),
                                            const SizedBox(
                                              height: 20,
                                            ),
                                          ],
                                        );
                                      },
                                    ),
                                    // ),
                                  ],
                                ),
                              //---------------------Document Details-------------------------------
                              if (isNotNullOrEmpty(newBpaData.documents))
                                BuildExpansion(
                                  title: getLocalizedString(
                                    i18.building.DOCUMENT_DETAILS,
                                    module: Modules.BPA,
                                  ),
                                  onExpansionChanged: (p0) async {
                                    if (p0 && !_isFileFetch) {
                                      await getFiles();
                                    }
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
                                            () => getFiles(),
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
                              //---------------------Inspection Report-------------------------------
                              if (isNotNullOrEmpty(
                                newBpaData
                                    .additionalDetails?.fieldInspectionPending,
                              ))
                                BuildExpansion(
                                  title: getLocalizedString(
                                    i18.building.INSPECTION_REPORT,
                                    module: Modules.BPA,
                                  ),
                                  children: [
                                    ListView.builder(
                                      shrinkWrap: true,
                                      itemCount: newBpaData.additionalDetails
                                          ?.fieldInspectionPending?.length,
                                      physics:
                                          const NeverScrollableScrollPhysics(),
                                      itemBuilder: (context, index) {
                                        final report = newBpaData
                                            .additionalDetails
                                            ?.fieldInspectionPending?[index];

                                        final docs = report?.docs;

                                        return _buildInspectionCard(
                                          report,
                                          docs,
                                          index,
                                        );
                                      },
                                    ),
                                    const SizedBox(
                                      height: 10,
                                    ),
                                  ],
                                ),
                              const SizedBox(
                                height: 10,
                              ),
                              //TODO: Noc
                              BuildExpansion(
                                title: getLocalizedString(
                                  i18.building.NOC_DETAILS,
                                  module: Modules.BPA,
                                ),
                                onExpansionChanged: (p0) async {
                                  if (p0 && !_isNocFetch) {
                                    await _getNoc();
                                  }

                                  if (p0 && !_isFeeFetch) {
                                    await _getFee();
                                  }
                                },
                                children: [_buildNocWidget()],
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

  Widget _buildNocWidget() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        StreamBuilder(
          stream: _bpaController.bpaNocDetailStream.stream,
          builder: (context, AsyncSnapshot snapshot) {
            if (snapshot.hasData) {
              final nocData = snapshot.data;
              if (nocData is Noc && nocData.noc!.isNotEmpty) {
                return ListView.builder(
                  itemCount: nocData.noc!.length,
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemBuilder: (context, index) {
                    final nocELement = nocData.noc![index];

                    return _buildNocCard(
                      nocElement: nocELement,
                    );
                  },
                );
              } else {
                return Container();
              }
            } else if (snapshot.hasError) {
              return networkErrorPage(
                context,
                () => _getNoc(),
              );
            } else {
              switch (snapshot.connectionState) {
                case ConnectionState.waiting:
                case ConnectionState.active:
                  return showCircularIndicator();
                default:
                  return const SizedBox.shrink();
              }
            }
          },
        ),
        const SizedBox(
          height: 10,
        ),
        StreamBuilder(
          stream: _bpaController.bpaFeeDetailStream.stream,
          builder: (context, AsyncSnapshot snapshot) {
            if (snapshot.hasData) {
              final feeData = snapshot.data;
              if (feeData is PaymentModel && feeData.payments!.isNotEmpty) {
                if (!isNotNullOrEmpty(feeData.payments)) {
                  return MediumTextNotoSans(
                    text: getLocalizedString(i18.common.NO_DATA),
                  );
                } else {
                  return ListView.builder(
                    itemCount: feeData.payments?.length,
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemBuilder: (context, index) {
                      final feeELement = feeData.payments![index];
                      return _feeDetailWidget(
                        feeELement: feeELement,
                      );
                    },
                  );
                }
              } else {
                return Container();
              }
            } else if (snapshot.hasError) {
              return networkErrorPage(
                context,
                () => _getFee(),
              );
            } else {
              switch (snapshot.connectionState) {
                case ConnectionState.waiting:
                case ConnectionState.active:
                  return showCircularIndicator();
                default:
                  return const SizedBox.shrink();
              }
            }
          },
        ),
      ],
    );
  }

  Widget _buildAppNo() {
    return BuildCard(
      child: Column(
        children: [
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.APPLICATION_NUMBER,
              module: Modules.BPA,
            ),
            text: newBpaData.applicationNo ?? "N/A",
          ),
          if (newBpaData.additionalDetails?.applicationType ==
                  'BUILDING_PLAN_SCRUTINY' &&
              newBpaData.status == 'APPROVED')
            Column(
              children: [
                const SizedBox(
                  height: 10,
                ),
                ColumnHeaderText(
                  label: getLocalizedString(
                    i18.building.PERMIT_APP_NUMBER,
                    module: Modules.BPA,
                  ),
                  text: newBpaData.approvalNo ?? "N/A",
                ),
                const SizedBox(
                  height: 10,
                ),
                ColumnHeaderText(
                  label: getLocalizedString(
                    i18.building.PERMIT_VALIDITY,
                    module: Modules.BPA,
                  ),
                  text:
                      '${newBpaData.approvalDate.toCustomDateFormat()} - ${newBpaData.additionalDetails!.validityDate.toCustomDateFormat()}',
                ),
              ],
            ),
          if (newBpaData.additionalDetails?.applicationType ==
                  'BUILDING_OC_PLAN_SCRUTINY' &&
              newBpaData.status == 'APPROVED')
            Column(
              children: [
                const SizedBox(
                  height: 10,
                ),
                ColumnHeaderText(
                  label: getLocalizedString(
                    i18.building.PERMIT_APP_NUMBER,
                    module: Modules.BPA,
                  ),
                  text: newBpaData.additionalDetails?.permitNumber ?? "N/A",
                ),
                const SizedBox(
                  height: 10,
                ),
                ColumnHeaderText(
                  label: getLocalizedString(
                    i18.building.OC_PERMIT_NUMBER_LABEL,
                    module: Modules.BPA,
                  ),
                  text: '${newBpaData.approvalNo}',
                ),
                const SizedBox(
                  height: 10,
                ),
                ColumnHeaderText(
                  label: getLocalizedString(
                    i18.building.OC_PERMIT_VALIDITY,
                    module: Modules.BPA,
                  ),
                  text:
                      '${newBpaData.approvalDate.toCustomDateFormat()} - ${newBpaData.additionalDetails!.validityDate.toCustomDateFormat()}',
                ),
              ],
            ),
        ],
      ),
    );
  }

  Widget _buildLicenseDetail() {
    return BuildCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          BigTextNotoSans(
            text: getLocalizedString(
              i18.building.BASIC_DETAILS,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.w600,
            size: 16.sp,
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.APPLICATION_DATE,
              module: Modules.BPA,
            ),
            text: newBpaData.auditDetails?.createdTime.toCustomDateFormat() ??
                "dd-MM-yyyy",
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.APPLICATION_TYPE,
              module: Modules.BPA,
            ),
            text: getLocalizedString(
              newBpaData.additionalDetails?.applicationType,
            ),
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.BPA_SERVICE_TYPE,
              module: Modules.BPA,
            ),
            text: getLocalizedString(newBpaData.additionalDetails?.serviceType),
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.OCCUPANCY_TYPE,
              module: Modules.BPA,
            ),
            text: _edrcData?.planDetail?.planInformation?.occupancy ?? "N/A",
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label:
                getLocalizedString(i18.building.RISK_TYPE, module: Modules.BPA),
            text: newBpaData.riskType ?? "N/A",
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.common.APPLICANT_NAME,
              module: Modules.BPA,
            ),
            text:
                _edrcData?.planDetail?.planInformation?.applicantName ?? "N/A",
          ),
        ],
      ),
    );
  }

  //Pdf Widget viewer
  Widget _buildEvidenceCard(FileStore fileStore) {
    return Padding(
      padding: const EdgeInsets.all(10.0),
      child: GridView.builder(
        itemCount: fileStore.fileStoreIds!.length,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          crossAxisSpacing: 10.0,
          mainAxisSpacing: 10.0,
          mainAxisExtent: 110.0,
        ),
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemBuilder: (context, index) {
          final fileUrl = fileStore.fileStoreIds![index].url!.split(',').first;
          final docType = newBpaData.documents!
              .where(
                (element) =>
                    element.fileStoreId == fileStore.fileStoreIds![index].id,
              )
              .toList()
              .firstOrNull;
          return !isNotNullOrEmpty(docType?.documentType)
              ? const SizedBox.shrink()
              : Column(
                  children: [
                    Container(
                      width: Get.width,
                      decoration: BoxDecoration(
                        color: BaseConfig.greyColor2,
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
                    const SizedBox(height: 10),
                    Tooltip(
                      message: getLocalizedString(docType?.documentType),
                      child: SmallTextNotoSans(
                        text: getLocalizedString(docType?.documentType),
                        color: Colors.grey.shade600,
                        maxLine: 2,
                        textOverflow: TextOverflow.ellipsis,
                      ),
                    ),
                  ],
                ).ripple(() {
                  final fileType = _fileController.getFileType(fileUrl).$2;
                  dPrint('FileType: ${fileType.name}');
                  if (fileType.name == FileExtType.pdf.name) {
                    showTypeDialogue(
                      context,
                      url: fileUrl,
                      isPdf: true,
                      title: getLocalizedString(docType?.documentType),
                    );
                  } else {
                    showTypeDialogue(
                      context,
                      url: fileUrl,
                      title: getLocalizedString(docType?.documentType),
                    );
                  }
                });
        },
      ),
    );
  }

  //Scrutiny Detail Widget
  Widget _scrutinyDetails() {
    return Container(
      padding: const EdgeInsets.all(10),
      color: BaseConfig.mainBackgroundColor,
      child: Column(
        children: [
          MediumText(
            text: getLocalizedString(
              i18.building.EDCR_DETAILS,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.bold,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.EDCR_NUMBER,
              module: Modules.BPA,
            ),
            text: newBpaData.edcrNumber ?? "N/A",
          ),
          const SizedBox(
            height: 10,
          ),
          MediumText(
            text: getLocalizedString(
              i18.building.UPLOADED_PLAN_DIAG,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.bold,
          ),
          if (isNotNullOrEmpty(_edrcData?.planReport))
            Column(
              children: [
                Container(
                  width: Get.width,
                  decoration: BoxDecoration(
                    color: BaseConfig.greyColor2,
                    // border: Border.all(color: Colors.grey),
                    borderRadius: BorderRadius.circular(10.r),
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: Icon(
                      _fileController.getFileType(_edrcData!.planReport!).$1,
                      size: 40.sp,
                      color: BaseConfig.greyColor3,
                    ),
                  ),
                ),
                SizedBox(height: 10.h),
              ],
            ).ripple(() {
              dPrint(_edrcData!.updatedDxfFile!);
              final fileType =
                  _fileController.getFileType(_edrcData!.updatedDxfFile!).$2;
              dPrint('FileType: ${fileType.name}');

              if (fileType.name == FileExtType.pdf.name) {
                showTypeDialogue(
                  context,
                  url: _edrcData!.updatedDxfFile!,
                  isPdf: true,
                  title: getLocalizedString(
                    i18.building.UPLOADED_PLAN_DIAG,
                    module: Modules.BPA,
                  ),
                );
              } else if (fileType.name == FileExtType.dxf.name) {
                launchURL(_edrcData!.updatedDxfFile!);
                //TODO: DXF file download
              } else {
                launchURL(_edrcData!.updatedDxfFile!);
              }
            }),
          const SizedBox(
            height: 10,
          ),
          MediumText(
            text: getLocalizedString(
              i18.building.SCRUNITY_REPORT_OUTPUT,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.bold,
          ),
          if (isNotNullOrEmpty(_edrcData?.planReport))
            Column(
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
                      _fileController.getFileType(_edrcData!.planReport!).$1,
                      size: 40,
                      color: BaseConfig.greyColor3,
                    ),
                  ),
                ),
                const SizedBox(height: 10),
              ],
            ).ripple(() {
              final fileType =
                  _fileController.getFileType(_edrcData!.planReport!).$2;
              dPrint('FileType: ${fileType.name}');
              if (fileType.name == FileExtType.pdf.name) {
                showTypeDialogue(
                  context,
                  url: _edrcData!.planReport!,
                  isPdf: true,
                  title: getLocalizedString(
                    i18.building.SCRUNITY_REPORT_OUTPUT,
                    module: Modules.BPA,
                  ),
                );
              } else if (fileType.name == FileExtType.dxf.name) {
                launchURL(_edrcData!.planReport!);
              } else {
                launchURL(_edrcData!.planReport!);
              }
            }),
        ],
      ),
    );
  }

  Widget _blockOccupancy() {
    return Container(
      color: BaseConfig.mainBackgroundColor,
      padding: const EdgeInsets.all(10),
      child: Column(
        children: [
          MediumText(
            text: getLocalizedString(
              i18.building.BLOCK_SUBHEADER,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.bold,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.SUB_OCCUPANCY,
              module: Modules.BPA,
            ),
            text: "NA",
          ),
          const SizedBox(
            height: 10,
          ),
          if (_edrcData?.planDetail?.blocks != null)
            SizedBox(
              height: Get.height * 0.2,
              child: ListView.builder(
                shrinkWrap: true,
                itemCount: _edrcData?.planDetail?.blocks?.length,
                itemBuilder: (context, index) {
                  return SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Table(
                      defaultColumnWidth: const IntrinsicColumnWidth(),
                      border: TableBorder.all(
                        color: Colors.grey.shade300,
                      ),
                      children: [
                        TableRow(
                          children: [
                            TableCell(
                              child: Padding(
                                padding: const EdgeInsets.all(5.0),
                                child: Center(
                                  child: MediumText(
                                    text: getLocalizedString(
                                      i18.building.FLOOR,
                                      module: Modules.BPA,
                                    ),
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                            ),
                            TableCell(
                              child: Padding(
                                padding: const EdgeInsets.all(5.0),
                                child: Center(
                                  child: MediumText(
                                    text: getLocalizedString(
                                      i18.building.LEVEL,
                                      module: Modules.BPA,
                                    ),
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                            ),
                            TableCell(
                              child: Padding(
                                padding: const EdgeInsets.all(5.0),
                                child: Center(
                                  child: MediumText(
                                    text: getLocalizedString(
                                      i18.building.OCCUPANCY_TABLE,
                                      module: Modules.BPA,
                                    ),
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                            ),
                            TableCell(
                              child: Padding(
                                padding: const EdgeInsets.all(5.0),
                                child: Center(
                                  child: MediumText(
                                    text: getLocalizedString(
                                      i18.building.BUILDUP,
                                      module: Modules.BPA,
                                    ),
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                            ),
                            TableCell(
                              child: Padding(
                                padding: const EdgeInsets.all(5.0),
                                child: Center(
                                  child: MediumText(
                                    text: getLocalizedString(
                                      i18.building.FLOORAREA,
                                      module: Modules.BPA,
                                    ),
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                            ),
                            TableCell(
                              child: Padding(
                                padding: const EdgeInsets.all(5.0),
                                child: Center(
                                  child: MediumText(
                                    text: getLocalizedString(
                                      i18.building.CARPETAREA,
                                      module: Modules.BPA,
                                    ),
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                        ..._edrcData!
                            .planDetail!.blocks![index].building!.floors!
                            .map((item) {
                              return TableRow(
                                decoration: BoxDecoration(
                                  color: getRowColor(item.number ?? 0),
                                ),
                                children: [
                                  TableCell(
                                    child: Padding(
                                      padding: const EdgeInsets.all(5.0),
                                      child: Center(
                                        child: SmallText(
                                          text: getLocalizedString(
                                            '${i18.building.BPA_FLOOR_NAME}${item.number}',
                                            module: Modules.BPA,
                                          ),
                                        ),
                                      ),
                                    ),
                                  ),
                                  TableCell(
                                    child: Padding(
                                      padding: const EdgeInsets.all(5.0),
                                      child: Center(
                                        child: SmallText(
                                          text: '${item.number ?? "N/A"}',
                                        ),
                                      ),
                                    ),
                                  ),
                                  TableCell(
                                    child: Padding(
                                      padding: const EdgeInsets.all(5.0),
                                      child: Center(
                                        child: SmallText(
                                          text: item.occupancies?[index]
                                                  .typeHelper?.type?.name ??
                                              "N/A",
                                        ),
                                      ),
                                    ),
                                  ),
                                  TableCell(
                                    child: Padding(
                                      padding: const EdgeInsets.all(5.0),
                                      child: Center(
                                        child: SmallText(
                                          text:
                                              "${item.occupancies?[index].builtUpArea ?? "N/A"}",
                                        ),
                                      ),
                                    ),
                                  ),
                                  TableCell(
                                    child: Padding(
                                      padding: const EdgeInsets.all(5.0),
                                      child: Center(
                                        child: SmallText(
                                          text:
                                              "${item.occupancies?[index].floorArea ?? "N/A"}",
                                        ),
                                      ),
                                    ),
                                  ),
                                  TableCell(
                                    child: Padding(
                                      padding: const EdgeInsets.all(5.0),
                                      child: Center(
                                        child: SmallText(
                                          text:
                                              "${item.occupancies?[index].carpetArea ?? "N/A"}",
                                        ),
                                      ),
                                    ),
                                  ),
                                ],
                              );
                            })
                            .toList()
                            .reversed,
                      ],
                    ),
                  );
                },
              ),
            ),
          // if (_edrcData?.planDetail?.blocks?.first.building?.floors != null)
          //   SingleChildScrollView(
          //     scrollDirection: Axis.horizontal,
          //     child: Table(
          //       defaultColumnWidth: const IntrinsicColumnWidth(),
          //       border: TableBorder.all(
          //         color: Colors.grey.shade300,
          //       ),
          //       children: [
          //         TableRow(
          //           children: [
          //             TableCell(
          //               child: Padding(
          //                 padding: const EdgeInsets.all(5.0),
          //                 child: Center(
          //                   child: MediumText(
          //                     text: getLocalizedString(i18.common.FLOOR),
          //                     fontWeight: FontWeight.bold,
          //                   ),
          //                 ),
          //               ),
          //             ),
          //             TableCell(
          //               child: Padding(
          //                 padding: const EdgeInsets.all(5.0),
          //                 child: Center(
          //                   child: MediumText(
          //                     text: getLocalizedString(i18.common.LEVEL),
          //                     fontWeight: FontWeight.bold,
          //                   ),
          //                 ),
          //               ),
          //             ),
          //             TableCell(
          //               child: Padding(
          //                 padding: const EdgeInsets.all(5.0),
          //                 child: Center(
          //                   child: MediumText(
          //                     text: getLocalizedString(
          //                       i18.building.OCCUPANCY_TABLE,
          //                       module: Modules.BPA,
          //                     ),
          //                     fontWeight: FontWeight.bold,
          //                   ),
          //                 ),
          //               ),
          //             ),
          //             TableCell(
          //               child: Padding(
          //                 padding: const EdgeInsets.all(5.0),
          //                 child: Center(
          //                   child: MediumText(
          //                     text: getLocalizedString(
          //                       i18.building.BUILDUP,
          //                       module: Modules.BPA,
          //                     ),
          //                     fontWeight: FontWeight.bold,
          //                   ),
          //                 ),
          //               ),
          //             ),
          //             TableCell(
          //               child: Padding(
          //                 padding: const EdgeInsets.all(5.0),
          //                 child: Center(
          //                   child: MediumText(
          //                     text: getLocalizedString(
          //                       i18.building.FLOORAREA,
          //                       module: Modules.BPA,
          //                     ),
          //                     fontWeight: FontWeight.bold,
          //                   ),
          //                 ),
          //               ),
          //             ),
          //             TableCell(
          //               child: Padding(
          //                 padding: const EdgeInsets.all(5.0),
          //                 child: Center(
          //                   child: MediumText(
          //                     text: getLocalizedString(
          //                       i18.building.CARPETAREA,
          //                       module: Modules.BPA,
          //                     ),
          //                     fontWeight: FontWeight.bold,
          //                   ),
          //                 ),
          //               ),
          //             ),
          //           ],
          //         ),
          //         ..._edrcData!.planDetail!.blocks!.first.building!.floors!
          //             .map((item) {
          //               return TableRow(
          //                 decoration: BoxDecoration(
          //                   color: _bpaController.getRowColor(item.number ?? 0),
          //                 ),
          //                 children: [
          //                   TableCell(
          //                     child: Padding(
          //                       padding: const EdgeInsets.all(5.0),
          //                       child: Center(
          //                         child: SmallText(
          //                           text: getLocalizedString('${i18.building.BPA_FLOOR_NAME}${item.number}',
          //                           module: Modules.BPA
          //                           ),
          //                         ),
          //                       ),
          //                     ),
          //                   ),
          //                   TableCell(
          //                     child: Padding(
          //                       padding: const EdgeInsets.all(5.0),
          //                       child: Center(
          //                         child: SmallText(
          //                           text: '${item.number ?? "N/A"}',
          //                         ),
          //                       ),
          //                     ),
          //                   ),
          //                   TableCell(
          //                     child: Padding(
          //                       padding: const EdgeInsets.all(5.0),
          //                       child: Center(
          //                         child: SmallText(
          //                           text: item.occupancies?.first.typeHelper
          //                                   ?.type?.name ??
          //                               "N/A",
          //                         ),
          //                       ),
          //                     ),
          //                   ),
          //                   TableCell(
          //                     child: Padding(
          //                       padding: const EdgeInsets.all(5.0),
          //                       child: Center(
          //                         child: SmallText(
          //                           text:
          //                               "${item.occupancies?.first.builtUpArea ?? "N/A"}",
          //                         ),
          //                       ),
          //                     ),
          //                   ),
          //                   TableCell(
          //                     child: Padding(
          //                       padding: const EdgeInsets.all(5.0),
          //                       child: Center(
          //                         child: SmallText(
          //                           text:
          //                               "${item.occupancies?.first.floorArea ?? "N/A"}",
          //                         ),
          //                       ),
          //                     ),
          //                   ),
          //                   TableCell(
          //                     child: Padding(
          //                       padding: const EdgeInsets.all(5.0),
          //                       child: Center(
          //                         child: SmallText(
          //                           text:
          //                               "${item.occupancies?.first.carpetArea ?? "N/A"}",
          //                         ),
          //                       ),
          //                     ),
          //                   ),
          //                 ],
          //               );
          //             })
          //             .toList()
          //             .reversed,
          //       ],
          //     ),
          //   ),
          const SizedBox(
            height: 10,
          ),
          MediumText(
            text: getLocalizedString(
              i18.building.DEMOLITION_DETAILS,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.bold,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.DEMOLITION_AREA,
              module: Modules.BPA,
            ),
            text:
                '${_edrcData?.planDetail?.planInformation?.demolitionArea} sq.mtrs',
          ),
          const SizedBox(
            height: 10,
          ),
        ],
      ),
    );
  }

  Widget _buildInspectionCard(
    FieldInspectionPending? report,
    List<Doc>? docs,
    int index,
  ) {
    String getFileStoreIdDocs(List<Doc>? docs) {
      if (docs == null) return '';
      return docs.map((e) => e.fileStoreId).join(', ');
    }

    final fileInspectionIds = getFileStoreIdDocs(docs);

    bool isLoadingInspection = false;

    return Container(
      decoration: BoxDecoration(
        color: BaseConfig.greyColor2.withValues(alpha: 0.2),
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: BaseConfig.borderColor),
      ),
      padding: const EdgeInsets.symmetric(horizontal: 5),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Center(
            child: MediumText(
              text:
                  "${getLocalizedString(i18.building.INSPECTION_REPORT, module: Modules.BPA)} - ${index + 1}",
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.INSPECTION_DATE,
              module: Modules.BPA,
            ),
            text: report?.date ?? "dd-MM-yyyy",
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.INSPECTION_TIME,
              module: Modules.BPA,
            ),
            text: report?.time ?? "00:00",
          ),
          const SizedBox(height: 10),
          ListView.builder(
            shrinkWrap: true,
            itemCount: report?.questions?.length,
            physics: const NeverScrollableScrollPhysics(),
            itemBuilder: (context, j) {
              final ques = report?.questions![j];
              return Container(
                decoration: BoxDecoration(
                  color: BaseConfig.mainBackgroundColor,
                  borderRadius: BorderRadius.circular(10),
                  border: Border.all(color: BaseConfig.borderColor),
                ),
                padding: const EdgeInsets.all(10),
                margin: const EdgeInsets.only(bottom: 10),
                child: Column(
                  children: [
                    ColumnHeaderText(
                      label: '${getLocalizedString(
                        ques?.question,
                        module: Modules.BPA,
                      )}?',
                      text: ques?.value ?? "N/A",
                    ),
                    SizedBox(height: 10.h),
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.building.INSPECTION_REMARKS,
                        module: Modules.BPA,
                      ),
                      text: ques?.remarks ?? "N/A",
                    ),
                  ],
                ),
              );
            },
          ),
          const SizedBox(height: 10),
          BuildExpansion(
            title: getLocalizedString(
              i18.building.NOC_DOCUMENT,
              module: Modules.BPA,
            ),
            onExpansionChanged: (isExpanded) async {
              isLoadingInspection = true;

              if (isExpanded) {
                await getInspectionDocs(fileInspectionIds);
              }
              isLoadingInspection = false;
              setState(() {});
            },
            children: [
              StreamBuilder(
                stream: inspectionDocsRx.stream,
                builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    final fileData = snapshot.data!;
                    return isLoadingInspection
                        ? showCircularIndicator().marginOnly(bottom: 10.h)
                        : _buildReportListDoc(fileData, docs);
                  } else if (snapshot.hasError) {
                    return networkErrorPage(
                      context,
                      () => getInspectionDocs(fileInspectionIds),
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
      ).marginOnly(bottom: 10),
    );
  }

  Widget _buildReportListDoc(FileStore fileStore, List<Doc>? docs) {
    return Padding(
      padding: const EdgeInsets.all(10.0),
      child: GridView.builder(
        itemCount: fileStore.fileStoreIds!.length,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          crossAxisSpacing: 10.0,
          mainAxisSpacing: 10.0,
          mainAxisExtent: 110.0,
        ),
        shrinkWrap: true,
        itemBuilder: (context, index) {
          final fileUrl = fileStore.fileStoreIds![index].url!.split(',').first;
          final docType = docs
              ?.where(
                (element) =>
                    element.fileStoreId == fileStore.fileStoreIds![index].id,
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
                    const SizedBox(height: 10),
                    Tooltip(
                      message: getLocalizedString(docType?.documentType),
                      child: SmallTextNotoSans(
                        text: getLocalizedString(docType?.documentType),
                        color: Colors.grey.shade600,
                        maxLine: 2,
                        textOverflow: TextOverflow.ellipsis,
                      ),
                    ),
                  ],
                ).ripple(() {
                  final fileType = _fileController.getFileType(fileUrl).$2;
                  dPrint('FileType: ${fileType.name}');
                  if (fileType.name == FileExtType.pdf.name) {
                    showTypeDialogue(
                      context,
                      url: fileUrl,
                      isPdf: true,
                      title: getLocalizedString(docType?.documentType),
                    );
                  } else {
                    showTypeDialogue(
                      context,
                      url: fileUrl,
                      title: getLocalizedString(docType?.documentType),
                    );
                  }
                })
              : const SizedBox.shrink();
        },
      ),
    );
  }

  Widget _buildingProposedAbstractWidget() {
    return Container(
      padding: const EdgeInsets.all(10),
      color: BaseConfig.mainBackgroundColor,
      child: Column(
        children: [
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.TOTAL_BUILDUP_AREA,
              module: Modules.BPA,
            ),
            text:
                '${_edrcData?.planDetail?.virtualBuilding?.totalBuitUpArea} sq.mtrs',
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.NUMBER_FLOOR,
              module: Modules.BPA,
            ),
            text: _edrcData
                    ?.planDetail?.blocks?.first.building?.floorsAboveGround
                    .toString() ??
                "N/A",
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.HEIGHT_FROM_GROUND,
              module: Modules.BPA,
            ),
            text: _edrcData
                    ?.planDetail?.blocks?.first.building?.declaredBuildingHeight
                    .toString() ??
                "N/A",
          ),
          const SizedBox(
            height: 10,
          ),
        ],
      ),
    );
  }

  Widget _buildNocCard({
    required NocElement nocElement,
  }) {
    List<String> fileIds = [];
    if (isNotNullOrEmpty(nocElement.documents)) {
      for (var element in nocElement.documents!) {
        fileIds.add(element.fileStoreId ?? '');
      }
    }

    final fileStoreIdsNoc = fileIds.join(', ');

    final Map<String, bool> fetchDocs = {for (var id in fileIds) id: false};

    bool isLoadingNoc = false;

    return Container(
      padding: const EdgeInsets.all(10),
      decoration: BoxDecoration(
        color: BaseConfig.mainBackgroundColor,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(
          color: BaseConfig.greyColor2,
        ),
      ),
      margin: const EdgeInsets.only(bottom: 10),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          MediumText(
            text: getLocalizedString(
              'BPA_${nocElement.nocType}_HEADER',
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.bold,
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              'BPA_${nocElement.nocType}_LABEL',
              module: Modules.BPA,
            ),
            text: isNotNullOrEmpty(nocElement.applicationNo)
                ? "${nocElement.applicationNo}"
                : "N/A",
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.BPA_STATUS_LABEL,
              module: Modules.BPA,
            ),
            text: getLocalizedString(
              nocElement.applicationStatus,
              module: Modules.BPA,
            ),
          ),
          const SizedBox(height: 10),
          if (isNotNullOrEmpty(fileStoreIdsNoc))
            BuildExpansion(
              title: getLocalizedString(
                i18.building.NOC_DOCUMENT,
                module: Modules.BPA,
              ),
              tilePadding: const EdgeInsets.all(0),
              onExpansionChanged: (isExpanded) async {
                isLoadingNoc = true;
                if (isExpanded) {
                  for (var fileId in fileIds) {
                    if (!fetchDocs[fileId]!) {
                      await getNocDocs(fileId).then((value) {
                        fetchDocs[fileId] = true;
                        _isFileFetch = false;
                      });
                    }
                  }
                }
                isLoadingNoc = false;
                setState(() {});
              },
              children: [
                StreamBuilder(
                  stream: nocDocsRx.stream,
                  builder: (context, snapshot) {
                    if (snapshot.hasData) {
                      final fileData = snapshot.data!;

                      return isLoadingNoc
                          ? showCircularIndicator().marginOnly(bottom: 10.h)
                          : _buildDocumentsDetailsCard(fileData, nocElement);
                    } else if (snapshot.hasError) {
                      return networkErrorPage(
                        context,
                        () => _fileController.getFiles(
                          tenantId: BaseConfig.STATE_TENANT_ID,
                          token: _authController.token!.accessToken!,
                          fileStoreIds: fileStoreIdsNoc,
                        ),
                      );
                    } else {
                      switch (snapshot.connectionState) {
                        case ConnectionState.waiting:
                        case ConnectionState.active:
                          return showCircularIndicator();
                        default:
                          return const SizedBox.shrink();
                      }
                    }
                  },
                ),
              ],
            )
          else
            Text(getLocalizedString(i18.common.BPA_NO_DOCUMENT)),
        ],
      ),
    );
  }

  Widget _feeDetailWidget({
    required Payment feeELement,
  }) {
    return Container(
      padding: const EdgeInsets.all(10),
      color: BaseConfig.mainBackgroundColor,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          BigTextNotoSans(
            text: getLocalizedString(
              i18.building.BPA_FEE_DETAILS,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.w600,
            size: 16.sp,
          ).paddingOnly(top: 10),
          MediumText(
            text: getLocalizedString(
              i18.building.BPA_APPL_FEES,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.bold,
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.BPA_APPL_FEES,
              module: Modules.BPA,
            ),
            text:
                "₹${feeELement.paymentDetails?.first.bill?.billDetails?.first.billAccountDetails?.first.amount}",
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.BPA_STATUS_LABEL,
              module: Modules.BPA,
            ),
            text: feeELement.paymentDetails?.first.bill?.billDetails?.first
                        .billAccountDetails?.first.amount ==
                    feeELement.paymentDetails?.first.bill?.billDetails?.first
                        .billAccountDetails?.first.adjustedAmount
                ? "Paid"
                : "Unpaid",
            textColor: feeELement.paymentDetails?.first.bill?.billDetails?.first
                        .billAccountDetails?.first.amount ==
                    feeELement.paymentDetails?.first.bill?.billDetails?.first
                        .billAccountDetails?.first.adjustedAmount
                ? BaseConfig.statusGreenColor
                : BaseConfig.redColor,
          ),
          const SizedBox(
            height: 10,
          ),
          MediumText(
            text: getLocalizedString(
              i18.building.BPA_FEES_DETAILS,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.bold,
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label:
                getLocalizedString(i18.building.SANC_FEES, module: Modules.BPA),
            text:
                "₹${feeELement.paymentDetails?.first.bill?.billDetails?.first.billAccountDetails?.last.amount}",
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.BPA_STATUS_LABEL,
              module: Modules.BPA,
            ),
            text: feeELement.paymentDetails?.first.bill?.billDetails?.first
                        .billAccountDetails?.first.amount ==
                    feeELement.paymentDetails?.first.bill?.billDetails?.first
                        .billAccountDetails?.first.adjustedAmount
                ? "Paid"
                : "Unpaid",
            textColor: feeELement.paymentDetails?.first.bill?.billDetails?.first
                        .billAccountDetails?.first.amount ==
                    feeELement.paymentDetails?.first.bill?.billDetails?.first
                        .billAccountDetails?.first.adjustedAmount
                ? BaseConfig.statusGreenColor
                : BaseConfig.redColor,
          ),
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.BPA_TOT_AMT_PAID,
              module: Modules.BPA,
            ),
            text: "₹${feeELement.paymentDetails?.first.bill?.totalAmount}",
          ),
        ],
      ),
    );
  }

  Widget _buildDocumentsDetailsCard(
    FileStore fileStore,
    NocElement nocElement,
  ) {
    return Padding(
      padding: const EdgeInsets.all(10.0),
      child: GridView.builder(
        itemCount: fileStore.fileStoreIds!.length,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          crossAxisSpacing: 10.0,
          mainAxisSpacing: 10.0,
          mainAxisExtent: 110.0, //110
        ),
        shrinkWrap: true,
        itemBuilder: (context, index) {
          final fileUrl =
              fileStore.fileStoreIds?[index].url?.split(',').firstOrNull;
          final docType = nocElement.documents?.firstWhereOrNull(
            (element) =>
                element.fileStoreId == fileStore.fileStoreIds?[index].id,
          );

          final docName = isNotNullOrEmpty(docType?.documentType)
              ? getLocalizedString(
                  docType?.documentType,
                )
              : 'N/A';

          return Tooltip(
            message: docName,
            child: Column(
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
                      _fileController.getFileType(fileUrl!).$1,
                      size: 40,
                      color: Colors.grey.shade600,
                    ),
                  ),
                ),
                const SizedBox(height: 10),
                SmallTextNotoSans(
                  text: docName,
                  color: Colors.grey.shade600,
                  maxLine: 2,
                  textOverflow: TextOverflow.ellipsis,
                ),
              ],
            ).ripple(() {
              final fileType = _fileController.getFileType(fileUrl).$2;
              dPrint('FileType: ${fileType.name}');
              showTypeDialogue(
                context,
                url: fileUrl,
                isPdf: fileType == FileExtType.pdf,
                title: docName,
              );
            }),
          );
        },
      ),
    );
  }
}
