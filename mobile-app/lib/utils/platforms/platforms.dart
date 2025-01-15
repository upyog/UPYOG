import 'dart:io';

import 'package:flutter/material.dart';

class AppPlatforms {
  /// Platform specific scroll physics
  static ScrollPhysics platformPhysics() => Platform.isAndroid
      ? const BouncingScrollPhysics()
      : const AlwaysScrollableScrollPhysics();

  /// Platform specific scroll physics
  static TextInputType platformKeyboardType() => Platform.isIOS
      ? const TextInputType.numberWithOptions(decimal: true, signed: true)
      : TextInputType.number;
}
