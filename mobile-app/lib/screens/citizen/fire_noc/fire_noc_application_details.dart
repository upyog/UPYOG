import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/fire_noc_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart' as b;
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/model/citizen/fire_noc/fire_noc.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/localization/localize_utils.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/build_card.dart';
import 'package:mobile_app/widgets/build_expansion.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/file_dialogue/file_dilaogue.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/timeline_widget.dart/timeline_wdget.dart';

class FireNocApplicationDetails extends StatefulWidget {
  const FireNocApplicationDetails({super.key});

  @override
  State<FireNocApplicationDetails> createState() =>
      _FireNocApplicationDetailsState();
}

class _FireNocApplicationDetailsState extends State<FireNocApplicationDetails> {
  final _fileController = Get.find<FileController>();
  final _authController = Get.find<AuthController>();
  final _fireNocController = Get.find<FireNocController>();
  final _timelineController = Get.find<TimelineController>();
  final _downloadController = Get.find<DownloadController>();
  final _paymentController = Get.find<PaymentController>();

  final fireNoc = Get.arguments as FireNoc;
  final _isLoading = true.obs;
  bool _isTimelineFetch = false;

  @override
  void initState() {
    super.initState();
    init();
  }

  init() async {
    _isLoading.value = true;
    if (showTakeActionButton()) {
      await _getTimeline();
      await _getWorkflow();
      await _getFee();
    }

    _isLoading.value = false;
  }

  Future<void> _getWorkflow() async {
    try {
      await _timelineController.getWorkFlow(
        token: _authController.token!.accessToken!,
        tenantId: fireNoc.tenantId!,
        workFlow: _timelineController
            .timeline!.processInstancesList!.first.businessService!,
      );
    } catch (e) {
      dPrint('Error in getting workflow: $e');
    }
  }

  Future<void> _getFee() async {
    try {
      if (showTakeActionButton()) {
        await _fireNocController.getFireNocFeeDetailBill(
          token: _authController.token!.accessToken!,
          consumerCodes: fireNoc.fireNocDetails!.applicationNumber!,
          tenantId: fireNoc.tenantId!,
          businessService: BusinessService.FIRENOC,
        );
      } else {
        await _fireNocController.getFireNocFeeDetailPayment(
          token: _authController.token!.accessToken!,
          consumerCodes: fireNoc.fireNocDetails!.applicationNumber!,
          tenantId: fireNoc.tenantId!,
          businessService: '',
        );
      }
    } catch (e) {
      dPrint('Error in getting fee: $e');
    }
  }

  String getFileStoreIds() {
    if (fireNoc.fireNocDetails!.additionalDetail?.documents == null) {
      return '';
    }
    List fileIds = [];
    for (var element in fireNoc
        .fireNocDetails!.additionalDetail!.ownerAuditionalDetail!.documents!) {
      fileIds.add(element.fileStoreId);
    }
    return fileIds.join(', ');
  }

  Future<void> _getTimeline() async {
    await _timelineController
        .getTimelineHistory(
      token: _authController.token!.accessToken!,
      tenantId: fireNoc.tenantId!,
      businessIds: fireNoc.fireNocDetails!.applicationNumber!,
    )
        .then((val) {
      setState(() {
        _isTimelineFetch = true;
      });
    });
  }

  bool showTakeActionButton() {
    var actionStatus = {
      FireNocStatus.PENDING_PAYMENT.name,
      // FireNocStatus.INITIATED.name,
    };
    return actionStatus.contains(fireNoc.fireNocDetails?.status);
  }

  bool canDownloadReceipt() {
    var actionStatus = {
      FireNocStatus.DOCUMENT_VERIFY.name,
      FireNocStatus.FIELD_INSPECTION.name,
      FireNocStatus.PENDING_APPROVAL.name,
      FireNocStatus.APPROVED.name,
    };
    return actionStatus.contains(fireNoc.fireNocDetails?.status);
  }

  Future<void> goPayment() async {
    final bill = _fireNocController.billInfo;
    if (!isNotNullOrEmpty(bill.bill)) return;

    Get.toNamed(
      AppRoutes.BILL_DETAIL_SCREEN,
      arguments: {
        'billData': bill.bill!.first,
        'module': Modules.NOC,
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        title: getLocalizedString(i18.noc.NOC_APP_HEADER),
        onPressed: () => Navigator.of(context).pop(),
      ),
      bottomNavigationBar: Obx(
        () => _isLoading.value
            ? const SizedBox.shrink()
            : showTakeActionButton()
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
                            (s) => s.state == fireNoc.fireNocDetails?.status,
                          )
                          .first
                          .actions!
                          .map(
                            (action) => PopupMenuItem<String>(
                              value: action.action,
                              child: SmallTextNotoSans(
                                text: LocalizeUtils.getTakeActionLocal(
                                  action.action,
                                  workflowCode: _timelineController
                                      .workflowBusinessServices
                                      .businessServices!
                                      .first
                                      .businessService!,
                                  module: Modules.NOC,
                                  isCitizen: true,
                                ),
                                color: BaseConfig.textColor,
                                fontWeight: FontWeight.w600,
                                size: o == Orientation.portrait ? 14.sp : 8.sp,
                              ),
                            ),
                          )
                          .toList(),
                      onSelected: (value) async {
                        dPrint(value);

                        if (!context.mounted) return;

                        // _inboxController.actionDialogue(
                        //   context,
                        //   workFlowId: _timelineController.timeline!
                        //       .processInstancesList!.first.businessService!,
                        //   action: value,
                        //   module: Modules.COMMON,
                        //   sectionType: ModulesEmp.FIRE_NOC,
                        //   tenantId: fireNoc.tenantId!,
                        //   businessService: BusinessService.FIRE_NOC,
                        //   isCitizen: true,
                        // );

                        if (value == 'PAY' &&
                            isNotNullOrEmpty(
                              _fireNocController.billInfo.bill,
                            )) {
                          await goPayment();
                        } else {
                          snackBar(
                            'Error',
                            'Bill not found!',
                            BaseConfig.redColor,
                          );
                        }
                      },
                    ),
                  )
                : const SizedBox.shrink(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          physics: const BouncingScrollPhysics(),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
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
                          tenantId: fireNoc.tenantId!,
                        );
                      },
                      child: MediumText(
                        text: getLocalizedString(
                          i18.common.TIMELINE,
                        ),
                        color: BaseConfig.redColor1,
                      ),
                    ),
                    SizedBox(
                      width: 10.w,
                    ),
                    if (canDownloadReceipt())
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
                              text: getLocalizedString(i18.common.DOWNLOAD),
                              color: BaseConfig.redColor1,
                              size: 14.0,
                            ),
                          ],
                        ),
                        itemBuilder: (context) => <PopupMenuItem<String>>[
                          if (canDownloadReceipt())
                            PopupMenuItem<String>(
                              value: 'Download receipt',
                              child: const MediumText(
                                text: 'Download receipt',
                              ),
                              onTap: () async {
                                final payment =
                                    await _paymentController.verifyPayment(
                                  tenantId: fireNoc.tenantId!,
                                  token: _authController.token!.accessToken!,
                                  businessService: BusinessService.FIRENOC.name,
                                  consumerCodes: fireNoc
                                      .fireNocDetails!.applicationNumber!,
                                );

                                if (isNotNullOrEmpty(
                                  payment?.fileStoreId,
                                )) {
                                  final newFileStore =
                                      await _fileController.getFiles(
                                    fileStoreIds: payment!.fileStoreId!,
                                    tenantId: BaseConfig
                                        .STATE_TENANT_ID, //For Test ENV
                                    token: _authController.token!.accessToken!,
                                  );
                                  if (isNotNullOrEmpty(
                                    newFileStore?.fileStoreIds,
                                  )) {
                                    _downloadController.starFileDownload(
                                      url: newFileStore!
                                          .fileStoreIds!.first.url!,
                                      title: 'Download receipt',
                                    );
                                  }
                                } else {
                                  const PdfKey key =
                                      PdfKey.consolidated_receipt;
                                  final fileStoreId =
                                      await _fileController.getPdfServiceFile(
                                    tenantId: BaseConfig.STATE_TENANT_ID,
                                    token: _authController.token!.accessToken!,
                                    key: key,
                                    payment: payment,
                                  );

                                  final fileStore =
                                      await _fileController.getFiles(
                                    tenantId: BaseConfig.STATE_TENANT_ID,
                                    token: _authController.token!.accessToken!,
                                    fileStoreIds: fileStoreId,
                                  );

                                  if (fileStore?.fileStoreIds != null) {
                                    final url = fileStore!
                                        .fileStoreIds!.first.url!
                                        .split(',')
                                        .first;

                                    await _downloadController.starFileDownload(
                                      url: url,
                                      title: 'Download receipt',
                                    );
                                  }
                                }
                              },
                            ),
                        ],
                      ),
                  ],
                ),
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(10.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.noc.NOC_APPLICATION_NO,
                            module: Modules.NOC,
                          ),
                          text: fireNoc.fireNocDetails?.applicationNumber ??
                              "N/A",
                        ).paddingOnly(bottom: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(i18.common.DATE_LABEL),
                          text: fireNoc.auditDetails?.createdTime
                                  .toCustomDateFormat() ??
                              "N/A",
                        ).paddingOnly(bottom: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(i18.common.NOC_TYPE),
                          text: fireNoc.fireNocDetails?.fireNocType ?? "N/A",
                        ).paddingOnly(bottom: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.noc.NOC_PROV_FIRE,
                            module: Modules.NOC,
                          ),
                          text: fireNoc.fireNocNumber ?? "N/A",
                        ).paddingOnly(bottom: 10),
                        ColumnHeaderText(
                          label:
                              getLocalizedString(i18.common.APPLICATION_STATUS),
                          text: getLocalizedString(
                            'WF_FIRENOC_${fireNoc.fireNocDetails?.status}',
                          ),
                        ).paddingOnly(bottom: 10),
                      ],
                    ),
                  ),
                ),
                const SizedBox(
                  height: 10,
                ),
                BuildCard(
                  child: Column(
                    children: [
                      BuildExpansion(
                        title: getLocalizedString(i18.noc.NOC_FEE_ESTIMATE),
                        onExpansionChanged: (value) {
                          _getFee();
                        },
                        children: [
                          if (
                              // fireNoc.fireNocDetails?.status ==
                              //       FireNocStatus.INITIATED.name ||
                              fireNoc.fireNocDetails?.status ==
                                  FireNocStatus.PENDING_PAYMENT.name) ...[
                            StreamBuilder(
                              stream:
                                  _fireNocController.fireNocBillStream.stream,
                              builder: (context, AsyncSnapshot snapshot) {
                                if (snapshot.hasData) {
                                  final billData = snapshot.data;
                                  if (billData is b.BillInfo) {
                                    if (!isNotNullOrEmpty(billData.bill)) {
                                      return MediumTextNotoSans(
                                        text: getLocalizedString(
                                          i18.common.NO_DATA,
                                        ),
                                      ).paddingOnly(bottom: 10.h);
                                    } else {
                                      return ListView.builder(
                                        itemCount: billData.bill?.length,
                                        shrinkWrap: true,
                                        physics:
                                            const NeverScrollableScrollPhysics(),
                                        itemBuilder: (context, index) {
                                          final billELement =
                                              billData.bill![index];
                                          return _buildBillEstimate(
                                            billELement: billELement,
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
                          ] else ...[
                            StreamBuilder(
                              stream:
                                  _fireNocController.fireNocFeeStream.stream,
                              builder: (context, AsyncSnapshot snapshot) {
                                if (snapshot.hasData) {
                                  final feeData = snapshot.data;
                                  if (feeData is PaymentModel) {
                                    if (!isNotNullOrEmpty(feeData.payments)) {
                                      return MediumTextNotoSans(
                                        text: getLocalizedString(
                                          i18.common.NO_DATA,
                                        ),
                                      ).paddingOnly(bottom: 10.h);
                                    } else {
                                      return ListView.builder(
                                        itemCount: feeData.payments?.length,
                                        shrinkWrap: true,
                                        physics:
                                            const NeverScrollableScrollPhysics(),
                                        itemBuilder: (context, index) {
                                          final feeELement =
                                              feeData.payments![index];
                                          return _buildFeeEstimate(
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
                        ],
                      ),
                      _buildExpansion(),
                      _propertyLocationDetails(),
                      _buildApplicant(),
                      if (isNotNullOrEmpty(
                        fireNoc.fireNocDetails?.additionalDetail
                            ?.ownerAuditionalDetail?.documents,
                      ))
                        _buildDoc(),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildBillEstimate({
    required b.Bill billELement,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SmallTextNotoSans(
          text: getLocalizedString(
            i18.noc.NOC_TOTAL_AMOUNT,
          ),
        ),
        Row(
          children: [
            const Icon(
              Icons.currency_rupee_outlined,
              size: 15,
            ),
            MediumTextNotoSans(
              text: "${billELement.totalAmount ?? "N/A"}",
              fontWeight: FontWeight.w600,
            ),
          ],
        ),
        MediumTextNotoSans(
          text: getLocalizedString(i18.noc.NOC_NOT_PAID, module: Modules.NOC),
          color: BaseConfig.redColor,
        ),
        const SizedBox(
          height: 10,
        ),
        const Divider(
          color: BaseConfig.greyColor2,
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            MediumTextNotoSans(
              text: getLocalizedString(
                i18.noc.FIRENOC_ROUNDOFF,
                module: Modules.NOC,
              ),
            ),
            MediumTextNotoSans(
              text: billELement.billDetails!.first.billAccountDetails!
                  .firstWhere(
                    (e) => e.taxHeadCode == "FIRENOC_ROUNDOFF",
                  )
                  .amount
                  .toString(),
            ),
          ],
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            MediumTextNotoSans(
              text:
                  getLocalizedString(i18.noc.FIRENOC_FEES, module: Modules.NOC),
            ),
            MediumTextNotoSans(
              text: billELement.billDetails!.first.billAccountDetails!
                  .firstWhere((e) => e.taxHeadCode == "FIRENOC_FEES")
                  .amount
                  .toString(),
            ),
          ],
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            MediumTextNotoSans(
              text: getLocalizedString(
                i18.noc.FIRENOC_TAXES,
                module: Modules.NOC,
              ),
            ),
            MediumTextNotoSans(
              text: billELement.billDetails!.first.billAccountDetails!
                  .firstWhere((e) => e.taxHeadCode == "FIRENOC_TAXES")
                  .amount
                  .toString(),
            ),
          ],
        ),
        const Divider(
          color: BaseConfig.greyColor2,
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            MediumTextNotoSans(
              text: getLocalizedString(i18.noc.NOC_TOTAL_AMOUNT),
              fontWeight: FontWeight.w600,
            ),
            MediumTextNotoSans(
              text: billELement.totalAmount.toString(),
              fontWeight: FontWeight.w600,
            ),
          ],
        ),
        const SizedBox(
          height: 10,
        ),
      ],
    );
  }

  Widget _buildFeeEstimate({
    required Payment feeELement,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SmallTextNotoSans(
          text: getLocalizedString(i18.noc.NOC_TOTAL_AMOUNT),
        ),
        Row(
          children: [
            const Icon(
              Icons.currency_rupee_outlined,
              size: 15,
            ),
            MediumTextNotoSans(
              text: "${feeELement.totalAmountPaid ?? "N/A"}",
              fontWeight: FontWeight.w600,
            ),
          ],
        ),
        MediumTextNotoSans(
          text:
              getLocalizedString(i18.noc.NOC_PAID_SUCCESS, module: Modules.NOC),
          color: BaseConfig.statusGreenColor,
        ),
        const SizedBox(
          height: 10,
        ),
        const Divider(
          color: BaseConfig.greyColor2,
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            MediumTextNotoSans(
              text: getLocalizedString(
                i18.noc.FIRENOC_ROUNDOFF,
                module: Modules.NOC,
              ),
            ),
            MediumTextNotoSans(
              text: feeELement.paymentDetails!.first.bill!.billDetails!.first
                  .billAccountDetails!
                  .firstWhere(
                    (e) => e.taxHeadCode == "FIRENOC_ROUNDOFF",
                  )
                  .amount
                  .toString(),
            ),
          ],
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            MediumTextNotoSans(
              text:
                  getLocalizedString(i18.noc.FIRENOC_FEES, module: Modules.NOC),
            ),
            MediumTextNotoSans(
              text: feeELement.paymentDetails!.first.bill!.billDetails!.first
                  .billAccountDetails!
                  .firstWhere((e) => e.taxHeadCode == "FIRENOC_FEES")
                  .amount
                  .toString(),
            ),
          ],
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            MediumTextNotoSans(
              text: getLocalizedString(
                i18.noc.FIRENOC_TAXES,
                module: Modules.NOC,
              ),
            ),
            MediumTextNotoSans(
              text: feeELement.paymentDetails!.first.bill!.billDetails!.first
                  .billAccountDetails!
                  .firstWhere((e) => e.taxHeadCode == "FIRENOC_TAXES")
                  .amount
                  .toString(),
            ),
          ],
        ),
        const Divider(
          color: BaseConfig.greyColor2,
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            MediumTextNotoSans(
              text: getLocalizedString(i18.noc.NOC_TOTAL_AMOUNT),
              fontWeight: FontWeight.w600,
            ),
            MediumTextNotoSans(
              text: feeELement.totalAmountPaid.toString(),
              fontWeight: FontWeight.w600,
            ),
          ],
        ),
        const SizedBox(
          height: 10,
        ),
      ],
    );
  }

  Widget _buildExpansion() {
    return ExpansionTile(
      title: MediumTextNotoSans(
        text: getLocalizedString(
          i18.noc.NOC_PROPERTY_HEADER,
          module: Modules.NOC,
        ),
        fontWeight: FontWeight.w600,
      ),
      expandedCrossAxisAlignment: CrossAxisAlignment.start,
      children: [
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_TYPE,
            module: Modules.NOC,
          ),
          text: fireNoc.fireNocDetails?.noOfBuildings ?? "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(i18.noc.NOC_BUILDING, module: Modules.NOC),
          text: fireNoc.fireNocDetails?.buildings?.first.name ?? "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_BUILDING_USAGE,
            module: Modules.NOC,
          ),
          text: getLocalizedString(
            'FIRENOC_BUILDINGTYPE_${fireNoc.fireNocDetails!.buildings!.first.usageType!.split('.').first}',
          ),
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_BUILDING_USAGE_SUBTYPE,
            module: Modules.NOC,
          ),
          text: getLocalizedString(
            'FIRENOC_BUILDINGTYPE_${fireNoc.fireNocDetails!.buildings!.first.usageType!.split('.').first}_SUBDIVISIONA_1',
          ),
        ).paddingOnly(bottom: 10),
      ],
    );
  }

  Widget _buildApplicant() {
    return ExpansionTile(
      title: MediumTextNotoSans(
        text: getLocalizedString(
          i18.common.NOC_APPLICANT_DETAILS,
        ),
        fontWeight: FontWeight.w600,
      ),
      children: [
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_APPLICANT_MOBILE,
          ),
          text: fireNoc.fireNocDetails?.applicantDetails?.owners?.first
                  .mobileNumber ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(i18.common.NOC_APPLICANT_NAME),
          text: fireNoc.fireNocDetails?.applicantDetails?.owners?.first.name ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(i18.noc.NOC_GENDER, module: Modules.NOC),
          text:
              fireNoc.fireNocDetails?.applicantDetails?.owners?.first.gender ??
                  "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_APPLICANT_FATHER,
            module: Modules.NOC,
          ),
          text: fireNoc.fireNocDetails?.applicantDetails?.owners?.first
                  .fatherOrHusbandName ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_APPLICANT_RELATIONSHIP,
          ),
          text: fireNoc.fireNocDetails?.applicantDetails?.owners?.first
                  .relationship ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.common.NOC_APPLICANT_DOB,
          ),
          text: fireNoc.fireNocDetails?.applicantDetails?.owners?.first.dob
                  .toCustomDateFormat() ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.common.NOC_APPLICANT_EMAIL,
          ),
          text:
              fireNoc.fireNocDetails?.applicantDetails?.owners?.first.emailId ??
                  "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_APPLICANT_PAN,
          ),
          text: fireNoc.fireNocDetails?.applicantDetails?.owners?.first.pan ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_APPLICANT_CORRESPONDENCE,
            module: Modules.NOC,
          ),
          text: fireNoc.fireNocDetails?.applicantDetails?.owners?.first
                  .correspondenceAddress ??
              "N/A",
        ).paddingOnly(bottom: 10),
      ],
    );
  }

  Widget _propertyLocationDetails() {
    return ExpansionTile(
      title: MediumTextNotoSans(
        text: getLocalizedString(
          i18.noc.NOC_PROPERTY_LOCATION,
          module: Modules.NOC,
        ),
        fontWeight: FontWeight.w600,
      ),
      children: [
        ColumnHeaderText(
          label:
              getLocalizedString(i18.noc.NOC_PROPERTY_ID, module: Modules.NOC),
          text: fireNoc.fireNocDetails?.propertyDetails?.propertyId ?? "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_CITY,
            module: Modules.NOC,
          ),
          text: getLocalizedString(
            '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}_${fireNoc.fireNocDetails?.propertyDetails?.address?.city?.split('.').last}'
                .toUpperCase(),
          ),
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_DOOR,
            module: Modules.NOC,
          ),
          text:
              fireNoc.fireNocDetails?.propertyDetails?.address?.doorNo ?? "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_COLONY,
            module: Modules.NOC,
          ),
          text:
              fireNoc.fireNocDetails?.propertyDetails?.address?.buildingName ??
                  "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_STREET,
            module: Modules.NOC,
          ),
          text:
              fireNoc.fireNocDetails?.propertyDetails?.address?.street ?? "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_MOHALLA,
            module: Modules.NOC,
          ),
          text: getLocalizedString(
            fireNoc.fireNocDetails?.propertyDetails?.address?.locality?.code,
          ),
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label:
              getLocalizedString(i18.noc.NOC_PROPERTY_PIN, module: Modules.NOC),
          text: fireNoc.fireNocDetails?.propertyDetails?.address?.pincode ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_FIRESTATION,
            module: Modules.NOC,
          ),
          text: getLocalizedString(
            'FIRENOC_FIRESTATIONS_${fireNoc.fireNocDetails?.firestationId}',
          ),
        ).paddingOnly(bottom: 10),
      ],
    );
  }

  Widget _buildDoc() {
    return ExpansionTile(
      title: MediumTextNotoSans(
        text: getLocalizedString(
          i18.noc.NOC_SUMMARY_DOCUMENTS,
          module: Modules.NOC,
        ),
        fontWeight: FontWeight.w600,
      ),
      children: [
        FutureBuilder(
          future: _fileController.getFiles(
            tenantId: BaseConfig.STATE_TENANT_ID,
            token: _authController.token!.accessToken!,
            fileStoreIds: getFileStoreIds(),
          ),
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              var fileData = snapshot.data!;
              return _buildEvidenceCard(fileData);
            } else if (snapshot.hasError) {
              return networkErrorPage(
                context,
                () => _fileController.getFiles(
                  tenantId: BaseConfig.STATE_TENANT_ID,
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
        const SizedBox(height: 20),
      ],
    );
  }

  //Pdf Widget viewer
  Widget _buildEvidenceCard(FileStore fileStore) {
    return Padding(
      padding: const EdgeInsets.all(10.0),
      child: GridView.builder(
        itemCount: fileStore.fileStoreIds?.length,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          crossAxisSpacing: 10.0,
          mainAxisSpacing: 10.0,
          mainAxisExtent: 110.0,
        ),
        shrinkWrap: true,
        itemBuilder: (context, index) {
          final fileUrl = fileStore.fileStoreIds?[index].url?.split(',').first;
          final docType = fireNoc.fireNocDetails?.additionalDetail
              ?.ownerAuditionalDetail?.documents!
              .firstWhere(
            (element) =>
                element.fileStoreId == fileStore.fileStoreIds?[index].id,
            orElse: () => OwnerAuditionalDetailDocument(),
          );
          return isNotNullOrEmpty(docType)
              ? Column(
                  children: [
                    Container(
                      width: Get.width,
                      decoration: BoxDecoration(
                        color: BaseConfig.greyColor2,
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: fileUrl != null
                            ? Icon(
                                _fileController.getFileType(fileUrl).$1,
                                size: 40,
                                color: Colors.grey.shade600,
                              )
                            : Icon(
                                Icons.error,
                                size: 40,
                                color: Colors.grey.shade600,
                              ),
                      ),
                    ),
                    const SizedBox(height: 10),
                    Tooltip(
                      message: getLocalizedString(
                        docType?.documentType?.replaceAll('.', '_'),
                      ),
                      child: SmallTextNotoSans(
                        text: getLocalizedString(
                          docType?.documentType?.replaceAll('.', '_'),
                        ),
                        color: Colors.grey.shade600,
                        maxLine: 2,
                        textOverflow: TextOverflow.ellipsis,
                      ),
                    ),
                  ],
                ).ripple(() {
                  if (fileUrl != null) {
                    final fileType = _fileController.getFileType(fileUrl).$2;
                    dPrint('FileType: ${fileType.name}');
                    if (fileType.name == FileExtType.pdf.name) {
                      showTypeDialogue(
                        context,
                        url: fileUrl,
                        isPdf: true,
                        title: getLocalizedString(
                          docType?.documentType?.replaceAll('.', '_'),
                        ),
                      );
                    } else {
                      showTypeDialogue(
                        context,
                        url: fileUrl,
                        title: getLocalizedString(
                          docType?.documentType?.replaceAll('.', '_'),
                        ),
                      );
                    }
                  } else {
                    dPrint("fileUrl is null");
                    showSnackBar(
                      context,
                      getLocalizedString(i18.common.DOCUMENTS_NOT_FOUND),
                    );
                  }
                })
              : const SizedBox.shrink();
        },
      ),
    );
  }
}
