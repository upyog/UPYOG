import _ from 'lodash';

function removeObjectFromArray(array, key, value) {
  // Find the index of the object with the specified key and value
  const indexToRemove = array.findIndex((obj) => obj[key] === value);

  // If the object is found, remove it from the array
  if (indexToRemove !== -1) {
    array.splice(indexToRemove, 1);
  }

  // Return the modified array
  return array;
}

export const initialInboxState = {
  searchForm: {},
  filterForm: {},
  tableForm: {
    limit: 10,
    offset: 0,
  },
};

const reducer = (state, action) => {
  switch (action.type) {
    case 'searchForm':
      const { state: updatedSearchStateSearchForm } = action;
      return {
        ...state,
        searchForm: { ...state.searchForm, ...updatedSearchStateSearchForm },
      };
    case 'filterForm':
      const { state: updatedSearchStateFilterForm } = action;
      return {
        ...state,
        filterForm: { ...state.filterForm, ...updatedSearchStateFilterForm },
      };
    case 'tableForm':
      const updatedTableState = action.state;
      return {
        ...state,
        tableForm: { ...state.tableForm, ...updatedTableState },
      };
    case 'clearSearchForm':
      return { ...state, searchForm: action.state };
    case 'clearFilterForm':
      return { ...state, filterForm: action.state };
    case 'jsonPath':
      const {
        tag: { removableTagConf },
      } = action;
      const stateObj = _.cloneDeep(state);
      switch (removableTagConf?.type) {
        case 'multi':
          _.set(
            stateObj,
            removableTagConf?.sessionJsonPath,
            removeObjectFromArray(
              _.get(state, removableTagConf?.sessionJsonPath),
              removableTagConf?.deleteRef,
              removableTagConf?.value?.[removableTagConf?.deleteRef]
            )
          );
          return stateObj;

        case 'single':
          _.set(stateObj, removableTagConf?.sessionJsonPath, '');
          return stateObj;

        case 'dateRange':
          _.set(stateObj, removableTagConf?.sessionJsonPath, '');
          return stateObj;
        case 'workflowStatusFilter':
          //if we are here then we have dynamic ids to delete from state
          _.set(stateObj,`${removableTagConf?.sessionJsonPath}.${removableTagConf?.dynamicId}`, false)
          return stateObj
        default:
          break;
      }
    case 'obj':
        const {updatedState} = action
        return updatedState

    default:
      return state;
  }
};

export default reducer;
