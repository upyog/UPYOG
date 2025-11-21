import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch } from "react-router-dom";
import getAssetAcknowledgementData from "../../../../getAssetAcknowledgementData";

import { InventoryCreationData } from "../../../../utils";

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("ES_INVENTORY_RESPONSE_CREATE_ACTION") : t("CS_INVENTORY_UPDATE_APPLICATION_SUCCESS");
  } else if (props.isLoading) {
    return !window.location.href.includes("edit-application") ? t("CS_INVENTORY_APPLICATION_PENDING") : t("CS_INVENTORY_UPDATE_APPLICATION_PENDING");
  } else if (!props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("CS_INVENTORY_APPLICATION_FAILED") : t("CS_INVENTORY_UPDATE_APPLICATION_FAILED");
  }
};

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props)}
      applicationNumber={props.data?.Inventories?.[0]?.inventoryId || props.data?.Inventory?.[0]?.inventoryId}
      info={props.isSuccess ? props.t("ES_INVENTORY_RESPONSE_CREATE_LABEL") : ""}
      successful={props.isSuccess}
      style={{ width: "100%" }}
    />
  );
};

const InventoryResponsePage = ({ data, onSuccess }) => {
  
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.asset.useInventoryCreationAPI(tenantId);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const match = useRouteMatch();
  const { tenants } = storeData || {};

  useEffect(() => {
    try {
      let payLoadInventoryCreation = InventoryCreationData(data);
      mutation.mutate(payLoadInventoryCreation, {
        onSuccess,
      });
    } catch (err) {}
  }, []);

  const handleDownloadPdf = async () => {
    const { Inventories = [], Inventory = [] } = mutation.data;
    let INV = (Inventories && Inventories[0]) || (Inventory && Inventory[0]) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === INV.tenantId);
    let inventoryTenantId = INV.tenantId || tenantId;

    // Use inventory-specific acknowledgement data function when available
    // const data = await getInventoryAcknowledgementData({ ...INV }, tenantInfo, t);
    // Digit.Utils.pdf.generate(data);
  };

  return mutation.isLoading || mutation.isIdle ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={mutation.data} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />
      <StatusTable>
        {mutation.isSuccess && <Row rowContainerStyle={rowContainerStyle} last textStyle={{ whiteSpace: "pre", width: "60%" }} />}
      </StatusTable>
      {/* {mutation.isSuccess && <SubmitBar label={t("INVENTORY_REPORT")} onSubmit={handleDownloadPdf} />} */}
      <Link to={`/upyog-ui/employee`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default InventoryResponsePage;
