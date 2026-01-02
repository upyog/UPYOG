import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/MyCity/screens/home/calender_home/calender_home.dart';
import 'package:mobile_app/MyCity/widget/custom_expansion/expansion_icon_card.dart';
import 'package:mobile_app/MyCity/widget/header/city_header.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/utils.dart';

class TextModel {
  String title;
  String? icon;
  IconData? iconData;
  List<String>? sections;
  bool isNew;

  TextModel({
    required this.title,
    this.icon,
    this.iconData,
    this.sections,
    this.isNew = false,
  });
}

class UlbServiceManagementScreen extends StatefulWidget {
  const UlbServiceManagementScreen({super.key});

  @override
  State<UlbServiceManagementScreen> createState() =>
      _UlbServiceManagementScreenState();
}

class _UlbServiceManagementScreenState
    extends State<UlbServiceManagementScreen> {
  final List<TextModel> _textModel = [
    TextModel(
      title: 'G2G Service Management',
      icon: BaseConfig.g2gServiceManagementIcon,
      sections: [
        'F&A (Basic Reports in pdf)',
        'Asset Management',
        'HRMS',
        'File Management',
      ],
    ),
    TextModel(
      title: 'E-Governance Services (G2G, B2B service management)',
      icon: BaseConfig.eGovernanceServiceIcon,
      sections: [
        'Trade License Issuance and Payment',
        'Miscellaneous Collections',
        'Birth & Death',
        'Pet Registration',
        'Venue Booking',
        'Advertisement',
        'Water and Sewerage Connection Management',
      ],
    ),
    TextModel(
      title: '311 Request Management',
      icon: BaseConfig.requestManagementIcon,
      sections: [
        'Pothole Repair',
        'Street Light Outage',
        'Traffic signal Malfunction',
        'Garbage Collection',
        'Animal Control',
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
        title: isCalender ? 'Calender' : 'UlB Service Management',
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
                  SizedBox(height: 20.h),
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
                              ).marginOnly(bottom: 10.h);
                            },
                          ),
                  ),
                ],
              ),
            ),
    );
  }
}
