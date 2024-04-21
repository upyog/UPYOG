package org.egov.pt.calculator.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.egov.pt.calculator.producer.Producer;
import org.egov.pt.calculator.repository.AdoptionRepository;
import org.egov.pt.calculator.util.Configurations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdoptionService {

	@Autowired
	public AdoptionRepository adoptionRepository;

	@Autowired
	public Configurations configurations;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Producer producer;

	public void chatbotdailyreport(boolean isTotalReport, int daysForIncrement) {
		if(isTotalReport) 
			generateTotalReport();
		else 
			generateTodaysReport(daysForIncrement);

	}

	@Transactional(readOnly = true)
	public void generateTotalReport() {
		List<String> totalProperties = adoptionRepository.getTotalPropertiesCount();
		int limit = 500;
		Collection<List<String>> partitionConectionNoList = partitionBasedOnSize(totalProperties, limit);

		for (List<String> propertiesList : partitionConectionNoList) {
			pushAssessmentDataTokafka(propertiesList, 0);
			pushPaymentDataTokafka(propertiesList, 0);

		}

	}

	@Transactional(readOnly = true)
	public void generateTodaysReport(int daysForIncrement) {
		
		daysForIncrement = daysForIncrement < 1 ? 1 : daysForIncrement;

		List<String> propertiesData = adoptionRepository.getPropertiesFromAssessmentJob(daysForIncrement);
		int limit = 500;
		Collection<List<String>> partitionConectionNoList = partitionBasedOnSize(propertiesData, limit);

		for (List<String> propertiesList : partitionConectionNoList) {
			pushAssessmentDataTokafka(propertiesList, daysForIncrement);
			pushPaymentDataTokafka(propertiesList, daysForIncrement);

		}
	}
	public void pushAssessmentDataTokafka(List<String> propertiesList, int daysForIncrement) {
		List<String> adoptionData = adoptionRepository.generateAssessmentReport(propertiesList, daysForIncrement);

		List<Map> mapData = new LinkedList<Map>();

		adoptionData.stream().forEach(data -> {
			try {
				mapData.add(objectMapper.readValue(data, Map.class));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		producer.push(configurations.getKafkaWhatsappAdoptionDataTopic(), mapData);
	}
	
	public void pushPaymentDataTokafka(List<String> propertiesList, int daysForIncrement) {

		List<String> adoptionData = adoptionRepository.generatePaymentReport(propertiesList, daysForIncrement);

		List<Map> mapData = new LinkedList<Map>();

		adoptionData.stream().forEach(data -> {
			try {
				mapData.add(objectMapper.readValue(data, Map.class));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		producer.push(configurations.getKafkaWhatsappAdoptionDataTopic(), mapData);
	}

	static <T> Collection<List<T>> partitionBasedOnSize(List<T> inputList, int size) {
		final AtomicInteger counter = new AtomicInteger(0);
		return inputList.stream()
				.collect(Collectors.groupingBy(s -> counter.getAndIncrement()/size))
				.values();
	}




}
