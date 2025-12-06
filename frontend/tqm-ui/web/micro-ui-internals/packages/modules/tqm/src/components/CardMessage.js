import { Card } from "@egovernments/digit-ui-react-components";
import React from "react";

function CardMessage(props) {
  return (
    <Card className={`card-tqm-message ${props?.success ? "success" : "error"}`}>
      <header>{props?.title}</header>
      <p>{props?.message}</p>
    </Card>
  );
}

export default CardMessage;
