  /** 
  * @author - Shivank - NIUA

  This Page is consent page which is self delclare by the architect before forwarding the design/plan to citizen.

  Self declaration contain the content which is hardcoded for now because it is not going to change.

  This consent is open as a pop-up window when architect wants to see the declaration, this pop-up window
  logic and link both written and inserted in checkpage.js.
  
  CSS for modal container and content is written here because CSS is specific for this consent page.

  external libraries are used like - 
  1. Modal for Pop-up window and 
  2. jsPDF for downloading the PDF

  */
  import React, { useState } from 'react';
  import Modal from 'react-modal';
  import jsPDF from 'jspdf';
  import { useLocation } from "react-router-dom";
  import { SubmitBar, CitizenConsentForm } from '@upyog/digit-ui-react-components';
  import { useTranslation } from "react-i18next";
  import Axios from "axios";
  import Urls from '../../../../../../libraries/src/services/atoms/urls'; 



  const Architectconcent = ({ showTermsPopup, setShowTermsPopup, otpVerifiedTimestamp }) => {
    
    const [isModalOpen, setIsModalOpen] = useState(false);
    const { state } = useLocation();
    const { t } = useTranslation();
    const user = Digit.UserService.getUser();
    const architecname = user?.info?.name;
    const architectmobileNumber = user?.info.mobileNumber
    const [params] = Digit.Hooks.useSessionStorage("BUILDING_PERMIT", state?.edcrNumber ? { data: { scrutinyNumber: { edcrNumber: state?.edcrNumber } } } : {});

    
    const architectid = params?.additionalDetails?.architectid;
    const ownername = params?.owners?.owners?.[0]?.name;
    const architecttype = params?.additionalDetails?.typeOfArchitect;
    const khasranumber = params?.additionalDetails?.khasraNumber;
    const ulbname = params?.additionalDetails?.District;
    const district = params?.additionalDetails?.UlbName;
    const ward = params?.additionalDetails?.wardnumber;
    const area = params?.additionalDetails?.area;
    const zone = params?.additionalDetails?.zonenumber;
    const ulbgrade = params?.address?.city?.city?.ulbGrade
    const TimeStamp = otpVerifiedTimestamp;


     

    const selfdeclarationform =
    `
    To:

    ${ulbgrade}
    ULB ${ulbname} 
    District ${district}
    
    Dear Sir or Madam,

   I, under signed Shri/Smt/Kum ${architecname} (${architecttype}) having Registration No. ${architectid} is 
   appointed by the ${ownername} for the development on land bearing Kh. No ${khasranumber} of ${ulbname},
   Area ${area} (Sq.mts).
    
   This site falls in ward number ${ward} zone number ${zone}  in the Master plan of ${district} and 
   the proposed Residential/Commercial/Industrial construction is permissible in this area.
  
   I am currently registered as ${architecttype} with the Competent Authority and empanelled 
   under Self-Certification Scheme.
  
   I hereby certify that I/we have appointed by the owner to prepare the plans, sections and details, 
   structural details as required under the Punjab Municipal Building Byelaws for the above mentioned 
   project. 
  
   That the drawings prepared and uploaded along with other necessary documents on this 
   UPYOG Platform are as per the provisions of Punjab Municipal Building Byelaws and this building 
   plan has been applied under Self-Certification Scheme. 
  
   I certify that:
   
   That I am fully conversant with the provisions of the Punjab Municipal Building Byelaws and other 
   applicable instructions/ regulations, which are in force and I undertake to fulfill the same.
  
   That plans have been prepared within the framework of provisions of the Master Plan and 
   applicable Building Bye Laws / Regulations. 
  
   That site does not falls in any prohibited area/ government land/ encroachment or any other land 
   restricted for building construction or in any unauthorized colony. 
  
   That plan is in conformity to structural safety norms. 
  
   That I have seen the originals of all the documents uploaded and Nothing is concealed thereof. 
  
   That all the requisite documents/NOC required to be uploaded have been uploaded on E-Naksha 
   portal along with plan. 
  
   That above stated facts are true and all the requisite documents uploaded with this E-Naksha plan 
   have been signed by the owner/owners in my presence.

    Architect Name - ${architecname} (${architecttype})
    Architect Id - ${architectid}
    Mobile Number - ${architectmobileNumber}
    Signature - Verified By OTP at ${TimeStamp}
    `;

    const openModal = () => {
      setIsModalOpen(true);
    };

    const closeModal = () => {
      setShowTermsPopup(false);
    };

    


    const uploadSelfDeclaration = async () => {
      try {
        const doc = new jsPDF();
        const leftMargin = 15;
        const topMargin = 10;
        const lineSpacing = 5;
        const pageWidth = doc.internal.pageSize.getWidth();
        const maxLineWidth = pageWidth - 2 * leftMargin;
  
        let currentY = topMargin;
  
        doc.setFont("Times-Roman");
        doc.setFontSize(12);
  
       // Split and write text into the PDF
    const lines = selfdeclarationform.split("\n");
    lines.forEach((line) => {
      const wrappedLines = doc.splitTextToSize(line, maxLineWidth);
      wrappedLines.forEach((wrappedLine) => {
        if (currentY + lineSpacing > doc.internal.pageSize.getHeight() - topMargin) {
          doc.addPage();
          currentY = topMargin;
        }
        doc.text(leftMargin, currentY, wrappedLine);
        currentY += lineSpacing;
      });
    });

    // Convert the PDF to a Blob
    const pdfBlob = doc.output("blob", "declaration.pdf");

    // Prepare FormData for the upload
    const formData = new FormData();
    formData.append("file", pdfBlob, "declaration.pdf"); 
    formData.append("tenantId", "pg"); 
    formData.append("module", "BPA"); 

   
    const response = await Axios({
      method: "post",
      url: `${Urls.FileStore}`, 
      data: formData,
      headers: { "auth-token": Digit.UserService.getUser()?.access_token },
    });

    if (response?.data?.files?.length > 0) {
      alert("File Uploaded Successfully");
      sessionStorage.setItem("ArchitectConsentdocFilestoreid",response?.data?.files[0]?.fileStoreId);
    } else {
      alert("File Upload Failed"); 
    }
  } catch (error) {
    alert("Error Uploading PDF:", error); // Error handling
  }
};
    
    
    
    
    
    
    
    const modalStyles = {
      modal: {
        width: "100%",
        height: "100%",
        top: "0",
        position: "relative",
        backgroundColor: 'rgba(0, 0, 0, 0.7)',
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        
      },
      modalOverlay: {
        position: 'fixed',
        top: '0',
        left: '0',
        width: '100%',
        height: '100%',
        backgroundColor: 'rgba(0, 0, 0, 0.7)'
      },
      modalContent: {
        backgroundColor: '#FFFFFF',
        padding: '2rem', 
        borderRadius: '0.5rem', 
        maxWidth: '800px',
        margin: 'auto',
        fontFamily: 'Roboto, serif',
        overflowX: 'hidden', 
        textAlign: 'justify',
        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)', 
        maxHeight: '80vh', 
        overflowY: 'auto', 
      },
      heading: {
        textAlign: 'center',
        fontWeight: 'bold',
        marginBottom: '10px',
        
      },
      subheading: {
        textAlign: 'center',
        fontWeight: 'bold',
        marginBottom: '20px',
       
      },
    };

    return (
      <div>
        <Modal
          isOpen={showTermsPopup}
          onRequestClose={closeModal}
          contentLabel="Self-Declaration"
          style={{
            modal: modalStyles.modal,
            overlay: modalStyles.modalOverlay,
            content: modalStyles.modalContent,
          }}
        >
          <div>
            <h2 style={modalStyles.heading}>DECLARATION UNDER SELF-CERTIFICATION SCHEME</h2>
            <h3 style={modalStyles.subheading}>(For proposed Construction)</h3>
            <h3 style={modalStyles.subheading}>(By Architect/ Civil Engineer/ Building Designer and Supervisor)</h3>
            <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word', textAlign: 'justify', fontFamily: 'Roboto, serif'}}>{selfdeclarationform}</pre>            
            

            <div>
              <SubmitBar label={t("BPA_CLOSE")} onSubmit={closeModal} />
              <br></br>
              <br></br>
              <SubmitBar label={t("BPA_UPLOAD")} onSubmit={uploadSelfDeclaration} />
            </div>
          </div>
        </Modal>
      </div>
    );
  };

  export default Architectconcent;