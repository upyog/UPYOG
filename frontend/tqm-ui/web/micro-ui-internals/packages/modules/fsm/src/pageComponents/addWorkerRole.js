import {
  CardLabel,
  Dropdown,
  LabelFieldPair,
  Loader,
  RemoveableTag,
  MultiSelectDropdown,
  TextInput,
  RadioButtons,
  DustbinIcon,
  AddIcon,
} from "@egovernments/digit-ui-react-components";
import React, { Fragment, useEffect, useState } from "react";
import { cleanup } from "../utils";

const makeDefaultValues = (sessionFormData) => {
  return sessionFormData?.Jurisdictions?.map((ele, index) => {
    return {
      key: index,
      hierarchy: {
        code: ele?.hierarchy,
        name: ele?.hierarchy,
      },
      boundaryType: { label: ele?.boundaryType, i18text: ele.boundaryType ? `EGOV_LOCATION_BOUNDARYTYPE_${ele.boundaryType?.toUpperCase()}` : null },
      boundary: { code: ele?.boundary },
      roles: ele?.roles,
    };
  });
};

const AddWorkerRoles = ({ t, config, onSelect, userType, formData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const [inactiveWorkerRole, setInactiveWorkerRole] = useState([]);
  const employeeCreateSession = Digit.Hooks.useSessionStorage("NEW_EMPLOYEE_CREATE", {});
  const [sessionFormData, setSessionFormData, clearSessionFormData] = employeeCreateSession;
  const isEdit = window.location.href.includes("edit");
  const [functionalRole, setFunctionalRole] = useState([]);
  const [jurisdictions, setjurisdictions] = useState(
    !isEdit && sessionFormData?.Jurisdictions?.length > 0
      ? makeDefaultValues(sessionFormData)
      : formData?.Jurisdictions || formData[config.key]?.map((i, index) => ({ ...i, key: index + 1 })) || []
  );

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
    if (mdmsOptions?.SanitationWorkerFunctionalRoles) {
      const temp = mdmsOptions?.SanitationWorkerFunctionalRoles?.map((i) => ({ ...i, i18nKey: `ES_FSM_OPTION_${i.code}` }));
      setFunctionalRole(temp);
    }
  }, [mdmsOptions]);

  useEffect(() => {
    const jurisdictionsData = jurisdictions?.map((jurisdiction) => {
      let res = {
        id: jurisdiction?.id,
        fn_role: jurisdiction?.fn_role,
        sys_role: jurisdiction?.roles ? jurisdiction?.roles : jurisdiction?.sys_role,
        emp_Type: jurisdiction?.emp_Type,
        licenseNo: jurisdiction?.licenseNo,
        plant: jurisdiction?.plant,
      };
      res = cleanup(res);
      // if (jurisdiction?.roles) {
      //   res["roles"] = jurisdiction?.roles?.map((ele) => {
      //     delete ele.description;
      //     return ele;
      //   });
      // }
      return res;
    });

    onSelect(
      config.key,
      [...jurisdictionsData, ...inactiveWorkerRole].filter((value) => Object.keys(value).length !== 0)
    );
  }, [jurisdictions]);

  const reviseIndexKeys = () => {
    setjurisdictions((prev) => prev.map((unit, index) => ({ ...unit, key: index })));
  };

  const handleAddUnit = () => {
    setjurisdictions((prev) => [
      ...prev,
      {
        key: prev.length + 1,
        fn_role: null,
        sys_role: null,
        emp_Type: null,
        licenseNo: null,
        plant: null,
      },
    ]);
  };
  const handleRemoveUnit = (unit) => {
    if (unit.id) {
      let res = {
        id: unit?.id,
        id: unit?.id,
        fn_role: unit?.hierarchy?.code,
        sys_role: unit?.roles?.code,
        emp_Type: unit?.boundaryType?.label,
        licenseNo: unit?.boundary?.code,
        plant: unit?.boundary?.code,
        isdeleted: true,
        isActive: false,
      };
      res = cleanup(res);
      if (unit?.roles) {
        res["roles"] = unit?.roles.map((ele) => {
          delete ele.description;
          return ele;
        });
      }
      setInactiveWorkerRole([...inactiveWorkerRole, res]);
    }
    setjurisdictions((prev) => prev.filter((el) => el.key !== unit.key));
    if (FormData.errors?.Jurisdictions?.type == unit.key) {
      clearErrors("Jurisdictions");
    }
    reviseIndexKeys();
  };
  let hierarchylist = [];
  let boundaryTypeoption = [];
  const [focusIndex, setFocusIndex] = useState(-1);

  function getboundarydata() {
    return [];
  }

  // if (isLoading) {
  //   return <Loader />;
  // }
  return (
    <div>
      {jurisdictions?.map((jurisdiction, index) => (
        <AddWorkerRole
          t={t}
          formData={formData}
          jurisdictions={jurisdictions}
          key={index}
          keys={jurisdiction.key}
          jurisdiction={jurisdiction}
          setjurisdictions={setjurisdictions}
          index={index}
          focusIndex={focusIndex}
          setFocusIndex={setFocusIndex}
          handleRemoveUnit={handleRemoveUnit}
          functionalRole={functionalRole}
        />
      ))}
      {jurisdictions?.length < functionalRole?.length && (
        <label onClick={handleAddUnit} className="link-label" style={{ width: "12rem", display: "flex", gap: "0.5rem" }}>
          <div className="search-add-icon">
            <AddIcon />
          </div>
          {t("FSM_REGISTRY_ADD_WORKER_ROLE")}
        </label>
      )}
    </div>
  );
};

function AddWorkerRole({ t, jurisdiction, jurisdictions, setjurisdictions, handleRemoveUnit, index, functionalRole }) {
  const tenant = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

  const { isLoading: isPlantLoading, data: plant } = Digit.Hooks.tqm.useCustomMDMSV2({
    tenantId: tenant,
    schemaCode: "PQM.Plant",
    changeQueryName: "Plant",
    config: {
      enabled: jurisdiction?.fn_role ? true : false,
    },
  });

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
        const temp = {
          SanitationWorkerSkills: data?.FSM?.SanitationWorkerSkills,
          SanitationWorkerEmployer: data?.FSM?.SanitationWorkerEmployer,
          SanitationWorkerEmploymentType: data?.FSM?.SanitationWorkerEmploymentType.map((i) => ({ ...i, i18nKey: `ES_FSM_OPTION_${i.code}` })),
          SanitationWorkerFunctionalRoles: data?.FSM?.SanitationWorkerFunctionalRoles,
        };
        return temp;
      },
    }
  );
  const matchingMasterData = mdmsOptions?.SanitationWorkerFunctionalRoles.find((role) => role?.code === jurisdiction?.fn_role?.code);

  // Extract allowedSystemRoles from the matching object
  const allowedSysRoles = matchingMasterData ? matchingMasterData?.allowedSystemRoles?.filter((i) => i.isDefault !== true) : [];

  const [fnRoleSelected, setFnRoleSelected] = useState(mdmsOptions?.SanitationWorkerFunctionalRoles?.allowedSystemRoles);

  const [sysRole, setSysRole] = useState(allowedSysRoles);
  const [defaultsysRole, setDefaultSysRole] = useState(jurisdiction?.sys_role?.[0]);
  const [defaultSys, setDefaultSys] = useState(null);

  useEffect(() => {
    if (fnRoleSelected) {
      setSysRole(fnRoleSelected?.allowedSystemRoles?.filter((i) => i.isDefault !== true));
      setDefaultSys(fnRoleSelected?.allowedSystemRoles?.filter((i) => i.isDefault === true));
    }
  }, [fnRoleSelected]);

  const selectFunctionalRole = (value) => {
    setjurisdictions((pre) =>
      pre.map((item) =>
        item.key === jurisdiction.key
          ? {
              ...item,
              fn_role: value,
              roles: [
                {
                  code: "SANITATION_WORKER",
                  name: "Sanitation Worker",
                  isDefault: true,
                },
                {
                  code: "CITIZEN",
                  name: "Citizen",
                  isDefault: true,
                },
              ],
              sys_role: null,
              emp_Type: null,
              licenseNo: null,
            }
          : item
      )
    );
    setFnRoleSelected(value);
  };

  const selectEmpType = (value) => {
    setjurisdictions((pre) => pre.map((item) => (item.key === jurisdiction.key ? { ...item, emp_Type: value } : item)));
  };

  const selectLicenseNo = (e) => {
    setjurisdictions((pre) => pre.map((item) => (item.key === jurisdiction.key ? { ...item, licenseNo: e.target.value } : item)));
  };

  const selectrole = (e, data) => {
    let res = [];
    e &&
      e?.map((ob) => {
        res.push(ob?.[1]);
      });

    if (!res.some((role) => role?.code === "SANITATION_WORKER")) {
      res.push({
        code: "SANITATION_WORKER",
        name: "Sanitation Worker",
        isDefault: true,
      });
    }

    if (!res.some((role) => role?.code === "CITIZEN")) {
      res.push({
        code: "CITIZEN",
        name: "Citizen",
        isDefault: true,
      });
    }

    setjurisdictions((pre) => pre.map((item) => (item.key === jurisdiction.key ? { ...item, roles: res } : item)));
  };

  const selectPlant = (e, data) => {
    setjurisdictions((pre) => pre.map((item) => (item.key === jurisdiction.key ? { ...item, plant: e } : item)));
  };

  const onRemove = (index, key) => {
    let afterRemove = jurisdiction?.roles.filter((value, i) => {
      return i !== index;
    });
    setjurisdictions((pre) => pre.map((item) => (item.key === jurisdiction.key ? { ...item, roles: afterRemove } : item)));
  };

  return (
    <div key={jurisdiction?.keys} style={{ marginBottom: "16px" }}>
      <div style={{ border: "1px solid #E3E3E3", padding: "16px", marginTop: "8px" }}>
        {/* role title here */}
        <LabelFieldPair>
          <div className="label-field-pair" style={{ width: "100%" }}>
            <h2 className="card-label card-label-smaller" style={{ color: "#505A5F" }}>
              {t("FSM_REGISTRY_ROLE")} {index + 1}
            </h2>
          </div>
          {/* {jurisdictions.length > 1 ? ( */}
          <div onClick={() => handleRemoveUnit(jurisdiction)} style={{ marginBottom: "16px", padding: "5px", cursor: "pointer", textAlign: "right" }}>
            <DustbinIcon />
          </div>
          {/* ) : null} */}
        </LabelFieldPair>

        {/* functional role here */}
        <LabelFieldPair>
          <CardLabel isMandatory={true} className="card-label-smaller">{`${t("FSM_REGISTRY_TITLE_FUNCTIONAL_ROLE")} * `}</CardLabel>
          <Dropdown
            className="form-field"
            selected={jurisdiction?.fn_role}
            disable={false}
            isMandatory={true}
            option={functionalRole}
            select={selectFunctionalRole}
            optionKey="i18nKey"
            t={t}
          />
        </LabelFieldPair>

        {jurisdiction?.fn_role?.code && (
          <>
            {/* plant & license here */}
            {jurisdiction?.fn_role?.code === "DRIVER" ? (
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">
                  {t("FSM_REGISTRY_WORKER_LABEL_LICENSE")}
                  {/* {input.isMandatory ? " * " : null} */}
                </CardLabel>
                <div className="field" style={{ display: "flex" }}>
                  <TextInput className="" textInputStyle={{ width: "100%" }} value={jurisdiction?.licenseNo} onChange={selectLicenseNo} disable={false} />
                </div>
              </LabelFieldPair>
            ) : jurisdiction?.fn_role?.code === "PLANT_OPERATOR" ? (
              <LabelFieldPair>
                <CardLabel isMandatory={true} className="card-label-smaller">{`${t("FSM_REGISTRY_LABEL_PLANT")} * `}</CardLabel>
                <Dropdown
                  className="form-field"
                  selected={jurisdiction?.plant}
                  disable={false}
                  isMandatory={true}
                  option={plant}
                  select={selectPlant}
                  optionKey={"i18nKey"}
                  t={t}
                />
              </LabelFieldPair>
            ) : null}

            <LabelFieldPair style={{ alignItems: "baseline" }}>
              <CardLabel className="card-label-smaller">{`${t("FSM_REGISTRY_LABEL_EMPLOYMENT")} *`} </CardLabel>
              <div className="field">
                <RadioButtons
                  selectedOption={jurisdiction?.emp_Type}
                  onSelect={selectEmpType}
                  style={{ display: "flex", gap: "4rem" }}
                  innerStyles={{ marginLeft: "10px" }}
                  options={mdmsOptions?.SanitationWorkerEmploymentType}
                  optionsKey="i18nKey"
                  disabled={false}
                />
              </div>
            </LabelFieldPair>

            {/* system role here */}
            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("FSM_REGISTRY_LABEL_SYSTEM_ROLE")}</CardLabel>
              <div className="form-field">
                <MultiSelectDropdown
                  className="form-field"
                  isMandatory={true}
                  defaultUnit="Selected"
                  selected={
                    jurisdiction?.roles
                      ? jurisdiction?.roles?.filter((i) => i?.code !== "SANITATION_WORKER" && i?.code !== "CITIZEN")
                      : jurisdiction?.sys_role
                      ? jurisdiction?.sys_role
                      : []
                  }
                  options={sysRole}
                  onSelect={selectrole}
                  optionsKey="name"
                  t={t}
                />
                {/* <Dropdown
                  className="form-field"
                  selected={jurisdiction?.roles || defaultsysRole}
                  disable={false}
                  isMandatory={true}
                  option={sysRole}
                  select={selectrole}
                  optionKey="name"
                  t={t}
                /> */}
                {/* <MultiSelectDropdown
              className="form-field"
              isMandatory={true}
              defaultUnit="Selected"
              selected={jurisdiction?.roles}
              options={[{ code: "Driver" }, { code: "Plant Operator" }]}
              onSelect={selectrole}
              optionsKey="code"
              t={t}
            />
            <div className="tag-container">
              {jurisdiction?.roles.length > 0 &&
                jurisdiction?.roles.map((value, index) => {
                  return <RemoveableTag key={index} text={`${t(value["labelKey"]).slice(0, 22)} ...`} onClick={() => onRemove(index, value)} />;
                })}
            </div> */}
              </div>
            </LabelFieldPair>
          </>
        )}
      </div>
    </div>
  );
}

export default AddWorkerRoles;
