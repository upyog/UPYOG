import React from "react";
import ESTDynamicCheckPage from "./ESTDynamicCheckPage";

/** @deprecated Use ESTDynamicCheckPage with flow="allotment" — kept for registry/MDMS compat. */
const ESTAssignAssetsCheckPage = (props) => (
  <ESTDynamicCheckPage flow="allotment" {...props} />
);

export default ESTAssignAssetsCheckPage;
