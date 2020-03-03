package me.zohar.runscore.rechargewithdraw.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import me.zohar.runscore.rechargewithdraw.domain.RechargeWait;

public interface RechargeWaitRepo extends JpaRepository<RechargeWait, String>, JpaSpecificationExecutor<RechargeWait> {



}
