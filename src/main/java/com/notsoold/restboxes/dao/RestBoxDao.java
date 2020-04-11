package com.notsoold.restboxes.dao;

import com.notsoold.restboxes.model.RestBox;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RestBoxDao extends CrudRepository<RestBox, Long> {

    List<RestBox> findAllByContainedInEquals(RestBox containerBox);

}
