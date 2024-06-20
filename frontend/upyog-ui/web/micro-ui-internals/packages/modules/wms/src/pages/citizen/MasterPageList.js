import { Card } from "@egovernments/digit-ui-react-components";
import React, {Fragment} from "react";
import {Link,useLocation} from "react-router-dom"
const MasterPageList = ()=>{
    const location = useLocation();
console.log("location ",location)
    const data = [
        {
            head:"Contractor Master",
            body:[
                {"name":"Vender Type List","link":"/upyog-ui/citizen/wms/vendor-type/list"},
                {"name":"Bank List","link":"/upyog-ui/citizen/wms/bank/list"},
                {"name":"Vendor Sub Type","link":"/upyog-ui/citizen/wms/vendor-sub-type/list"},
                {"name":"Vendor Class List","link":"/upyog-ui/citizen/wms/vendor-class/list"},
                {"name":"Function","link":"/upyog-ui/citizen/wms/function-app/list"},
                {"name":"Account Head","link":"/upyog-ui/citizen/wms/account-head/list"},
            ]
        },
        {
            head:"Tender Entry",
            body:[
                {"name":"Department List","link":"/upyog-ui/citizen/wms/tender-entry/department/list"},
                {"name":"Tender Category","link":"/upyog-ui/citizen/wms/tender-entry/tender-category/list"}
            ]
        },
    ]

return(
<Card>
<header class="card-sub-header ">Master Page Links</header>
{data.map((item,i)=>{
 return (
    <>
 <div key={i} style={{"padding":"5px 10px", "backgroundColor":"#bb4e50","color":"#ffffff"}}><strong>{item?.head}</strong></div>
 <ul>
    {
     item?.body?.map((res)=>(<li style={{"padding":"5px 0px","border-bottom":"1px solid #000"}}><Link to={res.link}>{res.name}</Link></li>))
    }
</ul></>)

})}
</Card>)
}

export default MasterPageList