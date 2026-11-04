import React, { useState } from "react";

const PlusMinusInput = (props, customProps) => {
  let count = props?.defaultValues || 1;

  function incrementCount() {
    if (count >= 1) {
      count = count + 1;
      props.onSelect(count);
    } else {
      count = 1;
      props.onSelect(count);
    }
  }
  function decrementCount() {
    if (count > 1) {
      count = count - 1;
      props.onSelect(count);
    } else {
      count = 1;
      props.onSelect(count);
    }
  }

  return (
    <React.Fragment>
      <div className="PlusMinus">
        <button type="button" onClick={() => decrementCount(count)} className="PlusMinusbutton">
          -
        </button>
        <input
          readOnly={true}
          value={count}
          className="fsm-plus-minus-input-centered"
        />
        <button type="button" onClick={() => incrementCount(count)} className="PlusMinusbutton">
          +
        </button>
      </div>
    </React.Fragment>
  );
};

export default PlusMinusInput;
