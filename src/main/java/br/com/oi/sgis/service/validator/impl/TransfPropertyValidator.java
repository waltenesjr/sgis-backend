package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.TransfPropertyDTO;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.DepartmentService;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
@RequiredArgsConstructor
public class TransfPropertyValidator implements Validator<TransfPropertyDTO> {

    private final DepartmentService departmentService;
    private final UnityRepository unityRepository;


    @Override
    public void validate(TransfPropertyDTO transfPropertyDTO) {
        validateDestination(transfPropertyDTO.getIdDepDestination());
        validateOriginForDestination(transfPropertyDTO);
    }

    private void validateOriginForDestination(TransfPropertyDTO transfPropertyDTO) {
        Unity unity;
        unity = unityRepository.findById(transfPropertyDTO.getIdUnity())
                .orElseThrow(() -> new IllegalArgumentException(MessageUtils.UNITY_NOT_FOUND_BY_ID.getDescription() + transfPropertyDTO.getIdUnity()));

        if(Objects.equals(unity.getDeposit().getId(), transfPropertyDTO.getIdDepDestination())){
            throw new IllegalArgumentException(MessageUtils.UNITY_PROP_TRANSF_SAME_DEPART_ERROR.getDescription());
        }

    }

    private void validateDestination(String idDestination)  {
        try {
            departmentService.findById(idDestination);
        } catch (DepartmentNotFoundException e) {
            throw new IllegalArgumentException(MessageUtils.UNITY_PROP_TRANSF_ERROR.getDescription() + ": " + e.getMessage());
        }
    }

}
