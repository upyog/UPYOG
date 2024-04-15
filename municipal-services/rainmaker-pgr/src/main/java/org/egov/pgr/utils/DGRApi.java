package org.egov.pgr.utils;

import okhttp3.*;

public class DGRApi {	

	public String apiCalling(String complaintId)
	{
		String jsonData = null;
	OkHttpClient client = new OkHttpClient().newBuilder()
			  .followRedirects(false)
			  .build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, " {\"Complaint_Id\":\""+complaintId+"\", \r\n \"Remarks\":\"Resolved Succesffully\", \r\n \"Status\":\"resolved\"}");
			Request request = new Request.Builder()
			  .url("http://devgrievanceapi.psegs.in/api/grievance/GetComplaintStatus_PMIDC")
			  .method("POST", body)
			  .addHeader("Content-Type", "application/json")
			  .build();
			Response responses = null;
			try {
				responses = client.newCall(request).execute();
				jsonData = responses.body().string();
				
				
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
			return jsonData;
	}

}
