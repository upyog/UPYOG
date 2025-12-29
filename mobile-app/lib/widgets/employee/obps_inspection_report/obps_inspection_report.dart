import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/obps_dynamic_form_controller.dart';
import 'package:mobile_app/model/employee/emp_bpa_model/emp_bpa_model.dart'
    as bp;
import 'package:mobile_app/model/employee/emp_mdms_model/emp_mdms_model.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/build_card.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/required_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class ObpsInspectionReport extends StatefulWidget {
  final Orientation o;
  final bp.Item item;
  const ObpsInspectionReport({super.key, required this.o, required this.item});

  @override
  State<ObpsInspectionReport> createState() => _ObpsInspectionReportState();
}

class _ObpsInspectionReportState extends State<ObpsInspectionReport> {
  final _authController = Get.find<AuthController>();
  final _dynamicFormController = Get.find<ObpsDynamicFormController>();
  final _fileController = Get.find<FileController>();

  bp.Item get item => widget.item;

  CheckList? checkList;

  @override
  void initState() {
    super.initState();
    _dynamicFormController.clearField();
    init();
  }

  init() {
    try {
      checkList = _dynamicFormController.filterCheckList(
        applicationType: item
            .businessObject!.additionalDetails!.applicationType!
            .toUpperCase(),
        serviceType:
            item.businessObject!.additionalDetails!.serviceType!.toUpperCase(),
        businessService: item.businessObject!.businessService!,
        wfState: item.processInstance!.state!.state!.toUpperCase(),
      );
    } catch (e) {
      dPrint('Error while fetching checklist: $e');
    }
  }

  void onCheckChange({
    required String section,
    String? selectedName,
    String? remark,
  }) {
    final index = _dynamicFormController.customChecks
        .indexWhere((element) => element.sectionName == section);

    if (index != -1) {
      _dynamicFormController.customChecks[index]
        ..selectedName = selectedName ??
            _dynamicFormController.customChecks[index].selectedName
        ..remark = remark ?? _dynamicFormController.customChecks[index].remark;
    } else {
      _dynamicFormController.customChecks.add(
        CustomCheck(
          sectionName: section,
          selectedName: selectedName,
          remark: remark,
        ),
      );
    }

    dPrint('customChecks: ${_dynamicFormController.customChecks.toString()}');

    setState(() {});
  }

  addImage(String section, int index) async {
    final pickFile = await _fileController.selectAndPickFile(isPop: false);
    if (pickFile.$1 == null) return;

    final fileType = _fileController.getFileTypeString(pickFile.$1!.path);

    final fireId = await _fileController.postFile(
      token: _authController.token!.accessToken!,
      tenantId: BaseConfig.STATE_TENANT_ID,
      module: BusinessServicesEmp.BPA.name,
    );

    if (fireId == null) return;

    final newDoc = CustomDoc(
      name: '',
      filePath: pickFile.$1!.path,
      docType: fileType.$2.name,
      sectionName: section,
      fileStoreId: fireId,
    );

    final existingIndex = _dynamicFormController.customDocs
        .indexWhere((doc) => doc.sectionName == section);

    if (existingIndex != -1) {
      _dynamicFormController.customDocs[existingIndex] = newDoc;
    } else {
      _dynamicFormController.customDocs.add(newDoc);
    }

    dPrint(newDoc.toString());
    setState(() {});
  }

  removeImage(String section, String name) {
    _dynamicFormController.customDocs.removeWhere(
      (element) => element.sectionName == section && element.name == name,
    );
    setState(() {});
  }

  RxBool sectionHasFile(String section) {
    bool hasFile = _dynamicFormController.customDocs.any(
      (element) =>
          element.sectionName == section && isNotNullOrEmpty(element.filePath),
    );
    return RxBool(hasFile);
  }

  // Function to show Cupertino date picker
  Future<void> selectDate() async {
    showDatePickerModal(
      context: context,
      selectedDate: _dynamicFormController.selectedDate,
      onDateSelected: (DateTime newDate) {
        setState(() {
          _dynamicFormController.selectedDate = newDate;
        });
      },
      onClear: () {
        setState(() {
          _dynamicFormController.selectedDate = null;
        });
      },
      onToday: () {
        setState(() {
          _dynamicFormController.selectedDate = DateTime.now();
        });
      },
    );
  }

  // Function to show Cupertino time picker
  Future<void> selectTime() async {
    showModalBottomSheet(
      context: context,
      builder: (BuildContext context) {
        return SizedBox(
          height: 300.h,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Align(
                alignment: Alignment.centerRight,
                child: IconButton(
                  icon: const Icon(
                    Icons.close,
                    color: BaseConfig.appThemeColor1,
                  ),
                  onPressed: () => Get.back(),
                ),
              ),
              Expanded(
                child: CupertinoDatePicker(
                  initialDateTime: DateTime.now(),
                  mode: CupertinoDatePickerMode.time,
                  onDateTimeChanged: (DateTime newTime) {
                    setState(() {
                      _dynamicFormController.selectedTime =
                          TimeOfDay.fromDateTime(newTime);
                    });
                  },
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return BuildCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          BigTextNotoSans(
            text: getLocalizedString(
              i18.building.INSPECTION_REPORT,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.w600,
            size: 16.sp,
          ),
          SizedBox(
            height: 10.h,
          ),
          ColumnHeaderTextField(
            label: getLocalizedString(
              i18.building.INSPECTION_DATE,
              module: Modules.BPA,
            ),
            hintText: _dynamicFormController.selectedDate != null
                ? _dynamicFormController.getSelectedDate()
                : 'dd-mm-yyyy',
            icon: Icons.calendar_today,
            textSize: 14.sp,
            isRequired: true,
            readOnly: true,
            textInputAction: TextInputAction.done,
            suffixIcon: IconButton(
              onPressed: selectDate,
              icon: const Icon(
                Icons.calendar_today,
                color: BaseConfig.appThemeColor1,
              ),
            ),
          ),
          SizedBox(height: 10.h),

          // Cupertino TimePicker for Inspection Time
          ColumnHeaderTextField(
            label: getLocalizedString(i18.common.EMP_TIME),
            hintText: _dynamicFormController.selectedTime != null
                ? _dynamicFormController.getSelectedTime(context)
                : '00:00',
            icon: Icons.access_time,
            textSize: 14.sp,
            isRequired: true,
            readOnly: true,
            textInputAction: TextInputAction.done,
            suffixIcon: IconButton(
              onPressed: selectTime,
              icon: const Icon(
                Icons.schedule,
                color: BaseConfig.appThemeColor1,
              ),
            ),
          ),
          SizedBox(
            height: 20.h,
          ),
          const Divider(),
          SizedBox(
            height: 10.h,
          ),
          BigTextNotoSans(
            text: getLocalizedString(
              i18.building.INSPECTION_CHECKLIST,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.w600,
            size: 16.sp,
          ),
          SizedBox(
            height: 10.h,
          ),
          ListView.builder(
            itemCount: checkList?.questions?.length ?? 0,
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemBuilder: (context, index) {
              final question = checkList!.questions![index];
              return Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  ColumnHeaderRadioButton(
                    label: getLocalizedString(
                      question.question,
                      module: Modules.BPA,
                    ),
                    options: question.fieldType!.split('/').toList(),
                    selectedValue: _dynamicFormController.customChecks
                        .firstWhereOrNull(
                          (e) => e.sectionName == question.question,
                        )
                        ?.selectedName,
                    onChanged: (value) {
                      dPrint('value: $value');
                      onCheckChange(
                        section: question.question!,
                        selectedName: value,
                      );
                    },
                    textSize: 14.sp,
                    isRequired: true,
                  ),
                  SizedBox(
                    height: 10.h,
                  ),
                  ColumnHeaderTextField(
                    label: 'Remarks',
                    hintText: '',
                    textSize: 14.sp,
                    onChange: (remark) => onCheckChange(
                      section: question.question!,
                      remark: remark,
                    ),
                    onSave: (remark) => onCheckChange(
                      section: question.question!,
                      remark: remark,
                    ),
                    keyboardType: TextInputType.text,
                    textInputAction: TextInputAction.done,
                  ),
                  SizedBox(
                    height: 10.h,
                  ),
                ],
              );
            },
          ),
          SizedBox(
            height: 10.h,
          ),
          const Divider(),
          SizedBox(
            height: 10.h,
          ),
          BigTextNotoSans(
            text: getLocalizedString(
              i18.building.INSPECTION_DOCUMENTS,
              module: Modules.BPA,
            ),
            fontWeight: FontWeight.w600,
            size: 16.sp,
          ),
          SizedBox(
            height: 10.h,
          ),
          ListView.builder(
            itemCount: checkList?.docTypes?.length ?? 0,
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemBuilder: (context, index) {
              final doc = checkList!.docTypes![index];
              return Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Expanded(
                        child: RequiredText(
                          text:
                              getLocalizedString(doc.code, module: Modules.BPA),
                          fontSize: 14.sp,
                          required: doc.required ?? false,
                        ),
                      ),
                      IconButton.filled(
                        style: ButtonStyle(
                          backgroundColor: const WidgetStatePropertyAll<Color>(
                            BaseConfig.appThemeColor1,
                          ),
                          shape: WidgetStatePropertyAll<OutlinedBorder>(
                            RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10.r),
                            ),
                          ),
                        ),
                        icon: const Icon(
                          Icons.add,
                          color: BaseConfig.mainBackgroundColor,
                        ),
                        onPressed: () {
                          addImage(doc.code!, index);
                        },
                      ),
                    ],
                  ),
                  SizedBox(
                    height: 10.h,
                  ),
                  Obx(
                    () => sectionHasFile(doc.code!).value
                        ? fileViewBuilder(doc.code!)
                        : const SizedBox.shrink(),
                  ),
                ],
              );
            },
          ),
        ],
      ),
    );
  }

  Widget fileViewBuilder(String sectionName) {
    final filteredImages = _dynamicFormController.customDocs
        .where(
          (element) =>
              element.sectionName == sectionName &&
              isNotNullOrEmpty(element.filePath),
        )
        .toList();

    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 10.w),
      child: GridView.builder(
        itemCount: filteredImages.length,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          crossAxisSpacing: 10.0,
          mainAxisSpacing: 10.0,
          mainAxisExtent: 110.0, //110
        ),
        shrinkWrap: true,
        itemBuilder: (context, index) {
          final fileDoc = filteredImages[index];

          final (fileIcon, fileType) =
              _fileController.getFileType(fileDoc.filePath!);

          return Tooltip(
            message: fileDoc.name,
            child: Stack(
              children: [
                SizedBox(
                  height: 80.h,
                  width: 60.w,
                  child: Column(
                    children: [
                      ClipRRect(
                        borderRadius: BorderRadius.circular(10.r),
                        child: fileType == FileExtType.pdf
                            ? Icon(
                                fileIcon,
                                size: 40.sp,
                                color: Colors.grey.shade600,
                              )
                            : Image.file(
                                File(fileDoc.filePath!),
                                fit: BoxFit.cover,
                              ),
                      ),
                      SmallTextNotoSans(
                        text: fileDoc.name,
                        maxLine: 1,
                        textOverflow: TextOverflow.ellipsis,
                      ),
                    ],
                  ),
                ),
                Positioned(
                  right: 30,
                  child: SizedBox(
                    height: 30.h,
                    width: 30.w,
                    child: IconButton.filled(
                      style: IconButton.styleFrom(
                        backgroundColor:
                            BaseConfig.mainBackgroundColor.withAlpha(50),
                      ),
                      onPressed: () {
                        removeImage(sectionName, fileDoc.name);
                      },
                      icon: Icon(
                        Icons.delete_outline,
                        size: 16.sp,
                        color: BaseConfig.redColor,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
