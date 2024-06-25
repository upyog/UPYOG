package org.egov.asset.web.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.asset.service.AssetService;
import org.egov.asset.util.ResponseInfoFactory;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetSearchCriteria;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.AssetResponse;
import org.egov.asset.web.models.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;
import io.swagger.annotations.ApiParam;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")

@Controller
@RequestMapping("/asset-services")
public class AssetControllerV1 {

	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	
	@Autowired
	AssetService assetService;

	@Autowired
	public AssetControllerV1(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

	@RequestMapping(value = "/v1/assets/_create", method = RequestMethod.POST)
	public ResponseEntity<AssetResponse> v1AssetsCreatePost(
			@ApiParam(value = "Details for the new asset(s) + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
		//String accept = request.getHeader("Accept");
		//if (accept != null && accept.contains("application/json")) {
			Asset asset = assetService.create(assetRequest);
			List<Asset> assets = new ArrayList<Asset>();
			assets.add(asset);
			AssetResponse response = AssetResponse.builder().assets(assets)
					.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
					.build();
			return  new ResponseEntity<>(response, HttpStatus.OK);
		//}

		//return new ResponseEntity<AssetResponse>(HttpStatus.NOT_IMPLEMENTED);
	}

//	@RequestMapping(value = "/v1/assets/_search", method = RequestMethod.POST)
//	public ResponseEntity<AssetResponse> v1AssetsSearchPost(
//			@NotNull @ApiParam(value = "Unique id for a tenant.", required = true) @Valid @RequestParam(value = "tenantId", required = true) String tenantId,
//			@ApiParam(value = "Parameter to carry Request metadata in the request body") @Valid @RequestBody RequestInfo requestInfo,
//			@ApiParam(value = "Filter assets by category.") @Valid @RequestParam(value = "assetCategory", required = false) String assetCategory,
//			@Size(max = 50) @ApiParam(value = "unique identifier of asset registration") @Valid @RequestParam(value = "ids", required = false) List<Long> ids,
//			@Size(min = 2, max = 64) @ApiParam(value = "Unique application number for the Birth Registration Application") @Valid @RequestParam(value = "applicationNumber", required = false) String applicationNumber) {
//		String accept = request.getHeader("Accept");
//		if (accept != null && accept.contains("application/json")) {
//			try {
//				return new ResponseEntity<AssetResponse>(objectMapper.readValue(
//						"{  \"ResponseInfo\" : {    \"ver\" : \"ver\",    \"resMsgId\" : \"resMsgId\",    \"msgId\" : \"msgId\",    \"apiId\" : \"apiId\",    \"ts\" : 0,    \"status\" : \"SUCCESSFUL\"  },  \"Assets\" : [ {    \"approvalNo\" : \"approvalNo\",    \"businessService\" : \"businessService\",    \"assetBookRefNo\" : \"assetBookRefNo\",    \"approvalDate\" : 6,    \"address\" : {      \"pincode\" : \"pincode\",      \"city\" : \"city\",      \"latitude\" : 5.962133916683182,      \"locality\" : {        \"code\" : \"code\",        \"materializedPath\" : \"materializedPath\",        \"children\" : [ null, null ],        \"latitude\" : \"latitude\",        \"name\" : \"name\",        \"label\" : \"label\",        \"longitude\" : \"longitude\"      },      \"type\" : \"type\",      \"addressId\" : \"addressId\",      \"buildingName\" : \"buildingName\",      \"street\" : \"street\",      \"tenantId\" : \"tenantId\",      \"addressNumber\" : \"addressNumber\",      \"addressLine1\" : \"addressLine1\",      \"addressLine2\" : \"addressLine2\",      \"doorNo\" : \"doorNo\",      \"detail\" : \"detail\",      \"landmark\" : \"landmark\",      \"longitude\" : 5.637376656633329    },    \"parentAssetSpecificDetails\" : {      \"DepriciationRate\" : \"DepriciationRate\",      \"FromWhomDeedTaken\" : \"FromWhomDeedTaken\",      \"OandMTaskDetail\" : \"OandMTaskDetail\",      \"OandMCOI\" : \"OandMCOI\",      \"assetParentCategory\" : \"assetParentCategory\",      \"DateofPossesion\" : \"DateofPossesion\",      \"LandType\" : \"LandType\",      \"CouncilResolutionNumber\" : \"CouncilResolutionNumber\",      \"AnyBuiltup\" : \"AnyBuiltup\",      \"Revenuegeneratedbyasset\" : \"Revenuegeneratedbyasset\",      \"TypeofTrees\" : \"TypeofTrees\",      \"CollectororderNumber\" : \"CollectororderNumber\",      \"howassetbeingused\" : \"howassetbeingused\",      \"Totalcost\" : \"Totalcost\",      \"GovernmentorderNumber\" : \"GovernmentorderNumber\",      \"Costafterdepriciation\" : \"Costafterdepriciation\",      \"assetAssetCategory\" : \"assetAssetCategory\",      \"OSRLand\" : \"OSRLand\",      \"Area\" : \"Area\",      \"BookValue\" : \"BookValue\",      \"AwardNumber\" : \"AwardNumber\",      \"isitfenced\" : \"isitfenced\",      \"assetAssetSubCategory\" : \"assetAssetSubCategory\",      \"Currentassetvalue\" : \"Currentassetvalue\",      \"DateofDeedExecution\" : \"DateofDeedExecution\"    },    \"assetClassification\" : \"assetClassification\",    \"applicationNo\" : \"applicationNo\",    \"description\" : \"description\",    \"additionalDetails\" : \"{}\",    \"applicant\" : \"\",    \"assetParentCategory\" : \"assetParentCategory\",    \"auditDetails\" : {      \"lastModifiedTime\" : 7,      \"createdBy\" : \"createdBy\",      \"lastModifiedBy\" : \"lastModifiedBy\",      \"createdTime\" : 2    },    \"tenantId\" : \"tenantId\",    \"assetName\" : \"assetName\",    \"action\" : \"action\",    \"id\" : \"id\",    \"department\" : \"department\",    \"applicationDate\" : 1,    \"status\" : \"status\"  }, {    \"approvalNo\" : \"approvalNo\",    \"businessService\" : \"businessService\",    \"assetBookRefNo\" : \"assetBookRefNo\",    \"approvalDate\" : 6,    \"address\" : {      \"pincode\" : \"pincode\",      \"city\" : \"city\",      \"latitude\" : 5.962133916683182,      \"locality\" : {        \"code\" : \"code\",        \"materializedPath\" : \"materializedPath\",        \"children\" : [ null, null ],        \"latitude\" : \"latitude\",        \"name\" : \"name\",        \"label\" : \"label\",        \"longitude\" : \"longitude\"      },      \"type\" : \"type\",      \"addressId\" : \"addressId\",      \"buildingName\" : \"buildingName\",      \"street\" : \"street\",      \"tenantId\" : \"tenantId\",      \"addressNumber\" : \"addressNumber\",      \"addressLine1\" : \"addressLine1\",      \"addressLine2\" : \"addressLine2\",      \"doorNo\" : \"doorNo\",      \"detail\" : \"detail\",      \"landmark\" : \"landmark\",      \"longitude\" : 5.637376656633329    },    \"parentAssetSpecificDetails\" : {      \"DepriciationRate\" : \"DepriciationRate\",      \"FromWhomDeedTaken\" : \"FromWhomDeedTaken\",      \"OandMTaskDetail\" : \"OandMTaskDetail\",      \"OandMCOI\" : \"OandMCOI\",      \"assetParentCategory\" : \"assetParentCategory\",      \"DateofPossesion\" : \"DateofPossesion\",      \"LandType\" : \"LandType\",      \"CouncilResolutionNumber\" : \"CouncilResolutionNumber\",      \"AnyBuiltup\" : \"AnyBuiltup\",      \"Revenuegeneratedbyasset\" : \"Revenuegeneratedbyasset\",      \"TypeofTrees\" : \"TypeofTrees\",      \"CollectororderNumber\" : \"CollectororderNumber\",      \"howassetbeingused\" : \"howassetbeingused\",      \"Totalcost\" : \"Totalcost\",      \"GovernmentorderNumber\" : \"GovernmentorderNumber\",      \"Costafterdepriciation\" : \"Costafterdepriciation\",      \"assetAssetCategory\" : \"assetAssetCategory\",      \"OSRLand\" : \"OSRLand\",      \"Area\" : \"Area\",      \"BookValue\" : \"BookValue\",      \"AwardNumber\" : \"AwardNumber\",      \"isitfenced\" : \"isitfenced\",      \"assetAssetSubCategory\" : \"assetAssetSubCategory\",      \"Currentassetvalue\" : \"Currentassetvalue\",      \"DateofDeedExecution\" : \"DateofDeedExecution\"    },    \"assetClassification\" : \"assetClassification\",    \"applicationNo\" : \"applicationNo\",    \"description\" : \"description\",    \"additionalDetails\" : \"{}\",    \"applicant\" : \"\",    \"assetParentCategory\" : \"assetParentCategory\",    \"auditDetails\" : {      \"lastModifiedTime\" : 7,      \"createdBy\" : \"createdBy\",      \"lastModifiedBy\" : \"lastModifiedBy\",      \"createdTime\" : 2    },    \"tenantId\" : \"tenantId\",    \"assetName\" : \"assetName\",    \"action\" : \"action\",    \"id\" : \"id\",    \"department\" : \"department\",    \"applicationDate\" : 1,    \"status\" : \"status\"  } ]}",
//						AssetResponse.class), HttpStatus.NOT_IMPLEMENTED);
//			} catch (IOException e) {
//				return new ResponseEntity<AssetResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		}
//
//		return new ResponseEntity<AssetResponse>(HttpStatus.NOT_IMPLEMENTED);
//	}
	
	@RequestMapping(value = "/v1/assets/_search", method = RequestMethod.POST)
	public ResponseEntity<AssetResponse> v1AssetsSearchPost(
			@RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute AssetSearchCriteria searchCriteria) {
		List<Asset> assets = assetService.search(searchCriteria, requestInfoWrapper.getRequestInfo());
		AssetResponse response = AssetResponse.builder().assets(assets)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return  new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/v1/assets/_update", method = RequestMethod.POST)
	public ResponseEntity<AssetResponse> v1AssetsUpdatePost(
			@ApiParam(value = "Details for updating existing assets + RequestInfo metadata.", required = true) @Valid @RequestBody AssetRequest assetRequest) {
		Asset asset = assetService.update(assetRequest);
		List<Asset> assets = new ArrayList<Asset>();
		assets.add(asset);
		AssetResponse response = AssetResponse.builder().assets(assets)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(assetRequest.getRequestInfo(), true))
				.build();
		return  new ResponseEntity<>(response, HttpStatus.OK);
	}

}
