import React, { Fragment, useState } from "react";
import Card from "../../atoms/Card";
import { Loader } from "../../atoms/Loader";
import { RenderDataSection, RenderDocumentsSection, RenderWfActions, RenderWfHistorySection } from "./renderUtils";
import HorizontalNav from "../../atoms/HorizontalNav";
import CardSubHeader from "../../atoms/CardSubHeader";

// format of data expected by this component

// {
//   cards:[
//     {
//       sections: [
//         {
//           type: "DATA",
//           sectionHeader: { value: "Section 1", inlineStyles: {} },
//           cardHeader: { value: "Card 2", inlineStyles: {} },
//           values: [
//             {
//               key: "key 1",
//               value: "value 1",
//             },
//             {
//               key: "key 2",
//               value: "value 2",
//             },
//             {
//               key: "key 3",
//               value: "value 3",
//             },
//           ],
//         },
//         {
//           type: "DATA",
//           sectionHeader: { value: "Section 2", inlineStyles: { marginTop: "2rem" } },
//           // cardHeader:{value:"Card 1",inlineStyles:{}},
//           values: [
//             {
//               key: "key 1",
//               value: "value 1",
//             },
//             {
//               key: "key 2",
//               value: "value 2",
//             },
//             {
//               key: "key 3",
//               value: "value 3",
//             },
//           ],
//         },
//         {
//           type: "DOCUMENTS",
//           documents: [
//             {
//               title: "WORKS_RELEVANT_DOCUMENTS",
//               BS: "Works",
//               values: [
//                 {
//                   title: "Proposal document",
//                   documentType: "PROJECT_PROPOSAL",
//                   documentUid: "cfed582b-31b0-42e9-985f-fb9bb4543670",
//                   fileStoreId: "cfed582b-31b0-42e9-985f-fb9bb4543670",
//                 },
//                 {
//                   title: "Finalised worklist",
//                   documentType: "FINALIZED_WORKLIST",
//                   documentUid: "f7543894-d3a1-4263-acb2-58b1383eebec",
//                   fileStoreId: "f7543894-d3a1-4263-acb2-58b1383eebec",
//                 },
//                 {
//                   title: "Feasibility analysis",
//                   documentType: "FEASIBILITY_ANALYSIS",
//                   documentUid: "c4fb4f5d-a4c3-472e-8991-e05bc2d671f5",
//                   fileStoreId: "c4fb4f5d-a4c3-472e-8991-e05bc2d671f5",
//                 },
//               ],
//             },
//           ],
//           inlineStyles: {
//             marginTop: "1rem",
//           },
//         },
//         {
//           type: "WFHISTORY",
//           businessService: "ESTIMATE",
//           applicationNo: "ES/2023-24/000828",
//           tenantId: "pg.citya",
//           timelineStatusPrefix: "TEST",
//         },
//         {
//           type: "WFACTIONS",
//           forcedActionPrefix: "TEST",
//           businessService: "ESTIMATE",
//           applicationNo: "ES/2023-24/000828",
//           tenantId: "pg.citya",
//           applicationDetails: {},
//           url: "/estimate/v1/_update",
//           moduleCode: "Estimate",
//           editApplicationNumber: undefined,
//         },
//       ],
//     },
//   ],
//   apiResponse:{},
//   additionalDetails:{}
// }

const renderCardSectionJSX = (section) => {
  const { type } = section;
  switch (type) {
    case "DATA":
      return <RenderDataSection section={section} />;
    case "DOCUMENTS":
      return <RenderDocumentsSection section={section} />;
    case "WFHISTORY":
      return <RenderWfHistorySection section={section} />;
    case "WFACTIONS":
      return <RenderWfActions section={section} />;
    case "COMPONENT":
      const Component = Digit.ComponentRegistryService.getComponent(section.component);
      return (
        <>
          {section.cardHeader && <CardSubHeader style={section?.cardHeader?.inlineStyles}>{section.cardHeader.value}</CardSubHeader>}
          <Component {...section.props} />
        </>
      );
    default:
      return null;
  }
};

//data is the response of the hook call for View Screen
const ViewComposer = ({ isLoading = false, data, ...props }) => {
  const { cards } = data;
  const [activeNav, setActiveNav] = useState(data?.horizontalNav?.activeByDefault);
  

  if (isLoading) return <Loader />;

  return (
    <>
      {/* This first {} is for rendering cards at the top without navigationKey(out of navbar) */}
      {cards
        ?.filter((card) => !card?.navigationKey && card?.sections)
        ?.map((card, cardIdx) => {
          const { sections } = card;
          return (
            <Card style={activeNav && card.navigationKey ? (activeNav !== card.navigationKey ? { display: "none" } : {}) : {}} className={"employeeCard-override"}>
              {sections?.map((section, sectionIdx) => {
                return renderCardSectionJSX(section);
              })}
            </Card>
          );
        })}
      {/* This second section is for rendering cards that are part of the navBar) */}

      <HorizontalNav
        showNav={data?.horizontalNav?.showNav}
        configNavItems={data?.horizontalNav?.configNavItems}
        activeLink={activeNav}
        setActiveLink={setActiveNav}
        inFormComposer={false}
      >
        {cards
          ?.filter((card) => card?.navigationKey)
          ?.map((card, cardIdx) => {
            const { sections } = card;
            return (
              <Card style={activeNav && card.navigationKey ? (activeNav !== card.navigationKey ? { display: "none" } : {}) : {}} className={"employeeCard-override"}>
                {sections?.map((section, sectionIdx) => {
                  return renderCardSectionJSX(section);
                })}
              </Card>
            );
          })}
      </HorizontalNav>
    </>
  );
};

export default ViewComposer;
