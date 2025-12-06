/**
 * @file PetCaption.js
 * @description Displays pet-related details including date, name, contact, source, and comments.
 * 
 * @components
 * - `TelePhone`: Renders the mobile number as a clickable link.
 * - `Reason`: Displays comments with additional details.
 * 
 * @props
 * - `data`: Object containing pet details:
 *    - `date`: Application date.
 *    - `name`: Pet owner's name.
 *    - `mobileNumber`: Contact number.
 *    - `source`: Application source.
 *    - `comment`: Main comment.
 *    - `otherComment`: Additional comment.
 */

import React from "react";
import { useTranslation } from "react-i18next";
import { TelePhone } from "@upyog/digit-ui-react-components";
import Reason from "./Reason";

const PetCaption = ({ data }) => {
  const { t } = useTranslation();
  return (
    <div>
      {data.date && <p>{data.date}</p>}
      <p>{data.name}</p>
      {data.mobileNumber && <TelePhone mobile={data.mobileNumber} />}
      {data.source && <p>{t("ES_APPLICATION_DETAILS_APPLICATION_CHANNEL_" + data.source.toUpperCase())}</p>}
      {data.comment && <Reason otherComment={data?.otherComment} headComment={data?.comment}></Reason>}
    </div>
  );
};

export default PetCaption;
