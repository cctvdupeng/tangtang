package me.zohar.runscore.admin.chatkf.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.util.ArrayUtil;
import me.zohar.runscore.chatkf.domain.ChatContent;
import me.zohar.runscore.chatkf.domain.ChatKf;
import me.zohar.runscore.chatkf.repo.ChatContentRepo;
import me.zohar.runscore.chatkf.repo.ChatKfRepo;
import me.zohar.runscore.chatkf.service.ChatContentService;
import me.zohar.runscore.chatkf.service.ChatKfService;
import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.storage.service.StorageService;
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
	@Autowired
	StorageService storageService;

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
		
		ChatKf chatKf = new ChatKf();
		List<ChatKf> list = chatKfService.selectOnebyId(userName);
		chatKf = (ChatKf) list.get(0);
		chatKf.setState(state);
		chatKfRepo.save(chatKf);
		
		ChatContent chatContent = new ChatContent();
		chatContent.setId(loginLog.getId()+Math.random()*10000000);
		chatContent.setCreateTime(new Date());
		chatContent.setBackContent("您好，请问有什么可以帮助你的");
		if(state ==2) {
			chatContent.setFlag(1);
			chatContent.setBackContent("本次会话已经结束，祝您生活愉快。");
		}
		chatContent.setKfName(loginLog.getUserName());
		chatContent.setUserName(userName);
		chatContent.setState(2);
		chatContent.setType(0);
		chatContentRepo.save(chatContent);
		
		
		return Result.success();
	}
	
	
	
	/**
	 * 点击发送，存入信息
	 * @throws IOException 
	 */
	@RequestMapping(value="/sendMs",method = RequestMethod.POST)
	@ResponseBody
	public Result sendMs(String content,Integer type,String userName,String kfName) throws IOException {
		// 获取用户sessionId,通过session获取用户信息
		//state =1 问题，=2 回复
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		LoginLog loginLog = new LoginLog();
		loginLog = loginLogRepo.findTopBySessionIdOrderByLoginTime(sessionId);
		String name = loginLog.getUserName();
		
		ChatContent chatContent = new ChatContent();
		chatContent.setId(loginLog.getId()+Math.random()*10000000);


			chatContent.setBackContent(content);
			chatContent.setUserName(userName);
			chatContent.setKfName(name);
			chatContent.setState(2);
			chatContent.setType(0);
			chatContent.setFlag(0);
		
		chatContent.setCreateTime(new Date());
		chatContentRepo.save(chatContent);
		
		return Result.success();
	}
	
	/**
	 * 上传图片
	 * @throws IOException 
	 */
	@RequestMapping(value="/uploadPhoto")
	@ResponseBody
	public Result uploadPic(@RequestParam("file_data") MultipartFile[] files) throws IOException {
		if (ArrayUtil.isEmpty(files)) {
			return Result.fail("请选择要上传的图片");
		}
		String storageIds = null;
		for (MultipartFile file : files) {
			String filename = file.getOriginalFilename();
			String storageId = storageService.uploadGatheringCode(file.getInputStream(), file.getSize(),
					file.getContentType(), filename);
			storageIds=storageId;
		}
		String url = "storage/fetch/"+storageIds;
		return Result.success().setData(url);
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
		
		//查询出最新一条信息
		List<ChatContent> list = chatContentService.findContentByUserName(userName,1);
		
		//给他标记已经读取状态
		if(!list.isEmpty()) {
			ChatContent chatContet = list.get(0);
			chatContet.setType(1);
			chatContentRepo.save(chatContet);
		}
		System.out.println(list);
		return Result.success().setData(list);
	}
	
	
	
	
	
	
	
	
	
	
	

}
