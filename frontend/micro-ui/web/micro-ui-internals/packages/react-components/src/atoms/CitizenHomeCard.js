import React from "react";
import { Link } from "react-router-dom";

const CitizenHomeCard = ({ header, links = [], state, Icon, Info, isInfo = false, styles }) => {
  return (
    <div className="citizen-homecard-cls" style={styles ? styles : {}}>
      <div className="header">
        <h2>{header}</h2>
        <Icon />
      </div>

      <div className="links">
        {links.map((e, i) => (
          <div className="linksWrapper">
            {(e?.parentModule?.toUpperCase() == "BIRTH" ||
              e?.parentModule?.toUpperCase() == "DEATH" ||
              e?.parentModule?.toUpperCase() == "FIRENOC") ?
              <a href={e.link}>{e.i18nKey}</a> :
              <div>
                {(e.cardIcon && <img src={e.cardIcon} className="citizen-card-img" />)}
                <Link key={i} to={{ pathname: e.link, state: e.state }} className="citizen-card-txt">
                  {e.i18nKey}
                </Link>
              </div>
              
            }
          </div>
        ))}
      </div>
      <div>{isInfo ? <Info /> : null}</div>
    </div>
  );
};

export default CitizenHomeCard;
