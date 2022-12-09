package br.com.oi.sgis.service;

import br.com.oi.sgis.entity.Uf;
import br.com.oi.sgis.repository.UfRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UfService {

    private final UfRepository ufRepository;

    public List<Uf> listAll(){
        return ufRepository.findAll().stream().sorted(Comparator.comparing(Uf::getId)).collect(Collectors.toList());
    }

}
