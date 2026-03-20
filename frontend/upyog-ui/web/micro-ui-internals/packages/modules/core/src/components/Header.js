import React from "react";
import { useTranslation } from "react-i18next";
import { Loader } from "@upyog/digit-ui-react-components"

const Header = () => {
  const { data: storeData, isLoading } = Digit.Hooks.useStore.getInitData();
  const { stateInfo } = storeData || {};
  const { t } = useTranslation()

  if (isLoading) return <Loader/>;

  return (
    <div className="bannerHeader" style={{paddingBottom: "10px", borderBottom: "1px solid #192771"}}>
      <img src="https://upload.wikimedia.org/wikipedia/commons/9/99/Seal_of_Uttarakhand.svg" style={{width: "47px", height: "47px", marginRight: "8px"}} />
      <h3 style={{ color: "#000000", fontSize: "20px", marginLeft: "10px" }}><strong>NagarSewa Portal</strong><br /><p style={{ fontSize: "14px" }}>Government of Uttarakhand</p></h3>
    </div>
  );
}

export default Header;