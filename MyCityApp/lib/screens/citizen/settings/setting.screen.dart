import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class SettingScreen extends StatelessWidget {
  const SettingScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Get.offAndToNamed(AppRoutes.BOTTOM_NAV);
        },
        title: "Setting",
      ),
      body: const Placeholder(),
    );
  }
}
