import React, { useEffect } from "react";
import {
  Banner,
  Card,
  CardText,
  Loader,
  Row,
  StatusTable,
  SubmitBar,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";

import {
  convertToPropertyLightWeight,
  convertToUpdatePropertyLightWeight,
} from "../utils";

const GetActionMessage = ({ isSuccess, isPending }) => {
  const { t } = useTranslation();

  const isEdit = window.location.href.includes("edit-application");
  const isEmployee = window.location.href.includes("employee");

  if (isSuccess) {
    return !isEdit
      ? isEmployee
        ? t("CS_NEW_PROPERTY_APPLICATION_CREATED_SUCCESS")
        : t("CS_NEW_PROPERTY_APPLICATION_SUBMITTED_SUCCESS")
      : t("CS_PROPERTY_UPDATE_APPLICATION_SUCCESS");
  }

  if (isPending) {
    return !isEdit
      ? t("CS_PROPERTY_APPLICATION_PENDING")
      : t("CS_PROPERTY_UPDATE_APPLICATION_PENDING");
  }

  return !isEdit
    ? t("CS_PROPERTY_APPLICATION_FAILED")
    : t("CS_PROPERTY_UPDATE_APPLICATION_FAILED");
};

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

const BannerPicker = ({ data, isSuccess, isPending }) => {
  const { t } = useTranslation();

  return (
    <Banner
      message={GetActionMessage({
        isSuccess,
        isPending,
      })}
      applicationNumber={data?.Properties?.[0]?.acknowldgementNumber}
      info={isSuccess ? t("PT_APPLICATION_NO") : ""}
      successful={isSuccess}
    />
  );
};

const PTAcknowledgement = ({
  data: propData,
  onSuccess,
  onSelect,
  formData,
  redirectUrl,
  userType,
}) => {
  const { t } = useTranslation();

  const location = useLocation();

  const navigate = Digit.Hooks.useCustomNavigate();

  const stateId = Digit.ULBService.getStateId();

  const tenantId = Digit.ULBService.getCurrentTenantId();

  /**
   * Support old + new data flow
   */
  let data = location?.state?.data || propData;
  console.log("PT DATA", data);

  if (onSelect) {
    data = formData?.cptNewProperty?.property;
  }

  let createNUpdate = false;

  Digit.Hooks.pt
    .useMDMS(stateId, "PropertyTax", "PTWorkflow")
    .data?.PropertyTax?.PTWorkfow?.forEach((item) => {
      if (item.enable && item.businessService.includes("WNS")) {
        createNUpdate = true;
      }
    });

// Initialize mutation for creating a property (type=true)
  const mutation = Digit.Hooks.pt.usePropertyAPI(
    data?.locationDet?.city ? data.locationDet.city.code : tenantId,
    true,
  );

// Initialize mutation for updating a property (type=false)
  const mutationForUpdate = Digit.Hooks.pt.usePropertyAPI(
    data?.locationDet?.city ? data.locationDet.city.code : tenantId,
    false,
  );

// Effect to create property on component mount
  useEffect(() => {
    if (!data || Object.keys(data).length === 0) return;

    const tenant =
      userType === "employee" ? tenantId : data?.locationDet?.cityCode?.code;

    data.tenantId = tenant;

    const formdata = convertToPropertyLightWeight(data);

    formdata.Property.tenantId = formdata?.Property?.tenantId || tenant;

    const timer = setTimeout(() => {
      mutation.mutate(formdata, {
        onSuccess,
        onError: (error) => {
          console.error("Property create failed", error);
        },
      });
    }, 0);

    return () => clearTimeout(timer);
  }, []);

  // useEffect(() => {

  //   if (
  //     !data ||
  //     !data.owners ||
  //     !data.locationDet
  //   ) {
  //     return;
  //   }

  //   const formdata =
  //     convertToPropertyLightWeight(data);

  //   mutation.mutate(
  //     formdata,
  //     {
  //       onSuccess,
  //       onError: (error) => {
  //         console.error(
  //           "PT Create Failed",
  //           error
  //         );
  //       }
  //     }
  //   );

  // }, [data]);

  useEffect(() => {
    if (mutation.isSuccess && !createNUpdate) {
      setTimeout(() => {
        if (redirectUrl) {
          navigate(
            `${redirectUrl}?propertyId=${mutation.data.Properties[0].propertyId}&tenantId=${mutation.data.Properties[0].tenantId}`,
            {
              state: {
                ...location?.state?.prevState,
              },
            },
          );
        }
      }, 3000);
    }
  }, [mutation.isSuccess]);

  useEffect(() => {
    if (mutation.isSuccess && createNUpdate) {
      try {
        const tenant =
          userType === "employee" ? tenantId : data?.locationDet?.city?.code;

        data.tenantId = tenant;

        const formdata = convertToUpdatePropertyLightWeight(data);

        formdata.Property.tenantId = formdata.Property.tenantId || tenant;

        mutationForUpdate.mutate(formdata, {
          onSuccess,
        });
      } catch (e) {
        console.error(e);
      }
    }
  }, [mutation.isSuccess]);

// Handler for proceeding after acknowledgement, storing data in session storage
  const onNext = () => {
    if (onSelect && mutation.isSuccess) {
      sessionStorage.setItem(
        "Digit_OBPS_PT",
        JSON.stringify(mutation.data.Properties[0]),
      );

      sessionStorage.setItem(
        "Digit_FSM_PT",
        JSON.stringify(mutation.data.Properties[0]),
      );

      onSelect("cpt", {
        details: mutation.data.Properties[0],
      });
    }
  };

  if (mutation.isPending || mutation.status === "idle") {
    return <Loader />;
  }

// Render card with status and navigation actions
  return (
    <Card>
      <BannerPicker
        data={mutation.data}
        isSuccess={mutation.isSuccess}
        isPending={mutation.isPending}
      />

      {mutation.isSuccess ? (
        <CardText>
          {window.location.href.includes("employee")
            ? t("CS_CREATE_PROPERTY_SUCCESS_EMP_RESPONSE")
            : t("CS_CREATE_PROPERTY_SUCCESS_CITIZEN_RESPONSE")}
        </CardText>
      ) : (
        <CardText>{t("CS_FILE_PROPERTY_FAILED_RESPONSE")}</CardText>
      )}

      <StatusTable>
        {mutation.isSuccess && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last
            label={t("PT_COMMON_TABLE_COL_PT_ID")}
            text={mutation.data.Properties[0].propertyId}
            textStyle={{
              whiteSpace: "pre",
              width: "200%",
            }}
          />
        )}
      </StatusTable>

      {mutation.isSuccess &&
        window.location.href.includes("/citizen/") &&
        (onSelect ? (
          <SubmitBar label={t("CS_COMMON_PROCEED")} onSubmit={onNext} />
        ) : (
          <SubmitBar
            label={t("CS_COMMON_PROCEED")}
            onSubmit={() => {
              if (redirectUrl) {
                navigate(
                  `${redirectUrl}?propertyId=${mutation.data.Properties[0].propertyId}&tenantId=${mutation.data.Properties[0].tenantId}`,
                  {
                    state: {
                      ...location?.state?.prevState,
                    },
                  },
                );
              }
            }}
          />
        ))}
    </Card>
  );
};

export default PTAcknowledgement;
