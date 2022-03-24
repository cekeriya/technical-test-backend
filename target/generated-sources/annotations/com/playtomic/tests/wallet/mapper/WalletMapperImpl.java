package com.playtomic.tests.wallet.mapper;

import com.playtomic.tests.wallet.dto.WalletCreateDto;
import com.playtomic.tests.wallet.dto.WalletGetDto;
import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.model.Wallet.WalletBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-03-24T01:02:39+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_251 (Oracle Corporation)"
)
@Component
public class WalletMapperImpl implements WalletMapper {

    @Override
    public WalletGetDto toWalletGetDto(Wallet wallet) {
        if ( wallet == null ) {
            return null;
        }

        WalletGetDto walletGetDto = new WalletGetDto();

        walletGetDto.setUuid( wallet.getUuid() );
        walletGetDto.setBalance( wallet.getBalance() );
        walletGetDto.setCreatedDate( wallet.getCreatedDate() );
        walletGetDto.setLastModifiedDate( wallet.getLastModifiedDate() );
        walletGetDto.setEnable( wallet.getEnable() );

        return walletGetDto;
    }

    @Override
    public List<WalletGetDto> toWalletGetDtoList(List<Wallet> walletList) {
        if ( walletList == null ) {
            return null;
        }

        List<WalletGetDto> list = new ArrayList<WalletGetDto>( walletList.size() );
        for ( Wallet wallet : walletList ) {
            list.add( toWalletGetDto( wallet ) );
        }

        return list;
    }

    @Override
    public Wallet toWallet(WalletCreateDto walletCreateDto) {
        if ( walletCreateDto == null ) {
            return null;
        }

        WalletBuilder wallet = Wallet.builder();

        wallet.balance( walletCreateDto.getBalance() );
        wallet.enable( walletCreateDto.getEnable() );

        return wallet.build();
    }
}
