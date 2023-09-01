import { FormComposer,CardLabel,TextInput,TextArea, Dropdown,LabelFieldPair, CardText, Header, CardHeader, StatusTable, LinkButton, Row, Loader, SubmitBar, Card } from "@egovernments/digit-ui-react-components";
import React ,{Fragment} from "react";
import { useTranslation } from "react-i18next";
import * as predefined from "../../../components/config/index";
import { newConfig } from "../../../components/config/config";


const PhysicalMilestone = ({ onSelect, config, formData, register, control, errors, setError }) => {
    const { t } = useTranslation();

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
        <div style={{display:"flex", justifyContent: "space-between",}}>
            <CardHeader>
                Milestone Entry
            </CardHeader>
            <StatusTable style={{border:"2px solid red"}}>
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
            </StatusTable>
        </div>
        </Card>
      </form>
    );
};

export default PhysicalMilestone;