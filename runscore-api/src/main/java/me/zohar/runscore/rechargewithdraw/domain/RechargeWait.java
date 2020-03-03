package me.zohar.runscore.rechargewithdraw.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "recharge_wait")
@DynamicInsert(true)
@DynamicUpdate(true)
public class RechargeWait{


	/**
	 * 主键id
	 */
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name="account_name")
	private String accountName;
	
	@Column(name = "state")
	private Integer state;
	
	@Column(name = "create_time")
	private Date createTime;
	
	@Column(name = "amount")
	private Integer amount;

	@Override
	public String toString() {
		return "RechargeWait [id=" + id + ", userName=" + userName + ", state=" + state + ", createTime=" + createTime
				+ ", amount=" + amount + "]";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
}
