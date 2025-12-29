import 'dart:async';

import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/model/citizen/fsm/fsm.dart';
import 'package:mobile_app/repository/fsm_repository.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class FsmController extends GetxController {
  var streamCtrl = StreamController.broadcast();
  late FsmModel fsmModel;

  RxInt length = 0.obs;
  RxBool isLoading = false.obs;
  int limit = 10, offset = 0;

  Future<void> loadMore({
    required String token,
    required String uuid,
  }) async {
    try {
      isLoading.value = true;
      offset += limit;
      await getFsmApplications(token: token, uuid: uuid);
    } finally {
      isLoading.value = false;
    }
  }

  void setDefaultLimit() {
    length.value = 0;
    limit = 10;
    offset = 0;
  }

  Future<void> getFsmApplications({
    required String? token,
    required String? uuid,
  }) async {
    if (token == null && token!.isEmpty) return;
    try {
      final fsmResponse = await FsmRepository.getFsmApplications(
        token: token,
        query: {
          "tenantId": BaseConfig.STATE_TENANT_ID,
          "uuid": uuid,
          "limit": limit.toString(),
          "offset": offset.toString(),
        },
      );
      fsmModel = fsmResponse;
      streamCtrl.add(fsmModel);
      length.value = fsmModel.fsm?.length ?? 0;
      dPrint("Fire Noc --------------------------------");
      dPrint(fsmModel.toJson());
    } catch (e, s) {
      dPrint('GET FSM APPLICATION ERROR: $e');
      streamCtrl.add('FSM APPLICATION ERROR');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }
}
