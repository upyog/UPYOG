import 'package:mobile_app/utils/enums/app_enums.dart';

class CustomException implements Exception {
  final String message;
  final int statusCode;
  final ExceptionType exceptionType;
  final String code;
  final String? requestUrl;
  CustomException(this.message, this.statusCode, this.exceptionType, this.code,
      this.requestUrl,);

  @override
  String toString() {
    return 'CustomException(message: $message, statusCode: $statusCode, exceptionType: $exceptionType, code: $code)';
  }
}
