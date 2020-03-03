package me.zohar.runscore.chatkf.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import me.zohar.runscore.chatkf.domain.ChatContent;

public interface ChatContentRepo extends JpaRepository<ChatContent,String>,JpaSpecificationExecutor<ChatContent>{

}
