package com.mseva.gisintegration.repository;

import com.mseva.gisintegration.model.SewerageTax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SewerageTaxRepository extends JpaRepository<SewerageTax, Integer> {

    List<SewerageTax> findByPropertyid(String propertyid);

    List<SewerageTax> findBySurveyid(String surveyid);

    List<SewerageTax> findBySurveyidAndPropertyid(String surveyid, String propertyid);

    List<SewerageTax> findByConnectionno(String connectionno);

}
