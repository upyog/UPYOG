import React,{Fragment} from 'react'
import { Header,BreakLine, CardHeader,Card,CardSubHeader } from "@nudmcdgnpm/digit-ui-react-components"

const WhoHasResponded = ({t,userInfo}) => {
    const data = Object.entries(userInfo);
  return (
    <div className="eng-survey-results-view-spacing">
        <header className="eng-who-has-responded-header">{t("WHO_RESPONDED")}</header>
       
            {/* <header className="custom-style">Email</header>
            <header className="custom-style">Phone Number</header> */}
         {/* <div className='responses-container'>
            {data.map(user => <div className='response-result' className="custom-style"> <p className="custom-style">{user[1]}</p>
            <p className="custom-style">{user[0]}</p><BreakLine /></div>  )} */}


            <div className="eng-who-has-responded-flex-container">
                <div className="eng-who-has-responded-container-padding">
                    <header className="eng-who-has-responded-header-2">{t("SURVEY_EMAIL")}</header>
                    {data.map(user=> <p className="eng-who-has-responded-spacing">{user[1]}</p>)}
                </div>
                <div className="eng-who-has-responded-container-padding">
                    <header className="eng-who-has-responded-header-2">{t("SURVEY_PHONE_NUMBER")}</header>
                    {data.map(user=> <p className="eng-who-has-responded-spacing">{user[0]}</p>)}
                </div>
                
            </div>
    </div>

    
    )
}

export default WhoHasResponded