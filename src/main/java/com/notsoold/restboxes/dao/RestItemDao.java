package com.notsoold.restboxes.dao;

import com.notsoold.restboxes.model.RestItem;
import org.springframework.data.repository.CrudRepository;

public interface RestItemDao extends CrudRepository<RestItem, Long> {
}
