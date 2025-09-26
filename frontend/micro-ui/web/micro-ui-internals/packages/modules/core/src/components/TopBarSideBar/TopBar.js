import { Dropdown, Hamburger } from "@demodigit/digit-ui-react-components";
import React from "react";
import { useHistory, useLocation } from "react-router-dom";
import ChangeCity from "../ChangeCity";
import ChangeLanguage from "../ChangeLanguage";

const TextToImg = (props) => (
  <span className="user-img-txt" onClick={props.toggleMenu} title={props.name}>
    {props?.name?.[0]?.toUpperCase()}
  </span>
);

const TopBar = ({
  t,
  stateInfo,
  toggleSidebar,
  handleLogout,
  userDetails,
  CITIZEN,
  mobileView,
  userOptions,
  handleUserDropdownSelection,
  showLanguageChange = true,
  setSideBarScrollTop,
}) => {
  const [profilePic, setProfilePic] = React.useState(null);

  React.useEffect(() => {
    (async () => {
      const tenant = Digit.ULBService.getCurrentTenantId();
      const uuid = userDetails?.info?.uuid;
      if (uuid) {
        const usersResponse = await Digit.UserService.userSearch(tenant, { uuid: [uuid] }, {});
        if (usersResponse?.user?.length) {
          const userDetails = usersResponse.user[0];
          const thumbs = userDetails?.photo?.split(",");
          setProfilePic(thumbs?.at(0));
        }
      }
    })();
  }, [userDetails?.info?.uuid]);

  const CitizenHomePageTenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  let history = useHistory();
  const { pathname } = useLocation();

  const { data: { unreadCount: unreadNotificationCount } = {} } =
    Digit.Hooks.useNotificationCount({
      tenantId: CitizenHomePageTenantId,
      config: {
        enabled: true,
      },
    });

  const loggedin = !!userDetails?.access_token;

  function onNotificationIconClick() {
    history.push("/digit-ui/citizen/engagement/notifications");
  }

  return (
    <React.Fragment>
    <style>
      {
        `
        


  :root {
    --primary-color: #00599f;
    --ribbon-height: 28px;
    --header-height: 74px;
  }
  
  /* Ribbon */
  .gov-ribbon {
    background: #0056ad;
    color: #fff;
    font-weight: 500 !important;
    text-align: center;
    padding: 4px 0;
    letter-spacing: 0.3px;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: var(--ribbon-height);
    z-index: 1000;
    border-bottom-left-radius: 12px;
    border-bottom-right-radius: 12px;
  }
  
  /* Header */
  .header {
    position: fixed;
    top: var(--ribbon-height);
    left: 0;
    width: 100%;
    height: var(--header-height);
    z-index: 1090;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 20px;
    background: #fff;
    
    box-shadow: 0 6px 18px rgba(0, 89, 159, 0.35);
  
    /* watermark */
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300' viewBox='0 0 220 220'%3E%3Cg fill='none' stroke='%2300599f' stroke-opacity='.15'%3E%3Ccircle cx='110' cy='110' r='95' stroke-width='10'/%3E%3Ccircle cx='110' cy='110' r='70' stroke-width='6'/%3E%3Cg stroke-width='6'%3E%3Cline x1='110' y1='18' x2='110' y2='202'/%3E%3Cline x1='18' y1='110' x2='202' y2='110'/%3E%3Cline x1='41' y1='41' x2='179' y2='179'/%3E%3Cline x1='41' y1='179' x2='179' y2='41'/%3E%3C/g%3E%3Ccircle cx='110' cy='110' r='20' stroke-width='8'/%3E%3C/g%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right 50px center;
    background-size: 260px auto;
  }
  
  .header .logo h2,
  .header .logo small {
    color: var(--primary-color);
  }
  
  /* Emblem */
  .emblem-wrap {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: #fff;
    border-radius: 50%;
    padding: 4px;
    margin-top: -10px;
  }
  
  .state-emblem {
    height: 80px;
    width: auto;
    object-fit: contain;
    display: block;
    z-index: 1201;
  }
  
  /* Branding */
  .portal-title {
    font-weight: 800;
    letter-spacing: 1px;
    background: linear-gradient(90deg, #00599f, #00aaff);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    display: flex;
    align-items: baseline;
  }
  
  .portal-title-odia {
    font-weight: 600;
    color: #444;
    margin-left: 10px;
  }
  
  .tagline-wrap {
    margin-top: 2px;
    margin-bottom: 5px;
    display: flex;
    align-items: center;
  }
  
  .portal-subtitle {
    font-size: 0.95rem;
    color: #333;
    font-weight: 600;
    letter-spacing: 0.2px;
    white-space: nowrap;
  }
  
  .separator-tricolor {
    display: inline-block;
    width: 3px;
    height: 20px;
    margin: 0 10px;
    background: linear-gradient(
      to bottom,
      #ff9933 33%,
      #fff 33% 66%,
      #138808 66%
    );
  }
  
  .powered-by {
    font-size: 0.8rem;
    color: #666;
    font-weight: 500;
    white-space: nowrap;
  }
  
  /* Profile Section */
  .profile select.lang-switch {
    border-radius: 6px;
    min-width: 70px;
    border: 1px solid var(--primary-color);
    padding: 4px 8px;
    font-size: 12px !important;
    color: var(--primary-color);
    background-color: #fff;
    appearance: none;
    -webkit-appearance: none;
    -moz-appearance: none;
    background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 140 140' xmlns='http://www.w3.org/2000/svg'%3E%3Cpolygon points='0,0 140,0 70,70' fill='%2300599f'/%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right 8px center;
    background-size: 12px;
    cursor: pointer;
  }
  
  .icon-box {
    position: relative;
    color: var(--primary-color);
    cursor: pointer;
    transition: transform 0.2s, color 0.2s;
  }
  
  .icon-box:hover {
    transform: scale(1.1);
    color: #003d66;
  }
  
  .icon-box .badge {
    position: absolute;
    top: -6px;
    right: -8px;
    background: red;
    color: #fff;
    font-size: 0.65rem;
    padding: 2px 6px;
    border-radius: 50%;
    font-weight: 700;
  }
  
@media only screen and (max-width: 768px) {
  .app-toolbar,
  .citizen-header-logo,
  .appbar-right-logo,
  .appbar-municipal-label {
    display: none !important; }
  .notification-icon-web {
    display: none !important; } }

@media only screen and (min-width: 768px) {
  .appbar-title-label {
    display: none !important; }
  .notification-icon-mobile {
    display: none !important; }
  .notification-icon-web {
    margin-right: 50px; } }
    /* Header main container */
    .header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: var(--header-height);
      padding: 10px 20px;
      background: #fff;
      box-shadow: 0 6px 18px rgba(0, 89, 159, 0.35);
    }
    
    /* Brand Section */
    .brand {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }
    
    .portal-title {
      font-weight: 800;
      font-size: 1.5rem;
      background: linear-gradient(90deg, #00599f, #00aaff);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      display: flex;
      align-items: baseline;
      gap: 8px;
    }
    
    .portal-title-odia {
      font-weight: 600;
      color: #444;
    }
    
    .tagline-wrap {
      font-size: 0.85rem;
      color: #444;
      display: flex;
      align-items: center;
      flex-wrap: nowrap;
      gap: 8px;
    }
    
    /* Profile Right Section */
    .profile {
      display: flex;
      align-items: center;
      gap: 20px;
    }
    
    .dropdowns {
      display: flex;
      align-items: center;
      gap: 10px; /* Tenant + Language inline */
    }
    
    .icon-box {
      position: relative;
      color: var(--primary-color);
      cursor: pointer;
    }
    
    .icon-box .badge {
      position: absolute;
      top: -6px;
      right: -8px;
      background: red;
      color: #fff;
      font-size: 0.65rem;
      padding: 2px 6px;
      border-radius: 50%;
      font-weight: 700;
    }
    
    .helpline {
      font-size: 0.9rem;
      font-weight: 600;
      color: #333;
      margin-left: 10px;
    }
    
    /* Profile Picture */
    .profile-pic {
      height: 40px;
      width: 40px;
      border-radius: 50%;
      object-fit: cover;
    }
    
        `
      }
    </style>
      {/* Government Ribbon */}
      <div className="gov-ribbon">
        Government of Gujrat | State Wide Attention on Grievances by Application of Technology
      </div>

      {/* Header */}
      <div className="header">
  {/* Brand */}
  <div className="brand">
    <h2 className="portal-title">
    SWAGAT <span className="portal-title-odia">| સ્વાગત</span>
    </h2>
    <div className="tagline-wrap">
      <span className="portal-subtitle">
      State Wide Attention on Grievances by Application of Technology
      </span>
      <span className="separator-tricolor"></span>
      <span className="powered-by">
        Govt. of Gujrat
      </span>
    </div>
  </div>

  {/* Right Section */}
  <div className="profile">
    <div className="dropdowns">
      {/* <ChangeCity dropdown={true} t={t} /> */}
      {showLanguageChange && <ChangeLanguage dropdown={true} />}
    </div>

    {/* Notifications */}
    <div className="icon-box" onClick={onNotificationIconClick}>
      <i className="fas fa-bell fa-lg"></i>
      {unreadNotificationCount > 0 && (
        <span className="badge">
          {unreadNotificationCount < 99 ? unreadNotificationCount : "99+"}
        </span>
      )}
    </div>

    {/* User Dropdown */}
    {/* {loggedin && (
      <Dropdown
        option={userOptions}
        optionKey={"name"}
        select={handleUserDropdownSelection}
        showArrow={true}
        freeze={true}
        optionCardStyles={{ overflow: "revert" }}
        customSelector={
          profilePic == null ? (
            <TextToImg
              name={
                userDetails?.info?.name ||
                userDetails?.info?.userInfo?.name ||
                "Employee"
              }
            />
          ) : (
            <img src={profilePic} className="profile-pic" alt="Profile" />
          )
        }
      />
    )} */}

    {/* Logout */}
    {loggedin && (
      <div className="icon-box" onClick={handleLogout}>
        <i className="fas fa-sign-out-alt fa-lg"></i>
      </div>
    )}

    {/* Helpline */}
    {/* <span className="helpline">HELPLINE: 001-2345876</span> */}
  </div>
</div>

      </React.Fragment>
  );
};

export default TopBar;
