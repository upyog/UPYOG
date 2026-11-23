import { Loader, Modal, FormComposer, Dropdown, UploadFile } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect, useMemo } from "react";

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

const WORKSActionModal = ({
  t,
  action,
  tenantId,
  closeModal,
  submitAction,
  applicationData,
}) => {
  const assigneeRoles = action?.assigneeRoles?.filter(Boolean) || [];
  const shouldFetchApprovers = !action?.isTerminateState && assigneeRoles.length > 0;

  const { data: approverData, isLoading: approverLoading } = Digit.Hooks.useEmployeeSearch(
    tenantId,
    {
      roles: assigneeRoles.map((code) => ({ code })),
      isActive: true,
    },
    { enabled: shouldFetchApprovers }
  );

  const [approvers, setApprovers] = useState([]);
  const [selectedApprover, setSelectedApprover] = useState(null);
  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const list = (approverData?.Employees || []).map((employee) => {
      const currentAssignment =
        employee?.assignments?.find((a) => a?.isCurrentAssignment) ||
        employee?.assignments?.[0] ||
        {};
      return {
        uuid: employee?.uuid || employee?.user?.uuid,
        name: employee?.user?.name || employee?.user?.userName || employee?.code,
        department: currentAssignment?.department || "",
        designation: currentAssignment?.designation || "",
      };
    });
    setApprovers(list);
  }, [approverData]);

  function selectFile(e) {
    setFile(e.target.files?.[0] || null);
  }

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 5242880) {
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage("WORKS", file, tenantId);
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response.data.files[0].fileStoreId);
            } else {
              setError(t("CS_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            setError(t("CS_FILE_UPLOAD_ERROR"));
          }
        }
      }
    })();
  }, [file, tenantId, t]);

  function submit(data) {
    const workflow = {
      action: action?.action,
      comments: data?.comments || "",
    };

    if (selectedApprover?.uuid) {
      workflow.assignes = [selectedApprover.uuid];
    }
    if (selectedApprover?.department) {
      workflow.approvingDepartment = selectedApprover.department;
    }
    if (selectedApprover?.designation) {
      workflow.designation = selectedApprover.designation;
    }
    if (selectedApprover?.name) {
      workflow.approverName = selectedApprover.name;
    }
    if (uploadedFile) {
      workflow.documents = [
        {
          documentType: `${action?.action}_DOC`,
          fileName: file?.name,
          fileStoreId: uploadedFile,
        },
      ];
    }

    submitAction({
      ActionPlan: {
        ...applicationData,
        declarationAccepted:
          applicationData?.declarationAccepted !== undefined
            ? applicationData.declarationAccepted
            : true,
        workflow,
      },
    });
  }

  const config = useMemo(() => {
    if (!action) return null;
    const body = [];

    if (!action?.isTerminateState && approvers.length > 0) {
      body.push({
        label: t("WF_ASSIGNEE_NAME", "Assignee Name"),
        type: "dropdown",
        populators: (
          <Dropdown
            option={approvers}
            optionKey="name"
            id="works-approver-dropdown"
            selected={selectedApprover}
            select={setSelectedApprover}
            t={t}
          />
        ),
      });
    }

    body.push(
      {
        label: `${t("WF_COMMON_COMMENTS", "Comments")} *`,
        type: "textarea",
        populators: {
          name: "comments",
          validation: {
            required: true,
          },
        },
      },
      {
        label: `${t("CS_ACTION_SUPPORTING_DOCUMENTS", "Supporting Document")}${action?.docUploadRequired ? " *" : ""}`,
        populators: (
          <UploadFile
            id="works-workflow-doc"
            onUpload={selectFile}
            onDelete={() => {
              setFile(null);
              setUploadedFile(null);
            }}
            message={
              uploadedFile
                ? `1 ${t("CS_ACTION_FILEUPLOADED")}`
                : t("CS_ACTION_NO_FILEUPLOADED")
            }
            error={error}
          />
        ),
      }
    );

    return {
      label: {
        heading: t(`WF_WORKS_${action?.action}`, action?.action),
        submit: t(`WF_WORKS_${action?.action}`, action?.action),
        cancel: t("CS_COMMON_BACK", "Cancel"),
      },
      form: [{ body }],
    };
  }, [action, approvers, selectedApprover, uploadedFile, error, t]);

  return action && config?.form && !approverLoading ? (
    <Modal
      headerBarMain={<Heading label={config.label.heading} />}
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      actionCancelLabel={config.label.cancel}
      actionCancelOnSubmit={closeModal}
      actionSaveLabel={config.label.submit}
      actionSaveOnSubmit={() => {}}
      formId="modal-action"
    >
      <FormComposer
        config={config.form}
        noBoxShadow
        inline
        childrenAtTheBottom
        onSubmit={submit}
        defaultValues={{}}
        formId="modal-action"
      />
    </Modal>
  ) : (
    <Loader />
  );
};

export default WORKSActionModal;

