import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Card, KeyNote,LinkButton, FormStep } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * Address Component
 * 
 * This component fetches the logged-in user's saved addresses
 * and allows the user to select one address for their application.
 * 
 * Features:
 * - Fetch user's addresses from backend
 * - Display addresses in selectable card format
 * - Highlight selected address
 * - Allow user to add a new address if less than 3 addresses exist
 * - Pass selected address data to parent component on form step submit
 */

const Address = ({t, config, formData, onSelect}) => {
  const userUUID = Digit.UserService.getUser().info?.uuid;
  const [addresses, setAddresses] = useState([]);
  const [selectedAddressStatement, setSelectedAddressStatement] = useState(formData?.addressDetails?.selectedAddressStatement || "");
  const stateId = Digit.ULBService.getStateId();
  useEffect(() => {
    const fetchUserDetails = async () => {
      if (userUUID) {
        try {
          const response = await Digit.UserService.userSearchV2(stateId, { uuid: [userUUID] }, {});
          const user = response?.user?.[0];
          if (user && user.addresses) {
            setAddresses(user.addresses);
          }
        } catch (error) {
          console.error("Error fetching user details:", error);
        }
      }
    };

    fetchUserDetails();
  }, [userUUID]);

  const applicationContainerStyle = {
    padding: '10px',
    border: '1px solid #ccc',
    transition: 'background-color 0.3s ease, box-shadow 0.3s ease',
    position: 'relative',
    marginBottom: '16px',
    width:"50%"
  };

  const applicationContainerHoverStyle = {
    boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.1)'
  };

  const selectedStyle = {
    backgroundColor: '#f0f7ff',
    borderColor: '#2196f3',
    boxShadow: '0px 0px 0px 2px rgba(33, 150, 243, 0.3)'
  };

  const checkmarkStyle = {
    position: 'absolute',
    top: '10px',
    right: '10px',
    color: '#2196f3',
    fontWeight: 'bold',
    fontSize: '20px'
  };

  const goNext = () => {
    let addressStep = { ...formData.address, selectedAddressStatement };
    onSelect(config.key, { ...formData[config.key], ...addressStep }, false);
  };

  const isSelected = (address) => {
    if (!selectedAddressStatement) return false;
    return JSON.stringify(address) === JSON.stringify(selectedAddressStatement);
  };

  return (
    <React.Fragment>
      <FormStep t={t} config={config} onSelect={goNext} isDisabled={!selectedAddressStatement}>
      {addresses.length < 3 &&(
        <div style={{marginBottom:"5px"}}>
        <Link to={`/cnd-ui/citizen/cnd/apply/address-details`}>
        <LinkButton label={t("CND_NEW_OTHER_ADDRESS")} />
        </Link>
        </div>
        )}
        <div>
          {addresses.length > 0 &&
            addresses.map((address, index) => {
              console.log("address",address);
              const selected = isSelected(address);
              return (
                <div key={index}> 
                  <Card
                    style={{ 
                      ...applicationContainerStyle, 
                      ...(selected ? selectedStyle : {}),
                      cursor: "pointer" 
                    }}
                    onMouseEnter={(e) => {
                      if (!selected) {
                        e.currentTarget.style.boxShadow = applicationContainerHoverStyle.boxShadow;
                      }
                    }}
                    onMouseLeave={(e) => {
                      if (!selected) {
                        e.currentTarget.style.boxShadow = '';
                      }
                    }}
                    onClick={() => {
                      setSelectedAddressStatement(address);
                    }}
                  >
                    {selected && <div style={checkmarkStyle}>✓</div>}
                    <KeyNote keyValue={t("CND_ADDRESS_TYPE")} note={address?.type} />
                    <KeyNote keyValue={t("HOUSE_NO")} note={address?.houseNumber} />
                    <KeyNote keyValue={t("ADDRESS_LINE1")} note={address?.address} />
                    <KeyNote keyValue={t("ADDRESS_LINE2")} note={t(`${address?.address2}`)} />
                    <KeyNote keyValue={t("LANDMARK")} note={t(`${address?.landmark}`)} />
                    <KeyNote keyValue={t("CITY")} note={t(`${address?.city}`)} />
                    <KeyNote keyValue={t("LOCALITY")} note={t(`${address?.locality}`)} />
                    <KeyNote keyValue={t("PINCODE")} note={t(`${address?.pinCode}`)} />
                  </Card>
                </div>
              );
            })}
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default Address;