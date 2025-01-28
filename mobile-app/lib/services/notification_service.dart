import 'dart:io';

import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/services/firebase_service.dart';
import 'package:mobile_app/utils/utils.dart';

class NotificationService {
  static final _firebaseMessaging = FirebaseMessaging.instance;
  static final FlutterLocalNotificationsPlugin
      _flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();

  static bool _isRequestingPermission = false;

  /// Request notification permission
  static Future<void> init() async {
    if (_isRequestingPermission) return;

    _isRequestingPermission = true;
    try {
      await _firebaseMessaging.requestPermission(
        alert: true,
        announcement: true,
        badge: true,
        carPlay: false,
        criticalAlert: true,
        provisional: false,
        sound: true,
      );
    } finally {
      _isRequestingPermission = false;
    }
  }

  /// Get device token
  static Future getDeviceToken() async {
    try {
      final token = await _firebaseMessaging.getToken();
      dPrint('FCM Token: $token');

      saveTokenToFirestore(token: token!);
      return token;
    } catch (e) {
      dPrint('Failed to get device token');
      return null;
    }
  }

  /// Save device token to firestore
  static saveTokenToFirestore({required String token}) {
    final authCtrl = Get.find<AuthController>();
    dPrint('Is valid user: ${authCtrl.isValidUser}');
    if (authCtrl.isValidUser) {
      FirebaseService.saveFcmToken(token);
      dPrint('Save to the firestore!!');
    }

    // If token change save it
    _firebaseMessaging.onTokenRefresh.listen((event) {
      if (authCtrl.isValidUser) {
        dPrint('Token refreshed');
        saveTokenToFirestore(token: token);
      }
    });
  }

  /// Initialize local notification
  static Future localNotificationInit() async {
    const AndroidInitializationSettings initializationSettingsAndroid =
        AndroidInitializationSettings('@mipmap/ic_launcher');

    final DarwinInitializationSettings initializationSettingsDarwin =
        DarwinInitializationSettings(
      onDidReceiveLocalNotification: (id, title, body, payload) {},
    );

    // Platform specific initialization
    final InitializationSettings initializationSettings =
        InitializationSettings(
      android: initializationSettingsAndroid,
      iOS: initializationSettingsDarwin,
    );

    // Request notification permission for Android 13 or above and ios
    if (Platform.isAndroid) {
      final androidPlugin = _flutterLocalNotificationsPlugin
          .resolvePlatformSpecificImplementation<
              AndroidFlutterLocalNotificationsPlugin>();
      if (androidPlugin != null) {
        await androidPlugin.requestNotificationsPermission();
      }
    } else if (Platform.isIOS) {
      final iosPlugin = _flutterLocalNotificationsPlugin
          .resolvePlatformSpecificImplementation<
              IOSFlutterLocalNotificationsPlugin>();
      if (iosPlugin != null) {
        await iosPlugin.requestPermissions(
          alert: true,
          badge: true,
          sound: true,
        );
      }
    }

    _flutterLocalNotificationsPlugin.initialize(
      initializationSettings,
      onDidReceiveNotificationResponse: onNotificationTap,
      onDidReceiveBackgroundNotificationResponse: onNotificationTap,
    );
  }

  /// Handle foreground notification tap
  static void onNotificationTap(
    NotificationResponse notificationResponse,
  ) async {
    dPrint('OnNotificationTap received');
  }

  /// Show a simple notification
  static Future<void> showNotification({
    required String title,
    required String body,
    required String payload,
  }) async {
    const AndroidNotificationDetails androidNotificationDetails =
        AndroidNotificationDetails(
      'channel_id',
      'channel_name',
      channelDescription: 'channel_description',
      importance: Importance.max,
      priority: Priority.high,
      ticker: 'ticker',
    );

    const DarwinNotificationDetails iosNotificationDetails =
        DarwinNotificationDetails(
      presentAlert: true,
      presentBadge: true,
      presentSound: true,
    );

    const NotificationDetails notificationDetails = NotificationDetails(
      android: androidNotificationDetails,
      iOS: iosNotificationDetails,
    );

    await _flutterLocalNotificationsPlugin.show(
      0,
      title,
      body,
      notificationDetails,
      payload: payload,
    );
  }
}
