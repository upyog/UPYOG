import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class FileRepository {
  static final BaseService _baseService = BaseService();

  static Future<dynamic> getFiles({
    required Map body,
    required String token,
    required query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.FILES_URL,
      body: body,
      method: RequestType.GET,
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      return response;
    }
  }

  static Future<dynamic> getPdfService({
    required body,
    required query,
    required String token,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.PDF_SERVICE,
      body: body,
      method: RequestType.POST,
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      return response;
    }
  }

  //Download Challan PDF - EMP - UC
  static Future<dynamic> getChallanPdfService({
    required query,
    required String token,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.UC_CHALLAN_DOWNLOAD_URL,
      body: {},
      method: RequestType.POST,
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      return response;
    }
  }
}
