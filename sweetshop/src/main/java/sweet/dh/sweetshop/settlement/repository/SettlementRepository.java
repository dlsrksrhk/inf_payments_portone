package sweet.dh.sweetshop.settlement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sweet.dh.sweetshop.settlement.entity.Settlement;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

}