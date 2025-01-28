import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:hive/hive.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/location_controller.dart';
import 'package:mobile_app/model/citizen/token/token.dart';
import 'package:mobile_app/model/custom_exception_model.dart';
import 'package:mobile_app/repository/authenticate_repository.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class AuthController extends GetxController {
  //TextEditingController
  final mobileNoController = TextEditingController().obs;
  final otpEditingController = TextEditingController().obs;
  final userNameController = TextEditingController().obs;
  final passwordController = TextEditingController().obs;
  final nameController = TextEditingController().obs;
  final dobController = TextEditingController().obs;
  Token? token;
  RxString? userType = UserType.CITIZEN.name.obs;
  RxString selectedCity = "".obs;
  RxBool isButtonEnabled = false.obs;
  RxBool isLoading = false.obs,
      isPasswordVisible = false.obs,
      disableNumberField = false.obs,
      isNumberValid = false.obs,
      termsCondition = false.obs,
      isOtpValid = false.obs;

  void onTermsConditionChanged(bool value) {
    termsCondition.value = value;
    if (termsCondition.value) {
      if (isOtpValid.value) {
        isButtonEnabled.value = true;
      } else if (isNumberValid.value) {
        isButtonEnabled.value = true;
      } else {
        isButtonEnabled.value = false;
      }
    } else {
      isButtonEnabled.value = false;
    }
  }

  //Check user validity
  bool get isValidUser => _isValidUser();

  //Check user valid data
  bool _isValidUser() {
    box = Hive.box(HiveConstants.appBox);
    final userData = box.get(HiveConstants.LOGIN_KEY);
    final empUserData = box.get(HiveConstants.EMP_LOGIN_KEY);

    if (token == null) {
      return false;
    }

    if (userType?.value == UserType.CITIZEN.name) {
      return (userData != null && isNotNullOrEmpty(token?.accessToken));
    } else {
      return (empUserData != null && isNotNullOrEmpty(token?.accessToken));
    }
  }

  void validateNumber(String value) {
    if (value.length == 10 && RegExp(r'^\d+$').hasMatch(value)) {
      isNumberValid.value = true;
      isButtonEnabled.value = true;
    } else {
      isNumberValid.value = false;
      isButtonEnabled.value = false;
    }
    isButtonEnabled.value = isNumberValid.value && termsCondition.value;
  }

  void validateOtp(String value) {
    if (value.length == 6 && RegExp(r'^\d+$').hasMatch(value)) {
      isOtpValid.value = true;
      isButtonEnabled.value = true;
    } else {
      isOtpValid.value = false;
      isButtonEnabled.value = false;
    }
    isButtonEnabled.value = isOtpValid.value && termsCondition.value;
  }

  @override
  void onReady() {
    super.onReady();
    getUserLocalData();
  }

  @override
  void onClose() {
    super.onClose();
    mobileNoController.value.dispose();
    userNameController.value.dispose();
    passwordController.value.dispose();
    nameController.value.dispose();
    dobController.value.dispose();
    otpEditingController.value.dispose();
  }

  //Clear employee login field
  void clearEmployeeLoginFields() {
    userNameController.value.clear();
    passwordController.value.clear();
    Get.find<CityController>().empSelectedCity.value = "";
  }

  //SignUp with DoB, Name & Mobile number
  Future<void> signUpOTP({
    required String mobile,
    required String dob,
    required String name,
  }) async {
    try {
      Map body = {
        "otp": {
          "dob": dob,
          "mobileNumber": mobile,
          "name": name,
          "tenantId": BaseConfig.STATE_TENANT_ID,
          "userType": "citizen",
          "type": "register",
        },
      };

      final signUpRes = await AuthenticateRepository.signUpOTP(
        query: {
          'tenantId': BaseConfig.STATE_TENANT_ID,
        },
        body: body,
      );

      if (signUpRes != null) {
        isLoading.value = false;
        disableNumberField.value = true;
        // Get.toNamed(
        //   AppRoutes.LOGIN,
        // );
        dPrint(signUpRes);
      }
    } on CustomException catch (e) {
      isLoading.value = false;
      if (e.statusCode == 400 && e.exceptionType == ExceptionType.FETCHDATA) {
        Get.toNamed(AppRoutes.SIGN_UP);
      }
    }
  }

  // Login validations with mobile no
  Future<void> loginValidate({
    required String mobile,
  }) async {
    try {
      Map body = {
        "otp": {
          "mobileNumber": mobile,
          "tenantId": BaseConfig.STATE_TENANT_ID,
          "type": "login",
        },
      };

      final loginRes = await AuthenticateRepository.login(
        query: {
          'tenantId': BaseConfig.STATE_TENANT_ID,
        },
        body: body,
      );

      if (loginRes != null) {
        isLoading.value = false;
        disableNumberField.value = true;
        isButtonEnabled.value = false;
        otpEditingController.value.text = '';
        // Get.toNamed(AppRoutes.OTP_VERIFY);
      }
    } on CustomException catch (e) {
      isLoading.value = false;
      if (e.statusCode == 400 && e.exceptionType == ExceptionType.FETCHDATA) {
        Get.toNamed(AppRoutes.SIGN_UP);
      }
    }
  }

  Future<bool> sendOtp({required String mobile}) async {
    bool isSendOtp = false;
    try {
      var body = {
        "otp": {
          "mobileNumber": mobile,
          "tenantId": BaseConfig.STATE_TENANT_ID,
          "userType": "citizen",
          "type": "login",
        },
      };

      final loginRes = await AuthenticateRepository.login(
        query: {
          'tenantId': BaseConfig.STATE_TENANT_ID,
        },
        body: body,
      );

      if (loginRes != null) {
        isLoading.value = false;
        isSendOtp = true;
      }
    } on CustomException catch (e) {
      isLoading.value = false;
      isSendOtp = false;
      if (e.statusCode == 400 && e.exceptionType == ExceptionType.FETCHDATA) {
        Get.toNamed(AppRoutes.SIGN_UP);
      }
    }
    return isSendOtp;
  }

  //Validate SignUp Otp
  Future<void> createUser({
    String? name,
    String? phoneNo,
    String? otp,
    bool isSignUp = true,
  }) async {
    try {
      final otpRes = await AuthenticateRepository.createUser(
        body: {
          "User": {
            "name": name!,
            "username": phoneNo!,
            "otpReference": otp!,
            "tenantId": BaseConfig.STATE_TENANT_ID,
          },
        },
        query: {
          "tenantId": BaseConfig.STATE_TENANT_ID,
        },
      );

      if (otpRes != null) {
        dPrint('OTP: $otpRes', enableLog: true);
        disableNumberField.value = false;
        // Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
        Get.offAllNamed(AppRoutes.SELECT_CATEGORY);
      }
    } on CustomException catch (e) {
      if (e.statusCode == 400 && e.exceptionType == ExceptionType.FETCHDATA) {
        snackBar('Invalid', 'Invalid OTP', Colors.red);
        //await HiveService.clearBox();
      }
    }
  }

  // Verify Otp
  Future<void> otpValidate({
    String? phoneNo,
    String? otp,
    bool isSignUp = true,
    required UserType userType,
  }) async {
    try {
      var headers = {
        HttpHeaders.contentTypeHeader: 'application/x-www-form-urlencoded',
        "Access-Control-Allow-Origin": "*",
        "authorization": "Basic ${BaseConfig.AUTHORIZATION_KEY}",
      };

      Map<String, String> body;

      if (userType == UserType.CITIZEN) {
        body = {
          "username": phoneNo!,
          "password": otp!,
          "scope": "read",
          "grant_type": "password",
          "tenantId": BaseConfig.STATE_TENANT_ID,
          "userType": userType.name,
        };
      } else {
        body = {
          "username": userNameController.value.text,
          "password": passwordController.value.text,
          "scope": "read",
          "grant_type": "password",
          "tenantId": Get.find<CityController>().empSelectedCity.value,
          "userType": userType.name,
        };
      }

      final otpRes = await AuthenticateRepository.validateOtp(
        headers: headers,
        body: body,
      );

      if (otpRes != null) {
        token = Token.fromJson(otpRes);

        if (userType.name == UserType.EMPLOYEE.name) {
          clearEmployeeLoginFields();

          HiveService.setData(
            HiveConstants.FIRST_TIME_USER,
            true,
          );

          await HiveService.setData(
            HiveConstants.EMP_LOGIN_KEY,
            jsonEncode(token?.toJson()),
          );
          Get.offAllNamed(AppRoutes.EMP_BOTTOM_NAV);
        } else {
          await HiveService.setData(
            HiveConstants.LOGIN_KEY,
            jsonEncode(token?.toJson()),
          );
          if (isSignUp) {
            Get.offAllNamed(AppRoutes.LOCATION_CHOOSE);
          }
        }
      }
    } on CustomException catch (e) {
      if (e.statusCode == 400 && e.exceptionType == ExceptionType.FETCHDATA) {
        snackBar(
          'Invalid',
          userType == UserType.CITIZEN
              ? 'Invalid OTP'
              : 'Invalid login credentials',
          Colors.red,
        );
        await HiveService.deleteData(HiveConstants.LOGIN_KEY);
      } else if (e.statusCode == 429) {
        snackBar(
          'Rate limit exceeded',
          userType == UserType.CITIZEN
              ? 'Rate limit exceeded'
              : 'RateLimitExceededException',
          Colors.red,
        );
        await HiveService.deleteData(HiveConstants.LOGIN_KEY);
      }
    }
  }

  Future<void> getUserLocalData() async {
    // if (!isValidUser) return;
    final uType = await getUserType();
    userType!.value = uType;

    if (userType?.value == UserType.CITIZEN.name) {
      await citizenLocalData();
    } else {
      await employeeLocalData();
    }
  }

  Future<void> citizenLocalData() async {
    var data = await HiveService.getData(HiveConstants.LOGIN_KEY);
    if (data != null && data.isNotEmpty) {
      var decodeData = jsonDecode(data);
      token = Token.fromJson(decodeData);
      if (token?.accessToken != null) {
        await Get.find<EditProfileController>().getProfile(token: token!);
      }
      dPrint('Token: ${token?.toJson().toString()}', enableLog: true);
    }
  }

  Future<void> employeeLocalData() async {
    var dataEmployee = await HiveService.getData(HiveConstants.EMP_LOGIN_KEY);
    if (dataEmployee != null && dataEmployee.isNotEmpty) {
      var decodeData = jsonDecode(dataEmployee);
      token = Token.fromJson(decodeData);
      if (token?.accessToken != null) {
        await Get.find<EditProfileController>().getProfile(token: token!);
      }
      dPrint('Token: ${token?.toJson().toString()}', enableLog: true);
    }
  }
}
