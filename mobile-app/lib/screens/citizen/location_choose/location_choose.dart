import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/gradient_btn.dart';
import 'package:mobile_app/components/text_formfield_normal.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/controller/location_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class LocationChooseScreen extends StatefulWidget {
  const LocationChooseScreen({super.key});

  @override
  State<LocationChooseScreen> createState() => _LocationChooseScreenState();
}

class _LocationChooseScreenState extends State<LocationChooseScreen> {
  final CityController cityController = Get.put(CityController());
  final _languageCtrl = Get.find<LanguageController>();
  final TextEditingController _searchController = TextEditingController();
  List<TenantTenant> filteredPopularCities = [];
  List<TenantTenant> filteredOtherCities = [];
  bool noResultsFound = false;

  @override
  void initState() {
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
    return OrientationBuilder(
      builder: (context, orientation) {
        return Scaffold(
          appBar: AppBar(
            leading: IconButton(
              onPressed: () {
                Get.offAndToNamed(AppRoutes.LOGIN);
              },
              icon: const Icon(
                Icons.arrow_back_ios,
              ),
            ),
          ),
          body: GetBuilder<LanguageController>(
            builder: (languageController) {
              // if (orientation == Orientation.portrait) {
              return SingleChildScrollView(
                physics: const NeverScrollableScrollPhysics(),
                child: Container(
                  height: Get.height,
                  width: Get.width,
                  padding: EdgeInsets.all(20.w),
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
                      ),
                      SizedBox(
                        height: 8.h,
                      ),
                      //Don't delete this comment
                      // TextButtonApp(
                      //   height: 36.h,
                      //   backgroundColor:
                      //   BaseConfig.appThemeColor1.withOpacity(0.1),
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
                      const Divider(),
                      MediumTextNotoSans(
                        text: getLocalizedString(i18.common.SELECT_CITY),
                        fontWeight: FontWeight.w700,
                        size: 16.h,
                      ),
                      const Divider(),
                      SizedBox(
                        height: 20.h,
                      ),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          if (noResultsFound)
                            SizedBox(
                              height: Get.height * 0.4,
                              child:
                                  const Center(child: Text('No Cities Found')),
                            ),
                          if (!noResultsFound) ...[
                            if (isNotNullOrEmpty(
                              _languageCtrl.popularCities,
                            )) ...[
                              SmallTextNotoSans(
                                text: getLocalizedString(
                                  i18.common.POPULAR_CITIES,
                                ),
                                fontWeight: FontWeight.w600,
                                size: 12.h,
                                color: BaseConfig.greyColor4,
                              ),
                              SizedBox(
                                height: 16.h,
                              ),
                              SizedBox(
                                width: Get.width,
                                height: 110.h,
                                child: ListView.builder(
                                  scrollDirection: Axis.horizontal,
                                  itemCount:
                                      isNotNullOrEmpty(filteredPopularCities)
                                          ? filteredPopularCities.length
                                          : _languageCtrl.popularCities?.length,
                                  shrinkWrap: true,
                                  itemBuilder: (context, index) {
                                    final popularCity =
                                        isNotNullOrEmpty(filteredPopularCities)
                                            ? filteredPopularCities[index]
                                            : _languageCtrl
                                                .popularCities![index];

                                    return Obx(() {
                                      bool isSelected =
                                          cityController.selectedCity.value ==
                                                  popularCity.name ||
                                              cityController.cityName.value ==
                                                  popularCity.name;
                                      return InkWell(
                                        onTap: () async {
                                          dPrint(
                                            "Selected City: ${popularCity.code}",
                                          );
                                          cityController.selectedCity.value =
                                              popularCity.code!;
                                          cityController.cityName.value =
                                              popularCity.name!;
                                          await cityController
                                              .setSelectedCity(popularCity);
                                        },
                                        child: Column(
                                          children: [
                                            Container(
                                              decoration: BoxDecoration(
                                                borderRadius:
                                                    BorderRadius.circular(50.r),
                                                color:
                                                    BaseConfig.lightIndigoColor,
                                              ),
                                              width: 60.w,
                                              height: 60.h,
                                              child: const Icon(
                                                Icons.business_outlined,
                                                color:
                                                    BaseConfig.appThemeColor1,
                                                size: 26,
                                              ),
                                            ),
                                            SizedBox(
                                              height: 8.h,
                                            ),
                                            SizedBox(
                                              width: 60.w,
                                              child: Tooltip(
                                                message: getLocalizedString(
                                                  '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}_${popularCity.code!.split('.').last}'
                                                      .toUpperCase(),
                                                ),
                                                child: SmallTextNotoSans(
                                                  text: getLocalizedString(
                                                    '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}_${popularCity.code!.split('.').last}'
                                                        .toUpperCase(),
                                                  ),
                                                  fontWeight: FontWeight.w600,
                                                  size: 12.h,
                                                  maxLine: 2,
                                                  textAlign: TextAlign.center,
                                                  textOverflow:
                                                      TextOverflow.ellipsis,
                                                  color: isSelected
                                                      ? BaseConfig
                                                          .appThemeColor1
                                                      : BaseConfig.greyColor4,
                                                ),
                                              ),
                                            ),
                                            SizedBox(
                                              height: 4.h,
                                            ),
                                            if (isSelected)
                                              Container(
                                                width: 8.w,
                                                height: 8.h,
                                                decoration: const BoxDecoration(
                                                  shape: BoxShape.circle,
                                                  color: BaseConfig
                                                      .appThemeColor1, // Or any color you prefer for the dot
                                                ),
                                              ),
                                          ],
                                        ).marginOnly(right: 10),
                                      );
                                    });
                                  },
                                ),
                              ),
                              SizedBox(
                                height: 25.h,
                              ),
                            ],
                            SmallTextNotoSans(
                              text: getLocalizedString(i18.common.OTHER_CITIES),
                              fontWeight: FontWeight.w600,
                              size: 12.h,
                              color: BaseConfig.greyColor4,
                            ),
                            SizedBox(
                              height: 16.h,
                            ),
                            SizedBox(
                              width: Get.width,
                              child: _buildView(
                                languageController.mdmsResTenant.tenants
                                    ?.where((city) => city.isPopular == false)
                                    .toList(),
                                languageController,
                                orientation,
                              ),
                            ),
                          ],
                        ],
                      ),
                      SizedBox(
                        height: isNotNullOrEmpty(
                          _languageCtrl.popularCities,
                        )
                            ? 21.h
                            : 40,
                      ),
                      gradientBtn(
                        height: 44.h,
                        horizonPadding: 0,
                        radius: 12.r,
                        width: Get.width,
                        text: getLocalizedString(i18.common.CONTINUE),
                        onPressed: () async {
                          if (cityController.selectedCity.value.isEmpty) {
                            snackBar(
                              'Required',
                              'City required',
                              Colors.red,
                            );
                            return;
                          }
                          HiveService.setData(
                            HiveConstants.FIRST_TIME_USER,
                            true,
                          );
                          Get.offNamed(AppRoutes.BOTTOM_NAV);
                        },
                      ),
                    ],
                  ),
                ),
              );
            },
          ),
        );
      },
    );
  }

  Widget _buildView(
    List<TenantTenant>? tenants,
    LanguageController languageController,
    orientation,
  ) {
    return SizedBox(
      width: Get.width,
      height: isNotNullOrEmpty(
        _languageCtrl.popularCities,
      )
          ? 0.65.sw
          : Get.height * 0.5,
      child: Column(
        children: [
          if (noResultsFound)
            const Center(child: Text('No Other Cities Found')),
          if (isNotNullOrEmpty(tenants) && !noResultsFound) ...[
            SizedBox(
              width: Get.width,
              height: isNotNullOrEmpty(
                _languageCtrl.popularCities,
              )
                  ? 0.65.sw
                  : Get.height * 0.5,
              child: ListView.builder(
                physics: const AlwaysScrollableScrollPhysics(),
                itemCount: isNotNullOrEmpty(filteredOtherCities)
                    ? filteredOtherCities.length
                    : tenants!.length,
                shrinkWrap: true,
                itemBuilder: (context, index) {
                  TenantTenant tenant = isNotNullOrEmpty(filteredOtherCities)
                      ? filteredOtherCities[index]
                      : tenants![index];
                  return _buildTenant(tenant, orientation);
                },
              ),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildTenant(TenantTenant tenant, Orientation orientation) {
    return Obx(() {
      bool isSelected = (cityController.selectedCity.value == tenant.name) ||
          (cityController.cityName.value == tenant.name);

      return Column(
        children: [
          ListTile(
            minVerticalPadding: 2,
            dense: true,
            title: MediumTextNotoSans(
              text: getCityName(tenant.code ?? 'N/A'),
              fontWeight: FontWeight.w600,
              size: orientation == Orientation.portrait ? 12.sp : 7.sp,
              color: isSelected
                  ? BaseConfig.appThemeColor1
                  : BaseConfig.greyColor4,
            ),
            contentPadding: EdgeInsets.zero,
            trailing: isSelected
                ? Container(
                    width: 10.w,
                    height: 10.h,
                    decoration: const BoxDecoration(
                      shape: BoxShape.circle,
                      color: BaseConfig.appThemeColor1, // Dot color
                    ),
                  )
                : null,
            onTap: () async {
              cityController.selectedCity.value = tenant.code!;
              cityController.cityName.value = tenant.name!;
              await cityController.setSelectedCity(tenant);
              dPrint("------------Checking tenant pg district code");
              dPrint('${tenant.city!.districtCode}');
            },
          ),
          const Divider(color: Colors.grey), // Keep the divider
        ],
      );
    });
  }

  /** 
  //popup dialoge
  void _detectLocation() {
    Get.dialog(
      AlertDialog(
        insetPadding: EdgeInsets.symmetric(horizontal: 16.w),
        contentPadding: EdgeInsets.symmetric(vertical: 20.h, horizontal: 16.w),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8.r),
        ),
        backgroundColor: BaseConfig.mainBackgroundColor,
        content: SizedBox(
          // height: 258.h,
          width: Get.width,
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Container(
                  width: 48.w,
                  height: 48.h,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(50),
                    color: BaseConfig.lightIndigoColor,
                  ),
                  child: const Icon(
                    Icons.location_on_outlined,
                    color: BaseConfig.appThemeColor1,
                    size: 24,
                  ),
                ),
                SizedBox(
                  height: 20.h,
                ),
                MediumTextNotoSans(
                  text: 'Access Required',
                  fontWeight: FontWeight.w700,
                  size: 16.h,
                ),
                SizedBox(
                  height: 14.h,
                ),
                Wrap(
                  children: [
                    SmallTextNotoSans(
                      text: 'Allow ',
                      fontWeight: FontWeight.w400,
                      size: 14.h,
                    ),
                    SmallTextNotoSans(
                      text: '${BaseConfig.APP_NAME} Vision ',
                      fontWeight: FontWeight.w700,
                      size: 14.h,
                    ),
                    SmallTextNotoSans(
                      text: 'to access your ',
                      fontWeight: FontWeight.w400,
                      size: 14.h,
                    ),
                  ],
                ),
                SmallTextNotoSans(
                  text: 'Location.',
                  fontWeight: FontWeight.w400,
                  size: 14.h,
                ),
                SizedBox(
                  height: 16.h,
                ),
                const Divider(
                  color: BaseConfig.borderColor,
                ),
                SizedBox(
                  height: 16.h,
                ),
                Row(
                  children: [
                    Expanded(
                      child: SizedBox(
                        height: 44.h,
                        child: gradientBtn(
                          text: 'Deny',
                          buttonColor: Colors.white,
                          horizonPadding: 5,
                          textColor: BaseConfig.appThemeColor1,
                          onPressed: () {
                            Get.back();
                          },
                        ),
                      ),
                    ),
                    Expanded(
                      child: SizedBox(
                        height: 44.h,
                        child: gradientBtn(
                          text: 'Allow',
                          horizonPadding: 5,
                          onPressed: () {
                            cityController.fetchPosition();
                            Get.back();
                          },
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
      barrierDismissible: false,
    );
  }

  void _detectLocationLand() {
    Get.dialog(
      AlertDialog(
        insetPadding: EdgeInsets.symmetric(horizontal: 16.w),
        contentPadding: EdgeInsets.symmetric(vertical: 20.h, horizontal: 16.w),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8.r),
        ),
        backgroundColor: BaseConfig.mainBackgroundColor,
        content: SizedBox(
          // height: 258.h,
          width: Get.width,
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Container(
                  width: 35.w,
                  height: 100.h,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(50),
                    color: BaseConfig.lightIndigoColor,
                  ),
                  child: Icon(
                    Icons.location_on_outlined,
                    color: BaseConfig.appThemeColor1,
                    size: 15.sp,
                  ),
                ),
                SizedBox(
                  height: 20.h,
                ),
                MediumTextNotoSans(
                  text: 'Access Required',
                  fontWeight: FontWeight.w700,
                  size: 7.sp,
                ),
                SizedBox(
                  height: 14.h,
                ),
                Wrap(
                  children: [
                    SmallTextNotoSans(
                      text: 'Allow ',
                      fontWeight: FontWeight.w400,
                      size: 7.sp,
                    ),
                    SmallTextNotoSans(
                      text: '${BaseConfig.APP_NAME} Vision ',
                      fontWeight: FontWeight.w700,
                      size: 7.sp,
                    ),
                    SmallTextNotoSans(
                      text: 'to access your ',
                      fontWeight: FontWeight.w400,
                      size: 7.sp,
                    ),
                  ],
                ),
                SmallTextNotoSans(
                  text: 'Location.',
                  fontWeight: FontWeight.w400,
                  size: 7.sp,
                ),
                SizedBox(
                  height: 16.h,
                ),
                const Divider(
                  color: BaseConfig.borderColor,
                ),
                SizedBox(
                  height: 16.h,
                ),
                Row(
                  children: [
                    Expanded(
                      child: SizedBox(
                        height: 50.h,
                        child: gradientBtn(
                          text: 'Deny',
                          fontSize: 8.sp,
                          buttonColor: Colors.white,
                          horizonPadding: 5,
                          textColor: BaseConfig.appThemeColor1,
                          onPressed: () {
                            Get.back();
                          },
                        ),
                      ),
                    ),
                    Expanded(
                      child: SizedBox(
                        height: 50.h,
                        child: gradientBtn(
                          text: 'Allow',
                          fontSize: 8.sp,
                          horizonPadding: 5,
                          onPressed: () {
                            cityController.fetchPosition();
                            Get.back();
                          },
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
      barrierDismissible: false,
    );
  }

  **/
}
