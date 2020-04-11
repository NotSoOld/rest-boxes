package com.notsoold.restboxes.model;

import javax.persistence.*;

@Entity
@Table(name = "Item")
public class RestItem {

    @Id
    @Column(name = "item_id", nullable = false)
    private Long id;

    @Column(name = "color", length = 100)
    private String color;

    @ManyToOne(targetEntity = RestBox.class)
    @JoinColumn(name = "contained_in")
    private RestBox containedIn;

    public RestItem() {}

    public RestItem(Long id, RestBox containedIn) {
        this.id = id;
        if (containedIn != null) {
	    this.containedIn = containedIn;
	}
    }

    public RestItem(Long id, String color, RestBox containedIn) {
        this.id = id;
        this.color = color;
        if (containedIn != null) {
	    this.containedIn = containedIn;
	}
    }

    public Long getId() {
	return id;
    }

    public String getColor() {
	return color;
    }

    public void setColor(String color) {
	this.color = color;
    }

    public RestBox getContainedIn() {
	return containedIn;
    }

    public void setContainedIn(RestBox containedIn) {
	this.containedIn = containedIn;
    }

}
