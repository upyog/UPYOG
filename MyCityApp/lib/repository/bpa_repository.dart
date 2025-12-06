import 'package:mobile_app/model/citizen/bpa_model/bpa_model.dart';
import 'package:mobile_app/model/citizen/bpa_model/comparison/comparison_model.dart';
import 'package:mobile_app/model/citizen/bpa_model/noc/noc_model.dart';
import 'package:mobile_app/model/citizen/bpa_model/scrutiny_model.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class BpaRepository {
  static final BaseService _baseService = BaseService();

  static Future<Bpa> getBpaApplications({
    required String token,
    query,
  }) async {
    final local = await getLocal();
    late Bpa bpa;
    final response = await _baseService.makeRequest(
      url: Url.BPA_URL,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );
    if (response != null) {
      bpa = Bpa.fromJson(response);
    }
    return bpa;
  }

  static Future<Scrutiny> getEdcrApplications({
    required String token,
    query,
  }) async {
    final local = await getLocal();
    late Scrutiny scrutiny;
    final response = await _baseService.makeRequest(
      url: Url.EDCR_URL,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
      isBPA: true,
    );
    if (response != null) {
      scrutiny = Scrutiny.fromJson(response);
    }
    return scrutiny;
  }

  static Future<Noc> getBpaNocDetail({
    required String token,
    query,
  }) async {
    late Noc noc;
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.BPA_NOC_DETAIL,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );
    if (response != null) {
      noc = Noc.fromJson(response);
    }
    return noc;
  }

  static Future<Comparison?> getComparison({
    required String token,
    query,
  }) async {
    final local = await getLocal();
    late Comparison comparison;
    final response = await _baseService.makeRequest(
      url: Url.COMPARISON,
      method: RequestType.POST,
      body: {},
      requestInfo:
          RequestInfo(authToken: token, isComparison: true, local: local),
      queryParameters: query,
    );
    if (response != null) {
      comparison = Comparison.fromJson(response);
    }
    return comparison;
  }
}
