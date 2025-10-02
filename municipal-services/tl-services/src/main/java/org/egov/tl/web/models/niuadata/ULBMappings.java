package org.egov.tl.web.models.niuadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ULBMappings {

	private static final Map<String, String> MUNICIPALITIES;

	static {
		Map<String, String> map = new HashMap<>();

		map.put("Shimla", "hp.municipalcorporationshimla");
		map.put("Rohru", "hp.municipalcouncilrohroo");
		map.put("Rampur", "hp.municipalcouncilrampur");
		map.put("Theog", "hp.municipalcounciltheog");
		map.put("Narkanda", "hp.nagarpanchayatnarkanda");
		map.put("Chopal", "hp.nagarpanchayatchopal");
		map.put("Kotkhai", "hp.nagarpanchayatkotkhai");
		map.put("Jubbal", "hp.nagarpanchayatjubbal");
		map.put("Sunni", "hp.nagarpanchayatsunni");
		map.put("Chirgaon", "hp.nagarpanchayatchirgaon");
		map.put("Nerwa", "hp.nagarpanchayatnerwa");
		map.put("Solan", "hp.municipalcorporationsolan");
		map.put("Nalagarh", "hp.municipalcouncilnalagarh");
		map.put("Parwanoo", "hp.municipalcouncilparwanoo");
		map.put("Baddi", "hp.municipalcouncilbaddi");
		map.put("Kandaghat", "hp.nagarpanchayatkandaghat");
		map.put("Arki", "hp.nagarpanchayatarki");
		map.put("Nahan", "hp.municipalcouncilnahan");
		map.put("PoantaSahib", "hp.municipalcouncilpaonta");
		map.put("Rajgarh", "hp.nagarpanchayatrajgarh");
		map.put("Hamirpur", "hp.municipalcouncilhamirpur");
		map.put("Nadaun", "hp.nagarpanchayatnadaun");
		map.put("Sujanpur", "hp.municipalcouncilsujanpur-tihra");
		map.put("Bhota", "hp.nagarpanchayatbhotta");
		map.put("Dharamshala", "hp.municipalcorporationdharamshala");
		map.put("Palampur", "hp.municipalcorporationpalampur");
		map.put("Kangra", "hp.municipalcouncilkangra");
		map.put("Nurpur", "hp.municipalcouncilnurpur");
		map.put("Nagrota", "hp.municipalcouncilnagrota-bagwan");
		map.put("Jawalamkuhi", "hp.municipalcounciljawalamkuhi");
		map.put("Dehra", "hp.municipalcouncildehra");
		map.put("BaijnathPaprola", "hp.nagarpanchayatbaijnath-paprola");
		map.put("Jawali", "hp.nagarpanchayatjawali");
		map.put("Shahpur", "hp.nagarpanchayatshahpur");
		map.put("Bilaspur", "hp.municipalcouncilbilaspur");
		map.put("Nainadeviji", "hp.municipalcouncilnainadeviji");
		map.put("Ghumarwin", "hp.municipalcouncilghumarwin");
		map.put("Talai", "hp.nagarpanchayattalai");
		map.put("Chamba", "hp.municipalcouncilchamba");
		map.put("Dalhousie", "hp.municipalcouncildalhousie");
		map.put("Chowari", "hp.nagarpanchayatchowari");
		map.put("Mandi", "hp.municipalcorporationmandi");
		map.put("Sundernagar", "hp.municipalcouncilsundernagar");
		map.put("NerChowk", "hp.municipalcouncilnerchowk");
		map.put("Jogindernagar", "hp.municipalcounciljogindernagar");
		map.put("Sarkaghat", "hp.municipalcouncilsarkaghat");
		map.put("Rewalsar", "hp.nagarpanchayatrewalsar");
		map.put("Karsog", "hp.nagarpanchayatkarsog");
		map.put("Kullu", "hp.municipalcouncilkullu");
		map.put("Manali", "hp.municipalcouncilmanali");
		map.put("Bhuntar", "hp.nagarpanchayatbhunter");
		map.put("Banjar", "hp.nagarpanchayatbanjar");
		map.put("Nirmand", "hp.nagarpanchayatnirmand");
		map.put("Una", "hp.municipalcounciluna");
		map.put("Santokhgarh", "hp.municipalcouncilsantokhgarh");
		map.put("MehatpurBasdehr", "hp.municipalcouncilmehatpurbasdehra");
		map.put("Daulatpur", "hp.nagarpanchayatdaulatpurchowk");
		map.put("Gagret", "hp.nagarpanchayatgagret");
		map.put("Tahliwal", "hp.nagarpanchayattahliwal");
		map.put("Amb", "hp.nagarpanchayatamb");
		map.put("Jawalamukhi", "hp.municipalcounciljawalamkuhi");

		MUNICIPALITIES = Collections.unmodifiableMap(map);
	}

	public String getCode(String name) {
		return MUNICIPALITIES.get(name);
	}

	public Map<String, String> getAll() {
		return MUNICIPALITIES;
	}

}
