import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/location_controller.dart';
import 'package:mobile_app/model/citizen/meripehchaan/meripehchaan_model.dart';
import 'package:mobile_app/model/citizen/token/token.dart';
import 'package:mobile_app/model/custom_exception_model.dart';
import 'package:mobile_app/repository/authenticate_repository.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/services/secure_storage_service.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/utils.dart';

class AuthController extends GetxController {
  //TextEditingController
  final mobileNoController = TextEditingController().obs;
  final otpEditingController = TextEditingController().obs;
  final userNameController = TextEditingController().obs;
  final passwordController = TextEditingController().obs;
  final nameController = TextEditingController().obs;
  final dobController = TextEditingController().obs;

  // Add reactive variables to track text values
  final RxString usernameText = ''.obs;
  final RxString passwordText = ''.obs;

  Token? token;
  UserRequest? userRequest;
  RxString? userType = UserType.CITIZEN.name.obs;
  RxString selectedCity = "".obs;
  RxBool isButtonEnabled = false.obs;
  RxBool isLoading = false.obs,
      isPasswordVisible = false.obs,
      disableNumberField = false.obs,
      isNumberValid = false.obs,
      termsCondition = false.obs,
      isOtpValid = false.obs;

  //Check user validity
  String? _userData;
  String? _empUserData;
  Meripehchaan? meripehchaan;

  @override
  void onReady() {
    super.onReady();
    initValidUser();
    getUserLocalData();

    // Add listeners to sync reactive variables with text controllers
    userNameController.value.addListener(() {
      usernameText.value = userNameController.value.text.trim();
    });

    passwordController.value.addListener(() {
      passwordText.value = passwordController.value.text.trim();
    });
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
    if (token == null) {
      return false;
    }

    if (userType?.value == UserType.CITIZEN.name) {
      return (_userData != null && isNotNullOrEmpty(token?.accessToken));
    } else {
      return (_empUserData != null && isNotNullOrEmpty(token?.accessToken));
    }
  }

  Future<void> initValidUser() async {
    _userData = await storage.getString(SecureStorageConstants.LOGIN_KEY);
    _empUserData =
        await storage.getString(SecureStorageConstants.EMP_LOGIN_KEY);
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
          "userType": UserType.CITIZEN.name,
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
        Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
        // Get.offAllNamed(AppRoutes.SELECT_CATEGORY);
      }
    } on CustomException catch (e) {
      if (e.statusCode == 400 && e.exceptionType == ExceptionType.FETCHDATA) {
        snackBar('Invalid', 'Invalid OTP', Colors.red);
        // await storage.clearAll();
      }
    }
  }

  // Verify Otp
  Future<void> otpValidate({
    String? phoneNo,
    String? otp,
    bool isSignUp = true,
    required UserType userType,
    bool isMeripehchaanLogin = false,
  }) async {
    try {
      final headers = isMeripehchaanLogin
          ? null
          : {
              HttpHeaders.contentTypeHeader:
                  'application/x-www-form-urlencoded',
              "Access-Control-Allow-Origin": "*",
              "authorization": "Basic ${BaseConfig.AUTHORIZATION_KEY}",
            };

      Map<String, dynamic> body;

      if (userType == UserType.CITIZEN) {
        if (isMeripehchaanLogin) {
          body = {
            "TokenReq": {
              "dlReqRef": meripehchaan!.dlReqRef,
              "code": otp,
              "module": "SSO",
            },
          };
        } else {
          body = {
            "username": phoneNo!,
            "password": otp!,
            "scope": "read",
            "grant_type": "password",
            "tenantId": BaseConfig.STATE_TENANT_ID,
            "userType": userType.name,
          };
        }
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

      // disableNumberField.value = false;

      final otpRes = await AuthenticateRepository.validateOtp(
        isMeripehchaanLogin: isMeripehchaanLogin,
        headers: headers,
        body: body,
      );
      if (otpRes != null) {
        token = Token.fromJson(otpRes);

        if (termsCondition.value) {
          Future.delayed(const Duration(seconds: 1), () {
            termsCondition.value = false;
          });
        }

        if (userType.name == UserType.EMPLOYEE.name) {
          clearEmployeeLoginFields();

          bool checkRole = InspectorType.values.any(
            (element) =>
                token?.userRequest?.roles
                    ?.any((role) => role.code == element.name) ??
                false,
          );

          if (!checkRole) {
            snackBar(
              'User ${token?.userRequest?.userName} does not have the required role to use the Mobile App.',
              'Allowed Roles are: ${InspectorType.values.map((e) => e.name).toList().join(', ')}',
              Colors.red,
              seconds: 5,
            );
            return;
          }

          storage.setBool(
            SecureStorageConstants.FIRST_TIME_USER,
            true,
          );

          await storage.setString(
            SecureStorageConstants.EMP_LOGIN_KEY,
            jsonEncode(token?.toJson()),
          );
          await initValidUser();
          Get.offAllNamed(AppRoutes.EMP_BOTTOM_NAV);
        } else {
          await storage.setString(
            SecureStorageConstants.LOGIN_KEY,
            jsonEncode(token?.toJson()),
          );
          if (isSignUp) {
            Get.offAllNamed(AppRoutes.LOCATION_CHOOSE);
          }
        }
      }
    } on CustomException catch (e) {
      if (e.statusCode == 400 &&
          e.exceptionType == ExceptionType.FETCHDATA &&
          !isMeripehchaanLogin) {
        snackBar(
          'Invalid',
          userType == UserType.CITIZEN
              ? 'Invalid OTP'
              : 'Invalid login credentials',
          Colors.red,
        );
        await storage.delete(SecureStorageConstants.LOGIN_KEY);
      } else if (e.statusCode == 429) {
        snackBar(
          'Rate limit exceeded',
          userType == UserType.CITIZEN
              ? 'Rate limit exceeded'
              : 'RateLimitExceededException',
          Colors.red,
        );
        await storage.delete(SecureStorageConstants.LOGIN_KEY);
      }
    }
  }

  //MeriPahchan Login
  Future<String?> getMeriPahchanUrl() async {
    try {
      final query = {'module': 'SSO'};
      final res = await AuthenticateRepository.getMeriPahchanUrl(query: query);
      meripehchaan = meripehchaanFromJson(res);
      return meripehchaan!.redirectUrl!;
    } catch (e) {
      dPrint('getMeriPahchanUrl Error: $e', enableLog: true);
      return null;
    }
  }

  //MeriPahchan Login
  // Future<void> meriPahchanLogin(String code) async {
  //   try {
  //     final query = {
  //       'code': code,
  //       'codeVerifier': await storage.getString(SecureStorageConstants.CODE_VERIFIER),
  //     };
  //     final res = await AuthenticateRepository.meriPahchanLogin(query: query);
  //     token = Token.fromJson(res);
  //     await HiveService.setData(
  //       HiveConstants.LOGIN_KEY,
  //       jsonEncode(token?.toJson()),
  //     );
  //     Get.offAllNamed(AppRoutes.LOCATION_CHOOSE);
  //   } catch (e) {
  //     dPrint('meriPahchanLogin Error: $e', enableLog: true);
  //   }
  // }

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
    var data = await storage.getString(SecureStorageConstants.LOGIN_KEY);
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
    var dataEmployee =
        await storage.getString(SecureStorageConstants.EMP_LOGIN_KEY);
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
