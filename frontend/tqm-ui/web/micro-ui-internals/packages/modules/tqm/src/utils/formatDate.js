const formatDate = (dat) => {
  const date = new Date(dat);
  const day = date.getDate();
  const month = date.toLocaleString("default", { month: "short" });
  const year = date.getFullYear();
  return `${day}${ordinalSuffix(day)} ${month}, ${year}`;
};

const ordinalSuffix = (day) => {
  if (day >= 11 && day <= 13) {
    return "th";
  }
  const lastDigit = day % 10;
  switch (lastDigit) {
    case 1:
      return "st";
    case 2:
      return "nd";
    case 3:
      return "rd";
    default:
      return "th";
  }
};

const getDateRange = ({ startDate, endDate }) => {
  const formattedEndDate = formatDate(endDate);
  const formattedStartDate = formatDate(startDate);

  return {
    endDate: formattedEndDate,
    startDate: formattedStartDate,
  };
};

export default getDateRange;
