import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/model/citizen/user_profile/user_profile.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/utils.dart';

class ProfileRepository {
  static final BaseService _baseService = BaseService();

  static Future<UserProfile> getProfile({
    required Map body,
    required String token,
  }) async {
    final local = await getLocal();
    late UserProfile userProfile;
    final response = await _baseService.makeRequest(
      url: UserUrl.USER_PROFILE,
      body: body,
      method: RequestType.POST,
      requestInfo: RequestInfo(authToken: token, local: local),
    );

    if (response != null) {
      userProfile = UserProfile.fromJson(response);
      userProfile.user!.first.setText();
    }
    return userProfile;
  }

  static Future<dynamic> profileUpdate({
    required Map body,
    required String token,
    required query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: UserUrl.USER_PROFILE_UPDATE,
      body: body,
      method: RequestType.POST,
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      return response;
    }
  }

  static Future<dynamic> updatePassword({
    required Map body,
    required String token,
    required query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.EMP_UPDATE_PASSWORD,
      body: body,
      method: RequestType.POST,
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );
    if (response != null) {
      return response;
    }
  }



}
