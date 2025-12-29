import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:mobile_app/components/acknowlegement/acknowledgement.dart';
import 'package:mobile_app/components/acknowlegement/emp_acknowledge_screen.dart';
import 'package:mobile_app/components/bottom_sheet.dart';
import 'package:mobile_app/components/drodown_button.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/text_form_field.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/bpa_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/fire_noc_controller.dart';
import 'package:mobile_app/controller/grievance_controller.dart';
import 'package:mobile_app/controller/obps_dynamic_form_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/controller/trade_license_controller.dart';
import 'package:mobile_app/controller/water_controller.dart';
import 'package:mobile_app/model/citizen/bpa_model/bpa_model.dart' as bp;
import 'package:mobile_app/model/citizen/fire_noc/fire_noc.dart' as noc;
import 'package:mobile_app/model/citizen/grievance/grievance.dart' as gr;
import 'package:mobile_app/model/citizen/trade_license/trade_license.dart';
import 'package:mobile_app/model/citizen/water_sewerage/sewerage.dart' as s;
import 'package:mobile_app/model/citizen/water_sewerage/water.dart' as w;
import 'package:mobile_app/model/employee/employee_model/employees_model.dart';
import 'package:mobile_app/model/request/emp_property_action_request/emp_property_action_request_model.dart';
import 'package:mobile_app/services/secure_storage_service.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/icon_text_fill_button.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class InboxController extends GetxController {
  //Instance of controllers
  final fileController = Get.find<FileController>();
  final authController = Get.find<AuthController>();
  final timelineController = Get.find<TimelineController>();

  //Text editing controller
  final commentsController = TextEditingController();

  RxBool isLoading = false.obs;
  String? fileId;
  String assigneeUuid = '', assigneeName = '';

  @override
  void onClose() {
    super.onClose();
    commentsController.dispose();
  }

  //Emp Grievance action
  List<Employee>? filterAssigneesPGR() {
    final grievanceController = Get.find<GrievanceController>();
    final serviceCode =
        grievanceController.serviceWrapper?.service?.serviceCode;
    final service = grievanceController.getDepartment(serviceCode ?? '');

    return timelineController.employeeModel?.employees
        ?.where(
          (emp) =>
              emp.assignments?.any(
                (assignment) => assignment.department == service?.department,
              ) ??
              false,
        )
        .toList();
  }

  // Action dialog for all modules
  actionDialogue(
    context, {
    required String action,
    required String workFlowId,
    Modules module = Modules.COMMON,
    required ModulesEmp sectionType,
    required BusinessService businessService,
    required String tenantId,
    bool isCitizen = false,
  }) {
    isLoading.value = false;
    dPrint('Localize Module: ${module.name}');
    dPrint('${i18.common.CS_COMMON}$action');
    showDialog(
      context: context,
      useSafeArea: false,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        backgroundColor: BaseConfig.mainBackgroundColor,
        titlePadding: EdgeInsets.symmetric(horizontal: 10.h),
        insetPadding: EdgeInsets.symmetric(horizontal: 10.h),
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Align(
              alignment: Alignment.topRight,
              child: IconButton(
                icon: const Icon(
                  Icons.cancel_presentation_outlined,
                  size: 30,
                  color: BaseConfig.appThemeColor1,
                ),
                onPressed: () => Get.back(),
              ),
            ),
            Center(
              child: (isCitizen &&
                      sectionType.name == ModulesEmp.BPA_SERVICES.name)
                  ? Tooltip(
                      message: getLocalizedString(
                        i18.building.BPA_FORWARD_APPLICATION_HEADER,
                        module: module,
                      ),
                      child: MediumTextNotoSans(
                        text: getLocalizedString(
                          i18.building.BPA_FORWARD_APPLICATION_HEADER,
                          module: module,
                        ),
                        fontWeight: FontWeight.w600,
                        textOverflow: TextOverflow.ellipsis,
                        size: 17,
                      ),
                    )
                  : sectionType == ModulesEmp.PGR_SERVICES
                      ? Tooltip(
                          message: getLocalizedString(
                            i18.grievance.EMP_ASSIGN,
                            module: module,
                          ),
                          child: MediumTextNotoSans(
                            text: getLocalizedString(
                              i18.grievance.EMP_ASSIGN,
                              module: module,
                            ),
                            fontWeight: FontWeight.w600,
                            textOverflow: TextOverflow.ellipsis,
                            size: 17,
                          ),
                        )
                      : SizedBox(
                          width: 200,
                          child: Center(
                            child: Tooltip(
                              message: getLocalizedString(
                                'WF_${action}_APPLICATION',
                              ),
                              child: MediumTextNotoSans(
                                text: getLocalizedString(
                                  'WF_${action}_APPLICATION',
                                ),
                                textOverflow: TextOverflow.ellipsis,
                                fontWeight: FontWeight.w600,
                                size: 17,
                              ),
                            ),
                          ),
                        ),
            ),
          ],
        ),
        actions: [
          Obx(
            () => FilledButtonApp(
              width: Get.width,
              backgroundColor: BaseConfig.redColor2,
              isLoading: isLoading.value,
              circularColor: BaseConfig.mainBackgroundColor,
              onPressed: () async {
                try {
                  FocusScope.of(context).unfocus();

                  if (commentsController.text.isEmpty) {
                    return snackBar(
                      'Error',
                      'Please fill in the comments before submitting',
                      BaseConfig.redColor1,
                    );
                  }

                  isLoading.value = true;

                  if (fileController.isSelectedFile.value) {
                    fileId = await fileController.postFile(
                      token: authController.token!.accessToken!,
                      tenantId: tenantId,
                      module: businessService.name,
                    );
                  }
                  dPrint('File ID: $fileId');

                  final (res, applicationNo) = await getDocModel(
                    module: sectionType,
                    action: action,
                    workflowId: workFlowId,
                    isCitizen: isCitizen,
                  );

                  commentsController.clear();

                  isLoading.value = false;

                  dPrint('Take Action App No: $applicationNo');

                  if (res) {
                    snackBar(
                      'Success',
                      'Application $action successfully',
                      Colors.green,
                    );
                    if (isCitizen) {
                      if (sectionType == ModulesEmp.BPA_SERVICES) {
                        Get.off(
                          () => AcknowledgementWidget(
                            applicationNo: Get.find<BpaController>()
                                    .bpa
                                    .bpaele
                                    ?.first
                                    .applicationNo ??
                                '',
                          ),
                        );
                      } else {
                        snackBar(
                          'Error',
                          'Inbox ${sectionType.name} not implemented!',
                          Colors.red,
                        );
                      }
                    } else {
                      Get.off(
                        () => EmpAcknowledgeScreen(
                          mainTitle: 'Update Acknowledgement',
                          subTitle: 'Application Updated Successfully!',
                          appIdName: 'Application Number',
                          applicationNo: applicationNo ?? 'N/A',
                          message:
                              'The Notification has been sent to registered mobile number of the user/owner.',
                        ),
                      );
                    }
                  } else {
                    snackBar(
                      'Error',
                      'Failed to $action application',
                      BaseConfig.redColor1,
                    );
                  }
                } catch (e) {
                  isLoading.value = false;
                  dPrint('Inbox Error: $e');
                }
              },
              text: (isCitizen &&
                      (sectionType.name == ModulesEmp.BPA_SERVICES.name))
                  ? getLocalizedString(
                      'BPA_${action}_BUTTON'.toUpperCase(),
                      module: module,
                    )
                  : sectionType == ModulesEmp.PGR_SERVICES
                      ? getLocalizedString(
                          '${i18.common.CS_COMMON}$action'.toUpperCase(),
                        )
                      : getLocalizedString(
                          '${i18.common.WF_EMPLOYEE}${workFlowId}_$action'
                              .toUpperCase(),
                          module: module,
                        ),
            ),
          ),
        ],
        content: StatefulBuilder(
          builder: (context, setState) {
            return SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (action != BaseAction.reject.name &&
                      action != BaseAction.sendBackToCitizen.name &&
                      action != BaseAction.revocate.name &&
                      action != BaseAction.resolve.name &&
                      !isCitizen &&
                      isNotNullOrEmpty(
                        timelineController.employeeModel?.employees,
                      )) ...[
                    dropDownButton<Employee>(
                      context,
                      hinText: sectionType == ModulesEmp.PGR_SERVICES
                          ? getLocalizedString(
                              i18.grievance.EMP_EMPLOYEE_NAME,
                              module: module,
                            )
                          : 'Assignee name',
                      radius: 6.r,
                      contentPadding: EdgeInsets.all(10.w),
                      items: (sectionType == ModulesEmp.PGR_SERVICES
                              ? filterAssigneesPGR()
                              : timelineController.employeeModel?.employees ??
                                  [])
                          ?.map((e) {
                        return DropdownMenuItem(
                          value: e,
                          child: SmallTextNotoSans(text: e.user?.name ?? 'N/A'),
                        );
                      }).toList(),
                      onChanged: (value) {
                        assigneeUuid = value?.user?.uuid;
                        assigneeName = value?.user?.name ?? '';
                        dPrint('${value?.user?.uuid}');
                      },
                    ),
                    SizedBox(height: 10.h),
                  ],
                  ConstrainedBox(
                    constraints: BoxConstraints(
                      maxHeight: 100.h,
                      minHeight: 50.h,
                    ),
                    child: TextFormFieldApp(
                      controller: commentsController,
                      hintText: (isCitizen &&
                              (sectionType.name ==
                                  ModulesEmp.BPA_SERVICES.name))
                          ? '${getLocalizedString(
                              i18.building.ES_OBPS_ACTION_COMMENTS
                                  .toUpperCase(),
                              module: module,
                            )}*'
                          : sectionType == ModulesEmp.PGR_SERVICES
                              ? '${getLocalizedString(
                                  i18.grievance.EMP_EMPLOYEE_COMMENTS,
                                  module: module,
                                )}*'
                              : '${getLocalizedString(
                                  i18.common.COMMENTS.toUpperCase(),
                                )}*',
                      radius: 6.r,
                      keyboardType: TextInputType.multiline,
                    ),
                  ),
                  SizedBox(height: 10.h),
                  iconTextButton(
                    width: Get.width,
                    label: getLocalizedString(
                      i18.common.CS_COMMON_CHOOSE_FILE.toUpperCase(),
                    ),
                    icon: const Icon(
                      Icons.upload_file_outlined,
                      color: BaseConfig.appThemeColor1,
                    ),
                    onPressed: () {
                      openBottomSheet(
                        title: 'Choose image source',
                        onTabImageGallery: () {
                          fileController.selectAndPickImage();
                        },
                        onTabImageCamera: () {
                          fileController.selectAndPickImage(
                            imageSource: ImageSource.camera,
                          );
                        },
                      );
                    },
                  ),
                  SizedBox(height: 10.h),
                  Obx(
                    () => fileController.isSelectedFile.value
                        ? Stack(
                            children: [
                              SizedBox(
                                height: 80.h,
                                width: 80.w,
                                child: ClipRRect(
                                  borderRadius: BorderRadius.circular(10.r),
                                  child: Image.file(
                                    fileController.imageFile!,
                                    fit: BoxFit.cover,
                                  ),
                                ),
                              ),
                              Positioned(
                                right: 0,
                                child: Container(
                                  height: 35.h,
                                  width: 35.w,
                                  margin: EdgeInsets.all(8.w),
                                  child: IconButton.filled(
                                    style: IconButton.styleFrom(
                                      backgroundColor: Colors.black54,
                                    ),
                                    onPressed: () {
                                      fileController.removeSelectedImage();
                                    },
                                    icon: Icon(
                                      Icons.delete_outline,
                                      size: 18.sp,
                                      color: Colors.white,
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          )
                        : const SizedBox.shrink(),
                  ),
                ],
              ),
            );
          },
        ),
      ),
    );
  }

  /// update action based on module and action
  Future<(bool, String?)> getDocModel({
    required ModulesEmp module,
    required String action,
    required String workflowId,
    required bool isCitizen,
  }) async {
    switch (module) {
      case ModulesEmp.TL_SERVICES:
        final tlController = Get.find<TradeLicenseController>();
        if (fileController.isSelectedFile.value) {
          var wfDocs = WfDocument()
            ..fileStoreId = fileId
            ..documentType = '$action DOC'
            ..fileName = fileController.fileName;

          tlController.tradeLicense.licenses!.first.wfDocuments = [
            wfDocs,
          ];
        }
        tlController.tradeLicense.licenses!.first.comment =
            commentsController.text;

        tlController.tradeLicense.licenses!.first.action = action;

        if (assigneeUuid.isNotEmpty &&
            action != BaseAction.reject.name &&
            action != BaseAction.sendBackToCitizen.name) {
          tlController.tradeLicense.licenses!.first.assignee = [
            assigneeUuid,
          ];
        }

        return await tlController.empTlActionUpdate(
          token: authController.token!.accessToken!,
          license: tlController.tradeLicense.licenses!.first,
        );

      //WS update action
      case ModulesEmp.WS_SERVICES:
        final waterController = Get.find<WaterController>();
        final process = waterController.waterConnection?.processInstance;
        if (fileController.isSelectedFile.value) {
          var wsDocs = w.WsDocument()
            ..fileStoreId = fileId
            ..documentType = '$action DOC'
            ..fileName = fileController.fileName;

          var wfDocs = w.WfDocument()
            ..fileStoreId = fileId
            ..documentType = '$action DOC'
            ..fileName = fileController.fileName;

          process!.documents = [
            wsDocs,
          ];
          waterController.waterConnection?.wfDocuments = [
            wfDocs,
          ];
        } else {
          process!.documents = [];
        }
        waterController.waterConnection
          ?..comment = commentsController.text
          ..action = action;

        process.comment = commentsController.text;

        if (action == BaseAction.forward.name ||
            action == BaseAction.verifyForward.name) {
          final data = await storage.getString(
            SecureStorageConstants.WS_SESSION_APPLICATION_DETAILS,
          );
          w.WaterConnection editWaterConnection =
              w.WaterConnection.fromJson(jsonDecode(data ?? '{}'));

          waterController.waterConnection
            ?..plumberInfo = editWaterConnection.plumberInfo
            ..roadCuttingInfo = editWaterConnection.roadCuttingInfo
            ..connectionType = editWaterConnection.connectionType
            ..additionalDetails?.detailsProvidedBy =
                editWaterConnection.additionalDetails?.detailsProvidedBy
            ..additionalDetails?.detailsProvidedBy =
                editWaterConnection.additionalDetails?.detailsProvidedBy
            ..waterSource = editWaterConnection.waterSource
            ..noOfTaps = editWaterConnection.noOfTaps
            ..pipeSize = editWaterConnection.pipeSize;
        }

        if (assigneeUuid.isNotEmpty &&
            action != BaseAction.reject.name &&
            action != BaseAction.sendBackToCitizen.name) {
          waterController.waterConnection?.assignee = [assigneeUuid];

          var assignee = w.Assignes()..uuid = assigneeUuid;

          waterController.waterConnection?.assignes = [assignee];
          process.assignes = [assignee];
        }
        process.action = action;
        process.businessService = null;
        process.moduleName = null;

        waterController.waterConnection?.processInstance = process;

        return await waterController.empActionUpdate(
          token: authController.token!.accessToken!,
          waterConnection: waterController.waterConnection,
          module: module,
        );

      // SW update action
      case ModulesEmp.SW_SERVICES:
        final waterController = Get.find<WaterController>();
        final process = waterController.sewerageConnection?.processInstance;
        if (fileController.isSelectedFile.value) {
          var swDocs = s.SwDocument()
            ..fileStoreId = fileId
            ..documentType = '$action DOC'
            ..fileName = fileController.fileName;

          var wfDocs = s.WfDocument()
            ..fileStoreId = fileId
            ..documentType = '$action DOC'
            ..fileName = fileController.fileName;

          process!.documents = [
            swDocs,
          ];
          waterController.sewerageConnection?.wfDocuments = [
            wfDocs,
          ];
        } else {
          process!.documents = [];
        }
        waterController.sewerageConnection
          ?..comment = commentsController.text
          ..action = action;

        process.comment = commentsController.text;

        if (action == BaseAction.forward.name ||
            action == BaseAction.verifyForward.name) {
          final data = await storage.getString(
            SecureStorageConstants.WS_SESSION_APPLICATION_DETAILS,
          );

          s.SewerageConnection editSewerageConnection =
              s.SewerageConnection.fromJson(jsonDecode(data ?? '{}'));

          waterController.sewerageConnection
            ?..plumberInfo = editSewerageConnection.plumberInfo
            ..roadCuttingInfo = editSewerageConnection.roadCuttingInfo
            ..connectionType = editSewerageConnection.connectionType
            ..additionalDetails?.detailsProvidedBy =
                editSewerageConnection.additionalDetails?.detailsProvidedBy
            ..noOfWaterClosets = editSewerageConnection.noOfWaterClosets
            ..noOfToilets = editSewerageConnection.noOfToilets;
        }

        if (assigneeUuid.isNotEmpty &&
            action != BaseAction.reject.name &&
            action != BaseAction.sendBackToCitizen.name) {
          waterController.sewerageConnection?.assignee = [assigneeUuid];

          final assignees = s.Assignes()..uuid = assigneeUuid;

          waterController.sewerageConnection?.assignes = [assignees];
          process.assignes = [assignees];
        }

        process.action = action;
        process.businessService = null;
        process.moduleName = null;

        waterController.sewerageConnection?.processInstance = process;

        return await waterController.empActionUpdate(
          token: authController.token!.accessToken!,
          sewerageConnection: waterController.sewerageConnection,
          module: module,
        );

      // BPA update action
      case ModulesEmp.BPA_SERVICES:
        final bpaController = Get.find<BpaController>();
        final dynamicFormController = Get.find<ObpsDynamicFormController>();
        if (fileController.isSelectedFile.value) {
          if (isCitizen) {
            var docs = bp.VerificationDocument()
              ..fileStoreId = fileId
              ..documentType = '$action DOC'
              ..fileName = fileController.fileName;

            bpaController.bpaElement.workflow ??= bp.Workflow();
            bpaController.bpaElement.workflow!.verificationDocuments = [docs];
          } else {
            var wfDocs = bp.WfDocument()
              ..fileStoreId = fileId
              ..documentType = '$action DOC'
              ..fileName = fileController.fileName;

            bpaController.bpaElement.wfDocuments = [
              wfDocs,
            ];
          }
        }
        if (isCitizen) {
          // bpaController.bpaElement.riskType =
          //     bpaController.bpaElement.riskType ?? 'LOW';
          bpaController.bpaElement.workflow ??= bp.Workflow();
          bpaController.bpaElement.workflow!
            ..comments = commentsController.text
            ..action = action;
        } else {
          bpaController.bpaElement.comment = commentsController.text;

          bpaController.bpaElement.additionalDetails ??=
              bp.BpaAdditionalDetails();

          //Inspection Reports
          var fieldInspectionPending = bp.FieldInspectionPending()
            ..date = dynamicFormController.selectedDateString
            ..time = dynamicFormController.selectedTime24String;

          fieldInspectionPending.docs ??= [];
          fieldInspectionPending.questions ??= [];

          final docs = dynamicFormController.customDocs;
          final questions = dynamicFormController.customChecks;

          for (var doc in docs) {
            fieldInspectionPending.docs!.add(
              bp.Doc()
                ..documentType = doc.sectionName!.addLastPart()
                ..fileStoreId = doc.fileStoreId
                ..fileStore = doc.fileStoreId
                ..fileName = doc.name
                ..dropDownValues = bp.DropDownValues()
                ..dropDownValues?.value = doc.sectionName!.addLastPart(),
            );
          }

          for (var q in questions) {
            fieldInspectionPending.questions!.add(
              bp.Question()
                ..question = q.sectionName
                ..value = q.selectedName
                ..remarks = q.remark,
            );
          }

          bpaController.bpaElement.additionalDetails!.fieldInspectionPending = [
            fieldInspectionPending,
          ];
        }

        final workflow = bp.Workflow()
          ..action = action
          ..comment = commentsController.text
          ..comments = commentsController.text;

        if (assigneeUuid.isNotEmpty &&
            action != BaseAction.reject.name &&
            action != BaseAction.sendBackToCitizen.name) {
          bpaController.bpaElement.assignee = [assigneeUuid];
        }

        final riskType = getRiskType(bpaController.bpaElement.businessService!);

        bpaController.bpaElement.riskType = riskType;

        bpaController.bpaElement.workflow = workflow;
        bpaController.bpaElement.action = action;

        return await bpaController.empActionUpdate(
          token: authController.token!.accessToken!,
          module: module,
        );

      // PT update action
      case ModulesEmp.PT_SERVICES:
        final ptController = Get.find<PropertiesTaxController>();
        ptController.propertyActionRequest?.workflow ??= Workflow()
          ..action = action
          ..moduleName = module.name
          ..businessService = workflowId
          ..comment = commentsController.text;

        if (fileController.isSelectedFile.value) {
          var wfDocs = WorkflowDocument()
            ..fileStoreId = fileId
            ..documentType = '$action DOC'
            ..fileName = fileController.fileName;

          ptController.propertyActionRequest?.workflow?.documents = [
            wfDocs,
          ];
        }

        if (assigneeUuid.isNotEmpty &&
            action != BaseAction.reject.name &&
            action != BaseAction.sendBackToCitizen.name) {
          var assignee = Assignee()
            ..name = assigneeName
            ..uuid = assigneeUuid;

          ptController.propertyActionRequest?.workflow?.assignes = [
            assignee,
          ];
        }

        return await ptController.empActionUpdate(
          token: authController.token!.accessToken!,
          module: module,
        );
      //FireNoc update action
      case ModulesEmp.FIRE_NOC:
        final fireNocController = Get.find<FireNocController>();
        fireNocController.fireNoc?.fireNocDetails?.action = action;
        fireNocController.fireNoc?.fireNocDetails?.additionalDetail?.comment =
            commentsController.text;

        if (fileController.isSelectedFile.value) {
          var wfDocs = noc.WfDocument()
            ..fileStoreId = fileId
            ..documentType = '$action DOC'
            ..fileName = fileController.fileName;

          fireNocController
              .fireNoc?.fireNocDetails?.additionalDetail?.wfDocuments = [
            wfDocs,
          ];
        } else {
          fireNocController
              .fireNoc?.fireNocDetails?.additionalDetail?.wfDocuments = [];
        }

        final uoms = fireNocController
            .fireNoc?.fireNocDetails?.buildings?.first.uoms
            ?.where((element) => element.active == true)
            .toList();

        if (uoms != null) {
          Map<String, dynamic> uomsMap = {
            for (var uom in uoms)
              if (uom.code != null && uom.value != null) uom.code!: uom.value!,
          };

          fireNocController.fireNoc?.fireNocDetails?.buildings?.first
            ?..uoms = uoms
            ..uomsMap = uomsMap;
        }

        if (assigneeUuid.isNotEmpty &&
            action != BaseAction.reject.name &&
            action != BaseAction.sendBackToCitizen.name) {
          fireNocController
              .fireNoc?.fireNocDetails?.additionalDetail?.assignee = [
            assigneeUuid,
          ];
        }

        return await fireNocController.empActionUpdate(
          token: authController.token!.accessToken!,
          module: module,
        );

      case ModulesEmp.PGR_SERVICES:
        final grievanceController = Get.find<GrievanceController>();

        grievanceController.serviceWrapper?.workflow?.action = action;
        grievanceController.serviceWrapper?.workflow?.comments =
            commentsController.text;
        grievanceController.serviceWrapper?.workflow?.verificationDocuments =
            [];

        if (fileController.isSelectedFile.value) {
          var wfDocs = gr.VerificationDocument()
            ..fileStoreId = fileId
            ..documentType = 'PHOTO'
            ..documentUid = '';

          grievanceController.serviceWrapper?.workflow?.verificationDocuments =
              [
            wfDocs,
          ];
        }

        final city = grievanceController
            .serviceWrapper?.service?.address?.tenantId
            ?.replaceAll('.', '_')
            .toUpperCase();

        final serviceDef = grievanceController.getDepartment(
          grievanceController.serviceWrapper?.service?.serviceCode ?? '',
        );

        final detailsReq = {
          "CS_COMPLAINT_DETAILS_COMPLAINT_NO":
              grievanceController.serviceWrapper?.service?.serviceRequestId ??
                  '',
          "CS_COMPLAINT_DETAILS_APPLICATION_STATUS":
              "CS_COMMON_${grievanceController.serviceWrapper?.service?.applicationStatus}",
          "CS_ADDCOMPLAINT_COMPLAINT_TYPE":
              "SERVICEDEFS.${serviceDef?.menuPath?.toUpperCase()}",
          "CS_ADDCOMPLAINT_COMPLAINT_SUB_TYPE":
              "SERVICEDEFS.${grievanceController.serviceWrapper?.service?.serviceCode?.toUpperCase()}",
          "CS_ADDCOMPLAINT_PRIORITY_LEVEL":
              grievanceController.serviceWrapper?.service?.priority ?? '',
          "CS_COMPLAINT_ADDTIONAL_DETAILS":
              grievanceController.serviceWrapper?.service?.description ?? '',
          "CS_COMPLAINT_FILED_DATE": grievanceController
                  .serviceWrapper?.service?.auditDetails?.createdTime
                  .toCustomDateFormat() ??
              '',
          "ES_CREATECOMPLAINT_ADDRESS": [
            grievanceController.serviceWrapper?.service?.address?.landmark ??
                '',
            "${city}_ADMIN_${grievanceController.serviceWrapper?.service?.address?.locality?.code}",
            "${grievanceController.serviceWrapper?.service?.address?.city}",
            "${grievanceController.serviceWrapper?.service?.address?.pinCode}",
          ],
        };

        final service = grievanceController.serviceWrapper
          ?..details = detailsReq;

        return await grievanceController.empActionUpdate(
          token: authController.token!.accessToken!,
          module: module,
          serviceWrapperRequest: service,
        );
      default:
        return (false, null);
    }
  }
}
