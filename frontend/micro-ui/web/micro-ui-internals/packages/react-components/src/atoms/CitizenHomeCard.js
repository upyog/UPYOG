import React from "react";
import { Link } from "react-router-dom";

const CitizenHomeCard = ({ header, links = [], state, Icon, Info, isInfo = false, styles }) => {
  console.log("links",links,header)
  let linksNew=[]
  if(links[0].name.includes("WS"))
  {
     linksNew = [
      { label: "search", img: "https://i.postimg.cc/WbX7cKWV/icons8-search-bar-100.png" },
      { label: "water connection", img: "	https://i.postimg.cc/9FNGKqSy/icons8-bill-96-1.png" },
      { label: "new connection", img: "https://i.postimg.cc/FHCG3q3p/icons8-form-50.png" },
      { label: "application", img: "https://i.postimg.cc/25Bdv5zj/icons8-tap-90.png" },
      { label: "payment", img: "https://i.postimg.cc/cL70JR3d/icons8-payments-96.png" },
      { label: "audit", img: "https://i.postimg.cc/FHxXv9tR/icons8-audit-96.png" },
      { label: "faq", img: "https://i.postimg.cc/FHCG3q3p/icons8-form-50.png" },
      {label: "Create Property", img:"https://i.postimg.cc/Y2P6yWTY/icons8-faq-100.png"}
    ];
  }
  else if(links[0].name.includes("PT"))
  {
     linksNew = [
      { label: "My Properties", img: "https://i.postimg.cc/WbX7cKWV/icons8-search-bar-100.png" },
      { label: "water connection", img: "https://i.postimg.cc/8ckr71FR/icons8-bill-100.png" },
      { label: "new connection", img: "https://i.postimg.cc/85RhFKZS/icons8-country-house-100.png" },
      { label: "application", img: "https://i.postimg.cc/FHCG3q3p/icons8-form-50.png" },
      { label: "payment", img: "https://i.postimg.cc/cL70JR3d/icons8-payments-96.png" },
      { label: "audit", img: "https://i.postimg.cc/FHxXv9tR/icons8-audit-96.png" },
      { label: "faq", img: "https://i.postimg.cc/FHxXv9tR/icons8-audit-96.png" },
      {label: "Create Property", img:"https://i.postimg.cc/Y2P6yWTY/icons8-faq-100.png"},
      {label: "How Ir works", img:"https://i.postimg.cc/t4zQ5p7Z/icons8-it-100.png"}
    ];
  }
  else if(links[0].name.includes("TL"))
  {
     linksNew = [
      { label: "new", img: "https://i.postimg.cc/63yYGFMg/icons8-license-100-1.png" },
      { label: "renew", img: "https://i.postimg.cc/639M5yZW/icons8-renew-100.png" },
      { label: "application", img: "https://i.postimg.cc/FHCG3q3p/icons8-form-50.png" },
      { label: "faq", img: "https://i.postimg.cc/1tqW9HgH/icons8-faq-100.png" }
    ];
  }
  else if(links[0].name.includes("UC"))
  {
     linksNew = [
      { label: "My Properties", img: "https://i.postimg.cc/WbX7cKWV/icons8-search-bar-100.png" },
      { label: "water connection", img: "https://i.postimg.cc/bN8wYPC6/icons8-invoice-64.png" },
      { label: "new connection", img: "https://i.postimg.cc/Y2P6yWTY/icons8-faq-100.png" },
    ];
  }
  else if(links[1].link.includes("fsm"))
  {
     linksNew = [
      { label: "My Properties", img: "https://i.postimg.cc/7PTrFRqK/icons8-truck-100-1.png" },
      { label: "water connection", img: "https://i.postimg.cc/bN8wYPC6/icons8-invoice-64.png" },
      { label: "new connection", img: "https://i.postimg.cc/kGM8bSM4/icons8-people-100.png" },
    ];
  }
  else if(links[0].name.includes("BPA"))
  {
     linksNew = [
      { label: "My application", img: "https://i.postimg.cc/FHCG3q3p/icons8-form-50.png" },
      { label: "stakeholder", img: "https://i.postimg.cc/QCQHMdZb/icons8-stakeholder-68.png" },
      { label: "architect", img: "https://i.postimg.cc/yYLrJYNv/icons8-architect-100.png" },
      { label: "faq", img: "https://i.postimg.cc/Y2P6yWTY/icons8-faq-100.png" },
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
      <div className="header" style={{marginTop:"60px",color:"#ff6600",marginBottom:"30px"}}>
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
  
  .HomePageContainer {
    background-color: #ebf1fb;
    background-image: url(https://jaljeevanmission.gov.in/themes/edutheme/images/header-bg.png);
    background-size: cover;
    background-position: center;
  }
  
  .HomePageWrapper {
    padding: 40px 20px;
    background: linear-gradient(135deg, #f4f7fb 0%, #e9f1f9 100%);
    min-height: 100vh;
  }
  
  .HomePageWrapper h1 {
    text-align: center;
    font-size: 2.3rem;
    font-weight: 700;
    color: #004080;
    margin-bottom: 10px;
  }
  
  .HomePageWrapper p {
    text-align: center;
    font-size: 1.05rem;
    color: #444;
    margin-bottom: 30px;
    max-width: 700px;
    margin-left: auto;
    margin-right: auto;
  }
  
  .cardContainer {
    display: flex;
    flex-wrap: wrap;
    gap: 30px;
    justify-content: center;
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .incidentBlock {
    background: white;
    border-radius: 16px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 250px;
    height: 160px;
    padding: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }
  
  .incidentBlock:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  }
  
  .incidentBlock img {
    width: 64px;
    height: 64px;
    object-fit: contain;
    margin-bottom: 12px;
    min-width: 0vw;
  }
  
  .incidentBlock span, .incidentBlock .external-link {
    font-size: 1.1rem;
    font-weight: 600;
    color: #002b60;
    text-align: center;
    line-height: 1.4;
    text-decoration: none;
  }
  .text {
    font-size: 1.1rem;
    font-weight: 600;
    color: #002b60;
    text-align: center;
    line-height: 1.4;
  }
  .incidentBlock .external-link:hover {
    text-decoration: underline;
  }
  
  /* Responsive Fixes */
  @media (max-width: 768px) {
    .incidentBlock {
      width: 100%;
      height: auto;
      padding: 20px;
    }
  
    .incidentBlock img {
      width: 56px;
      height: 56px;
    }
  
    .incidentBlock span, .incidentBlock .external-link {
      font-size: 0.95rem;
    }
  
    .HomePageWrapper h1 {
      font-size: 1.8rem;
    }
  
    .HomePageWrapper p {
      font-size: 0.95rem;
    }
    .moduleLinkHomePage img {
      height: 33vw;
      background: -webkit-gradient(linear, left top, left bottom, from(#000), to(#000));
      background: linear-gradient(#0000, #0000);
      width: 100%;
      -o-object-fit: cover;
      object-fit: cover;
      min-width: 0vw;
  }
  }
  
  `
}
</style>
<div className="HomePageContainer">
      <div className="cardContainer">
        {links.map((e, i) => {
          const isExternal = ["BIRTH", "DEATH", "FIRENOC"].includes(
            e?.parentModule?.toUpperCase()
          );
          const image = linksNew[i]?.img;

          return (
            <div className="incidentBlock" key={i}>
              {isExternal ? (
                <a
                  href={e.link}
                  className="external-link"
                  style={{ textDecoration: "none", color: "inherit" }}
                >
                  <div
                    style={{
                      display: "flex",
                      flexDirection: "column-reverse",
                      alignItems: "center",
                      cursor: "pointer",
                    }}
                  >
                    <div>{e.i18nKey}</div>
                    <img
                      src={image}
                      alt={e.i18nKey || `icon-${i}`}
                      style={{
                        width: "70px",
                        height: "70px",
                        background: "white",
                        objectFit: "contain",
                      }}
                    />
                  </div>
                </a>
              ) : (
                <Link
                  to={{ pathname: e.link, state: e.state }}
                  style={{ textDecoration: "none", color: "inherit" }}
                >
                  <div
                    style={{
                      display: "flex",
                      flexDirection: "column-reverse",
                      alignItems: "center",
                      cursor: "pointer",
                    }}
                  >
                    <div className="text">{e.i18nKey}</div>
                    <img
                      src={image}
                      alt={e.i18nKey || `icon-${i}`}
                      style={{
                        width: "70px",
                        height: "70px",
                        background: "white",
                        objectFit: "contain",
                      }}
                    />
                  </div>
                </Link>
              )}
            </div>
          );
        })}
      </div>
    </div>


      <div>{isInfo ? <Info /> : null}</div>
    </div>
  );
};

export default CitizenHomeCard;
