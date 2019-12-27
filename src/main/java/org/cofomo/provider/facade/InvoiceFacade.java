package org.cofomo.provider.facade;

import java.util.List;

import org.cofomo.commons.domain.transaction.Invoice;
import org.cofomo.provider.error.InvoiceNotFoundException;
import org.cofomo.provider.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvoiceFacade {

	@Autowired
	InvoiceRepository repository;

	public List<Invoice> getAll() {
		return (List<Invoice>) repository.findAll();
	}

	public Invoice getById(String invoiceId) {
		return repository.findById(invoiceId).orElseThrow(() -> new InvoiceNotFoundException(invoiceId));
	}

	public List<Invoice> getByStatus(String status) {
		return repository.findByStatus(status);
	}
	
}
