import 'dart:async';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/model/citizen/token/token.dart';
import 'package:mobile_app/model/citizen/user_profile/user_profile.dart';
import 'package:mobile_app/repository/profile_repository.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class EditProfileController extends GetxController {
  final currentPwd = TextEditingController().obs;
  final newPwd = TextEditingController().obs;
  final confirmPwd = TextEditingController().obs;
  var streamCtrl = StreamController.broadcast();
  User user = User();
  UserProfile userProfile = UserProfile();

  final Rx<User?> myuser = Rx<User?>(null);
  var isValidEmail = false.obs;
  var showFields = false.obs;

  void toggleFields() {
    showFields.value = !showFields.value;
  }

  void setUser(User newUser) {
    myuser.value = newUser;
    //validateEmail(newUser.emailCtrl.value.text);
  }

  void validateEmail(String email) {
    isValidEmail.value = _isValidEmail(email);
  }

  bool _isValidEmail(String email) {
    final RegExp emailRegExp = RegExp(
      r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$',
    );
    return emailRegExp.hasMatch(email);
  }

  @override
  void onClose() {
    super.onClose();
    streamCtrl.close();
  }

  /// Get user profile
  Future<void> getProfile({
    required Token token,
  }) async {
    if (!isNotNullOrEmpty(token.accessToken)) return;
    try {
      var body = {
        //"tenantId": BaseConfig.STATE_TENANT_ID,
        "tenantId": token.userRequest!.tenantId!,
        "uuid": [
          token.userRequest!.uuid,
        ],
        "pageSize": "100",
      };

      final profileRes = await ProfileRepository.getProfile(
        body: body,
        token: token.accessToken!,
      );
      streamCtrl.add(profileRes.user!.first);
      user = profileRes.user!.first;
      userProfile = profileRes;
    } catch (e, s) {
      streamCtrl.add('Profile Error');
      dPrint('GetProfileError: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Update gender to User model
  void onChangeOfGender(String gender, User user) {
    user.gender = gender;
  }

  /// Update profile
  Future<void> updateProfile({
    String? fileStoreId,
    required String token,
    required User user,
  }) async {
    try {
      var userMap = _userRequest(user); // Filter user data
      final profileRes = await ProfileRepository.profileUpdate(
        body: {'user': userMap},
        token: token,
        query: {
          "tenantId": user.tenantId,
        },
      );
      if (profileRes != null) {
        userProfile = UserProfile.fromJson(profileRes);
        snackBar(
          getLocalizedString(i18.profile.PROFILE_UPDATE_SUCCESS),
          "",
          BaseConfig.statusGreenColor,
        );
      }
    } catch (e, s) {
      dPrint('updateProfileError: $e');
      ErrorHandler.logError(e.toString(), s);
    }
  }

  /// Remove unsupported field for request body
  Map _userRequest(User userData) {
    userData.dob = userData.dob?.replaceAll('-', '/');
    var userJson = userData.toJson();
    userJson.remove('salutation');
    userJson.remove('altContactNumber');
    userJson.remove('pan');
    userJson.remove('aadhaarNumber');
    userJson.remove('permanentAddress');
    userJson.remove('permanentPinCode');
    userJson.remove('correspondenceAddress');
    userJson.remove('correspondenceCity');
    userJson.remove('correspondencePinCode');
    userJson.remove('alternatemobilenumber');
    userJson.remove('accountLocked');
    userJson.remove('accountLockedDate');
    userJson.remove('fatherOrHusbandName');
    userJson.remove('relationship');
    userJson.remove('signature');
    userJson.remove('bloodGroup');
    userJson.remove('identificationMark');
    userJson.remove('createdBy');
    userJson.remove('lastModifiedBy');
    userJson.remove('createdDate');
    userJson.remove('lastModifiedDate');
    userJson.remove('pwdExpiryDate');
    return userJson;
  }

  Future<void> updatePassword({
    required String token,
  }) async {
    try {
      final profileRes = await ProfileRepository.updatePassword(
        body: {
          "existingPassword": currentPwd.value.text,
          "newPassword": newPwd.value.text,
          "tenantId": user.tenantId,
          "type": user.type,
          "username": user.userName,
          "confirmPassword": confirmPwd.value.text,
        },
        token: token,
        query: {
          "tenantId": user.tenantId,
        },
      );
      if (profileRes != null) {
        userProfile = UserProfile.fromJson(profileRes);
        snackBar(
          i18.profile.EDIT_PROFILE.tr,
          "Password updated successfully",
          Colors.green,
        );
        currentPwd.value.text = '';
        newPwd.value.text = '';
        confirmPwd.value.text = '';
      }
    } catch (e, s) {
      dPrint('updateProfileError: $e');
      snackBar(
        "updateProfileError:",
        "$e",
        BaseConfig.redColor1,
      );
      ErrorHandler.logError(e.toString(), s);
    }
  }
}
