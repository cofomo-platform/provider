package org.cofomo.provider.controller;

import java.util.List;

import org.cofomo.commons.domain.transaction.Invoice;
import org.cofomo.provider.api.IInvoice;
import org.cofomo.provider.facade.InvoiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Invoice API", description = "Implements IInvoice")
@RequestMapping(path = "/v1/invoice")
public class InvoiceController implements IInvoice {
	
	@Autowired
	InvoiceFacade facade;
	
	@Override
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Get all invoices")
	public List<Invoice> getAll() {
		return facade.getAll();
	}

	@Override
	@GetMapping(path = "/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Get invoice by id")
	public Invoice getById(@PathVariable String invoiceId) {
		return facade.getById(invoiceId);
	}

	@Override
	@GetMapping(path = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Get invoices by status")
	public List<Invoice> getByStatus(@PathVariable String status) {
		return facade.getByStatus(status);
	}

}
