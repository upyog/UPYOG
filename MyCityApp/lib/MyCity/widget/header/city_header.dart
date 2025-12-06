import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/MyCity/widget/home_header/home_header.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class MyCityHeader extends StatelessWidget implements PreferredSizeWidget {
  final double height;
  final String? title;
  final VoidCallback? calenderOnPressed;
  final bool automaticallyImplyLeading;
  final double elevation;
  final bool isBackButton;
  final bool showBack;

  const MyCityHeader({
    super.key,
    this.height = 100,
    this.title,
    this.calenderOnPressed,
    this.automaticallyImplyLeading = false,
    this.elevation = 0.0,
    this.isBackButton = false,
    this.showBack = false,
  });

  @override
  Widget build(BuildContext context) {
    return AppBar(
      automaticallyImplyLeading: automaticallyImplyLeading,
      title: const SizedBox(
        child: HomeHeader(),
      ),
      bottom: PreferredSize(
        preferredSize: Size.fromHeight(height),
        child: Stack(
          children: [
            Container(
              width: Get.width,
              height: 40.h,
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [
                    Color(0xfff3af81),
                    Color(0xffbf4720),
                  ],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
              ),
              child: Center(
                child: MediumTextNotoSans(
                  text: title ?? '',
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            if (isBackButton || showBack)
              Positioned(
                left: 8,
                top: 0,
                bottom: 0,
                child: SizedBox(
                  height: 30,
                  child: IconButton(
                    style: IconButton.styleFrom(
                      shape: const CircleBorder(),
                      padding: EdgeInsets.zero,
                    ),
                    onPressed: showBack ? () => Get.back() : calenderOnPressed,
                    icon: const Icon(
                      Icons.arrow_back_ios,
                      color: Colors.white,
                      size: 20,
                    ),
                  ),
                ),
              ),
            Positioned(
              right: 8,
              top: 0,
              bottom: 0,
              child: SizedBox(
                height: 30,
                child: IconButton(
                  style: IconButton.styleFrom(
                    shape: const CircleBorder(),
                    padding: EdgeInsets.zero,
                  ),
                  onPressed: calenderOnPressed,
                  icon: const Icon(
                    Icons.calendar_month_outlined,
                    color: Colors.white,
                    size: 20,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
      elevation: elevation,
    );
  }

  @override
  Size get preferredSize => Size.fromHeight(height);
}
