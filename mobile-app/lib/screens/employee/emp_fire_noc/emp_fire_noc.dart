import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/fire_noc_controller.dart';
import 'package:mobile_app/controller/locality_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/timeline/timeline.dart' as timeLine;
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class EmpFireNocScreen extends StatefulWidget {
  const EmpFireNocScreen({super.key});

  @override
  State<EmpFireNocScreen> createState() => _EmpFireNocScreenState();
}

class _EmpFireNocScreenState extends State<EmpFireNocScreen> {
  final _authController = Get.find<AuthController>();
  final _commonController = Get.find<CommonController>();
  final _fireNocController = Get.find<FireNocController>();
  final _ptController = Get.find<PropertiesTaxController>();
  final _localityController = Get.find<LocalityController>();
  final searchController = TextEditingController();
  final pageController = PageController(viewportFraction: 1.05, keepPage: true);
  var isSelected = false;
  late TenantTenant tenantCity;
  var isLoading = false.obs;

  @override
  void initState() {
    _init();
    super.initState();
  }

  _init() async {
    await _commonController.fetchLabels(modules: Modules.NOC);
    isLoading.value = true;
    _fireNocController.setDefaultLimit();
    await _fetchInbox();
    isLoading.value = false;
  }

  Future<void> _fetchInbox() async {
    tenantCity = await getCityTenant();
    await _fireNocController.getFireNocApplicationsEmp(
      token: _authController.token!.accessToken!,
      tenantId: '${tenantCity.code}',
      businessService: ModulesEmp.FIRE_NOC.name.toUpperCase(),
    );
  }

  //Apply search filters
  Future<void> applyFilter() async {
    await _ptController.getEmpPtInboxApplications(
      token: _authController.token!.accessToken!,
      tenantId: '${tenantCity.code}',
      isFilter: _localityController.selectedLocalityList.isEmpty ? false : true,
      locality: _localityController.selectedLocalityList.isEmpty
          ? null
          : _localityController.getSelectedLocalityCode(),
    );
    dPrint('Apply filter closed!!!');
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Obx(
          () => Wrap(
            children: [
              Text(getLocalizedString(i18.common.EMP_FIRE_NOC)),
              Text(
                ' (${_fireNocController.length.value})',
              ),
            ],
          ),
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: RefreshIndicator(
        color: BaseConfig.appThemeColor1,
        onRefresh: _fetchInbox,
        child: SizedBox(
          height: Get.height,
          width: Get.width,
          child: SingleChildScrollView(
            child: Column(
              children: [
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
      stream: _fireNocController.streamCtrl.stream,
      builder: (context, AsyncSnapshot snapshot) {
        if (snapshot.hasData) {
          if (snapshot.data is String || snapshot.data == null) {
            return Center(
              child: Text(getLocalizedString(i18.inbox.NO_APPLICATION)),
            );
          }

          final timeLine.Timeline data = snapshot.data;

          if (snapshot.data == null || data.processInstancesList!.isEmpty) {
            return Center(
              child: Text(getLocalizedString(i18.inbox.NO_APPLICATION)),
            );
          }

          final process = data.processInstancesList
              ?.where(
                (value) =>
                    value.state?.state == InboxStatus.FIELD_INSPECTION.name,
              )
              .toList();

          return ListView.builder(
            itemCount: process!.length,
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemBuilder: (context, index) {
              final item = process[index];

              return ComplainCard(
                title: item.businessService ?? 'N/A',
                id: '${getLocalizedString(i18.noc.NOC_APPLICATION_NO, module: Modules.NOC)}: ${item.businessId ?? 'N/A'}',
                date:
                    '${getLocalizedString(i18.common.DATE_LABEL)}: ${item.auditDetails?.lastModifiedTime?.toCustomDateFormat() ?? 'N/A'}', //pattern: 'd/MM/yyyy'
                address: '${getLocalizedString(i18.inbox.LOCALITY)}: N/A',
                sla:
                    '${getLocalizedString(i18.common.EMP_SLA)}: ${getSlaStatus(item.businesssServiceSla)}',
                status: getLocalizedString(
                  'COMMON_${item.state?.state}'.toUpperCase(),
                ),
                statusColor: getStatusColor(
                  '${item.state?.state}',
                ),
                statusBackColor: getStatusBackColor(
                  '${item.state?.state}',
                ),
                o: o,
                onTap: () {
                  //Go to details page
                  Get.toNamed(
                    AppRoutes.EMP_FIRE_NOC_DETAILS,
                    arguments: item,
                  );
                },
              ).paddingOnly(bottom: 10);
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

  // void _openFilterBottomSheet(bool isSelected, Orientation o) {
  //   Get.bottomSheet(
  //     StatefulBuilder(
  //       builder: (context, setState) {
  //         return FilterBottomSheet(
  //           title: getLocalizedString(i18.inbox.FILTER_BY),
  //           content: Column(
  //             crossAxisAlignment: CrossAxisAlignment.start,
  //             children: [
  //               MediumTextNotoSans(
  //                 text: '${getLocalizedString(i18.inbox.LOCALITY)}:',
  //                 fontWeight: FontWeight.w500,
  //                 size: o == Orientation.portrait ? 14.sp : 8.sp,
  //               ),

  //               SizedBox(height: 16.h),

  //               const LocalitySelectionWidget(),
  //             ],
  //           ),
  //           onApply: () {
  //             // applyFilter();
  //             Get.back();
  //           },
  //           onCancel: () {
  //             Get.back();
  //           },
  //           orientation: o,
  //         );
  //       },
  //     ),
  //     isScrollControlled: true,
  //     isDismissible: false,
  //     enableDrag: false,
  //   );
  // }
}
