package com.ffm.lms.commons.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseController {

	public Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected void log(Object object, String message) {
		
		ObjectMapper mapper = new ObjectMapper();
		String mapperValue = "";
		try {
			mapperValue = mapper.writeValueAsString(object);
			log.info(message, mapperValue);
		}catch(JsonProcessingException e) {
			log.info("Unable to convert object to JSON");
		}
	}

}
