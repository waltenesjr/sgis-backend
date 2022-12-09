package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GenericQueryItemDTO;
import br.com.oi.sgis.entity.GenericQueryItem;
import br.com.oi.sgis.mapper.GenericQueryItemMapper;
import br.com.oi.sgis.repository.GenericQueryItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.oi.sgis.util.MessageUtils.GENERIC_QUERY_ITEM_SAVE_ERROR;
import static br.com.oi.sgis.util.MessageUtils.GENERIC_QUERY_ITEM_DELETE_ERROR;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GenericQueryItemService {

    private final GenericQueryItemRepository genericQueryItemRepository;
    private static final GenericQueryItemMapper genericQueryItemMapper = GenericQueryItemMapper.INSTANCE;

    public void save(GenericQueryItemDTO dto){
        try {
            genericQueryItemRepository.save(genericQueryItemMapper.toModel(dto));
        } catch (Exception e) {
            throw new IllegalArgumentException(GENERIC_QUERY_ITEM_SAVE_ERROR.getDescription());
        }
    }

    public void delete(GenericQueryItem genericQueryItem){
        try {
            genericQueryItemRepository.delete(genericQueryItem);
        } catch (Exception e){
            throw new IllegalArgumentException(GENERIC_QUERY_ITEM_DELETE_ERROR.getDescription());
        }
    }
}
