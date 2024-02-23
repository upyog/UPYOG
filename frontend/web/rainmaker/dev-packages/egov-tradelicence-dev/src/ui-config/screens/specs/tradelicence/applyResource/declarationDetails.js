import {
  getBreak,
  getCommonTitle,
  getCommonContainer,
} from "egov-ui-framework/ui-config/screens/specs/utils";
let nnname;
let demo="(above)";

export const selfdata =async (state, dispatch) =>{
  debugger
  nnname= state.screenConfiguration.preparedFinalObject.Licenses[0].businessService;
  
}

const declarationDetails = getCommonContainer({
  checkbox: {
    uiFramework: "custom-containers-local",
    moduleName: "egov-tradelicence",
    componentPath: "CheckboxContainer",
    jsonPath: "Licenses[0].isDeclared",
    props: {
      label: {
        labelName:
          "I hereby Solemnly                                                             affirm and declare that the information as furnished is true and correct to the best of my knowledge and belief. I/ We have not been barred for building construction activities by any competent authority and further undertake that if any information at any stage shall be found to be false, my registration shall be liable to be canceled without any prior notice in that regard and I shall not claim any compensation etc. for such a default on my part. In case of any discrepancies found later, I shall be liable for punishment under the relevant provisions of Law as also under Municipal Act and the Act.",
        labelKey: ` I / we applicant of trade license do hereby solemnly affirm and undertake as follows:
        That I am /We are                                             the Proprietor/partner(s)/Director(s)/Manager(s)/Karta  of the above mention trade at above mentioned address.
        1(a) That the above referred                                             Establishment is being used as mentioned in the application  only (Nature of trade/trades, callings and professions).
        (b) I/We hereby solemnly affirm and state that the business which l/We have started isn't restrained or
        banned or prohibited by any Act, rules, Law or Order of any Court of Law or any competent authority and
        the premises.
        (c) The premises where I am/We are carrying out the said profession, trade and calling is free from any
        encumbrances and encroachments.
        (d) I/We have obtained the necessary statutory Certificates, licenses, permissions, permits etc. for
        carrying out of this trade from the appropriate authority in accordance with law.
        (e) That I agree that the trade license is being issued in the name of the owner/establishment and do not
        in any way carry any legal title, right or ownership. That I shall not claim any right/title in the property/
        premises on the basis of the said trade license.
        2) That I have gone through all the standards & technical requirements, terms & conditions as described
        in the Rules, Byelaws, Govt. Instructions etc. and have fulfilled the same and the trade being applied for
        is not a hazardous/obnoxious/ prohibited trade under the Punjab Municipal Corporation Act, 1976/ the
        Punjab Municipal Act, 1911 as the case may be.
        3) That I further undertake that I shall not claim any regularization of unauthorised construction and
        ownership nor raise any other type of claim on the basis of the Trade license being granted to us and the
        said building /shop is safe in all respects.
        4) That I will not hold the Municipal Corporation/ Municipal Council/Nagar Panchayat responsible or
        liable for any action against me by authority, arising from or out of the grant of the said trade license and
        completely indemnify/ keep harmless of Municipal Corporation/ Municipal Council/Nagar Panchayat of
        any action that may be taken or proposed to be taken by any of the authority of Government Department
        or any Court of Law.
        5) That there is no court case on account of violation of any law pending against the above
        owner/establishment with respect to grant of Trade license and I fully indemnify Municipal Corporation/
        Municipal Council/Nagar Panchayat for any loss due to court case or any reason whatsoever with regard
        to dispute being applied/sanctioned herewith.
        6) That I undertake to ensure that I have obtained due approvals as per law to dispose of pollution if any,
        and waste caused by running the above said trade at the above said premises.
        7) That I undertake to ensure that I have obtained no dues certificate as per law for the above said
        premises.

        8) That I/We hereby declare that the information provided above is true and correct to the best of my/our
        personal knowledge, information and belief. I am/We are fully aware about the consequences of giving
        false information. If the information is found to be false, l/We shall he liable for prosecution and
        punishment under the Indian Penal Code, 1860 and/or the Punjab Municipal Corporation Act, 1976/
        Punjab Municipal Act, 1911 as the case may be and /or any other law applicable there to in addition to
        the cancellation of Trade License.
        `,
      },
      jsonPath: "Licenses[0].isDeclared",
    },
     visible: process.env.REACT_APP_NAME === "Citizen" ? true : false,
    type: "array",
  },
});

export const declarationSummary = getCommonContainer({
  
  headers: getCommonTitle(
    {
      labelName: "Declaration",
      labelKey: "SELF DECLARATION",
      
    },
    
    {
      style: {
        marginBottom: 10,
        marginTop: 18,
      },
    }
    
  ),
  header: {
    uiFramework: "custom-atoms",
    componentPath: "Container",
    props: {
      style: {
        margin: "10px",
      },
    },
    children: {
      body: declarationDetails,
    },
  },

});

