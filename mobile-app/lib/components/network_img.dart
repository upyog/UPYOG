import 'package:cached_network_image/cached_network_image.dart';
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
    return CachedNetworkImage(
      imageUrl: imgUrl,
      height: height,
      width: width,
      fit: fit,
      placeholder: (context, url) =>
          showCircularIndicator(strokeWidth: 3).paddingAll(2.w),
      errorWidget: (context, url, error) => Container(
        height: height,
        width: width,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(errorBorderRadius),
        ),
        child: showErrorIcon(),
      ),
    );
  }
}
