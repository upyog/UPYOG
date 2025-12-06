import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class PropertyTaxRepository {
  static final BaseService _baseService = BaseService();

  static Future<dynamic> getMyProperties({
    required String token,
    required Map<String, dynamic> query,
    Map<String, dynamic>? body,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.PROPERTY,
      method: RequestType.POST,
      body: body ?? {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    return response;
  }

  static Future<dynamic> getPtApplicationHistory({
    required String token,
    required query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.TIMELINE_HISTORY,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      return response;
    }
  }

  static Future<dynamic> getPtMyPayments({
    required String token,
    required query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.PT_MY_PAYMENTS,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );
    if (response != null) {
      return response;
    }
  }

  static Future<dynamic> getMyPropertiesWithoutQuery({
    required String token,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.PROPERTY,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
    );
    return response;
  }
}
