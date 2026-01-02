import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/utils/firebase_configurations/firebase_collections.dart';
import 'package:mobile_app/utils/utils.dart';

class FirebaseService {
  /// Save FCM token to firestore
  static Future saveFcmToken(String token) async {
    try {
      final profileController = Get.find<EditProfileController>();
      final uuid = profileController.userProfile.user?.first.uuid;
      Map<String, dynamic> data = {
        'uuid': uuid,
        'phone': profileController.userProfile.user?.first.mobileNumber ?? '',
        'fcmToken': token,
      };
      await FirebaseFirestore.instance
          .collection(FirebaseCollections.USERS)
          .doc(uuid)
          .set(
            data,
            SetOptions(
              merge: true,
            ),
          );

      dPrint(
        'Fcm token doc added to ${profileController.userProfile.user!.first.mobileNumber}',
      );
    } catch (e) {
      dPrint('saveFcmToken error: $e');
    }
  }
}
