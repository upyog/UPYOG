import React, { useState, useEffect } from 'react';

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
