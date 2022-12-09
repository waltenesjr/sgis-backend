package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.AddressDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Address;
import br.com.oi.sgis.exception.AddressNotFoundException;
import br.com.oi.sgis.service.AddressService;
import br.com.oi.sgis.util.PageableUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @InjectMocks
    private AddressController addressController;

    @Mock
    private AddressService addressService;

    @Test
    void shouldFindAddressById() {
        AddressDTO addressDTO = new EasyRandom().nextObject(AddressDTO.class);
        Mockito.doReturn(addressDTO).when(addressService).findById(Mockito.any());

        AddressDTO addressDTOReturned = addressController.findById("12345");

        assertEquals(addressDTO.getId(), addressDTOReturned.getId());
    }

    @Test
    void shouldListAllWithSearchPaginated() {
        List<Address> addresss = new EasyRandom().objects(Address.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(addresss, paging, addresss.size()));

        Mockito.doReturn(expectedResponse).when(addressService).listPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = addressController.listAllPaginated(0, 10, List.of("id"), List.of("registerDate"), "");

        Assertions.assertNotNull(response.getBody());
        assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void shouldCreateAddress() {
        AddressDTO addressDTO = new EasyRandom().nextObject(AddressDTO.class);
        Mockito.doReturn(addressDTO).when(addressService).createAddress(Mockito.any());
        AddressDTO response = addressController.createAddress(addressDTO);
        assertEquals(addressDTO.getId(), response.getId());
    }

    @Test
    void shouldUpdateById() {
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        AddressDTO addressDTO = new EasyRandom().nextObject(AddressDTO.class);
        Mockito.doReturn(messageResponseDTO).when(addressService).updateById(Mockito.any());

        MessageResponseDTO response = addressController.updateById( addressDTO);
        assertEquals(HttpStatus.CREATED, response.getStatus());

    }

    @Test
    void shouldDeleteById() {
        addressController.deleteById("1L");
        Mockito.verify(addressService, Mockito.times(1)).deleteById("1L");
    }

    @Test
    void findByIdOrCNPJ() throws AddressNotFoundException {
        AddressDTO addressDTO = new EasyRandom().nextObject(AddressDTO.class);
        Mockito.doReturn(addressDTO).when(addressService).findByIdOrCnpj(Mockito.any());

        AddressDTO addressDTOReturned = addressController.findByIdOrCnpj("12345");

        assertEquals(addressDTO.getId(), addressDTOReturned.getId());
    }

    @Test
    void addressesByCompany() {
        AddressDTO addressDTO = new EasyRandom().nextObject(AddressDTO.class);
        Mockito.doReturn(List.of(addressDTO)).when(addressService).getAddressesByCompany(Mockito.anyString());
        List<AddressDTO> addressDTOS = addressController.addressesByCompany("123");
        assertEquals(1, addressDTOS.size());

    }
}