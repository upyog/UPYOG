import React from "react";
import { Link } from "react-router-dom";

const CitizenHomeCard = ({ header, links = [], state, Icon, Info, isInfo = false, styles }) => {
  console.log("links",links,header)
  let linksNew=[]
  if(links[0].name.includes("WS"))
  {
     linksNew = [
      { label: "search", img: "https://i.postimg.cc/WbX7cKWV/icons8-search-bar-100.png" },
      { label: "water connection", img: "https://i.postimg.cc/X7fYZ6SM/icons8-water-96.png" },
      { label: "new connection", img: "https://i.postimg.cc/25Bdv5zj/icons8-tap-90.png" },
      { label: "application", img: "https://i.postimg.cc/FHCG3q3p/icons8-form-50.png" },
      { label: "payment", img: "https://i.postimg.cc/cL70JR3d/icons8-payments-96.png" },
      { label: "audit", img: "https://i.postimg.cc/FHxXv9tR/icons8-audit-96.png" },
      { label: "faq", img: "https://i.postimg.cc/1tqW9HgH/icons8-faq-100.png" },
    ];
  }
  else
  {
    linksNew = [
      { label: "new complaint", img: "https://i.postimg.cc/QdCqYkr0/icons8-complaint-100.png" },
      { label: "My complaint", img: "https://i.postimg.cc/s2Z4sjxW/icons8-complaints-96.png" },
    ];
  }


  return (
    <div className="CitizenHomeCardw" style={styles ? styles : {}}>
      <div className="header" style={{marginTop:"60px",color:"#ff6600"}}>
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
    color: #0a97d5;
    margin: 0;
  }
  
  .header svg {
    width: 28px;
    height: 28px;
    fill: #0a97d5;
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
    border: 4px solid #0a97d5;
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
    color: #0a97d5;
    font-weight: bold;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .moduleLinkHomePage img
  {
    width:70px !important;
    height:70px !important;
    background: white;
    min-width : 70px !important
  }
  `
}
</style>
<div className="links">
  {links.map((e, i) => (
    <div className="linkBox" key={i} style={{ display: "flex", flexDirection: "column" }}>
      {(e?.parentModule?.toUpperCase() === "BIRTH" ||
        e?.parentModule?.toUpperCase() === "DEATH" ||
        e?.parentModule?.toUpperCase() === "FIRENOC") ? (
        <a href={e.link}>{e.i18nKey}</a>
      ) : (
        <Link
          to={{ pathname: e.link, state: e.state }}
          style={{ textDecoration: "none", color: "inherit" }}
        >
          <div style={{
            display: "flex",
            flexDirection: "column-reverse",
            alignItems: "center",
            cursor: "pointer"
          }}>
            <div>{e.i18nKey}</div>
            <img
              width="90"
              height="90"
              src={linksNew[i]?.img}
              alt={e.i18nKey || `icon-${i}`}
              style={{
                width: "70px",
                height: "70px",
                background: "white",
                objectFit: "contain"
              }}
            />
          </div>
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
