package com.playtomic.tests.wallet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void getWallet_success() throws Exception {
		// prepare
		Wallet wallet = createWalletObject();
		WalletGetDto walletGetDto = createWalledGetDtoObject(wallet);

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

		ErrorResponse errorResponse = createWalletNotFoundErrorResponseObject();

		// mock
		Mockito.when(mockWalletService.findByUuid(notFoundUuid)).thenReturn(Optional.empty());

		// test
		mockMvc.perform(get("/wallets/" + notFoundUuid).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().json(objectMapper.writeValueAsString(errorResponse)));
	}

	@Test
	void getWallets_success() throws Exception {
		// prepare
		Wallet wallet = createWalletObject();
		WalletGetDto walletGetDto = createWalledGetDtoObject(wallet);

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
	void createWallet() {
	}

	@Test
	void charge() {
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

	private ErrorResponse createWalletNotFoundErrorResponseObject() {
		return new ErrorResponse(HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND.value(), WALLET_NOT_FOUND.getMessage());
	}
}