import { Header, ActionBar, SubmitBar, ExternalLinkIcon, Menu, GenericFileIcon, LinkButton } from '@egovernments/digit-ui-react-components';
import React, { useState , useEffect } from 'react'
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import { useParams } from "react-router-dom";



const WmsManageApplication = ({ location, match, history, }) => {

  const params = useParams();
  const [data, setData] = useState([]);

  useEffect(() => {
    (async () => {
      const result = await axios(`https://62f0e3e5e2bca93cd23f2ada.mockapi.io/sor/${params.id}`);
      setData(result.data);
      console.log("gooo" ,result.data);
    })();
  }, [params.id]);


    let isMobile = window.Digit.Utils.browser.isMobile();
    const { t } = useTranslation();
   
    const [displayMenu, setDisplayMenu] = React.useState(false);
    const [showModal, setShowModal] = useState(false);


    function onActionSelect(action) {
        // setSelectedAction(action);
     
          history.push(`/digit-ui/employee/sor/responseemp`)
    }


    return (
        <div>
            <Header>{t(`Work Management System`)}</Header>
            <div className="notice_and_circular_main gap-ten">
                <div className="documentDetails_wrapper">
                    {/* <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('ULB')}:`}</p> <p>{data?.tenantId}</p> </div> */}
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Chapter')}:`}</p> <p>{data?.sorChapter}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Description Of Item')}:`}</p> <p>{t(data?.sorDescriptionOfItem)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Item No')}:`}</p> <p>{t(data?.sorItemNo)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Unit')}:`}</p> <p>{t(data?.sorUnit)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Rate')}:`}</p> <p>{t(data?.sorRate)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('NAME')}:`}</p> <p>{t(data?.sorName)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('Start Date')}:`}</p> <p>{t(data?.sorStartDate)}</p> </div>
                    <div className="documentDetails_row_items"><p className="documentDetails_title">{`${t('End Date')}:`}</p> <p>{t(data?.sorEndDate)}</p> </div>
                </div>
            </div>
            <ActionBar>
        {displayMenu ? (
          <Menu
            localeKeyPrefix={"WMS"}
            options={['Delete', 'Cancel']}
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
          actionSaveLabel={t('DELETE')}
          actionSaveOnSubmit={handleDelete}
        >
          <Card style={{ boxShadow: "none" }}>
            <CardText>{t(`CANCEL`)}</CardText>
          </Card>
        </Modal>
          }
        </div>
    )
}

export default WmsManageApplication;
