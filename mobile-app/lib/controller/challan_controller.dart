import 'package:get/get.dart';
import 'package:mobile_app/model/employee/emp_challans/emp_challans_model.dart';
import 'package:mobile_app/repository/challan_repository.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class ChallanController extends GetxController {
  Challans? challans;

  RxBool isLoading = false.obs;

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

      final response = await ChallanRepository.createChallan(
        query: query,
        token: token,
        body: bodyEncode,
      );

      challans = response;
      isLoading.value = false;
      return challans?.challans?.firstOrNull?.challanNo;
    } catch (e, s) {
      isLoading.value = false;
      dPrint('createChallan Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return null;
  }
}
