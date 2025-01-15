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
import 'package:mobile_app/controller/trade_license_controller.dart';
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/property/property.dart';
import 'package:mobile_app/model/employee/emp_tl_model/emp_tl_model.dart' as tl;
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

class EmpTlDetailsScreen extends StatefulWidget {
  const EmpTlDetailsScreen({super.key});

  @override
  State<EmpTlDetailsScreen> createState() => _EmpTlDetailsScreenState();
}

class _EmpTlDetailsScreenState extends State<EmpTlDetailsScreen> {
  final _tlController = Get.find<TradeLicenseController>();
  final _authController = Get.find<AuthController>();
  final _propertyController = Get.find<PropertyController>();
  final _fileController = Get.find<FileController>();
  final _timelineController = Get.find<TimelineController>();
  // final _downloadController = Get.find<DownloadController>();
  final _commonController = Get.find<CommonController>();
  final _inboxController = Get.find<InboxController>();

  final pageController = PageController(viewportFraction: 1.05, keepPage: true);

  Properties? _property;
  Completer<FileStore?> fileStoreFuture = Completer<FileStore?>();

  tl.Item? _item;
  int index = 0;
  late StatusMap statusMap;

  var isLoading = false.obs;
  bool _isTimelineFetch = false;

  @override
  void initState() {
    super.initState();
    _item = Get.arguments?['item'] as tl.Item?;
    index = Get.arguments?['index'] as int? ?? 0;
    statusMap = Get.arguments?['statusMap'] as StatusMap? ?? StatusMap();
    _tlController.isMore.value = false;
    _init();
  }

  void _init() async {
    isLoading.value = true;
    _initModule();
    await _getTlApplication();
    if (!isNotNullOrEmpty(_tlController.tradeLicense.licenses)) {
      isLoading.value = false;
      return;
    }
    await _getProperties();
    await _getTimeline();
    await _getWorkflow();
    getFilesStore();
    isLoading.value = false;
  }

  void _initModule() async {
    await _commonController.fetchLabels(modules: Modules.PT);
  }

  Future<void> _getTlApplication() async {
    try {
      await _tlController.getTlApplications(
        token: _authController.token!.accessToken!,
        applicationId: _item!.businessObject!.applicationNumber!,
        tenantId: _item!.processInstance!.tenantId!,
      );
    } catch (e) {
      dPrint('Error in getting trade license application: $e');
    }
  }

  String getFileStoreIds() {
    if (!isNotNullOrEmpty(
      _item?.businessObject?.tradeLicenseDetail?.applicationDocuments,
    )) {
      return '';
    }

    List fileIds = [];
    for (var element
        in _item!.businessObject!.tradeLicenseDetail!.applicationDocuments!) {
      fileIds.add(element.fileStoreId);
    }
    return fileIds.join(', ');
  }

  void getFilesStore() {
    try {
      fileStoreFuture.complete(
        _fileController.getFiles(
          tenantId: BaseConfig.STATE_TENANT_ID,
          token: _authController.token!.accessToken!,
          fileStoreIds: getFileStoreIds(),
        ),
      );
    } catch (e) {
      dPrint('EMP_TL_GetFilesStore_Error: $e');
    }
  }

  Future<void> _getTimeline() async {
    try {
      await _timelineController
          .getTimelineHistory(
        token: _authController.token!.accessToken!,
        tenantId: _item!.processInstance!.tenantId!,
        businessIds: _item!.processInstance!.businessId!,
      )
          .then((_) {
        setState(() {
          _isTimelineFetch = true;
        });
      });
    } catch (e) {
      dPrint('Error in getting timeline: $e');
    }
  }

  Future<void> _getProperties() async {
    try {
      TenantTenant tenantCity = await getCityTenantEmployee();
      _property = await _propertyController.getPropertiesByID(
        token: _authController.token!.accessToken!,
        propertyId: _item!
            .businessObject!.tradeLicenseDetail!.additionalDetail!.propertyId!,
        tenantCity: tenantCity.code!,
      );
    } catch (e) {
      dPrint('Error in getting properties: $e');
    }
  }

  Future<void> _getWorkflow() async {
    try {
      await _getTimeline();
      await _timelineController.getWorkFlow(
        token: _authController.token!.accessToken!,
        tenantId: _item!.processInstance!.tenantId!,
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
          i18.tradeLicense.APPLICATION_DETAILS,
          module: Modules.TL,
        ),
      ),
      bottomNavigationBar: Obx(
        () => isLoading.value
            ? const SizedBox.shrink()
            : isNotNullOrEmpty(_tlController.tradeLicense.licenses)
                ? (_item!.businessObject!.status ==
                            InboxStatus.FIELD_INSPECTION.name ||
                        _item!.businessObject!.status ==
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
                                      module: Modules.TL,
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
                                value != BaseAction.sendBackToCitizen.name) {
                              await _timelineController.getEmployees(
                                token: _authController.token!.accessToken!,
                                tenantId: _item!.processInstance!.tenantId!,
                                uuid: uuid,
                              );
                            }

                            if (!context.mounted) return;

                            _inboxController.actionDialogue(
                              context,
                              workFlowId: statusMap.businessService!,
                              action: value,
                              module: Modules.TL,
                              sectionType: ModulesEmp.TL_SERVICES,
                              tenantId: _item!.processInstance!.tenantId!,
                              businessService: BusinessService.TL,
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
              : !isNotNullOrEmpty(_tlController.tradeLicense.licenses)
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
                                      tenantId:
                                          _item!.processInstance!.tenantId!,
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
                            _buildDetails(o, _item!),
                            const SizedBox(height: 10),
                            BuildCard(
                              padding: 5,
                              child: Column(
                                children: [
                                  if (_item?.businessObject?.tradeLicenseDetail
                                          ?.tradeUnits !=
                                      null)
                                    BuildExpansion(
                                      title: getLocalizedString(
                                        i18.tradeLicense.TRADE_UNITS,
                                        module: Modules.TL,
                                      ),
                                      children: [
                                        _buildTradeUnits(_item!, index, o: o),
                                        const SizedBox(height: 20),
                                      ],
                                    ),
                                  if (isNotNullOrEmpty(
                                    _item?.businessObject?.tradeLicenseDetail
                                        ?.additionalDetail?.propertyId,
                                  ))
                                    BuildExpansion(
                                      title: getLocalizedString(
                                        i18.common.EMP_PROPERTY_DETAILS,
                                      ),
                                      children: [
                                        _buildPropertyDetails(
                                          _item!,
                                          index,
                                          o: o,
                                        ),
                                        const SizedBox(height: 10),
                                      ],
                                    ),
                                  if (isNotNullOrEmpty(
                                    _item?.businessObject?.tradeLicenseDetail
                                        ?.applicationDocuments,
                                  ))
                                    BuildExpansion(
                                      title: getLocalizedString(
                                        i18.common.EMP_COMMON_DOCS,
                                      ),
                                      children: [
                                        SizedBox(height: 8.h),
                                        FutureBuilder(
                                          future: fileStoreFuture.future,
                                          builder: (context, snapshot) {
                                            if (snapshot.hasData) {
                                              var fileData = snapshot.data!;
                                              return _buildEvidenceCard(
                                                context,
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

  Widget _buildEvidenceCard(BuildContext context, FileStore fileStore) {
    final fileController = Get.find<FileController>();
    return Padding(
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
              fileStore.fileStoreIds![index].url!.split(',').firstOrNull;
          final docType = _item
              ?.businessObject?.tradeLicenseDetail?.applicationDocuments
              ?.firstWhereOrNull(
            (element) =>
                element.fileStoreId == fileStore.fileStoreIds?[index].id,
          );
          return isNotNullOrEmpty(docType)
              ? Column(
                  children: [
                    Container(
                      width: Get.width,
                      decoration: BoxDecoration(
                        color: BaseConfig.greyColor2,
                        // border: Border.all(color: Colors.grey),
                        borderRadius: BorderRadius.circular(10.r),
                      ),
                      child: Padding(
                        padding: EdgeInsets.all(8.w),
                        child: Icon(
                          fileController.getFileType(fileUrl!).$1,
                          size: 40,
                          color: Colors.grey.shade600,
                        ),
                      ),
                    ),
                    const SizedBox(height: 10),
                    Tooltip(
                      message: getLocalizedString(
                        docType!.documentType,
                        module: Modules.TL,
                      ),
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
                  ],
                ).ripple(() {
                  final fileType = fileController.getFileType(fileUrl).$2;
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
                })
              : const SizedBox.shrink();
        },
      ),
    );
  }

  Widget _buildPropertyDetails(
    tl.Item item,
    int index, {
    required Orientation o,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        ColumnHeaderText(
          label: getLocalizedString(
            i18.common.PROPERTY_ID,
          ),
          text: item.businessObject?.tradeLicenseDetail?.additionalDetail
                  ?.propertyId ??
              'N/A',
        ),
        SizedBox(height: 8.h),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.common.OWNER_NAME,
          ),
          text: item.businessObject?.tradeLicenseDetail?.owners?.first.name ??
              'N/A',
        ),
        SizedBox(height: 8.h),
        _buildAddress(o, item),
        TextButton(
          onPressed: () async {
            await _commonController.fetchLabels(modules: Modules.PT);
            Get.toNamed(
              AppRoutes.EMP_PROPERTY_INFO,
              arguments: {
                "properties": _property,
                'item': item,
              },
            );
          },
          child: Row(
            children: [
              SmallTextNotoSans(
                text: '${getLocalizedString(
                  i18.tlProperty.VIEW_DETAILS_BTN,
                )}: ',
                fontWeight: FontWeight.w400,
                size: o == Orientation.portrait ? 12.sp : 6.sp,
                color: BaseConfig.appThemeColor1,
              ),
              SizedBox(width: 10.w),
              const Icon(
                Icons.arrow_right_alt_outlined,
                color: BaseConfig.appThemeColor1,
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildAddress(Orientation o, tl.Item item) {
    return Wrap(
      children: [
        SmallTextNotoSans(
          text: '${getLocalizedString(
            i18.tlProperty.ADDRESS,
          )}: ',
          fontWeight: FontWeight.w600,
          size: o == Orientation.portrait ? 12.sp : 6.sp,
        ),
        Wrap(
          children: [
            if (isNotNullOrEmpty(
              item.businessObject?.tradeLicenseDetail?.address?.doorNo,
            )) ...[
              SmallTextNotoSans(
                text: item.businessObject!.tradeLicenseDetail!.address!.doorNo!,
                maxLine: 4,
                size: o == Orientation.portrait ? 12.sp : 6.sp,
              ),
              const SmallTextNotoSans(text: ', '),
            ],
            if (isNotNullOrEmpty(
              item.businessObject?.tradeLicenseDetail?.address?.street,
            )) ...[
              SmallTextNotoSans(
                text: item.businessObject!.tradeLicenseDetail!.address!.street!,
                maxLine: 4,
                size: o == Orientation.portrait ? 12.sp : 6.sp,
              ),
              const SmallTextNotoSans(text: ', '),
            ],
            if (isNotNullOrEmpty(
              item.businessObject?.tradeLicenseDetail?.address?.landmark,
            )) ...[
              SmallTextNotoSans(
                text:
                    item.businessObject!.tradeLicenseDetail!.address!.landmark!,
                maxLine: 4,
                size: o == Orientation.portrait ? 12.sp : 6.sp,
              ),
              const SmallTextNotoSans(text: ', '),
            ],
            if (isNotNullOrEmpty(
              item.businessObject?.tradeLicenseDetail?.address?.locality?.name,
            )) ...[
              SmallTextNotoSans(
                text: item.businessObject!.tradeLicenseDetail!.address!
                    .locality!.name!,
                maxLine: 4,
                size: o == Orientation.portrait ? 12.sp : 6.sp,
              ),
              const SmallTextNotoSans(text: ', '),
            ],
            if (isNotNullOrEmpty(
              item.businessObject?.tradeLicenseDetail?.address?.city,
            )) ...[
              SmallTextNotoSans(
                text: item.businessObject!.tradeLicenseDetail!.address!.city!,
                maxLine: 4,
                size: o == Orientation.portrait ? 12.sp : 6.sp,
              ),
            ],
          ],
        ),
      ],
    );
  }

  Widget _buildTradeUnits(tl.Item item, int index, {Orientation? o}) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        ListView.builder(
          shrinkWrap: true,
          itemCount:
              item.businessObject?.tradeLicenseDetail?.tradeUnits?.length,
          physics: AppPlatforms.platformPhysics(),
          itemBuilder: (context, index) {
            final tradeUnit =
                item.businessObject?.tradeLicenseDetail?.tradeUnits?[index];
            final isActive = tradeUnit?.active ?? false;
            return isActive
                ? _buildTradeUnitCard(
                    tradeUnit!,
                    index + 1,
                  )
                : const SizedBox.shrink();
          },
        ),
        SizedBox(height: 8.h),
      ],
    );
  }

  Widget _buildTradeUnitCard(tl.TradeUnit tradeUnit, index) {
    final tradeType = tradeUnit.tradeType;

    final tlCategory = tradeType?.split('.').firstOrNull ?? '';
    final tlType = tradeType != null && tradeType.contains('.')
        ? tradeType.split('.')[1]
        : '';
    final tlSubType =
        tradeType?.replaceAll('.', '_').replaceAll('-', '_') ?? '';

    dPrint('''
    TL Category: $tlCategory
    TL Type: $tlType
    TL SubType: $tlSubType
    ''');
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
                '${i18.tradeLicense.TRADE_TYPE_RES}$tlCategory',
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
                '${i18.tradeLicense.TRADE_TYPE_RES}$tlType',
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
                '${i18.tradeLicense.TRADE_TYPE_RES}$tlSubType',
                module: Modules.TL,
              ),
            ),
            const SizedBox(height: 10),
          ],
        ),
      ),
    );
  }

  Widget _buildDetails(Orientation o, tl.Item item) {
    return BuildCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ColumnHeaderText(
            label:
                '${getLocalizedString(i18.tradeLicense.EMP_APPLICATION_NO)}: ',
            text: item.processInstance?.businessId ?? 'N/A',
          ),
          SizedBox(height: 8.h),
          ColumnHeaderText(
            label:
                '${getLocalizedString(i18.tradeLicense.EMP_APPLICATION_DATE, module: Modules.TL)}: ',
            text: item.businessObject?.applicationDate?.toCustomDateFormat() ??
                'N/A',
          ),
          SizedBox(height: 8.h),
          MediumTextNotoSans(
            text: getLocalizedString(i18.common.EMP_TR_DETAILS),
            fontWeight: FontWeight.w600,
            size: o == Orientation.portrait ? 14.sp : 7.sp,
          ),
          SizedBox(height: 8.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.EMP_APPLICATION_CHALLAN,
              module: Modules.TL,
            ),
            text: getLocalizedString(
              'TL_CHANNEL_${item.businessObject?.tradeLicenseDetail?.channel}',
              module: Modules.TL,
            ),
          ),
          SizedBox(height: 8.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.EMP_FINANCIAL_YEAR,
              module: Modules.TL,
            ),
            text: isNotNullOrEmpty(item.businessObject?.financialYear)
                ? 'FY${item.businessObject?.financialYear}'
                : 'N/A',
          ),
          SizedBox(height: 8.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.EMP_LIC_TYPE,
              module: Modules.TL,
            ),
            text: getLocalizedString(
              '${i18.tradeLicense.TRADE_LICENSE_TYPE_RES}${item.businessObject?.licenseType}',
              module: Modules.TL,
            ),
          ),
          SizedBox(height: 8.h),
          ColumnHeaderText(
            label: getLocalizedString(i18.common.EMP_TRD_NAME),
            text: item.businessObject?.tradeName ?? 'N/A',
          ),
          SizedBox(height: 8.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.EMP_STRUCT_TYPE,
              module: Modules.TL,
            ),
            text: isNotNullOrEmpty(
              item.businessObject?.tradeLicenseDetail?.structureType,
            )
                ? item.businessObject!.tradeLicenseDetail!.structureType!
                    .split('.')
                    .first
                : 'N/A',
          ),
          SizedBox(height: 8.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.EMP_STRUCT_SUB_TYPE,
              module: Modules.TL,
            ),
            text: isNotNullOrEmpty(
              item.businessObject?.tradeLicenseDetail?.structureType,
            )
                ? item.businessObject!.tradeLicenseDetail!.structureType!
                    .split('.')
                    .last
                : 'N/A',
          ),
          SizedBox(height: 8.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.EMP_TRADE_COMM_DATE,
              module: Modules.TL,
            ),
            text: item.businessObject?.applicationDate
                    ?.toCustomDateFormat(pattern: 'd/MM/yyyy') ??
                'N/A',
          ),
          SizedBox(height: 8.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.EMP_GST_NUMBER,
              module: Modules.TL,
            ),
            text: isNotNullOrEmpty(
              item.businessObject?.tradeLicenseDetail?.additionalDetail
                  ?.tradeGstNo,
            )
                ? item.businessObject!.tradeLicenseDetail!.additionalDetail!
                    .tradeGstNo!
                : 'N/A',
          ),
          SizedBox(height: 8.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.EMP_OPERATIONAL_SQ_FT,
              module: Modules.TL,
            ),
            text: isNotNullOrEmpty(
              item.businessObject?.tradeLicenseDetail?.operationalArea,
            )
                ? item.businessObject!.tradeLicenseDetail!.operationalArea!
                    .toString()
                : 'N/A',
          ),
          SizedBox(height: 8.h),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tradeLicense.EMP_NUMBER_OF_EMP,
              module: Modules.TL,
            ),
            text: isNotNullOrEmpty(
              item.businessObject?.tradeLicenseDetail?.noOfEmployees,
            )
                ? item.businessObject!.tradeLicenseDetail!.noOfEmployees!
                    .toString()
                : 'N/A',
          ),
          SizedBox(height: 8.h),
        ],
      ),
    );
  }
}
