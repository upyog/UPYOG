import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class InboxRepository {
  static final BaseService _baseService = BaseService();

  static Future<dynamic> getInboxApplications({
    required String token,
    Map body = const {},
    query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.INBOX_URL,
      method: RequestType.POST,
      body: body,
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    return response;
  }

  static Future<dynamic> actionUpdate({
    required String token,
    required Map body,
    query,
    required ModulesEmp type,
    bool isEmpFireNoc = false,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: _getUrlString(type),
      method: RequestType.POST,
      body: body,
      requestInfo: RequestInfo(
          authToken: token, isEmpFireNoc: isEmpFireNoc, local: local),
      queryParameters: query,
    );
    return response;
  }

  static String? _getUrlString(ModulesEmp type) {
    switch (type) {
      case ModulesEmp.WS_SERVICES:
        return Url.EMP_WS_ACTION_UPDATE;
      case ModulesEmp.SW_SERVICES:
        return Url.EMP_SW_ACTION_UPDATE;
      case ModulesEmp.TL_SERVICES:
        return Url.EMP_TL_ACTION_UPDATE;
      case ModulesEmp.PT_SERVICES:
        return Url.EMP_PT_UPDATE;
      case ModulesEmp.BPA_SERVICES:
        return Url.BPA_ACTION_UPDATE;
      case ModulesEmp.FIRE_NOC:
        return Url.NOC_ACTION_UPDATE;
      case ModulesEmp.PGR_SERVICES:
        return Url.PGR_ACTION_UPDATE;
      default:
        return null;
    }
  }
}
