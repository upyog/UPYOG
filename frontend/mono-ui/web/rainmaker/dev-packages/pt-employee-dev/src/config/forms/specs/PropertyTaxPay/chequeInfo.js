const formConfig = {
  name: "chequeInfo ",
  fields: {
    chequeNo: {
      id: "chequeNo",
      type: "textfield",
      floatingLabelText: "TL_PAYMENT_CHQ_NO_LABEL",
      hintText: "TL_PAYMENT_CHQ_NO_PLACEHOLDER",
      jsonPath: "Receipt[0].instrument.transactionNumber",
      pattern: /^([0-9]){6}$/i,
      errorMessage: "PT_CHEQUE_NUMBER_ERROR_MESSAGE",
      errorStyle: { position: "absolute", bottom: -8, zIndex: 5 },
      required: true,
      value: ""
    },
    chequeDate: {
      id: "chequeDate",
      type: "textfield",
      floatingLabelText: "TL_PAYMENT_CHQ_DATE_LABEL",
      hintText: "dd/mm/yy",
      required: true,
      jsonPath: "Receipt[0].instrument.transactionDateInput",
      errorStyle: { position: "absolute", bottom: -8, zIndex: 5 },
      errorMessage: "",
      value: ""
    },
    ifscCode: {
      id: "ifscCode",
      type: "textFieldIcon",
      text: "CS_COMMON_SUBMIT",
      className: "pt-old-pid-text-field",
      floatingLabelText: "PT_IFSC_FLOATING_LABEL",
      hintText: "PT_IFSC_HINT_TEXT",
      required: true,
      numcols: 12,
      errorStyle: { position: "absolute", bottom: -8, zIndex: 5 },
      errorMessage: "PT_IFSC_ERROR_MESSAGE",
      jsonPath: "Receipt[0].instrument.ifscCode",
      pattern: /^[a-zA-Z0-9]{1,11}$/i,
      value: ""
    },
    BankName: {
      id: "BankName",
      required: true,
      hideField: true,
      type: "textfield",
      disabled: true,
      floatingLabelText: "TL_PAYMENT_BANK_LABEL",
      dropDownData: [{ label: "RBI", value: "10101" }],
      jsonPath: "Receipt[0].instrument.bank.name",
      value: ""
    },
    BankBranch: {
      id: "BankBranch",
      required: true,
      disabled: true,
      hideField: true,
      type: "textfield",
      floatingLabelText: "PT_BANK_BRANCH",
      jsonPath: "Receipt[0].instrument.branchName",
      value: ""
    }
  },
  action: "",
  redirectionRoute: "",
  saveUrl: "",
  isFormValid: false
};

export default formConfig;
