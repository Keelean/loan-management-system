package com.ffm.lms.customer.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ffm.lms.commons.controller.BaseController;
import com.ffm.lms.commons.data.response.ApiResponseBase;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.customer.domain.dto.CustomerDTO;
import com.ffm.lms.customer.domain.dto.CustomerResponseDTO;
import com.ffm.lms.customer.domain.dto.CustomerUpdateDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
//import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(value = "Customer Controller", protocols = "http", tags = { "Customer-Controller" })
@RequestMapping("/lms/api/customer")
@RequiredArgsConstructor
@RestController
public class CustomerController extends BaseController{

	private final CustomerService service;

	@PostMapping("/add")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<CustomerResponseDTO>> add(@RequestBody CustomerDTO customerDTO) {
		log(customerDTO, "Customer Create Request: [{}]");
		ApiResponseBase<CustomerResponseDTO> response = new ApiResponseBase<>();
		response.setResponse(service.create(customerDTO));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully added customer");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
    @PutMapping("/update")
    @Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
    public ResponseEntity<ApiResponseBase<CustomerResponseDTO>> update(@RequestBody CustomerUpdateDTO customerDTO) throws ApplicationException {
    	log(customerDTO, "Customer Update Request: [{}]");
        ApiResponseBase<CustomerResponseDTO> response = new ApiResponseBase<>();
        response.setResponse(service.modify(customerDTO));
        response.setStatus("success");
        response.setSuccess(true);
        response.setMessage("Successfully updated customer");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{customerId}")
    @Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
    public ResponseEntity<ApiResponseBase<Boolean>> delete(@PathVariable("customerId") Long customerId) throws ApplicationException {
    	log(customerId, "Customer Delete Request: [{}]");
        ApiResponseBase<Boolean> response = new ApiResponseBase<>();
        response.setMessage("Successfully deleted customer");
        response.setResponse(service.delete(customerId));
        response.setStatus("success");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
	//@RolesAllowed("ADMIN")
    @GetMapping
    //@Permission(permitActions = {"DELETE","CREATE"})
    @Secured({ "ROLE_BASIC", "ROLE_STANDARD", "ROLE_ADMIN" })
    public ResponseEntity<ApiResponseBase<Page<CustomerResponseDTO>>> list(@PageableDefault(size = 10, page = 0, direction = Direction.DESC) Pageable pageable) {
        ApiResponseBase<Page<CustomerResponseDTO>> response = new ApiResponseBase<>();
        response.setResponse(service.list(pageable));
        response.setMessage("Successfully retrieved customer(s)");
        response.setStatus("success");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("{customerId}")
    @Secured({ "ROLE_BASIC", "ROLE_STANDARD", "ROLE_ADMIN" })
    public ResponseEntity<ApiResponseBase<CustomerResponseDTO>> findById(@PathVariable("customerId") Long customerId) {
    	log(customerId, "Customer get byID Request: [{}]");
        ApiResponseBase<CustomerResponseDTO> response = new ApiResponseBase<>();
        response.setResponse(service.findById(customerId));
        response.setMessage("Successfully got customer");
        response.setStatus("success");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/filter")
    @Secured({ "ROLE_BASIC", "ROLE_STANDARD", "ROLE_ADMIN" })
    public ResponseEntity<ApiResponseBase<Page<CustomerResponseDTO>>> filter(@PageableDefault(size = 10, page = 0) Pageable pageable,
                                                                  @ApiParam(value = "customer email", required = false) @RequestParam(name = "email") String email,
                                                                  @ApiParam(value = "customer first name", required = false) @RequestParam(name = "firstName") String firstName,
                                                                  @ApiParam(value = "customer last name", required = false) @RequestParam(name = "lastName") String lastName,
                                                                  @ApiParam(value = "customer computer number", required = false) @RequestParam(name = "computerNumber") String computerNumber) {
        ApiResponseBase<Page<CustomerResponseDTO>> response = new ApiResponseBase<>();
        response.setMessage("Successfully retrieved customer(s)");
        response.setStatus("success");
        response.setResponse(service.filter(email, firstName, lastName, computerNumber, pageable));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
