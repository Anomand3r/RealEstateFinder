package com.realestatefinder;

public class Offer {
    private String id;
    private String link;
    private String title;
    private int price;
    private int rooms;
    private double surface;
    private String floor;
    private String roomStructure;

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public double getSurface() {
        return surface;
    }

    public void setSurface(double surface) {
        this.surface = surface;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoomStructure() {
        return roomStructure;
    }

    public void setRoomStructure(String roomStructure) {
        this.roomStructure = roomStructure;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", rooms=" + rooms +
                ", surface=" + surface +
                ", floor='" + floor + '\'' +
                ", roomStructure='" + roomStructure + '\'' +
                ", pricePerSquareMeter=" + getPricePerSquareMeter() +
                '}';
    }

    public double getPricePerSquareMeter() {
        return price / surface;
    }
}
