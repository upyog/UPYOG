import React, { forwardRef, useCallback, useEffect, useRef } from "react";
import LinkButton from "./LinkButton";
import { PrimaryDownlaodIcon } from "./svgindex";
import { useTranslation } from "react-i18next";

const MultiLink = forwardRef(({ className, onHeadClick, displayOptions = false, options, label, icon, setShowOptions = null, showOptions, downloadBtnClassName, downloadOptionsClassName, optionsClassName, style, optionsStyle, reportStyles, optionStyle }, ref) => {
  const { t } = useTranslation();
  const menuRef = useRef();
  const parRef = useRef();
  const handleOnClick = useCallback(() => {
    setShowOptions ? setShowOptions(false) : null
  }, [])

  const handleClickOutside = (event) => {
    if (parRef.current && !parRef.current.contains(event.target)) {
      handleOnClick();
    }
  };
  
  useEffect(() => {
    document.addEventListener('click', handleClickOutside);

    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  const MenuWrapper = React.forwardRef((props, ref) => {
    return <div ref={ref} className={`multilink-optionWrap ${optionsClassName} ${downloadOptionsClassName}`} style={optionsStyle}>
      {options.map((option, index) => (
        <div onClick={() => option.onClick()} key={index} className={`multilink-option`} style={optionStyle}>
          {option?.icon}
          {option.label}
        </div>
      ))}
    </div>
  })

  return (
    <div className={className} ref={parRef} style={reportStyles}>
      <div className={`multilink-labelWrap ${downloadBtnClassName}`} onClick={onHeadClick} style={style}>
        {icon ? icon : <PrimaryDownlaodIcon />}
        <LinkButton label={label || t("CS_COMMON_DOWNLOAD")} className="multilink-link-button multilink-label" />
      </div>
      {displayOptions ? <MenuWrapper ref={ref} /> : null}
    </div>
  );
});

export default MultiLink;
