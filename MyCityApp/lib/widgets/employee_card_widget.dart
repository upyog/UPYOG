import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class EmployeeCardWidget extends StatelessWidget {
  final String header;
  final IconData icon;
  final String count;
  final String nSla;
  final Function() inbox;
  final Function() searchApp;
  const EmployeeCardWidget({
    super.key,
    required this.header,
    required this.icon,
    required this.count,
    required this.nSla,
    required this.inbox,
    required this.searchApp,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(20),
      ),
      height: 300,
      child: Stack(
        children: [
          Column(
            children: [
              Container(
                height: 195,
                decoration: const BoxDecoration(
                  borderRadius: BorderRadius.only(
                    topLeft: Radius.circular(20),
                    topRight: Radius.circular(20),
                  ),
                ),
                child: Stack(
                  alignment: Alignment.center,
                  children: [
                    ClipRRect(
                      borderRadius: const BorderRadius.all(
                        Radius.circular(20),
                      ),
                      child: Image.asset(
                        BaseConfig.empTlCardImg,
                        width: 400,
                        height: 400,
                        fit: BoxFit.cover,
                      ),
                    ),
                    Positioned(
                      top: 30,
                      left: 15,
                      child: Text(
                        header,
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          shadows: [
                            Shadow(
                              blurRadius: 10.0,
                              color: Colors.black,
                              offset: Offset(2, 2),
                            ),
                          ],
                        ),
                      ),
                    ),
                    Positioned(
                      top: 20,
                      right: 20,
                      child: Icon(
                        icon,
                        size: 70,
                        color: Colors.white,
                      ),
                    ),
                  ],
                ),
              ),
              Container(
                height: 55,
                decoration: const BoxDecoration(
                  borderRadius: BorderRadius.only(
                    bottomLeft: Radius.circular(20),
                    bottomRight: Radius.circular(20),
                  ),
                  color: Colors.white,
                ),
              ),
            ],
          ),
          Positioned(
            // alignment: const Alignment(0, 0.7),
            bottom: 50,
            left: 0,
            right: 0,
            child: Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(20),
                boxShadow: [
                  BoxShadow(
                    color: Colors.white.withValues(alpha: 0.3),
                    spreadRadius: 3,
                    blurRadius: 10,
                    offset: const Offset(0, 0),
                  ),
                ],
              ),
              margin: const EdgeInsets.symmetric(horizontal: 20),
              width: Get.width * 0.85,
              height: Get.height * 0.17,
              child: Column(
                children: [
                  const SizedBox(
                    height: 20,
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      Container(
                        color: BaseConfig.appThemeColor2,
                        child: Icon(
                          icon,
                          color: Colors.white,
                          size: 50,
                        ),
                      ),
                      Column(
                        children: [
                          BigText(
                            text: count,
                            size: 20,
                            color: BaseConfig.appThemeColor2,
                            fontWeight: FontWeight.bold,
                          ),
                          const MediumText(
                            text: "Total",
                            color: BaseConfig.appThemeColor2,
                          ),
                        ],
                      ),
                      Column(
                        children: [
                          BigText(
                            text: nSla,
                            size: 20,
                            color: BaseConfig.appThemeColor2,
                            fontWeight: FontWeight.bold,
                          ),
                          const MediumText(
                            text: "Nearing SLA",
                            color: BaseConfig.appThemeColor2,
                          ),
                        ],
                      ),
                    ],
                  ),
                  Row(
                    children: [
                      TextButton(
                        onPressed: inbox,
                        child: const MediumText(
                          text: "Inbox",
                          color: BaseConfig.textColor,
                        ),
                      ),
                      const Text(' | '),
                      TextButton(
                        onPressed: searchApp,
                        child: const MediumText(
                          text: "Search Applications",
                          color: BaseConfig.textColor,
                        ),
                      ),
                      const Text(' | '),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
