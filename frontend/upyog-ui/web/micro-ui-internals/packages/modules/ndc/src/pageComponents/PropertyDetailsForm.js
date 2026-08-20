import React, { useEffect, useState } from "react";

import {
  CardLabel,
  LabelFieldPair,
  TextInput,
  Loader,
  DeleteIcon,
  Table,
} from "@nudmcdgnpm/digit-ui-react-components";

import {
  useForm,
  Controller,
} from "react-hook-form";

import { useSelector } from "react-redux";

import { useTranslation } from "react-i18next";

export const PropertyDetailsForm = ({
  config,
  onSelect,
  userType,
  formData,
  formState,
  clearErrors,
}) => {
  const { control } = useForm({
    mode: "onChange",
  });

  const { t } = useTranslation();

  const tenantId =
    Digit.ULBService.getCurrentTenantId();

  /*
   * =========================================================
   * REDUX DATA
   * =========================================================
   */

  const apiDataCheck = useSelector(
    (state) =>
      state.ndc.NDCForm
        ?.formData
        ?.responseData
  );

  const checkApiDataCheck = useSelector(
    (state) =>
      state.ndc.NDCForm
        ?.formData
        ?.apiData
  );

  const cptFromRedux = useSelector(
    (state) =>
      state.ndc.NDCForm
        ?.formData
        ?.cpt
  );

  const cpt =
    cptFromRedux ||
    formData?.cpt;

  const propertyId =
    cpt?.details?.propertyId;

  /*
   * =========================================================
   * STATES
   * =========================================================
   */

  const [
    showToast,
    setShowToast,
  ] = useState(null);

  const [
    propertyLoader,
    setPropertyLoader,
  ] = useState(false);

  const [
    showPayModal,
    setShowPayModal,
  ] = useState(false);

  const [
    selectedBillData,
    setSelectedBillData,
  ] = useState({});

  const [
    propertyDetails,
    setPropertyDetails,
  ] = useState(
    formData?.PropertyDetails ||
      {}
  );

  const [
    selectedRow,
    setSelectedRow,
  ] = useState(null);

  /*
   * =========================================================
   * NDC STYLES
   * =========================================================
   */

  const FIELD_WIDTH =
    "450px";

  const FIELD_HEIGHT =
    "40px";

  /*
   * =========================================================
   * FIELD
   * =========================================================
   */

  const fieldContainerStyle = {
    width:
      FIELD_WIDTH,

    maxWidth:
      "100%",

    boxSizing:
      "border-box",
  };

  const inputStyle = {
    width:
      FIELD_WIDTH,

    minWidth:
      FIELD_WIDTH,

    maxWidth:
      FIELD_WIDTH,

    height:
      FIELD_HEIGHT,

    minHeight:
      FIELD_HEIGHT,

    boxSizing:
      "border-box",
  };

  /*
   * =========================================================
   * BUTTON
   * =========================================================
   */

  const buttonStyle = {
    display:
      "flex",

    alignItems:
      "center",

    justifyContent:
      "center",

    width:
      "155px",

    minWidth:
      "155px",

    height:
      FIELD_HEIGHT,

    minHeight:
      FIELD_HEIGHT,

    margin:
      "0",

    padding:
      "0 14px",

    backgroundColor:
      "#a82227",

    color:
      "#ffffff",

    border:
      "none",

    borderRadius:
      "0",

    outline:
      "none",

    fontSize:
      "12px",

    fontWeight:
      "600",

    lineHeight:
      "1",

    cursor:
      "pointer",

    boxSizing:
      "border-box",

    whiteSpace:
      "nowrap",

    textAlign:
      "center",

    flexShrink:
      0,
  };

  /*
   * =========================================================
   * NO DUES
   *
   * IMPORTANT:
   * This is intentionally wider than the normal buttons.
   * =========================================================
   */

  const noDueStyle = {
    display:
      "flex",

    alignItems:
      "center",

    justifyContent:
      "flex-start",

    width:
      "320px",

    minWidth:
      "320px",

    maxWidth:
      "320px",

    minHeight:
      FIELD_HEIGHT,

    margin:
      "0",

    padding:
      "10px 16px",

    backgroundColor:
      "#a82227",

    color:
      "#ffffff",

    border:
      "none",

    borderRadius:
      "0",

    fontSize:
      "12px",

    fontWeight:
      "600",

    lineHeight:
      "16px",

    boxSizing:
      "border-box",

    whiteSpace:
      "normal",

    wordBreak:
      "break-word",

    overflowWrap:
      "anywhere",

    textAlign:
      "left",

    flexShrink:
      0,
  };

  /*
   * =========================================================
   * ACTION COLUMN
   * =========================================================
   */

  const actionColumnStyle = {
    display:
      "flex",

    flexDirection:
      "column",

    alignItems:
      "flex-start",

    width:
      FIELD_WIDTH,

    maxWidth:
      "100%",

    marginTop:
      "10px",

    marginBottom:
      "24px",

    boxSizing:
      "border-box",

    gap:
      "8px",
  };

  /*
   * =========================================================
   * WATER SEARCH
   * =========================================================
   */

  const {
    isLoading:
      waterConnectionLoading,

    data:
      waterConnectionData,
  } =
    Digit.Hooks.ws.useSearchWS({
      tenantId,

      filters: {
        searchType:
          "CONNECTION",

        propertyId:
          propertyId,
      },

      config: {
        enabled:
          !!propertyId,
      },

      bussinessService:
        "WS",

      t,
    });

  /*
   * =========================================================
   * SEWERAGE SEARCH
   * =========================================================
   */

  const {
    isLoading:
      sewerageConnectionLoading,

    data:
      sewerageConnectionData,
  } =
    Digit.Hooks.ws.useSearchWS({
      tenantId,

      filters: {
        searchType:
          "CONNECTION",

        propertyId:
          propertyId,
      },

      config: {
        enabled:
          !!propertyId,
      },

      bussinessService:
        "SW",

      t,
    });

  /*
   * =========================================================
   * OWNER TABLE
   * =========================================================
   */

  const applicationFeeColumns = [
    {
      Header: t(
        "TL_COMMON_TABLE_COL_OWN_NAME"
      ),

      accessor:
        "name",

      Cell: ({
        row,
      }) => (
        <div>
          {
            row
              ?.original
              ?.name
          }
        </div>
      ),
    },

    {
      Header:
        "",

      accessor:
        "amount",

      Cell: ({
        row,
      }) => (
        <input
          type="radio"

          name="applicationFee"

          checked={
            selectedRow
              ?.uuid ===
            row
              ?.original
              ?.uuid
          }

          onChange={() =>
            setSelectedRow(
              row?.original
            )
          }
        />
      ),
    },
  ];

  /*
   * =========================================================
   * OWNER DETAILS
   * =========================================================
   */

  useEffect(() => {
    const owner =
      cpt
        ?.details
        ?.owners?.[0];

    const ownerObj =
      selectedRow;

    const emailApi =
      apiDataCheck?.[0]
        ?.owners?.[0]
        ?.emailId;

    const firstName =
      ownerObj?.name ||
      owner?.name ||
      "";

    const email =
      ownerObj?.emailId ||
      emailApi ||
      owner?.emailId ||
      "";

    const mobileNumber =
      ownerObj?.mobileNumber ||
      owner?.mobileNumber ||
      "";

    const address =
      ownerObj?.permanentAddress ||
      owner?.permanentAddress ||
      "";

    const updated = {
      email,

      propertyBillData: {
        isLoading:
          false,

        billData:
          formData
            ?.PropertyDetails
            ?.propertyBillData
            ?.billData ||
          {},
      },
    };

    if (
      firstName
    ) {
      updated.firstName =
        firstName;
    }

    if (
      mobileNumber
    ) {
      updated.mobileNumber =
        mobileNumber;
    }

    if (
      address
    ) {
      updated.address =
        address;
    }

    setPropertyDetails(
      (prev) => ({
        ...prev,
        ...updated,
      })
    );
  }, [
    cpt?.details,
    apiDataCheck,
    selectedRow,
    formData?.PropertyDetails,
  ]);

  /*
   * =========================================================
   * WATER CONNECTION DATA
   * =========================================================
   */

  useEffect(() => {
    let waterConnection =
      [];

    if (
      apiDataCheck?.[0]
        ?.NdcDetails
    ) {
      waterConnection =
        apiDataCheck?.[0]
          ?.NdcDetails
          ?.filter(
            (item) =>
              item.businessService ===
              "WS"
          )
          ?.map(
            (item) => ({
              connectionNo:
                item?.consumerCode,

              isEdit:
                false,

              billData: {
                totalAmount:
                  0,
              },

              isLoading:
                false,
            })
          ) || [];
    } else {
      waterConnection =
        waterConnectionData
          ?.map(
            (item) => ({
              connectionNo:
                item?.connectionNo,

              isEdit:
                false,

              billData:
                {},

              isLoading:
                false,
            })
          ) || [];
    }

    setPropertyDetails(
      (prev) => ({
        ...prev,

        waterConnection,
      })
    );
  }, [
    waterConnectionData,
    apiDataCheck,
  ]);

  /*
   * =========================================================
   * SEWERAGE CONNECTION DATA
   * =========================================================
   */

  useEffect(() => {
    let sewerageConnection =
      [];

    if (
      apiDataCheck?.[0]
        ?.NdcDetails
    ) {
      sewerageConnection =
        apiDataCheck?.[0]
          ?.NdcDetails
          ?.filter(
            (item) =>
              item.businessService ===
              "SW"
          )
          ?.map(
            (item) => ({
              connectionNo:
                item?.consumerCode,

              isEdit:
                false,

              billData: {
                totalAmount:
                  0,
              },

              isLoading:
                false,
            })
          ) || [];
    } else {
      sewerageConnection =
        sewerageConnectionData
          ?.map(
            (item) => ({
              connectionNo:
                item?.connectionNo,

              isEdit:
                false,

              billData:
                {},

              isLoading:
                false,
            })
          ) || [];
    }

    setPropertyDetails(
      (prev) => ({
        ...prev,

        sewerageConnection,
      })
    );
  }, [
    sewerageConnectionData,
    apiDataCheck,
  ]);

  /*
   * =========================================================
   * SEND DATA TO FORM
   * =========================================================
   */

  useEffect(() => {
    if (
      typeof onSelect ===
      "function"
    ) {
      onSelect(
        "PropertyDetails",
        propertyDetails,
        config
      );
    }
  }, [
    propertyDetails,
  ]);

  /*
   * =========================================================
   * ADD WATER
   * =========================================================
   */

  function addWaterConnection() {
    setPropertyDetails(
      (prev) => ({
        ...prev,

        waterConnection: [
          ...(prev.waterConnection ||
            []),

          {
            connectionNo:
              "",

            isEdit:
              true,

            billData:
              {},

            isLoading:
              false,
          },
        ],
      })
    );
  }

  /*
   * =========================================================
   * ADD SEWERAGE
   * =========================================================
   */

  function addSewerageConnection() {
    setPropertyDetails(
      (prev) => ({
        ...prev,

        sewerageConnection: [
          ...(prev.sewerageConnection ||
            []),

          {
            connectionNo:
              "",

            isEdit:
              true,

            billData:
              {},

            isLoading:
              false,
          },
        ],
      })
    );
  }

  /*
   * =========================================================
   * FETCH BILL
   * =========================================================
   */

  async function fetchBill(
    bussinessService,
    consumercodes,
    index
  ) {
    try {
      /*
       * WATER LOADER
       */

      if (
        bussinessService ===
        "WS"
      ) {
        const updated = [
          ...(propertyDetails
            ?.waterConnection ||
            []),
        ];

        if (
          !updated[index]
        ) {
          return;
        }

        updated[index] = {
          ...updated[index],

          isLoading:
            true,
        };

        setPropertyDetails(
          (prev) => ({
            ...prev,

            waterConnection:
              updated,
          })
        );
      }

      /*
       * SEWERAGE LOADER
       */

      if (
        bussinessService ===
        "SW"
      ) {
        const updated = [
          ...(propertyDetails
            ?.sewerageConnection ||
            []),
        ];

        if (
          !updated[index]
        ) {
          return;
        }

        updated[index] = {
          ...updated[index],

          isLoading:
            true,
        };

        setPropertyDetails(
          (prev) => ({
            ...prev,

            sewerageConnection:
              updated,
          })
        );
      }

      if (
        bussinessService ===
        "PT"
      ) {
        setPropertyLoader(
          true
        );
      }

      /*
       * API
       */

      const result =
        await Digit.PaymentService.fetchBill(
          tenantId,
          {
            businessService:
              bussinessService,

            consumerCode:
              consumercodes,
          }
        );

      /*
       * =====================================================
       * BILL FOUND
       * =====================================================
       */

      if (
        result?.Bill?.length >
        0
      ) {
        const bill =
          result.Bill[0];

        /*
         * WATER
         */

        if (
          bussinessService ===
          "WS"
        ) {
          const updated = [
            ...(propertyDetails
              ?.waterConnection ||
              []),
          ];

          if (
            updated[index]
          ) {
            updated[index] = {
              ...updated[index],

              billData:
                bill,

              isLoading:
                false,
            };
          }

          setPropertyDetails(
            (prev) => ({
              ...prev,

              waterConnection:
                updated,
            })
          );

          if (
            Number(
              bill?.totalAmount
            ) > 0
          ) {
            setShowToast({
              error:
                true,

              label:
                t(
                  "NDC_MESSAGE_DUES_FOUND_PLEASE_PAY"
                ),
            });
          } else {
            setShowToast({
              error:
                false,

              label:
                t(
                  "NDC_NO_BILLS_FOUND_WS"
                ),
            });
          }

          return;
        }

        /*
         * SEWERAGE
         */

        if (
          bussinessService ===
          "SW"
        ) {
          const updated = [
            ...(propertyDetails
              ?.sewerageConnection ||
              []),
          ];

          if (
            updated[index]
          ) {
            updated[index] = {
              ...updated[index],

              billData:
                bill,

              isLoading:
                false,
            };
          }

          setPropertyDetails(
            (prev) => ({
              ...prev,

              sewerageConnection:
                updated,
            })
          );

          if (
            Number(
              bill?.totalAmount
            ) > 0
          ) {
            setShowToast({
              error:
                true,

              label:
                t(
                  "NDC_MESSAGE_DUES_FOUND_PLEASE_PAY"
                ),
            });
          } else {
            setShowToast({
              error:
                false,

              label:
                t(
                  "NDC_NO_BILLS_FOUND_SW"
                ),
            });
          }

          return;
        }

        /*
         * PROPERTY
         */

        if (
          bussinessService ===
          "PT"
        ) {
          setPropertyDetails(
            (prev) => ({
              ...prev,

              propertyBillData: {
                ...(prev.propertyBillData ||
                  {}),

                billData:
                  bill,

                isLoading:
                  false,
              },
            })
          );

          setPropertyLoader(
            false
          );

          if (
            Number(
              bill?.totalAmount
            ) > 0
          ) {
            setShowToast({
              error:
                true,

              label:
                t(
                  "NDC_MESSAGE_DUES_FOUND_PLEASE_PAY"
                ),
            });
          } else {
            setShowToast({
              error:
                false,

              label:
                t(
                  "NDC_NO_BILLS_FOUND_PROPERTY"
                ),
            });
          }

          return;
        }
      }

      /*
       * =====================================================
       * EMPTY BILL RESPONSE
       * =====================================================
       */

      if (
        result?.Bill
      ) {
        if (
          bussinessService ===
          "WS"
        ) {
          const updated = [
            ...(propertyDetails
              ?.waterConnection ||
              []),
          ];

          if (
            !updated[index]
          ) {
            return;
          }

          updated[index] = {
            ...updated[index],

            billData: {
              totalAmount:
                0,
            },

            isLoading:
              false,
          };

          setPropertyDetails(
            (prev) => ({
              ...prev,

              waterConnection:
                updated,
            })
          );

          setShowToast({
            error:
              false,

            label:
              t(
                "NDC_NO_BILLS_FOUND_WS"
              ),
          });

          return;
        }

        if (
          bussinessService ===
          "SW"
        ) {
          const updated = [
            ...(propertyDetails
              ?.sewerageConnection ||
              []),
          ];

          if (
            !updated[index]
          ) {
            return;
          }

          updated[index] = {
            ...updated[index],

            billData: {
              totalAmount:
                0,
            },

            isLoading:
              false,
          };

          setPropertyDetails(
            (prev) => ({
              ...prev,

              sewerageConnection:
                updated,
            })
          );

          setShowToast({
            error:
              false,

            label:
              t(
                "NDC_NO_BILLS_FOUND_SW"
              ),
          });

          return;
        }

        if (
          bussinessService ===
          "PT"
        ) {
          setPropertyDetails(
            (prev) => ({
              ...prev,

              propertyBillData: {
                ...(prev.propertyBillData ||
                  {}),

                isLoading:
                  false,

                billData: {
                  totalAmount:
                    0,
                },
              },
            })
          );

          setPropertyLoader(
            false
          );

          setShowToast({
            error:
              false,

            label:
              t(
                "NDC_NO_BILLS_FOUND_PROPERTY"
              ),
          });

          return;
        }
      }

      /*
       * =====================================================
       * FALLBACK
       * =====================================================
       */

      if (
        bussinessService ===
        "WS"
      ) {
        const updated = [
          ...(propertyDetails
            ?.waterConnection ||
            []),
        ];

        if (
          !updated[index]
        ) {
          return;
        }

        updated[index] = {
          ...updated[index],

          isLoading:
            false,

          billData: {
            totalAmount:
              0,
          },
        };

        setPropertyDetails(
          (prev) => ({
            ...prev,

            waterConnection:
              updated,
          })
        );

        setShowToast({
          error:
            false,

          label:
            t(
              "NDC_NO_BILLS_FOUND_WS"
            ),
        });
      }

      if (
        bussinessService ===
        "SW"
      ) {
        const updated = [
          ...(propertyDetails
            ?.sewerageConnection ||
            []),
        ];

        if (
          !updated[index]
        ) {
          return;
        }

        updated[index] = {
          ...updated[index],

          isLoading:
            false,

          billData: {
            totalAmount:
              0,
          },
        };

        setPropertyDetails(
          (prev) => ({
            ...prev,

            sewerageConnection:
              updated,
          })
        );

        setShowToast({
          error:
            false,

          label:
            t(
              "NDC_NO_BILLS_FOUND_SW"
            ),
        });
      }

      if (
        bussinessService ===
        "PT"
      ) {
        setPropertyDetails(
          (prev) => ({
            ...prev,

            propertyBillData: {
              ...(prev.propertyBillData ||
                {}),

              isLoading:
                false,

              billData: {
                totalAmount:
                  0,
              },
            },
          })
        );

        setPropertyLoader(
          false
        );

        setShowToast({
          error:
            false,

          label:
            t(
              "NDC_NO_BILLS_FOUND_PROPERTY"
            ),
        });
      }
    } catch (error) {
      console.error(
        "Error while fetching bill:",
        error
      );

      setPropertyLoader(
        false
      );

      /*
       * STOP WATER LOADER
       */

      if (
        bussinessService ===
        "WS"
      ) {
        const updated = [
          ...(propertyDetails
            ?.waterConnection ||
            []),
        ];

        if (
          updated[index]
        ) {
          updated[index] = {
            ...updated[index],

            isLoading:
              false,
          };
        }

        setPropertyDetails(
          (prev) => ({
            ...prev,

            waterConnection:
              updated,
          })
        );
      }

      /*
       * STOP SEWERAGE LOADER
       */

      if (
        bussinessService ===
        "SW"
      ) {
        const updated = [
          ...(propertyDetails
            ?.sewerageConnection ||
            []),
        ];

        if (
          updated[index]
        ) {
          updated[index] = {
            ...updated[index],

            isLoading:
              false,
          };
        }

        setPropertyDetails(
          (prev) => ({
            ...prev,

            sewerageConnection:
              updated,
          })
        );
      }

      setShowToast({
        error:
          true,

        label:
          t(
            "NDC_MESSAGE_FETCH_FAILED"
          ),
      });
    }
  }

  /*
   * =========================================================
   * CLOSE TOAST
   * =========================================================
   */

  const closeToast =
    () => {
      setShowToast(
        null
      );
    };

  /*
   * =========================================================
   * PAYMENT MODAL
   * =========================================================
   */

  const PayWSBillModal =
    Digit
      ?.ComponentRegistryService
      ?.getComponent(
        "PayWSBillModal"
      );

  /*
   * =========================================================
   * AUTO CLOSE TOAST
   * =========================================================
   */

  useEffect(() => {
    if (
      !showToast
    ) {
      return;
    }

    const timer =
      setTimeout(
        () => {
          setShowToast(
            null
          );
        },
        3000
      );

    return () =>
      clearTimeout(
        timer
      );
  }, [
    showToast,
  ]);

  /*
   * =========================================================
   * SELECT PRIMARY OWNER
   * =========================================================
   */

  useEffect(() => {
    if (
      !selectedRow &&
      checkApiDataCheck
        ?.Applications?.[0]
    ) {
      const owners =
        checkApiDataCheck
          ?.Applications?.[0]
          ?.owners;

      const primaryOwner =
        owners?.find(
          (owner) =>
            owner?.isPrimaryOwner
        );

      if (
        primaryOwner
      ) {
        setSelectedRow(
          primaryOwner
        );
      }
    }
  }, [
    checkApiDataCheck,
    selectedRow,
  ]);

  /*
   * =========================================================
   * CONNECTION ROW
   * =========================================================
   */

  const renderConnectionRows =
    (
      connectionType,
      connections,
      loading,
      labelKey,
      checkStatusKey,
      addHandler
    ) => {
      return (
        <>
          <div
            style={{
              marginTop:
                "32px",

              marginBottom:
                "24px",
            }}
          >
            <div
              style={{
                ...fieldContainerStyle,

                marginBottom:
                  "10px",
              }}
            >
              <CardLabel className="card-label-smaller ndc_card_labels">
                {t(
                  labelKey
                )}
              </CardLabel>
            </div>

            {loading ? (
              <Loader />
            ) : (
              <div
                style={{
                  ...fieldContainerStyle,

                  display:
                    "flex",

                  flexDirection:
                    "column",

                  gap:
                    "10px",
                }}
              >
                {connections?.map(
                  (
                    item,
                    index
                  ) => (
                    <div
                      key={
                        index
                      }
                      style={{
                        ...fieldContainerStyle,

                        display:
                          "flex",

                        flexDirection:
                          "column",

                        alignItems:
                          "flex-start",

                        gap:
                          "8px",

                        marginBottom:
                          "8px",
                      }}
                    >
                      <Controller
                        control={
                          control
                        }

                        name={`${connectionType}[${index}]`}

                        defaultValue={
                          item?.connectionNo ||
                          ""
                        }

                        render={({
                          field,
                        }) => (
                          <TextInput
                            value={
                              item?.connectionNo ||
                              ""
                            }

                            onChange={(
                              e
                            ) => {
                              const value =
                                e
                                  .target
                                  .value;

                              const updated =
                                [
                                  ...(connections ||
                                    []),
                                ];

                              updated[
                                index
                              ] = {
                                ...updated[
                                  index
                                ],

                                connectionNo:
                                  value,
                              };

                              setPropertyDetails(
                                (
                                  prev
                                ) => ({
                                  ...prev,

                                  [connectionType]:
                                    updated,
                                })
                              );

                              field.onChange(
                                value
                              );
                            }}

                            onBlur={
                              field.onBlur
                            }

                            disabled={
                              !item?.isEdit
                            }

                            style={
                              inputStyle
                            }
                          />
                        )}
                      />

                      {item?.isLoading ? (
                        <Loader />
                      ) : (
                        <div
                          style={
                            actionColumnStyle
                          }
                        >
                          {!apiDataCheck?.[0]
                            ?.NdcDetails &&
                            item?.connectionNo &&
                            !item?.billData
                              ?.id &&
                            item?.billData
                              ?.totalAmount !==
                              0 && (
                              <button
                                className="submit-bar ndc-action-button"
                                type="button"
                                style={
                                  buttonStyle
                                }

                                onClick={() =>
                                  fetchBill(
                                    connectionType ===
                                      "waterConnection"
                                      ? "WS"
                                      : "SW",

                                    item.connectionNo,

                                    index
                                  )
                                }
                              >
                                {t(
                                  checkStatusKey
                                )}
                              </button>
                            )}

                          {item?.connectionNo &&
                            item?.billData
                              ?.totalAmount >
                              0 && (
                              <button
                                className="submit-bar ndc-action-button"
                                type="button"
                                style={
                                  buttonStyle
                                }

                                onClick={() => {
                                  setSelectedBillData(
                                    item?.billData
                                  );

                                  setShowPayModal(
                                    true
                                  );
                                }}
                              >
                                {t(
                                  "PAY_DUES"
                                )}
                              </button>
                            )}

                          {item?.connectionNo &&
                            item?.billData
                              ?.totalAmount ===
                              0 && (
                              <div
                                className="ndc-no-dues-box"
                                style={
                                  noDueStyle
                                }
                              >
                                {t(
                                  "NO_DUES"
                                )}
                              </div>
                            )}

                          {item?.isEdit && (
                            <button
                              type="button"

                              className="ndc-delete-button"

                              onClick={() => {
                                const updated =
                                  [
                                    ...(connections ||
                                      []),
                                  ];

                                updated.splice(
                                  index,
                                  1
                                );

                                setPropertyDetails(
                                  (
                                    prev
                                  ) => ({
                                    ...prev,

                                    [connectionType]:
                                      updated,
                                  })
                                );
                              }}
                            >
                              <DeleteIcon
                                className="delete"

                                fill="#a82227"
                              />
                            </button>
                          )}
                        </div>
                      )}
                    </div>
                  )
                )}
              </div>
            )}
          </div>

          <div
            style={{
              marginBottom:
                "36px",
            }}
          >
            <button
              className="submit-bar ndc-action-button"

              type="button"

              style={
                buttonStyle
              }

              onClick={
                addHandler
              }
            >
              {t(
                connectionType ===
                  "waterConnection"
                  ? "ADD_WATER"
                  : "ADD_SEWERAGE"
              )}
            </button>
          </div>
        </>
      );
    };

  /*
   * =========================================================
   * RENDER
   * =========================================================
   */

  return (
    <div
      className="ndc-margin-bottom-16"

      style={{
        width:
          "100%",

        boxSizing:
          "border-box",
      }}
    >
      <style>
        {`
          /*
           * =================================================
           * NDC MODULE ONLY
           * =================================================
           */

          .ndc-details-form-wrapper {
            width: 100%;
            box-sizing: border-box;
          }

          /*
           * =================================================
           * FIELDS
           * =================================================
           */

          .ndc-details-form-wrapper
          .form-field {
            width: 450px !important;
            min-width: 450px !important;
            max-width: 450px !important;

            box-sizing: border-box !important;
          }

          .ndc-details-form-wrapper
          .form-field input {
            width: 450px !important;
            min-width: 450px !important;
            max-width: 450px !important;

            height: 40px !important;
            min-height: 40px !important;

            box-sizing: border-box !important;
          }

          .ndc-details-form-wrapper
          .ndc_card_labels {
            display: block !important;

            width: 450px !important;
            max-width: 100% !important;

            margin-bottom: 8px !important;

            box-sizing: border-box !important;
          }

          /*
           * =================================================
           * BUTTONS
           * =================================================
           */

          .ndc-details-form-wrapper
          .ndc-action-button {
            width: 155px !important;
            min-width: 155px !important;
            max-width: 155px !important;

            height: 40px !important;
            min-height: 40px !important;
            max-height: 40px !important;

            margin: 0 !important;

            padding: 0 14px !important;

            background-color:
              #a82227 !important;

            color:
              #ffffff !important;

            border:
              none !important;

            border-radius:
              0 !important;

            outline:
              none !important;

            font-size:
              12px !important;

            font-weight:
              600 !important;

            line-height:
              1 !important;

            box-sizing:
              border-box !important;

            white-space:
              nowrap !important;

            display:
              flex !important;

            align-items:
              center !important;

            justify-content:
              center !important;

            cursor:
              pointer !important;
          }

          .ndc-details-form-wrapper
          .ndc-action-button span,

          .ndc-details-form-wrapper
          .ndc-action-button p,

          .ndc-details-form-wrapper
          .ndc-action-button div {
            color:
              #ffffff !important;
          }

          /*
           * =================================================
           * NO DUES BOX
           *
           * THIS IS THE IMPORTANT FIX.
           * =================================================
           */

          .ndc-details-form-wrapper
          .ndc-no-dues-box {
            display:
              flex !important;

            align-items:
              center !important;

            justify-content:
              flex-start !important;

            width:
              320px !important;

            min-width:
              320px !important;

            max-width:
              320px !important;

            min-height:
              40px !important;

            margin:
              0 !important;

            padding:
              10px 16px !important;

            background-color:
              #a82227 !important;

            color:
              #ffffff !important;

            border:
              none !important;

            border-radius:
              0 !important;

            font-size:
              12px !important;

            font-weight:
              600 !important;

            line-height:
              16px !important;

            box-sizing:
              border-box !important;

            white-space:
              normal !important;

            word-break:
              break-word !important;

            overflow-wrap:
              anywhere !important;

            text-align:
              left !important;

            flex-shrink:
              0 !important;
          }

          .ndc-details-form-wrapper
          .ndc-no-dues-box * {
            color:
              #ffffff !important;
          }

          /*
           * =================================================
           * DELETE
           * =================================================
           */

          .ndc-details-form-wrapper
          .ndc-delete-button {
            border:
              none !important;

            background:
              transparent !important;

            padding:
              0 !important;

            margin:
              0 !important;

            cursor:
              pointer !important;
          }

          /*
           * =================================================
           * OWNER TABLE
           * =================================================
           */

          .ndc-details-form-wrapper
          .ndc-owner-table {
            width:
              500px !important;

            max-width:
              100% !important;
          }

          /*
           * =================================================
           * STATUS MESSAGE
           * =================================================
           */

          .ndc-details-form-wrapper
          .ndc-details-form-message {
            display:
              flex !important;

            align-items:
              center !important;

            justify-content:
              flex-start !important;

            width:
              320px !important;

            min-width:
              320px !important;

            max-width:
              320px !important;

            min-height:
              40px !important;

            margin-top:
              10px !important;

            margin-bottom:
              20px !important;

            padding:
              10px 16px !important;

            background-color:
              #a82227 !important;

            color:
              #ffffff !important;

            border:
              none !important;

            border-radius:
              0 !important;

            font-size:
              12px !important;

            font-weight:
              600 !important;

            line-height:
              16px !important;

            box-sizing:
              border-box !important;

            white-space:
              normal !important;

            word-break:
              break-word !important;

            overflow-wrap:
              anywhere !important;

            text-align:
              left !important;
          }

          .ndc-details-form-wrapper
          .ndc-details-form-message * {
            color:
              #ffffff !important;
          }

          /*
           * =================================================
           * MOBILE
           * =================================================
           */

          @media (max-width: 600px) {

            .ndc-details-form-wrapper
            .form-field,

            .ndc-details-form-wrapper
            .form-field input,

            .ndc-details-form-wrapper
            .ndc_card_labels {
              width:
                100% !important;

              min-width:
                0 !important;

              max-width:
                100% !important;
            }

            .ndc-details-form-wrapper
            .ndc-no-dues-box,

            .ndc-details-form-wrapper
            .ndc-details-form-message {
              width:
                100% !important;

              min-width:
                0 !important;

              max-width:
                100% !important;
            }
          }
        `}
      </style>

      <div
        className="ndc-details-form-wrapper"
      >
        {(cpt?.details ||
          apiDataCheck?.[0]
            ?.NdcDetails) && (
          <div>

            {/* =================================================
                WATER CONNECTION
            ================================================== */}

            {renderConnectionRows(
              "waterConnection",

              propertyDetails
                ?.waterConnection,

              waterConnectionLoading,

              "NDC_WATER_CONNECTION",

              "CHECK_STATUS_WATER",

              addWaterConnection
            )}

            {/* =================================================
                SEWERAGE CONNECTION
            ================================================== */}

            <div
              style={{
                marginBottom:
                  "24px",
              }}
            >
              <div
                style={{
                  ...fieldContainerStyle,

                  marginBottom:
                    "10px",
                }}
              >
                <CardLabel className="card-label-smaller ndc_card_labels">
                  {t(
                    "NDC_SEWERAGE_CONNECTION"
                  )}
                </CardLabel>
              </div>

              {sewerageConnectionLoading ? (
                <Loader />
              ) : (
                <div
                  style={{
                    ...fieldContainerStyle,

                    display:
                      "flex",

                    flexDirection:
                      "column",

                    gap:
                      "10px",
                  }}
                >
                  {propertyDetails
                    ?.sewerageConnection
                    ?.map(
                      (
                        item,
                        index
                      ) => (
                        <div
                          key={
                            index
                          }

                          style={{
                            ...fieldContainerStyle,

                            display:
                              "flex",

                            flexDirection:
                              "column",

                            alignItems:
                              "flex-start",

                            gap:
                              "8px",

                            marginBottom:
                              "8px",
                          }}
                        >
                          <Controller
                            control={
                              control
                            }

                            name={`sewerageConnection[${index}]`}

                            defaultValue={
                              item?.connectionNo ||
                              ""
                            }

                            render={({
                              field,
                            }) => (
                              <TextInput
                                value={
                                  item?.connectionNo ||
                                  ""
                                }

                                onChange={(
                                  e
                                ) => {
                                  const value =
                                    e
                                      .target
                                      .value;

                                  const updated =
                                    [
                                      ...(propertyDetails
                                        ?.sewerageConnection ||
                                        []),
                                    ];

                                  updated[
                                    index
                                  ] = {
                                    ...updated[
                                      index
                                    ],

                                    connectionNo:
                                      value,
                                  };

                                  setPropertyDetails(
                                    (
                                      prev
                                    ) => ({
                                      ...prev,

                                      sewerageConnection:
                                        updated,
                                    })
                                  );

                                  field.onChange(
                                    value
                                  );
                                }}

                                onBlur={
                                  field.onBlur
                                }

                                disabled={
                                  !item?.isEdit
                                }

                                style={
                                  inputStyle
                                }
                              />
                            )}
                          />

                          {item?.isLoading ? (
                            <Loader />
                          ) : (
                            <div
                              style={
                                actionColumnStyle
                              }
                            >
                              {!apiDataCheck?.[0]
                                ?.NdcDetails &&
                                item?.connectionNo &&
                                !item?.billData
                                  ?.id &&
                                item?.billData
                                  ?.totalAmount !==
                                  0 && (
                                  <button
                                    className="submit-bar ndc-action-button"

                                    type="button"

                                    style={
                                      buttonStyle
                                    }

                                    onClick={() =>
                                      fetchBill(
                                        "SW",

                                        item.connectionNo,

                                        index
                                      )
                                    }
                                  >
                                    {t(
                                      "CHECK_STATUS_SEWERAGE"
                                    )}
                                  </button>
                                )}

                              {item?.connectionNo &&
                                item?.billData
                                  ?.totalAmount >
                                  0 && (
                                  <button
                                    className="submit-bar ndc-action-button"

                                    type="button"

                                    style={
                                      buttonStyle
                                    }

                                    onClick={() => {
                                      setSelectedBillData(
                                        item?.billData
                                      );

                                      setShowPayModal(
                                        true
                                      );
                                    }}
                                  >
                                    {t(
                                      "PAY_DUES"
                                    )}
                                  </button>
                                )}

                              {item?.connectionNo &&
                                item?.billData
                                  ?.totalAmount ===
                                  0 && (
                                  <div
                                    className="ndc-no-dues-box"

                                    style={
                                      noDueStyle
                                    }
                                  >
                                    {t(
                                      "NO_DUES"
                                    )}
                                  </div>
                                )}

                              {item?.isEdit && (
                                <button
                                  type="button"

                                  className="ndc-delete-button"

                                  onClick={() => {
                                    const updated =
                                      [
                                        ...(propertyDetails
                                          ?.sewerageConnection ||
                                          []),
                                      ];

                                    updated.splice(
                                      index,
                                      1
                                    );

                                    setPropertyDetails(
                                      (
                                        prev
                                      ) => ({
                                        ...prev,

                                        sewerageConnection:
                                          updated,
                                      })
                                    );
                                  }}
                                >
                                  <DeleteIcon
                                    className="delete"

                                    fill="#a82227"
                                  />
                                </button>
                              )}
                            </div>
                          )}
                        </div>
                      )
                    )}
                </div>
              )}
            </div>

            {/* =================================================
                ADD SEWERAGE
            ================================================== */}

            <div
              style={{
                marginBottom:
                  "40px",
              }}
            >
              <button
                className="submit-bar ndc-action-button"

                type="button"

                style={
                  buttonStyle
                }

                onClick={
                  addSewerageConnection
                }
              >
                {t(
                  "ADD_SEWERAGE"
                )}
              </button>
            </div>

            {/* =================================================
                OWNER SELECTION
            ================================================== */}

            <div
              style={{
                width:
                  "500px",

                maxWidth:
                  "100%",

                marginTop:
                  "10px",

                marginBottom:
                  "30px",
              }}
            >
              <p
                style={{
                  color:
                    "green",

                  fontSize:
                    "12px",

                  lineHeight:
                    "17px",

                  padding:
                    "0",

                  margin:
                    "0 0 10px 0",
                }}
              >
                Please Select One
                Owner Name Who
                will have the access
                of No Due Certificate
              </p>

              <div
                className="ndc-owner-table"

                style={{
                  width:
                    "500px",

                  maxWidth:
                    "100%",
                }}
              >
                <Table
                  className="customTable table-border-style"

                  t={t}

                  data={
                    cpt
                      ?.details
                      ?.owners ||
                    []
                  }

                  columns={
                    applicationFeeColumns
                  }

                  getCellProps={() => ({
                    style:
                      {},
                  })}

                  disableSort={
                    true
                  }

                  manualPagination={
                    false
                  }

                  isPaginationRequired={
                    false
                  }
                />
              </div>
            </div>

            {/* =================================================
                OWNER DETAILS
            ================================================== */}

            {selectedRow && (
              <div>

                {/* FULL NAME */}

                <LabelFieldPair
                  style={{
                    display:
                      "block",

                    marginTop:
                      "20px",

                    marginBottom:
                      "22px",
                  }}
                >
                  <CardLabel className="card-label-smaller ndc_card_labels">
                    {t(
                      "NDC_FULL_NAME"
                    )}

                    <span
                      style={{
                        color:
                          "red",
                      }}
                    >
                      {" "}
                      *
                    </span>
                  </CardLabel>

                  <div
                    className="form-field"

                    style={
                      fieldContainerStyle
                    }
                  >
                    <Controller
                      control={
                        control
                      }

                      name="firstName"

                      defaultValue={
                        propertyDetails
                          ?.firstName ||
                        ""
                      }

                      rules={{
                        required:
                          t(
                            "REQUIRED_FIELD"
                          ),

                        validate: {
                          pattern:
                            (
                              value
                            ) => {
                              if (
                                !value ||
                                String(
                                  value
                                ).trim() ===
                                  ""
                              ) {
                                return t(
                                  "REQUIRED_FIELD"
                                );
                              }

                              return /^[-@.\/#&+\w\s]*$/.test(
                                String(
                                  value
                                )
                              )
                                ? true
                                : t(
                                    "INVALID_NAME"
                                  );
                            },
                        },
                      }}

                      render={({
                        field,
                      }) => (
                        <TextInput
                          value={
                            propertyDetails
                              ?.firstName ||
                            ""
                          }

                          onChange={(
                            e
                          ) => {
                            const value =
                              e
                                .target
                                .value;

                            setPropertyDetails(
                              (
                                prev
                              ) => ({
                                ...prev,

                                firstName:
                                  value,
                              })
                            );

                            field.onChange(
                              value
                            );
                          }}

                          onBlur={
                            field.onBlur
                          }

                          disabled={
                            !!cpt
                              ?.details
                              ?.owners?.[0]
                              ?.name
                          }

                          style={
                            inputStyle
                          }
                        />
                      )}
                    />
                  </div>
                </LabelFieldPair>

                {/* EMAIL */}

                <LabelFieldPair
                  style={{
                    display:
                      "block",

                    marginBottom:
                      "22px",
                  }}
                >
                  <CardLabel className="card-label-smaller ndc_card_labels">
                    {t(
                      "NDC_EMAIL"
                    )}

                    <span
                      style={{
                        color:
                          "red",
                      }}
                    >
                      {" "}
                      *
                    </span>
                  </CardLabel>

                  <div
                    className="form-field"

                    style={
                      fieldContainerStyle
                    }
                  >
                    <Controller
                      control={
                        control
                      }

                      name="email"

                      defaultValue={
                        propertyDetails
                          ?.email ||
                        ""
                      }

                      rules={{
                        required:
                          t(
                            "REQUIRED_FIELD"
                          ),

                        validate: {
                          pattern:
                            (
                              value
                            ) => {
                              if (
                                !value ||
                                String(
                                  value
                                ).trim() ===
                                  ""
                              ) {
                                return t(
                                  "REQUIRED_FIELD"
                                );
                              }

                              return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(
                                String(
                                  value
                                ).trim()
                              )
                                ? true
                                : t(
                                    "INVALID_EMAIL"
                                  );
                            },
                        },
                      }}

                      render={({
                        field,
                      }) => (
                        <TextInput
                          value={
                            propertyDetails
                              ?.email ||
                            ""
                          }

                          onChange={(
                            e
                          ) => {
                            const value =
                              e
                                .target
                                .value;

                            setPropertyDetails(
                              (
                                prev
                              ) => ({
                                ...prev,

                                email:
                                  value,
                              })
                            );

                            field.onChange(
                              value
                            );
                          }}

                          onBlur={
                            field.onBlur
                          }

                          style={
                            inputStyle
                          }
                        />
                      )}
                    />
                  </div>
                </LabelFieldPair>

                {/* MOBILE */}

                <LabelFieldPair
                  style={{
                    display:
                      "block",

                    marginBottom:
                      "22px",
                  }}
                >
                  <CardLabel className="card-label-smaller ndc_card_labels">
                    {t(
                      "NDC_MOBILE_NUMBER"
                    )}

                    <span
                      style={{
                        color:
                          "red",
                      }}
                    >
                      {" "}
                      *
                    </span>
                  </CardLabel>

                  <div
                    className="form-field"

                    style={
                      fieldContainerStyle
                    }
                  >
                    <Controller
                      control={
                        control
                      }

                      name="mobileNumber"

                      defaultValue={
                        propertyDetails
                          ?.mobileNumber ||
                        ""
                      }

                      rules={{
                        required:
                          t(
                            "REQUIRED_FIELD"
                          ),

                        validate: {
                          pattern:
                            (
                              value
                            ) => {
                              const mobile =
                                String(
                                  value ||
                                    ""
                                ).trim();

                              if (
                                !mobile
                              ) {
                                return t(
                                  "REQUIRED_FIELD"
                                );
                              }

                              return /^[6-9]\d{9}$/.test(
                                mobile
                              )
                                ? true
                                : t(
                                    "NDC_MESSAGE_MOBILE_NUMBER_MUST_BE_A_VALID_TEN_DIGIT_INDIAN_NUMBER"
                                  );
                            },
                        },
                      }}

                      render={({
                        field,
                      }) => (
                        <TextInput
                          value={
                            propertyDetails
                              ?.mobileNumber ||
                            ""
                          }

                          onChange={(
                            e
                          ) => {
                            const value =
                              e
                                .target
                                .value;

                            setPropertyDetails(
                              (
                                prev
                              ) => ({
                                ...prev,

                                mobileNumber:
                                  value,
                              })
                            );

                            field.onChange(
                              value
                            );
                          }}

                          onBlur={
                            field.onBlur
                          }

                          disabled={
                            !!cpt
                              ?.details
                              ?.owners?.[0]
                              ?.mobileNumber
                          }

                          style={
                            inputStyle
                          }
                        />
                      )}
                    />
                  </div>
                </LabelFieldPair>

                {/* ADDRESS */}

                <LabelFieldPair
                  style={{
                    display:
                      "block",

                    marginBottom:
                      "22px",
                  }}
                >
                  <CardLabel className="card-label-smaller ndc_card_labels">
                    {t(
                      "NDC_ADDRESS"
                    )}

                    <span
                      style={{
                        color:
                          "red",
                      }}
                    >
                      {" "}
                      *
                    </span>
                  </CardLabel>

                  <div
                    className="form-field"

                    style={
                      fieldContainerStyle
                    }
                  >
                    <Controller
                      control={
                        control
                      }

                      name="address"

                      defaultValue={
                        propertyDetails
                          ?.address ||
                        ""
                      }

                      rules={{
                        required:
                          t(
                            "REQUIRED_FIELD"
                          ),

                        validate: {
                          required:
                            (
                              value
                            ) =>
                              value &&
                              String(
                                value
                              ).trim() !==
                                ""
                                ? true
                                : t(
                                    "REQUIRED_FIELD"
                                  ),
                        },
                      }}

                      render={({
                        field,
                      }) => (
                        <TextInput
                          value={
                            propertyDetails
                              ?.address ||
                            ""
                          }

                          onChange={(
                            e
                          ) => {
                            const value =
                              e
                                .target
                                .value;

                            setPropertyDetails(
                              (
                                prev
                              ) => ({
                                ...prev,

                                address:
                                  value,
                              })
                            );

                            field.onChange(
                              value
                            );
                          }}

                          onBlur={
                            field.onBlur
                          }

                          disabled={
                            !!cpt
                              ?.details
                              ?.owners?.[0]
                              ?.permanentAddress
                          }

                          style={
                            inputStyle
                          }
                        />
                      )}
                    />
                  </div>
                </LabelFieldPair>
              </div>
            )}
          </div>
        )}

        {/* =====================================================
            REMARKS
        ====================================================== */}

        <LabelFieldPair
          style={{
            display:
              "block",

            marginTop:
              "10px",

            marginBottom:
              "22px",
          }}
        >
          <CardLabel className="card-label-smaller ndc_card_labels">
            {t(
              "Remarks"
            )}
          </CardLabel>

          <div
            className="form-field"

            style={
              fieldContainerStyle
            }
          >
            <Controller
              control={
                control
              }

              name="remarks"

              defaultValue={
                propertyDetails
                  ?.remarks ||
                ""
              }

              rules={{
                required:
                  t(
                    "REQUIRED_FIELD"
                  ),
              }}

              render={({
                field,
              }) => (
                <TextInput
                  value={
                    propertyDetails
                      ?.remarks ||
                    ""
                  }

                  onChange={(
                    e
                  ) => {
                    const value =
                      e
                        .target
                        .value;

                    setPropertyDetails(
                      (
                        prev
                      ) => ({
                        ...prev,

                        remarks:
                          value,
                      })
                    );

                    field.onChange(
                      value
                    );
                  }}

                  onBlur={
                    field.onBlur
                  }

                  style={
                    inputStyle
                  }
                />
              )}
            />
          </div>
        </LabelFieldPair>

        {/* =====================================================
            TRADE LICENSE NUMBER
        ====================================================== */}

        <LabelFieldPair
          style={{
            display:
              "block",

            marginBottom:
              "22px",
          }}
        >
          <CardLabel className="card-label-smaller ndc_card_labels">
            {t(
              "NDC_TL_NUMBER"
            )}
          </CardLabel>

          <div
            className="form-field"

            style={
              fieldContainerStyle
            }
          >
            <Controller
              control={
                control
              }

              name="tlNumber"

              defaultValue={
                propertyDetails
                  ?.tlNumber ||
                ""
              }

              render={({
                field,
              }) => (
                <TextInput
                  value={
                    propertyDetails
                      ?.tlNumber ||
                    ""
                  }

                  onChange={(
                    e
                  ) => {
                    const value =
                      e
                        .target
                        .value;

                    setPropertyDetails(
                      (
                        prev
                      ) => ({
                        ...prev,

                        tlNumber:
                          value,
                      })
                    );

                    field.onChange(
                      value
                    );
                  }}

                  onBlur={
                    field.onBlur
                  }

                  style={
                    inputStyle
                  }
                />
              )}
            />
          </div>
        </LabelFieldPair>

        {/* =====================================================
            TOAST
        ====================================================== */}

        {showToast && (
          <div
            className="ndc-details-form-message"
            role="alert"
          >
            {t(
              showToast?.label
            )}
          </div>
        )}

        {/* =====================================================
            PAYMENT MODAL
        ====================================================== */}

        {showPayModal &&
          PayWSBillModal && (
            <PayWSBillModal
              setShowToast={() => {
                setShowPayModal(
                  false
                );

                setSelectedBillData(
                  {}
                );
              }}

              billData={
                selectedBillData
              }
            />
          )}

        {/* =====================================================
            LOADER
        ====================================================== */}

        {propertyLoader && (
          <Loader />
        )}
      </div>
    </div>
  );
};

export default PropertyDetailsForm;