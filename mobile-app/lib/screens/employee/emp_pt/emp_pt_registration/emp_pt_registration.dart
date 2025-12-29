import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:mobile_app/components/acknowlegement/emp_acknowledge_screen.dart';
import 'package:mobile_app/components/bottom_sheet.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/text_form_field.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/controller/locality_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/request/emp_property_request/property_request_model.dart';
import 'package:mobile_app/screens/citizen/grievance_redressal/new_grievance_form/new_grievance_form.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/loaders.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/icon_text_fill_button.dart';
import 'package:mobile_app/widgets/required_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class EmpPtRegistration extends StatefulWidget {
  const EmpPtRegistration({super.key});

  @override
  State<EmpPtRegistration> createState() => _EmpPtRegistrationState();
}

class _EmpPtRegistrationState extends State<EmpPtRegistration> {
  final _authController = Get.find<AuthController>();
  final localityController = Get.put(LocalityController());
  final languageController = Get.find<LanguageController>();
  final _fileController = Get.find<FileController>();
  final _ptController = Get.find<PropertiesTaxController>();
  final _commonController = Get.find<CommonController>();
  final ScrollController _scrollController = ScrollController();

  final GlobalKey<FormState> _formKeyPt = GlobalKey<FormState>();
  final TextEditingController pinCode = TextEditingController();
  final TextEditingController streetName = TextEditingController();
  final TextEditingController houseNo = TextEditingController();
  final TextEditingController areaSqFt = TextEditingController();
  final TextEditingController electricityId = TextEditingController();
  final TextEditingController uniqueId = TextEditingController();
  final List<PropertyTypeFormWidget> _propertyTypeFormWidget = [];
  final List<PropertyTypeOwnerFormWidget> _propertyTypeOwnerFormWidget = [];
  final List<TextEditingController> _builtUpSpaceControllers = [];
  final List<TextEditingController> _rentINRController = [];
  final List<TextEditingController> _ownerNameControllers = [];
  final List<TextEditingController> _mobileNumberControllers = [];
  final List<TextEditingController> _guardianNameControllers = [];
  final List<TextEditingController> _emailControllers = [];
  final List<TextEditingController> _documentIdControllers = [];
  final List<TextEditingController> _ownerAddressControllers = [];

  List<PgrImage> usageProofImages = [];
  List<PgrImage> occupancyProofImages = [];
  List<PgrImage> constructionProofImages = [];
  List<PgrImage> registrationProofImages = [];
  List<PgrImage> identityProofImages = [];
  List<PgrImage> addressProofImages = [];
  TenantTenant? tenantCity;
  Rx<TenantTenant?> filteredPincodeTenant = Rx<TenantTenant?>(null);
  Rx<String> selectUse = ''.obs,
      usesCategory = ''.obs,
      usesCategoryMajor = ''.obs,
      selectPropertyType = ''.obs,
      selectStructureType = ''.obs,
      ageOfProperty = ''.obs,
      ageOfPropertyName = ''.obs,
      ageOfPropertyCode = ''.obs,
      ownershipDetail = ''.obs,
      ownershipCategory = ''.obs,
      usageProof = ''.obs,
      occupancyProof = ''.obs,
      constructionProof = ''.obs,
      registrationProof = ''.obs,
      identityProof = ''.obs,
      addressProof = ''.obs,
      floorSelectedErrorMessages = ''.obs;

  RxString selectedPtCityCode = ''.obs,
      selectedPtCityName = ''.obs,
      selectedPtAreaName = ''.obs,
      selectedPtLocalityName = ''.obs,
      selectedPtLocalityCode = ''.obs;

  var isLoading = false.obs,
      disableSubmit = false.obs,
      localityLoading = false.obs;

  final documents = <DocumentRequest>[];

  @override
  void initState() {
    super.initState();
    clearAll();
    init();

    _scrollController.addListener(() {
      if (_scrollController.position.isScrollingNotifier.value) {
        FocusScope.of(context).unfocus();
      }
    });
  }

  @override
  void dispose() {
    pinCode.dispose();
    streetName.dispose();
    houseNo.dispose();
    areaSqFt.dispose();
    electricityId.dispose();
    uniqueId.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  init() async {
    isLoading.value = true;
    await _initModule();
    await _ptController.getEmpMdmsPTForm();
    pinCode.addListener(_searchPinCode);
    addMoreWidget();
    addMoreOwnerWidget();
    isLoading.value = false;
  }

  Future<void> _initModule() async {
    tenantCity = await getCityTenant();
    await _commonController.fetchLabels(modules: Modules.PT);
  }

  void clearAll() {
    selectUse.value = '';
    usesCategory.value = '';
    usesCategoryMajor.value = '';
    selectPropertyType.value = '';
    selectStructureType.value = '';
    ageOfProperty.value = '';
    ownershipDetail.value = '';
    ownershipCategory.value = '';
    usageProof.value = '';
    occupancyProof.value = '';
    constructionProof.value = '';
    registrationProof.value = '';
    identityProof.value = '';
    addressProof.value = '';

    floorSelectedErrorMessages.value = '';

    authorizedInstitutionName.clear();
    authorizedOwnerName.clear();
    authorizedLandlineNumber.clear();
    authorizedMobileNumber.clear();
    authorizedDesignation.clear();
    authorizedEmail.clear();
    authorizedOwnerAddress.clear();
    authorizedInstitutionTypeValue = '';

    pinCode.clear();
    streetName.clear();
    houseNo.clear();
    areaSqFt.clear();
    electricityId.clear();
    uniqueId.clear();
    for (var element in _builtUpSpaceControllers) {
      element.clear();
    }
    for (var element in _rentINRController) {
      element.clear();
    }
    for (var element in _ownerNameControllers) {
      element.clear();
    }
    for (var element in _mobileNumberControllers) {
      element.clear();
    }
    for (var element in _guardianNameControllers) {
      element.clear();
    }
    for (var element in _emailControllers) {
      element.clear();
    }
    for (var element in _documentIdControllers) {
      element.clear();
    }
    for (var element in _ownerAddressControllers) {
      element.clear();
    }
    usageProofImages.clear();
    occupancyProofImages.clear();
    constructionProofImages.clear();
    registrationProofImages.clear();
    identityProofImages.clear();
    addressProofImages.clear();
    _propertyTypeFormWidget.clear();
    _propertyTypeOwnerFormWidget.clear();
    setDefault();
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
      selectedPtCityCode.value = tenant.code!;
      selectedPtCityName.value = tenant.city!.name!;

      await localityController.fetchLocality(
        hierarchyTypeCode: 'ADMIN',
        tenantId: selectedPtCityCode.value,
      );

      final boundaries = localityController.locality.value?.tenantBoundary;
      if (boundaries != null && boundaries.isNotEmpty) {
        final boundary = boundaries.first.boundary
            ?.firstWhereOrNull((b) => b.pinCode?.contains(code) ?? false);

        if (boundary != null) {
          selectedPtLocalityName.value = boundary.name!;
          selectedPtLocalityCode.value = boundary.code!;
          selectedPtAreaName.value = boundary.area!;
          return;
        }
      }
    }

    setDefault();

    snackBar('Error', 'No matching pincode found.', BaseConfig.redColor);

    dPrint('No matching pincode found.');
  }

  setDefault() {
    selectedPtCityName.value = '';
    selectedPtLocalityName.value = '';
    selectedPtLocalityCode.value = '';
    filteredPincodeTenant.value = null;
  }

  void addMoreWidget() {
    setState(() {
      TextEditingController newController = TextEditingController();
      _builtUpSpaceControllers.add(newController);
      TextEditingController newControllerINR = TextEditingController();
      _rentINRController.add(newController);
      _propertyTypeFormWidget.add(
        PropertyTypeFormWidget(
          index: _propertyTypeFormWidget.length,
          builtUpSpace: newController,
          occupancy: (value) => onOccupancy(0, value!),
          provideUsageType: (value) => onProvideUsageType(0, value!),
          unitUsageType: (value) => onUnitUsageType(0, value!),
          selectedFloor: (value) => onSelectedFloor(0, value!),
          widgetLength: _propertyTypeFormWidget.length + 1,
          onRemove: () => _removeWidget(_propertyTypeFormWidget.length),
          totalRentedMonths: (value) => onTotalRentedMonths(0, value!),
          rentINR: newControllerINR,
          remainingMonthOccupancy: (value) =>
              onRemainingMonthOccupancy(0, value!),
        ),
      );
    });
  }

  void addMoreOwnerWidget() {
    setState(() {
      TextEditingController newOwnerNameController = TextEditingController();
      _ownerNameControllers.add(newOwnerNameController);
      TextEditingController newMobileNumberController = TextEditingController();
      _mobileNumberControllers.add(newMobileNumberController);
      TextEditingController newguardiansNameController =
          TextEditingController();
      _guardianNameControllers.add(newguardiansNameController);
      TextEditingController newEmailController = TextEditingController();
      _emailControllers.add(newEmailController);
      TextEditingController newOwnerDocumentIdController =
          TextEditingController();
      _documentIdControllers.add(newOwnerDocumentIdController);
      TextEditingController newOwnerAddressController = TextEditingController();
      _ownerNameControllers.add(newOwnerAddressController);
      _propertyTypeOwnerFormWidget.add(
        PropertyTypeOwnerFormWidget(
          index: _propertyTypeOwnerFormWidget.length,
          gender: (value) => onGender(0, value!),
          relationShip: (value) => onRelationShip(0, value!),
          specialCategory: (value) => onSpecialCategory(0, value!),
          specialCategoryDocumentType: (value) =>
              onSpecialCategoryDocumentTypeValue(0, value!),
          ownerName: newOwnerNameController,
          mobileNumber: newMobileNumberController,
          guardiansName: newguardiansNameController,
          email: newEmailController,
          ownerDocumentId: newOwnerDocumentIdController,
          ownerAddress: newOwnerAddressController,
          widgetLength: _propertyTypeOwnerFormWidget.length + 1,
          onRemove: () =>
              _removeOwnerWidget(_propertyTypeOwnerFormWidget.length),
        ),
      );
    });
  }

  void onProvideUsageType(int index, String value) {
    setState(() {
      _propertyTypeFormWidget[index].propertyUsesType = value;
      dPrint(_propertyTypeFormWidget[index].propertyUsesType);
    });
  }

  void onUnitUsageType(int index, String value) {
    dPrint('unitUsesCat: $value');
    setState(() {
      _propertyTypeFormWidget[index].unitUsageTypeValue = value;
    });
  }

  void onTotalRentedMonths(int index, String value) {
    setState(() {
      _propertyTypeFormWidget[index].totalRentedMonthsValue = value;
      dPrint(_propertyTypeFormWidget[index].totalRentedMonthsValue);
    });
  }

  void onRemainingMonthOccupancy(int index, String value) {
    setState(() {
      _propertyTypeFormWidget[index].remainingMonthOccupancyValue = value;
      dPrint(_propertyTypeFormWidget[index].remainingMonthOccupancyValue);
    });
  }

  void onSelectedFloor(int index, String value) {
    setState(() {
      _propertyTypeFormWidget[index].selectedFloorValue = value;
      dPrint(_propertyTypeFormWidget[index].selectedFloorValue);
    });
    validateUnitFloors();
  }

  void onOccupancy(int index, String value) {
    setState(() {
      _propertyTypeFormWidget[index].occupancyValue = value;
      dPrint(_propertyTypeFormWidget[index].occupancyValue);
    });
  }

  void onRelationShip(int index, String value) {
    setState(() {
      _propertyTypeOwnerFormWidget[index].relationShipValue = value;
      dPrint(_propertyTypeOwnerFormWidget[index].relationShipValue);
    });
  }

  void onSpecialCategory(int index, String value) {
    setState(() {
      _propertyTypeOwnerFormWidget[index].specialCategoryValue = value;
      dPrint(_propertyTypeOwnerFormWidget[index].specialCategoryValue);
    });
  }

  void onSpecialCategoryDocumentTypeValue(int index, String value) {
    setState(() {
      _propertyTypeOwnerFormWidget[index].specialCategoryDocumentTypeValue =
          value;
      dPrint(
        _propertyTypeOwnerFormWidget[index].specialCategoryDocumentTypeValue,
      );
    });
  }

  void onGender(int index, String value) {
    setState(() {
      _propertyTypeOwnerFormWidget[index].selectedGenderValue = value;
      dPrint(_propertyTypeOwnerFormWidget[index].selectedGenderValue);
    });
  }

  void _removeOwnerWidget(int index) {
    setState(() {
      if (_propertyTypeOwnerFormWidget.isNotEmpty) {
        _propertyTypeOwnerFormWidget.removeAt(index);
        dPrint("Removed widget at index: $index");
      }
    });
  }

  void _removeWidget(int index) {
    setState(() {
      if (_propertyTypeFormWidget.isNotEmpty) {
        _propertyTypeFormWidget.removeAt(index);
        dPrint("Removed widget at index: $index");
      }
    });
    validateUnitFloors();
  }

  _pickImageUsageProof(int index, {required ImageSource source}) async {
    try {
      await _fileController.selectAndPickImage(imageSource: source);

      if (_fileController.imageFile == null) return;

      setState(() {
        disableSubmit.value = true;
        usageProofImages.add(
          PgrImage(
            fileType: null,
            filePath: null,
            fileName: null,
            isLoading: true,
          ),
        );
      });

      final fileType =
          _fileController.getFileTypeString(_fileController.imageFile!.path);

      final fileStoreId = await _fileController.postFile(
        token: _authController.token!.accessToken!,
        tenantId: tenantCity?.code ?? '',
        module: 'property-upload',
        customFileImage: _fileController.imageFile!.path,
        customFileName: _fileController.fileName,
      );

      if (fileStoreId == null) {
        setState(() {
          disableSubmit.value = false;
          usageProofImages.removeLast();
        });
        return;
      }

      documents.add(
        DocumentRequest()
          ..documentType = usageProof.value
          ..fileStoreId = fileStoreId
          ..documentUid = fileStoreId,
      );

      setState(() {
        usageProofImages[usageProofImages.length - 1] = PgrImage(
          fileType: fileType.$2.name,
          filePath: _fileController.imageFile!.path,
          fileName: _fileController.fileName,
          isLoading: false,
        );
        disableSubmit.value = false;
      });

      dPrint("Updated Usage Proof List: ${usageProofImages.map(
            (img) => (img.filePath, img.fileType, img.fileName),
          ).toList()}");
    } catch (e) {
      setState(() {
        disableSubmit.value = false;
        usageProofImages.removeLast();
      });
      dPrint("Error in picking or uploading file: $e");
    }
  }

  _pickImageOccupancyProof(int index, {required ImageSource source}) async {
    try {
      await _fileController.selectAndPickImage(imageSource: source);

      if (_fileController.imageFile == null) return;

      setState(() {
        disableSubmit.value = true;
        occupancyProofImages.add(
          PgrImage(
            fileType: null,
            filePath: null,
            fileName: null,
            isLoading: true,
          ),
        );
      });

      final fileType =
          _fileController.getFileTypeString(_fileController.imageFile!.path);

      final fileStoreId = await _fileController.postFile(
        token: _authController.token!.accessToken!,
        tenantId: tenantCity?.code ?? '',
        module: 'property-upload',
        customFileImage: _fileController.imageFile!.path,
        customFileName: _fileController.fileName,
      );

      if (fileStoreId == null) {
        setState(() {
          disableSubmit.value = false;
          occupancyProofImages.removeLast();
        });
        return;
      }

      documents.add(
        DocumentRequest()
          ..documentType = occupancyProof.value
          ..fileStoreId = fileStoreId
          ..documentUid = fileStoreId,
      );

      setState(() {
        occupancyProofImages[occupancyProofImages.length - 1] = PgrImage(
          fileType: fileType.$2.name,
          filePath: _fileController.imageFile!.path,
          fileName: _fileController.fileName,
          isLoading: false,
        );

        disableSubmit.value = false;
      });
    } catch (e) {
      dPrint("Error in picking or uploading file: $e");
      setState(() {
        disableSubmit.value = false;
        occupancyProofImages.removeLast();
      });
    }
  }

  _pickImageConstructionProof(int index, {required ImageSource source}) async {
    try {
      await _fileController.selectAndPickImage(imageSource: source);

      if (_fileController.imageFile == null) return;

      setState(() {
        disableSubmit.value = true;
        constructionProofImages.add(
          PgrImage(
            fileType: null,
            filePath: null,
            fileName: null,
            isLoading: true,
          ),
        );
      });

      final fileType =
          _fileController.getFileTypeString(_fileController.imageFile!.path);

      final fileStoreId = await _fileController.postFile(
        token: _authController.token!.accessToken!,
        tenantId: tenantCity?.code ?? '',
        module: 'property-upload',
        customFileImage: _fileController.imageFile!.path,
        customFileName: _fileController.fileName,
      );

      if (fileStoreId == null) {
        setState(() {
          disableSubmit.value = false;
          constructionProofImages.removeLast();
        });
        return;
      }

      documents.add(
        DocumentRequest()
          ..documentType = constructionProof.value
          ..fileStoreId = fileStoreId
          ..documentUid = fileStoreId,
      );

      setState(() {
        constructionProofImages[constructionProofImages.length - 1] = PgrImage(
          fileType: fileType.$2.name,
          filePath: _fileController.imageFile!.path,
          fileName: _fileController.fileName,
          isLoading: false,
        );

        disableSubmit.value = false;
      });
    } catch (e) {
      setState(() {
        disableSubmit.value = false;
        constructionProofImages.removeLast();
      });
      dPrint("Error in picking or uploading file: $e");
    }
  }

  _pickImageRegistrationProof(int index, {required ImageSource source}) async {
    try {
      await _fileController.selectAndPickImage(imageSource: source);

      if (_fileController.imageFile == null) return;

      setState(() {
        disableSubmit.value = true;
        registrationProofImages.add(
          PgrImage(
            fileType: null,
            filePath: null,
            fileName: null,
            isLoading: true,
          ),
        );
      });

      final fileType =
          _fileController.getFileTypeString(_fileController.imageFile!.path);

      final fileStoreId = await _fileController.postFile(
        token: _authController.token!.accessToken!,
        tenantId: tenantCity?.code ?? '',
        module: 'property-upload',
        customFileImage: _fileController.imageFile!.path,
        customFileName: _fileController.fileName,
      );

      if (fileStoreId == null) {
        setState(() {
          disableSubmit.value = false;
          registrationProofImages.removeLast();
        });
        return;
      }

      documents.add(
        DocumentRequest()
          ..documentType = registrationProof.value
          ..fileStoreId = fileStoreId
          ..documentUid = fileStoreId,
      );

      setState(() {
        registrationProofImages[registrationProofImages.length - 1] = PgrImage(
          fileType: fileType.$2.name,
          filePath: _fileController.imageFile!.path,
          fileName: _fileController.fileName,
          isLoading: false,
        );

        disableSubmit.value = false;
      });
    } catch (e) {
      dPrint("Error in picking or uploading file: $e");
      setState(() {
        disableSubmit.value = false;
        registrationProofImages.removeLast();
      });
    }
  }

  _pickImageIdentityProof(int index, {required ImageSource source}) async {
    try {
      await _fileController.selectAndPickImage(imageSource: source);

      if (_fileController.imageFile == null) return;

      setState(() {
        disableSubmit.value = true;
        identityProofImages.add(
          PgrImage(
            fileType: null,
            filePath: null,
            fileName: null,
            isLoading: true,
          ),
        );
      });

      final fileType =
          _fileController.getFileTypeString(_fileController.imageFile!.path);

      final fileStoreId = await _fileController.postFile(
        token: _authController.token!.accessToken!,
        tenantId: tenantCity?.code ?? '',
        module: 'property-upload',
        customFileImage: _fileController.imageFile!.path,
        customFileName: _fileController.fileName,
      );

      if (fileStoreId == null) {
        setState(() {
          disableSubmit.value = false;
          identityProofImages.removeLast();
        });
        return;
      }

      documents.add(
        DocumentRequest()
          ..documentType = identityProof.value
          ..fileStoreId = fileStoreId
          ..documentUid = fileStoreId,
      );

      setState(() {
        identityProofImages[identityProofImages.length - 1] = PgrImage(
          fileType: fileType.$2.name,
          filePath: _fileController.imageFile!.path,
          fileName: _fileController.fileName,
          isLoading: false,
        );

        disableSubmit.value = false;
      });
    } catch (e) {
      dPrint("Error in picking or uploading file: $e");
      setState(() {
        disableSubmit.value = false;
        identityProofImages.removeLast();
      });
    }
  }

  _pickImageAddressProof(int index, {required ImageSource source}) async {
    try {
      await _fileController.selectAndPickImage(imageSource: source);
      if (_fileController.imageFile == null) return;

      setState(() {
        disableSubmit.value = true;
        addressProofImages.add(
          PgrImage(
            fileType: null,
            filePath: null,
            fileName: null,
            isLoading: true,
          ),
        );
      });

      final fileType =
          _fileController.getFileTypeString(_fileController.imageFile!.path);

      final fileStoreId = await _fileController.postFile(
        token: _authController.token!.accessToken!,
        tenantId: tenantCity?.code ?? '',
        module: 'property-upload',
        customFileImage: _fileController.imageFile!.path,
        customFileName: _fileController.fileName,
      );

      if (fileStoreId == null) {
        setState(() {
          disableSubmit.value = false;
          addressProofImages.removeLast();
        });
        return;
      }

      documents.add(
        DocumentRequest()
          ..documentType = addressProof.value
          ..fileStoreId = fileStoreId
          ..documentUid = fileStoreId,
      );

      setState(() {
        addressProofImages[addressProofImages.length - 1] = PgrImage(
          fileType: fileType.$2.name,
          filePath: _fileController.imageFile!.path,
          fileName: _fileController.fileName,
          isLoading: false,
        );

        disableSubmit.value = false;
      });
    } catch (e) {
      dPrint("Error in picking or uploading file: $e");
      setState(() {
        disableSubmit.value = false;
        addressProofImages.removeLast();
      });
    }
  }

  //authorized Person Details
  TextEditingController authorizedInstitutionName = TextEditingController();
  TextEditingController authorizedOwnerName = TextEditingController();
  TextEditingController authorizedLandlineNumber = TextEditingController();
  TextEditingController authorizedMobileNumber = TextEditingController();
  TextEditingController authorizedDesignation = TextEditingController();
  TextEditingController authorizedEmail = TextEditingController();
  TextEditingController authorizedOwnerAddress = TextEditingController();

  String authorizedInstitutionTypeValue = '';

  void onAuthorizedInstitutionType(int index, String value) {
    setState(() {
      authorizedInstitutionTypeValue = value;
      dPrint(authorizedInstitutionTypeValue);
    });
  }

  //Submit all value
  Future<void> submitAllSelectedValues() async {
    try {
      Loaders.showLoadingDialog(context, label: 'Processing...');

      final property = PropertyRequest();

      final locality = LocalityRequest()
        ..code = selectedPtLocalityCode.value
        ..area = selectedPtAreaName.value;

      final address = AddressRequest()
        ..city = selectedPtCityName.value
        ..locality = locality;

      if (isNotNullOrEmpty(streetName.text)) {
        address.street = streetName.text;
      }
      if (isNotNullOrEmpty(houseNo.text)) {
        address.doorNo = houseNo.text;
      }
      if (isNotNullOrEmpty(pinCode.text)) {
        address.pincode = pinCode.text;
      }

      final ageOfP = AgeOfPropertyRequest()
        ..active = true
        ..i18NKey = ageOfProperty.value
        ..code = ageOfPropertyCode.value
        ..name = ageOfPropertyName.value;

      final structureTypeName = getLocalizedString(
        selectStructureType.value,
        module: Modules.PT,
      );

      final structureType = AgeOfPropertyRequest()
        ..active = true
        ..i18NKey = selectStructureType.value
        ..code = structureTypeName.toLowerCase()
        ..name = structureTypeName;

      final ownersAd = <OwnerRequest>[];
      final owners = <OwnerRequest>[];

      if (isNotNullOrEmpty(owners) && isNotNullOrEmpty(ownersAd)) {
        ownersAd.clear();
        owners.clear();
      }

      if (ownershipDetail.value != "INSTITUTIONALGOVERNMENT" &&
          ownershipDetail.value != "INSTITUTIONALPRIVATE") {
        for (int j = 0; j < _propertyTypeOwnerFormWidget.length; j++) {
          final ownerForm = _propertyTypeOwnerFormWidget[j];
          final ownerAdditionalAd = OwnerAdditionalDetailsRequest()
            ..ownerName = ownerForm.ownerName.text
            ..ownerSequence = j;

          final owner = OwnerRequest()
            ..name = ownerForm.ownerName.text
            ..gender = ownerForm.selectedGenderValue
            ..mobileNumber = ownerForm.mobileNumber.text
            ..fatherOrHusbandName = ownerForm.guardiansName.text
            ..relationship = ownerForm.relationShipValue.split('_').last
            ..ownerType = ownerForm.specialCategoryValue
            ..emailId = ownerForm.email.text
            ..correspondenceAddress = ownerForm.ownerAddress.text
            ..additionalDetails = ownerAdditionalAd;

          ownersAd.add(owner);
          owners.add(owner);
        }
      }

      final units = <PropertyRequestUnit>[];
      final additionalUnitsReq = <AdditionalDetailsUnitRequest>[];

      if (isNotNullOrEmpty(units)) {
        units.clear();
        additionalUnitsReq.clear();
      }
      for (int i = 0; i < _propertyTypeFormWidget.length; i++) {
        final propertyForm = _propertyTypeFormWidget[i];
        final unitUsesCat = propertyForm.unitUsageTypeValue
            .split('COMMON_PROPSUBUSGTYPE_')
            .last;
        dPrint('unitUsesCat: $unitUsesCat');

        final constructionDetail = ConstructionDetailRequest()
          ..builtUpArea = propertyForm.builtUpSpace.text;

        final pUnit = PropertyRequestUnit()
          ..occupancyType = propertyForm.occupancyValue
          ..floorNo = propertyForm.selectedFloorValue
          ..tenantId = selectedPtCityCode.value
          ..constructionDetail = constructionDetail
          ..usageCategory = selectUse.value == 'RESIDENTIAL'
              ? selectUse.value
              : unitUsesCat.replaceAll('_', '.');

        if (isNotNullOrEmpty(propertyForm.rentINR.text)) {
          pUnit.arv = propertyForm.rentINR.text;
        }

        RegExp regExp = RegExp(r'\d+$');
        Match? match = regExp.firstMatch(propertyForm.totalRentedMonthsValue);

        final nonRented = propertyForm.remainingMonthOccupancyValue ==
                'NON_RENT_SELFOCCUPIED'
            ? 'NonRentSelfOccupied'
            : propertyForm.remainingMonthOccupancyValue == 'NON_RENT_UNOCCUPIED'
                ? 'NonRentUnOccupied'
                : null;

        dPrint('RentedMonths: ${match?.group(0)}');

        final additionalUnits = AdditionalDetailsUnitRequest()
          ..floorNo = null
          ..nonRentedMonthsUsage =
              isNotNullOrEmpty(nonRented) ? nonRented! : null
          ..rentedMonths = isNotNullOrEmpty(match) ? match!.group(0)! : null;

        additionalUnitsReq.add(additionalUnits);
        units.add(pUnit);
      }

      if (ownershipDetail.value == "INSTITUTIONALGOVERNMENT") {
        property.institution = InstitutionRequest()
          ..name = authorizedInstitutionName.text
          ..type = authorizedInstitutionTypeValue
          ..designation = authorizedDesignation.text
          ..nameOfAuthorizedPerson = authorizedOwnerName.text
          ..tenantId = selectedPtCityCode.value;

        final ownerAdIt = OwnerAdditionalDetailsRequest()
          ..ownerName = authorizedOwnerName.text
          ..ownerSequence = 0;

        final ownerIt = OwnerRequest()
          ..name = authorizedOwnerName.text
          ..emailId = authorizedEmail.text
          ..mobileNumber = authorizedMobileNumber.text
          ..designation = authorizedDesignation.text
          ..ownerType = 'NONE'
          ..altContactNumber = authorizedLandlineNumber.text
          ..additionalDetails = ownerAdIt
          ..isCorrespondenceAddress = false
          ..correspondenceAddress = authorizedOwnerAddress.text;

        owners.add(ownerIt);
        ownersAd.add(ownerIt);
      }

      if (ownershipDetail.value == "INSTITUTIONALPRIVATE") {
        property.institution = InstitutionRequest()
          ..name = authorizedInstitutionName.text
          ..type = authorizedInstitutionTypeValue
          ..designation = authorizedDesignation.text
          ..nameOfAuthorizedPerson = authorizedOwnerName.text
          ..tenantId = selectedPtCityCode.value;

        final ownerAdIt = OwnerAdditionalDetailsRequest()
          ..ownerName = authorizedOwnerName.text
          ..ownerSequence = 0;

        final ownerIt = OwnerRequest()
          ..name = authorizedOwnerName.text
          ..emailId = authorizedEmail.text
          ..designation = authorizedDesignation.text
          ..ownerType = 'NONE'
          ..mobileNumber = authorizedMobileNumber.text
          ..altContactNumber = authorizedLandlineNumber.text
          ..additionalDetails = ownerAdIt
          ..isCorrespondenceAddress = false
          ..correspondenceAddress = authorizedOwnerAddress.text;

        owners.add(ownerIt);
        ownersAd.add(ownerIt);
      }

      final additionalDetails = PropertyRequestAdditionalDetails()
        ..primaryOwner = owners.first.name
        ..unit = additionalUnitsReq
        ..ageOfProperty = ageOfP
        ..structureType = structureType
        ..electricity = electricityId.text
        ..uid = uniqueId.text
        ..owners = ownersAd;

      property
        ..tenantId = selectedPtCityCode.value
        ..address = address
        ..usageCategoryMinor =
            selectUse.value == 'RESIDENTIAL' ? null : selectUse.value
        ..usageCategoryMajor = usesCategoryMajor.value
        ..usageCategory = usesCategory.value
        ..landArea = int.parse(areaSqFt.text)
        ..superBuiltUpArea = int.parse(areaSqFt.text)
        ..propertyType = selectPropertyType.value.replaceAll('_', '.')
        ..noOfFloors = _propertyTypeFormWidget.length
        ..ownershipCategory = ownershipCategory.value
        ..additionalDetails = additionalDetails
        ..documents = documents
        ..owners = owners
        ..units = selectPropertyType.value != 'VACANT' ? units : []
        ..channel = 'CFC_COUNTER'
        ..creationReason = 'CREATE'
        ..source = 'MUNICIPAL_RECORDS'
        ..applicationStatus = 'CREATE';

      final pRes = await _ptController.createNewPTApplicationEmp(
        token: _authController.token!.accessToken!,
        property: property,
      );

      Navigator.of(context).pop();

      if (pRes != null) {
        clearAll();

        Get.off(
          () => EmpAcknowledgeScreen(
            applicationNo: pRes.acknowledgementNumber ?? '',
            mainTitle: getLocalizedString(
              i18.propertyTax.EMP_RESPONSE_ACTION,
              module: Modules.PT,
            ),
            appIdName: getLocalizedString(
              i18.propertyTax.EMP_RESPONSE_LABEL,
              module: Modules.PT,
            ),
            message: getLocalizedString(
              i18.propertyTax.EMP_RESPONSE_CREATE_DISPLAY,
              module: Modules.PT,
            ),
          ),
        );
      }
    } catch (e) {
      snackBar('Error', e.toString(), BaseConfig.redColor);
    }
  }

  /// Unit floor selection validation
  void validateUnitFloors() {
    List<int> missingFloors = [];

    final selectedFloors = _propertyTypeFormWidget
        .map((unit) => unit.selectedFloorValue)
        .where((floor) => isNotNullOrEmpty(floor))
        .map((floor) => int.parse(floor))
        .toList();

    final availableFloors = _ptController
        .getPropertyAssessmentFloors()
        .map(
          (floor) => floor.contains('_')
              ? int.parse(floor.replaceAll('_', '-'))
              : int.parse(floor),
        )
        .toList();

    for (var unit in _propertyTypeFormWidget) {
      if (isNotNullOrEmpty(unit.selectedFloorValue)) {
        final selectedFloor = int.parse(unit.selectedFloorValue);
        List<int> rangeFloors = selectedFloor > 0
            ? availableFloors
                .where((floor) => floor <= selectedFloor && floor >= 0)
                .toList()
                .reversed
                .toList()
            : availableFloors
                .where((floor) => floor >= selectedFloor && floor <= 0)
                .toList();

        rangeFloors.removeWhere((floor) => selectedFloors.contains(floor));
        missingFloors.addAll(rangeFloors);
      }
    }

    floorSelectedErrorMessages.value = missingFloors.isNotEmpty
        ? "${getLocalizedString(i18.propertyTax.FLOORS_MISSING_UNITS, module: Modules.PT)}- ${missingFloors.toSet().join(', ')}"
        : "";
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      resizeToAvoidBottomInset: true,
      appBar: HeaderTop(
        titleWidget: Obx(
          () => isLoading.value
              ? const SizedBox.shrink()
              : Text(
                  getLocalizedString(
                    i18.propertyTax.EMP_NEW_ENUMERATION,
                    module: Modules.PT,
                  ),
                ),
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      bottomNavigationBar: Obx(
        () => (isLoading.value || disableSubmit.value)
            ? const SizedBox.shrink()
            : Container(
                height: 44.h,
                width: Get.width,
                margin: EdgeInsets.all(o == Orientation.portrait ? 16.w : 12.w),
                child: FilledButtonApp(
                  text: getLocalizedString(
                    i18.common.UC_SUBMIT,
                  ),
                  onPressed: () async {
                    if (areaSqFt.text.trim().isEmpty) {
                      return snackBar(
                        'Error',
                        'Area Sq Ft is required',
                        BaseConfig.redColor,
                      );
                    } else if (electricityId.text.trim().isEmpty) {
                      return snackBar(
                        'Error',
                        'Electricity Id is required',
                        BaseConfig.redColor,
                      );
                    } else if (uniqueId.text.trim().isEmpty) {
                      return snackBar(
                        'Error',
                        'Unique Id is required',
                        BaseConfig.redColor,
                      );
                    }
                    if (!_formKeyPt.currentState!.validate()) {
                      return;
                    }
                    await submitAllSelectedValues();
                  },
                ),
              ),
      ),
      body: Container(
        decoration: BoxDecoration(
          color: BaseConfig.mainBackgroundColor,
          borderRadius: BorderRadius.only(
            topLeft: Radius.circular(16.r),
            topRight: Radius.circular(16.r),
          ),
        ),
        child: SingleChildScrollView(
          controller: _scrollController,
          child: Padding(
            padding: EdgeInsets.symmetric(vertical: 10.h, horizontal: 16.w),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                Obx(
                  () => isLoading.value
                      ? SizedBox(
                          height: Get.height - 100.h,
                          child: showCircularIndicator(),
                        )
                      : SingleChildScrollView(
                          child: Form(
                            key: _formKeyPt,
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                _locationDetails(),
                                _propertyAssessment(),
                                Obx(() {
                                  if (isNotNullOrEmpty(
                                        selectUse.value,
                                      ) &&
                                      isNotNullOrEmpty(
                                        selectPropertyType.value,
                                      ) &&
                                      selectPropertyType.value != 'VACANT') {
                                    return Column(
                                      children: [
                                        for (int i = 0;
                                            i < _propertyTypeFormWidget.length;
                                            i++)
                                          PropertyTypeFormWidget(
                                            index: i,
                                            builtUpSpace:
                                                _propertyTypeFormWidget[i]
                                                    .builtUpSpace,
                                            occupancy: (value) => onOccupancy(
                                              i,
                                              value!,
                                            ),
                                            onRemove: () {
                                              _removeWidget(i);
                                            },
                                            provideUsageType: (value) =>
                                                onProvideUsageType(
                                              i,
                                              value!,
                                            ),
                                            selectedFloor: (value) =>
                                                onSelectedFloor(
                                              i,
                                              value!,
                                            ),
                                            unitUsageType: (value) =>
                                                onUnitUsageType(
                                              i,
                                              value!,
                                            ),
                                            propertyUsesType: selectUse.value,
                                            widgetLength:
                                                _propertyTypeFormWidget.length,
                                            totalRentedMonths: (value) =>
                                                onTotalRentedMonths(
                                              i,
                                              value!,
                                            ),
                                            rentINR: _propertyTypeFormWidget[i]
                                                .rentINR,
                                            remainingMonthOccupancy: (value) =>
                                                onRemainingMonthOccupancy(
                                              i,
                                              value!,
                                            ),
                                          ),
                                      ],
                                    );
                                  }
                                  return const SizedBox();
                                }),
                                Obx(
                                  () => floorSelectedErrorMessages
                                          .value.isNotEmpty
                                      ? Text(
                                          floorSelectedErrorMessages.value,
                                          style: TextStyle(
                                            color: BaseConfig.redColor,
                                            fontSize: 12.sp,
                                          ),
                                        )
                                      : const SizedBox(),
                                ),
                                Obx(() {
                                  if (isNotNullOrEmpty(
                                        selectUse.value,
                                      ) &&
                                      isNotNullOrEmpty(
                                        selectPropertyType.value,
                                      ) &&
                                      selectPropertyType.value != 'VACANT') {
                                    return iconTextButton(
                                      iconAlignment: IconAlignment.end,
                                      label: getLocalizedString(
                                        i18.propertyTax.EMP_ADD_MORE_UNIT,
                                        module: Modules.PT,
                                      ),
                                      labelColor: BaseConfig.appThemeColor1,
                                      icon: Icon(
                                        Icons.add,
                                        color: BaseConfig.appThemeColor1,
                                        size: 20.sp,
                                      ),
                                      onPressed: addMoreWidget,
                                    );
                                  }
                                  return const SizedBox();
                                }),
                                SizedBox(height: 10.h),
                                const Divider(),
                                SizedBox(height: 10.h),
                                _ownershipDetails(),
                                SizedBox(height: 10.h),
                                const Divider(),
                                SizedBox(height: 10.h),
                                _documentsDetails(),
                                SizedBox(height: 10.h),
                              ],
                            ),
                          ),
                        ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _locationDetails() {
    return Container(
      margin: EdgeInsets.symmetric(vertical: 10.h),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.r),
        border: Border.all(
          color: BaseConfig.borderColor1,
          width: 1,
        ),
      ),
      child: Padding(
        padding: EdgeInsets.all(4.w),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.common.EMP_LOCATION_DETAILS,
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
              height: 55,
              child: Obx(
                () => TextFormFieldApp(
                  radius: 8,
                  hintText: getLocalizedString(
                    i18.common.PINCODE,
                  ),
                  keyboardType: AppPlatforms.platformKeyboardType(),
                  controller: pinCode,
                  maxLength: 6,
                  inputFormatters: [
                    FilteringTextInputFormatter.digitsOnly,
                    LengthLimitingTextInputFormatter(
                      6,
                    ),
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
                  i18.common.EMP_CITY,
                ),
                selectedValue: selectedPtCityCode.value,
                enable: filteredPincodeTenant.value == null,
                options: languageController.mdmsResTenant.tenants!
                    .map(
                      (tenant) => '${tenant.code}',
                    )
                    .toList(),
                onChanged: (value) async {
                  selectedPtCityCode.value = value!;
                  final tenant = languageController.mdmsResTenant.tenants!
                      .firstWhereOrNull(
                    (t) => t.code == value,
                  );

                  selectedPtCityName.value = tenant?.city?.name ?? '';

                  if (filteredPincodeTenant.value != null ||
                      isNotNullOrEmpty(
                        selectedPtCityCode.value,
                      )) {
                    localityLoading.value = true;
                    await localityController.fetchLocality(
                      hierarchyTypeCode: 'ADMIN',
                      tenantId: selectedPtCityCode.value,
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
                        i18.propertyTax.EMP_LOCALITY,
                        module: Modules.PT,
                      ),
                      selectedValue: selectedPtLocalityName.value,
                      enable: filteredPincodeTenant.value == null,
                      options: localityController.locality.value?.tenantBoundary
                              ?.firstOrNull?.boundary
                              ?.map((e) => e.code ?? '')
                              .toList() ??
                          [],
                      onChanged: (value) {
                        selectedPtLocalityName.value = value!;
                        final boundariesList =
                            localityController.locality.value?.tenantBoundary;
                        final boundaryCd =
                            boundariesList!.first.boundary!.first.code;

                        selectedPtLocalityCode.value = boundaryCd ?? "";

                        dPrint(
                          'SelectedPtLocalityCode: $boundaryCd | SelectedPtLocalityName: ${boundariesList.first.boundary!.first.area}',
                        );
                      },
                      textSize: 14.sp,
                      isRequired: true,
                      enableLocal: true,
                    ),
            ),
            const SizedBox(height: 10),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.common.EMP_STREET_NAME,
              ),
              fontWeight: FontWeight.w600,
              size: 13.h,
            ),
            const SizedBox(height: 5),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.common.EMP_STREET_NAME,
                ),
                controller: streetName,
                textInputAction: TextInputAction.done,
              ),
            ),
            const SizedBox(height: 10),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.common.EMP_HOUSE_NO,
              ),
              fontWeight: FontWeight.w600,
              size: 13.h,
            ),
            const SizedBox(height: 5),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.common.EMP_HOUSE_NO,
                ),
                controller: houseNo,
                keyboardType: AppPlatforms.platformKeyboardType(),
                textInputAction: TextInputAction.done,
              ),
            ),
            SizedBox(height: 10.h),
          ],
        ),
      ),
    );
  }

  Widget _propertyAssessment() {
    return Container(
      margin: EdgeInsets.symmetric(vertical: 10.h),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.r),
        border: Border.all(
          color: BaseConfig.borderColor1,
          width: 1,
        ),
      ),
      child: Padding(
        padding: EdgeInsets.all(4.w),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.propertyTax.EMP_PROPERTY_ASSESSMENT,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w600,
              color: BaseConfig.appThemeColor1,
              size: 14.h,
            ),
            SizedBox(height: 10.h),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_USE,
                module: Modules.PT,
              ),
              options: _ptController
                  .getPropertyAssessmentUse()
                  .map(
                    (e) => 'PROPERTYTAX_BILLING_SLAB_$e',
                  )
                  .toList(),
              selectedValue: isNotNullOrEmpty(
                selectUse.value,
              )
                  ? 'PROPERTYTAX_BILLING_SLAB_$selectUse'
                  : null,
              onChanged: (value) {
                // final rawUses =
                //     _ptController.rawUsesTypeEmp(value);

                dPrint('value1: $value');

                final val = value?.split('_').last ?? '';

                selectUse.value = val;
                dPrint('value: $selectUse');

                final rawUses = _ptController.rawUsesTypeEmp(
                  val,
                );

                usesCategory.value = rawUses;
                usesCategoryMajor.value = rawUses.split('.').first;

                dPrint(
                  'usesCategory: $usesCategory | usesCategoryMajor: $usesCategoryMajor',
                );
              },
              textSize: 14.sp,
              isRequired: true,
              enableLocal: true,
              module: Modules.PT,
              validator: (val) {
                if (!isNotNullOrEmpty(val)) {
                  return 'Required Field';
                }
                return null;
              },
            ),
            SizedBox(height: 10.h),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_PROPERTY_TYPE,
                module: Modules.PT,
              ),
              options: _ptController
                  .getPropertyAssessmentType()
                  .map(
                    (e) => 'COMMON_PROPTYPE_$e',
                  )
                  .toList(),
              selectedValue: isNotNullOrEmpty(
                selectPropertyType.value,
              )
                  ? 'COMMON_PROPTYPE_$selectPropertyType'
                  : null,
              onChanged: (value) {
                selectPropertyType.value = value
                        ?.split(
                          'COMMON_PROPTYPE_',
                        )
                        .last ??
                    '';

                dPrint(
                  'Property Type: $selectPropertyType',
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
            SizedBox(height: 10.h),
            RequiredText(
              text: getLocalizedString(
                i18.propertyTax.EMP_AREA_SQ_FT,
                module: Modules.PT,
              ),
              required: true,
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
            ),
            const SizedBox(height: 5),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_AREA_SQ_FT,
                  module: Modules.PT,
                ),
                controller: areaSqFt,
                keyboardType: AppPlatforms.platformKeyboardType(),
                textInputAction: TextInputAction.done,
              ),
            ),
            SizedBox(height: 10.h),
            RequiredText(
              text: getLocalizedString(
                i18.propertyTax.EMP_ELECTRICITY_ID,
                module: Modules.PT,
              ),
              required: true,
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
            ),
            const SizedBox(height: 5),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_ELECTRICITY_ID,
                  module: Modules.PT,
                ),
                controller: electricityId,
                textInputAction: TextInputAction.done,
                keyboardType: AppPlatforms.platformKeyboardType(),
                maxLength: 10,
                inputFormatters: [
                  FilteringTextInputFormatter.digitsOnly,
                  LengthLimitingTextInputFormatter(
                    10,
                  ),
                ],
              ),
            ),
            SizedBox(height: 10.h),
            RequiredText(
              text: getLocalizedString(
                i18.propertyTax.EMP_UID,
                module: Modules.PT,
              ),
              required: true,
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
            ),
            const SizedBox(height: 5),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_UID,
                  module: Modules.PT,
                ),
                controller: uniqueId,
                textInputAction: TextInputAction.done,
                keyboardType: AppPlatforms.platformKeyboardType(),
                maxLength: 15,
                inputFormatters: [
                  FilteringTextInputFormatter.digitsOnly,
                  LengthLimitingTextInputFormatter(
                    15,
                  ),
                ],
              ),
            ),
            SizedBox(height: 10.h),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_STRUCTURE_TYPE,
                module: Modules.PT,
              ),
              options: const [
                'Permanent',
                'Temporary',
                'SEMI_PERMANENT',
                'RCC',
              ],
              onChanged: (value) {
                selectStructureType.value = value!;
                dPrint(
                  'value: ${getLocalizedString(selectStructureType.value, module: Modules.PT)}',
                );
              },
              selectedValue: selectStructureType.value,
              textSize: 14.sp,
              isRequired: true,
              enableLocal: true,
              module: Modules.PT,
              validator: (val) {
                if (!isNotNullOrEmpty(val)) {
                  return 'Required Field';
                }
                return null;
              },
            ),
            SizedBox(height: 10.h),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_AGE_OF_PROPERTY,
                module: Modules.PT,
              ),
              options: const [
                'PROPERTYTAX_MONTH>10',
                'PROPERTYTAX_MONTH>15',
                'PROPERTYTAX_MONTH>25',
              ],
              onChanged: (value) {
                ageOfProperty.value = value!;

                final isGtr = ageOfProperty.value.contains('>');

                ageOfPropertyCode.value =
                    ageOfProperty.value.split(isGtr ? '>' : '<').last;

                ageOfPropertyName.value = isGtr
                    ? 'greater than ${ageOfPropertyCode.value} years'
                    : 'less than ${ageOfPropertyCode.value} years';

                dPrint(
                  '''
                                              value: $ageOfProperty |
                                              code: $ageOfPropertyCode |
                                              name: $ageOfPropertyName
                                              ''',
                );
              },
              selectedValue: ageOfProperty.value,
              textSize: 14.sp,
              isRequired: true,
              enableLocal: true,
              module: Modules.PT,
              validator: (val) {
                if (!isNotNullOrEmpty(val)) {
                  return 'Required Field';
                }
                return null;
              },
            ),
            SizedBox(height: 10.h),
          ],
        ),
      ),
    );
  }

  Widget _ownershipDetails() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        BigTextNotoSans(
          text: 'Ownership Details',
          fontWeight: FontWeight.w600,
          color: BaseConfig.appThemeColor1,
          size: 14.h,
        ),
        SizedBox(height: 10.h),
        ColumnHeaderDropdownSearch(
          label: getLocalizedString(
            i18.propertyTax.EMP_PROVIDE_OWNERSHIP_DETAIL,
            module: Modules.PT,
          ),
          options: _ptController
              .getProvideOwnershipDetails()
              .map((e) => 'PT_OWNERSHIP_$e')
              .toList(),
          onChanged: (value) {
            authorizedInstitutionTypeValue = '';
            final val = value
                ?.split(
                  'PT_OWNERSHIP_',
                )
                .last;

            ownershipDetail.value = val!;

            final rawOwner = _ptController.getRawSingleOwner(val);

            ownershipCategory.value = rawOwner;

            dPrint(
              'value: $ownershipDetail | $rawOwner',
            );
          },
          selectedValue: isNotNullOrEmpty(
            ownershipDetail.value,
          )
              ? 'PT_OWNERSHIP_$ownershipDetail'
              : null,
          textSize: 14.sp,
          isRequired: true,
          enableLocal: true,
          module: Modules.PT,
          validator: (val) {
            if (!isNotNullOrEmpty(val)) {
              return 'Required Field';
            }
            return null;
          },
        ),
        SizedBox(height: 10.h),
        Obx(() {
          if (isNotNullOrEmpty(
            ownershipDetail.value == 'MULTIPLEOWNERS' ||
                ownershipDetail.value == 'SINGLEOWNER',
          )) {
            return Column(
              children: [
                const Divider(),
                for (int j = 0; j < _propertyTypeOwnerFormWidget.length; j++)
                  PropertyTypeOwnerFormWidget(
                    index: j,
                    gender: (value) => onGender(j, value!),
                    relationShip: (value) => onRelationShip(
                      j,
                      value!,
                    ),
                    specialCategory: (value) => onSpecialCategory(
                      j,
                      value!,
                    ),
                    specialCategoryDocumentType: (value) =>
                        onSpecialCategoryDocumentTypeValue(
                      j,
                      value!,
                    ),
                    ownerName: _propertyTypeOwnerFormWidget[j].ownerName,
                    mobileNumber: _propertyTypeOwnerFormWidget[j].mobileNumber,
                    guardiansName:
                        _propertyTypeOwnerFormWidget[j].guardiansName,
                    email: _propertyTypeOwnerFormWidget[j].email,
                    ownerDocumentId:
                        _propertyTypeOwnerFormWidget[j].ownerDocumentId,
                    ownerAddress: _propertyTypeOwnerFormWidget[j].ownerAddress,
                    widgetLength: _propertyTypeOwnerFormWidget.length,
                    onRemove: () {
                      _removeOwnerWidget(j);
                    },
                  ),
              ],
            );
          } else if (isNotNullOrEmpty(
            ownershipDetail.value == 'INSTITUTIONALGOVERNMENT' ||
                ownershipDetail.value == 'INSTITUTIONALPRIVATE' ||
                ownershipDetail.value == 'OTHERGOVERNMENTINSTITUITION' ||
                ownershipDetail.value == 'OTHERSPRIVATEINSTITUITION' ||
                ownershipDetail.value == 'CENTRALGOVERNMENT' ||
                ownershipDetail.value == 'STATEGOVERNMENT' ||
                ownershipDetail.value == 'ULBGOVERNMENT' ||
                ownershipDetail.value == 'PRIVATEBOARD' ||
                ownershipDetail.value == 'PRIVATETRUST' ||
                ownershipDetail.value == 'NGO' ||
                ownershipDetail.value == 'PRIVATECOMPANY',
          )) {
            return PropertyAuthorizedPersonnelDetail(
              authorizedInstitutionName: authorizedInstitutionName,
              authorizedInstitutionType: (value) => onAuthorizedInstitutionType(
                0,
                value!,
              ),
              authorizedInstitutionTypeValue: authorizedInstitutionTypeValue,
              authorizedOwnerName: authorizedOwnerName,
              authorizedLandlineNumber: authorizedLandlineNumber,
              authorizedMobileNumber: authorizedMobileNumber,
              authorizedDesignation: authorizedDesignation,
              authorizedEmail: authorizedEmail,
              authorizedOwnerAddress: authorizedOwnerAddress,
              ownershipType: ownershipDetail.value,
            );
          }
          return const SizedBox.shrink();
        }),
        Obx(() {
          if (isNotNullOrEmpty(
            ownershipDetail.value == 'MULTIPLEOWNERS',
          )) {
            return iconTextButton(
              iconAlignment: IconAlignment.end,
              label: 'Add Owner',
              labelColor: BaseConfig.appThemeColor1,
              icon: Icon(
                Icons.add,
                color: BaseConfig.appThemeColor1,
                size: 20.sp,
              ),
              onPressed: addMoreOwnerWidget,
            );
          }
          return const SizedBox.shrink();
        }),
      ],
    );
  }

  Widget _documentsDetails() {
    return Container(
      margin: EdgeInsets.symmetric(vertical: 10.h),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.r),
        border: Border.all(
          color: BaseConfig.borderColor1,
          width: 1,
        ),
      ),
      child: Padding(
        padding: EdgeInsets.all(4.w),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.propertyTax.EMP_DOCUMENTS_REQUIRED,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w600,
              color: BaseConfig.appThemeColor1,
              size: 14.h,
            ),
            SizedBox(height: 10.h),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_USAGEPROOF,
                module: Modules.PT,
              ),
              options: _ptController
                  .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
                  .where(
                    (doc) => doc.code.toString() == 'OWNER.USAGEPROOF',
                  )
                  .expand(
                    (doc) => doc.dropdownData as List<dynamic>,
                  )
                  .map(
                    (data) => getLocalizedString(
                      data.code.toString().replaceAll('.', '_').toUpperCase(),
                      module: Modules.PT,
                    ),
                  )
                  .toList(),
              onChanged: (value) {
                final selectedData = _ptController
                    .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
                    .where(
                      (doc) => doc.code.toString() == 'OWNER.USAGEPROOF',
                    )
                    .expand(
                      (doc) => doc.dropdownData as List<dynamic>,
                    )
                    .firstWhere(
                      (data) =>
                          getLocalizedString(
                            data.code
                                .toString()
                                .replaceAll(
                                  '.',
                                  '_',
                                )
                                .toUpperCase(),
                            module: Modules.PT,
                          ) ==
                          value,
                      orElse: () => null,
                    );

                if (selectedData != null) {
                  usageProof.value = selectedData.code.toString();
                }

                dPrint('Selected value: $value');
                dPrint(
                  'Updated usageProof.value: ${usageProof.value}',
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
            const SizedBox(height: 10),
            Obx(() {
              if (isNotNullOrEmpty(
                usageProof.value,
              )) {
                return SizedBox(
                  height: 100,
                  child: GridView.builder(
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 5,
                      crossAxisSpacing: 8,
                      mainAxisSpacing: 8,
                    ),
                    itemCount: usageProofImages.length + 1,
                    itemBuilder: (context, index) {
                      if (index == usageProofImages.length) {
                        return IconButton.filled(
                          style: ButtonStyle(
                            backgroundColor:
                                const WidgetStatePropertyAll<Color>(
                              BaseConfig.appThemeColor1,
                            ),
                            shape: WidgetStatePropertyAll<OutlinedBorder>(
                              RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(
                                  10.r,
                                ),
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
                                _pickImageUsageProof(
                                  index,
                                  source: ImageSource.gallery,
                                );
                              },
                              onTabImageCamera: () {
                                _pickImageUsageProof(
                                  index,
                                  source: ImageSource.camera,
                                );
                              },
                            );
                          },
                        );
                      }

                      final pImage = usageProofImages[index];
                      return Stack(
                        fit: StackFit.expand,
                        children: [
                          if (pImage.isLoading)
                            showCircularIndicator()
                          else
                            ClipRRect(
                              borderRadius: BorderRadius.circular(10.r),
                              child: Image.file(
                                File(pImage.filePath!),
                                fit: BoxFit.cover,
                              ),
                            ),
                          if (!pImage.isLoading)
                            Positioned(
                              top: 4,
                              right: 4,
                              child: GestureDetector(
                                onTap: () {
                                  setState(() {
                                    usageProofImages.removeAt(
                                      index,
                                    );

                                    documents.removeWhere(
                                      (doc) =>
                                          doc.documentType == usageProof.value,
                                    );
                                  });
                                },
                                child: Container(
                                  decoration: const BoxDecoration(
                                    color: BaseConfig.appThemeColor1,
                                    shape: BoxShape.circle,
                                  ),
                                  padding: const EdgeInsets.all(
                                    4,
                                  ),
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
                );
              }
              return const SizedBox();
            }),
            SizedBox(height: 10.h),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_OCCUPANCY_PROOF,
                module: Modules.PT,
              ),
              options: _ptController
                  .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
                  .where(
                    (doc) => doc.code.toString() == 'OWNER.OCCUPANCYPROOF',
                  )
                  .expand(
                    (doc) => doc.dropdownData as List<dynamic>,
                  )
                  .map(
                    (data) => getLocalizedString(
                      data.code.toString().replaceAll('.', '_').toUpperCase(),
                      module: Modules.PT,
                    ),
                  )
                  .toList(),
              onChanged: (value) {
                final selectedData = _ptController
                    .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
                    .where(
                      (doc) => doc.code.toString() == 'OWNER.OCCUPANCYPROOF',
                    )
                    .expand(
                      (doc) => doc.dropdownData as List<dynamic>,
                    )
                    .firstWhere(
                      (data) =>
                          getLocalizedString(
                            data.code
                                .toString()
                                .replaceAll(
                                  '.',
                                  '_',
                                )
                                .toUpperCase(),
                            module: Modules.PT,
                          ) ==
                          value,
                      orElse: () => null,
                    );

                if (selectedData != null) {
                  occupancyProof.value = selectedData.code.toString();
                }

                dPrint('Selected value: $value');
                dPrint(
                  'Updated occupancyProof.value: ${occupancyProof.value}',
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
            const SizedBox(height: 10),
            Obx(() {
              if (isNotNullOrEmpty(
                occupancyProof.value,
              )) {
                return SizedBox(
                  height: 100,
                  child: GridView.builder(
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 5,
                      crossAxisSpacing: 8,
                      mainAxisSpacing: 8,
                    ),
                    itemCount: occupancyProofImages.length + 1,
                    itemBuilder: (context, index) {
                      if (index == occupancyProofImages.length) {
                        return IconButton.filled(
                          style: ButtonStyle(
                            backgroundColor:
                                const WidgetStatePropertyAll<Color>(
                              BaseConfig.appThemeColor1,
                            ),
                            shape: WidgetStatePropertyAll<OutlinedBorder>(
                              RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(
                                  10.r,
                                ),
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
                                _pickImageOccupancyProof(
                                  index,
                                  source: ImageSource.gallery,
                                );
                              },
                              onTabImageCamera: () {
                                _pickImageOccupancyProof(
                                  index,
                                  source: ImageSource.camera,
                                );
                              },
                            );
                          },
                        );
                      }

                      final pImage = occupancyProofImages[index];
                      return Stack(
                        fit: StackFit.expand,
                        children: [
                          if (pImage.isLoading)
                            showCircularIndicator()
                          else
                            ClipRRect(
                              borderRadius: BorderRadius.circular(10.r),
                              child: Image.file(
                                File(pImage.filePath!),
                                fit: BoxFit.cover,
                              ),
                            ),
                          if (!pImage.isLoading)
                            Positioned(
                              top: 4,
                              right: 4,
                              child: GestureDetector(
                                onTap: () {
                                  setState(() {
                                    occupancyProofImages.removeAt(
                                      index,
                                    );
                                    documents.removeWhere(
                                      (doc) =>
                                          doc.documentType ==
                                          occupancyProof.value,
                                    );
                                  });
                                },
                                child: Container(
                                  decoration: const BoxDecoration(
                                    color: BaseConfig.appThemeColor1,
                                    shape: BoxShape.circle,
                                  ),
                                  padding: const EdgeInsets.all(
                                    4,
                                  ),
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
                );
              }
              return const SizedBox();
            }),
            SizedBox(height: 10.h),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_CONSTRUCTION_PROOF,
                module: Modules.PT,
              ),
              options: _ptController
                  .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
                  .where(
                    (doc) => doc.code.toString() == 'OWNER.CONSTRUCTIONPROOF',
                  )
                  .expand(
                    (doc) => doc.dropdownData as List<dynamic>,
                  )
                  .map(
                    (data) => getLocalizedString(
                      data.code.toString().replaceAll('.', '_').toUpperCase(),
                      module: Modules.PT,
                    ),
                  )
                  .toList(),
              onChanged: (value) {
                final selectedData = _ptController
                    .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
                    .where(
                      (doc) => doc.code.toString() == 'OWNER.CONSTRUCTIONPROOF',
                    )
                    .expand(
                      (doc) => doc.dropdownData as List<dynamic>,
                    )
                    .firstWhere(
                      (data) =>
                          getLocalizedString(
                            data.code
                                .toString()
                                .replaceAll(
                                  '.',
                                  '_',
                                )
                                .toUpperCase(),
                            module: Modules.PT,
                          ) ==
                          value,
                      orElse: () => null,
                    );

                if (selectedData != null) {
                  constructionProof.value = selectedData.code.toString();
                }

                dPrint('Selected value: $value');
                dPrint(
                  'Updated constructionProof.value: ${constructionProof.value}',
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
            const SizedBox(height: 10),
            Obx(() {
              if (isNotNullOrEmpty(
                constructionProof.value,
              )) {
                return SizedBox(
                  height: 100,
                  child: GridView.builder(
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 5,
                      crossAxisSpacing: 8,
                      mainAxisSpacing: 8,
                    ),
                    itemCount: constructionProofImages.length + 1,
                    itemBuilder: (context, index) {
                      if (index == constructionProofImages.length) {
                        return IconButton.filled(
                          style: ButtonStyle(
                            backgroundColor:
                                const WidgetStatePropertyAll<Color>(
                              BaseConfig.appThemeColor1,
                            ),
                            shape: WidgetStatePropertyAll<OutlinedBorder>(
                              RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(
                                  10.r,
                                ),
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
                                _pickImageConstructionProof(
                                  index,
                                  source: ImageSource.gallery,
                                );
                              },
                              onTabImageCamera: () {
                                _pickImageConstructionProof(
                                  index,
                                  source: ImageSource.camera,
                                );
                              },
                            );
                          },
                        );
                      }

                      final pImage = constructionProofImages[index];

                      return Stack(
                        fit: StackFit.expand,
                        children: [
                          if (pImage.isLoading)
                            showCircularIndicator()
                          else
                            ClipRRect(
                              borderRadius: BorderRadius.circular(10.r),
                              child: Image.file(
                                File(pImage.filePath!),
                                fit: BoxFit.cover,
                              ),
                            ),
                          if (!pImage.isLoading)
                            Positioned(
                              top: 4,
                              right: 4,
                              child: GestureDetector(
                                onTap: () {
                                  setState(() {
                                    constructionProofImages.removeAt(
                                      index,
                                    );
                                    documents.removeWhere(
                                      (doc) =>
                                          doc.documentType ==
                                          constructionProof.value,
                                    );
                                  });
                                },
                                child: Container(
                                  decoration: const BoxDecoration(
                                    color: BaseConfig.appThemeColor1,
                                    shape: BoxShape.circle,
                                  ),
                                  padding: const EdgeInsets.all(
                                    4,
                                  ),
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
                );
              }
              return const SizedBox();
            }),
            SizedBox(height: 10.h),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_REGISTRATION_PROOF,
                module: Modules.PT,
              ),
              options: _ptController
                  .empMdmsResModel!.mdmsResEmp!.propertyTax!.mutationDocuments!
                  .where(
                    (doc) =>
                        doc.code.toString() == 'OWNER.TRANSFERREASONDOCUMENT',
                  )
                  .expand(
                    (doc) => doc.dropdownData as List<dynamic>,
                  )
                  .map(
                    (data) => getLocalizedString(
                      data.code.toString().replaceAll('.', '_').toUpperCase(),
                      module: Modules.PT,
                    ),
                  )
                  .toList(),
              onChanged: (value) {
                final selectedData = _ptController.empMdmsResModel!.mdmsResEmp!
                    .propertyTax!.mutationDocuments!
                    .where(
                      (doc) =>
                          doc.code.toString() == 'OWNER.TRANSFERREASONDOCUMENT',
                    )
                    .expand(
                      (doc) => doc.dropdownData as List<dynamic>,
                    )
                    .firstWhere(
                      (data) =>
                          getLocalizedString(
                            data.code
                                .toString()
                                .replaceAll(
                                  '.',
                                  '_',
                                )
                                .toUpperCase(),
                            module: Modules.PT,
                          ) ==
                          value,
                      orElse: () => null,
                    );

                if (selectedData != null) {
                  registrationProof.value = selectedData.code.toString();
                }

                dPrint('Selected value: $value');
                dPrint(
                  'Updated registrationProof.value: ${registrationProof.value}',
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
            const SizedBox(height: 10),
            Obx(() {
              if (isNotNullOrEmpty(
                registrationProof.value,
              )) {
                return SizedBox(
                  height: 100,
                  child: GridView.builder(
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 5,
                      crossAxisSpacing: 8,
                      mainAxisSpacing: 8,
                    ),
                    itemCount: registrationProofImages.length + 1,
                    itemBuilder: (context, index) {
                      if (index == registrationProofImages.length) {
                        return IconButton.filled(
                          style: ButtonStyle(
                            backgroundColor:
                                const WidgetStatePropertyAll<Color>(
                              BaseConfig.appThemeColor1,
                            ),
                            shape: WidgetStatePropertyAll<OutlinedBorder>(
                              RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(
                                  10.r,
                                ),
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
                                _pickImageRegistrationProof(
                                  index,
                                  source: ImageSource.gallery,
                                );
                              },
                              onTabImageCamera: () {
                                _pickImageRegistrationProof(
                                  index,
                                  source: ImageSource.camera,
                                );
                              },
                            );
                          },
                        );
                      }

                      final pImage = registrationProofImages[index];

                      return Stack(
                        fit: StackFit.expand,
                        children: [
                          if (pImage.isLoading)
                            showCircularIndicator()
                          else
                            ClipRRect(
                              borderRadius: BorderRadius.circular(10.r),
                              child: Image.file(
                                File(pImage.filePath!),
                                fit: BoxFit.cover,
                              ),
                            ),
                          if (!pImage.isLoading)
                            Positioned(
                              top: 4,
                              right: 4,
                              child: GestureDetector(
                                onTap: () {
                                  setState(() {
                                    registrationProofImages.removeAt(
                                      index,
                                    );
                                    documents.removeWhere(
                                      (doc) =>
                                          doc.documentType ==
                                          registrationProof.value,
                                    );
                                  });
                                },
                                child: Container(
                                  decoration: const BoxDecoration(
                                    color: BaseConfig.appThemeColor1,
                                    shape: BoxShape.circle,
                                  ),
                                  padding: const EdgeInsets.all(
                                    4,
                                  ),
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
                );
              }
              return const SizedBox();
            }),
            SizedBox(height: 10.h),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.common.EMP_IDENTITYPROOF,
              ),
              options: _ptController
                  .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
                  .where(
                    (doc) => doc.code.toString() == 'OWNER.IDENTITYPROOF',
                  )
                  .expand(
                    (doc) => doc.dropdownData as List<dynamic>,
                  )
                  .map(
                    (data) => data.code.toString(),
                  )
                  .toList(),
              onChanged: (value) {
                final selectedData = _ptController
                    .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
                    .where(
                      (doc) => doc.code.toString() == 'OWNER.IDENTITYPROOF',
                    )
                    .expand(
                      (doc) => doc.dropdownData as List<dynamic>,
                    )
                    .firstWhere(
                      (data) =>
                          getLocalizedString(
                            data.code
                                .toString()
                                .replaceAll(
                                  '.',
                                  '_',
                                )
                                .toUpperCase(),
                            module: Modules.PT,
                          ) ==
                          value,
                      orElse: () => null,
                    );

                if (selectedData != null) {
                  identityProof.value = selectedData.code.toString();
                }

                dPrint('Selected value: $value');
                dPrint(
                  'Updated identityProof.value: ${identityProof.value = value!}',
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
            const SizedBox(height: 10),
            Obx(() {
              if (isNotNullOrEmpty(
                identityProof.value,
              )) {
                return SizedBox(
                  height: 100,
                  child: GridView.builder(
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 5,
                      crossAxisSpacing: 8,
                      mainAxisSpacing: 8,
                    ),
                    itemCount: identityProofImages.length + 1,
                    itemBuilder: (context, index) {
                      if (index == identityProofImages.length) {
                        return IconButton.filled(
                          style: ButtonStyle(
                            backgroundColor:
                                const WidgetStatePropertyAll<Color>(
                              BaseConfig.appThemeColor1,
                            ),
                            shape: WidgetStatePropertyAll<OutlinedBorder>(
                              RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(
                                  10.r,
                                ),
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
                                _pickImageIdentityProof(
                                  index,
                                  source: ImageSource.gallery,
                                );
                              },
                              onTabImageCamera: () {
                                _pickImageIdentityProof(
                                  index,
                                  source: ImageSource.camera,
                                );
                              },
                            );
                          },
                        );
                      }

                      final pImage = identityProofImages[index];

                      return Stack(
                        fit: StackFit.expand,
                        children: [
                          if (pImage.isLoading)
                            showCircularIndicator()
                          else
                            ClipRRect(
                              borderRadius: BorderRadius.circular(10.r),
                              child: Image.file(
                                File(pImage.filePath!),
                                fit: BoxFit.cover,
                              ),
                            ),
                          if (!pImage.isLoading)
                            Positioned(
                              top: 4,
                              right: 4,
                              child: GestureDetector(
                                onTap: () {
                                  setState(() {
                                    identityProofImages.removeAt(
                                      index,
                                    );
                                    documents.removeWhere(
                                      (doc) =>
                                          doc.documentType ==
                                          identityProof.value,
                                    );
                                  });
                                },
                                child: Container(
                                  decoration: const BoxDecoration(
                                    color: BaseConfig.appThemeColor1,
                                    shape: BoxShape.circle,
                                  ),
                                  padding: const EdgeInsets.all(
                                    4,
                                  ),
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
                );
              }
              return const SizedBox();
            }),
            SizedBox(height: 10.h),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.common.EMP_ADDRESSPROOF,
              ),
              options: _ptController
                  .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
                  .where(
                    (doc) => doc.code.toString() == 'OWNER.ADDRESSPROOF',
                  )
                  .expand(
                    (doc) => doc.dropdownData as List<dynamic>,
                  )
                  .map(
                    (data) => getLocalizedString(
                      data.code.toString().replaceAll('.', '_').toUpperCase(),
                      module: Modules.PT,
                    ),
                  )
                  .toList(),
              onChanged: (value) {
                final selectedData = _ptController
                    .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
                    .where(
                      (doc) => doc.code.toString() == 'OWNER.ADDRESSPROOF',
                    )
                    .expand(
                      (doc) => doc.dropdownData as List<dynamic>,
                    )
                    .firstWhere(
                      (data) =>
                          getLocalizedString(
                            data.code
                                .toString()
                                .replaceAll(
                                  '.',
                                  '_',
                                )
                                .toUpperCase(),
                            module: Modules.PT,
                          ) ==
                          value,
                      orElse: () => null,
                    );

                if (selectedData != null) {
                  addressProof.value = selectedData.code.toString();
                }

                dPrint('Selected value: $value');
                dPrint(
                  'Updated constructionProof.value: ${addressProof.value}',
                );
                // addressProof.value = value!;
                // dPrint('value: $addressProof');
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
            const SizedBox(height: 10),
            Obx(() {
              if (isNotNullOrEmpty(
                addressProof.value,
              )) {
                return SizedBox(
                  height: 100,
                  child: GridView.builder(
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 5,
                      crossAxisSpacing: 8,
                      mainAxisSpacing: 8,
                    ),
                    itemCount: addressProofImages.length + 1,
                    itemBuilder: (context, index) {
                      if (index == addressProofImages.length) {
                        return IconButton.filled(
                          style: ButtonStyle(
                            backgroundColor:
                                const WidgetStatePropertyAll<Color>(
                              BaseConfig.appThemeColor1,
                            ),
                            shape: WidgetStatePropertyAll<OutlinedBorder>(
                              RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(
                                  10.r,
                                ),
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
                                _pickImageAddressProof(
                                  index,
                                  source: ImageSource.gallery,
                                );
                              },
                              onTabImageCamera: () {
                                _pickImageAddressProof(
                                  index,
                                  source: ImageSource.camera,
                                );
                              },
                            );
                          },
                        );
                      }

                      final pImage = addressProofImages[index];

                      return Stack(
                        fit: StackFit.expand,
                        children: [
                          if (pImage.isLoading)
                            showCircularIndicator()
                          else
                            ClipRRect(
                              borderRadius: BorderRadius.circular(10.r),
                              child: Image.file(
                                File(pImage.filePath!),
                                fit: BoxFit.cover,
                              ),
                            ),
                          if (!pImage.isLoading)
                            Positioned(
                              top: 4,
                              right: 4,
                              child: GestureDetector(
                                onTap: () {
                                  setState(() {
                                    addressProofImages.removeAt(
                                      index,
                                    );
                                    documents.removeWhere(
                                      (doc) =>
                                          doc.documentType ==
                                          addressProof.value,
                                    );
                                  });
                                },
                                child: Container(
                                  decoration: const BoxDecoration(
                                    color: BaseConfig.appThemeColor1,
                                    shape: BoxShape.circle,
                                  ),
                                  padding: const EdgeInsets.all(
                                    4,
                                  ),
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
                );
              }
              return const SizedBox();
            }),
          ],
        ),
      ),
    );
  }
}

// ignore: must_be_immutable
class PropertyTypeFormWidget extends StatefulWidget {
  Function(String?) selectedFloor;
  Function(String?) provideUsageType;
  Function(String?) unitUsageType;
  Function(String?) occupancy;
  Function(String?) totalRentedMonths;
  Function(String?) remainingMonthOccupancy;
  TextEditingController builtUpSpace;
  TextEditingController rentINR;
  VoidCallback? onRemove;
  int? index, widgetLength;
  String selectedFloorValue;
  String unitUsageTypeValue;
  String occupancyValue;
  String totalRentedMonthsValue;
  String remainingMonthOccupancyValue;
  String? propertyUsesType;

  PropertyTypeFormWidget({
    super.key,
    required this.selectedFloor,
    required this.provideUsageType,
    required this.unitUsageType,
    required this.occupancy,
    required this.totalRentedMonths,
    required this.remainingMonthOccupancy,
    required this.builtUpSpace,
    required this.rentINR,
    this.index,
    this.widgetLength,
    this.onRemove,
    this.selectedFloorValue = '',
    this.unitUsageTypeValue = '',
    this.occupancyValue = '',
    this.totalRentedMonthsValue = '',
    this.remainingMonthOccupancyValue = '',
    this.propertyUsesType,
  });

  @override
  State<PropertyTypeFormWidget> createState() => _PropertyTypeFormWidgetState();
}

class _PropertyTypeFormWidgetState extends State<PropertyTypeFormWidget> {
  final _ptController = Get.find<PropertiesTaxController>();
  late String selectedFloorValue,
      unitUsageTypeValue,
      occupancyValue,
      totalRentedMonthsValue,
      remainingMonthOccupancyValue;

  @override
  void initState() {
    super.initState();
    selectedFloorValue = widget.selectedFloorValue;
    unitUsageTypeValue = widget.unitUsageTypeValue;
    occupancyValue = widget.occupancyValue;
    totalRentedMonthsValue = widget.totalRentedMonthsValue;
    remainingMonthOccupancyValue = widget.remainingMonthOccupancyValue;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.symmetric(vertical: 10.h),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.r),
        border: Border.all(
          color: BaseConfig.borderColor1,
          width: 1,
        ),
      ),
      child: Padding(
        padding: EdgeInsets.all(4.w),
        child: Column(
          spacing: 10.h,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                SmallTextNotoSans(
                  text: 'Unit - ${widget.index! + 1}',
                  fontWeight: FontWeight.w600,
                  color: BaseConfig.appThemeColor1,
                  size: 14.h,
                ),
                if (widget.index != 0)
                  IconButton(
                    onPressed: widget.onRemove,
                    icon: const Icon(
                      Icons.delete,
                      color: BaseConfig.redColor,
                    ),
                  ),
              ],
            ),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_SELECT_FLOOR,
                module: Modules.PT,
              ),
              options: _ptController
                  .getPropertyAssessmentFloors()
                  .map((e) => 'PROPERTYTAX_FLOOR_$e')
                  .toList(),
              onChanged: (value) {
                final floor = modifyUnderscoreAndAddedMinus(value!);
                widget.selectedFloor(floor);
                dPrint('value: $value');
              },
              selectedValue: isNotNullOrEmpty(selectedFloorValue)
                  ? 'PROPERTYTAX_FLOOR_${modifyUnderscoreMinusToUnderscore(selectedFloorValue)}'
                  : null,
              textSize: 14.sp,
              isRequired: true,
              enableLocal: true,
              module: Modules.PT,
              validator: (val) {
                if (!isNotNullOrEmpty(val)) {
                  return 'Required Field';
                }
                return null;
              },
            ),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_USAGE_TYPE,
                module: Modules.PT,
              ),
              options: _ptController
                  .getPropertyAssessmentUse()
                  .map(
                    (e) => 'PROPERTYTAX_BILLING_SLAB_$e',
                  )
                  .toList(),
              selectedValue: isNotNullOrEmpty(widget.propertyUsesType)
                  ? 'PROPERTYTAX_BILLING_SLAB_${widget.propertyUsesType}'
                  : null,
              onChanged: (value) {
                widget.provideUsageType(value?.split('_').last);
                dPrint('value: $value');
              },
              enable: !isNotNullOrEmpty(widget.propertyUsesType),
              textSize: 14.sp,
              isRequired: true,
              enableLocal: true,
              module: Modules.PT,
              validator: (val) {
                if (!isNotNullOrEmpty(val)) {
                  return 'Required Field';
                }
                return null;
              },
            ),
            if (widget.propertyUsesType != 'RESIDENTIAL')
              ColumnHeaderDropdownSearch(
                label: getLocalizedString(
                  i18.propertyTax.EMP_UNIT_USAGE_TYPE,
                  module: Modules.PT,
                ),
                options: _ptController
                    .getPropertyAssessmentUnitUsesTypes(
                      useType: widget.propertyUsesType ?? '',
                    )
                    .map((e) => 'COMMON_PROPSUBUSGTYPE_$e')
                    .toList(),
                onChanged: (value) {
                  dPrint('value: $value');
                  widget.unitUsageType(value);
                },
                selectedValue: isNotNullOrEmpty(unitUsageTypeValue)
                    ? 'COMMON_PROPSUBUSGTYPE_${widget.unitUsageType}'
                    : null,
                textSize: 14.sp,
                isRequired: true,
                enableLocal: true,
                module: Modules.PT,
                validator: (val) {
                  if (!isNotNullOrEmpty(val)) {
                    return 'Required Field';
                  }
                  return null;
                },
              ),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_OCCUPANCY,
                module: Modules.PT,
              ),
              options: _ptController
                  .empMdmsResModel!.mdmsResEmp!.propertyTax!.occupancyType!
                  .map(
                    (data) => data.code ?? '',
                    // getLocalizedString(
                    //   data.code.toString().replaceAll('.', '_').toUpperCase(),
                    //   module: Modules.PT,
                    // ),
                  )
                  .toList(),
              onChanged: (value) {
                setState(() {
                  occupancyValue = value!;
                });
                widget.occupancy(value);
                dPrint('occupancyValue: $value');
              },
              selectedValue: occupancyValue,
              textSize: 14.sp,
              isRequired: true,
              enableLocal: true,
              module: Modules.PT,
              validator: (val) {
                if (!isNotNullOrEmpty(val)) {
                  return 'Required Field';
                }
                return null;
              },
            ),
            if (occupancyValue.toLowerCase() == 'rented')
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  RequiredText(
                    fontWeight: FontWeight.w600,
                    fontSize: 13.h,
                    required: true,
                    text: 'Rent(INR)',
                  ),
                  const SizedBox(
                    height: 5,
                  ),
                  SizedBox(
                    height: 55,
                    child: TextFormFieldApp(
                      radius: 8,
                      hintText: 'Rent(INR)',
                      controller: widget.rentINR,
                      textInputAction: TextInputAction.done,
                      keyboardType: AppPlatforms.platformKeyboardType(),
                    ),
                  ),
                  const SizedBox(
                    height: 10,
                  ),
                  ColumnHeaderDropdownSearch(
                    label: "Total Rented Months",
                    options: const [
                      'PROPERTYTAX_MONTH1',
                      'PROPERTYTAX_MONTH2',
                      'PROPERTYTAX_MONTH3',
                      'PROPERTYTAX_MONTH4',
                      'PROPERTYTAX_MONTH5',
                      'PROPERTYTAX_MONTH6',
                      'PROPERTYTAX_MONTH7',
                      'PROPERTYTAX_MONTH8',
                      'PROPERTYTAX_MONTH9',
                      'PROPERTYTAX_MONTH10',
                      'PROPERTYTAX_MONTH11',
                      'PROPERTYTAX_MONTH12',
                    ],
                    onChanged: (value) {
                      setState(() {
                        totalRentedMonthsValue = value!;
                      });
                      widget.totalRentedMonths(value);
                    },
                    selectedValue: totalRentedMonthsValue,
                    textSize: 14.sp,
                    isRequired: true,
                    enableLocal: true,
                    module: Modules.PT,
                    validator: (val) {
                      if (!isNotNullOrEmpty(val)) {
                        return 'Required Field';
                      }
                      return null;
                    },
                  ),
                ],
              ),
            if (totalRentedMonthsValue.isNotEmpty)
              ColumnHeaderDropdownSearch(
                label: "Remaining months occupancy",
                options: const ['NON_RENT_SELFOCCUPIED', 'NON_RENT_UNOCCUPIED'],
                onChanged: (value) {
                  widget.remainingMonthOccupancy(value);
                },
                selectedValue: remainingMonthOccupancyValue,
                textSize: 14.sp,
                isRequired: true,
                enableLocal: true,
                module: Modules.PT,
                validator: (val) {
                  if (!isNotNullOrEmpty(val)) {
                    return 'Required Field';
                  }
                  return null;
                },
              ),
            RequiredText(
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
              required: true,
              text: getLocalizedString(
                i18.propertyTax.EMP_BUILT_AREA,
                module: Modules.PT,
              ),
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_BUILT_AREA,
                  module: Modules.PT,
                ),
                controller: widget.builtUpSpace,
                keyboardType: AppPlatforms.platformKeyboardType(),
                textInputAction: TextInputAction.done,
              ),
            ),
            const SizedBox.shrink(),
          ],
        ),
      ),
    );
  }
}

//Owner Form Widget
// ignore: must_be_immutable
class PropertyTypeOwnerFormWidget extends StatefulWidget {
  Function(String?) gender;
  Function(String?) relationShip;
  Function(String?) specialCategory;
  Function(String?) specialCategoryDocumentType;
  TextEditingController ownerName;
  TextEditingController mobileNumber;
  TextEditingController guardiansName;
  TextEditingController email;
  TextEditingController ownerDocumentId;
  TextEditingController ownerAddress;
  VoidCallback? onRemove;
  int? index, widgetLength;
  String selectedGenderValue;
  String relationShipValue;
  String specialCategoryValue;
  String specialCategoryDocumentTypeValue;

  PropertyTypeOwnerFormWidget({
    super.key,
    required this.gender,
    required this.relationShip,
    required this.specialCategory,
    required this.specialCategoryDocumentType,
    required this.ownerName,
    required this.mobileNumber,
    required this.guardiansName,
    required this.email,
    required this.ownerDocumentId,
    required this.ownerAddress,
    this.index,
    this.widgetLength,
    this.onRemove,
    this.selectedGenderValue = '',
    this.relationShipValue = '',
    this.specialCategoryValue = '',
    this.specialCategoryDocumentTypeValue = '',
  });

  @override
  State<PropertyTypeOwnerFormWidget> createState() =>
      _PropertyTypeOwnerFormWidgetState();
}

class _PropertyTypeOwnerFormWidgetState
    extends State<PropertyTypeOwnerFormWidget> {
  final _ptController = Get.find<PropertiesTaxController>();
  late String selectedGenderValue,
      relationShipValue,
      specialCategoryValue,
      specialCategoryDocumentTypeValue;

  @override
  void initState() {
    selectedGenderValue = widget.selectedGenderValue;
    relationShipValue = widget.relationShipValue;
    specialCategoryValue = widget.specialCategoryValue;
    specialCategoryDocumentTypeValue = widget.specialCategoryDocumentTypeValue;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.symmetric(vertical: 10.h),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.r),
        border: Border.all(
          color: BaseConfig.borderColor1,
          width: 1,
        ),
      ),
      child: Padding(
        padding: EdgeInsets.all(4.w),
        child: Column(
          spacing: 10.h,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                BigTextNotoSans(
                  text: 'Owner ${widget.index! + 1}',
                  fontWeight: FontWeight.w600,
                  color: BaseConfig.appThemeColor1,
                  size: 13.h,
                ),
                if (widget.index != 0)
                  IconButton(
                    onPressed: widget.onRemove,
                    icon: const Icon(
                      Icons.delete,
                      color: BaseConfig.appThemeColor1,
                    ),
                  ),
              ],
            ),
            RequiredText(
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
              required: true,
              text: getLocalizedString(
                i18.propertyTax.EMP_OWNER_NAME,
                module: Modules.PT,
              ),
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_OWNER_NAME,
                  module: Modules.PT,
                ),
                controller: widget.ownerName,
                textInputAction: TextInputAction.done,
              ),
            ),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_GENDER,
                module: Modules.PT,
              ),
              options: (_ptController.empMdmsResModel?.mdmsResEmp
                          ?.commonMastersObps?.genderType ??
                      [])
                  .map((data) => data.code.toString())
                  .toList(),
              onChanged: (value) {
                widget.gender(value);
                setState(() {
                  selectedGenderValue = value ?? '';
                });
              },
              selectedValue: selectedGenderValue,
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
            RequiredText(
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
              required: true,
              text: getLocalizedString(
                i18.propertyTax.EMP_MOBILE_NUMBER,
                module: Modules.PT,
              ),
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_MOBILE_NUMBER,
                  module: Modules.PT,
                ),
                keyboardType: AppPlatforms.platformKeyboardType(),
                controller: widget.mobileNumber,
                textInputAction: TextInputAction.done,
                maxLength: 10,
                inputFormatters: [
                  FilteringTextInputFormatter.digitsOnly,
                  LengthLimitingTextInputFormatter(10),
                ],
              ),
            ),
            RequiredText(
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
              required: true,
              text: getLocalizedString(
                i18.propertyTax.EMP_GUARDIAN_NAME,
                module: Modules.PT,
              ),
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_GUARDIAN_NAME,
                  module: Modules.PT,
                ),
                controller: widget.guardiansName,
                textInputAction: TextInputAction.done,
              ),
            ),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_RELATIONSHIP,
                module: Modules.PT,
              ),
              options: const [
                'PT_FORM3_FATHER',
                'PT_FORM3_HUSBAND',
              ],
              onChanged: (value) {
                widget.relationShip(value);
                dPrint('relationShip: $value');
              },
              selectedValue: relationShipValue,
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
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_SPECIAL_CATEGORY,
                module: Modules.PT,
              ),
              options: _ptController
                      .empMdmsResModel?.mdmsResEmp?.propertyTax?.ownerType
                      ?.map(
                        (data) => data.code ?? '',
                      )
                      .toList() ??
                  [],
              onChanged: (value) {
                setState(() {
                  specialCategoryValue = value!;
                  widget.specialCategory(value);
                });

                dPrint('value: $value');
              },
              // onChanged: (value) {
              //   widget.specialCategory(value);
              // },
              selectedValue: specialCategoryValue,
              textSize: 14.sp,
              isRequired: true,
              enableLocal: true,
              module: Modules.PT,
              validator: (val) {
                if (!isNotNullOrEmpty(val)) {
                  return 'Required Field';
                }
                return null;
              },
            ),
            if (isNotNullOrEmpty(specialCategoryValue) &&
                specialCategoryValue != 'NONE')
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  ColumnHeaderDropdownSearch(
                    label: "Document Type",
                    options: _ptController
                        .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
                        .where(
                          (doc) => doc.code == 'OWNER.SPECIALCATEGORYPROOF',
                        )
                        .expand((doc) => doc.dropdownData ?? [])
                        .where(
                          (data) =>
                              data.parentValue.contains(specialCategoryValue),
                        )
                        .map(
                          (data) => getLocalizedString(
                            data.code.toString(),
                            module: Modules.PT,
                          ),
                        )
                        .toList(),
                    onChanged: (value) {
                      setState(() {
                        specialCategoryDocumentTypeValue = value!;
                      });
                      widget.specialCategory(value);
                      dPrint('Selected Document Type: $value');
                    },
                    selectedValue: specialCategoryDocumentTypeValue,
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
                  const SizedBox(height: 10),
                  RequiredText(
                    fontWeight: FontWeight.w600,
                    fontSize: 13.h,
                    required: true,
                    text: 'Document ID',
                  ),
                  const SizedBox(height: 5),
                  SizedBox(
                    height: 55,
                    child: TextFormFieldApp(
                      radius: 8,
                      hintText: 'Document ID',
                      controller: widget.ownerDocumentId,
                      textInputAction: TextInputAction.done,
                    ),
                  ),
                ],
              ),
            // if (isNotNullOrEmpty(specialCategoryValue) &&
            //     specialCategoryValue != 'NONE')
            //   Column(
            //     crossAxisAlignment: CrossAxisAlignment.start,
            //     children: [
            //       ColumnHeaderDropdownSearch(
            //         label: "Document Type",
            //         options: _ptController
            //             .empMdmsResModel!.mdmsResEmp!.propertyTax!.documents!
            //             .where(
            //               (doc) =>
            //                   doc.code.toString() ==
            //                   'OWNER.SPECIALCATEGORYPROOF',
            //             )
            //             .expand(
            //               (doc) => doc.dropdownData as List<dynamic>,
            //             )
            //             .map(
            //               (data) => getLocalizedString(
            //                 data.code.toString(),
            //                 module: Modules.PT,
            //               ),
            //             )
            //             .toList(),
            //         onChanged: (value) {
            //           widget.specialCategory(value);
            //         },
            //         selectedValue: specialCategoryDocumentTypeValue,
            //         textSize: 14.sp,
            //         isRequired: true,
            //         enableLocal: true,
            //         validator: (val) {
            //           if (!isNotNullOrEmpty(val)) {
            //             return 'Required Field';
            //           }
            //           return null;
            //         },
            //       ),
            //       const SizedBox(
            //         height: 10,
            //       ),
            //       RequiredText(
            //         fontWeight: FontWeight.w600,
            //         fontSize: 13.h,
            //         required: true,
            //         text: 'Document ID',
            //       ),
            //       const SizedBox(
            //         height: 5,
            //       ),
            //       SizedBox(
            //         height: 55,
            //         child: TextFormFieldApp(
            //           radius: 8,
            //           hintText: 'Document ID',
            //           controller: widget.ownerDocumentId,
            //           textInputAction: TextInputAction.done,
            //         ),
            //       ),
            //     ],
            //   ),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.propertyTax.EMP_EMAIL,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w600,
              size: 13.h,
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_EMAIL,
                  module: Modules.PT,
                ),
                controller: widget.email,
                textInputAction: TextInputAction.done,
              ),
            ),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.propertyTax.EMP_OWNER_ADDRESS,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w600,
              size: 13.h,
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_OWNER_ADDRESS,
                  module: Modules.PT,
                ),
                controller: widget.ownerAddress,
                textInputAction: TextInputAction.done,
              ),
            ),
            const SizedBox.shrink(),
          ],
        ),
      ),
    );
  }
}

// ignore: must_be_immutable
class PropertyAuthorizedPersonnelDetail extends StatefulWidget {
  TextEditingController authorizedInstitutionName;
  Function(String?) authorizedInstitutionType;
  TextEditingController authorizedOwnerName;
  TextEditingController authorizedLandlineNumber;
  TextEditingController authorizedMobileNumber;
  TextEditingController authorizedDesignation;
  TextEditingController authorizedEmail;
  TextEditingController authorizedOwnerAddress;
  String authorizedInstitutionTypeValue;
  String ownershipType;

  PropertyAuthorizedPersonnelDetail({
    super.key,
    required this.authorizedInstitutionName,
    required this.authorizedInstitutionType,
    required this.authorizedOwnerName,
    required this.authorizedLandlineNumber,
    required this.authorizedMobileNumber,
    required this.authorizedDesignation,
    required this.authorizedEmail,
    required this.authorizedOwnerAddress,
    required this.authorizedInstitutionTypeValue,
    required this.ownershipType,
  });

  @override
  State<PropertyAuthorizedPersonnelDetail> createState() =>
      _PropertyAuthorizedPersonnelDetailState();
}

class _PropertyAuthorizedPersonnelDetailState
    extends State<PropertyAuthorizedPersonnelDetail> {
  final _ptController = Get.find<PropertiesTaxController>();

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.symmetric(vertical: 10.h),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.r),
        border: Border.all(
          color: BaseConfig.borderColor1,
          width: 1,
        ),
      ),
      child: Padding(
        padding: EdgeInsets.all(4.w),
        child: Column(
          spacing: 10.h,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox.shrink(),
            BigTextNotoSans(
              text: 'Authorized Person Details',
              fontWeight: FontWeight.w600,
              color: BaseConfig.appThemeColor1,
              size: 13.h,
            ),
            RequiredText(
              text: getLocalizedString(
                i18.propertyTax.EMP_INSTITUTION_NAME,
                module: Modules.PT,
              ),
              required: true,
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_INSTITUTION_NAME,
                  module: Modules.PT,
                ),
                keyboardType: AppPlatforms.platformKeyboardType(),
                controller: widget.authorizedInstitutionName,
                textInputAction: TextInputAction.done,
              ),
            ),
            ColumnHeaderDropdownSearch(
              label: getLocalizedString(
                i18.propertyTax.EMP_INSTITUTION_TYPE,
                module: Modules.PT,
              ),
              options: _ptController.empMdmsResModel?.mdmsResEmp?.propertyTax
                      ?.subOwnerShipCategory
                      ?.where(
                        (type) =>
                            type.ownerShipCategory == widget.ownershipType,
                      )
                      .map(
                        (data) => isNotNullOrEmpty(data.code)
                            ? 'COMMON_MASTERS_OWNERSHIPCATEGORY_${data.code}'
                            : '',
                      )
                      .toList() ??
                  [],
              onChanged: (value) {
                widget.authorizedInstitutionType(
                  value?.split('COMMON_MASTERS_OWNERSHIPCATEGORY_').last,
                );
                dPrint('Value: $value');
              },
              selectedValue: isNotNullOrEmpty(
                widget.authorizedInstitutionTypeValue,
              )
                  ? 'COMMON_MASTERS_OWNERSHIPCATEGORY_${widget.authorizedInstitutionTypeValue}'
                  : null,
              textSize: 14.sp,
              isRequired: true,
              enableLocal: true,
              module: Modules.PT,
              validator: (val) {
                if (!isNotNullOrEmpty(val)) {
                  return 'Required Field';
                }
                return null;
              },
            ),
            RequiredText(
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
              required: true,
              text: getLocalizedString(
                i18.propertyTax.EMP_OWNER_NAME,
                module: Modules.PT,
              ),
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_OWNER_NAME,
                  module: Modules.PT,
                ),
                keyboardType: AppPlatforms.platformKeyboardType(),
                controller: widget.authorizedOwnerName,
                textInputAction: TextInputAction.done,
              ),
            ),
            RequiredText(
              text: getLocalizedString(
                i18.propertyTax.EMP_LANDLINE_NUMBER,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
              required: true,
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_LANDLINE_NUMBER,
                  module: Modules.PT,
                ),
                keyboardType: AppPlatforms.platformKeyboardType(),
                controller: widget.authorizedLandlineNumber,
                textInputAction: TextInputAction.done,
                maxLength: 11,
                inputFormatters: [
                  FilteringTextInputFormatter.digitsOnly,
                  LengthLimitingTextInputFormatter(11),
                ],
              ),
            ),
            RequiredText(
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
              required: true,
              text: getLocalizedString(
                i18.propertyTax.EMP_MOBILE_NUMBER,
                module: Modules.PT,
              ),
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_MOBILE_NUMBER,
                  module: Modules.PT,
                ),
                keyboardType: AppPlatforms.platformKeyboardType(),
                controller: widget.authorizedMobileNumber,
                textInputAction: TextInputAction.done,
                maxLength: 10,
                inputFormatters: [
                  FilteringTextInputFormatter.digitsOnly,
                  LengthLimitingTextInputFormatter(10),
                ],
              ),
            ),
            RequiredText(
              text: getLocalizedString(
                i18.propertyTax.EMP_DESIGNATION,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
              required: true,
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_DESIGNATION,
                  module: Modules.PT,
                ),
                keyboardType: AppPlatforms.platformKeyboardType(),
                controller: widget.authorizedDesignation,
                textInputAction: TextInputAction.done,
              ),
            ),
            BigTextNotoSans(
              text: getLocalizedString(
                i18.propertyTax.EMP_EMAIL,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w600,
              size: 13.h,
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_EMAIL,
                  module: Modules.PT,
                ),
                keyboardType: AppPlatforms.platformKeyboardType(),
                controller: widget.authorizedEmail,
                textInputAction: TextInputAction.done,
              ),
            ),
            RequiredText(
              text: getLocalizedString(
                i18.propertyTax.EMP_OWNER_ADDRESS,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w600,
              fontSize: 13.h,
              required: true,
            ),
            SizedBox(
              height: 55,
              child: TextFormFieldApp(
                radius: 8,
                hintText: getLocalizedString(
                  i18.propertyTax.EMP_OWNER_ADDRESS,
                  module: Modules.PT,
                ),
                keyboardType: AppPlatforms.platformKeyboardType(),
                controller: widget.authorizedOwnerAddress,
                textInputAction: TextInputAction.done,
              ),
            ),
            SizedBox(height: 2.h),
          ],
        ),
      ),
    );
  }
}
