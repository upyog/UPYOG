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
  import React, { useState, useEffect } from 'react';
  import Modal from 'react-modal';
  import { useLocation } from "react-router-dom";
  import { SubmitBar } from '@upyog/digit-ui-react-components';
  import { useTranslation } from "react-i18next";
  



  const Architectconcent = ({ showTermsPopup, setShowTermsPopup, otpVerifiedTimestamp }) => {
    
    const [isModalOpen, setIsModalOpen] = useState(false);
    const { state } = useLocation();
    const { t } = useTranslation();
    const user = Digit.UserService.getUser();
    const architecname = user?.info?.name;
    const architectmobileNumber = user?.info.mobileNumber
    const [params, setParams] = Digit.Hooks.useSessionStorage("BUILDING_PERMIT", state?.edcrNumber ? { data: { scrutinyNumber: { edcrNumber: state?.edcrNumber } } } : {});
    const [isUploading, setIsUploading] = useState(false); // it will check whether the file upload is in process or not
    const [isFileUploaded, setIsFileUploaded] = useState(false);
    const architectid = params?.additionalDetails?.architectid;
    const ownername = params?.owners?.owners?.[0]?.name;
    const mobile = params?.owners?.owners?.[0]?.mobileNumber
    const architecttype = params?.additionalDetails?.typeOfArchitect;
    const khasranumber = params?.additionalDetails?.khasraNumber;
    const ulbname = params?.additionalDetails?.District;
    const district = params?.additionalDetails?.UlbName;
    const ward = params?.additionalDetails?.wardnumber;
    const area = params?.additionalDetails?.area;
    const zone = params?.additionalDetails?.zonenumber;
    const ulbgrade = params?.additionalDetails?.Ulblisttype
    const TimeStamp = otpVerifiedTimestamp;


    useEffect(() => {
      if (params?.additionalDetails && !params.additionalDetails.TimeStamp) {
        setParams(prevParams => ({
          ...prevParams,
          additionalDetails: {
            ...prevParams.additionalDetails,
            TimeStamp: otpVerifiedTimestamp
          }
        }));
      }
    }, [params, otpVerifiedTimestamp, setParams]);


    const selfdeclarationform =
    `
   To,
   <b>${ulbgrade}</b>
   <b>${district}</b>
   
    
   Dear Sir or Madam,

   I, under signed Shri/Smt/Kum <b>${architecname}</b> (<b>${architecttype}</b>) having Registration No. 
   <b>${architectid}</b> is appointed by the <b>${ownername}</b> Mobile number <b>${mobile}</b> for the development on
   land bearing Kh. No <b>${khasranumber}</b> Area <b>${area}</b> (Sq.mts).
    
   This site falls in ward number <b>${ward}</b> zone number <b>${zone}</b>  in the Master plan of 
   <b>${district}</b> and the proposed Residential/Commercial/Industrial construction is permi
   -ssible in this area.
  
   I am currently registered as <b>${architecttype}</b> with the Competent Authority and empane
   -lled under Self-Certification Scheme.
  
   I hereby certify that I/we have appointed by the owner to prepare the plans, sections and 
   details, structural details as required under the Punjab Municipal Building Byelaws for the 
   above mentioned project. 
  
   That the drawings prepared and uploaded along with other necessary documents on this 
   E-Naksha Platform are as per the provisions of Punjab Municipal Building Byelaws and th
   -is building plan has been applied under Self-Certification Scheme. 
  
   I certify that:
   That I am fully conversant with the provisions of the Punjab Municipal Building Byelaws and 
   other applicable instructions/ regulations, which are in force and I undertake to fulfill the 
   same.
  
   That plans have been prepared within the framework of provisions of the Master Plan and app
   -licable Building Bye Laws / Regulations. 
  
   That site does not falls in any prohibited area/ government land/ encroachment or any other 
   land restricted for building construction or in any unauthorized colony. 
  
   That plan is in conformity to structural safety norms. 
  
   That I have seen the originals of all the documents uploaded and Nothing is concealed 
   thereof. 
  
   That all the requisite documents/NOC required to be uploaded have been uploaded on 
   E-Naksha portal along with plan. 
  
   That above stated facts are true and all the requisite documents uploaded with this E-Naksha plan.
   


   This Document is Verified By OTP at <b>${TimeStamp}</b>


   Name of Professional - <b>${architecname}</b> 
   Designation - <b>${architecttype}</b>
   Architect Id - <b>${architectid}</b> 
   Mobile Number - <b>${architectmobileNumber}</b>
   
                                  
    `;

    const isRightAlignedLine = (line) => [
      `Name of Professional - <b>${architecname}</b>`,
      `Designation - <b>${architecttype}</b>`,
      `Architect Id - <b>${architectid}</b>`,
      `Mobile Number - <b>${architectmobileNumber}</b>`,
    ].includes(line.trim());
  
    const shouldAddSpacing = (currentLine, nextLine) => {
      const lineToCheck1 = 'That above stated facts are true and all the requisite documents uploaded with this E-Naksha plan.';
      const lineToCheck2 = '';
      const lineToCheck3 = `This Document is Verified By OTP at <b>${TimeStamp}</b>`;
  
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
        let result = await Digit.PaymentService.generatePdf(Digit.ULBService.getStateId(), { Bpa: [params] }, "architectconsent");
        
      if (result?.filestoreIds[0]?.length > 0) {
        alert("File Uploaded Successfully");
        sessionStorage.setItem("ArchitectConsentdocFilestoreid",result?.filestoreIds[0]);
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
      rightAlignedText: {
        textAlign: 'right',
        whiteSpace: 'pre-wrap',
        wordWrap: 'break-word',
        fontFamily: 'Roboto, serif'
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
          

          </div>
          <div>
            <h2 style={modalStyles.heading}>DECLARATION UNDER SELF-CERTIFICATION SCHEME</h2>
            <h3 style={modalStyles.subheading}>(For proposed Construction)</h3>
            <h3 style={modalStyles.subheading}>(By Architect/ Civil Engineer/ Building Designer and Supervisor)</h3>
            
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

  export default Architectconcent;