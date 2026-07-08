import React, { useMemo, useState, useEffect } from "react";
import {
  Table,
  Header,
  Card,
  SearchField,
  TextInput,
  SubmitBar,
  Loader,
  Dropdown,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";

// EST Property Allottee Details Component
// This component allows users to search and view details of property allottees with filtering options based on asset number, allottee name, and allotment status.

const ESTPropertyAllotteeDetails = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [selectedStatus, setSelectedStatus] = useState(null);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= 768);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const [filteredData, setFilteredData] = useState([]);
  const [loading, setLoading] = useState(true);
  const { register, handleSubmit, reset } = useForm();
  const isMountedRef = React.useRef(true);

  useEffect(() => {
    fetchAllotmentData();
    return () => {
      isMountedRef.current = false;
    };
  }, []);

  const fetchAllotmentData = async (searchParams = {}) => {
    if (!isMountedRef.current) return;
    setLoading(true);
    
    try {
      const response = await Digit.ESTService.allotmentSearch({
        tenantId,
        filters: {
          tenantId,
          ...searchParams
        }
      });
      
      if (!isMountedRef.current) return;
      const allotments = response?.Allotments || [];
      setFilteredData(allotments);
    } catch (error) {
      console.error("Error fetching allotment data:", error);
      if (isMountedRef.current) {
        setFilteredData([]);
      }
    } finally {
      if (isMountedRef.current)
        setLoading(false);
    }
  };
  const { data: allotmentStatusData } = Digit.Hooks.useCustomMDMS(
  Digit.ULBService.getStateId(),
  "Estate",
  [{ name: "AllotmentStatus" }],
  {
    select: (data) => data?.Estate?.AllotmentStatus || [],
  }
);

 const onSubmit = (data) => {
  const searchParams = {};
  if (data.assetNumber?.trim()) searchParams.assetNo = data.assetNumber.trim();
  if (data.alloteeName?.trim()) searchParams.alloteeName = data.alloteeName.trim();
  if (selectedStatus?.code) searchParams.status = selectedStatus.code;

  fetchAllotmentData(searchParams);
};

const clearFilters = (event) => {
  event.preventDefault(); // Prevent page refresh
  reset({ assetNumber: "", alloteeName: "" });
  setSelectedStatus(null);
  fetchAllotmentData();
};


  const GetCell = (value) => {
    if (!value || value === "string" || value === "0" || value === 0) {
      return <span className="cell-text">N/A</span>;
    }
    return <span className="cell-text">{value}</span>;
  };

  const columns = useMemo(
    () => [
      { 
        Header: t("EST_ASSET_NUMBER"), 
        accessor: "assetNo", 
        disableSortBy: true, 
        Cell: ({ row }) => (
          <span 
            style={{ color: "#a82227", cursor: "pointer", textDecoration: "underline" }}
            onClick={() => navigate(`${modulePath}/application-details/${row.original.assetNo}`)}
          >
            {row.original.assetNo || "N/A"}
          </span>
        )
      },
      { 
        Header: t("EST_ALLOTTEE_NAME"), 
        accessor: "alloteeName", 
        disableSortBy: true, 
        Cell: ({ row }) => GetCell(row.original.alloteeName) 
      },
      { 
        Header: t("EST_PHONE_NUMBER"), 
        accessor: "mobileNo", 
        disableSortBy: true, 
        Cell: ({ row }) => GetCell(row.original.mobileNo) 
      },
      { 
        Header: t("EST_MONTHLY_RENT"), 
        accessor: "monthlyRent", 
        disableSortBy: true, 
        Cell: ({ row }) => {
          const rent = row.original.monthlyRent;
          return GetCell(rent && rent !== "string" && rent !== 0 ? `₹${rent}` : "");
        } 
      },
      { 
        Header: t("EST_STATUS"), 
        accessor: "status", 
        disableSortBy: true, 
        Cell: ({ row }) => GetCell(row.original.status || "ACTIVE") 
      },
    ],
    [t, navigate, modulePath]
  );

  if (loading) return <Loader />;

  return (
    <div style={{ padding: isMobile ? '10px' : '20px' }}>
      <Header style={{ fontSize: isMobile ? '18px' : '24px', marginBottom: '15px' }}>{t("EST_COMMON_ALLOTTEE_DETAILS")}</Header>
      
      
      <Card style={{ padding: isMobile ? '10px' : '16px' }}>
         <span style={{color:"#505A5F", fontSize: isMobile ? '14px' : '16px',padding: isMobile ? '8px' : '10px',}}>{t("Provide at least one parameter to search for allottee details")}</span>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div style={{ 
            display: "grid", 
            gridTemplateColumns: isMobile ? "1fr" : "1fr 1fr 1fr", 
            gap: isMobile ? "12px" : "16px", 
            alignItems: "end" 
          }}>
            <div>
  <label htmlFor="assetNumber">Asset Number</label>
  <TextInput id="assetNumber" name="assetNumber" />
</div>

            <SearchField style={{ marginBottom: isMobile ? '10px' : '0' }}>
              <label style={{ fontSize: isMobile ? '14px' : '16px', marginBottom: '5px', display: 'block' }}>{t("EST_ALLOTTEE_NAME")}</label>
              <TextInput 
                name="alloteeName" 
                inputRef={register("alloteeName")}
                style={{ width: '100%', fontSize: isMobile ? '14px' : '16px', padding: isMobile ? '8px' : '10px' }}
              />
            </SearchField>

            <SearchField className="submit" style={{ display: 'flex', flexDirection: isMobile ? 'column' : 'row', gap: '10px', alignItems: isMobile ? 'stretch' : 'center' }}>
             <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSubmit(onSubmit)} submit style={{ width: isMobile ? '100%' : 'auto', fontSize: isMobile ? '14px' : '16px' }} />
              <p 
                style={{ 
                  marginTop: isMobile ? "10px" : "0", 
                  cursor: "pointer",
                  width: isMobile ? '100%' : 'auto',
                  textAlign: isMobile ? 'center' : 'left',
                  fontSize: isMobile ? '14px' : '16px'
                }} 
                onClick={clearFilters}
              >
                {t("ES_COMMON_CLEAR_ALL")}
              </p>
            </SearchField>
          </div>
        </form>
      </Card>

      <div style={{ 
        overflowX: "auto", 
        marginTop: "20px",
        WebkitOverflowScrolling: "touch",
        padding: isMobile ? '5px' : '10px'
      }}>
        <Table
          t={t}
          data={filteredData}
          columns={columns}
          totalRecords={filteredData.length}
          isPaginationRequired={true}
          pageSizeLimit={10}
          manualPagination={false}
          disableSort={true}
          getCellProps={() => ({
            style: {
              padding: isMobile ? "10px 8px" : "20px 18px",
              fontSize: isMobile ? "12px" : "16px",
              textAlign: "left",
              borderBottom: "1px solid #e0e0e0",
            },
          })}
        />
      </div>
    </div>
  );
};

export default ESTPropertyAllotteeDetails;
