import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/model/employee/emp_mdms_model/emp_mdms_model.dart';
import 'package:mobile_app/repository/core_repository.dart';
import 'package:mobile_app/services/mdms_service.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';

class CustomCheck {
  String? sectionName, remark, selectedName;

  CustomCheck({this.sectionName, this.remark, this.selectedName});

  @override
  String toString() {
    return 'CustomCheck{sectionName: $sectionName, remark: $remark, selectedName: $selectedName}';
  }
}

class CustomDoc {
  final String name;
  final String? filePath;
  final String? docType;
  final String? sectionName;
  final String fileStoreId;

  CustomDoc({
    required this.name,
    this.filePath,
    this.docType,
    this.sectionName,
    required this.fileStoreId,
  });

  @override
  String toString() {
    return 'CustomDoc{name: $name, filePath: $filePath, docType: $docType, sectionName: $sectionName, fileStoreId: $fileStoreId}';
  }
}

class DocFile {
  String sectionName;
  String fileStoreId;

  DocFile({required this.sectionName, required this.fileStoreId});
}

class ObpsDynamicFormController extends GetxController {
  List<TextEditingController> textControllers = [];

  //Model
  late EmpMdmsResModel empMdmsResModel;
  BpaObps? bpaObps;
  CheckList? checkList;

  //Radio check multiple
  List<CustomCheck> customChecks = [];

  //Document upload
  // List<SectionDoc> sectionsDocs = [];
  List<CustomDoc> customDocs = [];

  Set<DocFile> docFiles = {};

  // Date and time selection
  DateTime? selectedDate;
  TimeOfDay? selectedTime;
  String selectedTime24String = '';
  String selectedDateString = '';

  void clearField() {
    customChecks.clear();
    customDocs.clear();
    selectedDate = null;
    selectedTime = null;
  }

  String getSelectedTime(context) {
    final time = selectedTime!.format(context);
    final hour = selectedTime!.hour;
    final minute = selectedTime!.minute;
    dPrint('Selected Time in 24-hour format: $hour:$minute');
    selectedTime24String = '$hour:$minute';
    return time;
  }

  String getSelectedDate() {
    selectedDateString =
        '${selectedDate!.year}-${selectedDate!.month}-${selectedDate!.day}';
    return '${selectedDate!.day}-${selectedDate!.month}-${selectedDate!.year}';
  }

  // EMP - OBPS - Mdms service call
  Future<void> getMdmsObps() async {
    try {
      final query = {
        'tenantId': BaseConfig.STATE_TENANT_ID,
      };

      final empRes = await CoreRepository.getMdmsDynamic(
        query: query,
        body: getEmpMdmsBodyChecklist(),
      );

      empMdmsResModel = EmpMdmsResModel.fromJson(empRes);
      bpaObps = empMdmsResModel.mdmsResEmp?.bpaObps;
    } catch (e, s) {
      dPrint('GetMdmsObps Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  CheckList? filterCheckList({
    required String applicationType,
    required String serviceType,
    required String businessService,
    required String wfState,
  }) {
    final riskType = getRiskType(businessService.toUpperCase());

    if (isNotNullOrEmpty(bpaObps?.checkList)) {
      for (var element in bpaObps!.checkList!) {
        if (element.applicationType?.toUpperCase() == applicationType &&
            element.serviceType?.toUpperCase() == serviceType &&
            element.riskType?.toUpperCase() == riskType &&
            element.wfState?.toUpperCase() == wfState) {
          checkList = element;
          return element;
        }
      }
    }
    return null;
  }

  // Validation function
  bool validatedField() {
    try {
      if (selectedDate == null) {
        snackBar('Error', 'Please select the inspection date.', Colors.red);
        return false;
      }

      if (selectedTime == null) {
        snackBar('Error', 'Please select the inspection time.', Colors.red);
        return false;
      }

      for (var question in checkList!.questions!) {
        var check = customChecks
            .firstWhereOrNull((e) => e.sectionName == question.question);

        if (check == null || check.selectedName == null) {
          snackBar(
            'Error',
            'Please select an option for ${getLocalizedString(
              question.question,
              module: Modules.BPA,
            )}.',
            Colors.red,
          );
          return false;
        }
      }

      for (var doc in checkList!.docTypes!) {
        bool hasFile = customDocs.any(
          (element) => element.sectionName == doc.code,
        );

        if (doc.required == true && !hasFile) {
          snackBar(
            'Error',
            'Please upload the document for ${getLocalizedString(
              doc.code,
              module: Modules.BPA,
            )}.',
            Colors.red,
          );
          return false;
        }
      }
      return true;
    } catch (e) {
      dPrint('validatedField error: $e');
      return false;
    }
  }
}
