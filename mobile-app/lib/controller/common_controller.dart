import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/model/citizen/localization/localization_label.dart';
import 'package:mobile_app/repository/core_repository.dart';
import 'package:mobile_app/services/secure_storage_service.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';

// Get Localized String
String getLocalizedString(String? label, {Modules module = Modules.COMMON}) {
  final languageCtrl = Get.find<LanguageController>();
  final commonCtrl = Get.find<CommonController>();
  if (module == Modules.COMMON) {
    return commonCtrl.commonLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.PGR) {
    return commonCtrl.pgrLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.TL) {
    return commonCtrl.tlLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.PT) {
    return commonCtrl.ptLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.BND) {
    return commonCtrl.bndLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.BPA) {
    return commonCtrl.buildLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.BPAREG) {
    return commonCtrl.buildRegLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.PG) {
    return commonCtrl.buildRegLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.WS) {
    return commonCtrl.wsLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.ABG) {
    return commonCtrl.abgLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.FSM) {
    return commonCtrl.fsmLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.UC) {
    return commonCtrl.ucLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  if (module == Modules.NOC) {
    return commonCtrl.nocLocalizedLabels[languageCtrl.locale.languageCode]
            ?[label] ??
        label ??
        'N/A';
  }
  return label ?? getLocalizedString(i18.common.NA);
}

class CommonController extends GetxController {
  RxBool isLabelsLoading = false.obs;

  Map<String, Map<String, String>> commonLocalizedLabels = {};
  Map<String, Map<String, String>> pgrLocalizedLabels = {};
  Map<String, Map<String, String>> tlLocalizedLabels = {};
  Map<String, Map<String, String>> ptLocalizedLabels = {};
  Map<String, Map<String, String>> bndLocalizedLabels = {};
  Map<String, Map<String, String>> buildLocalizedLabels = {};
  Map<String, Map<String, String>> buildRegLocalizedLabels = {};
  Map<String, Map<String, String>> pgLocalizedLabels = {};
  Map<String, Map<String, String>> wsLocalizedLabels = {};
  Map<String, Map<String, String>> swLocalizedLabels = {};
  Map<String, Map<String, String>> abgLocalizedLabels = {};
  Map<String, Map<String, String>> fsmLocalizedLabels = {};
  Map<String, Map<String, String>> ucLocalizedLabels = {};
  Map<String, Map<String, String>> nocLocalizedLabels = {};

  Future<void> fetchLabels({
    Modules modules = Modules.COMMON,
  }) async {
    final languageCtrl = Get.find<LanguageController>();

    try {
      if (languageCtrl.stateInfo != null) {
        final languages = languageCtrl.stateInfo!.languages!;

        List<List<LocalizationLabel>> commonLabelRes = [];
        List<List<LocalizationLabel>> pgrLabelRes = [];
        List<List<LocalizationLabel>> tlLabelRes = [];
        List<List<LocalizationLabel>> ptLabelRes = [];
        List<List<LocalizationLabel>> bndLabelRes = [];
        List<List<LocalizationLabel>> buildLabelRes = [];
        List<List<LocalizationLabel>> buildRegLabelRes = [];
        List<List<LocalizationLabel>> pgLabelRes = [];
        List<List<LocalizationLabel>> wsLabelRes = [];
        List<List<LocalizationLabel>> swLabelRes = [];
        List<List<LocalizationLabel>> abgLabelRes = [];
        List<List<LocalizationLabel>> fsmLabelRes = [];
        List<List<LocalizationLabel>> ucLabelRes = [];
        List<List<LocalizationLabel>> nocLabelRes = [];

        for (var lang in languages) {
          final isLabelsAvailable =
              await areLabelsAvailableForModule(lang.value!, modules);
          dPrint('isLabelsAvailable: $isLabelsAvailable - ${lang.value}');
          if (isLabelsAvailable) {
            final allLabels = await getLocalLabels(lang.value!, modules);
            if (modules == Modules.COMMON) {
              commonLabelRes.add(allLabels);
            }
            if (modules == Modules.PGR) {
              pgrLabelRes.add(allLabels);
            }
            if (modules == Modules.TL) {
              tlLabelRes.add(allLabels);
            }
            if (modules == Modules.PT) {
              ptLabelRes.add(allLabels);
            }
            if (modules == Modules.BND) {
              bndLabelRes.add(allLabels);
            }
            if (modules == Modules.BPA) {
              buildLabelRes.add(allLabels);
            }
            if (modules == Modules.BPAREG) {
              buildRegLabelRes.add(allLabels);
            }
            if (modules == Modules.PG) {
              pgLabelRes.add(allLabels);
            }
            if (modules == Modules.WS) {
              wsLabelRes.add(allLabels);
            }
            if (modules == Modules.SW) {
              swLabelRes.add(allLabels);
            }
            if (modules == Modules.ABG) {
              abgLabelRes.add(allLabels);
            }
            if (modules == Modules.FSM) {
              fsmLabelRes.add(allLabels);
            }
            if (modules == Modules.UC) {
              ucLabelRes.add(allLabels);
            }
            if (modules == Modules.NOC) {
              nocLabelRes.add(allLabels);
            }
          } else {
            final moduleLabels = await getLabelsByLanguage(
              language: lang.value!,
              modules: modules,
              token: (modules == Modules.BPA || modules == Modules.UC)
                  ? Get.find<AuthController>().token?.accessToken
                  : null,
            );
            if (moduleLabels != null) {
              if (modules == Modules.COMMON) {
                commonLabelRes.add(moduleLabels);
              }
              if (modules == Modules.PGR) {
                pgrLabelRes.add(moduleLabels);
              }
              if (modules == Modules.TL) {
                tlLabelRes.add(moduleLabels);
              }
              if (modules == Modules.PT) {
                ptLabelRes.add(moduleLabels);
              }
              if (modules == Modules.BND) {
                bndLabelRes.add(moduleLabels);
              }
              if (modules == Modules.BPA) {
                buildLabelRes.add(moduleLabels);
              }
              if (modules == Modules.BPAREG) {
                buildRegLabelRes.add(moduleLabels);
              }
              if (modules == Modules.PG) {
                pgLabelRes.add(moduleLabels);
              }
              if (modules == Modules.WS) {
                wsLabelRes.add(moduleLabels);
              }
              if (modules == Modules.SW) {
                swLabelRes.add(moduleLabels);
              }
              if (modules == Modules.ABG) {
                abgLabelRes.add(moduleLabels);
              }
              if (modules == Modules.FSM) {
                fsmLabelRes.add(moduleLabels);
              }
              if (modules == Modules.UC) {
                ucLabelRes.add(moduleLabels);
              }
              if (modules == Modules.NOC) {
                nocLabelRes.add(moduleLabels);
              }
            }
          }
        }

        if (modules == Modules.COMMON) {
          commonLocalizedLabels = mergeLocalizationLabels(commonLabelRes);
        }
        if (modules == Modules.PGR) {
          pgrLocalizedLabels = mergeLocalizationLabels(pgrLabelRes);
        }
        if (modules == Modules.TL) {
          tlLocalizedLabels = mergeLocalizationLabels(tlLabelRes);
        }
        if (modules == Modules.PT) {
          ptLocalizedLabels = mergeLocalizationLabels(ptLabelRes);
        }
        if (modules == Modules.BND) {
          bndLocalizedLabels = mergeLocalizationLabels(bndLabelRes);
        }
        if (modules == Modules.BPA) {
          buildLocalizedLabels = mergeLocalizationLabels(buildLabelRes);
        }
        if (modules == Modules.BPAREG) {
          buildRegLocalizedLabels = mergeLocalizationLabels(buildRegLabelRes);
        }
        if (modules == Modules.PG) {
          pgLocalizedLabels = mergeLocalizationLabels(pgLabelRes);
        }
        if (modules == Modules.WS) {
          wsLocalizedLabels = mergeLocalizationLabels(wsLabelRes);
        }
        if (modules == Modules.SW) {
          swLocalizedLabels = mergeLocalizationLabels(swLabelRes);
        }
        if (modules == Modules.ABG) {
          abgLocalizedLabels = mergeLocalizationLabels(abgLabelRes);
        }
        if (modules == Modules.FSM) {
          fsmLocalizedLabels = mergeLocalizationLabels(fsmLabelRes);
        }
        if (modules == Modules.UC) {
          ucLocalizedLabels = mergeLocalizationLabels(ucLabelRes);
        }
        if (modules == Modules.NOC) {
          nocLocalizedLabels = mergeLocalizationLabels(nocLabelRes);
        }

        updateTranslations();
      }
    } catch (e) {
      dPrint('Fetch Labels Error: $e');
    }
  }

  void updateTranslations() {
    Get.updateLocale(Get.find<LanguageController>().locale);
    update();
  }

  Future<List<LocalizationLabel>> getLocalLabels(
    String language,
    Modules module,
  ) async {
    final labelsJson =
        await storage.getString('Citizen.$language.${module.name}');
    return decodeLocalizationLabels(labelsJson ?? '[]');
  }

  List<LocalizationLabel> decodeLocalizationLabels(String jsonString) {
    final List<dynamic> parsed = jsonDecode(jsonString);
    return parsed
        .map((dynamic item) => LocalizationLabel.fromJson(item))
        .toList();
  }

  Future<bool> areLabelsAvailableForModule(
    String language,
    Modules modules,
  ) async {
    dPrint('Checking if labels are available for $language - ${modules.name}');
    final labels = await storage.getString('Citizen.$language.${modules.name}');
    return labels != null && labels != '[]';
  }

  Future<List<LocalizationLabel>?> getLabelsByLanguage({
    required String language,
    required Modules modules,
    String? token,
  }) async {
    try {
      final query = {
        'module': modules.name,
        'locale': language,
        'tenantId': BaseConfig.STATE_TENANT_ID,
      };

      final response =
          await CoreRepository.getLocalization(query, token: token);
      await setLocalizationLabelList(
        response,
        'Citizen.$language.${modules.name}',
      );
      dPrint('getLabelsByLanguage: Citizen.$language.${modules.name}');
      return response;
    } catch (e) {
      return null;
    }
  }

  Map<String, Map<String, String>> mergeLocalizationLabels(
    List<List<LocalizationLabel>> apiResponses,
  ) {
    Map<String, Map<String, String>> mergedResponse = {};

    for (var apiResponse in apiResponses) {
      for (var label in apiResponse) {
        String locale = label.locale!;
        mergedResponse.putIfAbsent(locale, () => {});

        mergedResponse[locale]![label.code!] = label.message!;
      }
    }

    return mergedResponse;
  }

  Future setLocalizationLabelList(
    List<LocalizationLabel> labels,
    String key,
  ) async {
    try {
      await storage.setString(
        key,
        jsonEncode(labels),
      );
    } catch (e) {
      snackBar(
        'Unable to store the details',
        'ERROR',
        Colors.red,
      );
    }
  }
}
