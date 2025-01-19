import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/fire_noc_controller.dart';
import 'package:mobile_app/controller/inbox_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart' as b;
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/model/citizen/fire_noc/fire_noc.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/model/citizen/timeline/timeline.dart' as t;
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
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

class EmpFireNocDetails extends StatefulWidget {
  const EmpFireNocDetails({super.key});

  @override
  State<EmpFireNocDetails> createState() => _EmpFireNocDetailsState();
}

class _EmpFireNocDetailsState extends State<EmpFireNocDetails> {
  final _fileController = Get.find<FileController>();
  final _authController = Get.find<AuthController>();
  final _fireNocController = Get.find<FireNocController>();
  final _timelineController = Get.find<TimelineController>();
  final _inboxController = Get.find<InboxController>();
  final Completer<FileStore?> fileStoreFuture = Completer<FileStore?>();

  final process = Get.arguments as t.ProcessInstanceTimeline;
  final _isLoading = true.obs;
  bool _isTimelineFetch = false;
  bool _isFeeFetch = false;
  bool _isFileFetch = false;
  late TenantTenant tenant;

  @override
  void initState() {
    super.initState();
    _init();
  }

  _init() async {
    _isLoading.value = true;
    tenant = await getCityTenantEmployee();
    await _fireNocController.getFireNocApplicationById(
      token: _authController.token!.accessToken!,
      tenantId: tenant.code!,
      applicationNo: process.businessId!,
    );
    if (process.state?.state == InboxStatus.FIELD_INSPECTION.name) {
      await _getTimeline();
      await _getWorkflow();
    }
    _isLoading.value = false;
  }

  Future<void> _getWorkflow() async {
    try {
      await _timelineController.getWorkFlow(
        token: _authController.token!.accessToken!,
        tenantId: tenant.code!,
        workFlow: process.businessService!,
      );
    } catch (e) {
      dPrint('Error in getting workflow: $e');
    }
  }

  Future<void> _getFee() async {
    if (_fireNocController.fireNoc?.fireNocDetails?.status ==
            FireNocStatus.INITIATED.name ||
        _fireNocController.fireNoc?.fireNocDetails?.status ==
            FireNocStatus.PENDING_PAYMENT.name) {
      await _fireNocController
          .getFireNocFeeDetailBill(
        token: _authController.token!.accessToken!,
        consumerCodes:
            _fireNocController.fireNoc!.fireNocDetails!.applicationNumber!,
        tenantId: _fireNocController.fireNoc!.tenantId!,
        businessService: BusinessService.FIRENOC,
      )
          .then((_) {
        setState(() {
          _isFeeFetch = true;
        });
      });
    } else {
      await _fireNocController
          .getFireNocFeeDetailPayment(
        token: _authController.token!.accessToken!,
        businessService: process.businessService!,
        consumerCodes:
            _fireNocController.fireNoc!.fireNocDetails!.applicationNumber!,
        tenantId: _fireNocController.fireNoc!.tenantId!,
      )
          .then((_) {
        setState(() {
          _isFeeFetch = true;
        });
      });
    }
  }

  String getFileStoreIds() {
    if (_fireNocController
            .fireNoc!.fireNocDetails?.additionalDetail?.documents ==
        null) {
      return '';
    }
    List fileIds = [];
    for (var element in _fireNocController.fireNoc!.fireNocDetails!
        .additionalDetail!.ownerAuditionalDetail!.documents!) {
      fileIds.add(element.fileStoreId);
    }
    return fileIds.join(', ');
  }

  Future<void> _getTimeline() async {
    await _timelineController
        .getTimelineHistory(
      token: _authController.token!.accessToken!,
      tenantId: _fireNocController.fireNoc!.tenantId!,
      businessIds:
          _fireNocController.fireNoc!.fireNocDetails!.applicationNumber!,
    )
        .then((val) {
      setState(() {
        _isTimelineFetch = true;
      });
    });
  }

  Future<void> getFiles() async {
    fileStoreFuture.complete(
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
      }),
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
            : (process.state?.state == InboxStatus.FIELD_INSPECTION.name)
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
                      itemBuilder: (context) => process.nextActions!
                          .map(
                            (action) => PopupMenuItem<String>(
                              value: action.action,
                              child: SmallTextNotoSans(
                                text: LocalizeUtils.getTakeActionLocal(
                                  action.action,
                                  workflowCode: process.businessService!,
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
                        //TODO: Take Action
                        dPrint(value);

                        // Get the next state of the action
                        List<String> roles = [];

                        for (var state in _timelineController
                            .workflowBusinessServices
                            .businessServices!
                            .first
                            .states!) {
                          if (state.state == process.state!.state! &&
                              state.applicationStatus ==
                                  process.state!.applicationStatus) {
                            for (var action in state.actions!) {
                              roles.addAll(action.roles!);
                            }
                          }
                        }

                        final userRoles = roles.join(',');

                        dPrint('UUID: $userRoles');

                        if (userRoles.isEmpty) {
                          snackBar(
                            'InComplete',
                            'Next State is Empty',
                            Colors.green,
                          );
                          return;
                        }

                        if (value != BaseAction.reject.name &&
                            value != BaseAction.sendBackToCitizen.name) {
                          await _timelineController.getEmployees(
                            token: _authController.token!.accessToken!,
                            tenantId: tenant.code!,
                            uuid: userRoles,
                            empNoc: true,
                          );
                        }

                        if (!context.mounted) return;

                        _inboxController.actionDialogue(
                          context,
                          workFlowId: process.businessService!,
                          action: value,
                          module: Modules.COMMON,
                          sectionType: ModulesEmp.FIRE_NOC,
                          tenantId: tenant.code!,
                          businessService: BusinessService.FIRE_NOC,
                        );
                      },
                    ),
                  )
                : const SizedBox.shrink(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Obx(
          () => _isLoading.value
              ? showCircularIndicator()
              : SingleChildScrollView(
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
                                  tenantId:
                                      _fireNocController.fireNoc!.tenantId!,
                                );
                              },
                              child: MediumText(
                                text: getLocalizedString(
                                  i18.common.TIMELINE,
                                ),
                                color: BaseConfig.redColor1,
                              ),
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
                                  text: _fireNocController.fireNoc
                                          ?.fireNocDetails?.applicationNumber ??
                                      "N/A",
                                ).paddingOnly(bottom: 10),
                                ColumnHeaderText(
                                  label:
                                      getLocalizedString(i18.common.DATE_LABEL),
                                  text: _fireNocController
                                          .fireNoc!.auditDetails!.createdTime!
                                          .toCustomDateFormat() ??
                                      "N/A",
                                ).paddingOnly(bottom: 10),
                                ColumnHeaderText(
                                  label:
                                      getLocalizedString(i18.common.NOC_TYPE),
                                  text: _fireNocController.fireNoc
                                          ?.fireNocDetails?.fireNocType ??
                                      "N/A",
                                ).paddingOnly(bottom: 10),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.noc.NOC_PROV_FIRE,
                                    module: Modules.NOC,
                                  ),
                                  text: _fireNocController
                                          .fireNoc!.fireNocNumber ??
                                      "N/A",
                                ).paddingOnly(bottom: 10),
                                ColumnHeaderText(
                                  label: getLocalizedString(
                                    i18.common.APPLICATION_STATUS,
                                  ),
                                  text: getLocalizedString(
                                    'WF_FIRENOC_${process.state?.state}',
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
                                title: getLocalizedString(
                                  i18.noc.NOC_FEE_ESTIMATE,
                                ),
                                tilePadding: const EdgeInsets.all(0),
                                onExpansionChanged: (value) {
                                  if (value && !_isFeeFetch) _getFee();
                                },
                                children: [
                                  if (_fireNocController.fireNoc?.fireNocDetails
                                              ?.status ==
                                          FireNocStatus.INITIATED.name ||
                                      _fireNocController.fireNoc?.fireNocDetails
                                              ?.status ==
                                          FireNocStatus
                                              .PENDING_PAYMENT.name) ...[
                                    StreamBuilder(
                                      stream: _fireNocController
                                          .fireNocBillStream.stream,
                                      builder:
                                          (context, AsyncSnapshot snapshot) {
                                        if (snapshot.hasData) {
                                          final billData = snapshot.data;
                                          if (billData is b.BillInfo) {
                                            if (!isNotNullOrEmpty(
                                              billData.bill,
                                            )) {
                                              return MediumTextNotoSans(
                                                text: getLocalizedString(
                                                  i18.common.NO_DATA,
                                                ),
                                              );
                                            } else {
                                              return ListView.builder(
                                                itemCount:
                                                    billData.bill?.length,
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
                                              return showCircularIndicator();
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
                                      stream: _fireNocController
                                          .fireNocFeeStream.stream,
                                      builder:
                                          (context, AsyncSnapshot snapshot) {
                                        if (snapshot.hasData) {
                                          final feeData = snapshot.data;
                                          if (feeData is PaymentModel) {
                                            if (!isNotNullOrEmpty(
                                              feeData.payments,
                                            )) {
                                              return MediumTextNotoSans(
                                                text: getLocalizedString(
                                                  i18.common.NO_DATA,
                                                ),
                                              );
                                            } else {
                                              return ListView.builder(
                                                itemCount:
                                                    feeData.payments?.length,
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
                                            return const SizedBox.shrink();
                                          }
                                        } else if (snapshot.hasError) {
                                          return networkErrorPage(
                                            context,
                                            () => _getFee(),
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
                                ],
                              ),
                              _buildExpansion(),
                              _propertyLocationDetails(),
                              _buildApplicant(),
                              if (isNotNullOrEmpty(
                                _fireNocController
                                    .fireNoc
                                    ?.fireNocDetails
                                    ?.additionalDetail
                                    ?.ownerAuditionalDetail
                                    ?.documents,
                              )) ...[
                                _buildDoc(),
                              ],
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
    return BuildExpansion(
      title: getLocalizedString(
        i18.noc.NOC_PROPERTY_HEADER,
        module: Modules.NOC,
      ),
      tilePadding: const EdgeInsets.all(0),
      children: [
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_TYPE,
            module: Modules.NOC,
          ),
          text: _fireNocController.fireNoc?.fireNocDetails?.noOfBuildings ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(i18.noc.NOC_BUILDING, module: Modules.NOC),
          text: _fireNocController
                  .fireNoc!.fireNocDetails?.buildings?.first.name ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_BUILDING_USAGE,
            module: Modules.NOC,
          ),
          text: isNotNullOrEmpty(
            _fireNocController
                .fireNoc?.fireNocDetails?.buildings?.firstOrNull?.usageType,
          )
              ? getLocalizedString(
                  'FIRENOC_BUILDINGTYPE_${_fireNocController.fireNoc!.fireNocDetails!.buildings!.first.usageType!.split('.').first}',
                )
              : "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_BUILDING_USAGE_SUBTYPE,
            module: Modules.NOC,
          ),
          text: isNotNullOrEmpty(
            _fireNocController
                .fireNoc?.fireNocDetails?.buildings?.firstOrNull?.usageType,
          )
              ? getLocalizedString(
                  'FIRENOC_BUILDINGTYPE_${_fireNocController.fireNoc!.fireNocDetails!.buildings!.first.usageType!.split('.').first}_SUBDIVISIONA_1',
                )
              : "N/A",
        ).paddingOnly(bottom: 10),
      ],
    );
  }

  Widget _buildApplicant() {
    return BuildExpansion(
      title: getLocalizedString(
        i18.common.NOC_APPLICANT_DETAILS,
      ),
      tilePadding: const EdgeInsets.all(0),
      children: [
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_APPLICANT_MOBILE,
          ),
          text: _fireNocController.fireNoc?.fireNocDetails?.applicantDetails
                  ?.owners?.first.mobileNumber ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(i18.common.NOC_APPLICANT_NAME),
          text: _fireNocController.fireNoc?.fireNocDetails?.applicantDetails
                  ?.owners?.first.name ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(i18.noc.NOC_GENDER, module: Modules.NOC),
          text: _fireNocController.fireNoc?.fireNocDetails?.applicantDetails
                  ?.owners?.firstOrNull?.gender ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_APPLICANT_FATHER,
            module: Modules.NOC,
          ),
          text: _fireNocController.fireNoc?.fireNocDetails?.applicantDetails
                  ?.owners?.first.fatherOrHusbandName ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_APPLICANT_RELATIONSHIP,
          ),
          text: _fireNocController.fireNoc?.fireNocDetails?.applicantDetails
                  ?.owners?.first.relationship ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.common.NOC_APPLICANT_DOB,
          ),
          text: _fireNocController
                  .fireNoc!.fireNocDetails?.applicantDetails?.owners?.first.dob
                  .toCustomDateFormat() ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.common.NOC_APPLICANT_EMAIL,
          ),
          text: _fireNocController.fireNoc?.fireNocDetails?.applicantDetails
                  ?.owners?.first.emailId ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_APPLICANT_PAN,
          ),
          text: _fireNocController.fireNoc?.fireNocDetails?.applicantDetails
                  ?.owners?.first.pan ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_APPLICANT_CORRESPONDENCE,
            module: Modules.NOC,
          ),
          text: _fireNocController.fireNoc?.fireNocDetails?.applicantDetails
                  ?.owners?.first.correspondenceAddress ??
              "N/A",
        ).paddingOnly(bottom: 10),
      ],
    );
  }

  Widget _propertyLocationDetails() {
    return BuildExpansion(
      title: getLocalizedString(
        i18.noc.NOC_PROPERTY_LOCATION,
        module: Modules.NOC,
      ),
      tilePadding: const EdgeInsets.all(0),
      children: [
        ColumnHeaderText(
          label:
              getLocalizedString(i18.noc.NOC_PROPERTY_ID, module: Modules.NOC),
          text: _fireNocController
                  .fireNoc?.fireNocDetails?.propertyDetails?.propertyId ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_CITY,
            module: Modules.NOC,
          ),
          text: isNotNullOrEmpty(
            _fireNocController
                .fireNoc?.fireNocDetails?.propertyDetails?.address?.city,
          )
              ? getLocalizedString(
                  '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}_${_fireNocController.fireNoc!.fireNocDetails!.propertyDetails!.address!.city!.split('.').last}'
                      .toUpperCase(),
                )
              : "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_DOOR,
            module: Modules.NOC,
          ),
          text: _fireNocController
                  .fireNoc?.fireNocDetails?.propertyDetails?.address?.doorNo ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_COLONY,
            module: Modules.NOC,
          ),
          text: _fireNocController.fireNoc?.fireNocDetails?.propertyDetails
                  ?.address?.buildingName ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_STREET,
            module: Modules.NOC,
          ),
          text: _fireNocController
                  .fireNoc?.fireNocDetails?.propertyDetails?.address?.street ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_MOHALLA,
            module: Modules.NOC,
          ),
          text: getLocalizedString(
            _fireNocController.fireNoc?.fireNocDetails?.propertyDetails?.address
                ?.locality?.code,
          ),
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label:
              getLocalizedString(i18.noc.NOC_PROPERTY_PIN, module: Modules.NOC),
          text: _fireNocController
                  .fireNoc?.fireNocDetails?.propertyDetails?.address?.pincode ??
              "N/A",
        ).paddingOnly(bottom: 10),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.noc.NOC_PROPERTY_FIRESTATION,
            module: Modules.NOC,
          ),
          text: isNotNullOrEmpty(
            _fireNocController.fireNoc?.fireNocDetails?.firestationId,
          )
              ? getLocalizedString(
                  'FIRENOC_FIRESTATIONS_${_fireNocController.fireNoc!.fireNocDetails!.firestationId}',
                )
              : "N/A",
        ).paddingOnly(bottom: 10),
      ],
    );
  }

  Widget _buildDoc() {
    return BuildExpansion(
      title: getLocalizedString(
        i18.noc.NOC_SUMMARY_DOCUMENTS,
        module: Modules.NOC,
      ),
      tilePadding: const EdgeInsets.all(0),
      onExpansionChanged: (p0) async {
        if (p0 && !_isFileFetch) {
          await getFiles();
        }
      },
      children: [
        FutureBuilder(
          future: fileStoreFuture.future,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final fileData = snapshot.data!;
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
          final docType = _fireNocController.fireNoc!.fireNocDetails
              ?.additionalDetail?.ownerAuditionalDetail?.documents!
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
                        child: isNotNullOrEmpty(fileUrl)
                            ? Icon(
                                _fileController.getFileType(fileUrl!).$1,
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
