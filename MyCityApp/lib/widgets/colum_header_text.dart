import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/components/icon_row.dart';
import 'package:mobile_app/components/text_formfield_normal.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/widgets/required_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class ColumnHeaderText extends StatelessWidget {
  const ColumnHeaderText({
    super.key,
    required this.label,
    required this.text,
    this.icon,
    this.fontWeight,
    this.fontWeightHeader,
    this.textSize,
    this.textColor = BaseConfig.textColor,
  });

  final String label;
  final String text;
  final IconData? icon;
  final FontWeight? fontWeight, fontWeightHeader;
  final double? textSize;
  final Color textColor;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SmallTextNotoSans(
          text: label,
          fontWeight: fontWeightHeader ?? FontWeight.w600,
        ),
        IconRow(
          text: text,
          icon: icon,
          fontWeight: fontWeight ?? FontWeight.w400,
          textSize: textSize ?? 12.sp,
          textColor: textColor,
        ),
      ],
    );
  }
}

class ColumnHeaderTextField extends StatelessWidget {
  const ColumnHeaderTextField({
    super.key,
    required this.label,
    required this.hintText,
    this.icon,
    this.fontWeight = FontWeight.w600,
    this.textSize,
    this.textColor = BaseConfig.textColor,
    this.controller,
    this.textInputAction,
    this.isRequired = false,
    this.onChange,
    this.onSave,
    this.suffixIcon,
    this.prefixIcon,
    this.keyboardType,
    this.validator,
    this.inputFormatters,
    this.readOnly = false,
  });

  final String label;
  final String hintText;
  final IconData? icon;
  final FontWeight fontWeight;
  final double? textSize;
  final Color textColor;
  final TextEditingController? controller;
  final TextInputAction? textInputAction;
  final bool isRequired;
  final Function(String)? onChange;
  final Function(String?)? onSave;
  final Widget? prefixIcon;
  final Widget? suffixIcon;
  final TextInputType? keyboardType;
  final String? Function(String?)? validator;
  final List<TextInputFormatter>? inputFormatters;
  final bool readOnly;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        RequiredText(
          text: label,
          fontWeight: fontWeight,
          required: isRequired,
        ),
        textFormFieldNormal(
          context,
          hintText,
          controller: controller,
          textInputAction: textInputAction,
          onChange: onChange,
          onSave: onSave,
          suffixIcon: suffixIcon,
          prefixIcon: prefixIcon,
          keyboardType: keyboardType,
          validator: validator,
          inputFormatters: inputFormatters,
          readOnly: readOnly,
        ),
      ],
    );
  }
}

class ColumnHeaderRadioButton extends StatelessWidget {
  const ColumnHeaderRadioButton({
    super.key,
    required this.label,
    required this.options,
    this.icon,
    this.fontWeight = FontWeight.w600,
    this.textSize,
    this.textColor = BaseConfig.textColor,
    this.selectedValue,
    required this.onChanged,
    this.isRequired = false,
    this.module = Modules.COMMON,
  });

  final String label;
  final List<String> options;
  final IconData? icon;
  final FontWeight fontWeight;
  final double? textSize;
  final Color textColor;
  final String? selectedValue;
  final ValueChanged<String?> onChanged;
  final bool isRequired;
  final Modules module;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        RequiredText(
          text: label,
          fontWeight: fontWeight,
          required: isRequired,
        ),
        Wrap(
          spacing: 10.0,
          alignment: WrapAlignment.start,
          children: options.map((option) {
            return Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Radio<String>(
                  value: option,
                  groupValue: selectedValue,
                  activeColor: BaseConfig.appThemeColor1,
                  onChanged: onChanged,
                ),
                Text(getLocalizedString(option, module: module)),
              ],
            );
          }).toList(),
        ),
      ],
    );
  }
}

class ColumnHeaderDropdownSearch extends StatelessWidget {
  const ColumnHeaderDropdownSearch({
    super.key,
    required this.label,
    required this.options,
    this.fontWeight = FontWeight.w600,
    this.textSize,
    this.textColor = BaseConfig.textColor,
    this.selectedValue,
    required this.onChanged,
    this.isRequired = false,
    this.module = Modules.COMMON,
    this.trailingIcon = Icons.check,
    this.enableLocal = false,
    this.validator,
  });

  final String label;
  final List<String> options;
  final IconData? trailingIcon;
  final FontWeight fontWeight;
  final double? textSize;
  final Color textColor;
  final String? selectedValue;
  final ValueChanged<String?> onChanged;
  final bool isRequired;
  final Modules module;
  final bool enableLocal;
  final String? Function(String?)? validator;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        RequiredText(
          text: label,
          fontWeight: fontWeight,
          required: isRequired,
        ),
        DropdownSearch<String>(
          key: key,
          items: (filter, infiniteScrollProps) => options,
          selectedItem: selectedValue,
          decoratorProps: DropDownDecoratorProps(
            decoration: InputDecoration(
              suffixIconColor: BaseConfig.appThemeColor1,
              contentPadding: EdgeInsets.symmetric(
                vertical: 15.h,
                horizontal: 10.w,
              ),
              fillColor: BaseConfig.mainBackgroundColor,
              border: OutlineInputBorder(
                borderSide: const BorderSide(
                  width: 1.5,
                ),
                borderRadius: BorderRadius.circular(8.r),
              ),
              enabledBorder: OutlineInputBorder(
                borderSide: BorderSide(
                  width: 1.5,
                  color: Theme.of(context)
                      .colorScheme
                      .secondary
                      .withValues(alpha: 0.4),
                ),
                borderRadius: BorderRadius.circular(8.r),
              ),
              focusedBorder: OutlineInputBorder(
                borderSide: const BorderSide(
                  width: 1.5,
                  color: BaseConfig.appThemeColor1,
                ),
                borderRadius: BorderRadius.circular(8.r),
              ),
              errorBorder: OutlineInputBorder(
                borderSide: const BorderSide(
                  width: 1.5,
                  color: BaseConfig.redColor,
                ),
                borderRadius: BorderRadius.circular(8.r),
              ),
              errorStyle: TextStyle(
                fontSize: 12.sp,
                color: Theme.of(context).colorScheme.error,
              ),
            ),
          ),
          compareFn: (item, selectedItem) => enableLocal
              ? getLocalizedString(
                    item,
                    module: item.contains('ADVT') ? Modules.UC : module,
                  ) ==
                  getLocalizedString(
                    selectedItem,
                    module: selectedItem.contains('ADVT') ? Modules.UC : module,
                  )
              : item == selectedItem,
          dropdownBuilder: (context, selectedItem) {
            final isUc = selectedItem?.contains('ADVT') ?? false;
            return SmallTextNotoSans(
              text: enableLocal
                  ? getLocalizedString(
                      selectedItem,
                      module: isUc ? Modules.UC : module,
                    )
                  : selectedItem ?? '',
              size: 14.sp,
            );
          },
          popupProps: PopupProps.menu(
            showSearchBox: true,
            itemBuilder: (context, item, isDisabled, isSelected) {
              final isUc = item.contains('ADVT');
              return ListTile(
                trailing: trailingIcon != null && item == selectedValue
                    ? CircleAvatar(
                        radius: 14.w,
                        backgroundColor:
                            BaseConfig.appThemeColor1.withValues(alpha: 0.1),
                        child: Icon(
                          trailingIcon,
                          color: BaseConfig.appThemeColor1,
                          size: 20.sp,
                        ),
                      )
                    : null,
                title: SmallTextNotoSans(
                  text: enableLocal
                      ? getLocalizedString(
                          item,
                          module: isUc ? Modules.UC : module,
                        )
                      : item,
                  color: item == selectedValue
                      ? BaseConfig.appThemeColor1
                      : BaseConfig.textColor,
                ),
                onTap: () {
                  onChanged(item);
                },
              );
            },
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
                hintText: 'Search $label',
                border: OutlineInputBorder(
                  borderSide: const BorderSide(
                    width: 1.5,
                    color: BaseConfig.borderColor,
                  ),
                  borderRadius: BorderRadius.circular(8.r),
                ),
                enabledBorder: OutlineInputBorder(
                  borderSide: const BorderSide(
                    width: 1.5,
                    color: BaseConfig.borderColor,
                  ),
                  borderRadius: BorderRadius.circular(8.r),
                ),
                focusedBorder: OutlineInputBorder(
                  borderSide: const BorderSide(
                    width: 1.5,
                    color: BaseConfig.appThemeColor1,
                  ),
                  borderRadius: BorderRadius.circular(8.r),
                ),
              ),
            ),
            emptyBuilder: (context, searchEntry) {
              return SizedBox(
                height: 50.h,
                child: Center(
                  child: SmallTextNotoSans(
                    text: getLocalizedString(i18.common.NO_OPTION),
                    textAlign: TextAlign.center,
                    color: BaseConfig.appThemeColor1,
                  ),
                ),
              );
            },
          ),
          onChanged: onChanged,
          validator: validator,
        ),
      ],
    );
  }
}
