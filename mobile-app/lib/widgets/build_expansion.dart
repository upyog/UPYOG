import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/widgets/big_text.dart';

class BuildExpansion extends StatelessWidget {
  const BuildExpansion({
    super.key,
    this.title = '',
    required this.children,
    this.initiallyExpanded = false,
    this.onExpansionChanged,
    this.childrenPadding = const EdgeInsets.symmetric(horizontal: 10),
    this.tilePadding,
  });

  final String title;
  final List<Widget> children;
  final bool initiallyExpanded;
  final Function(bool)? onExpansionChanged;
  final EdgeInsetsGeometry childrenPadding;
  final EdgeInsetsGeometry? tilePadding;

  @override
  Widget build(BuildContext context) {
    return ExpansionTile(
      childrenPadding: childrenPadding,
      initiallyExpanded: initiallyExpanded,
      onExpansionChanged: onExpansionChanged,
      tilePadding: tilePadding,
      title: BigTextNotoSans(
        text: title,
        fontWeight: FontWeight.w600,
        size: 16.sp,
      ),
      children: children,
    );
  }
}
