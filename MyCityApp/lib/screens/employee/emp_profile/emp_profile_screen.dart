import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:mobile_app/components/bottom_sheet.dart';
import 'package:mobile_app/components/drodown_button.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/profile_widget.dart';
import 'package:mobile_app/components/text_formfield_normal.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/model/citizen/user_profile/user_profile.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/utils/constants/constants.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/loaders.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:photo_view/photo_view.dart';

class EmpProfileScreen extends StatefulWidget {
  const EmpProfileScreen({super.key});

  @override
  State<EmpProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<EmpProfileScreen> {
  final _authController = Get.find<AuthController>();
  final _fileController = Get.find<FileController>();
  final _editProfileController = Get.find<EditProfileController>();

  final mobileNo = TextEditingController();
  final cityCtrl = TextEditingController();

  String? gender;

  final _formKey = GlobalKey<FormState>();
  DateTime? selectedDate;
  String formattedDate = '';

  List<String> maleItems = [
    'MALE',
    'FEMALE',
    'TRANSGENDER',
  ];

  Map arguments = Get.arguments ?? {};

  Future<void> passwordValidator() async {
    if (!_formKey.currentState!.validate()) return;

    // Check if new password and confirm password match
    if (_editProfileController.newPwd.value.text !=
        _editProfileController.confirmPwd.value.text) {
      snackBar(
        "Password Mismatch",
        "The new password and confirm password do not match.",
        Colors.red,
      );
      return;
    }

    // If passwords match, proceed with updating the password
    await _editProfileController.updatePassword(
      token: _authController.token!.accessToken!,
    );
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback(
      (_) => _editProfileController.getProfile(token: _authController.token!),
    );
  }

  Future<void> selectDate(BuildContext context, User user) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      builder: (BuildContext context, Widget? child) {
        return Theme(
          data: ThemeData.light().copyWith(
            colorScheme: const ColorScheme.light(
              primary: BaseConfig.appThemeColor1,
            ),
          ),
          child: child!,
        );
      },
      initialDate: selectedDate ?? DateTime.now(),
      firstDate: DateTime(1900),
      lastDate: DateTime.now(),
    );
    if (picked == null) return;
    if (picked != selectedDate) {
      setState(() {
        selectedDate = picked;
        formattedDate = formatDate(picked, format: 'dd-MM-yyyy');
        print(formattedDate);
        user.dobCtrl.text = formattedDate;
      });
    }
  }

  Future<void> formValidator(
    context,
    User user, {
    bool isDelete = false,
  }) async {
    if (!_formKey.currentState!.validate()) return;
    String? fileId;
    final token = _authController.token?.accessToken!;
    final tenantId = _editProfileController.userProfile.user!.first.tenantId!;
    Loaders.showLoadingDialog(context);

    if (_fileController.imageFile != null) {
      fileId = await _fileController.postFile(
        token: token!,
        tenantId: tenantId,
        module: 'citizen-profile',
      );

      dPrint(
        'File StoreData: $fileId',
        enableLog: true,
      );

      await HiveService.setData(Constants.PROFILE_FILESTORE_ID, fileId);

      if (fileId != null) {
        final fileStore = await _fileController.getFiles(
          tenantId: tenantId,
          token: token,
          fileStoreIds: fileId,
        );
        if (fileStore != null) {
          user.photo = fileStore.fileStoreIds!.first.id;
        }
      }
    }

    if (fileId == null) {
      var imgId = await HiveService.getData(Constants.PROFILE_FILESTORE_ID);
      user.photo = imgId ?? "";
    }

    if (isDelete) {
      user.photo = null;
    }
    _editProfileController.user.getText();
    print("-----------User Details Chk--------");
    print(user.toJson());
    await _editProfileController
        .updateProfile(
      token: token!,
      user: user,
    )
        .whenComplete(
      () async {
        _fileController.removeSelectedImage();
        await _editProfileController.getProfile(token: _authController.token!);
        Navigator.of(context).pop();
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, o) {
        return Scaffold(
          appBar: HeaderTop(
            onPressed: () {
              Get.offAndToNamed(AppRoutes.EMP_BOTTOM_NAV);
            },
            title: getLocalizedString(i18.profile.EDIT_PROFILE),
          ),
          body: SizedBox(
            height: Get.height,
            width: Get.width,
            child: SingleChildScrollView(
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: !_authController.isValidUser
                    ? _loginPage()
                    : StreamBuilder(
                        stream: _editProfileController.streamCtrl.stream,
                        builder: (context, snapshot) {
                          if (snapshot.hasData) {
                            if (snapshot.data is String ||
                                snapshot.data == null) {
                              return const Center(
                                child: Text("No Profile Info"),
                              );
                            }
                            return Card(
                              child: Padding(
                                padding: const EdgeInsets.fromLTRB(
                                  20.0,
                                  0.0,
                                  20.0,
                                  0.0,
                                ),
                                child: _buildProfileForm(
                                  context,
                                  snapshot.data,
                                  o,
                                ),
                              ),
                            );
                          } else if (snapshot.hasError) {
                            return networkErrorPage(
                              context,
                              () => _editProfileController.getProfile(
                                token: _authController.token!,
                              ),
                            );
                          } else {
                            switch (snapshot.connectionState) {
                              case ConnectionState.waiting:
                                return SizedBox(
                                  height: Get.height * 0.8,
                                  child: showCircularIndicator(),
                                );
                              case ConnectionState.active:
                                return SizedBox(
                                  height: Get.height * 0.8,
                                  child: showCircularIndicator(),
                                );
                              default:
                                return const SizedBox.shrink();
                            }
                          }
                        },
                      ),
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _loginPage() {
    return Center(
      child: FilledButtonApp(
        text: getLocalizedString(i18.common.LOGIN),
        onPressed: () => Get.offAllNamed(AppRoutes.LOGIN),
      ),
    );
  }

  Widget _buildProfileForm(BuildContext context, User user, Orientation o) {
    mobileNo.text = user.mobileNumber ?? '';
    cityCtrl.text = getLocalizedString(user.tenantId);
    return Form(
      key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Center(
            child: GetBuilder<FileController>(
              builder: (fileController) {
                return ProfileWidget(
                  imageUrl: fileController.imageFile != null
                      ? fileController.imageFile!.path
                      : _editProfileController.userProfile.getUserPhoto() ?? '',
                  size: 150.0,
                  iconSize: 100,
                  svgSize: 30,
                  isFile: fileController.imageFile != null ? true : false,
                  onPressed: () {
                    fileController.removeSelectedImage();
                    openBottomSheet(
                      onTabImageGallery: () {
                        _fileController.selectAndPickImage();
                      },
                      onTabImageCamera: () {
                        _fileController.selectAndPickImage(
                          imageSource: ImageSource.camera,
                        );
                      },
                    );
                  },
                  imgClick: () {
                    dPrint('Click: ${fileController.imageFile}');
                    if (fileController.imageFile == null &&
                        _editProfileController.userProfile.user?.first.photo ==
                            null) {
                      return;
                    }
                    _showDeleteDialogue(
                      context,
                      fileController.imageFile,
                      user,
                    );
                  },
                );
              },
            ),
          ),
          const SizedBox(height: 20),
          Wrap(
            children: [
              Wrap(
                children: [
                  MediumTextNotoSans(
                    fontWeight: FontWeight.w700,
                    size: o == Orientation.portrait ? 16.sp : 8.sp,
                    text: getLocalizedString(i18.profile.NAME),
                  ),
                  MediumTextNotoSans(
                    fontWeight: FontWeight.w700,
                    size: o == Orientation.portrait ? 16.sp : 8.sp,
                    text: '*',
                    color: BaseConfig.redColor1,
                  ),
                ],
              ),
            ],
          ),
          textFormFieldNormal(
            context,
            getLocalizedString(i18.profile.NAME),
            controller: user.nameCtrl,
            textInputAction: TextInputAction.done,
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Name is required';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),
          MediumTextNotoSans(
            fontWeight: FontWeight.w700,
            size: o == Orientation.portrait ? 16.sp : 8.sp,
            text: 'Gender',
          ),
          dropDownButton<String>(
            context,
            hinText: getLocalizedString(i18.profile.GENDER),
            value: user.gender,
            radius: 6.r,
            onChanged: (val) =>
                _editProfileController.onChangeOfGender(val, user),
            items: maleItems.map((e) {
              return DropdownMenuItem(
                value: e,
                child: MediumText(text: e),
              );
            }).toList(),
          ),
          const SizedBox(height: 20),
          MediumTextNotoSans(
            fontWeight: FontWeight.w700,
            size: o == Orientation.portrait ? 16.sp : 8.sp,
            text: 'City',
          ),
          textFormFieldNormal(
            context,
            getLocalizedString('City'),
            controller: cityCtrl,
            textInputAction: TextInputAction.done,
            readOnly: true,
          ),
          const SizedBox(height: 20),
          Wrap(
            children: [
              MediumTextNotoSans(
                fontWeight: FontWeight.w700,
                size: o == Orientation.portrait ? 16.sp : 8.sp,
                text: 'Mobile no',
              ),
              MediumTextNotoSans(
                fontWeight: FontWeight.w700,
                size: o == Orientation.portrait ? 16.sp : 8.sp,
                text: '*',
                color: BaseConfig.redColor1,
              ),
            ],
          ),
          textFormFieldNormal(
            context,
            'Mobile no',
            controller: mobileNo,
            textInputAction: TextInputAction.next,
            readOnly: true,
            prefixIcon: Padding(
              padding: const EdgeInsets.only(top: 12.0, left: 8.0, right: 8.0),
              child: MediumTextNotoSans(
                size: 16.sp,
                text: '+91',
              ),
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Mobile no is required';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),
          MediumTextNotoSans(
            fontWeight: FontWeight.w700,
            size: o == Orientation.portrait ? 16.sp : 8.sp,
            text: getLocalizedString(i18.profile.EMAIL),
          ),
          textFormFieldNormal(
            context,
            getLocalizedString(i18.profile.EMAIL),
            controller: user.emailCtrl,
            keyboardType: TextInputType.emailAddress,
            textInputAction: TextInputAction.done,
          ),
          const SizedBox(height: 20),
          Wrap(
            children: [
              MediumTextNotoSans(
                fontWeight: FontWeight.w700,
                size: o == Orientation.portrait ? 16.sp : 8.sp,
                text: getLocalizedString(i18.profile.DOB),
              ),
              MediumTextNotoSans(
                fontWeight: FontWeight.w700,
                size: o == Orientation.portrait ? 16.sp : 8.sp,
                text: '*',
                color: BaseConfig.redColor1,
              ),
            ],
          ),
          textFormFieldNormal(
            context,
            getLocalizedString(i18.profile.DOB),
            controller: user.dobCtrl,
            readOnly: true,
            keyboardType: AppPlatforms.platformKeyboardType(),
            inputFormatters: [
              FilteringTextInputFormatter.digitsOnly,
            ],
            textInputAction: TextInputAction.done,
            suffixIcon: IconButton(
              onPressed: () => selectDate(context, user),
              icon: const Icon(
                Icons.calendar_month,
                color: BaseConfig.appThemeColor1,
              ),
            ),
            validator: (String? value) {
              if (value == null || value.isEmpty) {
                return 'Date of Birth is required';
              }
              return null;
            },
          ),
          const SizedBox(
            height: 20,
          ),
          MediumTextNotoSans(
            fontWeight: FontWeight.w700,
            size: o == Orientation.portrait ? 16.sp : 8.sp,
            text: 'Change Password',
            color: BaseConfig.redColor1,
          ).ripple(() {
            _editProfileController.toggleFields();
          }),
          Obx(() {
            return _editProfileController.showFields.value
                ? Column(
                    children: [
                      textFormFieldNormal(
                        context,
                        'Current Password',
                        controller: _editProfileController.currentPwd.value,
                        keyboardType: TextInputType.text,
                        validator: (value) {
                          if (value == null) {
                            return "required *";
                          }
                        },
                        textInputAction: TextInputAction.done,
                      ),
                      const SizedBox(
                        height: 20,
                      ),
                      textFormFieldNormal(
                        context,
                        'New Current',
                        controller: _editProfileController.newPwd.value,
                        keyboardType: TextInputType.text,
                        validator: (value) {
                          if (value == null) {
                            return "required *";
                          }
                        },
                        textInputAction: TextInputAction.done,
                      ),
                      const SizedBox(
                        height: 20,
                      ),
                      textFormFieldNormal(
                        context,
                        'Confirm Password',
                        controller: _editProfileController.confirmPwd.value,
                        keyboardType: TextInputType.text,
                        validator: (value) {
                          if (value == null) {
                            return "required *";
                          }
                        },
                        textInputAction: TextInputAction.done,
                      ),
                    ],
                  )
                : Container();
          }),
          const SizedBox(height: 30),
          FilledButtonApp(
            height: 55,
            width: Get.width,
            text: getLocalizedString(i18.common.SAVE),
            onPressed: () {
              if (_editProfileController.currentPwd.value.text.isEmpty &&
                  _editProfileController.newPwd.value.text.isEmpty &&
                  _editProfileController.confirmPwd.value.text.isEmpty) {
                formValidator(
                  context,
                  user,
                );
              } else {
                passwordValidator();
              }
            },
          ),
          const SizedBox(height: 50),
        ],
      ),
    );
  }

  _showDeleteDialogue(context, File? img, user) {
    return showAdaptiveDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: Theme.of(context).colorScheme.surface,
        surfaceTintColor: Theme.of(context).colorScheme.surface,
        scrollable: false,
        content: SizedBox(
          width: Get.width,
          height: Get.height * 0.6,
          child: Column(
            children: [
              SizedBox(
                width: Get.width,
                height: Get.height * 0.5,
                child: PhotoView(
                  backgroundDecoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.surface,
                    borderRadius: BorderRadius.circular(10),
                  ),
                  imageProvider: img != null
                      ? Image.file(
                          img,
                        ).image
                      : NetworkImage(
                          _editProfileController.userProfile.getUserPhoto() ??
                              '',
                        ),
                ),
              ),
              IconButton(
                onPressed: () {
                  if (img != null) {
                    _fileController.removeSelectedImage();
                    return Get.back();
                  }
                  formValidator(
                    context,
                    user,
                    isDelete: true,
                  );
                  Get.back();
                },
                icon: const Column(
                  children: [
                    Icon(
                      Icons.delete,
                      color: BaseConfig.redColor1,
                      size: 30,
                    ),
                    MediumText(
                      text: 'Delete',
                      color: BaseConfig.redColor1,
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
