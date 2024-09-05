import React from "react";
const ConfirmationBox = ({ t, title,styles={} }) => {
  return (
    <div className="confirmation_box" style={styles}>
      <span>{t(`${title}`)} </span>
    </div>
  );
};

export default ConfirmationBox;
