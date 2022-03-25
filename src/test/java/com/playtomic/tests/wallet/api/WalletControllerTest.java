package com.playtomic.tests.wallet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.dto.ChargeRequestDto;
import com.playtomic.tests.wallet.dto.WalletCreateDto;
import com.playtomic.tests.wallet.dto.WalletGetDto;
import com.playtomic.tests.wallet.exception.ErrorResponse;
import com.playtomic.tests.wallet.mapper.WalletMapper;
import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.service.WalletService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static com.playtomic.tests.wallet.exception.ErrorMessage.WALLET_NOT_FOUND;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private WalletService mockWalletService;

	@MockBean
	private WalletMapper mockWalletMapper;

	private Wallet wallet;
	private WalletGetDto walletGetDto;
	private WalletCreateDto walletCreateDto;
	private ChargeRequestDto chargeRequestDto;

	@BeforeEach
	void setUp() {
		this.wallet = createWalletObject();
		this.walletGetDto = createWalledGetDtoObject(wallet);
		this.walletCreateDto = createWalletCreateDtoObject(wallet);
		this.chargeRequestDto = createChargeRequestDtoObject();
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void getWallet_success() throws Exception {
		// mock
		Mockito.when(mockWalletService.findByUuid(walletGetDto.getUuid())).thenReturn(Optional.of(wallet));
		Mockito.when(mockWalletMapper.toWalletGetDto(wallet)).thenReturn(walletGetDto);

		// test
		mockMvc.perform(get("/wallets/" + walletGetDto.getUuid().toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(walletGetDto)));
	}

	@Test
	void getWallet_walletNotFound() throws Exception {
		// prepare
		UUID notFoundUuid = UUID.randomUUID();
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND.value(), WALLET_NOT_FOUND.getMessage());

		// mock
		Mockito.when(mockWalletService.findByUuid(notFoundUuid)).thenReturn(Optional.empty());

		// test
		mockMvc.perform(get("/wallets/" + notFoundUuid).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().json(objectMapper.writeValueAsString(errorResponse)));
	}

	@Test
	void getWallets_success() throws Exception {
		List<Wallet> walletList = Arrays.asList(wallet);
		List<WalletGetDto> walletGetDtoList = Arrays.asList(walletGetDto);

		// mock
		Mockito.when(mockWalletService.findAll()).thenReturn(walletList);
		Mockito.when(mockWalletMapper.toWalletGetDtoList(Arrays.asList(wallet))).thenReturn(walletGetDtoList);

		// test
		mockMvc.perform(get("/wallets").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(walletGetDtoList)));
	}


	@Test
	void getWallet_uuid_validationError() throws Exception {
		// test
		mockMvc.perform(get("/wallets/" + " ").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("uuid")));
	}

	@Test
	void createWallet_success() throws Exception {
		// mock
		Mockito.when(mockWalletService.save(any(Wallet.class))).thenReturn(wallet);
		Mockito.when(mockWalletMapper.toWalletGetDto(any(Wallet.class))).thenReturn(walletGetDto);
		Mockito.when(mockWalletMapper.toWallet(any(WalletCreateDto.class))).thenReturn(wallet);

		// test
		mockMvc.perform(post("/wallets")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(walletCreateDto)))
				.andExpect(status().isCreated())
				.andExpect(content().json(objectMapper.writeValueAsString(walletGetDto)));
	}

	@Test
	void createWallet_balance_validationError() throws Exception {
		// prepare
		walletCreateDto.setBalance(new BigDecimal(-100));

		// test
		mockMvc.perform(post("/wallets")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(walletCreateDto)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("balance")));
	}

	@Test
	void charge_success() throws Exception {
		// prepare
		UUID walletUuid = UUID.randomUUID();

		// mock
		Mockito.doNothing().when(mockWalletService).charge(any(ChargeRequestDto.class), any(UUID.class));

		// test
		mockMvc.perform(post("/wallets/" + walletUuid + "/charge")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(chargeRequestDto)))
				.andExpect(status().isOk());
	}

	@Test
	void charge_amount_validationError() throws Exception {
		// prepare
		UUID walletUuid = UUID.randomUUID();
		ChargeRequestDto chargeRequestDto = createChargeRequestDtoObject();
		chargeRequestDto.setAmount(new BigDecimal(-10));

		// mock
		Mockito.doNothing().when(mockWalletService).charge(any(ChargeRequestDto.class), any(UUID.class));

		// test
		mockMvc.perform(post("/wallets/" + walletUuid + "/charge")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(chargeRequestDto)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("amount")));
	}

	@Test
	void charge_creditCardNumber_validationError() throws Exception {
		// prepare
		UUID walletUuid = UUID.randomUUID();
		ChargeRequestDto chargeRequestDto = createChargeRequestDtoObject();
		chargeRequestDto.setCreditCardNumber("invalid_credit_card_number");

		// mock
		Mockito.doNothing().when(mockWalletService).charge(any(ChargeRequestDto.class), any(UUID.class));

		// test
		mockMvc.perform(post("/wallets/" + walletUuid + "/charge")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(chargeRequestDto)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("creditCardNumber")));
	}

	private WalletGetDto createWalledGetDtoObject(Wallet wallet) {
		return WalletGetDto.builder()
					   .createdDate(wallet.getCreatedDate())
					   .lastModifiedDate(wallet.getLastModifiedDate())
					   .enable(wallet.getEnable())
					   .balance(wallet.getBalance())
					   .uuid(wallet.getUuid())
					   .build();
	}

	private Wallet createWalletObject() {
		return Wallet.builder()
					   .createdDate(new Date())
					   .lastModifiedDate(new Date())
					   .enable(false)
					   .balance(new BigDecimal(100))
					   .uuid(UUID.randomUUID())
					   .build();
	}

	private WalletCreateDto createWalletCreateDtoObject(Wallet wallet) {
		return WalletCreateDto.builder()
					   .balance(wallet.getBalance())
					   .enable(wallet.getEnable())
					   .build();
	}

	private ChargeRequestDto createChargeRequestDtoObject() {
		return ChargeRequestDto.builder()
					   .creditCardNumber("5555555555554444")
					   .amount(new BigDecimal(10))
					   .build();
	}
}