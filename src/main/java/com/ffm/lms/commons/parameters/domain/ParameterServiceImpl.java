package com.ffm.lms.commons.parameters.domain;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.parameters.domain.dto.ParameterDTO;
import com.ffm.lms.commons.service.BaseService;
import com.ffm.lms.customer.commons.data.DeleteFlag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ParameterServiceImpl extends BaseService implements ParameterService {

	private final ParameterRepository repository;
	private final ParameterValidator validator;

	@Override
	@Secured({ "ROLE_ADMIN" })
	public ParameterDTO add(ParameterDTO parameterDTO) {
		
		validator.validate(parameterDTO);
		String paramName = parameterDTO.getName().trim().toUpperCase().replaceAll("\\s+", "_");
		if(doesParameterNameExist(paramName)) {
			throw new ApplicationException("Parameter of " + parameterDTO.getName() + " already exist!");
		}

		parameterDTO.setName(paramName);
		parameterDTO.setValue(parameterDTO.getValue().trim());

		Parameter parameter = repository.save(mapper.map(parameterDTO, Parameter.class));

		return mapper.map(parameter, ParameterDTO.class);
	}

	@Override
	@Secured({ "ROLE_ADMIN" })
	public boolean delete(Long parameterId) {

		Parameter parameter = repository.findById(parameterId)
				.orElseThrow(() -> new ApplicationException("Invalid Parameter"));

		// delete loan and all corresponding transactions
		parameter.setDelFlag(DeleteFlag.DELETED);
		return repository.softDelete(parameter);
	}

	@Override
	@Secured({ "ROLE_ADMIN" })
	public Page<ParameterDTO> list(Pageable pageable) {
		Page<Parameter> parameters = repository.findAll(pageable);

		if (parameters == null || parameters.isEmpty())
			throw new ApplicationException("Empty record");

		return parameters.map(parameter -> {
			return mapper.map(parameter, ParameterDTO.class);
		});
	}

	@Override
	public ParameterDTO getParameterByName(final String paramName) {
		log.info("Parameter Name: ------------" + paramName);
		String param = paramName.trim().toUpperCase();
		Parameter parameter = repository.findByName(paramName)
				.orElseThrow(() -> new ApplicationException("Parameter " + param + " does not exist. Contact admin"));
		return mapper.map(parameter, ParameterDTO.class);
	}
	
	@Secured({ "ROLE_ADMIN" })
	public ParameterDTO modify(ParameterDTO parameterUpdate, Long paramId) {
		
		if(parameterUpdate.getId().longValue() != paramId.longValue()) {
			throw new ApplicationException("Parameter does not exist");
		}
		
		if (isParameterExist(paramId)) {
			Parameter existingParam = getParameter(paramId);
			existingParam.setValue(parameterUpdate.getValue());
			existingParam.setName(parameterUpdate.getName().trim().toUpperCase().replaceAll("\\s+", "_"));
			return mapper.map(repository.save(existingParam), ParameterDTO.class);
		}
		
		throw new ApplicationException("System error. Parameter update failed.");
	}
	
	private boolean isParameterExist(Long paramId) {

		return repository.findById(paramId).isPresent();
	}
	
	private Parameter getParameter(Long paramId) {

		return repository.findById(paramId).orElseThrow(()-> new ApplicationException("Parameter does not exist!"));
	}


	private boolean doesParameterNameExist(String paramName) {
		return repository.findByName(paramName).isPresent();
	}
}
