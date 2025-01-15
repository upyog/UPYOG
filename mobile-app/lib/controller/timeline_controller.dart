import 'package:get/get.dart';
import 'package:mobile_app/model/citizen/timeline/timeline.dart';
import 'package:mobile_app/model/employee/employee_model/employees_model.dart';
import 'package:mobile_app/model/employee/workflow_business_service/workflow_business_service_model.dart';
import 'package:mobile_app/repository/employee_repository.dart';
import 'package:mobile_app/repository/timeline_repository.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:rxdart/rxdart.dart';

class TimelineController extends GetxController {
  final timelineStreamCtrl = BehaviorSubject<Timeline?>();
  Timeline? timeline;
  late WorkflowBusinessServices workflowBusinessServices;
  late EmployeeModel employeeModel;

  RxBool isTermsConditionsAccepted = false.obs;
  int countApiCall = 0;

  Future<void> getTimelineHistory({
    required String token,
    required String tenantId,
    required String businessIds,
  }) async {
    try {
      final query = {
        "tenantId": tenantId,
        "businessIds": businessIds,
        "history": 'true',
      };
      final tlRes = await TimelineRepository.timelineHistory(
        token: token,
        query: query,
        body: {},
      );

      timeline = Timeline.fromJson(tlRes);
      timelineStreamCtrl.sink.add(timeline);
    } catch (e, s) {
      dPrint('Timeline History Error: $e');
      timelineStreamCtrl.sink.addError(e);
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Get Workflow Business Services
  Future<void> getWorkFlow({
    required String token,
    required String tenantId,
    required String workFlow,
  }) async {
    try {
      isTermsConditionsAccepted.value = false;
      final query = {
        "tenantId": tenantId,
        "businessServices": workFlow,
      };
      final res = await TimelineRepository.getWorkFlowBusinessServices(
        token: token,
        query: query,
      );
      workflowBusinessServices = WorkflowBusinessServices.fromJson(res);
    } catch (e, s) {
      dPrint('Workflow Business Services Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  /// Get Workflow Roles String
  String getWorkflowRoles(String uuid, {bool empNoc = false}) {
    if (empNoc) return uuid;
    final roles = <String>[];
    for (var service in workflowBusinessServices.businessServices!) {
      for (var state in service.states!) {
        if (state.uuid == uuid) {
          for (var action in state.actions!) {
            roles.addAll(action.roles!);
          }
        }
      }
    }
    return roles.join(',');
  }

  /// Get Employees
  Future<void> getEmployees({
    required String token,
    required String tenantId,
    required String uuid,
    bool empNoc = false,
  }) async {
    try {
      final roles = getWorkflowRoles(uuid, empNoc: empNoc);
      if (roles.isEmpty) return;

      dPrint('WORKFLOW ROLES: $roles');

      final query = {
        "isActive": 'true',
        "roles": roles,
        'tenantId': tenantId,
      };
      final empRes = await EmployeeRepository.getEmployees(
        token: token,
        query: query,
      );
      employeeModel = empRes;
    } catch (e, s) {
      dPrint('Employees Error: $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }
}
