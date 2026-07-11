import React from "react";
import ESTDynamicCheckPage from "./ESTDynamicCheckPage";

/** @deprecated Use ESTDynamicCheckPage with flow="registration" — kept for registry/MDMS compat. */
const ESTRegCheckPage = (props) => (
  <ESTDynamicCheckPage flow="registration" {...props} />
);

export default ESTRegCheckPage;
