import React from "react";

const Arrow_Upward = ({ style }) => (
  <svg width="16px" height="16px" viewBox="0 0 16 16" xmlns="http://www.w3.org/2000/svg" style={style}>
    <g transform="translate(-141, -55)">
      <g transform="translate(141, 55)">
        <polygon points="0 0 24 0 24 24 0 24" fill="none" />
        <polygon fill="#259B24" fillRule="nonzero" points="4 12 5.41 13.41 11 7.83 11 20 13 20 13 7.83 18.58 13.42 20 12 12 4" />
      </g>
    </g>
  </svg>
);

export function ArrowUpwardElement(marginRight, marginLeft) {
  return (
    <Arrow_Upward
      style={{
        display: "inline-block",
        verticalAlign: "baseline",
        marginRight: !marginRight ? "0px" : marginRight,
        marginLeft: !marginLeft ? "0px" : marginLeft,
      }}
    />
  );
};
