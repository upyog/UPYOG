import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/model/citizen/trade_license/trade_license.dart';
import 'package:mobile_app/model/employee/emp_tl_model/emp_tl_model.dart';
import 'package:mobile_app/repository/inbox_repository.dart';
import 'package:mobile_app/repository/trade_license_repository.dart';
import 'package:mobile_app/services/mdms_service.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:rxdart/rxdart.dart';

class TradeLicenseController extends GetxController {
  //Stream
  var streamCtrl = StreamController.broadcast();
  var streamCtrlApproved = StreamController.broadcast();
  final BehaviorSubject<List<License>> licenseStreamCtrl =
      BehaviorSubject<List<License>>();

//Controller
  final commentsController = TextEditingController();

  ///Trade License Model
  late TradeLicense tradeLicense;

  RxInt length = 0.obs;
  String propertyId = '';
  String? propertyOwnerNames;
  RxBool isLoading = false.obs, isMore = false.obs;

  //For Employee
  var empTlInboxCtrl = StreamController.broadcast();
  EmpTlModel? empTlModel;
  RxInt empTlCount = 0.obs;
  int limit = 10, offset = 0;
  RxBool isOwnerDetails = false.obs;

  @override
  void onClose() {
    super.onClose();
    streamCtrlApproved.close();
    streamCtrl.close();
    commentsController.dispose();
    licenseStreamCtrl.close();
  }

  /// Load more bpa Application
  Future<void> loadMoreTlApp({
    required String token,
    required String tenantId,
    required String renewalTlApp,
  }) async {
    isLoading.value = true;
    if (length.value >= limit) {
      offset += limit;
      limit = 10;
      await getTlApplications(
        token: token,
        tenantId: tenantId,
        renewalTlApp: renewalTlApp,
      );
    }
    isLoading.value = false;
  }

  /// Get user profile
  Future<void> getTlApplications({
    required String? token,
    String? applicationId,
    String? tenantId,
    String? renewalTlApp,
  }) async {
    if (token == null && token!.isEmpty) return;
    Map<String, String?> query;
    try {
      if (isNotNullOrEmpty(tenantId) && isNotNullOrEmpty(applicationId)) {
        query = {
          'applicationNumber': applicationId,
          'tenantId': tenantId,
        };
      } else if (tenantId != null && applicationId == null) {
        query = {
          'tenantId': tenantId,
          'applicationType': renewalTlApp,
          'limit': limit.toString(),
          'offset': offset.toString(),
        };
      } else {
        query = {
          'applicationNumber': applicationId,
        };
      }

      final tlResponse = await TradeLicenseRepository.getTlApplications(
        token: token,
        query: query, // applicationId != null ? query : null,
      );
      tradeLicense = tlResponse;
      streamCtrl.add(tradeLicense);
      length.value = tradeLicense.licenses?.length ?? 0;
    } catch (e, s) {
      dPrint('GetTlApplicationsError: $e');
      streamCtrl.add('TL Application Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Get Trade License owners list by application id
  Future<void> getOwnersLicenseByAppID({
    required String token,
    required String applicationNo,
  }) async {

    final tenantCity = await getCityTenant();

    try {
      isOwnerDetails.value = true;
      final tlResponse = await TradeLicenseRepository.getTlApplications(
        token: token,
        query: {
          'applicationNumber': applicationNo,
          'tenantId': '${tenantCity.code}',
        },
      );

      final licenseFiltered = tlResponse.licenses!
          .where((value) => value.applicationNumber == applicationNo)
          .toList();
      if (licenseFiltered.isNotEmpty) {
        propertyId = licenseFiltered
                .first.tradeLicenseDetail?.additionalDetail?.propertyId ??
            '';
      } else {
        propertyId = '';
      }

      licenseStreamCtrl.sink.add(licenseFiltered);

      dPrint(
        'PropertyId: $propertyId',
      );
    } catch (e, s) {
      dPrint('getOwnersLicenseByAppID Error: $e');
      licenseStreamCtrl.sink.addError('getOwnersLicenseByAppID Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  Future<void> getTlApprovedApplications({
    required String token,
    required String tenantId,
  }) async {
    try {
      var query = {
        'tenantId': tenantId,
        'status': 'APPROVED',
      };
      final tlResponse = await TradeLicenseRepository.getTlApplications(
        token: token,
        query: query,
      );
      tradeLicense = tlResponse;
      streamCtrlApproved.add(tradeLicense);
    } catch (e, s) {
      dPrint('GetTlApplicationsError: $e');
      streamCtrlApproved.add('TL Application Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /* -------------------------------------------------------------------------- */
  /*                                For Employee                                */
  /* -------------------------------------------------------------------------- */

  //Check roles for fieldInspection
  bool checkRolesForTlFieldInspection() {
    final roles = Get.find<AuthController>().token?.userRequest?.roles;
    if (roles == null) return false;

    return roles.any(
      (element) => element.code == InspectorType.TL_FIELD_INSPECTOR.name,
    );
  }

  /// Get TL inbox applications for employee
  Future<void> getEmpTlInboxApplications({
    required String? token,
    required String tenantId,
    bool isFilter = false,
    List<String>? locality,
    String? assigneeUid,
  }) async {
    if (token == null && token!.isEmpty) return;
    try {
      final body = getEmpBodyFilter(
        locality: locality,
        assigneeUid: assigneeUid,
        isFilter: isFilter,
        limit: limit,
        offset: offset,
        tenantId: tenantId,
        module: ModulesEmp.TL_SERVICES,
        businessServices: [
          BusinessServicesEmp.NewTL,
          BusinessServicesEmp.DIRECT_RENEWAL,
          BusinessServicesEmp.EDIT_RENEWAL,
        ],
      );

      final empRes = await InboxRepository.getInboxApplications(
        token: token,
        body: body,
      );
      empTlModel = EmpTlModel.fromJson(empRes);

      //Check employee permission for field inspection
      final isFieldInspector = checkRolesForTlFieldInspection();

      final filteredItems = empTlModel?.items
          ?.where(
            (item) =>
                (item.processInstance?.state?.state ==
                    InboxStatus.FIELD_INSPECTION.name) &&
                isFieldInspector,
          )
          .toList();

      // Filter wsItems for FIELD_INSPECTION status
      empTlModel?.items = filteredItems;

      empTlCount.value = empTlModel?.items?.length ?? 0;
      dPrint('EMP TL: ${empTlCount.value}');
      empTlInboxCtrl.add(empTlModel);
    } catch (e, s) {
      dPrint('GetEmpTlInboxError: $e');
      empTlInboxCtrl.add('Emp TL Inbox Error');
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
    await getEmpTlInboxApplications(token: token, tenantId: tenantId);
    isLoading.value = false;
  }

  void setDefaultLimit() {
    limit = 10;
    offset = 0;
  }

  // EMP - TL - Action update
  Future<(bool, String?)> empTlActionUpdate({
    required String token,
    required License license,
  }) async {
    final lic = jsonEncode(license.toJson());

    commentsController.clear();

    try {
      final body = {
        'Licenses': [jsonDecode(lic)],
      };

      final empRes = await InboxRepository.actionUpdate(
        token: token,
        body: body,
        type: ModulesEmp.TL_SERVICES,
      );

      tradeLicense = TradeLicense.fromJson(empRes);

      if (tradeLicense.responseInfo!.status == 'successful') {
        return (true, tradeLicense.licenses?.firstOrNull?.applicationNumber);
      }

      return (false, null);
    } catch (e, s) {
      dPrint('UpdateEmpTlActionError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
      return (false, null);
    }
  }
}
