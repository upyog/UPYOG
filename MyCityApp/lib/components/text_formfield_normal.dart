import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:mobile_app/config/base_config.dart';

Widget textFormFieldNormal(
  BuildContext context,
  String hintText, {
  String? initialValue,
  Widget? label,
  Function(String)? onChange,
  Function(String?)? onSave,
  TextEditingController? controller,
  TextInputAction? textInputAction,
  TextInputType? keyboardType,
  bool obscureText = false,
  bool isFilled = true,
  Color hintTextColor = BaseConfig.textColor,
  Color focusBorderColor = BaseConfig.appThemeColor1,
  Color fillColor = Colors.white,
  Color cursorColor = BaseConfig.appThemeColor1,
  AutovalidateMode? autovalidateMode,
  validator,
  int? maxLine,
  int? maxLength,
  int? minLines,
  double opacity = 0.4,
  final double? fontSize,
  bool borderNone = false,
  bool readOnly = false,
  BorderRadius? borderRadius,
  Widget? suffixIcon,
  Widget? prefixIcon,
  inputFormatters,
}) {
  return TextFormField(
    initialValue: initialValue,
    controller: controller,
    keyboardType: keyboardType,
    textInputAction: textInputAction,
    obscureText: obscureText,
    onChanged: onChange,
    textCapitalization: TextCapitalization.sentences,
    enableInteractiveSelection: true,
    cursorColor: cursorColor,
    style: TextStyle(
      color: hintTextColor,
      fontSize: fontSize ?? 14.sp,
    ),
    autovalidateMode: autovalidateMode,
    validator: validator,
    readOnly: readOnly,
    inputFormatters: inputFormatters,
    maxLines: maxLine,
    minLines: minLines,
    onSaved: onSave,
    decoration: InputDecoration(
      filled: isFilled,
      fillColor: fillColor,
      label: label,
      hintText: hintText,
      suffixIcon: suffixIcon,
      prefixIcon: prefixIcon,
      contentPadding: const EdgeInsets.fromLTRB(20, 15, 20, 15),
      hintStyle: GoogleFonts.notoSans.call().copyWith(
            color: hintTextColor,
          ),
      border: OutlineInputBorder(
        borderSide: borderNone ? BorderSide.none : const BorderSide(width: 1.5),
        borderRadius: borderRadius ?? BorderRadius.circular(8.r),
      ),
      enabledBorder: OutlineInputBorder(
        borderSide: BorderSide(
          color: Theme.of(context)
              .colorScheme
              .secondary
              .withValues(alpha: opacity),
        ),
        borderRadius: borderRadius ?? BorderRadius.circular(8.r),
      ),
      focusedBorder: OutlineInputBorder(
        borderSide: BorderSide(
          color: focusBorderColor,
        ),
        borderRadius: borderRadius ?? BorderRadius.circular(8.r),
      ),
      errorBorder: OutlineInputBorder(
        borderSide: BorderSide(
          color: Theme.of(context).colorScheme.error,
          width: 1,
        ),
        borderRadius: borderRadius ?? BorderRadius.circular(8.r),
      ),
      errorStyle: TextStyle(
        fontSize: 12.sp,
        color: Theme.of(context).colorScheme.error,
      ),
    ),
  );
}
