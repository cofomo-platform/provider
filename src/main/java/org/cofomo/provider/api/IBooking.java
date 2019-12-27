package org.cofomo.provider.api;

import java.util.List;

import org.cofomo.commons.domain.transaction.Booking;

public interface IBooking {
	
	public List<Booking> getAll();

	public Booking getById(String id);
	
	public List<Booking> getByStatus(String status);

	public Booking create(Booking booking);

	public void update(Booking booking, String id);

	public void cancel(String id);
	
	public void start(String id);
	
	public void finish(String id);
}