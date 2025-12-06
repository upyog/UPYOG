import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/MyCity/screens/home/calender_home/calender_home.dart';
import 'package:mobile_app/MyCity/screens/home/official_home/ulb_service_management/ulb_service_managment.dart';
import 'package:mobile_app/MyCity/widget/custom_expansion/expansion_icon_card.dart';
import 'package:mobile_app/MyCity/widget/header/city_header.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/utils.dart';

class ReportsAndDashboardScreen extends StatefulWidget {
  const ReportsAndDashboardScreen({super.key});

  @override
  State<ReportsAndDashboardScreen> createState() =>
      _ReportsAndDashboardScreenState();
}

class _ReportsAndDashboardScreenState extends State<ReportsAndDashboardScreen> {
  final List<TextModel> _textModel = [
    TextModel(
      title: 'ULB Dashboard',
      icon: BaseConfig.g2gServiceManagementIcon,
      sections: [
        'ICCC Analytics',
        '311 Request Analytics',
        'E-Governance Service Analytics (G2G Reports)',
        'E-Governance Service Analytics (G2C | G2B Services)',
      ],
    ),
    TextModel(
      title: 'Executive Dashboard (ICCC & e-Gov)',
      icon: BaseConfig.eGovernanceServiceIcon,
      sections: [
        'Service wise e-Governance Analytics',
        'ICCC Snapshot (chronic sites)',
        'Service wise SLA% and average TAT',
        '311 resolution % & avg TAT',
        'Government Scheme Utilization %',
        'Works and Vendor Management Snapshot',
      ],
    ),
  ];

  late List<RxBool> isExpanded;
  List<TextModel> filteredTextModel = [];
  final TextEditingController _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    isExpanded = List.generate(_textModel.length, (index) => RxBool(false));
    filteredTextModel = _textModel;
    _searchController.addListener(_onSearchChanged);
  }

  void _onSearchChanged() {
    setState(() {
      final query = _searchController.text.toLowerCase();
      filteredTextModel = _textModel
          .where(
            (item) =>
                item.title.toLowerCase().contains(query) ||
                (item.sections?.any(
                      (section) => section.toLowerCase().contains(query),
                    ) ??
                    false),
          )
          .toList();
    });
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  bool isCalender = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: MyCityHeader(
        title: isCalender ? 'Calender' : 'Reports and Dashboard',
        isBackButton: isCalender,
        showBack: true,
        calenderOnPressed: () {
          setState(() {
            isCalender = !isCalender;
          });
        },
      ),
      body: isCalender
          ? const CalenderHomeScreen()
          : Padding(
              padding: const EdgeInsets.all(8.0),
              child: Column(
                children: [
                  // SizedBox(height: 10.h),
                  // Container(
                  //   decoration: BoxDecoration(
                  //     borderRadius: BorderRadius.circular(30),
                  //     gradient: const LinearGradient(
                  //       colors: [
                  //         Color(0xfff3af81),
                  //         Color(0xffbf4720),
                  //       ],
                  //       begin: Alignment.topLeft,
                  //       end: Alignment.bottomRight,
                  //     ),
                  //   ),
                  //   child: textFormFieldNormal(
                  //     context,
                  //     'Search',
                  //     controller: _searchController,
                  //     isFilled: false,
                  //     hintTextColor: BaseConfig.mainBackgroundColor,
                  //     cursorColor: BaseConfig.mainBackgroundColor,
                  //     borderRadius: BorderRadius.all(Radius.circular(30.r)),
                  //     focusBorderColor: BaseConfig.textColor,
                  //     prefixIcon: const Icon(
                  //       Icons.search,
                  //       color: BaseConfig.mainBackgroundColor,
                  //     ),
                  //     suffixIcon: IconButton(
                  //       onPressed: () {
                  //         //TODO: Voice search
                  //       },
                  //       icon: Padding(
                  //         padding: EdgeInsets.only(left: 12.w, right: 12.w),
                  //         child: Icon(
                  //           Icons.mic_none_outlined,
                  //           color:
                  //               BaseConfig.mainBackgroundColor.withValues(alpha:0.7),
                  //         ),
                  //       ),
                  //     ),
                  //   ),
                  // ),
                  SizedBox(height: 10.h),
                  Expanded(
                    child: filteredTextModel.isEmpty
                        ? Center(
                            child: Text(
                              'No results found',
                              style: TextStyle(fontSize: 16.sp),
                            ),
                          )
                        : ListView.builder(
                            itemCount: filteredTextModel.length,
                            itemBuilder: (context, index) {
                              final item = filteredTextModel[index];
                              return ExpansionIconCard(
                                item: item,
                                isExpanded: isExpanded[index],
                                onExpansionTap: () {
                                  isExpanded[index].value =
                                      !isExpanded[index].value;
                                },
                                onItemTap: (section) {
                                  dPrint(section);
                                  Get.toNamed(AppRoutes.EMP_LOGIN);
                                },
                              );
                            },
                          ),
                  ),
                ],
              ),
            ),
    );
  }
}
