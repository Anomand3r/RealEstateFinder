package com.realestatefinder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Offer {
    private static final Logger logger = LogManager.getLogger(Offer.class.getName());

    private String id;
    private String link;
    private String title;
    private int price;
    private int rooms;
    private double surface;
    private String floor;
    private String roomStructure;
    private ClujNeighbourhood neighbourhood;

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
        return "<p><a href='" + link + "'>" + title + "</a>: " + price + " EUR / suprafata: " + surface + " / camere: " + rooms + " / " + floor + " / " + roomStructure + " / pret/mp: " + getPricePerSquareMeter() + "</p>";
//        return "Offer{" +
//                "id='" + id + '\'' +
//                ", link='" + link + '\'' +
//                ", title='" + title + '\'' +
//                ", price=" + price +
//                ", rooms=" + rooms +
//                ", surface=" + surface +
//                ", floor='" + floor + '\'' +
//                ", roomStructure='" + roomStructure + '\'' +
//                ", pricePerSquareMeter=" + getPricePerSquareMeter() +
//                '}';
    }

    public double getPricePerSquareMeter() {
        return getPrice() / surface;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ClujNeighbourhood getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(ClujNeighbourhood neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Offer offer = (Offer) o;
        if (id.equals(offer.getId())) {
            if (price != offer.price) {
                logger.info("PRICE UPDATE DETECTED: " + id + " - " + price + " -> " + offer.price);
                return false;
            }
            return true;
        }

        if (price != offer.price) return false;
        if (rooms != offer.rooms) return false;
        if (Double.compare(offer.surface, surface) != 0) return false;
        if (roomStructure != null ? !roomStructure.equals(offer.roomStructure) : offer.roomStructure != null)
            return false;
        return !(neighbourhood != null ? !neighbourhood.equals(offer.neighbourhood) : offer.neighbourhood != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = price;
        result = 31 * result + rooms;
        temp = Double.doubleToLongBits(surface);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (roomStructure != null ? roomStructure.hashCode() : 0);
        result = 31 * result + (neighbourhood != null ? neighbourhood.hashCode() : 0);
        return result;
    }
}
