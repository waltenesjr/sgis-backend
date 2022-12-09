package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.AddressDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Address;
import br.com.oi.sgis.exception.AddressNotFoundException;
import br.com.oi.sgis.mapper.AddressMapper;
import br.com.oi.sgis.repository.AddressRepository;
import br.com.oi.sgis.util.MessageUtils;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @InjectMocks
    private AddressService addressService;

    @Mock
    private AddressRepository addressRepository;

    @MockBean
    private AddressMapper addressMapper = AddressMapper.INSTANCE;


    @Test
    void createAddress(){
        Address address = new EasyRandom().nextObject(Address.class);
        AddressDTO addressDTO = addressMapper.toDTO(address);
        address.setId("0000000001");
        Mockito.doReturn(address).when(addressRepository).findTopByOrderByIdDesc();
        Mockito.doReturn(address).when(addressRepository).save(Mockito.any());
        AddressDTO response = addressService.createAddress(addressDTO);

        assertEquals(address.getId(), response.getId());
    }

    @Test
    void createAddressException(){
        Address address = new EasyRandom().nextObject(Address.class);
        AddressDTO addressDTO = addressMapper.toDTO(address);
        address.setId("0000000001");
        Mockito.doReturn(address).when(addressRepository).findTopByOrderByIdDesc();
        Mockito.doThrow(SqlScriptParserException.class).when(addressRepository).save(Mockito.any());
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> addressService.createAddress(addressDTO));
        assertEquals(MessageUtils.ADDRESS_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void shouldListAll(){
        List<Address> allAdresses = new EasyRandom().objects(Address.class, 5).collect(Collectors.toList());
        Mockito.doReturn(allAdresses).when(addressRepository).findAll();
        List<AddressDTO> response = addressService.listAll();

        assertEquals(allAdresses.size(), response.size());
        Assertions.assertNotNull(allAdresses.get(0));
        assertEquals(allAdresses.get(0).getId(), response.get(0).getId());
    }

    @Test
    void shouldListAllWithSearchWithTerm(){
        List<Address> adresses = new EasyRandom().objects(Address.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Address> pagedResult = new PageImpl<>(adresses, paging, adresses.size());

        Mockito.doReturn(pagedResult).when(addressRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> addressDTOSToReturn = addressService.listPaginated(0, 10, List.of("id"), List.of("description"), "Rua 02");
        assertEquals(adresses.size(), addressDTOSToReturn.getData().size());
    }

    @Test
    void shouldListAllWithSearchWithoutTerm(){
        List<Address> adresses = new EasyRandom().objects(Address.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Address> pagedResult = new PageImpl<>(adresses, paging, adresses.size());

        Mockito.doReturn(pagedResult).when(addressRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> addressDTOSToReturn = addressService.listPaginated(0, 10, List.of("id"), List.of("description"), "");
        assertEquals(adresses.size(), addressDTOSToReturn.getData().size());
    }

    @Test
    void shouldFindById() {
        Address address = new EasyRandom().nextObject(Address.class);

        Mockito.doReturn(Optional.of(address)).when(addressRepository).findById(Mockito.any());
        AddressDTO addressToReturn = addressService.findById("1L");

        assertEquals(address.getId(), addressToReturn.getId());
    }
    @Test
    void shouldFindByIdWithException() {
        Mockito.doReturn(Optional.empty()).when(addressRepository).findById(Mockito.any());

        Assertions.assertThrows(AddressNotFoundException.class, () -> addressService.findById("1L"));
    }

    @Test
    void shouldDeleteById() {
        Address address = new EasyRandom().nextObject(Address.class);
        Mockito.doReturn(Optional.of(address)).when(addressRepository).findById(Mockito.anyString());

        addressService.deleteById(address.getId());
        Mockito.verify( addressRepository,Mockito.times(1)).findById(address.getId());
    }

    @Test
    void shouldDeleteByIdWithException() {
        Address address = new EasyRandom().nextObject(Address.class);
        Mockito.doReturn(Optional.empty()).when(addressRepository).findById(Mockito.anyString());

        Assertions.assertThrows(AddressNotFoundException.class, ()->addressService.deleteById(address.getId()));
    }

    @Test
    void shouldUpdateById() {
        Address address = new EasyRandom().nextObject(Address.class);
        AddressDTO addressDTO = addressMapper.toDTO(address);
        Mockito.doReturn(Optional.of(address)).when(addressRepository).findById(Mockito.anyString());
        Mockito.doReturn(address).when(addressRepository).save(Mockito.any());

        MessageResponseDTO response = addressService.updateById( addressDTO);
        Mockito.verify( addressRepository,Mockito.times(1)).findById(address.getId());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void shouldUpdateByIdWithException() {
        Address address = new EasyRandom().nextObject(Address.class);
        AddressDTO addressDTO = addressMapper.toDTO(address);

        Mockito.doReturn(Optional.empty()).when(addressRepository).findById(Mockito.anyString());

        Assertions.assertThrows(AddressNotFoundException.class, ()->addressService.updateById( addressDTO));
    }

    @Test
    void findByIdOrCnpj() throws AddressNotFoundException {
        Address address = new EasyRandom().nextObject(Address.class);

        Mockito.doReturn(Optional.of(address)).when(addressRepository).findTopByIdOrCgcCpfId(Mockito.any());
        AddressDTO addressToReturn = addressService.findByIdOrCnpj("12121");

        assertEquals(address.getId(), addressToReturn.getId());
    }
    @Test
    void findByIdOrCnpjWithException() {
        Mockito.doReturn(Optional.empty()).when(addressRepository).findTopByIdOrCgcCpfId(Mockito.any());

        Assertions.assertThrows(AddressNotFoundException.class, () -> addressService.findByIdOrCnpj("415415"));
    }

    @Test
    void getAddressesByCompany() {
        Address address = new EasyRandom().nextObject(Address.class);
        Mockito.doReturn(List.of(address)).when(addressRepository).findAllByCgcCpfId(Mockito.anyString());
        List<AddressDTO> addressesList =  addressService.getAddressesByCompany("123");
        assertEquals(1, addressesList.size());

    }
}