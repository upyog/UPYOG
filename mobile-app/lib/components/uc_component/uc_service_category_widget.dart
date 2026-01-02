import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/challan_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/model/common/copy_model.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/small_text.dart';

final _border = OutlineInputBorder(
  borderRadius: BorderRadius.circular(12.r),
  borderSide: const BorderSide(
    color: BaseConfig.borderColor,
    width: 2.0,
  ),
);

class UcServiceCategoryWidget extends StatelessWidget {
  final String hintText;
  final bool isMultiple;
  final String? Function(CopyModel?)? validatorSingle;
  final String? Function(List<CopyModel>?)? validatorList;

  UcServiceCategoryWidget({
    super.key,
    required this.hintText,
    this.isMultiple = true,
    this.validatorSingle,
    this.validatorList,
  });

  final _challansController = Get.find<ChallansController>();

  @override
  Widget build(BuildContext context) {
    return Obx(
      () => _challansController.isLoading.value
          ? showCircularIndicator()
          : isMultiple
              ? _buildMultiSelectionDropdown()
              : _buildSingleSelectionDropdown(context),
    );
  }

  Widget _buildSingleSelectionDropdown(BuildContext context) {
    return DropdownSearch<CopyModel>(
      items: _challansController.uniqueBusinessServices
          .map(
            (businessService) => CopyModel(
              originalCode: businessService ?? '',
              replaceCode:
                  businessService?.replaceAll('.', '_').toUpperCase() ?? '',
            ),
          )
          .toList(),
      selectedItem: _challansController.selectedServiceCategory.isNotEmpty
          ? _challansController.selectedServiceCategory.first
          : null,
      dropdownDecoratorProps: DropDownDecoratorProps(
        dropdownSearchDecoration: InputDecoration(
          focusedBorder: _border,
          enabledBorder: _border,
          hintText: hintText,
          contentPadding: EdgeInsets.symmetric(
            horizontal: 15.w,
            vertical: 15.h,
          ),
          border: _border,
          errorStyle: TextStyle(
            fontSize: 12.sp,
            color: Theme.of(context).colorScheme.error,
          ),
        ),
      ),
      onChanged: (CopyModel? value) {
        if (value != null) {
          _challansController.selectedServiceCategory.value = [value];
        }
      },
      dropdownBuilder: (context, selectedItem) {
        if (selectedItem == null) {
          return Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              SizedBox(width: 10.w),
              SmallSelectableTextNotoSans(
                text: hintText,
              ),
            ],
          );
        }
        return isNotNullOrEmpty(selectedItem.replaceCode)
            ? SmallSelectableTextNotoSans(
                text: getLocalizedString(
                  '${i18.challans.UC_BUSINESS_SERVICE}${selectedItem.replaceCode}',
                ),
                fontWeight: FontWeight.w400,
              )
            : const SizedBox.shrink();
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
            border: _border,
            enabledBorder: _border,
            focusedBorder: _border,
          ),
          padding: EdgeInsets.symmetric(
            horizontal: 10.w,
            vertical: 16.h,
          ),
        ),
        itemBuilder: (context, item, isSelected) {
          return ListTile(
            title: isNotNullOrEmpty(item.replaceCode)
                ? SmallSelectableTextNotoSans(
                    text: getLocalizedString(
                      '${i18.challans.UC_BUSINESS_SERVICE}${item.replaceCode}',
                    ),
                    fontWeight: FontWeight.w400,
                  )
                : const SizedBox.shrink(),
          );
        },
      ),
      filterFn: (item, filter) {
        if (filter.isEmpty) {
          return true;
        }
        return item.originalCode!.toLowerCase().contains(filter.toLowerCase());
      },
      validator: validatorSingle,
    );
  }

  Widget _buildMultiSelectionDropdown() {
    return DropdownSearch<CopyModel>.multiSelection(
      items: _challansController.uniqueBusinessServices
          .map(
            (businessService) => CopyModel(
              originalCode: businessService ?? '',
              replaceCode:
                  businessService?.replaceAll('.', '_').toUpperCase() ?? '',
            ),
          )
          .toList(),
      selectedItems: _challansController.selectedServiceCategory,
      dropdownDecoratorProps: DropDownDecoratorProps(
        dropdownSearchDecoration: InputDecoration(
          focusedBorder: _border,
          enabledBorder: _border,
          hintText: hintText,
          contentPadding: EdgeInsets.symmetric(
            horizontal: 10.w,
            vertical: 10.h,
          ),
          border: _border,
        ),
      ),
      onChanged: (value) {
        _challansController.selectedServiceCategory.assignAll(value);
      },
      dropdownBuilder: (context, selectedItems) {
        if (!isNotNullOrEmpty(selectedItems)) {
          return Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              SizedBox(width: 10.w),
              SmallSelectableTextNotoSans(
                text: hintText,
              ),
            ],
          );
        }

        List<Widget> selectedChips = selectedItems.map((item) {
          return Chip(
            side: const BorderSide(
              color: BaseConfig.borderColor,
              width: 1.0,
            ),
            backgroundColor: BaseConfig.mainBackgroundColor,
            label: SmallSelectableTextNotoSans(
              text: getLocalizedString(
                '${i18.challans.UC_BUSINESS_SERVICE}${item.replaceCode}',
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
            border: _border,
            enabledBorder: _border,
            focusedBorder: _border,
          ),
          padding: EdgeInsets.symmetric(
            horizontal: 10.w,
            vertical: 16.h,
          ),
        ),
        emptyBuilder: (context, searchEntry) {
          return SizedBox(
            height: 50.h,
            child: Center(
              child: SmallSelectableTextNotoSans(
                text: getLocalizedString(i18.common.NO_OPTION),
                textAlign: TextAlign.center,
                color: BaseConfig.appThemeColor1,
              ),
            ),
          );
        },
        itemBuilder: (context, item, isSelected) {
          return ListTile(
            title: isNotNullOrEmpty(item.replaceCode)
                ? SmallSelectableTextNotoSans(
                    text: getLocalizedString(
                      '${i18.challans.UC_BUSINESS_SERVICE}${item.replaceCode}',
                    ),
                    fontWeight: FontWeight.w400,
                  )
                : const SizedBox.shrink(),
          );
        },
      ),
      filterFn: (locality, filter) {
        final localized = getLocalizedString(
          '${i18.challans.UC_BUSINESS_SERVICE}${locality.replaceCode}',
        );

        return localized.toLowerCase().contains(filter.toLowerCase());
      },
      validator: validatorList,
    );
  }
}
