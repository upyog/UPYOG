import { FormComposer,CardLabel,TextInput,TextArea,SearchField, Dropdown,LabelFieldPair, CardText, Header, CardHeader, StatusTable, LinkButton, Row, Loader, SubmitBar, Card } from "@egovernments/digit-ui-react-components";
import React ,{useState,Fragment} from "react";
import { useTranslation } from "react-i18next";
import { Controller, useWatch } from "react-hook-form";
import * as predefined from "../../../components/config/index";
import MilestoneDetails from "./MilestoneDetails";
import { newConfig } from "../../../components/config/config";



const PhysicalMilestone = ({ onSelect, config, formData, register, control, errors, setError }) => {
    const { t } = useTranslation();

    // const [mobileNumber, setMobileNumber] = useState(formData?.mobileNumber || "");

    // const onSubmit = (data) => {  
    //     const DocumentEntity = {
    //       name: data.documentName,
    //       description: data?.description.length ? data.description : "",
    //       category: data.docCategory?.name,
    //       documentLink: data.document?.documentLink,
    //       filestoreId: data.document?.filestoreId?.fileStoreId,
    //       fileSize: data.document?.filestoreId?.fileSize,
    //       fileType: data.document?.filestoreId?.fileType,
    //       tenantIds: data.ULB.map((e) => e.code),
    //     };
    
    //     history.push("/upyog-ui/employee/engagement/documents/response", { DocumentEntity });
    //   };

console.log("newConfig ",newConfig)
console.log("newConfig.. ",...newConfig)
    return (
      <form>
        <Card>
        <CardHeader>
                Milestone Entry
            </CardHeader>
        <div >
            {/* <StatusTable style={{border:"2px solid red"}}>
            <Row
              key={"Project Name"}
              label={"Project Name"}
              labelStyle={{ minWidth: "fit-content", fontWeight: "normal" }}
              text={
                <div>
                  <TextInput type="number" name="wasteRecieved"  />
                </div>
              }
              rowContainerStyle={{ display: "block" ,justifyContent: "space-between" }}

            />
            <Row
              key={"Project Name"}
              label={"Milestone Name"}
              labelStyle={{ minWidth: "fit-content", fontWeight: "normal" }}
              text={
                <div>
                  <TextInput type="number" name="wasteRecieved"  />
                </div>
              }
              rowContainerStyle={{ display: "block" ,justifyContent: "space-between" }}

            /><Row
            key={"Project Name"}
            label={"Work Name"}
            labelStyle={{ minWidth: "fit-content", fontWeight: "normal" }}
            text={
              <div>
                <TextInput type="number" name="wasteRecieved"  />
              </div>
            }
            rowContainerStyle={{ display: "block" ,justifyContent: "space-between" }}

          /><Row
          key={"Project Name"}
          label={"Milestone Percentage"}
          labelStyle={{ minWidth: "fit-content", fontWeight: "normal" }}
          text={
            <div>
              <TextInput type="number" name="wasteRecieved"  />
            </div>
          }
          rowContainerStyle={{ display: "block" ,justifyContent: "space-between" }}

        />
            </StatusTable> */}
            <div >

            
            <SearchField>
                <label >
                    {`${t("Project Name")} *`}
                </label>
                <TextInput
                    name="billNo"
                    
                />
            </SearchField>
            <SearchField>
                <label>
                    {`${t("Milestone Name")} *`}
                </label>
                <TextInput name="billNo"/>
                    
            </SearchField>
            <SearchField>
                <label>
                    {`${t("Work Name")} *`}
                </label>
                <TextInput
                    name="billNo"
                    
                />
            </SearchField>
            <SearchField>
                <label>
                    {`${t("Milestone Percentage")} *`}
                </label>
                <TextInput
                    name="billNo"
                    
                />
            </SearchField>

            </div>

            <div>
              {/* <div className="filter-label">{t("LABEL_FOR_ULB")}</div> */}
              {/* <Dropdown option={"project 1"}   t={t} /> */}
              {/* <Controller
                    control={control}
                    rules={{ required: t("REQUIRED_FIELD") }}
                    name="businesService"
                    render={(props) => (
                        <Dropdown
                            option={serviceTypeList}
                            select={(e) => {
                                props.onChange(e);
                            }}
                            optionKey="name"
                            onBlur={props.onBlur}
                            t={t}
                            selected={props.value}
                            optionCardStyles={{zIndex:"20"}}
                        />
                    )}
                /> */}
            </div>
            
        </div>
        <SubmitBar style={{marginBottom: "30px"}} label={t("Save")} submit />
        {/* Need to execute Reset button here , as well  */}

        {/* <div className="field-container">
          <span className="employee-card-input employee-card-input--front" style={{ marginTop: "-1px" }}>
            +91
          </span>
          <TextInput
            type={"mobileNumber"}
            t={t}
            isMandatory={false}
            style={{maxWidth:"500px"}}
            optionKey="i18nKey"
            
          />
          <TextInput
            type={"any"}
            t={t}
            isMandatory={false}
            style={{maxWidth:"500px"}}
            optionKey="i18nKey"
            
          />
        </div> */}

        <hr style= {{color: "rgb(204, 204, 204)"}}></hr>

        <MilestoneDetails />
        </Card>
        
      </form>
    );
};

export default PhysicalMilestone;