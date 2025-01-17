import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/text_formfield_normal.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/controller/location_controller.dart';
import 'package:mobile_app/model/citizen/token/token.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class EmpHomeCityScreen extends StatefulWidget {
  const EmpHomeCityScreen({super.key});

  @override
  State<EmpHomeCityScreen> createState() => _EmpHomeCityScreenState();
}

class _EmpHomeCityScreenState extends State<EmpHomeCityScreen> {
  final _cityController = Get.find<CityController>();
  final _authController = Get.find<AuthController>();
  final _languageController = Get.find<LanguageController>();

  final TextEditingController _searchController = TextEditingController();
  List<Role>? _roles;
  List<Role> _filteredRoles = [];
  bool _noResultsFound = false;

  @override
  void initState() {
    super.initState();
    _languageController.getLocalizationData();
    _searchController.addListener(_filterRoles);
    _filterRoles();
  }

  @override
  void dispose() {
    _searchController.removeListener(_filterRoles);
    _searchController.dispose();
    super.dispose();
  }

  void _filterRoles() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final searchText = _searchController.text.toLowerCase();

      if (_roles == null) return;

      if (searchText.isEmpty) {
        _filteredRoles = _roles!;
        _noResultsFound = false;
      } else {
        _filteredRoles = _roles!.where((role) {
          String roleName = getLocalizedString(
            role.code!.contains('pg')
                ? i18.common.LOCATION_PREFIX
                : '${i18.common.LOCATION_PREFIX}${getTenantCode(role.tenantId!)}',
          ).toLowerCase();
          return roleName.contains(searchText);
        }).toList();

        _noResultsFound = _filteredRoles.isEmpty;
      }

      if (mounted) {
        setState(() {});
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          onPressed: () {
            Get.offAndToNamed(AppRoutes.EMP_BOTTOM_NAV);
          },
          icon: const Icon(
            Icons.arrow_back_ios,
          ),
        ),
      ),
      body: GetBuilder<LanguageController>(
        builder: (languageController) {
          return SingleChildScrollView(
            physics: const NeverScrollableScrollPhysics(),
            child: Container(
              height: Get.height,
              width: Get.width,
              padding: EdgeInsets.all(20.w),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  textFormFieldNormal(
                    context,
                    getLocalizedString(i18.common.SEARCH),
                    hintTextColor: BaseConfig.greyColor1,
                    prefixIcon: const Icon(Icons.search),
                    textInputAction: TextInputAction.search,
                    //onChange: _onSearchChanged,
                  ),
                  SizedBox(
                    height: 20.h,
                  ),
                  MediumTextNotoSans(
                    text: getLocalizedString(i18.common.SELECT_CITY),
                    fontWeight: FontWeight.w700,
                    size: 16.h,
                  ),
                  SizedBox(
                    height: 8.h,
                  ),
                  SizedBox(
                    height: 1.25.sw,
                    width: Get.width,
                    child: _buildView(
                      _authController.token?.userRequest?.roles!
                          .removeDuplicates(),
                      o,
                    ),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildView(List<Role>? roles, Orientation o) {
    _roles = roles;
    _filterRoles();

    return SizedBox(
      height: Get.height * 0.7.h,
      child: Column(
        children: [
          if (_noResultsFound)
            SizedBox(
              height: Get.height * 0.4,
              child: const Center(child: Text('No Cities Found')),
            ),
          if (!_noResultsFound) ...[
            Expanded(
              child: ListView.builder(
                itemCount: isNotNullOrEmpty(_filteredRoles)
                    ? _filteredRoles.length
                    : roles?.length ?? 0,
                shrinkWrap: true,
                itemBuilder: (context, index) {
                  final role = isNotNullOrEmpty(_filteredRoles)
                      ? _filteredRoles[index]
                      : roles?[index];
                  return role != null
                      ? _buildTenant(role, o)
                      : const SizedBox.shrink();
                },
              ),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildTenant(Role role, Orientation o) {
    var isSelected = _cityController.empSelectedCity.value == role.tenantId;
    return Column(
      children: [
        ListTile(
          minVerticalPadding: 2,
          dense: true,
          title: MediumTextNotoSans(
            text: role.code!.contains('pg')
                ? getLocalizedString(i18.common.LOCATION_PREFIX)
                : getLocalizedString(
                    '${i18.common.LOCATION_PREFIX}${getTenantCode(role.tenantId!)}',
                  ),
            fontWeight: FontWeight.w600,
            size: o == Orientation.portrait ? 12.sp : 7.sp,
            color:
                isSelected ? BaseConfig.appThemeColor1 : BaseConfig.greyColor4,
          ),
          contentPadding: EdgeInsets.zero,
          trailing: isSelected
              ? Container(
                  width: 10.w,
                  height: 10.h,
                  decoration: const BoxDecoration(
                    shape: BoxShape.circle,
                    color: BaseConfig.appThemeColor1, // Dot color
                  ),
                )
              : null,
          onTap: () async {
            _cityController.empSelectedCity.value = role.tenantId!;

            var tenant = _languageController.mdmsResTenant.tenants!
                .firstWhere((tenant) => tenant.code == role.tenantId!);

            await _cityController.setEmpSelectedCity(tenant);
          },
        ),
        const Divider(),
      ],
    );
  }
}
