import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { TypeSelectCard } from "@egovernments/digit-ui-react-components";
import { Dropdown } from "@egovernments/digit-ui-react-components";
import { useRouteMatch, useHistory } from "react-router-dom";
import { useQueryClient } from "react-query";
import { FormComposer } from "../../../../components/FormComposer";
//import { createComplaint } from "../../../../redux/actions";
//import { useDispatch } from "react-redux";
const SelectComplaintType = ({ t, config, onSelect, value }) => {
// const [complaintType, setComplaintType] = useState(() => {
//   const { complaintType } = value;
//   return complaintType ? complaintType : {};
// });
  const goNext = () => {
    onSelect({ subType });
  };

  const textParams = config.texts;

  const menu = Digit.Hooks.pgr.useComplaintTypes({ stateCode: Digit.ULBService.getCurrentTenantId() });
  //const dispatch = useDispatch();

  function selectedValue(value) {
    setComplaintType(value);
    // SessionStorage.set("complaintType", value);
  }
  const cities = Digit.Hooks.pgr.useTenants();
  // const { t } = useTranslation();
  const getCities = () => cities?.filter((e) => e.code === Digit.ULBService.getCurrentTenantId()) || [];
  const [complaintType, setComplaintType] = useState({});
  const [subTypeMenu, setSubTypeMenu] = useState([]);
  const [subType, setSubType] = useState({});
  const [selectedCity, setSelectedCity] = useState(getCities()[0] ? getCities()[0] : null);
  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    getCities()[0]?.code,
    "admin",
    {
      enabled: !!getCities()[0],
    },
    
  );
  const [canSubmit, setSubmitValve] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const tenantId = window.Digit.SessionStorage.get("Employee.tenantId");
 // const menu = Digit.Hooks.pgr.useComplaintTypes({ stateCode: tenantId });
  const history = useHistory();
  const serviceDefinitions = Digit.GetServiceDefinitions;
  const client = useQueryClient();
  useEffect(() => {
    if (complaintType?.key && subType?.key && selectedCity?.code && selectedLocality?.code) {
      setSubmitValve(true);
    } else {
      setSubmitValve(false);
    }
  }, [complaintType, subType]);
  async function selectedType(value) {
    if (value.key !== complaintType.key) {
      if (value.key === "Others") {
        setSubType({ name: "" });
        setComplaintType(value);
        setSubTypeMenu([{ key: "Others", name: t("SERVICEDEFS.OTHERS") }]);
      } else {
        setSubType({ name: "" });
        setComplaintType(value);
        setSubTypeMenu(await serviceDefinitions.getSubMenu(tenantId, value, t));
      }
    }
  }
  function selectedSubType(value) {
    setSubType(value);
  }
  const wrapperSubmit = (data) => {
    if (!canSubmit) return;
    setSubmitted(true);
    !submitted && onSubmit(data);
  };
  //On SUbmit
  const onSubmit = async (data) => {
    if (!canSubmit) return;
    const cityCode = selectedCity.code;
    const city = selectedCity.city.name;
    const district = selectedCity.city.name;
    const region = selectedCity.city.name;
    const localityCode = selectedLocality.code;
    const localityName = selectedLocality.name;
    const landmark = data.landmark;
    const { key } = subType;
    const complaintType = key;
    const mobileNumber = data.mobileNumber;
    const name = data.name;
    const formData = { ...data, cityCode, city, district, region, localityCode, localityName, landmark, complaintType, mobileNumber, name };
   // await dispatch(createComplaint(formData));
    await client.refetchQueries(["fetchInboxData"]);
    history.push(parentUrl + "/response");
  };
  const config1 = [
    {
      head: t("CS_COMPLAINT_DETAILS_COMPLAINT_DETAILS"),
      body: [
        {
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_TYPE"),
          isMandatory: true,
          type: "dropdown",
          populators: <Dropdown option={menu} optionKey="name" id="complaintType" selected={complaintType} select={selectedType} />,
        },
        {
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_SUBTYPE"),
          isMandatory: true,
          type: "dropdown",
          menu: { ...subTypeMenu },
          populators: <Dropdown option={subTypeMenu} optionKey="name" id="complaintSubType" selected={subType} select={selectedSubType} />,
        },
        
      ],
     
    },
  ];
  return (
    <FormComposer
    heading={t("ES_CREATECOMPLAINT_NEW_COMPLAINT")}
    config={config1}
    onSubmit={goNext}
    isDisabled={false}
    label={"NEXT"}
  />
  );
};

export default SelectComplaintType;
