import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';

class OwnerCardWidget extends StatelessWidget {
  const OwnerCardWidget({
    super.key,
    required this.property,
    required this.index,
    required this.owner,
  });

  final Property property;
  final int index;
  final Owner owner;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey),
        borderRadius: BorderRadius.circular(10),
      ),
      margin: EdgeInsets.only(bottom: 10.h),
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (property.owners!.length > 1)
              Wrap(
                children: [
                  BigTextNotoSans(
                    text: 'Owner',
                    size: 16.sp,
                    fontWeight: FontWeight.w600,
                    color: const Color.fromRGBO(80, 90, 95, 1.0),
                  ),
                  BigTextNotoSans(
                    text: ' - ${index + 1}',
                    size: 16.sp,
                    fontWeight: FontWeight.w600,
                    color: const Color.fromRGBO(80, 90, 95, 1.0),
                  ),
                ],
              ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.OWNER_NAME,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(owner.name) ? owner.name! : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.GUARDIAN_NAME,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(owner.fatherOrHusbandName)
                  ? owner.fatherOrHusbandName!
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.OWNER_GENDER,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(owner.gender) ? owner.gender! : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_TYPE,
                module: Modules.PT,
              ),
              text: getLocalizedString(
                '${i18.propertyTax.PT_OWNERSHIP}${property.ownershipCategory}',
                module: Modules.PT,
              ),
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.MOBILE_NUMBER,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(owner.mobileNumber)
                  ? owner.mobileNumber!
                  : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.OWNER_EMAIL,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(owner.emailId) ? owner.emailId! : 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.TRANSFEROR_SPECIAL_CATEGORY,
                module: Modules.PT,
              ),
              text: owner.ownerType ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.tlProperty.OWNERSHIP_ADDRESS,
                module: Modules.PT,
              ),
              text: isNotNullOrEmpty(owner.permanentAddress)
                  ? owner.permanentAddress!
                  : 'N/A',
            ),
            const SizedBox(height: 10),
          ],
        ),
      ),
    );
  }
}
