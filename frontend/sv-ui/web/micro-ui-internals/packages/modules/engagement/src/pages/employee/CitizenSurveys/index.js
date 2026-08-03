import React from "react"
import { Routes, Route } from "react-router-dom"
import { PrivateRoute } from "@nudmcdgnpm/upyog-ui-react-components-lts"
import Inbox from "./Inbox"
import NewSurvey from "./NewSurvey"
import CreateResponse from "./responses/create"
import UpdateResponse from './responses/update'
import DeleteResponse from "./responses/delete"
//import EditSurvey from "./EditSurvey"
import SurveyDetails from "./SurveyDetails"
import SurveyResults from "./SurveyResults"

const Surveys = ({ match, tenants, parentRoute }) => {
// REMOVED: match.path destructuring (not needed in v6)

    return (
        <Routes>
            <Route 
                path="inbox/create"
                element={
                    <PrivateRoute>
                        <NewSurvey />
                    </PrivateRoute>
                    }
            />
            <Route 
                path="create" 
                element={
                    <PrivateRoute>
                        <NewSurvey />
                    </PrivateRoute>
                } 
            />
            <Route 
                path="inbox/details/:id" 
                element={
                    <PrivateRoute>
                        <SurveyDetails />
                    </PrivateRoute>
                } 
            />
            <Route 
                path="inbox/results/:id" 
                element={
                    <PrivateRoute>
                        <SurveyResults />
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
                path="create-response" 
                element={
                    <PrivateRoute>
                        <CreateResponse />
                    </PrivateRoute>
                } 
            />
            <Route 
                path="update-response" 
                element={
                    <PrivateRoute>
                        <UpdateResponse />
                    </PrivateRoute>
                } 
            />
            <Route 
                path="update-response" 
                element={
                    <PrivateRoute>
                        <UpdateResponse />
                    </PrivateRoute>
                } 
            />
            <Route 
                path="delete-response" 
                element={
                    <PrivateRoute>
                        <DeleteResponse />
                    </PrivateRoute>
                } 
            />
        
        </Routes>
    )
}

export default Surveys