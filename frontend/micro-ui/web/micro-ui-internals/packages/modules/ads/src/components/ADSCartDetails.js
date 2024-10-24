import React, { useEffect, useState } from "react";
import ApplicationTable from "./ApplicationTable";
import { CardSubHeader, DeleteIcon, Modal } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>
);

const CloseBtn = (props) => {
  return (
    <div className="icon-bg-secondary" onClick={props.onClick}>
      <Close />
    </div>
  );
};
/**
 * ADSCartDetails component displays  a table of advertisement cart items,
 * allowing users to view, delete items, and see the total price of the cart.
 */

const ADSCartDetails = ({ onClose }) => {
  const { t } = useTranslation();
  const { pathname: url } = useLocation();
  const [data, setData] = useState([]);

  useEffect(() => {
    const datalist = [
      { name: "UNIPOLE_12_8", fromDate: "15-11-2024", toDate: "25-11-2024", total_price: 4600 },
      { name: "UNIPOLE_12_8", fromDate: "15-11-2024", toDate: "25-11-2024", total_price: 4600 },
      { name: "UNIPOLE_12_8", fromDate: "15-11-2024", toDate: "25-11-2024", total_price: 4600 },
      { name: "UNIPOLE_12_8", fromDate: "15-11-2024", toDate: "25-11-2024", total_price: 4600 },
      { name: "UNIPOLE_12_8", fromDate: "15-11-2024", toDate: "25-11-2024", total_price: 4600 },
      { name: "UNIPOLE_12_8", fromDate: "15-11-2024", toDate: "25-11-2024", total_price: 4600 }
      // Add more data as needed
    ];
    setData(datalist);
  }, []);

  const handleDelete = (index) => {
    const updatedList = data.filter((_, idx) => idx !== index);
    setData(updatedList);
  };

  const calculateTotalPrice = () => {
    return data.reduce((total, item) => total + Number(item.total_price), 0);
  };

  const columns = [
    { Header: t("ADVERTISEMENT_NAME"), accessor: "name" },
    { Header: t("FROM_DATE"), accessor: "fromDate" },
    { Header: t("TO_DATE"), accessor: "toDate" },
    { Header: t("TOTAL_PRICE"), accessor: "total_price" },
    {
      Header: t("DELETE_KEY"),
      accessor: "delete",
      Cell: ({ row }) => (
        <button onClick={() => handleDelete(row.index)}>
          <DeleteIcon className="delete" fill="#a82227" style={{ cursor: "pointer", marginLeft: "20px" }} />
        </button>
      ),
    },
  ];

  return (
    <Modal
      headerBarMain={<CardSubHeader style={{ color: '#a82227', margin: '25px' }}>MY_CART</CardSubHeader>}
      headerBarEnd={<CloseBtn onClick={onClose} />}
      popupStyles={{ backgroundColor: "#fff", position: 'relative', maxHeight: '80vh', width: '80%', overflowY: 'auto' }}
      popupModuleMianStyles={{ padding: "10px" }}
      hideSubmit={true} 
      headerBarMainStyle={{ position: "sticky", top: 0, backgroundColor: "#f5f5f5" }}
      formId="modal-action"
    >
      <ApplicationTable
        t={t}
        data={data}
        columns={columns}
        getCellProps={(cellInfo) => ({
          style: {
            minWidth: "150px",
            padding: "20px",
            fontSize: "16px",
          },
        })}
        isPaginationRequired={false}
        totalRecords={data.length}
      />
      <div style={{ padding: "20px", fontSize: "18px", fontWeight: "bold" }}>
        Total Price: {calculateTotalPrice()} 
      </div>
    </Modal>
  );
};

export default ADSCartDetails;