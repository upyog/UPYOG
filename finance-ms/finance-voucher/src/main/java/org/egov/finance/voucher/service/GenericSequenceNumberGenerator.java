package org.egov.finance.voucher.service;

import java.io.Serializable;

import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
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
		} catch (SQLGrammarException e) {
			// Log warning that sequence is missing and being created
			System.out.printf("Sequence [%s] not found. Creating it dynamically...\n", normalizedSequenceName);
			databaseSequenceCreator.createSequence(normalizedSequenceName);

			// Retry fetching the sequence
			return databaseSequenceProvider.getNextSequence(normalizedSequenceName);
		}
	}
}
