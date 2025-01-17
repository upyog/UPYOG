import 'package:flutter/material.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/utils.dart';

class TextButtonApp extends StatelessWidget {
  const TextButtonApp({
    super.key,
    this.onPressed,
    this.backgroundColor,
    this.height = 50,
    this.width,
    this.isLoading = false,
    this.radius = 10,
    this.circularColor = BaseConfig.appThemeColor2,
    required this.child,
    this.padding = const EdgeInsets.all(0),
  });

  final void Function()? onPressed;
  final Color? backgroundColor;
  final double height;
  final double? width;
  final bool isLoading;
  final double radius;
  final Color circularColor;
  final Widget child;
  final EdgeInsetsGeometry padding;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: height,
      width: width,
      child: TextButton(
        style: ButtonStyle(
          shape: WidgetStateProperty.all<RoundedRectangleBorder>(
            RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(radius),
            ),
          ),
          backgroundColor: backgroundColor != null
              ? WidgetStateProperty.all<Color>(backgroundColor!)
              : null,
          padding: WidgetStateProperty.all<EdgeInsetsGeometry>(
            padding,
          ),
        ),
        onPressed: onPressed,
        child: isLoading ? showCircularIndicator(color: circularColor) : child,
      ),
    );
  }
}
