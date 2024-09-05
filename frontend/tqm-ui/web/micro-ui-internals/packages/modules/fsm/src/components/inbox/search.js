import React, { useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import {
  TextInput,
  Label,
  SubmitBar,
  LinkLabel,
  ActionBar,
  CloseSvg,
  DatePicker,
  CardLabelError,
  Header,
  Card,
  Toast,
} from '@egovernments/digit-ui-react-components';
import DropdownStatus from './DropdownStatus';
import { useTranslation } from 'react-i18next';

const SearchApplication = ({
  onSearch,
  type,
  onClose,
  isFstpOperator,
  searchFields,
  searchParams,
  isInboxPage,
}) => {
  const storedSearchParams = isInboxPage
    ? Digit.SessionStorage.get('fsm/inbox/searchParams')
    : Digit.SessionStorage.get('fsm/search/searchParams');

  const {
    data: applicationStatuses,
    isFetched: areApplicationStatus,
  } = Digit.Hooks.fsm.useApplicationStatus();

  const { t } = useTranslation();
  const { register, handleSubmit, reset, watch, control } = useForm({
    defaultValues: storedSearchParams || searchParams,
  });
  const [error, setError] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const mobileView = innerWidth <= 640;
  const FSTP = Digit.UserService.hasAccess('FSM_EMP_FSTPO') || false;
  const watchSearch = watch([
    'applicationNos',
    'mobileNumber',
    'fromDate',
    'toDate',
  ]);

  const onSubmitInput = (data) => {
    let isValidate = searchValidation();
    if (isValidate === false) return null;
    if (!data.mobileNumber) {
      delete data.mobileNumber;
    }
    onSearch(data);
    if (type === 'mobile') {
      onClose();
    }
  };

  function clearSearch() {
    const resetValues = searchFields.reduce(
      (acc, field) => ({ ...acc, [field?.name]: '' }),
      {}
    );
    reset(resetValues);
    if (isInboxPage) {
      Digit.SessionStorage.del('fsm/inbox/searchParams');
    } else {
      Digit.SessionStorage.del('fsm/search/searchParams');
    }
    onSearch({});
  }

  const clearAll = (mobileView) => {
    const mobileViewStyles = mobileView ? { margin: 0 } : {};
    return (
      <LinkLabel
        style={{ display: 'inline', ...mobileViewStyles }}
        onClick={clearSearch}
      >
        {t('ES_COMMON_CLEAR_SEARCH')}
      </LinkLabel>
    );
  };

  const searchValidation = (data) => {
    if (FSTP) return null;

    if (
      watchSearch.applicationNos ||
      watchSearch.mobileNumber ||
      (watchSearch.fromDate && watchSearch.toDate)
    ) {
      setError(false);
    } else {
      setError(true);
      setShowToast({ warning: true, label: "ERR_PT_FILL_VALID_FIELDS" });
      setTimeout(() => {
        setShowToast(null);
      }, 2000);
    }
    return watchSearch.applicationNos ||
      watchSearch.mobileNumber ||
      (watchSearch.fromDate && watchSearch.toDate)
      ? true
      : false;
  };

  const getFields = (input) => {
    switch (input.type) {
      case 'date':
        return (
          <Controller
            render={(props) => (
              <DatePicker date={props.value} onChange={props.onChange} />
            )}
            name={input.name}
            control={control}
            defaultValue={null}
          />
        );
      case 'status':
        return (
          <Controller
            render={(props) => (
              <DropdownStatus
                onAssignmentChange={props.onChange}
                value={props.value}
                applicationStatuses={applicationStatuses}
                areApplicationStatus={areApplicationStatus}
              />
            )}
            name={input.name}
            control={control}
            defaultValue={null}
          />
        );
      default:
        return (
          <TextInput
            {...input}
            inputRef={register}
            {...register(input.name)}
            watch={watch}
            shouldUpdate={true}
          />
        );
    }
  };
  const checkInboxLocation =
    window.location.href.includes('employee/fsm/inbox') ||
    window.location.href.includes('employee/fsm/fstp-inbox') ||
    window.location.href.includes('employee/fsm/fstp-fsm-request') ||
    window.location.href.includes('fsm/vehicle-tracking/alerts');

  const checkFSMInbox = window.location.href.includes('employee/fsm/inbox');

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        {!checkInboxLocation ? (
          <Header styles={mobileView ? { marginTop: '10px' } : {}}>
            {t('ACTION_TEST_SEARCH_FSM_APPLICATION')}
          </Header>
        ) : (
          ''
        )}
        {checkFSMInbox ? (
          <div
            className='search-container'
            style={{
              width: 'auto',
            }}
          >
            <div
              className='search-complaint-container'
              style={{ height: '194px' }}
            >
              {(type === 'mobile' || mobileView) && (
                <div className='complaint-header'>
                  <h2>{t('ES_COMMON_SEARCH_BY')}</h2>
                  <span
                    style={{
                      position: 'absolute',
                      top: '2%',
                      right: '8px',
                    }}
                    onClick={onClose}
                  >
                    <CloseSvg />
                  </span>
                </div>
              )}
              <div
                className={
                  FSTP
                    ? 'complaint-input-container for-pt for-search'
                    : 'complaint-input-container'
                }
                style={{ width: '100%' }}
              >
                {searchFields?.map((input, index) => (
                  <div key={input.name} className='input-fields'>
                    <span key={index} className={'mobile-input'}>
                      {/* <span key={index} className={index === 0 ? "complaint-input" : "mobile-input"}> */}
                      <Label>{input.label}</Label>
                      {getFields(input)}{' '}
                    </span>
                  </div>
                ))}
                {type === 'desktop' && !mobileView && (
                  <div
                    style={{
                      maxWidth: 'unset',
                      marginLeft: 'unset',
                      marginTop: '55px',
                    }}
                    className='search-submit-wrapper'
                  >
                    <SubmitBar
                      className='submit-bar-search'
                      label={t('ES_COMMON_SEARCH')}
                      submit
                    />
                    <div>{clearAll()}</div>
                  </div>
                )}
              </div>
            </div>
          </div>
        ) : (
          <div
            className='search-container'
            style={{
              width: 'auto',
              marginLeft: FSTP ? '' : isInboxPage ? '24px' : 'revert',
            }}
          >
            <div className='search-complaint-container'>
              {(type === 'mobile' || mobileView) && (
                <div className='complaint-header'>
                  <h2>{t('ES_COMMON_SEARCH_BY')}</h2>
                  <span
                    style={{
                      position: 'absolute',
                      top: '2%',
                      right: '8px',
                    }}
                    onClick={onClose}
                  >
                    <CloseSvg />
                  </span>
                </div>
              )}
              {!isInboxPage && (
                <span style={{ color: "#505A5F" }}>
                  {t("ES_SEARCH_APPLICATION_ERROR")}
                </span>
            )}
              <div
                className={
                  FSTP
                    ? 'complaint-input-container for-pt for-search'
                    : 'complaint-input-container'
                }
                style={{ width: '100%' }}
              >
                {searchFields?.map((input, index) => (
                  <div key={input.name} className='input-fields'>
                    <span key={index} className={'mobile-input'}>
                      {/* <span key={index} className={index === 0 ? "complaint-input" : "mobile-input"}> */}
                      <Label>{input.label}</Label>
                      {getFields(input)}{' '}
                    </span>
                  </div>
                ))}
                {type === 'desktop' && !mobileView && (
                  <div
                    style={{
                      maxWidth: 'unset',
                      marginLeft: 'unset',
                      marginTop: '55px',
                      display: "grid",
                      gridTemplateColumns: "1fr 1fr",
                      alignItems: "baseline",
                    }}
                    className='search-submit-wrapper'
                  >
                    <div>{clearAll()}</div>
                    <SubmitBar
                      className='submit-bar-search'
                      label={t('ES_COMMON_SEARCH')}
                      submit
                    />
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {(type === 'mobile' || mobileView) && (
          <ActionBar className='clear-search-container'>
            <button className='clear-search' style={{ flex: 1 }}>
              {clearAll(mobileView)}
            </button>
            <SubmitBar
              label={t('ES_COMMON_SEARCH')}
              style={{ flex: 1 }}
              submit={true}
            />
          </ActionBar>
        )}
        {showToast && (
          <Toast
            error={showToast.error}
            warning={showToast.warning}
            label={t(showToast.label)}
            onClose={() => {
              setShowToast(null);
            }}
          />
        )}
      </React.Fragment>
    </form>
  );
};

export default SearchApplication;
