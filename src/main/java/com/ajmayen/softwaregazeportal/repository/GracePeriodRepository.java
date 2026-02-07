package com.ajmayen.softwaregazeportal.repository;

import com.ajmayen.softwaregazeportal.model.GracePeriodSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GracePeriodRepository extends JpaRepository<GracePeriodSetting,Long> {

    Optional<GracePeriodSetting> findTopByOrderByUpdatedAtDesc();

}
