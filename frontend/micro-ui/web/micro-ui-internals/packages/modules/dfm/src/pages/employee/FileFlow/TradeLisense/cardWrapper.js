import React from "react";

const CardWrapper = ({children,title}) => {
  return (
      <div className="rowSections">
        <div className="col-12">
          <hr className="m-v-15 seperter"></hr>
        </div>
        <div className="col-md-4">
          <h4 className="card-title">{title}</h4>
        </div>
        <div className="col-md-8">
          <div className="cardSection">
            <div className="cardSectionBody">
                {children}
            </div>
          </div>
        </div>
      </div>
  );
};

export default CardWrapper;
