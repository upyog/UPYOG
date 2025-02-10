import 'package:mobile_app/model/citizen/bill/bill_info.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class MiscChallanRepository {
  static final BaseService _baseService = BaseService();

  static Future<BillInfo> fetchChallans(
      {required String token, Map<String, dynamic>? body, query,}) async {
    late BillInfo billInfo;
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.BILL_GENIE,
      method: RequestType.POST,
      body: body ?? {},
      queryParameters: query,
      requestInfo: RequestInfo(authToken: token, local: local),
    );

    if (response != null) {
      billInfo = BillInfo.fromJson(response);
    }
    return billInfo;
  }
}
