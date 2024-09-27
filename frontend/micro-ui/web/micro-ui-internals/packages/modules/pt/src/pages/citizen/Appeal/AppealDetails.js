import { CheckBox } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
const AppealDetails = (props) =>{
    const { t } = useTranslation();
    const onChangeCheck = (e)=>{
        props.setdeclarationhandler(e.target.checked)
    }

    return (
        <React.Fragment>
            <div className="card">
                <div><h2 style={{fontWeight: "600", marginBottom: "20px"}}>Appeal Details</h2></div>
                <div className="row">
                    <div className="col-sm-6">
                        <label>Appeal No</label>
                        <div>AP/001/00234</div>
                    </div>
                    <div className="col-sm-6">
                        <label>For the year</label>
                        <div>{props?.formData?.assessmentYear}</div>
                    </div>
                    <div className="col-sm-6">
                        <label>Name of Owner</label>
                        <div>{props?.formData?.ownerName}</div>
                    </div>
                    <div className="col-sm-6">
                        <label>Property Address</label>
                        <div>{props?.formData?.propertyAddress}</div>
                    </div>
                    <div className="col-sm-6">
                        <label>Property Id</label>
                        <div>{props?.formData?.propertyId}</div>
                    </div>
                    <div className="col-sm-6">
                        <label>Name of Assessing Officer</label>
                        <div>{props?.formData?.nameOfAssessingOfficer}</div>
                    </div>
                    <div className="col-sm-6">
                        <label>Assessing Officer Designation</label>
                        <div>{props?.formData?.assessingOfficerDesignation}</div>
                    </div>
                </div>
                <hr style={{marginBottom: "10px", marginTop: "10px"}} />
                <div className="row">
                    <div className="col-sm-4">
                        <label>Rule under which order passed</label>
                        <div>{props?.formData?.ruleUnderOrderPassed}</div>
                    </div>
                    <div className="col-sm-4">
                        <label>Date of Order</label>
                        <div>{props?.formData?.dateOfOrder}</div>
                    </div>
                    <div className="col-sm-4">
                        <label>Date of Service</label>
                        <div>{props?.formData?.dateOfService}</div>
                    </div>
                </div>
                <hr style={{marginBottom: "10px", marginTop: "10px"}} />
                <div>
                <div style={{fontWeight: "600", marginBottom: "10px"}}>Admitted tax liability under The Manipur Municipality Rules, 2019</div>
                    <div className="row">
                        <div className="col-sm-4">
                            <label>Date of Payment</label>
                            <div>{props?.formData?.dateOfPayment}</div>
                        </div>
                        <div className="col-sm-4">
                            <label>Applicant Address</label>
                            <div>{props?.formData?.applicantAddress}</div>
                        </div>
                        <div className="col-sm-4">
                            <label>Relief Claimed in Appeal</label>
                            <div>{props?.formData?.reliefClaimedInAppeal}</div>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col-sm-12">
                            <label>Statement of Facts</label>
                            <div>{props?.formData?.statementOfFacts}</div>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col-sm-12">
                            <label>Ground of Appeal</label>
                            <div>{props?.formData?.groundOfAppeal}</div>
                        </div>
                    </div>
                </div>
                <hr style={{marginBottom: "10px", marginTop: "10px"}} />
                <div style={{fontWeight: "600", marginBottom: "10px"}}>List of Documents</div>
                <div>
                <div className="row appeal-row-cls">
                    <div className="col-sm-12">
                    {props?.formData?.documents && props?.formData?.documents.length>0 &&
                        <div style={{ width: '100%' }}>
                          <table style={{ width: '100%', border: '1px solid #b7b7b7'}}>
                            <tr style={{background: '#eaeaea', lineHeight: '35px'}}>
                              <th style={{paddingLeft: "10px"}}>Sr. No.</th>
                              <th>Document Type</th>
                              <th>Document Name</th>
                            </tr>
                            {props?.formData?.documents.map((e, i)=>{
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
                </div>
                <hr style={{marginBottom: "10px", marginTop: "10px"}} />
                <div className="row">
                    <div className="col-sm-12">
                        <CheckBox
                        label={t("I hereby declare and affirm that the above-furnished information is true and correct and nothing has been concealed therefrom. I am also aware of the fact that in case this information is found false/incorrect, the authorities are at liberty to initiate recovery of amount / interest / penalty / fine as provided in Manipur Municipality Act 1994 or Manipur Municipality (Property Tax) Rules 2019.")}
                        onChange={onChangeCheck}
                        styles={{ height: "auto" }}
                        // checked={true}
                        // value={true}
                        //disabled={!agree}
                        />
                    </div>
                
                </div>
            </div>
        </React.Fragment>
    )

}
export default AppealDetails;