import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/text_formfield_normal.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/controller/location_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/screens/citizen/location_choose/location_choose.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/utils.dart';

class HomeLocationChoose extends StatefulWidget {
  const HomeLocationChoose({super.key});

  @override
  State<HomeLocationChoose> createState() => _HomeLocationChooseState();
}

class _HomeLocationChooseState extends State<HomeLocationChoose> {
  final cityController = Get.find<CityController>();
  final _languageCtrl = Get.find<LanguageController>();

  final TextEditingController _searchController = TextEditingController();
  List<TenantTenant> filteredPopularCities = [];
  List<TenantTenant> filteredOtherCities = [];
  bool noResultsFound = false;

  @override
  void initState() {
    cityController.fetchSelectedCity();
    _languageCtrl.getLocalizationData();
    _searchController.addListener(_filterCities);
    super.initState();
  }

  void _filterCities() {
    List<TenantTenant> otherCity = [];
    var searchText = _searchController.text.toLowerCase();
    final cityList = _languageCtrl.mdmsResTenant.tenants!;
    final filterCities =
        cityList.where((city) => city.isPopular == true).toList();

    filteredPopularCities = filterCities.where((city) {
      String name = getLocalizedString(
        '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}_${city.code!.split('.').last}'
            .toUpperCase(),
      );
      return name.toLowerCase().contains(searchText);
    }).toList();

    if (filteredPopularCities.isEmpty) {
      for (var tenant in cityList) {
        bool isInPopularCities = filterCities.any((popularCity) {
          return popularCity.code == tenant.code;
        });

        if (!isInPopularCities) {
          otherCity.add(tenant);
        }
      }

      filteredOtherCities = otherCity.where((city) {
        String name = getLocalizedString(
          '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}_${city.code!.split('.').last}'
              .toUpperCase(),
        );
        return name.toLowerCase().contains(searchText);
      }).toList();
    } else {
      filteredOtherCities.clear();
    }

    noResultsFound =
        filteredPopularCities.isEmpty && filteredOtherCities.isEmpty;

    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          onPressed: () {
            Get.offAndToNamed(AppRoutes.BOTTOM_NAV);
          },
          icon: const Icon(
            Icons.arrow_back_ios,
          ),
        ),
      ),
      body:
          // if (orientation == Orientation.portrait) {
        SingleChildScrollView(
        physics: const NeverScrollableScrollPhysics(),
        child: SizedBox(
          height: Get.height,
          width: Get.width,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              textFormFieldNormal(
                controller: _searchController,
                context,
                getLocalizedString(i18.common.SEARCH),
                hintTextColor: BaseConfig.greyColor1,
                prefixIcon: const Icon(Icons.search),
                textInputAction: TextInputAction.search,
                //onChange: _onSearchChanged,
              ).marginOnly(left: 20.w, right: 20.w, top: 20.h),
              SizedBox(
                height: 8.h,
              ),
              //Don't delete this comment
              // TextButtonApp(
              //   height: 36.h,
              //   backgroundColor:
              //   BaseConfig.appThemeColor1.withValues(alpha:0.1),
              //   child: Row(
              //     children: [
              //       const Icon(
              //         Icons.location_on_outlined,
              //         color: BaseConfig.appThemeColor1,
              //       ).paddingSymmetric(horizontal: 8.w),
              //       SmallTextNotoSans(
              //         text: "Detect my Location",
              //         fontWeight: FontWeight.w600,
              //         size: 12.h,
              //         color: BaseConfig.appThemeColor1,
              //       ),
              //     ],
              //   ),
              //   onPressed: () {
              //     WidgetsBinding.instance.addPostFrameCallback((_) => _detectLocation());
              //   },
              // ),
              // SizedBox(
              //   height: 16.h,
              // ),
              Obx(() {
                cityController.selectedCity.value;
                cityController.cityName.value;
                return PopularCityWidget(
                  languageController: _languageCtrl,
                  cityController: cityController,
                  filteredPopularCities: filteredPopularCities,
                  noResultsFound: noResultsFound,
                  selectedCity: cityController.selectedCity.value,
                  cityName: cityController.cityName.value,
                  onCityTap: (tenant) async {
                    dPrint(
                      "Selected City: ${tenant.code}",
                    );
                    cityController.selectedCity.value = tenant.code!;
                    cityController.cityName.value = tenant.name!;
                    await cityController.setSelectedCity(tenant);
                  },
                );
              }),
              if (!noResultsFound)
                Obx(() {
                  cityController.selectedCity.value;
                  cityController.cityName.value;
                  return OtherCityWidget(
                    o: o,
                    languageController: _languageCtrl,
                    cityController: cityController,
                    filteredOtherCities: filteredOtherCities,
                    tenants: _languageCtrl.mdmsResTenant.tenants
                            ?.where((city) => city.isPopular == false)
                            .toList() ??
                        [],
                    onCityTap: (tenant) async {
                      dPrint(
                        "Selected City: ${tenant.code}",
                      );
                      cityController.selectedCity.value = tenant.code!;
                      cityController.cityName.value = tenant.name!;
                      await cityController.setSelectedCity(tenant);
                    },
                  );
                }),
              SizedBox(
                height: 21.h,
              ),
            ],
          ),
        ),
      ),
    );
  }

  // //popup dialoge
  // void _detectLocation() {
  //   Get.dialog(
  //     AlertDialog(
  //       insetPadding: EdgeInsets.symmetric(horizontal: 16.w),
  //       contentPadding: EdgeInsets.symmetric(vertical: 20.h, horizontal: 16.w),
  //       shape: RoundedRectangleBorder(
  //         borderRadius: BorderRadius.circular(8.r),
  //       ),
  //       backgroundColor: BaseConfig.mainBackgroundColor,
  //       content: SizedBox(
  //         // height: 258.h,
  //         width: Get.width,
  //         child: SingleChildScrollView(
  //           child: Column(
  //             mainAxisAlignment: MainAxisAlignment.center,
  //             crossAxisAlignment: CrossAxisAlignment.center,
  //             children: <Widget>[
  //               Container(
  //                 width: 48.w,
  //                 height: 48.h,
  //                 decoration: BoxDecoration(
  //                   borderRadius: BorderRadius.circular(50),
  //                   color: BaseConfig.lightIndigoColor,
  //                 ),
  //                 child: const Icon(
  //                   Icons.location_on_outlined,
  //                   color: BaseConfig.appThemeColor1,
  //                   size: 24,
  //                 ),
  //               ),
  //               SizedBox(
  //                 height: 20.h,
  //               ),
  //               MediumTextNotoSans(
  //                 text: 'Access Required',
  //                 fontWeight: FontWeight.w700,
  //                 size: 16.h,
  //               ),
  //               SizedBox(
  //                 height: 14.h,
  //               ),
  //               Wrap(
  //                 children: [
  //                   SmallTextNotoSans(
  //                     text: 'Allow ',
  //                     fontWeight: FontWeight.w400,
  //                     size: 14.h,
  //                   ),
  //                   SmallTextNotoSans(
  //                     text: '${BaseConfig.APP_NAME} Vision ',
  //                     fontWeight: FontWeight.w700,
  //                     size: 14.h,
  //                   ),
  //                   SmallTextNotoSans(
  //                     text: 'to access your ',
  //                     fontWeight: FontWeight.w400,
  //                     size: 14.h,
  //                   ),
  //                 ],
  //               ),
  //               SmallTextNotoSans(
  //                 text: 'Location.',
  //                 fontWeight: FontWeight.w400,
  //                 size: 14.h,
  //               ),
  //               SizedBox(
  //                 height: 16.h,
  //               ),
  //               const Divider(
  //                 color: BaseConfig.borderColor,
  //               ),
  //               SizedBox(
  //                 height: 16.h,
  //               ),
  //               Row(
  //                 children: [
  //                   Expanded(
  //                     child: SizedBox(
  //                       height: 44.h,
  //                       child: gradientBtn(
  //                         text: 'Deny',
  //                         buttonColor: Colors.white,
  //                         horizonPadding: 5,
  //                         textColor: BaseConfig.appThemeColor1,
  //                         onPressed: () {
  //                           Get.back();
  //                         },
  //                       ),
  //                     ),
  //                   ),
  //                   Expanded(
  //                     child: SizedBox(
  //                       height: 44.h,
  //                       child: gradientBtn(
  //                         text: 'Allow',
  //                         horizonPadding: 5,
  //                         onPressed: () {
  //                           cityController.fetchPosition();
  //                           Get.back();
  //                         },
  //                       ),
  //                     ),
  //                   ),
  //                 ],
  //               ),
  //             ],
  //           ),
  //         ),
  //       ),
  //     ),
  //     barrierDismissible: false,
  //   );
  // }

  // void _detectLocationLand() {
  //   Get.dialog(
  //     AlertDialog(
  //       insetPadding: EdgeInsets.symmetric(horizontal: 16.w),
  //       contentPadding: EdgeInsets.symmetric(vertical: 20.h, horizontal: 16.w),
  //       shape: RoundedRectangleBorder(
  //         borderRadius: BorderRadius.circular(8.r),
  //       ),
  //       backgroundColor: BaseConfig.mainBackgroundColor,
  //       content: SizedBox(
  //         // height: 258.h,
  //         width: Get.width,
  //         child: SingleChildScrollView(
  //           child: Column(
  //             mainAxisAlignment: MainAxisAlignment.center,
  //             crossAxisAlignment: CrossAxisAlignment.center,
  //             children: <Widget>[
  //               Container(
  //                 width: 35.w,
  //                 height: 100.h,
  //                 decoration: BoxDecoration(
  //                   borderRadius: BorderRadius.circular(50),
  //                   color: BaseConfig.lightIndigoColor,
  //                 ),
  //                 child: Icon(
  //                   Icons.location_on_outlined,
  //                   color: BaseConfig.appThemeColor1,
  //                   size: 15.sp,
  //                 ),
  //               ),
  //               SizedBox(
  //                 height: 20.h,
  //               ),
  //               MediumTextNotoSans(
  //                 text: 'Access Required',
  //                 fontWeight: FontWeight.w700,
  //                 size: 7.sp,
  //               ),
  //               SizedBox(
  //                 height: 14.h,
  //               ),
  //               Wrap(
  //                 children: [
  //                   SmallTextNotoSans(
  //                     text: 'Allow ',
  //                     fontWeight: FontWeight.w400,
  //                     size: 7.sp,
  //                   ),
  //                   SmallTextNotoSans(
  //                    text: '${BaseConfig.APP_NAME} Vision ',
  //                     fontWeight: FontWeight.w700,
  //                     size: 7.sp,
  //                   ),
  //                   SmallTextNotoSans(
  //                     text: 'to access your ',
  //                     fontWeight: FontWeight.w400,
  //                     size: 7.sp,
  //                   ),
  //                 ],
  //               ),
  //               SmallTextNotoSans(
  //                 text: 'Location.',
  //                 fontWeight: FontWeight.w400,
  //                 size: 7.sp,
  //               ),
  //               SizedBox(
  //                 height: 16.h,
  //               ),
  //               const Divider(
  //                 color: BaseConfig.borderColor,
  //               ),
  //               SizedBox(
  //                 height: 16.h,
  //               ),
  //               Row(
  //                 children: [
  //                   Expanded(
  //                     child: SizedBox(
  //                       height: 50.h,
  //                       child: gradientBtn(
  //                         text: 'Deny',
  //                         fontSize: 8.sp,
  //                         buttonColor: Colors.white,
  //                         horizonPadding: 5,
  //                         textColor: BaseConfig.appThemeColor1,
  //                         onPressed: () {
  //                           Get.back();
  //                         },
  //                       ),
  //                     ),
  //                   ),
  //                   Expanded(
  //                     child: SizedBox(
  //                       height: 50.h,
  //                       child: gradientBtn(
  //                         text: 'Allow',
  //                         fontSize: 8.sp,
  //                         horizonPadding: 5,
  //                         onPressed: () {
  //                           cityController.fetchPosition();
  //                           Get.back();
  //                         },
  //                       ),
  //                     ),
  //                   ),
  //                 ],
  //               ),
  //             ],
  //           ),
  //         ),
  //       ),
  //     ),
  //     barrierDismissible: false,
  //   );
  // }
}
