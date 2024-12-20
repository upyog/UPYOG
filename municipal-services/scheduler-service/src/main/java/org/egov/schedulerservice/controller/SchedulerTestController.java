package org.egov.schedulerservice.controller;

import java.lang.reflect.Method;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.egov.schedulerservice.call.Scheduler;
import org.egov.schedulerservice.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/scheduler")
public class SchedulerTestController {

	@Autowired
	private Scheduler schedulerService;

	@PostMapping("/execute")
	public String executeScheduler(@RequestBody @Valid RequestInfoWrapper requestInfoWrapper,
			@RequestParam(required = false) String methodName) {

		String message = "Successfully connected.";

		if (StringUtils.isNotBlank(methodName)) {
			try {
				// Get the method from the schedulerService based on the provided methodName
				Method method = schedulerService.getClass().getMethod(methodName);

				// Invoke the method on schedulerService,
				method.invoke(schedulerService);

				message = "Success: Method '" + methodName + "' executed.";

			} catch (NoSuchMethodException e) {
				// Handle the case where the method doesn't exist
				message = "Error: Method '" + methodName + "' not found.";
				log.error(message, e);

			} catch (Exception e) {
				// Handle other
				message = "Error: Unknown exception occured for the Method '" + methodName + "'.";
				log.error(message, e);
			}
		}

		return message;
	}
}
