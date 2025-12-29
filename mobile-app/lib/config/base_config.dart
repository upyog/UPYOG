import 'package:flutter/material.dart';
import 'package:mobile_app/utils/dashboard_icon_role.dart';

class BaseConfig {
  static const String APP_NAME = "UPYOG";

  //run command: flutter pub get

  //App Logo can be changed following the below path from pubspec.yaml
  // and run command: dart run flutter_launcher_icons:generate
  // image_path: "assets/images/launcherIcon1.png"

  // Change native splash logo from pubspec.yaml
  // and run command: dart run flutter_native_splash:create
  // image: "assets/images/launcherIcon1.png"
  // branding: "assets/images/niuaWithName.png"

  static const String BACKEND_URL = "https://upyog.niua.org/";
  // static const String DEV_URL = "https://upyog-test.niua.org/";
  // static const String STAGE_URL = "https://upyog-sandbox.niua.org/";

  static const String STATE_TENANT_ID = 'pg';

  static const String AUTHORIZATION_KEY = 'ZWdvdi11c2VyLWNsaWVudDo=';
  static const String upyogBannerIconPng = "assets/images/upyogBannerIcon.png";
  static const String HOME_HEADER_LOGO = "assets/images/home_header_logo.png";
  static const String APP_LOGIN_BANNERS =
      "assets/images/banner1.jpeg,assets/images/banner2.jpeg,assets/images/banner3.jpeg,assets/images/banner1.jpeg,assets/images/banner2.jpeg,assets/images/banner3.jpeg";

  static const String APP_HOME_BANNERS =
      "assets/images/banner1.jpeg,assets/images/banner2.jpeg,assets/images/banner3.jpeg";

  static const String APP_LOGIN_BANNERS_TEXT =
      "Open Digital Platform, Single Window services to citizens, Increase the efficiency and productivity of ULBs, Integrated view of ULB information, Provide timely & reliable management information, Standards-based Approach";

  static const String APP_HOME_BANNERS_EMP =
      "assets/images/bannerEmp1.jpeg,assets/images/bannerEmp2.jpeg,assets/images/bannerEmp3.jpeg";
  static const String UPYOG_LOGO = "assets/images/upyoglogo.png";
  static const String LOGIN_BACK_PNG = "assets/images/loginBack.png";
  static const String LOGIN_BACK_NEW_PNG = "assets/images/loginBacknew.png";
  static const String MINISTRY_SPLASH_LOGO =
      "assets/images/ministry_splash_logo.png";
  static const String SPLASH_BOTTOM_NIUA_LOGO = "assets/images/niuaLogo.png";

  //--------------------- File upload limit ---------------------//
  static const int MAX_IMAGE_SIZE_MB = 5;
  static int get MAX_FILE_SIZE_BYTES => MAX_IMAGE_SIZE_MB * 1024 * 1024;

  //--------------------- Base payment URL ---------------------//
  static const String NTT_RELEASE_URL =
      'https://payment1.atomtech.in/ots/aipay/auth';
  static const String NTT_DEV_URL = 'https://caller.atomtech.in/ots/aipay/auth';

  //https://caller.atomtech.in/ots/aipay/mconfig?encData=Va1pXOMCJbph0kA497UHM4/Iidj4wjGLi26grQAW9FdApdOsUgE57tyDrr/rRjviFTd4ORvmTfoVznMLRO7PyVBV0tXv3Ss2BReBWU3Z1WCbxaO8YwkgphcH+s/a/RC+Mp+z2erwClTVGWC46C2Rf44r+1XMqbeWE13V1YEQsX8=&merchId=446442&info1=15696063574b225af4d7bf6d70165ba7&info2=20f0fad850653db04ba83a766f6bf2e9

  //--------------------- Base payment return URL ---------------------//
  static const String NTT_RELEASE_RETURN_URL =
      'https://payment.atomtech.in/mobilesdk/param';
  static const String NTT_DEV_RETURN_URL =
      'https://pgtest.atomtech.in/mobilesdk/param';

  //----------------- Ntt Data Payment Config -----------------//
  static const String NTT_MERCHANT_PASSWORD = 'Test@123';
  static const String NTT_PROD_ID = 'NSE';
  static const String NTT_REQUEST_HASH_KEY = 'KEY123657234';
  static const String NTT_RESPONSE_HASH_KEY = 'KEYRESP123657234';
  static const String NTT_REQUEST_RESPONSE_KEY =
      'A4476C2062FFA58980DC8F79EB6A799E';
  static const String NTT_REQUEST_DECRYPTION_KEY =
      '75AEF0FA1B94B3C10D4F5B268F757F11';
  static const String NTT_CLIENT_CODE = 'NAVIN';
  static const String NTT_TXN_CURRENCY = 'INR';
  static const String NTT_MCC_CODE = '5499';
  static const String NTT_MERCHANT_TYPE = 'R';

  static const String helpLineNo = '2565425632';

  //-------------------- App Colors --------------------//
  static const Color appThemeColor1 = Color(0xFFC55637);
  static const Color appThemeColor2 = Color(0xff5d002f);
  static const Color splashBackColor = Color(0xffF6FCFF);
  static const Color lightAmber = Color(0xFFE66E53);
  static const Color shadeAmber = Color(0xFFEFC5BB);
  static const Color redColor1 = Color(0xFFB22B30);
  static const Color redColor2 = Color(0xFFa82227);
  static const Color redColor = Colors.red;
  static const Color mainBackgroundColor = Color(0xFFFFFFFF);
  static const Color bodyIconColor = Color(0xFF34306D);
  static const Color greyColor1 = Color(0xFF545454);
  static const Color greyColor2 = Color(0xFFCAD1E2);
  static const Color greyColor3 = Color(0xFF8B8B8B);
  static const Color greyColor4 = Color(0xFF656263);
  static const Color textColor = Color(0xFF242424);
  static const Color textColor2 = Color(0xFF1A1A1A);
  static const Color subTextColor = Color(0xFF616161);
  static const Color fillAppBtnCircularColor = Color(0xFFFFFFFF);
  static const Color dotGrayColor = Color(0xFFE0E0E0);
  static const Color dotBlueColor = Color(0xFF0081C4);

  static const Color selectedNavColor = Color.fromARGB(255, 214, 75, 37);
  static const Color borderColor = Color(0xFFE9E9E9);
  static const Color borderColor1 = Color(0xFF8C8C8C);
  static const Color deepGreenColor = Color(0xFF0C8F5C);
  static const Color statusGreenColor = Color(0xFF00703C);
  static const Color shadeAmberColor = Color(0xFFEFC5BB);
  static const Color lightIndigoColor = Color(0xFFE3EAFB);
  static const Color lightGreenColor = Color(0xFFD9F6EF);
  static const Color lightPurpleColor = Color(0xFFEEE3FB);
  static const Color lightPinkColor = Color(0xFFFBE3E9);

  //Status Color
  static const Color statusPendingColor = Color(0xFFE09200);
  static const Color statusPendingBackColor = Color(0xFFFFF5E1);

  static const Color statusAcknowledgeColor = Color(0xFF094CFF);
  static const Color statusAcknowledgeBackColor = Color(0xFFE6EDFF);

  static const Color statusResolvedColor = Color(0xFF1A805E);
  static const Color statusResolvedBackColor = Color(0xFFE7F4EF);

  static const Color statusRejectedColor = Color(0xFFE62336);
  static const Color statusRejectedBackColor = Color(0xFFFEEFF1);

  //Rating color
  static const Color ratingColor = Color(0xFFFFB01F);
  static const Color ratingUnratedColor = Color(0xFFE9E9E9);

  //Timeline grey text
  static const Color timelineGreyTextColor = Color(0xFFA7A5A6);
  static const Color timelineCircleColor = Color(0xFFEDF8EF);
  static const Color timelineDashColor = Color(0xFFBDBCBC);

  //Filter icon color
  static const Color filterIconColor = Color(0xFF6B7280);
  static const Color filterBackgroundColor = Color(0xFF232323);

  //Rating star color
  static const Color ratingBackColor = Color(0xFFE9EFFF);

  //-------------------- App Strings --------------------//
  static const IconData headerMenuIcon = Icons.table_rows_rounded;
  static const IconData headerBellIcon = Icons.notifications_outlined;
  static const IconData headerProfilePlaceholderIcon = Icons.person;
  static const IconData footerHomeIcon = Icons.home;
  static const IconData footerCityIcon = Icons.language;
  static const IconData footerLanguageIcon = Icons.g_translate_outlined;
  static const IconData footerSettingsIcon = Icons.settings;
  static const IconData footerProfileIcon = Icons.person;
  static const IconData homeUiMyPaymentIcon = Icons.payments_rounded;
  static const IconData homeUiMyCertificateIcon = Icons.card_giftcard_rounded;
  static const IconData homeUiMyApplicationIcon = Icons.grid_view_rounded;

  //-------------------- Bottom navigation icons --------------------//
  static const String homeIconSvg = "assets/icons/home.svg";
  static const String paymentsIconSvg = "assets/icons/rupee.svg";
  static const String servicesIconSvg = "assets/icons/services.svg";
  static const String certificatesIconSvg = "assets/icons/certificate.svg";
  static const String appsIconSvg = "assets/icons/apps.svg";

  //Card background
  static const String cardBackgroundImg = "assets/images/card_background.png";

  //Icons
  static const String phoneIconSvg = "assets/icons/phone.svg";
  static const String buildingIconSvg = "assets/icons/building.svg";

  //Icons
  static const String bellSvg = "assets/images/bell.svg";
  static const String propertySvg = "assets/images/property.svg";
  static const String listSvg = "assets/images/list.svg";
  static const String ticketSvg = "assets/images/ticket.svg";
  static const String waterSvg = "assets/images/water.svg";

  static const String tradeLicenseSvgIcon = 'assets/icons/trade_license.svg';
  static const String birthDeathSvgIcon = 'assets/icons/birth_death.svg';
  static const String helpGrievanceSvgIcon = 'assets/icons/help_grievance.svg';
  static const String buildingApprovalSvgIcon =
      'assets/icons/building_approval.svg';
  static const String deslaudgingSvgIcon = 'assets/icons/desludging.svg';
  static const String fireNocSvgIcon = 'assets/icons/fire_noc.svg';
  static const String filterIconSvg = "assets/icons/filter.svg";

  static const String homeUiCardPtIcon = "assets/images/property_tax.svg";
  static const String homeUiCardTlIcon = "assets/images/trade_license.svg";
  static const String homeUiCardBirthIcon =
      "assets/images/modified_birth_death.svg";
  static const String homeUiCardWsIcon = "assets/images/water_sewerage.svg";
  static const String homeUiCardGrievanceIcon = "assets/images/grievance.svg";
  static const String homeUiCardBpaIcon = "assets/images/building_plan.svg";
  static const String homeUiCardDsIcon = "assets/images/desludging.svg";
  static const String homeUiCardFnIcon = "assets/images/fire_noc.svg";
  static const String homeUiCardMcIcon = "assets/images/miscellaneous.svg";
  static const String profileStartCameraIcon = "assets/images/star_camera.svg";
  static const String meriPehchaanButtonLogoIcon =
      "assets/images/meripehchaan_logo.svg";

  /* -------------------------------------------------------------------------- */
  /*                        ULB Service Management Icons                        */
  /* -------------------------------------------------------------------------- */
  static const String g2gServiceManagementIcon =
      "assets/v2_images/g2g_service.png";
  static const String eGovernanceServiceIcon =
      "assets/v2_images/e_governance.png";
  static const String requestManagementIcon =
      "assets/v2_images/request_management.png";
  static const String ulbServiceManagementIcon =
      "assets/v2_images/ulb_service.png";
  static const String reportsAndDashboardIcon =
      "assets/v2_images/reports_dashboard.png";

  static const String aboutCityIcon = "assets/v2_images/about_city.png";
  static const String reportGrievanceIcon =
      "assets/v2_images/report_grievance.png";
  static const String bookingIcon = "assets/v2_images/booking.png";
  static const String updatesHubIcon = "assets/v2_images/update_hub.png";
  static const String placesIcon = "assets/v2_images/places.png";
  static const String parkingNavigationIcon = "assets/v2_images/navigation.png";
  static const String faqEnquiriesIcon = "assets/v2_images/faq.png";
  static const String convenienceIcon = "assets/v2_images/convenience.png";
  static const String publicAnalyticsIcon = "assets/v2_images/analytics.png";

  static const String customerSupportIcon =
      "assets/v2_images/customer_support.png";

  /* -------------------------------------------------------------------------- */
  /*                           Citizen Home Icons                               */
  /* -------------------------------------------------------------------------- */
  static const String aboutIcon = "assets/v2_images/info.png";
  static const String grievanceIcon = "assets/v2_images/information.png";
  static const String waterSewerageIcon = "assets/v2_images/briefcase.png";
  static const String propertyIcon = "assets/v2_images/apartment.png";
  static const String billIcon = "assets/v2_images/invoice.png";
  static const String desludgingIcon = "assets/v2_images/mini_truck.png";
  static const String fireIcon = "assets/v2_images/fire.png";
  static const String govtIcon = "assets/v2_images/museum.png";
  static const String enquiryIcon = "assets/v2_images/help.png";
  static const String utilitiesIcon = "assets/v2_images/utilities.png";
  static const String permitsIcon = "assets/v2_images/permits.png";
  static const String sosIcon = "assets/v2_images/sos.png";
  static const String homeIcon = "assets/v2_images/home.png";
  static const String gridIcon = "assets/v2_images/grid.png";

  /* -------------------------------------------------------------------------- */
  /*                                 UC Collect                                 */
  /* -------------------------------------------------------------------------- */
  static const String ucCollectIconSvg = "assets/icons/domain.svg";

  static const IconData ptMyPropertiesIcon = Icons.apartment;
  static const IconData ptMyApplicationsIcon = Icons.list_alt;

  static const IconData tlMyApplicationsIcon = Icons.list_alt;

  static const IconData birthIcon = Icons.payments_rounded;
  static const IconData deathIcon = Icons.payments_rounded;
  static const IconData my_payment_icon = Icons.receipt_long_outlined;

  //---------------------- Water ----------------------//
  static const IconData wMyApplicationIcon = Icons.list_alt;
  static const IconData wMyBillsIcon = Icons.currency_rupee_outlined;
  static const IconData wMyPaymentsIcon = Icons.receipt_outlined;

  //-------------------- App Images --------------------//
  static const String infoIcon = "assets/images/info.png";
  static const String digiLockerIcon = "assets/images/digilocker.png";
  static const String cityImg_A = "assets/images/imgA.png";
  static const String cityImg_B = "assets/images/imgB.png";
  static const String cityImg_C = "assets/images/imgC.png";
  static const String cityImg_D = "assets/images/imgD.png";
  static const String cityImg_E = "assets/images/imgE.png";
  static const String cityImg_PG = "assets/images/imgPG.png";

  static const String splashBuildingPng = "assets/images/splashBuilding.png";
  static const String loginBuildingPng = "assets/images/loginBuilding.png";
  static const String newloginBuildingPng = "assets/images/newBuildingImg.png";
  static const String delhiBuildingPng = "assets/images/delhi.png";
  static const String hyderabadBuildingPng = "assets/images/hyderabad.png";
  static const String bengaluruBuildingPng = "assets/images/bengaluru.png";
  static const String calcuttaBuildingPng = "assets/images/calcutta.png";

  //Global Search for home page
  static List<DashboardIcon> globalSearchList = [
    DashboardIcon(
      title: "Property Tax",
      icon: BaseConfig.propertySvg,
    ),
    DashboardIcon(
      title: "Property Tax My Applications",
      icon: BaseConfig.propertySvg,
    ),
    DashboardIcon(
      title: "Property Tax My Properties",
      icon: BaseConfig.propertySvg,
    ),
    DashboardIcon(
      title: "Property Tax My Bills",
      icon: BaseConfig.propertySvg,
    ),
    DashboardIcon(
      title: "Property Tax My Payments",
      icon: BaseConfig.propertySvg,
    ),
    DashboardIcon(
      title: "Property Tax My Certificates",
      icon: BaseConfig.propertySvg,
    ),
    DashboardIcon(
      title: "Trade License",
      icon: BaseConfig.tradeLicenseSvgIcon,
    ),
    DashboardIcon(
      title: "Trade License My Applications",
      icon: BaseConfig.tradeLicenseSvgIcon,
    ),
    DashboardIcon(
      title: "Trade License My Certificates Approved",
      icon: BaseConfig.tradeLicenseSvgIcon,
    ),
    DashboardIcon(
      title: "Water",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Water My Applications",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Water My Bills",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Water My Payments",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Sewerage",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Sewerage My Applications",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Sewerage My Bills",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Sewerage My Payments",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Water My Certificates",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Sewerage My Certificates",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Grievances",
      icon: BaseConfig.helpGrievanceSvgIcon,
    ),
    DashboardIcon(
      title: "Grievances My Applications",
      icon: BaseConfig.helpGrievanceSvgIcon,
    ),
    DashboardIcon(
      title: "Building Plan Approval/ OBPS",
      icon: BaseConfig.buildingApprovalSvgIcon,
    ),
    DashboardIcon(
      title: "OBPS Permit Applications",
      icon: BaseConfig.buildingApprovalSvgIcon,
    ),
    DashboardIcon(
      title: "OBPS Occupancy Certificate",
      icon: BaseConfig.buildingApprovalSvgIcon,
    ),
    DashboardIcon(
      title: "Select Location/ Location",
      icon: BaseConfig.buildingApprovalSvgIcon,
    ),
    DashboardIcon(
      title: "My Certificates",
      icon: BaseConfig.certificatesIconSvg,
    ),
    DashboardIcon(
      title: "Profile/ Edit Profile",
      icon: BaseConfig.propertySvg,
    ),
    DashboardIcon(
      title: "Payments",
      icon: BaseConfig.paymentsIconSvg,
    ),
  ];

  static List<DashboardIcon> globalTopServicesSearch = [
    DashboardIcon(
      title: "Property Tax",
      icon: BaseConfig.propertySvg,
    ),
    DashboardIcon(
      title: "Trade License",
      icon: BaseConfig.tradeLicenseSvgIcon,
    ),
    DashboardIcon(
      title: "Water",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Sewerage",
      icon: BaseConfig.waterSvg,
    ),
    DashboardIcon(
      title: "Grievances",
      icon: BaseConfig.helpGrievanceSvgIcon,
    ),
    DashboardIcon(
      title: "Building Plan Approval/ OBPS",
      icon: BaseConfig.buildingApprovalSvgIcon,
    ),
  ];

  // Employee
  static const String empTlCardImg = "assets/images/top-red-card.png";
}
