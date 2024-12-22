package org.upyog.mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonDetailsMapperFactory {

	private final List<CommonDetailsMapper> mappers;

	@Autowired
	public CommonDetailsMapperFactory(List<CommonDetailsMapper> mappers) {
		this.mappers = mappers;
	}

	public CommonDetailsMapper getMapper(String moduleName) {
		for (CommonDetailsMapper mapper : mappers) {
			if (moduleName.equalsIgnoreCase(mapper.getModuleName())) {
				return mapper;
			}
		}
		throw new IllegalArgumentException("No mapper found for module: " + moduleName);
	}

}
