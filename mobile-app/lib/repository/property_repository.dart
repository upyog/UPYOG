import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class PropertyRepository {
  static final BaseService _baseService = BaseService();

  static Future<dynamic> getProperty({
    required String token,
    query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.PROPERTY,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );
    return response;
  }
}
