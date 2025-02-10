import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/MyCity/widget/mycity_whatsnew_widget.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart';

class LatestNews extends StatefulWidget {
  const LatestNews({super.key});

  @override
  State<LatestNews> createState() => _LatestNewsState();
}

class _LatestNewsState extends State<LatestNews> {
  late PageController _pageController;
  Timer? _autoPlayTimer;
  int _currentPage = 0;

  @override
  void initState() {
    super.initState();
    _pageController = PageController();
    _startAutoPlay();
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }
  
  void _startAutoPlay() {
    _autoPlayTimer = Timer.periodic(const Duration(seconds: 3), (Timer timer) {
      if (_pageController.hasClients) {
        if (_currentPage < BaseConfig.APP_LOGIN_BANNERS.split(',').length - 1) {
          _currentPage++;
        } else {
          _currentPage = 0;
        }
        _pageController.animateToPage(
          _currentPage,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeInOut,
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Padding(
      padding: EdgeInsets.symmetric(
        horizontal: 16.w,
      ),
      child: Stack(
        children: [
          SizedBox(height: 16.h),
          SizedBox(
            height: o == Orientation.portrait ? 188.h : 220.h,
            child: PageView.builder(
              controller: _pageController,
              itemCount: BaseConfig.APP_LOGIN_BANNERS.split(',').length,
              onPageChanged: (int index) {
                setState(() {
                  _currentPage = index;
                });
                _autoPlayTimer?.cancel();
                _startAutoPlay();
              },
              itemBuilder: (BuildContext context, int index) {
                final img = BaseConfig.APP_LOGIN_BANNERS.split(',')[index];
                final imgText =
                    BaseConfig.APP_LOGIN_BANNERS_TEXT.split(',')[index];
                return FractionallySizedBox(
                  widthFactor: 1 / _pageController.viewportFraction,
                  child: MyCityWhatsNewWidget(
                    img: img,
                    orientation: o,
                    imgText: imgText,
                  ),
                );
              },
            ),
          ),
          Positioned(
            bottom: 10.h,
            left: 0,
            right: 0,
            child: Center(
              child: SmoothPageIndicator(
                controller: _pageController,
                count: BaseConfig.APP_LOGIN_BANNERS.split(',').length,
                effect: ExpandingDotsEffect(
                  dotHeight: 6.h,
                  dotWidth: o == Orientation.portrait ? 6.w : 3.w,
                  expansionFactor: 2,
                  spacing: 4.w,
                  activeDotColor: BaseConfig.appThemeColor1,
                  dotColor: BaseConfig.dotGrayColor,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
