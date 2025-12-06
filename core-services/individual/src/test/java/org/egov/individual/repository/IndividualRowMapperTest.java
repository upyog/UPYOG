package org.egov.individual.repository;

import org.egov.individual.repository.rowmapper.AddressRowMapper;
import org.egov.individual.repository.rowmapper.IdentifierRowMapper;
import org.egov.individual.repository.rowmapper.IndividualRowMapper;
import org.egov.individual.repository.rowmapper.SkillRowMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class IndividualRowMapperTest {

    @InjectMocks
    private AddressRowMapper addressRowMapper;

    @InjectMocks
    private IdentifierRowMapper identifierRowMapper;

    @InjectMocks
    private SkillRowMapper skillRowMapper;

    @InjectMocks
    private IndividualRowMapper individualRowMapper;

    @Test
    void shouldBeAbleToMapIdRowOfAddress() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        lenient().when(resultSet.getString("id")).thenReturn("some-Id");
        assertEquals(addressRowMapper.mapRow(resultSet, 1).getId(), "some-Id");
    }

    @Test
    void shouldBeAbleToMapIdRowOfIdentifier() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        lenient().when(resultSet.getString("id")).thenReturn("some-id");
        assertEquals(identifierRowMapper.mapRow(resultSet, 1).getId(), "some-id");
    }

    @Test
    void shouldBeAbleToMapIdRowOfSkill() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        lenient().when(resultSet.getString("id")).thenReturn("some-id");
        assertEquals(skillRowMapper.mapRow(resultSet, 1).getId(), "some-id");

    }

    @Test
    void shouldBeAbleToMapIdRowOfIndividual() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        lenient().when(resultSet.getString("id")).thenReturn("some-id");
        assertEquals(individualRowMapper.mapRow(resultSet, 1).getId(), "some-id");

    }
}
