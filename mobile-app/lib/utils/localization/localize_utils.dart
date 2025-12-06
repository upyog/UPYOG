import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';

class LocalizeUtils {
  // Get localization on action name for Employee
  // WF_EMPLOYEE_NEWTL_SENDBACKTOCITIZEN
  static String getTakeActionLocal(
    String? action, {
    required String workflowCode,
    Modules module = Modules.COMMON,
    bool isCommon = false,
    bool isCitizen = false,
  }) {
    dPrint(workflowCode);
    switch (action) {
      case 'SENDBACKTOCITIZEN':
        return getLocalizedString(
          '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'.toUpperCase(),
          module: module,
        );
      case 'SEND_BACK_TO_CITIZEN':
        return getLocalizedString(
          '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'.toUpperCase(),
          module: module,
        );
      case 'FORWARD':
        return getLocalizedString(
          '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'.toUpperCase(),
          module: isCommon ? Modules.COMMON : module,
        );
      case 'SENDBACK':
        return getLocalizedString(
          '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'.toUpperCase(),
          module: module,
        );
      case 'APPROVE':
        return getLocalizedString(
          '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'.toUpperCase(),
          module: module,
        );
      case 'REJECT':
        return getLocalizedString(
          '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'.toUpperCase(),
          module: module,
        );
      case 'SEND_BACK_FOR_DOCUMENT_VERIFICATION':
        return getLocalizedString(
          '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'.toUpperCase(),
          module: module,
        );
      case 'VERIFY_AND_FORWARD':
        return getLocalizedString(
          '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'.toUpperCase(),
          module: module,
        );
      case 'SENDBACKFROMFI':
        return getLocalizedString(
          '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'.toUpperCase(),
          module: module,
        );
      case 'SEND_TO_ARCHITECT':
        return getLocalizedString(
          isCitizen
              ? 'WF_${workflowCode}_$action'
              : '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'
                  .toUpperCase(),
          module: module,
        );
      case 'REVOCATE':
        return getLocalizedString(
          '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'.toUpperCase(),
          module: module,
        );

      case 'PAY':
        // WF_CITIZEN_FIRENOC_PAY
        return getLocalizedString(
          isCitizen
              ? 'WF_CITIZEN_${workflowCode}_$action'.toUpperCase()
              : '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'
                  .toUpperCase(),
          module: module,
        );
      case 'REFER':
        return getLocalizedString(
          '${i18.common.WF_EMPLOYEE}${workflowCode}_$action'.toUpperCase(),
          module: module,
        );

      default:
        return 'N/A';
    }
  }
}
