import 'package:mobile_app/model/citizen/trade_license/trade_license.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class TradeLicenseRepository {
  static final BaseService _baseService = BaseService();

  static Future<TradeLicense> getTlApplications({
    required String token,
    query,
  }) async {
    final local = await getLocal();
    late TradeLicense tradeLicense;
    final response = await _baseService.makeRequest(
      url: Url.TL_SERVICES,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      tradeLicense = TradeLicense.fromJson(response);
    }
    return tradeLicense;
  }
}
