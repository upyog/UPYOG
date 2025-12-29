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
import 'package:mobile_app/controller/water_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/employee/emp_ws_model/emp_ws_model.dart';
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

class EmpWaterScreen extends StatefulWidget {
  const EmpWaterScreen({super.key});

  @override
  State<EmpWaterScreen> createState() => _EmpWaterScreenState();
}

class _EmpWaterScreenState extends State<EmpWaterScreen> {
  final _authController = Get.find<AuthController>();
  final _waterController = Get.find<WaterController>();
  final _commonController = Get.find<CommonController>();
  final _localityController = Get.put(LocalityController());

  final searchController = TextEditingController();

  final pageController = PageController(viewportFraction: 1.05, keepPage: true);
  bool isSelected = false;
  late TenantTenant tenantCity;
  RxBool isLoading = false.obs;

  bool get isValidToken => _authController.token?.accessToken != null;

  @override
  void initState() {
    isLoading.value = true;
    _waterController.setDefaultLimit();
    _initModule();
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _fetchInbox();
    });
    isLoading.value = false;
  }

  void _initModule() async {
    await _commonController.fetchLabels(modules: Modules.WS);
    await _commonController.fetchLabels(modules: Modules.PT);
    await _commonController.fetchLabels(modules: Modules.ABG);

    await _waterController.clearEditWaterDetailsLocalData();
  }

  Future<void> _fetchInbox() async {
    tenantCity = await getCityTenant();
    await _waterController.getEmpWsInboxApplications(
      token: _authController.token!.accessToken!,
      tenantId: '${tenantCity.code}',
    );

    await _localityController.fetchLocality();
  }

  // Apply search filters
  Future<void> applyFilter() async {
    await _waterController.getEmpWsInboxApplications(
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
          () => Wrap(
            children: [
              Text(getLocalizedString(i18.common.WATER_SEWERAGE)),
              Text(' (${_waterController.totalCount.value})'),
            ],
          ),
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
                () => isLoading.value
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
      stream: _waterController.streamCtrl.stream,
      builder: (context, AsyncSnapshot snapshot) {
        if (snapshot.hasData) {
          if (snapshot.data is String || snapshot.data == null) {
            return Center(
              child: Text(getLocalizedString(i18.inbox.NO_APPLICATION)),
            );
          }

          final EmpWsModel wsInboxes = snapshot.data;

          return ListView.builder(
            itemCount: wsInboxes.wsItems!.length + 1,
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemBuilder: (context, index) {
              if (index == wsInboxes.wsItems!.length) {
                return wsInboxes.wsItems!.isNotEmpty
                    ? Obx(
                        () => FilledButtonApp(
                          text: 'Load more',
                          isLoading: _waterController.isLoading.value,
                          circularColor: BaseConfig.fillAppBtnCircularColor,
                          onPressed: () {
                            _waterController.loadMore(
                              token: _authController.token!.accessToken!,
                              tenantId: tenantCity.code!,
                            );
                          },
                        ),
                      )
                    : const Center(
                        child: SmallTextNotoSans(
                          text: 'No items found!',
                          color: BaseConfig.greyColor3,
                        ),
                      );
              }

              final item = wsInboxes.wsItems![index];
              final businessObject = item.businessObject;
              final serviceCode = BusinessServicesEmp.NEW_WS1.name;
              final statusMap = getFilteredStatusMapEmp(
                wsInboxes.statusMap,
                serviceCode,
                applicationStatus: businessObject?.data?.applicationStatus,
              );

              final locality = getLocalizedString(
                getLocality(
                  tenantCity,
                  businessObject?.data?.additionalDetails?.locality ?? '',
                ),
              );

              return businessObject != null
                  ? ComplainCard(
                      title: getLocalizedString(
                        '${i18.common.EMP_INBOX}${statusMap?.businessService ?? BusinessServicesEmp.NEW_WS1.name}'
                            .toUpperCase(),
                      ),
                      id: '${getLocalizedString(i18.waterSewerage.EMP_APP_NO, module: Modules.WS)}: ${businessObject.data?.applicationNo ?? 'N/A'}',
                      date:
                          'Date: ${businessObject.data?.additionalDetails?.appCreatedDate?.toCustomDateFormat() ?? 'N/A'}', //pattern: 'd/MM/yyyy'
                      address: 'Locality: $locality'.capitalize,
                      sla:
                          '${getLocalizedString(i18.common.EMP_SLA)}: ${businessObject.serviceSla}',
                      status: getLocalizedString(
                        'CS_${businessObject.data?.applicationStatus}'
                            .toUpperCase(),
                      ),
                      statusColor: BaseConfig.statusPendingColor,
                      statusBackColor: BaseConfig.statusPendingBackColor,
                      o: o,
                      onTap: () {
                        Get.toNamed(
                          AppRoutes.EMP_WS_DETAILS,
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
                const AssignedFilter(),
                SizedBox(height: 16.h),
                MediumTextNotoSans(
                  text: '${getLocalizedString(i18.inbox.LOCALITY)}:',
                  fontWeight: FontWeight.w500,
                  size: o == Orientation.portrait ? 14.sp : 8.sp,
                ),
                SizedBox(height: 16.h),
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
