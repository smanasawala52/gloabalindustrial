package com.alpha.interview.wizard.repository.mall;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.interview.wizard.model.mall.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
