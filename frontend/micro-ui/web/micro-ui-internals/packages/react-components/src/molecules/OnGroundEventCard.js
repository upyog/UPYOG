import React from "react"
import EventCalendarView from "../atoms/EventCalendarView"
import { MapMarker, Clock } from "../atoms/svgindex"
import { useTranslation } from "react-i18next";
const OnGroundEventCard = ({onClick = () => null, name, id, eventDetails, onGroundEventMonth="MAR", onGroundEventDate="12 - 16", onGroundEventName="To the moon", onGroundEventLocation="Moon", onGroundEventTimeRange="10:00 am - 1:00 pm", eventCategory, showEventCatergory,description ,auditDetails}) => {
    const { t } = useTranslation();
    const getEventSLA = (item) => {
        const days = (Date.now() - item?.lastModifiedTime) / (1000 * 60 * 60 * 24);
        let time;
      let unit;
        if (item.eventType === "EVENTSONGROUND") {
          const disp = getDateFormat(items?.fromDate) + " " + getTimeFormat(item.eventDetails.fromDate) + "-" + getDateFormat(item.eventDetails.toDate) + " " + getTimeFormat(item.eventDetails.toDate);
          time="";
          unit=disp;
        } else {
          if (days >= 60){
            time=[Math.floor(days / 30)];
            unit="EV_SLA_MONTH";
          } 
          else if (days >= 30){
            time=[Math.floor(days / 30)];
            unit="EV_SLA_MONTH_ONE";
          } 
          else if (days >= 14){
            time=[Math.floor(days / 7)];
            unit="EV_SLA_WEEK";
          } 
          else if (days >= 7) {
            time=[Math.floor(days / 7)];
            unit="EV_SLA_WEEK_ONE";
          }
          else if (days >= 2){
            time=[Math.floor(days)];
            unit="CS_SLA_DAY";
          } 
          else if (days >= 1){
            time=[Math.floor(days)];
            unit="EV_SLA_DAY_ONE";
        } 
          else if ((days % 1) * 24 >= 2) {
            time=[Math.floor((days % 1) * 24)];
            unit="EV_SLA_TIME";
          }
          else if ((days % 1) * 24 >= 1){
            time=[Math.floor((days % 1) * 24)];
            unit="EV_SLA_TIME_ONE";
          }
          else if ((days % 1) * 24 * 60 >= 2) {
            time=[Math.floor((days % 1) * 24 * 60)];
            unit="EV_SLA_MINUTE";
          }
          else if ((days % 1) * 24 * 60 >= 1) {
            time=[Math.floor((days % 1) * 24 * 60)];
            unit="EV_SLA_MINUTE_ONE";
    
          }
          else{
            time="";
            unit="CS_SLA_NOW";
          }
        }
      
        return {
            time,unit
        };
      };
        const slaDetails=getEventSLA(auditDetails);

         const timePastAfterEventCreation= slaDetails.time
         const timeApproxiamationInUnits = slaDetails.unit

    const onEventCardClick = () => onClick(id)
console.log("eventDetails",eventDetails,auditDetails)
    return <div className="OnGroundEventCard" onClick={onEventCardClick} style={{width:"100%"}}>auditDetails
        <style>
            {
                `
                .OnGroundEventCard .EventDetails .EventCategory p
                {
                    font-size: 12px;
                    line-height: 24px
                }
                `
            }
        </style>
        {/* <EventCalendarView {...{onGroundEventMonth, onGroundEventDate}} /> */}
        <div className="EventDetails" style={{width:"100%"}}>
            <h2>{name}</h2>
            {description ? <div className="EventCategory">
                <p sttle={{fontSize:"12px"}}>{description}</p>
            </div> : null}
            <p style={{color:"black",fontSize:"12px"}}>{timePastAfterEventCreation + ` ${t(timeApproxiamationInUnits)}`}</p>
        </div>
    </div>
}

export default OnGroundEventCard