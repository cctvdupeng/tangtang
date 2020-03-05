package me.zohar.runscore.chatkf.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.zohar.runscore.chatkf.domain.ChatKf;
import me.zohar.runscore.chatkf.repo.ChatKfRepo;

@Validated
@Service
public class ChatKfService {

	@Autowired
	ChatKfRepo chatKfRepo;
	private @Autowired EntityManager em;

	// 获取聊天等待人数。
	@SuppressWarnings("rawtypes")
	public List getWaitNum(String userName) {
		String sql = "select * from chat_kf WHERE state =0 and create_time <= (select create_time from chat_kf WHERE user_name = '"+userName+"')";
		Query query = em.createNativeQuery(sql, ChatKf.class);
		return query.getResultList();
	}

	//根据Id获取信息
	@SuppressWarnings("unchecked")
	public List<ChatKf> selectOnebyId(String userName) {
		String sql = "select * from chat_kf where user_name ='"+userName+"'";
		Query query = em.createNativeQuery(sql, ChatKf.class);
		List<ChatKf> list = query.getResultList();
		return list;
	}

	//获取聊天的用户
	@SuppressWarnings("unchecked")
	public List<ChatKf> getChatingUser(Integer i) {
		String sql ="select * from chat_kf where state="+i+"";
		Query query = em.createNativeQuery(sql, ChatKf.class);
		List<ChatKf> list = query.getResultList();
		return list;
	}


}
