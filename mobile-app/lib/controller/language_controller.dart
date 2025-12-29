import 'dart:async';
import 'dart:convert';
import 'dart:ui';

import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/localization/mdms_static_data.dart';
import 'package:mobile_app/model/custom_exception_model.dart';
import 'package:mobile_app/repository/core_repository.dart';
import 'package:mobile_app/services/mdms_service.dart';
import 'package:mobile_app/services/secure_storage_service.dart';
import 'package:mobile_app/utils/constants/constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class LanguageController extends GetxController implements GetxService {
  var streamController = StreamController.broadcast();

  StateInfo? stateInfo;
  CitizenConsentForm? citizenConsentForm;
  late MdmsResTenant mdmsResTenant;
  List<TenantTenant>? popularCities;
  MdmsStaticData? mdmsStaticData; // Non-reactive variable
  var isLoading = true.obs; // Loading state
  var errorMessage = ''.obs;

  int _languageSelectionIndex = 0;
  int get languageSelectionIndex => _languageSelectionIndex;
  RxString selectedAppLanguage = 'ENGLISH'.obs;

  Locale _locale = const Locale('en', 'IN');

  Locale get locale => _locale;

  List<Languages> get languages => stateInfo!.languages!;

  List<TenantTenant> filteredTenants = [];

  @override
  void onReady() async {
    super.onReady();
    await getMdmsAccessResponse();
    await init();
  }

  Future<void> init() async {
    final String? userType = await storage.getString(Constants.USER_TYPE);

    final user = userType ?? UserType.CITIZEN.name;

    await getMdmsStaticData(
      tenantId: BaseConfig.STATE_TENANT_ID,
      isUc: user == UserType.EMPLOYEE.name,
    );
  }

  @override
  void onClose() {
    super.onClose();
    streamController.close();
    Get.reset();
  }

  Future<void> getMdmsStaticData({
    required String tenantId,
    bool isUc = false,
  }) async {
    try {
      final storedData = await storage.getString(
        isUc
            ? Constants.MDMS_STATIC_DATA_EMPLOYEE
            : Constants.MDMS_STATIC_DATA_CITIZEN,
      );

      if (isNotNullOrEmpty(storedData)) {
        mdmsStaticData = MdmsStaticData.fromJson(jsonDecode(storedData!));
        return;
      }

      final mdmsResponse = await CoreRepository.getMdmsDynamic(
        body: payloadParametersBody(tenantId: tenantId, isUc: isUc),
        query: {
          'tenantId': tenantId,
        },
      );

      mdmsStaticData = MdmsStaticData.fromJson(mdmsResponse);

      await storage.setString(
        isUc
            ? Constants.MDMS_STATIC_DATA_EMPLOYEE
            : Constants.MDMS_STATIC_DATA_CITIZEN,
        jsonEncode(mdmsStaticData!.toJson()),
      );
    } catch (e, s) {
      errorMessage('Failed to fetch getMdmsStaticData data: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Get the Privacy Policy response
  Future<void> getMdmsAccessResponse() async {
    try {
      final storedData =
          await storage.getString(Constants.CITIZEN_CONSENT_FORM);
      if (isNotNullOrEmpty(storedData)) {
        citizenConsentForm =
            CitizenConsentForm.fromJson(jsonDecode(storedData!));
        return;
      }

      final mdmsRes = await CoreRepository.getMdms(
        query: {
          'tenantId': BaseConfig.STATE_TENANT_ID,
        },
        body: getMdmsAccessControl(tenantId: BaseConfig.STATE_TENANT_ID),
      );

      if (isNotNullOrEmpty(
        mdmsRes.mdmsRes?.commonMasters?.citizenConsentForm,
      )) {
        citizenConsentForm =
            mdmsRes.mdmsRes!.commonMasters!.citizenConsentForm!.first;

        await storage.setString(
          Constants.CITIZEN_CONSENT_FORM,
          jsonEncode(citizenConsentForm!.toJson()),
        );

        dPrint('Checking MDMS Response: ${citizenConsentForm?.toJson()}');
      }
    } catch (e, s) {
      ErrorHandler.logError(
        'Failed to fetch getMdmsAccessResponse data: $e',
        s,
      );
    }
  }

  Future<void> getLocalizationData() async {
    try {
      _languageSelectionIndex =
          await storage.getInt(Constants.LANG_SELECTION_INDEX) ?? 0;
      final res = await getLanguages();
      final tenantsRes = await getTenants();
      if (res != null && tenantsRes != null) {
        stateInfo = res;
        mdmsResTenant = tenantsRes;
        popularCities = tenantsRes.tenants
            ?.where((city) => city.isPopular == true)
            .toList();

        stateInfo?.languages?.forEach((lan) => lan.isSelected = false);

        final stateInfos = <StateInfo>[];
        stateInfos.add(StateInfo.fromJson(res.toJson()));

        stateInfos.first.languages?[_languageSelectionIndex].isSelected = true;
        final selectedStateLangues =
            stateInfo?.languages?[_languageSelectionIndex];
        selectedStateLangues!.isSelected = true;

        setSelectedState(stateInfo!);
        _locale = Locale(selectedStateLangues.value!);
        Get.updateLocale(_locale);

        await storage.setString(
          Constants.LANGUAGE,
          selectedStateLangues.value.toString(),
        );
        streamController.add(stateInfos);
      } else {
        final localizationList = await CoreRepository.getMdms(
          query: {
            'tenantId': BaseConfig.STATE_TENANT_ID,
          },
          body: initRequestBody(tenantId: BaseConfig.STATE_TENANT_ID),
        );
        stateInfo = localizationList.mdmsRes!.commonMasters!.stateInfo!.first;
        mdmsResTenant = localizationList.mdmsRes!.tenant!;

        if (stateInfo != null && mdmsResTenant.tenants != null) {
          setSelectedState(stateInfo!);
          setMdmsResTenant(mdmsResTenant);

          final defaultSelect = stateInfo?.languages?.first;

          defaultSelect!.isSelected = true;
          _locale = Locale(defaultSelect.value!);
          Get.updateLocale(_locale);

          await storage.setString(
            Constants.LANGUAGE,
            defaultSelect.value.toString(),
          );
        }
        streamController.add(
          localizationList.mdmsRes?.commonMasters?.stateInfo ?? <StateInfo>[],
        );
      }
    } on CustomException catch (e, s) {
      ErrorHandler.handleApiException(e, s);
      streamController.add('GetLocalizationDataError: ${e.message}');
    } catch (e, s) {
      ErrorHandler.logError(e.toString(), s);
      streamController.add('GetLocalizationDataError: $e');
    }
  }

  void onSelectionOfLanguage(
    Languages language,
    List<Languages> languages,
    int index,
  ) async {
    if (language.isSelected) return;
    for (var element in languages) {
      element.isSelected = false;
    }
    _languageSelectionIndex = index;
    language.isSelected = true;
    languages[languages.indexOf(language)] = language;
    stateInfo!.languages = languages;
    _locale = Locale(language.value!);

    dPrint('Language: ${_locale.runtimeType}');
    dPrint('Language Selected: $_locale');

    await storage.setString(Constants.LANGUAGE, language.value.toString());
    Get.updateLocale(_locale);
    setSelectedState(stateInfo!);

    update();
  }

  void setSelectedState(StateInfo stateInfo) {
    storage.setString(
      Constants.STATES_KEY,
      jsonEncode(stateInfo.toJson()),
    );
  }

  void setMdmsResTenant(MdmsResTenant mdmsResTenant) {
    storage.setString(
      Constants.TENANTS,
      jsonEncode(mdmsResTenant.toJson()),
    );
  }

  Future<StateInfo?> getLanguages() async {
    String? res;
    StateInfo? stateLocal;
    try {
      res = await storage.getString(Constants.STATES_KEY);
    } catch (e) {
      dPrint('getLanguagesError: $e');
    }
    if (res != null) {
      stateLocal = StateInfo.fromJson(jsonDecode(res));
    }

    return stateLocal;
  }

  Future<MdmsResTenant?> getTenants() async {
    String? res;
    MdmsResTenant? mdmsTenant;

    try {
      res = await storage.getString(Constants.TENANTS);
    } catch (e) {
      dPrint('getTenantsError: $e');
    }

    if (res != null) {
      mdmsTenant = MdmsResTenant.fromJson(jsonDecode(res));
    }

    return mdmsTenant;
  }

  Languages? get selectedLanguage =>
      stateInfo?.languages?.firstWhere((element) => element.isSelected == true);

  filterTenants(String text) {
    if (text.isEmpty) {
      filteredTenants.clear();
    } else {
      filteredTenants = mdmsResTenant.tenants!.where((element) {
        return element.name!.toLowerCase().contains(text.toLowerCase());
      }).toList();
    }
    update();
  }
}
