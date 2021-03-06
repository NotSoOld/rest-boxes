package com.notsoold.restboxes.controller;

import com.notsoold.restboxes.dao.RestBoxDao;
import com.notsoold.restboxes.dao.RestItemDao;
import com.notsoold.restboxes.model.JsonRestInput;
import com.notsoold.restboxes.model.RestBox;
import com.notsoold.restboxes.model.RestItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GenericRestController {

    private RestBoxDao restBoxDao;
    private RestItemDao restItemDao;

    @Autowired
    public GenericRestController(RestBoxDao restBoxDao, RestItemDao restItemDao) {
	this.restBoxDao = restBoxDao;
	this.restItemDao = restItemDao;
    }

    /**
     * Performs a deep search of all items with supplied {@code color} inside the box with supplied {@code id}.
     * @param inputObject JSON with 'box' and 'color' parameters (i.e. {"box":"1", "color":"red"})
     * @return ids of all found items in square brackets, delimited by comma: [], [1], [1,2,3]
     */
    @PostMapping("/test")
    @ResponseBody
    String getItemsInBoxByColor(@RequestBody JsonRestInput inputObject) {
        List<Long> resultingItemsIds = new ArrayList<>();

	RestBox startBox = restBoxDao.findById(inputObject.getBox()).orElseThrow(
			() -> new BoxNotFoundException(inputObject.getBox()));

	for (RestBox containedBox: restBoxDao.findAllByContainedInEquals(startBox)) {
	    // Search through boxes inside startBox.
	    resultingItemsIds.addAll(getItemsInBoxByColorRecursively(containedBox, inputObject.getColor()));
	}
	// Add all suitable items which are just inside startBox.
	resultingItemsIds.addAll(restItemDao.findAllByContainedInEqualsAndColorEquals(startBox, inputObject.getColor())
			.stream().map(RestItem::getId).collect(Collectors.toList()));

	// Sort items' ids and make a return string.
	resultingItemsIds.sort(Comparator.naturalOrder());
	return "[" + String.join(",", resultingItemsIds.stream().map(item -> item + "").collect(Collectors.toList())) + "]";
    }

    /**
     * Helper to perform the recursive search for the method above.
     */
    private List<Long> getItemsInBoxByColorRecursively(RestBox startBox, String color) {
	List<Long> resultingItemsIds = new ArrayList<>();

	for (RestBox containedBox: restBoxDao.findAllByContainedInEquals(startBox)) {
	    resultingItemsIds.addAll(getItemsInBoxByColorRecursively(containedBox, color));
	}
	resultingItemsIds.addAll(restItemDao.findAllByContainedInEqualsAndColorEquals(startBox, color)
			.stream().map(RestItem::getId).collect(Collectors.toList()));

	return resultingItemsIds;
    }


}
