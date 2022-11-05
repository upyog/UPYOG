import { FormComposer, Toast ,Loader,CameraIcon} from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../components/config/config";
import UploadDrawer from "./ImageUpload/UploadDrawer";

const defaultImage =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAO4AAADUCAMAAACs0e/bAAAAM1BMVEXK0eL" +
  "/" +
  "/" +
  "/" +
  "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";

const CreateEmployee = ({userType}) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [canSubmit, setSubmitValve] = useState(false);
  const stateId = Digit.ULBService.getStateId();
  const [mobileNumber, setMobileNumber] = useState(null);
  const [phonecheck, setPhonecheck] = useState(false);
  const [checkfield, setcheck] = useState(false)
  const { t } = useTranslation();
  const history = useHistory();
  const [profilePic, setProfilePic] = useState(null);
  const [profileImg, setProfileImg] = useState("");
  const [userDetails, setUserDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState(null);
  const [openUploadSlide, setOpenUploadSide] = useState(false);
  const [windowWidth, setWindowWidth] = React.useState(window.innerWidth);
  const [errors, setErrors] = React.useState({});
  
  // const [showToast, setShowToast] = useState(null);
  const userInfo = Digit.UserService.getUser()?.info || {};
 const { data: mdmsData,isLoading } = Digit.Hooks.useCommonMDMS(Digit.ULBService.getStateId(), "egov-hrms", ["CommonFieldsConfig"], {
    select: (data) => {
      return {
        config: data?.MdmsRes?.['egov-hrms']?.CommonFieldsConfig
      };
    },
    retry: false,
    enable: false,
  });
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_HRMS_MUTATION_HAPPENED", false);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("EMPLOYEE_HRMS_ERROR_DATA", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_HRMS_MUTATION_SUCCESS_DATA", false);

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
  }, []);

  React.useEffect(() => {
    window.addEventListener("resize", () => setWindowWidth(window.innerWidth));
    return () => {
      window.removeEventListener("resize", () => setWindowWidth(window.innerWidth));
    };
  });

  const checkMailNameNum = (formData) => {

    const email = formData?.SelectEmployeeEmailId?.emailId || '';
    const name = formData?.SelectEmployeeName?.employeeName || '';
    const address = formData?.SelectEmployeeCorrespondenceAddress?.correspondenceAddress || '';
    const validEmail = email.length == 0 ? true : email.match(Digit.Utils.getPattern('Email'));
    return validEmail && name.match(Digit.Utils.getPattern('Name')) && address.match(Digit.Utils.getPattern('Address'));
  }
  useEffect(() => {
    if (mobileNumber && mobileNumber.length == 10 && mobileNumber.match(Digit.Utils.getPattern('MobileNo'))) {
      // setShowToast(null);
      showToast(null);
      Digit.HRMSService.search(tenantId, null, { phone: mobileNumber }).then((result, err) => {
        if (result.Employees.length > 0) {
          // setShowToast({ key: true, label: "ERR_HRMS_USER_EXIST_MOB" });
          showToast("error", t("ERR_HRMS_USER_EXIST_MOB"),5000);
          setPhonecheck(false);
        } else {
          setPhonecheck(true);
        }
      });
    } else {
      setPhonecheck(false);
    }
  }, [mobileNumber]);

  const showToast = (type, message, duration = 5000) => {
    console.log(type);
    setToast({ key: type, action: message });
    setTimeout(() => {
      setToast(null);
    }, duration);
  };
  const getUserInfo = async () => {
    const uuid = userInfo?.uuid;
    if (uuid) {
      const usersResponse = await Digit.UserService.userSearch(tenantId, { uuid: [uuid] }, {});
      usersResponse && usersResponse.user && usersResponse.user.length && setUserDetails(usersResponse.user[0]);
    }
  };

  React.useEffect(() => {
    window.addEventListener("resize", () => setWindowWidth(window.innerWidth));
    return () => {
      window.removeEventListener("resize", () => setWindowWidth(window.innerWidth));
    };
  });
  
  useEffect(() => {
    setLoading(true);

    getUserInfo();

    // setGender({
    //   i18nKey: undefined,
    //   code: userDetails?.gender,
    //   value: userDetails?.gender,
    // });

    const thumbs = userDetails?.photo?.split(",");
    setProfileImg(thumbs?.at(0));

    setLoading(false);
  }, [userDetails !== null]);

  const defaultValues = {

    Jurisdictions:
      [{
        id: undefined,
        key: 1,
        hierarchy: null,
        boundaryType: null,
        boundary: {
          code: tenantId
        },
        roles: [],
      }]
  }
  

  const removeProfilePic = () => {
    setProfilePic(null);
    setProfileImg(null);
  };
  
  const onFormValueChange = (setValue = true, formData) => {
    if (formData?.SelectEmployeePhoneNumber?.mobileNumber) {
      setMobileNumber(formData?.SelectEmployeePhoneNumber?.mobileNumber);
    } else {
      setMobileNumber(formData?.SelectEmployeePhoneNumber?.mobileNumber);
    }
    for (let i = 0; i < formData?.Jurisdictions?.length; i++) {
      let key = formData?.Jurisdictions[i];
      if (!(key?.boundary && key?.boundaryType && key?.hierarchy && key?.tenantId && key?.roles?.length > 0)) {
        setcheck(false);
        break;
      } else {
        setcheck(true);
      }
    }

    let setassigncheck = false;
    for (let i = 0; i < formData?.Assignments?.length; i++) {
      let key = formData?.Assignments[i];
      if (
        !(key.department && key.designation && key.fromDate && (formData?.Assignments[i].toDate || formData?.Assignments[i]?.isCurrentAssignment))
      ) {
        setassigncheck = false;
        break;
      } else if (formData?.Assignments[i].toDate == null && formData?.Assignments[i]?.isCurrentAssignment == false) {
        setassigncheck = false;
        break;
      } else {
        setassigncheck = true;
      }
    }
    if (
      formData?.SelectDateofEmployment?.dateOfAppointment &&
      formData?.SelectEmployeeCorrespondenceAddress?.correspondenceAddress &&
      formData?.SelectEmployeeGender?.gender.code &&
      formData?.SelectEmployeeName?.employeeName &&
      formData?.SelectEmployeeType?.code &&
      formData?.SelectEmployeePhoneNumber?.mobileNumber &&
      checkfield &&
      setassigncheck &&
      phonecheck &&
      checkMailNameNum(formData)
    ) {
      setSubmitValve(true);
    } else {
      setSubmitValve(false);
    }
  };

  const navigateToAcknowledgement = (Employees) => {
    history.replace("/digit-ui/employee/hrms/response", { Employees, key: "CREATE", action: "CREATE" });
  }


  const onSubmit = (data) => {
    if (data.Jurisdictions.filter(juris => juris.tenantId == tenantId).length == 0) {
      // setShowToast({ key: true, label: "ERR_BASE_TENANT_MANDATORY" });
      showToast("error", t("ERR_BASE_TENANT_MANDATORY"), 5000);
      return;
    }
    if (!Object.values(data.Jurisdictions.reduce((acc, sum) => {
      if (sum && sum?.tenantId) {
        acc[sum.tenantId] = acc[sum.tenantId] ? acc[sum.tenantId] + 1 : 1;
      }
      return acc;
    }, {})).every(s => s == 1)) {
      // setShowToast({ key: true, label: "ERR_INVALID_JURISDICTION" });
      showToast("error", t("ERR_INVALID_JURISDICTION"), 5000);
      return;
    }
    let roles = data?.Jurisdictions?.map((ele) => {
      return ele.roles?.map((item) => {
        item["tenantId"] = ele.boundary;
        return item;
      });
    });

    const mappedroles = [].concat.apply([], roles);
    let Employees = [
      {
        tenantId: tenantId,
        employeeStatus: "EMPLOYED",
        assignments: data?.Assignments,
        code: data?.SelectEmployeeId?.code ? data?.SelectEmployeeId?.code : undefined,
        dateOfAppointment: new Date(data?.SelectDateofEmployment?.dateOfAppointment).getTime(),
        employeeType: data?.SelectEmployeeType?.code,
        jurisdictions: data?.Jurisdictions,
        user: {
          mobileNumber: data?.SelectEmployeePhoneNumber?.mobileNumber,
          name: data?.SelectEmployeeName?.employeeName,
          correspondenceAddress: data?.SelectEmployeeCorrespondenceAddress?.correspondenceAddress,
          emailId: data?.SelectEmployeeEmailId?.emailId ? data?.SelectEmployeeEmailId?.emailId : undefined,
          gender: data?.SelectEmployeeGender?.gender.code,
          dob: new Date(data?.SelectDateofBirthEmployment?.dob).getTime(),
          roles: mappedroles,
          tenantId: tenantId,
          photo:profilePic
        },
        serviceHistory: [],
        education: [],
        tests: [],
      },
    ];
      /* use customiseCreateFormData hook to make some chnages to the Employee object */
      Employees=Digit?.Customizations?.HRMS?.customiseCreateFormData?Digit.Customizations.HRMS.customiseCreateFormData(data,Employees):Employees;

    if (data?.SelectEmployeeId?.code && data?.SelectEmployeeId?.code?.trim().length > 0) {
      Digit.HRMSService.search(tenantId, null, { codes: data?.SelectEmployeeId?.code }).then((result, err) => {
        if (result.Employees.length > 0) {
          // setShowToast({ key: true, label: "ERR_HRMS_USER_EXIST_ID" });
          showToast("error", t("ERR_HRMS_USER_EXIST_ID"), 5000);
          return;
        } else {
          navigateToAcknowledgement(Employees);
        }
      });
    } else {
      navigateToAcknowledgement(Employees);
    }
  };
  if (isLoading) {
    return <Loader />;
  }
  const onClickAddPic = () => setOpenUploadSide(!openUploadSlide);
  const closeFileUploadDrawer = () => setOpenUploadSide(false);
  const setFileStoreId = async (fileStoreId) => {
    setProfilePic(fileStoreId);

    const thumbnails = fileStoreId ? await getThumbnails([fileStoreId], stateId) : null;

    setProfileImg(thumbnails?.thumbs[0]);

    closeFileUploadDrawer();
  };
  const getThumbnails = async (ids, tenantId) => {
    const res = await Digit.UploadServices.Filefetch(ids, tenantId);
    if (res.data.fileStoreIds && res.data.fileStoreIds.length !== 0) {
      return {
        thumbs: res.data.fileStoreIds.map((o) => o.url.split(",")[3]),
        images: res.data.fileStoreIds.map((o) => Digit.Utils.getFileUrl(o.url)),
      };
    } else {
      return null;
    }
  };

  const config =mdmsData?.config?mdmsData.config: newConfig; 
  if (loading) return <Loader></Loader>;

  return (
    <div>
      <section
          style={{
            position: "relative",
            display: "flex",
            flex: userType === "citizen" ? 1 : 2.5,
            justifyContent: "center",
            alignItems: "center",
            maxWidth: "100%",
            height: "376px",
            borderRadius: "4px",
            boxShadow: userType === "citizen" ? "" : "1px 1px 4px 0px rgba(0,0,0,0.2)",
            border: `${userType === "citizen" ? "8px" : "24px"} solid #fff`,
            background: "#EEEEEE",
            padding: userType === "citizen" ? "8px" : "16px",
          }}
        >
          <div
            style={{
              position: "relative",
              height: userType === "citizen" ? "114px" : "150px",
              width: userType === "citizen" ? "114px" : "150px",
              margin: "16px",
            }}
          >
            <img
              style={{
                margin: "auto",
                borderRadius: "300px",
                justifyContent: "center",
                height: "100%",
                width: "100%",
              }}
              src={!profileImg || profileImg === "" ? defaultImage : profileImg}
            />
            <button style={{ position: "absolute", left: "50%", bottom: "-24px", transform: "translateX(-50%)" }} onClick={onClickAddPic}>
              <CameraIcon />
            </button>
          </div>
        </section>
        
        {toast && (
        <Toast
          error={toast.key === "error"}
          label={t(toast.key === "success" ? `CORE_COMMON_PROFILE_UPDATE_SUCCESS` : toast.action)}
          onClose={() => setToast(null)}
          style={{ maxWidth: "670px" }}
        />
      )}  

      {openUploadSlide == true ? (
        <UploadDrawer
          setProfilePic={setFileStoreId}
          closeDrawer={closeFileUploadDrawer}
          userType={userType}
          removeProfilePic={removeProfilePic}
          showToast={showToast}
        />
      ) : (
        ""
      )}

      <FormComposer

        defaultValues={defaultValues}
        heading={t("HR_COMMON_CREATE_EMPLOYEE_HEADER")}
        config={config}
        onSubmit={onSubmit}
        onFormValueChange={onFormValueChange}
        isDisabled={!canSubmit}
        label={t("HR_COMMON_BUTTON_SUBMIT")}
      />
      {/* {showToast && (
        <Toast
          error={showToast.key}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )} */}
    </div>
  );
};
export default CreateEmployee;
