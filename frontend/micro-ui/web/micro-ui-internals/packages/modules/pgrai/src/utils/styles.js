/**
 * Centralized styles for the PGR AI module components
 * This file contains reusable styles that can be imported across components
 */

// Add CSS for spinner animation
const spinnerStyle = document.createElement('style');
spinnerStyle.innerHTML = `
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  .spinner {
    animation: spin 1s linear infinite;
  }
`;
document.head.appendChild(spinnerStyle);

export const styles = {
  // Document.js styles
  documentContainer: {
    marginBottom: "24px"
  },
  documentField: {
    display: "flex", 
    alignItems: "center", 
    justifyContent: "space-between",
    width: "50%"
  },
  addButton: {
    padding: "8px 16px",
    marginLeft: "8px",
    backgroundColor: "white",
    border: "2px solid #902434",
    borderRadius: "20px",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: "500",
    color: "#902434",
    boxShadow: "0 2px 4px rgba(144, 36, 52, 0.1)",
    outline: "none",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    width: "120px"
  },
  deleteButton: {
    padding: "8px 16px",
    marginLeft: "8px",
    backgroundColor: "white",
    border: "2px solid #902434",
    borderRadius: "20px",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: "500",
    color: "#902434",
    boxShadow: "0 2px 4px rgba(144, 36, 52, 0.1)",
    outline: "none",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    width: "50px"
  },
  deleteIcon: {
    cursor: "pointer"
  },
  textInputStyle: {
    borderRadius: "8px",
    border: "1px solid #ccc",
    boxShadow: "0 1px 3px rgba(0, 0, 0, 0.1)",
    transition: "all 0.3s ease",
    "&:focus": {
      border: "1px solid #999",
      boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)"
    }
  },
  submitBarStyle: {
    display: "flex",
    justifyContent: "flex-start",
    marginLeft: "8px",
    width: "187px"
  },
  suggestionContainer: {
    position: "absolute",
    top: "100%",
    left: 0,
    backgroundColor: "#fff",
    border: "none",
    borderRadius: "0 0 8px 8px",
    zIndex: 10,
    maxHeight: "250px",
    overflowY: "auto",
    boxShadow: "0 4px 12px rgba(0, 0, 0, 0.15)",
    width: "100%",
    padding: "4px",
    scrollbarWidth: "thin",
    scrollbarColor: "#902434 #f0f0f0",
    marginTop: "-1px" // This connects the suggestions to the textarea
  },
  suggestionItem: {
    padding: "12px 16px",
    cursor: "pointer",
    borderRadius: "6px",
    margin: "2px 0",
    transition: "all 0.2s ease",
    "&:hover": {
      backgroundColor: "#fff8f8",
      transform: "translateX(4px)"
    }
  },
  lastSuggestionItem: {
    borderBottom: "none"
  },
  suggestionSubtype: {
    fontSize: "13px",
    color: "#666",
    marginTop: "4px",
    fontWeight: "400"
  },
  locationField: {
    display: "flex",
    position: "relative"
  },
  locationButton: {
    padding: "16px 16px",
    marginLeft: "8px",
    backgroundColor: "white",
    border: "2px solid #902434",
    borderRadius: "19px",
    cursor: "pointer",
    fontSize: "15px",
    fontWeight: "500",
    color: "#902434",
    boxShadow: "0 2px 4px rgba(144, 36, 52, 0.1)",
    transition: "all 0.3s ease",
    outline: "none",
    display: "flex",
    alignItems: "center",
    width: "187px",
    justifyContent: "center",
    "&:hover": {
      backgroundColor: "#fff8f8",
      transform: "translateY(-1px)"
    }
  },
  readOnlyInput: {
    color: "#666"
  },
  errorMessage: {
    color: "#902434",
    margin: "10px 0"
  },
  loaderContainer: {
    display: "flex",
    alignItems: "center",
    marginTop: "8px",
    marginBottom: "8px"
  },
  customLoader: {
    width: "24px",
    height: "24px",
    border: "3px solid rgba(144, 36, 52, 0.1)",
    borderRadius: "50%",
    borderTop: "3px solid #902434",
    marginRight: "10px"
  },
  loadingText: {
    color: "#902434",
    fontSize: "14px",
    fontWeight: "500"
  },
  cardLabelStyle: {
    fontSize: "18px",
    fontWeight: "600",
    color: "#505A5F",
    marginBottom: "8px",
    display: "inline-block"
  },
  requiredAsterisk: {
    color: "#FF3333",
    fontSize: "16px",
    fontWeight: "600"
  },
  suggestionTitle: {
    fontWeight: "600", 
    color: "#902434",
    fontSize: "15px"
  },
  orDivider: {
    padding: "4px 12px",
    backgroundColor: "#f8f9fa",
    borderRadius: "12px",
    fontSize: "14px",
    color: "#666",
    fontWeight: "500",
  },
  orDividerContainer: {
    display: 'flex', 
    justifyContent: 'flex-start', 
    marginLeft: "28%",
    marginTop: "16px",
    marginBottom: "16px"
  },
  locationIcon: {
    marginRight: "2px", 
    fill: "#902434"
  }
};

export default styles;