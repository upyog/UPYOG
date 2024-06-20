// import {
//     Card,
//     CardHeader,
//     CardSubHeader,
//     CardText,
//     CheckBox,
//     LinkButton,
//     Row,
//     StatusTable,
//     SubmitBar
//   } from "@egovernments/digit-ui-react-components";
import {
  Card,
  CheckBox,
  CardHeader,
  CardSubHeader,
  CardText,
  CitizenInfoLabel,
  Header,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar,
  Table,
  CardSectionHeader,
  EditIcon,
  PDFSvg,
  Loader,
} from "@egovernments/digit-ui-react-components";
import React, { useState, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
//   import {
//     checkForNA,
//     getFixedFilename, isPropertyIndependent, isPropertyselfoccupied,
//     ispropertyunoccupied
//   } from "../../../utils";

import Timeline from "../../../../components/RAFB/Timeline";

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();
  function routeTo() {
    history.push(jumpTo);
  }

  return <LinkButton label={t("CS_COMMON_CHANGE")} className="check-page-link-button" style={{ display: "inline-block" }} onClick={routeTo} />;
};

const CheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const history = useHistory();

  console.log("check page ", value);
  const { ProjectInfo, RABillTaxDetail, TenderWorkDetail, mbNotPaid, previous_bill: datafromAPI, withheldDeductionsDetail } = value;
  console.log(
    "ProjectInfo,RABillTaxDetail,TenderWorkDetail,mbNotPaid,previous_bill,withheldDeductionsDetail ",
    ProjectInfo,
    RABillTaxDetail,
    TenderWorkDetail,
    mbNotPaid,
    datafromAPI[0],
    withheldDeductionsDetail
  );

  //Previous Running Bill Information table data start
  let PreviousRunningBillInfo_user = Digit.UserService.getUser();
  let PreviousRunningBillInfo_BusinessService;
  if (value.PreviousRunningBillInfo_BusinessService === "BPA_LOW") PreviousRunningBillInfo_BusinessService = "BPA.LOW_RISK_PERMIT_FEE";
  else if (value.PreviousRunningBillInfo_BusinessService === "BPA") PreviousRunningBillInfo_BusinessService = "BPA.NC_APP_FEE";
  else if (value.PreviousRunningBillInfo_BusinessService === "BPA_OC") PreviousRunningBillInfo_BusinessService = "BPA.NC_OC_APP_FEE";

  const PreviousRunningBillInfo_tenantId =
    PreviousRunningBillInfo_user?.info?.permanentCity || value?.tenantId || Digit.ULBService.getCurrentTenantId();

  // const { previous_bill: datafromAPI, isLoading, refetch } = Digit.Hooks.obps.useScrutinyDetails(tenantId, value?.data?.scrutinyNumber, {
  //     enabled: value?.data?.scrutinyNumber?true:false,
  //   })
  console.log("datafromAPI ", datafromAPI);

  const PreviousRunningBillInfo_TableHeader = [
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_FINAL_RA_BILL_DATE",
      id: "raBillDate",
    },
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_FINAL_RA_BILL_NO",
      id: "raBillno",
    },

    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_FINAL_RA_BILL_AMOUNT",
      id: "raBillAmount",
    },
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_FINAL_TAX_AMOUNT",
      id: "finalTaxAmount",
    },
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_WITHHELD_REMARK",
      id: "withheldRemark",
    },
  ];
  const PreviousRunningBillInfo_accessData = (plot) => {
    const name = plot;
    return (originalRow, rowIndex, columns) => {
      return originalRow[name];
    };
  };

  const PreviousRunningBillInfo_TableColumns = useMemo(() => {
    return PreviousRunningBillInfo_TableHeader.map((ob) => ({
      Header: t(`${ob.name}`),
      accessor: PreviousRunningBillInfo_accessData(ob.id),
      id: ob.id,
      //symbol: plot?.symbol,
      //sortType: sortRows,
    }));
  });

  function PreviousRunningBillInforData(block) {
    let PreviousRunningBillData = [];
    block?.map((wmsRaBill) => {
      PreviousRunningBillData.push({
        raBillDate: wmsRaBill?.billDate,
        raBillno: wmsRaBill?.bilNo,
        raBillAmount: wmsRaBill?.billAmount,
        finalTaxAmount: wmsRaBill?.deductionAmount,
        withheldRemark: wmsRaBill?.remark,
        key: t(`WMSPrevious_BILL_NAME_${wmsRaBill.bilNo}`),
      });
    });
    return PreviousRunningBillData;
  }
  //Previous Running Bill Information table data end

  //Withheld Deductions Details start
  let WithheldDeductionsDetails_user = Digit.UserService.getUser();
  let WithheldDeductionsDetails_BusinessService;
  if (value.WithheldDeductionsDetails_BusinessService === "BPA_LOW") WithheldDeductionsDetails_BusinessService = "BPA.LOW_RISK_PERMIT_FEE";
  else if (value.WithheldDeductionsDetails_BusinessService === "BPA") WithheldDeductionsDetails_BusinessService = "BPA.NC_APP_FEE";
  else if (value.WithheldDeductionsDetails_BusinessService === "BPA_OC") WithheldDeductionsDetails_BusinessService = "BPA.NC_OC_APP_FEE";

  const WithheldDeductionsDetails_tenantId =
    WithheldDeductionsDetails_user?.info?.permanentCity || value?.tenantId || Digit.ULBService.getCurrentTenantId();

  // const { previous_bill: datafromAPI, isLoading, refetch } = Digit.Hooks.obps.useScrutinyDetails(tenantId, value?.data?.scrutinyNumber, {
  //     enabled: value?.data?.scrutinyNumber?true:false,
  //   })
  console.log("datafromAPI ", datafromAPI);

  const WithheldDeductionsDetails_TableHeader = [
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_WITHHELD_TAX_CATEGORY",
      id: "withheldTaxcategory",
    },
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_WITHHELD_REMARK",
      id: "withheldAmount",
    },
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_WITHHELD_AMOUNT",
      id: "withheldRemark",
    },
  ];
  const WithheldDeductionsDetails_accessData = (plot) => {
    const name = plot;
    return (originalRow, rowIndex, columns) => {
      return originalRow[name];
    };
  };

  const WithheldDeductionsDetails_TableColumns = useMemo(() => {
    return WithheldDeductionsDetails_TableHeader.map((ob) => ({
      Header: t(`${ob.name}`),
      accessor: WithheldDeductionsDetails_accessData(ob.id),
      id: ob.id,
      //symbol: plot?.symbol,
      //sortType: sortRows,
    }));
  });

  function WithheldDeductionsDetailsData(block) {
    let WithheldData = [];
    block?.map((data) => {
      WithheldData.push({
        withheldTaxcategory: data?.taxcategory?.i18nKey,
        withheldAmount: data?.amount,
        withheldRemark: data?.remark,
        // finalTaxAmount: data?.deductionAmount,
        // withheldRemark: data?.remark,
        key: t(`Withheld_KEY${data.amount}`),
      });
    });
    return WithheldData;
  }
  //Withheld Deductions Details end

  //RA Bill Tax Details table data start
  let RABillTaxDetails_user = Digit.UserService.getUser();
  let RABillTaxDetails_BusinessService;
  if (value.RABillTaxDetails_businessService === "BPA_LOW") RABillTaxDetails_BusinessService = "BPA.LOW_RISK_PERMIT_FEE";
  else if (value.RABillTaxDetails_businessService === "BPA") RABillTaxDetails_BusinessService = "BPA.NC_APP_FEE";
  else if (value.RABillTaxDetails_businessService === "BPA_OC") RABillTaxDetails_BusinessService = "BPA.NC_OC_APP_FEE";

  const RABillTaxDetails_tenantId = RABillTaxDetails_user?.info?.permanentCity || value?.tenantId || Digit.ULBService.getCurrentTenantId();

  // const { previous_bill: datafromAPI, isLoading, refetch } = Digit.Hooks.obps.useScrutinyDetails(tenantId, value?.data?.scrutinyNumber, {
  //     enabled: value?.data?.scrutinyNumber?true:false,
  //   })
  console.log("datafromAPI ", datafromAPI);

  const RABillTaxDetails_TableHeader = [
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_RABILL_TOTAL",
      id: "total",
    },
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_RABILL_TAX_CATEGORY",
      id: "taxcategory",
    },
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_RABILL_ADDITION_DEDUCTION",
      id: "addition_deduction",
    },
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_RABILL_AMOUNT_PERCENTAGE",
      id: "amount_percentage",
    },
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_RABILL_PERCENTAGE_VALUE",
      id: "percentageValue",
    },
    {
      name: "WMS_RUNNING_ACCOUNT_FINAL_BILL_WITHHELD_AMOUNT",
      id: "amount",
    },
  ];
  const RABillTaxDetails_accessData = (plot) => {
    const name = plot;
    return (originalRow, rowIndex, columns) => {
      return originalRow[name];
    };
  };

  const RABillTaxDetails_TableColumns = useMemo(() => {
    return RABillTaxDetails_TableHeader.map((ob) => ({
      Header: t(`${ob.name}`),
      accessor: RABillTaxDetails_accessData(ob.id),
      id: ob.id,
      //symbol: plot?.symbol,
      //sortType: sortRows,
    }));
  });

  function getRABillsTaxDetailsData(block) {
    let RABillsData = [];
    block?.map((wmsRaBill) => {
      console.log("obssss RAFB ", wmsRaBill);
      RABillsData.push({
        taxcategory: wmsRaBill.taxcategory?.i18nKey_0,
        addition_deduction: wmsRaBill.addition_deduction?.i18nKey_1,
        amount_percentage: wmsRaBill.amount_percentage?.i18nKey_2,
        percentageValue: wmsRaBill.percentageValue,
        amount: wmsRaBill.amount,
        total: wmsRaBill.total || 0,
        key: t(`WMSRaBill_BILL_NAME_${wmsRaBill.number}`),
      });
    });
    return RABillsData;
  }
  //RA Bill Tax Details table data end

  
  // const {
  //   address,
  //   isResdential,
  //   PropertyType,
  //   noOfFloors,
  //   noOofBasements,
  //   units = [{}],
  //   landarea,
  //   landArea,
  //   UnOccupiedArea,
  //   city_complaint,
  //   locality_complaint,
  //   street,
  //   doorNo,
  //   landmark,
  //   ownerType,
  //   Floorno,
  //   ownershipCategory,
  //   Constructiondetails,
  //   IsAnyPartOfThisFloorUnOccupied,
  //   propertyArea,
  //   selfOccupied,
  //   floordetails,
  //   owners,
  //   isEditProperty,
  //   isUpdateProperty,
  // } = value;

  // const typeOfApplication = !isEditProperty && !isUpdateProperty ? `new-application` : `edit-application`;
  // let flatplotsize;
  // if (isPropertyselfoccupied(selfOccupied?.i18nKey)) {
  //   flatplotsize = parseInt(landarea?.floorarea);
  //   if (ispropertyunoccupied(IsAnyPartOfThisFloorUnOccupied?.i18nKey)) {
  //     flatplotsize = flatplotsize + parseInt(UnOccupiedArea?.UnOccupiedArea);
  //   }
  // } else {
  //   flatplotsize = parseInt(landarea?.floorarea) + parseInt(Constructiondetails?.RentArea);
  //   if (!ispropertyunoccupied(IsAnyPartOfThisFloorUnOccupied?.i18nKey)) {
  //     flatplotsize = flatplotsize + parseInt(UnOccupiedArea?.UnOccupiedArea);
  //   }
  // }
  // if (isPropertyIndependent(PropertyType?.i18nKey)) {
  //   flatplotsize = parseInt(propertyArea?.builtUpArea) + parseInt(propertyArea?.plotSize);
  // }

  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  alert("RAFB"+!agree)
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={7} /> : null}
      <Card>
        <CardHeader>{t("WMS_RUNNING_ACCOUNT_FINAL_BILL_CHECK_CHECK_YOUR_ANSWERS")}</CardHeader>
        <div>
          <CardText>{t("WMS_RUNNING_ACCOUNT_FINAL_BILL_CHECK_CHECK_YOUR_ANSWERS_TEXT")}</CardText>
          <CardSubHeader>{t("WMS_RUNNING_ACCOUNT_FINAL_BILL_PROJECT_INFORMATION")}</CardSubHeader>
          <StatusTable>
            {/* <Row
              label={t("PT_PROPERTY_ADDRESS_SUB_HEADER")}
              text={`${ProjectInfo?.ProjectName ? `${ProjectInfo?.ProjectName}, ` : "Road Construction, "} ${ProjectInfo?.WorkName ? `${ProjectInfo?.WorkName}, ` : ""}${
                ProjectInfo?.WorkOrderNo ? `${ProjectInfo?.WorkOrderNo}, ` : ""
              }`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/add/${typeOfApplication}/pincode`} />}
            /> */}
            <Row
              label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_PROJECT_NAME")}
              text={`${ProjectInfo?.ProjectName ? `${ProjectInfo?.ProjectName}` : ""}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/ProjectName`} />}
            />
            <Row
              label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_WORK_NAME")}
              text={` ${ProjectInfo?.WorkName ? `${ProjectInfo?.WorkName}` : ""}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/work-name`} />}
            />
            <Row
              label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_WORK_ORDER_NO")}
              text={`${ProjectInfo?.WorkOrderNo ? `${ProjectInfo?.WorkOrderNo}` : ""}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/work-order-no`} />}
            />
          </StatusTable>

          <div>
            <CardSubHeader>{t("WMS_RUNNING_ACCOUNT_FINAL_BILL_PREVIOUS_RUNNING_BILL_INFORMATION")}</CardSubHeader>
            <div style={{ textAlign: "right" }}>
              <ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/previous-running-bill`} />
            </div>
            <div style={{ overflow: "scroll" }}>
              <Table
                className="customTable table-fixed-first-column table-border-style"
                t={t}
                disableSort={false}
                autoSort={false}
                manualPagination={false}
                isPaginationRequired={false}
                initSortId="S N "
                data={PreviousRunningBillInforData(datafromAPI[0])}
                columns={PreviousRunningBillInfo_TableColumns}
                getCellProps={(cellInfo) => {
                  return {
                    style: {},
                  };
                }}
              />
            </div>
          </div>

          <div>
            <CardSubHeader>{t("WMS_RUNNING_ACCOUNT_MB_NOT_PAID")}</CardSubHeader>
            <StatusTable>
              <Row
                label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_MB_SELECT")}
                text={`${mbNotPaid?.i18nKey ? `${mbNotPaid?.i18nKey}` : ""}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/select-measurement-book`} />}
              />
              <Row
                label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_MB_DATE")}
                text={` ${mbNotPaid?.mbDate ? `${mbNotPaid?.mbDate}` : ""}`}
                // actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/measurement-book-date`} />}
              />
              <Row
                label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_MB_NO")}
                text={`${mbNotPaid?.mbNumber ? `${mbNotPaid?.mbNumber}` : ""}`}
                // actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/measurement-book-no`} />}
              />
              <Row
                label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_MB_AMMOUNT")}
                text={`${mbNotPaid?.amount ? `${mbNotPaid?.amount}` : ""}`}
                // actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/measurement-book-amount`} />}
              />
            </StatusTable>
          </div>

          <div>
          <CardSubHeader>{t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_WORK_DETAILS")}</CardSubHeader>
          <StatusTable>
            <Row
              label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_WORK_NAME")}
              text={`${TenderWorkDetail?.workName ? `${TenderWorkDetail?.workName}` : ""}`}
              // actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/select-measurement-book`} />}
            />
            <Row
              label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_ESTIMATED_WORK_COST")}
              text={` ${TenderWorkDetail?.estimatedWorkCost ? `${TenderWorkDetail?.estimatedWorkCost}` : ""}`}
              // actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/measurement-book-date`} />}
            />
            <Row
              label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_TENDER_TYPE")}
              text={`${TenderWorkDetail?.tenderType ? `${TenderWorkDetail?.tenderType}` : ""}`}
              // actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/measurement-book-no`} />}
            />
            <Row
              label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_PERCENTAGE_TYPE")}
              text={`${TenderWorkDetail?.percentageType ? `${TenderWorkDetail?.percentageType}` : ""}`}
              // actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/measurement-book-amount`} />}
            />
            <Row
              label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_AMOUNT")}
              text={`${TenderWorkDetail?.amount ? `${TenderWorkDetail?.amount}` : ""}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/measurement-book-amount`} />}
            />
          </StatusTable>
          </div>

          <div>
            <CardSubHeader>{t("WMS_RUNNING_ACCOUNT_FINAL_BILL_WITHHELD_DEDUCTIONS_DETAILS")}</CardSubHeader>
            <div style={{ textAlign: "right" }}>
              <ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/withheld-deductions-details`} />
            </div>
            <div style={{ overflow: "scroll" }}>
              <Table
                className="customTable table-fixed-first-column table-border-style"
                t={t}
                disableSort={false}
                autoSort={true}
                manualPagination={false}
                isPaginationRequired={false}
                initSortId="S N "
                data={WithheldDeductionsDetailsData(withheldDeductionsDetail?.withheldDeductionsDetail)}
                columns={WithheldDeductionsDetails_TableColumns}
                getCellProps={(cellInfo) => {
                  return {
                    style: {},
                  };
                }}
              />
            </div>
          </div>

          <div>
            <CardSubHeader>{t("WMS_RUNNING_ACCOUNT_FINAL_BILL_RA_BILL_TAX_DETAILS")}</CardSubHeader>
            <div style={{ textAlign: "right" }}>
              <ActionButton jumpTo={`/upyog-ui/citizen/wms/running-account/${"edit"}/ra-bills-tax-details`} />
            </div>
            <div style={{ overflow: "scroll" }}>
              <Table
                className="customTable table-fixed-first-column table-border-style"
                t={t}
                disableSort={false}
                autoSort={true}
                manualPagination={false}
                isPaginationRequired={false}
                initSortId="S N "
                data={getRABillsTaxDetailsData(RABillTaxDetail?.RABillTaxDetail)}
                columns={RABillTaxDetails_TableColumns}
                getCellProps={(cellInfo) => {
                  return {
                    style: {},
                  };
                }}
              />
            </div>
          </div>

          {/* Loop start */}
          {/* <div>
            {owners &&
              owners.map &&
              owners.map((owner, index) => (
                <div key={index}>
                  {owners.length != 1 && (
                    <CardSubHeader>
                      {t("PT_OWNER_SUB_HEADER")} - {index + 1}
                    </CardSubHeader>
                  )}
                  {ownershipCategory?.value == "INSTITUTIONALPRIVATE" || ownershipCategory?.value == "INSTITUTIONALGOVERNMENT" ? (
                    <div>
                      <StatusTable>
                        <Row
                          label={t("PT_COMMON_INSTITUTION_NAME")}
                          text={`${t(checkForNA(owner?.inistitutionName))}`}
                          actionButton={
                            <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/inistitution-details/`}${index}`} />
                          }
                        />
                        <Row
                          label={t("PT_TYPE_OF_INSTITUTION")}
                          text={`${t(checkForNA(owner?.inistitutetype?.code))}`}
                          actionButton={
                            <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/inistitution-details/`}${index}`} />
                          }
                        />
                        <Row
                          label={t("PT_OWNER_NAME")}
                          text={`${t(checkForNA(owner?.name))}`}
                          actionButton={
                            <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/inistitution-details/`}${index}`} />
                          }
                        />
                        <Row
                          label={`${t("PT_COMMON_AUTHORISED_PERSON_DESIGNATION")}`}
                          text={`${t(checkForNA(owner?.designation))}`}
                          actionButton={
                            <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/inistitution-details/`}${index}`} />
                          }
                        />
                        <Row
                          label={`${t("PT_FORM3_MOBILE_NUMBER")}`}
                          text={`${t(checkForNA(owner?.mobileNumber))}`}
                          actionButton={
                            <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/inistitution-details/`}${index}`} />
                          }
                        />
                        <Row
                          label={`${t("PT_OWNERSHIP_INFO_TEL_PHONE_NO")}`}
                          text={`${t(checkForNA(owner?.altContactNumber))}`}
                          actionButton={
                            <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/inistitution-details/`}${index}`} />
                          }
                        />
                        <Row
                          label={`${t("PT_FORM3_EMAIL_ID")}`}
                          text={`${t(checkForNA(owner?.emailId))}`}
                          actionButton={
                            <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/inistitution-details/`}${index}`} />
                          }
                        />
                        <Row
                          label={`${t("PT_OWNERSHIP_INFO_CORR_ADDR")}`}
                          text={`${t(checkForNA(owner?.permanentAddress))}`}
                          actionButton={
                            <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/institutional-owner-address/`}${index}`} />
                          }
                        />
                        <Row
                          label={`${t("PT_COMMON_SAME_AS_PROPERTY_ADDRESS")}`}
                          text={`${t(checkForNA(owner?.isCorrespondenceAddress))}`}
                          actionButton={
                            <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/institutional-owner-address/`}${index}`} />
                          }
                        />
                        <Row
                          label={t("PT_PROOF_IDENTITY_HEADER")}
                          text={`${(owner?.documents["proofIdentity"]?.name && getFixedFilename(owner.documents["proofIdentity"].name)) || "na"}`}
                          actionButton={
                            <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/institutional-proof-of-identity/`}${index}`} />
                          }
                        />
                      </StatusTable>
                    </div>
                  ) : (
                    <div>
                      <StatusTable>
                        <Row
                          label={t("PT_OWNER_NAME")}
                          text={`${t(checkForNA(owner?.name))}`}
                          actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/owner-details/`}${index}`} />}
                        />
                        <Row
                          label={t("PT_FORM3_GENDER")}
                          text={`${t(checkForNA(owner?.gender?.code))}`}
                          actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/owner-details/`}${index}`} />}
                        />
                        <Row
                          label={`${t("PT_FORM3_MOBILE_NUMBER")}`}
                          text={`${t(checkForNA(owner?.mobileNumber))}`}
                          actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/owner-details/`}${index}`} />}
                        />
                        <Row
                          label={t("PT_FORM3_GUARDIAN_NAME")}
                          text={`${t(checkForNA(owner?.fatherOrHusbandName))}`}
                          actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/owner-details/`}${index}`} />}
                        />
                        <Row
                          label={t("PT_FORM3_RELATIONSHIP")}
                          text={`${t(checkForNA(owner?.relationship?.code))}`}
                          actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/owner-details/`}${index}`} />}
                        />
                        <Row
                          label={t("PT_SPECIAL_OWNER_CATEGORY")}
                          text={`${t(checkForNA(owner?.ownerType?.code))}`}
                          actionButton={
                            <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/special-owner-category/`}${index}`} />
                          }
                        />
                        <Row
                          label={`${t("PT_OWNERS_ADDRESS")}`}
                          text={`${t(checkForNA(owner?.permanentAddress))}`}
                          actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/owner-address/`}${index}`} />}
                        />
                        <Row
                          label={`${t("PT_COMMON_SAME_AS_PROPERTY_ADDRESS")}`}
                          text={`${t(checkForNA(owner?.isCorrespondenceAddress))}`}
                          actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/owner-address/`}${index}`} />}
                        />
                        {owner?.ownerType?.code !== "NONE" ? (
                          <Row
                            label={t("PT_SPECIAL_OWNER_CATEGORY_PROOF_HEADER")}
                            text={`${
                              (owner?.documents["specialProofIdentity"]?.name && getFixedFilename(owner.documents["specialProofIdentity"].name)) || "na"
                            }`}
                            actionButton={
                              <ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/special-owner-category-proof/`}${index}`} />
                            }
                          />
                        ) : (
                          ""
                        )}
                        <Row
                          label={t("PT_PROOF_IDENTITY_HEADER")}
                          text={`${(owner?.documents["proofIdentity"]?.name && getFixedFilename(owner.documents["proofIdentity"].name)) || "na"}`}
                          actionButton={<ActionButton jumpTo={`${`/upyog-ui/citizen/pt/property/${typeOfApplication}/proof-of-identity/`}${index}`} />}
                        />
                      </StatusTable>
                    </div>
                  )}
                </div>
              ))}
          </div> */}
          {/* Loop end */}

          {/* Loop with sort method start */}
          {/* <CardSubHeader>{t("PT_ASSESMENT_INFO_SUB_HEADER")}</CardSubHeader>
          <StatusTable>
            <Row
              label={t("PT_ASSESMENT1_PROPERTY_TYPE")}
              text={`${t(checkForNA(PropertyType?.i18nKey))}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/pt/property/${typeOfApplication}/property-type`} />}
            />
            {PropertyType?.code !== "VACANT" &&<Row
              label={t("PT_ASSESMENT1_PLOT_SIZE")}
              text={`${landArea?.floorarea}`}
              actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/pt/property/${typeOfApplication}/landarea`} />}
            />}
            {PropertyType?.code === "VACANT" && (
              <Row
                label={t("PT_ASSESMENT1_PLOT_SIZE")}
                text={`${landarea?.floorarea}`}
                actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/pt/property/${typeOfApplication}/PtUnits`} />}
              />
            )}
            {PropertyType?.code !== "VACANT" &&
              units
                .sort((x, y) => x.floorNo - y.floorNo)
                .map((unit, unitIndex) => {
                  return (
                    <div>
                      {units.length > 1 && <CardSubHeader>{t(`PT_UNIT`)}-{unitIndex}</CardSubHeader>}
                      <Row
                        label={t("PT_BUILT_UP_AREA")}
                        text={`${unit?.constructionDetail?.builtUpArea}`}
                        actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/pt/property/${typeOfApplication}/PtUnits`} />}
                      />
                      <Row
                        label={t("PT_ASSESMENT_INFO_OCCUPLANCY")}
                        text={t(`PROPERTYTAX_OCCUPANCYTYPE_${unit?.occupancyType}`)}
                        actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/pt/property/${typeOfApplication}/PtUnits`} />}
                      />
                      <Row
                        label={t("PT_FORM2_USAGE_TYPE")}
                        text={t(
                          `PROPERTYTAX_BILLING_SLAB_${
                            unit?.usageCategory?.split(".").length > 2 ? unit?.usageCategory?.split(".")[1] : unit?.usageCategory?.split(".")[0]
                          }`
                        )}
                        actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/pt/property/${typeOfApplication}/PtUnits`} />}
                      />{" "}
                      {unit?.unitType && (
                        <Row
                          label={t("PT_FORM2_SUB_USAGE_TYPE")}
                          text={t(`PROPERTYTAX_BILLING_SLAB_${unit?.unitType}`)}
                          actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/pt/property/${typeOfApplication}/PtUnits`} />}
                        />
                      )}
                      <Row
                        label={t("PT_FLOOR_NO")}
                        text={`${unit?.floorNo}`}
                        actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/pt/property/${typeOfApplication}/PtUnits`} />}
                      />
                      {unit?.arv && (
                        <Row
                          label={t("PT_PROPERTY_ANNUAL_RENT_LABEL")}
                          text={`${unit?.arv}`}
                          actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/pt/property/${typeOfApplication}/PtUnits`} />}
                        />
                      )}
                    </div>
                  );
                })}
           </StatusTable> */}
          {/* Loop with sort method end */}

          <CheckBox
            label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_FINAL_DECLARATION_MESSAGE")}
            onChange={setdeclarationhandler}
            styles={{ height: "auto" }}
            disabled={!agree}
          />
        </div>
        <SubmitBar
          label={t("WMS_RUNNING_ACCOUNT_FINAL_BILL_COMMON_BUTTON_SUBMIT")}
          onSubmit={onSubmit}
          disabled={!agree}
        />
      </Card>
    </React.Fragment>
  );
};

export default CheckPage;
