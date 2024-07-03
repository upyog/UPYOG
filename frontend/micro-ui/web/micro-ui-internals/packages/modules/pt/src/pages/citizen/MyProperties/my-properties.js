import { Card, KeyNote, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const MyProperty = ({ application }) => {
  const { t } = useTranslation();
  const address = application?.address;
  const owners = application?.owners;
  const [billData, setBillData]=useState(null);
  const [loading, setLoading]=useState(false);
  const fetchBillData=async()=>{
        setLoading(true);
        const result= await Digit.PaymentService.fetchBill(
          application.tenantId,{
          businessService: "PT",
          consumerCode: application.propertyId,
      });
      
      setBillData(result);
      setLoading(false);
  };
  useEffect(()=>{
    fetchBillData();
  }, [application.tenantId, application.propertyId]); 
  sessionStorage.removeItem("type" );
  sessionStorage.removeItem("pincode");
  sessionStorage.removeItem("tenantId");
  sessionStorage.removeItem("localityCode");
  sessionStorage.removeItem("landmark"); 
  sessionStorage.removeItem("propertyid")
  const ownersSequences=owners?.additionalDetails!==null ? owners.sort((a,b)=>a?.additionalDetails?.ownerSequence-b?.additionalDetails?.ownerSequence): owners;
  return (
    <Card>
      <KeyNote keyValue={t("PT_COMMON_TABLE_COL_PT_ID")} note={application.propertyId} />
      <KeyNote
        keyValue={t("PT_COMMON_TABLE_COL_OWNER_NAME")}
        note={ownersSequences.map((owners, index) => (
          <div key="index">{index == owners.length - 1 ? owners?.name + "," : owners.name}</div>
        ))}
      />
      <KeyNote
        keyValue={t("PT_COMMON_COL_ADDRESS")}
        note={
          `${t(address?.locality.name)}, ${t(address?.city)},${t(address?.pincode) ? `${address.pincode}` : " "}` || "CS_APPLICATION_TYPE_PT"
        }
      />
      <KeyNote keyValue={t("PT_COMMON_TABLE_COL_STATUS_LABEL")} note={t("PT_COMMON_" + application.status)} />
      <Link to={`/digit-ui/citizen/pt/property/properties/${application.propertyId}`}>
        <SubmitBar label={t("PT_VIEW_DETAILS")} />
      </Link>
      {billData?.Bill.length > 0  ? (
      <Link to={`/digit-ui/citizen/payment/my-bills/PT/${application?.propertyId}`}>
    
        <div style={{marginTop:"10px"}}><SubmitBar label={t("COMMON_MAKE_PAYMENT")}/></div>
      </Link>
      ):null}
      
    </Card>
  );
};

export default MyProperty;
