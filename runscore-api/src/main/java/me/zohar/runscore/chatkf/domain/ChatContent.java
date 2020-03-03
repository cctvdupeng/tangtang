package me.zohar.runscore.chatkf.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Table(name = "chat_content")
@Getter
@Setter
@Entity
@DynamicInsert(true)
@DynamicUpdate(true)
public class ChatContent {
	
	@javax.persistence.Id
	@Column(name="id")
	private String Id;

	@Column(name="user_name")
	private String userName;

	@Column(name="content")
	private String content;
	
	@Column(name="create_time")
	private Date createTime;

	@Column(name="kf_name")
	private String kfName;

	@Column(name="back_content")
	private String backContent;
	
	@Column(name="state")
	private Integer state;

	@Column(name="type")
	private Integer type;

	@Column(name="flag")
	private Integer flag;

	@Override
	public String toString() {
		return "ChatContent [Id=" + Id + ", userName=" + userName + ", content=" + content + ", createTime="
				+ createTime + ", kfName=" + kfName + ", backContent=" + backContent + ", state=" + state + "]";
	}


	

}
