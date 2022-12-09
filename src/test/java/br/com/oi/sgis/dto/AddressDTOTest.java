package br.com.oi.sgis.dto;

import br.com.oi.sgis.entity.Uf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class AddressDTOTest {

    @Test
    void addressDTOTest(){
        AddressDTO addressDTO = AddressDTO.builder().build();
        addressDTO.setCity("São Luís");
        addressDTO.setContact("99455-5465");
        addressDTO.setUf(Uf.builder().id("MA").build());
        addressDTO.setDescription("Cidade");
        addressDTO.setAddressDescription("Descrição do endereço");
        addressDTO.setId("0014");

        Assertions.assertEquals("São Luís", addressDTO.getCity());
        Assertions.assertEquals("99455-5465", addressDTO.getContact());
        Assertions.assertEquals("MA", addressDTO.getUf().getId());
        Assertions.assertEquals("Cidade", addressDTO.getDescription());
        Assertions.assertEquals("Descrição do endereço", addressDTO.getAddressDescription());
        Assertions.assertEquals("0014", addressDTO.getId());
        Assertions.assertNull(addressDTO.getPhone());
    }
}