import 'dart:convert';
import 'dart:developer';
import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/env/app_config.dart';
import 'package:mobile_app/model/citizen/date_filter_model.dart';
import 'package:mobile_app/model/citizen/fsm/fsm.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/employee/status_map/status_map.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/constants/constants.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:url_launcher/url_launcher.dart';

String formatDate(DateTime date, {required String format}) {
  return DateFormat(format).format(date);
}

snackBar(String txt1, String txt2, Color color, {int seconds = 3}) {
  if (txt1.isEmpty && txt2.isEmpty) return;
  Get.snackbar(
    txt1,
    txt2,
    borderRadius: 0,
    backgroundColor: color,
    colorText: Colors.white,
    maxWidth: double.infinity,
    margin: EdgeInsets.zero,
    padding: const EdgeInsets.all(12),
    duration: Duration(seconds: seconds),
    snackPosition: SnackPosition.BOTTOM,
  );
}

dPrint(Object? value, {bool enableLog = false}) {
  if (kDebugMode) {
    if (enableLog) {
      log('$value');
      return;
    }
    print(value);
  }
}

void showSnackBar(BuildContext context, String text) {
  ScaffoldMessenger.of(context)
    ..hideCurrentSnackBar()
    ..showSnackBar(
      SnackBar(
        backgroundColor: BaseConfig.appThemeColor1,
        content: Text(text),
      ),
    );
}

showWebViewDialogue(
  BuildContext context, {
  required String url,
  bool enablePrivacyTitleSize = false,
}) {
  dPrint('Privacy Url: $url');

  InAppWebViewSettings options = InAppWebViewSettings(
    useShouldOverrideUrlLoading: true,
    mediaPlaybackRequiresUserGesture: false,
    transparentBackground: true,
    javaScriptEnabled: true,
    cacheEnabled: true,
    userAgent: Platform.isIOS
        ? "Mozilla/5.0 (iPhone; CPU iPhone OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/117.0.5938.108 Mobile/15E148 Safari/604.1"
        : "",
    supportZoom: true,
    useOnLoadResource: true,
    useHybridComposition: true,
    allowsInlineMediaPlayback: true,
  );

  RxDouble progressVal = 0.0.obs;

  return showAdaptiveDialog(
    context: context,
    builder: (context) => AlertDialog(
      backgroundColor: BaseConfig.mainBackgroundColor,
      surfaceTintColor: BaseConfig.mainBackgroundColor,
      content: SizedBox(
        width: double.maxFinite,
        height: Get.height * 0.7,
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                IconButton(
                  icon: const Icon(
                    Icons.cancel_presentation_outlined,
                    color: BaseConfig.appThemeColor1,
                  ),
                  onPressed: () => Get.back(),
                ),
              ],
            ),
            Expanded(
              child: Stack(
                children: [
                  InAppWebView(
                    initialSettings: options,
                    initialUrlRequest:
                        URLRequest(url: WebUri.uri(Uri.parse(url))),
                    onLoadStop: (controller, url) async {
                      // Only for privacy policy
                      if (enablePrivacyTitleSize) {
                        const titleSize =
                            '''window.document.getElementsByTagName('h1')[0].style.fontSize = "24px"''';
                        await controller.evaluateJavascript(source: titleSize);
                      }
                      progressVal.value = 1.0;
                    },
                    onProgressChanged: (controller, progress) {
                      progressVal.value = progress / 100;
                    },
                  ),
                  Obx(
                    () => progressVal.value != 1.0
                        ? Center(child: showCircularIndicator())
                        : const SizedBox.shrink(),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    ),
  );
}

showCustomDialogue(
  BuildContext context, {
  Widget? child,
  bool scrollable = false,
  double height = 0.7,
}) {
  return showAdaptiveDialog(
    context: context,
    barrierDismissible: false,
    builder: (context) => AlertDialog(
      backgroundColor: BaseConfig.mainBackgroundColor,
      surfaceTintColor: BaseConfig.mainBackgroundColor,
      scrollable: scrollable,
      content: SizedBox(
        width: double.maxFinite,
        height: Get.height * height,
        child: child,
      ),
    ),
  );
}

Widget showCircularIndicator({
  double strokeWidth = 4.0,
  Color color = BaseConfig.appThemeColor1,
}) {
  return Center(
    child: CircularProgressIndicator.adaptive(
      strokeWidth: strokeWidth,
      valueColor: AlwaysStoppedAnimation<Color>(color),
    ),
  );
}

Widget showErrorIcon({
  Color color = BaseConfig.redColor1,
  double? size,
}) {
  return Center(
    child: Icon(
      Icons.error_outline,
      color: color,
      size: size,
    ),
  );
}

Future<void> launchPhoneDialer(String contactNumber) async {
  final Uri phoneUri = Uri(
    scheme: "tel",
    path: contactNumber,
  );
  try {
    if (await canLaunchUrl(phoneUri)) {
      await launchUrl(phoneUri);
    }
  } catch (error) {
    throw ("Cannot dial");
  }
}

String convertDateFormat(String? inputDate) {
  if (inputDate == null) return '';
  List<String> parts = inputDate.split('-');
  if (parts.length != 3) {
    throw const FormatException("Invalid date format");
  }
  return "${parts[2]}-${parts[1]}-${parts[0]}";
}

Future<dynamic> getCityTenant() async {
  final jsonData = await HiveService.getData(Constants.HOME_CITY);
  if (jsonData == null) {
    await clearData();
    // return Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
    return Get.offAllNamed(AppRoutes.SELECT_CATEGORY);
  }
  final json = jsonDecode(jsonData);
  final tenant = TenantTenant.fromJson(json);
  return tenant;
}

Future<dynamic> getCityTenantEmployee() async {
  final jsonData = await HiveService.getData(Constants.HOME_CITY_EMP);
  if (jsonData == null) {
    await clearData();
    // return Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
    return Get.offAllNamed(AppRoutes.SELECT_CATEGORY);
  }
  final json = jsonDecode(jsonData);
  final tenant = TenantTenant.fromJson(json);
  return tenant;
}

String parseNoOfFloors(dynamic noOfFloors) {
  if (noOfFloors is int) {
    return noOfFloors.toString();
  } else if (noOfFloors is Map<String, dynamic>) {
    return noOfFloors['code'];
  } else {
    return 'N/A';
  }
}

String additionalDtl(dynamic additionalDetails) {
  if (additionalDetails is String) {
    return additionalDetails;
  } else if (additionalDetails is Map<String, dynamic>) {
    return checkAdditionalDetails(additionalDetails);
  } else {
    return 'N/A';
  }
}

String getTimelineState(state) {
  switch (state) {
    case 'CLOSEDAFTERRESOLUTION':
      return i18.grievance.CLOSED_AFTER_RESOLUTION;
    case 'RESOLVED':
      return i18.grievance.COMPLIANT_RESOLVED;
    case 'PENDINGATLME':
      return i18.grievance.PENDING_LME;
    case 'PENDINGFORASSIGNMENT':
      return i18.grievance.ACKNOWLEDGED;
    default:
      return '';
  }
}

String getStatus(status, {bool isGprResponse = false}) {
  if (status == null) return '';
  switch (status) {
    case GrievanceStatus.CLOSED_SOLUTION:
      return isGprResponse ? i18.grievance.CLOSED : i18.common.CLOSED;
    case GrievanceStatus.RESOLVED:
      return isGprResponse ? i18.grievance.CLOSED : i18.common.CLOSED;
    case GrievanceStatus.PENDING:
      return isGprResponse ? i18.grievance.OPEN : i18.common.OPEN;
    default:
      return status;
  }
}

Color getStatusColor(String state) {
  switch (state) {
    case 'CLOSEDAFTERRESOLUTION' || 'INWORKFLOW' || 'INITIATED':
      return BaseConfig.statusAcknowledgeColor;
    case 'RESOLVED' || 'APPLIED' || 'ACTIVE' || 'Active' || 'APPROVED':
      return BaseConfig.statusResolvedColor;
    case 'PENDINGFORASSIGNMENT' ||
          'CITIZENACTIONREQUIRED' ||
          'INACTIVE' ||
          'Inactive' ||
          'INPROGRESS':
      return BaseConfig.statusPendingColor;
    default:
      return BaseConfig.statusRejectedColor;
  }
}

//Grievance card status text color
Color getGrievanceStatusTextColor(String status) {
  if (status == GrievanceStatus.PENDING.name ||
      status == GrievanceStatus.PENDING_SUPERVISOR.name) {
    return BaseConfig.statusAcknowledgeColor;
  } else if (status == GrievanceStatus.RESOLVED.name) {
    return BaseConfig.statusResolvedColor;
  } else if (status == GrievanceStatus.CLOSED_SOLUTION.name ||
      status == GrievanceStatus.CLOSED_REJECTION.name ||
      status == GrievanceStatus.REJECTED.name) {
    return BaseConfig.statusRejectedColor;
  } else {
    return BaseConfig.statusResolvedColor;
  }
}

// Grievance card color based on status
Color getGrievanceStatusBackColor(String status) {
  if (status == GrievanceStatus.PENDING.name ||
      status == GrievanceStatus.PENDING_SUPERVISOR.name) {
    return BaseConfig.statusAcknowledgeBackColor;
  } else if (status == GrievanceStatus.RESOLVED.name) {
    return BaseConfig.statusResolvedBackColor;
  } else if (status == GrievanceStatus.CLOSED_SOLUTION.name ||
      status == GrievanceStatus.CLOSED_REJECTION.name ||
      status == GrievanceStatus.REJECTED.name) {
    return BaseConfig.statusRejectedBackColor;
  } else {
    return BaseConfig.statusResolvedBackColor;
  }
}

Color getStatusBackColor(String state) {
  switch (state) {
    case 'CLOSEDAFTERRESOLUTION' || 'INWORKFLOW' || 'INITIATED':
      return BaseConfig.statusAcknowledgeBackColor;
    case 'RESOLVED' || 'APPLIED' || 'ACTIVE' || 'Active' || 'APPROVED':
      return BaseConfig.statusResolvedBackColor;
    case 'PENDINGFORASSIGNMENT' ||
          'CITIZENACTIONREQUIRED' ||
          'INACTIVE' ||
          'Inactive' ||
          'INPROGRESS':
      return BaseConfig.statusPendingBackColor;
    default:
      return BaseConfig.statusRejectedBackColor;
  }
}

launchURL(String url, {LaunchMode mode = LaunchMode.platformDefault}) async {
  final uri = Uri.parse(url);
  if (await canLaunchUrl(uri)) {
    await launchUrl(uri, mode: mode);
  } else {
    throw 'Could not launch $url';
  }
}

// get the user type from the local storage [CITIZEN or EMPLOYEE]
Future<String> getUserType() async {
  final String userType =
      await HiveService.getData(Constants.USER_TYPE) ?? UserType.CITIZEN.name;
  return userType;
}

// Clear the data from the local storage
Future<void> clearData() async {
  await HiveService.deleteData(HiveConstants.LOGIN_KEY);
  await HiveService.deleteData(HiveConstants.EMP_LOGIN_KEY);
}

Future<void> skipButton() async {
  await HiveService.setData(HiveConstants.SKIP_BUTTON, true);
  Get.offAllNamed(AppRoutes.BOTTOM_NAV);
}

// Filter list with date opened
List<DateOpenedFilter> dateOpenedFilters = [
  DateOpenedFilter(
    label: 'Last Week',
    startDate: DateTime.now().subtract(const Duration(days: 7)),
    endDate: DateTime.now(),
  ),
  DateOpenedFilter(
    label: 'Last 2 Weeks',
    startDate: DateTime.now().subtract(const Duration(days: 14)),
    endDate: DateTime.now(),
  ),
  DateOpenedFilter(
    label: 'Last Month',
    startDate: DateTime.now().subtract(const Duration(days: 30)),
    endDate: DateTime.now(),
  ),
  DateOpenedFilter(
    label: 'Last 2 Months',
    startDate: DateTime.now().subtract(const Duration(days: 60)),
    endDate: DateTime.now(),
  ),
];

//Get district code - dynamic
// Use: Employee city selection
String getTenantCode(String city) {
  final languageController = Get.find<LanguageController>();
  final tenants = languageController.mdmsResTenant.tenants!;
  for (var tenant in tenants) {
    if (tenant.code == city) {
      final tenantPg = tenant.code!.split('.').first.toUpperCase();
      final tenantCity = tenant.code!.split('.').last.toUpperCase();
      return '${tenantPg}_$tenantCity';
    }
  }
  return '';
}

// Get SLA status
String getSlaStatus(int? businessServiceSla) {
  String slaDays = businessServiceSla != null
      ? (businessServiceSla / (24 * 60 * 60 * 1000)).round().toString()
      : "-";
  return slaDays;
}

// EMP - Locality
String getLocality(TenantTenant tenant, String locality) {
  var city = tenant.code?.split('.').last.toUpperCase();
  return 'PG_${city}_REVENUE_$locality';
}

// Get tenant city name
String getCityName(String tenantId) {
  return !tenantId.contains(".")
      ? getLocalizedString(
          '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}'
              .toUpperCase(),
        )
      : getLocalizedString(
          '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}_${tenantId.split('.').last}'
              .toUpperCase(),
        );
}

// Present date to fromDate
DateTime calculateFromDate(int toDateMilliseconds) {
  DateTime toDate = DateTime.fromMillisecondsSinceEpoch(toDateMilliseconds);
  DateTime fromDate = DateTime(
    toDate.year - 1,
    toDate.month,
    toDate.day,
    toDate.hour,
    toDate.minute,
    toDate.second,
    toDate.millisecond,
    toDate.microsecond,
  );

  return fromDate;
}

// Check if the value is not null or empty
bool isNotNullOrEmpty(dynamic value) {
  if (value == null) {
    return false;
  }

  if (value is String) {
    return value.isNotEmpty;
  }

  if (value is Iterable || value is List || value is Set || value is Map) {
    return value.isNotEmpty;
  }

  if (value is int) {
    return value != 0;
  }

  if (value is bool) {
    return value;
  }

  if (value is double) {
    return value != 0.0;
  }

  if (value is Object) {
    return value.toString().isNotEmpty;
  }

  return true;
}

// Replace . to _ in the document name and remove last name
String filterAndModifyDocName(String input) {
  if (input.contains('.')) {
    var parts = input.split('.');

    if (parts.length > 1) {
      return '${parts.first}_${parts.sublist(1, parts.length - 1).join('_')}';
    }
  }
  return input;
}

//Get BPA services
BusinessService getBpaServiceStatus(String status) {
  if (status == BpaStatus.PENDING_APPL_FEE_PAYMENT.name ||
      status == BpaStatus.PENDING_APPL_FEE.name) {
    return BusinessService.BPA_NC_APP_FEE;
  } else if (status == BpaStatus.PENDING_SANC_FEE_PAYMENT.name ||
      status == BpaStatus.PENDING_SANC_FEE.name) {
    return BusinessService.BPA_NC_SAN_FEE;
  } else if (status == BpaStatus.PENDING_FEE.name) {
    return BusinessService.BPA_LOW_RISK_PERMIT_FEE;
  } else {
    return BusinessService.OBPS;
  }
}

//Row Colors
Color getRowColor(int fno) {
  return fno % 2 == 0 ? Colors.grey[300]! : Colors.white;
}

FileExtType getFileType(String filePath) {
  final extension = filePath.split('.').last.toLowerCase();

  if (extension == 'pdf') {
    return FileExtType.pdf;
  } else if (extension == 'dxf') {
    return FileExtType.dxf;
  } else if (extension == 'png') {
    return FileExtType.png;
  } else if (extension == 'jpg') {
    return FileExtType.jpg;
  } else if (extension == 'jpeg') {
    return FileExtType.jpeg;
  } else if (extension == 'xml') {
    return FileExtType.xml;
  } else if (extension == 'doc' || extension == 'docx') {
    return FileExtType.docs;
  } else {
    return FileExtType.none;
  }
}

//Get the business service name billing service
BusinessService getBusinessServiceByStatus(String service) {
  final serviceMap = <String, BusinessService>{
    'TL': BusinessService.TL,
    'WS.ONE_TIME_FEE': BusinessService.WS_ONE_TIME_FEE,
    'SW.ONE_TIME_FEE': BusinessService.SW_ONE_TIME_FEE,
    'WS': BusinessService.WS,
    'SW': BusinessService.SW,
    'PT': BusinessService.PT,
    'OBPS': BusinessService.OBPS,
    'BPA.NC_SAN_FEE': BusinessService.BPA_NC_SAN_FEE,
    'BPA.NC_APP_FEE': BusinessService.BPA_NC_APP_FEE,
    'BPA.LOW_RISK_PERMIT_FEE': BusinessService.BPA_LOW_RISK_PERMIT_FEE,
    'PT.MUTATION': BusinessService.PT_MUTATION,
    'ADVT.Gas_Balloon_Advertisement': BusinessService.GAS_BALLOON_ADVERTISEMENT,
    'CH.Burning_of_Waste_Challan_fee':
        BusinessService.BURNING_OF_WASTE_CHALLAN_FEE,
    'ADVT.Hoardings': BusinessService.HOARDINGS,
    'ADVT.Light_Wala_Board': BusinessService.LIGHT_WALA_BOARD,
    'ADVT.Unipolls': BusinessService.UNIPOLLS,
    'ADVT.Wall_Paint_Advertisement': BusinessService.WALL_PAINT_ADVERTISEMENT,
    'ADVT.Parking_Fee': BusinessService.PARKING_FEE,
    'CH.Santitation_dumping_garbage':
        BusinessService.SANTITATION_DUMPING_GARBAGE,
    'CH.Collection_and_Demolition_Waste_Challan_fee':
        BusinessService.COLLECTION_AND_DEMOLITION_WASTE_CHALLAN_FEE,
    'FSM.TRIP_CHARGES': BusinessService.FSM_TRIP_CHARGES,
    'FIRENOC': BusinessService.FIRENOC,
  };

  if (serviceMap.containsKey(service)) {
    return serviceMap[service]!;
  } else {
    throw ArgumentError('Invalid status: $service');
  }
}

//Get the business service name billing service
String getBusinessServiceByType(String service) {
  switch (service) {
    case 'BPA':
      return BusinessService.BPA_NC_SAN_FEE.name;
    case 'BPA_LOW':
      return BusinessService.BPA_LOW_RISK_PERMIT_FEE.name;
    case 'BPA_OC':
      return BusinessService.BPA_NC_OC_APP_FEE.name;
    default:
      throw ArgumentError('Invalid status: $service');
  }
}

String getBusinessServiceByTypeEmp(String service) {
  switch (service) {
    case 'BPA':
      return BusinessService.BPA_NC_APP_FEE.name;
    case 'BPA_LOW':
      return BusinessService.BPA_LOW_RISK_PERMIT_FEE.name;
    case 'BPA_OC':
      return BusinessService.BPA_NC_OC_APP_FEE.name;
    default:
      throw ArgumentError('Invalid status: $service');
  }
}

/// Employee Inbox - Get filtered `StatusMap?`
StatusMap? getFilteredStatusMapEmp(
  List<StatusMap>? statusMaps,
  String businessService, {
  String? applicationStatus,
}) {
  if (!isNotNullOrEmpty(statusMaps)) return null;

  if (isNotNullOrEmpty(applicationStatus)) {
    for (var statusMap in statusMaps!) {
      if (statusMap.businessService == businessService &&
          statusMap.applicationStatus == applicationStatus) {
        return statusMap;
      }
    }
  } else {
    for (var statusMap in statusMaps!) {
      if (statusMap.businessService == businessService) {
        return statusMap;
      }
    }
  }
  return null;
}

//OBPS get risk type
String getRiskType(String wfState) {
  switch (wfState) {
    case 'BPA':
      return 'HIGH';
    case 'BPA_LOW':
      return 'LOW';
    default:
      throw ArgumentError('Invalid status: $wfState');
  }
}

//Error Pop-up
void showErrorDialog(String statusCode, message, url) {
  if (Get.isDialogOpen == true) {
    Get.back();
  }
  ScrollController scrollController = ScrollController();
  Get.dialog(
    AlertDialog(
      //titlePadding: EdgeInsets.all(0),
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
                color: BaseConfig.appThemeColor1,
                child: Row(
                  children: [
                    Expanded(
                      child: MediumTextNotoSans(
                        text: "Backend API Error",
                        fontWeight: FontWeight.w600,
                        size: 14.h,
                        textAlign: TextAlign.center,
                        color: BaseConfig.mainBackgroundColor,
                      ),
                    ),
                    IconButton(
                      onPressed: () => Get.back(),
                      icon: const Icon(
                        Icons.cancel_presentation_outlined,
                        size: 30,
                        color: BaseConfig.mainBackgroundColor,
                      ),
                    ),
                  ],
                ),
              ),
              SizedBox(
                height: 20.h,
              ),
              SizedBox(
                height: 100,
                child: Scrollbar(
                  controller: scrollController,
                  thumbVisibility: true,
                  child: SingleChildScrollView(
                    controller: scrollController,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        SmallTextNotoSans(
                          text: message,
                          fontWeight: FontWeight.w600,
                          size: 15.h,
                          color: BaseConfig.redColor,
                        ),
                        const Divider(),
                        SmallTextNotoSans(
                          text: url,
                          fontWeight: FontWeight.w400,
                          size: 14.h,
                          color: BaseConfig.redColor,
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    ),
    barrierDismissible: false,
  );
}

/// Get the last two parts of the string - EMP /WS/SW
String getLastTwoPart(String input) {
  List<String> parts = input.split('_');
  if (parts.length >= 2) {
    return '${parts[parts.length - 2]}_${parts[parts.length - 1]}';
  } else if (parts.isNotEmpty) {
    return parts.last;
  }
  return '';
}

PdfKey getPdfKeyByServiceBPA({required String service}) {
  if (service == 'BPA') {
    return PdfKey.building_permit;
  } else {
    return PdfKey.building_permit_low;
  }
}

//get payment callbackurl
String getPaymentCallbackUrl({
  required String businessService,
  required String consumerCode,
  required String tenantId,
  required String module,
}) {
  switch (module) {
    case 'rainmaker-tl':
      return '$apiBaseUrl${Url.CITIZEN_PAYMENT_SUCCESS}/$businessService/$consumerCode/$tenantId?propertyId=$consumerCode';
    // case Modules.BPA:
    //   return '$apiBaseUrl${Url.CITIZEN_PAYMENT_SUCCESS}/$businessService/$consumerCode/$tenantId?applicationNumber=$consumerCode';
    // case Modules.WS:
    //   return '$apiBaseUrl${Url.CITIZEN_PAYMENT_SUCCESS}/$businessService/$consumerCode/$tenantId?workflow=WNS';
    // case Modules.SW:
    //   return '$apiBaseUrl${Url.CITIZEN_PAYMENT_SUCCESS}/$businessService/$consumerCode/$tenantId?workflow=WNS';
    case 'rainmaker-fsm':
      return '$apiBaseUrl${Url.CITIZEN_PAYMENT_SUCCESS}/$businessService/$consumerCode/$tenantId?propertyId=$consumerCode';
    case 'rainmaker-uc':
      return '$apiBaseUrl${Url.CITIZEN_PAYMENT_SUCCESS}/$businessService/$consumerCode/$tenantId?workflow=mcollect';
    // case 'FIRENOC':
    // https://upyog.niua.org/citizen/egov-common/paymentRedirectPage
    // return '$apiBaseUrl${Url.CITIZEN_PAYMENT_SUCCESS}/$businessService/$consumerCode/$tenantId';

    default:
      return '$apiBaseUrl${Url.CITIZEN_PAYMENT_SUCCESS}/$businessService/$consumerCode/$tenantId';
  }
}

Future<void> showDatePickerModal({
  required BuildContext context,
  required DateTime? selectedDate,
  required Function(DateTime) onDateSelected,
  required Function() onClear,
  required Function() onToday,
  Color? primaryColor,
}) async {
  showModalBottomSheet(
    context: context,
    builder: (BuildContext context) {
      return SizedBox(
        height: 300,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Align(
              alignment: Alignment.centerRight,
              child: IconButton(
                icon: Icon(
                  Icons.close,
                  color: primaryColor ?? BaseConfig.appThemeColor1,
                ),
                onPressed: () => Get.back(),
              ),
            ),
            Expanded(
              child: CupertinoDatePicker(
                initialDateTime: selectedDate ?? DateTime.now(),
                mode: CupertinoDatePickerMode.date,
                onDateTimeChanged: (DateTime newDate) {
                  onDateSelected(newDate);
                },
              ),
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                TextButton(
                  child: Text(
                    'Clear',
                    style: TextStyle(
                      color: primaryColor ?? BaseConfig.appThemeColor1,
                    ),
                  ),
                  onPressed: () {
                    onClear();
                    Get.back();
                  },
                ),
                TextButton(
                  child: Text(
                    'Today',
                    style: TextStyle(
                      color: primaryColor ?? BaseConfig.appThemeColor1,
                    ),
                  ),
                  onPressed: () {
                    onToday();
                    Get.back();
                  },
                ),
              ],
            ),
            SizedBox(height: 20.h),
          ],
        ),
      );
    },
  );
}

/// Get local
Future<String> getLocal() async {
  final local = await HiveService.getData(
    Constants.LOCALE,
  );

  dPrint(local.toString());
  return local ?? '';
}
