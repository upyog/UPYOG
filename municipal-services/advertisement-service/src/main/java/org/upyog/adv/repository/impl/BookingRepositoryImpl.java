package org.upyog.adv.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.kafka.Producer;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.web.models.BookingRequest;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BookingRepositoryImpl implements BookingRepository {

	@Autowired
	private Producer producer;
	
	@Autowired
	private BookingConfiguration bookingConfiguration;
//	@Autowired
//	private CommunityHallBookingQueryBuilder queryBuilder;
//	@Autowired
//	private CommunityHallBookingRowmapper bookingRowmapper;
//	@Autowired
//	private BookingSlotDetailRowmapper slotDetailRowmapper;
//	
//	@Autowired
//	private DocumentDetailsRowMapper detailsRowMapper;
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//	@Autowired
//	private CommunityHallSlotAvailabilityRowMapper availabilityRowMapper;

	@Override
	public void saveBooking(BookingRequest bookingRequest) {
		log.info("Saving Advertisement booking request data for booking no : " + bookingRequest.getBookingApplication().getBookingNo());
		producer.push(bookingConfiguration.getAdvertisementBookingSaveTopic(), bookingRequest);

	}

//	@Override
//	public void saveCommunityHallBookingInit(CommunityHallBookingRequest bookingRequest) {
//		log.info("Saving community hall booking init data : " + bookingRequest.getHallsBookingApplication().getBookingId());
//		RequestInfo requestInfo = bookingRequest.getRequestInfo();
//		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
//		CommunityHallBookingRequestInit testPersist = CommunityHallBookingRequestInit.builder()
//				.bookingId(bookingDetail.getBookingId()).tenantId(bookingDetail.getTenantId())
//				.bookingStatus(bookingDetail.getBookingStatus())
//				.bookingDetails(bookingRequest.getHallsBookingApplication()).createdBy(requestInfo.getUserInfo().getUuid())
//				.createdDate(CommunityHallBookingUtil.getCurrentTimestamp()).lastModifiedBy(requestInfo.getUserInfo().getUuid())
//				.lastModifiedDate(CommunityHallBookingUtil.getCurrentTimestamp()).build();
//		CommunityHallBookingInitDetail bookingPersiter = CommunityHallBookingInitDetail.builder()
//				.hallsBookingApplication(testPersist).build();
//		producer.push(bookingConfiguration.getCommunityHallBookingInitSaveTopic(), bookingPersiter);
//
//	}
//
//	@Override
//	public List<CommunityHallBookingDetail> getBookingDetails(
//			CommunityHallBookingSearchCriteria bookingSearchCriteria) {
//		List<Object> preparedStmtList = new ArrayList<>();
//		String query = queryBuilder.getCommunityHallBookingSearchQuery(bookingSearchCriteria, preparedStmtList);
//
//		log.info("getBookingDetails : Final query: " + query );
//		log.info("preparedStmtList :  " + preparedStmtList);
//		List<CommunityHallBookingDetail> bookingDetails = jdbcTemplate.query(query, preparedStmtList.toArray(),
//				bookingRowmapper);
//		
//		log.info("Fetched booking details size : " + bookingDetails.size());
//		
//		if(bookingDetails.size() == 0) {
//			return bookingDetails; 
//		}
//		
//		HashMap<String , CommunityHallBookingDetail> bookingMap =  bookingDetails.stream().collect(Collectors.toMap(CommunityHallBookingDetail::getBookingId,
//	           Function.identity(),
//	            (left, right) -> left,
//	            HashMap::new));
//		
//		List<String> bookingIds = new ArrayList<String>();
//		bookingIds.addAll(bookingMap.keySet());
//		
//		List<BookingSlotDetail> slotDetails = jdbcTemplate.
//				query(queryBuilder.getSlotDetailsQuery(bookingIds), bookingIds.toArray(), slotDetailRowmapper);
//		slotDetails.stream().forEach(slotDetail -> {
//			bookingMap.get(slotDetail.getBookingId()).addBookingSlots(slotDetail);
//		});
//		
//		List<DocumentDetail> documentDetails = jdbcTemplate.query(queryBuilder.getDocumentDetailsQuery(bookingIds), bookingIds.toArray(),
//				detailsRowMapper);
//		
//		documentDetails.stream().forEach(documentDetail -> {
//			bookingMap.get(documentDetail.getBookingId()).addUploadedDocumentDetailsItem(documentDetail);
//		});
//		return bookingDetails;
//	}
//	
//	@Override
//	public Integer getBookingCount(@Valid CommunityHallBookingSearchCriteria criteria) {
//		List<Object> preparedStatement = new ArrayList<>();
//		String query = queryBuilder.getCommunityHallBookingSearchQuery(criteria, preparedStatement);
//		
//		if (query == null)
//			return 0;
//
//		Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
//		return count;
//	}
//
//	@Override
//	public void updateBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest) {
//		log.info("Updating community hall booking request data for booking no : " + communityHallsBookingRequest.getHallsBookingApplication().getBookingNo());
//		producer.push(bookingConfiguration.getCommunityHallBookingUpdateTopic(), communityHallsBookingRequest);
//	}
//
//	@Override
//	public List<CommunityHallSlotAvailabilityDetail> getCommunityHallSlotAvailability(
//			CommunityHallSlotSearchCriteria criteria) {
//		List<Object> paramsList = new ArrayList<>();
//		
//		StringBuilder query = queryBuilder.getCommunityHallSlotAvailabilityQuery(criteria, paramsList);
//		
//		String hallCodeQuery = " AND ecsd.hall_code ";
//		
//		if(StringUtils.isNotBlank(criteria.getHallCode())) {
//			query.append(hallCodeQuery).append(" = ? ");
//			paramsList.add(criteria.getHallCode());
//		}else {
//			List<String> hallCodes = criteria.getHallCodes();
//			query.append(hallCodeQuery).append(" IN ( ");
//			int i = 0;
//			while(i < hallCodes.size()) {
//				query.append(" ? ");
//				if(i != hallCodes.size() - 1) {
//					query.append( " , ");
//				}
//				
//				paramsList.add(hallCodes.get(i));
//				
//				i++;
//			}
//			query.append(" ) "); 
//		}
//
//		log.info("getBookingDetails : Final query: " + query);
//		log.info("paramsList : " + paramsList);
//		List<CommunityHallSlotAvailabilityDetail> availabiltityDetails = jdbcTemplate.query(query.toString(), paramsList.toArray(),
//				availabilityRowMapper);
//		
//		log.info("Fetched slot availabilty details : " + availabiltityDetails);
//		return availabiltityDetails;
//	}


}
