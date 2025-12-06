import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';

Widget dropDownButton<T>(
  BuildContext context, {
  String? hinText,
  dynamic value,
  List<DropdownMenuItem<T>>? items,
  required void Function(dynamic)? onChanged,
  List<Widget> Function(BuildContext)? selectedItemBuilder,
  double hintFontSize = 16,
  validator,
  Widget? prefixIcon,
  Color? suffixIconColor,
  BorderSide? focusBorder,
  Color focusBorderColor = BaseConfig.redColor1,
  InputBorder? border,
  EdgeInsetsGeometry? contentPadding = const EdgeInsets.fromLTRB(13, 4, 13, 0),
  double? radius,
}) {
  return DropdownButtonFormField<T>(
    items: items,
    value: value,
    onChanged: onChanged,
    validator: validator,
    iconEnabledColor: BaseConfig.greyColor1,
    borderRadius: BorderRadius.circular(radius ?? 15.r),
    dropdownColor: BaseConfig.mainBackgroundColor,
    style: const TextStyle(color: BaseConfig.textColor),
    selectedItemBuilder: selectedItemBuilder,
    decoration: InputDecoration(
      filled: true,
      fillColor: BaseConfig.mainBackgroundColor,
      hintText: hinText,
      contentPadding: contentPadding,
      prefixIcon: prefixIcon,
      suffixIconColor: suffixIconColor,
      border: border,
      hintStyle: TextStyle(
        fontSize: hintFontSize,
        color: BaseConfig.greyColor1,
      ),
      enabledBorder: OutlineInputBorder(
        borderSide: const BorderSide(
          color: Colors.black54,
          width: 1.5,
        ),
        borderRadius: BorderRadius.circular(radius ?? 15.r),
      ),
      focusedBorder: OutlineInputBorder(
        borderSide: BorderSide(
          color: focusBorderColor,
        ),
        borderRadius: BorderRadius.circular(radius ?? 15.r),
      ),
      errorBorder: OutlineInputBorder(
        borderSide: BorderSide(
          color: Theme.of(context).colorScheme.error,
          width: 1,
        ),
        borderRadius: BorderRadius.circular(radius ?? 15.r),
      ),
      errorStyle: TextStyle(
        fontSize: 12.sp,
        color: Theme.of(context).colorScheme.error,
      ),
    ),
  );
}
