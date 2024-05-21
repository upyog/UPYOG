import React, { useEffect, useState } from "react";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch, useParams } from "react-router-dom";
import { newConfigAmalgamate } from "../../../config/Amalgamate/config";

import { useTranslation } from "react-i18next";
// import { Card, Dropdown, FormComposer, Loader, Modal, ResponseComposer, Toast } from "@egovernments/digit-ui-react-components";
import Timeline from "../../../components/TLTimeline";
import {
    CardLabel,
    CardLabelError,
    CitizenInfoLabel,
    Dropdown,
    FormStep,
    LabelFieldPair,
    Loader,
    Modal,
    RadioButtons,
    UploadFile,
  } from "@egovernments/digit-ui-react-components";
// import AppealDetails from "./AppealDetails";
// import AppealAcknowledgement from "./AppealAcknowledgement";

  const InputError = ({ message }) => {
    return (
      <p class="flex items-center gap-1 px-2 font-semibold text-red-500 bg-red-100 rounded-md" style="opacity: 1; transform: none;">
        <svg stroke="currentColor" fill="currentColor" stroke-width="0" viewBox="0 0 24 24" height="1em" width="1em" xmlns="http://www.w3.org/2000/svg"><path fill="none" d="M0 0h24v24H0z"></path><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"></path></svg>
        {message}
      </p>
    )
  }

const MyNotices = (props) => {
  const { t } = useTranslation();
  const match = useRouteMatch();
  const { pathname } = useLocation();
  const history = useHistory();

  const [tableList, setTableList] = useState([]);
//   const tenantId = Digit.ULBService.getCurrentTenantId();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);

//   const tenantId = window.localStorage.getItem("Citizen.tenant-id") || stateCode;

  const onInit = async ()=>{
    try {
      // TODO: change module in file storage
      const response = await Digit.PTService.noticeSearch({},tenantId);
      console.log("response==",response)
      if (response) {
      } else {
      }
    } catch (err) {
    }
  }

  onInit();
  
  return (
    <React.Fragment>
        <div className="row appeal-row-cls">
                    <div className="col-sm-12">
                    {tableList && tableList.length>0 &&
                        <div style={{ width: '100%' }}>
                          <table style={{ width: '100%', border: '1px solid #b7b7b7'}}>
                            <tr style={{background: '#eaeaea', lineHeight: '35px'}}>
                              <th style={{paddingLeft: "10px"}}>Sr. No.</th>
                              <th>Document Type</th>
                              <th>Document Name</th>
                            </tr>
                            {tableList.map((e, i)=>{
                              return (<tr>
                                <td style={{paddingLeft: "10px"}}>{i+1}</td>
                                <td>{e?.documentType}</td>
                                <td>{e?.documentName}</td>
                              </tr>)
                            })}
                          </table>
                        </div>}
                    </div>
                    
                </div>
    
    </React.Fragment>
  );
};

export default MyNotices;
