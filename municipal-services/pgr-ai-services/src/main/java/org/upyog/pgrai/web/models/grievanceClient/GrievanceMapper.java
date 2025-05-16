package org.upyog.pgrai.web.models.grievanceClient;

import org.upyog.pgrai.web.models.Address;
import org.upyog.pgrai.web.models.Service;
import org.upyog.pgrai.web.models.ServiceRequest;

/**
 * Utility class for mapping a ServiceRequest object to a Grievance object.
 * This class provides a static method to transform the data from the service request
 * into a format suitable for grievance processing.
 */
public class GrievanceMapper {

    /**
     * Converts a ServiceRequest object into a Grievance object.
     *
     * @param request The ServiceRequest object containing the service and address details.
     * @return A Grievance object populated with data from the provided ServiceRequest.
     */
    public static Grievance toGrievance(ServiceRequest request) {
        Service service = request.getService();
        Address address = service.getAddress();

        return Grievance.builder()
                .tenantId(service.getTenantId())
                .serviceCode(service.getServiceCode())
                .serviceType(service.getServiceType())
                .rating(service.getRating())
                .applicationStatus(service.getApplicationStatus())
                .source(service.getSource())
                .latitude(address.getGeoLocation() != null ? address.getGeoLocation().getLatitude() : null)
                .longitude(address.getGeoLocation() != null ? address.getGeoLocation().getLongitude() : null)
                .locality(address.getLocality() != null ? address.getLocality().getName() : null)
                .landmark(address.getLandmark())
                .pincode(address.getPincode())
                .city(address.getCity())
                .state(address.getState())
                .priority(service.getPriority() != null ? service.getPriority().name() : null)
                .inputGrievance(service.getInputGrievance())
                .businessService(service.getServiceCode()) // or another appropriate field
                .action(request.getWorkflow() != null ? request.getWorkflow().getAction() : null)
//                .assignee(request.getWorkflow() != null ? request.getWorkflow().getAssignes().get(0) : null)
                .assignee(null)
                .comments(request.getWorkflow() != null ? request.getWorkflow().getComments() : null)
                .grievanceId(service.getServiceRequestId())
                .build();
    }
}
