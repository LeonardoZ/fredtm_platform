package com.fredtm.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.fredtm.core.model.Activity;

public interface ActivityRepository extends CrudRepository<Activity, Long>{

}
