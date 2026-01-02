import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class CityNotFound extends StatelessWidget {
  const CityNotFound({super.key});
  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: Get.height * 0.4,
      child: const Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.location_off, size: 35, color: Colors.grey),
            MediumTextNotoSans(
              text: 'No Cities Found',
              fontWeight: FontWeight.w700,
              color: Colors.grey,
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
}
