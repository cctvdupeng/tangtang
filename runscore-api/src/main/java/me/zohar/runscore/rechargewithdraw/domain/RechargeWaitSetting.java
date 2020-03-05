package me.zohar.runscore.rechargewithdraw.domain;

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
@Table(name = "recharge_wait_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
public class RechargeWaitSetting{


	/**
	 * 主键id
	 */
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "time")
	private Integer time;
	
	@Column(name = "page_content")
	private String pageContent;

	@Override
	public String toString() {
		return "RechargeWaitSetting [id=" + id + ", time=" + time + ", pageContent=" + pageContent + "]";
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
}
