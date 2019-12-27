package org.cofomo.provider.facade;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class UsingFacade {

	public void sendMessage(String message) {
		System.out.println("Message received: " + message);
	}

	public void addCommunicationOptions(List<String> communicationOptions) {
		System.out.println("CommunicationOptions received: " + communicationOptions);
	}
}
