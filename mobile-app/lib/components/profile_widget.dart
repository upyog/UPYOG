import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/image_placeholder.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';

class ProfileWidget extends StatelessWidget {
  final String imageUrl;
  final double size;
  final double? svgSize;
  final double iconSize;
  final Function()? onPressed;
  final Function? imgClick;
  final bool isFile;

  const ProfileWidget({
    super.key,
    required this.imageUrl,
    required this.size,
    this.iconSize = 50,
    this.svgSize,
    this.onPressed,
    this.imgClick,
    this.isFile = false,
  });

  @override
  Widget build(BuildContext context) {
    dPrint('Image Url: $imageUrl');
    return Stack(
      alignment: Alignment.bottomCenter,
      children: [
        SizedBox(
          width: size,
          height: size,
          child: ImagePlaceHolder(
            width: size,
            height: size,
            photoUrl: imageUrl,
            iconSize: iconSize,
            isFile: isFile,
          ).ripple(imgClick!),
        ).paddingAll(20),
        Positioned(
          bottom: -15,
          child: Container(
            decoration: const BoxDecoration(
              color: Colors.white,
              shape: BoxShape.circle,
            ),
            child: IconButton(
              icon: SvgPicture.asset(
                height: svgSize,
                width: svgSize,
                BaseConfig.profileStartCameraIcon,
              ),
              onPressed: onPressed,
            ),
          ).paddingAll(20),
        ),
      ],
    );
  }
}
