import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/model/employee/employee_model/employees_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class EmployeeRepository {
  static final BaseService _baseService = BaseService();

  //Get all employees based on roles
  static Future<EmployeeModel> getEmployees({
    required String token,
    Map? body,
    query,
  }) async {
    final local = await getLocal();
    late EmployeeModel empModel;
    final response = await _baseService.makeRequest(
      url: Url.EGOV_HRMS,
      method: RequestType.POST,
      body: body ?? {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      empModel = EmployeeModel.fromJson(response);
    }
    return empModel;
  }
}
