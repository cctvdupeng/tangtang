package me.zohar.runscore.chatkf.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import me.zohar.runscore.chatkf.domain.ChatKf;

public interface ChatKfRepo extends JpaRepository<ChatKf,String>,JpaSpecificationExecutor<ChatKf>{

}
