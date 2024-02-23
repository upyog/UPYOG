import React from "react";
import "./index.css";

const styles={
  box:{

  },
  
}

const CountDetails = ({ count, total,status }) => {
  return (
    <div className="box">
      <div className="count-details">
        Showing {count} of {count} {status} complaints
      </div>
    </div>
  );
};

export default CountDetails;
