import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:mobile_app/config/base_config.dart';

class TextFormFieldApp extends StatelessWidget {
  const TextFormFieldApp({
    super.key,
    this.hintText,
    this.label,
    this.onChange,
    this.controller,
    this.textInputAction,
    this.keyboardType,
    this.obscureText = false,
    this.suffixIcon,
    this.prefixIcon,
    this.prefixIconColor = BaseConfig.textColor,
    this.suffixIconColor = BaseConfig.textColor,
    this.focusNode,
    this.fillColor = BaseConfig.mainBackgroundColor,
    this.hintTextColor = BaseConfig.textColor,
    this.focusBorderColor = BaseConfig.textColor,
    this.autovalidateMode,
    this.validator,
    this.maxLine,
    this.maxLength,
    this.minLines,
    this.opacity = 0.4,
    this.borderNone = false,
    this.readOnly = false,
    this.autoFocus = false,
    this.borderRadius,
    this.inputFormatters,
    this.onTap,
    this.contentPadding = const EdgeInsets.fromLTRB(13, 4, 13, 0),
    this.padding = const EdgeInsets.fromLTRB(0, 15, 0, 10),
    this.onSubmitted,
    this.hintFontSize,
    this.radius,
    this.borderColor = BaseConfig.greyColor1,
  });

  final String? hintText;
  final Widget? label;
  final Function(String)? onChange;
  final Function(String)? onSubmitted;
  final Function()? onTap;
  final TextEditingController? controller;
  final TextInputAction? textInputAction;
  final TextInputType? keyboardType;
  final bool obscureText;
  final Widget? suffixIcon;
  final Widget? prefixIcon;
  final Color prefixIconColor;
  final Color suffixIconColor;
  final FocusNode? focusNode;
  final Color fillColor;
  final Color hintTextColor;
  final Color focusBorderColor;
  final AutovalidateMode? autovalidateMode;
  final String? Function(String?)? validator;
  final int? maxLine;
  final int? maxLength;
  final int? minLines;
  final double opacity;
  final bool borderNone;
  final bool readOnly;
  final bool autoFocus;
  final BorderRadius? borderRadius;
  final List<TextInputFormatter>? inputFormatters;
  final EdgeInsetsGeometry? contentPadding;
  final EdgeInsetsGeometry? padding;
  final double? hintFontSize;
  final double? radius;
  final Color borderColor;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: padding,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(radius ?? 15.r),
        border: Border.all(
          color: borderColor,
        ),
      ),
      alignment: Alignment.center,
      child: TextFormField(
        autofocus: autoFocus,
        onTap: onTap,
        controller: controller,
        keyboardType: keyboardType,
        textInputAction: textInputAction,
        obscureText: obscureText,
        onChanged: onChange,
        onFieldSubmitted: onSubmitted,
        focusNode: focusNode,
        textCapitalization: TextCapitalization.sentences,
        cursorColor: BaseConfig.appThemeColor1,
        style: TextStyle(color: hintTextColor),
        autovalidateMode: autovalidateMode,
        validator: validator,
        readOnly: readOnly,
        inputFormatters: inputFormatters,
        // maxLength: maxLength,
        maxLines: maxLine,
        minLines: minLines,
        decoration: InputDecoration(
          filled: true,
          fillColor: fillColor.withValues(alpha: opacity),
          label: label,
          labelStyle: GoogleFonts.notoSans().copyWith(
                fontSize: hintFontSize ?? 14.sp,
                fontWeight: FontWeight.w400,
                color: BaseConfig.greyColor3,
              ),
          suffixIcon: suffixIcon,
          prefixIcon: prefixIcon,
          prefixIconColor: prefixIconColor,
          suffixIconColor: suffixIconColor,
          hintText: hintText,
          hintStyle: GoogleFonts.notoSans().copyWith(
                fontSize: 14.sp,
                fontWeight: FontWeight.w400,
                color: BaseConfig.greyColor3,
              ),
          contentPadding: contentPadding,
          errorStyle: const TextStyle(
            fontSize: 14,
            color: Colors.red,
            height: 0.3,
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(radius ?? 15.0),
            borderSide: const BorderSide(
              color: Colors.transparent,
            ),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(radius ?? 15.0),
            borderSide: const BorderSide(
              color: Colors.transparent,
              width: 2.0,
            ),
          ),
        ),
      ),
    );
  }
}
