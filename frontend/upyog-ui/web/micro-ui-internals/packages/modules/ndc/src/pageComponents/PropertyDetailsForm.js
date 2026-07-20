import React, { useState, useEffect, useMemo, useCallback, useRef, use } from "react";
import {
  CardLabel,
  LabelFieldPair,
  TextInput,
  Loader,
  Toast,
  DeleteIcon,
  Table
} from "@nudmcdgnpm/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import { useSelector } from "react-redux";
import { useTranslation } from "react-i18next";
import _ from "lodash";

export const PropertyDetailsForm = ({ config, onSelect, userType, formData, formState, clearErrors }) => {
  const { control, formState: localFormState, watch, setValue, trigger, getValues } = useForm();
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const apiDataCheck = useSelector((state) => state.ndc.NDCForm?.formData?.responseData);
  const checkApiDataCheck = useSelector((state) => state.ndc.NDCForm?.formData?.apiData);
  const cptFromRedux = useSelector((state) => state.ndc.NDCForm?.formData?.cpt);
  const [showToast, setShowToast] = useState(null);
  const [propertyLoader, setPropertyLoader] = useState(false);
  const [showPayModal, setShowPayModal] = useState(false);
  const [selectedBillData, setSelectedBillData] = useState({});
  const cpt = cptFromRedux || formData?.cpt;
  const propertyId = cpt?.details?.propertyId;
  const [propertyDetails, setPropertyDetails] = useState(formData?.PropertyDetails || {});
  const [selectedRow, setSelectedRow] = useState(null);


  const { isLoading: waterConnectionLoading, data: waterConnectionData, error: waterConnectionError } = Digit.Hooks.ws.useSearchWS({
    tenantId,
    filters: {
      searchType: "CONNECTION",
      propertyId: propertyId,
    },
    config: {
      enabled: !!propertyId, //  Only run if propertyId is defined
    },
    bussinessService: "WS",
    t,
  });

  const { isLoading: sewerageConnectionLoading, data: sewerageConnectionData, error: sewerageConnectionError } = Digit.Hooks.ws.useSearchWS({
    tenantId,
    filters: {
      searchType: "CONNECTION",
      propertyId: propertyId,
    },
    config: {
      enabled: !!propertyId, //  Only run if propertyId is defined
    },
    bussinessService: "SW",
    t,
  });

  const applicationFeeColumns = [
    {
      Header: t("TL_COMMON_TABLE_COL_OWN_NAME"),
      accessor: "name",
      Cell: ({ row }) => {
        return <div>{row?.original?.name}</div>;
      },
    },
    {
      Header: "",
      accessor: "amount",
      Cell: ({ row }) => {
        const rowId = row?.index;
        return (
          <input
            type="radio"
            name="applicationFee"
            checked={selectedRow?.uuid === row?.original?.uuid}
            onChange={() => {
              setSelectedRow(row?.original);
            }}
          />
        );
      },
    },
  ];

  useEffect(() => {
    const owner = cpt?.details?.owners?.[0];
    const ownerForName = cpt?.details?.owners || [];
    const ownerNames = ownerForName
      ?.map((owner) => owner?.name)
      ?.filter(Boolean)
      ?.join(", ");

    const ownerObj = selectedRow;

    const emailApi = apiDataCheck?.[0]?.owners?.[0]?.emailId;
    const firstName = ownerObj?.name;
   
    const email = ownerObj?.emailId || emailApi || "";
    const mobileNumber = ownerObj?.mobileNumber;
    const address = ownerObj?.permanentAddress;

    const combinedObject = {};
    if (firstName) combinedObject.firstName = firstName;
    // if (lastName) combinedObject.lastName = lastName;
    combinedObject.email = email;
    if (mobileNumber) combinedObject.mobileNumber = mobileNumber;
    if (address) combinedObject.address = address;
    combinedObject.propertyBillData = {
      isLoading: false,
      billData: formData?.PropertyDetails?.propertyBillData?.billData || {},
    };

    setPropertyDetails((prev) => {
      return {
        ...prev,
        ...combinedObject,
      };
    });
  }, [cpt?.details, apiDataCheck, selectedRow]);

  useEffect(() => {
    let waterConnection;
    if (apiDataCheck?.[0]?.NdcDetails) {
      const resData = apiDataCheck?.[0]?.NdcDetails?.filter((item) => {
        return item.businessService == "WS";
      });

      waterConnection =
        resData?.map((item) => ({
          connectionNo: item?.consumerCode,
          isEdit: false,
          billData: { totalAmount: 0 },
          isLoading: false,
        })) || [];
    } else {
      waterConnection =
        waterConnectionData?.map((item) => ({
          connectionNo: item?.connectionNo,
          isEdit: false,
          billData: {},
          isLoading: false,
        })) || [];
    }

    setPropertyDetails((prev) => ({ ...prev, waterConnection }));
  }, [waterConnectionData, apiDataCheck]);

  useEffect(() => {
    let sewerageConnection;
    if (apiDataCheck?.[0]?.NdcDetails) {
      const resData = apiDataCheck?.[0]?.NdcDetails?.filter((item) => {
        return item.businessService == "SW";
      });
      sewerageConnection =
        resData?.map((item) => ({
          connectionNo: item?.consumerCode,
          isEdit: false,
          billData: { totalAmount: 0 },
          isLoading: false,
        })) || [];
    } else {
      sewerageConnection =
        sewerageConnectionData?.map((item) => ({
          connectionNo: item?.connectionNo,
          isEdit: false,
          billData: {},
          isLoading: false,
        })) || [];
    }

    setPropertyDetails((prev) => ({ ...prev, sewerageConnection }));
  }, [sewerageConnectionData, apiDataCheck]);

  useEffect(() => {
    onSelect("PropertyDetails", propertyDetails, config);
  }, [propertyDetails]);

  function addWaterConnection() {
    setPropertyDetails((prev) => ({
      ...prev,
      waterConnection: [...(prev.waterConnection || []), { connectionNo: "", isEdit: true }],
    }));
  }

  function addSewerageConnection() {
    setPropertyDetails((prev) => ({
      ...prev,
      sewerageConnection: [...(prev.sewerageConnection || []), { connectionNo: "", isEdit: true }],
    }));
  }

  async function fetchBill(bussinessService, consumercodes, index) {
    if (bussinessService === "WS") {
      const updated = [...propertyDetails.waterConnection];
      updated[index].isLoading = true;
      setPropertyDetails((prev) => ({
        ...prev,
        waterConnection: updated,
      }));
    } else if (bussinessService === "SW") {
      const updated = [...propertyDetails.sewerageConnection];
      updated[index].isLoading = true;
      setPropertyDetails((prev) => ({
        ...prev,
        sewerageConnection: updated,
      }));
    } else if (bussinessService === "PT") {
      setPropertyLoader(true);
      let updated = { ...propertyDetails?.propertyBillData };
      updated.isLoading = true;
      setPropertyDetails((prev) => ({
        ...prev,
        propertyBillData: updated,
      }));
    }

    try {
      const result = await Digit.PaymentService.fetchBill(tenantId, {
        businessService: bussinessService,
        consumerCode: consumercodes,
      });
      setPropertyLoader(false);

      if (result?.Bill?.length > 0) {
        if (result?.Bill[0]?.totalAmount > 0) {
          setShowToast({ error: true, label: t("NDC_MESSAGE_DUES_FOUND_PLEASE_PAY") });
        } else {
          setShowToast({ error: false, label: t("NDC_MESSAGE_NO_DUES_FOUND") });
        }
        if (bussinessService === "WS") {
          const updated = [...propertyDetails.waterConnection];
          updated[index].billData = result?.Bill[0];
          updated[index].isLoading = false;
          setPropertyDetails((prev) => ({
            ...prev,
            waterConnection: updated,
          }));
        } else if (bussinessService === "SW") {
          const updated = [...propertyDetails.sewerageConnection];
          updated[index].billData = result?.Bill[0];
          updated[index].isLoading = false;
          setPropertyDetails((prev) => ({
            ...prev,
            sewerageConnection: updated,
          }));
        } else if (bussinessService === "PT") {
          let updated = { ...propertyDetails.propertyBillData };
          updated.billData = result?.Bill[0];
          updated.isLoading = false;
          setPropertyDetails((prev) => ({
            ...prev,
            propertyBillData: updated,
          }));
        }
      } else if (result?.Bill) {
        if (bussinessService === "WS") {
          const updated = [...propertyDetails.waterConnection];
          updated[index].billData = {
            totalAmount: 0,
          };
          updated[index].isLoading = false;
          setPropertyDetails((prev) => ({
            ...prev,
            waterConnection: updated,
          }));
          setShowToast({ error: false, label: t("NDC_NO_BILLS_FOUND_WS") });
        } else if (bussinessService === "SW") {
          const updated = [...propertyDetails.sewerageConnection];
          updated[index].billData = {
            totalAmount: 0,
          };
          updated[index].isLoading = false;
          setPropertyDetails((prev) => ({
            ...prev,
            sewerageConnection: updated,
          }));
          setShowToast({ error: false, label: t("NDC_NO_BILLS_FOUND_SW") });
        } else if (bussinessService === "PT") {
          let updated = { ...propertyDetails.propertyBillData };
          updated.isLoading = false;
          setPropertyDetails((prev) => ({
            ...prev,
            propertyBillData: updated,
          }));
          setShowToast({ error: false, label: t("NDC_NO_BILLS_FOUND_PROPERTY") });
        }
        // setError(t("No Bills Found For this consumer number"));
      } else {
        if (bussinessService === "WS") {
          const updated = [...propertyDetails.waterConnection];
          updated[index].isLoading = false;
          updated[index].billData = {
            totalAmount: 0,
          };
          setPropertyDetails((prev) => ({
            ...prev,
            waterConnection: updated,
          }));
          setShowToast({ error: false, label: t("NDC_NO_BILLS_FOUND_WS") });
        } else if (bussinessService === "SW") {
          const updated = [...propertyDetails.sewerageConnection];
          updated[index].isLoading = false;
          updated[index].billData = {
            totalAmount: 0,
          };
          setPropertyDetails((prev) => ({
            ...prev,
            sewerageConnection: updated,
          }));
          setShowToast({ error: false, label: t("NDC_NO_BILLS_FOUND_SW") });
        } else if (bussinessService === "PT") {
          let updated = { ...propertyDetails.propertyBillData };
          updated.isLoading = false;
          setPropertyDetails((prev) => ({
            ...prev,
            propertyBillData: updated,
          }));
          setShowToast({ error: false, label: t("NDC_NO_BILLS_FOUND_PROPERTY") });
        }
        // setError(t("Invalid Consumer Number"));
      }
    } catch (error) {
      setPropertyLoader(false);
      setShowToast({ error: true, label: t("NDC_MESSAGE_FETCH_FAILED") });
    }
  }

  const closeToast = () => {
    setShowToast(null);
    // setError("");
  };

  const PayWSBillModal = Digit?.ComponentRegistryService?.getComponent("PayWSBillModal");

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 3000); // auto close after 3 sec

      return () => clearTimeout(timer); // cleanup
    }
  }, [showToast]);

  useEffect(() => {
    if (!selectedRow && checkApiDataCheck?.Applications?.[0]) {
      const checkOwners = checkApiDataCheck.Applications[0].owners;
      const filterRow = checkOwners?.find((owner) => owner?.isPrimaryOwner);
      setSelectedRow(filterRow || null);
    }
  }, [checkApiDataCheck]);

  return (
    <div className="ndc-margin-bottom-16">
      {(cpt?.details || apiDataCheck?.[0]?.NdcDetails) && (
        <div>
          <LabelFieldPair className="ndc-margin-top-40">
            <CardLabel className="card-label-smaller ndc_card_labels">{`${t("NDC_WATER_CONNECTION")}`}</CardLabel>
            {waterConnectionLoading ? (
              <Loader />
            ) : (
              <div className="form-field ndc-form-field-column">
                {propertyDetails?.waterConnection?.map((item, index) => (
                  <div key={index} className="ndc_property_search ndc-flex-row-center-gap">
                    <Controller
                      key={index}
                      control={control}
                      name={`waterConnection[${index}]`}
                      defaultValue={item.connectionNo}
                      render={(props) => (
                        <TextInput
                          value={item.connectionNo}
                          onChange={(e) => {
                            // Update local state
                            const updated = [...propertyDetails.waterConnection];
                            updated[index] = { connectionNo: "", isEdit: true, billData: {}, isLoading: false };
                            updated[index].connectionNo = e.target.value;
                            setPropertyDetails((prev) => ({
                              ...prev,
                              waterConnection: updated,
                            }));
                            // Update react-hook-form
                            props.onChange(e.target.value);
                          }}
                          disabled={!item.isEdit}
                          onBlur={props.onBlur}
                        />
                      )}
                    />

                    {item.isLoading ? (
                      <Loader />
                    ) : (
                      <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
                        {!apiDataCheck?.[0]?.NdcDetails && item?.connectionNo && !item?.billData?.id && item?.billData?.totalAmount != 0 && (
                          <button
                            className="submit-bar"
                            type="button"
                            style={{ color: "white", fontSize: "13px" }}
                            onClick={() => {
                              fetchBill("WS", item.connectionNo, index);
                            }}
                          >
                            {`${t("CHECK_STATUS_WATER")}`}
                            {/* Check Status for Water */}
                          </button>
                        )}

                        {item?.connectionNo && item?.billData?.totalAmount > 0 && (
                          <button
                            className="submit-bar"
                            type="button"
                            style={{ color: "white" }}
                            onClick={() => {
                            }}
                          >
                            {`${t("PAY_DUES")}`}
                          </button>
                        )}

                        {item?.connectionNo && item?.billData?.totalAmount == 0 && <div style={{ color: "green" }}>{t("NO_DUES")}</div>}

                        {item?.isEdit && (
                          <button
                            onClick={() => {
                              let updated = [...propertyDetails.waterConnection];
                              updated.splice(index, 1);
                              setPropertyDetails((prev) => {
                                return {
                                  ...prev,
                                  waterConnection: updated,
                                };
                              });
                            }}
                          >
                            <DeleteIcon className="delete" fill="#a82227" style={{ cursor: "pointer", marginLeft: "20px" }} />
                          </button>
                        )}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </LabelFieldPair>

          <button
            className="submit-bar"
            type="button"
            style={{ color: "white", fontSize: "12px" }}
            onClick={() => {
              addWaterConnection();
            }}
          >
            {`${t("ADD_WATER")}`}
          </button>

          <LabelFieldPair style={{ marginTop: "40px" }}>
            <CardLabel className="card-label-smaller ndc_card_labels">{`${t("NDC_SEWERAGE_CONNECTION")} `}</CardLabel>
            {sewerageConnectionLoading ? (
              <Loader />
            ) : (
              <div className="form-field" style={{ display: "flex", flexDirection: "column", gap: "8px", width: "100%" }}>
                {propertyDetails?.sewerageConnection?.map((item, index) => (
                  <div
                    className="ndc_property_search"
                    key={index}
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                      gap: "16px",
                    }}
                  >
                    <Controller
                      key={index}
                      control={control}
                      name={`sewerageConnection[${index}]`}
                      defaultValue={item.connectionNo}
                      render={(field) => (
                        <TextInput
                          value={item.connectionNo}
                          onChange={(e) => {
                            // Update local state
                            const updated = [...propertyDetails.sewerageConnection];
                            updated[index] = { connectionNo: "", isEdit: true, billData: {}, isLoading: false };
                            updated[index].connectionNo = e.target.value;
                            setPropertyDetails((prev) => ({
                              ...prev,
                              sewerageConnection: updated,
                            }));

                            // Update react-hook-form
                            field.onChange(e.target.value);
                          }}
                          disabled={!item.isEdit}
                          onBlur={field.onBlur}
                        />
                      )}
                    />

                    {item.isLoading ? (
                      <Loader />
                    ) : (
                      <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
                        {!apiDataCheck?.[0]?.NdcDetails && item?.connectionNo && !item?.billData?.id && item?.billData?.totalAmount != 0 && (
                          <button
                            className="submit-bar"
                            type="button"
                            style={{ color: "white", fontSize: "13px" }}
                            onClick={() => {
                              fetchBill("SW", item.connectionNo, index, item.isEdit);
                            }}
                          >
                            {`${t("CHECK_STATUS_SEWERAGE")}`}
                            {/* Check Status for Sewerage */}
                          </button>
                        )}

                        {item?.connectionNo && item?.billData?.totalAmount > 0 && (
                          <button
                            className="submit-bar"
                            type="button"
                            style={{ color: "white" }}
                            onClick={() => {
                              // setSelectedBillData(item?.billData);
                              // setShowPayModal(true);
                            }}
                          >
                            {`${t("PAY_DUES")}`}
                          </button>
                        )}

                        {item?.connectionNo && item?.billData?.totalAmount == 0 && <div style={{ color: "green" }}>{t("NO_DUES")}</div>}

                        {item?.isEdit && (
                          <button
                            onClick={() => {
                              let updated = [...propertyDetails.sewerageConnection];
                              updated.splice(index, 1);
                              setPropertyDetails((prev) => {
                                return {
                                  ...prev,
                                  sewerageConnection: updated,
                                };
                              });
                            }}
                          >
                            <DeleteIcon className="delete" fill="#a82227" style={{ cursor: "pointer", marginLeft: "20px" }} />
                          </button>
                        )}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </LabelFieldPair>

          <button
            className="submit-bar"
            type="button"
            style={{ color: "white", fontSize: " 12px" }}
            onClick={() => {
              addSewerageConnection();
            }}
          >
            {`${t("ADD_SEWERAGE")}`}
          </button>

          <div style={{ marginTop: "40px", marginBottom: "20px" }}>
            <p style={{ color: " green", fontSize: "14px", paddingBottom: "10px" }}>
              Please Select One Owner Name Who will have the access of No Due Certificate
            </p>
            <Table
              className="customTable table-border-style"
              t={t}
              data={cpt?.details?.owners || []}
              columns={applicationFeeColumns}
              getCellProps={() => ({ style: {} })}
              disableSort={true}
              manualPagination={false}
              isPaginationRequired={false}
            />
          </div>

          {selectedRow && (
            <div>
              {/* name */}
              <LabelFieldPair style={{ marginTop: "40px" }}>
                <CardLabel className="card-label-smaller ndc_card_labels">{`${t("NDC_FULL_NAME")} * `}</CardLabel>
                <div className="form-field">
                  <Controller
                    control={control}
                    name={"firstName"}
                    defaultValue={propertyDetails?.firstName || ""}
                    rules={{
                      required: t("REQUIRED_FIELD"),
                      validate: { pattern: (val) => (/^[-@.\/#&+\w\s]*$/.test(val) ? true : t("INVALID_NAME")) },
                    }}
                    render={(props) => (
                      <TextInput
                        value={propertyDetails?.firstName || ""}
                        onChange={(e) => {
                          setPropertyDetails((prev) => ({ ...prev, firstName: e.target.value }));
                          props.onChange(e.target.value);
                        }}
                        onBlur={(e) => {
                          // setFocusIndex({ index: -1 });
                          props.onBlur(e);
                        }}
                        disabled={!cpt?.details?.owners?.[0]?.name?.split(" ")?.[0]?.length > 0}
                      />
                    )}
                  />
                </div>
              </LabelFieldPair>

              {/* email */}
              <LabelFieldPair>
                <CardLabel className="card-label-smaller ndc_card_labels">{`${t("NDC_EMAIL")} * `}</CardLabel>
                <div className="form-field">
                  <Controller
                    control={control}
                    name={"email"}
                    defaultValue={propertyDetails?.email || ""}
                    render={(props) => (
                      <TextInput
                        value={propertyDetails?.email}
                        onChange={(e) => {
                          setPropertyDetails((prev) => ({ ...prev, email: e.target.value }));
                          props.onChange(e.target.value);
                        }}
                        onBlur={(e) => {
                          // setFocusIndex({ index: -1 });
                          props.onBlur(e);
                        }}
                        // disabled={formData?.cpt?.details?.owners?.[0]?.emailId}
                      />
                    )}
                  />
                </div>
              </LabelFieldPair>

              {/* mobile number */}
              <LabelFieldPair>
                <CardLabel className="card-label-smaller ndc_card_labels">{`${t("NDC_MOBILE_NUMBER")} * `}</CardLabel>
                <div className="form-field">
                  <Controller
                    control={control}
                    name={"mobileNumber"}
                    defaultValue={propertyDetails?.mobileNumber || ""}
                    render={(props) => (
                      <TextInput
                        value={propertyDetails?.mobileNumber}
                        onChange={(e) => {
                          setPropertyDetails((prev) => ({ ...prev, mobileNumber: e.target.value }));
                          props.onChange(e.target.value);
                        }}
                        onBlur={(e) => {
                          // setFocusIndex({ index: -1 });
                          props.onBlur(e);
                        }}
                        disabled={cpt?.details?.owners?.[0]?.mobileNumber}
                      />
                    )}
                  />
                </div>
              </LabelFieldPair>

              {/* address */}
              <LabelFieldPair>
                <CardLabel className="card-label-smaller ndc_card_labels">{`${t("NDC_ADDRESS")} * `}</CardLabel>
                <div className="form-field">
                  <Controller
                    control={control}
                    name={"address"}
                    defaultValue={propertyDetails?.address || ""}
                    render={(props) => (
                      <TextInput
                        value={propertyDetails?.address}
                        onChange={(e) => {
                          setPropertyDetails((prev) => ({ ...prev, address: e.target.value }));
                          props.onChange(e.target.value);
                        }}
                        onBlur={(e) => {
                          // setFocusIndex({ index: -1 });
                          props.onBlur(e);
                        }}
                        disabled={cpt?.details?.owners?.[0]?.permanentAddress?.length > 0}
                      />
                    )}
                  />
                </div>
              </LabelFieldPair>
            </div>
          )}
        </div>
      )}

      {/* remarks */}
      <LabelFieldPair>
        <CardLabel className="card-label-smaller ndc_card_labels">{`${t("Remarks")}`}</CardLabel>
        <div className="form-field">
          <Controller
            control={control}
            name={"remarks"}
            defaultValue={propertyDetails?.remarks || ""}
            rules={{
              required: t("REQUIRED_FIELD"),
            }}
            render={(props) => (
              <TextInput
                value={propertyDetails?.remarks || ""}
                onChange={(e) => {
                  setPropertyDetails((prev) => ({ ...prev, remarks: e.target.value }));
                  props.onChange(e.target.value);
                }}
                onBlur={(e) => {
                  // setFocusIndex({ index: -1 });
                  props.onBlur(e);
                }}
              />
            )}
          />
        </div>
      </LabelFieldPair>
      {/* tl number */}
      <LabelFieldPair>
        <CardLabel className="card-label-smaller ndc_card_labels">{`${t("NDC_TL_NUMBER")}`}</CardLabel>
        <div className="form-field">
          <Controller
            control={control}
            name={"tlNumber"}
            defaultValue={propertyDetails?.tlNumber || ""}
            render={(props) => (
              <TextInput
                value={propertyDetails?.tlNumber}
                onChange={(e) => {
                  setPropertyDetails((prev) => ({ ...prev, tlNumber: e.target.value }));
                  props.onChange(e.target.value);
                }}
                onBlur={(e) => {
                  // setFocusIndex({ index: -1 });
                  props.onBlur(e);
                }}
              />
            )}
          />
        </div>
      </LabelFieldPair>
      {showToast && <Toast isDleteBtn={true} error={showToast?.error} label={showToast?.label} onClose={closeToast} />}
      {showPayModal && (
        <PayWSBillModal
          setShowToast={() => {
            setShowPayModal(false);
            setSelectedBillData({});
          }}
          billData={selectedBillData}
        />
      )}
    </div>
  );
};
