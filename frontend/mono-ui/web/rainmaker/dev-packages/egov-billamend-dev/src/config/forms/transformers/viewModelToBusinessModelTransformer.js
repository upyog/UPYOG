import { prepareFormData, getTenantForLatLng } from "egov-ui-kit/utils/commons";
import get from "lodash/get";
import { getTenantId, getUserInfo, localStorageGet } from "egov-ui-kit/utils/localStorageUtils";

const updateComplaintStatus = (state, form) => {
  const formData = prepareFormData(form);
  const serviceRequestId = decodeURIComponent(window.location.pathname.split("/").pop());
  const complaint = state.complaints.byId[serviceRequestId];
  if (!formData.services) {
    formData.services = [];
    formData.services[0] = complaint;
  } else {
    formData.services[0] = { ...formData.services[0], ...complaint };
  }
  return formData;
};

const filterObjByKey = (obj, predicate) => {
  return Object.keys(obj)
    .filter((key) => predicate(key))
    .reduce((res, key) => ((res[key] = obj[key]), res), {});
};

const transformer = (formKey, form = {}, state = {}) => {
  const transformers = {
    assignComplaint: () => {
      const formData = prepareFormData(form);
      const serviceRequestId = decodeURIComponent(window.location.pathname.split("/").pop());
      const serviceData = state.complaints.byId[serviceRequestId];
      var filteredServiceData = filterObjByKey(serviceData, (key) => key !== "actions");
      if (!formData.services) formData.services = [];
      formData.services[0] = filteredServiceData;
      return formData;
    },

    comment: () => {
      const formData = prepareFormData(form);
      const serviceRequestId = decodeURIComponent(window.location.pathname.split("/").pop());
      const serviceData = state.complaints.byId[serviceRequestId];
      var filteredServiceData = filterObjByKey(serviceData, (key) => key !== "actions");
      if (!formData.services) formData.services = [];
      formData.services[0] = filteredServiceData;
      return formData;
    },
    requestReassign: () => {
      return updateComplaintStatus(state, form);
    },
    reopenComplaint: () => {
      return updateComplaintStatus(state, form);
    },
    feedback: () => {
      return updateComplaintStatus(state, form);
    },
    rejectComplaint: () => {
      return updateComplaintStatus(state, form);
    },
    complaintResolved: () => {
      return updateComplaintStatus(state, form);
    },
    profile: () => {
      const { fields } = form;
      let { userInfo: user } = state.auth;
      user = { ...user, name: fields.name.value, permanentCity: fields.city.value, emailId: fields.email.value };
      const photos = form.files && form.files["photo"];
      let photo = (photos && photos.length && photos[0]) || null;
      photo = photo ? photo.fileStoreId || photo.imageUri : null;
      user = { ...user, photo };
      return { user };
    },
    profileEmployee: () => {
      const { fields } = form;
      let { userInfo: user } = state.auth;
      user = { ...user, name: fields.name.value, mobileNumber: fields.phonenumber.value, emailId: fields.email.value };
      const photos = form.files && form.files["photo"];
      let photo = (photos && photos.length && photos[0]) || null;
      photo = photo ? photo.fileStoreId || photo.imageUri : null;
      user = { ...user, photo };
      return { user };
    },
    otp: () => {
      const { previousRoute } = state.app;
      const { fields: otpFields } = form;
      let fields;

      if (previousRoute.endsWith("register")) {
        fields = state.form["register"].fields;
        fields = {
          ...otpFields,
          username: {
            jsonPath: "User.username",
            value: fields.phone.value,
          },
          name: {
            jsonPath: "User.name",
            value: fields.name.value,
          },
          tenantId: {
            jsonPath: "User.tenantId",
            value: fields.city.value,
          },
        };
      } else if (previousRoute.endsWith("login")) {
        fields = state.form["login"].fields;
        fields = {
          password: {
            jsonPath: "login.password",
            value: otpFields.otp.value,
          },
          username: {
            jsonPath: "login.username",
            value: fields.phone.value,
          },
        };
      }
      return prepareFormData({ ...form, fields });
    },
    employeeOTP: () => {
      const formData = prepareFormData(form);
      const { fields } = state.form.employeeForgotPasswd || {};
      formData.tenantId = fields.tenantId.value;
      formData.type="EMPLOYEE";
      return formData;
    },
    employeeChangePassword: () => {
      const formData = prepareFormData(form);
      const { auth } = state;
      const username = get(auth, "userInfo.userName");
      const type = process.env.REACT_APP_NAME === "Citizen" ? "CITIZEN" : "EMPLOYEE";
      const tenantId = getTenantId();
      formData.tenantId = tenantId;
      formData.username = username;
      formData.type = type;
      return formData;
    },
    complaint: async () => {
      const formData = prepareFormData(form);
      const userInfo = getUserInfo();
      const isNative = JSON.parse(localStorageGet("isNative"));
      // let userRole = null;
      try {
        const { phone } = form.fields;
        formData.services[0].phone = phone.value;
      } catch (error) {}

      try {
        const { latitude, longitude, city } = form.fields;
        let tenantId = "";
        if (latitude.value) {
          tenantId = await getTenantForLatLng(latitude.value, longitude.value);
        } else {
          tenantId = city.value && city.value;
        }
        formData.services[0].tenantId = tenantId;
        const userRolesArray = JSON.parse(userInfo).roles.filter(item => item.tenantId === tenantId || item.tenantId === process.env.REACT_APP_DEFAULT_TENANT_ID);
        const index = userRolesArray.findIndex((role) => {
          return role.code === "CSR";
        });
        formData.services[0].source = index > -1 ? "ivr" : isNative ? "mobileapp" : "web" ;
      } catch (error) {
        throw new Error(error.message);
      }
      return formData;
    },
  };

  if (formKey in transformers) {
    try {
      return transformers[formKey]();
    } catch (error) {
      throw new Error(error.message);
    }
  } else {
    return prepareFormData(form);
  }
};

export default transformer;
