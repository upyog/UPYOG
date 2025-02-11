import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { FormComposer, Toast, Header } from "@upyog/digit-ui-react-components";
import { FormComposerV2 } from "../../../../../../../react-components/src";
import { useHistory } from "react-router-dom";
import WorkerConfig from "../../configs/WorkerConfig";
import { useQueryClient } from "react-query";

const AddWorker = ({ parentUrl, heading }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const [showToast, setShowToast] = useState(null);
  const history = useHistory();
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const [canSubmit, setSubmitValve] = useState(false);
  const [checkRoleField, setCheckRoleField] = useState(false);
  const [Config, setConfig] = useState(WorkerConfig({ t }));
  const [skillsOption, setSkillsOption] = useState([]);
  const [employer, setEmployer] = useState([]);
  const { isLoading: isLoading, isError: vendorCreateError, data: updateResponse, error: updateError, mutate } = Digit.Hooks.fsm.useWorkerCreate(
    tenantId
  );
  const {
    isLoading: isVendorUpdateLoading,
    isError: isvendorUpdateError,
    data: vendorUpdateResponse,
    error: vendorUpdateError,
    mutate: vendorMutate,
  } = Digit.Hooks.fsm.useVendorUpdate(tenantId);

  const {
    isLoading: isPlantUserLoading,
    isError: isPlantUserError,
    data: plantUserResponse,
    error: PlantUserError,
    mutate: PlantUserMutate,
  } = Digit.Hooks.fsm.usePlantUserCreate(tenantId);

  const { isLoading: ismdms, data: mdmsOptions } = Digit.Hooks.useCustomMDMS(
    stateId,
    "FSM",
    [
      {
        name: "SanitationWorkerSkills",
      },
      {
        name: "SanitationWorkerEmployer",
      },
      {
        name: "SanitationWorkerEmploymentType",
      },
      {
        name: "SanitationWorkerFunctionalRoles",
      },
    ],
    {
      select: (data) => {
        return data?.FSM;
      },
    }
  );

  useEffect(() => {
    const tempSkills = mdmsOptions?.SanitationWorkerSkills?.map((i) => ({ ...i, i18nKey: `ES_FSM_OPTION_${i.code}` }));
    const tempEmp = mdmsOptions?.SanitationWorkerEmployer?.map((i) => ({ ...i, i18nKey: `ES_FSM_OPTION_${i.code}` }));
    setSkillsOption(tempSkills);
    setEmployer(tempEmp);
  }, [mdmsOptions, ismdms]);

  useEffect(() => {
    setConfig(WorkerConfig({ t, skillsOption, employer }));
  }, [skillsOption, employer]);

  const defaultValues = {};

  const onFormValueChange = (setValue, formData, errors) => {
    for (let i = 0; i < formData?.AddWorkerRoles?.length; i++) {
      let key = formData?.AddWorkerRoles[i];
      if (!(key?.emp_Type && key?.fn_role && key?.sys_role && key?.fn_role?.code)) {
        setCheckRoleField(false);
        break;
      } else {
        setCheckRoleField(true);
      }
    }

    if (
      !isNaN(formData?.SelectEmployeePhoneNumber?.mobileNumber?.length) &&
      formData?.SelectEmployeePhoneNumber?.mobileNumber?.length === 10 &&
      formData?.name &&
      // formData?.selectGender &&
      // formData?.dob &&
      formData?.address?.city &&
      formData?.address?.locality &&
      formData?.skills &&
      formData?.employementDetails?.employer &&
      formData?.employementDetails?.vendor &&
      (!formData?.AddWorkerRoles || formData?.AddWorkerRoles?.length === 0 || (formData?.AddWorkerRoles?.length > 0 && checkRoleField)) &&
      !errors?.SelectEmployeePhoneNumber?.isMobilePresent
    ) {
      setSubmitValve(true);
    } else {
      setSubmitValve(false);
    }
  };

  const closeToast = () => {
    setShowToast(null);
  };

  const onSubmit = (data) => {
    const name = data?.name;
    const mobileNumber = data?.SelectEmployeePhoneNumber?.mobileNumber;
    const gender = data?.selectGender?.code;
    const dob = data.dob ? new Date(`${data.dob}`).getTime() : null;
    const photograph = data?.documents?.img_photo?.[0]?.[1]?.fileStoreId?.fileStoreId || null;
    const pincode = data?.pincode;
    const city = data?.address?.city?.name;
    const locality = data?.address?.locality?.code;
    const doorNo = data?.doorNo;
    const street = data?.street;
    const landmark = data?.landmark;
    const skills = data?.skills?.map((i) => {
      return { type: i?.code, level: "UNSKILLED" };
    });
    const employer = data?.employementDetails?.employer?.code;
    const vendor = data?.employementDetails?.vendor;
    const roleDetails = data?.AddWorkerRoles;
    const restructuredData = [];
    roleDetails?.forEach((item) => {
      const restructuredItem = {};
      restructuredItem["FUNCTIONAL_ROLE"] = item.fn_role.code;
      restructuredItem["EMPLOYMENT_TYPE"] = item.emp_Type.name;
      restructuredItem["SYSTEM_ROLE"] = item.sys_role;
      restructuredItem["PLANT"] = item?.plant;
      restructuredData.push(restructuredItem);
    });

    const driverLicenses = roleDetails
      ?.filter((entry) => entry.fn_role && entry.fn_role.code === "DRIVER" && entry.licenseNo)
      .map((entry) => entry.licenseNo);
    const roleDetailsArray = [];

    roleDetails?.forEach((item, index) => {
      // Extracting functional role information
      const fnRoleKey = `FUNCTIONAL_ROLE_${index + 1}`;
      const fnRoleValue = item.fn_role.code;

      // Extracting employment type information
      const empTypeKey = `EMPLOYMENT_TYPE_${index + 1}`;
      const empTypeValue = item.emp_Type.name.toUpperCase();

      // Pushing the extracted information to the output array
      roleDetailsArray.push({ key: fnRoleKey, value: fnRoleValue });
      roleDetailsArray.push({ key: empTypeKey, value: empTypeValue });
    });

    // Adding the count of functional roles
    if (roleDetails) {
      roleDetailsArray.push({ key: "FUNCTIONAL_ROLE_COUNT", value: `${roleDetails?.length < 10 ? "0" : ""}${roleDetails?.length.toString()}` });
    }

    // Adding the employer information (assuming it's a constant value like "PRIVATE_VENDOR")
    roleDetailsArray.push({ key: "EMPLOYER", value: employer });

    const checkDuplicacy = roleDetailsArray.filter((item) => item.key.startsWith("FUNCTIONAL_ROLE")).map((item) => item.value);
    const isDuplicate = checkDuplicacy.length === new Set(checkDuplicacy).size;

    if (!isDuplicate) {
      setShowToast({ key: "error", action: `ES_FSM_WORKER_DUPLICATE_ROLE` });
      setTimeout(() => {
        closeToast();
      }, 5000);
      return;
    }

    const formData = {
      Individual: {
        tenantId: tenantId,
        name: {
          givenName: name,
        },
        dateOfBirth: dob,
        gender: gender,
        mobileNumber: mobileNumber,
        address: [
          {
            tenantId: tenantId,
            pincode: pincode,
            city: tenantId,
            street: street,
            doorNo: doorNo,
            locality: {
              code: locality,
            },
            landmark: landmark,
            type: "PERMANENT",
          },
        ],
        identifiers:
          driverLicenses?.length > 0
            ? [
                {
                  identifierType: "DRIVING_LICENSE_NUMBER",
                  identifierId: driverLicenses?.[0],
                },
              ]
            : null,
        skills: skills,
        photo: photograph,
        additionalFields: {
          // fields: restructuredData,
          fields: roleDetailsArray,
        },
        isSystemUser: true,
        userDetails: {
          username: mobileNumber,
          tenantId: tenantId,
          roles: roleDetails
            ? // Object.values(roleDetails[0].sys_role).map((role) => ({
              //     code: role.code,
              //     tenantId: tenantId,
              //   }))
              roleDetails
                .map((item) => Object.values(item.sys_role))
                .flat()
                .reduce((result, role) => {
                  if (!result.some((entry) => entry.code === role.code)) {
                    result.push({ code: role.code, tenantId });
                  }
                  return result;
                }, [])
            : [{ code: "SANITATION_WORKER", tenantId }],
          type: "CITIZEN",
        },
      },
    };

    mutate(formData, {
      onError: (error, variables) => {
        setShowToast({ key: "error", action: "ES_FSM_WORKER_ADD_FAILED" });
        setTimeout(closeToast, 5000);
      },
      onSuccess: async (data, variables) => {
        // setShowToast({ key: "success", action: "ADD_WORKER" });
        // queryClient.invalidateQueries("FSM_WORKER_SEARCH");
        // if (roleDetails?.some((entry) => entry.plant)) {
        //   try {
        //     const PlantCode = roleDetails
        //       ?.map((entry) => ({
        //         tenantId: tenantId,
        //         plantCode: entry?.plant?.code,
        //         individualId: data?.Individual?.individualId,
        //         isActive: true,
        //       }))
        //       .filter((i) => i?.plantCode);
        //     const plantFormData = {
        //       plantUsers: PlantCode,
        //     };
        //     const plantresponse = await PlantUserMutate(plantFormData);
        //   } catch (err) {
        //     console.error("Plant user create", err);
        //     setShowToast({ key: "error", action: err });
        //   }
        // }
        if (vendor) {
          try {
            const vendorData = {
              vendor: {
                ...vendor,
                workers: vendor.workers
                  ? [...vendor.workers, { individualId: data?.Individual?.id, vendorWorkerStatus: "ACTIVE" }]
                  : [{ individualId: data?.Individual?.id, vendorWorkerStatus: "ACTIVE" }],
              },
            };
            const response = await vendorMutate(vendorData);
            setShowToast({ key: "success", action: "WORDER_ADDED_WITH_VENDOR" });
          } catch (updateError) {
            console.error("Error updating data:", updateError);
            setShowToast({ key: "success", action: "WORDER_ADDED_VENDOR_FAILED" });
          }
        } else {
          setShowToast({ key: "success", action: "ADD_WORKER_SUCCESS" });
        }
        setTimeout(() => {
          closeToast();
          history.push(`/${window?.contextPath}/employee/fsm/registry?selectedTabs=WORKER`);
        }, 5000);
      },
    });
  };
  const isMobile = window.Digit.Utils.browser.isMobile();

  return (
    <React.Fragment>
      <div>
        <Header>{t("FSM_REGISTRY_ADD_WORKER_HEADING")}</Header>
      </div>
      <div style={!isMobile ? { marginLeft: "-15px" } : {}}>
        <FormComposerV2
          isDisabled={!canSubmit}
          label={t("ES_COMMON_APPLICATION_SUBMIT")}
          config={Config.filter((i) => !i.hideInEmployee).map((config) => {
            return {
              ...config,
              body: config.body.filter((a) => !a.hideInEmployee),
            };
          })}
          fieldStyle={{ marginRight: 0 }}
          sectionHeaderClassName="fsm-registry"
          onSubmit={onSubmit}
          defaultValues={defaultValues}
          onFormValueChange={onFormValueChange}
          noBreakLine={true}
          cardStyle={{
            padding: "1rem 1.5rem",
          }}
        />
        {showToast && (
          <Toast
            error={showToast.key === "error" ? true : false}
            label={t(showToast.key === "success" ? `ES_FSM_REGISTRY_${showToast.action}_SUCCESS` : showToast.action)}
            onClose={closeToast}
          />
        )}
      </div>
    </React.Fragment>
  );
};

export default AddWorker;
