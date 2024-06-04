import React, { useEffect, useState } from "react";
import { LabelFieldPair, CardLabel, TextInput, Dropdown, Loader, CardLabelError } from "@upyog/digit-ui-react-components";
import { useParams, useLocation } from "react-router-dom";

const AdvanceCollection = ({ t, config, onSelect, formData, userType, FSMTextFieldStyle }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const state = Digit.ULBService.getStateId();
  const { pathname: url } = useLocation();
  let { id: applicationNumber } = useParams();
  const userInfo = Digit.UserService.getUser();
  const [TotalAmount, setTotalAmount] = useState();
  const [AdvanceAmount, setAdvanceAmounts] = useState();
  const { isLoading: applicationLoading, isError, data: applicationData, error } = Digit.Hooks.fsm.useSearch(
    tenantId,
    { applicationNos: applicationNumber, uuid: userInfo.uuid },
    { staleTime: Infinity }
  );

  const [vehicle, setVehicle] = useState({ label: formData?.tripData?.vehicleCapacity });
  const [billError, setError] = useState(false);

  const { isLoading: isVehicleMenuLoading, data: vehicleData } = Digit.Hooks.fsm.useMDMS(state, "Vehicle", "VehicleType", { staleTime: Infinity });

  const { data: dsoData, isLoading: isDsoLoading, isSuccess: isDsoSuccess, error: dsoError } = Digit.Hooks.fsm.useDsoSearch(tenantId, {
    limit: -1,
    status: "ACTIVE",
  });

  const inputs = [
    {
      label: "ES_NEW_APPLICATION_ADVANCE_COLLECTION",
      type: "text",
      name: "advanceAmount",
      validation: {
        isRequired: true,
        disabled: true,
        min: "0",
        pattern: `^[0-9]+`,
        title: t("ES_NEW_APPLICATION_AMOUNT_INVALID"),
      },

      default: url.includes("modify") ? applicationData?.advanceAmount : formData?.advanceAmount,
      isMandatory: true,
    },
  ];

  function setValue(object) {
    onSelect(config.key, { ...formData[config.key], ...object });
  }
  function setAdvanceAmount(value) {
    onSelect(config.key, { ...formData[config.key], advanceAmount: value });
  }

  useEffect(() => {
    (async () => {
      if (formData?.tripData?.vehicleType !== vehicle) {
        setVehicle({ label: formData?.tripData?.vehicleType?.capacity });
      }

      if (
        formData?.propertyType &&
        formData?.subtype &&
        formData?.address &&
        formData?.tripData?.vehicleType?.capacity &&
        formData?.address?.propertyLocation?.code === "WITHIN_ULB_LIMITS"
      ) {
        const capacity = formData?.tripData?.vehicleType.capacity;
        const { slum: slumDetails } = formData.address;
        const slum = slumDetails ? "YES" : "NO";
        const billingDetails = await Digit.FSMService.billingSlabSearch(tenantId, {
          propertyType: formData?.subtype,
          capacity,
          slum,
        });

        const billSlab = billingDetails?.billingSlab?.length && billingDetails?.billingSlab[0];

        if (billSlab?.price || billSlab?.price === 0) {
          const totaltripAmount = billSlab.price * formData.tripData.noOfTrips;
          const isTotalAmountOdd=totaltripAmount %2 !==0;

          const { advanceAmount: advanceBalanceAmount } = await Digit.FSMService.advanceBalanceCalculate(tenantId, {
            totalTripAmount: totaltripAmount,
          });
          Digit.SessionStorage.set("total_amount", totaltripAmount);
          Digit.SessionStorage.set("advance_amount", advanceBalanceAmount);
          setTotalAmount(totaltripAmount);
          setAdvanceAmounts(advanceBalanceAmount);
          !url.includes("modify") || (url.includes("modify") && advanceBalanceAmount > formData?.advancepaymentPreference?.advanceAmount)
            ? setValue({
                advanceAmount: (isTotalAmountOdd ? Math.ceil(advanceBalanceAmount) : advanceBalanceAmount) ,
              })
            : null;
          setError(false);
        } else {
          sessionStorage.removeItem("Digit.total_amount");
          sessionStorage.removeItem("Digit.advance_amount");
          setError(true);
        }
      }
    })();
  }, [formData?.propertyType, formData?.subtype, formData?.address?.slum, formData?.tripData?.vehicleType?.capacity, formData?.tripData?.noOfTrips]);

  useEffect(() => {
    (async () => {
      if (formData?.address?.propertyLocation?.code === "FROM_GRAM_PANCHAYAT" && formData.tripData.noOfTrips && formData.tripData.amountPerTrip) {
        const totaltripAmount = formData.tripData.amountPerTrip * formData.tripData.noOfTrips;

        const { advanceAmount: advanceBalanceAmount } = await Digit.FSMService.advanceBalanceCalculate(tenantId, {
          totalTripAmount: totaltripAmount,
        });
        Digit.SessionStorage.set("total_amount", totaltripAmount);
        Digit.SessionStorage.set("advance_amount", advanceBalanceAmount);
        setTotalAmount(totaltripAmount);
        setAdvanceAmounts(advanceBalanceAmount);
        if (formData?.address?.propertyLocation?.code === "FROM_GRAM_PANCHAYAT" && url.includes("modify")) {
          setValue({
            advanceAmount: 0,
          });
        } else if (
          !url.includes("modify") ||
          url.includes("modify") ||
          (formData?.advancepaymentPreference?.advanceAmount > 0 && advanceBalanceAmount > formData?.advancepaymentPreference?.advanceAmount)
        ) {
          setValue({
            advanceAmount: advanceBalanceAmount,
          });
        }

        setError(false);
      }
    })();
  }, [formData.tripData.noOfTrips, formData.tripData.amountPerTrip]);
  return isVehicleMenuLoading && isDsoLoading ? (
    <Loader />
  ) : (
    <div>
      {formData?.tripData?.amountPerTrip !== 0 &&
        inputs?.map((input, index) => {
          let currentValue = formData && formData[config.key] && formData[config.key][input.name];
          return (
            <React.Fragment key={index}>
              <LabelFieldPair key={index}>
                <CardLabel className="card-label-smaller">
                  {t(input.label) + " (₹)"}
                  {input.isMandatory ? " * " : null}
                </CardLabel>
                <div className="field">
                  <TextInput
                    disabled={
                      url.includes("modify")
                        ? formData?.address?.propertyLocation?.code === "FROM_GRAM_PANCHAYAT" && applicationData?.advanceAmount > 0
                          ? false
                          : true
                        : false
                    }
                    type={input.type}
                    key={input.name}
                    style={FSMTextFieldStyle}
                    onChange={(e) => setAdvanceAmount(e.target.value)}
                    value={formData && formData[config.key] ? formData[config.key][input.name] : applicationData?.advanceAmount}
                    {...input.validation}
                  />
                  {currentValue > TotalAmount && (
                    <CardLabelError style={{ width: "100%", marginTop: "-15px", fontSize: "14px", marginBottom: "0px" }}>
                      {t("FSM_ADVANCE_AMOUNT_MAX")}
                    </CardLabelError>
                  )}
                  {currentValue < AdvanceAmount && (
                    <CardLabelError style={{ width: "100%", marginTop: "-15px", fontSize: "14px", marginBottom: "0px" }}>
                      {t("FSM_ADVANCE_AMOUNT_MIN")}
                    </CardLabelError>
                  )}
                  {url.includes("modify-application") &&
                    applicationData?.advanceAmount > 0 &&
                    Number(formData?.tripData?.amountPerTrip) > 0 &&
                    Number(currentValue) === 0 && (
                      <CardLabelError style={{ width: "100%", marginTop: "-15px", fontSize: "14px", marginBottom: "0px" }}>
                        {t("FSM_ADVANCE_AMOUNT_LESS_THAN_AMOUNT_PER_TRIP")}
                      </CardLabelError>
                    )}
                </div>
              </LabelFieldPair>
            </React.Fragment>
          );
        })}
    </div>
  );
};

export default AdvanceCollection;
