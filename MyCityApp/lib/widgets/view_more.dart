import 'package:flutter/material.dart';

class ViewMore extends StatelessWidget {
  const ViewMore({
    super.key,
    this.isMore = false,
    required this.lessWidget,
    required this.moreWidget,
  });

  final bool isMore;
  final Widget lessWidget;
  final Widget moreWidget;

  @override
  Widget build(BuildContext context) {
    return isMore ? moreWidget : lessWidget;
  }
}
