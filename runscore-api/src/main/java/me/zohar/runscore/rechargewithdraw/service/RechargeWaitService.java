package me.zohar.runscore.rechargewithdraw.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.zohar.runscore.chatkf.domain.ChatKf;
import me.zohar.runscore.chatkf.repo.ChatKfRepo;
import me.zohar.runscore.rechargewithdraw.domain.RechargeWait;
import me.zohar.runscore.rechargewithdraw.domain.RechargeWaitSetting;
import me.zohar.runscore.rechargewithdraw.repo.RechargeWaitRepo;

@Validated
@Service
public class RechargeWaitService {

	@Autowired
	RechargeWaitRepo rechargeWaitRepo;
	private @Autowired EntityManager em;

	// 提交排队。
	@SuppressWarnings("unchecked")
	public void rechargeLineUp(String userName, String accountName, Integer amount) {
		String sql = " select * from recharge_wait where user_name = '" + userName + "' and state !=3 ";
		Query query = em.createNativeQuery(sql, RechargeWait.class);
		List<RechargeWait> list = query.getResultList();
		RechargeWait rechargeWait = new RechargeWait();
		if (list.size() == 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String date = sdf.format(new Date());
			rechargeWait.setId(date + Math.random() * 1000000);
			rechargeWait.setUserName(userName);
			rechargeWait.setAccountName(accountName);
			rechargeWait.setAmount(amount);
			rechargeWait.setState(0); // 状态0=正在排队，1=充值确定 ,2=正在充值
			rechargeWait.setCreateTime(new Date());
			rechargeWaitRepo.save(rechargeWait);
		}
		
	}

	@SuppressWarnings("unchecked")
	public List<RechargeWait> rechargeWaitRefresh(String userName) {

		String sql = " select * from recharge_wait where user_name='"+ userName + "' and state !=3";
		Query query = em.createNativeQuery(sql, RechargeWait.class);
		List<RechargeWait> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<RechargeWait> rechargeWaitRefresh1(String userName) {

		String sql = " select * from recharge_wait where state in(3) and amount >0 and create_time <= (select create_time from recharge_wait where user_name = '"
				+ userName + "' ORDER BY create_time desc LIMIT 1) ";
		Query query = em.createNativeQuery(sql, RechargeWait.class);
		List<RechargeWait> list = query.getResultList();
		System.out.println("sql:" + sql);
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<RechargeWait> findUserInfoByUserName(String userName) {
		String sql = " select * from recharge_wait where user_name = '" + userName + "' ";
		Query query = em.createNativeQuery(sql, RechargeWait.class);
		List<RechargeWait> list = query.getResultList();
		return list;
	}

	@Transactional
	public void updateRechargeState(RechargeWait rechargeWait, Integer state) {
		rechargeWait.setState(state);
		rechargeWaitRepo.save(rechargeWait);
	}

	@SuppressWarnings("unchecked")
	public RechargeWaitSetting findPageWord() {
		String sql = " select * from recharge_wait_setting ";
		Query query = em.createNativeQuery(sql, RechargeWaitSetting.class);
		List<RechargeWaitSetting> list = query.getResultList();
		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	public Integer getWaitNum(String userName) {
		String sql = " select * from recharge_wait  where state !=3  and create_time <= (select create_time from recharge_wait where user_name ='"+userName+"' !=3 limit 1 ) ";
		Query query = em.createNativeQuery(sql, RechargeWait.class);
		List<RechargeWait> list = query.getResultList();
		Integer num = list.size() +1;
		return num;
	}

}
