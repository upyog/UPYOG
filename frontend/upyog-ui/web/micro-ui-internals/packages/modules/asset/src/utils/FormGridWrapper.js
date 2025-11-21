import React from "react";

const FormGridWrapper = ({ children }) => {
  return (
    <div>
      <style>
        {`
          .responsive-form-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 16px;
            margin-bottom: 16px;
          }
          .responsive-form-grid > div {
            width: 100%;
          }
          .responsive-form-grid .form-field {
            width: 100% !important;
            min-width: 100%;
          }
          .responsive-form-grid .card-label {
            width: 100% !important;
            display: inline-block;
            word-wrap: break-word;
          }
          @media (max-width: 1024px) {
            .responsive-form-grid {
              grid-template-columns: repeat(2, 1fr);
            }
          }
          @media (max-width: 768px) {
            .responsive-form-grid {
              grid-template-columns: 1fr;
            }
          }
        `}
      </style>
      <div className="responsive-form-grid">{children}</div>
    </div>
  );
};

export default FormGridWrapper;