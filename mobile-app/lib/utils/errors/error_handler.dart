import 'package:dio/dio.dart';
import 'package:get/get.dart';
import 'package:mobile_app/model/custom_exception_model.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class ErrorHandler {
  static void logError(String error, [StackTrace? stackTrace]) {
    dPrint(error, stackTrace: stackTrace);
  }

  static Future<bool> handleApiException(
    CustomException e, [
    StackTrace? stackTrace,
  ]) async {
    switch (e.exceptionType) {
      case ExceptionType.UNAUTHORIZED:
        await clearData();
        Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
        // Get.offAllNamed(AppRoutes.SELECT_CATEGORY);
        // final userType = await getUserType();
        // if (userType == UserType.CITIZEN.name) {
        //   await clearData();
        //   Get.offAllNamed(AppRoutes.LOGIN);
        // } else {
        //   await clearData();
        //   Get.offAllNamed(AppRoutes.EMP_LOGIN);
        // }
        return true;
      case ExceptionType.INVALIDINPUT:
      case ExceptionType.OTHER:
      case ExceptionType.FETCHDATA:
      case ExceptionType.BADREQUEST:
        logError(e.message, stackTrace);
        return true;
      case ExceptionType.CONNECTIONISSUE:
        // snackBar('ERROR', e.message, Colors.red);
        logError(e.message, stackTrace);
        return false;
      case ExceptionType.RUNTIMEERROR:
        return true;
      default:
        return true;
    }
  }

  static Future<bool> allExceptionsHandler(
    dynamic e, [
    StackTrace? stackTrace,
  ]) async {
    var status = false;
    if (e is CustomException) {
      if (await ErrorHandler.handleApiException(e, stackTrace)) {
        showErrorDialog(e.message, e.requestUrl);
        //snackBar('ERROR', e.message, Colors.red);
      }
      status = true;
    } else if (e is DioException) {
      status = true;
      ErrorHandler.logError(e.toString(), stackTrace);
      showErrorDialog(
        e.message,
        e.response?.requestOptions.uri.toString() ?? '',
      );
    } else if (e is String || e is int?) {
      status = true;
      ErrorHandler.logError(e.toString(), stackTrace);
      showErrorDialog(e.toString(), "");
    } else if (e is Map<String, dynamic>) {
      status = true;
      ErrorHandler.logError(e.toString(), stackTrace);
      showErrorDialog(e.toString(), "");
    } else {
      status = true;
      ErrorHandler.logError(e.toString(), stackTrace);
      showErrorDialog(e.toString(), "");
      //snackBar('ERROR', e.toString(), Colors.red);
    }
    return status;
  }
}
