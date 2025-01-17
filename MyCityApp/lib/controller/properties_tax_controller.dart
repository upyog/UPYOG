import 'dart:async';

import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/model/employee/emp_pt_model/emp_pt_model.dart';
import 'package:mobile_app/repository/inbox_repository.dart';
import 'package:mobile_app/repository/payment_repository.dart';
import 'package:mobile_app/repository/propertytax_repository.dart';
import 'package:mobile_app/services/mdms_service.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:rxdart/rxdart.dart';

class PropertiesTaxController extends GetxController {
  var streamCtrl = StreamController.broadcast();
  var streamCtrlPt = StreamController.broadcast();
  final mutationPropertyCtrl = BehaviorSubject();
  late String token;

  PtMyProperties? myProperties, myProp;
  Payment? myPayments;
  BillInfo? billInfo;
  late EmpPtModel empPtModel;
  late Property property;

  RxInt length = 0.obs;
  int limit = 10, offset = 0;
  RxBool isLoading = false.obs;
  String assigneeUuid = '';

  @override
  void onClose() {
    super.onClose();
    streamCtrl.close();
    streamCtrlPt.close();
    mutationPropertyCtrl.close();
  }

  /// Load more my properties
  Future<void> loadMore({
    required String token,
    bool isMyApplication = false,
  }) async {
    try {
      isLoading.value = true;
      offset += limit;
      await getMyPropertiesStream(
        token: token,
        isMyApplication: isMyApplication,
      );
    } finally {
      isLoading.value = false;
    }
  }

  void setDefaultLimit() {
    length.value = 0;
    limit = 10;
    offset = 0;
  }

  /// Get my properties
  Future<PtMyProperties?> fetchMyProperties({
    required String token,
    String? propertyIds,
    bool isMyApplication = false,
  }) async {
    isLoading.value = true;
    PtMyProperties? myProperties;
    try {
      Map<String, dynamic> query = {};
      if (propertyIds != null) {
        query = {
          "propertyIds": propertyIds,
        };
      } else {
        final TenantTenant tenantCityId = await getCityTenant();
        query = {
          'tenantId': tenantCityId.code,
          'limit': limit.toString(),
          'sortOrder': 'ASC',
          'sortBy': 'createdTime',
          'offset': offset.toString(),
          'status': isMyApplication ? 'INWORKFLOW' : 'ACTIVE,INACTIVE',
        };
      }

      final res = await PropertyTaxRepository.getMyProperties(
        token: token,
        query: query,
      );

      dPrint('API Response: $res');

      myProperties = PtMyProperties.fromJson(res);

      length.value = myProperties.properties?.length ?? 0;

      isLoading.value = false;
    } catch (e, s) {
      dPrint('getPropertiesError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    } finally {
      isLoading.value = false;
    }
    return myProperties;
  }

  Future<void> getMyPropertiesStream({
    required String token,
    String? propertyIds,
    String? mobileNumber,
    bool isMyApplication = false,
    bool isCertificate = false,
  }) async {
    try {
      Map<String, dynamic> query = {};
      final TenantTenant tenantCityId = await getCityTenant();

      if (isCertificate) {
        query = {
          'tenantId': tenantCityId.code,
          'mobileNumber': mobileNumber,
          'creationReason': 'MUTATION',
          'status': 'ACTIVE',
          'sortOrder': 'DESC',
        };
      } else {
        if (propertyIds != null) {
          query = {
            'tenantId': tenantCityId.code,
            'propertyIds': propertyIds,
            'audit': 'true',
          };
        } else {
          query = {
            'tenantId': tenantCityId.code,
            'limit': limit.toString(),
            'sortOrder': 'ASC',
            'sortBy': 'createdTime',
            'offset': offset.toString(),
            'status': isMyApplication ? 'INWORKFLOW' : 'ACTIVE,INACTIVE',
          };
        }
      }

      final res = await PropertyTaxRepository.getMyProperties(
        token: token,
        query: query,
      );

      myProperties = PtMyProperties.fromJson(res);
      streamCtrl.add(myProperties);
      length.value = myProperties?.properties?.length ?? 0;
    } catch (e, s) {
      streamCtrl.add('getPropertiesError');
      dPrint('getPropertiesError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  Future<void> getMyPropertiesMutation({
    required String token,
    required String tenantId,
    String? propertyIds,
  }) async {
    try {
      var query = {
        'tenantId': tenantId,
        'propertyIds': propertyIds,
        'audit': 'true',
      };

      final res = await PropertyTaxRepository.getMyProperties(
        token: token,
        query: query,
      );

      final myProperties = PtMyProperties.fromJson(res);
      mutationPropertyCtrl.sink.add(myProperties.properties!.first);
    } catch (e, s) {
      mutationPropertyCtrl.add('getPropertiesError');
      dPrint('getPropertiesError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
      return;
    }
  }

  Future<PtMyProperties?> getPropId({
    required String token,
  }) async {
    try {
      final propIdRes = await PropertyTaxRepository.getMyPropertiesWithoutQuery(
        token: token,
      );
      myProp = PtMyProperties.fromJson(propIdRes);
    } catch (e, s) {
      dPrint('getPropertiesError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return myProp;
  }

  Future<List<Payment>?> getPtMyPayments({
    required String token,
    String? consumerCodes,
  }) async {
    List<Payment> paymentList = [];
    try {
      final paymentRes = await PropertyTaxRepository.getPtMyPayments(
        token: token,
        query: {
          'tenantId': BaseConfig.STATE_TENANT_ID,
          'consumerCodes': consumerCodes,
        },
      );
      dPrint('VerifyPayment Data: ${paymentRes['Payments'][0]}');
      final getPayments = paymentRes['Payments'];
      if (getPayments != []) {
        for (var payment in getPayments) {
          final data = Payment.fromJson(payment);
          paymentList.add(data);
        }
      }
    } catch (e, s) {
      paymentList = [];
      dPrint('GetMyPaymentsError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return paymentList;
  }

  //Card Count
  Future<int> getPTApplicationCount({
    required String token,
    tenantId,
    bool isMyApplication = false,
  }) async {
    int value = 0;
    try {
      int today = DateTime.now().millisecondsSinceEpoch;
      Map<String, dynamic> query = {
        'tenantId': tenantId,
        'fromDate': '${calculateFromDate(today).millisecondsSinceEpoch}',
        'toDate': today.toString(),
        'isRequestForCount': 'true',
      };
      if (isMyApplication) {
        query['status'] = 'ACTIVE';
      }

      final res = await PropertyTaxRepository.getMyProperties(
        token: token,
        query: query,
      );

      if (res.containsKey('count') && res['count'] is int) {
        value = res['count'];
      }
    } catch (e, s) {
      dPrint('getPropertiesCountError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }

    return value;
  }

  Future<BillInfo?> getPtMyBills({
    required String tenantId,
    required String token,
    String? mobileNumber,
    required BusinessService businessService,
  }) async {
    try {
      Map<String, String> query;
      if (mobileNumber != null) {
        query = {
          'tenantId': tenantId,
          'mobileNumber': mobileNumber,
          'businessService': businessService.name,
          'limit': limit.toString(),
          'offset': offset.toString(),
        };
      } else {
        query = {
          'tenantId': tenantId,
          'businessService': businessService.name,
          'limit': limit.toString(),
          'offset': offset.toString(),
        };
      }

      final billRes = await PaymentRepository.fetchBill(
        token: token,
        query: query,
      );
      billInfo = BillInfo.fromJson(billRes);
      length.value = billInfo?.bill?.length ?? 0;
    } catch (e, s) {
      dPrint('GetMyPaymentsError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return billInfo;
  }

  /* -------------------------------------------------------------------------- */
  /*                                For Employee                                */
  /* -------------------------------------------------------------------------- */

  //Check roles for fieldInspection
  bool checkRolesForPtFieldInspection() {
    final roles = Get.find<AuthController>().token?.userRequest?.roles;
    if (roles == null) return false;

    return roles.any(
      (element) => element.code == InspectorType.PT_FIELD_INSPECTOR.name,
    );
  }

  /// Get PT inbox applications for employee
  Future<void> getEmpPtInboxApplications({
    required String? token,
    required String tenantId,
    bool isFilter = false,
    List? locality,
  }) async {
    if (token == null && token!.isEmpty) return;
    try {
      final body = getEmpBodyFilter(
        isFilter: isFilter,
        locality: locality,
        limit: limit,
        offset: offset,
        tenantId: tenantId,
        module: ModulesEmp.PT_SERVICES,
        businessServices: [
          BusinessServicesEmp.PT_CREATE,
          BusinessServicesEmp.PT_MUTATION,
          BusinessServicesEmp.PT_UPDATE,
        ],
      );

      final empRes = await InboxRepository.getInboxApplications(
        token: token,
        body: body,
      );
      empPtModel = EmpPtModel.fromJson(empRes);

      //Check employee permission for field inspection
      final isFieldInspector = checkRolesForPtFieldInspection();

      // Filter wsItems for PENDING_FIELD_INSPECTION status
      final filteredItems = empPtModel.items
          ?.where(
            (item) =>
                (item.processInstance?.state?.state ==
                    InboxStatus.DOC_VERIFIED.name) &&
                isFieldInspector,
          )
          .toList();

      empPtModel.items = filteredItems;

      length.value = empPtModel.items?.length ?? 0;
      streamCtrl.add(empPtModel);
      dPrint('EMP PT: ${empPtModel.items?.length}');
    } catch (e, s) {
      dPrint('GetEmpTlInboxError: $e');
      streamCtrl.add('Emp PT Inbox Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Load more my properties
  Future<void> loadMoreEmp({
    required String token,
    required String tenantId,
  }) async {
    isLoading.value = true;
    limit = limit + 10;
    await getEmpPtInboxApplications(token: token, tenantId: tenantId);
    isLoading.value = false;
  }

  void setDefaultLimitEmp() {
    limit = 10;
    offset = 0;
    length.value = 0;
  }

  Future<void> getMyPropertiesEmp({
    required String token,
    required String propertyId,
    required String tenantId,
  }) async {
    try {
      var query = {
        "propertyIds": propertyId,
        "tenantId": tenantId,
      };

      final res = await PropertyTaxRepository.getMyProperties(
        token: token,
        query: query,
      );

      myProperties = PtMyProperties.fromJson(res);
      property = myProperties!.properties!.first;
      dPrint('getMyPropertiesEmp: $res');
    } catch (e, s) {
      dPrint('getPropertiesEmpError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  // EMP - TL - Action update
  Future<(bool, String?)> empActionUpdate({
    required String token,
    required ModulesEmp module,
  }) async {
    try {
      var body = {
        'Property': property.toJson(),
      };

      final empRes = await InboxRepository.actionUpdate(
        token: token,
        body: body,
        type: module,
      );

      myProperties = PtMyProperties.fromJson(empRes);
      dPrint('------Action Update------');
      dPrint(myProperties!.toJson());
      if (myProperties!.responseInfo!.status == 'successful') {
        return (
          true,
          myProperties?.properties?.firstOrNull?.acknowledgementNumber
        );
      }

      return (false, null);
    } catch (e, s) {
      dPrint('PT_UpdateEmpPTActionError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
      return (false, null);
    }
  }
}
