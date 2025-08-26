import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Card, KeyNote,LinkButton, FormStep } from "@nudmcdgnpm/digit-ui-react-components";
import { cndStyles } from "../utils/cndStyles";

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
  const userType= Digit.UserService.getUser().info?.type;
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
        <div style={cndStyles.addButtonMargin}>
        <Link
          to={{
            pathname: `/cnd-ui/${userType === "EMPLOYEE" ? "employee" : "citizen"}/cnd/apply/address-details`,
            state: { usedAddressTypes: addresses.map(a => a.addressType) }
          }}
        >
          <LinkButton label={t("CND_NEW_OTHER_ADDRESS")} />
        </Link>
        </div>
        <div>
          {addresses.length > 0 &&
            addresses.map((address, index) => {
              const selected = isSelected(address);
              return (
                <div key={index}> 
                  <Card
                    style={{ 
                      ...cndStyles.applicationContainerStyle,
                      ...(selected ? cndStyles.selectedStyle : {}),
                    }}
                    onMouseEnter={(e) => {
                      if (!selected) {
                        e.currentTarget.style.boxShadow = cndStyles.applicationContainerHoverStyle.boxShadow
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
                    {selected && <div style={cndStyles.checkmarkStyle}>✓</div>}
                    <div style={cndStyles.addressGrid}>
                    <KeyNote keyValue={t("CND_ADDRESS_TYPE")} note={address?.type} />
                    <KeyNote keyValue={t("HOUSE_NO")} note={address?.houseNumber} />
                    <KeyNote keyValue={t("ADDRESS_LINE1")} note={address?.address} />
                    <KeyNote keyValue={t("ADDRESS_LINE2")} note={t(`${address?.address2}`)} />
                    <KeyNote keyValue={t("LANDMARK")} note={t(`${address?.landmark}`)} />
                    <KeyNote keyValue={t("CITY")} note={t(`${address?.city}`)} />
                    <KeyNote keyValue={t("LOCALITY")} note={t(`${address?.locality}`)} />
                    <KeyNote keyValue={t("PINCODE")} note={t(`${address?.pinCode}`)} />
                    </div>
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