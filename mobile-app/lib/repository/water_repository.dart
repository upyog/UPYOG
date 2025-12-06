import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';

class WaterRepository {
  static final BaseService _baseService = BaseService();

  static Future<dynamic> getWater({
    required String token,
    Map<String, dynamic>? body,
    required ModulesEmp type,
    query,
  }) async {
    final local = await getLocal();
    final res = await _baseService.makeRequest(
      url: _getUrlString(type),
      method: RequestType.POST,
      body: body ?? {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    return res;
  }

  static String? _getUrlString(ModulesEmp type) {
    switch (type) {
      case ModulesEmp.WS_SERVICES:
        return Url.WS_WC_SERVICES;
      case ModulesEmp.SW_SERVICES:
        return Url.SW_SWC_SERVICES;
      case ModulesEmp.WS_CONSUMPTION:
        return Url.WATER_CONSUMPTION;
      default:
        return null;
    }
  }

  static Future<dynamic> getWaterEstimation({
    required String token,
    Map<String, dynamic>? body,
    required Modules type,
    query,
  }) async {
    final local = await getLocal();
    final res = await _baseService.makeRequest(
      url: type == Modules.WS ? Url.WS_ESTIMATE : Url.SW_ESTIMATE,
      method: RequestType.POST,
      body: body ?? {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    return res;
  }
}
