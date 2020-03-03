package me.zohar.runscore.chatkf.controller;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;

import me.zohar.runscore.chatkf.domain.ChatContent;
import me.zohar.runscore.chatkf.domain.ChatKf;
import me.zohar.runscore.chatkf.repo.ChatContentRepo;
import me.zohar.runscore.chatkf.repo.ChatKfRepo;
import me.zohar.runscore.chatkf.service.ChatContentService;
import me.zohar.runscore.chatkf.service.ChatKfService;
import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.useraccount.domain.LoginLog;
import me.zohar.runscore.useraccount.domain.UserAccount;
import me.zohar.runscore.useraccount.repo.LoginLogRepo;
import me.zohar.runscore.useraccount.repo.UserAccountRepo;

@Controller
@RequestMapping(value = "/chatKf")
public class chatKfController {

	@Autowired
	ChatKfService chatKfService;
	@Autowired
	LoginLogRepo loginLogRepo;
	@Autowired
	UserAccountRepo userAccountRepo;
	@Autowired
	ChatKfRepo chatKfRepo;
	@Autowired
	ChatContentRepo chatContentRepo;
	@Autowired
	ChatContentService chatContentService;

	/**
	 * 点击客服，如果不是你则显示等待人数，如果排队到你则显示聊天页面。
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/getWaitInfo")
	@ResponseBody
	public Result getWaitNum() {
		// 获取用户sessionId,通过session获取用户信息
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		LoginLog loginLog = new LoginLog();
		loginLog = loginLogRepo.findTopBySessionIdOrderByLoginTime(sessionId);
		
		UserAccount userAccount = new UserAccount();
		userAccount = userAccountRepo.findByIdAndDeletedFlagIsFalse(loginLog.getId());
		System.out.println("userAccount:" + userAccount);

		ChatKf chatKf = new ChatKf();
		List<ChatKf> list = chatKfService.selectOnebyId(loginLog.getUserName());

		System.out.println("list:" + list);
		// 如果表中没此用户则创建排队
		if (list.size() == 0) {
			chatKf.setCreateTime(new Date());
			chatKf.setId(loginLog.getId());
			chatKf.setState(0);
			chatKf.setUserAccountId(loginLog.getId());
			chatKf.setUserName(loginLog.getUserName());
			chatKfRepo.save(chatKf);
		}
		// 如果表中有此用户，但已经结束聊天，则激活排队。
		else if (list.get(0).getState() == 2) {
			chatKf = (ChatKf) list.get(0);
			chatKf.setCreateTime(new Date());
			chatKf.setState(0);
			chatKfRepo.save(chatKf);
		}
		// 如果state=1，则正在聊天。
		else if (list.get(0).getState() == 1) {
			return Result.success();
		}
		// 显示排队人数。
		List user = chatKfService.getWaitNum(loginLog.getUserName());
		return Result.success().setData(user);
	}

	/**
	 * 获取聊天记录列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getChatRecord")
	@ResponseBody
	public Result getChatRecord() {

		List<ChatKf> user = chatKfService.getChatingUser(1);
		List<ChatKf> user1 = chatKfService.getChatingUser(0);
		for(ChatKf chatKf : user1) {
			user.add(chatKf);
		}
		return Result.success().setData(user);
	}

	/**
	 * 获取未读数量
	 */
//	@RequestMapping(value="/getNum")
//	@ResponseBody
	
	
	/**
	 * 更改用户聊天状态
	 */
	@RequestMapping(value = "/updateChatState")
	@ResponseBody
	public Result inChat(Integer state,String userName) {
		// state =1接入聊天，=2 结束聊天。
		// 获取用户sessionId,通过session获取用户信息
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		LoginLog loginLog = new LoginLog();
		loginLog = loginLogRepo.findTopBySessionIdOrderByLoginTime(sessionId);

		if(userName==null && state ==2) {
			userName = loginLog.getUserName();
		}
		
		ChatKf chatKf = new ChatKf();
		List<ChatKf> list = chatKfService.selectOnebyId(userName);
		chatKf = (ChatKf) list.get(0);
		chatKf.setState(state);
		chatKfRepo.save(chatKf);
		return Result.success();
	}
	
	
	
	/**
	 * 点击发送，存入信息
	 */
	@RequestMapping(value="/sendMs")
	@ResponseBody
	public Result sendMs(String content,Integer type,String userName,String kfName) {
		// 获取用户sessionId,通过session获取用户信息
		//type =1 问题，=2 回复
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		LoginLog loginLog = new LoginLog();
		loginLog = loginLogRepo.findTopBySessionIdOrderByLoginTime(sessionId);
		String name = loginLog.getUserName();
		
		ChatContent chatContent = new ChatContent();
		chatContent.setId(loginLog.getId()+Math.random()*10000000);
		if(type ==1) {
			chatContent.setContent(content);
			chatContent.setKfName(kfName);
			chatContent.setUserName(name);
			chatContent.setState(1);
			chatContent.setType(0);
		}
		if(type ==2) {
			chatContent.setBackContent(content);
			chatContent.setUserName(userName);
			chatContent.setKfName(name);
			chatContent.setState(2);
			chatContent.setType(0);
		}
		chatContent.setCreateTime(new Date());
		chatContentRepo.save(chatContent);
		
		return Result.success();
	}
	
	/**
	 * 获取信息
	 */
	@RequestMapping(value="/getChatContent")
	@ResponseBody
	public Result getChatContent(String userName) {
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		LoginLog loginLog = new LoginLog();
		loginLog = loginLogRepo.findTopBySessionIdOrderByLoginTime(sessionId);
		
		userName =loginLog.getUserName();
		
		List<ChatContent> list = chatContentService.findContentByUserName(userName,2);
		
		//给他标记已经读取状态
		if(!list.isEmpty()) {
			//给他标记已经读取状态
			for(ChatContent chatContet:list) {
				chatContet.setType(1);
				chatContentRepo.save(chatContet);
			}
		}
		
		return Result.success().setData(list);
	}
	
	
	
	
	
	
	
	
	
	

}
