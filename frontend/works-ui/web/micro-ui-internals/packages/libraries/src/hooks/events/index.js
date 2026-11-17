import { queryTemplate } from "../../common/queryTemplate";
import { mutationTemplate } from "../../common/mutationTemplate";



const tsToDate = (ts) => {
const plus0 = (num) => `0${num.toString()}`.slice(-2);
const d = new Date(ts);

return {
month: d.toLocaleString("default", { month: "short" }).toUpperCase(),
date: plus0(d.getDate()),
hour: plus0(d.getHours()),
minute: plus0(d.getMinutes()),
};
};

const isEqual = (from, to) => (from === to ? from : `${from} - ${to}`);

const timeStampBreakdown = (fromTS, toTS) => {
const fromDateTime = tsToDate(fromTS);
const toDateTime = tsToDate(toTS);

return {
onGroundEventMonth: isEqual(fromDateTime.month, toDateTime.month),
onGroundEventDate: isEqual(fromDateTime.date, toDateTime.date),
onGroundEventTimeRange: `${fromDateTime.hour}:${fromDateTime.minute} - ${toDateTime.hour}:${toDateTime.minute}`,
};
};

const fetchImageLinksFromFilestoreIds = async (filesArray, tenantId) => {
const ids = filesArray?.map((file) => file.fileStoreId);
const res = await Digit.UploadServices.Filefetch(ids, tenantId);

if (res.data.fileStoreIds?.length) {
return res.data.fileStoreIds.map((o) => ({
actionUrl: o.url.split(",")[0],
code: "VIEW_ATTACHMENT",
}));
}

return [];
};

const getTransformedLocale = (label) => {
if (typeof label === "number") return label;
label = label?.trim();
return label && label.toUpperCase().replace(/[.:-\s/]/g, "_");
};

const getTimeFormat = (epochTime) => {
const date = new Date(epochTime);
const period = date.getHours() < 12 ? "AM" : "PM";
const hour = date.getHours() % 12 || 12;

return `${hour}:${date.toString().split(":")[1]} ${period}`;
};

const getDateFormat = (epochTime) => {
const months = ["Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"];
const date = new Date(epochTime);

return `${date.getDate()} ${months[date.getMonth()]}`;
};

const getEventSLA = (item) => {
const days = (Date.now() - item.auditDetails.lastModifiedTime) / (1000 * 60 * 60 * 24);

if (item.eventType === "EVENTSONGROUND") {
return {
time: "",
unit:
getDateFormat(item.eventDetails.fromDate) +
" " +
getTimeFormat(item.eventDetails.fromDate) +
"-" +
getDateFormat(item.eventDetails.toDate) +
" " +
getTimeFormat(item.eventDetails.toDate),
};
}

if (days >= 60) return { time: [Math.floor(days / 30)], unit: "EV_SLA_MONTH" };
if (days >= 30) return { time: [Math.floor(days / 30)], unit: "EV_SLA_MONTH_ONE" };
if (days >= 14) return { time: [Math.floor(days / 7)], unit: "EV_SLA_WEEK" };
if (days >= 7) return { time: [Math.floor(days / 7)], unit: "EV_SLA_WEEK_ONE" };
if (days >= 2) return { time: [Math.floor(days)], unit: "CS_SLA_DAY" };
if (days >= 1) return { time: [Math.floor(days)], unit: "EV_SLA_DAY_ONE" };

const hours = (days % 1) * 24;
if (hours >= 2) return { time: [Math.floor(hours)], unit: "EV_SLA_TIME" };
if (hours >= 1) return { time: [Math.floor(hours)], unit: "EV_SLA_TIME_ONE" };

const minutes = hours * 60;
if (minutes >= 2) return { time: [Math.floor(minutes)], unit: "EV_SLA_MINUTE" };
if (minutes >= 1) return { time: [Math.floor(minutes)], unit: "EV_SLA_MINUTE_ONE" };

return { time: "", unit: "CS_SLA_NOW" };
};

/* -------------------- Core Logic -------------------- */

const filterAllEvents = async (data, variant) => {
const filteredEvents = data.filter((e) => e.status === "ACTIVE");

const events = [];

for (const e of filteredEvents) {
const actionLinks =
e?.eventDetails?.documents?.length && e?.tenantId
? await fetchImageLinksFromFilestoreIds(e.eventDetails.documents, e.tenantId)
: [];


const sla = getEventSLA(e);

events.push({
  ...e,
  timePastAfterEventCreation: sla.time,
  timeApproxiamationInUnits: sla.unit,
  eventNotificationText: e?.description,
  header: e?.eventType === "SYSTEMGENERATED" ? getTransformedLocale(e?.name) : e?.name,
  eventType: e?.eventType,
  actions: [...(e?.actions?.actionUrls || []), ...actionLinks],
  ...(variant === "events" || e?.eventType === "EVENTSONGROUND"
    ? timeStampBreakdown(e.eventDetails.fromDate, e.eventDetails.toDate)
    : {}),
});


}

return events;
};

const variantBasedFilter = async (variant, data) => {
const events = await filterAllEvents(data.events, variant);

return events;
};

const getEventsData = async (variant, tenantId) => {
const isLoggedIn = Digit.UserService.getUser();

const data = await Digit.EventsServices.Search({
tenantId,
auth: !!isLoggedIn,
...(variant === "events" ? { filter: { eventTypes: "EVENTSONGROUND" } } : {}),
});

return variantBasedFilter(variant, data);
};

/* -------------------- Hooks -------------------- */

const useEvents = ({ tenantId, variant, config = {} }) => {
return queryTemplate({
queryKey: ["EVENTS_SEARCH", tenantId, variant],
queryFn: () => getEventsData(variant, tenantId),
config,
});
};

const useClearNotifications = () => {
return mutationTemplate({
mutationFn: ({ tenantId }) =>
Digit.EventsServices.ClearNotification({ tenantId }),
});
};

const useNotificationCount = ({ tenantId, config = {} }) => {
return queryTemplate({
queryKey: ["NOTIFICATION_COUNT", tenantId],
queryFn: () =>
Digit.EventsServices.NotificationCount({ tenantId }),
config,
});
};

export { useEvents, useClearNotifications, useNotificationCount };