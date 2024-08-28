import React, { useEffect, useState } from "react";

const CommonRedirect = () => {
  const [receipt, setReceipt] = useState("");
  const tenantId = Digit.ULBService.getCurrentTenantId();

  useEffect(() => {
    const fetchReceipt = async () => {
      if (window.location.href.includes("filestore")) {
        const filestoreId = window.location.href.split("filestore=")[1];
        const receiptFile = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: filestoreId });
        setReceipt(receiptFile);
      }
    };

    fetchReceipt();
  }, [tenantId]);

  useEffect(() => {
    const openReceipt = () => {
      const filestoreId = window.location.href.split("filestore=")[1];
      if (receipt?.[filestoreId]) {
        window.open(receipt[filestoreId], "_blank");
      }
    };

    openReceipt();
  }, [receipt]);

  return <React.Fragment>E Signed PDF Downloaded</React.Fragment>;
};

export default CommonRedirect;
