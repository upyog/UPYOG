import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class HomeMyApplications extends StatelessWidget {
  const HomeMyApplications({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () => Navigator.of(context).pop(),
        title: 'My Applications',
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                ListTile(
                  title: const BigText(
                    text: 'Property Tax',
                    fontWeight: FontWeight.bold,
                  ),
                  subtitle: const Text('My Applications'),
                  trailing: const Icon(Icons.east),
                  onTap: () {
                    Get.toNamed(AppRoutes.PROPERTY_APPLICATIONS);
                  },
                ),
                ListTile(
                  title: const BigText(
                    text: 'Trade License',
                    fontWeight: FontWeight.bold,
                  ),
                  subtitle: const Text('My Applications'),
                  trailing: const Icon(Icons.east),
                  onTap: () {
                    Get.toNamed(AppRoutes.TRADE_LICENSE_APPLICATIONS);
                  },
                ),
                ListTile(
                  title: const BigText(
                    text: 'Water',
                    fontWeight: FontWeight.bold,
                  ),
                  subtitle: const Text('My Applications'),
                  trailing: const Icon(Icons.east),
                  onTap: () {
                    Get.toNamed(AppRoutes.WATER_MY_APPLICATIONS);
                  },
                ),
                ListTile(
                  title: const BigText(
                    text: 'Sewerage',
                    fontWeight: FontWeight.bold,
                  ),
                  subtitle: const Text('My Applications'),
                  trailing: const Icon(Icons.east),
                  onTap: () {
                    Get.toNamed(AppRoutes.SEWERAGE_MY_APPLICATIONS);
                  },
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
