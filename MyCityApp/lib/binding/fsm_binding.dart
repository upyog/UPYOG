import 'package:get/get.dart';
import 'package:mobile_app/controller/fsm_controller.dart';

class FsmBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => FsmController());
  }
}
