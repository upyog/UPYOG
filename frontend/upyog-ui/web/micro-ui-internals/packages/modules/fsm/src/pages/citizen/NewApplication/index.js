import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { Navigate, Route, Routes, useLocation, useMatch } from "react-router-dom";
import { TypeSelectCard, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import { newConfig } from "../../../config/NewApplication/config";
import CheckPage from "./CheckPage";
import Response from "./Response";
import { useQueryClient } from "@tanstack/react-query";

const FileComplaint = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  let config = [];
  let configs = []
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("FSM_CITIZEN_FILE_PROPERTY", {});
  const { data: commonFields, isLoading } = Digit.Hooks.fsm.useMDMS(stateId, "FSM", "CommonFieldsConfig");
  const mutation = Digit.Hooks.fsm.useDesludging(params?.address?.city ? params.address?.city?.code : tenantId);

  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("FSM_MUTATION_HAPPENED", false);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("FSM_ERROR_DATA", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("FSM_MUTATION_SUCCESS_DATA", false);

  useEffect(() => {
    if (!pathname?.includes("new-application/response")) {
      setMutationHappened(false);
      clearSuccessData();
      clearError();
    }
  }, []);

  const goNext = (skipStep) => {
    const currentPath = pathname.split("/").pop();
    const { nextStep } = configs.find((routeObj) => routeObj.route === currentPath);
    let redirectWithHistory = (to, state) => navigate(to, state != null ? { state } : undefined);
    if (skipStep) {
      redirectWithHistory = (to, state) => navigate(to, state != null ? { replace: true, state } : { replace: true });
    }
    if (nextStep === null) {
      return redirectWithHistory(`${parentRoute}/new-application/check`);
    }
    redirectWithHistory(`${nextStep}`);
  };

  const submitComplaint = async () => {
    try {
      const amount = Digit.SessionStorage.get("total_amount");
      const amountPerTrip = Digit.SessionStorage.get("amount_per_trip");
      const { subtype, propertyID, pitDetail, address, pitType, source, selectGender, selectPaymentPreference, selectTripNo } = params;
      const {
        city,
        locality,
        geoLocation,
        pincode,
        street,
        doorNo,
        landmark,
        slum,
        gramPanchayat,
        village,
        propertyLocation,
        newLocality,
        newGramPanchayat,
        newVillage,
      } = address;
      
      const advanceAmount = amount === 0 ? null : selectPaymentPreference?.advanceAmount;
      const formdata = {
        fsm: {
          citizen: {
            gender: selectGender?.code,
          },
          tenantId: city?.code,
          propertyUsage: subtype?.code,
          address: {
            tenantId: city?.code,
            additionalDetails: {
              boundaryType: propertyLocation?.code === "FROM_GRAM_PANCHAYAT" ? "GP" : "Locality",
              gramPanchayat: {
                code: gramPanchayat?.code,
                name: gramPanchayat?.name,
              },
              village: village?.code
                ? {
                    code: village?.code ? village?.code : "",
                    name: village?.name ? village?.name : "",
                  }
                : newVillage,
              newLocality: newLocality,
              newGramPanchayat: newGramPanchayat,
            },
            street: street?.trim(),
            doorNo: doorNo?.trim(),
            landmark: landmark,
            slumName: slum,
            city: city?.name,
            pincode,
            locality: {
              code: propertyLocation?.code === "WITHIN_ULB_LIMITS" ? locality?.code : gramPanchayat?.code,
              name: propertyLocation?.code === "WITHIN_ULB_LIMITS" ? locality?.name : gramPanchayat?.name,
            },
            geoLocation: {
              latitude: geoLocation?.latitude,
              longitude: geoLocation?.longitude,
              additionalDetails: {
                digipin: address?.digipin || "",
              },
            },
          },
          pitDetail: {
            additionalDetails: {
              fileStoreId: {
                CITIZEN: pitDetail?.images,
              },
            },
          },
          source,
          sanitationtype: pitType?.code,
          paymentPreference: amount === 0 ? null : selectPaymentPreference?.paymentType ? selectPaymentPreference?.paymentType?.code : null,
          noOfTrips: selectTripNo ? selectTripNo?.tripNo?.code : 1,
          vehicleCapacity: selectTripNo ? selectTripNo?.vehicleCapacity?.capacity : "",
          additionalDetails: {
            totalAmount: amount,
            tripAmount: typeof amountPerTrip === "number" ? JSON.stringify(amountPerTrip) : amountPerTrip,
            distancefromroad : params?.roadWidth?.distancefromroad,
            roadWidth: params?.roadWidth?.roadWidth,
            propertyID : params?.cptId?.id
          },
          advanceAmount: typeof advanceAmount === "number" ? JSON.stringify(advanceAmount) : advanceAmount,
        },
        workflow: null,
      };
      
      mutation.mutate(formdata, {
        onSuccess: (response) => {
          clearParams();
          queryClient.invalidateQueries("FSM_CITIZEN_SEARCH");
          sessionStorage.removeItem("Digit.total_amount");
          sessionStorage.removeItem("Digit.fsm.file.address.city");
          navigate(`${parentRoute}/new-application/response`, {
            state: {
              data: response,
              isSuccess: true,
              formData: params
            },
          });
        },
        onError: (error) => {
          console.log("error", error);
          navigate(`${parentRoute}/new-application/response`, {
            state: {
              data: null,
              isSuccess: false,
              error: error,
              formData: params
            },
          });
        },
      });
    } catch (err) {
      console.log("Error in form submission:", err);
    }
  };

  function handleSelect(key, data, skipStep) {
    setParams({ ...params, ...{ [key]: { ...params[key], ...data } }, ...{ source: "ONLINE" } });
    goNext(skipStep);
  }

  const handleSkip = () => {};

  const handleSUccess = () => {
    clearParams();
    queryClient.invalidateQueries("FSM_CITIZEN_SEARCH");
    setMutationHappened(true);
  };

  if (isLoading) {
    return <Loader />;
  }

  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });

  configs = [...config]
  configs.indexRoute = "select-trip-number";

  return (
    <Routes>
      {configs.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route
            path={`${routeObj.route}`}
            key={index}
            element={<Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} />}
          />
        );
      })}
      <Route path={`check`} element={<CheckPage onSubmit={submitComplaint} value={params} />} />
      <Route path={`response`} element={<Response />} />
      <Route path="*" element={<Navigate to={`${configs.indexRoute}`} />} />
    </Routes>
  );
};

export default FileComplaint;
