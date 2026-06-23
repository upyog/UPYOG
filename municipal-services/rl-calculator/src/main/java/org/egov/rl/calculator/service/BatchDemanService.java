package org.egov.rl.calculator.service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.egov.common.contract.request.RequestInfo;
import org.egov.rl.calculator.repository.DemandRepository;
import org.egov.rl.calculator.util.Configurations;
import org.egov.rl.calculator.web.models.demand.Demand;
import org.egov.rl.calculator.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class BatchDemanService {

	@Autowired
	private DemandRepository demandRepository;
	
	@Autowired
	private CycleWorkflowService cycleWorkflowService;

	@Lazy
	@Autowired
	private DemandService demandService;

	@Autowired
	private Configurations config;

	public <T> List<List<Demand>> partition(List<Demand> list, int batchSize) {
		if (batchSize <= 0)
			throw new IllegalArgumentException("batchSize must be > 0");

		int size = list.size();
		int numberOfBatches = (size + batchSize - 1) / batchSize;

		return IntStream.range(0, numberOfBatches).mapToObj(batchIndex -> {
			int start = batchIndex * batchSize;
			int end = Math.min(start + batchSize, size);
			return list.subList(start, end);
		}).collect(Collectors.toList());
	}

	public void batchRun(List<Demand> list, int batchSize, RequestInfo requestInfo) {
		AtomicInteger noofBatch = new AtomicInteger(0);
		List<Demand> data = IntStream.rangeClosed(0, (list.size() - 1)).boxed().map(a -> list.get(a))
				.collect(Collectors.toList());
		List<List<Demand>> batches = partition(data, config.getDemandBatchSize());
		batches.forEach(demands -> {
			int batch=noofBatch.incrementAndGet();
			LocalDate currentDate = LocalDate.now();
			try {
				List<Demand> savedDemands = demandRepository.saveDemand(requestInfo, demands);
				demandService.fetchBillForDemands(savedDemands, requestInfo);
				batchWorkflowUpdate(requestInfo,demands);
				demands.forEach(d -> {
					
//					log.info("{} , Batch No :{} ,Success Bulk demands generation for consumerCode: {} , from: {} , to: {} and for amount: {}",
//							currentDate,batch, d.getConsumerCode(), d.getTaxPeriodFrom(), d.getTaxPeriodTo(),
//							d.getMinimumAmountPayable());
					System.out.println(currentDate+" , Batch No : "+ batch +" ,Success Bulk demands generation for consumerCode: "+d.getConsumerCode()+" , from: "+d.getTaxPeriodFrom()+" , to: "+d.getTaxPeriodTo()+" and for amount: "+d.getMinimumAmountPayable());
				});
			} catch (Exception e) {
				System.out.println("-----:: Failed Batch Demand Generation ::-----");
				demands.forEach(d -> {
//					log.error("{} , Batch No :{} : Failed Bulk demands for consumerCode: {} and from: {} to: {} for amount: {}",
//							currentDate,noofBatch.incrementAndGet(), d.getConsumerCode(), d.getTaxPeriodFrom(), d.getTaxPeriodTo(),
//							d.getMinimumAmountPayable());
					System.out.println(currentDate+" , Batch No : "+ batch +" ,Failed Bulk demands generation for consumerCode: "+d.getConsumerCode()+" , from: "+d.getTaxPeriodFrom()+" , to: "+d.getTaxPeriodTo()+" and for amount: "+d.getMinimumAmountPayable());
					
				});
				e.printStackTrace();
			}

		});
	}
	
	public void batchWorkflowUpdate(RequestInfo requestInfo,List<Demand> list) {

       try {
        	CompletableFuture.runAsync(() -> {
        		list.stream().forEach(dmd->{
        			cycleWorkflowService.process(dmd.getTenantId(),requestInfo,dmd.getConsumerCode()
                );});
        	  });
        	
            
        } catch (Exception e) {
			// TODO: handle exception
		} 
    }
}
