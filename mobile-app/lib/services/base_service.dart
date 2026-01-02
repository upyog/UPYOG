import 'dart:convert';
import 'dart:developer';
import 'dart:io';

import 'package:http/http.dart' as http;
import 'package:mobile_app/env/app_config.dart';
import 'package:mobile_app/model/custom_exception_model.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/utils/constants/constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class BaseService {
  Future<dynamic> makeRequest({
    required String? url,
    String? baseUrl,
    dynamic body,
    String? contentType,
    Map<String, dynamic>? queryParameters,
    Map<String, String>? headers,
    RequestType method = RequestType.GET,
    RequestInfo? requestInfo,
    bool isBPA = false,
  }) async {
    Uri uri;

    // For payment testing
    // final apiBaseUrl1 = isTestPayment ? testApiBaseUrl : apiBaseUrl;

    if (queryParameters == null) {
      uri = Uri.parse('${baseUrl ?? apiBaseUrl}$url');
    } else {
      String queryString = Uri(queryParameters: queryParameters).query;
      uri = Uri.parse(
        '${baseUrl ?? apiBaseUrl}$url?$queryString',
      );
    }

    if (requestInfo != null) {
      if (isBPA) {
        body = {"RequestInfo": requestInfo.toJsonWithoutPAR(), ...body};
      } else {
        if (body != null) {
          body = {"RequestInfo": requestInfo.toJson(), ...body};
        } else {
          body = requestInfo.toJson();
        }
      }
    }

    if (headers == null ||
        headers[HttpHeaders.contentTypeHeader] == 'application/json') {
      body = jsonEncode(body);
    }

    var header = headers ??
        {
          HttpHeaders.contentTypeHeader: 'application/json',
        };

    http.Response response;
    try {
      switch (method) {
        case RequestType.GET:
          response = await http.get(uri);
          break;
        case RequestType.PUT:
          response = await http.put(uri, body: json.encode(body));
          break;
        case RequestType.POST:
          response = await http.post(uri, headers: header, body: body);
          break;
        case RequestType.DELETE:
          response = await http.delete(uri, body: json.encode(body));
      }
      log("Body Request: $body");
      dPrint('''
            API URL INFO:
            URL: $uri\n
            Method: $method\n
            Header: $header\n
            Body Request: $body\n\n
      ''');
      log("Response Request: ${_response(response)}");
      return _response(response);
    } on CustomException catch (e) {
      dPrint(e);
      rethrow;
    } catch (e) {
      dPrint(e);
      throw CustomException(
        e.toString(),
        502,
        ExceptionType.CONNECTIONISSUE,
        "",
        "",
      );
    }
  }

  dynamic _response(http.Response response) {
    dynamic data;
    final requestUrl = response.request?.url.toString() ?? 'Unknown URL';

    // Check for HTML response
    final contentType = response.headers['content-type'];
    if (contentType != null && contentType.contains('text/html')) {
      final htmlContent = utf8.decode(response.bodyBytes);
      dPrint('Received HTML response: $htmlContent');
      throw CustomException(
        'Received HTML response instead of JSON.',
        response.statusCode,
        ExceptionType.OTHER,
        '',
        requestUrl,
      );
    }

    try {
      data = json.decode(utf8.decode(response.bodyBytes));
    } catch (e) {
      data = utf8.decode(response.bodyBytes);
      return data;
    }

    var errorCode = '';
    var errorMessage;

    if (data?['error'] is String) {
      errorMessage = data?['error'] ?? '';
    } else {
      var errors = data?['Errors'] ?? [];

      if (errors.isNotEmpty) {
        var firstError = errors[0];
        errorCode = firstError['code'] ?? '';
        errorMessage = firstError['message'] ?? firstError['description'] ?? '';
      } else {
        errorCode = data?['error']?['fields']?[0]?['code'] ?? '';
        errorMessage = data?['error_description'] ??
            data?['error']?['fields']?[0]?['message'] ??
            '';
      }
    }

    var errorList = data?['Errors'] ?? [];
    dPrint('Request URL: $requestUrl');
    dPrint('MakeRequest Response Error Code: $errorCode');
    dPrint('MakeRequest Response Error Message: $errorMessage');
    dPrint('MakeRequest Response Error List: $errorList');

    switch (response.statusCode) {
      case 200:
        return data;
      case 201:
        return data;
      case 202:
        return data;
      case 400:
        throw getException(
          errorCode,
          errorMessage,
          errorList,
          response.statusCode,
          ExceptionType.FETCHDATA,
          requestUrl,
        );
      case 401:
      case 403:
        throw getException(
          errorCode,
          errorMessage,
          errorList,
          response.statusCode,
          ExceptionType.RUNTIMEERROR,
          requestUrl,
        );
      case 500:
        throw getException(
          errorCode,
          errorMessage,
          errorList,
          response.statusCode,
          ExceptionType.RUNTIMEERROR,
          requestUrl,
        );
      case 502:
        throw getException(
          errorCode,
          errorMessage,
          errorList,
          response.statusCode,
          ExceptionType.CONNECTIONISSUE,
          requestUrl,
        );
      case 503:
        throw CustomException(
          'Service Temporarily Unavailable',
          response.statusCode,
          ExceptionType.SERVICEUNAVAILABLE,
          '503',
          requestUrl,
        );
      default:
        throw CustomException(
          errorMessage,
          response.statusCode,
          ExceptionType.OTHER,
          errorCode,
          requestUrl,
        );
    }
  }

  CustomException getException(
    String errorCode,
    String errorMsg,
    List<dynamic> errorList,
    int statusCode,
    ExceptionType exceptionType,
    String requestUrl,
  ) {
    for (var error in errorList) {
      if ((error['message'] ?? '').contains(Constants.INVALID_EXCEPTION_CODE)) {
        throw CustomException(
          'Session Expired',
          statusCode,
          ExceptionType.UNAUTHORIZED,
          errorCode,
          requestUrl,
        );
      }
    }
    throw CustomException(
        errorMsg, statusCode, exceptionType, errorCode, requestUrl,);
  }
}
