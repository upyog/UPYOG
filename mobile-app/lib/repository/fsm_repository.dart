import 'package:mobile_app/model/citizen/fsm/fsm.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class FsmRepository {
  static final BaseService _baseService = BaseService();

  static Future<FsmModel> getFsmApplications({
    required String token,
    query,
  }) async {
    final local = await getLocal();
    late FsmModel fsmModel;
    final response = await _baseService.makeRequest(
      url: Url.FSM,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      fsmModel = FsmModel.fromJson(response);
    }
    return fsmModel;
  }
}
