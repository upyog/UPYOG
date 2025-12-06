import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/inbox_controller.dart';
import 'package:mobile_app/controller/property_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/controller/water_controller.dart';
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/property/property.dart';
import 'package:mobile_app/model/employee/emp_ws_model/emp_ws_model.dart';
import 'package:mobile_app/model/employee/status_map/status_map.dart';
import 'package:mobile_app/routes/routes.dart';
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

class EmpSwDetailsScreen extends StatefulWidget {
  const EmpSwDetailsScreen({super.key});

  @override
  State<EmpSwDetailsScreen> createState() => _EmpSwDetailsScreenState();
}

class _EmpSwDetailsScreenState extends State<EmpSwDetailsScreen> {
  final _authController = Get.find<AuthController>();
  final _propertyController = Get.find<PropertyController>();
  final _fileController = Get.find<FileController>();
  final _timelineController = Get.find<TimelineController>();
  // final _downloadController = Get.find<DownloadController>();
  final _waterController = Get.find<WaterController>();
  // final _paymentController = Get.find<PaymentController>();
  final _inboxController = Get.find<InboxController>();
  final _commonController = Get.find<CommonController>();

  int index = 0;

  WsItem? _item;
  late StatusMap statusMap;

  late Future<Properties?> _propertyFuture;
  Completer<FileStore?> fileStoreFuture = Completer<FileStore?>();

  // Map<String, dynamic> editApplicationMap = {};

  var isLoading = false.obs;
  bool _isTimelineFetch = false;

  @override
  initState() {
    index = Get.arguments?['index'] as int? ?? 0;
    _item = Get.arguments?['item'] as WsItem?;
    statusMap = Get.arguments?['statusMap'] as StatusMap? ?? StatusMap();
    super.initState();
    _init();
    _initModule();
  }

  void _initModule() async {
    await _commonController.fetchLabels(modules: Modules.PT);
  }

  void _init() async {
    isLoading.value = true;
    await _getWater();
    if (!isNotNullOrEmpty(_waterController.sewerage?.sewerageConnections)) {
      isLoading.value = false;
      return;
    }
    await getEstimateFee();
    getFiles();
    await _getProperties();
    await _getTimeline();
    await _getWorkflow();
    isLoading.value = false;
  }

  Future<void> _getWater() async {
    try {
      await _waterController.getWaterConnection(
        token: _authController.token!.accessToken!,
        tenantId: _item!.businessObject!.data!.tenantId!,
        appId: _item!.businessObject!.data!.applicationNo!,
        module: ModulesEmp.SW_SERVICES,
      );
    } catch (error) {
      dPrint('Emp Water Details Error: $error');
    }
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
    if (!isNotNullOrEmpty(
      _waterController.sewerageConnection?.documents,
    )) {
      return '';
    }

    List fileIds = [];
    for (var element in _waterController.sewerageConnection!.documents!) {
      fileIds.add(element.fileStoreId);
    }
    return fileIds.join(', ');
  }

  Future<void> getEstimateFee() async {
    try {
      await _waterController.getEstimateCharge(
        token: _authController.token!.accessToken!,
        tenantId: _item!.businessObject!.data!.tenantId!,
        appNo: _item!.businessObject!.data!.applicationNo!,
        module: Modules.SW,
      );
    } catch (error) {
      dPrint('Emp Water Details Error: $error');
    }
  }

  Future<void> _getTimeline() async {
    try {
      await _timelineController
          .getTimelineHistory(
        token: _authController.token!.accessToken!,
        tenantId: _waterController.sewerageConnection!.tenantId!,
        businessIds: _waterController.sewerageConnection!.applicationNo!,
      )
          .then((_) {
        setState(() {
          _isTimelineFetch = true;
        });
      });
    } catch (error) {
      dPrint('Emp Water Details Timeline Error: $error');
    }
  }

  Future<void> _getProperties() async {
    TenantTenant tenantCity = await getCityTenantEmployee();
    _propertyFuture = _propertyController.getPropertiesByID(
      token: _authController.token!.accessToken!,
      propertyId: _waterController.sewerageConnection!.propertyId!,
      tenantCity: tenantCity.code!,
    );
  }

  Future<void> _getWorkflow() async {
    try {
      await _timelineController.getWorkFlow(
        token: _authController.token!.accessToken!,
        tenantId: _item!.businessObject!.data!.tenantId!,
        workFlow: statusMap.businessService!,
      );
    } catch (e) {
      dPrint('Error in getting workflow: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Navigator.of(context).pop();
        },
        title: getLocalizedString(
          i18.waterSewerage.APPLICATION_DETAILS_HEADER,
          module: Modules.WS,
        ),
      ),
      bottomNavigationBar: Obx(
        () => isLoading.value
            ? const SizedBox.shrink()
            : isNotNullOrEmpty(_waterController.sewerage?.sewerageConnections)
                ? (_item!.businessObject!.data!.applicationStatus! ==
                        InboxStatus.PENDING_FIELD_INSPECTION.name
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
                          itemBuilder: (context) {
                            final actionsList = _timelineController
                                .workflowBusinessServices
                                .businessServices
                                ?.firstOrNull
                                ?.states
                                ?.where((s) => s.uuid == statusMap.statusId)
                                .firstOrNull
                                ?.actions
                                ?.map(
                                  (action) => PopupMenuItem<String>(
                                    value: action.action,
                                    child: SmallTextNotoSans(
                                      text: LocalizeUtils.getTakeActionLocal(
                                        action.action,
                                        workflowCode:
                                            statusMap.businessService!,
                                        module: Modules.WS,
                                      ),
                                      color: BaseConfig.textColor,
                                      fontWeight: FontWeight.w600,
                                      size: o == Orientation.portrait
                                          ? 14.sp
                                          : 8.sp,
                                    ),
                                  ),
                                )
                                .toList();

                            // Add "Edit" as the last item
                            // if (_waterController.checkWaterAdditionalDetails())
                            actionsList?.add(
                              PopupMenuItem<String>(
                                value: 'edit',
                                child: SmallTextNotoSans(
                                  text: getLocalizedString(
                                    '${i18.common.WF_EMPLOYEE}${statusMap.businessService!}_EDIT'
                                        .toUpperCase(),
                                    module: Modules.WS,
                                  ),
                                  color: BaseConfig.textColor,
                                  fontWeight: FontWeight.w600,
                                  size:
                                      o == Orientation.portrait ? 14.sp : 8.sp,
                                ),
                              ),
                            );

                            return actionsList ?? <PopupMenuEntry<String>>[];
                          },
                          onSelected: (value) async {
                            dPrint(value);

                            if (!isNotNullOrEmpty(
                              _waterController.empMdmsResModel.mdmsResEmp,
                            )) {
                              await _waterController.getMdmsWsSw();
                            }

                            if (value == 'edit') {
                              return Get.toNamed(
                                AppRoutes.EMP_WATER_EDIT_APP,
                                arguments: {
                                  'data': _waterController.sewerageConnection,
                                },
                              );
                            }

                            //Check edit application local data
                            final isEditApplicationEmpty =
                                await _waterController
                                    .checkEditWaterDetailsLocalData(value);

                            if (isEditApplicationEmpty) {
                              return snackBar(
                                'Incomplete',
                                'Please fill the Edit Application form',
                                BaseConfig.redColor,
                              );
                            }

                            // Existing action handling logic
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
                                'Incomplete',
                                'Next State is Empty',
                                BaseConfig.redColor,
                              );
                              return;
                            }

                            if (value != BaseAction.reject.name &&
                                value != BaseAction.sendBackToCitizen.name) {
                              await _timelineController.getEmployees(
                                token: _authController.token!.accessToken!,
                                tenantId: _waterController
                                    .sewerageConnection!.tenantId!,
                                uuid: uuid,
                              );
                            }

                            if (!context.mounted) return;

                            _inboxController.actionDialogue(
                              context,
                              workFlowId: statusMap.businessService!,
                              action: value,
                              module: Modules.WS,
                              sectionType: ModulesEmp.SW_SERVICES,
                              tenantId: _waterController
                                  .sewerageConnection!.tenantId!,
                              businessService: BusinessService.SW,
                            );
                          },
                        ),
                      )
                    : const SizedBox.shrink())
                : const SizedBox.shrink(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Obx(
          () => isLoading.value
              ? showCircularIndicator()
              : !isNotNullOrEmpty(
                  _waterController.sewerage?.sewerageConnections,
                )
                  ? const NoApplicationFoundWidget()
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
                                      await _getTimeline();
                                    }
                                    TimelineHistoryApp.buildTimelineDialogue(
                                      context,
                                      tenantId: _waterController
                                          .sewerageConnection!.tenantId!,
                                    );
                                  },
                                  child: MediumText(
                                    text:
                                        getLocalizedString(i18.common.TIMELINE),
                                    color: BaseConfig.redColor1,
                                  ),
                                ),
                              ],
                            ),
                            _buildDetails(),
                            if (isNotNullOrEmpty(
                              _waterController.estimate?.calculation,
                            )) ...[
                              SizedBox(height: 10.h),
                              _buildApplicationChargeBuild(),
                            ],
                            const SizedBox(height: 10),
                            BuildCard(
                              padding: 5,
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  // Property Details
                                  FutureBuilder(
                                    future: _propertyFuture,
                                    builder: (context, snapshot) {
                                      if (!snapshot.hasError &&
                                          snapshot.hasData &&
                                          snapshot
                                              .data!.properties!.isNotEmpty) {
                                        Properties property = snapshot.data!;
                                        return _buildPropertyDetails(property);
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

                                  _buildApplicantDetails(),

                                  const SizedBox(height: 20),
                                  BuildExpansion(
                                    title: getLocalizedString(
                                      i18.waterSewerage.CONNECTION_DETAIL,
                                      module: Modules.WS,
                                    ),
                                    children: [
                                      _buildConnectionDetails(),
                                      const SizedBox(height: 20),
                                    ],
                                  ),

                                  BuildExpansion(
                                    title: getLocalizedString(
                                      i18.waterSewerage.ACTIVATION_DETAILS,
                                      module: Modules.WS,
                                    ),
                                    children: [
                                      _buildActivationDetails(),
                                      const SizedBox(height: 20),
                                    ],
                                  ),

                                  if (_waterController
                                              .sewerageConnection?.documents !=
                                          null &&
                                      _waterController.sewerageConnection!
                                          .documents!.isNotEmpty)
                                    BuildExpansion(
                                      title: getLocalizedString(
                                        i18.waterSewerage.DOCUMENT_DETAILS,
                                        module: Modules.WS,
                                      ),
                                      children: [
                                        FutureBuilder(
                                          future: fileStoreFuture.future,
                                          builder: (context, snapshot) {
                                            if (snapshot.hasData) {
                                              var fileData = snapshot.data!;
                                              return _buildDocumentsDetailsCard(
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

  Widget _buildApplicationChargeBuild() {
    final calculation = _waterController.estimate?.calculation?.firstOrNull;
    return BuildCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          BigTextNotoSans(
            text: getLocalizedString(
              i18.waterSewerage.APPLICATION_CHARGE,
              module: Modules.WS,
            ),
            fontWeight: FontWeight.w600,
            size: 16.sp,
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.waterSewerage.APPLICATION_FEE,
              module: Modules.WS,
            ),
            text: calculation?.fee != null ? '₹ ${calculation!.fee}' : 'N/A',
          ).paddingOnly(left: 7.0),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.waterSewerage.APPLICATION_SERVICE_FEE,
              module: Modules.WS,
            ),
            text: isNotNullOrEmpty(calculation?.charge)
                ? '₹ ${calculation!.charge}'
                : 'N/A',
          ).paddingOnly(left: 7.0),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.common.EMP_WS_TAX,
            ),
            text: calculation?.taxAmount != null
                ? '₹ ${calculation!.taxAmount}'
                : 'N/A',
          ).paddingOnly(left: 7.0),
          SizedBox(height: 10.h),
          const Divider(),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.waterSewerage.WS_TOTAL_AMT,
              module: Modules.WS,
            ),
            text: calculation?.totalAmount != null
                ? '₹ ${calculation!.totalAmount}'
                : 'N/A',
          ).paddingOnly(left: 7.0),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.common.EMP_INBOX_STATUS,
            ),
            text: getLocalizedString(
              '${i18.waterSewerage.WS_COMMON}NOT_PAID',
              module: Modules.WS,
            ),
            textColor: BaseConfig.appThemeColor1,
          ).paddingOnly(left: 7.0),
        ],
      ),
    );
  }

  Widget _buildConnectionDetails() => Obx(() {
        final connection = _waterController.editSewerageConnection.value;
        final isRoadCuttingLengthBigger =
            (connection?.roadCuttingInfo?.length ?? 1) > 1;
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.EMP_NATURE_CONNECTION,
                module: Modules.WS,
              ),
              text: connection?.connectionType ?? 'N/A',
            ).paddingOnly(left: 7.0),
            SizedBox(height: 10.h),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.WS_NUMBER_WATER_CLOSETS_LABEL,
                module: Modules.WS,
              ),
              text: '${connection?.noOfWaterClosets ?? 'N/A'}',
            ).paddingOnly(left: 7.0),
            SizedBox(height: 10.h),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.SERV_DETAIL_NO_OF_TOILETS,
                module: Modules.WS,
              ),
              text: connection?.noOfToilets.toString() ?? 'N/A',
            ).paddingOnly(left: 7.0),
            SizedBox(height: 10.h),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.waterSewerage.PLUMBER_DETAILS,
                module: Modules.WS,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            SizedBox(height: 10.h),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.PLUMBER_PROVIDED_BY,
                module: Modules.WS,
              ),
              text: connection?.additionalDetails?.detailsProvidedBy ?? 'N/A',
            ).paddingOnly(left: 7.0),
            SizedBox(height: 10.h),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.waterSewerage.ROAD_CUTTING_DETAILS,
                module: Modules.WS,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            for (int i = 0; i < (connection?.roadCuttingInfo?.length ?? 1); i++)
              Container(
                margin: isRoadCuttingLengthBigger
                    ? EdgeInsets.only(top: 4.h, bottom: 8.h)
                    : EdgeInsets.zero,
                decoration: isRoadCuttingLengthBigger
                    ? BoxDecoration(
                        borderRadius: BorderRadius.circular(10.r),
                        color: BaseConfig.borderColor.withValues(alpha: 0.3),
                        border: Border.all(
                          color: BaseConfig.borderColor,
                          width: 1,
                        ),
                      )
                    : null,
                child: Padding(
                  padding: isRoadCuttingLengthBigger
                      ? EdgeInsets.all(4.w)
                      : EdgeInsets.zero,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const SizedBox(height: 10),
                      ColumnHeaderText(
                        label: getLocalizedString(
                          i18.waterSewerage.ADDN_DETAIL_ROAD_TYPE,
                          module: Modules.WS,
                        ),
                        text: isNotNullOrEmpty(
                          connection?.roadCuttingInfo?[i].roadType,
                        )
                            ? getLocalizedString(
                                '${i18.waterSewerage.WS_ROADTYPE}${connection?.roadCuttingInfo?[i].roadType}',
                                module: Modules.WS,
                              )
                            : 'N/A',
                      ).paddingOnly(left: 7.0),
                      SizedBox(height: 10.h),
                      ColumnHeaderText(
                        label: getLocalizedString(
                          i18.waterSewerage.ROAD_CUTTING_AREA_LABEL,
                          module: Modules.WS,
                        ),
                        text: connection?.roadCuttingInfo?[i].roadCuttingArea
                                .toString() ??
                            'N/A',
                      ).paddingOnly(left: 7.0),
                      SizedBox(height: 10.h),
                    ],
                  ),
                ),
              ),
            SizedBox(height: 10.h),
          ],
        );
      });

  Widget _buildPropertyDetails(Properties property) {
    final ownerList = property.properties?.first.owners?.reversed.toList();
    final addressParts = [
      property.properties?.first.address?.doorNo,
      property.properties?.first.address?.street,
      property.properties?.first.address?.locality?.name,
      property.properties?.first.address?.city,
      property.properties?.first.address?.pinCode,
    ];

    final filteredAddressParts =
        addressParts.where((part) => part != null && part.isNotEmpty).toList();
    final finalAddress = filteredAddressParts.join(', ');
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.waterSewerage.PROPERTY_DETAILS,
                module: Modules.WS,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(i18.tlProperty.ID),
              text: _waterController.sewerageConnection?.propertyId ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.CONSUMER_NAME_LABEL,
                module: Modules.WS,
              ),
              text: ownerList?.first.name ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.PROPERTY_ADDRESS,
                module: Modules.WS,
              ),
              text: finalAddress,
            ),
            const SizedBox(height: 10),
          ],
        ).paddingOnly(top: 10.0, left: 10.0, right: 10.0, bottom: 0),
        Align(
          alignment: Alignment.centerLeft,
          child: TextButton(
            onPressed: () async {
              final TenantTenant tenant = await getCityTenantEmployee();
              //TODO: Details page
              Get.toNamed(
                AppRoutes.WATER_PROPERTY_INFO,
                arguments: {
                  'property': property,
                  'tenant': tenant,
                },
              );
            },
            child: Row(
              children: [
                MediumText(
                  text: getLocalizedString(
                    i18.waterSewerage.VIEW_PROPERTY,
                    module: Modules.WS,
                  ),
                  color: Colors.red,
                ),
                const SizedBox(width: 10),
                const Icon(
                  Icons.arrow_right_alt_outlined,
                  color: Colors.red,
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildActivationDetails() {
    return Padding(
      padding: const EdgeInsets.all(10.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ColumnHeaderText(
            label: getLocalizedString(
              i18.waterSewerage.DETAIL_CONN_EXECUTION_DATE,
              module: Modules.WS,
            ),
            text: isNotNullOrEmpty(
              _waterController.sewerageConnection?.connectionExecutionDate,
            )
                ? _waterController.waterConnection!.connectionExecutionDate
                    .toCustomDateFormat(pattern: 'd/MM/yyyy')!
                : 'N/A',
          ).paddingOnly(left: 7.0),
          SizedBox(height: 10.h),
        ],
      ),
    );
  }

  Widget _buildApplicantDetails() => Padding(
        padding: const EdgeInsets.fromLTRB(10.0, 10.0, 10.0, 0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.waterSewerage.WS_CONN_HOLDER_SAME_AS_OWNER_DETAILS,
                module: Modules.WS,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            SizedBox(height: 10.h),
            MediumText(
              text: getLocalizedString(
                'SCORE_YES',
              ),
              fontWeight: FontWeight.w600,
              color: Colors.grey,
            ),
          ],
        ),
      );

  Widget _buildDocumentsDetailsCard(FileStore fileStore) {
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
          final fileUrl =
              fileStore.fileStoreIds?[index].url?.split(',').firstOrNull;
          final docType =
              _waterController.sewerageConnection?.documents?.firstWhereOrNull(
            (element) =>
                element.fileStoreId == fileStore.fileStoreIds?[index].id,
          );
          return isNotNullOrEmpty(docType)
              ? Tooltip(
                  message: getLocalizedString(
                    docType?.documentType,
                    module: Modules.WS,
                  ),
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
                        text: getLocalizedString(
                          docType?.documentType,
                          module: Modules.WS,
                        ),
                        color: Colors.grey.shade600,
                        maxLine: 2,
                        textOverflow: TextOverflow.ellipsis,
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
                        title: getLocalizedString(
                          docType?.documentType,
                          module: Modules.WS,
                        ),
                      );
                    } else {
                      showTypeDialogue(
                        context,
                        url: fileUrl,
                        title: getLocalizedString(
                          docType?.documentType,
                          module: Modules.WS,
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

  Widget _buildDetails() {
    return BuildCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ColumnHeaderText(
            label: getLocalizedString(
              i18.waterSewerage.APPLICATION_NO,
              module: Modules.WS,
            ),
            text: _waterController.sewerageConnection?.applicationNo ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.waterSewerage.SERVICE_NAME_LABEL,
              module: Modules.WS,
            ),
            text: getLocalizedString(
              '${i18.waterSewerage.WS_APPLICATION_TYPE_}${_waterController.sewerageConnection?.applicationType}',
              module: Modules.WS,
            ),
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.waterSewerage.WS_NO_WATER_CLOSETS_LABEL,
              module: Modules.WS,
            ),
            text:
                '${_waterController.sewerageConnection?.proposedWaterClosets ?? 'N/A'}',
          ),
          SizedBox(height: 10.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.waterSewerage.WS_TOILETS_LABEL,
              module: Modules.WS,
            ),
            text:
                '${_waterController.sewerageConnection?.proposedToilets ?? 'N/A'}',
          ),
          // const SizedBox(height: 10),
          // _buildPrice(),
        ],
      ),
    );
  }

  // Widget _buildFeeDetails() {
  //   return BuildCard(
  //     child: Column(
  //       crossAxisAlignment: CrossAxisAlignment.start,
  //       children: [
  //         BigTextNotoSans(
  //           text: getLocalizedString(
  //             i18.waterSewerage.FEE_DETAILS_HEADER,
  //             module: Modules.WS,
  //           ),
  //           fontWeight: FontWeight.w600,
  //           size: 16.sp,
  //         ),
  //         const SizedBox(height: 10),
  //         if (_paymentController.billInfo?.bill?.first.billDetails?.first
  //                 .billAccountDetails !=
  //             null) ...[
  //           for (int i = 0;
  //               i <
  //                   _paymentController.billInfo!.bill!.first.billDetails!.first
  //                       .billAccountDetails!.length;
  //               i++)
  //             ColumnHeaderText(
  //               label: getLocalizedString(
  //                 _paymentController.billInfo!.bill!.first.billDetails!.first
  //                     .billAccountDetails![i].taxHeadCode!
  //                     .toUpperCase(),
  //                 module: Modules.WS,
  //               ),
  //               text: _paymentController.billInfo!.bill!.first.billDetails!
  //                           .first.billAccountDetails![i].amount !=
  //                       null
  //                   ? '₹${_paymentController.billInfo!.bill!.first.billDetails!.first.billAccountDetails![i].amount.doubleToString()}'
  //                   : 'N/A',
  //             ).marginOnly(bottom: 10),
  //         ],
  //         ColumnHeaderText(
  //           label: getLocalizedString(
  //             i18.waterSewerage.TOTAL_AMOUNT_DUE,
  //             module: Modules.WS,
  //           ),
  //           text: _paymentController.billInfo?.bill?.first.totalAmount == null
  //               ? '₹0'
  //               : '₹${_paymentController.billInfo!.bill!.first.totalAmount}',
  //           fontWeight: FontWeight.w900,
  //           textSize: 16.0,
  //         ),
  //         const SizedBox(height: 10),
  //         ColumnHeaderText(
  //           label: getLocalizedString(
  //             i18.waterSewerage.APPLICATION_STATUS,
  //             module: Modules.WS,
  //           ),
  //           text: _paymentController.billInfo?.bill?.first.status ?? 'N/A',
  //           textColor: Colors.red,
  //         ),
  //       ],
  //     ),
  //   );
  // }
}
