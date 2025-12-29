import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/fsm_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart';
import 'package:mobile_app/model/citizen/bill/demands.dart';
import 'package:mobile_app/model/citizen/fsm/fsm.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/timeline_widget.dart/timeline_wdget.dart';

class FsmApplicationDetails extends StatefulWidget {
  const FsmApplicationDetails({super.key});

  @override
  State<FsmApplicationDetails> createState() => _FsmApplicationDetailsState();
}

class _FsmApplicationDetailsState extends State<FsmApplicationDetails> {
  final _timelineController = Get.find<TimelineController>();
  final _authController = Get.find<AuthController>();
  final _paymentController = Get.find<PaymentController>();
  final _fsmController = Get.find<FsmController>();
  final _fileController = Get.find<FileController>();
  final _downloadController = Get.find<DownloadController>();

  final fsm = Get.arguments as Fsm;

  bool _isTimelineFetch = false;

  List<Payment>? payments;

  final _isLoading = false.obs;

  @override
  void initState() {
    super.initState();
    _init();
  }

  Future<void> _init() async {
    _isLoading.value = true;
    if (canDownloadAdvancePaymentReceipt(fsm.applicationStatus) ||
        canDownloadFinalPaymentReceipt(fsm.applicationStatus)) {
      payments = await _paymentController.getPaymentDetails(
        tenantId: BaseConfig.STATE_TENANT_ID,
        consumerCodes: fsm.applicationNo!,
        token: _authController.token!.accessToken!,
        businessService: BusinessService.FSM_TRIP_CHARGES.name,
      );
    }

    _isLoading.value = false;
  }

  Future<void> _getTimeline() async {
    await _timelineController
        .getTimelineHistory(
      token: _authController.token!.accessToken!,
      tenantId: fsm.tenantId!,
      businessIds: fsm.applicationNo!,
    )
        .then((val) {
      setState(() {
        _isTimelineFetch = true;
      });
    });
  }

  // void goPayment() async {
  //   if (!_authController.isValidUser) return;
  //   Get.to(
  //     () => PaymentScreen(
  //       token: _authController.token!.accessToken!,
  //       consumerCode: fsm.applicationNo!,
  //       businessService: BusinessService.FSM,
  //       cityTenantId: fsm.tenantId!,
  //       module: Modules.FSM.name,
  //     ),
  //   );
  // }

  Future<BillInfo?> _fetchMyBills() async {
    return _paymentController.searchBillById(
      tenantId: fsm.tenantId!,
      token: _authController.token!.accessToken!,
      consumerCode: fsm.applicationNo!,
      service: BusinessService.FSM_TRIP_CHARGES.name,
    );
  }

  Future<DemandsModel?> _fetchDemands() async {
    return _paymentController.searchDemandById(
      tenantId: fsm.tenantId!,
      token: _authController.token!.accessToken!,
      consumerCode: fsm.applicationNo!,
      businessService: BusinessService.FSM_TRIP_CHARGES.name,
    );
  }

  void goPayment() async {
    if (!_authController.isValidUser) return;

    if (fsm.applicationStatus ==
            FsmStatus.PENDING_APPL_FEE_PAYMENT_CITIZEN.name ||
        fsm.applicationStatus == FsmStatus.PENDING_APPL_FEE_PAYMENT.name) {
      final bill = await _fetchMyBills();

      if (!isNotNullOrEmpty(bill?.bill)) return;

      Get.toNamed(
        AppRoutes.BILL_DETAIL_SCREEN,
        arguments: {
          'billData': bill?.bill?.first,
          'module': Modules.FSM,
          'advanceAmount': fsm.advanceAmount,
        },
      );
    } else if (fsm.applicationStatus == FsmStatus.DSO_INPROGRESS.name) {
      final demand = await _fetchDemands();
      Get.toNamed(
        AppRoutes.BILL_DETAIL_SCREEN,
        arguments: {
          'billData': demand?.demands?.first,
          'module': Modules.FSM,
          'advanceAmount': fsm.advanceAmount,
        },
      );
    }
  }

  Widget _makePayment() {
    return FilledButtonApp(
      radius: 0,
      width: Get.width,
      text: getLocalizedString(
        i18.common.MAKE_PAYMENT,
      ),
      onPressed: () => goPayment(),
      circularColor: BaseConfig.fillAppBtnCircularColor,
      backgroundColor: BaseConfig.appThemeColor1,
    );
  }

  bool checkPaymentButton() {
    final additionalDetails = fsm.address?.additionalDetails;
    if (additionalDetails is AddressAdditionalDetails) {
      if (additionalDetails.boundaryType != 'GP') {
        var validStatuses = {
          FsmStatus.PENDING_APPL_FEE_PAYMENT_CITIZEN.name,
          FsmStatus.PENDING_APPL_FEE_PAYMENT.name,
          FsmStatus.DSO_INPROGRESS.name,
        };
        return validStatuses.contains(fsm.applicationStatus);
      }
    }

    return false;
  }

  bool canDownloadAdvancePaymentReceipt(String? applicationStatus) {
    if (applicationStatus == null) return false;
    var validStatuses = {
      FsmStatus.ASSING_DSO.name,
      FsmStatus.DSO_INPROGRESS.name,
      FsmStatus.PENDING_DSO_APPROVAL.name,
      FsmStatus.COMPLETED.name,
      FsmStatus.CITIZEN_FEEDBACK_PENDING.name,
    };
    return validStatuses.contains(applicationStatus);
  }

  bool canDownloadFinalPaymentReceipt(String? applicationStatus) {
    if (applicationStatus == null) return false;
    var validStatuses = {
      FsmStatus.PENDING_DSO_APPROVAL.name,
      FsmStatus.COMPLETED.name,
      FsmStatus.CITIZEN_FEEDBACK_PENDING.name,
    };
    return validStatuses.contains(applicationStatus);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        title: getLocalizedString(
          i18.fsmLocal.FSM_APPLICATION_DETAIL,
          module: Modules.FSM,
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      bottomNavigationBar: checkPaymentButton() ? _makePayment() : null,
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Obx(
          () => _isLoading.value
              ? showCircularIndicator()
              : SingleChildScrollView(
                  physics: const BouncingScrollPhysics(),
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
                                  tenantId: fsm.tenantId!,
                                );
                              },
                              child: MediumSelectableTextNotoSans(
                                text: getLocalizedString(
                                  i18.common.TIMELINE,
                                ),
                                color: BaseConfig.redColor1,
                              ),
                            ),
                            const SizedBox(width: 10),
                            if ((canDownloadAdvancePaymentReceipt(
                                          fsm.applicationStatus,
                                        ) &&
                                        ((payments?.length ?? 0) > 1) ||
                                    (payments?.length ?? 0) == 1) ||
                                canDownloadFinalPaymentReceipt(
                                  fsm.applicationStatus,
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
                                    MediumSelectableTextNotoSans(
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
                                  if (canDownloadFinalPaymentReceipt(
                                    fsm.applicationStatus,
                                  ))
                                    PopupMenuItem<String>(
                                      value: 'Final Payment Receipt',
                                      child: const MediumSelectableTextNotoSans(
                                        text: 'Final Payment Receipt',
                                      ),
                                      onTap: () async {
                                        _fsmController.isLoading.value = true;

                                        if (isNotNullOrEmpty(payments)) {
                                          payments?.sort(
                                            (a, b) => (a.transactionDate ?? 0)
                                                .compareTo(
                                              b.transactionDate ?? 0,
                                            ),
                                          );

                                          if (isNotNullOrEmpty(
                                            payments?.firstOrNull?.fileStoreId,
                                          )) {
                                            final newFileStore =
                                                await _fileController.getFiles(
                                              fileStoreIds:
                                                  payments!.first.fileStoreId!,
                                              tenantId: BaseConfig
                                                  .STATE_TENANT_ID, //For Test ENV
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
                                                title: 'Final Payment Receipt',
                                              );
                                            }
                                          } else {
                                            const PdfKey key =
                                                PdfKey.fsm_receipt;
                                            final fileStoreId =
                                                await _fileController
                                                    .getPdfServiceFile(
                                              tenantId:
                                                  BaseConfig.STATE_TENANT_ID,
                                              token: _authController
                                                  .token!.accessToken!,
                                              key: key,
                                              payment: payments!.first,
                                            );

                                            final fileStore =
                                                await _fileController.getFiles(
                                              tenantId:
                                                  BaseConfig.STATE_TENANT_ID,
                                              token: _authController
                                                  .token!.accessToken!,
                                              fileStoreIds: fileStoreId,
                                            );

                                            if (fileStore?.fileStoreIds !=
                                                null) {
                                              final url = fileStore!
                                                  .fileStoreIds!.first.url!
                                                  .split(',')
                                                  .first;

                                              await _downloadController
                                                  .starFileDownload(
                                                url: url,
                                                title: 'Final Payment Receipt',
                                              );
                                            }
                                          }
                                        }
                                        _fsmController.isLoading.value = false;
                                      },
                                    ),
                                  if (canDownloadAdvancePaymentReceipt(
                                        fsm.applicationStatus,
                                      ) &&
                                      (payments?.length ?? 0) > 1)
                                    PopupMenuItem<String>(
                                      value: 'Advance Payment Receipt',
                                      child: const MediumSelectableTextNotoSans(
                                        text: 'Advance Payment Receipt',
                                      ),
                                      onTap: () async {
                                        _fsmController.isLoading.value = true;

                                        if (isNotNullOrEmpty(payments)) {
                                          payments?.sort(
                                            (a, b) => (a.transactionDate ?? 0)
                                                .compareTo(
                                              b.transactionDate ?? 0,
                                            ),
                                          );

                                          if (isNotNullOrEmpty(
                                                payments?[1].fileStoreId,
                                              ) &&
                                              (payments?.length ?? 0) > 1) {
                                            final newFileStore =
                                                await _fileController.getFiles(
                                              fileStoreIds:
                                                  payments![1].fileStoreId!,
                                              tenantId: BaseConfig
                                                  .STATE_TENANT_ID, //For Test ENV
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
                                                title:
                                                    'Advance Payment Receipt',
                                              );
                                            }
                                          } else {
                                            const PdfKey key =
                                                PdfKey.fsm_receipt;
                                            final fileStoreId =
                                                await _fileController
                                                    .getPdfServiceFile(
                                              tenantId:
                                                  BaseConfig.STATE_TENANT_ID,
                                              token: _authController
                                                  .token!.accessToken!,
                                              key: key,
                                              payment: payments![1],
                                            );

                                            final fileStore =
                                                await _fileController.getFiles(
                                              tenantId:
                                                  BaseConfig.STATE_TENANT_ID,
                                              token: _authController
                                                  .token!.accessToken!,
                                              fileStoreIds: fileStoreId,
                                            );

                                            if (fileStore?.fileStoreIds !=
                                                null) {
                                              final url = fileStore!
                                                  .fileStoreIds!.first.url!
                                                  .split(',')
                                                  .first;

                                              await _downloadController
                                                  .starFileDownload(
                                                url: url,
                                                title:
                                                    'Advance Payment Receipt',
                                              );
                                            }
                                          }
                                        }
                                        _fsmController.isLoading.value = false;
                                      },
                                    ),
                                  if (canDownloadAdvancePaymentReceipt(
                                        fsm.applicationStatus,
                                      ) &&
                                      (payments?.length ?? 0) == 1)
                                    PopupMenuItem<String>(
                                      value: 'Advance Payment Receipt',
                                      child: const MediumSelectableTextNotoSans(
                                        text: 'Advance Payment Receipt',
                                      ),
                                      onTap: () async {
                                        _fsmController.isLoading.value = true;

                                        if (isNotNullOrEmpty(payments)) {
                                          payments?.sort(
                                            (a, b) => (a.transactionDate ?? 0)
                                                .compareTo(
                                              b.transactionDate ?? 0,
                                            ),
                                          );

                                          if (isNotNullOrEmpty(
                                                payments
                                                    ?.firstOrNull?.fileStoreId,
                                              ) &&
                                              (payments?.length ?? 0) > 1) {
                                            final newFileStore =
                                                await _fileController.getFiles(
                                              fileStoreIds:
                                                  payments!.first.fileStoreId!,
                                              tenantId: BaseConfig
                                                  .STATE_TENANT_ID, //For Test ENV
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
                                                title:
                                                    'Advance Payment Receipt',
                                              );
                                            }
                                          } else {
                                            const PdfKey key =
                                                PdfKey.fsm_receipt;
                                            final fileStoreId =
                                                await _fileController
                                                    .getPdfServiceFile(
                                              tenantId:
                                                  BaseConfig.STATE_TENANT_ID,
                                              token: _authController
                                                  .token!.accessToken!,
                                              key: key,
                                              payment: payments!.first,
                                            );

                                            final fileStore =
                                                await _fileController.getFiles(
                                              tenantId:
                                                  BaseConfig.STATE_TENANT_ID,
                                              token: _authController
                                                  .token!.accessToken!,
                                              fileStoreIds: fileStoreId,
                                            );

                                            if (fileStore?.fileStoreIds !=
                                                null) {
                                              final url = fileStore!
                                                  .fileStoreIds!.first.url!
                                                  .split(',')
                                                  .first;

                                              await _downloadController
                                                  .starFileDownload(
                                                url: url,
                                                title:
                                                    'Advance Payment Receipt',
                                              );
                                            }
                                          }
                                        }
                                        _fsmController.isLoading.value = false;
                                      },
                                    ),
                                ],
                              ),
                          ],
                        ),
                        Card(
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(10),
                          ),
                          child: Padding(
                            padding: const EdgeInsets.all(10.0),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_REQUEST_NO,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.applicationNo ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_APPLICATION_CHANNEL,
                                    module: Modules.FSM,
                                  ),
                                  text: getLocalizedString(
                                    '${i18.fsmLocal.FSM_SOURCE_TITLE}${fsm.source}',
                                    module: Modules.FSM,
                                  ),
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_APPLICANT_NAME,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.citizen?.name ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_MOBILE_NO,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.citizen?.mobileNumber ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_EMAIL_ID,
                                    module: Modules.FSM,
                                  ),
                                  text: isNotNullOrEmpty(fsm.citizen?.emailId)
                                      ? fsm.citizen!.emailId!
                                      : "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_PROPERTY_ID,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.additionalDetails?.propertyId ??
                                      "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_PROPERTY_TYPE,
                                    module: Modules.FSM,
                                  ),
                                  text: getLocalizedString(
                                    '${i18.fsmLocal.FSM_PROPERTY_TYPE_TITLE}${fsm.propertyUsage!.split('.').first}',
                                    module: Modules.FSM,
                                  ),
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_PROPERTY_SUB_TYPE,
                                    module: Modules.FSM,
                                  ),
                                  text: getLocalizedString(
                                    '${i18.fsmLocal.FSM_PROPERTY_TYPE_TITLE}${fsm.propertyUsage}',
                                    module: Modules.FSM,
                                  ),
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_LOCALITY,
                                    module: Modules.FSM,
                                  ),
                                  text: getLocalizedString(
                                    '${i18.fsmLocal.FSM_LOCALITY_TITLE}${fsm.address?.locality?.code}',
                                  ),
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_CITY,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.address?.city ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_PINCODE,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.address?.pincode ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_STREET_NAME,
                                    module: Modules.PT,
                                  ),
                                  text: fsm.address?.street ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_HOUSE_NO,
                                    module: Modules.PT,
                                  ),
                                  text: fsm.address?.doorNo ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_LANDMARK,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.address?.landmark ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_SLUM,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.address?.slumName ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_GEOLOCATION,
                                    module: Modules.FSM,
                                  ),
                                  text:
                                      "${fsm.address?.geoLocation?.longitude} : ${fsm.address?.geoLocation?.latitude}",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_PIT_TYPE,
                                    module: Modules.FSM,
                                  ),
                                  text: getLocalizedString(
                                    '${i18.fsmLocal.FSM_PIT_TYPE_TITLE}${fsm.sanitationtype}',
                                    module: Modules.FSM,
                                  ),
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_ROAD_WIDTH,
                                    module: Modules.FSM,
                                  ),
                                  text:
                                      fsm.additionalDetails?.roadWidth ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_DISTANCE_FROM_ROAD,
                                    module: Modules.FSM,
                                  ),
                                  text:
                                      fsm.additionalDetails?.distancefromroad ??
                                          "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_PIT_DIMENSION,
                                    module: Modules.FSM,
                                  ),
                                  text:
                                      "${fsm.pitDetail?.length}m x ${fsm.pitDetail?.width}m x ${fsm.pitDetail?.height}m",
                                ),
                                SmallSelectableTextNotoSans(
                                  text:
                                      "(${getLocalizedString(i18.fsmLocal.FSM_LENGTH, module: Modules.FSM)} x ${getLocalizedString(i18.fsmLocal.FSM_BREADTH, module: Modules.FSM)} x ${getLocalizedString(i18.fsmLocal.FSM_DEPTH, module: Modules.FSM)})",
                                  color: BaseConfig.greyColor3,
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_NO_TRIP,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.noOfTrips.toString(),
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_AMOUNT_TRIP,
                                    module: Modules.FSM,
                                  ),
                                  text:
                                      '₹ ${fsm.additionalDetails?.tripAmount}',
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_TOTAL_AMOUNT,
                                    module: Modules.FSM,
                                  ),
                                  text:
                                      '₹ ${fsm.additionalDetails?.totalAmount}',
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_ADV_AMOUNT,
                                    module: Modules.FSM,
                                  ),
                                  text: '₹ ${fsm.advanceAmount}',
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_ASSIGNED_PSSO,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.vehicleType ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_VEHICLE_NO,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.vehicle ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_VEHICLE_CAPACITY,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.vehicleCapacity ?? "N/A",
                                ),
                                const SizedBox(
                                  height: 10,
                                ),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.fsmLocal.FSM_SERVICE_DATE,
                                    module: Modules.FSM,
                                  ),
                                  text: fsm.possibleServiceDate
                                          .toCustomDateFormat() ??
                                      "N/A",
                                ),
                                // const SizedBox(
                                //   height: 10,
                                // ),
                                // if (fsm.applicationStatus ==
                                //         'PENDING_APPL_FEE_PAYMENT_CITIZEN' ||
                                //     fsm.applicationStatus ==
                                //         'PENDING_APPL_FEE_PAYMENT' ||
                                //     fsm.applicationStatus == 'DSO_INPROGRESS') ...[
                                //   const SizedBox(height: 10),
                                //   FilledButtonApp(
                                //     width: Get.width,
                                //     text: getLocalizedString(
                                //       i18.common.MAKE_PAYMENT,
                                //     ),
                                //     onPressed: () => goPayment(),
                                //     circularColor: BaseConfig.fillAppBtnCircularColor,
                                //   ),
                                // ],
                              ],
                            ),
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
}
