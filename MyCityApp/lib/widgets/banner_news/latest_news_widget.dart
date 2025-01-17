import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/widgets/news_card.dart';

class LatestNewsWidget extends StatefulWidget {
  final Orientation orientation;
  final String bannerImages;
  final PageController pageController;
  final ValueChanged<int> onPageChanged;

  const LatestNewsWidget({
    required this.orientation,
    required this.bannerImages,
    required this.pageController,
    required this.onPageChanged,
    super.key,
  });

  @override
  State<LatestNewsWidget> createState() => _LatestNewsWidgetState();
}

class _LatestNewsWidgetState extends State<LatestNewsWidget> {
  late int _currentPage;
  Timer? _autoPlayTimer;

  @override
  void initState() {
    super.initState();
    _currentPage = 0;
    _startAutoPlay();
  }

  void _startAutoPlay() {
    _autoPlayTimer = Timer.periodic(const Duration(seconds: 5), (timer) {
      if (widget.pageController.hasClients) {
        final nextPage =
            (_currentPage + 1) % widget.bannerImages.split(',').length;
        widget.pageController.animateToPage(
          nextPage,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeInOut,
        );
        setState(() {
          _currentPage = nextPage;
        });
        widget.onPageChanged(_currentPage);
      }
    });
  }

  @override
  void dispose() {
    _autoPlayTimer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final bannerList = widget.bannerImages.split(',');

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          height: widget.orientation == Orientation.portrait ? 280.h : 220.h,
          child: PageView.builder(
            controller: widget.pageController,
            itemCount: bannerList.length,
            onPageChanged: (int index) {
              setState(() {
                _currentPage = index;
              });
              _autoPlayTimer?.cancel();
              _startAutoPlay();
              widget.onPageChanged(_currentPage);
            },
            itemBuilder: (BuildContext context, int index) {
              final img = bannerList[index];
              return FractionallySizedBox(
                widthFactor: 1 / widget.pageController.viewportFraction,
                child: NewsCard(
                  img: img,
                  orientation: widget.orientation,
                  pageController: widget.pageController,
                  length: bannerList.length,
                ),
              );
            },
          ),
        ),
      ],
    );
  }
}
