// ignore_for_file: constant_identifier_names

import 'package:get/get.dart';
import 'package:mobile_app/MyCity/screens/home/business_home/business_home.dart';
import 'package:mobile_app/MyCity/screens/home/citizen_home/citizen_home.dart';
import 'package:mobile_app/MyCity/screens/home/official_home/official_home.dart';
import 'package:mobile_app/MyCity/screens/home/official_home/reports_and_dashboard/reports_and_dashboard.dart';
import 'package:mobile_app/MyCity/screens/home/official_home/ulb_service_management/ulb_service_managment.dart';
import 'package:mobile_app/MyCity/screens/home/select_category.dart';
import 'package:mobile_app/MyCity/screens/home/visitor_home/visitor_home.dart';
import 'package:mobile_app/binding/challan_binding.dart';
import 'package:mobile_app/binding/fire_noc_binding.dart';
import 'package:mobile_app/binding/fsm_binding.dart';
import 'package:mobile_app/binding/grievance_binding.dart';
import 'package:mobile_app/screens/citizen/bottom_navigation/bottom_navigation.dart';
import 'package:mobile_app/screens/citizen/building_plan_approval/bpa_my_applications/bpa_occupancy_certificate.dart';
import 'package:mobile_app/screens/citizen/building_plan_approval/bpa_my_applications/bpa_permit_applications.dart';
import 'package:mobile_app/screens/citizen/building_plan_approval/bpa_my_applications/building_detail_screen.dart';
import 'package:mobile_app/screens/citizen/building_plan_approval/bpa_screen.dart';
import 'package:mobile_app/screens/citizen/fire_noc/fire_noc_application.dart';
import 'package:mobile_app/screens/citizen/fire_noc/fire_noc_application_details.dart';
import 'package:mobile_app/screens/citizen/fsm/fsm_application.dart';
import 'package:mobile_app/screens/citizen/fsm/fsm_application_details.dart';
import 'package:mobile_app/screens/citizen/grievance_redressal/grievance_complaints_view_all/grievance_complaints_view_all.dart';
import 'package:mobile_app/screens/citizen/grievance_redressal/grievance_details_screen/grievance_details_screen.dart';
import 'package:mobile_app/screens/citizen/grievance_redressal/grievances_screen.dart';
import 'package:mobile_app/screens/citizen/grievance_redressal/new_grievance_form/new_grievance_form.dart';
import 'package:mobile_app/screens/citizen/home/home_my_certificates.dart';
import 'package:mobile_app/screens/citizen/home/home_screen.dart';
import 'package:mobile_app/screens/citizen/home/my_applications/my_applications.dart';
import 'package:mobile_app/screens/citizen/home/my_certificates/bpa_certificate.dart';
import 'package:mobile_app/screens/citizen/home/my_certificates/property_tax_my_certificates.dart';
import 'package:mobile_app/screens/citizen/home/my_certificates/sewerage_my_certificates.dart';
import 'package:mobile_app/screens/citizen/home/my_certificates/trade_license_my_certificates.dart';
import 'package:mobile_app/screens/citizen/home/my_certificates/water_my_certificates.dart';
import 'package:mobile_app/screens/citizen/home/my_payments/my_payments.dart';
import 'package:mobile_app/screens/citizen/home_global_search/home_global_search.dart';
import 'package:mobile_app/screens/citizen/home_location_choose/home_location_choose.dart';
import 'package:mobile_app/screens/citizen/location_choose/location_choose.dart';
import 'package:mobile_app/screens/citizen/misc_collections/my_challans/my_challans.dart';
import 'package:mobile_app/screens/citizen/notification/notification_screen.dart';
import 'package:mobile_app/screens/citizen/notification/notification_screen_temp.dart';
import 'package:mobile_app/screens/citizen/payments/bills_detail_screen.dart';
import 'package:mobile_app/screens/citizen/profile/profile_screen.dart';
import 'package:mobile_app/screens/citizen/property_tax/my_applications/my_properties_applications_screen.dart';
import 'package:mobile_app/screens/citizen/property_tax/my_applications/my_property_application_details.dart';
import 'package:mobile_app/screens/citizen/property_tax/my_bills/my_bills_screen.dart';
import 'package:mobile_app/screens/citizen/property_tax/my_payments/my_payments_screen.dart';
import 'package:mobile_app/screens/citizen/property_tax/my_properties/properties_details_screen.dart';
import 'package:mobile_app/screens/citizen/property_tax/my_properties/property_tax_screen.dart';
import 'package:mobile_app/screens/citizen/property_tax/my_properties_screen.dart';
import 'package:mobile_app/screens/citizen/sign_up/signup_screen.dart';
import 'package:mobile_app/screens/citizen/trade_license/my_tl_applications/new_tl_applications.dart';
import 'package:mobile_app/screens/citizen/trade_license/my_tl_applications/trade_license_details/tl_details_screen.dart';
import 'package:mobile_app/screens/citizen/trade_license/my_tl_applications/trade_license_renewal.dart';
import 'package:mobile_app/screens/citizen/trade_license/property_information/property_information_screen.dart';
import 'package:mobile_app/screens/citizen/trade_license/trade_license_screen.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/sewerage/sewerage_application/sewerage_my_application_details.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/sewerage/sewerage_application/sewerage_my_application_screen.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/sewerage/sewerage_connections/sewerage_connection_applications.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/sewerage/sewerage_connections/sewerage_connection_details.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/sewerage/sewerage_my_bills/sewerage_bill_screen.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/sewerage/sewerage_my_payments/sewerage_my_payments.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/water/water_application/water_my_applicaiton_details.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/water/water_application/water_my_applications_screen.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/water/water_connections/water_connection_applications.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/water/water_connections/water_connection_details.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/water/water_my_bills/water_my_bills_screen.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/water/water_my_payments/water_my_payments.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/water_property_information/property_information_screen.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/water_sewerage_screen.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/water_view_consumption_detail/ws_view_consumption_detail.dart';
import 'package:mobile_app/screens/citizen/water_sewerage/ws_view_all_screen.dart';
import 'package:mobile_app/screens/employee/emp_bottom_navigation/emp_bottom_navigation.dart';
import 'package:mobile_app/screens/employee/emp_bpa/emp_bpa_details/emp_bpa_obps_details.dart';
import 'package:mobile_app/screens/employee/emp_bpa/emp_bpa_obps.dart';
import 'package:mobile_app/screens/employee/emp_dashboard/emp_dashboard.dart';
import 'package:mobile_app/screens/employee/emp_fire_noc/emp_fire_noc.dart';
import 'package:mobile_app/screens/employee/emp_fire_noc/emp_fire_noc_details/emp_fire_noc_details.dart';
import 'package:mobile_app/screens/employee/emp_grievance/emp_grievance.dart';
import 'package:mobile_app/screens/employee/emp_grievance/emp_grievance_details.dart';
import 'package:mobile_app/screens/employee/emp_login/emp_login.dart';
import 'package:mobile_app/screens/employee/emp_profile/emp_profile_screen.dart';
import 'package:mobile_app/screens/employee/emp_pt/emp_pt.dart';
import 'package:mobile_app/screens/employee/emp_pt/emp_pt_details/emp_pt_details.dart';
import 'package:mobile_app/screens/employee/emp_pt/emp_pt_registration/emp_pt_registration.dart';
import 'package:mobile_app/screens/employee/emp_trade_license/emp_tl_details/emp_tl_details.dart';
import 'package:mobile_app/screens/employee/emp_trade_license/emp_trade_license.dart';
import 'package:mobile_app/screens/employee/emp_trade_license/property_information/property_information_screen.dart';
import 'package:mobile_app/screens/employee/emp_uc_collect/emp_challan_details/emp_challan_details.dart';
import 'package:mobile_app/screens/employee/emp_uc_collect/emp_create_challans/emp_create_uc_collect.dart';
import 'package:mobile_app/screens/employee/emp_uc_collect/emp_challans.dart';
import 'package:mobile_app/screens/employee/emp_water/emp_sw/emp_sewerage.dart';
import 'package:mobile_app/screens/employee/emp_water/emp_sw/emp_sw_details/emp_sw_details.dart';
import 'package:mobile_app/screens/employee/emp_water/emp_water_edit_application/emp_water_edit_application.dart';
import 'package:mobile_app/screens/employee/emp_water/emp_ws/emp_water_details/emp_water_details.dart';
import 'package:mobile_app/screens/employee/emp_water/emp_ws/emp_water_screen.dart';
import 'package:mobile_app/screens/login/login_screen.dart';
import 'package:mobile_app/screens/login/meri_pehchaan_login_screen.dart';
import 'package:mobile_app/screens/select_citizen/select_citizen_new.dart';
import 'package:mobile_app/screens/splash/splash_screen.dart';

class AppRoutes {
  static const String SPLASH = '/SPLASH';
  static const String SELECT_CITIZEN = '/SELECT_CITIZEN';

  /* -------------------------------------------------------------------------- */
  /*                               Citizen Routes                               */
  /* -------------------------------------------------------------------------- */
  static const String HOME = '/HOME';
  static const String BOTTOM_NAV = '/BOTTOM_NAV';
  static const String LOGIN = '/LOGIN';
  static const String LOGIN_MERIPEHCHAAN = '/LOGIN_MERIPEHCHAAN';
  static const String SIGN_UP = '/SIGN_UP';
  static const String GRIEVANCES_SCREEN = '/GRIEVANCES';
  static const String GRIEVANCES_DETAILS_SCREEN = '/GRIEVANCES_DETAILS_SCREEN';
  static const String GRIEVANCES_COMPLAINTS_VIEW_ALL =
      '/GRIEVANCES_COMPLAINTS_VIEW_ALL';
  static const String GRIEVANCES_FORM = '/GRIEVANCES_FORM';
  static const String PROFILE = '/PROFILE';
  static const String TRADE_LICENSE = '/TRADE_LICENSE';
  static const String TRADE_LICENSE_APPLICATIONS =
      '/TRADE_LICENSE_APPLICATIONS';
  static const String TL_DETAILS = '/TL_DETAILS';
  static const String PROPERTY_INFO = '/PROPERTY_INFO';
  static const String LOCATION_CHOOSE = '/LOCATION_CHOOSE';
  static const String NOTIFICATION = '/NOTIFICATION';
  static const String NOTIFICATION_TEMP = '/NOTIFICATION_TEMP';
  // static const String PAYMENTS = '/PAYMENTS';
  static const String PROPERTY_TAX = '/PROPERTY_TAX';
  static const String PROPERTY_APPLICATIONS = '/PROPERTY_APPLICATIONS';
  static const String MY_PROPERTIES = '/MY_PROPERTIES';
  static const String MY_PROPERTIES_DETAILS = '/MY_PROPERTIES_DETAILS';
  static const String PROPERTY_APPLICATIONS_DETAILS =
      '/PROPERTY_APPLICATIONS_DETAILS';
  static const String PROPERTY_MY_PAYMENT_SCREEN =
      '/PROPERTY_MY_PAYMENT_SCREEN';
  static const String PROPERTY_MY_BILLS_SCREEN = '/PROPERTY_MY_BILLS_SCREEN';
  static const String BILL_DETAIL_SCREEN = '/BILL_DETAIL_SCREEN';
  //Building plan Approval
  static const String BUILDING_APPLICATION = '/BD_PLAN_APPROV_APPLICATION';
  static const String BUILDING_PLAN_CERTIFICATE = '/BUILDING_PLAN_CERTIFICATE';
  static const String BPA_PERMIT_APPLICATION = '/BPA_PERMIT_APPLICATION';
  static const String BPA_OCC_CERTIFICATE = '/BPA_OCC_CERTIFICATE';
  static const String BUILDING_APPLICATION_DETAILS =
      '/BUILDING_APPLICATION_DETAILS';
  static const String WATER_SEWERAGE = '/WATER_SEWERAGE';
  static const String WATER_MY_APPLICATIONS = '/WATER_MY_APPLICATIONS';
  static const String SEWERAGE_MY_APPLICATIONS = '/SEWERAGE_MY_APPLICATIONS';
  static const String SEWERAGE_MY_APPLICATIONS_DETAILS =
      '/SEWERAGE_MY_APPLICATIONS_DETAILS';
  static const String WATER_MY_APPLICATIONS_DETAILS =
      '/WATER_MY_APPLICATIONS_DETAILS';
  static const String WATER_PROPERTY_INFO = '/WATER_PROPERTY_INFO';
  static const String HOME_SELECT_CITY = '/HOME_SELECT_CITY';
  static const String WATER_MY_BILLS = '/WATER_MY_BILLS';
  static const String SEWERAGE_MY_BILLS = '/SEWERAGE_MY_BILLS';
  static const String WATER_MY_PAYMENT = '/WATER_MY_PAYMENT';
  static const String SEWERAGE_MY_PAYMENT = '/SEWERAGE_MY_PAYMENT';
  static const String WATER_MY_CONNECTION = '/WATER_MY_CONNECTION';
  static const String SEWERAGE_MY_CONNECTION = '/SEWERAGE_MY_CONNECTION';
  static const String WATER_MY_CONNECTION_DETAILS =
      '/WATER_MY_CONNECTION_DETAILS';
  static const String SEWERAGE_MY_CONNECTION_DETAILS =
      '/SEWERAGE_MY_CONNECTION_DETAILS';
  static const String WS_VIEW_ALL = '/WS_VIEW_ALL';
  static const String HOME_MY_PAYMENTS = '/HOME_MY_PAYMENTS';
  static const String HOME_MY_APPLICATIONS = '/HOME_MY_APPLICATIONS';
  static const String HOME_MY_CERTIFICATES = '/HOME_MY_CERTIFICATES';
  static const String HOME_GLOBAL_SEARCH = '/HOME_GLOBAL_SEARCH';
  static const String WATER_MY_CERTIFICATES = '/WATER_MY_CERTIFICATES';
  static const String WATER_CONSUMPTION_DETAILS = '/WATER_CONSUMPTION_DETAILS';
  static const String SEWERAGE_MY_CERTIFICATES = '/SEWERAGE_MY_CERTIFICATES';
  static const String PROPERTY_MY_CERTIFICATES = '/PROPERTY_MY_CERTIFICATES';
  static const String TRADE_LICENSE_APPROVED = '/TRADE_LICENSE_APPROVED';
  static const String NEW_TL_APPLICATIONS = '/NEW_TL_APPLICATIONS';
  static const String TL_APP_RENEWAL = '/TL_APP_RENEWAL';
  static const String FIRE_NOC_SCREEN = '/FIRE_NOC_SCREEN';
  static const String FIRE_NOC_SCREEN_DETAIL = '/FIRE_NOC_SCREEN_DETAIL';
  // static const String MISC_COLLECTION = '/MISC_COLLECTION';
  static const String MY_CHALLANS = '/MY_CHALLANS';
  static const String FSM_SCREEN = '/FSM_SCREEN';
  static const String FSM_SCREEN_DETAIL = '/FSM_SCREEN_DETAIL';
  static const String SELECT_CATEGORY = '/SELECT_CATEGORY';
  static const String CITIZEN_HOME = '/CITIZEN_HOME';
  /* -------------------------------------------------------------------------- */
  /*                               Employee Routes                              */
  /* -------------------------------------------------------------------------- */
  static const String EMP_LOGIN = '/EMP_LOGIN';
  static const String EMP_DASHBOARD = '/EMP_DASHBOARD';
  static const String EMP_PROFILE = '/EMP_PROFILE';
  // static const String EMP_INBOX = '/EMP_INBOX';
  static const String EMP_BOTTOM_NAV = '/EMP_BOTTOM_NAV';
  static const String EMP_TRADE_LICENSE = '/EMP_TRADE_LICENSE';
  static const String EMP_TRADE_LICENSE_DETAILS = '/EMP_TRADE_LICENSE_DETAILS';
  static const String EMP_PROPERTY_INFO = '/EMP_PROPERTY_INFO';

  static const String EMP_WS = '/EMP_WS';
  static const String EMP_WS_DETAILS = '/EMP_WS_DETAILS';
  static const String EMP_SW = '/EMP_SW';
  static const String EMP_SW_DETAILS = '/EMP_SW_DETAILS';
  static const String EMP_WATER_EDIT_APP = '/EMP_WATER_EDIT_APP';

  static const String EMP_BPA_OBPS = '/EMP_BPA_OBPS';
  static const String EMP_BPA_OBPS_DETAILS = '/EMP_BPA_OBPS_DETAILS';

  //EMP - PT
  static const String EMP_PT = '/EMP_PT';
  static const String EMP_PT_DETAILS = '/EMP_PT_DETAILS';
  static const String EMP_PT_REGISTRATION = '/EMP_PT_REGISTRATION';

  static const String EMP_UC_CHALLANS = '/EMP_UC_CHALLANS';
  static const String EMP_UC_CHALLAN_DETAILS = '/EMP_UC_CHALLAN_DETAILS';
  static const String EMP_CREATE_UC_COLLECT = '/EMP_CREATE_UC_COLLECT';

  //EMP - FireNoc
  static const String EMP_FIRE_NOC = '/EMP_FIRE_NOC';
  static const String EMP_FIRE_NOC_DETAILS = '/EMP_FIRE_NOC_DETAILS';

  //EMP - PGR-LME
  static const String EMP_GRIEVANCES = '/EMP_GRIEVANCES';
  static const String EMP_GRIEVANCES_DETAILS = '/EMP_GRIEVANCES_DETAILS';

  /* -------------------------------------------------------------------------- */
  /*                             My City App Routes                             */
  /* -------------------------------------------------------------------------- */
  static const String OFFICIAL = '/OFFICIAL';
  static const String ULB_SERVICE_MANAGEMENT = '/ULB_SERVICE_MANAGEMENT';
  static const String REPORTS_AND_DASHBOARD = '/REPORTS_AND_DASHBOARD';

  static const String VISITOR = '/VISITOR';
  static const String BUSINESS_HOME = '/BUSINESS_HOME';

  /* -------------------------------------------------------------------------- */
  /*                                  Get Pages                                 */
  /* -------------------------------------------------------------------------- */
  static List<GetPage> getPages = [
    GetPage(name: BOTTOM_NAV, page: () => const BottomNavigationPage()),
    GetPage(name: SPLASH, page: () => const SplashScreen()),
    GetPage(
      name: SELECT_CITIZEN,
      page: () => const SelectCitizen(),
      transition: Transition.noTransition,
    ),
    GetPage(name: HOME, page: () => const HomeScreen()),
    GetPage(name: LOGIN, page: () => const LoginScreen()),
    GetPage(
      name: LOGIN_MERIPEHCHAAN,
      page: () => const MeriPehchanLoginScreen(),
      transition: Transition.downToUp,
    ),
    GetPage(name: SIGN_UP, page: () => const SignUpScreen()),
    // GetPage(name: OTP_VERIFY, page: () => const OTPScreen()),
    GetPage(
      name: GRIEVANCES_SCREEN,
      binding: GrievanceBinding(),
      page: () => const GrievancesScreen(),
    ),
    GetPage(
      name: GRIEVANCES_COMPLAINTS_VIEW_ALL,
      page: () => const GrievanceComplaintsViewAll(),
    ),
    GetPage(
      name: GRIEVANCES_DETAILS_SCREEN,
      page: () => const GrievanceDetailsScreen(),
    ),
    GetPage(
      name: GRIEVANCES_FORM,
      page: () => const NewGrievanceForm(),
    ),
    // GetPage(name: GRIEVANCE, page: () => const GrievanceRedressalScreen()),
    // GetPage(name: MY_GRIEVANCE, page: () => const MyGrievancesScreen()),
    GetPage(name: PROFILE, page: () => const ProfileScreen()),
    //GetPage(name: TRADE_LICENSE, page: () => const TradeLicenseScreen()),
    GetPage(
      name: TRADE_LICENSE_APPLICATIONS,
      page: () => const TradeLicenseApplicationScreen(),
    ),
    GetPage(name: TL_DETAILS, page: () => const TLDetailsScreen()),
    GetPage(name: NEW_TL_APPLICATIONS, page: () => const NewTlApplications()),
    GetPage(name: TL_APP_RENEWAL, page: () => const TradeLicenseRenewal()),
    GetPage(name: PROPERTY_INFO, page: () => PropertyInformationScreen()),
    GetPage(name: LOCATION_CHOOSE, page: () => const LocationChooseScreen()),
    GetPage(name: NOTIFICATION, page: () => const NotificationScreen()),
    GetPage(
      name: NOTIFICATION_TEMP,
      page: () => const NotificationScreenTemp(),
    ),
    // GetPage(name: PAYMENTS, page: () =>   PaymentScreen()),
    GetPage(name: PROPERTY_TAX, page: () => const PropertyTaxScreen()),
    GetPage(name: MY_PROPERTIES, page: () => const MyPropertiesScreen()),
    GetPage(
      name: PROPERTY_APPLICATIONS,
      page: () => MyPropertyApplications(),
    ),
    GetPage(
      name: MY_PROPERTIES_DETAILS,
      page: () => const MyPropertyDetailsScreen(),
    ),
    GetPage(
      name: PROPERTY_APPLICATIONS_DETAILS,
      page: () => const MyPropertyApplicationDetails(),
    ),
    GetPage(
      name: PROPERTY_MY_PAYMENT_SCREEN,
      page: () => const MyPaymentsScreen(),
    ),
    GetPage(
      name: PROPERTY_MY_BILLS_SCREEN,
      page: () => const PropertyMyBillsScreen(),
    ),
    GetPage(
      name: BILL_DETAIL_SCREEN,
      page: () => const BillsDetailScreen(),
    ),
    GetPage(
      name: FIRE_NOC_SCREEN,
      binding: FireNocBinding(),
      page: () => const FireNocApplication(),
    ),
    GetPage(
      name: FIRE_NOC_SCREEN_DETAIL,
      page: () => const FireNocApplicationDetails(),
    ),
    //Building Plan Approval
    GetPage(
      name: BUILDING_APPLICATION,
      page: () => const BdPlanApproveApplication(),
    ),
    GetPage(
      name: BUILDING_PLAN_CERTIFICATE,
      page: () => const BpaCertificate(),
    ),
    GetPage(
      name: BPA_PERMIT_APPLICATION,
      page: () => const BpaPermitApplications(),
    ),
    GetPage(
      name: BPA_OCC_CERTIFICATE,
      page: () => const BpaOccupancyCertificate(),
    ),
    GetPage(
      name: BUILDING_APPLICATION_DETAILS,
      page: () => const BuildingDetailScreen(),
    ),
    GetPage(name: WATER_SEWERAGE, page: () => const WaterSewerageScreen()),
    GetPage(
      name: WATER_MY_APPLICATIONS,
      page: () => const WsMyApplicationsScreen(),
    ),
    GetPage(
      name: SEWERAGE_MY_APPLICATIONS,
      page: () => const SewerageMyApplicationsScreen(),
    ),
    GetPage(
      name: SEWERAGE_MY_APPLICATIONS_DETAILS,
      page: () => const SewerageMyApplicationDetails(),
    ),
    GetPage(
      name: WATER_MY_APPLICATIONS_DETAILS,
      page: () => const WaterMyApplicationDetailsScreen(),
    ),
    GetPage(
      name: WATER_PROPERTY_INFO,
      page: () => WaterPropertyInformationScreen(),
    ),
    GetPage(
      name: WS_VIEW_ALL,
      page: () => const WsViewAllScreen(),
    ),
    GetPage(name: HOME_SELECT_CITY, page: () => const HomeLocationChoose()),
    GetPage(name: WATER_MY_BILLS, page: () => const WsMyBillsScreen()),
    GetPage(name: SEWERAGE_MY_BILLS, page: () => const SewerageBillScreen()),
    GetPage(name: WATER_MY_PAYMENT, page: () => const WaterMyPayments()),
    GetPage(name: SEWERAGE_MY_PAYMENT, page: () => const SewerageMyPayments()),
    GetPage(name: HOME_MY_PAYMENTS, page: () => const HomeMyPayments()),
    GetPage(name: HOME_MY_APPLICATIONS, page: () => const HomeMyApplications()),
    GetPage(name: HOME_MY_CERTIFICATES, page: () => const HomeMyCertificates()),
    GetPage(name: HOME_GLOBAL_SEARCH, page: () => const HomeGlobalSearch()),
    GetPage(
      name: WATER_MY_CERTIFICATES,
      page: () => const WaterMyCertificates(),
    ),
    GetPage(
      name: WATER_CONSUMPTION_DETAILS,
      page: () => WsViewConsumptionDetail(),
    ),
    GetPage(
      name: SEWERAGE_MY_CERTIFICATES,
      page: () => const SewerageMyCertificates(),
    ),
    GetPage(
      name: WATER_MY_CONNECTION,
      page: () => const WaterConnectionApplications(),
    ),
    GetPage(
      name: SEWERAGE_MY_CONNECTION,
      page: () => const SewerageConnectionApplications(),
    ),
    GetPage(
      name: WATER_MY_CONNECTION_DETAILS,
      page: () => const WaterConnectionDetails(),
    ),
    GetPage(
      name: SEWERAGE_MY_CONNECTION_DETAILS,
      page: () => const SewerageConnectionDetails(),
    ),
    GetPage(
      name: PROPERTY_MY_CERTIFICATES,
      page: () => const PropertyMyCertificates(),
    ),
    GetPage(
      name: TRADE_LICENSE_APPROVED,
      page: () => const TradeLicenseApproved(),
    ),
    // GetPage(name: MISC_COLLECTION, page: () => const MiscCollections()),
    GetPage(name: MY_CHALLANS, page: () => const MyChallans()),
    GetPage(
      name: FSM_SCREEN,
      binding: FsmBinding(),
      page: () => const FsmApplication(),
    ),
    GetPage(name: FSM_SCREEN_DETAIL, page: () => const FsmApplicationDetails()),
    GetPage(name: SELECT_CATEGORY, page: () => const SelectCategory()),
    GetPage(name: CITIZEN_HOME, page: () => const CitizenHome()),

    /* -------------------------------------------------------------------------- */
    /*                               EMPLOYEE ROUTES                              */
    /* -------------------------------------------------------------------------- */
    GetPage(name: EMP_LOGIN, page: () => const EmpLoginScreen2()),
    GetPage(name: EMP_DASHBOARD, page: () => const EmpDashboard()),
    GetPage(name: EMP_PROFILE, page: () => const EmpProfileScreen()),
    // GetPage(name: EMP_INBOX, page: () => const EmpInbox()),
    GetPage(name: EMP_BOTTOM_NAV, page: () => const EmpBottomNavigationPage()),
    GetPage(name: EMP_TRADE_LICENSE, page: () => const EmpTradeLicense()),
    GetPage(
      name: EMP_TRADE_LICENSE_DETAILS,
      page: () => const EmpTlDetailsScreen(),
    ),
    GetPage(
      name: EMP_PROPERTY_INFO,
      page: () => EmpPropertyInformationScreen(),
    ),

    GetPage(name: EMP_WS, page: () => const EmpWaterScreen()),
    GetPage(name: EMP_WS_DETAILS, page: () => const EmpWaterDetails()),
    GetPage(name: EMP_SW, page: () => const EmpSewerageScreen()),
    GetPage(name: EMP_SW_DETAILS, page: () => const EmpSwDetailsScreen()),
    GetPage(
      name: EMP_WATER_EDIT_APP,
      page: () => const EmpWaterEditApplicationScreen(),
    ),

    GetPage(name: EMP_BPA_OBPS, page: () => const EmpBpaObpsScreen()),
    GetPage(name: EMP_BPA_OBPS_DETAILS, page: () => const EmpBpaObpsDetails()),

    GetPage(name: EMP_PT, page: () => const EmpPtScreen()),
    GetPage(name: EMP_PT_DETAILS, page: () => const EmpPtDetailsScreen()),
    GetPage(name: EMP_PT_REGISTRATION, page: () => const EmpPtRegistration()),

    GetPage(
      name: EMP_UC_CHALLANS,
      page: () => const EmpChallansScreen(),
      binding: ChallanBinding(),
    ),
    GetPage(
      name: EMP_UC_CHALLAN_DETAILS,
      page: () => const EmpChallanDetailsScreen(),
    ),

    GetPage(
      name: EMP_CREATE_UC_COLLECT,
      page: () => const EmpCreateUcCollectScreen(),
      // binding: ChallanBinding(),
    ),

    GetPage(
      name: EMP_FIRE_NOC,
      page: () => const EmpFireNocScreen(),
      binding: FireNocBinding(),
    ),
    GetPage(
      name: EMP_FIRE_NOC_DETAILS,
      page: () => const EmpFireNocDetails(),
    ),
    GetPage(
      binding: GrievanceBinding(),
      name: EMP_GRIEVANCES,
      page: () => const EmpGrievanceScreen(),
    ),
    GetPage(
      name: EMP_GRIEVANCES_DETAILS,
      page: () => const EmpGrievanceDetails(),
    ),

    /* -------------------------------------------------------------------------- */
    /*                             My City App Routes                             */
    /* -------------------------------------------------------------------------- */
    GetPage(name: OFFICIAL, page: () => const OfficialHomeScreen()),
    GetPage(
      name: ULB_SERVICE_MANAGEMENT,
      page: () => const UlbServiceManagementScreen(),
    ),
    GetPage(
      name: REPORTS_AND_DASHBOARD,
      page: () => const ReportsAndDashboardScreen(),
    ),

    GetPage(name: VISITOR, page: () => const VisitorHomeScreen()),
    GetPage(name: BUSINESS_HOME, page: () => const BusinessHome()),
  ];
}
