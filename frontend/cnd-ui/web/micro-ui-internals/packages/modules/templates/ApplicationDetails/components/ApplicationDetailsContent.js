import {
  BreakLine,
  Card,
  CardSectionHeader,
  CardSubHeader,
  CheckPoint,
  ConnectingCheckPoints,
  Loader,
  Row,
  StatusTable,
  LinkButton,
  PDFSvg
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import TLCaption from "./TLCaption";
import DocumentPreview from "./DocumentPreview";

function ApplicationDetailsContent({
  applicationDetails,
  workflowDetails,
  isDataLoading,
  applicationData,
  timelineStatusPrefix,
  showTimeLine = true,
  statusAttribute = "status"
}) {
  const { t } = useTranslation();

  const ownersSequences = applicationDetails?.applicationData?.owners

  function OpenImage(imageSource, index, thumbnailsToShow) {
    window.open(thumbnailsToShow?.fullImage?.[0], "_blank");
  }

  const [fetchBillData, updatefetchBillData] = useState({});

  const setBillData = async (tenantId, propertyIds, updatefetchBillData, updateCanFetchBillData) => {
    let billData = {};
    updatefetchBillData(billData);
    updateCanFetchBillData({
      loading: false,
      loaded: true,
      canLoad: true,
    });
  };
  const [billData, updateCanFetchBillData] = useState({
    loading: false,
    loaded: false,
    canLoad: false,
  });

  if (applicationData?.status == "ACTIVE" && !billData.loading && !billData.loaded && !billData.canLoad) {
    updateCanFetchBillData({
      loading: false,
      loaded: false,
      canLoad: true,
    });
  }
  if (billData?.canLoad && !billData.loading && !billData.loaded) {
    updateCanFetchBillData({
      loading: true,
      loaded: false,
      canLoad: true,
    });
    setBillData(applicationData?.tenantId || tenantId, applicationData?.propertyId, updatefetchBillData, updateCanFetchBillData);
  }
  const convertEpochToDateDMY = (dateEpoch) => {
    if (dateEpoch == null || dateEpoch == undefined || dateEpoch == "") {
      return "NA";
    }
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    return `${day}/${month}/${year}`;
  };
  const getTimelineCaptions = (checkpoint, index = 0, timeline) => {
      const caption = {
        date: convertEpochToDateDMY(applicationData?.auditDetails?.lastModifiedTime),
        name: checkpoint?.assignes?.[0]?.name,
        wfComment: checkpoint?.wfComment,
        mobileNumber: checkpoint?.assignes?.[0]?.mobileNumber,
        thumbnailsToShow: checkpoint?.thumbnailsToShow
      };
      return <TLCaption data={caption} OpenImage={OpenImage} />;
  };

  const getTranslatedValues = (dataValue, isNotTranslated) => {
    if (dataValue) {
      return !isNotTranslated ? t(dataValue) : dataValue;
    } else {
      return t("NA");
    }
  };


  const isCND = window.location.href.includes("employee/cnd");
  const getTextValue = (value) => {
    if (value?.skip) return value.value;
    else if (value?.isUnit) return value?.value ? `${getTranslatedValues(value?.value, value?.isNotTranslated)} ${t(value?.isUnit)}` : t("N/A");
    else return value?.value ? getTranslatedValues(value?.value, value?.isNotTranslated) : t("N/A");
  };


  const [showAllTimeline, setShowAllTimeline] = useState(false);
  const toggleTimeline = () => {
    setShowAllTimeline((prev) => !prev);
  }
// this is file open service
  const openFilePDF = (fileId) => {
    Digit.UploadServices.Filefetch([fileId], Digit.ULBService.getStateId()).then((res) => {
  
      // Extract the concatenated URL string
      const concatenatedUrls = res?.data?.fileStoreIds?.[0]?.url;
  
      if (concatenatedUrls) {
        // Split the string by commas to get individual URLs
        const urlArray = concatenatedUrls.split(',');
  
        // Pick the first URL (or any other logic to decide which URL to open)
        const fileUrl = urlArray[0];
  
        if (fileUrl) {
          window.open(fileUrl, '_blank'); // Open the file in a new tab
        } else {
          console.error('No valid URL found to open!');
        }
      } else {
        console.error('URL is missing in the response!');
      }
    }).catch((error) => {
      console.error('Error fetching file:', error);
    });
  };
  
  return (
    <Card style={{ position: "relative" }} className={isCND?"employeeCard-override":""}>
      {applicationDetails?.applicationDetails?.map((detail, index) => (
        <React.Fragment key={index}>
          <div>
            {index === 0 && !detail.asSectionHeader ? (
              <CardSubHeader style={{ marginBottom: "16px", fontSize: "24px" }}>{t(detail.title)}</CardSubHeader>
            ) : (
              <React.Fragment>
                <CardSectionHeader
                  style={ 
                      { marginBottom: "16px", marginTop: "32px", fontSize: "24px" }
                  }
                >
                  {t(detail.title)}
                  {detail?.Component ? <detail.Component /> : null}
                </CardSectionHeader>
              </React.Fragment>
            )}
            {/* TODO, Later will move to classes */}
            {/* Here Render the table for adjustment amount details detail.isTable is true for that table*/}

            {detail?.isTable && (
              <table style={{ tableLayout: "fixed", width: "63%", borderCollapse: "collapse",border: "1px solid black" }}>
                <tr style={{ textAlign: "centre" }}>
                  {detail?.headers.map((header) => (
                    <th style={{ padding: "10px", paddingLeft: "0px",  border: "1px solid black" }}>{t(header)}</th>
                  ))}
                </tr>
                {detail?.tableRows.map((row, index) => {
                  return <tr>
                  {row.map((element, idx) => (
                    Array.isArray(element) && element.length > 1 ? (
                      <td style={{ paddingTop: "20px", textAlign: "centre", border: "1px solid black", verticalAlign: "middle" }} key={idx}>
                      <div style={{ display: "flex", flexWrap: "nowrap", gap: "5px" }}>
                        {element.map((file, fileIndex) => (
                          <a
                            key={fileIndex} // Ensure each <a> tag has a unique key
                            onClick={() => openFilePDF(file.fileStoreId)}
                            rel="noopener noreferrer"
                            style={{ marginRight: "5px", display: "inline-block", cursor: "pointer"  }}
                          >
                            <PDFSvg style={{ width: "35px", height: "35px" }}/>
                          </a>
                        ))}
                      </div>
                    </td>
                    
                    ) : (
                      <td key={idx} style={{ paddingTop: "20px", textAlign: "center", border: "1px solid black" }}>
                        {t(element)}
                      </td>
                    )
                  ))}
                </tr>
                })}
              </table>
            )}

            {detail?.additionalDetails?.documents && <DocumentPreview documents={detail?.additionalDetails?.documents} />}  

              <StatusTable>
              {detail?.title &&
                detail?.values?.map((value, index) => {
                  if (value.map === true && value.value !== "N/A") {
                    return <Row labelStyle={{ wordBreak: "break-all" }} textStyle={{ wordBreak: "break-all" }} key={t(value.title)} label={t(value.title)} text={<img src={t(value.value)} alt="" privacy={value?.privacy} />} />;
                  }
                  if (value?.isLink == true) {
                    return (
                      <Row
                        key={t(value.title)}
                        label={t(value.title)}
                        text={
                          <div>
                            <Link to={value?.to}>
                              <span className="link" style={{ color: "#a82227" }}>
                                {value?.value}
                              </span>
                            </Link>
                          </div>
                        }
                        last={index === detail?.values?.length - 1}
                        caption={value.caption}
                        className="border-none"
                        labelStyle={{ wordBreak: "break-all" }}
                        textStyle={{ wordBreak: "break-all" }}
                      />
                    );
                  }
                  return (
                    <div>
                        {(<Row
                          key={t(value.title)}
                          label={t(value.title)}
                          text={getTextValue(value)}
                          last={index === detail?.values?.length - 1}
                          caption={value.caption}
                          className="border-none"
                          privacy={value?.privacy}
                          labelStyle={{
                            wordBreak: "break-all",
                            fontWeight: value.isBold ? 'bold' : 'normal',
                            fontStyle: value.isBold ? 'italic' : 'normal'
                          }}
                          textStyle={{
                            wordBreak: "break-all",
                            fontWeight: value.isBold ? 'bold' : 'normal',
                            fontStyle: value.isBold ? 'italic' : 'normal'
                          }}
                        />
                      )}
                    </div>
                  )
                })}
            </StatusTable>
          </div>
          {detail?.belowComponent && <detail.belowComponent />}
          {detail?.additionalDetails?.redirectUrl && (
            <div style={{ fontSize: "16px", lineHeight: "24px", fontWeight: "400", padding: "10px 0px" }}>
              <Link to={detail?.additionalDetails?.redirectUrl?.url}>
                <span className="link" style={{ color: "#a82227" }}>
                  {detail?.additionalDetails?.redirectUrl?.title}
                </span>
              </Link>
            </div>
          )}

        </React.Fragment>
      ))}

      


      {showTimeLine && workflowDetails?.data?.timeline?.length > 0 && (
        <React.Fragment>
          <BreakLine />
          {(workflowDetails?.isLoading || isDataLoading) && <Loader />}
          {!workflowDetails?.isLoading && !isDataLoading && (
            <Fragment>
              <div id="timeline">
                <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>
                  {t("ES_APPLICATION_DETAILS_APPLICATION_TIMELINE")}
                </CardSectionHeader>
                {workflowDetails?.data?.timeline && workflowDetails?.data?.timeline?.length === 1 ? (
                  <CheckPoint
                    isCompleted={true}
                    label={t(`${timelineStatusPrefix}${workflowDetails?.data?.timeline[0]?.state}`)}
                    customChild={getTimelineCaptions(workflowDetails?.data?.timeline[0], workflowDetails?.data?.timeline)}
                  />
                ) : (
                  <ConnectingCheckPoints>
                    {workflowDetails?.data?.timeline &&
                      workflowDetails?.data?.timeline.slice(0, showAllTimeline ? workflowDetails?.data.timeline.length : 2).map((checkpoint, index, arr) => {
                        let timelineStatusPostfix = "";
                        return (
                          <React.Fragment key={index}>
                            <CheckPoint
                              keyValue={index}
                              isCompleted={index === 0}
                              info={checkpoint.comment}
                              label={t(
                                `${timelineStatusPrefix}${checkpoint?.[statusAttribute]
                                }${timelineStatusPostfix}`
                              )}
                              customChild={getTimelineCaptions(checkpoint, index, workflowDetails?.data?.timeline)}
                            />
                          </React.Fragment>
                        );
                      })}
                  </ConnectingCheckPoints>
                )}
                {workflowDetails?.data?.timeline?.length > 2 && (
                  <LinkButton label={showAllTimeline ? t("COLLAPSE") : t("VIEW_TIMELINE")} onClick={toggleTimeline}>
                  </LinkButton>
                )}
              </div>
            </Fragment>
          )}
        </React.Fragment>
      )}
    </Card>
  );
}

export default ApplicationDetailsContent;
