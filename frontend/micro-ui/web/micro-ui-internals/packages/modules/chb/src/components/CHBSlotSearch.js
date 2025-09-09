import React, { useState, useEffect } from 'react';

/**
 * CHBSlotSearch Component
 * 
 * This component is responsible for searching and checking the availability of booking slots in the CHB (Community Hall Booking) module.
 * It fetches slot availability data and determines whether any of the slots are already booked.
 * 
 * Props:
 * - `slotDetail`: Array containing details of the booking slots, including:
 *    - `name`: The name or code of the community hall.
 *    - `bookingDate`: The start and end dates of the booking.
 *    - `hallCode1`: The code of the hall.
 * 
 * State Variables:
 * - `isSlotBooked`: Boolean state indicating whether any of the slots are already booked.
 * - `loading`: Boolean state indicating whether the slot data is being fetched.
 * 
 * Variables:
 * - `tenantId`: The current tenant ID fetched using the Digit ULB Service.
 * 
 * Hooks:
 * - `useEffect`: Fetches slot availability data when the component is mounted or when `slotDetail` changes.
 * - `Digit.Hooks.chb.useChbSlotSearch`: Custom hook to fetch slot availability data based on the provided filters.
 * 
 * Logic:
 * - Fetches slot availability data using the `useChbSlotSearch` hook with the following filters:
 *    - `communityHallCode`: The name or code of the community hall.
 *    - `bookingStartDate`: The start date of the booking.
 *    - `bookingEndDate`: The end date of the booking.
 *    - `hallCode`: The code of the hall.
 * - Refetches the slot data and checks if any of the slots have a status of "BOOKED".
 * - Updates the `isSlotBooked` state based on the result.
 * - Handles errors during the data fetch and logs them to the console.
 * 
 * Returns:
 * - A component that checks and manages the availability of booking slots.
 * - Displays a loading state while the data is being fetched.
 */
const CHBSlotSearch = ({ slotDetail }) => {
  const [isSlotBooked, setIsSlotBooked] = useState(false);
  const [loading, setLoading] = useState(true);
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        // Fetch slot search data
        const { data: slotSearchData, refetch } = Digit.Hooks.chb.useChbSlotSearch({
          tenantId: tenantId,
          filters: {
            communityHallCode: slotDetail[0].name,
            bookingStartDate: slotDetail[0].bookingDate,
            bookingEndDate: slotDetail[slotDetail.length - 1].bookingDate,
            hallCode: slotDetail[0].hallCode1
          }
        });

        // Perform the refetch and check if any slot is booked
        const result = await refetch();
        const booked = result?.data?.hallSlotAvailabiltityDetails?.some(
          (slot) => slot.slotStatus === "BOOKED"
        );

        setIsSlotBooked(booked);
      } catch (error) {
        console.error('Error fetching slot data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [slotDetail, tenantId]);

  if (loading) {
    return <div>Loading...</div>;
  }
  console.log("isSlotBooked",isSlotBooked);
  return (
    <div>
      {isSlotBooked ? 'Slot is booked' : 'Slot is available'}
    </div>
  );
};

export default CHBSlotSearch;
