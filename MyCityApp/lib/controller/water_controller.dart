import 'dart:async';
import 'dart:convert';

import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/model/citizen/water_sewerage/consumption.dart';
import 'package:mobile_app/model/citizen/water_sewerage/sewerage.dart';
import 'package:mobile_app/model/citizen/water_sewerage/water.dart';
import 'package:mobile_app/model/common/estimate_model/estimate_model.dart';
import 'package:mobile_app/model/employee/emp_mdms_model/emp_mdms_model.dart';
import 'package:mobile_app/model/employee/emp_ws_model/emp_ws_model.dart';
import 'package:mobile_app/repository/core_repository.dart';
import 'package:mobile_app/repository/inbox_repository.dart';
import 'package:mobile_app/repository/water_repository.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/services/mdms_service.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class WaterController extends GetxController {
  final streamCtrl = StreamController.broadcast();
  final sewerageStreamCtrl = StreamController.broadcast();

  Water? water;
  Sewerage? sewerage;
  Consumption? consumption;

  late EmpWsModel empWsModel;
  WaterConnection? waterConnection;
  SewerageConnection? sewerageConnection;

  late EmpMdmsResModel empMdmsResModel;
  Estimate? estimate;

  //EMP - Edit application water/sewerage connection
  final Rxn<WaterConnection> editWaterConnection = Rxn<WaterConnection>();
  final Rxn<SewerageConnection> editSewerageConnection =
      Rxn<SewerageConnection>();

  RxInt length = 0.obs;
  RxBool isLoading = false.obs;

  RxInt totalCount = 0.obs;

  int limit = 10, offset = 0;
  String assigneeUuid = '';

  /* -------------------------------------------------------------------------- */
  //For EMP WS/SW edit application
  RxString natureConnection = ''.obs,
      waterSource = ''.obs,
      waterSourceSplit = ''.obs,
      waterSubSource = ''.obs,
      waterSubSourceSplit = ''.obs,
      noOfTaps = ''.obs,
      noOfWaterClosets = ''.obs,
      numberOfToiletSeats = ''.obs,
      plumberLicenseNo = ''.obs,
      plumberName = ''.obs,
      plumberMobileNo = ''.obs,
      roadTypeBy = ''.obs,
      areaPlot = ''.obs;

  RxDouble pipeSize = 0.0.obs;

  RxString plumberProvidedBy = ''.obs;

  clearEditAppDataEmp() {
    natureConnection.value = '';
    waterSource.value = '';
    waterSourceSplit.value = '';
    waterSubSource.value = '';
    waterSubSourceSplit.value = '';
    noOfTaps.value = '';
    pipeSize.value = 0.0;
    plumberProvidedBy.value = '';
    plumberLicenseNo.value = '';
    plumberName.value = '';
    plumberMobileNo.value = '';
    roadTypeBy.value = '';
    areaPlot.value = '';
    noOfWaterClosets.value = '';
    numberOfToiletSeats.value = '';
  }

  /* -------------------------------------------------------------------------- */

  /// Check Employee connection details is completed
  bool isConnectionDetailsCompleted() {
    return isNotNullOrEmpty(natureConnection.value) &&
        isNotNullOrEmpty(waterSource.value) &&
        isNotNullOrEmpty(waterSubSource.value) &&
        isNotNullOrEmpty(noOfTaps.value) &&
        isNotNullOrEmpty(pipeSize.value);
  }

  /// Check Employee connection details is completed
  bool isPlumberDetailsCompleted() {
    return isNotNullOrEmpty(plumberProvidedBy.value);
  }

  /// Load more Water App
  Future<void> loadMoreWaterApp({
    required String token,
    required mobileNumber,
    required String tenantId,
    String? searchType,
    bool isSewerage = false,
  }) async {
    try {
      isLoading.value = true;
      offset += limit;
      if (isSewerage) {
        await getSewerageMyApplications(
          token: token,
          tenantId: tenantId,
          mobileNumber: mobileNumber,
          searchType: searchType,
        );
      } else {
        await getWaterMyApplications(
          token: token,
          tenantId: tenantId,
          mobileNumber: mobileNumber,
          searchType: searchType,
        );
      }
    } finally {
      isLoading.value = false;
    }
  }

  @override
  void onClose() {
    super.onClose();
    streamCtrl.close();
    sewerageStreamCtrl.close();
  }

  bool isDownloadAvailable(String? status, String? applicationStatus) {
    final activeStatuses = {
      WsStatus.CONNECTION_ACTIVATED.name,
      WsStatus.PENDING_APPROVAL_FOR_CONNECTION.name,
    };

    final pendingOrActivatedStatuses = {
      WsStatus.CONNECTION_ACTIVATED.name,
      WsStatus.PENDING_FOR_DISCONNECTION_EXECUTION.name,
      WsStatus.PENDING_FOR_CONNECTION_ACTIVATION.name,
    };

    return (status == WsStatus.ACTIVE.name &&
            activeStatuses.contains(applicationStatus)) ||
        (status == WsStatus.INACTIVE.name &&
            applicationStatus == WsStatus.DISCONNECTION_EXECUTED.name) ||
        pendingOrActivatedStatuses.contains(applicationStatus);
  }

  /// Get my water applications
  Future<void> getWaterMyApplications({
    required String tenantId,
    required mobileNumber,
    required String token,
    String? searchType,
    bool isCertificate = false,
  }) async {
    try {
      Map<String, dynamic> query = {};

      if (isCertificate) {
        query = {
          "tenantId": tenantId,
          "mobileNumber": mobileNumber,
          "sortOrder": 'DESC',
          "isConnectionSearch": 'true',
          "applicationStatus": 'CONNECTION_ACTIVATED',
        };
      } else {
        if (searchType != null) {
          query = {
            "tenantId": tenantId,
            "mobileNumber": mobileNumber,
            "searchType": searchType,
            "limit": limit.toString(),
            "offset": offset.toString(),
          };
        } else {
          query = {
            "tenantId": tenantId,
            "mobileNumber": mobileNumber,
            "limit": limit.toString(),
            "offset": offset.toString(),
          };
        }
      }
      final res = await WaterRepository.getWater(
        token: token,
        query: query,
        type: ModulesEmp.WS_SERVICES,
      );

      water = Water.fromJson(res);
      length.value = water?.waterConnection?.length ?? 0;

      streamCtrl.add(water);
    } catch (e, s) {
      streamCtrl.add('getWaterMyApplications Error');
      dPrint('getWaterMyApplications Error: $e');
      await ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Get my water applications
  Future<Water?> getWaterMyApplicationsFuture({
    required String tenantId,
    required String mobileNumber,
    required String token,
    String? searchType,
    String? connectionNos,
  }) async {
    try {
      Map<String, dynamic> query = {
        "tenantId": tenantId,
        "mobileNumber": mobileNumber,
        "limit": limit.toString(),
        "offset": offset.toString(),
      };

      if (searchType != null) {
        query["searchType"] = searchType;
      }
      if (connectionNos != null) {
        query["connectionNos"] = connectionNos;
      }

      final res = await WaterRepository.getWater(
        token: token,
        query: query,
        type: ModulesEmp.WS_SERVICES,
      );

      water = Water.fromJson(res);
      length.value = water?.waterConnection?.length ?? 0;
    } catch (e, s) {
      print('getWaterMyApplicationsFuture Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return water;
  }

  /// Get Water Consumption Details
  Future<Consumption?> getWaterConsumptionDetailFuture({
    required String tenantId,
    required String mobileNumber,
    required String token,
    String? searchType,
    String? connectionNos,
  }) async {
    try {
      Map<String, dynamic> query = {
        "tenantId": tenantId,
        "mobileNumber": mobileNumber,
        "connectionNos": connectionNos,
      };

      final res = await WaterRepository.getWater(
        token: token,
        query: query,
        type: ModulesEmp.WS_CONSUMPTION,
      );

      consumption = Consumption.fromJson(res);
      length.value = consumption?.meterReadings?.length ?? 0;
    } catch (e, s) {
      print('getWaterConsumptionDetailsFuture Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }

    return consumption;
  }

  /// Get my sewerage applications
  Future<void> getSewerageMyApplications({
    required String tenantId,
    required mobileNumber,
    required String token,
    String? searchType,
    bool isCertificate = false,
  }) async {
    try {
      Map<String, dynamic> query = {};

      if (searchType != null) {
        query = {
          "tenantId": tenantId,
          "mobileNumber": mobileNumber,
          "searchType": searchType,
          "limit": limit.toString(),
          "offset": offset.toString(),
        };
      } else {
        query = {
          "tenantId": tenantId,
          "mobileNumber": mobileNumber,
          "limit": limit.toString(),
          "offset": offset.toString(),
        };
      }

      final res = await WaterRepository.getWater(
        token: token,
        query: query,
        type: ModulesEmp.SW_SERVICES,
      );

      sewerage = Sewerage.fromJson(res);
      length.value = sewerage?.sewerageConnections?.length ?? 0;
      sewerageStreamCtrl.add(sewerage);
    } catch (e, s) {
      sewerageStreamCtrl.add('getSewerageMyApplications Error');
      print('getSewerageMyApplications Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Fetches sewerage applications and returns the data.
  Future<Sewerage?> getSewerageMyApplicationsFuture({
    required String tenantId,
    required String mobileNumber,
    required String token,
    String? searchType,
    bool isCertificate = false,
  }) async {
    try {
      Map<String, dynamic> query = {};

      if (isCertificate) {
        query = {
          "tenantId": tenantId,
          //"sortBy":commencementDate,
          "mobileNumber": mobileNumber,
          "sortOrder": 'DESC',
          "isConnectionSearch": 'true',
          "applicationStatus": 'CONNECTION_ACTIVATED',
        };
      } else {
        query = {
          "tenantId": tenantId,
          "mobileNumber": mobileNumber,
          "searchType": searchType,
        };
      }
      final res = await WaterRepository.getWater(
        token: token,
        query: query,
        type: ModulesEmp.SW_SERVICES,
      );

      sewerage = Sewerage.fromJson(res);
      length.value = sewerage?.sewerageConnections?.length ?? 0;

      sewerageStreamCtrl.add(sewerage);
    } catch (e, s) {
      print('getSewerageMyApplications Error: $e');
      sewerageStreamCtrl.add('Error fetching sewerage applications');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return sewerage;
  }

  /* -------------------------------------------------------------------------- */
  /*                                For Employee                                */
  /* -------------------------------------------------------------------------- */

  //Check roles for fieldInspection
  bool checkRolesForWsSwFieldInspection() {
    final roles = Get.find<AuthController>().token?.userRequest?.roles;
    if (roles == null) return false;

    return roles.any(
      (element) => (element.code == InspectorType.WS_FIELD_INSPECTOR.name ||
          element.code == InspectorType.SW_FIELD_INSPECTOR.name),
    );
  }

  /// Get WS inbox applications for employee
  Future<void> getEmpWsInboxApplications({
    required String token,
    required String tenantId,
    bool isFilter = false,
    List<String>? locality,
    String? assigneeUid,
  }) async {
    try {
      final body = getEmpBodyFilter(
        locality: locality,
        assigneeUid: assigneeUid,
        isFilter: isFilter,
        limit: limit,
        offset: offset,
        tenantId: tenantId,
        module: ModulesEmp.WS_SERVICES,
        businessServices: [
          BusinessServicesEmp.NEW_WS1,
          BusinessServicesEmp.MODIFY_WS_CONNECTION,
          BusinessServicesEmp.DISCONNECT_WS_CONNECTION,
        ],
      );

      final empRes = await InboxRepository.getInboxApplications(
        token: token,
        body: body,
      );

      empWsModel = EmpWsModel.fromJson(empRes);

      //Check employee permission for field inspection
      final isFieldInspector = checkRolesForWsSwFieldInspection();

      final filteredItems = empWsModel.wsItems
          ?.where(
            (item) =>
                (item.businessObject?.data?.applicationStatus ==
                    InboxStatus.PENDING_FIELD_INSPECTION.name) &&
                isFieldInspector,
          )
          .toList();

      // Filter wsItems for PENDING_FIELD_INSPECTION status
      empWsModel.wsItems = filteredItems;

      totalCount.value = empWsModel.wsItems?.length ?? 0;

      dPrint('EMP WS: ${empWsModel.wsItems!.length}');
      streamCtrl.add(empWsModel);
    } catch (e, s) {
      print('GetEmpWsInboxError: $e');
      streamCtrl.add('Emp WS Inbox Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Get SW inbox applications for employee
  Future<void> getEmpSwInboxApplications({
    required String token,
    required String tenantId,
    bool isFilter = false,
    List<String>? locality,
    String? assigneeUid,
  }) async {
    try {
      final body = getEmpBodyFilter(
        locality: locality,
        assigneeUid: assigneeUid,
        isFilter: isFilter,
        limit: limit,
        offset: offset,
        tenantId: tenantId,
        module: ModulesEmp.SW_SERVICES,
        businessServices: [
          BusinessServicesEmp.NEW_SW1,
          BusinessServicesEmp.MODIFY_SW_CONNECTION,
          BusinessServicesEmp.DISCONNECT_SW_CONNECTION,
        ],
      );

      final empRes = await InboxRepository.getInboxApplications(
        token: token,
        body: body,
      );

      empWsModel = EmpWsModel.fromJson(empRes);

      //Check employee permission for field inspection
      final isFieldInspector = checkRolesForWsSwFieldInspection();

      // Filter wsItems for PENDING_FIELD_INSPECTION status
      final pendingItems = empWsModel.wsItems
          ?.where(
            (item) =>
                (item.businessObject?.data?.applicationStatus ==
                    InboxStatus.PENDING_FIELD_INSPECTION.name) &&
                isFieldInspector,
          )
          .toList();

      empWsModel.wsItems = pendingItems;

      totalCount.value = empWsModel.wsItems!.length;
      dPrint('EMP SW: ${empWsModel.wsItems!.length}');
      streamCtrl.add(empWsModel);
    } catch (e, s) {
      print('GetEmpSwInboxError: $e');
      streamCtrl.add('Emp SW Inbox Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Load more my properties
  Future<void> loadMore({
    required String token,
    required String tenantId,
  }) async {
    isLoading.value = true;
    limit = limit + 10;
    await getEmpWsInboxApplications(token: token, tenantId: tenantId);
    isLoading.value = false;
  }

  Future<void> loadMoreSw({
    required String token,
    required String tenantId,
  }) async {
    isLoading.value = true;
    limit = limit + 10;
    await getEmpSwInboxApplications(token: token, tenantId: tenantId);
    isLoading.value = false;
  }

  void setDefaultLimit() {
    limit = 10;
    offset = 0;
  }

  /// Get water by id
  Future<void> getWaterConnection({
    required String tenantId,
    required String token,
    required String appId,
    required ModulesEmp module,
  }) async {
    try {
      final query = {
        "tenantId": tenantId,
        "applicationNumber": appId,
      };

      final res = await WaterRepository.getWater(
        token: token,
        query: query,
        type: module,
      );

      if (module == ModulesEmp.WS_SERVICES) {
        water = Water.fromJson(res);
        if (water?.waterConnection != null) {
          waterConnection = water!.waterConnection!.first;
        }
      } else {
        sewerage = Sewerage.fromJson(res);
        if (sewerage?.sewerageConnections != null) {
          sewerageConnection = sewerage!.sewerageConnections!.first;
        }
      }
    } catch (e, s) {
      print('getWaterConnection Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Get water estimate charge
  Future<void> getEstimateCharge({
    required String tenantId,
    required String token,
    required String appNo,
    required Modules module,
  }) async {
    try {
      final body = module == Modules.WS
          ? {
              "CalculationCriteria": [
                {
                  "applicationNo": appNo,
                  "tenantId": tenantId,
                  'waterConnection': waterConnection?.toJson(),
                }
              ],
              "isconnectionCalculation": 'false',
            }
          : {
              "CalculationCriteria": [
                {
                  "applicationNo": appNo,
                  "tenantId": tenantId,
                  'sewerageConnection': sewerageConnection?.toJson(),
                }
              ],
              "isconnectionCalculation": 'false',
            };

      final estimateRes = await WaterRepository.getWaterEstimation(
        token: token,
        type: module,
        body: body,
      );

      estimate = Estimate.fromJson(estimateRes);
    } catch (e, s) {
      print('getEstimateCharge Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// EMP - WS/SW - Action update
  Future<(bool, String?)> empActionUpdate({
    required String token,
    WaterConnection? waterConnection,
    SewerageConnection? sewerageConnection,
    required ModulesEmp module,
  }) async {
    var body = {};

    try {
      if (module == ModulesEmp.WS_SERVICES) {
        body = {
          'WaterConnection': waterConnection!.toJson(),
          'disconnectRequest': false,
          'reconnectRequest': false,
        };
      }
      if (module == ModulesEmp.SW_SERVICES) {
        body = {
          'SewerageConnection': sewerageConnection!.toJson(),
          'disconnectRequest': false,
          'reconnectRequest': false,
        };
      }

      final empRes = await InboxRepository.actionUpdate(
        token: token,
        body: body,
        type: module,
      );

      if (module == ModulesEmp.WS_SERVICES) {
        water = Water.fromJson(empRes);
        print('------Action Update------');
        print(waterConnection?.toJson());
        if (water!.responseInfo!.status == 'successful') {
          return (true, water?.waterConnection?.firstOrNull?.applicationNo);
        }
      }
      if (module == ModulesEmp.SW_SERVICES) {
        sewerage = Sewerage.fromJson(empRes);
        print('------Action Update------');
        print(sewerage?.toJson());
        if (sewerage!.responseInfo!.status == 'successful') {
          return (
            true,
            sewerage?.sewerageConnections?.firstOrNull?.applicationNo
          );
        }
      }

      return (false, null);
    } catch (e, s) {
      print('WS/WS_UpdateEmpTlActionError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
      return (false, null);
    }
  }

  // EMP - WS/SW - Mdms service call
  Future<void> getMdmsWsSw() async {
    try {
      final query = {
        'tenantId': BaseConfig.STATE_TENANT_ID,
      };

      final empRes = await CoreRepository.getMdmsDynamic(
        query: query,
        body: getEmpMdmsBodyWsSw(),
      );

      empMdmsResModel = EmpMdmsResModel.fromJson(empRes);
    } catch (e, s) {
      print('GetMdmsWsSw Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Check WS/SW additional details for EMP
  bool checkWaterAdditionalDetails() {
    final wfService = waterConnection?.processInstance?.businessService;
    if (wfService == BusinessService.WS_RECONNECTION.name ||
        wfService == BusinessService.SW_RECONNECTION.name) {
      return true;
    } else {
      final connectionType = waterConnection?.connectionType;

      final noOfTaps =
          waterConnection?.noOfTaps == 0 ? null : waterConnection?.noOfTaps;

      final pipeSize =
          waterConnection?.pipeSize == 0 ? null : waterConnection?.pipeSize;

      final waterSource = waterConnection?.waterSource;

      final plumberDetails =
          waterConnection?.additionalDetails?.detailsProvidedBy;

      final roadCuttingInfo = waterConnection?.roadCuttingInfo;

      if (!isNotNullOrEmpty(connectionType) ||
          !((isNotNullOrEmpty(noOfTaps) &&
              isNotNullOrEmpty(pipeSize) &&
              isNotNullOrEmpty(waterSource))) ||
          !isNotNullOrEmpty(plumberDetails) ||
          !isNotNullOrEmpty(roadCuttingInfo)) {
        return false;
      }

      return true;
    }
  }

  List<String> getSubWaterSourceTypes() {
    final waterSubSources =
        empMdmsResModel.mdmsResEmp?.wsServicesMasters?.waterSource ?? [];

    final waterSubSourcesList = waterSubSources
        .where(
          (e) =>
              (e.active ?? false) &&
              isNotNullOrEmpty(e.code) &&
              e.code!.contains(waterSourceSplit.value),
        )
        .map((e) => e.code!.replaceAll('.', '_'))
        .toList();

    return waterSubSourcesList;
  }

  /// Check WS/SW additional details for EMP
  Future<bool> checkEditWaterDetailsLocalData(String value) async {
    if (value == 'VERIFY_AND_FORWARD') {
      final editAppData = await HiveService.getData(
        HiveConstants.WS_SESSION_APPLICATION_DETAILS,
      );

      dPrint('Edit App Data: $editAppData');

      if (!isNotNullOrEmpty(editAppData)) {
        return true;
      }
    }
    return false;
  }

  /// Clear edit water details data
  Future<void> clearEditWaterDetailsLocalData() async {
    await HiveService.deleteData(HiveConstants.WS_SESSION_APPLICATION_DETAILS);
    editSewerageConnection.value = null;
    editWaterConnection.value = null;

    dPrint('Edit Application Data Cleared');
  }

  /// Update edit water details data for emp ws/sw
  void updateEditApplicationFormData({Modules module = Modules.WS}) async {
    final data =
        await HiveService.getData(HiveConstants.WS_SESSION_APPLICATION_DETAILS);
    if (isNotNullOrEmpty(data) && module == Modules.WS) {
      editWaterConnection.value = WaterConnection.fromJson(jsonDecode(data));
    }
    if (isNotNullOrEmpty(data) && module == Modules.SW) {
      editSewerageConnection.value =
          SewerageConnection.fromJson(jsonDecode(data));
    }
  }
}
