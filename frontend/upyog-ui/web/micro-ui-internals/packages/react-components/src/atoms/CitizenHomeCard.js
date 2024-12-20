import React from "react";
import { Link } from "react-router-dom";

const CitizenHomeCard = ({ header, links = [], state, Icon, Info, isInfo = false, styles }) => {
  return (
    <div className="CitizenHomeCard" style={styles ? styles : {}}>
      <div className="header">
        <h2>{header}</h2>
        <Icon />
      </div>

      <div className="links">
        {links.map((e, i) => (
          <div className="linksWrapper" style={{paddingLeft:"10px"}}>
            {(e?.parentModule?.toUpperCase() == "BIRTH" ||
              e?.parentModule?.toUpperCase() == "DEATH" ||
              e?.parentModule?.toUpperCase() == "FIRENOC") ?
              <a href={e.link}>{e.i18nKey}</a> :
              <Link key={i} to={{ pathname: e.link, state: e.state }}>
                {e.i18nKey}
              </Link>
            }
          </div>
        ))}
      </div>
      <div>{isInfo ? <Info /> : null}</div>
    </div>
  );
};

export default CitizenHomeCard;
