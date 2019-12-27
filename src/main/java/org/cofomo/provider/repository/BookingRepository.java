package org.cofomo.provider.repository;

import java.util.List;

import org.cofomo.commons.domain.transaction.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends CrudRepository<Booking, String> {
	List<Booking> findByStatus(String status); 
}
