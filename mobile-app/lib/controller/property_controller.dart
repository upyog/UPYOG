import 'dart:async';
import 'dart:developer';

import 'package:get/get.dart';
import 'package:mobile_app/model/citizen/property/property.dart';
import 'package:mobile_app/repository/property_repository.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:rxdart/rxdart.dart';

class PropertyController extends GetxController {
  final streamCntrl = BehaviorSubject();
  Properties? properties;

  RxBool isLoading = false.obs;

  @override
  void onClose() {
    super.onClose();
    streamCntrl.close();
  }

  Future<Properties?> getPropertiesByID({
    required String token,
    required String propertyId,
    required String tenantCity,
  }) async {
    log('Property call');
    isLoading.value = true;
    try {
      var query = {
        'tenantId': tenantCity,
        'propertyIds': propertyId,
      };
      final propertyResponse =
          await PropertyRepository.getProperty(token: token, query: query);
      properties = Properties.fromJson(propertyResponse);
      streamCntrl.sink.add(properties);
      isLoading.value = false;
    } catch (e, s) {
      isLoading.value = false;
      dPrint('getPropertiesByIDError: $e');
      streamCntrl.sink.addError(e);
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return properties;
  }
}
