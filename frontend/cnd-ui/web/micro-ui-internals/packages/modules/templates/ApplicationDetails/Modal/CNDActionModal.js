import { Loader, Modal, FormComposer } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect, act } from "react";
import { configCNDApproverApplication } from "../config";
import { useHistory } from "react-router-dom";

/* This component, ActionModal, is responsible for displaying a modal dialog 
 that allows users to submit actions related to a specific application. 
 It fetches approvers based on the selected action and manages the state 
 for the selected approver and any uploaded files. When the form is submitted, 
 it constructs a workflow object with the action details and sends it to the 
 submitAction function for processing. The modal also includes a loading state 
 while the approver data is being fetched.*/


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

  const { data: approverData, } = Digit.Hooks.useEmployeeSearch(tenantId,
    {
      roles: action?.assigneeRoles?.map?.((e) => ({ code: e })),
      isActive: true,
    },
    { enabled: true }
  );
  const history = useHistory(); // Initialize useHistory

    /* we have used it here to fetch vendor data when the state is "PENDING_FOR_VEHICLE_DRIVER_ASSIGN". */
   const { data: dsoData } = Digit.Hooks.cnd.useVendorSearch({
    tenantId,
    config: { enabled: true },
  });
  /*  
   This is used to filter vendors from `dsoData` that have an additional 
   description field with the value "CND".The filtered vendors are then stored in the `vendorDescription` array with their names 
   as code, name, and i18nKey */
  
  let vendorDescription = [];
  dsoData?.vendor?.map((item) => {
    if (item?.additionalDetails?.serviceType === "CND") {
      vendorDescription.push({ code: item?.name, name: item?.name, i18nKey: item?.name, vendorId: item?.ownerId});
    }
  });

  const { data:vehicleData,isSuccess } = Digit.Hooks.cnd.useVehiclesSearch({
    tenantId,
    config: { enabled: true },
  });

  /*  
   This is used to extract vehicle details from `vehicleData` and store them in the `vehicleDescription` array. 
   Each entry contains the vehicle's registration number and vehicle capacity.
   */
  let vehicleDescription = [];
  vehicleData?.vehicle?.map((item) => {
    if(item?.additionalDetails?.serviceType==="CND"){
    vehicleDescription.push({
      code: item?.registrationNumber,
      name: item?.registrationNumber,
      i18nKey: item?.registrationNumber,
      tankerCapacity: item?.tankCapacity,
      vehicleId: item?.id,
    })};
  });

  const [config, setConfig] = useState({});
  const [approvers, setApprovers] = useState([]);
  const [selectedApprover, setSelectedApprover] = useState(null);
  const [selectedVendor, setSelectedVendor] = useState(null);
  const [selectVehicle, setSelectVehicle] = useState(null);

  useEffect(() => {
    setApprovers(approverData?.Employees?.map((employee) => ({ uuid: employee?.uuid, name: employee?.user?.name })));
  }, [approverData]);

  function submit(data) {
    let workflow = { action: action?.action, comments: data?.comments, businessService, moduleName: moduleCode, assignes:
      action?.state === "WASTE_PICKUP_INPROGRESS"
      ? null
      : action?.state === "PENDING_FOR_VEHICLE_DRIVER_ASSIGN"
      ? [selectedVendor?.vendorId]
      : [selectedApprover?.uuid]};

    if (action?.state === "PENDING_FOR_VEHICLE_DRIVER_ASSIGN") {
      applicationData.vendorId = selectedVendor?.vendorId;
      applicationData.pickupDate = data?.date;

    };
    if (action?.action === "ASSIGN_VEHICLE_DRIVER") {
      applicationData.vehicleId = selectVehicle?.vehicleId;
    };
    submitAction({
      cndApplication:
      {
        ...applicationData,
        workflow,
      },

    });
  }
"COMPLETE_REQUEST"
  useEffect(() => {
    if(action?.action==="APPROVE"){
      history.push(`/cnd-ui/employee/cnd/cnd-service/edit/`+ `${applicationData?.applicationNumber}`);
    }
    else if (action?.action==="COMPLETE_REQUEST"){
      history.push(`/cnd-ui/employee/cnd/cnd-service/facility-centre/`+ `${applicationData?.applicationNumber}`);
    }
    else if (action.action === "PAY") {
      return history.push(`/cnd-ui/employee/payment/collect/cnd-service/${applicationData.applicationNumber}`);
    }
    else{
    setConfig(
        configCNDApproverApplication({
        t,
        action,
        approvers,
        selectedApprover,
        setSelectedApprover,
        businessService,
        vendorDescription:dsoData ? vendorDescription : undefined,
        selectedVendor,
        setSelectedVendor,
        vehicleDescription: vehicleData ? vehicleDescription : undefined, 
        selectVehicle,
        setSelectVehicle
      })
    )};
  }, [action, approvers]);

  return action && config.form ? (
    <Modal
      headerBarMain={<Heading label={t(config.label.heading)} />}
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      actionCancelLabel={t(config.label.cancel)}
      actionCancelOnSubmit={closeModal}
      actionSaveLabel={t(config.label.submit)}
      actionSaveOnSubmit={() => { }}
      formId="modal-action"
    >

      <FormComposer
        config={config.form}
        noBoxShadow
        inline
        childrenAtTheBottom
        onSubmit={submit}
        formId="modal-action"
      />

    </Modal>
  ) : (
    <Loader />
  );
};

export default ActionModal;
