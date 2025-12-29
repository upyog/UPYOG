import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/locality_widget/locality_selection_widget.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/components/text_formfield_normal.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/grievance_controller.dart';
import 'package:mobile_app/controller/locality_controller.dart';
import 'package:mobile_app/model/citizen/grievance/grievance.dart' as gr;
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/screens/citizen/grievance_redressal/grievance_complaints_view_all/grievance_complaints_view_all.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/check_box_app.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class EmpGrievanceScreen extends StatefulWidget {
  const EmpGrievanceScreen({super.key});

  @override
  State<EmpGrievanceScreen> createState() => _EmpGrievanceScreenState();
}

class _EmpGrievanceScreenState extends State<EmpGrievanceScreen> {
  final _authController = Get.find<AuthController>();
  final _grievanceController = Get.find<GrievanceController>();
  final _commonController = Get.find<CommonController>();
  final _localityController = Get.put(LocalityController());

  final searchController = TextEditingController();

  final pageController = PageController(viewportFraction: 1.05, keepPage: true);
  var isSelected = false;
  var isLoading = false.obs;
  List<gr.ServiceWrapper>? serviceWrappersSearch;
  TenantTenant? tenant;

  // All grievance status list
  List<GrievanceStatusFilter> allGrievanceStatusList = <GrievanceStatusFilter>[
    GrievanceStatusFilter(
      title: GrievanceStatus.PENDING_FOR_ASSIGNMENT.name,
      isSelected: false,
    ),
    GrievanceStatusFilter(
      title: GrievanceStatus.PENDING_FOR_REASSIGNMENT.name,
      isSelected: false,
    ),
    GrievanceStatusFilter(
      title: GrievanceStatus.PENDING_AT_LME.name,
      isSelected: false,
    ),
    GrievanceStatusFilter(
      title: GrievanceStatus.REJECTED.name,
      isSelected: false,
    ),
    GrievanceStatusFilter(
      title: GrievanceStatus.RESOLVED.name,
      isSelected: false,
    ),
    GrievanceStatusFilter(
      title: GrievanceStatus.CLOSED_REJECTION.name,
      isSelected: false,
    ),
    GrievanceStatusFilter(
      title: GrievanceStatus.CLOSED_SOLUTION.name,
      isSelected: false,
    ),
    GrievanceStatusFilter(
      title: GrievanceStatus.PENDING_SUPERVISOR.name,
      isSelected: false,
    ),
    GrievanceStatusFilter(
      title: GrievanceStatus.RESOLVED_SUPERVISOR.name,
      isSelected: false,
    ),
    GrievanceStatusFilter(
      title: GrievanceStatus.CANCELLED.name,
      isSelected: false,
    ),
  ];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _fetchGrievance();
    });
    searchController.addListener(_searchGrievances);
  }

  Future<void> _fetchGrievance() async {
    try {
      serviceWrappersSearch = null;
      isLoading.value = true;
      _grievanceController.length.value = 0;
      await _commonController.fetchLabels(modules: Modules.PGR);

      tenant = await getCityTenant();
      await _grievanceController.getGrievancesEmp(
        token: _authController.token!.accessToken!,
        tenantId: '${tenant!.code}',
      );

      await _localityController.fetchLocality();

      isLoading.value = false;
    } catch (e) {
      isLoading.value = false;
      dPrint('Error fetching grievances: $e');
    }
  }

  // Grievance search with title and id
  void _searchGrievances() {
    String query = searchController.text.toLowerCase();

    if (query.trim().isEmpty) return;

    if (_grievanceController.grievance.serviceWrappers != null) {
      setState(() {
        serviceWrappersSearch =
            _grievanceController.grievance.serviceWrappers?.where(
          (serviceWrapper) {
            if (serviceWrapper.service == null) return false;
            final title = getLocalizedString(
              '${i18.common.SERVICE_DEFS}${serviceWrapper.service?.serviceCode}'
                  .toUpperCase(),
              module: Modules.PGR,
            ).toLowerCase();
            String id =
                serviceWrapper.service?.serviceRequestId?.toLowerCase() ?? '';
            return title.contains(query.toLowerCase()) ||
                id.contains(query.toLowerCase());
          },
        ).toList();
      });
    }
  }

  // Apply search filters
  Future<void> applyFilters() async {
    List<GrievanceStatusFilter> selectedStatus =
        allGrievanceStatusList.where((status) => status.isSelected).toList();

    bool isStatusEmpty = selectedStatus.isEmpty;

    if (!isStatusEmpty) {
      serviceWrappersSearch = _grievanceController.grievance.serviceWrappers
          ?.where((serviceWrapper) {
        bool statusMatch = isStatusEmpty ||
            selectedStatus.any(
              (status) =>
                  serviceWrapper.service?.applicationStatus == status.title,
            );

        return statusMatch;
      }).toList();
    } else {
      serviceWrappersSearch = _grievanceController.grievance.serviceWrappers;
    }

    if (_localityController.selectedLocalityList.isNotEmpty) {
      await _grievanceController.getGrievancesEmp(
        token: _authController.token!.accessToken!,
        tenantId: '${tenant!.code}',
        locality: _localityController.getSelectedLocalityCode().join(','),
      );
    }

    setState(() {});
  }

  @override
  void dispose() {
    super.dispose();
    searchController.removeListener(_searchGrievances);
    serviceWrappersSearch = null;
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Wrap(
          children: [
            Text(getLocalizedString(i18.grievance.GRIEVANCE)),
            Obx(
              () => Text(' (${_grievanceController.length})'),
            ),
          ],
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: RefreshIndicator(
        color: BaseConfig.appThemeColor1,
        onRefresh: _fetchGrievance,
        child: SizedBox(
          height: Get.height,
          width: Get.width,
          child: SingleChildScrollView(
            child: Column(
              children: [
                Obx(
                  () => isLoading.value
                      ? const SizedBox.shrink()
                      : Row(
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
                                    borderRadius: BorderRadius.circular(12.r),
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
                                  height:
                                      o == Orientation.portrait ? 24.h : 34.h,
                                  width:
                                      o == Orientation.portrait ? 24.w : 34.w,
                                ),
                              ),
                            ),
                          ],
                        ).marginOnly(bottom: 16.h),
                ),
                _buildBody(o),
              ],
            ).paddingSymmetric(horizontal: 16.w, vertical: 20.h),
          ),
        ),
      ),
    );
  }

  Widget _buildBody(Orientation o) {
    return StreamBuilder(
      stream: _grievanceController.streamCtrl.stream,
      builder: (context, AsyncSnapshot snapshot) {
        if (snapshot.hasData) {
          if (snapshot.data is String || snapshot.data == null) {
            return const NoApplicationFoundWidget();
          }
          final gr.Grievance grievance = snapshot.data;
          final serviceWrappersToShow =
              serviceWrappersSearch ?? grievance.serviceWrappers;
          if (isNotNullOrEmpty(serviceWrappersToShow)) {
            return SingleChildScrollView(
              physics: const BouncingScrollPhysics(),
              child: ListView.builder(
                itemCount: serviceWrappersToShow!.length >= 10
                    ? serviceWrappersToShow.length + 1
                    : serviceWrappersToShow.length,
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemBuilder: (context, index) {
                  if (index == serviceWrappersToShow.length &&
                      serviceWrappersToShow.length >= 10) {
                    return Obx(() {
                      if (_grievanceController.isLoading.value) {
                        return showCircularIndicator();
                      } else {
                        return IconButton(
                          onPressed: () async {
                            TenantTenant tenantCity = await getCityTenant();
                            await _grievanceController.loadMoreGriAppEmp(
                              token: _authController.token!.accessToken!,
                              tenantId: '${tenantCity.code}',
                            );
                          },
                          icon: const Icon(
                            Icons.expand_circle_down_outlined,
                            size: 30,
                            color: BaseConfig.appThemeColor1,
                          ),
                        );
                      }
                    });
                  } else {
                    final serviceWrappers = serviceWrappersToShow[index];
                    return isNotNullOrEmpty(serviceWrappers.service)
                        ? ComplainCard(
                            title: serviceWrappers.service?.serviceCode != null
                                ? getLocalizedString(
                                    '${i18.common.SERVICE_DEFS}${serviceWrappers.service?.serviceCode}'
                                        .toUpperCase(),
                                    module: Modules.PGR,
                                  )
                                : 'N/A',
                            id: 'Grievance ID: ${serviceWrappers.service?.serviceRequestId ?? ''}',
                            date:
                                'Date: ${serviceWrappers.service?.auditDetails?.createdTime.toCustomDateFormat() ?? 'd-MMM-yyyy'}',
                            status: getLocalizedString(
                              '${i18.common.CS_COMMON}${serviceWrappers.service?.applicationStatus}',
                              module: Modules.PGR,
                            ),
                            rating: serviceWrappers.service?.rating ?? 0,
                            statusColor: getGrievanceStatusTextColor(
                              serviceWrappers.service!.applicationStatus!,
                            ),
                            statusBackColor: getGrievanceStatusBackColor(
                              serviceWrappers.service!.applicationStatus!,
                            ),
                            o: o,
                            onTap: () async {
                              await _grievanceController.getEmpMdmsPGR(
                                serviceWrappers.service!.tenantId!,
                              );

                              if (closedStatusList.contains(
                                    serviceWrappers.service!.applicationStatus!,
                                  ) ||
                                  resolvedStatusList.contains(
                                    serviceWrappers.service!.applicationStatus!,
                                  )) {
                                Get.toNamed(
                                  AppRoutes.EMP_GRIEVANCES_DETAILS,
                                  arguments: {
                                    'serviceWrappers': serviceWrappers,
                                  },
                                );
                              } else {
                                Get.toNamed(
                                  AppRoutes.EMP_GRIEVANCES_DETAILS,
                                  arguments: {
                                    'serviceWrappers': serviceWrappers,
                                  },
                                );
                              }
                            },
                          ).paddingOnly(bottom: 16)
                        : const SizedBox.shrink();
                  }
                },
              ),
            );
          } else {
            return const NoApplicationFoundWidget();
          }
        } else if (snapshot.hasError) {
          return networkErrorPage(
            context,
            () => _fetchGrievance(),
          );
        } else {
          switch (snapshot.connectionState) {
            case ConnectionState.waiting:
              return SizedBox(
                height: Get.height * 0.6,
                child: showCircularIndicator(),
              );
            case ConnectionState.active:
              return SizedBox(
                height: Get.height * 0.6,
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
          return Container(
            height:
                o == Orientation.portrait ? Get.height * 0.6 : Get.height * 0.9,
            decoration: BoxDecoration(
              color: BaseConfig.mainBackgroundColor,
              borderRadius: BorderRadius.only(
                topLeft: Radius.circular(20.r),
                topRight: Radius.circular(20.r),
              ),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 16),
                Row(
                  children: [
                    IconButton(
                      onPressed: () => Get.back(),
                      icon: const Icon(
                        Icons.chevron_left,
                        size: 28,
                      ),
                    ),
                    SizedBox(width: o == Orientation.portrait ? 8.w : 1.w),
                    MediumTextNotoSans(
                      text: 'Filter & Sort',
                      size: o == Orientation.portrait ? 16.sp : 8.sp,
                      fontWeight: FontWeight.w500,
                    ),
                  ],
                ),
                SizedBox(height: 8.h),
                const Divider(),
                Expanded(
                  child: SingleChildScrollView(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        SizedBox(height: 16.h),
                        MediumTextNotoSans(
                          text: getLocalizedString(
                            i18.grievance.PGR_FILTER_STATUS,
                            module: Modules.PGR,
                          ),
                          fontWeight: FontWeight.w500,
                          size: o == Orientation.portrait ? 14.sp : 8.sp,
                        ),
                        SizedBox(height: 16.h),
                        GridView.builder(
                          gridDelegate:
                              const SliverGridDelegateWithFixedCrossAxisCount(
                            crossAxisCount: 2,
                            crossAxisSpacing: 10.0,
                            mainAxisSpacing: 10.0,
                            childAspectRatio: 4.0,
                          ),
                          itemCount: allGrievanceStatusList.length,
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          itemBuilder: (context, index) {
                            var status = allGrievanceStatusList[index];
                            return CheckBoxApp(
                              o: o,
                              title: getLocalizedString(
                                '${i18.common.CS_COMMON}${status.title}'
                                    .toUpperCase(),
                                module: Modules.PGR,
                              ),
                              value: status.isSelected,
                              onTap: () {
                                setState(() {
                                  status.isSelected = !status.isSelected;
                                });
                              },
                            );
                          },
                        ),
                      ],
                    ),
                  ),
                ),
                MediumTextNotoSans(
                  text:
                      '${getLocalizedString(i18.grievance.PGR_LOCALITY, module: Modules.PGR)}:',
                  fontWeight: FontWeight.w500,
                  size: o == Orientation.portrait ? 14.sp : 8.sp,
                ),
                SizedBox(height: 16.h),
                const LocalitySelectionWidget(),
                SizedBox(height: 16.h),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Expanded(
                      child: FilledButtonApp(
                        text: 'Cancel',
                        textColor: BaseConfig.appThemeColor1,
                        onPressed: () => Get.back(),
                        backgroundColor: BaseConfig.mainBackgroundColor,
                        fontSize: o == Orientation.portrait ? 14.sp : 7.sp,
                        fontWeight: FontWeight.w600,
                        side: const BorderSide(
                          color: BaseConfig.appThemeColor1,
                          width: 1.5,
                        ),
                      ),
                    ),
                    SizedBox(width: 16.w),
                    Expanded(
                      child: FilledButtonApp(
                        fontSize: o == Orientation.portrait ? 14.sp : 7.sp,
                        text: 'Apply',
                        onPressed: () {
                          applyFilters();
                          Get.back();
                        },
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 16.h),
              ],
            ).paddingSymmetric(horizontal: 16.w),
          );
        },
      ),
      isScrollControlled: true,
      isDismissible: false,
      enableDrag: false,
    );
  }
}
