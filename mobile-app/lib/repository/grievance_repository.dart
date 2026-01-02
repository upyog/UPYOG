import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class GrievanceRepository {
  static final BaseService _baseService = BaseService();

  static Future<dynamic> getGrievances({
    required String token,
    required query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.GRIEVANCES,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      return response;
    }
  }

  static Future<dynamic> submitGrievanceForm({
    required String token,
    required query,
    required body,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.GRIEVANCES_FORM,
      method: RequestType.POST,
      body: body,
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      return response;
    }
  }
}
