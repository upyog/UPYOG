import React from "react";
import { Link } from "react-router-dom";

const CitizenHomeCard = ({ header, links = [], state, Icon, Info, isInfo = false, styles }) => {
  return (
    <div className="CitizenHomeCardw" style={styles ? styles : {}}>
      <div className="header">
        <h2>{header}</h2>
        {/* <Icon /> */}
      </div>
<style>
{
  `.header {
    display: flex;
    align-items: center;
    justify-content: space-around;
  }
  
  .header h2 {
    font-size: 24px;
    font-weight: 600;
    color: #0e338a;
    margin: 0;
  }
  
  .header svg {
    width: 28px;
    height: 28px;
    fill: #0e338a;
    cursor: pointer;
    transition: transform 0.2s ease;
  }
  
  .header svg:hover {
    transform: scale(1.1);
  }
  
  .links {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin: 16px 0;
    justify-content: space-around;
  }
  
  .linkBox {
    width: 100%;
    max-width: 280px; /* optional: control box width */
    height: 200px;
    margin: 10px;
    padding: 20px;
    background-color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    font-weight: bold;
    box-shadow: rgba(0, 0, 0, 0.35) 0px 5px 15px;
    border-radius: 15px;
    cursor: pointer;
    border: 4px solid #0e338a;
    text-align: center;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }
  
  .linkBox:hover {
    transform: translateY(-4px);
    box-shadow: rgba(0, 0, 0, 0.45) 0px 8px 20px;
  }
  
  .linkBox a,
  .linkBox .Link {
    text-decoration: none;
    color: #0e338a;
    font-weight: bold;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  `
}
</style>
      <div className="links">
  {links.map((e, i) => (
    <div className="linkBox" key={i}>
      {(e?.parentModule?.toUpperCase() === "BIRTH" ||
        e?.parentModule?.toUpperCase() === "DEATH" ||
        e?.parentModule?.toUpperCase() === "FIRENOC") ? (
        <a href={e.link}>{e.i18nKey}</a>
      ) : (
        <Link to={{ pathname: e.link, state: e.state }}>
          {e.i18nKey}
        </Link>
      )}
    </div>
  ))}
</div>

      <div>{isInfo ? <Info /> : null}</div>
    </div>
  );
};

export default CitizenHomeCard;
