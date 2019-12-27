package org.cofomo.provider.controller;

import java.util.List;

import org.cofomo.commons.domain.transaction.Booking;
import org.cofomo.provider.api.IBooking;
import org.cofomo.provider.facade.BookingFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Booking API", description = "Implements IBooking")
@RequestMapping(path = "/v1/booking")
public class BookingController implements IBooking {
	
	@Autowired
	BookingFacade facade;
	
	@Override
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create booking")
	public Booking create(@RequestBody Booking booking) {
		return facade.create(booking);
	}
	
	@Override
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Get all bookings")
	public List<Booking> getAll() {
		return facade.getAll();
	}

	@Override
	@GetMapping(path = "/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Get booking by id")
	public Booking getById(@PathVariable String bookingId) {
		return facade.getById(bookingId);
	}
	
	@Override
	@GetMapping(path = "/status/{bookingStatus}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Get bookings by status")
	public List<Booking> getByStatus(@PathVariable String bookingStatus) {
		return facade.getByStatus(bookingStatus);
	}

	
	@Override
	@PutMapping(path = "/{bookingId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.ACCEPTED)
	@Operation(summary = "Update booking")
	public void update(@RequestBody Booking booking, @PathVariable String bookingId) {
		facade.update(booking, bookingId);
	}

	@Override
	@PutMapping("/{bookingId}/cancel")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Cancel booking")
	public void cancel(@PathVariable String bookingId) {
		facade.cancel(bookingId);
	}
	
	@Override
	@PutMapping("/{bookingId}/start")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Start booking")
	public void start(@PathVariable String bookingId) {
		facade.start(bookingId);
	}
	
	@Override
	@PutMapping("/{bookingId}/finish")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Finish booking")
	public void finish(@PathVariable String bookingId) {
		facade.finish(bookingId);
	}

}
