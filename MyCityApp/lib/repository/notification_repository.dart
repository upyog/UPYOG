import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class NotificationRepository {
  static final BaseService _baseService = BaseService();

  static Future<dynamic> getNotificationApplications({
    required String token,
    required NotificationType type,
    query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: _getUrlNotificationString(type),
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    return response;
  }

  static String? _getUrlNotificationString(NotificationType type) {
    switch (type) {
      case NotificationType.SEARCH:
        return Url.NOTIFICATION_SEARCH;
      case NotificationType.UPDATE:
        return Url.NOTIFICATION_UPDATE;
      case NotificationType.COUNT:
        return Url.NOTIFICATION_COUNT;
    }
  }
}
