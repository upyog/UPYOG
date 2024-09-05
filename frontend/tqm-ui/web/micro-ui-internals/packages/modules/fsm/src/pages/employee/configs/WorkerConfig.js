import React from "react";
import { DatePicker, InfoIcon } from "@egovernments/digit-ui-react-components";
import { convertEpochToDate } from "../../../utils";

const WorkerConfig = ({ t, disabled = false, skillsOption = [], defaultSkill = [], employer = [] }) => {
  return [
    {
      head: "ES_FSM_REGISTRY_PERSONAL_DETAILS",
      body: [
        {
          type: "component",
          component: "SelectSWEmployeePhoneNumber",
          key: "SelectEmployeePhoneNumber",
          withoutLabel: true,
        },
        {
          label: "ES_NEW_APPLICATION_APPLICANT_NAME",
          isMandatory: true,
          type: "text",
          key: "name",
          disable: disabled,
          populators: {
            name: "name",
            validation: {
              required: false,
              pattern: /^[a-zA-Z-' ]+$/,
            },
            error: t("FSM_REGISTRY_INVALID_NAME"),
            defaultValue: "",
            className: "payment-form-text-input-correction",
          },
        },
        {
          label: "ES_FSM_REGISTRY_NEW_GENDER",
          // isMandatory: true,
          type: "component",
          route: "select-gender",
          hideInEmployee: false,
          key: "selectGender",
          component: "SelectGender",
          // disabled: disabled,
          texts: {
            headerCaption: "",
            header: "CS_COMMON_CHOOSE_GENDER",
            cardText: "CS_COMMON_SELECT_GENDER",
            submitBarLabel: "CS_COMMON_NEXT",
            skipText: "CORE_COMMON_SKIP_CONTINUE",
          },
        },
        {
          label: t("ES_FSM_REGISTRY_NEW_DOB"),
          // isMandatory: true,
          type: "custom",
          key: "dob",
          populators: {
            name: "dob",
            validation: {
              required: true,
            },
            component: (props, customProps) => (
              <DatePicker onChange={props.onChange} date={props.value} {...customProps} max={convertEpochToDate(new Date().setFullYear(new Date().getFullYear() - 18))} />
            ),
          },
        },
        {
          type: "documentUpload",
          withoutLabel: true,
          module: "Photograph",
          master: "FSM",
          error: "WORKS_REQUIRED_ERR",
          name: "documents",
          customClass: "",
          localePrefix: "FSM_REGISTRY",
        },
      ],
    },
    {
      head: "FSM_REGISTRY_WORKER_ADDRESS_HEADING",
      body: [
        {
          label: "ES_FSM_REGISTRY_NEW_PINCODE",
          isMandatory: false,
          type: "text",
          key: "pincode",
          populators: {
            name: "pincode",
            validation: {
              required: false,
              pattern: /^[1-9][0-9]{5}$/,
            },
            error: t("FSM_REGISTRY_INVALID_PINCODE"),
            defaultValue: "",
            className: "payment-form-text-input-correction",
          },
        },
        {
          route: "address",
          component: "SelectAddress",
          withoutLabel: true,
          texts: {
            headerCaption: "CS_FILE_APPLICATION_PROPERTY_LOCATION_LABEL",
            header: "CS_FILE_APPLICATION_PROPERTY_LOCATION_ADDRESS_TEXT",
            cardText: "CS_FILE_APPLICATION_PROPERTY_LOCATION_CITY_MOHALLA_TEXT",
            submitBarLabel: "CS_COMMON_NEXT",
          },
          key: "address",
          isMandatory: true,
          type: "component",
        },
        {
          label: "ES_FSM_REGISTRY_NEW_STREET",
          isMandatory: false,
          type: "text",
          key: "street",
          populators: {
            name: "street",
            defaultValue: "",
            className: "payment-form-text-input-correction",
          },
        },
        {
          label: "ES_FSM_REGISTRY_NEW_DOOR",
          isMandatory: false,
          type: "text",
          key: "doorNo",
          populators: {
            name: "doorNo",
            defaultValue: "",
            className: "payment-form-text-input-correction",
          },
        },
        {
          label: "ES_FSM_REGISTRY_NEW_LANDMARK",
          isMandatory: false,
          type: "text",
          key: "landmark",
          populators: {
            name: "landmark",
            defaultValue: "",
            className: "payment-form-text-input-correction",
          },
        },
      ],
    },
    {
      head: "FSM_REGISTRY_WORKER_PROFESSIONAL_HEADING",
      body: [
        {
          isMandatory: true,
          key: "skills",
          type: "dropdown",
          optionsDisable: disabled,
          label: "FSM_REGISTRY_WORKER_SKILLS",
          labelChildren: (
            <div className="tooltip" style={{ paddingLeft: "10px", marginBottom: "-3px" }}>
              <InfoIcon />
              <span className="tooltiptext" style={{ width: "150px", left: "230%", fontSize: "14px" }}>
                {t("ES_FSM_SW_SKILLS_INFO_TIP")}
              </span>
            </div>
          ),
          populators: {
            optionsDisable: disabled,
            allowMultiSelect: true,
            name: "skills",
            optionsKey: "i18nKey",
            error: "ES_TQM_REQUIRED",
            required: true,
            options: skillsOption,
            defaultOptions: defaultSkill,
            //   mdmsv2: {
            //     schemaCode: "PQM.Plant",
            //   }
          },
        },
      ],
    },
    {
      head: "FSM_REGISTRY_WORKER_EMPLOYEE_HEADING",
      body: [
        {
          type: "component",
          isMandatory: true,
          component: "SelectSWEmploymentDetails",
          key: "employementDetails",
          withoutLabel: true,
        },
        // {
        //   isMandatory: true,
        //   key: "employer",
        //   type: "radio",
        //   label: "Employer",
        //   disable: false,
        //   populators: {
        //     name: "FSM_REGISTRY_WORKER_EMPLOYER",
        //     optionsKey: "name",
        //     error: "FSM_REGISTRY_WORKER_EMPLOYER_ERROR",
        //     required: true,
        //     options: employer,
        //   },
        // },
        // {
        //   label: "FSM_REGISTRY_WORKER_SELECT_VENDOR",
        //   type: "apidropdown",
        //   isMandatory: false,
        //   disable: false,
        //   populators: {
        //     optionsCustomStyle: {
        //       top: "2.3rem",
        //     },
        //     name: "vendor",
        //     optionsKey: "name",
        //     allowMultiSelect: false,
        //     masterName: "commonUiConfig",
        //     moduleName: "SearchVendor",
        //     customfn: "populateReqCriteria",
        //   },
        // },
      ],
    },
    {
      head: "FSM_REGISTRY_WORKER_ROLE_HEADING",
      body: [
        {
          type: "component",
          isMandatory: true,
          component: "AddWorkerRoles",
          key: "AddWorkerRoles",
          withoutLabel: true,
        },
      ],
    },
  ];
};

export default WorkerConfig;
