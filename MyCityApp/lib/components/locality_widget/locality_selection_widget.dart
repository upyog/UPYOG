import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/locality_controller.dart';
import 'package:mobile_app/model/common/locality/locality_model.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/small_text.dart';

class LocalitySelectionWidget extends StatefulWidget {
  final bool isMultiple;
  final String? Function(Boundary?)? validatorSingle;
  final String? Function(List<Boundary>?)? validatorList;

  const LocalitySelectionWidget({
    super.key,
    this.isMultiple = true,
    this.validatorSingle,
    this.validatorList,
  });

  @override
  State<LocalitySelectionWidget> createState() =>
      _LocalitySelectionWidgetState();
}

class _LocalitySelectionWidgetState extends State<LocalitySelectionWidget> {
  final _localityController = Get.find<LocalityController>();
  final _authController = Get.find<AuthController>();

  final border = OutlineInputBorder(
    borderRadius: BorderRadius.circular(12.r),
    borderSide: const BorderSide(
      color: BaseConfig.borderColor,
      width: 2.0,
    ),
  );

  @override
  Widget build(BuildContext context) {
    return Obx(
      () => _localityController.isLoading.value
          ? showCircularIndicator()
          : widget.isMultiple
              ? _buildMultiSelectionDropdown()
              : _buildSingleSelectionDropdown(),
    );
  }

  Widget _buildSingleSelectionDropdown() {
    return DropdownSearch<Boundary>(
      items: (filter, infiniteScrollProps) =>
          _localityController.locality?.tenantBoundary?.firstOrNull?.boundary
              ?.toList() ??
          [],
      selectedItem: _localityController.selectedLocalityList.isNotEmpty
          ? _localityController.selectedLocalityList.first
          : null,
      decoratorProps: DropDownDecoratorProps(
        decoration: InputDecoration(
          focusedBorder: border,
          enabledBorder: border,
          hintText: getLocalizedString(i18.inbox.LOCALITY),
          contentPadding: EdgeInsets.symmetric(
            horizontal: 15.w,
            vertical: 15.h,
          ),
          border: border,
          errorStyle: TextStyle(
            fontSize: 12.sp,
            color: Theme.of(context).colorScheme.error,
          ),
        ),
      ),
      onChanged: (Boundary? value) {
        if (value != null) {
          _localityController.selectedLocalityList.value = [value];
        }
      },
      dropdownBuilder: (context, selectedItem) {
        if (selectedItem == null) {
          return Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              SizedBox(width: 10.w),
              SmallTextNotoSans(
                text: getLocalizedString(i18.inbox.LOCALITY),
              ),
            ],
          );
        }
        return SmallTextNotoSans(
          text: getLocalizedString(
            (_authController.token!.userRequest!.tenantId!.contains(".")
                    ? '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId!.split('.').last}_REVENUE_${selectedItem.code}'
                    : '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId}_REVENUE_${selectedItem.code}')
                .toUpperCase(),
          ),
          fontWeight: FontWeight.w400,
        );
      },
      popupProps: PopupProps.menu(
        fit: FlexFit.tight,
        showSearchBox: true,
        containerBuilder: (context, popupWidget) {
          return Container(
            color: BaseConfig.mainBackgroundColor,
            child: popupWidget,
          );
        },
        constraints: BoxConstraints(
          maxHeight: 300.h,
        ),
        searchFieldProps: TextFieldProps(
          decoration: InputDecoration(
            hintText: getLocalizedString(i18.common.SEARCH),
            border: border,
            enabledBorder: border,
            focusedBorder: border,
          ),
          padding: EdgeInsets.symmetric(
            horizontal: 10.w,
            vertical: 16.h,
          ),
        ),
        itemBuilder: (context, item, isDisabled, isSelected) {
          final locality = _localityController
              .locality?.tenantBoundary?.firstOrNull?.boundary
              ?.firstWhere(
            (element) => element.code == item.code,
            orElse: () => Boundary(),
          );

          return ListTile(
            title: SmallTextNotoSans(
              text: getLocalizedString(
                (_authController.token!.userRequest!.tenantId!.contains(".")
                        ? '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId!.split('.').last}_REVENUE_${locality!.code}'
                        : '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId}_REVENUE_${locality!.code}')
                    .toUpperCase(),
              ),
              fontWeight: FontWeight.w400,
            ),
          );
        },
      ),
      filterFn: (item, filter) {
        final localityString = getLocalizedString(
          (_authController.token!.userRequest!.tenantId!.contains(".")
                  ? '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId!.split('.').last}_REVENUE_${item.code}'
                  : '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId}_REVENUE_${item.code}')
              .toUpperCase(),
        );

        return localityString.toLowerCase().contains(filter.toLowerCase());
      },
      validator: widget.validatorSingle,
    );
  }

  Widget _buildMultiSelectionDropdown() {
    return DropdownSearch<Boundary>.multiSelection(
      items: (filter, infiniteScrollProps) =>
          _localityController.locality?.tenantBoundary?.firstOrNull?.boundary
              ?.toList() ??
          [],
      selectedItems: _localityController.selectedLocalityList,
      decoratorProps: DropDownDecoratorProps(
        decoration: InputDecoration(
          focusedBorder: border,
          enabledBorder: border,
          hintText: getLocalizedString(i18.inbox.LOCALITY),
          contentPadding: EdgeInsets.symmetric(
            horizontal: 10.w,
            vertical: 10.h,
          ),
          border: border,
        ),
      ),
      onChanged: (value) {
        _localityController.selectedLocalityList.value = value;
      },
      dropdownBuilder: (context, selectedItems) {
        if (selectedItems.isEmpty) {
          return Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              SizedBox(width: 10.w),
              SmallTextNotoSans(
                text: getLocalizedString(i18.inbox.LOCALITY),
              ),
            ],
          );
        }

        List<Widget> selectedChips = selectedItems.map((selectedItem) {
          var locality = _localityController
              .locality?.tenantBoundary?.firstOrNull?.boundary
              ?.firstWhere(
            (element) =>
                getLocalizedString(
                  (_authController.token!.userRequest!.tenantId!.contains(".")
                          ? '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId!.split('.').last}_REVENUE_${element.code}'
                          : '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId}_REVENUE_${element.code}')
                      .toUpperCase(),
                ) ==
                getLocalizedString(
                  (_authController.token!.userRequest!.tenantId!.contains(".")
                          ? '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId!.split('.').last}_REVENUE_${selectedItem.code}'
                          : '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId}_REVENUE_${selectedItem.code}')
                      .toUpperCase(),
                ),
            orElse: () => Boundary(),
          );

          return Chip(
            side: const BorderSide(
              color: BaseConfig.borderColor,
              width: 1.0,
            ),
            backgroundColor: BaseConfig.mainBackgroundColor,
            label: SmallTextNotoSans(
              text: getLocalizedString(
                (_authController.token!.userRequest!.tenantId!.contains(".")
                        ? '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId!.split('.').last}_REVENUE_${locality!.code}'
                        : '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId}_REVENUE_${locality!.code}')
                    .toUpperCase(),
              ),
              fontWeight: FontWeight.w400,
            ),
          );
        }).toList();

        return Wrap(
          spacing: 8.w,
          children: selectedChips,
        );
      },
      popupProps: PopupPropsMultiSelection.menu(
        fit: FlexFit.tight,
        showSearchBox: true,
        containerBuilder: (context, popupWidget) {
          return Container(
            color: BaseConfig.mainBackgroundColor,
            child: popupWidget,
          );
        },
        constraints: BoxConstraints(
          maxHeight: 300.h,
        ),
        searchFieldProps: TextFieldProps(
          decoration: InputDecoration(
            hintText: getLocalizedString(i18.common.SEARCH),
            border: border,
            enabledBorder: border,
            focusedBorder: border,
          ),
          padding: EdgeInsets.symmetric(
            horizontal: 10.w,
            vertical: 16.h,
          ),
        ),
        itemBuilder: (context, item, isDisabled, isSelected) {
          var locality = _localityController
              .locality?.tenantBoundary?.firstOrNull?.boundary
              ?.firstWhere(
            (element) =>
                getLocalizedString(
                  (_authController.token!.userRequest!.tenantId!.contains(".")
                          ? '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId!.split('.').last}_REVENUE_${element.code}'
                          : '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId}_REVENUE_${element.code}')
                      .toUpperCase(),
                ) ==
                getLocalizedString(
                  (_authController.token!.userRequest!.tenantId!.contains(".")
                          ? '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId!.split('.').last}_REVENUE_${item.code}'
                          : '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId}_REVENUE_${item.code}')
                      .toUpperCase(),
                ),
          );

          return ListTile(
            title: SmallTextNotoSans(
              text: getLocalizedString(
                (_authController.token!.userRequest!.tenantId!.contains(".")
                        ? '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId!.split('.').last}_REVENUE_${locality!.code}'
                        : '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId}_REVENUE_${locality!.code}')
                    .toUpperCase(),
              ),
              fontWeight: FontWeight.w400,
            ),
          );
        },
      ),
      filterFn: (locality, filter) {
        return getLocalizedString(
          (_authController.token!.userRequest!.tenantId!.contains(".")
                  ? '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId!.split('.').last}_REVENUE_${locality.code}'
                  : '${BaseConfig.STATE_TENANT_ID}_${_authController.token!.userRequest!.tenantId}_REVENUE_${locality.code}')
              .toUpperCase(),
        ).toLowerCase().contains(filter.toLowerCase());
      },
      validator: widget.validatorList,
    );
  }
}
