import 'dart:convert';

import 'package:geocoding/geocoding.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/localization/mdms_static_data.dart'
    as mdms;
import 'package:mobile_app/model/request/emp_challan_request/taxt_head_model.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/utils/constants/constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class CityController extends GetxController {
  var selectedCity = ''.obs;
  var empSelectedCity = ''.obs, empSelectedCity2 = ''.obs;
  Position? position;
  var cityName = ''.obs;

  RxString selectedServiceCategory = ''.obs, selectedServiceType = ''.obs;
  RxList<mdms.BusinessService> filteredBusinessService =
      <mdms.BusinessService>[].obs;
  RxList<mdms.TaxHeadMaster> filteredTaxHead = <mdms.TaxHeadMaster>[].obs;
  List<TaxHeadModel> taxHeadAmounts = [];

  final List<TenantTenant> allCities = [];
  final RxList<TenantTenant> remainingCities = <TenantTenant>[].obs;

  void setSelectedServiceCategoryUC(String serviceCategory) {
    selectedServiceCategory.value = serviceCategory;
  }

  void clearUc() {
    selectedServiceCategory.value = '';
    selectedServiceType.value = '';
    filteredBusinessService.clear();
    filteredTaxHead.clear();
    taxHeadAmounts.clear();
  }

  Future<void> setSelectedCity(TenantTenant tenant) async {
    selectedCity.value = tenant.code!;
    await HiveService.setData(Constants.HOME_CITY, jsonEncode(tenant));
    update();
  }

  Future<void> setEmpSelectedCity(TenantTenant tenant) async {
    empSelectedCity.value = tenant.code!;
    await HiveService.setData(Constants.HOME_CITY_EMP, jsonEncode(tenant));
  }

  Future<void> fetchSelectedCity() async {
    final userType = await HiveService.getData(Constants.USER_TYPE);
    if (userType == UserType.CITIZEN.name) {
      final city = await HiveService.getData(Constants.HOME_CITY);
      if (city != null) {
        final cityTenant = TenantTenant.fromJson(jsonDecode(city));
        selectedCity.value = cityTenant.code!;
        cityName.value = cityTenant.name!;
      }
      return;
    }
    TenantTenant empTenant = await getCityTenantEmployee();
    empSelectedCity.value = empTenant.code!;
  }

  Future<void> fetchPosition() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      Get.snackbar('Error', 'Location Service is not enabled');
      return;
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        Get.snackbar('Error', 'You denied the permission');
        return;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      Get.snackbar('Error', 'You denied the permission forever');
      return;
    }

    Position currentPosition = await Geolocator.getCurrentPosition();
    position = currentPosition;
    update();
    getCityName(currentPosition);
  }

  Future<void> getCityName(Position position) async {
    try {
      List<Placemark> placemarks =
          await placemarkFromCoordinates(position.latitude, position.longitude);
      if (placemarks.isNotEmpty) {
        Placemark place = placemarks[0];
        cityName.value = place.locality!;
        selectedCity.value = place.locality!;
        getFindCityFromLocation(place.locality!);
        update();
      }
    } catch (e) {
      dPrint('GetCityName Error: $e');
    }
  }

  void getFindCityFromLocation(String name) {
    var cityList = Get.find<LanguageController>().mdmsResTenant.tenants!;
    for (var tenant in cityList) {
      if (tenant.name == name) {
        dPrint('City selected from location: ${tenant.code}');
        setSelectedCity(tenant);
        cityName.value = name;
        selectedCity.value = tenant.code!;
        update();
      }
    }
  }
}
