package me.zohar.runscore.rechargewithdraw.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import me.zohar.runscore.rechargewithdraw.domain.RechargeWait;
import me.zohar.runscore.rechargewithdraw.domain.RechargeWaitSetting;

public interface RechargeWaitSettingRepo extends JpaRepository<RechargeWaitSetting, String>, JpaSpecificationExecutor<RechargeWaitSetting> {



}
