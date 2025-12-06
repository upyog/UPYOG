import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
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
import 'package:mobile_app/widgets/login_redirect_page.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:photo_view/photo_view.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final _authController = Get.find<AuthController>();
  final _fileController = Get.find<FileController>();
  final _editProfileController = Get.find<EditProfileController>();

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

  @override
  void initState() {
    super.initState();
    if (!_authController.isValidUser) return;
    WidgetsBinding.instance.addPostFrameCallback(
      (_) => _editProfileController.getProfile(token: _authController.token!),
    );
    _editProfileController.user.emailCtrl.addListener(() {
      _editProfileController.validateEmail(
        _editProfileController.user.emailCtrl.value.text,
      );
    });
    _editProfileController.validateEmail(
      _editProfileController.user.emailCtrl.text,
    );
  }

  bool get showBackBtn {
    if (arguments.containsKey('showBackBtn')) {
      return arguments['showBackBtn'] as bool;
    }
    return false;
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
        dPrint(formattedDate);
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
    dPrint("-----------User Details Chk--------");
    dPrint(user.toJson());
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
            orientation: o,
            onPressed: () {
              Get.offAndToNamed(AppRoutes.BOTTOM_NAV);
            },
            title: getLocalizedString(i18.profile.EDIT_PROFILE),
          ),
          body: SizedBox(
            height: Get.height,
            width: Get.width,
            child: !_authController.isValidUser
                ? loginRedirectPage()
                : SingleChildScrollView(
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: StreamBuilder(
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
                                  10.0,
                                  0.0,
                                  10.0,
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

  Widget _buildProfileForm(BuildContext context, User user, Orientation o) {
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
                      {
                    }
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
          textFormFieldNormal(
            context,
            getLocalizedString(i18.profile.NAME),
            controller: user.nameCtrl,
            fontSize: o == Orientation.portrait ? 14.sp : 7.sp,
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
            text: getLocalizedString(i18.profile.GENDER),
          ),
          dropDownButton<String>(
            context,
            hinText: getLocalizedString(i18.profile.GENDER),
            value: user.gender,
            radius: 5.r,
            onChanged: (val) =>
                _editProfileController.onChangeOfGender(val, user),
            items: maleItems.map((e) {
              return DropdownMenuItem(
                value: e,
                child: MediumTextNotoSans(
                  text: e,
                  size: o == Orientation.portrait ? 14.sp : 7.sp,
                ),
              );
            }).toList(),
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
            textInputAction: TextInputAction.done,
            fontSize: o == Orientation.portrait ? 14.sp : 7.sp,
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
              // if (!value.isValidDob()) {
              //   return 'Invalid Date of Birth';
              // }
              return null;
            },
          ),
          const SizedBox(height: 20),
          MediumTextNotoSans(
            fontWeight: FontWeight.w700,
            size: o == Orientation.portrait ? 16.sp : 8.sp,
            text: getLocalizedString(i18.profile.EMAIL),
          ),
          Obx(() {
            return TextFormField(
              controller: _editProfileController.user.emailCtrl,
              keyboardType: TextInputType.emailAddress,
              textInputAction: TextInputAction.done,
              decoration: InputDecoration(
                border: OutlineInputBorder(
                  borderSide: const BorderSide(width: 1.5),
                  borderRadius: BorderRadius.circular(8.r),
                ),
                suffixIcon: Icon(
                  _editProfileController.isValidEmail.value
                      ? Icons.check_circle
                      : Icons.cancel,
                  color: _editProfileController.isValidEmail.value
                      ? BaseConfig.statusGreenColor
                      : Colors.red,
                ),
              ),
              onChanged: (value) {
                _editProfileController.validateEmail(value);
              },
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter an email';
                } else if (!_editProfileController.isValidEmail.value) {
                  return 'Enter a valid email';
                }
                return null;
              },
            );
          }),
          const SizedBox(height: 40),
          FilledButtonApp(
            height: 55,
            width: Get.width,
            text: getLocalizedString(i18.common.SAVE),
            fontSize: o == Orientation.portrait ? 16.sp : 8.sp,
            onPressed: () => formValidator(
              context,
              user,
            ),
          ),
          const SizedBox(height: 60),
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
