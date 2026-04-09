package com.mseva.gisintegration.repository;

import com.mseva.gisintegration.model.WaterTax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaterTaxRepository extends JpaRepository<WaterTax, Integer> {

    List<WaterTax> findByPropertyid(String propertyid);

    List<WaterTax> findBySurveyid(String surveyid);

    List<WaterTax> findBySurveyidAndPropertyid(String surveyid, String propertyid);

    List<WaterTax> findByConnectionno(String connectionno);

}
