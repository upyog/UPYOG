import 'package:mobile_app/model/citizen/bill/demands.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/services/base_service.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class PaymentRepository {
  static final BaseService _baseService = BaseService();

  static Future<dynamic> fetchBill({
    required String token,
    required Map<String, dynamic> query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.FETCH_BILL,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      return response;
    }
  }

  static Future<dynamic> createBill({
    required String token,
    required Map<String, dynamic> body,
    required Map<String, dynamic> query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.CREATE_BILL,
      method: RequestType.POST,
      body: body,
      requestInfo:
          RequestInfo(authToken: token, local: local, isPgCreate: true),
      queryParameters: query,
    );

    if (response != null) {
      return response;
    }
  }

  static Future<dynamic> updatePayment({
    required String token,
    required Map<String, dynamic> query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.PAYMENT_UPDATE,
      method: RequestType.POST,
      body: {},
      requestInfo:
          RequestInfo(authToken: token, local: local, isPgUpdate: true),
      queryParameters: query,
      // isTestPayment: true,
    );

    if (response != null) {
      return response;
    }
  }

  static Future<dynamic> verifyPayment({
    required String token,
    required Map<String, dynamic> query,
    required String businessService,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: '${Url.COLLECTION}$businessService/_search',
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
      // isTestPayment: true,
    );
    if (response != null) {
      return response;
    }
  }

  static Future<dynamic> searchBill({
    required String token,
    required query,
  }) async {
    final local = await getLocal();
    final response = await _baseService.makeRequest(
      url: Url.SEARCH_BILL,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );
    if (response != null) {
      return response;
    }
  }

  static Future<DemandsModel> searchBillDemand({
    required String token,
    required query,
  }) async {
    final local = await getLocal();
    late DemandsModel demandsModel;
    final response = await _baseService.makeRequest(
      url: Url.DEMAND_BILL,
      method: RequestType.POST,
      body: {},
      requestInfo: RequestInfo(authToken: token, local: local),
      queryParameters: query,
    );

    if (response != null) {
      demandsModel = DemandsModel.fromJson(response);
    }

    return demandsModel;
  }
}
