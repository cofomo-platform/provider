package org.cofomo.provider.api;

import java.util.List;

import org.cofomo.commons.domain.transaction.Invoice;

public interface IInvoice {
	
	public List<Invoice> getAll();

	public Invoice getById(String id);

	public List<Invoice> getByStatus(String status);

}
