import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:mobile_app/components/acknowlegement/acknowledgement.dart';
import 'package:mobile_app/components/bottom_sheet.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/text_form_field.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/grievance_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/controller/locality_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/localization/mdms_static_data.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/loaders.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/small_text.dart';

class NewGrievanceForm extends StatefulWidget {
  const NewGrievanceForm({super.key});

  @override
  State<NewGrievanceForm> createState() => _NewGrievanceFormState();
}

class _NewGrievanceFormState extends State<NewGrievanceForm> {
  final _authController = Get.find<AuthController>();
  final _fileController = Get.find<FileController>();
  final languageController = Get.find<LanguageController>();
  final localityController = Get.put(LocalityController());
  final _grievanceController = Get.find<GrievanceController>();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  List<(String?, VerificationDocumentPGR?, bool)> selectedDocuments = [];

  // List<PgrImage> selectedImages = [];
  var selectedGrievanceType = ''.obs;
  String selectedGrievancePriority = '';
  RxString selectedGrievanceCity = ''.obs,
      selectedGrievanceCityName = ''.obs,
      selectedGrievanceLocality = ''.obs,
      selectedGrievanceLocalityCode = ''.obs,
      selectedGrievanceSubType = ''.obs;
  RxList<String> grievanceSubtypes = <String>[].obs;
  TenantTenant? tenant;
  Rx<TenantTenant?> filteredPincodeTenant = Rx<TenantTenant?>(null);
  final TextEditingController pinCode = TextEditingController();
  final TextEditingController landmark = TextEditingController();
  final TextEditingController additionalDetails = TextEditingController();
  RainmakerPgr? rainmaker;

  var loading = false.obs,
      disableSubmit = false.obs,
      localityLoading = false.obs;
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    init();

    _scrollController.addListener(() {
      if (_scrollController.position.isScrollingNotifier.value) {
        FocusScope.of(context).unfocus();
      }
    });
  }

  @override
  void dispose() {
    pinCode.removeListener(_searchPinCode);
    pinCode.dispose();
    landmark.dispose();
    additionalDetails.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  init() async {
    loading.value = true;
    rainmaker = await _grievanceController.getMdmsPgr();

    selectedGrievanceType.value = '';
    selectedGrievanceSubType.value = '';
    selectedGrievancePriority = '';
    pinCode.clear();
    landmark.clear();

    additionalDetails.clear();
    filteredPincodeTenant.value = null;

    pinCode.addListener(_searchPinCode);
    loading.value = false;

    setState(() {});
  }

  _searchPinCode() async {
    final text = pinCode.text.trim();
    final code = int.tryParse(text);

    if (code == null || text.length != 6) {
      setDefault();
      return;
    }

    dPrint("Searching for pincode: $code");

    final tenant = languageController.mdmsResTenant.tenants
        ?.firstWhereOrNull((tenant) => tenant.pincode?.contains(code) ?? false);

    filteredPincodeTenant.value = tenant;

    if (tenant != null) {
      selectedGrievanceCity.value = tenant.code!;
      selectedGrievanceCityName.value = tenant.city!.name!;

      await localityController.fetchLocality(
        hierarchyTypeCode: 'ADMIN',
        tenantId: selectedGrievanceCity.value,
      );

      final boundaries = localityController.locality.value?.tenantBoundary;
      if (boundaries != null && boundaries.isNotEmpty) {
        final boundary = boundaries.first.boundary
            ?.firstWhereOrNull((b) => b.pinCode?.contains(code) ?? false);

        if (boundary != null) {
          selectedGrievanceLocality.value = boundary.name!;
          selectedGrievanceLocalityCode.value = boundary.code!;
          return;
        }
      }
    }

    setDefault();

    snackBar('Error', 'No matching pincode found.', BaseConfig.redColor);

    dPrint('No matching pincode found.');
  }

  setDefault() {
    selectedGrievanceCity.value = '';
    selectedGrievanceLocality.value = '';
    selectedGrievanceLocalityCode.value = '';
    filteredPincodeTenant.value = null;
  }

  _pickImage(int index, {required ImageSource source}) async {
    try {
      await _fileController.selectAndPickImage(imageSource: source);
      if (_fileController.imageFile == null) {
        dPrint("No file selected.");
        return;
      }

      setState(() {
        disableSubmit.value = true;
        selectedDocuments.add((null, null, true));
      });

      final fileType =
          _fileController.getFileTypeString(_fileController.imageFile!.path);

      final fileStoreId = await _fileController.postFile(
        token: _authController.token!.accessToken!,
        tenantId: tenant?.code ?? '',
        module: 'property-upload',
        customFileImage: _fileController.imageFile!.path,
        customFileName: _fileController.fileName,
      );

      if (fileStoreId == null) {
        setState(() {
          disableSubmit.value = false;
          selectedDocuments.removeLast();
        });
        return;
      }

      final newDoc = VerificationDocumentPGR(
        documentType: fileType.$2.name,
        fileStoreId: fileStoreId,
      );

      setState(() {
        selectedDocuments[selectedDocuments.length - 1] =
            (_fileController.imageFile!.path, newDoc, false);

        disableSubmit.value = false;
      });

      scrollToDown();

      dPrint("Updated Documents List: ${selectedDocuments.map(
            (doc) => (doc.$1, doc.$2?.toJson()),
          ).toList()}");
    } catch (e) {
      setState(() {
        selectedDocuments.removeLast();
        disableSubmit.value = false;
      });
      dPrint("Error in picking or uploading file: $e");
    }
  }

  void updateGrievanceType(String? type, List<ServiceDef> serviceDefs) {
    selectedGrievanceType.value = type ?? '';
    grievanceSubtypes.value = serviceDefs
        .where((e) {
          return e.menuPath == type;
        })
        .map((e) => e.serviceCode!)
        .toList();
  }

  scrollToDown() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 750),
          curve: Curves.easeOut,
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;

    final grievanceType = rainmaker?.serviceDefs
        ?.where(
          (e) => isNotNullOrEmpty(e.menuPath),
        )
        .map((e) => e.menuPath ?? '')
        .toSet()
        .toList();

    return Scaffold(
      resizeToAvoidBottomInset: true,
      appBar: HeaderTop(
        //orientation: o,
        titleWidget: Obx(
          () => loading.value
              ? const SizedBox.shrink()
              : Text(
                  getLocalizedString(
                    i18.grievance.NEW_COMPLAINT,
                    module: Modules.PGR,
                  ),
                ),
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      bottomNavigationBar: Obx(
        () => (loading.value || disableSubmit.value)
            ? const SizedBox.shrink()
            : Container(
                height: 44.h,
                width: Get.width,
                margin: EdgeInsets.all(o == Orientation.portrait ? 16.w : 12.w),
                child: FilledButtonApp(
                  text: getLocalizedString(
                    i18.grievance.SUBMIT_COMPLAINT,
                    module: Modules.PGR,
                  ),
                  onPressed: () async {
                    if (!_formKey.currentState!.validate()) {
                      return;
                    }

                    Loaders.showLoadingDialog(context, label: 'Processing...');

                    final pgr = await _grievanceController.submitGrievanceForm(
                      token: _authController.token!.accessToken.toString(),
                      grievanceSubType: selectedGrievanceSubType.value,
                      grievancePriority: selectedGrievancePriority,
                      landmark: landmark.text,
                      additionalDetails: additionalDetails.text,
                      localityCode: selectedGrievanceLocalityCode.value,
                      localityName: selectedGrievanceLocality.value,
                      city: selectedGrievanceCityName.value,
                      district: selectedGrievanceCityName.value,
                      region: selectedGrievanceCityName.value,
                      tenantId: selectedGrievanceCity.value,
                      pinCode: pinCode.text,
                      verificationDocuments:
                          selectedDocuments.map((doc) => doc.$2!).toList(),
                    );

                    Navigator.of(context).pop();

                    if (isNotNullOrEmpty(pgr?.service?.serviceRequestId)) {
                      Get.off(
                        () => AcknowledgementWidget(
                          headerText: getLocalizedString(
                            i18.grievance.COMPLAINT_SUBMITTED,
                            module: Modules.PGR,
                          ),
                          subText: getLocalizedString(
                            i18.grievance.COMPLAINT_NUMBER,
                            module: Modules.PGR,
                          ),
                          isPgr: true,
                          applicationNo: pgr!.service!.serviceRequestId!,
                          bottomText: getLocalizedString(
                            i18.grievance.COMPLAINT_TEXT,
                            module: Modules.PGR,
                          ),
                        ),
                      );
                    } else {
                      snackBar(
                        'Error',
                        'Failed to submit complaint',
                        BaseConfig.redColor,
                      );
                    }
                  },
                ),
              ),
      ),
      body: Container(
        height: Get.height,
        decoration: BoxDecoration(
          color: BaseConfig.mainBackgroundColor,
          borderRadius: BorderRadius.only(
            topLeft: Radius.circular(16.r),
            topRight: Radius.circular(16.r),
          ),
        ),
        child: Obx(
          () => loading.value
              ? showCircularIndicator()
              : SingleChildScrollView(
                  controller: _scrollController,
                  child: Padding(
                    padding:
                        EdgeInsets.symmetric(vertical: 20.h, horizontal: 16.w),
                    child: Form(
                      key: _formKey,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          SizedBox(height: 10.h),
                          BigTextNotoSans(
                            text: getLocalizedString(
                              i18.grievance.COMPLAINT_DETAILS,
                              module: Modules.PGR,
                            ),
                            fontWeight: FontWeight.w600,
                            color: BaseConfig.appThemeColor1,
                            size: 14.h,
                          ),
                          const SizedBox(height: 10),
                          Obx(
                            () => ColumnHeaderDropdownSearch(
                              label: getLocalizedString(
                                i18.grievance.COMPLAINT_TYPE,
                                module: Modules.PGR,
                              ),
                              module: Modules.PGR,
                              isServiceDef: true,
                              options: grievanceType ?? [],
                              selectedValue: selectedGrievanceType.value,
                              onChanged: (value) {
                                updateGrievanceType(
                                  value,
                                  rainmaker!.serviceDefs!,
                                );
                                dPrint(
                                  'Selected Grievance Type: $selectedGrievanceType',
                                );
                              },
                              textSize: 14.sp,
                              isRequired: true,
                              enableLocal: true,
                              validator: (val) {
                                if (!isNotNullOrEmpty(val)) {
                                  return 'Required Field';
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(height: 10),
                          Obx(
                            () => ColumnHeaderDropdownSearch(
                              label: getLocalizedString(
                                i18.grievance.COMPLAINT_SUBTYPE,
                                module: Modules.PGR,
                              ),
                              options: grievanceSubtypes,
                              selectedValue: selectedGrievanceSubType.value,
                              isServiceDef: true,
                              module: Modules.PGR,
                              onChanged: (value) {
                                selectedGrievanceSubType.value = value ?? '';
                                dPrint(
                                  'Selected Grievance Subtype: ${selectedGrievanceSubType.value}',
                                );
                              },
                              textSize: 14.sp,
                              isRequired: true,
                              enableLocal: true,
                              validator: (val) {
                                if (!isNotNullOrEmpty(val)) {
                                  return 'Required Field';
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(height: 10),
                          ColumnHeaderDropdownSearch(
                            label: getLocalizedString(
                              i18.grievance.COMPLAINT_PRIORITY_LEVEL,
                              module: Modules.PGR,
                            ),
                            options: const ['LOW', 'MEDIUM', 'HIGH'],
                            onChanged: (value) {
                              selectedGrievancePriority = value ?? '';
                              dPrint('value: $selectedGrievancePriority');
                            },
                            textSize: 14.sp,
                            isRequired: true,
                            enableLocal: true,
                            validator: (val) {
                              if (!isNotNullOrEmpty(val)) {
                                return 'Required Field';
                              }
                              return null;
                            },
                          ),
                          SizedBox(height: 20.h),
                          const Divider(),
                          SizedBox(height: 10.h),
                          BigTextNotoSans(
                            text: getLocalizedString(
                              i18.grievance.GRIEVANCE_LOCATION,
                              module: Modules.PGR,
                            ),
                            fontWeight: FontWeight.w600,
                            color: BaseConfig.appThemeColor1,
                            size: 14.h,
                          ),
                          const SizedBox(height: 10),
                          BigTextNotoSans(
                            text: getLocalizedString(
                              i18.common.PINCODE,
                            ),
                            fontWeight: FontWeight.w600,
                            // color: BaseConfig.appThemeColor1,
                            size: 13.h,
                          ),
                          const SizedBox(height: 5),
                          SizedBox(
                            height: 50,
                            child: Obx(
                              () => TextFormFieldApp(
                                radius: 8,
                                hintText: getLocalizedString(
                                  i18.common.PINCODE,
                                ),
                                keyboardType:
                                    AppPlatforms.platformKeyboardType(),
                                controller: pinCode,
                                maxLength: 6,
                                inputFormatters: [
                                  FilteringTextInputFormatter.digitsOnly,
                                  LengthLimitingTextInputFormatter(6),
                                ],
                                borderColor: filteredPincodeTenant.value != null
                                    ? Colors.green
                                    : BaseConfig.greyColor3,
                                suffixIcon: filteredPincodeTenant.value != null
                                    ? const Icon(
                                        Icons.check_circle,
                                        color: Colors.green,
                                      )
                                    : null,
                              ),
                            ),
                          ),
                          const SizedBox(height: 10),
                          Obx(
                            () => ColumnHeaderDropdownSearch(
                              label: getLocalizedString(
                                i18.grievance.GRIEVANCE_CITY,
                                module: Modules.PGR,
                              ),
                              selectedValue: selectedGrievanceCity.value,
                              enable: filteredPincodeTenant.value == null,
                              options: languageController.mdmsResTenant.tenants
                                      ?.map((tenant) => '${tenant.code}')
                                      .toList() ??
                                  [],
                              onChanged: (value) async {
                                selectedGrievanceCity.value = value!;
                                final tenant = languageController
                                    .mdmsResTenant.tenants!
                                    .firstWhereOrNull(
                                  (t) => t.code == value,
                                );

                                selectedGrievanceCityName.value =
                                    tenant?.city?.name ?? '';

                                dPrint('City: $selectedGrievanceCity');
                                dPrint(
                                  'CityName: $selectedGrievanceCityName',
                                );
                                if (filteredPincodeTenant.value != null ||
                                    isNotNullOrEmpty(
                                      selectedGrievanceCity.value,
                                    )) {
                                  localityLoading.value = true;
                                  await localityController.fetchLocality(
                                    hierarchyTypeCode: 'ADMIN',
                                    tenantId: selectedGrievanceCity.value,
                                  );
                                  localityLoading.value = false;
                                }
                              },
                              textSize: 14.sp,
                              isRequired: true,
                              enableLocal: true,
                              validator: (val) {
                                if (!isNotNullOrEmpty(val)) {
                                  return 'Required Field';
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(height: 10),
                          Obx(
                            () => localityLoading.value
                                ? showCircularIndicator()
                                : ColumnHeaderDropdownSearch(
                                    label: getLocalizedString(
                                      i18.grievance.MOHALLA,
                                      module: Modules.PGR,
                                    ),
                                    selectedValue:
                                        selectedGrievanceLocality.value,
                                    enable: filteredPincodeTenant.value == null,
                                    options: localityController
                                            .locality
                                            .value
                                            ?.tenantBoundary
                                            ?.firstOrNull
                                            ?.boundary
                                            ?.map((e) => e.name ?? '')
                                            .toList() ??
                                        [],
                                    onChanged: (value) {
                                      selectedGrievanceLocality.value =
                                          value ?? '';
                                      final boundariesList = localityController
                                          .locality.value?.tenantBoundary;
                                      final boundaryCd = boundariesList!
                                          .first.boundary!.first.code;
                                      selectedGrievanceLocalityCode.value =
                                          boundaryCd ?? "";
                                      dPrint(
                                        'Locality: ${selectedGrievanceLocality.value}',
                                      );
                                      dPrint(
                                        'LocalityCode: ${selectedGrievanceLocalityCode.value}',
                                      );
                                    },
                                    textSize: 14.sp,
                                    isRequired: true,
                                    enableLocal: true,
                                    validator: (val) {
                                      if (!isNotNullOrEmpty(val)) {
                                        return 'Required Field';
                                      }
                                      return null;
                                    },
                                  ),
                          ),
                          const SizedBox(height: 10),
                          BigTextNotoSans(
                            text: getLocalizedString(
                              i18.grievance.LANDMARK,
                              module: Modules.PGR,
                            ),
                            fontWeight: FontWeight.w600,
                            // color: BaseConfig.appThemeColor1,
                            size: 13.h,
                          ),
                          const SizedBox(height: 5),
                          SizedBox(
                            height: 50,
                            child: TextFormFieldApp(
                              borderColor: BaseConfig.greyColor3,
                              radius: 8,
                              hintText: getLocalizedString(
                                i18.grievance.LANDMARK,
                                module: Modules.PGR,
                              ),
                              controller: landmark,
                            ),
                          ),
                          SizedBox(height: 20.h),
                          const Divider(),
                          SizedBox(height: 10.h),
                          BigTextNotoSans(
                            text: getLocalizedString(
                              i18.grievance.PROVIDE_ADDITIONAL_DETAILS,
                              module: Modules.PGR,
                            ),
                            fontWeight: FontWeight.w600,
                            color: BaseConfig.appThemeColor1,
                            size: 14.h,
                          ),
                          const SizedBox(height: 10),
                          SmallTextNotoSans(
                            text: getLocalizedString(
                              i18.grievance.ADDITIONAL_DETAILS_TEXT,
                              module: Modules.PGR,
                            ),
                          ),
                          const SizedBox(height: 10),
                          TextFormFieldApp(
                            borderColor: BaseConfig.greyColor3,
                            radius: 8,
                            hintText: getLocalizedString(
                              i18.grievance.ADDITIONAL_DETAILS,
                              module: Modules.PGR,
                            ),
                            controller: additionalDetails,
                            maxLine: 5,
                          ),
                          SizedBox(height: 20.h),
                          const Divider(),
                          SizedBox(height: 10.h),
                          BigTextNotoSans(
                            text: getLocalizedString(
                              i18.grievance.EVIDENCE,
                              module: Modules.PGR,
                            ),
                            fontWeight: FontWeight.w600,
                            color: BaseConfig.appThemeColor1,
                            size: 14.h,
                          ),
                          const SizedBox(height: 10),
                          SmallTextNotoSans(
                            text: getLocalizedString(
                              i18.grievance.UPLOAD_PHOTO_TEXT,
                              module: Modules.PGR,
                            ),
                          ),
                          const SizedBox(height: 10),
                          SizedBox(
                            height: 100,
                            child: GridView.builder(
                              gridDelegate:
                                  const SliverGridDelegateWithFixedCrossAxisCount(
                                crossAxisCount: 5,
                                crossAxisSpacing: 8,
                                mainAxisSpacing: 8,
                              ),
                              itemCount: selectedDocuments.length + 1,
                              itemBuilder: (context, index) {
                                if (index == selectedDocuments.length) {
                                  return IconButton.filled(
                                    style: ButtonStyle(
                                      backgroundColor:
                                          const WidgetStatePropertyAll<Color>(
                                        BaseConfig.appThemeColor1,
                                      ),
                                      shape: WidgetStatePropertyAll<
                                          OutlinedBorder>(
                                        RoundedRectangleBorder(
                                          borderRadius:
                                              BorderRadius.circular(10.r),
                                        ),
                                      ),
                                    ),
                                    icon: const Icon(
                                      Icons.add,
                                      color: BaseConfig.mainBackgroundColor,
                                    ),
                                    onPressed: () {
                                      openBottomSheet(
                                        onTabImageGallery: () {
                                          _pickImage(
                                            index,
                                            source: ImageSource.gallery,
                                          );
                                        },
                                        onTabImageCamera: () {
                                          _pickImage(
                                            index,
                                            source: ImageSource.camera,
                                          );
                                        },
                                      );
                                    },
                                  );
                                }

                                final (filePath, document, isLoading) =
                                    selectedDocuments[index];

                                return Stack(
                                  fit: StackFit.expand,
                                  children: [
                                    if (isLoading)
                                      showCircularIndicator()
                                    else
                                      ClipRRect(
                                        borderRadius:
                                            BorderRadius.circular(10.r),
                                        child: Image.file(
                                          File(filePath!),
                                          fit: BoxFit.cover,
                                        ),
                                      ),
                                    if (!isLoading)
                                      Positioned(
                                        top: 4,
                                        right: 4,
                                        child: GestureDetector(
                                          onTap: () {
                                            setState(() {
                                              selectedDocuments.removeAt(index);
                                            });
                                          },
                                          child: Container(
                                            decoration: const BoxDecoration(
                                              color: BaseConfig.appThemeColor1,
                                              shape: BoxShape.circle,
                                            ),
                                            padding: const EdgeInsets.all(4),
                                            child: const Icon(
                                              Icons.close,
                                              color: Colors.white,
                                              size: 16,
                                            ),
                                          ),
                                        ),
                                      ),
                                  ],
                                );
                              },
                            ),
                          ),
                          const SizedBox(height: 10),
                        ],
                      ),
                    ),
                  ),
                ),
        ),
      ),
    );
  }
}

class VerificationDocumentPGR {
  final String documentType;
  final String fileStoreId;
  final String documentUid;
  final Map<String, dynamic> additionalDetails;

  VerificationDocumentPGR({
    required this.documentType,
    required this.fileStoreId,
    this.documentUid = "",
    this.additionalDetails = const {},
  });

  factory VerificationDocumentPGR.fromJson(Map<String, dynamic> json) {
    return VerificationDocumentPGR(
      documentType: json['documentType'] ?? '',
      fileStoreId: json['fileStoreId'] ?? '',
      documentUid: json['documentUid'] ?? '',
      additionalDetails: json['additionalDetails'] ?? {},
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'documentType': documentType,
      'fileStoreId': fileStoreId,
      'documentUid': documentUid,
      'additionalDetails': additionalDetails,
    };
  }
}

class PgrImage {
  String? fileType;
  String? filePath;
  String? fileName;
  bool isLoading;
  PgrImage({
    this.fileType,
    this.filePath,
    this.fileName,
    this.isLoading = false,
  });
}
