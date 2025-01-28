import 'package:get/get.dart';
import 'package:mobile_app/controller/challan_controller.dart';

class ChallanBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => ChallanController());
  }
}
