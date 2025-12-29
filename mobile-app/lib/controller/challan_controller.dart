import 'dart:async';

import 'package:get/get.dart';
import 'package:mobile_app/model/common/copy_model.dart';
import 'package:mobile_app/model/employee/emp_challans/emp_challans_model.dart';
import 'package:mobile_app/repository/challan_repository.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class ChallansController extends GetxController {
  final streamControllers = StreamController.broadcast();
  Challans? challans;

  RxList<CopyModel> selectedServiceCategory = <CopyModel>[].obs;

  RxBool isLoading = false.obs;
  RxInt length = 0.obs;
  int limit = 10;
  int offset = 0;

  @override
  void dispose() {
    streamControllers.close();
    super.dispose();
  }

  List<String>? _cachedBusinessServices;

  List<String?> get uniqueBusinessServices {
    if (_cachedBusinessServices != null) return _cachedBusinessServices!;

    _cachedBusinessServices = challans?.challans
            ?.expand((challan) {
              return challan.businessService != null
                  ? [challan.businessService!]
                  : [];
            })
            .toSet()
            .toList()
            .cast<String>() ??
        [];

    return _cachedBusinessServices!;
  }

  Future<void> loadMoreChallans({
    required String token,
    required String tenantId,
    String? receiptNumber,
    String? mobileNumber,
    String? businessServices,
    String? status,
  }) async {
    isLoading.value = true;
    if (length.value >= limit) {
      offset += limit;
      limit = 10;
      await getChallans(
        token: token,
        tenantId: tenantId,
        receiptNumber: receiptNumber,
        mobileNumber: mobileNumber,
        businessServices: businessServices,
        status: status,
      );
    }
    isLoading.value = false;
  }

  Future<void> getChallans({
    required String tenantId,
    required String token,
    String? challanNo,
    String? receiptNumber,
    String? mobileNumber,
    String? businessServices,
    String? status,
  }) async {
    try {
      final query = {
        'tenantId': tenantId,
        'limit': limit.toString(),
        'offset': offset.toString(),
        'sortOrder': 'ASC',
        if (challanNo != null) 'challanNo': challanNo,
        if (receiptNumber != null) 'receiptNumber': receiptNumber,
        if (mobileNumber != null) 'mobileNumber': mobileNumber,
        if (businessServices != null) 'businessService': businessServices,
        if (status != null) 'status': status,
      };

      final response = await ChallansRepository.getChallans(
        query: query,
        token: token,
      );

      challans = Challans.fromJson(response);
      length.value = challans?.challans?.length ?? 0;
      streamControllers.add(challans?.challans);
    } catch (e, s) {
      streamControllers.add('error');
      dPrint('getChallans Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  Future<String?> createChallan({
    required String tenantId,
    required String token,
    required Map<String, dynamic> body,
  }) async {
    isLoading.value = true;
    try {
      final query = {
        'tenantId': tenantId,
      };

      final bodyEncode = {
        'Challan': body,
      };

      final response = await ChallansRepository.createChallans(
        query: query,
        token: token,
        body: bodyEncode,
      );

      challans = response;
      isLoading.value = false;
      return challans?.challans?.firstOrNull?.challanNo;
    } catch (e, s) {
      isLoading.value = false;
      dPrint('createChallans Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return null;
  }
}
