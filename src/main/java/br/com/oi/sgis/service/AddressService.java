package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.AddressDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Address;
import br.com.oi.sgis.exception.AddressNotFoundException;
import br.com.oi.sgis.mapper.AddressMapper;
import br.com.oi.sgis.repository.AddressRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AddressService {

    private final AddressRepository addressRepository;
    private static final AddressMapper addressMapper = AddressMapper.INSTANCE;

    public AddressDTO createAddress(AddressDTO addressDTO) {
        Address addressToSave = addressMapper.toModel(addressDTO);
        String id = verifyNewAddressId();
        addressToSave.setId(id);
        try {
            Address savedAddress = addressRepository.save(addressToSave);
            return addressMapper.toDTO(savedAddress);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ADDRESS_SAVE_ERROR.getDescription());
        }
    }

    private String verifyNewAddressId() {
        Address lastAddress = addressRepository.findTopByOrderByIdDesc();
        String lastId = lastAddress.getId();
        DecimalFormat df = new DecimalFormat("0000000000");
        return df.format(new BigInteger(lastId).add(BigInteger.ONE));
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public List<AddressDTO> listAll(){
        List<Address> allAddresses = addressRepository.findAll();
        return allAddresses.stream().map(addressMapper::toDTO).collect(Collectors.toList());
    }

    public PaginateResponseDTO<Object> listPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term){
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if(term.isBlank())
            return PageableUtil.paginate(addressRepository.findAll(paging).map(addressMapper::toDTO));
        return PageableUtil.paginate(addressRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(addressMapper::toDTO));
    }

    @SneakyThrows
    public AddressDTO findById(String id){
        Address address = verifyIfExists(id);
        return addressMapper.toDTO(address);
    }

    @SneakyThrows
    public MessageResponseDTO updateById(AddressDTO addressDTO)  {
        verifyIfExists(addressDTO.getId());
        Address addressToUpdate = addressMapper.toModel(addressDTO);
        Address updatedAddress = addressRepository.save(addressToUpdate);
        return createMessageResponse(updatedAddress.getId(), "Endereço alterado com código ", HttpStatus.OK);
    }

    @SneakyThrows
    public void deleteById(String id) {
        verifyIfExists(id);
        addressRepository.deleteById(id);
    }

    private Address verifyIfExists(String id) throws AddressNotFoundException {
        return addressRepository.findById(id)
                .orElseThrow(()-> new AddressNotFoundException(MessageUtils.ADDRESS_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public AddressDTO findByIdOrCnpj(String idCnpj) throws AddressNotFoundException {
        Address address = addressRepository.findTopByIdOrCgcCpfId(idCnpj)
                .orElseThrow(()-> new AddressNotFoundException(MessageUtils.ADDRESS_NOT_FOUND_BY_ID.getDescription() + idCnpj));

        return addressMapper.toDTO(address);
    }

    public List<AddressDTO> getAddressesByCompany(String cgcCpf) {
        return addressRepository.findAllByCgcCpfId(cgcCpf).stream().map(addressMapper::toDTO).collect(Collectors.toList());
    }
}
