import React, { useState } from "react";
import { Route, useLocation, Routes } from "react-router-dom";
import { ActionBar, Menu, SubmitBar, BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
// import { ComplaintDetails } from "./ComplaintDetails";
// import { CreateComplaint } from "./CreateComplaint";
// import Inbox from "./Inbox";
import { Employee } from "../../constants/Routes";
// import Response from "./Response";

const Complaint = () => {
  const [displayMenu, setDisplayMenu] = useState(false);
  const [popup, setPopup] = useState(false);
  const match = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();

  const breadcrumConfig = {
    home: {
      content: t("CS_COMMON_HOME"),
      path: Employee.Home,
    },
    inbox: {
      content: t("CS_COMMON_INBOX"),
      path:  Employee.Inbox,
    },
    createComplaint: {
      content: t("CS_PGR_CREATE_COMPLAINT"),
      path:  Employee.CreateComplaint,
    },
    complaintDetails: {
      content: t("CS_PGR_COMPLAINT_DETAILS"),
      path:  Employee.ComplaintDetails + ":id",
    },
    response: {
      content: t("CS_PGR_RESPONSE"),
      path:  Employee.Response,
    },
    editApplication: {
      content: t("CS_PGR_EDIT_APPLICATION"),
      path:  Employee.EditApplication,
    },    
  };
  function popupCall(option) {
    setDisplayMenu(false);
    setPopup(true);
  }

  let location = useLocation().pathname;

  const CreateComplaint = Digit?.ComponentRegistryService?.getComponent('PGRCreateComplaintEmp');
  const ComplaintDetails = Digit?.ComponentRegistryService?.getComponent('PGRComplaintDetails');
  const Inbox = Digit?.ComponentRegistryService?.getComponent('PGRInbox');
  const Response = Digit?.ComponentRegistryService?.getComponent('PGRResponseEmp');
  const EditApplication = Digit.ComponentRegistryService.getComponent("PGREditApplication");
  return (
    <React.Fragment>
      <div className="ground-container">
        {!location.includes(Employee.Response) && (
          <Routes>
            <Route
              path={ Employee.CreateComplaint}
              element={<BreadCrumb crumbs={[breadcrumConfig.home, breadcrumConfig.createComplaint]}></BreadCrumb>}
            />
            <Route
              path={ Employee.ComplaintDetails + ":id"}
              element={<BreadCrumb crumbs={[breadcrumConfig.home, breadcrumConfig.inbox, breadcrumConfig.complaintDetails]}></BreadCrumb>}
            />
            <Route
              path={ Employee.Inbox}
              element={<BreadCrumb crumbs={[breadcrumConfig.home, breadcrumConfig.inbox]}></BreadCrumb>}
            />
            <Route
              path={ Employee.Response}
              element={<BreadCrumb crumbs={[breadcrumConfig.home, breadcrumConfig.response]}></BreadCrumb>}
            />
            <Route
              path={ Employee.EditApplication + ":id"}
              element={<BreadCrumb crumbs={[breadcrumConfig.home, breadcrumConfig.editApplication]}></BreadCrumb>}
            />
          </Routes>
        )}
        <Routes>
          <Route path={ Employee.CreateComplaint} element={<CreateComplaint parentUrl={match.url} />} />
          <Route path={ Employee.ComplaintDetails + ":id/*"} element={<ComplaintDetails />} />
          <Route path={ Employee.Inbox} element={<Inbox />} />
          <Route path={ Employee.Response} element={<Response />} />
          <Route path={ Employee.EditApplication + ":id/*"} element={<EditApplication />} />
        </Routes>
      </div>
      {/* <ActionBar>
        {displayMenu ? <Menu options={["Assign Complaint", "Reject Complaint"]} onSelect={popupCall} /> : null}
        <SubmitBar label="Take Action" onSubmit={() => setDisplayMenu(!displayMenu)} />
      </ActionBar> */}
    </React.Fragment>
  );
};

export default Complaint;
