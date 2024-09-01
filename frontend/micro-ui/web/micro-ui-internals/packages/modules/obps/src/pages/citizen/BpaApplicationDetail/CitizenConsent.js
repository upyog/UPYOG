
  import React, { useState, useEffect } from 'react';
  import Modal from 'react-modal';
  import { SubmitBar } from '@upyog/digit-ui-react-components';
  import { useTranslation } from "react-i18next";
  import { useParams } from "react-router-dom";


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
    const ulbselection = data?. applicationData?.additionalDetails?.Ulblisttype === "Municipal Corporation" ? "Commissioner" : "Executive Officer"

    const updatedAdditionalDetails = {
      ...data?.applicationData?.additionalDetails,
      TimeStamp: otpVerifiedTimestamp,
    };
  
    // Update the entire data object with the new additionalDetails
    const updatedData = {
      ...data,
      applicationData: {
        ...data?.applicationData,
        additionalDetails: updatedAdditionalDetails,
      },
    };
  



    const selfdeclarationform =
    `
    To,
    <b>${ulbselection}</b>
    <b>${ulbname}</b> 
    
    Dear Sir or Madam,

    I/We, Shri/Smt/Kum. <b>${ownername}</b> under signed owner of land bearing Kh. No. <b>${khasranumber}</b> of ULB 
    <b>${ulbname}</b> Area <b>${area}</b> (Sq.mts.), ward number <b>${ward}</b>, City <b>${district}</b>
    
    I/We hereby declare that the Architect name <b>${architectname}</b> (<b>${architecttype}</b>) Architect ID 
    <b>${architectid}</b> is appointed by me/us and is authorized to make representation/application 
    with regard to aforesaid construction to any of the authorities.

    I/We further declare that I am/We are aware of all the action taken or representation made 
    by the <b>${architecttype}</b> authorized by me/us.

    i) That I am/We are sole owner of the site.

    ii) There is no dispute regarding the site and if any dispute arises then I shall be solely resp
    -onsible for the same.

    iii) That construction of the building will be undertaken as per the approved building plans 
    and strutural design given by the Structural Engineer.

    That above stated facts are true and the requisite documents have been uploaded with this E-
    Naksha plan.


    This Document is Verified By OTP at <b>${TimeStamp}


    Name of Owner - <b>${ownername}</b>
    Mobile Number - <b>${ownermobileNumber}</b>
    Email Id  - <b>${ownerEmail}</b>
                                                                  
    `;



    const isRightAlignedLine = (line) => [
      `Name of Owner - <b>${ownername}</b>`,
      `Mobile Number - <b>${ownermobileNumber}</b>`,
      `Email Id  - <b>${ownerEmail}</b>`,
    ].includes(line.trim());
  
    const shouldAddSpacing = (currentLine, nextLine) => {
      const lineToCheck1 = 'That above stated facts are true and the requisite documents have been uploaded with this E-Naksha plan.';
      const lineToCheck2 = '';
      const lineToCheck3 = `This Document is Verified By OTP at ${TimeStamp}`;
  
      return (
        (currentLine.trim() === lineToCheck1 && nextLine?.trim() === lineToCheck2) ||
        currentLine.trim() === lineToCheck3
      );
    };


    const openModal = () => {
      setIsModalOpen(true);
    };

    const closeModal = () => {
      setShowTermsPopup(false);
    };

    
    const uploadSelfDeclaration = async () => {
      try {
        setIsUploading(true); // Set isUploading to true before starting the upload
        
        let result = await Digit.PaymentService.generatePdf(Digit.ULBService.getStateId(), { Bpa: [updatedData] }, "ownerconsent");

      if (result?.filestoreIds[0]?.length > 0) {
        alert("File Uploaded Successfully");
        sessionStorage.setItem("CitizenConsentdocFilestoreid",result?.filestoreIds[0]);
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
        lineHeight: "2" 
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
      rightAlignedText: {
        textAlign: 'right',
        whiteSpace: 'pre-wrap',
        wordWrap: 'break-word',
        fontFamily: 'Roboto, serif',
        lineHeight: "2" 
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
            {/* <div style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word', textAlign: 'justify', fontFamily: 'Roboto, serif' }}>{selfdeclarationform}</div>             */}
            <div style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word', textAlign: 'justify', fontFamily: 'Roboto, serif' }}>
            {selfdeclarationform.split('\n').map((line, index) => (
            <React.Fragment key={index}>
              <div style={isRightAlignedLine(line) ? modalStyles.rightAlignedText : {}}
              dangerouslySetInnerHTML={{ __html: line }}/>
                {/* {line}
               
              </div> */}
              {shouldAddSpacing(line, selfdeclarationform.split('\n')[index + 1]) && (
                <div style={{ marginBottom: '2rem' }} />
              )}
            </React.Fragment>
          ))}
        </div>
            

            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <SubmitBar label={t("BPA_CLOSE")} onSubmit={closeModal} />
            </div>
              <br></br>
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <br></br>
              <SubmitBar label={t("BPA_UPLOAD")} onSubmit={uploadSelfDeclaration} disabled={isUploading || isFileUploaded} />
            </div>
          </div>
        </Modal>
      </div>
    );
  };

  export default CitizenConsent;