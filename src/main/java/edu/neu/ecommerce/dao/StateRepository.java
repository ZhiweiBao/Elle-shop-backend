package edu.neu.ecommerce.dao;

import edu.neu.ecommerce.entity.State;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface StateRepository extends JpaRepository<State, Integer> {

  List<State> findByCountryCode(@Param("code") String code);
}
