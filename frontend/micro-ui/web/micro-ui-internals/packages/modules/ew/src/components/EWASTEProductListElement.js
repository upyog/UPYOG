import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";


const ProductListElement = ({p, quantity, price}) => {

    return (
        <div style={{border:"solid black 2px", borderRadius: "5px"}}>
            <div style={{border: "solid grey 1px", margin: "4px", borderRadius: "5px", display: "inline-block"}}>
                <p>{p?.code}</p>
            </div>
            <div style={{border: "solid grey 1px", margin: "4px", borderRadius: "5px", display: "inline-block"}}>
                <p>{quantity.code}</p>
            </div>
            <div style={{border: "solid grey 1px", margin: "4px", borderRadius: "5px", display: "inline-block"}}>
                <p>{price.code}</p>
            </div>
        </div>
    );
};

export default ProductListElement;
