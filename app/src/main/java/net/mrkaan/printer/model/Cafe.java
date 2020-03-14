package net.mrkaan.printer.model;


import android.location.Location;


import java.util.List;

public class Cafe {
    private int cafeId;
    private Location location;
    private String name, phone, email;
    private Response queue;

    public Response getQueueOk() {
        return queueOk;
    }

    public void setQueueOk(Response queueOk) {
        this.queueOk = queueOk;
    }

    private Response queueOk;
    private Boolean state;

    public int getCafeId() {
        return cafeId;
    }

    public void setCafeId(int cafeId) {
        this.cafeId = cafeId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public Response getQueue() {
        return queue;
    }

    public void setQueue(Response queue) {
        this.queue = queue;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Cafe(
            int cafeId,
            String email,
            Location location,
            String name,
            String phone,
            Response queue,
            Response queueOk,
            Boolean state) {
        setCafeId(cafeId);
        setEmail(email);
        setLocation(location);
        setName(name);
        setPhone(phone);
        setQueue(queue);
        setState(state);
        setQueueOk(queueOk);
    }

}

