import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/localization/localization_label.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class CoreRepository {
  static final BaseService _baseService = BaseService();

  static Future<List<LocalizationLabel>> getLocalization(
    Map<String, dynamic> query, {
    final String? token,
  }) async {
    late List<LocalizationLabel> labelList;
    final local = await getLocal();
    var res = await _baseService.makeRequest(
      url: Url.LOCALIZATION,
      queryParameters: query,
      requestInfo: RequestInfo(authToken: token, local: local),
      method: RequestType.POST,
    );
    if (res != null) {
      labelList = res['messages']
          .map<LocalizationLabel>((e) => LocalizationLabel.fromJson(e))
          .toList();
    }
    return labelList;
  }

  static Future<LanguageList> getMdms({
    Map? body,
    Map<String, dynamic>? query,
  }) async {
    final local = await getLocal();
    late LanguageList languageList;
    final response = await _baseService.makeRequest(
      queryParameters: query,
      url: Url.MDMS,
      body: body,
      method: RequestType.POST,
      requestInfo: RequestInfo(local: local),
    );

    if (response != null) {
      languageList = LanguageList.fromJson(response);
    }
    return languageList;
  }

  //Mdms dynamic data
  static Future<dynamic> getMdmsDynamic({
    Map? body,
    query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      queryParameters: query,
      url: Url.MDMS,
      body: body,
      method: RequestType.POST,
      requestInfo: RequestInfo(local: local),
    );
    return response;
  }

  //Mdms-PGR v2
  static Future<dynamic> getMdmsPgrData({
    Map? body,
    query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      queryParameters: query,
      url: Url.MDMS_PGR,
      body: body,
      method: RequestType.POST,
      requestInfo: RequestInfo(local: local),
    );
    return response;
  }
}
