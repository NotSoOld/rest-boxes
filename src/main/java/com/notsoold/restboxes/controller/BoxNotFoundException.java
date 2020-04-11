package com.notsoold.restboxes.controller;

public class BoxNotFoundException extends RuntimeException {

    public BoxNotFoundException(Long id) {
	super("Could not find box with id " + id);
    }
}
