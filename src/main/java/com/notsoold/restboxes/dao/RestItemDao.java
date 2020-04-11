package com.notsoold.restboxes.dao;

import com.notsoold.restboxes.model.RestBox;
import com.notsoold.restboxes.model.RestItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RestItemDao extends CrudRepository<RestItem, Long> {

    List<RestItem> findAllByContainedInEqualsAndColorEquals(RestBox containerBox, String color);

}
