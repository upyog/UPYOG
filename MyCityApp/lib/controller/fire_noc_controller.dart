import 'dart:async';
import 'dart:convert';

import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart';
import 'package:mobile_app/model/citizen/fire_noc/fire_noc.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/model/citizen/timeline/timeline.dart';
import 'package:mobile_app/model/employee/fire_noc_locality/fire_noc_locality.dart'
    as nocLocality;
import 'package:mobile_app/repository/fire_noc_repository.dart';
import 'package:mobile_app/repository/inbox_repository.dart';
import 'package:mobile_app/repository/payment_repository.dart';
import 'package:mobile_app/repository/timeline_repository.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:rxdart/rxdart.dart' as rx;

class FireNocController extends GetxController {
  var streamCtrl = StreamController.broadcast();
  final fireNocFeeStream = rx.BehaviorSubject();
  final fireNocBillStream = rx.BehaviorSubject();
  final fireNocStream = rx.BehaviorSubject();
  late FireNocModel fireNocModel;
  FireNoc? fireNoc;
  late PaymentModel paymentModel;
  late BillInfo billInfo;

  RxInt length = 0.obs;
  RxBool isLoading = false.obs;
  int limit = 10, offset = 0;

  //Employee Fire noc
  Rx<Timeline>? timeline = Timeline().obs;
  Rx<nocLocality.Locality> localityNoc = nocLocality.Locality().obs;

  @override
  void onClose() {
    super.onClose();
    streamCtrl.close();
    fireNocFeeStream.close();
    fireNocBillStream.close();
    fireNocStream.close();
  }

  Future<void> loadMore({
    required String token,
  }) async {
    try {
      isLoading.value = true;
      offset += limit;
      await getFireNocApplications(token: token);
    } finally {
      isLoading.value = false;
    }
  }

  void setDefaultLimit() {
    length.value = 0;
    limit = 10;
    offset = 0;
  }

  Future<void> getFireNocApplications({
    required String? token,
  }) async {
    if (token == null && token!.isEmpty) return;
    try {
      final fireNocResponse = await FireNocRepository.getFireNocApplications(
        token: token,
        query: {
          "tenantId": BaseConfig.STATE_TENANT_ID,
          "limit": limit.toString(),
          "offset": offset.toString(),
        },
      );
      fireNocModel = fireNocResponse;
      streamCtrl.add(fireNocModel);
      length.value = fireNocModel.fireNoCs?.length ?? 0;
      dPrint("Fire Noc --------------------------------");
      dPrint(fireNocModel.toString());
    } catch (e, s) {
      dPrint('GetFireNocApplicationsError: $e');
      streamCtrl.add('FireNoc Application Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  Future<void> getFireNocApplicationById({
    required String token,
    required String applicationNo,
    required String tenantId,
  }) async {
    try {
      final fireNocResponse = await FireNocRepository.getFireNocApplications(
        token: token,
        query: {
          "tenantId": tenantId,
          "applicationNumber": applicationNo,
        },
      );
      fireNoc = fireNocResponse.fireNoCs!.first;
      fireNocStream.sink.add(fireNoc);
      dPrint("Fire Noc --------------------------------");
      dPrint(fireNoc.toString());
    } catch (e, s) {
      dPrint('GetFireNocApplicationByIdError: $e');
      fireNocStream.add('FireNoc Application Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  Future<void> getFireNocFeeDetailPayment({
    required String token,
    required String consumerCodes,
    required String tenantId,
    required String businessService,
  }) async {
    try {
      final fireNocFeeResponse = await FireNocRepository.getFireNocPayment(
        token: token,
        businessService: businessService,
        query: {
          'tenantId': tenantId,
          'consumerCodes': consumerCodes,
        },
      );
      paymentModel = PaymentModel.fromJson(fireNocFeeResponse);
      fireNocFeeStream.sink.add(paymentModel);
    } catch (e, s) {
      dPrint('FIRE_NOC PAYMENT ERROR: $e');
      fireNocFeeStream.add('FIRE_NOC PAYMENT ERROR');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  Future<void> getFireNocFeeDetailBill({
    required String token,
    required String consumerCodes,
    required String tenantId,
    required BusinessService businessService,
  }) async {
    try {
      final fireNocBillFeeResponse = await PaymentRepository.fetchBill(
        token: token,
        query: {
          'tenantId': tenantId,
          'consumerCode': consumerCodes,
          'businessService': businessService.name,
        },
      );
      billInfo = BillInfo.fromJson(fireNocBillFeeResponse);
      fireNocBillStream.sink.add(billInfo);
    } catch (e, s) {
      dPrint('FIRE_NOC BILL ERROR: $e');
      fireNocBillStream.add('FIRE_NOC BILL ERROR');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /* -------------------------------------------------------------------------- */
  /*                                EMP Fire NOC                                */
  /* -------------------------------------------------------------------------- */

  Future<void> getFireNocApplicationsEmp({
    required String token,
    required String tenantId,
    required String businessService,
  }) async {
    try {
      final query = {
        "tenantId": tenantId,
        "businessServices": businessService,
      };
      final res = await TimelineRepository.timelineHistory(
        token: token,
        query: query,
        isEmpFireNoc: true,
      );

      timeline?.value = Timeline.fromJson(res);
      streamCtrl.add(timeline?.value);
      length.value = timeline?.value.processInstancesList?.length ?? 0;
      dPrint("Fire Noc --------------------------------");
      dPrint(timeline!.value.toString());
    } catch (e, s) {
      dPrint('GetFireNocApplicationsError: $e');
      streamCtrl.add('FireNoc Application Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  // Future<void> getFireNocLocality({
  //   required String token,
  //   required List<String?>? applicationNos,
  // }) async {
  //   if (!isNotNullOrEmpty(applicationNos)) return [];
  //   try {
  //     final body = {
  //       "searchCriteria": {
  //         'referenceNumber': applicationNos,
  //       },
  //     };

  //     final response = await FireNocRepository.getFireNocLocality(
  //       token: token,
  //       body: body,
  //       query: {
  //         "tenantId": BaseConfig.STATE_TENANT_ID,
  //       },
  //     );

  //     final locality = nocLocality.LocalityNoc.fromJson(response);

  //   } catch (e, s) {
  //     dPrint('GetFireNocLocalityError: $e');
  //     ErrorHandler.allExceptionsHandler(e, s);
  //   }
  // }

  // EMP - TL - Action update
  Future<(bool, String?)> empActionUpdate({
    required String token,
    required ModulesEmp module,
  }) async {
    try {
      final nocEncode = jsonEncode(fireNoc?.toJson());
      final body = {
        'FireNOCs': [
          jsonDecode(nocEncode),
        ],
      };

      final empRes = await InboxRepository.actionUpdate(
        token: token,
        body: body,
        type: module,
        isEmpFireNoc: true,
      );

      fireNocModel = FireNocModel.fromJson(empRes);
      dPrint('------Action Update------');
      dPrint('FireNocModel: ${fireNocModel.toJson()}');
      if (fireNocModel.responseInfo!.status == 'successful') {
        return (
          true,
          fireNocModel.fireNoCs?.firstOrNull?.fireNocDetails?.applicationNumber
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
