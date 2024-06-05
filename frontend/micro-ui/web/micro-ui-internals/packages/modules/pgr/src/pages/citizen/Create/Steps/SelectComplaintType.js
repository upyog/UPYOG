import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { TypeSelectCard } from "@upyog/digit-ui-react-components";
import { Dropdown } from "@upyog/digit-ui-react-components";
import { useRouteMatch, useHistory } from "react-router-dom";
import { useQueryClient } from "react-query";
import { FormComposer } from "../../../../components/FormComposer";

//import { createComplaint } from "../../../../redux/actions";
//import { useDispatch } from "react-redux";
const SelectComplaintType = ({ t, config, onSelect, value }) => {
  const serviceDefinitions = Digit.GetServiceDefinitions;
  const [complaintType, setComplaintType] = useState(() => {
    const { complaintType } = value;
    return complaintType ? complaintType : {};
  });
  const [subType, setSubType] = useState(() => {
    const { subType } = value;
    return subType ? subType : {};
  });
  const [priorityLevel, setPriorityLevel]=useState(()=>{
    const {priorityLevel}=value;
    return priorityLevel? priorityLevel:{};
  })
  const goNext = () => {
    sessionStorage.setItem("complaintType",JSON.stringify(complaintType))
    onSelect({ subType , priorityLevel});
  };


  const textParams = config.texts;

  const menu = Digit.Hooks.pgr.useComplaintTypes({ stateCode: Digit.ULBService.getCurrentTenantId() });
  const  priorityMenu= 
  [
    {
      "name": "LOW",
      "code": "LOW",
      "active": true
    },
    {
      "name": "MEDIUM",
      "code": "MEDIUM",
      "active": true
    },
    {
      "name": "HIGH",
      "code": "HIGH",
      "active": true
    }

  ]
  const prioritylevel=priorityLevel.code;
  const cities = Digit.Hooks.pgr.useTenants();
  const [subTypeMenu, setSubTypeMenu] = useState([]);
  const pttype=sessionStorage.getItem("type")
  useEffect(()=>{
    (async()=>{
      if (pttype=="PT") {
        setComplaintType(valuenew);
        setSubTypeMenu(await serviceDefinitions.getSubMenu("pg.citya", valuenew, t));
      }
    })();   
  },[]) 
 
  function selectedSubType(value) {
   
    console.log("selectedSubType",value)
    setSubType(value);
  }
  const config1 = [
    {
      head: t("CS_COMPLAINT_DETAILS_COMPLAINT_DETAILS"),
      body: [
        {
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_TYPE"),
          isMandatory: true,
          type: "dropdown",
          populators: <Dropdown option={menu} optionKey="name" id="complaintType" selected={complaintType} select={selectedValue} disable={pttype}/>,
        },
        {
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_SUBTYPE"),
          isMandatory: true,
          type: "dropdown",
          menu: { ...subTypeMenu },
          populators: <Dropdown option={subTypeMenu} optionKey="name" id="complaintSubType" selected={subType} select={selectedSubType} />,
        },
        {
          
          label: t("CS_COMPLAINT_DETAILS_COMPLAINT_PRIORITY_LEVEL"),
             isMandatory: true,
             type: "dropdown",
             populators: <Dropdown option={priorityMenu} optionKey="name" id="priorityLevel" selected={priorityLevel} select={selectedPriorityLevel} />,
           
         },
        
      ],
     
    },
  ];
  const tenantId = window.Digit.SessionStorage.get("Digit.Citizen.tenantId");
  async function selectedValue(value) {
    if (value.key !== complaintType.key) {
      if (value.key === "Others") {
        setSubType({ name: "" });
        setSubTypeMenu([{ key: "Others", name: t("SERVICEDEFS.OTHERS") }]);
      } else {
        setSubType({ name: "" });
        setComplaintType(value);
        setSubTypeMenu(await serviceDefinitions.getSubMenu("pg.citya", value, t));
      }
    }
  }
  async function selectedPriorityLevel(value){
    sessionStorage.setItem("priorityLevel", JSON.stringify(value))
    setPriorityLevel(value);
    //setPriorityMenu(await serviceDefinitions.getSubMen)
  }

  // function selectedValue(value) {
  //   setComplaintType(value);
  //   window.Digit.SessionStorage.set("complaintType", value);
  // }
  return (
    <TypeSelectCard
      {...textParams}
      {...{ menu: menu }}
      {...{ optionsKey: "name" }}
      {...{ selected: selectedValue }}
      {...{ selectedOption: complaintType }}
      {...{ onSave: goNext }}
      {...{ t }}
      disabled={Object.keys(complaintType).length === 0 || complaintType === null ? true : false}
    />
  );
};

export default SelectComplaintType;
