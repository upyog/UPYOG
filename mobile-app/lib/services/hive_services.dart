// ignore_for_file: constant_identifier_names

import 'package:hive/hive.dart';

late Box box;

class HiveConstants {
  //Main Box Name
  static const String appBox = 'appBox';

  // Key name
  static const String LOGIN_KEY = 'login_key';
  static const String EMP_LOGIN_KEY = 'employee_login_key';

  //Check first time
  static const String FIRST_TIME_USER = 'isFirstTimeUserKey';

  //Check skip button
  static const String SKIP_BUTTON = 'skipButtonKey';

  /// WS/SW employee edit application data 
  static const String WS_SESSION_APPLICATION_DETAILS = 'WS_SESSION_APPLICATION_DETAILS';

  // All modules
  static const String MODULES = 'modules';
}

// Local storage service
sealed class HiveService {
  static Future<void> setData(
    String key,
    dynamic data, {
    String boxName = HiveConstants.appBox,
  }) async {
    box = Hive.box(boxName);
    await box.put(key, data);
  }

  static Future<dynamic> getData(
    String key, {
    String boxName = HiveConstants.appBox,
  }) async {
    box = Hive.box(boxName);
    return box.get(key);
  }

  static Future<void> deleteData(
    String key, {
    String boxName = HiveConstants.appBox,
  }) async {
    box = Hive.box(boxName);
    await box.delete(key);
  }

  static Future<void> clearBox({String boxName = HiveConstants.appBox}) async {
    box = Hive.box(boxName);
    await box.clear();
  }
}
