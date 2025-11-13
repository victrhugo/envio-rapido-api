package com.gft.envioapi.repository;

import com.gft.envioapi.entity.Frete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreteRepository extends JpaRepository<Frete, Long> {
    Optional<Frete> findByEnvioEnvioId(Long envioId);
}
