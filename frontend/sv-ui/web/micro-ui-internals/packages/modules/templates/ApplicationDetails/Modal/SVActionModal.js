import { Loader, Modal, FormComposer, CloseSvg } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { configSVApproverApplication } from "../config";
import { useHistory } from "react-router-dom";
import EXIF from "exif-js";
import { getOpenStreetMapUrl } from "../../../../libraries/src/services/atoms/urls";

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

const ActionModal = ({ t, action, tenantId, state, id, closeModal, submitAction, actionData, applicationData, businessService, moduleCode, vending_Zone, UserVendingZone, UserVendingZoneCode }) => {
  const history = useHistory();
  const user = Digit.UserService.getUser().info;
  const selectApprover = user?.roles

  const { data: approverData, isLoading: PTALoading } = Digit.Hooks.useEmployeeSearch(

    tenantId,
    {
      roles: action?.assigneeRoles?.map?.((e) => ({ code: e })),
      isActive: true,
    },
    { enabled: action?.isTerminateState }
  );

  
  const [config, setConfig] = useState({});
  const [defaultValues, setDefaultValues] = useState({});
  const [approvers, setApprovers] = useState([]);
  const [selectedApprover, setSelectedApprover] = useState(null);
  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(null);
  const [error, setError] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [geoLocationData, setGeoLocationData] = useState();
  const [vendingZones, setvendingZones] = useState();
  const [comment, setComment] = useState("");


  useEffect(() => {
    setApprovers(approverData?.Employees?.map((employee) => ({ uuid: employee?.uuid, name: employee?.user?.name })));
  }, [approverData]);

  function selectFile(e) {
    setIsUploading(true);
    setFile(e.target.files[0]);
  }

  function convertToDecimal(coordinate) {
    const degrees = coordinate[0];
    const minutes = coordinate[1];
    const seconds = coordinate[2];
    return degrees + minutes / 60 + seconds / 3600;
  }

  const fetchAddress = async (lat, lng) => {
    try {
      const response = await fetch(getOpenStreetMapUrl(lat, lng));

      if (!response.ok) throw new Error("Failed to fetch address");

      const data = await response.json();
      if (data && data.address) {

        const addr = [
          data.address?.amenity,
          data.address?.road,
          data.address?.neighbourhood,
          data.address?.suburb,
          data.address?.city,
          data.address?.state,
          data.address?.postcode,
          data.address?.country
        ]
          .filter(Boolean) // Removes undefined or null values
          .join(", ");
        setGeoLocationData(addr);
      }

    } catch (error) {
      console.error("Error fetching address:", error);
    }
  }

  function extractGeoLocation(file) {
    return new Promise((resolve) => {
      EXIF.getData(file, function () {
        const lat = EXIF.getTag(this, 'GPSLatitude');
        const lon = EXIF.getTag(this, 'GPSLongitude');
        if (lat && lon) {
          const latDecimal = convertToDecimal(lat);
          const lonDecimal = convertToDecimal(lon);
          resolve({ latitude: latDecimal, longitude: lonDecimal });
        } else {
          resolve({ latitude: null, longitude: null });
        }
      });
    });
  }


  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {

        extractGeoLocation(file).then(({ latitude, longitude }) => {
          if (!latitude || !longitude) {
            console.log("No geolocation data found in the image.");
          }
          else {
            fetchAddress(latitude, longitude);
          }
        });

        if (file.size >= 5242880) {
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage("StreetVending", file, tenantId);
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              setError(t("CS_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            setError(t("CS_FILE_UPLOAD_ERROR"));
          }
          finally {
            setIsUploading(false)
          }
        }
      }
    })();
  }, [file]);


  function submit(data) {
    let workflow = { action: action?.action, comments: data?.comments, businessService, moduleName: moduleCode, assignes: action?.action === "SENDBACKTOCITIZEN" ? [applicationData?.auditDetails?.createdBy] : selectedApprover?.uuid ? [selectedApprover?.uuid] : null };
    if (uploadedFile)
      workflow["documents"] = [
        {
          documentType: action?.action + " DOC",
          fileName: file?.name,
          fileStoreId: uploadedFile,
        },
      ];
    submitAction({
      streetVendingDetail:
      {
        ...applicationData,
        vendingZone: vendingZones?.code || UserVendingZoneCode,
        workflow,
      },

    });
  }


  useEffect(() => {
    setConfig(
      configSVApproverApplication({
        t,
        action,
        approvers,
        selectedApprover,
        setSelectedApprover,
        selectFile,
        uploadedFile,
        setUploadedFile,
        businessService,
        isUploading,
        geoLocationData,
        vending_Zone,
        vendingZones,
        setvendingZones,
        UserVendingZone,
        selectApprover,
      })
    );
  }, [action, approvers, uploadedFile]);


  return action && config.form ? (
    <Modal
      headerBarMain={<Heading label={t(config.label.heading)} />}
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      actionCancelLabel={t(config.label.cancel)}
      actionCancelOnSubmit={closeModal}
      actionSaveLabel={t(config.label.submit)}
      actionSaveOnSubmit={() => { }}
      formId="modal-action"
      isDisabled={(action?.docUploadRequired && !uploadedFile && !comment) || !comment}
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
