package org.egov.refund.Repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.refund.model.Bill;
import org.egov.refund.querybuilder.PaymentQueryBuilder;
import org.egov.refund.rowmapper.BillRowMapper;
import org.egov.refund.rowmapper.PaymentRowMapper;
import org.egov.refund.web.contracat.Payment;
import org.egov.refund.web.contracat.PaymentSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import static java.util.Collections.reverseOrder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PaymentRepository {

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private PaymentQueryBuilder paymentQueryBuilder;

	private PaymentRowMapper paymentRowMapper;

	private BillRowMapper billRowMapper;

	@Autowired
	public PaymentRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
			PaymentQueryBuilder paymentQueryBuilder, PaymentRowMapper paymentRowMapper) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.paymentQueryBuilder = paymentQueryBuilder;
		this.paymentRowMapper = paymentRowMapper;
	}

	public List<Payment> fetchPayments(PaymentSearchCriteria paymentSearchCriteria) {
		Map<String, Object> preparedStatementValues = new HashMap<>();

		List<String> ids = fetchPaymentIdsByCriteria(paymentSearchCriteria);

		if (CollectionUtils.isEmpty(ids))
			return new LinkedList<>();

		String query = paymentQueryBuilder.getPaymentSearchQuery(ids, preparedStatementValues);
		log.info("Query: " + query);
		log.info("preparedStatementValues: " + preparedStatementValues);
		List<Payment> payments = namedParameterJdbcTemplate.query(query, preparedStatementValues, paymentRowMapper);
		if (!CollectionUtils.isEmpty(payments)) {
			Set<String> billIds = new HashSet<>();
			for (Payment payment : payments) {
				billIds.addAll(payment.getPaymentDetails().stream().map(detail -> detail.getBillId())
						.collect(Collectors.toSet()));
			}
			Map<String, Bill> billMap = getBills(billIds);
			for (Payment payment : payments) {
				payment.getPaymentDetails().forEach(detail -> {
					detail.setBill(billMap.get(detail.getBillId()));
				});
			}
			payments.sort(reverseOrder(Comparator.comparingLong(Payment::getTransactionDate)));
		}

		return payments;
	}

	private Map<String, Bill> getBills(Set<String> ids) {
		Map<String, Bill> mapOfIdAndBills = new HashMap<>();
		Map<String, Object> preparedStatementValues = new HashMap<>();
		preparedStatementValues.put("id", ids);
		String query = paymentQueryBuilder.getBillQuery();
		List<Bill> bills = namedParameterJdbcTemplate.query(query, preparedStatementValues, billRowMapper);
		bills.forEach(bill -> {
			mapOfIdAndBills.put(bill.getId(), bill);
		});

		return mapOfIdAndBills;

	}

	public List<String> fetchPaymentIdsByCriteria(PaymentSearchCriteria paymentSearchCriteria) {
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String query = paymentQueryBuilder.getIdQuery(paymentSearchCriteria, preparedStatementValues);
		return namedParameterJdbcTemplate.query(query, preparedStatementValues,
				new SingleColumnRowMapper<>(String.class));
	}

}