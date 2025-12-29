import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/screens/citizen/grievance_redressal/new_grievance_form/new_grievance_form.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/utils.dart';

Map initRequestBody({
  required String tenantId,
}) {
  return {
    "MdmsCriteria": {
      "tenantId": tenantId,
      "moduleDetails": [
        {
          "moduleName": "common-masters",
          "masterDetails": [
            {"name": "Department"},
            {"name": "Designation"},
            {"name": "StateInfo"},
            {"name": "wfSlaConfig"},
            {"name": "uiHomePage"},
          ],
        },
        {
          "moduleName": "tenant",
          "masterDetails": [
            {"name": "tenants"},
            {"name": "citymodule"},
          ],
        },
        {
          "moduleName": "DIGIT-UI",
          "masterDetails": [
            {"name": "ApiCachingSettings"},
          ],
        }
      ],
    },
  };
}

Map getMdmsAccessControl({required String tenantId}) {
  return {
    "MdmsCriteria": {
      "tenantId": tenantId,
      "moduleDetails": [
        {
          "moduleName": "common-masters",
          "masterDetails": [
            {
              "name": "CitizenConsentForm",
            }
          ],
        },
        {
          "moduleName": "ACCESSCONTROL-ACTIONS-TEST",
          "masterDetails": [
            {
              "name": "actions-test",
              "filter": "[?(@.url == 'digit-ui-card')]",
            }
          ],
        }
      ],
    },
  };
}

Map<String, dynamic> payloadParametersBody({
  required String tenantId,
  bool isUc = false,
}) {
  return {
    "MdmsCriteria": {
      "tenantId": tenantId,
      "moduleDetails": [
        {
          "moduleName": isUc ? "BillingService" : "common-masters",
          "masterDetails": isUc
              ? [
                  {
                    "name": "BusinessService",
                    "filter": "[?(@.type=='Adhoc' && @.isActive==true)]",
                  },
                  {
                    "name": "TaxHeadMaster",
                  },
                  {
                    "name": "TaxPeriod",
                  }
                ]
              : [
                  {
                    "name": "StaticData",
                  }
                ],
        },
      ],
    },
  };
}

Map getEmpBodyFilter({
  required String tenantId,
  required int limit,
  required int offset,
  required ModulesEmp module,
  required List<BusinessServicesEmp> businessServices,
  bool isFilter = false,
  List? locality,
  String? assigneeUid,
  String? applicationNumber,
  String? propertyId,
  String? mobileNumber,
}) {
  return {
    "inbox": {
      "tenantId": tenantId,
      "processSearchCriteria": {
        if (module == ModulesEmp.BPA_SERVICES) "assignee": "",
        "moduleName": module.name,
        "businessService": isFilter
            ? businessServices.map((e) => e.name).toList()
            // ? module != ModulesEmp.PT_SERVICES
            //     ? businessServices.map((e) => e.name).toList()
            //     : ["ptr"]
            : businessServices.map((e) => e.name).toList(),
        if (assigneeUid != null &&
            (module == ModulesEmp.PT_SERVICES ||
                module == ModulesEmp.TL_SERVICES ||
                module == ModulesEmp.BPA_SERVICES))
          "assignee": assigneeUid,
      },
      "moduleSearchCriteria": {
        if (applicationNumber != null) "applicationNumber": applicationNumber,
        if (propertyId != null) "propertyId": propertyId,
        if (mobileNumber != null) "mobileNumber": mobileNumber,
        if (assigneeUid != null &&
            (module != ModulesEmp.PT_SERVICES ||
                module != ModulesEmp.TL_SERVICES ||
                module != ModulesEmp.BPA_SERVICES))
          "assignee": assigneeUid,
        if (locality != null)
          "locality":
              module == ModulesEmp.PT_SERVICES ? locality : locality.join(','),
        if (module == ModulesEmp.PT_SERVICES)
          "creationReason": ["CREATE", "MUTATION", "UPDATE"],
        if ((module == ModulesEmp.WS_SERVICES ||
                module == ModulesEmp.SW_SERVICES) &&
            module != ModulesEmp.BPA_SERVICES)
          "businessService":
              businessServices.map((e) => e.name).toList().join(','),
        if (module != ModulesEmp.BPA_SERVICES) "sortBy": _getSortBy(module),
        "sortOrder":
            module == ModulesEmp.WS_SERVICES ? "DESC" : "ASC", //ASC or DESC
        if (module == ModulesEmp.WS_SERVICES || isFilter) "isInboxSearch": true,
      },
      "limit": limit,
      "offset": offset,
    },
  };
}

String _getSortBy(ModulesEmp module) {
  if (module == ModulesEmp.WS_SERVICES || module == ModulesEmp.SW_SERVICES) {
    return "additionalDetails.appCreatedDate";
  }
  if (module == ModulesEmp.PT_SERVICES) {
    return "createdTime";
  }
  return "applicationDate";
}

// Emp obps mdms service request
Map getEmpMdmsBodyChecklist({
  BusinessServicesEmp service = BusinessServicesEmp.BPA,
}) {
  return {
    "MdmsCriteria": {
      "tenantId": BaseConfig.STATE_TENANT_ID,
      "moduleDetails": [
        {
          "moduleName": service.name,
          "masterDetails": [
            {
              "name": "CheckList",
            }
          ],
        },
        {
          "moduleName": "DataSecurity",
          "masterDetails": [
            {
              "name": "SecurityPolicy",
            }
          ],
        },
        {
          "moduleName": "common-masters",
          "masterDetails": [
            {
              "name": "DocumentType",
            }
          ],
        },
        {
          "moduleName": "NOC",
          "masterDetails": [
            {
              "name": "DocumentTypeMapping",
            }
          ],
        }
      ],
    },
  };
}

/// Emp WS/SW mdms service request
Map getEmpMdmsBodyWsSw() {
  return {
    "MdmsCriteria": {
      "tenantId": BaseConfig.STATE_TENANT_ID,
      "moduleDetails": [
        {
          "moduleName": "ws-services-calculation",
          "masterDetails": [
            {"name": "PipeSize"},
            {"name": "RoadType"},
          ],
        },
        {
          "moduleName": "ws-services-masters",
          "masterDetails": [
            {"name": "connectionType"},
            {"name": "waterSource"},
          ],
        }
      ],
    },
  };
}

/// Emp PT mdms service request for property tax form
/// mdms/v2 service request
Map getEmpMdmsBodyPTRegForm() {
  return {
    "MdmsCriteria": {
      "tenantId": BaseConfig.STATE_TENANT_ID,
      "moduleDetails": [
        {
          "moduleName": "PropertyTax",
          "masterDetails": [
            {
              "name": "UsageCategory",
            },
            {
              "name": "PropertyType",
            },
            {
              "name": "Floor",
            },
            {
              "name": "OccupancyType",
            },
            {
              "name": "UsageCategory",
            },
            {
              "name": "Floor",
            },
            {
              "name": "UsageCategory",
            },
            {
              "name": "OccupancyType",
            },
            {
              "name": "Floor",
            },
            {
              "name": "OwnerType",
            },
            {
              "name": "OwnerShipCategory",
            },
            {
              "name": "Documents",
            },
            {
              "name": "SubOwnerShipCategory",
            },
            {
              "name": "OwnerShipCategory",
            },
            {
              "name": "SubOwnerShipCategory",
            },
            {
              "name": "OwnerShipCategory",
            },
            {
              "name": "UsageCategory",
            },
            {
              "name": "OccupancyType",
            },
            {
              "name": "Floor",
            },
            {
              "name": "OwnerType",
            },
            {
              "name": "OwnerShipCategory",
            },
            {
              "name": "Documents",
            },
            {
              "name": "SubOwnerShipCategory",
            },
            {
              "name": "OwnerShipCategory",
            },
            {
              "name": "MutationDocuments",
            }
          ],
        },
        {
          "moduleName": "common-masters",
          "masterDetails": [
            {
              "name": "GenderType",
            }
          ],
        }
      ],
    },
  };
}

/// Citizen PGR mdms/v2 service request
Map getCitizenMdmsBodyPgr() {
  return {
    "MdmsCriteria": {
      "tenantId": BaseConfig.STATE_TENANT_ID,
      "moduleDetails": [
        {
          "moduleName": "RAINMAKER-PGR",
          "masterDetails": [
            {"name": "ServiceDefs"},
          ],
        },
      ],
    },
  };
}

/// Emp PGR mdms/LME service request
Map getEmpMdmsBodyPGR(String tenantId) {
  return {
    "MdmsCriteria": {
      "tenantId": tenantId,
      "moduleDetails": [
        {
          "moduleName": "RAINMAKER-PGR",
          "masterDetails": [
            {
              "name": "ServiceDefs",
            }
          ],
        }
      ],
    },
  };
}

Map sendGrievanceFormValue({
  required String grievanceSubType,
  required String grievancePriority,
  required String landmark,
  required String additionalDetails,
  required String localityCode,
  required String localityName,
  required String city,
  required String district,
  required String region,
  required String tenantId,
  required String pinCode,
  required List<VerificationDocumentPGR> verificationDocuments,
}) {
  return {
    "service": {
      "tenantId": tenantId,
      "serviceCode": grievanceSubType,
      "priority": grievancePriority,
      "description": additionalDetails,
      "additionalDetail": {},
      "source": "mobile",
      "address": {
        "landmark": landmark,
        "city": city,
        "district": district,
        "region": region,
        "state": "Demo",
        if (isNotNullOrEmpty(pinCode)) "pincode": pinCode,
        "locality": {"code": localityCode, "name": localityName},
        "geoLocation": {},
      },
    },
    "workflow": {
      "action": "APPLY",
      if (isNotNullOrEmpty(verificationDocuments))
        "verificationDocuments":
            verificationDocuments.map((doc) => doc.toJson()).toList(),
    },
  };
}
