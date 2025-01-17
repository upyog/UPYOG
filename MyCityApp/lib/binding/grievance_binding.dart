import 'package:get/get.dart';
import 'package:mobile_app/controller/grievance_controller.dart';

class GrievanceBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<GrievanceController>(() => GrievanceController());
  }
}