import React, { useMemo, useEffect } from "react";
import { useForm, Controller } from "react-hook-form";
import {
  BreakLine,
  Card,
  CardLabel,
  CardLabelError,
  CardSubHeader,
  CardSectionHeader,
  TextArea,
  ActionBar,
  SubmitBar,
  LabelFieldPair,
} from "@demodigit/digit-ui-react-components";
import TextInput from "../../../../react-components/src/atoms/TextInput";
import { useTranslation } from "react-i18next";

export const FormComposer = (props) => {
  const {
    register,
    handleSubmit,
    errors,
    control,
    setValue,
    getValues,
    reset,
    watch,
    setError,
    clearErrors,
    formState,
  } = useForm();
  const { t } = useTranslation();
  const formData = watch();

  function onSubmit(data) {
    props.onSubmit(data);
  }
  const changeValue = (name, data) => {
    sessionStorage.setItem(name, data);
  };

  useEffect(() => {
    props.getFormAccessors && props.getFormAccessors({ setValue, getValues });
  }, []);

  const fieldSelector = (type, populators, component, value, config) => {
    const Component =
      typeof component === "string"
        ? Digit.ComponentRegistryService.getComponent(component)
        : component;

    switch (type) {
      case "text":
        return (
          <div className="field-container">
            {populators.componentInFront ? populators.componentInFront : null}
            <TextInput
              className="field desktop-w-full"
              {...populators}
              inputRef={register(populators.validation)}
              value={value}
              style={{ marginRight: populators.componentInFront ? "32px" : "0px" }}
            />
          </div>
        );
      case "textarea":
        return (
          <TextArea
            className="field desktop-w-full textarea-full"
            value={value}
            name={populators.name || ""}
            {...populators}
            inputRef={register(populators.validation)}
          />
        );
      case "component": {
        if (props?.config?.[1]?.body?.[2]?.isProperty) {
          return (
            <Controller
              render={(props) => (
                <Component
                  userType={"employee"}
                  t={t}
                  setValue={setValue}
                  onSelect={setValue}
                  config={config}
                  data={formData}
                  formData={formData}
                  register={register}
                  errors={errors}
                  props={props}
                  setError={setError}
                  clearErrors={clearErrors}
                  formState={formState}
                  onBlur={props.onBlur}
                />
              )}
              name={config.key}
              control={control}
            />
          );
        }
        break;
      }
      default:
        return populators?.dependency !== false ? populators : null;
    }
  };

  const formFields = useMemo(
    () =>
      props.config?.map((section, index, array) => {
        return (
          <React.Fragment key={index}>
            {/* Section header + subtle divider */}
            <div className="section-header-wrapper">
              <CardSectionHeader>{section.head}</CardSectionHeader>
              <div className="section-header-line" />
            </div>

            <style>
              {`
                /* layout grid: 3 columns on wide, 2 on medium, 1 on mobile */
                .form-section-grid {
                  display: grid;
                  grid-template-columns: repeat(3, 1fr);
                  gap: 18px;
                  margin-bottom: 28px;
                  align-items: start;
                }
                @media (max-width: 992px) {
                  .form-section-grid {
                    grid-template-columns: repeat(2, 1fr);
                  }
                }
                @media (max-width: 600px) {
                  .form-section-grid {
                    grid-template-columns: 1fr;
                  }
                }

                /* make textarea span the whole grid row */
                .form-field-wrapper.textarea-span {
                  grid-column: 1 / -1;
                }

                /* make sure the TextArea control fills width */
                .textarea-full,
                .form-field-wrapper.textarea-span .field textarea,
                .form-field-wrapper.textarea-span .field .dm-textarea {
                  width: 100% !important;
                  min-height: 140px;
                  box-sizing: border-box;
                  resize: vertical;
                }

                .section-header-wrapper {
                  margin-bottom: 12px;
                }
                .section-header-line {
                  height: 2px;
                  background: #eef2f6;
                  border-radius: 2px;
                  margin-top: 8px;
                }
                .card .card-label, .card-emp .card-label {
                  font-size: 16px;
                  line-height: 23px;
                  color: #0b0c0c;
                  margin-bottom: 8px;
                }
                .card-label-error {
                  color: #ff4d4f;
                  font-size: 12px;
                  margin-top: 4px;
                }
              `}
            </style>

            <div className="form-section-grid">
              {section.body.map((field, idx) => {
                // if textarea -> span full width
                const shouldSpanFull = field.type === "textarea" || field.populators?.fullWidth;
                const wrapperClass = shouldSpanFull ? "form-field-wrapper textarea-span" : "form-field-wrapper";

                return (
                  <div className={wrapperClass} key={idx}>
                    {errors[field?.populators?.name] && <CardLabelError>{field.populators.error}</CardLabelError>}
                    <LabelFieldPair>
                      <CardLabel>
                        {field.label}
                        {field.isMandatory ? " * " : null}
                      </CardLabel>
                      <div className="field">{fieldSelector(field.type, field.populators, field?.component, field.value, field)}</div>
                    </LabelFieldPair>
                  </div>
                );
              })}
            </div>

            {array.length - 1 === index ? null : <BreakLine />}
          </React.Fragment>
        );
      }),
    [props.config, errors]
  );

  const isDisabled = props.isDisabled || false;
  return (
    <form onSubmit={handleSubmit(onSubmit)} onChange={(e) => changeValue(e.target.name, e.target.value)}>
      <Card className="styled-form">
        <CardSubHeader className="form-header">{props.heading}</CardSubHeader>
        <div className="form-sections">{formFields}</div>
        {props.children}
        <ActionBar>
          <SubmitBar disabled={isDisabled} label={t(props.label)} submit="submit" />
        </ActionBar>

        <style>
          {`
            .styled-form { background: #fff; border-radius: 14px; padding: 22px; box-shadow: 0 6px 20px rgba(0,0,0,0.06); }
            .form-header { font-size: 20px; font-weight: 700; color: #222; margin-bottom: 18px; }
            .form-header::before { content: "📝"; margin-right: 8px; }
            .sticky-action-bar { position: sticky; bottom: 0; background: #fff; border-top: 1px solid #eee; padding: 12px; display:flex; justify-content:flex-end; z-index:10; border-radius:0 0 12px 12px;}
            .card-textarea {
              border-radius: 8px;
              min-height: 100px !important;
              max-width : 100%;
            }
            .select-wrap .select-active
{
  padding: 0px !important;
}
          `}
        </style>
      </Card>
    </form>
  );
};
