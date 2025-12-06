import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/drawer/employee/emp_drawer_widget.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/location_controller.dart';
import 'package:mobile_app/controller/obps_dynamic_form_controller.dart';
import 'package:mobile_app/controller/water_controller.dart';
import 'package:mobile_app/screens/employee/emp_dashboard/emp_dashboard.dart';
import 'package:mobile_app/screens/employee/emp_home_city/emp_home_city.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/employee/emp_nav_bar.dart';

class EmpBottomNavigationPage extends StatefulWidget {
  const EmpBottomNavigationPage({super.key});

  @override
  State<EmpBottomNavigationPage> createState() =>
      _EmpBottomNavigationPageState();
}

class _EmpBottomNavigationPageState extends State<EmpBottomNavigationPage> {
  final cityController = Get.find<CityController>();
  final _authController = Get.find<AuthController>();
  final _dynamicFormController = Get.find<ObpsDynamicFormController>();
  final _waterController = Get.find<WaterController>();

  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
  int selectedTab = 0;
  List<Widget> pages = [
    const EmpDashboard(),
    const EmpHomeCityScreen(),
    const SizedBox.shrink(),
  ];

  @override
  void initState() {
    SystemChrome.setEnabledSystemUIMode(
      SystemUiMode.manual,
      overlays: [SystemUiOverlay.top, SystemUiOverlay.bottom],
    );
    super.initState();
    _init();
  }

  void _init() async {
    if (!_authController.isValidUser) return;

    //Fetch city
    cityController.fetchSelectedCity();

    //ObpsMdms call
    final isObpsHasPermission = _authController.token!.userRequest!.roles!.any(
      (element) =>
          element.code!.contains(InspectorType.BPA_FIELD_INSPECTOR.name),
    );

    //WS/SW mdms call
    final isWaterHasPermission = _authController.token!.userRequest!.roles!.any(
      (element) =>
          element.code!.contains(InspectorType.WS_FIELD_INSPECTOR.name) ||
          element.code!.contains(InspectorType.SW_FIELD_INSPECTOR.name),
    );

    if (isObpsHasPermission) {
      await _dynamicFormController.getMdmsObps();
    }
    if (isWaterHasPermission) {
      await _waterController.getMdmsWsSw();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _scaffoldKey,
      extendBody: true,
      endDrawer: EmpDrawerWidget(),
      body: pages[selectedTab],
      resizeToAvoidBottomInset: false,
      bottomNavigationBar: EmpNavBar(
        pageIndex: selectedTab,
        onTap: (index) {
          if (index == 2) {
            _scaffoldKey.currentState?.openEndDrawer();
          } else {
            setState(() {
              selectedTab = index;
              dPrint(selectedTab);
            });
          }
        },
      ),
    );
  }
}
