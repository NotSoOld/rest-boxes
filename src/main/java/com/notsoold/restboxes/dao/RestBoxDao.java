package com.notsoold.restboxes.dao;

import com.notsoold.restboxes.model.RestBox;
import org.springframework.data.repository.CrudRepository;

public interface RestBoxDao extends CrudRepository<RestBox, Long> {
}
