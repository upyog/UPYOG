import { CREATE_COMPLAINT } from "./types";

const createComplaint = ({
  cityCode,
  complaintType,
  description,
  landmark,
  city,
  district,
  region,
  state,
  pincode,
  localityCode,
  localityName,
  uploadedImages,
  mobileNumber,
  name,
  additionalDetails
}) => async (dispatch, getState) => {
  const response = await Digit.Complaint.create({
    cityCode,
    complaintType,
    description,
    landmark,
    city,
    district,
    region,
    state,
    pincode,
    localityCode,
    localityName,
    uploadedImages,
    mobileNumber,
    name,
    additionalDetails
  });
  dispatch({
    type: CREATE_COMPLAINT,
    payload: response,
  });
};

export default createComplaint;
