import { Loader, Modal, FormComposer } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";

import { useHistory } from "react-router-dom";
import { configASSETApproverApplication } from "../config";


const Heading = (props) => {
  return <h1 className="heading-m">{props.label}</h1>;
};

const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>
);

const CloseBtn = (props) => {
  return (
    <div className="icon-bg-secondary" onClick={props.onClick}>
      <Close />
    </div>
  );
};

const ActionModal = ({ t, action, tenantId, state, id, closeModal, submitAction, actionData, applicationData, businessService, moduleCode }) => {
  
  const { data: approverData, isLoading: PTALoading } = Digit.Hooks.useEmployeeSearch(
    
    tenantId,
    {
      roles: action?.roles?.[0]?.map?.((e) => ({ code: e })),
      isActive: true,
    },
    { enabled: !action?.isTerminateState }
  );

  const history = useHistory(); // Initialize useHistory



  const [config, setConfig] = useState({});
  const [defaultValues, setDefaultValues] = useState({});
  const [approvers, setApprovers] = useState([]);
  const [selectedApprover, setSelectedApprover] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(null);
  

  

  useEffect(() => {
    setApprovers(approverData?.Employees?.map((employee) => ({ uuid: employee?.uuid, name: employee?.user?.name })));
  }, [approverData]);

  
  

  function submit(data) {
   
      let workflow = { action: action?.action, comment: data?.comments, businessService, moduleName: moduleCode };
      if (uploadedFile)
        workflow["documents"] = [
          
        ];
      submitAction({
        Asset: 
          {
            ...applicationData,
            workflow,
          },
        
      });
     
   
  }

  useEffect(() => {
    if(action?.state==="INITIATED"){
      history.push(`/digit-ui/employee/asset/assetservice/edit/`+ `${applicationData?.applicationNo}`);
    }
    else {
      setConfig(
        configASSETApproverApplication({
            t,
            action,
            approvers,
            selectedApprover,
            setSelectedApprover,
            // selectFile,
            uploadedFile,
            setUploadedFile,
            businessService,
          })
        );
      
    }
  }, [action, approvers, uploadedFile]);

  return action && config.form ? (
    <Modal
      headerBarMain={<Heading label={t(config.label.heading)} />}
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      actionCancelLabel={t(config.label.cancel)}
      actionCancelOnSubmit={closeModal}
      actionSaveLabel={t(config.label.submit)}
      actionSaveOnSubmit={() => {}}
      formId="modal-action"
    >
       
        <FormComposer
          config={config.form}
          noBoxShadow
          inline
          childrenAtTheBottom
          onSubmit={submit}
          defaultValues={defaultValues}
          formId="modal-action"
        />
      
    </Modal>
  ) : (
    <Loader />
  );
};

export default ActionModal;
