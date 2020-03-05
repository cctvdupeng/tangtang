package me.zohar.runscore.merchant.service;

import java.util.Arrays;
import java.util.List;

import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.merchant.repo.MerchantOrderRepo;
import me.zohar.runscore.merchant.vo.MyWaitConfirmOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.util.NumberUtil;
import me.zohar.runscore.merchant.domain.FreezeRecord;
import me.zohar.runscore.merchant.domain.MerchantOrder;
import me.zohar.runscore.merchant.repo.FreezeRecordRepo;

@Service
public class FreezeRecordService {

	@Autowired
	private FreezeRecordRepo freezeRecordRepo;

	@Autowired
	private MerchantOrderRepo merchantOrderRepo;

	@Transactional(readOnly = true)
	public double getFreezeAmount(String userAccountId) {
		double freezeAmount = 0d;
		List<FreezeRecord> freezeRecords = freezeRecordRepo.findByDealTimeIsNullAndReceivedAccountId(userAccountId);
		for (FreezeRecord freezeRecord : freezeRecords) {
			MerchantOrder merchantOrder = freezeRecord.getMerchantOrder();
			if (merchantOrder == null) {
				continue;
			}
			freezeAmount += merchantOrder.getGatheringAmount();
		}
		List<MyWaitConfirmOrderVO> myWaitConfirmOrderVOS = MyWaitConfirmOrderVO
				.convertFor(merchantOrderRepo.findByOrderStateInAndReceivedAccountIdOrderBySubmitTimeDesc(
							Arrays.asList(Constant.商户订单状态_已接单), userAccountId));
		for (MyWaitConfirmOrderVO vo : myWaitConfirmOrderVOS) {
			if (vo == null) {
				continue;
			}
			freezeAmount += vo.getGatheringAmount();
		}
		return NumberUtil.round(freezeAmount, 4).doubleValue();
	}

}
