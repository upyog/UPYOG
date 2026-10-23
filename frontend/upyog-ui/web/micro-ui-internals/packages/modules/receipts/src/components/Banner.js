import { CardLabel, LabelFieldPair } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";

const Banner = ({ t, config }) => {
    return (
        <LabelFieldPair>
            <CardLabel className="card-label-smaller rcpt-banner-card">.</CardLabel>
            <span className="form-field rcpt-banner-wrapper">{t(config?.texts?.header)}</span>
        </LabelFieldPair>)
}
export default Banner;