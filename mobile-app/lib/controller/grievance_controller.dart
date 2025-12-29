import 'dart:async';
import 'dart:convert';

import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/model/citizen/grievance/grievance.dart';
import 'package:mobile_app/model/citizen/localization/mdms_static_data.dart';
import 'package:mobile_app/model/employee/emp_mdms_model/emp_mdms_model.dart'
    as emp;
import 'package:mobile_app/repository/core_repository.dart';
import 'package:mobile_app/repository/grievance_repository.dart';
import 'package:mobile_app/repository/inbox_repository.dart';
import 'package:mobile_app/screens/citizen/grievance_redressal/new_grievance_form/new_grievance_form.dart';
import 'package:mobile_app/services/mdms_service.dart';
import 'package:mobile_app/services/secure_storage_service.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class GrievanceController extends GetxController {
  var streamCtrl = StreamController.broadcast();

  late Grievance grievance;
  ServiceWrapper? serviceWrapper;
  RainmakerPgr? rainmakerPgr;
  emp.EmpMdmsResModel? empMdmsResModel;
  int limit = 10, offset = 0;
  RxInt length = 0.obs;
  RxBool isLoading = false.obs, isMore = false.obs;

  @override
  void onClose() {
    super.onClose();
    streamCtrl.close();
  }

  /// Load more Grievance Application
  Future<void> loadMoreGriApp({
    required String token,
    required String tenantId,
    String? mobileNo,
  }) async {
    isLoading.value = true;
    if (length.value >= limit) {
      offset += limit;
      limit = 10;
      await getGrievance(token: token, tenantId: tenantId, mobileNo: mobileNo);
    }
    isLoading.value = false;
  }

  Future<void> getGrievance({
    required String token,
    required String tenantId,
    String? mobileNo,
  }) async {
    try {
      final query = {
        "tenantId": tenantId,
        'limit': limit.toString(),
        'offset': offset.toString(),
      };
      final queryPhone = {
        "tenantId": tenantId,
        "mobileNumber": mobileNo,
        'limit': limit.toString(),
        'offset': offset.toString(),
      };
      final grievanceRes = await GrievanceRepository.getGrievances(
        token: token,
        query: isNotNullOrEmpty(mobileNo) ? queryPhone : query,
      );
      if (grievanceRes != null) {
        grievance = Grievance.fromJson(grievanceRes);
        streamCtrl.add(grievance);
        length.value = grievance.serviceWrappers?.length ?? 0;
      }
    } catch (e, s) {
      streamCtrl.add('Grievance Error');
      dPrint('getGrievance Error: ${e.toString()}');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  Future<void> getIndividualGrievance({
    required String token,
    required String tenantId,
    required String serviceRequestId,
  }) async {
    try {
      final query = {
        "tenantId": tenantId,
        "serviceRequestId": serviceRequestId,
      };
      final grievanceRes = await GrievanceRepository.getGrievances(
        token: token,
        query: query,
      );

      serviceWrapper =
          ServiceWrapper.fromJson(grievanceRes['ServiceWrappers'][0]);
    } catch (e, s) {
      dPrint('getIndividualGrievance Error: ${e.toString()}');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  String? getGrievanceAddress(Address? address) {
    if (address == null) return null;
    var city = address.tenantId!.split('.').last.toUpperCase().tr;
    return '${address.landmark}, ${address.locality?.code}, $city';
  }

  // Grievance- mdms-v2 service call
  Future<RainmakerPgr?> getMdmsPgr() async {
    try {
      final query = {
        'tenantId': BaseConfig.STATE_TENANT_ID,
      };

      final getRainmakerPgr = await storage.getString(
        'Digit.MDMS.${BaseConfig.STATE_TENANT_ID}.PGR.serviceDefs',
      );
      if (isNotNullOrEmpty(getRainmakerPgr)) {
        rainmakerPgr = RainmakerPgr.fromJson(jsonDecode(getRainmakerPgr!));
        dPrint('Getting PGR from storage');
        return rainmakerPgr;
      }

      final mdmsPgrRes = await CoreRepository.getMdmsPgrData(
        query: query,
        body: getCitizenMdmsBodyPgr(),
      );

      final mdres = MdmsStaticData.fromJson(mdmsPgrRes);
      rainmakerPgr = mdres.mdmsRes!.rainmakerPgr!;

      await storage.setString(
        'Digit.MDMS.${BaseConfig.STATE_TENANT_ID}.PGR.serviceDefs',
        jsonEncode(rainmakerPgr!.toJson()),
      );

      return rainmakerPgr;
    } catch (e, s) {
      dPrint('GetMdmsPgr Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
      return null;
    }
  }

  // Send grievance data in form
  Future<ServiceWrapper?> submitGrievanceForm({
    required String token,
    required String grievanceSubType,
    required String grievancePriority,
    required String landmark,
    required String additionalDetails,
    required String localityCode,
    required String localityName,
    required String city,
    required String district,
    required String region,
    required String tenantId,
    String pinCode = '',
    required List<VerificationDocumentPGR> verificationDocuments,
  }) async {
    try {
      final query = {
        'tenantId': tenantId,
      };

      final pgrRes = await GrievanceRepository.submitGrievanceForm(
        token: token,
        query: query,
        body: sendGrievanceFormValue(
          grievanceSubType: grievanceSubType,
          grievancePriority: grievancePriority,
          landmark: landmark,
          additionalDetails: additionalDetails,
          localityCode: localityCode,
          localityName: localityName,
          city: city,
          district: district,
          region: region,
          tenantId: tenantId,
          pinCode: pinCode,
          verificationDocuments: verificationDocuments,
        ),
      );
      final pgr = Grievance.fromJson(pgrRes);

      return pgr.serviceWrappers!.first;
    } catch (e, s) {
      dPrint('GetFormSubmit Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
      return null;
    }
  }

  /* -------------------------------------------------------------------------- */
  /*                             EMP - Grievance LME                            */
  /* -------------------------------------------------------------------------- */
  Future<void> loadMoreGriAppEmp({
    required String token,
    required String tenantId,
  }) async {
    isLoading.value = true;
    if (length.value >= limit) {
      offset += limit;
      limit = 10;
      await getGrievancesEmp(token: token, tenantId: tenantId);
    }
    isLoading.value = false;
  }

  Future<void> getGrievancesEmp({
    required String token,
    required String tenantId,
    String? mobileNo,
    String? locality,
  }) async {
    try {
      dynamic query;

      dPrint('limit: $limit, offset: $offset');

      if (isNotNullOrEmpty(mobileNo)) {
        query = {
          "tenantId": tenantId,
          "mobileNumber": mobileNo,
          "limit": limit.toString(),
          "offset": offset.toString(),
          "start": "1",
          "end": "10",
        };
      } else if (isNotNullOrEmpty(locality)) {
        query = {
          "tenantId": tenantId,
          "locality": locality,
          "limit": limit.toString(),
          "offset": offset.toString(),
          "start": "1",
          "end": "10",
        };
      } else {
        query = {
          "tenantId": tenantId,
          "limit": limit.toString(),
          "offset": offset.toString(),
          "start": "1",
          "end": "10",
        };
      }
      final grievanceRes = await GrievanceRepository.getGrievances(
        token: token,
        query: query,
      );

      grievance = Grievance.fromJson(grievanceRes);
      streamCtrl.add(grievance);
      length.value = grievance.serviceWrappers?.length ?? 0;
    } catch (e, s) {
      streamCtrl.add('Grievance Error');
      dPrint('getGrievance Error: ${e.toString()}');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  emp.ServiceDef? getDepartment(String serviceCode) {
    return empMdmsResModel?.mdmsResEmp?.rainmakerPgr?.serviceDefs
        ?.firstWhereOrNull(
      (element) => element.serviceCode == serviceCode,
    );
  }

  // Future<void> getIndividualGrievanceEmp({
  //   required String tenantId,
  //   required String serviceRequestId,
  // }) async {
  //   try {
  //     final query = {
  //       "tenantId": tenantId,
  //       "serviceRequestId": serviceRequestId,
  //     };
  //     final grievanceRes = await GrievanceRepository.getGrievances(
  //       token: '',
  //       query: query,
  //     );
  //     if (grievanceRes != null) {
  //       serviceWrapper =
  //           ServiceWrapper.fromJson(grievanceRes['ServiceWrappers'][0]);
  //     }
  //   } catch (e, s) {
  //     dPrint('getIndividualGrievance Error: ${e.toString()}');
  //     ErrorHandler.allExceptionsHandler(e, s);
  //   }
  // }

  // EMP - PGR/LME - Mdms service call
  Future<void> getEmpMdmsPGR(String tenantCity) async {
    try {
      final serviceDef =
          await storage.getString('Digit.MDMS.$tenantCity.PGR.serviceDefs');
      if (isNotNullOrEmpty(serviceDef)) {
        empMdmsResModel = emp.EmpMdmsResModel.fromJson(jsonDecode(serviceDef!));
        return;
      }
      final query = {
        'tenantId': tenantCity,
      };

      final empRes = await CoreRepository.getMdmsDynamic(
        query: query,
        body: getEmpMdmsBodyPGR(tenantCity),
      );

      empMdmsResModel = emp.EmpMdmsResModel.fromJson(empRes);
      await storage.setString(
        'Digit.MDMS.$tenantCity.PGR.serviceDefs',
        jsonEncode(empMdmsResModel?.toJson()),
      );
    } catch (e, s) {
      dPrint('getEmpMdmsPGR Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  // EMP - TL - Action update
  Future<(bool, String?)> empActionUpdate({
    required String token,
    required ModulesEmp module,
    ServiceWrapper? serviceWrapperRequest,
  }) async {
    try {
      final empRes = await InboxRepository.actionUpdate(
        token: token,
        body: serviceWrapperRequest?.toJson() ?? {},
        type: module,
        isEmpFireNoc: true,
      );

      grievance = Grievance.fromJson(empRes);
      dPrint('------Action Update------');
      dPrint('FireNocModel: ${serviceWrapper?.toJson()}');
      if (grievance.responseInfo!.status == 'successful') {
        return (
          true,
          grievance.serviceWrappers!.first.service!.serviceRequestId!,
        );
      }

      return (false, null);
    } catch (e, s) {
      dPrint('EMP_FIRE_NOC_UpdateActionError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
      return (false, null);
    }
  }
}
