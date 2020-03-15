package net.mrkaan.printer.model;

import android.location.Location;

import java.util.Date;
import java.util.List;

class Response {
    List<Queue> items;

    public Response(List<Queue> items) {
        this.items = items;
    }
}

public class Queue {


    private int orderId, cafeId, userId;
    private Location geoLocation;
    private String pictureUrl, tableNo;
    private Boolean state, inCafe;
    //private Date time;
    private Long time;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCafeId() {
        return cafeId;
    }

    public void setCafeId(int cafeId) {
        this.cafeId = cafeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Location getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(Location geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Boolean getInCafe() {
        return inCafe;
    }

    public void setInCafe(Boolean inCafe) {
        this.inCafe = inCafe;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Queue() {
    }

    public Queue(
            int orderId,
            int cafeId,
            Location geoLocation,
            Boolean inCafe,
            String pictureUrl,
            Boolean state,
            Long time,
            String tableNo,
            int userId
    ) {
        setCafeId(cafeId);
        setUserId(userId);
        setGeoLocation(geoLocation);
        setPictureUrl(pictureUrl);
        setState(state);
        setTime(time);
        setTableNo(tableNo);
        setInCafe(inCafe);
        setOrderId(orderId);
    }
}
