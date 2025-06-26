package org.egov.finance.voucher.service;

import java.io.Serializable;
import java.sql.SQLException;

import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GenericSequenceNumberGenerator {

	private static final String DISALLOWED_CHARACTERS = "[\\/ -]";
	private static final String UNDERSCORE = "_";

	@Autowired
	private DatabaseSequenceCreator databaseSequenceCreator;

	@Autowired
	private DatabaseSequenceProvider databaseSequenceProvider;

	@Transactional
	public Serializable getNextSequence(String sequenceName) {
		// Normalize sequence name by removing disallowed characters
		String normalizedSequenceName = sequenceName.replaceAll(DISALLOWED_CHARACTERS, UNDERSCORE);

		try {
			return databaseSequenceProvider.getNextSequence(normalizedSequenceName);
		} catch (Exception e) {
			
			// Log warning that sequence is missing and being created
			log.info("Sequence [%s] not found. Creating it dynamically...\n", normalizedSequenceName);
			databaseSequenceCreator.createSequence(normalizedSequenceName);

			// Retry fetching the sequence
			return databaseSequenceProvider.getNextSequence(normalizedSequenceName);
		}
	}
}
