import React from 'react'
import {
    TextInput,
    SearchIconSvg,

} from "@nudmcdgnpm/upyog-ui-react-components-lts";

const Searchbar = ({searchValue, onChange, handleKeyPress, handleSearch, t}) => {
 

    return (
        <div>
            <TextInput textInputStyle={{maxWidth:"960px"}}
            className="searchInput" 
            placeholder={t("CE_SERACH_DOCUMENTS")} 
            value={searchValue}
            onChange={(ev) => onChange(ev.target.value)} 
            signature={true}
            signatureImg={<SearchIconSvg className="signature-img" onClick={() => handleSearch()}/>}
            onKeyPress={handleKeyPress}
            />
        </div>
    )
}

export default Searchbar;
