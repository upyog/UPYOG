// Description: CMSearchCertificate component renders a form having certificate type, certificate number and a captcha for verification and display's a table as a result
// @author: Khalid Rashid

// Hcaptcha library is used for captcha
// @github: https://github.com/hCaptcha/react-hcaptcha
// npm command: npm install @hcaptcha/react-hcaptcha --save

import React, { useEffect, useState, useRef } from "react";
import { Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import HCaptcha from "@hcaptcha/react-hcaptcha";
// import { hcaptchaDetails } from "../utils";
import { convertEpochToDate } from "../utils";
import { useForm, Controller } from "react-hook-form";
import {
  TextInput,
  SubmitBar,
  SearchForm,
  Dropdown,
  SearchField,
  Table,
  Card,
  Header,
} from "@nudmcdgnpm/digit-ui-react-components";

const CMSearchCertificate = ({ path }) => {
  const { variant } = useParams();
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const [showToast, setShowToast] = useState(null);

  const isMobile = window.Digit.Utils.browser.isMobile();
  const todaydate = new Date();
  const today = todaydate.toISOString().split("T")[0];

  const [token, setToken] = useState("");
  const captcha = useRef();
  const [ishuman, setIshuman] = useState(false);
  const [istable, setistable] = useState(false);
  const [certificate_name, setCertificate_name] = useState("");
  const [certificate_No, setCertificate_No] = useState("");

  // function to reset captcha
  const resetCaptcha = () => {
    if (captcha.current) {
      captcha.current.resetCaptcha();
    }
  };

  function setcertificate_No(e) {
    setCertificate_No(e.target.value);
  }

  // Initialized form handling using the useForm hook from React Hook Form, setting default values for pagination, sorting, and date range filters.
  const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
    defaultValues: {
      offset: 0,
      limit: !isMobile && 10,
      sortBy: "commencementDate",
      sortOrder: "DESC",
      fromDate: today,
      toDate: today,
    },
  });

  // sets ishuman to be true based on token
  useEffect(() => {
    if (token) {
      setIshuman(true);
    }
  }, [token]);

  useEffect(() => {
    register("offset", 0);
    register("limit", 10);
    register("sortBy", "commencementDate");
    register("sortOrder", "DESC");
    // setValue("fromDate", today);
    // setValue("toDate", today);
  }, [register, setValue, today]);

  // columns defined to be passed in applicationtable
  const columns = [
    { Header: t("CITIZEN_NAME"), accessor: "name" },
    { Header: t("CITIZEN_ADDRESS"), accessor: "address" },
    { Header: t("CERTIFICATE_NUMBER"), accessor: "certificateNumber" },
    { Header: t("ISSUE_DATE"), accessor: "issueDate" },
    { Header: t("VALID_UPTO"), accessor: "validUpto" },
    { Header: t("CERTIFICATE_STATUS"), accessor: "certificateStatus" },
  ];

  // defined a state to handle data object
  const [updatedData, setUpdatedData] = useState([
    {
      name: "",
      address: "",
      certificateNumber: "",
      issueDate: "",
      validUpto: "",
      certificateStatus: "",
    },
  ]);

  // certificates to be passed in the dropdown to select a particular certificate and their crtNo for the switch case in onsubmit function
  const certificateTypes = [
    {
      code: "Asset Certificate",
      i18nKey: "Asset Certificate",
      crtNo: 14,
    },
    {
      code: "Birth Certificate",
      i18nKey: "Birth Certificate",
      crtNo: 12,
    },
    {
      code: "Building Plan Approval",
      i18nKey: "Building Plan Approval",
      crtNo: 10,
    },
    {
      code: "Community Hall Booking",
      i18nKey: "Community Hall Booking",
      crtNo: 2,
    },
    {
      code: "Death Certificate",
      i18nKey: "Death Certificate",
      crtNo: 13,
    },
    {
      code: "Deslugging Service",
      i18nKey: "Deslugging Service",
      crtNo: 8,
    },
    {
      code: "E-Waste Certificate",
      i18nKey: "E-Waste Certificate",
      crtNo: 1,
    },
    {
      code: "Fire NOC Certificate",
      i18nKey: "Fire NOC Certificate",
      crtNo: 9,
    },
    {
      code: "Pet Certificate",
      i18nKey: "Pet Certificate",
      crtNo: 11,
    },
    {
      code: "Public Grievance Redressal",
      i18nKey: "Public Grievance Redressal",
      crtNo: 7,
    },
    {
      code: "Property Tax",
      i18nKey: "Property Tax",
      crtNo: 3,
    },
    {
      code: "Sewerage Certificate",
      i18nKey: "Sewerage Certificate",
      crtNo: 5,
    },
    {
      code: "Trade License",
      i18nKey: "Trade License",
      crtNo: 6,
    },
    {
      code: "Water Certificate",
      i18nKey: "Water Certificate",
      crtNo: 4,
    },
  ];

  // dataCMObjectConverter function to set data in a structure needed by the application table
  const dataCMObjectConverter = (name, address, certificateNumber, issueDate, validUpto, certificateStatus) => {
    return {
      name: name,
      address: address,
      certificateNumber: certificateNumber,
      issueDate: issueDate,
      validUpto: validUpto,
      certificateStatus: certificateStatus,
    };
  };

  // getAddress function is defined for setting the structure of address to be passed in the address field for applicationtable
  const getAddress = (module_data) => {
    return (
      (module_data?.address?.doorNo ? module_data?.address?.doorNo : "") +
      (module_data?.address?.landmark ? ", " + module_data?.address?.landmark : "") +
      (module_data.address?.locality ? ", " + module_data.address?.locality : "") +
      (module_data?.address?.addressLine1 ? ", " + module_data?.address?.addressLine1 : "") +
      (module_data?.address?.city ? ", " + module_data?.address?.city : "") +
      (module_data?.address?.pincode ? ", " + module_data?.address?.pincode : "")
    );

  };

  // Functions defined to use services of different modules to access and structure data to render in table
  async function ewCertificate(certificate_No) {
    const applicationDetails = await Digit.EwService.search({ tenantId, filters: { requestId: certificate_No } });
    const dataew = applicationDetails?.EwasteApplication[0];
    console.log("ewaste certificate::", dataew)

    if (dataew?.requestId) {
      const EW_Req_data = dataCMObjectConverter(
        dataew?.applicant?.applicantName,
        getAddress(dataew),
        dataew?.requestId,
        "-NA-",
        "-NA-",
        dataew?.requestStatus
      );
      setUpdatedData([EW_Req_data]);
    } else {
      setShowToast({ label: "Application doesn't exist", warning: true })
    }
  }

  async function chbCertificate(certificate_No) {
    const applicationDetails = await Digit.CHBServices.search({ tenantId, filters: { bookingNo: certificate_No } });
    const datachb = applicationDetails?.hallsBookingApplication[0];

    if (datachb.bookingNo) {
      const CHB_Req_data = dataCMObjectConverter(
        datachb?.applicantDetail?.accountHolderName,
        getAddress(datachb),
        datachb.bookingNo,
        convertEpochToDate(datachb?.paymentDate),
        "-NA-",
        datachb?.bookingStatus
      );

      setUpdatedData([CHB_Req_data]);
    } else {
      setShowToast({ label: "Application doesn't exist", warning: true })
    }
  }

  async function ptCertificate(certificate_No) {
    const applicationDetails = await Digit.PTService.search({ tenantId, filters: { propertyId: certificate_No } });
    const datapt = applicationDetails?.Properties[0];
    // console.log("applicatin pt certificate data ::", datapt)

    if (datapt.propertyId) {
      const ASSET_Req_data = dataCMObjectConverter(
        datapt?.owners[0]?.additionalDetails?.ownerName,
        getAddress(datapt),
        datapt.propertyId,
        convertEpochToDate(datapt?.owners[0]?.createdDate),
        "-NA-",
        datapt?.status
      );

      setUpdatedData([ASSET_Req_data]);
    } else {
      setShowToast({ label: "Application doesn't exist", warning: true })
    }
  }

  async function wsCertificate(certificate_No) {
    const applicationDetails = await Digit.WSService.WSWatersearch({ tenantId, filters: { applicationNumber: certificate_No } });
    const dataws = applicationDetails.WaterConnection[0];
    console.log("applicatin water certificate data ::", dataws);

    if (dataws.applicationNo) {
      const WS_Req_data = dataCMObjectConverter(
        dataws?.additionalDetails?.ownerName,
        "-NA-",
        dataws.applicationNo,
        convertEpochToDate(dataws?.auditDetails?.createdTime),
        "-NA-",
        dataws?.applicationStatus
      );

      setUpdatedData([WS_Req_data]);
    } else {
      setShowToast({ label: "Application doesn't exist", warning: true })
    }
  }

  async function swCertificate(certificate_No) {
    const applicationDetails = await Digit.WSService.WSSewsearch({ tenantId, filters: { applicationNumber: certificate_No } });
    const datasw = applicationDetails?.SewerageConnections[0];
    console.log("applicatin ws sewarage certificate data ::", datasw);

    if (datasw.applicationNo) {
      const SW_Req_data = dataCMObjectConverter(
        "-NA-",
        "-NA-",
        datasw.applicationNo,
        convertEpochToDate(datasw?.additionalDetails?.appCreatedDate),
        "-NA-",
        datasw?.applicationStatus
      );

      setUpdatedData([SW_Req_data]);
    } else {
      setShowToast({ label: "Application doesn't exist", warning: true })
    }
  }

  async function tlCertificate(certificate_No) {
    const applicationDetails = await Digit.TLService.TLsearch({ tenantId, filters: { applicationNumber: certificate_No } });
    const datatl = applicationDetails?.Licenses[0];
    console.log("trade license certificate data ::", datatl);

    if (datatl?.applicationNumber) {
      const TL_Req_data = dataCMObjectConverter(
        datatl?.tradeLicenseDetail?.owners[0]?.name,
        getAddress(datatl?.tradeLicenseDetail),
        datatl?.applicationNumber,
        convertEpochToDate(datatl?.applicationDate),
        convertEpochToDate(datatl?.validTo),
        datatl?.status
      );

      setUpdatedData([TL_Req_data]);
    } else {
      setShowToast({ label: "Application doesn't exist", warning: true })
    }
  }

  async function pgrCertificate(certificate_No) {
    console.log("pgr certificate no :: ", certificate_No)
    const applicationDetails = await Digit.PGRService.search(tenantId, { serviceRequestId: certificate_No });
    const datapgr = applicationDetails?.ServiceWrappers[0];
    // console.log("public grievance certificate data ::", datapgr);

    if (datapgr.service?.serviceRequestId) {
      const PGR_Req_data = dataCMObjectConverter(
        datapgr?.service?.citizen?.name,
        getAddress(datapgr?.service),
        datapgr.service?.serviceRequestId,
        convertEpochToDate(datapgr?.service?.auditDetails?.createdTime),
        "-NA-",
        datapgr?.service?.applicationStatus
      );

      setUpdatedData([PGR_Req_data]);
    } else {
      setShowToast({ label: "Application doesn't exist", warning: true })
    }
  }

  async function OBPSCertificate(certificate_No) {
    const applicationDetails = await Digit.OBPSService.BPASearch(tenantId, { applicationNo: certificate_No });
    const databpa = applicationDetails?.BPA[0];
    console.log("obps certificate data ::", applicationDetails);

    if (databpa?.applicationNo) {
      const OBPS_Req_data = dataCMObjectConverter(
        databpa?.landInfo?.owners?.name,
        getAddress(databpa?.landInfo),
        databpa?.applicationNo,
        convertEpochToDate(databpa?.landInfo?.owners?.auditDetails?.createdTime),
        convertEpochToDate(databpa?.landInfo?.owners?.pwdExpiryDate),
        databpa?.status
      );

      setUpdatedData([OBPS_Req_data]);
    } else {
      setShowToast({ label: "Application doesn't exist", warning: true })
    }
  }

  async function fsmCertificate(certificate_No) {
    const applicationDetails = await Digit.FSMService.search(tenantId, { applicationNos: certificate_No });
    const datafsm = applicationDetails?.fsm[0];
    console.log("fsm certificate data ::", datafsm);

    if (datafsm?.applicationNo) {
      const FSM_Req_data = dataCMObjectConverter(
        datafsm?.citizen?.name,
        getAddress(datafsm),
        datafsm?.applicationNo,
        convertEpochToDate(datafsm?.citizen?.createdDate),
        convertEpochToDate(datafsm?.citizen?.pwdExpiryDate),
        datafsm?.applicationStatus
      );
      setUpdatedData([FSM_Req_data]);
    } else {
      setShowToast({ label: "Application doesn't exist", warning: true })
    }
  }

  async function nocCertificate(certificate_No) {
    const applicationDetails = await Digit.NOCService.NOCsearch({ tenantId, filters: { applicationNo: certificate_No } });
    const datanoc = applicationDetails?.Noc[0];
    console.log("fire noc certificate data ::", datanoc);

    if (datanoc.applicationNo) {
      const NOC_Req_data = dataCMObjectConverter(
        datanoc?.additionalDetails?.applicantName,
        "-NA-",
        datanoc.applicationNo,
        "-NA-",
        "-NA-",
        datanoc?.applicationStatus
      );
      setUpdatedData([NOC_Req_data]);
    } else {
      setShowToast({ label: "Application doesn't exist", warning: true })
    }
  }

  async function petCertificate(certificate_No) {
    const applicationDetails = await Digit.PTRService.search({ tenantId, filters: { applicationNumber: certificate_No } });
    const datapet = applicationDetails?.PetRegistrationApplications[0];
    // console.log("pet certificate data ::", datapet);

    if (datapet?.applicationNumber) {
      const PTR_Req_data = dataCMObjectConverter(
        datapet?.applicantName,
        getAddress(datapet),
        datapet?.applicationNumber,
        "-NA-",
        datapet?.petDetails?.lastVaccineDate,
        "-NA-"
      );
      setUpdatedData([PTR_Req_data]);
    } else {
      setShowToast({ label: "Application doesn't exist", warning: true })
    }
  }

  async function assetCertificate(certificate_No) {
    // const applicationDetails = await Digit;
    // const dataasset = applicationDetails;
    // console.log("asset certificate data ::", dataasset);

    // ASSET_Req_data = dataCMObjectConverter(
    //   dataasset?.applicantDetail?.accountHolderName,
    //   getAddress(dataasset),
    //   convertEpochToDate(dataasset?.paymentDate),
    //   "-NA-",
    //   dataasset?.bookingStatus
    // );
    // setUpdatedData([ASSET_Req_data]);
  }

  async function BirthCertificate(certificate_No) {
    const applicationDetails = await Digit.BnDService.Bsearch({ tenantId, filters: { registrationNo: certificate_No } });
    const databirth = applicationDetails?.applications[0];
    console.log("birth certificate data ::", databirth);

    // Birth_Req_data = dataCMObjectConverter(
    //   dataasset?.applicantDetail?.accountHolderName,
    //   getAddress(dataasset),
    //   convertEpochToDate(dataasset?.paymentDate),
    //   "-NA-",
    //   dataasset?.bookingStatus
    // );
    // setUpdatedData([Birth_Req_data]);
  }

  async function DeathCertificate(certificate_No) {
    const applicationDetails = await Digit.BnDService.Dsearch({ tenantId, filters: {} });
    const datadeath = applicationDetails?.applications[0];
    console.log("death certificate data ::", datadeath);

    // ASSET_Req_data = dataCMObjectConverter(
    //   dataasset?.applicantDetail?.accountHolderName,
    //   getAddress(dataasset),
    //   convertEpochToDate(dataasset?.paymentDate),
    //   "-NA-",
    //   dataasset?.bookingStatus
    // );
    // setUpdatedData([ASSET_Req_data]);
  }

  function onSubmit() {
    switch (certificate_name.crtNo) {
      case 1:
        if (certificate_No) {
          ewCertificate(certificate_No);
        }
        break;

      case 2:
        if (certificate_No) {
          chbCertificate(certificate_No);
        }
        break;

      case 3:
        if (certificate_No) {
          ptCertificate(certificate_No);
        }
        break;

      case 4:
        if (certificate_No) {
          wsCertificate(certificate_No);
        }
        break;

      case 5:
        if (certificate_No) {
          swCertificate(certificate_No);
        }
        break;

      case 6:
        if (certificate_No) {
          tlCertificate(certificate_No);
        }
        break;

      case 7:
        if (certificate_No) {
          pgrCertificate(certificate_No);
        }
        break;

      case 8:
        if (certificate_No) {
          fsmCertificate(certificate_No);
        }
        break;

      case 9:
        if (certificate_No) {
          nocCertificate(certificate_No);
        }
        break;

      case 10:
        if (certificate_No) {
          OBPSCertificate(certificate_No);
        }
        break;

      case 11:
        if (certificate_No) {
          petCertificate(certificate_No);
        }
        break;

      case 12:
        if (certificate_No) {
          BirthCertificate(certificate_No);
        }
        break;

      case 13:
        if (certificate_No) {
          DeathCertificate(certificate_No);
        }
        break;

      case 14:
        if (certificate_No) {
          assetCertificate(certificate_No);
        }
        break;

      default:
        break;
    }

    setistable(true);
    resetCaptcha();
  }

  return (
    <React.Fragment>
      <div>
        <Header>{t("SEARCH_CERTIFICATE")}</Header>
        <Card className={"card-search-heading"}>
          <span style={{ color: "#505A5F" }}>{t("Provide at least one parameter to search for an application")}</span>
        </Card>
        <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
          <SearchField>
            <label>{t("CERTIFICATE_TYPE")}</label>
            <Controller
              control={control}
              name="certificateType"
              render={(props) => (
                <Dropdown
                  selected={certificate_name}
                  select={setCertificate_name}
                  onBlur={props.onBlur}
                  option={certificateTypes}
                  optionKey="i18nKey"
                  t={t}
                  disable={false}
                  placeholder={"Please type and select the certificate type"}
                />
              )}
            />
          </SearchField>
          <SearchField>
            <label>{t("CERTIFICATE_NUMBER")}</label>
            <TextInput
              name="certificateNo"
              t={t}
              type={"text"}
              optionKey="i18nKey"
              placeholder={"Please enter unique certificate number"}
              value={certificate_No}
              onChange={setcertificate_No}
              style={{ width: "86%" }}
            />
          </SearchField>
          <SearchField>
            <HCaptcha
              ref={captcha}
              sitekey="51424344-c730-4ac8-beec-0aca56be0754"
              onVerify={(token, ekey) =>
                //  handleVerificationSuccess(token, ekey)
                setToken(token)
              }
              onExpire={(e) => setToken("")}
            />
          </SearchField>

          <SearchField className="ssecondubmit">
            <SubmitBar
              label={t("ES_COMMON_SEARCH")}
              submit
              disabled={!ishuman}
            />
            <p
              style={{ marginTop: "10px" }}
              onClick={() => {
                reset({
                  applicationNo: "",
                  fromDate: today,
                  toDate: today,
                  status: "",
                  offset: 0,
                  limit: 10,
                  sortBy: "commencementDate",
                  sortOrder: "DESC",
                });
                setShowToast(null);
                setistable(false);
                setIshuman(false);
                setCertificate_name("");
                setCertificate_No("");
                resetCaptcha();
              }}
            >
              {t(`ES_COMMON_CLEAR_ALL`)}
            </p>
          </SearchField>
        </SearchForm>
        {/* {!isLoading && data?.display ? (
          <Card style={{ marginTop: 20 }}>
            {t(data.display)
              .split("\\n")
              .map((text, index) => (
                <p key={index} style={{ textAlign: "center" }}>
                  {text}
                </p>
              ))}
          </Card>
        ) : !isLoading && data !== "" ? ( */}
        {istable && (
          <Table
            t={t}
            data={updatedData}
            // totalRecords={count}
            columns={columns}
            getCellProps={(cellInfo) => {
              return {
                style: {
                  minWidth: cellInfo.column.Header === t("CM_INBOX_APPLICATION_NO") ? "240px" : "",
                  padding: "20px 18px",
                  fontSize: "16px",
                },
              };
            }}
            isPaginationRequired={false}
          />
        )}

    {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          isDleteBtn={true}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}

        {/* ) : (
          data !== "" || (isLoading && <Loader />)
        )} */}
      </div>
    </React.Fragment>
  );
};

export default CMSearchCertificate;