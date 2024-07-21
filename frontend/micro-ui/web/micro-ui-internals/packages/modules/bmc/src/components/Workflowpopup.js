import { Loader, Modal } from '@egovernments/digit-ui-react-components';
import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
//import configEstimateModal from './config/configEstimateModal';
//import configEstimateModal from './config/configEstimateModal';
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

// Import your configuration function

const WorkflowPopup = ({ action, tenantId, t, closeModal, submitAction, businessService, moduleCode, applicationDetails }) => {
    console.log("WorkflowPopup props:", { action, tenantId, t, closeModal, submitAction, businessService, moduleCode, applicationDetails }); // Debug log

    const [config, setConfig] = useState(null);
    const [modalSubmit, setModalSubmit] = useState(true);
    const { register, handleSubmit, formState: { errors } } = useForm();

    useEffect(() => {
        console.log("useEffect triggered for businessService and action"); // Debug log
        // Dynamic configuration for the form based on action and business service
        const dynamicFields = [
            { label: "UUID", type: "text", name: "uuid", value: action?.uuid || "" },
            { label: "State", type: "text", name: "state", value: action?.state || "" },
            { label: "Application Status", type: "text", name: "applicationStatus", value: action?.applicationStatus || "" }
        ];

        if (action?.roles) {
            dynamicFields.push({
                label: "Roles", 
                type: "select", 
                name: "roles", 
                options: action.roles.map(role => ({ label: role, value: role }))
            });
        }

        setConfig({
            form: [
                {
                    label: { heading: `Action: ${action?.action}`, cancel: "Cancel", submit: "Submit" },
                    fields: dynamicFields
                }
            ]
        });
    }, [action, businessService]);

    const _submit = (data) => {
        console.log("Form submitted:", data); // Debug log
        // Placeholder for actual submission logic
        submitAction(data, action);
    };

    if (!config) {
        return <Loader />;
    }

    return (
        <Modal
            headerBarMain={<h1 className="heading-m">{t(config.form[0].label.heading)}</h1>}
            headerBarEnd={
                <div className="icon-bg-secondary" onClick={closeModal}>
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
                        <path d="M0 0h24v24H0V0z" fill="none" />
                        <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
                    </svg>
                </div>
            }
            actionCancelLabel={t(config.form[0].label.cancel)}
            actionCancelOnSubmit={closeModal}
            actionSaveLabel={t(config.form[0].label.submit)}
            actionSaveOnSubmit={handleSubmit(_submit)}
            formId="modal-action"
            isDisabled={!modalSubmit}
        >
            <form id="modal-action" onSubmit={handleSubmit(_submit)}>
                {config.form[0].fields.map((field, index) => (
                    <div key={index}>
                        <label>{field.label}</label>
                        {field.type === "select" ? (
                            <select {...register(field.name, { required: true })}>
                                {field.options.map((option, idx) => (
                                    <option key={idx} value={option.value}>{option.label}</option>
                                ))}
                            </select>
                        ) : (
                            <input type={field.type} {...register(field.name, { required: true })} defaultValue={field.value} />
                        )}
                        {errors[field.name] && <span>This field is required</span>}
                    </div>
                ))}
                <button type="submit">Submit</button>
            </form>
        </Modal>
    );
};

export default WorkflowPopup;
