// ignore_for_file: must_be_immutable
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/property/property.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/build_expansion.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class WaterPropertyInformationScreen extends StatelessWidget {
  WaterPropertyInformationScreen({super.key});

  final Properties properties = Get.arguments['property'];
  final TenantTenant tenant = Get.arguments['tenant'];

  /// Merge all owners name
  String? getOwnersName(List<Owner>? owners) {
    if (owners == null || owners.isEmpty) {
      return null;
    }
    String ownerNames = '';
    final ownerReversed = owners.reversed.toList();
    for (var owner in ownerReversed) {
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
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(i18.tlProperty.ID, module: Modules.PT),
              text: properties.properties?.first.propertyId ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label:
                  getLocalizedString(i18.tlProperty.NAME, module: Modules.PT),
              text: getOwnersName(properties.properties?.first.owners) ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label:
                  getLocalizedString(i18.tlProperty.STATUS, module: Modules.PT),
              text: isNotNullOrEmpty(properties.properties?.first.status)
                  ? getLocalizedString(
                      '${i18.propertyTax.WF_PT}${properties.properties?.first.status}',
                      module: Modules.PT,
                    )
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.tlProperty.ADDRESS_HEADER,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.PINCODE,
                module: Modules.PT,
              ),
              text: properties.properties?.first.address?.pinCode ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label:
                  getLocalizedString(i18.tlProperty.CITY, module: Modules.PT),
              text: isNotNullOrEmpty(properties.properties?.first.address?.city)
                  ? getCityName(properties.properties!.first.address!.tenantId!)
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.MOHALLA,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(
                properties.properties?.firstOrNull?.address?.locality,
              )
                  ? getLocalizedString(
                      getLocality(
                        tenant,
                        properties.properties?.first.address?.locality?.code ??
                            '',
                      ),
                    )
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.HOUSE_NO,
                module: Modules.PT,
              ),
              text: properties.properties?.first.address?.doorNo ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.STREET_NAME,
                module: Modules.PT,
              ),
              text: properties.properties?.first.address?.street ?? 'N/A',
            ),
            const SizedBox(height: 10),
            BuildExpansion(
              tilePadding: const EdgeInsets.all(0),
              title: getLocalizedString(
                i18.tlProperty.PROPERTY_DETAILS_SUB,
                module: Modules.PT,
              ),
              children: [
                ColumnHeaderText(
                  label: getLocalizedString(
                    i18.tlProperty.PROPERTY_TYPE,
                    module: Modules.PT,
                  ),
                  text: isNotNullOrEmpty(
                    properties.properties?.first.propertyType,
                  )
                      ? getLocalizedString(
                          '${i18.propertyTax.PROPERTYTAX_BILLING_SLAB}${properties.properties?.first.propertyType!.split('.').first}'
                              .toUpperCase(),
                          module: Modules.PT,
                        )
                      : 'N/A',
                ),
                const SizedBox(height: 10),
                ColumnHeaderText(
                  label: getLocalizedString(
                    i18.tlProperty.USE,
                    module: Modules.PT,
                  ),
                  text: isNotNullOrEmpty(
                    properties.properties?.first.usageCategory,
                  )
                      ? getLocalizedString(
                          '${i18.propertyTax.PROPERTYTAX_BILLING_SLAB}${properties.properties?.first.usageCategory}',
                          module: Modules.PT,
                        )
                      : 'N/A',
                ),
                const SizedBox(height: 10),
                ColumnHeaderText(
                  label: getLocalizedString(
                    i18.tlProperty.AREA,
                    module: Modules.PT,
                  ),
                  text: properties.properties?.first.landArea
                          ?.toInt()
                          .toString() ??
                      'N/A',
                ),
                const SizedBox(height: 10),
                ColumnHeaderText(
                  label: getLocalizedString(
                    i18.tlProperty.NO_OF_FLOOR,
                    module: Modules.PT,
                  ),
                  text: isNotNullOrEmpty(
                    properties.properties?.firstOrNull?.noOfFloors,
                  )
                      ? '${properties.properties!.first.noOfFloors}'
                      : 'N/A',
                ),
              ],
            ),
            BuildExpansion(
              tilePadding: const EdgeInsets.all(0),
              title: getLocalizedString(
                i18.tlProperty.OWNER_DETAILS_SUB,
                module: Modules.PT,
              ),
              children: [
                ListView.builder(
                  itemCount: properties.properties?.first.owners?.length,
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemBuilder: (context, index) {
                    final owner =
                        properties.properties?.firstOrNull?.owners?[index];
                    return isNotNullOrEmpty(owner)
                        ? _buildOwnerCard(owner, index + 1)
                            .paddingOnly(bottom: 10)
                        : const SizedBox.shrink();
                  },
                ),
              ],
            ),
            const SizedBox(height: 16),
          ],
        ),
      ),
    );
  }

  Widget _buildOwnerCard(Owner? owner, index) {
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
                  fontWeight: FontWeight.w600,
                  size: 16.sp,
                  color: const Color.fromRGBO(80, 90, 95, 1.0),
                ),
                BigTextNotoSans(
                  text: ' $index',
                  fontWeight: FontWeight.w600,
                  size: 16.sp,
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
              text: owner?.name ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_INFO_GENDER,
                module: Modules.PT,
              ),
              text: owner?.gender ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_INFO_MOBILE_NO,
                module: Modules.PT,
              ),
              text: owner?.mobileNumber ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.SPECIAL_CATEGORY,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(
                owner?.ownerType,
              )
                  ? getLocalizedString(
                      '${i18.common.OWNER_TYPE}${owner!.ownerType}'
                          .toUpperCase(),
                    )
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.GUARDIAN_NAME,
                module: Modules.PT,
              ),
              text: owner?.fatherOrHusbandName ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_TYPE,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(
                properties.properties?.first.ownershipCategory,
              )
                  ? getLocalizedString(
                      properties.properties?.first.ownershipCategory,
                      module: Modules.PT,
                    )
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_INFO_EMAIL_ID,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(owner?.emailId) ? owner!.emailId! : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_ADDRESS,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(owner?.permanentAddress)
                  ? owner!.permanentAddress!
                  : 'N/A',
            ),
          ],
        ),
      ),
    );
  }
}
