import 'dart:async';
import 'dart:convert';
import 'dart:ui';

import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/localization/mdms_static_data.dart';
import 'package:mobile_app/model/custom_exception_model.dart';
import 'package:mobile_app/repository/core_repository.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/services/mdms_service.dart';
import 'package:mobile_app/utils/constants/constants.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class LanguageController extends GetxController implements GetxService {
  var streamController = StreamController.broadcast();

  StateInfo? stateInfo;
  late CitizenConsentForm citizenConsentForm;
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
  void onInit() async {
    super.onInit();
    getMdmsAccessResponse();
    getMdmsStaticData(tenantId: BaseConfig.STATE_TENANT_ID);
  }

  @override
  void onClose() {
    super.onClose();
    streamController.close();
  }

  Future<void> getMdmsStaticData({
    required String tenantId,
    bool isUc = false,
  }) async {
    try {
      final mdmsResponse = await CoreRepository.getMdmsDynamic(
        body: payloadParametersBody(tenantId: tenantId, isUc: isUc),
        query: {
          'tenantId': tenantId,
        },
      );
      mdmsStaticData = MdmsStaticData.fromJson(mdmsResponse);
    } catch (e, s) {
      errorMessage('Failed to fetch getMdmsStaticData data: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Get the Privacy Policy response
  Future<void> getMdmsAccessResponse() async {
    final mdmsRes = await CoreRepository.getMdms(
      query: {
        'tenantId': BaseConfig.STATE_TENANT_ID,
      },
      body: getMdmsAccessControl(tenantId: BaseConfig.STATE_TENANT_ID),
    );
    if (mdmsRes.mdmsRes != null) {
      if (mdmsRes.mdmsRes?.commonMasters?.citizenConsentForm != null &&
          mdmsRes.mdmsRes!.commonMasters!.citizenConsentForm!.isNotEmpty) {
        citizenConsentForm =
            mdmsRes.mdmsRes!.commonMasters!.citizenConsentForm!.first;
        dPrint("Checking mdms respone ----------------");
        dPrint(citizenConsentForm.toString());
      }
    }
  }

  Future<void> getLocalizationData() async {
    try {
      _languageSelectionIndex =
          await HiveService.getData(Constants.LANG_SELECTION_INDEX) ?? 0;
      final res = await getLanguages();
      final tenantsRes = await getTenants();
      if (res != null && tenantsRes != null) {
        stateInfo = res;
        mdmsResTenant = tenantsRes;
        popularCities = tenantsRes.tenants
            ?.where((city) => city.isPopular == true)
            .toList();
        for (var lan in stateInfo!.languages!) {
          if (lan.isSelected) {
            lan.isSelected = false;
          }
        }
        var stateInfos = <StateInfo>[];
        stateInfos.add(StateInfo.fromJson(res.toJson()));
        stateInfos.first.languages?[_languageSelectionIndex].isSelected = true;
        var selectedStateLangues =
            stateInfo?.languages?[_languageSelectionIndex];
        selectedStateLangues!.isSelected = true;
        setSelectedState(stateInfo!);
        _locale = Locale(selectedStateLangues.value!);
        Get.updateLocale(_locale);
        await HiveService.setData(
          Constants.LOCALE,
          selectedStateLangues.value!.toString(),
        );
        streamController.add(stateInfos);
        // await commonController.getLocalizationLabels();
      } else {
        var localizationList = await CoreRepository.getMdms(
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
          var defaultSelect = stateInfo?.languages?.first;
          defaultSelect!.isSelected = true;
          _locale = Locale(defaultSelect.value!);
          Get.updateLocale(_locale);
          await HiveService.setData(
            Constants.LANGUAGE,
            defaultSelect.value!.toString(),
          );
          await HiveService.setData(
            Constants.LOCALE,
            defaultSelect.value!.toString(),
          );
        }
        streamController.add(
          localizationList.mdmsRes?.commonMasters?.stateInfo ?? <StateInfo>[],
        );
      }
    } on CustomException catch (e, s) {
      ErrorHandler.handleApiException(e, s);
      streamController.addError('error');
    } catch (e, s) {
      ErrorHandler.logError(e.toString(), s);
      streamController.add('error');
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
    dPrint('Language Selcted: $_locale');
    await HiveService.setData(Constants.LOCALE, language.value!.toString());
    await HiveService.setData(Constants.LANGUAGE, language.value!.toString());
    Get.updateLocale(Locale(language.value!));
    setSelectedState(stateInfo!);
    // AppTranslations().load();

    update();
  }

  void setSelectedState(StateInfo stateInfo) {
    HiveService.setData(
      Constants.STATES_KEY,
      jsonEncode(stateInfo.toJson()),
    );
  }

  void setMdmsResTenant(MdmsResTenant mdmsResTenant) {
    HiveService.setData(
      Constants.TENANTS,
      jsonEncode(mdmsResTenant.toJson()),
    );
  }

  Future<StateInfo?> getLanguages() async {
    var res;
    StateInfo? stateLocal;
    try {
      res = await HiveService.getData(Constants.STATES_KEY);
    } catch (e) {
      print('getLanguagesError: $e');
    }
    if (res != null) {
      stateLocal = StateInfo.fromJson(jsonDecode(res));
    }

    return stateLocal;
  }

  Future<MdmsResTenant?> getTenants() async {
    var res;
    MdmsResTenant? mdmsTenant;
    try {
      res = await HiveService.getData(Constants.TENANTS);
    } catch (e) {
      print('getTenantsError: $e');
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
