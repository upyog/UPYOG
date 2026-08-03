import { CREATE_COMPLAINT } from "./types";

const createComplaint = ({
  cityCode,
  complaintType,
  priorityLevel,
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
  emailId,
  additionalDetails
}) => async (dispatch, getState) => {
  const response = await Digit.Complaint.create({
    cityCode,
    complaintType,
    priorityLevel,
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
    emailId,
    additionalDetails
  });
  dispatch({
    type: CREATE_COMPLAINT,
    payload: response,
  });
  // Return response so the dispatching component can capture and persist it
  return response;
};

export default createComplaint;
