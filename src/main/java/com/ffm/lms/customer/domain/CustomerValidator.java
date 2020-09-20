package com.ffm.lms.customer.domain;

import static com.ffm.lms.commons.constants.Constants.CUSTOMER_IMAGE_SIZE;
import static com.ffm.lms.commons.utils.CommonUtils.getStaticFieldValidationErrors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.validation.Validator;

import org.springframework.stereotype.Component;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.parameters.domain.ParameterService;
import com.ffm.lms.commons.parameters.domain.dto.ParameterDTO;
import com.ffm.lms.customer.domain.dto.CustomerDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomerValidator {
	
	private final Validator validator;
	private final ParameterService parameterService;
	private final CustomerRepository repository;
	
	public void validate(CustomerDTO customerDTO) {
		validateFields(customerDTO);
		validateImageSize(customerDTO.getPassport());
		isComputerNumberUnique(customerDTO.getComputerNumber());
	}
	
    private void validateFields(CustomerDTO customerDTO) {
        List<String> fieldValidationErrors = getStaticFieldValidationErrors(customerDTO, validator);
 
        if (fieldValidationErrors.isEmpty()) {
            return;
        }
        throw new ApplicationException("Invalid Fields","",fieldValidationErrors);
    }
    
    private void validateImageSize(byte[] passport) {
    	
    	if(passport == null || passport.length == 0)
    		return;
    	
    	ByteArrayOutputStream targetStream = new ByteArrayOutputStream();
    	try {
			targetStream.write(passport);
		} catch (IOException e) {
			throw new ApplicationException("Passport could not be processed. ");
		}
    	ParameterDTO parameter = parameterService.getParameterByName(CUSTOMER_IMAGE_SIZE);
    	Integer paramValue = Integer.valueOf(parameter.getValue());
    	int sizeInKb = targetStream.size()/1024;
    	
    	if(sizeInKb > paramValue)
    		throw new ApplicationException("Invalid Image Size. Size is greater than " + paramValue+"KB");
    }
    
    private void isComputerNumberUnique(String computerNumber) {
    	if(Objects.nonNull(computerNumber)) {
    		if(repository.findByComputerNumber(computerNumber).isPresent())
    			throw new ApplicationException(String.format("Computer Number (%s) already exist.", computerNumber));
    	}
    }
}
