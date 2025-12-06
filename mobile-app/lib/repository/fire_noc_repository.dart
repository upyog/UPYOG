import 'package:mobile_app/model/citizen/fire_noc/fire_noc.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class FireNocRepository {
  static final BaseService _baseService = BaseService();

  static Future<FireNocModel> getFireNocApplications({
    required String token,
    query,
  }) async {
    final local = await getLocal();
    late FireNocModel fireNocModel;
    final response = await _baseService.makeRequest(
      url: Url.FIRE_NOC,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      fireNocModel = FireNocModel.fromJson(response);
    }
    return fireNocModel;
  }

  static Future<dynamic> getFireNocPayment({
    required String token,
    required Map<String, dynamic> query,
    String? businessService,
  }) async {
    final local = await getLocal();
    final url = isNotNullOrEmpty(businessService)
        ? '${Url.COLLECTION}$businessService/_search'
        : '${Url.COLLECTION}_search';
    final response = await _baseService.makeRequest(
      url: url,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );
    if (response != null) {
      return response;
    }
  }

  static Future<dynamic> getFireNocLocality({
    required String token,
    required Map<String, dynamic> query,
    Map<String, dynamic>? body,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.EMP_FIRE_NOC_LOCALITY,
      method: RequestType.POST,
      body: body ?? {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );
    if (response != null) {
      return response;
    }
  }
}
