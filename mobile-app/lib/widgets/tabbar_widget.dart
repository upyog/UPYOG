import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';

class TabBarWidget extends StatefulWidget {
  const TabBarWidget({
    super.key,
    required this.tabs,
    required this.children,
    this.tabHeight,
    this.tabWidth,
    this.paddingWidth,
    this.onTap,
    this.tabController,
    this.onIndexChanged,
  });

  final List<Widget> tabs;
  final List<Widget> children;
  final double? tabHeight;
  final double? tabWidth;
  final double? paddingWidth;
  final Function(int)? onTap;
  final TabController? tabController;
  final Function(int)? onIndexChanged;

  @override
  State<TabBarWidget> createState() => _TabBarWidgetState();
}

class _TabBarWidgetState extends State<TabBarWidget>
    with SingleTickerProviderStateMixin {
  late TabController _controller;

  @override
  void initState() {
    super.initState();
    _controller = widget.tabController ??
        TabController(length: widget.tabs.length, vsync: this);
    _controller.addListener(_handleTabChange);
  }

  void _handleTabChange() {
    setState(() {
      widget.onIndexChanged?.call(_controller.index);
    });
  }

  @override
  void dispose() {
    _controller.removeListener(_handleTabChange);
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final borderRadius = BorderRadius.circular(15.r);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        Container(
          height: widget.tabHeight,
          width: widget.tabWidth,
          padding: EdgeInsets.all(widget.paddingWidth ?? 5.w),
          decoration: BoxDecoration(
            borderRadius: borderRadius,
            color: BaseConfig.borderColor,
          ),
          child: TabBar.secondary(
            controller: _controller,
            dividerColor: Colors.transparent,
            indicator: BoxDecoration(
              color: BaseConfig.appThemeColor1,
              borderRadius: borderRadius,
            ),
            unselectedLabelColor: BaseConfig.greyColor1,
            labelColor: BaseConfig.mainBackgroundColor,
            tabs: widget.tabs,
            onTap: widget.onTap,
          ),
        ),
        const SizedBox(
          height: 20,
        ),
        Expanded(
          child: TabBarView(
            controller: _controller,
            children: widget.children,
          ),
        ),
      ],
    );
  }
}
