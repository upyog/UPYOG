// ignore_for_file: must_be_immutable

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/model/citizen/property/property.dart';
import 'package:mobile_app/model/employee/emp_tl_model/emp_tl_model.dart' as tl;
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class EmpPropertyInformationScreen extends StatelessWidget {
  EmpPropertyInformationScreen({super.key});

  final Properties properties = Get.arguments['properties'];
  final tl.Item item = Get.arguments['item'];

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
    final propertyType = properties.properties?.firstOrNull?.propertyType
        ?.split('.')
        .firstOrNull;

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
              text: isNotNullOrEmpty(
                properties.properties?.firstOrNull?.propertyId,
              )
                  ? properties.properties!.firstOrNull!.propertyId!
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label:
                  getLocalizedString(i18.tlProperty.NAME, module: Modules.PT),
              text: getOwnersName(
                    item.businessObject?.tradeLicenseDetail?.owners,
                  ) ??
                  'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label:
                  getLocalizedString(i18.tlProperty.STATUS, module: Modules.PT),
              text: isNotNullOrEmpty(properties.properties?.firstOrNull?.status)
                  ? properties.properties!.firstOrNull!.status!
                  : 'N/A',
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
              text: isNotNullOrEmpty(
                properties.properties!.firstOrNull?.address?.pinCode,
              )
                  ? properties.properties!.firstOrNull!.address!.pinCode!
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label:
                  getLocalizedString(i18.tlProperty.CITY, module: Modules.PT),
              text: isNotNullOrEmpty(
                properties.properties?.firstOrNull?.address?.city,
              )
                  ? properties.properties!.firstOrNull!.address!.city!
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.MOHALLA,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(
                properties.properties?.firstOrNull?.address?.locality?.name,
              )
                  ? properties.properties!.firstOrNull!.address!.locality!.name!
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.HOUSE_NO,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(
                properties.properties?.firstOrNull?.address?.doorNo,
              )
                  ? properties.properties!.first.address!.doorNo!
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.STREET_NAME,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(
                properties.properties?.firstOrNull?.address?.street,
              )
                  ? properties.properties!.first.address!.street!
                  : 'N/A',
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
              text: isNotNullOrEmpty(
                properties.properties?.firstOrNull?.propertyType,
              )
                  ? getLocalizedString(
                      '${i18.propertyTax.PROPERTYTAX_BILLING_SLAB}${propertyType!}',
                      module: Modules.PT,
                    )
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(i18.tlProperty.USE, module: Modules.PT),
              text: isNotNullOrEmpty(
                properties.properties?.firstOrNull?.usageCategory,
              )
                  ? getLocalizedString(
                      '${i18.propertyTax.PROPERTYTAX_BILLING_SLAB}${properties.properties!.first.usageCategory}',
                    )
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label:
                  getLocalizedString(i18.tlProperty.AREA, module: Modules.PT),
              text:
                  isNotNullOrEmpty(properties.properties?.firstOrNull?.landArea)
                      ? '${properties.properties!.firstOrNull?.landArea}'
                      : 'N/A',
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
                  ? '${properties.properties!.firstOrNull?.noOfFloors}'
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.tlProperty.OWNER_DETAILS_SUB,
                module: Modules.PT,
              ),
              size: 16.sp,
              fontWeight: FontWeight.w600,
            ),
            const SizedBox(height: 10),
            for (int i = 0;
                i < item.businessObject!.tradeLicenseDetail!.owners!.length;
                i++)
              _buildOwnerCard(
                item.businessObject!.tradeLicenseDetail!.owners![i],
                i + 1,
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
              text: owner.name ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_INFO_GENDER,
                module: Modules.PT,
              ),
              text: owner.gender ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_INFO_MOBILE_NO,
                module: Modules.PT,
              ),
              text: owner.mobileNumber ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.SPECIAL_CATEGORY,
                module: Modules.PT,
              ),
              text: owner.type ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.GUARDIAN_NAME,
                module: Modules.PT,
              ),
              text: owner.fatherOrHusbandName ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_TYPE,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(
                item.businessObject?.tradeLicenseDetail?.subOwnerShipCategory,
              )
                  ? getLocalizedString(
                      item.businessObject?.tradeLicenseDetail
                          ?.subOwnerShipCategory!,
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
              text: owner.emailId ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_ADDRESS,
                module: Modules.PT,
              ),
              text: owner.permanentAddress ?? 'N/A',
            ),
            const SizedBox(height: 10),
          ],
        ),
      ),
    );
  }
}
