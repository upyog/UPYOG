// ignore_for_file: must_be_immutable

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/trade_license_controller.dart';
import 'package:mobile_app/model/citizen/property/property.dart';
import 'package:mobile_app/model/citizen/trade_license/trade_license.dart'
    as tl;
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/build_expansion.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class PropertyInformationScreen extends StatelessWidget {
  PropertyInformationScreen({super.key});

  final _tlController = Get.find<TradeLicenseController>();
  final _authController = Get.find<AuthController>();

  final Properties properties = Get.arguments['properties'];
  final tl.License license = Get.arguments['license'];
  final tl.TradeLicenseDetail _tradeLicenseDetail = Get.arguments['tlDetails'];

  /// Merge all owners name
  String? getOwnersName(List<tl.Owner>? owners) {
    if (owners == null || owners.isEmpty) {
      return null;
    }
    String ownerNames = '';
    for (var owner in owners) {
      ownerNames += '${owner.name}, ';
    }
    return ownerNames.substring(0, ownerNames.length - 2);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Navigator.of(context).pop();
        },
        title: getLocalizedString(
          i18.tlProperty.PROPERTY_INFO,
          module: Modules.PT,
        ),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: _buildPropertyDetails(),
          ),
        ),
      ),
    );
  }

  Widget _buildPropertyDetails() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.tlProperty.PROPERTY_DETAILS,
                module: Modules.PT,
              ),
              size: 16.sp,
              fontWeight: FontWeight.w600,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(i18.tlProperty.ID, module: Modules.PT),
              text: properties.properties?.first.propertyId ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label:
                  getLocalizedString(i18.tlProperty.NAME, module: Modules.PT),
              text: getOwnersName(_tradeLicenseDetail.owners) ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label:
                  getLocalizedString(i18.tlProperty.STATUS, module: Modules.PT),
              text: properties.properties!.first.status ?? 'NA',
            ),
            const SizedBox(height: 10),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.tlProperty.ADDRESS_HEADER,
                module: Modules.PT,
              ),
              size: 16.sp,
              fontWeight: FontWeight.w600,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.PINCODE,
                module: Modules.PT,
              ),
              text: properties.properties!.first.address?.pinCode ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label:
                  getLocalizedString(i18.tlProperty.CITY, module: Modules.PT),
              text: properties.properties!.first.address?.city ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.MOHALLA,
                module: Modules.PT,
              ),
              text:
                  properties.properties!.first.address?.locality?.name ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.HOUSE_NO,
                module: Modules.PT,
              ),
              text: properties.properties!.first.address?.doorNo ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.STREET_NAME,
                module: Modules.PT,
              ),
              text: properties.properties!.first.address?.street ?? 'NA',
            ),
            const SizedBox(height: 10),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.tlProperty.PROPERTY_DETAILS_SUB,
                module: Modules.PT,
              ),
              size: 16.sp,
              fontWeight: FontWeight.w600,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.PROPERTY_TYPE,
                module: Modules.PT,
              ),
              text: properties.properties!.first.propertyType ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(i18.tlProperty.USE, module: Modules.PT),
              text: 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label:
                  getLocalizedString(i18.tlProperty.AREA, module: Modules.PT),
              text: properties.properties!.first.landArea != null
                  ? '${properties.properties!.first.landArea}'
                  : 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.NO_OF_FLOOR,
                module: Modules.PT,
              ),
              text: properties.properties!.first.noOfFloors != null &&
                      properties.properties!.first.noOfFloors != 0
                  ? '${properties.properties!.first.noOfFloors}'
                  : 'NA',
            ),
            const SizedBox(height: 10),
            BuildExpansion(
              title: getLocalizedString(
                i18.tlProperty.OWNER_DETAILS_SUB,
                module: Modules.PT,
              ),
              onExpansionChanged: (p0) async {
                if (p0 && !_tlController.isOwnerDetails.value) {
                  await _tlController.getOwnersLicenseByAppID(
                    token: _authController.token?.accessToken ?? '',
                    applicationNo: license.applicationNumber!,
                  );
                }
              },
              children: [
                StreamBuilder(
                  stream: _tlController.licenseStreamCtrl.stream,
                  builder: (context, snapshot) {
                    if (snapshot.hasData) {
                      final snapData = snapshot.data!.first;
                      return ListView.builder(
                        shrinkWrap: true,
                        itemCount: snapData.tradeLicenseDetail!.owners!.length,
                        physics: const NeverScrollableScrollPhysics(),
                        itemBuilder: (context, index) {
                          var owner =
                              snapData.tradeLicenseDetail!.owners![index];

                          return _buildOwnerCard(owner, index + 1)
                              .paddingOnly(bottom: 10);
                        },
                      );
                    } else if (snapshot.hasError) {
                      return networkErrorPage(
                        context,
                        () => _tlController.getOwnersLicenseByAppID(
                          token: _authController.token?.accessToken ?? '',
                          applicationNo: license.applicationNumber!,
                        ),
                      );
                    } else {
                      switch (snapshot.connectionState) {
                        case ConnectionState.waiting:
                          return showCircularIndicator();
                        case ConnectionState.active:
                          return showCircularIndicator();
                        default:
                          return const SizedBox.shrink();
                      }
                    }
                  },
                ),
              ],
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }

  Widget _buildOwnerCard(tl.Owner owner, int index) {
    return Container(
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Wrap(
              children: [
                BigTextNotoSans(
                  text: getLocalizedString(
                    i18.tlProperty.OWNER,
                  ),
                  size: 16.sp,
                  fontWeight: FontWeight.w600,
                  color: const Color.fromRGBO(80, 90, 95, 1.0),
                ),
                BigTextNotoSans(
                  text: ' - $index',
                  size: 16.sp,
                  fontWeight: FontWeight.w600,
                  color: const Color.fromRGBO(80, 90, 95, 1.0),
                ),
              ],
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_INFO_NAME,
                module: Modules.PT,
              ),
              text: owner.name ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_INFO_GENDER,
                module: Modules.PT,
              ),
              text: owner.gender ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_INFO_MOBILE_NO,
                module: Modules.PT,
              ),
              text: owner.mobileNumber ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.SPECIAL_CATEGORY,
                module: Modules.PT,
              ),
              text: owner.ownerType ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.GUARDIAN_NAME,
                module: Modules.PT,
              ),
              text: owner.fatherOrHusbandName ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_INFO_EMAIL_ID,
                module: Modules.PT,
              ),
              text: owner.emailId ?? 'NA',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_ADDRESS,
                module: Modules.PT,
              ),
              text: owner.permanentAddress ?? 'NA',
            ),
            const SizedBox(height: 10),
          ],
        ),
      ),
    );
  }
}
