import React, { useState, useEffect } from "react";
import { collectionRemovalFeeAmt, stablingFeeAmt } from "../constants/dummyData"; // This will be replaced by an API URL later

const useStablingFee = () => {
    const [totalRemovalFee, setTotalRemovalFee] = useState(0);

    useEffect(() => {
        const fetchRemovalFee = async () => {
            try {
                //const response = await fetch(collectionRemovalFeeAmt);
                //const totalFee = await response.json(); // Assuming the response is in JSON format
                const totalFee = stablingFeeAmt;
                console.log(totalFee);
                setTotalRemovalFee(totalFee.amount);
            } catch (error) {
                console.error("Error fetching removal fee:", error);
            }
        };

        fetchRemovalFee();
    }, []);

    return totalRemovalFee;
};

export default useStablingFee;
