package edu.cnm.deepdive.coffeeshop.repository;

import edu.cnm.deepdive.coffeeshop.model.entity.Interest;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, UUID> {

  // TODO: 7/17/26 Add additional crud methods as needed.

  List<Interest> getAllByOrderByCategoryAsc();
}
