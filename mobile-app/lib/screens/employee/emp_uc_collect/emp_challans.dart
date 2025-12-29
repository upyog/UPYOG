import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/text_formfield_normal.dart';
import 'package:mobile_app/components/uc_component/uc_service_category_widget.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/challan_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/employee/emp_challans/emp_challans_model.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/app_bottomsheet.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/check_box_app.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class UcStatusFilter {
  UCStatus title;
  bool isSelected;

  UcStatusFilter({
    required this.title,
    this.isSelected = false,
  });
}

class EmpChallansScreen extends StatefulWidget {
  const EmpChallansScreen({super.key});

  @override
  State<EmpChallansScreen> createState() => _EmpChallansScreenState();
}

class _EmpChallansScreenState extends State<EmpChallansScreen> {
  final _authController = Get.find<AuthController>();
  final _commonController = Get.find<CommonController>();
  final _challansController = Get.find<ChallansController>();

  TenantTenant? tenant;
  RxBool hasCreateChallanPermission = false.obs,
      isApplyEnabled = false.obs,
      showResetButton = false.obs;

  final challanNo = TextEditingController();
  final mobileNo = TextEditingController();
  final receiptNo = TextEditingController();

  @override
  void initState() {
    super.initState();
    init();
    challanNo.addListener(updateApplyState);
    mobileNo.addListener(updateApplyState);
    receiptNo.addListener(updateApplyState);
  }

  Future<void> init() async {
    showResetButton.value = false;
    hasCreateChallanPermission.value =
        _authController.token!.userRequest!.roles!.any(
      (role) => role.code == InspectorType.UC_EMP_INSPECTOR.name,
    );

    _challansController.isLoading.value = true;
    tenant = await getCityTenant();
    await _commonController.fetchLabels(
      modules: Modules.UC,
    );

    await callChallans();

    _challansController.isLoading.value = false;
  }

  Future<void> callChallans({
    String? challanNo,
    String? receiptNumber,
    String? mobileNumber,
    String? businessServices,
    String? status,
  }) async {
    await _challansController.getChallans(
      tenantId: tenant!.code!,
      token: _authController.token!.accessToken!,
      challanNo: challanNo,
      receiptNumber: receiptNumber,
      mobileNumber: mobileNumber,
      businessServices: businessServices,
      status: status,
    );
  }

  void updateApplyState() {
    isApplyEnabled.value = challanNo.text.isNotEmpty ||
        receiptNo.text.isNotEmpty ||
        mobileNo.text.isNotEmpty;
  }

  @override
  void dispose() {
    challanNo.removeListener(updateApplyState);
    mobileNo.removeListener(updateApplyState);
    receiptNo.removeListener(updateApplyState);

    challanNo.dispose();
    mobileNo.dispose();
    receiptNo.dispose();
    showResetButton.value = false;
    super.dispose();
  }

  RxList<UcStatusFilter> statusFilters = [
    UcStatusFilter(title: UCStatus.PAID, isSelected: false),
    UcStatusFilter(title: UCStatus.CANCELLED, isSelected: false),
    UcStatusFilter(title: UCStatus.ACTIVE, isSelected: false),
  ].obs;

  // Apply search filters
  String? getStatusFilter() {
    List<UcStatusFilter> selectedStatus =
        statusFilters.where((status) => status.isSelected).toList();

    if (selectedStatus.isNotEmpty) {
      return selectedStatus.map((status) => status.title.name).join(',');
    }

    return null;
  }

  @override
  Widget build(BuildContext context) {
    final size = Get.size;
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Obx(
          () => _challansController.isLoading.value
              ? const SizedBox.shrink()
              : Wrap(
                  children: [
                    Text(
                      getLocalizedString(
                        i18.challans.UC_SEARCH_HEADER,
                        module: Modules.UC,
                      ),
                    ),
                    if (_challansController.length.value > 0)
                      Text(' (${_challansController.length.value})'),
                  ],
                ),
        ),
        actions: [
          Obx(() {
            if (_challansController.isLoading.value) {
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
                      title: getLocalizedString(
                        i18.challans.UC_SEARCH,
                        module: Modules.UC,
                      ),
                      actionText: getLocalizedString(i18.common.ES_SEARCH),
                      children: [
                        textFormFieldNormal(
                          context,
                          getLocalizedString(
                            i18.challans.EMP_CHALLAN_NO,
                            module: Modules.UC,
                          ),
                          controller: challanNo,
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
                            i18.challans.MOBILE_NUMBER,
                            module: Modules.UC,
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
                        const Divider(),
                        textFormFieldNormal(
                          context,
                          getLocalizedString(
                            i18.challans.UC_RECIEPT_NUMBER_LABEL,
                            module: Modules.UC,
                          ),
                          controller: receiptNo,
                          hintTextColor: BaseConfig.filterIconColor,
                          prefixIcon: const Icon(
                            Icons.search,
                            color: BaseConfig.filterIconColor,
                          ),
                          textInputAction: TextInputAction.done,
                          keyboardType: TextInputType.text,
                        ),
                        SizedBox(height: 8.h),
                        if (showResetButton.value)
                          FilledButtonApp(
                            onPressed: () async {
                              challanNo.clear();
                              receiptNo.clear();
                              mobileNo.clear();
                              showResetButton.value = false;
                              await callChallans();
                              Get.back();
                            },
                            text: getLocalizedString(
                              i18.challans.UC_CLEAR_SEARCH,
                              module: Modules.UC,
                            ),
                          ),
                      ],
                      onApply: () async {
                        if (isApplyEnabled.value) {
                          await callChallans(
                            challanNo: challanNo.text.isNotEmpty
                                ? challanNo.text
                                : null,
                            receiptNumber: receiptNo.text.isNotEmpty
                                ? receiptNo.text
                                : null,
                            mobileNumber:
                                mobileNo.text.isNotEmpty ? mobileNo.text : null,
                          );
                          showResetButton.value = true;
                          setState(() {
                            challanNo.clear();
                            receiptNo.clear();
                            mobileNo.clear();
                          });
                          Get.back();
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
                          text:
                              '${getLocalizedString(i18.challans.UC_STATUS)}:',
                          fontWeight: FontWeight.w500,
                          size: o == Orientation.portrait ? 14.sp : 8.sp,
                        ),
                        Obx(
                          () => GridView.builder(
                            gridDelegate:
                                const SliverGridDelegateWithFixedCrossAxisCount(
                              crossAxisCount: 2,
                              crossAxisSpacing: 10.0,
                              mainAxisSpacing: 10.0,
                              childAspectRatio: 4.0,
                            ),
                            itemCount: statusFilters.length,
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            itemBuilder: (context, index) {
                              final status = statusFilters[index];
                              return CheckBoxApp(
                                o: o,
                                title: getLocalizedString(
                                  'UC_${status.title.name}'.toUpperCase(),
                                ),
                                value: status.isSelected,
                                onTap: () {
                                  statusFilters[index] = UcStatusFilter(
                                    title: status.title,
                                    isSelected: !status.isSelected,
                                  );
                                },
                              );
                            },
                          ),
                        ),
                        SizedBox(height: 16.h),
                        MediumTextNotoSans(
                          text:
                              '${getLocalizedString(i18.challans.SERVICE_CATEGORY, module: Modules.UC)}:',
                          fontWeight: FontWeight.w500,
                          size: o == Orientation.portrait ? 14.sp : 8.sp,
                        ),
                        UcServiceCategoryWidget(
                          hintText:
                              '${getLocalizedString(i18.challans.SERVICE_CATEGORY, module: Modules.UC)}:',
                        ),
                      ],
                      onApply: () async {
                        _challansController.isLoading.value = true;
                        final status = getStatusFilter();
                        await callChallans(
                          businessServices: isNotNullOrEmpty(
                            _challansController.selectedServiceCategory,
                          )
                              ? _challansController.selectedServiceCategory
                                  .map(
                                    (e) => e.originalCode,
                                  )
                                  .join(',')
                              : null,
                          status: status,
                        );
                        _challansController.isLoading.value = false;
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
        onPressed: () => Get.back(),
      ),
      floatingActionButton: Obx(
        () => _challansController.isLoading.value
            ? const SizedBox.shrink()
            : hasCreateChallanPermission.value
                ? Padding(
                    padding: const EdgeInsets.only(bottom: 80.0),
                    child: _authController.isValidUser
                        ? IconButton(
                            onPressed: () {
                              Get.toNamed(
                                AppRoutes.EMP_CREATE_UC_COLLECT,
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
      body: RefreshIndicator(
        color: BaseConfig.appThemeColor1,
        onRefresh: () => init(),
        child: SizedBox(
          height: size.height,
          width: size.width,
          child: SingleChildScrollView(
            child: _buildBody(o)
                .paddingSymmetric(horizontal: 16.w, vertical: 20.h),
          ),
        ),
      ),
    );
  }

  Widget _buildBody(Orientation o) {
    return StreamBuilder(
      stream: _challansController.streamControllers.stream,
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

          final List<Challan> challans = snapshot.data;

          if (!isNotNullOrEmpty(challans)) {
            return SizedBox(
              height: Get.height / 1.5,
              child: Center(
                child: Text(getLocalizedString(i18.inbox.NO_APPLICATION)),
              ),
            );
          }

          return ListView.builder(
            itemCount: _challansController.length.value >= 10
                ? _challansController.length.value + 1
                : _challansController.length.value,
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemBuilder: (context, index) {
              if (index == _challansController.length.value &&
                  _challansController.length.value >= 10) {
                return Obx(() {
                  if (_challansController.isLoading.value) {
                    return showCircularIndicator();
                  } else {
                    return IconButton(
                      onPressed: () async {
                        final status = getStatusFilter();
                        await _challansController.loadMoreChallans(
                          token: _authController.token!.accessToken!,
                          tenantId: tenant!.code!,
                          receiptNumber:
                              receiptNo.text.isNotEmpty ? receiptNo.text : null,
                          mobileNumber:
                              mobileNo.text.isNotEmpty ? mobileNo.text : null,
                          businessServices: isNotNullOrEmpty(
                            _challansController.selectedServiceCategory,
                          )
                              ? _challansController.selectedServiceCategory
                                  .map(
                                    (e) => e.originalCode,
                                  )
                                  .join(',')
                              : null,
                          status: status,
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
                final challan = _challansController.challans!.challans![index];
                return ComplainCard(
                  title: isNotNullOrEmpty(challan.businessService)
                      ? getLocalizedString(
                          '${i18.challans.UC_BUSINESS_SERVICE}${challan.businessService!.replaceAll('.', '_').toUpperCase()}',
                        )
                      : 'N/A',
                  id: '${getLocalizedString(i18.challans.EMP_CHALLAN_NO, module: Modules.UC)}: ${challan.challanNo ?? 'N/A'}',
                  name: 'Name: ${challan.citizen?.name ?? 'N/A'}',
                  status: isNotNullOrEmpty(
                    challan.applicationStatus,
                  )
                      ? getLocalizedString(
                          'UC_${challan.applicationStatus}',
                        )
                      : 'N/A',
                  subtext:
                      '${getLocalizedString(i18.challans.UC_RECIEPT_NUMBER_LABEL, module: Modules.UC)}: ${challan.receiptNumber ?? 'N/A'}',
                  statusColor: getStatusColor(
                    challan.applicationStatus ?? 'N/A',
                  ),
                  statusBackColor: getStatusBackColor(
                    challan.applicationStatus ?? 'N/A',
                  ),
                  onTap: () {
                    Get.toNamed(
                      AppRoutes.EMP_UC_CHALLAN_DETAILS,
                      arguments: {
                        'challan': challan,
                        'tenant': tenant,
                      },
                    );
                  },
                ).paddingOnly(bottom: 10);
              }
            },
          );
        } else if (snapshot.hasError) {
          return networkErrorPage(
            context,
            () => init(),
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
