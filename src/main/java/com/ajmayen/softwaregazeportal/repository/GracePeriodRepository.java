package com.ajmayen.softwaregazeportal.repository;

import com.ajmayen.softwaregazeportal.model.GracePeriodSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GracePeriodRepository extends JpaRepository<GracePeriodSetting,Long> {

    GracePeriodSetting findTopByOrderByUpdatedAtDesc();
}
