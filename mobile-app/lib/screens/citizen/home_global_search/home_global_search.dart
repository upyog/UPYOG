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
    List<DashboardIcon> topServices =[];
    if (query.isNotEmpty) {
      dummyList = BaseConfig.globalSearchList
          .where((item) => item.title.toLowerCase().contains(query.toLowerCase()))
          .toList();

      topServices = BaseConfig.globalTopServicesSearch
          .where((item) => item.title.toLowerCase().contains(query.toLowerCase()))
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
      if (!searchHistory.any((item) => item.title.toLowerCase() == query.toLowerCase())) {
        setState(() {
          searchHistory.add(DashboardIcon(title: query, icon: ""));

          if (searchHistory.length > 5) {
            searchHistory.removeAt(0);
          }
        });
      }
    }
  }


  void clearRecentSearches() async{
    setState(() {
      searchHistory.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context,o){
        return Scaffold(
          body: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Container(
                padding: const EdgeInsets.fromLTRB(10, 60, 10, 10),
                decoration: const BoxDecoration(
                  //borderRadius: BorderRadius.circular(15),
                  gradient:LinearGradient(
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    colors: <Color>[BaseConfig.appThemeColor1, Colors.white ],),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      padding:const EdgeInsets.all(5),
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(15),
                        color: Colors.white,
                        border: Border.all(width: 2,color: BaseConfig.appThemeColor1),
                      ),
                      child: Row(
                        children: [
                          IconButton(onPressed: (){Navigator.of(context).pop();}, icon: const Icon(Icons.arrow_back_ios,size: 20,)),
                          SizedBox(
                            width: o == Orientation.portrait ?270.w:300.w,
                            child: TextField(
                              enabled: true,
                              enableSuggestions: true,
                              controller: searchController,
                              onChanged: (value) {
                                filterSearchResults(value);
                              },
                              onSubmitted: (value) {
                                if (value.isNotEmpty) {
                                  addToSearchHistory(value);
                                }
                              },
                              autofocus: true,
                              style:  TextStyle(color: Colors.black,fontWeight: FontWeight.w600,fontSize: o == Orientation.portrait ?14.sp:7.sp),
                              decoration: InputDecoration(
                                contentPadding: EdgeInsets.all(o == Orientation.portrait ? 8.w : 1.w),
                                fillColor: Colors.white,
                                filled: true,
                                hintText: 'Search...',
                                hintStyle: TextStyle(color: BaseConfig.greyColor4,fontWeight: FontWeight.w600,fontSize: o == Orientation.portrait ?14.sp:7.sp),
                                border: InputBorder.none,
                                suffixIcon: searchController.text.isNotEmpty
                                    ? IconButton(
                                  icon:const Icon(Icons.close, color: Colors.black),
                                  onPressed: () {
                                    setState(() {
                                      searchController.clear();
                                      filterSearchResults('');
                                    });
                                  },
                                ) : null,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 25),
                    const Row(
                      children: [
                        MediumTextNotoSans(text: 'Try - ',color: BaseConfig.greyColor4,fontWeight: FontWeight.w600,size: 12,),
                        MediumTextNotoSans(text: '"Property Tax"',color: BaseConfig.textColor,fontWeight: FontWeight.w600,size: 12),
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
                    const BigTextNotoSans(text: 'Top Services',color: BaseConfig.textColor,fontWeight: FontWeight.w600,size: 18,),
                    SizedBox(
                      height: 60,
                      width: double.infinity,
                      child: ListView.builder(
                        scrollDirection: Axis.horizontal,
                        itemCount: isNotNullOrEmpty(filteredTopServicesData)?filteredTopServicesData.length:BaseConfig.globalTopServicesSearch.length,
                        itemBuilder: (context, index) =>
                            Center(
                              child: Card(
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(15),
                                ),
                                child: Padding(
                                  padding: const EdgeInsets.all(10.0),
                                  child: Row(
                                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                                    children: [
                                      IconButton.filled(
                                        style: IconButton.styleFrom(
                                          backgroundColor: BaseConfig.appThemeColor1,
                                        ),
                                        onPressed: () {},
                                        icon: SvgPicture.asset(
                                          isNotNullOrEmpty(filteredTopServicesData)?filteredTopServicesData[index].icon:BaseConfig.globalTopServicesSearch[index].icon,
                                          height: 24,
                                          width: 24,
                                          fit: BoxFit.contain,
                                          colorFilter: const ColorFilter.mode(
                                            BaseConfig.mainBackgroundColor,
                                            BlendMode.srcIn,
                                          ),
                                        ),
                                      ),
                                      MediumTextNotoSans(text:isNotNullOrEmpty(filteredTopServicesData)?filteredTopServicesData[index].title:BaseConfig.globalTopServicesSearch[index].title,fontWeight: FontWeight.w600,color: BaseConfig.textColor,size: 12,),
                                    ],
                                  ),
                                ),
                              ).ripple((){
                                if(filteredTopServicesData[index].title ==  'Property Tax' || BaseConfig.globalTopServicesSearch[index].title ==  'Property Tax'){
                                  Get.toNamed(AppRoutes.MY_PROPERTIES);
                                }
                                if(filteredTopServicesData[index].title ==  'Trade License' || BaseConfig.globalTopServicesSearch[index].title ==  'Trade License'){
                                  Get.toNamed(AppRoutes.TRADE_LICENSE_APPLICATIONS,);
                                }
                                if(filteredTopServicesData[index].title ==  'Water' || BaseConfig.globalTopServicesSearch[index].title ==  'Water'){
                                  Get.toNamed(AppRoutes.WATER_SEWERAGE);
                                }
                                if(filteredTopServicesData[index].title ==  'Sewerage' || BaseConfig.globalTopServicesSearch[index].title ==  'Sewerage'){
                                  Get.toNamed(AppRoutes.WATER_SEWERAGE);
                                }
                                if(filteredTopServicesData[index].title ==  'Grievances' || BaseConfig.globalTopServicesSearch[index].title ==  'Grievances'){
                                  Get.toNamed(AppRoutes.GRIEVANCES_SCREEN);
                                }
                                if(filteredTopServicesData[index].title ==  'Building Plan Approval/ OBPS' || BaseConfig.globalTopServicesSearch[index].title ==  'Building Plan Approval/ OBPS'){
                                  Get.toNamed(AppRoutes.BUILDING_APPLICATION);
                                }
                              }),
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
                            const MediumTextNotoSans(
                              text: 'Recent Searches', color: BaseConfig.greyColor4,fontWeight: FontWeight.w600,size: 12,
                            ),
                            const MediumTextNotoSans(
                              text: 'Clear',color: BaseConfig.greyColor4,fontWeight: FontWeight.w600,size: 12,
                            ).ripple((){
                              clearRecentSearches();
                            }),
                          ],
                        ),
                        for (DashboardIcon search in searchHistory.reversed)
                          ListTile(
                            title: MediumTextNotoSans(
                              text: search.title,
                              color: BaseConfig.textColor,
                              fontWeight: FontWeight.w600,
                              size: 12,
                            ),
                            leading: const Icon(Icons.history, color: BaseConfig.textColor, size: 20),
                            onTap: () {
                              searchController.text = search.title;
                              filterSearchResults(search.title);
                              setState(() {
                                isSearching = false;
                              });
                            },
                            trailing: const Icon(Icons.north_west, color: BaseConfig.textColor, size: 20),
                          ),

                      ],
                    ),
                  ),
                ),
              if (filteredData.isNotEmpty && searchController.text.isNotEmpty)
                const Padding(
                  padding: EdgeInsets.fromLTRB(10,0,10,0),
                  child: BigTextNotoSans(text: 'Search Result',color: BaseConfig.textColor,fontWeight: FontWeight.w600,size: 18,),
                ),
              if (filteredData.isNotEmpty && searchController.text.isNotEmpty)
                Expanded(
                  child: ListView.builder(
                    itemCount: filteredData.length,
                    itemBuilder: (context, index) {
                      return Card(
                        child: ListTile(
                          leading: IconButton.filled(
                            style: IconButton.styleFrom(
                              backgroundColor: BaseConfig.appThemeColor1,
                            ),
                            onPressed: () {},
                            icon: SvgPicture.asset(
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
                          trailing: const Icon(Icons.arrow_forward_ios,color: BaseConfig.appThemeColor1,size: 20,),
                          title: MediumTextNotoSans(text:filteredData[index].title,color: BaseConfig.textColor,fontWeight: FontWeight.w600,size: 12),
                        ),
                      ).ripple((){
                        if(filteredData[index].title ==  'Property Tax'){
                          Get.toNamed(AppRoutes.MY_PROPERTIES);
                        }
                        if(filteredData[index].title ==  'Property Tax My Applications'){
                          Get.toNamed(
                            AppRoutes.PROPERTY_APPLICATIONS,
                          );
                        }
                        if(filteredData[index].title ==  'Property Tax My Properties'){
                          Get.toNamed(AppRoutes.PROPERTY_TAX);
                        }
                        if(filteredData[index].title ==  'Property Tax My Bills'){
                          Get.toNamed(AppRoutes.PROPERTY_MY_BILLS_SCREEN);
                        }
                        if(filteredData[index].title ==  'Property Tax My Payments'){
                          Get.toNamed(AppRoutes.PROPERTY_MY_PAYMENT_SCREEN,);
                        }
                        if(filteredData[index].title ==  'Trade License'){
                          Get.toNamed(AppRoutes.TRADE_LICENSE_APPLICATIONS,);
                        }
                        if(filteredData[index].title ==  'Trade License My Applications'){
                          Get.toNamed(
                            AppRoutes.TL_VIEW_ALL,
                          );
                        }
                        if(filteredData[index].title ==  'Water'){
                          Get.toNamed(AppRoutes.WATER_SEWERAGE);
                        }
                        if(filteredData[index].title ==  'Water My Applications'){
                          Get.toNamed(
                            AppRoutes.WATER_MY_APPLICATIONS,
                          );
                        }
                        if(filteredData[index].title ==  'Water My Bills'){
                          Get.toNamed(AppRoutes.WATER_MY_BILLS);
                        }
                        if(filteredData[index].title ==  'Water My Payments'){
                          Get.toNamed(AppRoutes.WATER_MY_PAYMENT);
                        }
                        if(filteredData[index].title ==  'Sewerage'){
                          Get.toNamed(AppRoutes.WATER_SEWERAGE);
                        }
                        if(filteredData[index].title ==  'Sewerage My Applications'){
                          Get.toNamed(
                            AppRoutes.SEWERAGE_MY_APPLICATIONS,
                          );
                        }
                        if(filteredData[index].title ==  'Sewerage My Bills'){
                          Get.toNamed(AppRoutes.SEWERAGE_MY_BILLS);
                        }
                        if(filteredData[index].title ==  'Sewerage My Payments'){
                          Get.toNamed(AppRoutes.SEWERAGE_MY_PAYMENT);
                        }
                        if(filteredData[index].title ==  'Grievances'){
                          Get.toNamed(AppRoutes.GRIEVANCES_SCREEN);
                        }
                        if(filteredData[index].title ==  'Grievances My Applications'){
                          Get.toNamed(
                            AppRoutes.GRIEVANCES_COMPLAINTS_VIEW_ALL,
                          );
                        }
                        if(filteredData[index].title ==  'Building Plan Approval/ OBPS'){
                          Get.toNamed(AppRoutes.BUILDING_APPLICATION);
                        }
                        if(filteredData[index].title ==  'OBPS Permit Applications'){
                          Get.toNamed(AppRoutes.BPA_PERMIT_APPLICATION);
                        }
                        if(filteredData[index].title ==  'OBPS Occupancy Certificate'){
                          Get.toNamed(AppRoutes.BPA_OCC_CERTIFICATE);
                        }
                        if(filteredData[index].title ==  'Select Location/ Location'){
                          Get.toNamed(AppRoutes.HOME_SELECT_CITY);
                        }
                        if(filteredData[index].title ==  'My Certificates'){
                          Get.toNamed(AppRoutes.HOME_MY_CERTIFICATES);
                        }
                        if(filteredData[index].title ==  'Trade License My Certificates Approved'){
                          Get.toNamed(AppRoutes.TRADE_LICENSE_APPROVED);
                        }
                        if(filteredData[index].title ==  'Water My Certificates'){
                          Get.toNamed(AppRoutes.WATER_MY_CERTIFICATES);
                        }
                        if(filteredData[index].title ==  'Sewerage My Certificates'){
                          Get.toNamed(AppRoutes.SEWERAGE_MY_CERTIFICATES);
                        }
                        if(filteredData[index].title ==  'Property Tax My Certificates'){
                          Get.toNamed(AppRoutes.PROPERTY_MY_CERTIFICATES);
                        }
                        if(filteredData[index].title ==  'Profile/ Edit Profile'){
                          Get.toNamed(AppRoutes.PROFILE);
                        }
                        if(filteredData[index].title ==  'Payments'){
                          Get.toNamed(AppRoutes.HOME_MY_PAYMENTS);
                        }
                        if(filteredData[index].title ==  'Property Tax My Payments'){
                          Get.toNamed(AppRoutes.PROPERTY_MY_PAYMENT_SCREEN);
                        }
                        if(filteredData[index].title ==  'Water My Payments'){
                          Get.toNamed(AppRoutes.WATER_MY_PAYMENT);
                        }
                        if(filteredData[index].title ==  'Sewerage My Payments'){
                          Get.toNamed(AppRoutes.SEWERAGE_MY_PAYMENT);
                        }
                      });
                    },
                  ),
                ),
              if (filteredData.isEmpty && searchController.text.isNotEmpty)
                SizedBox(
                  height: Get.height*0.3,
                  child: const Center(
                    child: MediumTextNotoSans(
                      text: 'No results found',color: BaseConfig.greyColor4,fontWeight: FontWeight.w600,size: 14,
                    ),
                  ),
                ),
            ],
          ),
        );
      },
    );
  }
}
