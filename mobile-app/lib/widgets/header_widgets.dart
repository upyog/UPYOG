import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

class HeaderTop extends StatelessWidget implements PreferredSizeWidget {
  final double height;
  final String? title;
  final Widget? titleWidget;
  final Function()? onPressed;
  final bool automaticallyImplyLeading;
  final Orientation orientation;
  final List<Widget>? actions;

  const HeaderTop({
    super.key,
    this.height = kToolbarHeight,
    this.title,
    this.onPressed,
    this.titleWidget,
    this.automaticallyImplyLeading = false,
    this.orientation = Orientation.portrait,
    this.actions,
  });

  @override
  Widget build(BuildContext context) {
    return AppBar(
      automaticallyImplyLeading: automaticallyImplyLeading,
      title: titleWidget ?? Text(title ?? ''),
      titleSpacing: 0.0,
      leading: IconButton(
        icon: const Icon(Icons.chevron_left_rounded, color: Color(0xFF231F20)),
        iconSize: orientation == Orientation.portrait ? 24.sp : 16.sp,
        onPressed: onPressed,
      ),
      actions: actions,
    );
  }

  @override
  Size get preferredSize => Size.fromHeight(height);
}
