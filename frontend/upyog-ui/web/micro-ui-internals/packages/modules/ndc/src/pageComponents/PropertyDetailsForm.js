import React, { useEffect, useState } from "react";
import { CardLabel, LabelFieldPair, TextInput, Loader, DeleteIcon, Table } from "@nudmcdgnpm/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import { useSelector } from "react-redux";
import { useTranslation } from "react-i18next";
import "../../css/ndc.css";

export const PropertyDetailsForm = ({ config, onSelect, userType, formData, formState, clearErrors }) => {
  const { control } = useForm({
    mode: "onChange",
  });

  const { t } = useTranslation();

  const tenantId = Digit.ULBService.getCurrentTenantId();

  const apiDataCheck = useSelector((state) => state.ndc.NDCForm?.formData?.responseData);

  const checkApiDataCheck = useSelector((state) => state.ndc.NDCForm?.formData?.apiData);

  const cptFromRedux = useSelector((state) => state.ndc.NDCForm?.formData?.cpt);

  const cpt = cptFromRedux || formData?.cpt;

  const propertyId = cpt?.details?.propertyId;

  const [showToast, setShowToast] = useState(null);
  const [propertyLoader, setPropertyLoader] = useState(false);
  const [showPayModal, setShowPayModal] = useState(false);
  const [selectedBillData, setSelectedBillData] = useState({});
  const [propertyDetails, setPropertyDetails] = useState(formData?.PropertyDetails || {});
  const [selectedRow, setSelectedRow] = useState(null);

  const { isLoading: waterConnectionLoading, data: waterConnectionData } = Digit.Hooks.ws.useSearchWS({
    tenantId,
    filters: {
      searchType: "CONNECTION",
      propertyId: propertyId,
    },
    config: {
      enabled: !!propertyId,
    },
    bussinessService: "WS",
    t,
  });

  const { isLoading: sewerageConnectionLoading, data: sewerageConnectionData } = Digit.Hooks.ws.useSearchWS({
    tenantId,
    filters: {
      searchType: "CONNECTION",
      propertyId: propertyId,
    },
    config: {
      enabled: !!propertyId,
    },
    bussinessService: "SW",
    t,
  });

  /*
   * Inline styles are kept here so the owner section remains
   * exactly aligned with the reference UI without changing
   * any form functionality.
   */
  const ownerFieldStyle = {
    display: "flex",
    flexDirection: "column",
    alignItems: "flex-start",
    justifyContent: "flex-start",
    width: "100%",
    margin: "18px 0 16px 0",
    padding: 0,
    boxSizing: "border-box",
  };

  const ownerLabelStyle = {
    display: "block",
    width: "100%",
    margin: "0 0 6px 0",
    padding: 0,
    textAlign: "left",
    fontSize: "10px",
    fontWeight: 700,
    lineHeight: "14px",
    boxSizing: "border-box",
  };

  const ownerFieldContainerStyle = {
    width: "300px",
    minWidth: "300px",
    maxWidth: "300px",
    margin: 0,
    padding: 0,
    boxSizing: "border-box",
  };

  const ownerInputStyle = {
    width: "300px",
    minWidth: "300px",
    maxWidth: "300px",
    height: "26px",
    minHeight: "26px",
    padding: "3px 8px",
    fontSize: "12px",
    lineHeight: "18px",
    boxSizing: "border-box",
  };

  const ownerTableStyle = {
    width: "372px",
    minWidth: "372px",
    maxWidth: "372px",
  };

  const applicationFeeColumns = [
    {
      Header: t("TL_COMMON_TABLE_COL_OWN_NAME"),
      accessor: "name",
      Cell: ({ row }) => <div>{row?.original?.name}</div>,
    },
    {
      Header: "",
      accessor: "amount",
      Cell: ({ row }) => (
        <input
          type="radio"
          name="applicationFee"
          checked={selectedRow?.uuid === row?.original?.uuid}
          onChange={() => setSelectedRow(row?.original)}
        />
      ),
    },
  ];

  useEffect(() => {
    const owner = cpt?.details?.owners?.[0];
    const ownerObj = selectedRow;
    const emailApi = apiDataCheck?.[0]?.owners?.[0]?.emailId;

    const firstName = ownerObj?.name || owner?.name || "";
    const email = ownerObj?.emailId || emailApi || owner?.emailId || "";
    const mobileNumber = ownerObj?.mobileNumber || owner?.mobileNumber || "";
    const address = ownerObj?.permanentAddress || owner?.permanentAddress || "";

    const updated = {
      email,
      propertyBillData: {
        isLoading: false,
        billData: formData?.PropertyDetails?.propertyBillData?.billData || {},
      },
    };

    if (firstName) {
      updated.firstName = firstName;
    }

    if (mobileNumber) {
      updated.mobileNumber = mobileNumber;
    }

    if (address) {
      updated.address = address;
    }

    setPropertyDetails((prev) => ({
      ...prev,
      ...updated,
    }));
  }, [cpt?.details, apiDataCheck, selectedRow, formData?.PropertyDetails]);

  useEffect(() => {
    let waterConnection = [];

    if (apiDataCheck?.[0]?.NdcDetails) {
      waterConnection =
        apiDataCheck?.[0]?.NdcDetails?.filter((item) => item.businessService === "WS")?.map((item) => ({
          connectionNo: item?.consumerCode,
          isEdit: false,
          billData: {
            totalAmount: 0,
          },
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

    setPropertyDetails((prev) => ({
      ...prev,
      waterConnection,
    }));
  }, [waterConnectionData, apiDataCheck]);

  useEffect(() => {
    let sewerageConnection = [];

    if (apiDataCheck?.[0]?.NdcDetails) {
      sewerageConnection =
        apiDataCheck?.[0]?.NdcDetails?.filter((item) => item.businessService === "SW")?.map((item) => ({
          connectionNo: item?.consumerCode,
          isEdit: false,
          billData: {
            totalAmount: 0,
          },
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

    setPropertyDetails((prev) => ({
      ...prev,
      sewerageConnection,
    }));
  }, [sewerageConnectionData, apiDataCheck]);

  useEffect(() => {
    if (typeof onSelect === "function") {
      onSelect("PropertyDetails", propertyDetails, config);
    }
  }, [propertyDetails]);

  function addWaterConnection() {
    setPropertyDetails((prev) => ({
      ...prev,
      waterConnection: [
        ...(prev.waterConnection || []),
        {
          connectionNo: "",
          isEdit: true,
          billData: {},
          isLoading: false,
        },
      ],
    }));
  }

  function addSewerageConnection() {
    setPropertyDetails((prev) => ({
      ...prev,
      sewerageConnection: [
        ...(prev.sewerageConnection || []),
        {
          connectionNo: "",
          isEdit: true,
          billData: {},
          isLoading: false,
        },
      ],
    }));
  }

  async function fetchBill(bussinessService, consumercodes, index) {
    try {
      if (bussinessService === "WS") {
        const updated = [...(propertyDetails?.waterConnection || [])];

        if (!updated[index]) {
          return;
        }

        updated[index] = {
          ...updated[index],
          isLoading: true,
        };

        setPropertyDetails((prev) => ({
          ...prev,
          waterConnection: updated,
        }));
      }

      if (bussinessService === "SW") {
        const updated = [...(propertyDetails?.sewerageConnection || [])];

        if (!updated[index]) {
          return;
        }

        updated[index] = {
          ...updated[index],
          isLoading: true,
        };

        setPropertyDetails((prev) => ({
          ...prev,
          sewerageConnection: updated,
        }));
      }

      if (bussinessService === "PT") {
        setPropertyLoader(true);
      }

      const result = await Digit.PaymentService.fetchBill(tenantId, {
        businessService: bussinessService,
        consumerCode: consumercodes,
      });

      if (result?.Bill?.length > 0) {
        const bill = result.Bill[0];

        if (bussinessService === "WS") {
          const updated = [...(propertyDetails?.waterConnection || [])];

          if (updated[index]) {
            updated[index] = {
              ...updated[index],
              billData: bill,
              isLoading: false,
            };
          }

          setPropertyDetails((prev) => ({
            ...prev,
            waterConnection: updated,
          }));

          if (Number(bill?.totalAmount) > 0) {
            setShowToast({
              error: true,
              label: t("NDC_MESSAGE_DUES_FOUND_PLEASE_PAY"),
            });
          } else {
            setShowToast({
              error: false,
              label: t("NDC_NO_BILLS_FOUND_WS"),
            });
          }

          return;
        }

        if (bussinessService === "SW") {
          const updated = [...(propertyDetails?.sewerageConnection || [])];

          if (updated[index]) {
            updated[index] = {
              ...updated[index],
              billData: bill,
              isLoading: false,
            };
          }

          setPropertyDetails((prev) => ({
            ...prev,
            sewerageConnection: updated,
          }));

          if (Number(bill?.totalAmount) > 0) {
            setShowToast({
              error: true,
              label: t("NDC_MESSAGE_DUES_FOUND_PLEASE_PAY"),
            });
          } else {
            setShowToast({
              error: false,
              label: t("NDC_NO_BILLS_FOUND_SW"),
            });
          }

          return;
        }

        if (bussinessService === "PT") {
          setPropertyDetails((prev) => ({
            ...prev,
            propertyBillData: {
              ...(prev.propertyBillData || {}),
              billData: bill,
              isLoading: false,
            },
          }));

          setPropertyLoader(false);

          if (Number(bill?.totalAmount) > 0) {
            setShowToast({
              error: true,
              label: t("NDC_MESSAGE_DUES_FOUND_PLEASE_PAY"),
            });
          } else {
            setShowToast({
              error: false,
              label: t("NDC_NO_BILLS_FOUND_PROPERTY"),
            });
          }

          return;
        }
      }

      if (result?.Bill) {
        if (bussinessService === "WS") {
          const updated = [...(propertyDetails?.waterConnection || [])];

          if (!updated[index]) {
            return;
          }

          updated[index] = {
            ...updated[index],
            billData: {
              totalAmount: 0,
            },
            isLoading: false,
          };

          setPropertyDetails((prev) => ({
            ...prev,
            waterConnection: updated,
          }));

          setShowToast({
            error: false,
            label: t("NDC_NO_BILLS_FOUND_WS"),
          });

          return;
        }

        if (bussinessService === "SW") {
          const updated = [...(propertyDetails?.sewerageConnection || [])];

          if (!updated[index]) {
            return;
          }

          updated[index] = {
            ...updated[index],
            billData: {
              totalAmount: 0,
            },
            isLoading: false,
          };

          setPropertyDetails((prev) => ({
            ...prev,
            sewerageConnection: updated,
          }));

          setShowToast({
            error: false,
            label: t("NDC_NO_BILLS_FOUND_SW"),
          });

          return;
        }

        if (bussinessService === "PT") {
          setPropertyDetails((prev) => ({
            ...prev,
            propertyBillData: {
              ...(prev.propertyBillData || {}),
              isLoading: false,
              billData: {
                totalAmount: 0,
              },
            },
          }));

          setPropertyLoader(false);

          setShowToast({
            error: false,
            label: t("NDC_NO_BILLS_FOUND_PROPERTY"),
          });

          return;
        }
      }

      if (bussinessService === "WS") {
        const updated = [...(propertyDetails?.waterConnection || [])];

        if (!updated[index]) {
          return;
        }

        updated[index] = {
          ...updated[index],
          isLoading: false,
          billData: {
            totalAmount: 0,
          },
        };

        setPropertyDetails((prev) => ({
          ...prev,
          waterConnection: updated,
        }));

        setShowToast({
          error: false,
          label: t("NDC_NO_BILLS_FOUND_WS"),
        });
      }

      if (bussinessService === "SW") {
        const updated = [...(propertyDetails?.sewerageConnection || [])];

        if (!updated[index]) {
          return;
        }

        updated[index] = {
          ...updated[index],
          isLoading: false,
          billData: {
            totalAmount: 0,
          },
        };

        setPropertyDetails((prev) => ({
          ...prev,
          sewerageConnection: updated,
        }));

        setShowToast({
          error: false,
          label: t("NDC_NO_BILLS_FOUND_SW"),
        });
      }

      if (bussinessService === "PT") {
        setPropertyDetails((prev) => ({
          ...prev,
          propertyBillData: {
            ...(prev.propertyBillData || {}),
            isLoading: false,
            billData: {
              totalAmount: 0,
            },
          },
        }));

        setPropertyLoader(false);

        setShowToast({
          error: false,
          label: t("NDC_NO_BILLS_FOUND_PROPERTY"),
        });
      }
    } catch (error) {
      console.error("Error while fetching bill:", error);

      setPropertyLoader(false);

      if (bussinessService === "WS") {
        const updated = [...(propertyDetails?.waterConnection || [])];

        if (updated[index]) {
          updated[index] = {
            ...updated[index],
            isLoading: false,
          };
        }

        setPropertyDetails((prev) => ({
          ...prev,
          waterConnection: updated,
        }));
      }

      if (bussinessService === "SW") {
        const updated = [...(propertyDetails?.sewerageConnection || [])];

        if (updated[index]) {
          updated[index] = {
            ...updated[index],
            isLoading: false,
          };
        }

        setPropertyDetails((prev) => ({
          ...prev,
          sewerageConnection: updated,
        }));
      }

      setShowToast({
        error: true,
        label: t("NDC_MESSAGE_FETCH_FAILED"),
      });
    }
  }

  const PayWSBillModal = Digit?.ComponentRegistryService?.getComponent("PayWSBillModal");

  useEffect(() => {
    if (!showToast) {
      return;
    }

    const timer = setTimeout(() => {
      setShowToast(null);
    }, 3000);

    return () => clearTimeout(timer);
  }, [showToast]);

  useEffect(() => {
    if (!selectedRow && checkApiDataCheck?.Applications?.[0]) {
      const owners = checkApiDataCheck?.Applications?.[0]?.owners;

      const primaryOwner = owners?.find((owner) => owner?.isPrimaryOwner);

      if (primaryOwner) {
        setSelectedRow(primaryOwner);
      }
    }
  }, [checkApiDataCheck, selectedRow]);

  const renderConnectionRows = (connectionType, connections, loading, labelKey, checkStatusKey, addHandler) => {
    return (
      <>
        <div className="ndc-connection-section">
          <div className="ndc-field-label-wrapper">
            <CardLabel className="card-label-smaller ndc_card_labels">{t(labelKey)}</CardLabel>
          </div>

          {loading ? (
            <Loader />
          ) : (
            <div className="ndc-connection-list">
              {connections?.map((item, index) => (
                <div key={index} className="ndc-connection-row">
                  <Controller
                    control={control}
                    name={`${connectionType}[${index}]`}
                    defaultValue={item?.connectionNo || ""}
                    render={({ field }) => (
                      <TextInput
                        value={item?.connectionNo || ""}
                        onChange={(e) => {
                          const value = e.target.value;

                          const updated = [...(connections || [])];

                          updated[index] = {
                            ...updated[index],
                            connectionNo: value,
                          };

                          setPropertyDetails((prev) => ({
                            ...prev,
                            [connectionType]: updated,
                          }));

                          field.onChange(value);
                        }}
                        onBlur={field.onBlur}
                        disabled={!item?.isEdit}
                        className="ndc-input"
                      />
                    )}
                  />

                  {item?.isLoading ? (
                    <Loader />
                  ) : (
                    <div className="ndc-action-column">
                      {!apiDataCheck?.[0]?.NdcDetails && item?.connectionNo && !item?.billData?.id && item?.billData?.totalAmount !== 0 && (
                        <button
                          className="submit-bar ndc-action-button"
                          onClick={() => fetchBill(connectionType === "waterConnection" ? "WS" : "SW", item.connectionNo, index)}
                        >
                          {t(checkStatusKey)}
                        </button>
                      )}

                      {item?.connectionNo && item?.billData?.totalAmount > 0 && (
                        <button
                          className="submit-bar ndc-action-button"
                          onClick={() => {
                            setSelectedBillData(item?.billData);
                            setShowPayModal(true);
                          }}
                        >
                          {t("PAY_DUES")}
                        </button>
                      )}

                      {item?.connectionNo && item?.billData?.totalAmount === 0 && <div className="ndc-no-dues-box">{t("NO_DUES")}</div>}

                      {item?.isEdit && (
                        <button
                          className="ndc-delete-button"
                          onClick={() => {
                            const updated = [...(connections || [])];

                            updated.splice(index, 1);

                            setPropertyDetails((prev) => ({
                              ...prev,
                              [connectionType]: updated,
                            }));
                          }}
                        >
                          <DeleteIcon className="delete" fill="#a82227" />
                        </button>
                      )}
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}

          <div className="ndc-add-connection">
            <button className="submit-bar ndc-action-button" onClick={addHandler}>
              {t(connectionType === "waterConnection" ? "ADD_WATER" : "ADD_SEWERAGE")}
            </button>
          </div>
        </div>
      </>
    );
  };

  return (
    <div className="ndc-margin-bottom-16 ndc-form-root">
      <div className="ndc-details-form-wrapper">
        {(cpt?.details || apiDataCheck?.[0]?.NdcDetails) && (
          <div>
            {renderConnectionRows(
              "waterConnection",
              propertyDetails?.waterConnection,
              waterConnectionLoading,
              "NDC_WATER_CONNECTION",
              "CHECK_STATUS_WATER",
              addWaterConnection,
            )}

            <div className="ndc-sewerage-section">
              <div className="ndc-field-label-wrapper">
                <CardLabel className="card-label-smaller ndc_card_labels">{t("NDC_SEWERAGE_CONNECTION")}</CardLabel>
              </div>

              {sewerageConnectionLoading ? (
                <Loader />
              ) : (
                <div className="ndc-connection-list">
                  {propertyDetails?.sewerageConnection?.map((item, index) => (
                    <div key={index} className="ndc-connection-row">
                      <Controller
                        control={control}
                        name={`sewerageConnection[${index}]`}
                        defaultValue={item?.connectionNo || ""}
                        render={({ field }) => (
                          <TextInput
                            value={item?.connectionNo || ""}
                            onChange={(e) => {
                              const value = e.target.value;

                              const updated = [...(propertyDetails?.sewerageConnection || [])];

                              updated[index] = {
                                ...updated[index],
                                connectionNo: value,
                              };

                              setPropertyDetails((prev) => ({
                                ...prev,
                                sewerageConnection: updated,
                              }));

                              field.onChange(value);
                            }}
                            onBlur={field.onBlur}
                            disabled={!item?.isEdit}
                            className="ndc-input"
                          />
                        )}
                      />

                      {item?.isLoading ? (
                        <Loader />
                      ) : (
                        <div className="ndc-action-column">
                          {!apiDataCheck?.[0]?.NdcDetails && item?.connectionNo && !item?.billData?.id && item?.billData?.totalAmount !== 0 && (
                            <button className="submit-bar ndc-action-button" onClick={() => fetchBill("SW", item.connectionNo, index)}>
                              {t("CHECK_STATUS_SEWERAGE")}
                            </button>
                          )}

                          {item?.connectionNo && item?.billData?.totalAmount > 0 && (
                            <button
                              className="submit-bar ndc-action-button"
                              onClick={() => {
                                setSelectedBillData(item?.billData);
                                setShowPayModal(true);
                              }}
                            >
                              {t("PAY_DUES")}
                            </button>
                          )}

                          {item?.connectionNo && item?.billData?.totalAmount === 0 && <div className="ndc-no-dues-box">{t("NO_DUES")}</div>}

                          {item?.isEdit && (
                            <button
                              className="ndc-delete-button"
                              onClick={() => {
                                const updated = [...(propertyDetails?.sewerageConnection || [])];

                                updated.splice(index, 1);

                                setPropertyDetails((prev) => ({
                                  ...prev,
                                  sewerageConnection: updated,
                                }));
                              }}
                            >
                              <DeleteIcon className="delete" fill="#a82227" />
                            </button>
                          )}
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="ndc-add-sewerage">
              <button className="submit-bar ndc-action-button" onClick={addSewerageConnection}>
                {t("ADD_SEWERAGE")}
              </button>
            </div>

            <div className="ndc-owner-selection">
              <p className="ndc-owner-help">{t("NDC_OWNER_ACCESS_MESSAGE")}</p>

              <div className="ndc-owner-table" style={ownerTableStyle}>
                <Table
                  className="customTable table-border-style"
                  t={t}
                  data={cpt?.details?.owners || []}
                  columns={applicationFeeColumns}
                  getCellProps={() => ({})}
                  disableSort={true}
                  manualPagination={false}
                  isPaginationRequired={false}
                />
              </div>
            </div>

            {selectedRow && (
              <div>
                {/* FULL NAME */}
                <LabelFieldPair className="ndc-owner-field ndc-owner-field-first" style={ownerFieldStyle}>
                  <CardLabel className="card-label-smaller ndc_card_labels" style={ownerLabelStyle}>
                    {t("NDC_FULL_NAME")}
                    <span className="ndc-required-star"> *</span>
                  </CardLabel>

                  <div className="form-field ndc-field-container" style={ownerFieldContainerStyle}>
                    <Controller
                      control={control}
                      name="firstName"
                      defaultValue={propertyDetails?.firstName || ""}
                      rules={{
                        required: t("REQUIRED_FIELD"),
                        validate: {
                          pattern: (value) => {
                            if (!value || String(value).trim() === "") {
                              return t("REQUIRED_FIELD");
                            }

                            return /^[-@.\/#&+\w\s]*$/.test(String(value)) ? true : t("INVALID_NAME");
                          },
                        },
                      }}
                      render={({ field }) => (
                        <TextInput
                          value={propertyDetails?.firstName || ""}
                          onChange={(e) => {
                            const value = e.target.value;

                            setPropertyDetails((prev) => ({
                              ...prev,
                              firstName: value,
                            }));

                            field.onChange(value);
                          }}
                          onBlur={field.onBlur}
                          disabled={!!cpt?.details?.owners?.[0]?.name}
                          className="ndc-input"
                          style={ownerInputStyle}
                        />
                      )}
                    />
                  </div>
                </LabelFieldPair>

                {/* EMAIL */}
                <LabelFieldPair className="ndc-owner-field" style={ownerFieldStyle}>
                  <CardLabel className="card-label-smaller ndc_card_labels" style={ownerLabelStyle}>
                    {t("NDC_EMAIL")}
                    <span className="ndc-required-star"> *</span>
                  </CardLabel>

                  <div className="form-field ndc-field-container" style={ownerFieldContainerStyle}>
                    <Controller
                      control={control}
                      name="email"
                      defaultValue={propertyDetails?.email || ""}
                      rules={{
                        required: t("REQUIRED_FIELD"),
                        validate: {
                          pattern: (value) => {
                            if (!value || String(value).trim() === "") {
                              return t("REQUIRED_FIELD");
                            }

                            return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(value).trim()) ? true : t("INVALID_EMAIL");
                          },
                        },
                      }}
                      render={({ field }) => (
                        <TextInput
                          value={propertyDetails?.email || ""}
                          onChange={(e) => {
                            const value = e.target.value;

                            setPropertyDetails((prev) => ({
                              ...prev,
                              email: value,
                            }));

                            field.onChange(value);
                          }}
                          onBlur={field.onBlur}
                          className="ndc-input"
                          style={ownerInputStyle}
                        />
                      )}
                    />
                  </div>
                </LabelFieldPair>

                {/* MOBILE NUMBER */}
                <LabelFieldPair className="ndc-owner-field" style={ownerFieldStyle}>
                  <CardLabel className="card-label-smaller ndc_card_labels" style={ownerLabelStyle}>
                    {t("NDC_MOBILE_NUMBER")}
                    <span className="ndc-required-star"> *</span>
                  </CardLabel>

                  <div className="form-field ndc-field-container" style={ownerFieldContainerStyle}>
                    <Controller
                      control={control}
                      name="mobileNumber"
                      defaultValue={propertyDetails?.mobileNumber || ""}
                      rules={{
                        required: t("REQUIRED_FIELD"),
                        validate: {
                          pattern: (value) => {
                            const mobile = String(value || "").trim();

                            if (!mobile) {
                              return t("REQUIRED_FIELD");
                            }

                            return /^[6-9]\d{9}$/.test(mobile) ? true : t("NDC_MESSAGE_MOBILE_NUMBER_MUST_BE_A_VALID_TEN_DIGIT_INDIAN_NUMBER");
                          },
                        },
                      }}
                      render={({ field }) => (
                        <TextInput
                          value={propertyDetails?.mobileNumber || ""}
                          onChange={(e) => {
                            const value = e.target.value;

                            setPropertyDetails((prev) => ({
                              ...prev,
                              mobileNumber: value,
                            }));

                            field.onChange(value);
                          }}
                          onBlur={field.onBlur}
                          disabled={!!cpt?.details?.owners?.[0]?.mobileNumber}
                          className="ndc-input"
                          style={ownerInputStyle}
                        />
                      )}
                    />
                  </div>
                </LabelFieldPair>

                {/* ADDRESS */}
                <LabelFieldPair className="ndc-owner-field" style={ownerFieldStyle}>
                  <CardLabel className="card-label-smaller ndc_card_labels" style={ownerLabelStyle}>
                    {t("NDC_ADDRESS")}
                    <span className="ndc-required-star"> *</span>
                  </CardLabel>

                  <div className="form-field ndc-field-container" style={ownerFieldContainerStyle}>
                    <Controller
                      control={control}
                      name="address"
                      defaultValue={propertyDetails?.address || ""}
                      rules={{
                        required: t("REQUIRED_FIELD"),
                        validate: {
                          required: (value) => (value && String(value).trim() !== "" ? true : t("REQUIRED_FIELD")),
                        },
                      }}
                      render={({ field }) => (
                        <TextInput
                          value={propertyDetails?.address || ""}
                          onChange={(e) => {
                            const value = e.target.value;

                            setPropertyDetails((prev) => ({
                              ...prev,
                              address: value,
                            }));

                            field.onChange(value);
                          }}
                          onBlur={field.onBlur}
                          disabled={!!cpt?.details?.owners?.[0]?.permanentAddress}
                          className="ndc-input"
                          style={ownerInputStyle}
                        />
                      )}
                    />
                  </div>
                </LabelFieldPair>
              </div>
            )}
          </div>
        )}

        {/* REMARKS */}
        <LabelFieldPair className="ndc-owner-field ndc-owner-field-remarks" style={ownerFieldStyle}>
          <CardLabel className="card-label-smaller ndc_card_labels" style={ownerLabelStyle}>
            {t("Remarks")}
          </CardLabel>

          <div className="form-field ndc-field-container" style={ownerFieldContainerStyle}>
            <Controller
              control={control}
              name="remarks"
              defaultValue={propertyDetails?.remarks || ""}
              rules={{
                required: t("REQUIRED_FIELD"),
              }}
              render={({ field }) => (
                <TextInput
                  value={propertyDetails?.remarks || ""}
                  onChange={(e) => {
                    const value = e.target.value;

                    setPropertyDetails((prev) => ({
                      ...prev,
                      remarks: value,
                    }));

                    field.onChange(value);
                  }}
                  onBlur={field.onBlur}
                  className="ndc-input"
                  style={ownerInputStyle}
                />
              )}
            />
          </div>
        </LabelFieldPair>

        {/* TRADE LICENSE NUMBER */}
        <LabelFieldPair className="ndc-owner-field" style={ownerFieldStyle}>
          <CardLabel className="card-label-smaller ndc_card_labels" style={ownerLabelStyle}>
            {t("NDC_TL_NUMBER")}
          </CardLabel>

          <div className="form-field ndc-field-container" style={ownerFieldContainerStyle}>
            <Controller
              control={control}
              name="tlNumber"
              defaultValue={propertyDetails?.tlNumber || ""}
              render={({ field }) => (
                <TextInput
                  value={propertyDetails?.tlNumber || ""}
                  onChange={(e) => {
                    const value = e.target.value;

                    setPropertyDetails((prev) => ({
                      ...prev,
                      tlNumber: value,
                    }));

                    field.onChange(value);
                  }}
                  onBlur={field.onBlur}
                  className="ndc-input"
                  style={ownerInputStyle}
                />
              )}
            />
          </div>
        </LabelFieldPair>

        {showToast && (
          <div className="ndc-details-form-message" role="alert">
            {t(showToast?.label)}
          </div>
        )}

        {showPayModal && PayWSBillModal && (
          <PayWSBillModal
            setShowToast={() => {
              setShowPayModal(false);
              setSelectedBillData({});
            }}
            billData={selectedBillData}
          />
        )}

        {propertyLoader && <Loader />}
      </div>
    </div>
  );
};

export default PropertyDetailsForm;
