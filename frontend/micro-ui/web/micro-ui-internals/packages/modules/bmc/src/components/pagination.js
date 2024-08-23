import React from "react";

const Pagination = ({ totalRecords, rowsPerPage, currentPage, onPageChange, onRowsPerPageChange }) => {
  const totalPages = Math.ceil(totalRecords / rowsPerPage);

  const handlePageChange = (page) => {
    if (page !== currentPage) {
      onPageChange(page);
    }
  };

  const renderPaginationNumbers = () => {
    const pageNumbers = [];
    for (let i = 1; i <= totalPages; i++) {
      pageNumbers.push(i);
    }
    return pageNumbers.map((page) => (
      <li key={page} className={`page-item ${page === currentPage ? "active" : ""}`} onClick={() => handlePageChange(page)}>
        <button className="page-link">{page}</button>
      </li>
    ));
  };

  return (
    <React.Fragment>
      <div className="bmc-pagination-container">
        <div className="bmc-pagination-info">
          Showing {(currentPage - 1) * rowsPerPage + 1} to {Math.min(currentPage * rowsPerPage, totalRecords)} of {totalRecords} records
          <span style={{ paddingLeft: "10px" }}>Rows per page:</span>
          <select value={rowsPerPage} onChange={(e) => onRowsPerPageChange(Number(e.target.value))}>
            <option value={15}>15</option>
            <option value={30}>30</option>
            <option value={45}>45</option>
            <option value={60}>60</option>
          </select>
        </div>
        <ul className="bmc-pagination">
          <li className={`page-item ${currentPage === 1 ? "disabled" : ""}`} onClick={() => handlePageChange(currentPage - 1)}>
            <button className="page-link" aria-label="Previous">
              &laquo;
            </button>
          </li>
          {renderPaginationNumbers()}
          <li className={`page-item ${currentPage === totalPages ? "disabled" : ""}`} onClick={() => handlePageChange(currentPage + 1)}>
            <button className="page-link" aria-label="Next">
              &raquo;
            </button>
          </li>
        </ul>
      </div>
    </React.Fragment>
  );
};

export default Pagination;
