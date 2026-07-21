/* 
 * Fire NOC Citizen Application Summary Review Page (CheckPage)
 * Consolidates application inputs into a single, scrollable summary card before final submission.
 * Integrates the Fee Estimate card, NBC building codes, location details, owner details, and final user declaration checkbox.
 */
import {
  Card,
  CardHeader,
  Header,
  CardSubHeader,
  EditIcon,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar,
  Loader,
  CheckBox,
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import Timeline from "../../../components/NocTimeline";
import { convertToFireNOCPayload } from "../../../utils";

const CheckPage = ({ onSubmit, value }) => {
  const { t } = useTranslation();
  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  const navigate = useNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const routeLink = `${modulePath}/new-application`;

  useEffect(() => {
    localStorage.setItem("NocAppSubmitEnabled", "true");
    return () => {
      localStorage.setItem("NocAppSubmitEnabled", "false");
    };
  }, []);

  const routeTo = (jumpTo) => {
    navigate(jumpTo);
  };

  const {
    nocType,
    ownershipCategory,
    owners,
    property,
    documents,
  } = value;
  const location = property?.location?.property;
  const tenantId = value?.location?.city?.code || Digit.ULBService.getCurrentTenantId();
  const applicationNumber = value?.applicationNumber || value?.existingApplication?.fireNOCDetails?.applicationNumber;

  const { data: billData, isLoading: isBillLoading } = Digit.Hooks.useFetchBillsForBuissnessService(
    {
      tenantId,
      businessService: "FIRENOC",
      consumerCode: applicationNumber
    },
    {
      enabled: !!applicationNumber
    }
  );

  const bill = billData?.Bill?.[0];
  const taxHeadEstimates = bill?.billDetails?.[0]?.billAccountDetails?.map(acc => ({
    taxHeadCode: acc.taxHeadCode,
    estimateAmount: acc.amount
  })) || [];
  const totalAmount = bill?.totalAmount;
  return (
    <React.Fragment>
      <Timeline currentStep={5} />
      <Header styles={{ fontSize: "32px" }}>{t("NOC_SUMMARY_HEADER")}</Header>

      <Card style={{ marginBottom: "20px", padding: "24px" }}>
        {/* 7. Fee Estimate */}
        {isBillLoading ? (
          <Loader />
        ) : (
          bill && (
            <div style={{ marginBottom: "24px" }}>
              <CardHeader styles={{ fontSize: "24px" }}>
                {t("NOC_FEE_ESTIMATE_HEADER", "Fee Estimate")}
              </CardHeader>
              <div style={{ border: "1px solid #EAEAEA", borderRadius: "4px", overflow: "hidden", marginTop: "16px" }}>
                <div style={{ padding: "16px" }}>
                  {taxHeadEstimates?.map((est, idx) => (
                    <div key={idx} style={{ display: "flex", justifyContent: "space-between", marginBottom: "12px" }}>
                      <div style={{ color: "#0B0C0C", fontSize: "16px" }}>{t(est.taxHeadCode)}</div>
                      <div style={{ color: "#0B0C0C", fontSize: "16px", textAlign: "right" }}>₹ {est.estimateAmount !== undefined ? est.estimateAmount : t("CS_NA")}</div>
                    </div>
                  ))}
                </div>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", background: "#F4F4F4", padding: "16px", borderTop: "1px solid #EAEAEA" }}>
                  <div style={{ fontWeight: "bold", fontSize: "18px", color: "#0B0C0C" }}>{t("NOC_TOTAL_AMOUNT_LABEL", "Total Amount")}</div>
                  <div style={{ fontWeight: "bold", fontSize: "18px", color: "#0B0C0C", textAlign: "right" }}>₹ {totalAmount !== undefined ? totalAmount : t("CS_NA")}</div>
                </div>
              </div>
            </div>
          )
        )}

        {/* 1. NOC Type Summary */}
        <div style={{ marginBottom: "24px" }}>
          <CardHeader styles={{ fontSize: "24px" }}>
            {t("NOC_TYPE_HEADER")}
            <LinkButton
              label={<EditIcon style={{ float: "right" }} />}
              onClick={() => routeTo(`${routeLink}/noc-type`)}
            />
          </CardHeader>
          <StatusTable>
            <Row
              label={t("NOC_TYPE_LABEL")}
              text={nocType?.nocType ? t(`NOC_TYPE_${nocType.nocType}`) : t("CS_NA")}
            />
            {nocType?.nocType === "PROVISIONAL" && (
              <Row
                label={t("NOC_PROVISIONAL_NUMBER_LABEL")}
                text={nocType?.provisionalNocNumber || t("CS_NA")}
              />
            )}
            {nocType?.oldFireNOCNumber && (
              <Row
                label={t("NOC_OLD_NUMBER_LABEL")}
                text={nocType?.oldFireNOCNumber || t("CS_NA")}
              />
            )}
          </StatusTable>
        </div>

        {/* 2. Ownership details */}
        <div style={{ marginBottom: "24px" }}>
          <CardHeader styles={{ fontSize: "24px" }}>
            {t("NOC_OWNERSHIP_HEADER")}
            <LinkButton
              label={<EditIcon style={{ float: "right" }} />}
              onClick={() => routeTo(`${routeLink}/owner-details`)}
            />
          </CardHeader>
          <StatusTable>
            <Row
              label={t("NOC_OWNERSHIP_TYPE_LABEL")}
              text={
                ownershipCategory?.code
                  ? t(`COMMON_MASTERS_OWNERSHIPCATEGORY_${ownershipCategory.code.replaceAll(".", "_")}`)
                  : ownershipCategory?.ownershipCategory?.code
                    ? t(`COMMON_MASTERS_OWNERSHIPCATEGORY_${ownershipCategory.ownershipCategory.code.replaceAll(".", "_")}`)
                    : t("CS_NA")
              }
            />
          </StatusTable>
        </div>

        {/* 3. Owners Summary */}
        <div style={{ marginBottom: "24px" }}>
          <CardHeader styles={{ fontSize: "24px" }}>
            {t("NOC_OWNER_DETAILS_HEADER")}
            <LinkButton
              label={<EditIcon style={{ float: "right" }} />}
              onClick={() => routeTo(`${routeLink}/owner-details`)}
            />
          </CardHeader>
          {(owners?.owners || []).map((owner, idx) => (
            <div key={idx} style={{ marginBottom: "15px", borderBottom: idx < owners.owners.length - 1 ? "1px solid #ccc" : "none", paddingBottom: "10px" }}>
              <CardSubHeader>{`${t("NOC_OWNER_LABEL")} ${idx + 1}`}</CardSubHeader>
              <StatusTable>
                <Row label={t("NOC_OWNER_NAME_LABEL")} text={owner.name || t("CS_NA")} />
                <Row label={t("NOC_OWNER_MOBILE_LABEL")} text={owner.mobileNumber || t("CS_NA")} />
                <Row label={t("NOC_OWNER_FATHER_LABEL")} text={owner.fatherOrHusbandName || t("CS_NA")} />
                <Row
                  label={t("NOC_OWNER_RELATIONSHIP_LABEL")}
                  text={owner.relationship?.label || owner.relationship || t("CS_NA")}
                />
                <Row label={t("NOC_OWNER_GENDER_LABEL")} text={owner.gender?.label || owner.gender || t("CS_NA")} />
                <Row label={t("NOC_OWNER_DOB_LABEL")} text={owner.dob || t("CS_NA")} />
                <Row label={t("NOC_OWNER_CORR_ADDR_LABEL")} text={owner.correspondenceAddress || t("CS_NA")} />
              </StatusTable>
            </div>
          ))}
        </div>

        {/* 4. Property details */}
        <div style={{ marginBottom: "24px" }}>
          <CardHeader styles={{ fontSize: "24px" }}>
            {t("NOC_PROPERTY_DETAILS_HEADER")}
            <LinkButton
              label={<EditIcon style={{ float: "right" }} />}
              onClick={() => routeTo(`${routeLink}/property-details`)}
            />
          </CardHeader>
          <StatusTable>
            <Row
              label={t("NOC_PROPERTY_TYPE_LABEL")}
              text={property?.propertyType?.code ? t(`FIRENOC_BUILDINGTYPE_${property.propertyType.code}`) : t("CS_NA")}
            />
            <Row
              label={t("NOC_NO_OF_BUILDINGS_LABEL")}
              text={property?.noOfBuildings ? t(`NOC_NO_OF_BUILDINGS_${property.noOfBuildings}_RADIOBUTTON`) : t("CS_NA")}
            />
          </StatusTable>
          {(property?.buildings || []).map((building, idx) => (
            <div key={idx} style={{ marginTop: "15px", borderBottom: idx < property.buildings.length - 1 ? "1px solid #ccc" : "none", paddingBottom: "10px" }}>
              <CardSubHeader>{building.name || `${t("NOC_BUILDING_LABEL")} ${idx + 1}`}</CardSubHeader>
              <StatusTable>
                <Row
                  label={t("NOC_BUILDING_USAGE_TYPE_LABEL")}
                  text={
                    building?.buildingSubUsageType?.code
                      ? t(`FIRENOC_BUILDINGTYPE_${building.buildingSubUsageType.code.replaceAll(".", "_")}`)
                      : t("CS_NA")
                  }
                />
                {Object.keys(building?.uomsMap || {}).map((uomKey) => (
                  <Row
                    key={uomKey}
                    label={t(`NOC_UOM_${uomKey.toUpperCase()}`)}
                    text={building.uomsMap[uomKey] || t("CS_NA")}
                  />
                ))}
              </StatusTable>
            </div>
          ))}
        </div>

        {/* 5. Location Details */}
        <div style={{ marginBottom: "24px" }}>
          <CardHeader styles={{ fontSize: "24px" }}>
            {t("NOC_LOCATION_DETAILS_HEADER")}
            <LinkButton
              label={<EditIcon style={{ float: "right" }} />}
              onClick={() => routeTo(`${routeLink}/property-details`)}
            />
          </CardHeader>
          <StatusTable>
            <Row label={t("NOC_LOCATION_CITY_LABEL")} text={location?.city?.name || t("CS_NA")} />
            <Row label={t("NOC_LOCATION_MOHALLA_LABEL")} text={location?.locality?.name || t("CS_NA")} />
            <Row label={t("NOC_LOCATION_PLOT_NO_LABEL")} text={location?.plotNo || t("CS_NA")} />
            <Row label={t("NOC_LOCATION_STREET_LABEL")} text={location?.streetName || t("CS_NA")} />
            <Row label={t("NOC_LOCATION_BUILDING_LABEL")} text={location?.buildingName || t("CS_NA")} />
            <Row label={t("NOC_LOCATION_PINCODE_LABEL")} text={location?.pincode || t("CS_NA")} />
            <Row label={t("NOC_LOCATION_FIRESTATION_LABEL")} text={location?.fireStation?.name || location?.fireStation || t("CS_NA")} />
            {location?.propertyId && <Row label={t("NOC_LOCATION_PROPERTY_ID_LABEL")} text={location.propertyId} />}
            {location?.latitude && <Row label={t("NOC_LOCATION_LATITUDE_LABEL")} text={`${location.latitude}`} />}
            {location?.longitude && <Row label={t("NOC_LOCATION_LONGITUDE_LABEL")} text={`${location.longitude}`} />}
          </StatusTable>
        </div>

        {/* 6. Document Details */}
        <div style={{ marginBottom: "24px" }}>
          <CardHeader styles={{ fontSize: "24px" }}>
            {t("NOC_DOCUMENT_DETAILS_HEADER")}
            <LinkButton
              label={<EditIcon style={{ float: "right" }} />}
              onClick={() => routeTo(`${routeLink}/document-details`)}
            />
          </CardHeader>
          <StatusTable>
            {(documents?.documents || []).map((doc, idx) => (
              <Row
                key={idx}
                label={t(doc.categoryCode ? doc.categoryCode.replaceAll(".", "_") : doc.documentType.replaceAll(".", "_"))}
                text={
                  doc.buildingName
                    ? `${doc.fileName} (${doc.buildingName})`
                    : doc.fileName
                }
              />
            ))}
          </StatusTable>
        </div>
      </Card>



      <div style={{ marginTop: "24px", marginBottom: "24px" }}>
        <CheckBox
          label={t("NOC_FINAL_DECLARATION_MESSAGE")}
          onChange={setdeclarationhandler}
          checked={agree}
          styles={{ height: "auto" }}
        />
      </div>

      <SubmitBar label={t("CS_COMMON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
    </React.Fragment>
  );
};

export default CheckPage;
