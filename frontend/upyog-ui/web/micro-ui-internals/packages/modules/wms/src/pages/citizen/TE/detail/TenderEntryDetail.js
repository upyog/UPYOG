import { Header, ActionBar, SubmitBar, ExternalLinkIcon, Menu, GenericFileIcon, LinkButton } from '@egovernments/digit-ui-react-components';
import React, { useState , useEffect } from 'react'
import { useTranslation } from 'react-i18next';
// import { openDocumentLink, openUploadedDocument } from '../../utils';
import axios from 'axios';
import { useParams,useHistory } from "react-router-dom";




const TenderEntryDetail  = ({ location, match }) => {
  // const tenantId = Digit.ULBService.getCurrentTenantId();
  const { id,tenantId } = useParams();
  const [data, setData] = useState([]);
  const history = useHistory();
console.log("id,tenantId ",{id,tenantId})
const teGetSingleData = Digit?.Hooks?.wms?.te?.useWmsTEGetSingleRecord(tenantId,id);
console.log("teGetSingleData sdfsdf  ",teGetSingleData)

  useEffect(() => {
    if (teGetSingleData.data && teGetSingleData.data.WMSTenderEntryApplications){
        setData(...teGetSingleData.data.WMSTenderEntryApplications);
    } 
  }, [teGetSingleData?.data?.WMSTenderEntryApplications]);


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
            <Header>{t(`Tender Entry Details`)}</Header>
            <div className="notice_and_circular_main gap-ten">
                <div className="documentDetails_wrapper">
                     <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Department Name')}:`}</p> <p>{t(data?.department_name)}</p> </div>
                     <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Financial Bid Open Date')}:`}</p> <p>{t(data?.financial_bid_open_date)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Issue From Date')}:`}</p> <p>{t(data?.issue_from_date)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Issue Till Date')}:`}</p> <p>{t(data?.issue_till_date)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Prebid Meeting Date')}:`}</p> <p>{t(data?.prebid_meeting_date)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Prebid Meeting Location')}:`}</p> <p>{t(data?.prebid_meeting_location)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Project Name')}:`}</p> <p>{t(data?.project_name)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Publish Date')}:`}</p> <p>{t(data?.publish_date)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Request Category')}:`}</p> <p>{t(data?.request_category)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Resolution Date')}:`}</p> <p>{t(data?.resolution_date)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Resolution No')}:`}</p> <p>{t(data?.resolution_no)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Technical Bid Open Date')}:`}</p> <p>{t(data?.technical_bid_open_date)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Validity')}:`}</p> <p>{t(data?.validity)}</p> </div>
                    
                  
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
        <SubmitBar label={t("WMS_COMMON_TAKE_ACTION")} onSubmit={() =>  history.push(`./../../edit/${tenantId}/${id}`)} />
        {/* <Link to={`edit/${props.tenantId}/${row?.original?.tender_id}`}>Edit</Link> */}
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

export default TenderEntryDetail ;
