package org.cofomo.provider.repository;

import java.util.List;

import org.cofomo.commons.domain.transaction.Booking;
import org.cofomo.commons.domain.transaction.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, String> {
	List<Invoice> findByStatus(String status); 
}
