import { Loader, Modal, FormComposer } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";

import { configWTApproverApplication } from "../config/WTApproverApplication";
/*
  ActionModal Component
  
  This component is responsible for rendering a modal that allows an approver to take action 
  on a specific task. It includes form fields for comments and file uploads, and provides the 
  ability to select an approver from a list fetched via an API. When the form is submitted, 
  it triggers the `submitAction` function with the necessary data.
  
  Key Features:
  - **File Upload**: Handles file upload with size validation (max 5MB).
  - **Approver Selection**: Dynamically loads approvers based on roles and sets the selected approver.
  - **Form Configuration**: Configures the form dynamically based on the `action` prop.
  - **Error Handling**: Displays error messages for file uploads or other validation issues.
  - **Submit Action**: Calls the provided `submitAction` function with the form data when submitted.
*/

// Heading component - renders an h1 element with a dynamic label prop.
const Heading = (props) => {
  return <h1 className="heading-m">{props.label}</h1>;
};
// Close component - renders an SVG icon representing a "close" button (X icon).
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
      roles: action?.assigneeRoles?.map?.((e) => ({ code: e })),
      isActive: true,
    },
    { enabled: !action?.isTerminateState }
  );
  /* 
   We have used this hook as it is already defined in FSM, 
   and we have used it here to fetch vendor data when the state is "PENDING_FOR_VEHICLE_DRIVER_ASSIGN". */
  
  const { data: dsoData, isLoading: isLoading, isSuccess: isDsoSuccess, error: dsoError, refetch } = Digit.Hooks.fsm.useVendorSearch({
    tenantId,
    config: { enabled: action?.state === "PENDING_FOR_VEHICLE_DRIVER_ASSIGN" },
  });
  
  /*  
   This is used to filter vendors from `dsoData` that have an additional 
   description field with the value "WT".The filtered vendors are then stored in the `vendorDescription` array with their names 
   as code, name, and i18nKey */
  
  let vendorDescription = [];
  dsoData?.vendor?.map((item) => {
    if (item?.additionalDetails?.serviceType === applicationData?.bookingNo.split("-")[0]) {
      vendorDescription.push({ code: item?.name, name: item?.name, i18nKey: item?.name, vendorId: item?.id, uuid:item?.owner?.uuid });
    }
  });

/* 
   We have used this hook as it is already defined in FSM,
   and we have used it here to fetch vehicle data when the system state is "DELIVERY_PENDING". */
  
  const { data:vehicleData,isSuccess } = Digit.Hooks.fsm.useVehiclesSearch({
    tenantId,
    config: { enabled: action?.state === "DELIVERY_PENDING" },
  });

/*  
   This is used to extract vehicle details from `vehicleData` and store them in the `vehicleDescription` array. 
   Each entry contains the vehicle's registration number and tanker capacity.  */
  
  let vehicleDescription = [];
  vehicleData?.vehicle?.map((item) => {
    vehicleDescription.push({
      code: item?.registrationNumber,
      name: item?.registrationNumber,
      i18nKey: item?.registrationNumber,
      tankerCapacity: item?.tankCapacity,
      vehicleId: item?.id,
      uuid: item?.owner?.uuid,
    });
  });

  const [config, setConfig] = useState({});
  const [defaultValues, setDefaultValues] = useState({});
  const [approvers, setApprovers] = useState([]);
  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(null);
  const [error, setError] = useState(null);
  const [selectedVendor, setSelectedVendor] = useState(null);
  const [selectVehicle, setSelectVehicle] = useState(null);
  const [comment, setComment] = useState("");
  const history = useHistory();


  useEffect(() => {
    setApprovers(approverData?.Employees?.map((employee) => ({ uuid: employee?.uuid, name: employee?.user?.name })));
  }, [approverData]);

  function selectFile(e) {
    setFile(e.target.files[0]);
  }

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 5242880) {
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage("WT", file, tenantId);
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              setError(t("CS_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            setError(t("CS_FILE_UPLOAD_ERROR"));
          }
        }
      }
    })();
  }, [file]);
  

  function submit(data) {
      let workflow = { action: action?.action, comments: data?.comments, businessService, moduleName: moduleCode };
    if (uploadedFile)
      workflow["documents"] = [
        {
          documentType: action?.action + " DOC",
          fileName: file?.name,
          fileStoreId: uploadedFile,
        },
      ];
    if (action?.state === "PENDING_FOR_VEHICLE_DRIVER_ASSIGN") {
      workflow.assignes =[selectedVendor?.uuid];
      applicationData.vendorId = selectedVendor?.vendorId;
    };

    if (action?.state === "DELIVERY_PENDING") {
      applicationData.vehicleId = selectVehicle?.vehicleId;
    };
   /*  
 * Constructs the request payload based on the business service type.  
 * If `businessService` is "watertanker", wraps `applicationData` in `waterTankerBookingDetail`.  
 * Otherwise, wraps it in `mobileToiletBookingDetail`.  
 *  
 * The payload is then passed to `submitAction` for processing.  
 */
   let requestPayload;
   if (businessService === "watertanker") {
     requestPayload = { waterTankerBookingDetail: { ...applicationData, workflow } };
   } else if (businessService === "treePruning") {
     requestPayload = { treePruningBookingDetail: { ...applicationData, workflow } };
   } else {
     requestPayload = { mobileToiletBookingDetail: { ...applicationData, workflow } };
   }
     submitAction(requestPayload);
      }

  useEffect(() => {
    if (action) {
      if (action.action === "PAY") {
        const bookingPrefix = applicationData.bookingNo?.split("-")[0];
        let servicePath = "";
        switch (bookingPrefix) {
          case "WT":
            servicePath = "request-service.water_tanker";
            break;
          case "MT":
            servicePath = "request-service.mobile_toilet";
            break;
          case "TP":
            servicePath = "request-service.tree_pruning";
            break;
          default:
            console.error("Unknown booking prefix:", bookingPrefix);
            return; // Or handle as needed
        }
      
        return history.push(`/digit-ui/employee/payment/collect/${servicePath}/${applicationData.bookingNo}`);
      }
      setConfig(
        configWTApproverApplication({
          t,
          action,
          selectFile,
          uploadedFile,
          setUploadedFile,
          selectedVendor,
          setSelectedVendor,
          vendorDescription: dsoData ? vendorDescription : undefined,
          vehicleDescription: vehicleData ? vehicleDescription : undefined, 
          selectVehicle,
          setSelectVehicle,
        })
      );
      
    }
  },[action, approvers, uploadedFile, dsoData,selectVehicle,vehicleData]);

  return action && config.form ? (
    <Modal
      headerBarMain={<Heading label={t(config.label.heading)} />}
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      actionCancelLabel={t(config.label.cancel)}
      actionCancelOnSubmit={closeModal}
      actionSaveLabel={t(config.label.submit)}
      actionSaveOnSubmit={() => {}}
      formId="modal-action"
      isDisabled={
        (action?.docUploadRequired && !uploadedFile && !comment) ||
        (action?.state === "PENDING_FOR_VEHICLE_DRIVER_ASSIGN" && !selectedVendor && !comment) ||
        (action?.state === "DELIVERY_PENDING" && !selectVehicle && !comment) ||
        !comment
      }
    >
       
      <FormComposer
        config={config.form}
        noBoxShadow
        inline
        childrenAtTheBottom
        onSubmit={submit}
        defaultValues={defaultValues}
        formId="modal-action"
        onFormValueChange={(setValue, values) => {
          setComment(values?.comments);
        }}
      />
      
    </Modal>
  ) : (
    <Loader />
  );
};

export default ActionModal;
