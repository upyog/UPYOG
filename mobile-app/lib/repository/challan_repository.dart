import 'package:mobile_app/model/employee/emp_challans/emp_challans_model.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class ChallansRepository {
  static final BaseService _baseService = BaseService();

  static Future<Challans> createChallans({
    required String token,
    dynamic body,
    query,
  }) async {
    late Challans challans;
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.CHALLANS_CREATE_URL,
      method: RequestType.POST,
      body: body ?? {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      challans = Challans.fromJson(response);
    }
    return challans;
  }

  static Future<dynamic> getChallans({
    required String token,
    query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.CHALLANS_SEARCH_URL,
      method: RequestType.POST,
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      return response;
    }
  }
}
