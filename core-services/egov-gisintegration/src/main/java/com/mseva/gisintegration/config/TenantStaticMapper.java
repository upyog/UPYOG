package com.mseva.gisintegration.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TenantStaticMapper {

    private static final Map<String, String> TOWN_MAP;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("pb.abohar", "ABOHAR");
        map.put("pb.adampur", "ADAMPUR");
        map.put("pb.ahmedgarh", "AHMEDGARH");
        map.put("pb.ajnala", "AJNALA");
        map.put("pb.alawalpur", "ALAWALPUR");
        map.put("pb.amargarh", "AMARGARH");
        map.put("pb.amloh", "AMLOH");
        map.put("pb.amritsar", "AMRITSAR");
        map.put("pb.anandpursahib", "ANANDPUR SAHIB");
        map.put("pb.arniwala", "ARNIWALA SHEIKH SUBHAN");
        map.put("pb.bababakalasahib", "BABA BAKALA");
        map.put("pb.badhnikalan", "BADANI KALA");
        map.put("pb.baghapurana", "BAGHA PURANA");
        map.put("pb.balachaur", "BALACHAUR");
        map.put("pb.banga", "BANGA");
        map.put("pb.banur", "BANUR");
        map.put("pb.bareta", "BARETA");
        map.put("pb.bariwala", "BARIWALA");
        map.put("pb.barnala", "BARNALA");
        map.put("pb.bassipathana", "BASSI PATHANA");
        map.put("pb.batala", "BATALA");
        map.put("pb.bathinda", "BATHINDA");
        map.put("pb.begowal", "BEGOWAL");
        map.put("pb.bhadaur", "BHADAUR");
        map.put("pb.bhadson", "BHADSON");
        map.put("pb.bhagtabhai", "BHAGTA BHAI KA");
        map.put("pb.bhairoopa", "BHAI RUPA");
        map.put("pb.bhawanigarh", "BHAWANIGARH");
        map.put("pb.bhikhi", "BHIKHI");
        map.put("pb.bhikhiwind", "BHIKHIWIND");
        map.put("pb.bhogpur", "BHOGPUR");
        map.put("pb.bhuchomandi", "BHUCHO MANDI");
        map.put("pb.bhulath", "BHULATH");
        map.put("pb.bilga", "BILGA");
        map.put("pb.boha", "BOHA");
        map.put("pb.budhlada", "BUDHLADA");
        map.put("pb.chamkaursahib", "CHAMKAUR SAHIB");
        map.put("pb.cheema", "CHEEMA");
        map.put("pb.dasuya", "DASUYA");
        map.put("pb.derababananak", "DERA BABA NANAK");
        map.put("pb.derabassi", "DERA BASSI");
        map.put("pb.devigarh", "DEVIGARH");
        map.put("pb.dhanaula", "DHANAULA");
        map.put("pb.dharamkot", "DHARAMKOT");
        map.put("pb.dhariwal", "DHARIWAL");
        map.put("pb.dhilwan", "DHILWAN");
        map.put("pb.dhuri", "DHURI");
        map.put("pb.dinanagar", "DINANAGAR");
        map.put("pb.dirba", "DIRBA");
        map.put("pb.doraha", "DORAHA");
        map.put("pb.faridkot", "FARIDKOT");
        map.put("pb.fatehgarhchurian", "FATEHGARH CHURIAN");
        map.put("pb.fatehgarhpanjtoor", "FATEHGARH PANJTOOR");
        map.put("pb.fazilka", "FAZILKA");
        map.put("pb.ferozepur", "FEROZEPUR");
        map.put("pb.garhshankar", "GARH SHANKAR");
        map.put("pb.garhdiwala", "GARHDIWALA");
        map.put("pb.ghagga", "GHAGGA");
        map.put("pb.ghanaur", "GHANAUR");
        map.put("pb.gidderbaha", "GIDDERBAHA");
        map.put("pb.mandigobindgarh", "GOBINDGARH");
        map.put("pb.goniana", "GONIANA");
        map.put("pb.goraya", "GORAYA");
        map.put("pb.gurdaspur", "GURDASPUR");
        map.put("pb.guruharsahai", "GURU HAR SAHAI");
        map.put("pb.handiaya", "HANDIAYA");
        map.put("pb.hariana", "HARIANA");
        map.put("pb.hoshiarpur", "HOSHIARPUR");
        map.put("pb.jagraon", "JAGRAON");
        map.put("pb.jaitu", "JAITU");
        map.put("pb.jalalabad", "JALALABAD");
        map.put("pb.jalandhar", "JALANDHAR");
        map.put("pb.jandialaguru", "JANDIALA GURU");
        map.put("pb.joga", "JOGA");
        map.put("pb.kartarpur", "KARTARPUR");
        map.put("pb.khamano", "KHAMANO");
        map.put("pb.khanauri", "KHANAURI");
        map.put("pb.khanna", "KHANNA");
        map.put("pb.kharar", "KHARAR");
        map.put("pb.khemkaran", "KHEMKARAN");
        map.put("pb.kiratpursahib", "KIRATPUR SAHIB");
        map.put("pb.kotissekhan", "KOT ISSE KHAN");
        map.put("pb.kotshamir", "KOT SHAMIR");
        map.put("pb.kotfatta", "KOTFATTA");
        map.put("pb.kothaguru", "KOTHA GURU");
        map.put("pb.kotkapura", "KOTKAPURA");
        map.put("pb.kurali", "KURALI");
        map.put("pb.lalru", "LALRU");
        map.put("pb.lehramohabbat", "LEHRA MOHABBAT");
        map.put("pb.lehragaga", "LEHRAGAGA");
        map.put("pb.lohiankhas", "LOHIAN KHAS");
        map.put("pb.longowal", "LONGOWAL");
        map.put("pb.ludhiana", "LUDHIANA");
        map.put("pb.machhiwara", "MACHHIWARA");
        map.put("pb.mahilpur", "MAHILPUR");
        map.put("pb.majitha", "MAJITHA");
        map.put("pb.makhu", "MAKHU");
        map.put("pb.malerkotla", "MALERKOTLA");
        map.put("pb.mallanwala", "MALLANWALA KHAS");
        map.put("pb.maloud", "MALOUD");
        map.put("pb.malout", "MALOUT");
        map.put("pb.maluka", "MALUKA");
        map.put("pb.mamdot", "MAMDOT");
        map.put("pb.mansa", "MANSA");
        map.put("pb.maur", "MAUR");
        map.put("pb.mehatpur", "MEHATPUR");
        map.put("pb.mehraj", "MEHRAJ");
        map.put("pb.moga", "MOGA");
        map.put("pb.moonak", "MOONAK");
        map.put("pb.morinda", "MORINDA");
        map.put("pb.mudki", "MUDKI");
        map.put("pb.mukerian", "MUKERIAN");
        map.put("pb.mullanpur", "MULLANPUR DAKHA");
        map.put("pb.nabha", "NABHA");
        map.put("pb.nadala", "NADALA");
        map.put("pb.nakodar", "NAKODAR");
        map.put("pb.nangal", "NANGAL");
        map.put("pb.narotjaimalsingh", "NAROT JAIMAL SINGH");
        map.put("pb.nathana", "NATHANA");
        map.put("pb.nawanshahr", "NAWAN SHAHAR");
        map.put("pb.nayagaon", "NAYAN GAON");
        map.put("pb.nihalsinghwala", "NIHAL SINGH WALA");
        map.put("pb.nurmahal", "NURMAHAL");
        map.put("pb.pathankot", "PATHANKOT");
        map.put("pb.patiala", "PATIALA");
        map.put("pb.patran", "PATRAN");
        map.put("pb.patti", "PATTI");
        map.put("pb.payal", "PAYAL");
        map.put("pb.phagwara", "PHAGWARA");
        map.put("pb.phillaur", "PHILLAUR");
        map.put("pb.quadian", "QADIAN");
        map.put("pb.rahon", "RAHON");
        map.put("pb.raikot", "RAIKOT");
        map.put("pb.rajasansi", "RAJA SANSI");
        map.put("pb.rajpura", "RAJPURA");
        map.put("pb.raman", "RAMAN MANDI");
        map.put("pb.ramdass", "RAMDASS");
        map.put("pb.rampuraphul", "RAMPURA PHUL");
        map.put("pb.rayya", "RAYYA");
        map.put("pb.ropar", "ROOP NAGAR");
        map.put("pb.mohali", "S.A.S. NAGAR");
        map.put("pb.sahnewal", "SAHNEWAL");
        map.put("pb.samana", "SAMANA");
        map.put("pb.samrala", "SAMRALA");
        map.put("pb.sanaur", "SANAUR");
        map.put("pb.sangatmandi", "SANGAT MANDI");
        map.put("pb.sangrur", "SANGRUR");
        map.put("pb.sardulgarh", "SARDULGARH");
        map.put("pb.shahkot", "SHAHKOT");
        map.put("pb.shamchurasi", "SHAM CHURASI");
        map.put("pb.sirhind", "SIRHIND FATEHGARH SAHIB");
        map.put("pb.srihargobindpur", "SRI HARGOBINDPUR");
        map.put("pb.muktsar", "SRI MUKTSAR SAHIB");
        map.put("pb.sujanpur", "SUJANPUR");
        map.put("pb.sultanpurlodhi", "SULTANPUR LODHI");
        map.put("pb.sunam", "SUNAM");
        map.put("pb.talwandibhai", "TALWANDI BHAI");
        map.put("pb.talwandisabo", "TALWANDI SABO");
        map.put("pb.talwara", "TALWARA");
        map.put("pb.tapa", "TAPA");
        map.put("pb.urmartanda", "URMAR TANDA");
        map.put("pb.zira", "ZIRA");
        map.put("pb.zirakpur", "ZIRAKPUR");
        map.put("pb.testing", "TESTING");
        
        TOWN_MAP = Collections.unmodifiableMap(map);
    }
    public static String getTenantIdByTown(String townName) {
        if (townName == null) return null;

        return TOWN_MAP.entrySet()
                .stream()
                .filter(entry -> townName.equalsIgnoreCase(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(townName); // Returns the input if no match is found
    }
    public static String getTownName(String tenantId) {
        if (tenantId == null) return "Unknown";
        // Returns the town name or falls back to tenantId if not found
        return TOWN_MAP.getOrDefault(tenantId, tenantId);
    }
}