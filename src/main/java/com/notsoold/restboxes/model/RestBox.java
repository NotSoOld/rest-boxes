package com.notsoold.restboxes.model;

import javax.persistence.*;

@Entity
@Table(name = "Box")
public class RestBox {

    @Id
    @Column(name = "box_id", nullable = false)
    private Long id;

    @ManyToOne(targetEntity = RestBox.class)
    @JoinColumn(name = "contained_in")
    private RestBox containedIn;

    public RestBox() {}

    public RestBox(Long id, RestBox containedIn) {
        this.id = id;
        if (containedIn != null) {
            this.containedIn = containedIn;
        }
    }

    public Long getId() {
        return id;
    }

    public RestBox getContainedIn() {
        return containedIn;
    }

    public void setContainedIn(RestBox containedIn) {
        this.containedIn = containedIn;
    }
}
