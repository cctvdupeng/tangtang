package me.zohar.runscore.rechargewithdraw.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;

import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.rechargewithdraw.domain.RechargeWait;
import me.zohar.runscore.rechargewithdraw.domain.RechargeWaitSetting;
import me.zohar.runscore.rechargewithdraw.param.LowerLevelRechargeOrderQueryCondParam;
import me.zohar.runscore.rechargewithdraw.param.RechargeOrderParam;
import me.zohar.runscore.rechargewithdraw.repo.RechargeWaitRepo;
import me.zohar.runscore.rechargewithdraw.service.PayChannelService;
import me.zohar.runscore.rechargewithdraw.service.RechargeService;
import me.zohar.runscore.rechargewithdraw.service.RechargeWaitService;
import me.zohar.runscore.useraccount.domain.LoginLog;
import me.zohar.runscore.useraccount.repo.LoginLogRepo;
import me.zohar.runscore.useraccount.vo.UserAccountDetails;

/**
 *
 * @author zohar
 * @date 2019年1月21日
 *
 */
@Controller
@RequestMapping("/recharge")
public class RechargeController {

	@Autowired
	private RechargeService rechargeService;

	@Autowired
	private PayChannelService payChannelService;

	@Autowired
	RechargeWaitService rechargeWaitService;

	@Autowired
	LoginLogRepo loginLogRepo;

	@Autowired
	RechargeWaitRepo rechargeWaitRepo;
	

	/**
	 * 充值提交
	 * @param userName
	 * @param amount
	 * @return
	 */
	@RequestMapping(value="/rechargeLineUp")
	@ResponseBody
	public Result rechargeLineUp(String accountName,Integer amount) {
		// 获取用户sessionId,通过session获取用户信息
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		if(sessionId ==null) {
			return Result.fail("登陆超时，请退出然后重新登陆。");
		}
		LoginLog loginLog = new LoginLog();
		loginLog = loginLogRepo.findTopBySessionIdOrderByLoginTime(sessionId);
		
		rechargeWaitService.rechargeLineUp(loginLog.getUserName() ,accountName,amount);
		return Result.success();
	}

	/**
	 * 充值等待刷新
	 * @param param
	 * @return
	 */
	@RequestMapping(value="/rechargeWaitRefresh")
	@ResponseBody
	public Result rechargeWaitRefresh(){
		// 获取用户sessionId,通过session获取用户信息
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		if(sessionId ==null) {
			return Result.success().setMsg("登陆超时，请退出然后重新登陆。");
		}
		LoginLog loginLog = new LoginLog();
		loginLog = loginLogRepo.findTopBySessionIdOrderByLoginTime(sessionId);
		
		List<RechargeWait> list = rechargeWaitService.rechargeWaitRefresh(loginLog.getUserName());

		Integer num = rechargeWaitService.getWaitNum(loginLog.getUserName());
		return Result.success().setData(list).setMsg(String.valueOf(num));
	}

	/**
	 * 更改充值等待状态
	 * 正在排队，state=0
	 * 排队完毕，state=1
	 * 正在充值，state=2
	 * 充值完毕，state=3
	 * 用户确认已经充值，state=4
	 * @param param
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/updateChargeWaitState")
	public Result updateChargeWaitState(Integer state) throws Exception{
		// 获取用户sessionId,通过session获取用户信息
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		if(sessionId ==null) {
			return Result.success().setMsg("登陆超时，请退出然后重新登陆。");
		}
		LoginLog loginLog = new LoginLog();
		loginLog = loginLogRepo.findTopBySessionIdOrderByLoginTime(sessionId);

		RechargeWait rechargeWait = new RechargeWait();
		List<RechargeWait> list = rechargeWaitService.findUserInfoByUserName(loginLog.getUserName());
		rechargeWait = list.get(0);
		rechargeWait.setState(state);
		if(state==2) {
			rechargeWait.setCreateTime(new Date());
		}
		rechargeWaitRepo.save(rechargeWait);
		return Result.success();
	}

	//获取充值页面显示的提示以及排队完毕的超时时间。
	@ResponseBody
	@RequestMapping(value="/getPageWord")	
	public Result getPageWord() {
		
		RechargeWaitSetting setting = rechargeWaitService.findPageWord();
		return Result.success().setData(setting);
	}
	


	@PostMapping("/generateRechargeOrder")
	@ResponseBody
	public Result generateRechargeOrder(RechargeOrderParam param) {
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setUserAccountId(user.getUserAccountId());
		return Result.success().setData(rechargeService.generateRechargeOrder(param));
	}

	@RequestMapping("/findEnabledPayType")
	@ResponseBody
	public Result findEnabledPayType() throws IOException {
		return Result.success().setData(payChannelService.findEnabledPayType());
	}

	@RequestMapping("/findEnabledPayChannel")
	@ResponseBody
	public Result findEnabledPayChannel() throws IOException {
		return Result.success().setData(payChannelService.findEnabledPayChannel());
	}

	@GetMapping("/findLowerLevelRechargeOrderByPage")
	@ResponseBody
	public Result findLowerLevelRechargeOrderByPage(LowerLevelRechargeOrderQueryCondParam param) {
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setCurrentAccountId(user.getUserAccountId());
		return Result.success().setData(rechargeService.findLowerLevelRechargeOrderByPage(param));
	}

}
