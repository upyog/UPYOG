import { Loader,Dropdown } from '@egovernments/digit-ui-react-components';
import React,{useState} from 'react'
import { useTranslation } from 'react-i18next';
import { useHistory } from 'react-router-dom';

const sampleResp = [
  {
      "id": "af8f0290-7900-4d2c-8e82-223735a732f0",
      "tenantId": "pg",
      "plantCode": "ANGUL_FSTP",
      "individualId": "41488b21-c742-4290-af8e-a2a1dc3749ac",
      "isActive": true,
      "additionalDetails": {},
      "auditDetails": {
          "createdBy": "d7867ef2-d046-4361-9a82-94c35c98416e",
          "lastModifiedBy": "d7867ef2-d046-4361-9a82-94c35c98416e",
          "createdTime": 1700225318913,
          "lastModifiedTime": 1700225318913
      },
      "i18nKey":"PQM_PLANT_ANGUL_FSTP"
  },
  {
      "id": "6ad6a8f2-7abe-4ce3-8456-14983968e290",
      "tenantId": "pg",
      "plantCode": "PURI_FSTP",
      "individualId": "41488b21-c742-4290-af8e-a2a1dc3749ac",
      "isActive": true,
      "additionalDetails": {},
      "auditDetails": {
          "createdBy": "d7867ef2-d046-4361-9a82-94c35c98416e",
          "lastModifiedBy": "d7867ef2-d046-4361-9a82-94c35c98416e",
          "createdTime": 1700225256636,
          "lastModifiedTime": 1700225256636
      },
      "i18nKey":"PQM_PLANT_PURI_FSTP"
  }
]

const ChangePlant = ({mobileView}) => {
  const history = useHistory();
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const userInfo = Digit.UserService.getUser();
  const [activePlant,setActivePlant] = useState(Digit.SessionStorage.get("active_plant")?.plantCode?Digit.SessionStorage.get("active_plant"): {i18nKey:"PQM_PLANT_DEFAULT_ALL"})
  const requestCriteria = {
    params:{},
    url:'/pqm-service/plant/user/v1/_search',
    body:{
      "plantUserSearchCriteria": {
        tenantId,
        // "plantCodes": [],
        "plantUserUuids": userInfo?.info?.uuid ?  [userInfo?.info?.uuid]: [],
        "additionalDetails": {}
      },
      "pagination": {}
    },
    config: {
      select:(data)=> {
        let userPlants =  data?.plantUsers?.map(row => {
          row.i18nKey = `PQM_PLANT_${row?.plantCode}`
          return row
        })?.filter(row=>row.isActive)
        userPlants.push({i18nKey:"PQM_PLANT_DEFAULT_ALL"})
        //remove this line when api works fine
        // userPlants = sampleResp

        Digit.SessionStorage.set("user_plants",userPlants );
        // Digit.SessionStorage.set("active_plant",userPlants?.[0] ? userPlants?.[0] : {} )
        // Digit.SessionStorage.set("active_plant", null )
        
        // setActivePlant(userPlants?.[0] ? userPlants?.[0] : {})
        return userPlants
      }
    }
  }
  const { isLoading, data} = Digit.Hooks.useCustomAPIHook(requestCriteria);
  const handlePlantChange = (plant) => {
    Digit.SessionStorage.set("active_plant",plant)
    setActivePlant(plant)
    history.push(
      `/${window?.contextPath}/employee/tqm/landing`
    )

  }

  if(isLoading) {
    return <Loader />
  }
  // if user is not linked to any plant 
  if(data?.filter(row => row?.plantCode)?.length === 0){
    return null
  }
  return (
    <div style={mobileView ? { color: "#767676" } : {}}>
      <Dropdown
        t={t}
        option={data}
        selected={activePlant}
        optionKey={"i18nKey"}
        select={handlePlantChange}
        freeze={true}
        customSelector={
          <label className="cp">
            {t(activePlant?.i18nKey)}
          </label>
        }
      />
    </div>
  )
}

export default ChangePlant