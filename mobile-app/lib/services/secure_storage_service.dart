import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:mobile_app/utils/utils.dart';

class SecureStorageConstants {
  static const String LOGIN_KEY = 'login_key';
  static const String EMP_LOGIN_KEY = 'employee_login_key';
  static const String FIRST_TIME_USER = 'isFirstTimeUserKey';
  static const String SKIP_BUTTON = 'skipButtonKey';
  static const String WS_SESSION_APPLICATION_DETAILS =
      'WS_SESSION_APPLICATION_DETAILS';
  static const String MODULES = 'modules';
}

class SecureStorageService {
  static final SecureStorageService _instance =
      SecureStorageService._internal();
  factory SecureStorageService() => _instance;
  SecureStorageService._internal();

  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage(
    aOptions: AndroidOptions(encryptedSharedPreferences: true),
    iOptions: IOSOptions(accessibility: KeychainAccessibility.first_unlock),
  );

  Future<void> _write<T>(String key, T value) async {
    try {
      await _secureStorage.write(key: key, value: value.toString());
    } catch (e) {
      dPrint('Error writing key: $key - $e');
    }
  }

  Future<T?> _read<T>(String key, {T? Function(String?)? parser}) async {
    try {
      final value = await _secureStorage.read(key: key);
      return parser != null ? parser(value) : value as T?;
    } catch (e) {
      dPrint('Error reading key: $key - $e');
      return null;
    }
  }

  Future<void> delete(String key) async {
    try {
      await _secureStorage.delete(key: key);
    } catch (e) {
      dPrint('Error deleting key: $key - $e');
    }
  }

  Future<void> clearAll() async {
    try {
      await _secureStorage.deleteAll();
    } catch (e) {
      dPrint('Error clearing storage - $e');
    }
  }

  Future<void> setString(String key, String value) async => _write(key, value);
  Future<String?> getString(String key) async => _read<String>(key);

  Future<void> setBool(String key, bool value) async => _write(key, value);
  Future<bool?> getBool(String key) async =>
      _read<bool>(key, parser: (value) => value == 'true');

  Future<void> setInt(String key, int value) async => _write(key, value);
  Future<int?> getInt(String key) async =>
      _read<int>(key, parser: (value) => int.tryParse(value ?? ''));

  Future<void> setDouble(String key, double value) async => _write(key, value);
  Future<double?> getDouble(String key) async =>
      _read<double>(key, parser: (value) => double.tryParse(value ?? ''));
}

final storage = SecureStorageService();
