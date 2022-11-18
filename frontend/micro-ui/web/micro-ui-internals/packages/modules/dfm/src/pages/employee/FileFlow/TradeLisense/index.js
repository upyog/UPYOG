import React from "react";
import { ReactComponent as BackArrow } from "../../Img/BackArrow.svg";
import { Switch, useLocation, Link } from "react-router-dom";
import {
  FormStep,
  TextInput,
  CheckBox,
  CardLabel,
  LabelFieldPair,
  TextArea,
  CitizenInfoLabel,
  ActionBar,
  SubmitBar,
} from "@egovernments/digit-ui-react-components";
import DFMuploadFiles from "./DFMuploadFiles";
import CardWrapper from "./cardWrapper";
import InputCommonWrapper from "./inputCommonWrapper";
import { useTranslation } from "react-i18next";
import DFMRadioButtons from "./DFMRadioButtons";

const TradeLisense = ({ parentUrl }) => {
  const handleClick = () => {
    console.log("clicked");
  };
  console.log(parentUrl);
  const { t } = useTranslation();
  const tmpOptions = [
    { label: "pension 2021-22", value: "pension 2021-22" },
    { label: "pension 2023-23", value: "pension 2022-23" },
  ];
  const genderOptions = [
    { label: "Male", value: "Male" },
    { label: "Female", value: "Female" },
    { label: "Others", value: "Others" },
  ];
  const pensionOptions = [
    { label: "Yes", value: "Yes" },
    { label: "No", value: "No" },
  ];

  return (
    <React.Fragment>
      <div className="tlContainer">
        <div className="tlWrapper offset-md-2 col-md-8">
          <div className="tlHead">
            <h4 className="tlBody">
              <Link to={parentUrl}>
                <div className="backIcon m-r-15">
                  <BackArrow />
                </div>
              </Link>
              Trade License File Flow
            </h4>
          </div>

          <CardWrapper title="Choose Type">
            <InputCommonWrapper label="Choose Type" placeholder="Choose Type" type="Dropdown" selectOptions={tmpOptions} onChange="" value="" />
          </CardWrapper>

          <CardWrapper title="Basic Details">
            <div className="col-md-10">
              <div className="cardSection">
                <div className="cardSectionBody">
                  <div className="cardContainer">
                    <div className="requiredHeader">Required Documents</div>
                    <div className="requiredBody">
                      <div className="requiredLabel">
                        <span className="badge badge-success"></span>Aadhar Card<span>*</span>
                      </div>
                      <div className="requiredLabel">
                        Ration Card<span>*</span>
                      </div>
                    </div>
                    <div className="requiredBody gapAdj-1">
                      <div className="requiredLabel">
                        Voters ID <span>*</span>
                      </div>
                      <div className="requiredLabel">
                        Passport<span>*</span>
                      </div>
                    </div>
                    <div className="requiredBody gapAdj-2">
                      <div className="requiredLabel">
                        Income Proof<span>*</span>
                      </div>
                      <div className="requiredLabel">
                        Nativity Certicificate<span>*</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </CardWrapper>

          <CardWrapper title="Application Details">
            <div className="">
              <InputCommonWrapper label="ID Type" placeholder="ID Type" type="TextInput" onChange="" value="" />
              <InputCommonWrapper label="ID No" placeholder="ID No" type="TextInput" onChange="" value="" />
              <InputCommonWrapper label="Name of Applicant" placeholder="Name of Applicant" type="TextInput" onChange="" value="" />
              <InputCommonWrapper label="House Name" placeholder="House Name" type="TextInput" onChange="" value="" />
              <InputCommonWrapper label="Ward Number & Name" placeholder="Ward Number & Name" type="TextInput" onChange="" value="" />
              <InputCommonWrapper label="Local Place" placeholder="Local Place" type="TextInput" onChange="" value="" />
              <InputCommonWrapper label="Main Place" placeholder="Main Place" type="TextInput" onChange="" value="" />
              <InputCommonWrapper label="Pin Code" placeholder="Pin Code" type="TextInput" onChange="" value="" />
              <InputCommonWrapper label="Mobile No" placeholder="Mobile No" type="TextInput" onChange="" value="" />
              <InputCommonWrapper label="Email ID" placeholder="Email ID" type="TextInput" onChange="" value="" />
            </div>
          </CardWrapper>
          <CardWrapper title="Service Details">
            <form>
              <InputCommonWrapper label="Date of Birth" placeholder="Date of Birth" type="DatePicker" onChange="" value="" />
              <InputCommonWrapper label="Name" placeholder="Name" type="TextInput" onChange="" value="" />
              <DFMRadioButtons title="Gender" options={genderOptions} />
              <DFMRadioButtons title="Has any pension" options={pensionOptions} />
              <InputCommonWrapper label="Remark" placeholder="Remark" type="TextArea" onChange="" value="" />
            </form>
          </CardWrapper>
          <CardWrapper title="Attachments">
            <div className="requiredHeader">Upload Files</div>
            <DFMuploadFiles title="Upload AadharCard" />
            <DFMuploadFiles title="Upload RationCard" />
            <DFMuploadFiles title="Upload Voters ID" />
            <DFMuploadFiles title="Upload Passport" />
            <DFMuploadFiles title="Upload Income Proof" />
            <DFMuploadFiles title="Upload Native Certificate" />
          </CardWrapper>
        </div>
      </div>
      <ActionBar>
        <SubmitBar disabled={false} label={t("Submit Appication")} submit="submit" onSubmit={() => handleClick()} />
      </ActionBar>
    </React.Fragment>
  );
};
export default TradeLisense;
