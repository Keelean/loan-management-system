package com.ffm.lms.commons.parameters.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ffm.lms.commons.parameters.domain.dto.ParameterDTO;

public interface ParameterService {
	
	ParameterDTO add(ParameterDTO parameter);
	
	boolean delete(Long parameterId);

	Page<ParameterDTO> list(Pageable pageable);
	
	ParameterDTO getParameterByName(String paramName);
	
	ParameterDTO modify(ParameterDTO parameterUpdate, Long paramId);
}
