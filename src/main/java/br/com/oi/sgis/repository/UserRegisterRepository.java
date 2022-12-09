package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.UserRegister;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRegisterRepository extends JpaRepository<UserRegister, String>, UserRegisterRepositoryCustom {
}
