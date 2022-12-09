package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GenericQueryTypeDTO;
import br.com.oi.sgis.mapper.GenericQueryTypeMapper;
import br.com.oi.sgis.repository.GenericQueryTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GenericQueryTypeService {

    private static final GenericQueryTypeMapper genericQueryTypeMapper = GenericQueryTypeMapper.INSTANCE;

    private GenericQueryTypeRepository genericQueryTypeRepository;

    public List<GenericQueryTypeDTO> findAll() {
            return genericQueryTypeRepository.findAll().stream()
                    .map(genericQueryTypeMapper::toDTO)
                    .collect(Collectors.toList());
    }
}
