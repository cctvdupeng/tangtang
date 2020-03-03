package me.zohar.runscore.chatkf.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.zohar.runscore.chatkf.domain.ChatContent;
import me.zohar.runscore.chatkf.repo.ChatKfRepo;

@Validated
@Service
public class ChatContentService {
	
	@Autowired
	ChatKfRepo chatKfRepo;
	private @Autowired EntityManager em;

	@SuppressWarnings("unchecked")
	public List<ChatContent> findContentByUserName(String userName,Integer state) {
		
		String sql = " select * from chat_content where user_name = '"+userName+"' and type= 0 and state ="+state+" order by create_time desc limit 1";
		Query query = em.createNativeQuery(sql,ChatContent.class);
		List<ChatContent> list =query.getResultList();
		System.out.println("sql:"+sql);
		return list;
	}



}
