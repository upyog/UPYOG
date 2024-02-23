import React from "react";
import { Dialog, Button, Image } from "components";
import Label from "egov-ui-kit/utils/translationNode";
import "./index.css";

const styles = {
  logoutContentStyle: { textAlign: "center", padding: "24px 20px" },
};

const WelcomeMessage = ({ WCPopupClose, WCPopupOpen, title, body }) => {
  const actions = [
    <Button
      label={"test"}
      backgroundColor={"#fff"}
      style={{ boxShadow: "none", display:"none" }}
    />
  ];
  return (
    <Dialog
      open={WCPopupOpen}
      title={
        <Label
          label={title}
          bold={true}
          color="rgba(0, 0, 0, 0.8700000047683716)"
          fontSize="20px"
          labelStyle={{ padding: "16px 0px 0px 24px" }}
        />
      }
      children={[
        <Image className="whatsApp-Image" source={`${body}`} />
      ]}
      handleClose={WCPopupClose}
      actions={actions}
      contentClassName={"wc-popup"}
      contentStyle={{ width: "90%" }}
      isClose={true}
      isImage ={true}
    />
  );
};

export default WelcomeMessage;