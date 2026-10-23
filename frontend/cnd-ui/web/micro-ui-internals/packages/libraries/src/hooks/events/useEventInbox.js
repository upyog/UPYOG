import { queryTemplate } from "../../common/queryTemplate";

const combineResponse = (data, users) => {
  data.events = data?.events?.map((event) => {
    const user = users.find(
      (user) => user.uuid === event?.auditDetails?.lastModifiedBy
    );
    return { ...event, user };
  });
  return data;
};

const useInbox = (tenantId, data, filter = {}, config = {}) => {
  const queryKey = [
    "EVENT_INBOX",
    tenantId,
    JSON.stringify(data),
    JSON.stringify(filter),
  ];

  const queryFn = async () => {
    const eventData = await Digit.EventsServices.Search({
      tenantId,
      data,
      filter,
    });

    const uuids = [];
    eventData?.events?.forEach((e) =>
      uuids.push(e?.auditDetails?.lastModifiedBy)
    );

    const usersResponse = await Digit.UserService.userSearch(
      null,
      { uuid: uuids },
      {}
    );

    return combineResponse(eventData, usersResponse?.user);
  };

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useInbox;