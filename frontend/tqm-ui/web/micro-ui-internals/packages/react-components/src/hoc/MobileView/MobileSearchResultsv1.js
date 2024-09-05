import React, { useMemo, useContext, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import DetailsCard from '../../molecules/DetailsCard';
import { Link } from 'react-router-dom';
import NoResultsFound from '../../atoms/NoResultsFound';
import { Loader } from '../../atoms/Loader';
import _ from 'lodash';
import { useHistory } from 'react-router-dom';

// const sampleSearchResult = [
//   {
//     businessObject:{
//       testId:"AW28929",
//       treatmentProcess:"KA - 25235",
//       stage:"Jagadamba Cleaners",
//       outputType:"KA - 25235",
//       pendingDate:"12/02/2013",
//       status:"Pending results",
//       sla:12
//     }
//   }
// ]

const convertRowToDetailCardData = (row,config,t,apiDetails,searchResult) => {
  const resultantObj = {
    apiResponse:{...row,hidden:true}
  }

  config.columns.map((column,idx) => {
    resultantObj[t(column.label)] = column.additionalCustomization ? Digit?.Customizations?.[apiDetails?.masterName]?.[apiDetails?.moduleName]?.additionalCustomizations(row,column?.label,column, _.get(row,column.jsonPath,""),t, searchResult) : String(_.get(row,column.jsonPath,"") ? column.translate? t(Digit.Utils.locale.getTransformedLocale(column.prefix?`${column.prefix}${_.get(row,column.jsonPath,"")}`:_.get(row,column.jsonPath,""))) : _.get(row,column.jsonPath,"") : t("ES_COMMON_NA")); 
  })

  return resultantObj
}

const convertDataForDetailsCard = (config,searchResult,t,apiDetails) => {
//map over columns and generate data accordingly

  const result = searchResult?.map((row,idx) => {
    return convertRowToDetailCardData(row,config,t,apiDetails,searchResult)
  } )

  return result
}

const MobileSearchResultsv1 = ({
  config,
  data,
  isLoading,
  isFetching,
  fullConfig,
}) => {
  const { t } = useTranslation();
  const history = useHistory()
  const { apiDetails } = fullConfig;
  const resultsKey = config.resultsJsonPath;

  let searchResult = _.get(data, resultsKey, []);
  
  //for sample result
  // let searchResult = _.get(sampleSearchResult, resultsKey, []);
  
  searchResult = searchResult?.length > 0 ? searchResult : [];
  // searchResult = searchResult?.reverse();
  
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const RenderResult = () => {
    const dataForDetailsCard =  convertDataForDetailsCard(config,searchResult,t,apiDetails) 
    const propsForDetailsCard = {
      t,
      data:dataForDetailsCard,
      showActionBar:config?.showActionBarMobileCard, // to show action button on detail card
      submitButtonLabel:config?.actionButtonLabelMobileCard,
      handleDetailCardClick:(obj)=>{ //fn when action button on card is clicked
        const linkToPushTo = Digit?.Customizations?.[apiDetails?.masterName]?.[apiDetails?.moduleName]?.onCardActionClick(obj)
        history.push(linkToPushTo)
      },
      handleSelect:(obj)=>{
       const linkToPushTo = Digit?.Customizations?.[apiDetails?.masterName]?.[apiDetails?.moduleName]?.onCardClick(obj)
       history.push(linkToPushTo)
        // Digit?.Customizations?.[apiDetails?.masterName]?.[apiDetails?.moduleName]?.onCardActionClick(obj)
      }, //fn when card container is clicked
      mode:"tqm",
      apiDetails,
    }
    
    return <DetailsCard {...propsForDetailsCard} />
  }

  if (isLoading || isFetching) {
    return <Loader />;
  }

  if (searchResult?.length === 0) {
    return ( <NoResultsFound/> );
  } 

  return (
    <React.Fragment>
      <RenderResult />
    </React.Fragment>
  );
};

export default MobileSearchResultsv1;
