package br.com.oi.sgis.service.factory;

import br.com.oi.sgis.dto.UnityDTO;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.UnityException;

public interface UnityFactory {


    Unity makeNewSpareUnity(UnityDTO unityDTO) throws UnityException;

    Unity makeRemovedFromSite(UnityDTO unityDTO);
}
