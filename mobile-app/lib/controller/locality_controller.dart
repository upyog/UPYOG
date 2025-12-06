import 'dart:async';

import 'package:get/get.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/model/common/locality/locality_model.dart';
import 'package:mobile_app/repository/locality_repository.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class LocalityController extends GetxController {
  //Controller
  final _authController = Get.find<AuthController>();

  //Stream controller
  var streamCtrl = StreamController.broadcast();

  LocalityModel? locality;

  RxBool isLoading = false.obs;
  RxList<Boundary> selectedLocalityList = <Boundary>[].obs;

  RxString assigneeUid = ''.obs;

  // @override
  // void onInit() {
  //   super.onInit();
  //   fetchLocality();
  // }

  @override
  void onClose() {
    super.onClose();
    streamCtrl.close();
  }

  List<String> getSelectedLocalityCode() {
    return selectedLocalityList.map((code) => code.code!).toList();
  }

  // Fetch Locality on page load
  Future<void> fetchLocality({
    String hierarchyTypeCode = 'REVENUE',
  }) async {
    isLoading.value = true;
    try {
      final query = {
        "hierarchyTypeCode": hierarchyTypeCode,
        "boundaryType": "Locality",
        "tenantId": _authController.token!.userRequest!.tenantId,
      };

      final localityRes = await LocalityRepository.getLocality(
        token: _authController.token!.accessToken!,
        query: query,
      );

      locality = LocalityModel.fromJson(localityRes);
      streamCtrl.add(locality);
      isLoading.value = false;
    } catch (e, s) {
      isLoading.value = false;
      dPrint('fetchLocality Error: ${e.toString()}');
      streamCtrl.addError('Locality Error');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }
}
