package org.cofomo.provider.facade;

import java.util.List;

import org.cofomo.commons.domain.transaction.Booking;
import org.cofomo.provider.error.BookingNotFoundException;
import org.cofomo.provider.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookingFacade {
	
	@Autowired
    BookingRepository repository;

	public Booking create(Booking booking) {
		return repository.save(booking);
	}

	public List<Booking> getAll() {
		return (List<Booking>) repository.findAll();
	}

	public Booking getById(String id) {
		return repository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
	}
	
	public List<Booking> getByStatus(String status) {
		return repository.findByStatus(status);
	}

	public void update(Booking booking, String bookingId) {
		repository.save(booking);
	}

	public void cancel(String bookingId) {
		Booking booking = repository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
		booking.setStatus("canceled");
		repository.save(booking);
	}

	public void start(String bookingId) {
		Booking booking = repository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
		booking.setStatus("running");
		repository.save(booking);
	}

	public void finish(String bookingId) {
		Booking booking = repository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
		booking.setStatus("finished");
		repository.save(booking);
	}

	
}
