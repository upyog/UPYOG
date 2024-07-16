/** 
*   @author - Shivank - NIUA
*   all the assets are dynamically rendering from this page on the slection of their parent or sub parent category
*   TODO: Need to change normal used functions to Arrow Function for the better performance.
*
*/



import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Dropdown,InfoBannerIcon  } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import Timeline from "../components/ASTTimeline";
import { Controller, useForm } from "react-hook-form";


const NewAsset
  = ({ t, config, onSelect, formData }) => {
    console.log("formdatats",formData);
    const { pathname: url } = useLocation();
    let index = window.location.href.charAt(window.location.href.length - 1);
    let validation = {};
    

    const [landType, setlandType] = useState(formData?.asset?.assetsubtype?.value || "");

    const [area, setArea] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].area) || formData?.assetDetails?.area || ""
    );

    const [buildingSno, setBuildingsno] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].buildingSno) || formData?.assetDetails?.buildingSno || ""
    );

    const [plotArea, setplotarea] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].plotArea) || formData?.assetDetails?.plotArea || ""
    );



    const [bookValue, setBookValue] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].bookValue) || formData?.assetDetails?.bookValue || ""
    );
    const [dateOfDeedExecution, setDateofDeedExecution] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].dateOfDeedExecution) || formData?.assetDetails?.dateOfDeedExecution || ""
    );
    const [dateOfPossession, setDateofPossesion] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].dateOfPossession) || formData?.assetDetails?.dateOfPossession || ""
    );
    const [fromWhomDeedTaken, setFromWhomDeedTaken] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].fromWhomDeedTaken) || formData?.assetDetails?.fromWhomDeedTaken || ""
    );
    const [governmentOrderNumber, setGovernmentorderNumber] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].governmentOrderNumber) || formData?.assetDetails?.governmentOrderNumber || ""
    );
    const [collectorOrderNumber, setCollectororderNumber] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].collectorOrderNumber) || formData?.assetDetails?.collectorOrderNumber || ""
    );
    const [councilResolutionNumber, setCouncilResolutionNumber] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].councilResolutionNumber) || formData?.assetDetails?.councilResolutionNumber || ""
    );
    const [awardNumber, setAwardNumber] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].awardNumber) || formData?.assetDetails?.awardNumber || ""
    );
    const [oAndMCOI, setOandMCOI] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].oAndMCOI) || formData?.assetDetails?.oAndMCOI || ""
    );
    const [oAndMTaskDetail, setOandMTaskDetail] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].oAndMTaskDetail) || formData?.assetDetails?.oAndMTaskDetail || ""
    );
    const [totalCost, setTotalcost] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].totalCost) || formData?.assetDetails?.totalCost || ""
    );
    const [depreciationRate, setDepriciationRate] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].depreciationRate) || formData?.assetDetails?.depreciationRate || ""
    );
    const [costAfterDepreciation, setCostafterdepriciation] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].costAfterDepreciation) || formData?.assetDetails?.costAfterDepreciation || ""
    );
    const [currentAssetValue, setCurrentassetvalue] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].currentAssetValue) || formData?.assetDetails?.currentAssetValue || ""
    );
    const [revenueGeneratedByAsset, setRevenuegeneratedbyasset] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].revenueGeneratedByAsset) || formData?.assetDetails?.revenueGeneratedByAsset || ""
    );
    const [osrLand, setOSRLand] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].osrLand) || formData?.assetDetails?.osrLand || ""
    );
    const [isitFenced, setisitfenced] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].isitFenced) || formData?.assetDetails?.isitFenced || ""
    );
    const [anyBuiltup, setAnyBuiltup] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].anyBuiltup) || formData?.assetDetails?.anyBuiltup || ""
    );
    const [howAssetBeingUsed, sethowassetbeingused] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].howAssetBeingUsed) || formData?.assetDetails?.howAssetBeingUsed || ""
    );


    const [plinthArea, setplinthArea] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].plinthArea) || formData?.assetDetails?.plinthArea || ""
    );
    const [floorNo, setfloorNo] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].floorNo) || formData?.assetDetails?.floorNo || ""
    );
    const [dimensions, setdimensions] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].dimensions) || formData?.assetDetails?.dimensions || ""
    );
    const [floorArea, setfloorArea] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].floorArea) || formData?.assetDetails?.floorArea || ""
    );



    const [roadType, setroadType] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].roadType) || formData?.assetDetails?.roadType || ""
    );
    const [totalLength, settotalLength] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].totalLength) || formData?.assetDetails?.totalLength || ""
    );
    const [roadWidth, setroadWidth] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].roadWidth) || formData?.assetDetails?.roadWidth || ""
    );
    const [surfaceType, setsurfaceType] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].surfaceType) || formData?.assetDetails?.surfaceType || ""
    );
    const [protectionLength, setprotectionLength] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].protectionLength) || formData?.assetDetails?.protectionLength || ""
    );
    const [drainageLength, setdrainageLength] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].drainageLength) || formData?.assetDetails?.drainageLength || ""
    );
    const [numOfFootpath, setnumOfFootpath] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].numOfFootpath) || formData?.assetDetails?.numOfFootpath || ""
    );
    const [numOfPedastrianCross, setnumOfPedastrianCross] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].numOfPedastrianCross) || formData?.assetDetails?.numOfPedastrianCross || ""
    );
    const [numOfBusStop, setnumOfBusStop] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].numOfBusStop) || formData?.assetDetails?.numOfBusStop || ""
    );
    const [numOfMetroStation, setnumOfMetroStation] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].numOfMetroStation) || formData?.assetDetails?.numOfMetroStation || ""
    );
    const [lengthOfCycletrack, setlengthOfCycletrack] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].lengthOfCycletrack) || formData?.assetDetails?.lengthOfCycletrack || ""
    );
    const [lastMaintainence, setlastMaintainence] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].lastMaintainence) || formData?.assetDetails?.lastMaintainence || ""
    );
    const [nextMaintainence, setnextMaintainence] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].nextMaintainence) || formData?.assetDetails?.nextMaintainence || ""
    );





    const [registrationNumber, setregistrationNumber] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].registrationNumber) || formData?.assetDetails?.registrationNumber || ""
    );
    const [engineNumber, setengineNumber] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].engineNumber) || formData?.assetDetails?.engineNumber || ""
    );
    const [chasisNumber, setchasisNumber] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].chasisNumber) || formData?.assetDetails?.chasisNumber || ""
    );
    const [parkingLocation, setparkingLocation] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].parkingLocation) || formData?.assetDetails?.parkingLocation || ""
    );
    const [acquisitionDate, setacquisitionDate] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].acquisitionDate) || formData?.assetDetails?.acquisitionDate || ""
    );
    const [acquiredFrom, setacquiredFrom] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].acquiredFrom) || formData?.assetDetails?.acquiredFrom || ""
    );
    const [improvementCost, setimprovementCost] = useState(
      (formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].improvementCost) || formData?.assetDetails?.improvementCost || ""
    );


   const [brand, setbrand] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].brand) || formData?.assetDetails?.brand || "");
   const [invoiceDate, setinvoiceDate] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].invoiceDate) || formData?.assetDetails?.invoiceDate || "");
   const [manufacturer, setmanufacturer] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].manufacturer) || formData?.assetDetails?.manufacturer || "")
   const [purchaseOrderNumber, setpurchaseOrderNumber] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].purchaseOrderNumber) || formData?.assetDetails?.purchaseOrderNumber || "")
   const [purchaseDate, setpurchaseDate] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].purchaseDate) || formData?.assetDetails?.purchaseDate || "")
   const [purchaseCost, setpurchaseCost] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].purchaseCost) || formData?.assetDetails?.purchaseCost || "")
   const [currentLocation, setcurrentLocation] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].currentLocation) || formData?.assetDetails?.currentLocation || "")
   const [assignedUser, setassignedUser] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].assignedUser) || formData?.assetDetails?.assignedUser || "")
   const [department, setdepartment] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].department) || formData?.assetDetails?.department || "")
   const [assetAge, setassetAge] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].assetAge) || formData?.assetDetails?.assetAge || "")
   const [warranty, setwarranty] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].warranty) || formData?.assetDetails?.warranty || "")
   const [operatingSystem, setoperatingSystem] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].operatingSystem) || formData?.assetDetails?.operatingSystem || "")
   const [ram, setram] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].ram) || formData?.assetDetails?.ram || "")
   const [cpu, setcpu] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].cpu) || formData?.assetDetails?.cpu || "")
   const [storage, setstorage] = useState((formData.assetDetails && formData.assetDetails[index] && formData.assetDetails[index].storage) || formData?.assetDetails?.storage || "")

    
   
   const fetchCurrentLocation = () => {
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords;
          setcurrentLocation(`${latitude}, ${longitude}`);
        },
        (error) => {
          console.error("Error getting location:", error);
          alert("Unable to retrieve your location. Please check your browser settings.");
        }
      );
    } else {
      alert("Geolocation is not supported by your browser.");
    }
  };
    



    const { control } = useForm();

    function setBrand(e) {
      setbrand(e.target.value);
    }

    function setInvoiceDate(e) {
      setinvoiceDate(e.target.value);
    }

    function setImprovementCost(e) {
      setimprovementCost(e.target.value);
    }

    function setAcquiredFrom(e) {
      setacquiredFrom(e.target.value);
    }

    function setAcquisitionDate(e) {
      setacquisitionDate(e.target.value);
    }
    function setParkingLocation(e) {
      setparkingLocation(e.target.value);
    }
    function setChasisNumber(e) {
      setchasisNumber(e.target.value);
    }
    function setEngineNumber(e) {
      setengineNumber(e.target.value);
    }
    function setRegistrationNumber(e) {
      setregistrationNumber(e.target.value);
    }


    function setLandType(e) {
      setlandType(e.target.value);
    }

    function setarea(e) {
      setArea(e.target.value);
    }

    function setbookValue(e) {
      setBookValue(e.target.value);
    }
    function setdateofDeedExecution(e) {
      setDateofDeedExecution(e.target.value);
    }
    function setdateofPossesion(e) {
      setDateofPossesion(e.target.value);
    }
    function setfromWhomDeedTaken(e) {
      setFromWhomDeedTaken(e.target.value);
    }
    function setgovernmentorderNumber(e) {
      setGovernmentorderNumber(e.target.value);
    }
    function setcollectororderNumber(e) {
      setCollectororderNumber(e.target.value);
    }
    function setcouncilResolutionNumber(e) {
      setCouncilResolutionNumber(e.target.value);
    }
    function setawardNumber(e) {
      setAwardNumber(e.target.value);
    }
    function setoandMCOI(e) {
      setOandMCOI(e.target.value);
    }
    function setoandMTaskDetail(e) {
      setOandMTaskDetail(e.target.value);
    }
    function settotalcost(e) {
      setTotalcost(e.target.value);
    }
    function setdepriciationRate(e) {
      setDepriciationRate(e.target.value);
    }
    function setcostafterdepriciation(e) {
      setCostafterdepriciation(e.target.value);
    }
    function setcurrentassetvalue(e) {
      setCurrentassetvalue(e.target.value);
    }
    function setrevenuegeneratedbyasset(e) {
      setRevenuegeneratedbyasset(e.target.value);
    }
    function setoSRLand(e) {
      setOSRLand(e.target.value);
    }
    function setIsitfenced(e) {
      setisitfenced(e.target.value);
    }
    function setanyBuiltup(e) {
      setAnyBuiltup(e.target.value);
    }
    function setHowassetbeingused(e) {
      sethowassetbeingused(e.target.value);
    }



    function setPlinthArea(e) {
      setplinthArea(e.target.value);
    }
    function setFloorNo(e) {
      setfloorNo(e.target.value);
    }
    function setDimensions(e) {
      setdimensions(e.target.value);
    }
    function setFloorArea(e) {
      setfloorArea(e.target.value);
    }




    function setRoadType(e) {
      setroadType(e.target.value);
    }
    function setTotalLength(e) {
      settotalLength(e.target.value);
    }
    function setRoadWidth(e) {
      setroadWidth(e.target.value);
    }
    function setSurfaceType(e) {
      setsurfaceType(e.target.value);
    }
    function setProtectionLength(e) {
      setprotectionLength(e.target.value);
    }
    function setDrainageLength(e) {
      setdrainageLength(e.target.value);
    }
    function setNumOfFootpath(e) {
      setnumOfFootpath(e.target.value);
    }
    function setNumOfPedastrianCross(e) {
      setnumOfPedastrianCross(e.target.value);
    }
    function setNumOfBusStop(e) {
      setnumOfBusStop(e.target.value);
    }
    function setNumOfBusStop(e) {
      setnumOfBusStop(e.target.value);
    }
    function setNumOfMetroStation(e) {
      setnumOfMetroStation(e.target.value);
    }
    function setLengthOfCycletrack(e) {
      setlengthOfCycletrack(e.target.value);
    }
    function setLastMaintainence(e) {
      setlastMaintainence(e.target.value);
    }
    

    function setNextMaintainence(e) {
      setnextMaintainence(e.target.value);
    }


    function setbuildingsNo(e) {
      setBuildingsno(e.target.value);
    }

    function setPlotArea(e) {
      setplotarea(e.target.value);
    }


    const { data: warrantyperiod } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "Warranty" }],
      {
        select: (data) => {
            const formattedData = data?.["ASSET"]?.["Warranty"]
            return formattedData;
        },
    }); 
    let warrantyTime = [];

    warrantyperiod && warrantyperiod.map((warrantytime) => {
      warrantyTime.push({i18nKey: `${warrantytime.name}`, code: `${warrantytime.code}`, value: `${warrantytime.name}`})
    }) 



    // used arrow functions for the IT assets, need tp change for other assets to 

    const selectmanufacturer = (e) => setmanufacturer(e.target.value);
    const selectpurchasecost = (e) => setpurchaseCost(e.target.value);
    const selectpurchasedate = (e) => {
      const selectedDate = e.target.value;
      setpurchaseDate(selectedDate);
      calculateAssetAge(selectedDate);
      // setpurchaseDate(e.target.value);

    }
    const selectpurchaseorder = (e) => setpurchaseOrderNumber(e.target.value);
    const selectcurrentlocation = (e) => setcurrentLocation(e.target.value);
    const selectassigneduser = (e) => setassignedUser(e.target.value);
    const selectdepartment = (e) => setdepartment(e.target.value);
    const selectassetage = (e) => setassetAge(e.target.value);
    const selectwarranty = (e) => setwarranty(e.target.value);
    const selectcpu = (e) => setcpu(e.target.value);
    const selectoperatingsystem = (e) => setoperatingSystem(e.target.value);
    const selectram = (e) => setram(e.target.value);
    const selectstorage = (e) => setstorage(e.target.value);



    const calculateAssetAge = (purchaseDate) => {
      const today = new Date();
      const purchaseDatetime = new Date(purchaseDate);
      const diffInYears = today.getFullYear() - purchaseDatetime.getFullYear();
      const diffInMonths = today.getMonth() - purchaseDatetime.getMonth();

      let age;
      if (diffInYears > 0) {
        age = diffInYears + ' ' + t("YEAR");
      } else if (diffInMonths >=0 ) {
        age = diffInMonths+ ' ' + t("MONTH");
      } else {
        const diffInDays = today.getDate() - purchaseDatetime.getDate();
        age = diffInDays + ' ' + t("DAY");
      }
      setassetAge(age)
    };

    useEffect(() => {
      if (purchaseDate) {
        calculateAssetAge(purchaseDate);
      }
    }, [purchaseDate]);


    const { data: brandsName } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "Brands" }],
      {
        select: (data) => {
            const formattedData = data?.["ASSET"]?.["Brands"]
            return formattedData;
        },
    }); 
    let brandsname = [];

    brandsName && brandsName.map((brands) => {
      if (brands.AssetSubCategory===formData?.asset?.assetparentsubCategory?.code) {
        brandsname.push({i18nKey: `${brands.name}`, code: `${brands.code}`, value: `${brands.name}`})
      }
     
    }) 
 







    const commondropvalues = [
      {
          code: "YES",
          i18nKey: "YES"
      },
      {
          code: "NO",
          i18nKey: "NO"
      }
    ]

    const surfaceselecttype = [
      {
          code: "RCC",
          i18nKey: "RCC"
      },
      {
          code: "PCC",
          i18nKey: "PCC"
      }
    ]

    const roadselecttype = [
      {
          code: "National Highways",
          i18nKey: "National Highways"
      },
      {
          code: "State Highways",
          i18nKey: "State Highways"
      },
      {
          code: "Rural Roads",
          i18nKey: "Rural Roads"
      },
      {
          code: "District Roads",
          i18nKey: "District Roads"
      },
      {
          code: "Border Roads",
          i18nKey: "Border Roads"
      }

    ]

    





    const goNext = () => {
      let owner = formData.assetDetails && formData.assetDetails[index];
      let ownerStep;
      if (formData?.asset?.assettype?.code === "LAND") {
        ownerStep = { ...owner, landType, area,bookValue, dateOfDeedExecution,dateOfPossession, fromWhomDeedTaken, governmentOrderNumber, collectorOrderNumber, councilResolutionNumber, awardNumber, osrLand,oAndMCOI, oAndMTaskDetail, totalCost, depreciationRate, costAfterDepreciation,currentAssetValue, revenueGeneratedByAsset, isitFenced,anyBuiltup, howAssetBeingUsed};
      } else if (formData?.asset?.assettype?.code === "BUILDING") {
        ownerStep = { ...owner, buildingSno, plotArea, plinthArea,floorArea, floorNo, dimensions,totalCost, depreciationRate, costAfterDepreciation,currentAssetValue, revenueGeneratedByAsset, bookValue };
      } else if (formData?.asset?.assettype?.code === 'SERVICE') {
        ownerStep= {...owner,roadType,surfaceType,protectionLength,drainageLength,numOfBusStop,numOfFootpath,numOfMetroStation,numOfPedastrianCross,lengthOfCycletrack,lastMaintainence,nextMaintainence,bookValue,totalCost, depreciationRate, costAfterDepreciation}
      } else if (formData?.asset?.assettype?.code === "VEHICLE"){
        ownerStep= {...owner, registrationNumber,parkingLocation,engineNumber,chasisNumber,improvementCost,acquiredFrom,acquisitionDate,bookValue,totalCost,currentAssetValue}
      }
      else if (formData?.asset?.assettype?.code === "IT"){
        ownerStep= {...owner,brand,invoiceDate,manufacturer,purchaseCost,purchaseDate,purchaseOrderNumber,currentLocation,assignedUser,department,assetAge,warranty }
      }
      else if (formData?.asset?.assettype?.code === "IT" && formData?.asset?.assetsubtype?.code ==="COMPUTERS_AND_LAPTOPS"){
        ownerStep= {...owner,brand, operatingSystem,ram,storage,cpu,invoiceDate,manufacturer,purchaseCost,purchaseDate,purchaseOrderNumber,currentLocation,assignedUser,department,assetAge,warranty }
      }

      onSelect(config.key, ownerStep, false, index);

   
    };

    const onSkip = () => onSelect();


   






    return (
      <React.Fragment>
        {
          window.location.href.includes("/employee") ?
            <Timeline currentStep={2} />
            : null
        }

        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={ ( formData?.asset?.assettype?.code === "LAND" ? (!landType || !area) : false) || (formData?.asset?.assettype?.code === "BUILDING" ? (!buildingSno || !plotArea) : false) || (formData?.asset?.assettype?.code === "IT"  ? (!currentLocation || !brand || !purchaseDate) : false ) }
        >
          <div>

           


            {formData?.asset?.assettype?.code === "LAND" && (
            <React.Fragment>
            <CardLabel>{`${t("AST_LAND_TYPE")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="landType"
              value={landType}
              onChange={setLandType}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("AST_LAND_AREA")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="area"
              value={area}
              onChange={setarea}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("AST_ACQUISTION_COST_BOOK_VALUE")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="bookValue"
              value={bookValue}
              onChange={setbookValue}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("AST_DATE_DEED_EXECUTION")}`}</CardLabel>
            <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="dateOfDeedExecution"
              value={dateOfDeedExecution}
              onChange={setdateofDeedExecution}
              style={{ width: "50%" }}
              max={new Date().toISOString().split('T')[0]}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}

            />
             <CardLabel>{`${t("AST_DATE_POSSESION")}`}</CardLabel>
            <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="dateOfPossession"
              value={dateOfPossession}
              onChange={setdateofPossesion}
              style={{ width: "50%" }}
              max={new Date().toISOString().split('T')[0]}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}

            />
             <CardLabel>{`${t("AST_FROM_DEED_TAKEN")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="fromWhomDeedTaken"
              value={fromWhomDeedTaken}
              onChange={setfromWhomDeedTaken}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_GOVT_ORDER_NUM")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="governmentOrderNumber"
              value={governmentOrderNumber}
              onChange={setgovernmentorderNumber}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_COLLECT_ORDER_NUM")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="collectorOrderNumber"
              value={collectorOrderNumber}
              onChange={setcollectororderNumber}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_COUNCIL_RES_NUM")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="councilResolutionNumber"
              value={councilResolutionNumber}
              onChange={setcouncilResolutionNumber}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_AWARD_NUMBER")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="awardNumber"
              value={awardNumber}
              onChange={setawardNumber}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_COI")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="oAndMCOI"
              value={oAndMCOI}
              onChange={setoandMCOI}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_OM_TASK")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="oAndMTaskDetail"
              value={oAndMTaskDetail}
              onChange={setoandMTaskDetail}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("AST_TOTAL_COST")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="totalCost"
              value={totalCost}
              onChange={settotalcost}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_DEPRICIATION_RATE")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="depreciationRate"
              value={depreciationRate}
              onChange={setdepriciationRate}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_COST_AFTER_DEPRICIAT")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="costAfterDepreciation"
              value={costAfterDepreciation}
              onChange={setcostafterdepriciation}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_CURRENT_VALUE")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="currentAssetValue"
              value={currentAssetValue}
              onChange={setcurrentassetvalue}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_REVENUE_GENERATED")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="revenueGeneratedByAsset"
              value={revenueGeneratedByAsset}
              onChange={setrevenuegeneratedbyasset}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_OSR_LAND")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="osrLand"
              value={osrLand}
              onChange={setoSRLand}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_FENCED")}`}</CardLabel>
             <Controller
              control={control}
              name={"isitFenced"}
              defaultValue={isitFenced}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown

                  className="form-field"
                  selected={isitFenced}
                  select={setisitfenced}
                  option={commondropvalues}
                  optionKey="i18nKey"
                  t={t}
                />

              )}

            />
             <CardLabel>{`${t("AST_ANY_BUILTUP")}`}</CardLabel>
             <Controller
              control={control}
              name={"anyBuiltup"}
              defaultValue={anyBuiltup}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown

                  className="form-field"
                  selected={anyBuiltup}
                  select={setAnyBuiltup}
                  option={commondropvalues}
                  optionKey="i18nKey"
                  t={t}
                />

              )}

            />
             <CardLabel>{`${t("AST_HOW_ASSET_USE")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="howAssetBeingUsed"
              value={howAssetBeingUsed}
              onChange={setHowassetbeingused}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

            </React.Fragment>
            )}

            {formData?.asset?.assettype?.code ==="BUILDING" && (

              <React.Fragment>
                <CardLabel>{`${t("AST_BUILDING_NO")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="buildingSno"
                value={buildingSno}
                onChange={setbuildingsNo}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_ACQUISTION_COST_BOOK_VALUE")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="bookValue"
                value={bookValue}
                onChange={setbookValue}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
  
              <CardLabel>{`${t("AST_PLOT_AREA")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="plotArea"
                value={plotArea}
                onChange={setPlotArea}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />

            <CardLabel>{`${t("AST_PLINTH_AREA")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="plinthArea"
              value={plinthArea}
              onChange={setPlinthArea}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("AST_FLOORS_NO")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="floorNo"
              value={floorNo}
              onChange={setFloorNo}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("AST_DIMENSIONS")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="dimensions"
              value={dimensions}
              onChange={setDimensions}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("AST_AREA_FLOOR")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="floorArea"
              value={floorArea}
              onChange={setFloorArea}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("AST_TOTAL_COST")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="totalCost"
              value={totalCost}
              onChange={settotalcost}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_DEPRICIATION_RATE")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="depreciationRate"
              value={depreciationRate}
              onChange={setdepriciationRate}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_COST_AFTER_DEPRICIAT")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="costAfterDepreciation"
              value={costAfterDepreciation}
              onChange={setcostafterdepriciation}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_CURRENT_VALUE")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="currentAssetValue"
              value={currentAssetValue}
              onChange={setcurrentassetvalue}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
             <CardLabel>{`${t("AST_REVENUE_GENERATED")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="revenueGeneratedByAsset"
              value={revenueGeneratedByAsset}
              onChange={setrevenuegeneratedbyasset}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("AST_HOW_ASSET_USE")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="howAssetBeingUsed"
              value={howAssetBeingUsed}
              onChange={setHowassetbeingused}
              style={{ width: "50%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
              </React.Fragment>
              
            )}

            {formData?.asset?.assettype?.code === "SERVICE" && (
              <React.Fragment>

              <CardLabel>{`${t("AST_ROAD_TYPE")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="roadType"
                value={roadType}
                onChange={setRoadType}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_TOTAL_LENGTH")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="totalLength"
                value={totalLength}
                onChange={setTotalLength}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_ROAD_WIDTH")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="totalCost"
                value={roadWidth}
                onChange={setRoadWidth}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_SURFACE_TYPE")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="totalCost"
                value={surfaceType}
                onChange={setSurfaceType}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_PROTECTION_LENGTH")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="protectionLength"
                value={protectionLength}
                onChange={setProtectionLength}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_DRAINAGE_CHANNEL_LENGTH")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="drainageLength"
                value={drainageLength}
                onChange={setDrainageLength}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_FOOTPATH_NUMBER")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="numOfFootpath"
                value={numOfFootpath}
                onChange={setNumOfFootpath}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_PEDASTRIAN_CROSSING_NUMBER")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="numOfPedastrianCross"
                value={numOfPedastrianCross}
                onChange={setNumOfPedastrianCross}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_BUSSTOP_NUMBER")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="numOfBusStop"
                value={numOfBusStop}
                onChange={setNumOfBusStop}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_METROSTATION_NUMBER")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="numOfMetroStation"
                value={numOfMetroStation}
                onChange={setNumOfMetroStation}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_CYCLETRACK_LENGTH")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="lengthOfCycletrack"
                value={lengthOfCycletrack}
                onChange={setLengthOfCycletrack}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_LAST_MAINTAINENCE")}`}</CardLabel>
              <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="lastMaintainence"
                value={lastMaintainence}
                onChange={setLastMaintainence}
              style={{ width: "50%" }}
              max={new Date().toISOString().split('T')[0]}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}

            />
              

              <CardLabel>{`${t("AST_NEXT_MAINTAINENCE")}`}</CardLabel>
              <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="nextMaintainence"
              value={nextMaintainence}
              onChange={setNextMaintainence}
              style={{ width: "50%" }}
              max={new Date(new Date().setFullYear(new Date().getFullYear() + 2)).toISOString().split("T")[0]}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}

            />



              <CardLabel>{`${t("AST_ACQUISTION_COST_BOOK_VALUE")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="bookValue"
                value={bookValue}
                onChange={setbookValue}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_TOTAL_COST")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="totalCost"
                value={totalCost}
                onChange={settotalcost}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
             <CardLabel>{`${t("AST_DEPRICIATION_RATE")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="depreciationRate"
                value={depreciationRate}
                onChange={setdepriciationRate}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
             <CardLabel>{`${t("AST_COST_AFTER_DEPRICIAT")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="costAfterDepreciation"
                value={costAfterDepreciation}
                onChange={setcostafterdepriciation}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />



              </React.Fragment>
            )}

            {formData?.asset?.assettype?.code === "VEHICLE" && (
              <React.Fragment>
                <CardLabel>{`${t("AST_REGISTRATION_NUMBER")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="registrationNumber"
                value={registrationNumber}
                onChange={setRegistrationNumber}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_ENGINE_NUMBER")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="engineNumber"
                value={engineNumber}
                onChange={setEngineNumber}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_CHASIS_NUMBER")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="chasisNumber"
                value={chasisNumber}
                onChange={setChasisNumber}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_PARKING_LOCATION")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="parkingLocation"
                value={parkingLocation}
                onChange={setParkingLocation}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              
              <CardLabel>{`${t("AST_ACQUISTION_DATE")}`}</CardLabel>
              <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="acquisitionDate"
              value={acquisitionDate}
              onChange={setAcquisitionDate}
              style={{ width: "50%" }}
              max={new Date().toISOString().split('T')[0]}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}
            />
              <CardLabel>{`${t("AST_ACQUIRED_SOURCE")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="acquiredFrom"
                value={acquiredFrom}
                onChange={setAcquiredFrom}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_IMPROVEMENT_COST")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="improvementCost"
                value={improvementCost}
                onChange={setImprovementCost}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />

              <CardLabel>{`${t("AST_ACQUISTION_COST_BOOK_VALUE")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="bookValue"
                value={bookValue}
                onChange={setbookValue}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_CURRENT_VALUE")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="currentAssetValue"
                value={currentAssetValue}
                onChange={setcurrentassetvalue}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              <CardLabel>{`${t("AST_TOTAL_COST")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="totalCost"
                value={totalCost}
                onChange={settotalcost}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z-.`' ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
              </React.Fragment>

              

            )}

            {formData?.asset?.assettype?.code === "IT" && (
              <React.Fragment>
              <CardLabel>{`${t("AST_BRAND") + " *"}`}</CardLabel>

            {/* Do NOT delete this comment as we can add the brands in MDMS so that we can convert this from text to dropdown */}

              {/* <Controller
                control={control}
                name={"brand"}
                defaultValue={brand}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown
                    className="form-field"
                    selected={brand}
                    select={setbrand}
                    option={brandsname}
                    optionKey="i18nKey"
                    placeholder={"Select"}
                    t={t}
                  />
                )}
              /> */}
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="brand"
                value={brand}
                onChange={setBrand}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: false,
                  pattern: "^[a-zA-Z ]+$",
                  type: "tel",
                  title: t("MATCH_THE_FORMAT"),
                })}
              />
              <CardLabel>{`${t("AST_INVOICE_DATE") + " *"}`}</CardLabel>
               <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="invoiceDate"
              value={invoiceDate}
              onChange={setInvoiceDate}
              style={{ width: "50%" }}
              max={new Date().toISOString().split('T')[0]}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}
            />

              <CardLabel>{`${t("AST_MANUFACTURER")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="manufacturer"
                value={manufacturer}
                onChange={selectmanufacturer}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: false,
                  pattern: "^[a-zA-Z ]+$",
                  type: "tel",
                  title: t("MATCH_THE_FORMAT"),
                })}
              />
              <div>{t("AST_PURCHASE_COST")}
              <div className="tooltip" style={{width: "12px", height: "5px",marginLeft:"10px", display: "inline-flex",alignItems: "center"}}>
              <InfoBannerIcon fill="#FF0000" style />
                    <span className="tooltiptext" style={{
                        whiteSpace: "pre-wrap",
                        fontSize: "small",
                        wordWrap:"break-word",
                        width:"300px",
                        marginLeft:"15px",
                        marginBottom:"-10px"
                        //overflow:"auto"
                    }}>
                    {`${t(`AST_PURCHASE`)}`}
                    </span>
                </div></div>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="purchaseCost"
                value={purchaseCost}
                onChange={selectpurchasecost}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[0-9]+$",
                  type: "tel",
                  title: "",
                })}
                placeholder={"In Rupees"}
              />
              
              <div>{t("AST_PURCHASE_ORDER") + " *"}
              <div className="tooltip" style={{width: "12px", height: "5px",marginLeft:"10px", display: "inline-flex",alignItems: "center"}}>
              <InfoBannerIcon fill="#FF0000" style />
                    <span className="tooltiptext" style={{
                        whiteSpace: "pre-wrap",
                        fontSize: "small",
                        wordWrap:"break-word",
                        width:"300px",
                        marginLeft:"15px",
                        marginBottom:"-10px"
                        //overflow:"auto"
                    }}>
                    {`${t(`AST_PURCHASE_NUMBER`)}`}
                    </span>
                </div></div>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="purchaseOrderNumber"
                value={purchaseOrderNumber}
                onChange={selectpurchaseorder}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: true,
                  pattern: "^[a-zA-Z0-9/-]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                })}
              />
             
              <div>{t("AST_PURCHASE_DATE") + " *"}
              <div className="tooltip" style={{width: "12px", height: "5px",marginLeft:"10px", display: "inline-flex",alignItems: "center"}}>
              <InfoBannerIcon fill="#FF0000" style />
                    <span className="tooltiptext" style={{
                        whiteSpace: "pre-wrap",
                        fontSize: "small",
                        wordWrap:"break-word",
                        width:"300px",
                        marginLeft:"15px",
                        marginBottom:"-10px"
                        //overflow:"auto"
                    }}>
                    {`${t(`AST_DATE_PURCHASE`)}`}
                    </span>
                </div></div>
              <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="purchaseDate"
              value={purchaseDate}
              onChange={selectpurchasedate}
              style={{ width: "50%" }}
              max={new Date().toISOString().split('T')[0]}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}
            />

              <CardLabel>{`${t("AST_CURRENT_LOCATION")}`}</CardLabel>
              <div style={{ display: 'flex', alignItems: 'stretch', width: '50%' }}>
                <TextInput
                  t={t}
                  type={"text"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="currentLocation"
                  value={currentLocation}
                  onChange={selectcurrentlocation}
                  style={{ flex: 1, marginRight: '10px' }}
                  ValidationRequired={false}
                  {...(validation = {
                    isRequired: true,
                    pattern: "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$",
                    type: "text",
                    title: t("VALID_LAT_LONG"),
                  })}
                />
                <button 
                  onClick={fetchCurrentLocation}
                  style={{
                    padding: '8px 16px',
                    backgroundColor: '#800000',
                    color: 'white',
                    border: 'none',
                    borderRadius: '0 4px 4px 0',
                    cursor: 'pointer',
                    whiteSpace: 'nowrap',
                    minWidth: 'fit-content',
                  }}
                >
                  {t("AST_FETCH_LOCATION")}
                </button>
              </div>

              <CardLabel>{`${t("AST_ASSIGNED_USER")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="assignedUser"
                value={assignedUser}
                onChange={selectassigneduser}
                style={{ width: "50%" }}
                ValidationRequired={false}
                {...(validation = {
                  isRequired: false,
                  pattern: "^[a-zA-Z ]+$",
                  type: "tel",
                  title: t("MATCH_THE_FORMAT"),
                })}
              />

              <CardLabel>{`${t("AST_ASSET_AGE")}`}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="assetAge"
                value={assetAge}
                style={{ width: "50%" }}
              />
              <CardLabel>{`${t("AST_WARRANTY")}`}</CardLabel>
              <Controller
                control={control}
                name={"warranty"}
                defaultValue={warranty}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown

                    className="form-field"
                    selected={warranty}
                    select={setwarranty}
                    option={warrantyTime}
                    optionKey="i18nKey"
                    placeholder={"Select"}
                    t={t}
                  />

                )}

              />

              </React.Fragment>
            )}

           

           


            

            

           

          </div>
        </FormStep>
      </React.Fragment>
    );
  };

export default NewAsset;