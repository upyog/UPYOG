import 'package:get/get.dart';
import 'package:mobile_app/controller/fire_noc_controller.dart';
import 'package:mobile_app/controller/locality_controller.dart';

class FireNocBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => FireNocController());
    Get.lazyPut(() => LocalityController());
  }
}
