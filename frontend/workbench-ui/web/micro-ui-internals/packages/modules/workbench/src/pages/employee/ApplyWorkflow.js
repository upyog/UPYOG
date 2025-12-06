import { Header, Dropdown, LabelFieldPair, CardLabel, Card, Button,Toast,TextInput,Modal } from "@egovernments/digit-ui-react-components";
import React, { useState, useEffect} from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import ReactJson from "react-json-view";
import { saveAs } from "file-saver";

const Heading = (props) => {
  return <h1 className="heading-m">{props.t("WBH_SELECT_OPERATION")}</h1>;
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

function ApplyWorkflow() {
  const { t } = useTranslation();
  const { control, formState: localFormState, watch, trigger } = useForm();

  const [tenant, setTenant] = useState("");
  const [schemaCode, setSchemaCode] = useState(""); // Default value
  const [business, setBusiness] = useState("");
  const [businessService, setBusinessService] = useState("");
  const [selectedUniqueIdentifier, setSelectedUniqueIdentifier] = useState(null);
  const [jsonData, setJsonData] = useState({});
  const [workflowOperationData, setWorkflowOperationData] = useState("");
  const schemaCodePayload = "WORKFLOW.BusinessServices";
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [isModalOpen, setModalOpen] = useState(false); // State to track modal visibility.
  const [selectedOperation, setSelectedOperation] = useState(null);
  const [shouldCallApi, setShouldCallApi] = useState(false);
 // State for managing the toast notification
   const [toastProps, setToastProps] = useState({
    label: "",
    error: false,
    warning: false,
    isDleteBtn: false,
  });

 const handleApplyChanges = () => {
   setModalOpen(true); // Open the modal.
   };

  const handleCancel = () => {
    setModalOpen(false); // Close the modal when cancel is clicked.
    setShouldCallApi(false);
  };

 

  const MdmsCriteria = {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: "common-masters",
        masterDetails: [
          {
            name: "WorkbenchWorkflowOperation",
          },
        ],
      },
    ],
  };
  const businessServiceMdmsCriteria = {
    tenantId: tenantId,
    schemaCode: schemaCodePayload,
    limit: 200,
  };


  const { isLoading, data } = Digit.Hooks.useCustomAPIHook({
    url: `/${Digit.Hooks.workbench.getMDMSContextPath()}/v2/_search`,
    body: { MdmsCriteria: businessServiceMdmsCriteria },
    config: {
      select: (data) => {
        console.log("Fetched MDMS Data for business services:", data);

        return {
          uniqueIdentifierData: data?.mdms?.map((item) => ({
            i18nKey: item?.uniqueIdentifier,
            code: item?.uniqueIdentifier,
            tenant: item?.data?.tenantId,
            schemaCode: item?.schemaCode,
            business: item?.data?.business,
            businessService: item?.data?.businessService,
            data: item?.data,
          })),
        };
      },
    },
  });

  console.log("Unique Identifier Data:", data?.uniqueIdentifierData);

  const { dataVal } = Digit.Hooks.useCustomAPIHook({
    url: `/${Digit.Hooks.workbench.getMDMSContextPath()}/v1/_search`,
    body: { MdmsCriteria },
    config: {
      select: (dataVal) => {
        console.log("Fetched MDMS Data for Workflow operation:", dataVal);
        const mappedData = dataVal?.MdmsRes?.["common-masters"]?.WorkbenchWorkflowOperation.map((item) => ({
          i18nKey: item?.code,
          code: item?.code,
        }));
        setWorkflowOperationData(mappedData);
        return {
          workflowOperationData: mappedData,
        };
      },
    },
  });

  const { isLoading: isApplying } = Digit.Hooks.useCustomAPIHook({
    url: "/apply-workflow/api/v1/_process",
    body: shouldCallApi ? {
      BusinessService: {
        tenantId: tenantId,
        uniqueIdentifier: selectedUniqueIdentifier?.code,
        applyType: selectedOperation?.code === "CREATE_WORKFLOW" ? "create" : "update",
      }
    } : null,
    config: {
      enabled: shouldCallApi,
      onSuccess: (data) => {
        setToastProps({
          label: t("Workflow Applied Successfully"),
          error: false,
          warning: false,
          isDleteBtn: true,
        });
        setModalOpen(false);
        setShouldCallApi(false);
      },
      onError: (error) => {
        
        setToastProps({
          label:  t("Error occurred while applying workflow"),
          error:true,
          warning: false,
          isDleteBtn: true,
        });
      },
      select: (data) => {
        console.log("Workflow applied:", data);
        setModalOpen(false);
        setShouldCallApi(false);
      },
    },
  });

  const handleApply = () => {
    if (!selectedOperation) {
      alert("Please select an operation to proceed.");
      return;
    }
    setShouldCallApi(true);
  };

 useEffect(() => {
    console.log("workflowOperationData updated:", workflowOperationData);
  }, [workflowOperationData]);

  useEffect(() => {
    if (selectedUniqueIdentifier) {
      const selectedData = data?.uniqueIdentifierData?.find((item) => item.code === selectedUniqueIdentifier.code);
      if (selectedData) {
        setTenant(selectedData.tenant);
        setBusiness(selectedData.business);
        setBusinessService(selectedData.businessService);
        setSchemaCode(selectedData.schemaCode), setJsonData(selectedData.data);
      }
    }
  }, [selectedUniqueIdentifier, data]);

  const handleDownloadJson = () => {
    if (!jsonData || Object.keys(jsonData).length === 0) {
      alert("No data available to download JSON file.");
      return;
    }
    const blob = new Blob([JSON.stringify(jsonData, null, 2)], { type: "application/json" });
    saveAs(blob, "workflow-data.json"); // Save as a .json file
  };

  return (
    <React.Fragment>
      <Header className="works-header-search">{t("WBH_WORKFLOW_CREATE/UPDATE_SERVICE")}</Header>

      <Card>
        <div style={{ marginBottom: "16px" }}>
          <div style={{ padding: "16px", marginTop: "8px" }}>
            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("WBH_UNIQUE_IDENTIFIER")}</CardLabel>
              <Controller
                control={control}
                name="tenant"
                render={(props) => (
                  <Dropdown
                    className="form-field"
                    //selected={tenantOptions}
                    selected={selectedUniqueIdentifier}
                    select={(value) => {
                      setSelectedUniqueIdentifier(value);
                      props.onChange(value);
                    }}
                    option={data?.uniqueIdentifierData}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />
            </LabelFieldPair>

            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("WBH_TENANT")}</CardLabel>
              <div className="field">
                <Controller
                  control={control}
                  name="tenant"
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z0-9/-\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                  }}
                  render={(props) => <TextInput value={tenant} style={{ marginTop: "20px" }} />}
                />
              </div>
            </LabelFieldPair>

            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("WBH_SCHEMA_CODE")}</CardLabel>
              <div className="field">
                <Controller
                  control={control}
                  name="schemacode"
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z0-9/-\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                  }}
                  render={(props) => <TextInput value={schemaCode} />}
                />
              </div>
            </LabelFieldPair>

            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("WBH_BUSINESS")}</CardLabel>
              <div className="field">
                <Controller
                  control={control}
                  name="business"
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z0-9/-\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                  }}
                  render={(props) => <TextInput value={business} />}
                />
              </div>
            </LabelFieldPair>

            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("WBH_BUSINESS_SERVICE")}</CardLabel>
              <div className="field">
                <Controller
                  control={control}
                  name="businessservice"
                  //defaultValue={comingDataFromAPI?.assetBookRefNo}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z0-9/-\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                  }}
                  render={(props) => (
                    <TextInput
                      value={businessService}
                      // disable={false}
                      // autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "BookPagereference"}
                    />
                  )}
                />
              </div>
            </LabelFieldPair>

          
            {jsonData && Object.keys(jsonData).length > 0 && (
              <div style={{ border: "1px solid #ccc", padding: "16px", borderRadius: "8px", marginTop: "50px" }}>
                {jsonData && <ReactJson style={{ fontSize: "16px" }} src={jsonData} name={false} collapsed={false} enableClipboard={true} />}
              </div>
            )}
          </div>
          <div style={{ display: "flex", gap:"20px" }}>
            <Button label="Apply changes" onButtonClick={handleApplyChanges} />
            <Button label="Download Json" onButtonClick={handleDownloadJson} />
             {/* Modal */}
      {isModalOpen && (
          <Modal
          headerBarMain={<Heading t={t}/>}
          headerBarEnd={<CloseBtn onClick={handleCancel} />}
          actionCancelLabel={null}
          // actionSaveLabel={"Apply"}
          actionSaveLabel={isApplying ? "Applying..." : "Apply"}
          actionSaveOnSubmit={handleApply} // Call handleApply on Apply button click
          formId="modal-action"
          popupStyles={{
            width: "60%", // Increase the modal width (you can adjust this value)
            minWidth: "400px", // Ensure the modal doesn't become too narrow
            maxWidth: "800px", // Optional: Set a maximum width
            height: "auto", // Adjust the height to fit the content
            maxHeight: "70vh", // Limit the height for scrollable content
            position: "fixed",
            transform: "translate(-50%, -50%)", // Centering the modal
            top: "50%",
            left: "50%",
            margin: "0 auto",
            padding: "20px", // Internal padding for the modal
            overflow: "auto", // Allow scrolling if content overflows
          }}
          
      >
        <LabelFieldPair>
      <CardLabel className="card-label-smaller">{t("WBH_SELECT_OPERATION")}</CardLabel>
      <Controller
        control={control}
        name="selectoperation"
        render={(props) => (
          <Dropdown
            className="form-field"
            placeholder={t("WBH_SELECT_OPERATION")}
            selected={props.value}
            select={(value) => {props.onChange(value)
              setSelectedOperation(value); // Update local state for selected operation
            }
          }
            option={workflowOperationData} // Options for dropdown
            optionKey="i18nKey" // Use the `i18nKey` for the dropdown options
            t={t}
          />
        )}
      />
    </LabelFieldPair>
    {/* Note below the Apply button */}
    <div style={{ marginTop: "30px", fontSize: "20px" }}>
      <span>{t("WBH_NOTE")}</span>
    </div>
      </Modal>
      )}
          </div>
        </div>
        <div>
           {/* Show Toast based on the state */}
      {toastProps.label && (
        <Toast
          label={toastProps.label}
          error={toastProps.error}
          warning={toastProps.warning}
          isDleteBtn={toastProps.isDleteBtn}
          onClose={() => setToastProps({ ...toastProps, label: "" })} // Close Toast
        />
      )}
        </div>
      </Card>
    </React.Fragment>
  );
}

export default ApplyWorkflow;
