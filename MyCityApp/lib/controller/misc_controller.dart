import 'dart:async';

import 'package:get/get.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart';
import 'package:mobile_app/repository/misc_challan_repository.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class MiscController extends GetxController {
  var streamCtrl = StreamController.broadcast();

  late BillInfo billInfo;

  RxInt totalChallans = 0.obs;
  RxBool isLoading = false.obs;
  int limit = 10, offset = 0;

  @override
  void onClose() {
    super.onClose();
    streamCtrl.close();
  }

  Future<void> loadMore({
    required String token,
    required String tenantId,
    String? mobileNo,
  }) async {
    try {
      isLoading.value = true;
      offset += limit;
      await getChallans(token: token, tenantId: tenantId, mobileNo: mobileNo);
    } finally {
      isLoading.value = false;
    }
  }

  void setDefaultLimit() {
    limit = 10;
    offset = 0;
  }

  Future<void> getChallans({
    required String token,
    required String tenantId,
    String? mobileNo,
  }) async {
    try {
      final body = {
        "searchCriteria": {
          "tenantId": tenantId,
          "mobileNumber": mobileNo,
        },
      };
      final billRes = await MiscChallanRepository.fetchChallans(
        token: token,
        body: body,
        query: {
          "limit": limit.toString(),
          "offset": offset.toString(),
        },
      );
      billInfo = billRes;
      streamCtrl.add(billInfo);
      totalChallans.value = billInfo.bills?.length ?? 0;
    } catch (e, s) {
      streamCtrl.add('getChallans Error');
      dPrint('getChallans Error: ${e.toString()}');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }
}
