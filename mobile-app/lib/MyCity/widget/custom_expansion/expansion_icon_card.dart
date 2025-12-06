import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/MyCity/screens/home/official_home/ulb_service_management/ulb_service_managment.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class ExpansionIconCard extends StatelessWidget {
  final RxBool isExpanded;
  final TextModel item;
  final Function(String?)? onItemTap;
  final Function()? onExpansionTap;
  const ExpansionIconCard({
    super.key,
    required this.item,
    required this.isExpanded,
    this.onItemTap,
    this.onExpansionTap,
  });

  @override
  Widget build(BuildContext context) {
    return Obx(
      () {
        return Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            _ExpansionCard(
              item: item,
              onTap: onExpansionTap,
              isExpanded: isExpanded.value,
            ),
            if (isExpanded.value)
              _buildCard(
                item,
                onTap: onItemTap,
              ),
          ],
        );
      },
    );
  }

  Widget _buildCard(TextModel item, {Function(String?)? onTap}) {
    return Padding(
      padding: const EdgeInsets.only(left: 55, right: 0, top: 0),
      child: Container(
        height: 200,
        width: Get.width * 0.8,
        decoration: BoxDecoration(
          border: Border.all(color: Colors.black),
        ),
        // margin: const EdgeInsets.only(bottom: 10),
        child: SingleChildScrollView(
          child: Column(
            children: item.sections!.map((e) {
              // return Tooltip(
              //   message: e,
              //   child: ListTile(
              //     contentPadding: const EdgeInsets.only(left: 20),
              //     minTileHeight: 0,
              //     title: MediumTextNotoSans(
              //       text: e,
              //       fontWeight: FontWeight.bold,
              //       maxLine: 2,
              //       textOverflow: TextOverflow.ellipsis,
              //     ),
              //     onTap: () {
              //       onTap!(e);
              //     },
              //   ),
              // );
              return Tooltip(
                message: e,
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    SizedBox(
                      width: Get.width * 0.8,
                      child: Padding(
                        padding: const EdgeInsets.all(8),
                        child: MediumTextNotoSans(
                          text: e,
                          // fontWeight: FontWeight.bold,
                          maxLine: 2,
                          textOverflow: TextOverflow.ellipsis,
                        ),
                      ),
                    ).ripple(() {
                      onTap!(e);
                    }),
                    if (item.sections!.last != e)
                      Container(
                        decoration: const BoxDecoration(
                          border: Border(
                            bottom: BorderSide(
                              color: Colors.black26,
                            ),
                          ),
                        ),
                      ),
                  ],
                ),
              );
            }).toList(),
          ),
        ),
      ),
    );
  }
}

class _ExpansionCard extends StatelessWidget {
  final bool isExpanded;
  final Function()? onTap;
  final TextModel item;

  const _ExpansionCard({
    required this.isExpanded,
    this.onTap,
    required this.item,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      // color: Colors.amber,
      height: 85,
      width: Get.width,
      child: Padding(
        padding: const EdgeInsets.only(bottom: 0),
        child: Stack(
          alignment: Alignment.centerRight,
          children: [
            Positioned(
              right: 0,
              left: 40,
              child: Material(
                color: BaseConfig.mainBackgroundColor,
                child: InkWell(
                  onTap: onTap,
                  child: Container(
                    alignment: Alignment.centerRight,
                    height: 70,
                    width: Get.width * 0.9,
                    decoration: BoxDecoration(
                      border: const Border(
                        top: BorderSide(
                          color: Colors.blueGrey,
                          width: 3,
                        ),
                        bottom: BorderSide(
                          color: Colors.blueGrey,
                          width: 3,
                        ),
                        right: BorderSide(
                          color: Colors.blueGrey,
                          width: 3,
                        ),
                      ),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: Tooltip(
                      message: item.title,
                      margin: const EdgeInsets.symmetric(horizontal: 10),
                      child: Row(
                        children: [
                          const SizedBox(width: 50),
                          Expanded(
                            child: MediumTextNotoSans(
                              text: item.title,
                              fontWeight: FontWeight.bold,
                              maxLine: 2,
                              textOverflow: TextOverflow.ellipsis,
                            ),
                          ),
                          isExpanded
                              ? const Icon(
                                  Icons.arrow_drop_up,
                                  size: 36,
                                  color: BaseConfig.textColor,
                                )
                              : const Icon(
                                  Icons.arrow_drop_down,
                                  size: 36,
                                  color: BaseConfig.textColor,
                                ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),
            Positioned(
              left: 0,
              top: 5,
              child: Container(
                width: 80,
                height: 80,
                decoration: BoxDecoration(
                  border: Border.all(
                    color: const Color(0xFF333333),
                    width: 4,
                  ),
                  borderRadius: BorderRadius.circular(60),
                  color: BaseConfig.mainBackgroundColor,
                ),
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: item.icon != null
                      ? Image.asset(item.icon!)
                      : item.iconData != null
                          ? Icon(item.iconData, size: 36)
                          : const SizedBox.shrink(),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
