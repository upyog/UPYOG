import 'dart:async';

import 'package:get/get.dart';
import 'package:mobile_app/model/citizen/grievance/grievance.dart';
import 'package:mobile_app/repository/grievance_repository.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class GrievanceController extends GetxController {
  var streamCtrl = StreamController.broadcast();

  late Grievance grievance;
  // For timeline service
  late ServiceWrapper timelineServiceWrapper;
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
        query: mobileNo != null ? queryPhone : query,
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
      if (grievanceRes != null) {
        grievance = Grievance.fromJson(grievanceRes);
      }
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
}
