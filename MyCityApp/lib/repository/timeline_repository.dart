import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class TimelineRepository {
  static final BaseService _baseService = BaseService();

  static Future<dynamic> timelineHistory({
    required String token,
    required query,
    Map<String, dynamic>? body,
    bool isEmpFireNoc = false,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.TIMELINE_HISTORY,
      method: RequestType.POST,
      body: body ?? {},
      requestInfo: RequestInfo(
        authToken: token,
        isEmpFireNoc: isEmpFireNoc,
        local: local,
      ),
      queryParameters: query,
    );

    if (response != null) {
      return response;
    }
  }

  static Future<dynamic> getWorkFlowBusinessServices({
    required String token,
    required query,
    Map<String, dynamic>? body,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.WORKFLOW_BUSINESS_SERVICES,
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
