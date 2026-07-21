/* 
 * NOC Type Selection Page Component (Step 1 of the Citizen wizard flow)
 * Allows users to choose between a "New Application" or a "Renewal / Provisional NOC" type.
 * Offers real-time search capabilities via provisional number integration to pull existing application data.
 */
import React, { useState, useEffect } from "react";
import { FormStep, RadioButtons, TextInput, CardLabel, CardLabelError, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import Timeline from "../components/NocTimeline";

const getCategoryCode = (docType) => {
  if (docType.startsWith("OWNER")) {
    return docType.split(".").slice(0, 2).join(".");
  }
  return docType;
};

const NocTypeSelection = ({ t, config, onSelect, userType, formData }) => {
  const [nocType, setNocType] = useState(() => {
    if (formData?.nocType?.nocType) {
      return {
        code: formData.nocType.nocType,
        i18nKey: `NOC_TYPE_${formData.nocType.nocType}_RADIOBUTTON`,
      };
    }
    return { i18nKey: "NOC_TYPE_NEW_RADIOBUTTON", code: "NEW" };
  });

  const [provisionalNocNumber, setProvisionalNocNumber] = useState(
    formData?.nocType?.provisionalNocNumber || ""
  );
  const [searching, setSearching] = useState(false);
  const [error, setError] = useState(null);

  /* Auto-dismisses warning toast messages after 3 seconds */
  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => {
        setError(null);
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [error]);

  const menu = [
    { i18nKey: "NOC_TYPE_NEW_RADIOBUTTON", code: "NEW" },
    { i18nKey: "NOC_TYPE_PROVISIONAL_RADIOBUTTON", code: "PROVISIONAL" },
  ];

  const selectNocType = (value) => {
    setNocType(value);
    setError(null);
  };

  const convertEpochToDateString = (epoch) => {
    if (!epoch) return "";
    const d = new Date(epoch);
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const year = d.getFullYear();
    return `${year}-${month}-${day}`;
  };

  const handleSearchProvisional = async () => {
    /* Validates empty input and sets search error to trigger warning toast */
    if (!provisionalNocNumber) {
      setError("ERR_FILL_PROVISIONAL_NOC_NUMBER");
      return;
    }
    setSearching(true);
    setError(null);
    try {
      const response = await Digit.NOCService.search(Digit.ULBService.getStateId(), {
        fireNOCNumber: provisionalNocNumber.trim(),
      });
      if (response && response.FireNOCs && response.FireNOCs.length > 0) {
        const firenoc = response.FireNOCs[0];
        const mappedData = {};

        mappedData.nocType = {
          nocType: "NEW",
          provisionalNocNumber: firenoc.provisionFireNOCNumber || firenoc.fireNOCNumber,
        };

        if (firenoc.fireNOCDetails?.applicantDetails?.ownerShipType) {
          const ownerShipType = firenoc.fireNOCDetails.applicantDetails.ownerShipType;
          mappedData.ownershipCategory = {
            code: ownerShipType,
            i18nKey: `COMMON_MASTERS_OWNERSHIPCATEGORY_${ownerShipType.replaceAll(".", "_")}`
          };
        }

        if (firenoc.fireNOCDetails?.applicantDetails?.owners) {
          const ownersList = firenoc.fireNOCDetails.applicantDetails.owners.map(o => ({
            name: o.name || "",
            gender: o.gender ? { code: o.gender, i18nKey: `NOC_GENDER_${o.gender}` } : null,
            mobileNumber: o.mobileNumber || "",
            fatherOrHusbandName: o.fatherOrHusbandName || "",
            emailId: o.emailId || "",
            relationship: o.relationship ? { code: o.relationship, i18nKey: `NOC_APPLICANT_RELATIONSHIP_${o.relationship}` } : null,
            dob: o.dob ? convertEpochToDateString(o.dob) : "",
            pan: o.pan || "",
            correspondenceAddress: o.correspondenceAddress || "",
            ownerType: o.ownerType ? { code: o.ownerType, i18nKey: `COMMON_MASTERS_OWNERTYPE_${o.ownerType}` } : null,
            institutionName: firenoc.fireNOCDetails?.applicantDetails?.additionalDetail?.institutionName || "",
            telephoneNumber: firenoc.fireNOCDetails?.applicantDetails?.additionalDetail?.telephoneNumber || "",
            institutionDesignation: firenoc.fireNOCDetails?.applicantDetails?.additionalDetail?.institutionDesignation || "",
          }));
          mappedData.owners = { owners: ownersList };
        }

        if (firenoc.fireNOCDetails?.buildings) {
          const buildingsList = firenoc.fireNOCDetails.buildings.map(b => {
            const usageTypeMajor = b.usageType ? b.usageType.split(".")[0] : "";
            const uomsMap = {};
            (b.uoms || []).forEach(u => {
              uomsMap[u.code] = String(u.value);
            });
            return {
              name: b.name || "",
              buildingUsageType: usageTypeMajor ? { code: usageTypeMajor, i18nKey: `FIRENOC_BUILDINGTYPE_${usageTypeMajor}` } : null,
              buildingSubUsageType: b.usageType ? { code: b.usageType, i18nKey: `FIRENOC_BUILDINGTYPE_${b.usageType.replaceAll(".", "_")}`, uom: b.uoms?.filter(u => u.isActiveUom).map(u => u.code) || [] } : null,
              uomsMap: {
                NO_OF_FLOORS: uomsMap.NO_OF_FLOORS || "",
                NO_OF_BASEMENTS: uomsMap.NO_OF_BASEMENTS || "",
                PLOT_SIZE: uomsMap.PLOT_SIZE || "",
                BUILTUP_AREA: uomsMap.BUILTUP_AREA || "",
                HEIGHT_OF_BUILDING: uomsMap.HEIGHT_OF_BUILDING || "",
                ...uomsMap
              }
            };
          });
          mappedData.property = {
            noOfBuildings: firenoc.fireNOCDetails.noOfBuildings || "SINGLE",
            buildings: buildingsList
          };
        }

        if (firenoc.fireNOCDetails?.propertyDetails?.address) {
          const address = firenoc.fireNOCDetails.propertyDetails.address;
          mappedData.location = {
            propertyId: firenoc.fireNOCDetails.propertyDetails.propertyId || "",
            city: address.city ? { code: address.city } : null,
            locality: address.locality ? { code: address.locality.code, name: address.locality.name, i18nkey: `${address.city?.toUpperCase().replace(/[.]/g, "_")}_REVENUE_${address.locality.code?.toUpperCase().replace(/[._:-\s\/]/g, "_")}` } : null,
            plotNo: address.doorNo || "",
            buildingName: address.buildingName || "",
            streetName: address.street || "",
            pincode: address.pincode || "",
            latitude: address.latitude || "",
            longitude: address.longitude || "",
            fireStation: firenoc.fireNOCDetails.firestationId ? { code: firenoc.fireNOCDetails.firestationId, i18nKey: `FIRENOC_FIRESTATIONS_${firenoc.fireNOCDetails.firestationId.toUpperCase()}` } : null,
          };
        }

        const docsList = [];
        (firenoc.fireNOCDetails?.applicantDetails?.additionalDetail?.documents || []).forEach(d => {
          docsList.push({
            documentType: d.documentType,
            fileStoreId: d.fileStoreId,
            categoryCode: getCategoryCode(d.documentType)
          });
        });
        (firenoc.fireNOCDetails?.buildings || []).forEach(b => {
          (b.applicationDocuments || []).forEach(d => {
            docsList.push({
              documentType: d.documentType,
              fileStoreId: d.fileStoreId,
              categoryCode: getCategoryCode(d.documentType),
              buildingName: b.name
            });
          });
        });
        mappedData.documents = { documents: docsList };

        onSelect("formData", {
          ...formData,
          ...mappedData
        });
        setNocType({ i18nKey: "NOC_TYPE_NEW_RADIOBUTTON", code: "NEW" });
      } else {
        setError(t("ERR_FIRENOC_NUMBER_INCORRECT"));
      }
    } catch (err) {
      console.error(err);
      setError(t("ERR_FIRENOC_SEARCH_FAILED"));
    } finally {
      setSearching(false);
    }
  };

  const goNext = () => {
    if (nocType.code === "NEW" && !provisionalNocNumber) {
      setError(t("NOC_PROVISIONAL_FIRE_NOC_NO_REQUIRED"));
      return;
    }
    onSelect(config.key, {
      nocType: nocType.code,
      provisionalNocNumber: nocType.code === "NEW" ? provisionalNocNumber.trim() : "",
    });
  };

  const onSkip = () => onSelect();

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}
      <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!nocType} forcedError={error}>
        <RadioButtons
          t={t}
          optionsKey="i18nKey"
          isMandatory={config.isMandatory}
          options={menu}
          selectedOption={nocType}
          onSelect={selectNocType}
        />

        {nocType?.code === "NEW" && (
          <div style={{ marginTop: "20px" }}>
            <CardLabel>{t("NOC_PROVISIONAL_FIRE_NOC_NO_LABEL")} <span style={{ color: "red" }}>*</span></CardLabel>
            <div style={{ display: "flex", gap: "10px", marginBottom: "15px" }}>
              <TextInput
                t={t}
                type="text"
                name="provisionalNocNumber"
                value={provisionalNocNumber}
                onChange={(e) => {
                  setProvisionalNocNumber(e.target.value);
                  setError(null);
                }}
                placeholder={t("NOC_PROVISIONAL_FIRE_NOC_NO_PLACEHOLDER")}
                style={{ flex: 1 }}
              />
              <button
                className="submit-bar"
                type="button"
                onClick={handleSearchProvisional}
                style={{ minWidth: "80px", height: "48px", marginTop: "0px" }}
              >
                {searching ? t("NOC_SEARCHING") : t("NOC_SEARCH")}
              </button>
            </div>
          </div>
        )}
      </FormStep>
      {error && <Toast error={true} label={t(error)} onClose={() => setError(null)} />}
    </React.Fragment>
  );
};

export default NocTypeSelection;

