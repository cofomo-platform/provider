package org.cofomo.provider.error;

public class BookingNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BookingNotFoundException(String id) {
		super("Booking not found : " + id);
	}

}
