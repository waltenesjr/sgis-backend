package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.GenericQueryDTO;
import br.com.oi.sgis.entity.Domain;
import br.com.oi.sgis.repository.DomainRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GenericQueryExecutionFactoryImplTest {

    @Mock
    private DomainRepository domainRepository;

    @InjectMocks
    private GenericQueryExecutionFactoryImpl genericQueryExecutionFactory;

    private GenericQueryDTO genericQueryDTO;
    private List<Domain> domainList;

    @BeforeEach
    void setUp(){
        genericQueryDTO = new EasyRandom().nextObject(GenericQueryDTO.class);
        genericQueryDTO.setTotalizeFlag(true);
        domainList = new EasyRandom().objects(Domain.class, genericQueryDTO.getColumns().size()).collect(Collectors.toList());
        genericQueryDTO.getColumns().get(0).setValueTwo(null);
        genericQueryDTO.getColumns().get(2).setValueTwo(null);
        genericQueryDTO.getColumns().get(2).setValueOne("100");
        genericQueryDTO.getColumns().get(2).setNoShow(true);

        for (int i = 0; i < domainList.size(); i++) {
            domainList.get(i).setDescription(genericQueryDTO.getColumns().get(i).getColumnName());
        }
    }

    @ParameterizedTest @ValueSource(strings = {"CNU", "CNR", "CNS", "CNI"})
    void createSqlQuery(String type) {
        genericQueryDTO.setGenericQueryTypeId(type);
        Mockito.doReturn(domainList).when(domainRepository).findDistinctByDescriptionInAndDomainID_Id(Mockito.any(), Mockito.any());
        Mockito.doReturn(List.of(domainList.get(0))).when(domainRepository).findAllByDomainID_Id(Mockito.any());
        int size = genericQueryDTO.getColumns().size() - 1;
        genericQueryDTO.getColumns().get(size).setFlagOrder(true);
        String resultQuery = genericQueryExecutionFactory.createSqlQuery(genericQueryDTO);
        assertNotNull(resultQuery);
    }


}