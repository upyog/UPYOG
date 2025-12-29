import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/dashboard_icon_role.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class HomeGlobalSearch extends StatefulWidget {
  const HomeGlobalSearch({super.key});

  @override
  State<HomeGlobalSearch> createState() => _HomeGlobalSearchState();
}

class _HomeGlobalSearchState extends State<HomeGlobalSearch> {
  TextEditingController searchController = TextEditingController();
  bool isSearching = false;

  List<DashboardIcon> filteredData = [];
  List<DashboardIcon> filteredTopServicesData = [];
  List<DashboardIcon> searchHistory = [];

  @override
  void initState() {
    super.initState();
    filteredData = BaseConfig.globalSearchList;
    filteredTopServicesData = BaseConfig.globalTopServicesSearch;
  }

  void filterSearchResults(String query) {
    List<DashboardIcon> dummyList = [];
    List<DashboardIcon> topServices = [];
    if (query.isNotEmpty) {
      dummyList = BaseConfig.globalSearchList
          .where(
            (item) => item.title.toLowerCase().contains(query.toLowerCase()),
          )
          .toList();

      topServices = BaseConfig.globalTopServicesSearch
          .where(
            (item) => item.title.toLowerCase().contains(query.toLowerCase()),
          )
          .toList();

      setState(() {
        filteredData = dummyList;
        filteredTopServicesData = topServices;
      });

      //addToSearchHistory(query);
    } else {
      setState(() {
        filteredData = BaseConfig.globalSearchList;
        filteredTopServicesData = BaseConfig.globalTopServicesSearch;
      });
    }
  }

  void addToSearchHistory(String query) {
    if (query.isNotEmpty) {
      if (!searchHistory
          .any((item) => item.title.toLowerCase() == query.toLowerCase())) {
        setState(() {
          searchHistory.add(DashboardIcon(title: query, icon: ""));

          if (searchHistory.length > 5) {
            searchHistory.removeAt(0);
          }
        });
      }
    }
  }

  void clearRecentSearches() async {
    setState(() {
      searchHistory.clear();
    });
  }

  void navigateToRoute(String title) {
    final routes = {
      // Property Tax Routes
      'Property Tax': AppRoutes.MY_PROPERTIES,
      'Property Tax My Applications': AppRoutes.PROPERTY_APPLICATIONS,
      'Property Tax My Properties': AppRoutes.PROPERTY_TAX,
      'Property Tax My Bills': AppRoutes.PROPERTY_MY_BILLS_SCREEN,
      'Property Tax My Payments': AppRoutes.PROPERTY_MY_PAYMENT_SCREEN,
      'Property Tax My Certificates': AppRoutes.PROPERTY_MY_CERTIFICATES,

      // Trade License Routes
      'Trade License': AppRoutes.TRADE_LICENSE_APPLICATIONS,
      'Trade License My Applications': AppRoutes.NEW_TL_APPLICATIONS,
      'Trade License My Certificates Approved':
          AppRoutes.TRADE_LICENSE_APPROVED,

      // Water Routes
      'Water': AppRoutes.WATER_SEWERAGE,
      'Water My Applications': AppRoutes.WATER_MY_APPLICATIONS,
      'Water My Bills': AppRoutes.WATER_MY_BILLS,
      'Water My Payments': AppRoutes.WATER_MY_PAYMENT,
      'Water My Certificates': AppRoutes.WATER_MY_CERTIFICATES,

      // Sewerage Routes
      'Sewerage': AppRoutes.WATER_SEWERAGE,
      'Sewerage My Applications': AppRoutes.SEWERAGE_MY_APPLICATIONS,
      'Sewerage My Bills': AppRoutes.SEWERAGE_MY_BILLS,
      'Sewerage My Payments': AppRoutes.SEWERAGE_MY_PAYMENT,
      'Sewerage My Certificates': AppRoutes.SEWERAGE_MY_CERTIFICATES,

      // Grievances Routes
      'Grievances': AppRoutes.GRIEVANCES_SCREEN,
      'Grievances My Applications': AppRoutes.GRIEVANCES_COMPLAINTS_VIEW_ALL,

      // Building Plan Approval Routes
      'Building Plan Approval/ OBPS': AppRoutes.BUILDING_APPLICATION,
      'OBPS Permit Applications': AppRoutes.BPA_PERMIT_APPLICATION,
      'OBPS Occupancy Certificate': AppRoutes.BPA_OCC_CERTIFICATE,

      // General Routes
      'Select Location/ Location': AppRoutes.HOME_SELECT_CITY,
      'My Certificates': AppRoutes.HOME_MY_CERTIFICATES,
      'Profile/ Edit Profile': AppRoutes.PROFILE,
      'Payments': AppRoutes.HOME_MY_PAYMENTS,
    };

    if (routes.containsKey(title)) {
      Get.toNamed(routes[title]!);
    }
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Container(
            padding: const EdgeInsets.fromLTRB(10, 60, 10, 10),
            decoration: const BoxDecoration(
              //borderRadius: BorderRadius.circular(15),
              gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: <Color>[BaseConfig.appThemeColor1, Colors.white],
              ),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Container(
                  padding: const EdgeInsets.all(5),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(15),
                    color: Colors.white,
                    border: Border.all(
                      width: 2,
                      color: BaseConfig.appThemeColor1,
                    ),
                  ),
                  alignment: Alignment.center,
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      IconButton(
                        onPressed: () {
                          Navigator.of(context).pop();
                        },
                        icon: const Icon(
                          Icons.arrow_back_ios,
                          size: 20,
                        ),
                      ),
                      Expanded(
                        child: TextField(
                          enabled: true,
                          enableSuggestions: true,
                          controller: searchController,
                          textInputAction: TextInputAction.done,
                          onChanged: (value) {
                            filterSearchResults(value);
                          },
                          onSubmitted: (value) {
                            if (value.isNotEmpty) {
                              addToSearchHistory(value);
                            }
                          },
                          autofocus: true,
                          style: TextStyle(
                            color: Colors.black,
                            fontWeight: FontWeight.w600,
                            fontSize: o == Orientation.portrait ? 14.sp : 7.sp,
                          ),
                          decoration: InputDecoration(
                            contentPadding: EdgeInsets.symmetric(
                              vertical: o == Orientation.portrait ? 10.w : 5.w,
                              horizontal:
                                  o == Orientation.portrait ? 10.w : 5.w,
                            ),
                            fillColor: Colors.white,
                            filled: true,
                            hintText: 'Search...',
                            hintStyle: TextStyle(
                              color: BaseConfig.greyColor4,
                              fontWeight: FontWeight.w600,
                              fontSize:
                                  o == Orientation.portrait ? 14.sp : 7.sp,
                            ),
                            border: InputBorder.none,
                            suffixIcon: searchController.text.isNotEmpty
                                ? IconButton(
                                    icon: const Icon(
                                      Icons.close,
                                      color: Colors.black,
                                    ),
                                    onPressed: () {
                                      setState(() {
                                        searchController.clear();
                                        filterSearchResults('');
                                      });
                                    },
                                  )
                                : null,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 25),
                const Row(
                  children: [
                    SmallSelectableTextNotoSans(
                      text: 'Try - ',
                      color: BaseConfig.greyColor4,
                      fontWeight: FontWeight.w600,
                    ),
                    SmallSelectableTextNotoSans(
                      text: "Property Tax",
                      color: BaseConfig.textColor,
                      fontWeight: FontWeight.w600,
                    ),
                  ],
                ),
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(10.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const BigSelectableTextNotoSans(
                  text: 'Top Services',
                  color: BaseConfig.textColor,
                  fontWeight: FontWeight.w600,
                  size: 18,
                ),
                SizedBox(
                  height: 60,
                  width: double.infinity,
                  child: ListView.builder(
                    scrollDirection: Axis.horizontal,
                    itemCount: isNotNullOrEmpty(filteredTopServicesData)
                        ? filteredTopServicesData.length
                        : BaseConfig.globalTopServicesSearch.length,
                    itemBuilder: (context, index) => Card(
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(15),
                      ),
                      child: Padding(
                        padding: const EdgeInsets.all(10.0),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceAround,
                          children: [
                            Container(
                              decoration: BoxDecoration(
                                color: BaseConfig.appThemeColor1,
                                borderRadius: BorderRadius.circular(15),
                              ),
                              padding: const EdgeInsets.all(5),
                              child: SvgPicture.asset(
                                isNotNullOrEmpty(filteredTopServicesData)
                                    ? filteredTopServicesData[index].icon
                                    : BaseConfig
                                        .globalTopServicesSearch[index].icon,
                                height: 24,
                                width: 24,
                                fit: BoxFit.contain,
                                colorFilter: const ColorFilter.mode(
                                  BaseConfig.mainBackgroundColor,
                                  BlendMode.srcIn,
                                ),
                              ),
                            ),
                            const SizedBox(width: 10),
                            SmallSelectableTextNotoSans(
                              text: isNotNullOrEmpty(
                                filteredTopServicesData,
                              )
                                  ? filteredTopServicesData[index].title
                                  : BaseConfig
                                      .globalTopServicesSearch[index].title,
                              fontWeight: FontWeight.w600,
                              color: BaseConfig.textColor,
                            ),
                          ],
                        ),
                      ).ripple(
                        () {
                          navigateToRoute(
                            isNotNullOrEmpty(filteredTopServicesData)
                                ? filteredTopServicesData[index].title
                                : BaseConfig
                                    .globalTopServicesSearch[index].title,
                          );
                        },
                        customBorder: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(15),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
          if (searchController.text.isEmpty && searchHistory.isNotEmpty)
            Padding(
              padding: const EdgeInsets.all(10.0),
              child: SingleChildScrollView(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const SmallSelectableTextNotoSans(
                          text: 'Recent Searches',
                          color: BaseConfig.greyColor4,
                          fontWeight: FontWeight.w600,
                        ),
                        const SmallSelectableTextNotoSans(
                          text: 'Clear',
                          color: BaseConfig.greyColor4,
                          fontWeight: FontWeight.w600,
                        ).ripple(() {
                          clearRecentSearches();
                        }),
                      ],
                    ),
                    for (DashboardIcon search in searchHistory.reversed)
                      ListTile(
                        title: SmallSelectableTextNotoSans(
                          text: search.title,
                          color: BaseConfig.textColor,
                          fontWeight: FontWeight.w600,
                        ),
                        leading: const Icon(
                          Icons.history,
                          color: BaseConfig.textColor,
                          size: 20,
                        ),
                        onTap: () {
                          searchController.text = search.title;
                          filterSearchResults(search.title);
                          setState(() {
                            isSearching = false;
                          });
                        },
                        trailing: const Icon(
                          Icons.north_west,
                          color: BaseConfig.textColor,
                          size: 20,
                        ),
                      ),
                  ],
                ),
              ),
            ),
          if (filteredData.isNotEmpty && searchController.text.isNotEmpty)
            const Padding(
              padding: EdgeInsets.fromLTRB(10, 0, 10, 0),
              child: BigSelectableTextNotoSans(
                text: 'Search Result',
                color: BaseConfig.textColor,
                fontWeight: FontWeight.w600,
                size: 18,
              ),
            ),
          if (filteredData.isNotEmpty && searchController.text.isNotEmpty)
            Expanded(
              child: ListView.builder(
                itemCount: filteredData.length,
                physics: const AlwaysScrollableScrollPhysics(),
                itemBuilder: (context, index) {
                  return Card(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(15),
                    ),
                    child: ListTile(
                      leading: Container(
                        decoration: BoxDecoration(
                          color: BaseConfig.appThemeColor1,
                          borderRadius: BorderRadius.circular(30),
                        ),
                        padding: const EdgeInsets.all(6),
                        child: SvgPicture.asset(
                          filteredData[index].icon,
                          height: 24,
                          width: 24,
                          fit: BoxFit.contain,
                          colorFilter: const ColorFilter.mode(
                            BaseConfig.mainBackgroundColor,
                            BlendMode.srcIn,
                          ),
                        ),
                      ),
                      trailing: const Icon(
                        Icons.arrow_forward_ios,
                        color: BaseConfig.appThemeColor1,
                        size: 20,
                      ),
                      title: SmallSelectableTextNotoSans(
                        text: filteredData[index].title,
                        color: BaseConfig.textColor,
                        fontWeight: FontWeight.w600,
                      ),
                    ).ripple(
                      () {
                        navigateToRoute(filteredData[index].title);
                      },
                      customBorder: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(15),
                      ),
                    ),
                  );
                },
              ),
            ),
          if (filteredData.isEmpty && searchController.text.isNotEmpty)
            SizedBox(
              height: Get.height * 0.3,
              child: const Center(
                child: MediumTextNotoSans(
                  text: 'No results found',
                  color: BaseConfig.greyColor4,
                  fontWeight: FontWeight.w600,
                  size: 14,
                ),
              ),
            ),
        ],
      ),
    );
  }
}
