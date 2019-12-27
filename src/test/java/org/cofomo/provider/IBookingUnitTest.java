package org.cofomo.provider;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cofomo.commons.domain.exploration.Location;
import org.cofomo.commons.domain.exploration.MobilityOption;
import org.cofomo.commons.domain.exploration.MobilityProvider;
import org.cofomo.commons.domain.identity.VerifiableClaim;
import org.cofomo.commons.domain.transaction.Booking;
import org.cofomo.provider.controller.BookingController;
import org.cofomo.provider.facade.BookingFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@WebMvcTest(BookingController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class IBookingUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BookingFacade facade;
	
	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(documentationConfiguration(restDocumentation).uris().withScheme("https")
						.withHost("provider.cofomo.org").withPort(443))
				.alwaysDo(
						document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
				.build();
	}

	@Test
	public void shouldReturnAllBookings() throws Exception {

		// create list of bookings
		List<Booking> bookingList = new ArrayList<Booking>();
		bookingList.add(createBooking1());
		bookingList.add(createBooking2());

		// define mock return value
		when(facade.getAll()).thenReturn(bookingList);

		// action
		this.mockMvc.perform(get("/v1/booking")).andDo(print()).andExpect(status().isOk())
				.andDo(document("booking-get-all", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	public void shouldReturnAllMobilityProviderByStatusBooked() throws Exception {

		// create list of bookings
		List<Booking> bookingList = new ArrayList<Booking>();
		bookingList.add(createBooking1());

		// define mock return value
		when(facade.getByStatus("booked")).thenReturn(bookingList);

		// action
		this.mockMvc.perform(get("/v1/booking/status/booked")).andDo(print()).andExpect(status().isOk())
				.andDo(document("booking-get-all-by-status", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	public void shouldReturnOneBooking() throws Exception {

		// create mobility provider
		Booking booking = createBooking1();

		// define mock return value
		when(facade.getById("1")).thenReturn(booking);
		// action
		this.mockMvc.perform(get("/v1/booking/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andDo(document("booking-get-by-id", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						responseFields(fieldWithPath("id").description("Autogenerated UUID of the booking"),
								fieldWithPath("startTime").description("Booking start time"),
								fieldWithPath("endTime").description("Booking end time"),
								fieldWithPath("option").description("Mobility option to book"),
								fieldWithPath("option.provider.id").description("Autogenerated UUID of the provider"),
								fieldWithPath("option.provider.name").description("Name of the provider"),
								fieldWithPath("option.provider.url").description("URI-endpoint of provider"),
								fieldWithPath("option.provider.operationAreas").description("Operation area of provider"),
								fieldWithPath("option.provider.serviceOffers").description("Services offered by provider"),
								fieldWithPath("option.provider.alive").description("Service still alive?"),
								fieldWithPath("option.provider.lastHeartBeat").description("Time of last heart beat"),
								fieldWithPath("option.pickupLocation.latitude").description("Latitude"),
								fieldWithPath("option.pickupLocation.longitude").description("Longitude"),
								fieldWithPath("option.pickupTime").description("Time to pickup the mobility ressource"),
								fieldWithPath("claims[]").description("List of claims corresponding to essential claims of mobility options"),
								fieldWithPath("claims.[].context").description("Used schema of the claim"),
								fieldWithPath("claims.[].type").description("Verifiable claim type"),
								fieldWithPath("claims.[].owner").description("Owner of the claim"),
								fieldWithPath("claims.[].issuer").description("Issuer of the claim"),
								fieldWithPath("claims.[].validation").description("Validation endpoint of the claim"),
								fieldWithPath("claims.[].issuanceDate").description("Date of issuance of the claim"),
								fieldWithPath("status").description("Can one of: booked, running, finished, canceled"),
								fieldWithPath("communicationOptions").description("Link communication options"))))
				.andDo(print());
	}

	@Test
	public void shouldCreateBooking() throws Exception {

		// create mobility provider
		Booking booking = createBooking1();

		// define mock return value
		when(facade.create(booking)).thenReturn(booking);

		// action
		MvcResult result = this.mockMvc
				.perform(post("/v1/booking").content(objectMapper.writeValueAsString(booking))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(document("booking-create-booking", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						requestFields(fieldWithPath("id").description("Autogenerated UUID of the booking"),
								fieldWithPath("startTime").description("Booking start time"),
								fieldWithPath("endTime").description("Booking end time"),
								fieldWithPath("option").description("Mobility option to book"),
								fieldWithPath("option.provider.id").description("Autogenerated UUID of the provider"),
								fieldWithPath("option.provider.name").description("Name of the provider"),
								fieldWithPath("option.provider.url").description("URI-endpoint of provider"),
								fieldWithPath("option.provider.operationAreas").description("Operation area of provider"),
								fieldWithPath("option.provider.serviceOffers").description("Services offered by provider"),
								fieldWithPath("option.provider.alive").description("Service still alive?"),
								fieldWithPath("option.provider.lastHeartBeat").description("Time of last heart beat"),
								fieldWithPath("option.pickupLocation.latitude").description("Latitude"),
								fieldWithPath("option.pickupLocation.longitude").description("Longitude"),
								fieldWithPath("option.pickupTime").description("Time to pickup the mobility ressource"),
								fieldWithPath("claims[]").description("List of claims corresponding to essential claims of mobility options"),
								fieldWithPath("claims.[].context").description("Used schema of the claim"),
								fieldWithPath("claims.[].type").description("Verifiable claim type"),
								fieldWithPath("claims.[].owner").description("Owner of the claim"),
								fieldWithPath("claims.[].issuer").description("Issuer of the claim"),
								fieldWithPath("claims.[].validation").description("Validation endpoint of the claim"),
								fieldWithPath("claims.[].issuanceDate").description("Date of issuance of the claim"),
								fieldWithPath("status").description("Can one of: booked, running, finished, canceled"),
								fieldWithPath("communicationOptions").description("Link communication options"))))
				.andDo(print())
				.andReturn();

//		// compare response object to request object
//		String bookingRequest = objectMapper.writeValueAsString(booking);
//		String bookingResponse = result.getResponse().getContentAsString();
//		assertThat(bookingRequest).isEqualToIgnoringWhitespace(bookingResponse);
	}

	@Test
	public void shouldUpdateOneBooking() throws Exception {

		// create mobility provider
		Booking booking = createBooking1();

		// action
		this.mockMvc
				.perform(put("/v1/booking/1").content(objectMapper.writeValueAsString(booking))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted())
				.andDo(document("booking-update-booking", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						requestFields(fieldWithPath("id").description("Autogenerated UUID of the booking"),
								fieldWithPath("startTime").description("Booking start time"),
								fieldWithPath("endTime").description("Booking end time"),
								fieldWithPath("option").description("Mobility option to book"),
								fieldWithPath("option.provider.id").description("Autogenerated UUID of the provider"),
								fieldWithPath("option.provider.name").description("Name of the provider"),
								fieldWithPath("option.provider.url").description("URI-endpoint of provider"),
								fieldWithPath("option.provider.operationAreas").description("Operation area of provider"),
								fieldWithPath("option.provider.serviceOffers").description("Services offered by provider"),
								fieldWithPath("option.provider.alive").description("Service still alive?"),
								fieldWithPath("option.provider.lastHeartBeat").description("Time of last heart beat"),
								fieldWithPath("option.pickupLocation.latitude").description("Latitude"),
								fieldWithPath("option.pickupLocation.longitude").description("Longitude"),
								fieldWithPath("option.pickupTime").description("Time to pickup the mobility ressource"),
								fieldWithPath("claims[]").description("List of claims corresponding to essential claims of mobility options"),
								fieldWithPath("claims.[].context").description("Used schema of the claim"),
								fieldWithPath("claims.[].type").description("Verifiable claim type"),
								fieldWithPath("claims.[].owner").description("Owner of the claim"),
								fieldWithPath("claims.[].issuer").description("Issuer of the claim"),
								fieldWithPath("claims.[].validation").description("Validation endpoint of the claim"),
								fieldWithPath("claims.[].issuanceDate").description("Date of issuance of the claim"),
								fieldWithPath("status").description("Can be one of: booked, running, finished, canceled"),
								fieldWithPath("communicationOptions").description("Link communication options"))))
				.andDo(print());
	}

	@Test
	public void shouldCancelBooking() throws Exception {

		// action
		this.mockMvc.perform(put("/v1/booking/1/cancel")).andExpect(status().isOk()).andDo(document("booking-cancel")).andDo(print());
	}
	
	@Test
	public void shouldStartBooking() throws Exception {

		// action
		this.mockMvc.perform(put("/v1/booking/1/start")).andExpect(status().isOk()).andDo(document("booking-start")).andDo(print());
	}
	
	@Test
	public void shouldFinishBooking() throws Exception {

		// action
		this.mockMvc.perform(put("/v1/booking/1/finish")).andExpect(status().isOk()).andDo(document("booking-finish")).andDo(print());
	}

	// helper functions
	private static Booking createBooking1() {
		MobilityOption option = createMobilityOption1();
		List<String> communicationList = new ArrayList<String>();
		communicationList.add("email");
		communicationList.add("phone");
		return new Booking(option, createVerifiableClaimList(), "booked", communicationList);
	}
	
	private static Booking createBooking2() {
		MobilityOption option = createMobilityOption2();
		List<String> communicationList = new ArrayList<String>();
		communicationList.add("phone");
		return new Booking(option, createVerifiableClaimList(), "running", communicationList);
	}
	
	private static List<VerifiableClaim> createVerifiableClaimList() {
		List<VerifiableClaim> claimList = new ArrayList<VerifiableClaim>();
		VerifiableClaim claim = new VerifiableClaim("https://www.w3.org/2018/credentials/v1", "vct_can_pay", "xMpCOKC5I4INzFCab3WEmw==", "Authority of vct_can_pay", "", new Date());
		VerifiableClaim claim2 = new VerifiableClaim("https://www.w3.org/2018/credentials/v1", "vct_has_drivers_license", "xMpCOKC5I4INzFCab3WEmw==", "Authority of vct_has_drivers_license", "", new Date());
		claimList.add(claim);
		claimList.add(claim2);
		return claimList;
	}

	private static MobilityOption createMobilityOption1() {
		Location location = new Location(48.521637, 8.057645);
		Date today = new Date();
		MobilityProvider provider = createMobilityProvider1();
		return new MobilityOption(provider, location, today);
	}
	
	private static MobilityOption createMobilityOption2() {
		Location location = new Location(48.521637, 8.057645);
		Date today = new Date();
		MobilityProvider provider = createMobilityProvider2();
		return new MobilityOption(provider, location, today);
	}

	private static MobilityProvider createMobilityProvider1() {
		List<String> serviceOffers = new ArrayList<String>();
		List<Integer> areas = new ArrayList<Integer>();
		serviceOffers.add("carsharing");
		areas.add(72072);
		return new MobilityProvider("Carsharing Inc.", "https://test.carsharing.org", areas, serviceOffers);
	}

	private static MobilityProvider createMobilityProvider2() {
		List<String> serviceOffers = new ArrayList<String>();
		List<Integer> areas = new ArrayList<Integer>();
		serviceOffers.add("bikesharing");
		serviceOffers.add("escootersharing");
		areas.add(72073);
		return new MobilityProvider("BikeSharing Inc.", "https://test.bikesharing.org", areas, serviceOffers);
	}

}
