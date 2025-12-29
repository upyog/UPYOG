import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';

class RegistrationDetailsWidget extends StatelessWidget {
  const RegistrationDetailsWidget({
    super.key,
    required this.property,
  });

  final Property property;

  @override
  Widget build(BuildContext context) {
    return Column(
      spacing: 10.h,
      children: [
        ColumnHeaderText(
          label: getLocalizedString(
            i18.propertyTax.PT_REASON_PROP_TRANSFER,
            module: Modules.PT,
          ),
          text: isNotNullOrEmpty(property.additionalDetails!.reasonForTransfer)
              ? getLocalizedString(
                  property.additionalDetails!.reasonForTransfer!,
                  module: Modules.PT,
                )
              : getLocalizedString(i18.common.CS_NA),
        ),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.propertyTax.PT_PROP_MARKET_VALUE,
            module: Modules.PT,
          ),
          text: isNotNullOrEmpty(property.additionalDetails!.marketValue)
              ? '${property.additionalDetails!.marketValue}'
              : getLocalizedString(i18.common.CS_NA),
        ),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.propertyTax.PT_REG_NUMBER,
            module: Modules.PT,
          ),
          text: isNotNullOrEmpty(property.additionalDetails!.documentNumber)
              ? property.additionalDetails!.documentNumber!
              : getLocalizedString(i18.common.CS_NA),
        ),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.propertyTax.PT_DOC_ISSUE_DATE,
            module: Modules.PT,
          ),
          text: isNotNullOrEmpty(property.additionalDetails?.documentDate)
              ? property.additionalDetails!.documentDate!
                  .toCustomDateFormat(pattern: 'd MMM yyyy')!
              : getLocalizedString(i18.common.CS_NA),
        ),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.propertyTax.PT_REG_DOC_VALUE,
            module: Modules.PT,
          ),
          text: isNotNullOrEmpty(property.additionalDetails?.documentValue)
              ? '${property.additionalDetails!.documentValue}'
              : getLocalizedString(i18.common.CS_NA),
        ),

        //TODO: Remarks Field
        ColumnHeaderText(
          label: getLocalizedString(
            i18.propertyTax.PT_REMARKS,
            module: Modules.PT,
          ),
          text: getLocalizedString(i18.common.CS_NA),
        ),
        SizedBox(height: 10.h),
      ],
    );
  }
}
