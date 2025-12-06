import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/filter/assigned_filter.dart';
import 'package:mobile_app/components/filter/filter_bottomsheet.dart';
import 'package:mobile_app/components/locality_widget/locality_selection_widget.dart';
import 'package:mobile_app/components/text_formfield_normal.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/locality_controller.dart';
import 'package:mobile_app/controller/trade_license_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/employee/emp_tl_model/emp_tl_model.dart' as tl;
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class EmpTradeLicense extends StatefulWidget {
  const EmpTradeLicense({super.key});

  @override
  State<EmpTradeLicense> createState() => _EmpTradeLicenseState();
}

class _EmpTradeLicenseState extends State<EmpTradeLicense> {
  final _authController = Get.find<AuthController>();
  final _commonController = Get.find<CommonController>();

  final _tlController = Get.find<TradeLicenseController>();
  // final _fileController = Get.find<FileController>();
  final _localityController = Get.put(LocalityController());

  final searchController = TextEditingController();

  final pageController = PageController(viewportFraction: 1.05, keepPage: true);
  var isSelected = false;
  late TenantTenant tenantCity;
  var isLoading = false.obs, moduleLoaded = false.obs;

  bool get isValidToken => _authController.token?.accessToken != null;

  @override
  void initState() {
    isLoading.value = true;
    _tlController.setDefaultLimit();
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _fetchInbox();
    });
    isLoading.value = false;
  }

  Future<void> _fetchInbox() async {
    await _commonController.fetchLabels(modules: Modules.TL).then((_) {
      moduleLoaded.value = true;
    });
    tenantCity = await getCityTenantEmployee();
    await _tlController.getEmpTlInboxApplications(
      token: _authController.token!.accessToken!,
      tenantId: '${tenantCity.code}',
    );
    await _localityController.fetchLocality();
    isLoading.value = false;
  }

  // Apply search filters
  Future<void> applyFilter() async {
    await _tlController.getEmpTlInboxApplications(
      token: _authController.token!.accessToken!,
      tenantId: '${tenantCity.code}',
      isFilter: _localityController.selectedLocalityList.isEmpty ? false : true,
      locality: _localityController.selectedLocalityList.isEmpty
          ? null
          : _localityController.getSelectedLocalityCode(),
      assigneeUid: _localityController.assigneeUid.value.isEmpty
          ? null
          : _localityController.assigneeUid.value,
    );
    dPrint('Apply filter closed!!!');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Obx(
          () => moduleLoaded.value
              ? Wrap(
                  children: [
                    Text(getLocalizedString(i18.common.TRADE_LICENSE)),
                    Text(' (${_tlController.empTlCount.value})'),
                  ],
                )
              : const SizedBox.shrink(),
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: OrientationBuilder(
        builder: (context, o) {
          return RefreshIndicator(
            color: BaseConfig.appThemeColor1,
            onRefresh: _fetchInbox,
            child: SizedBox(
              height: Get.height,
              width: Get.width,
              child: Obx(
                () => isLoading.value && !moduleLoaded.value
                    ? showCircularIndicator()
                    : SingleChildScrollView(
                        child: Column(
                          children: [
                            Row(
                              children: [
                                Expanded(
                                  flex: o == Orientation.portrait ? 6 : 7,
                                  child: textFormFieldNormal(
                                    context,
                                    'Search',
                                    controller: searchController,
                                    hintTextColor: BaseConfig.filterIconColor,
                                    prefixIcon: const Icon(
                                      Icons.search,
                                      color: BaseConfig.filterIconColor,
                                    ),
                                    textInputAction: TextInputAction.search,
                                    keyboardType: TextInputType.text,
                                  ),
                                ),
                                SizedBox(width: 8.w),
                                Expanded(
                                  flex: 1,
                                  child: IconButton(
                                    style: IconButton.styleFrom(
                                      shape: RoundedRectangleBorder(
                                        borderRadius:
                                            BorderRadius.circular(12.r),
                                      ),
                                    ),
                                    onPressed: () {
                                      _openFilterBottomSheet(isSelected, o);
                                    },
                                    icon: SvgPicture.asset(
                                      BaseConfig.filterIconSvg,
                                      colorFilter: const ColorFilter.mode(
                                        BaseConfig.filterIconColor,
                                        BlendMode.srcIn,
                                      ),
                                      height: o == Orientation.portrait
                                          ? 24.h
                                          : 34.h,
                                      width: o == Orientation.portrait
                                          ? 24.w
                                          : 34.w,
                                    ),
                                  ),
                                ),
                              ],
                            ),
                            SizedBox(height: 16.h),
                            _buildBody(o),
                          ],
                        ).paddingSymmetric(horizontal: 16.w, vertical: 20.h),
                      ),
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildBody(Orientation o) {
    return StreamBuilder(
      stream: _tlController.empTlInboxCtrl.stream,
      builder: (context, AsyncSnapshot snapshot) {
        if (snapshot.hasData) {
          if (snapshot.data is String || snapshot.data == null) {
            return Center(
              child: Text(getLocalizedString(i18.inbox.NO_APPLICATION)),
            );
          }

          final tl.EmpTlModel tlInboxes = snapshot.data;

          if (snapshot.data == null || tlInboxes.items!.isEmpty) {
            return Center(
              child: Text(getLocalizedString(i18.inbox.NO_APPLICATION)),
            );
          }

          final tlInboxesFiltered = tlInboxes.items!
              .where(
                (item) =>
                    item.processInstance?.state?.applicationStatus ==
                    InboxStatus.FIELD_INSPECTION.name,
              )
              .toList();

          return ListView.builder(
            itemCount: tlInboxesFiltered.length + 1,
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemBuilder: (context, index) {
              if (index == tlInboxesFiltered.length) {
                return tlInboxesFiltered.isNotEmpty
                    ? Obx(
                        () => FilledButtonApp(
                          text: 'Load more',
                          isLoading: _tlController.isLoading.value,
                          circularColor: BaseConfig.fillAppBtnCircularColor,
                          onPressed: () {
                            _tlController.loadMore(
                              token: _authController.token!.accessToken!,
                              tenantId: tenantCity.code!,
                            );
                          },
                        ),
                      )
                    : Center(
                        child: SmallTextNotoSans(
                          text: getLocalizedString(i18.inbox.NO_APPLICATION),
                          color: BaseConfig.greyColor3,
                        ),
                      );
              }

              final item = tlInboxesFiltered[index];
              final processInstance = item.processInstance;
              final businessObject = item.businessObject;
              final serviceCode = businessObject?.workflowCode ??
                  processInstance?.businessService ??
                  '';

              final appStatus = processInstance?.state?.applicationStatus ??
                  businessObject?.status;

              final statusMap = getFilteredStatusMapEmp(
                tlInboxes.statusMap,
                serviceCode,
                applicationStatus: appStatus,
              );

              final locality = getLocalizedString(
                getLocality(
                  tenantCity,
                  businessObject?.tradeLicenseDetail?.address?.locality?.code ??
                      '',
                ),
              );

              return processInstance != null
                  ? ComplainCard(
                      title: getLocalizedString(
                        '${i18.common.EMP_INBOX}${statusMap!.businessService}'
                            .toUpperCase(),
                      ),
                      id: '${getLocalizedString(i18.tradeLicense.EMP_APPLICATION_NO)}: ${processInstance.businessId ?? 'N/A'}',
                      date:
                          '${getLocalizedString(i18.tradeLicense.EMP_APPLICATION_DATE, module: Modules.TL)}: ${businessObject?.applicationDate.toCustomDateFormat() ?? 'N/A'}', //pattern: 'd/MM/yyyy'

                      address: 'Locality: $locality'.capitalize,
                      ulbOfficial:
                          '${getLocalizedString(i18.common.EMP_ULB)}: ${processInstance.assigner?.name ?? 'N/A'}',
                      sla:
                          '${getLocalizedString(i18.common.EMP_SLA)}: ${getSlaStatus(processInstance.businesssServiceSla)}',
                      status: getLocalizedString(
                        'WF_${businessObject!.workflowCode}_${businessObject.status}'
                            .toUpperCase(),
                        module: Modules.TL,
                      ),
                      rating: processInstance.rating ?? 0,
                      statusColor: BaseConfig.statusPendingColor,
                      statusBackColor: BaseConfig.statusPendingBackColor,
                      o: o,
                      onTap: () {
                        //TODO: GO to details page
                        Get.toNamed(
                          AppRoutes.EMP_TRADE_LICENSE_DETAILS,
                          arguments: {
                            'item': item,
                            'index': index,
                            'statusMap': statusMap,
                          },
                        );
                      },
                    ).paddingOnly(bottom: 10)
                  : const SizedBox.shrink();
            },
          );
        } else if (snapshot.hasError) {
          return networkErrorPage(
            context,
            () => _fetchInbox(),
          );
        } else {
          switch (snapshot.connectionState) {
            case ConnectionState.waiting:
              return SizedBox(
                height: Get.height / 1.5,
                child: showCircularIndicator(),
              );
            case ConnectionState.active:
              return SizedBox(
                height: Get.height / 1.5,
                child: showCircularIndicator(),
              );
            default:
              return const SizedBox.shrink();
          }
        }
      },
    );
  }

  void _openFilterBottomSheet(bool isSelected, Orientation o) {
    Get.bottomSheet(
      StatefulBuilder(
        builder: (context, setState) {
          return FilterBottomSheet(
            title: getLocalizedString(i18.inbox.FILTER_BY),
            content: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                //TODO: Assigned Filter Widget
                const AssignedFilter(),
                SizedBox(height: 16.h),

                MediumTextNotoSans(
                  text: '${getLocalizedString(i18.inbox.LOCALITY)}:',
                  fontWeight: FontWeight.w500,
                  size: o == Orientation.portrait ? 14.sp : 8.sp,
                ),
                SizedBox(height: 16.h),

                //TODO: Locality Selection Widget
                const LocalitySelectionWidget(),
              ],
            ),
            onApply: () {
              applyFilter();
              Get.back();
            },
            onCancel: () {
              Get.back();
            },
            orientation: o,
          );
        },
      ),
      isScrollControlled: true,
      isDismissible: false,
      enableDrag: false,
    );
  }
}
