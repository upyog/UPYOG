import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

export const DigiLockerService = {
  authorization: ({ filters }) =>
    Request({
      url: Urls.digiLocker.authorization,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: {module:"PT" },
    }),
    token: ( data ) =>
    Request({
      url: Urls.digiLocker.token,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      data:data,
    }),
    issueDoc: ( data ) =>
    Request({
      url: Urls.digiLocker.issueDoc,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      data:data,
    }),
    uri:( data) =>
    {
        console.log("ttttt",data)
    Request({
      url: Urls.digiLocker.uri + data.uriurl.uri,
      useCache: false,
      method: "POST",
      auth: true,
      userService: false,
      data:{"TokenReq":data?.uriurl?.TokenReq}
      
    })
},
};


