import React, { useState } from "react";
import {
  Header,
  Card,
  CardLabel,
  TextInput,
  Dropdown,
  UploadFile,
  SubmitBar,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

// Manage Rebate Component
// This component allows users to manage rebates by specifying amount type, amount, applicable ULBs, comments, and uploading relevant documents.

const ManageRebate = ({ t: propT }) => {
  const { t: hookT } = useTranslation();
  const t = propT || hookT;
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const [amountType, setAmountType] = useState("");
  const [amount, setAmount] = useState("");
  const [appliedTo, setAppliedTo] = useState("");
  const [comments, setComments] = useState("");
  const [document, setDocument] = useState(null);

  const amountTypeOptions = [
    { code: "PERCENTAGE", name: "%", i18nKey: "%" },
    { code: "RUPEES", name: "Rs.", i18nKey: "Rs." }
  ];

  const appliedToOptions = [
    { code: "ALL", name: "All ULBs", i18nKey: "All ULBs" },
    { code: "ULB 1", name: "ULB 1", i18nKey: "ULB 1" },
    { code: "ULB 2", name: "ULB 2", i18nKey: "ULB 2" },
     { code: "ULB 3", name: "ULB 3", i18nKey: "ULB 3" },
    { code: "ULB 4", name: "ULB 4", i18nKey: "ULB 4" }
  ];

  const handleFileUpload = async (file) => {
    if (!file) return;
    try {
      const response = await Digit.UploadServices.Filestorage("ESTATE", file, tenantId);
      const id = response?.data?.files?.[0]?.fileStoreId;
      if (id) {
        setDocument({ filestoreId: id, documentType: "REBATE_DOCUMENT" });
      }
    } catch (error) {
      console.error("File upload error:", error);
    }
  };

  const handleFileDelete = () => setDocument(null);

  const handleSubmit = () => {
    const payload = {
      amountType,
      amount: Number(amount),
      appliedTo,
      comments,
      document,
    };
    console.warn("Rebate API not yet integrated. Payload:", payload);
  };

  const fullWidthStyle = { width: "70%", marginBottom: "16px" };

  return (
    <div>
      <Header>Manage Rebate</Header>
      <Card style={{ padding: "16px" }}>
        <h3 style={{ marginBottom: "16px" }}>Rebate Amount</h3>
        
        <div style={{ display: "flex", gap: "16px", marginBottom: "16px" }}>
          {amountTypeOptions.map((option) => (
            <label key={option.code} style={{ display: "flex", alignItems: "center", cursor: "pointer" }}>
              <input
                type="radio"
                name="amountType"
                value={option.code}
                checked={amountType === option.code}
                onChange={() => setAmountType(option.code)}
                style={{ marginRight: "8px" }}
              />
              {option.name}
            </label>
          ))}
        </div>

        <TextInput
          placeholder="Enter Value"
          value={amount}
          onChange={(e) => setAmount(e.target.value.replace(/[^0-9.]/g, ""))}
          style={fullWidthStyle}
        />

        <CardLabel>Applied to</CardLabel>
        <Dropdown
          option={appliedToOptions}
          optionKey="i18nKey"
          selected={appliedToOptions.find(opt => opt.code === appliedTo) || null}
          select={(opt) => setAppliedTo(opt.code)}
          placeholder="Select ULB"
          t={t}
          style={fullWidthStyle}
        />

        <CardLabel>Comments</CardLabel>
        <TextInput
          placeholder="Enter comments"
          value={comments}
          onChange={(e) => setComments(e.target.value)}
          style={fullWidthStyle}
        />

        <CardLabel>{t("EST_REBATE_DOCUMENT")}</CardLabel>
<div style={fullWidthStyle}>
  <UploadFile
    onUpload={(e) => handleFileUpload(e.target.files[0])}
    onDelete={handleFileDelete}
    id="rebateDocument"
    message={document ? t("CS_ACTION_FILEUPLOADED") : t("CS_ACTION_NO_FILEUPLOADED")}
    accept=".png,.jpg,.jpeg,.pdf"
  />
</div>


        <SubmitBar
          label="Submit"
          onSubmit={handleSubmit}
          disabled={!amountType || !amount || !appliedTo}
        />
      </Card>
    </div>
  );
};

export default ManageRebate;
