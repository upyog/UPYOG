import { Header, ActionBar, SubmitBar, ExternalLinkIcon, Menu, GenericFileIcon, LinkButton } from '@egovernments/digit-ui-react-components';
import React, { useState , useEffect } from 'react'
import { useTranslation } from 'react-i18next';
// import { openDocumentLink, openUploadedDocument } from '../../utils';
import axios from 'axios';
import { useParams,useHistory } from "react-router-dom";




const ContractMasterDetail = ({ location, match }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const params = useParams();
  const [data, setData] = useState([]);
  const history = useHistory();
console.log("params ",params)
console.log("data ",data)
const cmGetSingleData = Digit?.Hooks?.wms?.cm?.useWmsCMCount(params.id);
console.log("cmGetSingleData ",cmGetSingleData)

  useEffect(() => {
    if (cmGetSingleData.data && cmGetSingleData.data.WMSContractorApplications){
        setData(...cmGetSingleData.data.WMSContractorApplications);
    } 
  }, [cmGetSingleData?.data?.WMSContractorApplications]);


    let isMobile = window.Digit.Utils.browser.isMobile();
    const { t } = useTranslation();
   
    const [displayMenu, setDisplayMenu] = React.useState(false);
    const [showModal, setShowModal] = useState(false);


    function onActionSelect(action) {
        // setSelectedAction(action);
          history.push(`/digit-ui/employee/br/responseemp`)
    }


    return (
        <div>
            {/* {showModal ? <Confirmation
                t={t}
                heading={'CONFIRM_DELETE_DOC'}
                docName={details?.name}
                closeModal={() => setShowModal(!showModal)}
                actionCancelLabel={'CS_COMMON_CANCEL'}
                actionCancelOnSubmit={onModalCancel}
                actionSaveLabel={'ES_COMMON_Y_DEL'}
                actionSaveOnSubmit={onModalSubmit}
            />

                : null} */}
            <Header>{t(`Contractor Master Details`)}</Header>
            <div className="notice_and_circular_main gap-ten">
                <div className="documentDetails_wrapper">
                    {/* <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('ULB')}:`}</p> <p>{data?.tenantId}</p> </div> */}
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('PFMS Vendor ID')}:`}</p> <p>{t(data?.pfms_vendor_code)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Vendor Type')}:`}</p> <p>{t(data?.vendor_type)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Vendor Name')}:`}</p> <p>{t(data?.vendor_name)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Mobile Number')}:`}</p> <p>{t(data?.mobile_number)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('UID Number')}:`}</p> <p>{t(data?.uid_number)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Vendor Status')}:`}</p> <p>{t(data?.vendor_status)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('VAT Number')}:`}</p> <p>{t(data?.vat_number)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Bank, Branch & IFSC Code')}:`}</p> <p>{t(data?.bank_branch_ifsc_code)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Function')}:`}</p> <p>{t(data?.function)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Vendor Class')}:`}</p> <p>{t(data?.vendor_class)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('PF Account Number')}:`}</p> <p>{t(data?.epfo_account_number)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Sub Type')}:`}</p> <p>{t(data?.vendor_sub_type)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Pay To')}:`}</p> <p>{t(data?.payto)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Email Id')}:`}</p> <p>{t(data?.email)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('GST Number')}:`}</p> <p>{t(data?.gst_number)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('PAN Number')}:`}</p> <p>{t(data?.pan_number)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Bank Account Number')}:`}</p> <p>{t(data?.bank_account_number)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Primary Account Head')}:`}</p> <p>{t(data?.primary_account_head)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Address')}:`}</p> <p>{t(data?.address)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Allow Direct Payment')}:`}</p> <p>{t(data?.allow_direct_payment)}</p> </div>
    
                    {/* <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('DCOUMENT_DESCRIPTION')}:`}</p> <p className="documentDetails__description">{details?.description?.length ? details?.description : 'NA'}</p> </div> */}
                    {/* <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('ES_COMMON_LINK_LABEL')}:`}</p>
                        {details?.documentLink ? <LinkButton
                            label={
                                <div className="link" onClick={() => openDocumentLink(details?.documentLink, details?.name)}>
                                    <p>{t(`CE_DOCUMENT_OPEN_LINK`)}</p>
                                </div>
                            }
                        /> : 'NA'}
                    </div> */}
                  
                </div>
            </div>

      <ActionBar>
        <SubmitBar label={t("WMS_COMMON_TAKE_ACTION")} onSubmit={() =>  history.push(`./../../edit/${tenantId}/${params.id}`)} />
        {/* <SubmitBar label={t("WMS_COMMON_TAKE_ACTION")} onSubmit={() =>  history.push(`/upyog-ui/citizen/wms/mb-edit/${data?.[0]?.mb_id}`)} /> */}
      </ActionBar>
{/*
      <ActionBar>
        {displayMenu ? (
          <Menu
            localeKeyPrefix={"BR"}
            options={['Approve', 'Reject']}
            t={t}
            onSelect={onActionSelect}
          />
        ) : null}
        <SubmitBar label={t("ES_COMMON_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
      </ActionBar>
       {showModal &&
        <Modal
          headerBarMain={<Heading label={t('ES_EVENT_DELETE_POPUP_HEADER')} />}
          headerBarEnd={<CloseBtn onClick={() => setShowModal(false)} />}
          actionCancelLabel={t("CS_COMMON_CANCEL")}
          actionCancelOnSubmit={() => setShowModal(false)}
          actionSaveLabel={t('APPROVE')}
          actionSaveOnSubmit={handleDelete}
        >
          <Card style={{ boxShadow: "none" }}>
            <CardText>{t(`REJECT`)}</CardText>
          </Card>
        </Modal>
          } */}
        </div>
    )
}

export default ContractMasterDetail;
