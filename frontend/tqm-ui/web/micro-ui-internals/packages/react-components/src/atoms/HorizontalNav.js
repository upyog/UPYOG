import React, {useState} from 'react'
import { useTranslation } from 'react-i18next'
import BreakLine from './BreakLine'
const HorizontalNav = ({ configNavItems, activeLink, setActiveLink, showNav = false, children, customStyle = {}, customClassName = "", inFormComposer = true, navClassName = "", navStyles = {},fromSearchComp=false }) => {
    const { t } = useTranslation()
    const setActive = (item) => {
        setActiveLink(item.name)
    }

    const MenuItem = ({ item }) => {
        let itemComponent = item.code;

        const Item = () => (
            <span className="menu-item">
                <div className="menu-label">{t(itemComponent)}</div>
            </span>
        );

        return (
            <Item />
        );
    };
   
    if(fromSearchComp) {
        return (
            <div className={navClassName} style={fromSearchComp ?{width:"100%",...navStyles} :{...navStyles}}>
                {showNav && <div className={fromSearchComp?`horizontal-nav ${customClassName}`:`horizontal-nav ${customClassName}`} style={inFormComposer?{ marginLeft: "16px", marginRight: "16px", marginTop:"0px",...customStyle }:{...customStyle}} >
                    {configNavItems?.map((item, index) => (
                        <div className={`sidebar-list-search-form ${activeLink === item.name ? "active" : ""}`} key={index} onClick={() => setActive(item)}>
                            <MenuItem item={item} />
                        </div>
                    ))}
                </div>
              }
              {/* Commenting out for now due to horizontal line coming in every inbox as well */}
              {/* <BreakLine style={{margin:"0px 16px 0px 16px"}}/> */}
              {children}
          </div>
        )
    }
  return (
      <div className={navClassName} style={{...navStyles}}>
          {showNav && <div className={`horizontal-nav ${customClassName}`} style={inFormComposer?{ marginLeft: "16px", marginRight: "16px", ...customStyle }:{...customStyle}} >
              {configNavItems?.map((item, index) => (
                  <div className={`sidebar-list ${activeLink === item.name ? "active" : ""}`} key={index} onClick={() => setActive(item)}>
                      <MenuItem item={item} />
                  </div>
              ))}
          </div>
        }
        {children}
    </div>
  )
}

export default HorizontalNav