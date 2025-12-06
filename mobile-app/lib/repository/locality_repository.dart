import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class LocalityRepository {
  static final BaseService _baseService = BaseService();

  static Future<dynamic> getLocality({
    required String token,
    Map<String, dynamic>? body,
    query,
  }) async {
    final local = await getLocal();
    final res = _baseService.makeRequest(
      url: Url.LOCALITY_URL,
      method: RequestType.POST,
      body: body ?? {},
      queryParameters: query,
      requestInfo: RequestInfo(authToken: token, local: local),
    );
    return res;
  }
}
