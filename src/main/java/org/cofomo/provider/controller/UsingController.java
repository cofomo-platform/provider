package org.cofomo.provider.controller;

import java.util.ArrayList;
import java.util.List;

import org.cofomo.provider.api.IUsing;
import org.cofomo.provider.facade.UsingFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Using API", description = "Implements IUsing")
@RequestMapping(path = "/v1/using")
public class UsingController implements IUsing {
	
	@Autowired
	UsingFacade facade;

	@Override
	@PostMapping(path="/message", consumes=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Send message to provider")
	public void sendMessage(@RequestBody String message) {
		facade.sendMessage(message);
	}

	@Override
	@PostMapping(path="/communicationOptions", consumes=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Add communication options")
	public void addCommunicationOptions(@RequestBody ArrayList<String> communicationOptions) {
		facade.addCommunicationOptions(communicationOptions);
	}

}

