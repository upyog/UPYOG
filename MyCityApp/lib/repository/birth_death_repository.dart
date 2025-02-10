import 'package:mobile_app/model/citizen/birth_death_model/birth_model.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class BirthDeathRepository{
  static final BaseService _baseService = BaseService();

  static Future<Birth> getBirthApplications({
    required String token,
    query,
  }) async {
    final local = await getLocal();
    late Birth birth;
    final response = await _baseService.makeRequest(
      url: Url.BIRTH_URL,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      birth = Birth.fromJson(response);
    }
    return birth;
  }

  static Future<Birth> getDeathApplications({
    required String token,
    query,
  }) async {
    final local = await getLocal();
    late Birth birth;
    final response = await _baseService.makeRequest(
      url: Url.DEATH_URL,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      birth = Birth.fromJson(response);
    }
    return birth;
  }


}