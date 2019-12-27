package org.cofomo.provider;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.springframework.restdocs.snippet.Attributes.key;

import org.cofomo.commons.domain.exploration.Location;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
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
import org.springframework.util.StringUtils;
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
						relaxedResponseFields(fieldWithPath("id").description("Autogenerated UUID of the booking").type("String"),
								fieldWithPath("startTime").description("Booking start time").type("DateTime"),
								fieldWithPath("endTime").description("Booking end time").type("DateTime"),
								fieldWithPath("option").description("Mobility option to book").type("MobilityOption"),
								fieldWithPath("claims").description("List of claims corresponding to essential claims of mobility options").type("VerifiableClaim[]"),
								fieldWithPath("status").description("Can one of: booked, running, finished, canceled").type("BookingStatus"),
								fieldWithPath("communicationOptions").description("Link communication options").type("CommunicationOptions"))))
				.andDo(print());
	}

	@Test
	public void shouldCreateBooking() throws Exception {

		// create mobility provider
		Booking booking = createBooking1();

		// define mock return value
		when(facade.create(booking)).thenReturn(booking);
		
		ConstrainedFields fields = new ConstrainedFields(Booking.class);

		// action
		MvcResult result = this.mockMvc
				.perform(post("/v1/booking").content(objectMapper.writeValueAsString(booking))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(document("booking-create-booking", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						relaxedRequestFields(fieldWithPath("id").description("Autogenerated UUID of the booking").type("String"),
								fields.withPath("startTime").description("Booking start time").type("DateTime"),
								fields.withPath("endTime").description("Booking end time").type("DateTime"),
								fields.withPath("option").description("Mobility option to book").type("MobilityOption"),
								fields.withPath("claims").description("List of claims corresponding to essential claims of mobility options").type("VerifiableClaim[]"),
								fields.withPath("status").description("Can one of: booked, running, finished, canceled").type("BookingStatus"),
								fields.withPath("communicationOptions").description("Link communication options").type("CommunicationOptions"))))
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
						relaxedRequestFields(fieldWithPath("id").description("Autogenerated UUID of the booking").type("String"),
								fieldWithPath("startTime").description("Booking start time").type("DateTime"),
								fieldWithPath("endTime").description("Booking end time").type("DateTime"),
								fieldWithPath("option").description("Mobility option to book").type("MobilityOption"),
								fieldWithPath("claims").description("List of claims corresponding to essential claims of mobility options").type("VerifiableClaim[]"),
								fieldWithPath("status").description("Can one of: booked, running, finished, canceled").type("BookingStatus"),
								fieldWithPath("communicationOptions").description("Link communication options").type("CommunicationOptions"))))
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
//		VerifiableClaim claim2 = new VerifiableClaim("https://www.w3.org/2018/credentials/v1", "vct_has_drivers_license", "xMpCOKC5I4INzFCab3WEmw==", "Authority of vct_has_drivers_license", "", new Date());
		claimList.add(claim);
//		claimList.add(claim2);
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
	
	private static class ConstrainedFields {

		private final ConstraintDescriptions constraintDescriptions;

		ConstrainedFields(Class<?> input) {
			this.constraintDescriptions = new ConstraintDescriptions(input);
		}

		private FieldDescriptor withPath(String path) {
			return fieldWithPath(path).attributes(key("constraints").value(StringUtils
					.collectionToDelimitedString(this.constraintDescriptions
							.descriptionsForProperty(path), ". ")));
		}
	}

}
