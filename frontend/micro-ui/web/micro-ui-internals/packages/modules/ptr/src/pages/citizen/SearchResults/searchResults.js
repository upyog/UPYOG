import React, { useEffect, useRef, useState } from "react";
import { Header, ResponseComposer, Loader, Modal, Card, KeyNote, SubmitBar, CitizenInfoLabel} from "@egovernments/digit-ui-react-components";
import PropTypes from "prop-types";
import { useHistory, Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";


const PTRSearchResults = ({ template, header, actionButtonLabel, isMutation, onSelect, config, clearParams = () => {} }) => {
  const { t } = useTranslation();
  const modalRef = useRef();
  const { mobileNumber, applicationNumber,/*propertyIds, oldPropertyIds,*/ locality, city,doorNo,name, PToffset } = Digit.Hooks.useQueryParams();
  console.log("mmmmmmmmmmmmmm",applicationNumber);
  let filters = {};
  const [modalData, setShowModal] = useState(false);

  let OfsetForSearch = PToffset;
  let t1;
  let off;
  if (!isNaN(parseInt(OfsetForSearch))) {
    off = OfsetForSearch;
    t1 = parseInt(OfsetForSearch) + 5;
  } else {
    t1 = 5;
  }
  let filter1 = !isNaN(parseInt(OfsetForSearch))
    ? { limit: "50", sortOrder: "ASC", sortBy: "createdTime", offset: off ,status:"ACTIVE"}
    : { limit: "5", sortOrder: "ASC", sortBy: "createdTime", offset: "0",status:"ACTIVE" };

  const closeModal = () => {
    setShowModal(false);
  };
  Digit.Hooks.useClickOutside(modalRef, closeModal, modalData);

  if (mobileNumber) filters.mobileNumber = mobileNumber;
  if (applicationNumber) filters.applicationNumber = applicationNumber;
  // if (propertyIds) filters.propertyIds = propertyIds;
  // if (oldPropertyIds) filters.oldPropertyIds = oldPropertyIds;
  if (locality) filters.locality = locality;
  if (doorNo) filters.doorNo = doorNo;
  if (name) filters.name = name;
  filters.limit = filter1.limit;
  filters.sortOrder = filter1.sortOrder;
  filters.sortBy = filter1.sortBy;
  filters.offset = filter1.offset;
  filters.status = filter1.status;


  const [owners, setOwners, clearOwners] = Digit.Hooks.useSessionStorage("PT_MUTATE_MULTIPLE_OWNERS", null);
  // const [params, setParams, ] = Digit.Hooks.useSessionStorage("PT_MUTATE_PROPERTY");
  const [lastPath, setLastPath, clearLastPath] = Digit.Hooks.useSessionStorage("PT_MUTATE_MULTIPLE_OWNERS_LAST_PATH", null);

  useEffect(() => {
    setOwners([]);
    clearParams();
    setLastPath("");
  }, []);

  // const auth = !!isMutation;    /*  to enable open search set false  */
  const auth =true;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const searchArgs = city ? { tenantId: city, filters, auth } : { filters, auth };
  const result = Digit.Hooks.ptr.usePTRSearch(searchArgs);
  const consumerCode = result?.data?.PetRegistrationApplications?.map((a) => a.applicationNumber).join(",");

  // let fetchBillParams = mobileNumber ? { mobileNumber, consumerCode } : { consumerCode };
  let fetchBillParams = mobileNumber ? { consumerCode } : { mobileNumber, consumerCode };

  if (window.location.href.includes("/search-results")) fetchBillParams = { consumerCode };

  const paymentDetails = Digit.Hooks.useFetchBillsForBuissnessService(
     
    { businessService: "pet-services", ...fetchBillParams, tenantId: city },
    {
      enabled: consumerCode ? true : false,
      retry: false,
    }
  );

  console.log("payyyyyyy",paymentDetails);

 


  const history = useHistory();

  const proceedToPay = (data) => {
    history.push(`/digit-ui/citizen/payment/my-bills/pet-services/${data.applicationNumber}`, { tenantId });
  };

  if (paymentDetails.isLoading || result.isLoading) {
    return <Loader />;
  }

  if (result.error || !consumerCode) {
    return (
      <div style={{height : "150px"}}>
        <Card style={{display: "flex", justifyContent: "center", alignItems: "center", height: "100%"}}>{t("CS_PTR_NO_PetRegistrationApplications_FOUND")}</Card>
      </div>
    );
  }

  const onSubmit = (data) => {

    console.log("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh",data);
    if (isMutation) {
      let PetRegistrationApplications = result?.data?.PetRegistrationApplications?.filter?.((e) => e.applicationNumber === data.applicationNumber)[0];
      if (Number(data.total_due) > 0) {
        setShowModal(data);
      } else onSelect(config.key, { data, PetRegistrationApplications });
    } else history.push(`/digit-ui/citizen/payment/my-bills/pet-services/${data.applicationNumber}`, { tenantId });
  };

  const payment = {};

  paymentDetails?.data?.Bill?.forEach((element) => {
    if (element?.consumerCode) {
      payment[element?.consumerCode] = {
        total_due: element?.totalAmount,
        bil_due__date: new Date(element?.billDetails?.[0]?.expiryDate).toDateString(),
      };
    }
  });

  const arr = isMutation ? result?.data?.PetRegistrationApplications?.filter((e) => e.status === "ACTIVE") : result?.data?.PetRegistrationApplications;

  const searchResults = arr?.map((PetRegistrationApplications) => {
    let addr = PetRegistrationApplications?.address || {};

    return {
      applicationNumber: PetRegistrationApplications?.applicationNumber,
      owner_name: (PetRegistrationApplications?.owners || [])?.map( o => o?.name ). join(","),
      property_address: [addr.doorNo || "", addr.buildingName || "", addr.street || "", addr.locality?.name || "", addr.city || ""]
        .filter((a) => a)
        .join(", "),
      total_due: payment[PetRegistrationApplications?.applicationNumber]?.total_due || 0,
      bil_due__date: payment[PetRegistrationApplications?.applicationNumber]?.bil_due__date || t("N/A"),
    };
  });

  return (
    <div className="static" style={{ marginTop: "16px" }}>
      <div className="static-wrapper">
        {header && (
          <Header style={{ marginLeft: "8px" }}>
            {t(header)} ({searchResults?.length})
          </Header>
        )}
        <ResponseComposer data={searchResults} template={template} actionButtonLabel={actionButtonLabel} onSubmit={onSubmit} />
      </div>

      {modalData ? (
        <Modal
          hideSubmit={true}
          isDisabled={false}
          popupStyles={{ width: "319px", height: "250px", margin: "auto" }}
          formId="modal-action"
        >
          <div ref={modalRef}>
            <KeyNote
              keyValue={t("PTR_AMOUNT_DUE")}
              note={`₹ ${modalData?.total_due?.toLocaleString("en-IN")}`}
              noteStyle={{ fontSize: "24px", fontWeight: "bold" }}
            />
            <p>
              {t("PTR_YOU_HAVE") +
                " " +
                "₹" +
                " " +
                modalData?.total_due.toLocaleString("en-IN") +
                " " +
                t("PTR_PENDING_AMOUNT") +
                " " +
                t("PTR_INORDER_TO_TRANSFER")}
            </p>
            <SubmitBar
              submit={false}
              onSubmit={() => proceedToPay(modalData)}
              style={{ marginTop: "14px", width: "100%" }}
              label={t("PTR_PROCEED_PAYMENT")}
            />
          </div>
        </Modal>
      ) : null}
      {!searchResults?.length > 0 && <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("PT_NO_PROP_FOUND_MSG")}</p>}
      {searchResults?.length !== 0 && (searchResults?.length == 5 || searchResults?.length == 50) && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              {t("PT_LOAD_MORE_MSG")}{" "}
              <span className="link">{<Link to={`/digit-ui/citizen/ptr/petservice/search-results?mobileNumber=${mobileNumber?mobileNumber:""}&propertyIds=${propertyIds?propertyIds:""}&oldPropertyIds=${oldPropertyIds?oldPropertyIds:""}&doorNo=${doorNo?doorNo:""}&name=${name?name:""}&city=${city?city:""}&locality=${locality?locality:""}&PToffset=${t1}`}>{t("PT_COMMON_CLICK_HERE")}</Link>}</span>
            </p>
          </div>
        )}
        { isMutation && searchResults?.length !== 0
          ? <CitizenInfoLabel
              info={t("CS_FILE_APPLICATION_INFO_LABEL")} 
              text={t("PT_CANNOT_TRANSFER_IF_AMOUNT_PENDING")} 
            />
          : null
        }
    </div>
  );
};

PTRSearchResults.propTypes = {
  template: PropTypes.any,
  header: PropTypes.string,
  actionButtonLabel: PropTypes.string,
};

PTRSearchResults.defaultProps = {
  template: [],
  header: null,
  actionButtonLabel: null,
};

export default PTRSearchResults;
