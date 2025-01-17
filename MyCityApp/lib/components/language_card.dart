import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class LanguageCard extends StatelessWidget {
  // final String text;
  // final String value;
  final Languages language;
  final double widthRect;
  final double cPadding;
  final double textSize;
  final EdgeInsetsGeometry? margin;
  final Function()? onTap;

  const LanguageCard({
    super.key,
    required this.language,
    required this.widthRect,
    required this.cPadding,
    required this.textSize,
    this.margin,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: margin,
        width: widthRect,
        padding: EdgeInsets.all(cPadding),
        decoration: BoxDecoration(
          border: Border.all(color: BaseConfig.borderColor),
          color: language.isSelected
              ? BaseConfig.appThemeColor1
              : Colors.transparent,
          borderRadius: BorderRadius.circular(10),
        ),
        alignment: Alignment.center,
        child: MediumTextNotoSans(
          text: '${language.label}'.capitalize ?? '',
          fontWeight: FontWeight.w400,
          color: language.isSelected
              ? BaseConfig.mainBackgroundColor
              : BaseConfig.textColor,
          size: textSize,
        ),
      ),
    );
  }
}
