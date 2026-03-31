import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Dropdown, Header, Toast } from "@upyog/digit-ui-react-components";
import { Switch, useLocation, Link } from "react-router-dom";


const CitizenFaqs = ({
  useNewInboxAPI,
  parentRoute,
  moduleCode = "PT",
  initialStates = {},
  filterComponent,
  isInbox,
  rawWfHandler,
  rawSearchHandler,
  combineResponse,
  wfConfig,
  searchConfig,
  middlewaresWf,
  middlewareSearch,
  EmptyResultInboxComp,
}) => {
    const tenantId = Digit.ULBService.getCurrentTenantId();

    const { t } = useTranslation();
    const [activeIndex, setActiveIndex] = useState(null);
    const [isOpen, setIsOpen] = useState(false);

    const toggle = (index) => {
        setActiveIndex(activeIndex === index ? null : index);
        setIsOpen(!isOpen);
    };  
    const faqList= [
        {
            "question": "What details must be provided in an amalgamation request?",
            "answer": "Existing owner names, UPINs, property addresses, ward numbers, proposed new ownership details."
        },
        {
            "question": "What details must be provided in a separation request?",
            "answer": "Existing owner details, property addresses, ward numbers, and proposed multiple ownership details."
        },
        {
            "question": "How is a self-assessment return (Form MMPTR-29) structured?",
            "answer": "In the My Properties module, the View Details option for the specific active Property ID contains a Self-Assess section that shows the Self-Assessment Year and the selected Payment Mode (Yearly, Half-Yearly, or Quarterly)."
        },
        {
            "question": "How are “Multiplicative Factors” defined?",
            "answer": "The factors include Location Factor, Structure Factor, and Ownership & Usage Factor, which are used in the calculation of the Annual Property Value."
        },
        {
            "question": "What roads are covered under the “Location Factor” definitions?",
            "answer": "National/State Highways (25-45m ROW), Major District Roads (7-15m), Other District Roads (4-12m), Village Roads (<4m)."
        },
        {
            "question": "How often does the Board review classification of municipal area and unit-area values?",
            "answer": "At least once every five years."
        },
        {
            "question": "What is the procedure if the Board fails to notify “Multiplicative Factors” on time?",
            "answer": "The current year's factors continue for the next assessment year."
        },
        {
            "question": "Where are notifications of “Multiplicative Factors” published?",
            "answer": "In the Official Gazette on the Property Tax Manipur Website."
        },
        {
            "question": "What is the “Rate of Property Tax” and how is it notified?",
            "answer": "Notified annually by 31 January; cannot be modified during the assessment year."
        },
        {
            "question": "How often can the property-tax rate be revised?",
            "answer": "At least once every two years, with a maximum increase of 3%."
        },
        {
            "question": "What is the “Annual Property Value” (APV)?",
            "answer": "APV is the sum of vacant land APV and covered area APV, calculated using area, unit value, and various factors."
        },
        {
            "question": "How is the “Location Factor” value determined?",
            "answer": "Based on the type of road adjoining the property."
        },
        {
            "question": "How is the “Structure Factor” determined?",
            "answer": "Based on building height and construction type (kutcha, semi-pucca, pucca)."
        },
        {
            "question": "How is the “Ownership / Usage Factor” calculated?",
            "answer": "Depends on ownership and usage type such as residential, commercial, mixed, tenant, or government use."
        },
        {
            "question": "What is a “Heritage Property” and how is it recorded?",
            "answer": "A property declared heritage and recorded as “Declared Heritage Property”."
        },
        {
            "question": "What is the meaning of “Semi-permanent / Semi-pucca”?",
            "answer": "A building constructed with partially permanent materials."
        },
        {
            "question": "What is the “Unit Area Value” and where is it published?",
            "answer": "Monetary value per unit area, published in draft classification notification."
        },
        {
            "question": "What is the “Assessment Year” in property-tax context?",
            "answer": "The fiscal year for which property tax is calculated."
        },
        {
            "question": "How are “Rebate” rules applied to property-tax payment dates?",
            "answer": "Early payments (e.g., before 30 June) may get rebates."
        },
        {
            "question": "How can the Property Owner check whether the Property ID has been verified?",
            "answer": "If status is Active, it's verified; Workflow means pending."
        },
        {
            "question": "What is the purpose of taking a “Picture of House” during the survey?",
            "answer": "To visually verify property condition and structure."
        },
        {
            "question": "How are “Co-owners” recorded in the ownership section?",
            "answer": "Names are listed and marked as multiple owners."
        },
        {
            "question": "What is the “Nature of Ownership” for a government property?",
            "answer": "Options include Central Govt., State Govt., Local Govt., PSU, etc."
        },
        {
            "question": "Who are considered in Special Category?",
            "answer": "Defense personnel, freedom fighters, disabled, war widows, etc."
        },
        {
            "question": "What does “Vacant Land Plot with commercial use” indicate?",
            "answer": "Land without buildings used for commercial activities."
        },
        {
            "question": "Difference between Land Area and Built-up Area?",
            "answer": "Land Area is total owned land; Built-up Area is constructed space."
        },
        {
            "question": "What payment modes are accepted for property-tax dues?",
            "answer": "Credit Card, Debit Card, Net Banking, UPI."
        },
        {
            "question": "How is a receipt for a property-tax payment obtained?",
            "answer": "Download during payment or later from My Payment module."
        },
        {
            "question": "How can a property owner file an appeal?",
            "answer": "Through the Appeal Section in My Properties module."
        },
        {
            "question": "What issues may arise if the property owner fails to submit the Self-Assessment?",
            "answer": "Interest, penalties, and notice may be issued."
        },
        {
            "question": "What are the statutory due dates for property-tax payment?",
            "answer": "30 June, 30 September, 31 December, 26 February."
        },
        {
            "question": "Which form is required for requesting structural changes to a property?",
            "answer": "Update Property section in My Properties module."
        },
        {
            "question": "What details appear on the property-tax receipt?",
            "answer": "Owner name, address, UPIN, payment details, transaction ID, etc."
        },
        {
            "question": "What is the procedure for transferring title and mutation?",
            "answer": "Search property, modify owner details, and submit in Transfer Ownership module."
        }
    ];
    const styles = {
        container: { border: "1px solid #ccc", marginBottom: "10px",width:"98%",borderRadius:"5px", overflow:"hidden" },
        header: { padding: "10px", background: "#ddd", cursor: "pointer", display: "flex", justifyContent: "space-between" },
        content: { padding: "10px", background: "#f9f9f9", transform: "scaleY(1)", transformOrigin: "top", transition: "transform 3s ease" },
    };

      return (
        <div>
            <div>
                <Header>{t("FAQs")}</Header>
            </div>
            <div>
                {faqList.map((item, index) => (
                    <div key={index} style={styles.container}>
                    <div style={styles.header} onClick={() => toggle(index)}>
                        <span>{index + 1}. {item.question}</span>
                        <span>{activeIndex == index ? "▲" : "▼"}</span>
                    </div>

                    {activeIndex === index && (
                        <div style={styles.content}>{item.answer}</div>
                    )}
                    </div>
                ))}
            </div>
        </div>
      );
};

export default CitizenFaqs;
