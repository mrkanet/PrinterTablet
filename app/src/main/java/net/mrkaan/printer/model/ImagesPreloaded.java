package net.mrkaan.printer.model;

public class ImagesPreloaded {
    public String getImgUrl() { return imgUrl; }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    private String imgUrl, imgName;

    public ImagesPreloaded() {}

    public ImagesPreloaded(String url, String name) {
        imgName = name;
        imgUrl = url;
    }


}
