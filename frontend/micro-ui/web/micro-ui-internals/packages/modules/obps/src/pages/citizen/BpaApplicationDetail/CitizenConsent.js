
  import React, { useState } from 'react';
  import Modal from 'react-modal';
  import jsPDF from 'jspdf';
  import { useLocation } from "react-router-dom";
  import { SubmitBar, CitizenConsentForm } from '@upyog/digit-ui-react-components';
  import { useTranslation } from "react-i18next";
  import { useParams } from "react-router-dom";
  import Axios from "axios";
  import Urls from '../../../../../../libraries/src/services/atoms/urls';



  const CitizenConsent = ({ showTermsPopup, setShowTermsPopup, otpVerifiedTimestamp }) => {
    
    const [isModalOpen, setIsModalOpen] = useState(false);
    
    const { t } = useTranslation();
    const user = Digit.UserService.getUser();
    const ownername = user?.info?.name;
    const ownermobileNumber = user?.info.mobileNumber
    const ownerEmail = user?.info?.emailId 
    const { id } = useParams();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const { data } = Digit.Hooks.obps.useBPADetailsPage(tenantId, { applicationNo: id });
    let workflowDetails = Digit.Hooks.useWorkflowDetails({
      tenantId: data?.applicationData?.tenantId,
      id: id,
      moduleCode: "OBPS",
      config: {
        enabled: !!data
      }
    });

    const [isUploading, setIsUploading] = useState(false); // it will check whether the file upload is in process or not
    const [isFileUploaded, setIsFileUploaded] = useState(false);
    const architectname = workflowDetails?.data?.timeline?.[0]?.assigner?.name
    const mobileNumber = workflowDetails?.data?.timeline?.[0]?.assigner?.mobileNumber
    const khasranumber = data?.applicationData?.additionalDetails?.khasraNumber;
    const ulbname = data?.applicationData?.additionalDetails?.UlbName;
    const district = data?.applicationData?.additionalDetails?.District;
    const ward = data?.applicationData?.additionalDetails?.wardnumber;
    const area = data?.applicationData?.additionalDetails?.area;
    const applicationnumber = data?.applicationNo
    const architectid = data?.applicationData?.additionalDetails?.architectid
    const architecttype =  data?.applicationData?.additionalDetails?.typeOfArchitect
    const TimeStamp = otpVerifiedTimestamp;
    const selfdeclarationform =
    `
    To:

    Executive Officer,
    ${ulbname} 
    ${district}

    Dear Sir or Madam,

    I/We, Shri/Smt/Kum. ${ownername} under signed owner of land bearing Kh. No. ${khasranumber} of ULB ${ulbname}
    Area ${area} (Sq.mts.), ward number ${ward}, City ${district}
    
    I/We hereby declare that the Architect name ${architectname} (${architecttype}) Architect ID - ${architectid} is 
    appointed by me/us and is authorized to make representation/application with regard to aforesaid 
    construction to any of the authorities.

    I/We further declare that I am/We are aware of all the action taken or representation made by the 
    ${architecttype} authorized by me/us.

    i) That I am/We are sole owner of the site.

    ii) There is no dispute regarding the site and if any dispute arises then I shall be solely 
    responsible for the same.

    iii) That construction of the building will be undertaken as per the approved building plans 
    and structural design given by the Structural Engineer.

    That above stated facts are true and the requisite documents have been uploaded with this 
    E-Naksha plan and nothing has been concealed thereof.


    Name of Owner - ${ownername}
    Mobile Number - ${ownermobileNumber}
    Application Number - ${applicationnumber}
    Email Id  - ${ownerEmail}
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
        setIsUploading(true); // Set isUploading to true before starting the upload
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
      sessionStorage.setItem("CitizenConsentdocFilestoreid",response?.data?.files[0]?.fileStoreId);
      setIsFileUploaded(true); // Set isFileUploaded to true on successful upload
    } else {
      alert("File Upload Failed"); 
    }
    } catch (error) {
      alert("Error Uploading PDF:", error); // Error handling
    }
    finally {
      setIsUploading(false); // Set isUploading to false after the upload is complete
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
            <h3 style={modalStyles.subheading}>(By OWNER)</h3>
            <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word', textAlign: 'justify', fontFamily: 'Roboto, serif'}}>{selfdeclarationform}</pre>            
            

            <div>
              <SubmitBar label={t("BPA_CLOSE")} onSubmit={closeModal} />
              <br></br>
              <br></br>
              <SubmitBar label={t("BPA_UPLOAD")} onSubmit={uploadSelfDeclaration} disabled={isUploading || isFileUploaded} />
            </div>
          </div>
        </Modal>
      </div>
    );
  };

  export default CitizenConsent;