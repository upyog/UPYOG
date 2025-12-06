import 'dart:io';

import 'package:flutter/material.dart';
import 'package:mobile_app/components/network_img.dart';
import 'package:mobile_app/config/base_config.dart';

class ImagePlaceHolder extends StatelessWidget {
  const ImagePlaceHolder({
    super.key,
    this.photoUrl,
    this.radius = 48,
    this.backgroundColor = BaseConfig.greyColor2,
    this.icon = Icons.person,
    this.iconColor = Colors.white54,
    this.iconSize = 50,
    this.padding = const EdgeInsets.only(top: 10),
    this.height = 105,
    this.width = 155,
    this.isFile = false,
  });

  final String? photoUrl;
  final double radius;
  final Color backgroundColor;
  final IconData icon;
  final Color iconColor;
  final double iconSize;
  final EdgeInsetsGeometry padding;
  final double height;
  final double width;
  final bool isFile;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: padding,
      child: CircleAvatar(
        backgroundColor: backgroundColor,
        radius: radius,
        child: photoUrl != null && photoUrl!.isNotEmpty
            ? isFile
                ? ClipOval(
                    child: Image.file(
                      File(photoUrl!),
                      height: height,
                      width: width,
                      fit: BoxFit.cover,
                    ),
                  )
                : ClipOval(
                    child: NetworkImg(
                      height: height,
                      width: width,
                      imgUrl: photoUrl!,
                      fit: BoxFit.cover,
                    ),
                  )
            : Icon(
                Icons.person,
                size: iconSize,
                color: iconColor,
              ),
      ),
    );
  }
}
