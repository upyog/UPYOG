package org.ksmart.birth.birthcommon.services;

import com.jayway.jsonpath.JsonPath;
import org.ksmart.birth.birthcommon.model.WorkFlowCheck;
import org.ksmart.birth.utils.BirthConstants;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
@Service
public class CommonValidationFromMdms {

    public WorkFlowCheck checkValidation(Object mdmsData, String birthPlace, Long dob, WorkFlowCheck wfc) {
        Calendar calendar = Calendar.getInstance();
        Long currentDate = calendar.getTimeInMillis();
        List<LinkedHashMap<String, Object>> wfLists = JsonPath.read(mdmsData, BirthConstants.CR_MDMS_BIRTH_NEW_WF_JSONPATH + "[*]");

        for (int n = 0; n < wfLists.size(); n++) {
            if((wfLists.get(n).get("startdateperiod") != null) && (wfLists.get(n).get("enddateperiod") != null)) {
                String startStr = wfLists.get(n).get("startdateperiod").toString();
                String endStr = wfLists.get(n).get("enddateperiod").toString();
                Long start = Long.parseLong(startStr);
                Long end = Long.parseLong(endStr);
                if (wfLists.get(n).get("BirtPlace") != null) {
                    if (wfLists.get(n).get("BirtPlace").equals(birthPlace)) {
                        if (end > 0L) {
                            Long comp = currentDate - dob;
                            if (comp < end && comp >= start) {
                                wfc.setApplicationType(wfLists.get(n).get("ApplicationType").toString());
                                wfc.setWorkflowCode(wfLists.get(n).get("WorkflowCode").toString());
                                if (wfLists.get(n).get("payment").toString().equals("true"))
                                    wfc.setPayment(true);
                                else
                                    wfc.setPayment(false);
                                wfc.setAmount(Integer.parseInt(wfLists.get(n).get("amount").toString()));
                                wfc.setActive(Boolean.getBoolean(wfLists.get(n).get("active").toString()));
                            }
                        } else {
                            Long comp = currentDate - dob;
                            if (comp >= start) {
                                wfc.setApplicationType(wfLists.get(n).get("ApplicationType").toString());
                                wfc.setWorkflowCode(wfLists.get(n).get("WorkflowCode").toString());
                                if (wfLists.get(n).get("payment").toString().equals("true"))
                                    wfc.setPayment(true);
                                else
                                    wfc.setPayment(false);
                                wfc.setAmount(Integer.parseInt(wfLists.get(n).get("amount").toString()));
                                wfc.setActive(Boolean.getBoolean(wfLists.get(n).get("active").toString()));
                            }
                        }
                    }
                }
            }
        }
        return wfc;
    }
}



