import 'package:get/get.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/notification/notification_model.dart';
import 'package:mobile_app/repository/notification_repository.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class NotificationController extends GetxController {
  RxBool isBadgeShow = false.obs;
  RxInt count = 0.obs;
  final RxList<NotificationModel> _notificationList =
      RxList<NotificationModel>([]);

  int limit = 10, offset = 0;
  RxInt length = 0.obs;
  RxInt totalCount = 0.obs;
  RxBool isLoading = false.obs;
  late NotificationList notificationListRes;

  void addNotification(NotificationModel model) {
    _notificationList.add(model);
    isBadgeShow.value = true;
    count.value = _notificationList.length;
  }

  void clearNotification() {
    _notificationList.clear();
    isBadgeShow.value = false;
    count.value = 0;
  }

  Future<List<NotificationModel>> getNotifications() async {
    return _notificationList;
  }

  /// Load more Notification
  Future<void> loadMoreNotification({
    required String token,
  }) async {
    try {
      isLoading.value = true;
      offset += limit;
      await getNotificationApplication(token: token);
    } finally {
      isLoading.value = false;
    }
  }

  void setDefaultLimit() {
    limit = 10;
    offset = 0;
  }

  //Notification Search Api Call
  Future<NotificationList?> getNotificationApplication({
    required String token,
  }) async {
    isLoading.value = true;
    NotificationList? notificationList;
    try {
      Map<String, dynamic> query = {};
      final TenantTenant tenantId = await getCityTenant();
      query = {
        'tenantId': tenantId.code,
        'limit': limit.toString(),
        'offset': offset.toString(),
      };

      final res = await NotificationRepository.getNotificationApplications(
        token: token,
        query: query,
        type: NotificationType.SEARCH,
      );
      dPrint('API Notification Response: $res');

      notificationList = NotificationList.fromJson(res);
      length.value = notificationList.events?.length ?? 0;

      isLoading.value = false;
    } catch (e, s) {
      dPrint('getNotificationError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    } finally {
      isLoading.value = false;
    }
    return notificationList;
  }

  //Notification Update Api Call
  Future<void> getNotificationUpdate({
    required String token,
  }) async {
    isLoading.value = true;
    try {
      Map<String, dynamic> query = {};
      final TenantTenant tenantId = await getCityTenant();
      query = {
        'tenantId': tenantId.code,
      };

      final res = await NotificationRepository.getNotificationApplications(
        token: token,
        query: query,
        type: NotificationType.UPDATE,
      );
      dPrint('API Notification Updated: $res');
      isLoading.value = false;
    } catch (e, s) {
      dPrint('updateNotificationError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    } finally {
      isLoading.value = false;
    }
  }

  //Notification Count Api Call
  Future<void> getNotificationCount({
    required String token,
  }) async {
    isLoading.value = true;
    try {
      Map<String, dynamic> query = {};
      final TenantTenant tenantId = await getCityTenant();
      query = {
        'tenantId': tenantId.code,
      };

      final res = await NotificationRepository.getNotificationApplications(
        token: token,
        query: query,
        type: NotificationType.COUNT,
      );

      if (res['ResponseInfo']['status'] == "successful") {
        totalCount.value = res['unreadCount'];
        dPrint('Notification Count Res: ${totalCount.value}');
      } else {
        dPrint('Notification Count Res: $res');
      }
      isLoading.value = false;
    } catch (e, s) {
      dPrint('countNotificationError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    } finally {
      isLoading.value = false;
    }
  }
}

class NotificationModel {
  final String? title;
  final String? text;

  NotificationModel({required this.title, required this.text});
}
