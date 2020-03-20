package net.mrkaan.printer.model;


import com.firebase.geofire.GeoLocation;


public class Cafe {
    private GeoLocation location;
    private String cafeId, name, phone, email, username;
    private Response queue, queueOk, queueCancelled;
    private Boolean state;

    public Response getQueueCancelled() {
        return queueCancelled;
    }

    public void setQueueCancelled(Response queueCancelled) {
        this.queueCancelled = queueCancelled;
    }


    public Response getQueueOk() {
        return queueOk;
    }

    public void setQueueOk(Response queueOk) {
        this.queueOk = queueOk;
    }


    public String getCafeId() {
        return cafeId;
    }

    public void setCafeId(String cafeId) {
        this.cafeId = cafeId;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
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
            String username,
            String cafeId,
            String email,
            GeoLocation location,
            String name,
            String phone,
            Response queue,
            Response queueOk,
            Response queueCancelled,
            Boolean state) {
        setCafeId(cafeId);
        setEmail(email);
        setLocation(location);
        setName(name);
        setPhone(phone);
        setQueue(queue);
        setState(state);
        setQueueOk(queueOk);
        setUsername(username);
        setQueueCancelled(queueCancelled);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

