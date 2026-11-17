import React from "react";
import Banner from "./components/Banner";
import ActionModal from "./components/Modal";
import ReceiptsFilter from "./components/ReceiptsFilter";
import EmployeeApp from "./pages";
import ReceiptAcknowledgement from "./pages/ReceiptAcknowledgement";
import ReceiptDetails from "./pages/ReceiptDetails";
import ReceiptInbox from "./pages/ReceiptInbox";
import ReceiptsCard from "./receiptHomeCard";

export const ReceiptsModule = ({ stateCode, userType }) => {
  const moduleCode = "RECEIPTS";
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
  const { path, url } = Digit.Hooks.useModuleBasePath();
  if (userType === "employee") {
    return <EmployeeApp path={path} url={url} userType={"employee"} />;
  } else return null;
};

const componentsToRegister = {
  ReceiptsModule,
  ReceiptsCard,
  ReceiptInbox: ReceiptInbox,
  ReceiptAcknowledgement: ReceiptAcknowledgement,
  ReceiptDetails: ReceiptDetails,
  ActionModal,
  Banner,
  RECEIPTS_INBOX_FILTER: (props) => <ReceiptsFilter {...props} />,
};

export const initReceiptsComponents = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};
