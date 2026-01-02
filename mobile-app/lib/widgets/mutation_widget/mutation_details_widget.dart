import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';

class MutationDetailsWidget extends StatelessWidget {
  const MutationDetailsWidget({
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
            i18.propertyTax.PT_MUTATION_PENDING_COURT,
            module: Modules.PT,
          ),
          text: isNotNullOrEmpty(property.additionalDetails?.isMutationInCourt)
              ? getLocalizedString(
                  property.additionalDetails!.isMutationInCourt!,
                )
              : getLocalizedString(i18.common.CS_NA),
        ),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.propertyTax.PT_DETAILS_COURT_CASE,
            module: Modules.PT,
          ),
          text: isNotNullOrEmpty(property.additionalDetails?.caseDetails)
              ? property.additionalDetails!.caseDetails!
              : getLocalizedString(i18.common.CS_NA),
        ),
        ColumnHeaderText(
          label: getLocalizedString(
            i18.propertyTax.PT_PROP_UNDER_GOV_ACQUISITION,
            module: Modules.PT,
          ),
          text: isNotNullOrEmpty(
            property.additionalDetails?.isPropertyUnderGovtPossession,
          )
              ? getLocalizedString(
                  property.additionalDetails!.isPropertyUnderGovtPossession!,
                )
              : getLocalizedString(i18.common.CS_NA),
        ),

        //TODO: Details of Government Acquisition Field
        ColumnHeaderText(
          label: getLocalizedString(
            i18.propertyTax.PT_DETAILS_GOV_ACQUISITION,
            module: Modules.PT,
          ),
          text: getLocalizedString(i18.common.CS_NA),
        ),
        SizedBox(height: 10.h),
      ],
    );
  }
}
