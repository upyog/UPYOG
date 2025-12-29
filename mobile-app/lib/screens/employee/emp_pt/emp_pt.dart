import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/locality_widget/locality_selection_widget.dart';
import 'package:mobile_app/components/text_formfield_normal.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/locality_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/employee/emp_pt_model/emp_pt_model.dart' as pt;
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/app_bottomsheet.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class EmpPtScreen extends StatefulWidget {
  const EmpPtScreen({super.key});

  @override
  State<EmpPtScreen> createState() => _EmpPtScreenState();
}

class _EmpPtScreenState extends State<EmpPtScreen> {
  final _authController = Get.find<AuthController>();
  final _commonController = Get.find<CommonController>();
  final _ptController = Get.find<PropertiesTaxController>();
  final _localityController = Get.put(LocalityController());

  final applicationNo = TextEditingController();
  final propertyId = TextEditingController();
  final mobileNo = TextEditingController();

  final pageController = PageController(viewportFraction: 1.05, keepPage: true);
  var isSelected = false;
  late TenantTenant tenantCity;
  var isLoading = false.obs,
      hasCempRole = false.obs,
      hasApproverRole = false.obs,
      isApplyEnabled = false.obs,
      showResetButton = false.obs;

  @override
  void initState() {
    super.initState();
    _ptController.setDefaultLimitEmp();
    _init();
    applicationNo.addListener(updateApplyState);
    propertyId.addListener(updateApplyState);
    mobileNo.addListener(updateApplyState);
  }

  Future<void> _init() async {
    isLoading.value = true;
    await _commonController.fetchLabels(modules: Modules.PT);
    await _fetchInbox();
    isLoading.value = false;
  }

  Future<void> _fetchInbox() async {
    showResetButton.value = false;
    hasCempRole.value = _authController.token!.userRequest!.roles!.any(
      (role) => role.code == InspectorType.PT_CEMP_INSPECTOR.name,
    );

    tenantCity = await getCityTenant();
    await callInbox();
    await _localityController.fetchLocality();
  }

  void updateApplyState() {
    isApplyEnabled.value = applicationNo.text.isNotEmpty ||
        propertyId.text.isNotEmpty ||
        mobileNo.text.isNotEmpty;
  }

  Future<void> callInbox({
    String? applicationNumber,
    String? propertyId,
    String? mobileNumber,
    List<dynamic>? locality,
  }) async {
    await _ptController.getEmpPtInboxApplications(
      token: _authController.token!.accessToken!,
      tenantId: '${tenantCity.code}',
      isFilter: _localityController.selectedLocalityList.isEmpty ? false : true,
      applicationNumber: applicationNumber,
      propertyId: propertyId,
      mobileNumber: mobileNumber,
      locality: locality,
    );
  }

  // Apply search filters
  // Future<void> applyFilter() async {
  //   await _ptController.getEmpPtInboxApplications(
  //     token: _authController.token!.accessToken!,
  //     tenantId: '${tenantCity.code}',
  //     isFilter: _localityController.selectedLocalityList.isEmpty ? false : true,
  //     locality: _localityController.selectedLocalityList.isEmpty
  //         ? null
  //         : _localityController.getSelectedLocalityCode(),
  //   );
  //   dPrint('Apply filter closed!!!');
  // }

  @override
  void dispose() {
    applicationNo.removeListener(updateApplyState);
    propertyId.removeListener(updateApplyState);
    mobileNo.removeListener(updateApplyState);
    applicationNo.dispose();
    propertyId.dispose();
    mobileNo.dispose();
    _ptController.setDefaultLimitEmp();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    dPrint('loading: ${isLoading.value}');
    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Obx(
          () => isLoading.value
              ? const SizedBox.shrink()
              : Wrap(
                  children: [
                    Text(getLocalizedString(i18.propertyTax.EMP_PT_TITLE)),
                    Text(' (${_ptController.length.value})'),
                  ],
                ),
        ),
        onPressed: () => Navigator.of(context).pop(),
        actions: [
          Obx(() {
            if (isLoading.value) {
              return const SizedBox.shrink();
            }
            return Row(
              children: [
                IconButton(
                  style: IconButton.styleFrom(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12.r),
                    ),
                  ),
                  onPressed: () {
                    openFilterBottomSheet(
                      title: getLocalizedString(i18.common.ES_SEARCH),
                      actionText: getLocalizedString(i18.common.ES_SEARCH),
                      children: [
                        textFormFieldNormal(
                          context,
                          getLocalizedString(
                            i18.propertyTax.PT_PROPERTY_APPLICATION_NO,
                            module: Modules.PT,
                          ),
                          controller: applicationNo,
                          hintTextColor: BaseConfig.filterIconColor,
                          prefixIcon: const Icon(
                            Icons.search,
                            color: BaseConfig.filterIconColor,
                          ),
                          textInputAction: TextInputAction.done,
                          keyboardType: TextInputType.text,
                        ),
                        const Divider(),
                        textFormFieldNormal(
                          context,
                          getLocalizedString(
                            i18.propertyTax.SEARCH_UNIQUE_PROPERTY_ID,
                            module: Modules.PT,
                          ),
                          controller: propertyId,
                          hintTextColor: BaseConfig.filterIconColor,
                          prefixIcon: const Icon(
                            Icons.search,
                            color: BaseConfig.filterIconColor,
                          ),
                          textInputAction: TextInputAction.done,
                          keyboardType: TextInputType.text,
                        ),
                        const Divider(),
                        textFormFieldNormal(
                          context,
                          getLocalizedString(
                            i18.propertyTax.SEARCH_APPLICATION_MOBILE_NO,
                            module: Modules.PT,
                          ),
                          controller: mobileNo,
                          hintTextColor: BaseConfig.filterIconColor,
                          prefixIcon: const Padding(
                            padding: EdgeInsets.fromLTRB(20, 15, 20, 15),
                            child: MediumTextNotoSans(
                              text: '+91',
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          textInputAction: TextInputAction.done,
                          keyboardType: AppPlatforms.platformKeyboardType(),
                          inputFormatters: [
                            LengthLimitingTextInputFormatter(
                              10,
                            ),
                            FilteringTextInputFormatter.digitsOnly,
                          ],
                        ),
                        SizedBox(height: 8.h),
                        if (showResetButton.value)
                          FilledButtonApp(
                            onPressed: () async {
                              applicationNo.clear();
                              propertyId.clear();
                              mobileNo.clear();
                              showResetButton.value = false;
                              await callInbox();
                              Get.back();
                            },
                            text: getLocalizedString(i18.common.ES_CLEAR),
                          ),
                      ],
                      onApply: () async {
                        if (isApplyEnabled.value) {
                          await callInbox(
                            applicationNumber: applicationNo.text.isNotEmpty
                                ? applicationNo.text
                                : null,
                            propertyId: propertyId.text.isNotEmpty
                                ? propertyId.text
                                : null,
                            mobileNumber:
                                mobileNo.text.isNotEmpty ? mobileNo.text : null,
                          );
                          showResetButton.value = true;
                          setState(() {
                            applicationNo.clear();
                            propertyId.clear();
                            mobileNo.clear();
                          });
                          Get.back();
                        } else {
                          snackBar('Required', '', Colors.red);
                        }
                      },
                    );
                  },
                  icon: Icon(
                    Icons.search_rounded,
                    color: BaseConfig.filterIconColor,
                    size: o == Orientation.portrait ? 24.h : 34.h,
                  ),
                ),
                IconButton(
                  style: IconButton.styleFrom(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12.r),
                    ),
                  ),
                  onPressed: () {
                    openFilterBottomSheet(
                      o: o,
                      title: getLocalizedString(i18.inbox.FILTER_BY),
                      children: [
                        MediumTextNotoSans(
                          text: '${getLocalizedString(i18.inbox.LOCALITY)}:',
                          fontWeight: FontWeight.w500,
                          size: o == Orientation.portrait ? 14.sp : 8.sp,
                        ),
                        SizedBox(height: 16.h),
                        const LocalitySelectionWidget(),
                      ],
                      onApply: () async {
                        await callInbox(
                          locality: _localityController
                                  .selectedLocalityList.isEmpty
                              ? null
                              : _localityController.getSelectedLocalityCode(),
                        );
                        Get.back();
                      },
                    );
                  },
                  icon: SvgPicture.asset(
                    BaseConfig.filterIconSvg,
                    colorFilter: const ColorFilter.mode(
                      BaseConfig.filterIconColor,
                      BlendMode.srcIn,
                    ),
                    height: o == Orientation.portrait ? 24.h : 34.h,
                    width: o == Orientation.portrait ? 24.w : 34.w,
                  ),
                ),
              ],
            );
          }),
        ],
      ),
      floatingActionButton: Obx(
        () => isLoading.value
            ? const SizedBox.shrink()
            : hasCempRole.value
                ? Padding(
                    padding: const EdgeInsets.only(bottom: 80.0),
                    child: _authController.isValidUser
                        ? IconButton(
                            onPressed: () {
                              Get.toNamed(
                                AppRoutes.EMP_PT_REGISTRATION,
                              );
                            },
                            icon: const Icon(
                              Icons.add_circle_outline,
                              size: 40,
                              color: BaseConfig.appThemeColor1,
                            ),
                          )
                        : const SizedBox.shrink(),
                  )
                : const SizedBox.shrink(),
      ),
      body: OrientationBuilder(
        builder: (context, o) {
          return RefreshIndicator(
            color: BaseConfig.appThemeColor1,
            onRefresh: _fetchInbox,
            child: SizedBox(
              height: Get.height,
              width: Get.width,
              child: SingleChildScrollView(
                child: _buildBody(o)
                    .paddingSymmetric(horizontal: 16.w, vertical: 20.h),
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildBody(Orientation o) {
    return StreamBuilder(
      stream: _ptController.streamCtrl.stream,
      builder: (context, AsyncSnapshot snapshot) {
        if (snapshot.hasData) {
          if (snapshot.data is String || snapshot.data == null) {
            return SizedBox(
              height: Get.height / 1.5,
              child: Center(
                child: Text(getLocalizedString(i18.inbox.NO_APPLICATION)),
              ),
            );
          }

          final pt.EmpPtModel ptInboxes = snapshot.data;

          if (!isNotNullOrEmpty(ptInboxes.items)) {
            return SizedBox(
              height: Get.height / 1.5,
              child: Center(
                child: Text(getLocalizedString(i18.inbox.NO_APPLICATION)),
              ),
            );
          }

          return ListView.builder(
            itemCount: ptInboxes.items!.length + 1,
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemBuilder: (context, index) {
              if (index == ptInboxes.items!.length) {
                return ptInboxes.items!.isNotEmpty &&
                        ptInboxes.items!.length >= 5
                    ? Obx(
                        () => FilledButtonApp(
                          text: 'Load more',
                          isLoading: _ptController.isLoading.value,
                          circularColor: BaseConfig.fillAppBtnCircularColor,
                          onPressed: () {
                            _ptController.loadMoreEmp(
                              token: _authController.token!.accessToken!,
                              tenantId: tenantCity.code!,
                              applicationNumber: applicationNo.text.isNotEmpty
                                  ? applicationNo.text
                                  : null,
                              propertyId: propertyId.text.isNotEmpty
                                  ? propertyId.text
                                  : null,
                              mobileNumber: mobileNo.text.isNotEmpty
                                  ? mobileNo.text
                                  : null,
                              locality: _localityController
                                      .selectedLocalityList.isEmpty
                                  ? null
                                  : _localityController
                                      .getSelectedLocalityCode(),
                            );
                          },
                        ),
                      )
                    : ptInboxes.items!.isNotEmpty
                        ? const SizedBox.shrink()
                        : Center(
                            child: SmallTextNotoSans(
                              text:
                                  getLocalizedString(i18.inbox.NO_APPLICATION),
                              color: BaseConfig.greyColor3,
                            ),
                          );
              }

              final item = ptInboxes.items![index];
              final processInstance = item.processInstance;
              final businessObject = item.businessObject;
              final serviceCode = businessObject?.businessService ??
                  processInstance?.businessService ??
                  '';

              final appStatus = processInstance?.state?.applicationStatus ??
                  businessObject?.status;

              final statusMap = getFilteredStatusMapEmp(
                ptInboxes.statusMap,
                serviceCode,
                applicationStatus: appStatus,
              );

              final locality = getLocalizedString(
                getLocality(
                  tenantCity,
                  businessObject?.address?.locality?.code ?? '',
                ),
              );

              return businessObject != null
                  ? ComplainCard(
                      title: getLocalizedString(
                        processInstance?.businessService,
                        module: Modules.PT,
                      ),
                      id: '${getLocalizedString(i18.propertyTax.PROPERTY_ID, module: Modules.PT)}: ${businessObject.propertyId ?? 'N/A'}',
                      date:
                          'Date: ${businessObject.auditDetails?.lastModifiedTime?.toCustomDateFormat() ?? 'N/A'}', //pattern: 'd/MM/yyyy'
                      address: 'Locality: $locality'.capitalize,
                      sla:
                          '${getLocalizedString(i18.common.EMP_SLA)}: ${getSlaStatus(processInstance?.businesssServiceSla)}',
                      status: getLocalizedString(
                        '${i18.propertyTax.EMP_PT_STATUS}${processInstance!.state!.state}'
                            .toUpperCase(),
                        module: Modules.PT,
                      ),
                      statusColor: BaseConfig.statusResolvedColor,
                      statusBackColor: BaseConfig.statusResolvedBackColor,
                      o: o,
                      onTap: () {
                        Get.toNamed(
                          AppRoutes.EMP_PT_DETAILS,
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
}
