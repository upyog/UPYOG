import React from "react";
import { HamburgerIcon } from "./svgindex";

const Hamburger = ({ handleClick, color }) => (
  <span className="hamburger-span rc-hamburger-spacing" onClick={handleClick}>
    <HamburgerIcon className="hamburger" color={color} />
  </span>
);

export default Hamburger;
