package org.egov.pt.dashboardservice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.pt.models.WardwithTanent;
import org.egov.pt.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

	@Autowired
	PropertyRepository propertyRepository;

	public Map<String, String> wardwithtanentlist() {
		List<WardwithTanent> wardwithTanent = propertyRepository.getTotalapplicationwithward();
		Map<String, String> wardwithtenatMap = wardwithTanent.stream().collect(
				Collectors.toMap(W -> W.getWardNo() + "-" + W.getTanentid(), V -> String.valueOf(V.getCount())));
		return wardwithtenatMap;
	}

	public Map<String, String> wardwithAssesment() {
		List<WardwithTanent> wardwithTanentassesmet = propertyRepository.getTotalapplicationwitAssessment();
		Map<String, String> wardwithtenatasmtMap = wardwithTanentassesmet.stream().collect(
				Collectors.toMap(W -> W.getWardNo() + "-" + W.getTanentid(), V -> String.valueOf(V.getCount())));
		return wardwithtenatasmtMap;
	}

	public Map<String, String> wardwithClosedcount() {
		List<WardwithTanent> wardwithTanentclosed = propertyRepository.getTotalapplicationwitClosed();
		Map<String, String> wardwithtenatclosedMap = wardwithTanentclosed.stream().collect(
				Collectors.toMap(W -> W.getWardNo() + "-" + W.getTanentid(), V -> String.valueOf(V.getCount())));
		return wardwithtenatclosedMap;
	}

	public Map<String, String> wardwithPaidcount() {
		List<WardwithTanent> wardwithTanentpaid = propertyRepository.getTotalapplicationApproved();
		Map<String, String> wardwithtenatpaidMap = wardwithTanentpaid.stream().collect(
				Collectors.toMap(W -> W.getWardNo() + "-" + W.getTanentid(), V -> String.valueOf(V.getCount())));
		return wardwithtenatpaidMap;
	}
	
	public Map<String, String> wardwithApprovedcount() {
		List<WardwithTanent> wardwithTanentapproved = propertyRepository.getTotalapplicationwithPaid();
		Map<String, String> wardwithtenatapprovedMap = wardwithTanentapproved.stream().collect(
				Collectors.toMap(W -> W.getWardNo() + "-" + W.getTanentid(), V -> String.valueOf(V.getCount())));
		return wardwithtenatapprovedMap;
	}

	public Map<String, String> wardwithMovedcount() {
		List<WardwithTanent> wardwithTanentmoved = propertyRepository.getTotalapplicationwithMoved();
		Map<String, String> wardwithtenatmovedMap = wardwithTanentmoved.stream()
				.collect(Collectors.toMap(w -> w.getWardNo() + "-" + w.getTanentid(),
						v -> v.getAction() + ":" + v.getCount(),
						(existing, replacement) -> existing + "," + replacement));

		return wardwithtenatmovedMap;
	}

	public Map<String, String> wardwithpropertyRegistered() {
		List<WardwithTanent> wardwithTanentpropertyregistered = propertyRepository.getTotalpropertyRegistered();
		Map<String, String> wardwithTanentpropertyregisteredMap = wardwithTanentpropertyregistered.stream()
				.collect(Collectors.toMap(w -> w.getWardNo() + "-" + w.getTanentid(),
						v -> v.getFinanciyalyear() + ":" + v.getCount(),
						(existing, replacement) -> existing + "," + replacement));
		return wardwithTanentpropertyregisteredMap;
	}
	
	public Map<String, String> wardwithpropertyAssed() {
		List<WardwithTanent> wardwithTanentpropertyAssed = propertyRepository.getTotalAssedproperties();
		Map<String, String> wardwithTanentpropertyassedMap = wardwithTanentpropertyAssed.stream()
				.collect(Collectors.toMap(w -> w.getWardNo() + "-" + w.getTanentid(),
						v -> v.getUsagecategory() + ":" + v.getCount(),
						(existing, replacement) -> existing + "," + replacement));
		return wardwithTanentpropertyassedMap;
	}
	
	public Map<String, String> wardwithTransactioncount() {
		List<WardwithTanent> wardwithTanentTransaction = propertyRepository.getTotaltransactionCount();
		Map<String, String> wardwithTanentTransactionMap = wardwithTanentTransaction.stream()
				.collect(Collectors.toMap(w -> w.getWardNo() + "-" + w.getTanentid(),
						v -> v.getUsagecategory() + ":" + v.getCount(),
						(existing, replacement) -> existing + "," + replacement));
		return wardwithTanentTransactionMap;
	}
	
	public Map<String, String> wardwithtodaysCollection() {
		List<WardwithTanent> wardwithTanenttotalCollection= propertyRepository.getTotaltodaysCollection();
		Map<String, String> wardwithTanenttodayscollectionMap = wardwithTanenttotalCollection.stream()
				.collect(Collectors.toMap(w -> w.getWardNo() + "-" + w.getTanentid(),
						v -> v.getUsagecategory() + ":" + v.getTodaysCollection(),
						(existing, replacement) -> existing + "," + replacement));
		return wardwithTanenttodayscollectionMap;
	}
	
	public Map<String, String> wardwithpropertyCount() {
		List<WardwithTanent> wardwithTanentpropertyCount= propertyRepository.getTotalpropertyCount();
		Map<String, String> wardwithTanentpropertycountMap = wardwithTanentpropertyCount.stream()
				.collect(Collectors.toMap(w -> w.getWardNo() + "-" + w.getTanentid(),
						v -> v.getUsagecategory() + ":" + v.getCount(),
						(existing, replacement) -> existing + "," + replacement));
		return wardwithTanentpropertycountMap;
	}
	
	public Map<String, String> wardwithrebategiven() {
	    List<WardwithTanent> wardwithTanentrebategiven = propertyRepository.getTotalrebateCollection();
	    Map<String, String> wardwithTanentrebategivenMap = wardwithTanentrebategiven.stream()
	    		.collect(Collectors.toMap(w -> w.getWardNo() + "-" + w.getTanentid(),
						v -> v.getUsagecategory() + ":" + v.getTodayrebategiven().negate(),
						(existing, replacement) -> existing + "," + replacement));
	    return wardwithTanentrebategivenMap;
	}
	
	public Map<String, String> wardwithpenaltyCollected() {
	    List<WardwithTanent> wardwithTanenpenaltyCollected = propertyRepository.getTotalpenaltyCollection();
	    Map<String, String> wardwithTanentpenaltycollectedMap = wardwithTanenpenaltyCollected.stream()
	    		.collect(Collectors.toMap(w -> w.getWardNo() + "-" + w.getTanentid(),
						v -> v.getUsagecategory() + ":" + v.getTodaypenaltycollection(),
						(existing, replacement) -> existing + "," + replacement));
	    return wardwithTanentpenaltycollectedMap;
	}
	
	public Map<String, String> wardwithinterestCollected() {
	    List<WardwithTanent> wardwithTanentinterestCollected = propertyRepository.getTotalinterestCollection();
	    Map<String, String> wardwithTanentinterestcollectedMap = wardwithTanentinterestCollected.stream()
	    		.collect(Collectors.toMap(w -> w.getWardNo() + "-" + w.getTanentid(),
						v -> v.getUsagecategory() + ":" + v.getTodayinterestcollection(),
						(existing, replacement) -> existing + "," + replacement));
	    return wardwithTanentinterestcollectedMap;
	}

}
