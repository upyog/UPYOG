import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Toast, Header, FormComposerV2, Loader } from "@egovernments/digit-ui-react-components";
import { useHistory } from "react-router-dom";
import WorkerConfig from "../../configs/WorkerConfig";
import { useQueryClient } from "react-query";

// IND-2023-11-24-010875
const EditWorker = ({ parentUrl, heading }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const [showToast, setShowToast] = useState(null);
  const history = useHistory();
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const [canSubmit, setSubmitValve] = useState(false);
  const [workerinfo, setWorkerinfo] = useState(null);
  const [defaultValues, setDefaultValues] = useState(null);
  const [skillsOption, setSkillsOption] = useState([]);
  const [employer, setEmployer] = useState([]);
  const [Config, setConfig] = useState(WorkerConfig({ t, disabled: true }));
  const searchParams = new URLSearchParams(location.search);
  const id = searchParams.get("id");
  const [checkRoleField, setCheckRoleField] = useState(false);

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

  const { isLoading: isPlantUserLoading, data: plantUserData } = Digit.Hooks.useCustomAPIHook({
    url: "/pqm-service/plant/user/v1/_search",
    params: {},
    body: {
      plantUserSearchCriteria: {
        tenantId: tenantId,
        individualIds: [id],
      },
      pagination: {},
    },
    changeQueryName: "plantUser",
    staletime: 0,
  });

  useEffect(() => {
    const tempSkills = mdmsOptions?.SanitationWorkerSkills?.map((i) => ({ ...i, i18nKey: `ES_FSM_OPTION_${i.code}` }));
    const tempEmp = mdmsOptions?.SanitationWorkerEmployer?.map((i) => ({ ...i, i18nKey: `ES_FSM_OPTION_${i.code}` }));
    setSkillsOption(tempSkills);
    setEmployer(tempEmp);
  }, [mdmsOptions, ismdms]);


  const { isLoading: isLoading, isError: vendorCreateError, data: updateResponse, error: updateError, mutate } = Digit.Hooks.fsm.useWorkerUpdate(tenantId);

  const { data: workerData, isLoading: WorkerLoading } = Digit.Hooks.fsm.useWorkerSearch({
    tenantId,
    params: {
      offset: 0,
      limit: 100,
    },
    details: {
      Individual: {
        individualId: id,
      },
    },
  });

  useEffect(() => {
    const workerDetails = workerData?.Individual?.[0];
    setConfig(WorkerConfig({ t, disabled: true, skillsOption, defaultSkill: workerDetails?.skills?.filter((i) => i.isDeleted === false), employer }));
  }, [skillsOption, employer, workerData]);
  
  const { data: vendorData, isLoading: isVendorLoading, isSuccess: isVendorSuccess, error: vendorError, refetch: refetchVendor } = Digit.Hooks.fsm.useDsoSearch(
    tenantId,
    { sortBy: "name", sortOrder: "ASC", status: "ACTIVE", individualIds: workerData?.Individual?.[0]?.id },
    { enabled: workerData && !WorkerLoading, cacheTime: 0 },
    t
  );

  const {
    isLoading: isPlantUpdateLoading,
    isError: isPlantUserError,
    data: plantUserResponse,
    error: PlantUserError,
    mutate: PlantUserMutate,
  } = Digit.Hooks.fsm.usePlantUserUpdate(tenantId);

  const {
    isLoading: isVendorUpdateLoading,
    isError: isvendorUpdateError,
    data: vendorUpdateResponse,
    error: vendorUpdateError,
    mutate: vendorMutate,
  } = Digit.Hooks.fsm.useVendorUpdate(tenantId);

  function transformData(rdata) {
    const data = rdata?.additionalFields?.fields;
    const functionalRoleCount = parseInt(data?.find((item) => item?.key === "FUNCTIONAL_ROLE_COUNT")?.value, 10) || null;
    const resultArray = [];
    const allowedRoles = mdmsOptions?.SanitationWorkerFunctionalRoles;
    const resproles = rdata?.userDetails?.roles;
    if (functionalRoleCount) {
      for (let i = 1; i <= functionalRoleCount; i++) {
        const functionalRoleKey = `FUNCTIONAL_ROLE_${i}`;
        const functionalRoleValue = data.find((item) => item.key === functionalRoleKey).value;
        const fnmap = allowedRoles?.find((i) => i.code === functionalRoleValue);

        const transformedData = {
          emp_Type: {
            code: data.find((item) => item.key === `EMPLOYMENT_TYPE_${i}`).value,
          },
          fn_role: {
            code: fnmap?.code,
            i18nKey: `ES_FSM_OPTION_${fnmap?.code}`,
          },
        };

        if (functionalRoleValue === "DRIVER" && rdata?.identifiers?.[0]?.identifierType === "DRIVING_LICENSE_NUMBER") {
          transformedData.licenseNo = rdata?.identifiers?.[0]?.identifierId;
        }

        if (functionalRoleValue === "PLANT_OPERATOR") {
          transformedData.plant = {
            ...plantUserData?.plantUsers?.[0],
            name: plantUserData?.plantUsers?.[0]?.plantCode,
            i18nKey: `PQM_PLANT_${plantUserData?.plantUsers?.[0]?.plantCode}`,
          };
        }

        const tempRoles = allowedRoles?.find((i) => i.code === functionalRoleValue)?.allowedSystemRoles;
        const filterCodes = resproles.map((item) => item.code);
        const filteredArray = tempRoles.filter((item) => filterCodes.includes(item.code));
        transformedData.sys_role = filteredArray;
        // transformedData.sys_role = filteredArray?.reduce((result, item, index) => {
        //   result[index] = item;
        //   return result;
        // }, {});
        resultArray.push(transformedData);
      }
    }

    return resultArray.length !== 0 ? resultArray : undefined;
  }

  useEffect(() => {
    if (workerData && workerData?.Individual  && !isVendorLoading && vendorData ) {
      const workerDetails = workerData?.Individual?.[0];
      const respSkills = workerDetails?.skills?.filter((i) => i.isDeleted === false);
      setWorkerinfo(workerDetails);
      const values = {
        SelectEmployeePhoneNumber: {
          mobileNumber: workerDetails?.mobileNumber,
        },
        name: workerDetails?.name?.givenName,
        selectGender: {
          code: workerDetails?.gender,
          active: true,
          i18nKey: `COMMON_GENDER_${workerDetails?.gender}`,
        },
        dob: workerDetails?.dateOfBirth ? workerDetails?.dateOfBirth.split("/").reverse().join("-") : null,
        pincode: workerDetails?.address?.[0]?.pincode,
        address: {
          city: {
            code: workerDetails?.address?.[0]?.city,
          },
          locality: { ...workerDetails?.address?.[0]?.locality },
        },
        street: workerDetails?.address?.[0]?.street,
        doorNo: workerDetails?.address?.[0]?.doorNo,
        landmark: workerDetails?.address?.[0]?.landmark,
        skills: mdmsOptions?.SanitationWorkerSkills?.filter((mdm) => respSkills?.some((item) => item?.type === mdm?.code))?.map((i) => ({
          ...i,
          i18nKey: `ES_FSM_OPTION_${i.code}`,
        })),
        // workerDetails?.skills?.map((obj) => ({ ...obj, code: obj.type })),
        employementDetails: {
          employer: { name: workerDetails?.additionalFields?.fields?.find((i) => i.key === "EMPLOYER")?.value },
          vendor: vendorData?.[0],
        },
        AddWorkerRoles: transformData(workerDetails),
        documents: {
          img_photo: workerDetails?.photo
            ? [
                [
                  "Photo",
                  {
                    file: { name: "photo" },
                    fileStoreId: {
                      fileStoreId: workerDetails?.photo,
                      tenantId: tenantId,
                    },
                  },
                ],
              ]
            : null,
        },
      };
      setDefaultValues(values);
    }
  }, [workerData, WorkerLoading, plantUserData, vendorData, isVendorLoading]);

  const onFormValueChange = (setValue, formData, errors) => {
    for (let i = 0; i < formData?.AddWorkerRoles?.length; i++) {
      let key = formData?.AddWorkerRoles[i];
      if (!(key?.emp_Type && key?.fn_role && key?.sys_role && key?.fn_role?.code)) {
        setCheckRoleField(false);
        // break;
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
      (!formData?.AddWorkerRoles || formData?.AddWorkerRoles?.length === 0 || (formData?.AddWorkerRoles?.length > 0 && checkRoleField))
    ) {
      if (errors?.SelectEmployeePhoneNumber?.isMobilePresent && formData?.SelectEmployeePhoneNumber?.mobileNumber !== workerData?.Individual?.[0]?.mobileNumber) {
        setSubmitValve(false);
      } else {
        setSubmitValve(true);
      }
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

    let respSkills = workerinfo?.skills;

    // if skills are not there but present in resp then remove
    respSkills = respSkills.filter((resp_item) => skills.some((mydata_item) => resp_item.type === mydata_item.type && resp_item.level === mydata_item.level));
    // respSkills.forEach((resp_item) => {
    //   const existsInSelected = skills.some((selected_item) => resp_item.type === selected_item.type && resp_item.level === selected_item.level);
    //   resp_item.isDeleted = !existsInSelected;
    // });

    // if skills are not there in resp but present in selected then add
    skills.forEach((selected_item) => {
      const exists = respSkills.some((resp_item) => resp_item.type === selected_item.type && resp_item.level === selected_item.level);
      if (!exists) {
        respSkills.push({
          type: selected_item.type,
          level: selected_item.level,
        });
      }
    });

    const employer = data?.employementDetails?.employer?.code || data?.employementDetails?.employer?.name;
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

    const driverLicenses = roleDetails?.filter((entry) => entry.fn_role && entry.fn_role.code === "DRIVER" && entry.licenseNo).map((entry) => entry.licenseNo);
    const roleDetailsArray = [];

    if (roleDetails) {
      roleDetails?.forEach((item, index) => {
        // Extracting functional role information
        const fnRoleKey = `FUNCTIONAL_ROLE_${index + 1}`;
        const fnRoleValue = item.fn_role.code;

        // Extracting employment type information
        const empTypeKey = `EMPLOYMENT_TYPE_${index + 1}`;
        const empTypeValue = item.emp_Type.code.toUpperCase();

        // Pushing the extracted information to the output array
        roleDetailsArray.push({ key: fnRoleKey, value: fnRoleValue });
        roleDetailsArray.push({ key: empTypeKey, value: empTypeValue });
      });

      // Adding the count of functional roles
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
        ...workerinfo,
        tenantId: tenantId,
        name: {
          ...workerinfo.name,
          givenName: name,
        },
        dateOfBirth: dob,
        gender: gender,
        mobileNumber: mobileNumber,
        address: [
          {
            ...workerinfo?.address?.[0],
            tenantId: tenantId,
            pincode: pincode,
            city: tenantId,
            street: street,
            doorNo: doorNo,
            locality: {
              ...workerinfo?.address?.[0]?.locality,
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
                  ...workerinfo?.indentifiers?.[0],
                  identifierType: "DRIVING_LICENSE_NUMBER",
                  identifierId: driverLicenses?.[0],
                },
              ]
            : null,
        skills: respSkills,
        photo: photograph,
        additionalFields: {
          ...workerinfo?.additionalDetails,
          // fields: restructuredData,
          fields: roleDetailsArray,
        },
        isSystemUser: true,
        userDetails: {
          ...workerinfo?.userDetails,
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
        setShowToast({ key: "error", action: `ES_FSM_WORKER_UPDATE_FAILED` });
        setTimeout(closeToast, 5000);
      },
      onSuccess: async (data, variables) => {
        // queryClient.invalidateQueries("FSM_WORKER_SEARCH");
        // if (roleDetails.some((entry) => entry.plant)) {
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
        if (vendor && !(vendor?.id === vendorData?.[0]?.id)) {
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
            setShowToast({ key: "success", action: "UPDATE_WORKER_WITH_VENDOR" });
          } catch (updateError) {
            console.error("Error updating data:", updateError);
            setShowToast({ key: "error", action: "UPDATE_WORKER_VENDOR_FAILED" });
          }
        } else {
          setShowToast({ key: "success", action: "EDIT_WORKER_SUCCESS" });
        }
        setTimeout(() => {
          closeToast();
          queryClient.invalidateQueries("FSM_WORKER_SEARCH");
          history.push(`/${window?.contextPath}/employee/fsm/registry/worker-details?id=${id}`);
        }, 5000);
      },
    });
  };
  const isMobile = window.Digit.Utils.browser.isMobile();

  if (!defaultValues) {
    return <Loader />;
  }
  return (
    <React.Fragment>
      <div>
        <Header>{t("FSM_REGISTRY_EDIT_WORKER_HEADING")}</Header>
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

export default EditWorker;