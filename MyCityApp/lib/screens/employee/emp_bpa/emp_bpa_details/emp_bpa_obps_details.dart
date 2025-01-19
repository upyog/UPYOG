import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/bpa_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/inbox_controller.dart';
import 'package:mobile_app/controller/obps_dynamic_form_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/model/citizen/bpa_model/bpa_model.dart';
import 'package:mobile_app/model/citizen/bpa_model/noc/noc_model.dart';
import 'package:mobile_app/model/citizen/bpa_model/scrutiny_model.dart';
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/model/employee/emp_bpa_model/emp_bpa_model.dart'
    as bp;
import 'package:mobile_app/model/employee/status_map/status_map.dart';
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
import 'package:mobile_app/widgets/employee/obps_inspection_report/obps_inspection_report.dart';
import 'package:mobile_app/widgets/file_dialogue/file_dilaogue.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/timeline_widget.dart/timeline_wdget.dart';

class EmpBpaObpsDetails extends StatefulWidget {
  const EmpBpaObpsDetails({super.key});

  @override
  State<EmpBpaObpsDetails> createState() => _EmpBpaObpsDetailsState();
}

class _EmpBpaObpsDetailsState extends State<EmpBpaObpsDetails> {
  final _authController = Get.find<AuthController>();
  final _bpaController = Get.find<BpaController>();
  final _timelineController = Get.find<TimelineController>();
  final _fileController = Get.find<FileController>();
  final _inboxController = Get.find<InboxController>();
  final _obpsDynamicController = Get.find<ObpsDynamicFormController>();

  final _isLoading = false.obs;
  bool _isTimelineFetch = false;

  late StatusMap statusMap;
  int index = 0;
  bp.Item? _item;

  Completer<FileStore?> fileStoreFuture = Completer<FileStore?>();

  @override
  initState() {
    index = Get.arguments?['index'] as int? ?? 0;
    _item = Get.arguments?['item'] as bp.Item?;
    statusMap = Get.arguments?['statusMap'] as StatusMap? ?? StatusMap();
    super.initState();
    _init();
  }

  _init() async {
    _isLoading.value = true;
    await _getApplicationNo();
    if (!isNotNullOrEmpty(_bpaController.bpa.bpaele)) {
      _isLoading.value = false;
      return;
    }
    await _getScrutiny();
    getFiles();
    await _getTimeline();
    await _getWorkflow();
    _isLoading.value = false;
  }

  void getFiles() {
    fileStoreFuture.complete(
      _fileController.getFiles(
        tenantId: BaseConfig.STATE_TENANT_ID,
        token: _authController.token!.accessToken!,
        fileStoreIds: getFileStoreIds(),
      ),
    );
  }

  String getFileStoreIds() {
    if (!isNotNullOrEmpty(_bpaData?.documents)) return '';

    List fileIds = [];
    for (var element in _bpaData!.documents!) {
      fileIds.add(element.fileStoreId);
    }
    return fileIds.join(', ');
  }

  Future<void> _getApplicationNo() async {
    await _bpaController.getIndividualBpaApplications(
      token: _authController.token!.accessToken!,
      applicationNo: _item!.businessObject!.applicationNo,
      tenantId: _item!.businessObject!.tenantId!,
    );
  }

  Future<void> _getScrutiny() async {
    await _bpaController.getIndividualEdcrApplications(
      token: _authController.token?.accessToken.toString(),
      edcrNo: _item!.businessObject!.edcrNumber,
      tenantId: _item!.businessObject!.tenantId!,
    );
  }

  Future<void> _getTimeline() async {
    await _timelineController
        .getTimelineHistory(
      token: _authController.token!.accessToken!,
      tenantId: _bpaData!.tenantId.toString(),
      businessIds: _item!.businessObject!.applicationNo!,
    )
        .then((_) {
      setState(() {
        _isTimelineFetch = true;
      });
    });
  }

  Future<void> _getWorkflow() async {
    try {
      await _timelineController.getWorkFlow(
        token: _authController.token!.accessToken!,
        tenantId: _item!.processInstance!.tenantId!,
        workFlow: _item!.businessObject!.businessService!,
      );
    } catch (e) {
      dPrint('Error in getting workflow: $e');
    }
  }

  // Get application by Id
  BpaElement? get _bpaData => _bpaController.getApplicationById(
        applicationNo: _item!.businessObject!.applicationNo!,
      );

  // Get Edrc by edrcNo
  EdcrDetail? get _edrcData =>
      _bpaController.getEdcrList(edcrNo: _item?.businessObject?.edcrNumber);

  Future<void> _getNoc() async {
    try {
      await _bpaController.getBpaNocDetail(
        token: _authController.token!.accessToken!,
        sourceRefId: _bpaData!.applicationNo!,
        tenantId: _bpaData!.tenantId!,
      );
    } catch (e) {
      dPrint('Error in getting Noc: $e');
    }
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
            : (isNotNullOrEmpty(_bpaData?.documents)
                ? _item!.businessObject!.status! ==
                        InboxStatus.FIELD_INSPECTION_INPROGRESS.name
                    ? Container(
                        height: 44.h,
                        width: Get.width,
                        margin: EdgeInsets.all(
                          o == Orientation.portrait ? 16.w : 12.w,
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
                              .where((s) => s.uuid == statusMap.statusId)
                              .first
                              .actions!
                              .map(
                                (action) => PopupMenuItem<String>(
                                  value: action.action,
                                  child: SmallTextNotoSans(
                                    text: LocalizeUtils.getTakeActionLocal(
                                      action.action,
                                      workflowCode: statusMap.businessService!,
                                      module: Modules.BPA,
                                    ),
                                    color: BaseConfig.textColor,
                                    fontWeight: FontWeight.w600,
                                    size: o == Orientation.portrait
                                        ? 14.sp
                                        : 8.sp,
                                  ),
                                ),
                              )
                              .toList(),
                          onSelected: (value) async {
                            //TODO: Take Action
                            dPrint(value);

                            final isValid =
                                _obpsDynamicController.validatedField();

                            if (!isValid) {
                              snackBar(
                                'InComplete',
                                'Please answer the required questions',
                                BaseConfig.redColor,
                              );
                              return;
                            }

                            // Get the next state of the action
                            String uuid = _timelineController
                                    .workflowBusinessServices
                                    .businessServices
                                    ?.first
                                    .states
                                    ?.where((s) => s.uuid == statusMap.statusId)
                                    .first
                                    .actions
                                    ?.where((a) => a.action == value)
                                    .first
                                    .nextState ??
                                '';

                            dPrint('UUID: $uuid');

                            if (uuid.isEmpty) {
                              snackBar(
                                'InComplete',
                                'Next State is Empty',
                                Colors.green,
                              );
                              return;
                            }

                            if (value != BaseAction.reject.name &&
                                value != BaseAction.sendBackToCitizen.name &&
                                value != BaseAction.revocate.name) {
                              await _timelineController.getEmployees(
                                token: _authController.token!.accessToken!,
                                tenantId: _item!.businessObject!.tenantId!,
                                uuid: uuid,
                              );
                            }

                            if (!context.mounted) return;

                            _inboxController.actionDialogue(
                              context,
                              workFlowId: statusMap.businessService!,
                              action: value,
                              module: Modules.BPA,
                              sectionType: ModulesEmp.BPA_SERVICES,
                              tenantId: _item!.businessObject!.tenantId!,
                              businessService: BusinessService.OBPS,
                            );
                          },
                        ),
                      )
                    : const SizedBox.shrink()
                : const SizedBox.shrink()),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Obx(
          () => _isLoading.value
              ? showCircularIndicator()
              : !isNotNullOrEmpty(_bpaController.bpa.bpaele)
                  ? const NoApplicationFoundWidget()
                  : SingleChildScrollView(
                      physics: AppPlatforms.platformPhysics(),
                      child: Padding(
                        padding: EdgeInsets.all(16.w),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            TextButton(
                              onPressed: () async {
                                if (!_isTimelineFetch) {
                                  await _getTimeline();
                                }

                                TimelineHistoryApp.buildTimelineDialogue(
                                  context,
                                  tenantId: BaseConfig.STATE_TENANT_ID,
                                );
                              },
                              child: MediumText(
                                text: getLocalizedString(
                                  i18.common.TIMELINE,
                                ),
                                color: BaseConfig.redColor1,
                              ),
                            ),
                            ObpsInspectionReport(o: o, item: _item!),
                            SizedBox(
                              height: 10.h,
                            ),
                            _buildAppNo(),
                            SizedBox(
                              height: 10.h,
                            ),
                            _buildBasicDetail(),
                            SizedBox(
                              height: 10.h,
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
                                        text: isNotNullOrEmpty(
                                          _edrcData?.planDetail
                                              ?.planInfoProperties?.plotAreaM2,
                                        )
                                            ? '${_edrcData!.planDetail!.planInfoProperties!.plotAreaM2} ${getLocalizedString(i18.building.BPA_SQ_FT_LABEL, module: Modules.BPA)}'
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
                                                ?.planInfoProperties?.plotNo ??
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
                                        text: "N/A",
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.building.LAND_REGIS_DETAIL,
                                          module: Modules.BPA,
                                        ),
                                        text: "N/A",
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.building.BOUNDARY_WALL_LEN,
                                          module: Modules.BPA,
                                        ),
                                        text: "N/A",
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                    ],
                                  ),
                                  if (_edrcData?.planReport != null)
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
                                  if (isNotNullOrEmpty(
                                    _edrcData?.planDetail?.blocks?.first
                                        .building?.floors,
                                  ))
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
                                        text: _bpaData
                                                ?.landInfo?.address?.pincode ??
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
                                        text:
                                            _bpaData?.landInfo?.address?.city ??
                                                "N/A",
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.building.LOCALITY,
                                          module: Modules.BPA,
                                        ),
                                        text:
                                            '${_bpaData?.landInfo?.address?.locality?.code} / ${_bpaData?.landInfo?.address?.locality?.name}',
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.building.STREET,
                                          module: Modules.BPA,
                                        ),
                                        text: _bpaData
                                                ?.landInfo?.address?.street ??
                                            "N/A",
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.building.LANDMARK,
                                        ),
                                        text: _bpaData
                                                ?.landInfo?.address?.landmark ??
                                            "N/A",
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                    ],
                                  ),
                                  //---------------------Owner Details-------------------------------
                                  BuildExpansion(
                                    title: getLocalizedString(
                                      i18.building.OWNER_DETAILS,
                                      module: Modules.BPA,
                                    ),
                                    children: [
                                      ColumnHeaderText(
                                        label:
                                            getLocalizedString(i18.common.NAME),
                                        text: _bpaData?.landInfo?.owners
                                                ?.firstOrNull?.name ??
                                            "N/A",
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.building.GENDER,
                                          module: Modules.BPA,
                                        ),
                                        text: _bpaData?.landInfo?.owners
                                                ?.firstOrNull?.gender ??
                                            "N/A",
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.common.MOBILE_NUMBER,
                                        ),
                                        text: _bpaData?.landInfo?.owners
                                                ?.firstOrNull?.mobileNumber ??
                                            "N/A",
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.common.EMAIL_ID,
                                        ),
                                        text: _bpaData?.landInfo?.owners
                                                ?.firstOrNull?.emailId ??
                                            "N/A",
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.building.PRIMARY_OWNER,
                                          module: Modules.BPA,
                                        ),
                                        text: _bpaData?.landInfo?.owners
                                                ?.firstOrNull?.isPrimaryOwner
                                                .toString() ??
                                            "N/A",
                                      ),
                                      SizedBox(
                                        height: 10.h,
                                      ),
                                    ],
                                  ),
                                  //---------------------Document Details-------------------------------
                                  if (isNotNullOrEmpty(_bpaData?.documents))
                                    BuildExpansion(
                                      title: getLocalizedString(
                                        i18.building.DOCUMENT_DETAILS,
                                        module: Modules.BPA,
                                      ),
                                      children: [
                                        FutureBuilder(
                                          future: fileStoreFuture.future,
                                          builder: (context, snapshot) {
                                            if (snapshot.hasData) {
                                              var fileData = snapshot.data!;
                                              return _buildEvidenceCard(
                                                fileData,
                                              );
                                            } else if (snapshot.hasError) {
                                              return networkErrorPage(
                                                context,
                                                () => _fileController.getFiles(
                                                  tenantId: BaseConfig
                                                      .STATE_TENANT_ID,
                                                  token: _authController
                                                      .token!.accessToken!,
                                                  fileStoreIds:
                                                      getFileStoreIds(),
                                                ),
                                              );
                                            } else {
                                              switch (
                                                  snapshot.connectionState) {
                                                case ConnectionState.waiting:
                                                  return showCircularIndicator();
                                                case ConnectionState.active:
                                                  return showCircularIndicator();
                                                default:
                                                  return const SizedBox
                                                      .shrink();
                                              }
                                            }
                                          },
                                        ),
                                        const SizedBox(height: 20),
                                      ],
                                    ),
                                  //---------------------Inspection Report-------------------------------
                                  if (isNotNullOrEmpty(
                                    _bpaData?.additionalDetails
                                        ?.fieldInspectionPending,
                                  ))
                                    BuildExpansion(
                                      title: getLocalizedString(
                                        i18.building.INSPECTION_REPORT,
                                        module: Modules.BPA,
                                      ),
                                      children: [
                                        _inspectionQuestionAnswerReportList1(),
                                        SizedBox(
                                          height: 10.h,
                                        ),
                                      ],
                                    ),
                                  BuildExpansion(
                                    title: getLocalizedString(
                                      i18.building.NOC_DETAILS,
                                      module: Modules.BPA,
                                    ),
                                    onExpansionChanged: (p0) {
                                      if (p0 == false) return;
                                      _getNoc();
                                    },
                                    children: [_buildNocWidget()],
                                  ),
                                  BuildExpansion(
                                    title: getLocalizedString(
                                      i18.building.BPA_FEE_DETAILS,
                                      module: Modules.BPA,
                                    ),
                                    onExpansionChanged: (p0) async {
                                      if (p0 == false) return;
                                      dPrint(
                                          'Expansion Changed: ${getBusinessServiceByType(
                                        _bpaData!.businessService!,
                                      )}');
                                      await _bpaController.getBpaFeeDetail(
                                        token:
                                            _authController.token!.accessToken!,
                                        consumerCodes: _bpaData?.applicationNo,
                                        tenantId: _bpaData!.tenantId!,
                                        isEmployee: true,
                                        businessService:
                                            getBusinessServiceByTypeEmp(
                                          _bpaData!.businessService!,
                                        ),
                                      );
                                    },
                                    children: [
                                      _feeDetailWidget(),
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

  Widget _buildAppNo() {
    return BuildCard(
      child: Column(
        children: [
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.APPLICATION_NUMBER,
              module: Modules.BPA,
            ),
            text: _bpaData?.applicationNo ?? "N/A",
          ),
        ],
      ),
    );
  }

  Widget _buildBasicDetail() {
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
          SizedBox(
            height: 10.h,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.APPLICATION_DATE,
              module: Modules.BPA,
            ),
            text: _bpaData?.auditDetails?.createdTime?.toCustomDateFormat() ??
                "dd-MM-yyyy",
          ),
          SizedBox(
            height: 10.h,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.APPLICATION_TYPE,
              module: Modules.BPA,
            ),
            text: isNotNullOrEmpty(_bpaData?.additionalDetails?.applicationType)
                ? getLocalizedString(
                    '${i18.building.BPA_APP_STATUS_PREF}${_bpaData?.additionalDetails?.applicationType}',
                  )
                : "N/A",
          ),
          SizedBox(
            height: 10.h,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.buildingReg.SERVICE_TYPE,
              module: Modules.BPAREG,
            ),
            text: isNotNullOrEmpty(_bpaData?.additionalDetails?.serviceType)
                ? getLocalizedString(_bpaData?.additionalDetails?.serviceType)
                : "N/A",
          ),
          SizedBox(
            height: 10.h,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.OCCUPANCY_TYPE,
              module: Modules.BPA,
            ),
            text: _bpaData?.landInfo?.unit?.first.occupancyType ?? "N/A",
          ),
          SizedBox(
            height: 10.h,
          ),
          ColumnHeaderText(
            label:
                getLocalizedString(i18.building.RISK_TYPE, module: Modules.BPA),
            text: _bpaData?.riskType ?? "N/A",
          ),
          SizedBox(
            height: 10.h,
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
        itemBuilder: (context, index) {
          final fileUrl = fileStore.fileStoreIds![index].url!.split(',').first;
          final docType = _bpaData!.documents!
              .where(
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
                      message: getLocalizedString(docType!.documentType),
                      child: SmallTextNotoSans(
                        text: getLocalizedString(docType.documentType),
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
                      title: getLocalizedString(docType.documentType),
                    );
                  } else {
                    showTypeDialogue(
                      context,
                      url: fileUrl,
                      title: getLocalizedString(docType.documentType),
                    );
                  }
                })
              : const SizedBox.shrink();
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
            text: _bpaData?.edcrNumber ?? "N/A",
          ),
          SizedBox(
            height: 10.h,
          ),
          MediumText(
            text: getLocalizedString(
              i18.building.UPLOADED_PLAN_DIAG,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.bold,
          ),
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
                    color: Colors.grey.shade600,
                  ),
                ),
              ),
              const SizedBox(height: 10),
            ],
          ).ripple(() {
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
            } else {
              showTypeDialogue(
                context,
                url: _edrcData!.updatedDxfFile!,
                title: getLocalizedString(
                  i18.building.UPLOADED_PLAN_DIAG,
                  module: Modules.BPA,
                ),
              );
            }
          }),
          SizedBox(
            height: 10.h,
          ),
          MediumText(
            text: getLocalizedString(
              i18.building.SCRUNITY_REPORT_OUTPUT,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.bold,
          ),
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
                    color: Colors.grey.shade600,
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
            } else {
              showTypeDialogue(
                context,
                url: _edrcData!.planReport!,
                title: getLocalizedString(
                  i18.building.SCRUNITY_REPORT_OUTPUT,
                  module: Modules.BPA,
                ),
              );
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
            text: "N/A",
          ),
          SizedBox(
            height: 10.h,
          ),
          SingleChildScrollView(
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
                ..._edrcData!.planDetail!.blocks!.first.building!.floors!
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
                                  text: item.occupancies?.first.typeHelper?.type
                                          ?.name ??
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
                                      "${item.occupancies?.first.builtUpArea ?? "N/A"}",
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
                                      "${item.occupancies?.first.floorArea ?? "N/A"}",
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
                                      "${item.occupancies?.first.carpetArea ?? "N/A"}",
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
          ),
          SizedBox(
            height: 10.h,
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
                '${_edrcData?.planDetail?.planInformation?.demolitionArea} ${getLocalizedString(i18.building.BPA_SQ_MTRS_LABEL, module: Modules.BPA)}',
          ),
          SizedBox(
            height: 10.h,
          ),
        ],
      ),
    );
  }

  Widget _inspectionQuestionAnswerReportList1() {
    String getFileStoreIdDocs(List<Doc>? docs) {
      if (docs == null) return '';
      List fileIdDocss = [];
      for (var element in docs) {
        fileIdDocss.add(element.fileStoreId);
      }
      return fileIdDocss.join(', ');
    }

    return ListView.builder(
      shrinkWrap: true,
      itemCount: _bpaData?.additionalDetails?.fieldInspectionPending?.length,
      itemBuilder: (context, index) {
        final docs =
            _bpaData?.additionalDetails?.fieldInspectionPending?[index].docs;
        final filesIds = getFileStoreIdDocs(docs);
        return Container(
          color: BaseConfig.mainBackgroundColor,
          padding: const EdgeInsets.all(10),
          child: Column(
            children: [
              MediumText(
                text: "Inspection Report ${index + 1}",
                fontWeight: FontWeight.bold,
              ),
              SizedBox(
                height: 10.h,
              ),
              ColumnHeaderText(
                label: "Inspection Date",
                text: _bpaData?.additionalDetails
                        ?.fieldInspectionPending?[index].date ??
                    "dd-MM-yyyy",
              ),
              SizedBox(
                height: 10.h,
              ),
              ColumnHeaderText(
                label: "Inspection Time",
                text: _bpaData?.additionalDetails
                        ?.fieldInspectionPending?[index].time ??
                    "00:00",
              ),
              SizedBox(
                height: 10.h,
              ),
              ListView.builder(
                shrinkWrap: true,
                itemCount: _bpaData?.additionalDetails
                    ?.fieldInspectionPending?[index].questions?.length,
                itemBuilder: (context, j) {
                  final ques = _bpaData?.additionalDetails
                      ?.fieldInspectionPending?[index].questions![j];
                  return Column(
                    children: [
                      ColumnHeaderText(
                        label: getLocalizedString(
                          ques?.question,
                          module: Modules.BPA,
                        ),
                        text: ques?.value ?? "N/A",
                      ),
                    ],
                  );
                },
              ),
              SizedBox(
                height: 10.h,
              ),
              FutureBuilder(
                future: _fileController.getFiles(
                  tenantId: BaseConfig.STATE_TENANT_ID,
                  token: _authController.token!.accessToken!,
                  fileStoreIds: filesIds,
                ),
                builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    var fileData = snapshot.data!;
                    return _buildReportListDoc(fileData, docs);
                  } else if (snapshot.hasError) {
                    return networkErrorPage(
                      context,
                      () => _fileController.getFiles(
                        tenantId: BaseConfig.STATE_TENANT_ID,
                        token: _authController.token!.accessToken!,
                        fileStoreIds: filesIds,
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
          ).marginOnly(bottom: 10),
        );
      },
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
              .first;
          return Column(
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
                child: MediumText(
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
            text: isNotNullOrEmpty(
              _edrcData?.planDetail?.virtualBuilding?.totalBuitUpArea,
            )
                ? '${_edrcData?.planDetail?.virtualBuilding?.totalBuitUpArea} ${getLocalizedString(i18.building.BPA_SQ_MTRS_LABEL, module: Modules.BPA)}'
                : "N/A",
          ),
          SizedBox(
            height: 10.h,
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
          SizedBox(
            height: 10.h,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.HEIGHT_FROM_GROUND,
              module: Modules.BPA,
            ),
            text: "N/A",
          ),
          SizedBox(
            height: 10.h,
          ),
        ],
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
                case ConnectionState.waiting || ConnectionState.active:
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

  Widget _buildNocCard({
    required NocElement nocElement,
  }) {
    List fileIds = [];
    if (isNotNullOrEmpty(nocElement.documents)) {
      for (var element in nocElement.documents!) {
        fileIds.add(element.fileStoreId);
      }
    }

    final fileStoreIdsNoc = fileIds.join(', ');

    return Container(
      padding: const EdgeInsets.all(10),
      color: BaseConfig.mainBackgroundColor,
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
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              'BPA_${nocElement.nocType}_LABEL',
              module: Modules.BPA,
            ),
            text: isNotNullOrEmpty(nocElement.applicationNo)
                ? "${nocElement.applicationNo}"
                : "N/A",
          ),
          const SizedBox(
            height: 10,
          ),
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
          const SizedBox(
            height: 10,
          ),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.building.NOC_DOCUMENT,
              module: Modules.BPA,
            ),
            text: "",
          ),
          if (isNotNullOrEmpty(fileStoreIdsNoc))
            FutureBuilder(
              future: _fileController.getFiles(
                tenantId: BaseConfig.STATE_TENANT_ID,
                token: _authController.token!.accessToken!,
                fileStoreIds: fileStoreIdsNoc,
              ),
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  var fileData = snapshot.data!;

                  return _buildDocumentsDetailsCard(fileData, nocElement);
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
                      return showCircularIndicator();
                    case ConnectionState.active:
                      return showCircularIndicator();
                    default:
                      return const SizedBox.shrink();
                  }
                }
              },
            )
          else
            const Text("No Documents Uploaded"),
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
          final fileUrl = fileStore.fileStoreIds?[index].url?.split(',').first;
          final docType = nocElement.documents?.firstWhereOrNull(
            (element) =>
                element.fileStoreId == fileStore.fileStoreIds?[index].id,
          );

          final docName = isNotNullOrEmpty(docType?.documentType)
              ? getLocalizedString(
                  docType!.documentType!,
                )
              : 'N/A';

          return isNotNullOrEmpty(docType)
              ? Tooltip(
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
                )
              : const SizedBox.shrink();
        },
      ),
    );
  }

  Widget _feeDetailWidget() {
    return StreamBuilder(
      stream: _bpaController.bpaFeeDetailStream.stream,
      builder: (context, snapshots) {
        if (snapshots.hasData && snapshots.data != null) {
          PaymentModel paymentModel = snapshots.data;

          return Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              MediumText(
                text: getLocalizedString(
                  i18.building.BPA_APPL_FEES_DETAILS,
                  module: Modules.BPA,
                ),
                fontWeight: FontWeight.bold,
              ).marginOnly(left: 10.w),
              SizedBox(height: 10.h),
              for (var payment in paymentModel.payments!) ...[
                ListView.builder(
                  itemCount: payment.paymentDetails!.length,
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemBuilder: (context, index) {
                    PaymentDetail paymentDetail =
                        payment.paymentDetails![index];

                    return Container(
                      padding: EdgeInsets.all(10.w),
                      color: BaseConfig.mainBackgroundColor,
                      child: Column(
                        children: [
                          ListView.builder(
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            itemCount: paymentDetail.bill!.billDetails!.length,
                            itemBuilder: (context, index) {
                              BillDetail billDetail =
                                  paymentDetail.bill!.billDetails![index];
                              billDetail.billAccountDetails!
                                  .sort((a, b) => a.order!.compareTo(b.order!));
                              return Column(
                                children: [
                                  for (var accountDetail
                                      in billDetail.billAccountDetails!) ...[
                                    Column(
                                      children: [
                                        ColumnHeaderText(
                                          label: getLocalizedString(
                                            accountDetail.taxHeadCode,
                                            module: Modules.BPA,
                                          ),
                                          text:
                                              '₹${accountDetail.amount ?? 'N/A'}',
                                        ),
                                        SizedBox(height: 10.h),
                                        ColumnHeaderText(
                                          label: getLocalizedString(
                                            i18.building.BPA_STATUS_LABEL,
                                            module: Modules.BPA,
                                          ),
                                          text: accountDetail.amount ==
                                                  accountDetail.adjustedAmount
                                              ? 'Paid'
                                              : 'Unpaid',
                                          textColor: accountDetail.amount ==
                                                  accountDetail.adjustedAmount
                                              ? BaseConfig.statusGreenColor
                                              : BaseConfig.redColor1,
                                        ),
                                      ],
                                    ),
                                  ],
                                ],
                              );
                            },
                          ),
                          SizedBox(height: 10.h),
                          ColumnHeaderText(
                            label: getLocalizedString(
                              i18.building.BPA_TOT_AMT_PAID,
                              module: Modules.BPA,
                            ),
                            fontWeight: FontWeight.bold,
                            text: '₹${paymentDetail.totalAmountPaid ?? 'N/A'}',
                          ),
                          SizedBox(height: 10.h),
                        ],
                      ),
                    );
                  },
                ),
              ],
            ],
          );
        } else {
          switch (snapshots.connectionState) {
            case ConnectionState.waiting:
              return showCircularIndicator().marginOnly(top: 20.h);
            case ConnectionState.active:
              return showCircularIndicator().marginOnly(top: 20.h);
            default:
              return const SizedBox.shrink();
          }
        }
      },
    );
  }
}
