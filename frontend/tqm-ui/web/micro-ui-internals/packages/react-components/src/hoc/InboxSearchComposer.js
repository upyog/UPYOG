import React, { useEffect, useReducer, useState } from "react";
import ResultsTable from "./ResultsTable"
import reducer, { initialInboxState } from "./InboxSearchComposerReducer";
import InboxSearchLinks from "../atoms/InboxSearchLinks";
import { InboxContext } from "./InboxSearchComposerContext";
import SearchComponent from "../atoms/SearchComponent";
import PopUp from "../atoms/PopUp";
import SearchAction from "../molecules/SearchAction";
import FilterAction from "../molecules/FilterAction";
import MobileSearchComponent from "./MobileView/MobileSearchComponent";
import MobileSearchResults from "./MobileView/MobileSearchResults";
import MediaQuery from 'react-responsive';
import _ from "lodash";
import { useTranslation } from "react-i18next";
import MobileSearchResultsv1 from "./MobileView/MobileSearchResultsv1";
import RemovableTags from "./RemovableTags";

function isFalsyOrEmpty(input) {
    if (input === false) {
      return true;
    }

    if(!input) {
      return true;
    }
    
    if (Array.isArray(input) && input.length === 0) {
      return true;
    }
    
    if (typeof input === 'object' && Object.keys(input).length === 0) {
      return true;
    }
    
    return false;
  }

const InboxSearchComposer = ({configs,scrollPosition,browserSession}) => {
    
    const [session,setSession,clearSession] = browserSession || []
   
    const {t} = useTranslation()
    const presets = Digit.Hooks.useQueryParams();
    // if(Object.keys(presets).length > 0) configs = Digit.Utils.configUpdater(configs)

    const [enable, setEnable] = useState(false);
    const [state, dispatch] = useReducer(reducer, initialInboxState);
    
    //for mobile view
    const [type, setType] = useState("");
    const [popup, setPopup] = useState(false);
   
    const apiDetails = configs?.apiDetails

    const [activeLink,setActiveLink] = useState(configs?.sections?.search?.uiConfig?.configNavItems?.filter(row=>row.activeByDefault)?.[0])
    
    //for mobile view
    useEffect(() => {
        if (type) setPopup(true);
      }, [type]);
    
    useEffect(() => {
        //here if jsonpaths for search & table are same then searchform gets overridden
        
        if (Object.keys(state.searchForm)?.length >= 0) {
            const result = { ..._.get(apiDetails, apiDetails?.searchFormJsonPath, {}), ...state.searchForm }
            Object.keys(result).forEach(key => {
                if (!result[key]) delete result[key]
            });
            _.set(apiDetails, apiDetails?.searchFormJsonPath, result)
        }
        if (Object.keys(state.filterForm)?.length >= 0) {
            const result = { ..._.get(apiDetails, apiDetails?.filterFormJsonPath, {}), ...state.filterForm }
            Object.keys(result).forEach(key => {
                if (!result[key] || result[key]?.length===0) delete result[key]
            });
            _.set(apiDetails, apiDetails?.filterFormJsonPath, result)
        }
        
        if(Object.keys(state.tableForm)?.length >= 0) {
            _.set(apiDetails, apiDetails?.tableFormJsonPath, { ..._.get(apiDetails, apiDetails?.tableFormJsonPath, {}),...state.tableForm })  
        }
        const searchFormParamCount = Object.keys(state.searchForm).reduce((count,key)=>isFalsyOrEmpty(state.searchForm[key])?count:count+1,0)
        const filterFormParamCount = Object.keys(state.filterForm).reduce((count, key) =>isFalsyOrEmpty(state.filterForm[key]) ? count : count + 1, 0)
        
        if (Object.keys(state.tableForm)?.length > 0 && (searchFormParamCount >= ( activeLink ?activeLink?.minParametersForSearchForm : apiDetails?.minParametersForSearchForm) || filterFormParamCount >= apiDetails?.minParametersForFilterForm)){
            setEnable(true)
        }

        if(configs?.type === 'inbox' || configs?.type === 'download') setEnable(true)

    },[state])
    
    //adding this effect in case of screen refresh
    useEffect(() => {
        if(_.isEqual(state, initialInboxState)){
            dispatch({
                type:"obj",
                updatedState:Object?.keys(session)?.length > 0 ? session : initialInboxState
            })
        } 
    },[])

    //adding another effect to sync session with state, the component invoking InboxSearchComposer will be passing session as prop
    useEffect(() => {
        // if(_.isEqual(state, initialInboxState)){
        //     return 
        // }
        // if(_.isEqual(state, session)){
        //     return 
        // }
        setSession(() => state)
        // if(!_.isEqual(state, session)){
        //     // setSession(()=>{
        //     //     return {
        //     //         ...state
        //     //     }
        //     // })
        //     setSession(state)
        // }
        
    },[state])

    

    let requestCriteria = {
        url:configs?.apiDetails?.serviceName,
        params:configs?.apiDetails?.requestParam,
        body:configs?.apiDetails?.requestBody,
        config: {
            enabled: enable,
        },
        state
    };

    const updatedReqCriteria = Digit?.Customizations?.[apiDetails?.masterName]?.[apiDetails?.moduleName]?.preProcess ? Digit?.Customizations?.[apiDetails?.masterName]?.[apiDetails?.moduleName]?.preProcess(requestCriteria,configs?.sections?.search?.uiConfig?.defaultValues,activeLink?.name) : requestCriteria 

    
    const { isLoading, data, revalidate,isFetching } = Digit.Hooks.useCustomAPIHook(updatedReqCriteria);
    
    
    useEffect(() => {
        return () => {
            revalidate();
            setEnable(false);
            if(!location.pathname.includes("tqm") && Digit.Utils.tqm.isUlbAdminLoggedIn()){
                sessionStorage.removeItem("Digit.TQM_INBOX_SESSION")
            }
        };
    });

    //for mobile view
    const handlePopupClose = () => {
        setPopup(false);
        setType("");
    };

    let fieldsForRemovableTags = []
    if((configs?.type === 'inbox' || configs?.type === 'search') && (configs?.showAsRemovableTagsInMobile)){
        if(configs?.sections?.search?.uiConfig?.fields) {
            fieldsForRemovableTags = configs?.sections?.search?.uiConfig?.fields
        }
        if(configs?.sections?.filter?.uiConfig?.fields) {
            fieldsForRemovableTags = [...fieldsForRemovableTags,...configs?.sections?.filter?.uiConfig?.fields]
        }
    }
    
    useEffect(() => {
        // Implement to scroll if scroll persistent is enabled 
        window.scrollTo(0, scrollPosition)
    })

    return (
        <InboxContext.Provider value={{state,dispatch}} >
            {/* <Header className="works-header-search">{t(configs?.label)}</Header> */}
            <div className="inbox-search-component-wrapper ">
            <div className={`sections-parent ${configs?.type}`}>
                {
                    configs?.sections?.links?.show &&  
                    <MediaQuery minWidth={426}>
                        <div className="section links">
                            <InboxSearchLinks 
                              headerText={configs?.sections?.links?.uiConfig?.label} 
                              links={configs?.sections?.links?.uiConfig?.links} 
                              businessService="WORKS" 
                              logoIcon={configs?.sections?.links?.uiConfig?.logoIcon}
                            ></InboxSearchLinks>
                        </div>
                    </MediaQuery>   
                }
                {
                    configs?.type === 'search' && configs?.sections?.search?.show &&
                    <MediaQuery minWidth={426}>
                        <div className="section search">
                            <SearchComponent 
                                uiConfig={ configs?.sections?.search?.uiConfig} 
                                header={configs?.sections?.search?.label} 
                                screenType={configs.type}
                                fullConfig={configs}
                                data={data}
                                activeLink={activeLink}
                                setActiveLink={setActiveLink}
                                browserSession={browserSession}
                                />
                        </div>
                    </MediaQuery>
                }
                {   
                    configs?.type === 'search' && configs?.sections?.filter?.show && 
                    <MediaQuery minWidth={426}>
                        <div className="section filter">
                            <SearchComponent 
                                uiConfig={ configs?.sections?.filter?.uiConfig} 
                                header={configs?.sections?.filter?.label} 
                                screenType={configs.type}
                                fullConfig={configs}
                                data={data}
                                activeLink={activeLink}
                                setActiveLink={setActiveLink}
                                browserSession={browserSession}
                                />
                        </div> 
                    </MediaQuery>

                }
                {
                    configs?.type === 'inbox' && configs?.sections?.search?.show &&
                    <MediaQuery minWidth={426}>
                        <div className="section search">
                            <SearchComponent 
                                uiConfig={ configs?.sections?.search?.uiConfig} 
                                header={configs?.sections?.search?.label} 
                                screenType={configs.type}
                                fullConfig={configs}
                                data={data}
                                browserSession={browserSession}
                                />
                        </div>
                     </MediaQuery>
                }
                {   
                    configs?.type === 'inbox' && configs?.sections?.filter?.show && 
                    <MediaQuery minWidth={426}>
                        <div className="section filter">
                            <SearchComponent 
                                uiConfig={ configs?.sections?.filter?.uiConfig} 
                                header={configs?.sections?.filter?.label} 
                                screenType={configs.type}
                                fullConfig={configs}
                                data={data}
                                browserSession={browserSession}
                                />
                        </div> 
                    </MediaQuery>
                }
                {   (configs?.type === 'inbox' || configs?.type === 'search') && 
                    <MediaQuery maxWidth={426}>
                        <div className="searchBox">
                        {
                        configs?.sections?.search?.show && (
                            <SearchAction 
                            text={t(configs?.sections?.search?.labelMobile)}
                            handleActionClick={() => {
                            setType("SEARCH");
                            setPopup(true);
                            }}
                            />
                        )}
                        {configs?.sections?.filter?.show && (
                        <FilterAction
                            text={t(configs?.sections?.filter?.labelMobile)}
                            handleActionClick={() => {
                                setType("FILTER");
                                setPopup(true);
                            }}
                        />
                        )}
                    </div>
                   </MediaQuery>
                }
                {
                    (configs?.type === 'inbox' || configs?.type === 'search') && (configs?.showAsRemovableTagsInMobile) &&
                    <MediaQuery maxWidth={426}>
                        <RemovableTags config={configs} browserSession={browserSession} data={data} 
                        // fields={[...configs?.sections?.search?.uiConfig?.fields,...configs?.sections?.filter?.uiConfig?.fields]} 
                        fields={fieldsForRemovableTags}
                        />
                   </MediaQuery>
                }
                {   
                    configs?.sections?.searchResult?.show &&
                        <div className="" style={data?.[configs?.sections?.searchResult?.uiConfig?.resultsJsonPath]?.length > 0 ? (!(isLoading || isFetching) ?{ overflowX: "auto" }: {}) : {  }} >
                            <MediaQuery minWidth={426}>
                    {/* configs?.sections?.searchResult?.show &&  
                        <div style={data?.[configs?.sections?.searchResult?.uiConfig?.resultsJsonPath]?.length > 0 ? (!(isLoading || isFetching) ?{ overflowX: "scroll", borderRadius : "4px" }: {}) : {  }} > */}

                            <ResultsTable 
                                config={configs?.sections?.searchResult?.uiConfig} 
                                data={data} 
                                isLoading={isLoading} 
                                isFetching={isFetching} 
                                fullConfig={configs}
                                type={configs?.type}
                                activeLink={activeLink}
                                browserSession={browserSession}
                                />
                            </MediaQuery>
                            <MediaQuery maxWidth={426}>
                            {/* <MobileSearchResults
                              config={configs?.sections?.searchResult?.uiConfig} 
                              data={data} 
                              isLoading={isLoading} 
                              isFetching={isFetching} 
                              fullConfig={configs}/> */}
                              <MobileSearchResultsv1
                              config={configs?.sections?.searchResult?.uiConfig} 
                              data={data} 
                              isLoading={isLoading} 
                              isFetching={isFetching} 
                              fullConfig={configs}/>
                            </MediaQuery>
                        </div>
                }
                {popup && (
              <PopUp>
              {type === "FILTER" && (
                <div className="popup-module">
                    <MobileSearchComponent
                    uiConfig={ configs?.sections?.filter?.uiConfig} 
                    header={configs?.sections?.filter?.label} 
                    modalType={type}
                    screenType={configs.type}
                    fullConfig={configs}
                    data={data}
                    onClose={handlePopupClose}
                    defaultValues={configs?.sections?.filter?.uiConfig?.defaultValues}
                    browserSession={browserSession}
                    />
                </div>
              )}
              {/* {type === "SORT" && (
            <div className="popup-module">
              {<SortBy type="mobile" sortParams={sortParams} onClose={handlePopupClose} onSort={onSort} />}
            </div>
              )} */}
              {type === "SEARCH" && (
                <div className="popup-module">
                    <MobileSearchComponent
                    uiConfig={ configs?.sections?.search?.uiConfig} 
                    header={configs?.sections?.search?.label} 
                    modalType={type}
                    screenType={configs.type}
                    fullConfig={configs}
                    data={data}
                    onClose={handlePopupClose}
                    defaultValues={configs?.sections?.search?.uiConfig?.defaultValues}
                    browserSession={browserSession}
                    />
                </div>
              )}
            </PopUp>
          )}
            </div>
            <div className="additional-sections-parent">
                {/* One can use this Parent to add additional sub parents to render more sections */}
            </div>
            </div>   
        </InboxContext.Provider>
    )
}

export default InboxSearchComposer;
