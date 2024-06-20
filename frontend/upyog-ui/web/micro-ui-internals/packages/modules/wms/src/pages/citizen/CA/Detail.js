import React, { Fragment, createContext, useContext, useEffect, useState, useMemo } from "react";
import { newConfig } from "../../../components/config/CA/ca-config";
import { Card, FormComposer, Header, Loader, Table, Toast } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
// export const StepContext = createContext()

const ContractAgreementDetail = () => {
  const history = useHistory();
  const { id } = useParams();
  console.log("history params ", history, id);
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [showToast, setShowToast] = useState(null);
  const [isTrue, setisTrue] = useState(false);
  const [imagePath, setimagePath] = useState();

  const singleData = Digit?.Hooks?.wms?.ca?.useWmsCAGet(id, "CASingleList") || {};
  const { data: getCaSingleData } = singleData;
  // console.log("getCaSingleData ", getCaSingleData);
  // useEffect(() => {
  //   setisTrue(true)
  //   singleData.refetch()
  // }, []);
  const allAgreementData = getCaSingleData?.WMSContractAgreementApplication?.map((item) => item);
  // const contractorData = getCaSingleData?.WMSContractAgreementApplication[0]?.contractors?.map((item) => item);
  // const agreementData = getCaSingleData?.WMSContractAgreementApplication[0]?.agreement?.map((item) => item);
  // console.log("allAgreementData ",allAgreementData)
  // console.log("allAgreementData contractors",allAgreementData && [...allAgreementData[0]?.contractors,...allAgreementData[0]?.party2_witness])

  useEffect(()=>{
    singleData.refetch();
  },[])
  const [selected, setSelected] = useState(0);
  const toggle = (index) => {
    if (selected === index) {
      return setSelected(null);
    }
    setSelected(index);
  };
  const contractorColumns = React.useMemo(() => {
    return [
      {
        Header: "Vendor Type",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.vendor_type) ? row.original?.vendor_type : "-"}`;
        },
      },
      {
        Header: "Vendor Name",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.vendor_name) ? row.original?.vendor_name : "-"}`;
        },
      },
      {
        Header: "Represented By",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.represented_by) ? row.original?.represented_by : "-"}`;
        },
      },
      {
        Header: "Primary Party",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.primary_party) ? row.original?.primary_party : "-"}`;
        },
      },
    ];
  }, []);

  const agreementColumns = React.useMemo(() => {
    return [
      // {
      //   Header: "Vendor Type",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return (
      //       <span className="link">
      //         <Link to={`details/${props.tenantId}/${row.original.name}`}>{row.original.name}</Link>
      //       </span>
      //     );
      //   },
      // },
      {
        Header: "Agreement No",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.agreement_no) ? row.original?.agreement_no : "-"}`;
        },
      },

      {
        Header: "Agreement Date",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.agreement_date) ? row.original?.agreement_date : "-"}`;
        },
      },
      {
        Header: "Department Name",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.department_name_ai) ? row.original?.department_name_ai : "-"}`;
        },
      },
      {
        Header: "LOA No",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.loa_no) ? row.original?.loa_no : "-"}`;
        },
      },
      {
        Header: "Resolution No",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.resolution_no) ? row.original?.resolution_no : "-"}`;
        },
      },
      {
        Header: "Resolution Date",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.resolution_date) ? row.original?.resolution_date : "-"}`;
        },
      },
      {
        Header: "Tender No",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.tender_no) ? row.original?.tender_no : "-"}`;
        },
      },
      {
        Header: "Tender Date",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.tender_date) ? row.original?.tender_date : "-"}`;
        },
      },
      {
        Header: "Agreement Type",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.agreement_type) ? row.original?.agreement_type : "-"}`;
        },
      },
      {
        Header: "Defect Liability Period",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.defect_liability_period) ? row.original?.defect_liability_period : "-"}`;
        },
      },
      {
        Header: "Contract Period",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.contract_period) ? row.original?.contract_period : "-"}`;
        },
      },
      {
        Header: "Agreement Amount",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.agreement_amount) ? row.original?.agreement_amount : "-"}`;
        },
      },
      {
        Header: "Payment Type",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.payment_type) ? row.original?.payment_type : "-"}`;
        },
      },
    ];
  }, []);

  const sdPgBgColumns = React.useMemo(() => {
    return [
      {
        Header: "Deposit Type",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.deposit_type) ? row.original?.deposit_type : "-"}`;
        },
      },
      {
        Header: "Deposit Amount",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.deposit_amount) ? row.original?.deposit_amount : "-"}`;
        },
      },
      {
        Header: "Account No",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.account_no) ? row.original?.account_no : "-"}`;
        },
      },
      {
        Header: "Particulars",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.particulars) ? row.original?.particulars : "-"}`;
        },
      },

      {
        Header: "Valid From Date",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.valid_from_date) ? row.original?.valid_from_date : "-"}`;
        },
      },
      {
        Header: "Valid Till Date",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.valid_till_date) ? row.original?.valid_till_date : "-"}`;
        },
      },
      {
        Header: "Bank, Branch & IFSC Code",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.bank_branch_ifsc_code) ? row.original?.bank_branch_ifsc_code : "-"}`;
        },
      },
      {
        Header: "Payment Mode",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.payment_mode) ? row.original?.payment_mode : "-"}`;
        },
      },
    ];
  }, []);

  const ULBParty1Columns = React.useMemo(() => {
    return [
      {
        Header: "Department Name",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.department_name_party1) ? row.original?.department_name_party1 : "-"}`;
        },
      },
      {
        Header: "Designation",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.designation) ? row.original?.designation : "-"}`;
        },
      },
      {
        Header: "Employee Name",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.employee_name) ? row.original?.employee_name : "-"}`;
        },
      },
      {
        Header: "Witness Name",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.witness_name_p1) ? row.original?.witness_name_p1 : "-"}`;
        },
      },

      {
        Header: "Address",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.address_p1) ? row.original?.address_p1 : "-"}`;
        },
      },
      {
        Header: "UID",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.uid_p1) ? row.original?.uid_p1 : "-"}`;
        },
      },
    ];
  }, []);

  const termsAndConditionsColumns = React.useMemo(() => {
    return [
      {
        Header: "Terms And Conditions",
        disableSortBy: true,
        Cell: ({ row }) => {
          return `${Boolean(row.original?.terms_and_conditions) ? row.original?.terms_and_conditions : "-"}`;
        },
      },
    ];
  }, []);

  const acordianData = [
    {
      title: "Party II (Contractor)",
      columns: contractorColumns,
      body: allAgreementData && allAgreementData[0]?.contractors,
    },
    {
      title: "Agreement",
      columns: agreementColumns,
      body: allAgreementData && allAgreementData[0]?.agreement,
    },
    {
      title: "SD / PG / BG Details",
      columns: sdPgBgColumns,
      body: allAgreementData && allAgreementData[0]?.sDPGBGDetails,
    },
    {
      title: "ULB Party 1",
      columns: ULBParty1Columns,
      body: allAgreementData && allAgreementData[0]?.party1Details,
    },
    {
      title: "Terms and Conditions",
      columns: termsAndConditionsColumns,
      body: allAgreementData && allAgreementData[0]?.termsAndConditions,
    },
  ];

  if (allAgreementData === undefined) {
    return <Loader />;
  }
  return (
    <>
      <Header>{t("WMS_CA_CONTRACT_DETAIL")}</Header>

      {acordianData.map((item, index) => (
        <>
          <div
            onClick={() => toggle(index)}
            style={{ backgroundColor: "rgb(238 238 238)", marginTop: "5px", lineHeight: "30px", color: "#000", fontSize: "12px", fontWeight: "600" }}
          >
            <strong
              style={{
                backgroundColor: "#ad2f33",
                display: "inline-block",
                color: "#fff",
                width: "30px",
                textAlign: "center",
                fontSize: "20px",
                lineHeight: "30px",
                float: "left",
                marginRight: "5px",
              }}
            >
              {" "}
              {selected === index ? "-" : "+"}
            </strong>{" "}
            {item.title}
          </div>
          <div
            style={
              selected === index
                ? {
                    height: "auto",
                    maxHeight: "9999px",
                    transition: "all 1.5s cubic-bezier(0, 1, 0, 1)",
                  }
                : {
                    maxHeight: "0",
                    overflow: "hidden",
                    transition: "all 0s cubic-bezier(0, 1, 0, 1)",
                  }
            }
          >
            {item.body?.length < 1 ? (
              <>
                <Table
                t={t}
                  columns={item.columns}
                  allAgreementData
                  data={item.body}
                  getCellProps={(cellInfo) => {
                    return {
                      style: {
                        maxWidth: "150px",
                        padding: "20px 18px",
                        fontSize: "16px",
                        minWidth: "150px",
                      },
                    };
                  }}
                  isPaginationRequired={false}
                />
                <div style={{ textAlign: "center", color: "red", background: "#fff",  paddingTop: "20px",paddingBottom: "20px", marginTop: "-25px" }}>
                  No Records Found
                </div>
              </>
            ) : (
              <Table
              t={t}
                columns={item.columns}
                data={item.body}
                getCellProps={(cellInfo) => {
                  return {
                    style: {
                      maxWidth: "150px",
                      padding: "20px 18px",
                      fontSize: "16px",
                      minWidth: "150px",
                    },
                  };
                }}
              />
            )}
          </div>
          
        </>
      ))}

      {/* <div
        style={{ backgroundColor: "rgb(238 238 238)", marginBottom: "0px", lineHeight: "30px", color: "#000", fontSize: "12px", fontWeight: "600" }}
      >
        <strong
          style={{
            backgroundColor: "#ad2f33",
            display: "inline-block",
            color: "#fff",
            width: "30px",
            textAlign: "center",
            fontSize: "20px",
            lineHeight: "30px",
            float: "left",
            marginRight: "5px",
          }}
        >
          {" "}
          -{" "}
        </strong>{" "}
        Party II {"(Contractor)"}
      </div>
      <div>
        {(allAgreementData && allAgreementData[0]?.contractors?.length) < 1 ? (
          <>
            <Table
              columns={contractorColumns}
              allAgreementData
              data={allAgreementData[0]?.contractors}
              // data={contractorData}
              getCellProps={(cellInfo) => {
                return {
                  style: {
                    maxWidth: "150px",
                    padding: "20px 18px",
                    fontSize: "16px",
                    minWidth: "150px",
                  },
                };
              }}
              isPaginationRequired={false}
            />
            <div style={{ textAlign: "center", color: "red", background: "#fff",  paddingTop: "20px",paddingBottom: "20px", marginTop: "-25px" }}>No Records Found</div>
          </>
        ) : (
          <Table
            columns={contractorColumns}
            data={allAgreementData[0]?.contractors}
            // data={contractorData}
            getCellProps={(cellInfo) => {
              return {
                style: {
                  maxWidth: "150px",
                  padding: "20px 18px",
                  fontSize: "16px",
                  minWidth: "150px",
                },
              };
            }}
          />
        )}
      </div>
      <div
        style={{ backgroundColor: "rgb(238 238 238)", marginBottom: "0px", marginTop: "10px", lineHeight: "30px", color: "#000", fontSize: "12px", fontWeight: "600" }}
      >
        <strong
          style={{
            backgroundColor: "#ad2f33",
            display: "inline-block",
            color: "#fff",
            width: "30px",
            textAlign: "center",
            fontSize: "20px",
            lineHeight: "30px",
            float: "left",
            marginRight: "5px",
          }}
        >
          {" "}
          -{" "}
        </strong>{" "}
        Agreement {"(Contractor)"}
      </div>
      <div>
        {(allAgreementData && allAgreementData[0]?.agreement?.length) < 1 ? (
          <>
            <Table
              columns={agreementColumns}
              // data={agreementData}
              data={allAgreementData[0]?.agreement}
              getCellProps={(cellInfo) => {
                return {
                  style: {
                    maxWidth: "150px",
                    padding: "20px 18px",
                    fontSize: "16px",
                    minWidth: "150px",
                  },
                };
              }}
              isPaginationRequired={false}
            />
            <div style={{ textAlign: "center", color: "red", background: "#fff",  paddingTop: "20px",paddingBottom: "20px", marginTop: "-25px" }}>No Records Found</div>
          </>
        ) : (
          <Table
            columns={agreementColumns}
            // data={agreementData}
            data={allAgreementData[0]?.agreement}
            getCellProps={(cellInfo) => {
              return {
                style: {
                  maxWidth: "150px",
                  padding: "20px 18px",
                  fontSize: "16px",
                  minWidth: "150px",
                },
              };
            }}
          />
        )}
      </div>
      <div
        style={{ backgroundColor: "rgb(238 238 238)", marginBottom: "0px", marginTop: "10px", lineHeight: "30px", color: "#000", fontSize: "12px", fontWeight: "600" }}
      >
        <strong
          style={{
            backgroundColor: "#ad2f33",
            display: "inline-block",
            color: "#fff",
            width: "30px",
            textAlign: "center",
            fontSize: "20px",
            lineHeight: "30px",
            float: "left",
            marginRight: "5px",
          }}
        >
          {" "}
          -{" "}
        </strong>{" "}
        SD / PG / BG Details{" "}
      </div>
      <div>
        {(allAgreementData && allAgreementData[0]?.sDPGBGDetails?.length) < 1 ? (
          <>
            <Table
              columns={contractorColumns}
              allAgreementData
              data={allAgreementData[0]?.sDPGBGDetails}
              // data={contractorData}
              getCellProps={(cellInfo) => {
                return {
                  style: {
                    maxWidth: "150px",
                    padding: "20px 18px",
                    fontSize: "16px",
                    minWidth: "150px",
                  },
                };
              }}
              isPaginationRequired={false}
            />
            <div style={{ textAlign: "center", color: "red", background: "#fff",  paddingTop: "20px",paddingBottom: "20px", marginTop: "-25px" }}>No Records Found</div>
          </>
        ) : (
          <Table
            columns={contractorColumns}
            data={allAgreementData[0]?.sDPGBGDetails}
            // data={contractorData}
            getCellProps={(cellInfo) => {
              return {
                style: {
                  maxWidth: "150px",
                  padding: "20px 18px",
                  fontSize: "16px",
                  minWidth: "150px",
                },
              };
            }}
          />
        )}
      </div>

      <div
        style={{ backgroundColor: "rgb(238 238 238)", marginBottom: "0px", marginTop: "10px", lineHeight: "30px", color: "#000", fontSize: "12px", fontWeight: "600" }}
      >
        <strong
          style={{
            backgroundColor: "#ad2f33",
            display: "inline-block",
            color: "#fff",
            width: "30px",
            textAlign: "center",
            fontSize: "20px",
            lineHeight: "30px",
            float: "left",
            marginRight: "5px",
          }}
        >
          {" "}
          -{" "}
        </strong>{" "}
        ULB Party 1{" "}
      </div>
      <div>
        {(allAgreementData && allAgreementData[0]?.party1Details?.length) < 1 ? (
          <>
            <Table
              columns={contractorColumns}
              allAgreementData
              data={allAgreementData[0]?.party1Details}
              // data={contractorData}
              getCellProps={(cellInfo) => {
                return {
                  style: {
                    maxWidth: "150px",
                    padding: "20px 18px",
                    fontSize: "16px",
                    minWidth: "150px",
                  },
                };
              }}
              isPaginationRequired={false}
            />
            <div style={{ textAlign: "center", color: "red", background: "#fff",  paddingTop: "20px",paddingBottom: "20px", marginTop: "-25px" }}>No Records Found</div>
          </>
        ) : (
          <Table
            columns={contractorColumns}
            data={allAgreementData[0]?.party1Details}
            // data={contractorData}
            getCellProps={(cellInfo) => {
              return {
                style: {
                  maxWidth: "150px",
                  padding: "20px 18px",
                  fontSize: "16px",
                  minWidth: "150px",
                },
              };
            }}
          />
        )}
      </div>

      <div
        style={{ backgroundColor: "rgb(238 238 238)", marginBottom: "0px", marginTop: "10px", lineHeight: "30px", color: "#000", fontSize: "12px", fontWeight: "600" }}
      >
        <strong
          style={{
            backgroundColor: "#ad2f33",
            display: "inline-block",
            color: "#fff",
            width: "30px",
            textAlign: "center",
            fontSize: "20px",
            lineHeight: "30px",
            float: "left",
            marginRight: "5px",
          }}
        >
          {" "}
          -{" "}
        </strong>{" "}
        Terms and Conditions{" "}
      </div>
      <div>
        {(allAgreementData && allAgreementData[0]?.termsAndConditions?.length) < 1 ? (
          <>
            <Table
              columns={contractorColumns}
              allAgreementData
              data={allAgreementData[0]?.termsAndConditions}
              // data={contractorData}
              getCellProps={(cellInfo) => {
                return {
                  style: {
                    maxWidth: "150px",
                    padding: "20px 18px",
                    fontSize: "16px",
                    minWidth: "150px",
                  },
                };
              }}
              isPaginationRequired={false}
            />
            <div style={{ textAlign: "center", color: "red", background: "#fff",  paddingTop: "20px",paddingBottom: "20px", marginTop: "-25px" }}>No Records Found</div>
          </>
        ) : (
          <Table
            columns={contractorColumns}
            data={allAgreementData[0]?.termsAndConditions}
            // data={contractorData}
            getCellProps={(cellInfo) => {
              return {
                style: {
                  maxWidth: "150px",
                  padding: "20px 18px",
                  fontSize: "16px",
                  minWidth: "150px",
                },
              };
            }}
          />
        )}
      </div> */}


<style>
        {`
      
      iframe{background-color:blue;display:none}
      `}
      </style>
    </>
  );
};
export default ContractAgreementDetail;
