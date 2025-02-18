// this component is made to export Heading in bookingpopup file
import React from 'react';

const Heading = React.memo(({ t }) => {
  return <h1 className="heading-m">{t("WT_MY_BOOKINGS_HEADER")}</h1>;
});

export default Heading;
