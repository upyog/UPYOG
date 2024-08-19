import React, { useRef, useEffect, useState } from "react";
import SubMenu from "./SubMenu";
import { Loader, SearchIcon } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import NavItem from "./NavItem";
import _, { findIndex } from "lodash";
import { Link, useLocation } from "react-router-dom";
import {
  HomeIcon,
  EditPencilIcon,
  LogoutIcon,
  AddressBookIcon,
  PropertyHouse,
  CaseIcon,
  CollectionIcon,
  PTIcon,
  OBPSIcon,
  PGRIcon,
  FSMIcon,
  WSICon,
  MCollectIcon,
  Phone,
  BirthIcon,
  DeathIcon,
  FirenocIcon,
  LoginIcon
} from "@upyog/digit-ui-react-components";
import { useHistory } from "react-router-dom";
import LogoutDialog from "../../Dialog/LogoutDialog";
import ChangeCity from "../../ChangeCity";

const defaultImage =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAO4AAADUCAMAAACs0e/bAAAAM1BMVEXK0eL" +
  "/" +
  "/" +
  "/" +
  "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";


const Profile = ({ info, stateName, t }) => (
  <div className="profile-section">
    <div className="imageloader imageloader-loaded">
      <img className="img-responsive img-circle img-Profile" src={defaultImage} />
    </div>
    <div id="profile-name" className="label-container name-Profile">
      <div className="label-text"> {info?.name} </div>
    </div>
    <div id="profile-location" className="label-container loc-Profile">
      <div className="label-text"> {info?.mobileNumber} </div>
    </div>
    {info?.emailId && (
      <div id="profile-emailid" className="label-container loc-Profile">
        <div className="label-text"> {info.emailId} </div>
      </div>
    )}
    <div className="profile-divider"></div>
    {window.location.href.includes("/employee") &&
      !window.location.href.includes("/employee/user/login") &&
      !window.location.href.includes("employee/user/language-selection") && <ChangeCity t={t} mobileView={true} />}
  </div>
);
const IconsObject = {
  CommonPTIcon: <PTIcon className="icon" />,
  OBPSIcon: <OBPSIcon className="icon" />,
  propertyIcon: <PropertyHouse className="icon" />,
  TLIcon: <CaseIcon className="icon" />,
  PGRIcon: <PGRIcon className="icon" />,
  FSMIcon: <FSMIcon className="icon" />,
  WSIcon: <WSICon className="icon" />,
  MCollectIcon: <MCollectIcon className="icon" />,
  BillsIcon: <CollectionIcon className="icon" />,
  BirthIcon: <BirthIcon className="icon" />,
  DeathIcon: <DeathIcon className="icon" />,
  FirenocIcon: <FirenocIcon className="icon" />,
  HomeIcon: <HomeIcon className="icon" />,
  EditPencilIcon: <EditPencilIcon className="icon" />,
  LogoutIcon: <LogoutIcon className="icon" />,
  Phone: <Phone className="icon" />,
  LoginIcon: <LoginIcon className="icon" />,
};

const EmployeeSideBar = () => {
  const sidebarRef = useRef(null);
  const { isLoading, data } = Digit.Hooks.useAccessControl();
  const [search, setSearch] = useState("");
  const { t } = useTranslation();
  const { data: storeData, isFetched } = Digit.Hooks.useStore.getInitData();
  const user = Digit.UserService.getUser();
  const { stateInfo } = storeData || {};
  const [isSidebarOpen, toggleSidebar] = useState(false);
  const [isSideBarScroll, setSideBarScrollTop] = useState(false);
  const history = useHistory();
  const [showDialog, setShowDialog] = useState(false);
  const location = useLocation();
  const { pathname } = location;
  const handleLogout = () => {
    toggleSidebar(false);
    setShowDialog(true);
  };
  const handleOnSubmit = () => {
    Digit.UserService.logout();
    setShowDialog(false);
  }
  const handleOnCancel = () => {
    setShowDialog(false);
  }
  const userProfile = () => {
    history.push("/digit-ui/employee/user/profile");
  };
  useEffect(() => {
    if (isLoading) {
      return <Loader />;
    }
    sidebarRef.current.style.cursor = "pointer";
    collapseNav();
  }, [isLoading]);

  const expandNav = () => {
    sidebarRef.current.style.width = "260px";
    sidebarRef.current.style.overflow = "auto";

    sidebarRef.current.querySelectorAll(".dropdown-link").forEach((element) => {
      element.style.display = "flex";
    });
    sidebarRef.current.querySelectorAll(".employee-select-wrap ").forEach((element) => {
      element.style.display = "flex";
    });
    sidebarRef.current.querySelectorAll(".label-text").forEach((element) => {
      element.style.display = "flex";
    });
  };
  const collapseNav = () => {
    sidebarRef.current.style.width = "55px";
    sidebarRef.current.style.overflow = "hidden";

    sidebarRef.current.querySelectorAll(".dropdown-link").forEach((element) => {
      element.style.display = "none";
    });
    sidebarRef.current.querySelectorAll(".label-text").forEach((element) => {
      element.style.display = "none";
    });
    sidebarRef.current.querySelectorAll(".employee-select-wrap ").forEach((element) => {
      element.style.display = "none";
    });
    sidebarRef.current.querySelectorAll(".actions").forEach((element) => {
      element.style.padding = "0";
    });
  };

  const configEmployeeSideBar = {};

  //creating the object structure from mdms value for easy iteration
  let configEmployeeSideBar1 = {};
  data?.actions?.filter((e) => e.url === "url")?.forEach((item) => {
    _.set(configEmployeeSideBar1,item.path,{...item}) 
  })

  data?.actions
    .filter((e) => e.url === "url")
    .forEach((item) => {
      let index = item.path.split(".")[0];
      if (search == "" && item.path !== "") {
         index = item.path.split(".")[0];
        if (index === "TradeLicense") index = "Trade License";
        if (!configEmployeeSideBar[index]) {
          configEmployeeSideBar[index] = [item];
        } else {
          configEmployeeSideBar[index].push(item);
        }
      } else if (item.path !== "" && t(`ACTION_TEST_${index?.toUpperCase()?.replace(/[ -]/g, "_")}`)?.toLowerCase().includes(search.toLowerCase())) {
         index = item.path.split(".")[0];
        if (index === "TradeLicense") index = "Trade License";
        if (!configEmployeeSideBar[index]) {
          configEmployeeSideBar[index] = [item];
        } else {
          configEmployeeSideBar[index].push(item);
        }
      }
    });
  let res = [];

  //method is used for restructing of configEmployeeSideBar1 nested object into nested array object
  function restructuringOfConfig (tempconfig){
    const result = [];
    for(const key in tempconfig){
      const value= tempconfig[key];
      if(typeof value === "object" && !(value?.id)){
      const children = restructuringOfConfig(value);
      result.push({label : key,children, icon:children?.[0]?.icon, to:""});
      }
      else{
        result.push({label: key, value, icon:value?.leftIcon, to: key === "Home" ? "/digit-ui/employee" : value?.navigationURL});
      }
    }

    return result
  }
  const splitKeyValue = () => {
    console.log("configEmployeeSideBar===",configEmployeeSideBar)
    const keys = Object.keys(configEmployeeSideBar);
    keys.sort((a, b) => a.orderNumber - b.orderNumber);
    for (let i = 0; i < keys.length; i++) {
      if (configEmployeeSideBar[keys[i]][0].path.indexOf(".") === -1) {
        if (configEmployeeSideBar[keys[i]][0].displayName === "Home") {
          const homeURL = "/digit-ui/employee";
          res.unshift({
            moduleName: keys[i].toUpperCase(),
            icon: configEmployeeSideBar[keys[i]][0],
            navigationURL: homeURL,
            type: "single",
          });
        } else {
          res.push({
            moduleName: configEmployeeSideBar[keys[i]][0]?.displayName.toUpperCase(),
            type: "single",
            icon: configEmployeeSideBar[keys[i]][0],
            navigationURL: configEmployeeSideBar[keys[i]][0].navigationURL,
          });
        }
      } else {
        res.push({
          moduleName: keys[i].toUpperCase(),
          links: configEmployeeSideBar[keys[i]],
          icon: configEmployeeSideBar[keys[i]][0],
          orderNumber: configEmployeeSideBar[keys[i]][0].orderNumber,
        });
      }
    }
    if(res.find(a => a.moduleName === "HOME"))
    {
      //res.splice(0,1);
      const indx = res.findIndex(a => a.moduleName === "HOME");
      const home = res?.filter((ob) => ob?.moduleName === "HOME")
      let res1 = res?.filter((ob) => ob?.moduleName !== "HOME")
      res = res1.sort((a,b) => a.moduleName.localeCompare(b.moduleName));
      home?.[0] && res.unshift(home[0]);
    }
    else
    {
      res.sort((a,b) => a.moduleName.localeCompare(b.moduleName));
    }
    //reverting the newsidebar change for now, in order to solve ndss login issue
    //let newconfig = restructuringOfConfig(configEmployeeSideBar1);
    //below lines are used for shifting home object to first place
    // newconfig.splice(newconfig.findIndex((ob) => ob?.label === ""),1);
    // newconfig.sort((a,b) => a.label.localeCompare(b.label));
    // const fndindex = newconfig?.findIndex((el) => el?.label === "Home");
    // const homeitem = newconfig.splice(fndindex,1);
    // newconfig.unshift(homeitem?.[0]);
    // return (
    //   newconfig.map((item, index) => {
    //       return <NavItem key={`${item?.label}-${index}`} item={item} />;
    //     })
    // );
    return res?.map((item, index) => {
      return <SubMenu item={item} key={index + 1} />;
    });
  };
  const redirectToLoginPage = () => {
    // localStorage.clear();
    // sessionStorage.clear();
    history.push("/digit-ui/employee/login");
  };
  const showProfilePage = () => {
    history.push("/digit-ui/employee/user/profile");
  };
  let menuItems = [
    {
      element: "HOME",
      icon: "HomeIcon",
      link: "/digit-ui/employee",
      text: "Home",
      type: "link"
    },
    {
      text: t("EDIT_PROFILE"),
      element: "PROFILE",
      icon: "EditPencilIcon",
      populators: {
        onClick: showProfilePage,
      },
    },
    {
      text: t("CORE_COMMON_LOGOUT"),
      element: "LOGOUT",
      icon: "LogoutIcon",
      populators: { onClick: handleLogout },
    },
    // {
    //   text: (
    //     <React.Fragment>
    //       {t("CS_COMMON_HELPLINE")}
    //       <div className="telephone" style={{ marginTop: "-10%" }}>
    //         <div className="link">
    //           <a href={`tel:${filteredTenantContact}`}>{filteredTenantContact}</a>
    //         </div>
    //       </div>
    //     </React.Fragment>
    //   ),
    //   element: "Helpline",
    //   icon: "Phone",
    // },
  ];

  let profileItem;

  if (isFetched && user && user.access_token) {
    profileItem = <Profile info={user?.info} stateName={stateInfo?.name} t={t} />;
    // menuItems = menuItems.filter((item) => item?.id !== "login-btn" && item?.id !== "help-line"); 
    
  }

  const MenuItem = ({ item }) => {
    const leftIconArray = item?.icon || item.icon?.type?.name;
    const leftIcon = leftIconArray ? IconsObject[leftIconArray] : IconsObject.BillsIcon;
    let itemComponent;
    if (item.type === "component") {
      itemComponent = item.action;
    } else {
      itemComponent = item.text;
    }
    const Item = () => (
      <span className="menu-item" {...item.populators}>
        {leftIcon}
        <div className="menu-label">{itemComponent}</div>
      </span>
    );
    if (item.type === "external-link") {
      return (
        <a href={item.link}>
          <Item />
        </a>
      );
    }
    if (item.type === "link") {
      return (
        <Link to={item?.link}>
          <Item />
        </Link>
      );
    }

    return <Item />;
  };

  if (isLoading) {
    return <Loader />;
  }
  if (!res) {
    return "";
  }

  const renderSearch = () => {
    return (
      <div className="submenu-container">
        <div className="sidebar-link">
          <div className="actions search-icon-wrapper">
            <SearchIcon className="search-icon" />
            <input
              className="employee-search-input"
              type="text"
              placeholder={t(`ACTION_TEST_SEARCH`)}
              name="search"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </div>
      </div>
    );
  };

  return (
    <div style={{marginTop: "87px"}} className="SideBarStatic" ref={sidebarRef} onMouseOver={expandNav} onMouseLeave={collapseNav}>
      {/* {renderSearch()} */}
      {/* {splitKeyValue()} */}
      {profileItem}
      <div className="drawer-desktop">
        {menuItems?.map((item, index) => (
          <div className={`sidebar-list ${pathname === item?.link || pathname === item?.sidebarURL ? "active" : ""}`} key={index}>
            <MenuItem item={item} />
          </div>
        ))}
      </div>
      {showDialog && (
        <LogoutDialog onSelect={handleOnSubmit} onCancel={handleOnCancel} onDismiss={handleOnCancel}></LogoutDialog>
      )}
    </div>
  );
};

export default EmployeeSideBar;
