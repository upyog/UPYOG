import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/utils/utils.dart';

class NetworkImg extends StatelessWidget {
  const NetworkImg({
    super.key,
    required this.imgUrl,
    this.height = 105,
    this.width = 155,
    this.fit = BoxFit.fill,
    this.errorBorderRadius = 16.0,
  });

  final String imgUrl;
  final double height;
  final double width;
  final double errorBorderRadius;
  final BoxFit fit;

  @override
  Widget build(BuildContext context) {
    return Image.network(
      imgUrl,
      height: height,
      width: width,
      fit: fit,
      loadingBuilder: (
        BuildContext context,
        Widget child,
        ImageChunkEvent? loadingProgress,
      ) {
        if (loadingProgress == null) {
          return child;
        }
        return showCircularIndicator(strokeWidth: 3).paddingAll(2.w);
      },
      errorBuilder: (context, error, stackTrace) {
        return Container(
          height: height,
          width: width,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(errorBorderRadius),
          ),
          child: showErrorIcon(),
        );
      },
    );
  }
}
