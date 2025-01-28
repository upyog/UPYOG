import 'dart:async';

import 'package:get/get.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/model/citizen/bpa_model/bpa_model.dart';
import 'package:mobile_app/model/citizen/bpa_model/comparison/comparison_model.dart';
import 'package:mobile_app/model/citizen/bpa_model/noc/noc_model.dart';
import 'package:mobile_app/model/citizen/bpa_model/scrutiny_model.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/model/employee/emp_bpa_model/emp_bpa_model.dart';
import 'package:mobile_app/repository/bpa_repository.dart';
import 'package:mobile_app/repository/inbox_repository.dart';
import 'package:mobile_app/repository/payment_repository.dart';
import 'package:mobile_app/services/mdms_service.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:rxdart/rxdart.dart';

class BpaController extends GetxController {
  //Stream
  var streamCtrl = StreamController.broadcast();
  var streamCtrlInd = StreamController.broadcast();
  final bpaNocDetailStream = BehaviorSubject();
  final bpaFeeDetailStream = BehaviorSubject();

  ///Bpa Model
  late Bpa bpa;
  EmpBpaModel? empBpaModel;
  late BpaElement bpaElement;
  Scrutiny? scrutiny;
  late Noc noc;
  late PaymentModel paymentModel;
  Comparison? comparison;

  RxBool isLoading = false.obs;
  RxInt lengthBpa = 0.obs;

  int limit = 10, offset = 0;
  RxInt totalCount = 0.obs;

  String assigneeUuid = '';

  /// Load more bpa Application
  Future<void> loadMoreBpaApp({
    required String token,
    required String tenantId,
    required String applicationType,
    required mobileNumber,
  }) async {
    isLoading.value = true;
    if (lengthBpa.value >= limit) {
      offset += limit;
      limit = 10;
      await getBpaApplications(
        token: token,
        tenantId: tenantId,
        applicationType: applicationType,
        mobileNumber: mobileNumber,
      );
    }
    isLoading.value = false;
  }

  bool isDownloadAvailable(String? status, String? businessService) {
    bool isInProgressOrApproved = status == BpaStatus.APPROVED.name ||
        status == BpaStatus.DOC_VERIFICATION_INPROGRESS.name ||
        status == BpaStatus.FIELD_INSPECTION_INPROGRESS.name ||
        status == BpaStatus.NOC_VERIFICATION_INPROGRESS.name ||
        status == BpaStatus.APPROVAL_INPROGRESS.name;

    bool isBpaOcStatus = status == BpaStatus.CITIZEN_APPROVAL_INPROCESS.name ||
        status == BpaStatus.INPROGRESS.name ||
        status == BpaStatus.PENDING_APPL_FEE.name;

    if (businessService == BusinessServicesEmp.BPA.name) {
      return isInProgressOrApproved;
    } else if (businessService == BusinessServicesEmp.BPA_LOW.name ||
        businessService == BusinessServicesEmp.BPA_OC.name) {
      return isInProgressOrApproved ||
          (businessService == BusinessServicesEmp.BPA_OC.name && isBpaOcStatus);
    }

    return false;
  }

  /// Get bpa Application
  Future<void> getBpaApplications({
    required String? token,
    required String? applicationType,
    required String tenantId,
    required String? mobileNumber,
    final bool isCertificate = false,
  }) async {
    if (token == null && token!.isEmpty) return;
    isLoading.value = true;
    try {
      Map<String, String?> query;

      if (isCertificate) {
        query = {
          'tenantId': tenantId,
          'applicationType': applicationType,
          'status': 'APPROVED',
          'requestor': mobileNumber,
          'mobileNumber': mobileNumber,
          'limit': limit.toString(),
          'offset': offset.toString(),
        };
      } else {
        query = {
          'tenantId': tenantId,
          'applicationType': applicationType,
          'requestor': mobileNumber,
          'mobileNumber': mobileNumber,
          'limit': limit.toString(),
          'offset': offset.toString(),
        };
      }

      final bpaResponse = await BpaRepository.getBpaApplications(
        token: token,
        query: query,
      );
      bpa = bpaResponse;
      streamCtrl.add(bpa);
      lengthBpa.value = bpa.bpaele?.length ?? 0;
      isLoading.value = false;
    } catch (e, s) {
      isLoading.value = false;
      dPrint('GetBpaApplicationsError: $e');
      streamCtrl.add('Bpa Application Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  Future<void> getIndividualBpaApplications({
    required String token,
    required String? applicationNo,
    required String tenantId,
  }) async {
    // isLoading.value = true;
    try {
      final bpaResponse = await BpaRepository.getBpaApplications(
        token: token,
        query: {
          'tenantId': tenantId,
          'applicationNo': applicationNo,
        },
      );
      bpa = bpaResponse;
      bpaElement = bpa.bpaele!.first;
      streamCtrlInd.add(bpa);
    } catch (e, s) {
      dPrint('GetBpaApplicationsError: $e');
      streamCtrlInd.add('Bpa Application Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  Future<Bpa?> getIndividualBpaAppDCR({
    required String token,
    required String? approvalNo,
    required String tenantId,
  }) async {
    Bpa? bpaRes;
    try {
      final query = {
        'tenantId': tenantId,
        'approvalNo': approvalNo,
      };

      final bpaResponse = await BpaRepository.getBpaApplications(
        token: token,
        query: query,
      );
      bpaRes = bpaResponse;
    } catch (e, s) {
      dPrint('getIndividualBpaAppDCR-Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return bpaRes;
  }

  BpaElement? getApplicationById({String? applicationNo}) {
    if (bpa.bpaele == null) return null;
    final filteredList = bpa.bpaele!
        .where(
          (element) => element.applicationNo == applicationNo,
        )
        .toList();
    return filteredList.isNotEmpty ? filteredList.first : null;
  }

  Future<void> getIndividualEdcrApplications({
    required String? token,
    required String? edcrNo,
    required String tenantId,
  }) async {
    if (token == null && token!.isEmpty) return;
    try {
      final scrutinyResponse = await BpaRepository.getEdcrApplications(
        token: token,
        query: {
          'tenantId': tenantId,
          'edcrNumber': edcrNo,
        },
      );
      scrutiny = scrutinyResponse;
      streamCtrlInd.add(scrutiny);
    } catch (e, s) {
      dPrint('GetScrutinyApplicationsError: $e');
      streamCtrlInd.add('Scrutiny Application Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  EdcrDetail? getEdcrList({String? edcrNo}) {
    if (scrutiny?.edcrDetail == null) return null;
    final filteredList = scrutiny!.edcrDetail!
        .where(
          (element) => element.edcrNumber == edcrNo,
        )
        .toList();
    return filteredList.isNotEmpty ? filteredList.first : null;
  }

  Future<void> getBpaNocDetail({
    required String token,
    required String sourceRefId,
    required String tenantId,
  }) async {
    try {
      final nocResponse = await BpaRepository.getBpaNocDetail(
        token: token,
        query: {
          'tenantId': tenantId,
          'sourceRefId': sourceRefId,
        },
      );
      noc = nocResponse;
      bpaNocDetailStream.sink.add(noc);
    } catch (e, s) {
      dPrint('BPA_NOC_DETAIL_ERROR: $e');
      bpaNocDetailStream.add('BPA_NOC_DETAIL_ERROR');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  Future<void> getBpaFeeDetail({
    required String? token,
    required String? consumerCodes,
    required String tenantId,
    required bool isEmployee,
    required String businessService,
  }) async {
    if (token == null && token!.isEmpty) return;
    try {
      final bpaFeeResponse = await PaymentRepository.verifyPayment(
        token: token,
        businessService: businessService,
        query: {
          'tenantId': tenantId,
          'consumerCodes': consumerCodes,
          'isEmployee': isEmployee.toString(),
        },
      );
      paymentModel = PaymentModel.fromJson(bpaFeeResponse);
      bpaFeeDetailStream.sink.add(paymentModel);
    } catch (e, s) {
      dPrint('BPA_PAYMENTS_ERROR: $e');
      bpaFeeDetailStream.add('BPA_PAYMENTS_ERROR');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Get comparison data
  Future<Comparison?> getComparisonData({
    required String token,
    required String tenantId,
    required String ocdcrNumber,
    required String edcrNumber,
    required String propertyId,
  }) async {
    try {
      final query = {
        'tenantId': tenantId,
        'ocdcrNumber': ocdcrNumber,
        'edcrNumber': edcrNumber,
        'propertyId': propertyId,
      };

      final comparisonRes = await BpaRepository.getComparison(
        token: token,
        query: query,
      );

      comparison = comparisonRes;
    } catch (e, s) {
      dPrint('getComparisonData Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }

    return comparison;
  }

  @override
  void onClose() {
    super.onClose();
    streamCtrl.close();
    streamCtrlInd.close();
    bpaNocDetailStream.close();
    bpaFeeDetailStream.close();
  }

  /* -------------------------------------------------------------------------- */
  /*                                For Employee                                */
  /* -------------------------------------------------------------------------- */

  //Check roles for fieldInspection
  bool checkRolesForBpaFieldInspection() {
    final roles = Get.find<AuthController>().token?.userRequest?.roles;
    if (roles == null) return false;

    return roles.any(
      (element) => element.code == InspectorType.BPA_FIELD_INSPECTOR.name,
    );
  }

  /// Get bpa Inbox Application
  Future<void> getEmpBpaInboxApplications({
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
        module: ModulesEmp.BPA_SERVICES,
        businessServices: [
          BusinessServicesEmp.BPA,
          BusinessServicesEmp.BPA_LOW,
          BusinessServicesEmp.BPA_OC,
        ],
      );

      final empRes = await InboxRepository.getInboxApplications(
        token: token,
        body: body,
      );

      empBpaModel = EmpBpaModel.fromJson(empRes);

      //Check employee permission for field inspection
      final isFieldInspector = checkRolesForBpaFieldInspection();

      // Filter wsItems for PENDING_FIELD_INSPECTION status
      final filteredItems = empBpaModel?.items
          ?.where(
            (item) =>
                (item.processInstance?.state?.state ==
                    InboxStatus.FIELDINSPECTION_PENDING.name) &&
                isFieldInspector,
          )
          .toList();

      empBpaModel?.items = filteredItems;

      totalCount.value = empBpaModel?.items?.length ?? 0;
      dPrint('EMP BPA: ${empBpaModel?.items!.length}');
      streamCtrl.add(empBpaModel);
    } catch (e, s) {
      dPrint('GetEmpBpaInboxError: $e');
      streamCtrl.add('Emp BPA Inbox Error');
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
    await getEmpBpaInboxApplications(token: token, tenantId: tenantId);
    isLoading.value = false;
  }

  void setDefaultLimit() {
    lengthBpa.value = 0;
    limit = 10;
    offset = 0;
  }

  // EMP - BPA - Action update
  Future<(bool, String?)> empActionUpdate({
    required String token,
    required ModulesEmp module,
  }) async {
    try {
      var body = {
        'BPA': bpaElement.toJson(),
      };

      final empRes = await InboxRepository.actionUpdate(
        token: token,
        body: body,
        type: module,
      );

      bpa = Bpa.fromJson(empRes);

      dPrint('------Action Update------');
      dPrint(bpa.toJson());
      if (bpa.responseInfo!.status == 'successful') {
        return (true, bpa.bpaele?.firstOrNull?.applicationNo);
      }
      return (false, null);
    } catch (e, s) {
      dPrint('BPA_UpdateEmpTlActionError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
      return (false, null);
    }
  }
}
