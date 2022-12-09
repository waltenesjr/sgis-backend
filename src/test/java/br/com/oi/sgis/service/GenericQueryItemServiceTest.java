package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GenericQueryItemDTO;
import br.com.oi.sgis.entity.GenericQueryItem;
import br.com.oi.sgis.repository.GenericQueryItemRepository;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.oi.sgis.util.MessageUtils.GENERIC_QUERY_ITEM_DELETE_ERROR;
import static br.com.oi.sgis.util.MessageUtils.GENERIC_QUERY_ITEM_SAVE_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericQueryItemServiceTest {

    @InjectMocks
    GenericQueryItemService queryItemService;
    @Mock
    GenericQueryItemRepository queryItemRepository;

    @Test
    void shouldSaveWithSuccess() {
        GenericQueryItemDTO dto = new EasyRandom().nextObject(GenericQueryItemDTO.class);

        queryItemService.save(dto);

        verify(queryItemRepository, times(1)).save(any());
    }

    @Test
    void shouldSaveThrowException(){
        GenericQueryItemDTO dto = new EasyRandom().nextObject(GenericQueryItemDTO.class);
        doThrow(SqlScriptParserException.class).when(queryItemRepository).save(any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> queryItemService.save(dto));

        assertEquals(GENERIC_QUERY_ITEM_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void shouldDeleteWithSuccess() {
        GenericQueryItem entity = new EasyRandom().nextObject(GenericQueryItem.class);

        queryItemService.delete(entity);

        verify(queryItemRepository, times(1)).delete(any());
    }

    @Test
    void shouldDeleteThrowException(){
        GenericQueryItem entity = new EasyRandom().nextObject(GenericQueryItem.class);
        doThrow(SqlScriptParserException.class).when(queryItemRepository).delete(any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> queryItemService.delete(entity));

        assertEquals(GENERIC_QUERY_ITEM_DELETE_ERROR.getDescription(), e.getMessage());
    }
}