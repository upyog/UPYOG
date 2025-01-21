import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';

class TabBarWidget extends StatelessWidget {
  const TabBarWidget({
    super.key,
    required this.tabText1,
    required this.tabText2,
    required this.children,
    this.tabHeight,
    this.tabWidth,
    this.paddingWidth,
  });

  final String? tabText1;
  final String? tabText2;
  final List<Widget> children;
  final double? tabHeight;
  final double? tabWidth;
  final double? paddingWidth;

  @override
  Widget build(BuildContext context) {
    final borderRadius = BorderRadius.circular(15.r);
    final List<Widget> tabs = [
      Tab(
        text: tabText1,
      ),
      Tab(
        text: tabText2,
      ),
    ];
    return DefaultTabController(
      length: tabs.length,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Container(
            height: tabHeight,
            width: tabWidth,
            padding: EdgeInsets.all(paddingWidth ?? 5.w),
            decoration: BoxDecoration(
              borderRadius: borderRadius,
              color: BaseConfig.borderColor,
            ),
            child: TabBar.secondary(
              dividerColor: Colors.transparent,
              indicator: BoxDecoration(
                color: BaseConfig.appThemeColor1,
                borderRadius: borderRadius,
              ),
              unselectedLabelColor: BaseConfig.greyColor1,
              labelColor: BaseConfig.mainBackgroundColor,
              tabs: tabs,
            ),
          ),
          const SizedBox(
            height: 20,
          ),
          Expanded(
            child: TabBarView(
              children: children,
            ),
          ),
        ],
      ),
    );
  }
}
