import React from 'react';

const ToggleSwitch = ({ id, isOn, handleToggle, onLabel = "True", offLabel = "False", disabled = false }) => {
  return (
    <div className={`toggle-switch ${disabled ? 'disabled' : ''}`}>
      <input
        checked={isOn}
        onChange={handleToggle}
        className="toggle-switch-checkbox"
        id={id}
        type="checkbox"
        disabled={disabled}
      />
      <label className="toggle-switch-label" htmlFor={id}>
        <span className="toggle-switch-inner" data-yes={onLabel} data-no={offLabel} />
        <span className="toggle-switch-switch" />
      </label>
    </div>
  );
};

export default ToggleSwitch;
