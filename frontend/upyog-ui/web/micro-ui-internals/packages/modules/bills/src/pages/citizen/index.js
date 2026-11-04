import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Routes } from "react-router-dom";
import Inbox from "../../pages/citizen/SearchBill/Inbox";
import { useTranslation } from "react-i18next";

const App = () => {
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const inboxInitialState = {
    searchParams: {},
  };
  return (
    <span className={"bill-citizen"}>
      <AppContainer>
        <BackButton>Back</BackButton>
        <Routes>
          <Route
            path={`/billSearch`}
            element={
              <PrivateRoute>
                <Inbox filterComponent="CITIZEN_SEARCH_FILTER" initialStates={inboxInitialState} isInbox={true} />
              </PrivateRoute>
            }
          />
        </Routes>
      </AppContainer>
    </span>
  );
};
export default App;
