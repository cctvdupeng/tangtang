package me.zohar.runscore.chatkf.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Table(name = "chat_kf")
@Getter
@Setter
@Entity
@DynamicInsert(true)
@DynamicUpdate(true)
public class ChatKf {
	
	@javax.persistence.Id
	@Column(name="id")
	private String Id;

	@Column(name="user_account_id")
	private String userAccountId;
	
	@Column(name="user_name")
	private String userName;
	
	@Column(name="create_time")
	private Date createTime;
	
	@Column(name="state")
	private Integer state;



	@Override
	public String toString() {
		return "chatKf [Id=" + Id + ", userAccountId=" + userAccountId + ", userName=" + userName + ", createTime="
				+ createTime + ", state=" + state + "]";
	}
	

}
