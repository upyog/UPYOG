import React, { useEffect, useState } from "react";
import { CardLabel, Dropdown, LabelFieldPair, RadioButtons } from "@egovernments/digit-ui-react-components";

const SelectSWEmploymentDetails = ({ t, config, onSelect, userType, formData, setValue }) => {
  const stateId = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [selectedEmployer, setSelectedEmployer] = useState(formData[config.key]?.employer || null);
  const [selectedVendor, setSelectedVendor] = useState(formData[config.key]?.vendor || null);
  const [vendorList, setVendorList] = useState(null);

  const { isLoading: ismdms, data: mdmsOptions } = Digit.Hooks.useCustomMDMS(
    stateId,
    "FSM",
    [
      {
        name: "SanitationWorkerEmployer",
      },
    ],
    {
      select: (data) => {
        const temp = data?.FSM?.SanitationWorkerEmployer?.map((i) => ({ ...i, i18nKey: `ES_FSM_OPTION_${i.code}` }));
        return { SanitationWorkerEmployer: temp };
      },
    }
  );

  useEffect(() => {
    if (mdmsOptions && formData[config.key]?.employer) {
      const temp = mdmsOptions?.SanitationWorkerEmployer.find((i) => i.code === formData[config.key]?.employer.name);
      setSelectedEmployer(temp);
    }
  }, [mdmsOptions]);

  const { data: vendorData, isLoading: isVendorLoading, isSuccess: isVendorSuccess, error: vendorError, refetch: refetchVendor } = Digit.Hooks.fsm.useVendorSearch({
    tenantId,
    filters: {
      sortBy: "name",
      sortOrder: "ASC",
      status: "ACTIVE",
    },
    config: {
      enabled: true,
      select: (data) => {
        return data?.vendor ? data?.vendor : [];
      },
    },
  });

  useEffect(() => {
    if (selectedEmployer?.code === "ULB" && vendorData) {
      const temp = vendorData.filter((i) => i.agencyType === "ULB");
      setVendorList(temp);
      setSelectedVendor(null);
    }
    if (selectedEmployer?.code === "Private" && vendorData) {
      const temp = vendorData.filter((i) => i.agencyType === "Private");
      setVendorList(temp);
      setSelectedVendor(null);
    }
  }, [selectedEmployer, vendorData]);

  const selectVendor = (type) => {
    setSelectedVendor(type);
    onSelect(config.key, { ...formData[config.key], vendor: type });
  };

  const selectEmployer = (type) => {
    setSelectedEmployer(type);
    const updatedParams = {
      tenantId,
      sortBy: "name",
      sortOrder: "ASC",
      status: "ACTIVE",
      agencyType: type?.code === "ULB" ? "ULB" : "",
    };
    refetchVendor(updatedParams);
    onSelect(config.key, { ...formData[config.key], employer: type, vendor: null });
  };

  useEffect(() => {}, [selectedEmployer]);

  return (
    <div>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">
          {t("FSM_REGISTRY_WORKER_EMPLOYER")}
          {config.isMandatory ? " * " : null}
        </CardLabel>
        <RadioButtons
          style={{ display: "flex", gap: "5rem" }}
          onSelect={(d) => selectEmployer(d)}
          selectedOption={selectedEmployer || formData[config.key]?.employer}
          optionsKey="i18nKey"
          options={mdmsOptions?.SanitationWorkerEmployer}
        />
      </LabelFieldPair>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">
          {t("FSM_REGISTRY_WORKER_SELECT_VENDOR")}
          {config.isMandatory ? " * " : null}
        </CardLabel>
        <Dropdown
          className="form-field"
          isMandatory
          selected={selectedVendor || formData[config.key]?.vendor}
          disable={false}
          option={vendorList}
          select={selectVendor}
          optionKey="name"
          t={t}
        />
      </LabelFieldPair>
    </div>
  );
};

export default SelectSWEmploymentDetails;
