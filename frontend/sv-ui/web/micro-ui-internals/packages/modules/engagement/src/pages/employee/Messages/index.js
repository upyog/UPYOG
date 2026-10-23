import React from "react"
import { Routes, Route } from "react-router-dom"
import { PrivateRoute } from "@nudmcdgnpm/upyog-ui-react-components-lts"
import Inbox from "./Inbox"
import NewMessage from "./NewMessage"
import Response from "./NewMessage/Response"
import EditMessage from "./EditMessage"
import MessageDetails from "./MessageDetails"
import DocumentDetails from "../../../components/Messages/DocumentDetails"

const Messages = ({ match, tenants, parentRoute }) => { 
    // match.path destructuring (not needed in v6)
    return (
        <Routes>
        <Route 
            path="create" 
            element={
            <PrivateRoute>
                <NewMessage />
            </PrivateRoute>
            } 
        />
        
        <Route 
            path="inbox/create" 
            element={
            <PrivateRoute>
                <NewMessage />
            </PrivateRoute>
            } 
        />
        
        <Route 
            path="inbox/details/:id" 
            element={
            <PrivateRoute>
                <DocumentDetails />
            </PrivateRoute>
            } 
        />
        
        <Route 
            path="inbox/edit/:id" 
            element={
            <PrivateRoute>
                <EditMessage />
            </PrivateRoute>
            } 
        />
        
        <Route 
            path="inbox" 
            element={
            <PrivateRoute>
                <Inbox tenants={tenants} parentRoute={parentRoute} />
            </PrivateRoute>
            } 
        />
        
        <Route 
            path="response" 
            element={
            <PrivateRoute>
                <Response />
            </PrivateRoute>
            } 
        />
    </Routes>
    )
}

export default Messages