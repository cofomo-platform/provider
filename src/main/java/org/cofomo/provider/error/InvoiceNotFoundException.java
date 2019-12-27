package org.cofomo.provider.error;

public class InvoiceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvoiceNotFoundException(String id) {
		super("Invoice not found : " + id);
	}

}
