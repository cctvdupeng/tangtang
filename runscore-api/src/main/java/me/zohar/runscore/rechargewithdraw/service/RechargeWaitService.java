package me.zohar.runscore.rechargewithdraw.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.zohar.runscore.chatkf.domain.ChatKf;
import me.zohar.runscore.chatkf.repo.ChatKfRepo;
import me.zohar.runscore.rechargewithdraw.domain.RechargeWait;
import me.zohar.runscore.rechargewithdraw.repo.RechargeWaitRepo;

@Validated
@Service
public class RechargeWaitService {
	
	@Autowired
	RechargeWaitRepo rechargeWaitRepo;
	private @Autowired EntityManager em;

	//提交充值请求，
	public void rechargeLineUp(String userName ,String accountName, Integer amount) {
		String sql = " select * from recharge_wait where user_name = '" + userName + "' ";
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
		rechargeWait= list.get(0);
		rechargeWait.setState(0);
		rechargeWait.setAmount(amount);
		rechargeWait.setAccountName(accountName);
		rechargeWait.setCreateTime(new Date());

		rechargeWaitRepo.save(rechargeWait);


	}

	@SuppressWarnings("unchecked")
	public  List<RechargeWait> rechargeWaitRefresh(String userName) {
		
		String sql = " select * from recharge_wait where state = 0 and create_time <= (select create_time from recharge_wait where user_name = '"+userName+"' ORDER BY create_time desc LIMIT 1) ";
		Query query = em.createNativeQuery(sql, RechargeWait.class);
		List<RechargeWait> list = query.getResultList();
		return list;
	}

	public void updateRechargeState(String userName, Integer state) {
		String sql = " update recharge_wait set state =2 where userName ='"+userName+"'";
		
	}

	@SuppressWarnings("unchecked")
	public List<RechargeWait> findUserInfoByUserName(String userName) {
		String sql = " select * from recharge_wait where user_name = '" + userName + "' ";
		Query query = em.createNativeQuery(sql, RechargeWait.class);
		List<RechargeWait> list = query.getResultList();
		return list;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
