package com.realestatefinder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RealEstateFinder {
    public static final String DATABASE_CONNECTION_STRING = "jdbc:sqlite:src/main/resources/RealEstateFinder.db";
    private static final Logger logger = LogManager.getLogger(RealEstateFinder.class.getName());

    public static void main(String[] args) throws IOException, SQLException {
//        List<Offer> olxOffers = getOlxOffers();
//
//        List<Offer> dbOffers = getOffersFromDB();
//        removeDuplicateOffers(olxOffers, dbOffers);
//        insertOffersInDB(olxOffers);

//        updateNeighbourhoodsInDB(offers);
//        sendOffersGroupedByNeighbourhood(offers);
//        getOffersFromImobiliare();
//        insertOffersInDB();

//        System.out.println(offers.stream().sorted((o1, o2) -> (int) (o1.getPricePerSquareMeter() - o2.getPricePerSquareMeter())).limit(100).map(Offer::toString).collect(Collectors.joining("\n")));
//        System.out.println("offers = " + olxOffers);
//        System.out.println("offers.size() = " + offers.size());
    }

    public static void removeDuplicateOffers(List<Offer> olxOffers, List<Offer> dbOffers) {
        Iterator<Offer> iterator = olxOffers.iterator();
        while (iterator.hasNext()) {
            Offer olxOffer = iterator.next();
            int dbOfferIndex = dbOffers.indexOf(olxOffer);
            if (dbOfferIndex > -1) {
                iterator.remove();
                logger.info("Ignoring duplicate offer: " + dbOffers.get(dbOfferIndex).getLink() + " & " + olxOffer.getLink());
            }
        }
    }


    private static void updateNeighbourhoodsInDB(List<Offer> offers) throws SQLException {
        Connection c = DriverManager.getConnection(DATABASE_CONNECTION_STRING);
        PreparedStatement updateNeighbourhood = c.prepareStatement("UPDATE OFFER SET NEIGHBOURHOOD = ? WHERE ID = ?");
        offers.stream().forEach(o -> {
            try {
                updateNeighbourhood.setString(1, o.getNeighbourhood().getNeighbourhoodName());
                updateNeighbourhood.setString(2, o.getId());
                updateNeighbourhood.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        c.close();
    }

    public static void insertOffersInDB(List<Offer> offers) throws SQLException {
        Connection c = DriverManager.getConnection(DATABASE_CONNECTION_STRING);
        PreparedStatement insertOffer = c.prepareStatement("INSERT INTO OFFER VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement insertPrice = c.prepareStatement("INSERT INTO OFFER_PRICE(OFFER_ID, PRICE, DATE) VALUES(?, ?, ?)");
        offers.stream().forEach(o -> {
            try {
                insertOffer.setString(1, o.getId());
                insertOffer.setString(2, o.getLink());
                insertOffer.setString(3, o.getTitle());
                insertOffer.setInt(4, o.getRooms());
                insertOffer.setDouble(5, o.getSurface());
                insertOffer.setString(6, o.getFloor());
                insertOffer.setString(7, o.getRoomStructure());
                insertOffer.setString(8, o.getNeighbourhood().getNeighbourhoodName());
                insertOffer.executeUpdate();

                insertPrice.setString(1, o.getId());
                insertPrice.setInt(2, o.getPrice());
                insertPrice.setString(3, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                insertPrice.executeUpdate();

            } catch (SQLException e) {
                logger.error(e);
            }
        });
        c.close();
        logger.info("Inserted " + offers.size() + " new offers.");
    }


    private static void sendOffersGroupedByNeighbourhood(List<Offer> offers) {
        Map<ClujNeighbourhood, List<Offer>> offersByNeighbourhood = offers.stream().sorted((o1, o2) -> (int) (o1.getPricePerSquareMeter() - o2.getPricePerSquareMeter())).collect(Collectors.groupingBy(Offer::getNeighbourhood));
        MailSender.sendMail("Oferte 12.08.2015", offersByNeighbourhood.entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey())).map(entry -> "<p><strong>" + entry.getKey() + "</strong> (" + entry.getValue().size() + " oferte)</p><p>--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</p>" + entry.getValue().stream().map(Offer::toString).collect(Collectors.joining("<br/>"))).collect((Collectors.joining("<br/><br/>"))));
    }

    public static List<Offer> getOffersFromDB() throws SQLException {
        List<Offer> offers = new ArrayList<>();
        Connection connection = DriverManager.getConnection(DATABASE_CONNECTION_STRING);
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM OFFER");
        PreparedStatement priceStatement = connection.prepareStatement("SELECT PRICE FROM OFFER_PRICE WHERE OFFER_ID = ?");
        while (resultSet.next()) {
            Offer offer = new Offer();
            offer.setId(resultSet.getString("ID"));
            offer.setLink(resultSet.getString("LINK"));
            offer.setTitle(resultSet.getString("TITLE"));
            offer.setRooms(resultSet.getInt("ROOMS"));
            offer.setSurface(resultSet.getDouble("SURFACE"));
            offer.setFloor(resultSet.getString("FLOOR"));
            offer.setRoomStructure(resultSet.getString("ROOM_STRUCTURE"));
            offer.setNeighbourhood(ClujNeighbourhood.getNeighbourhood(resultSet.getString("NEIGHBOURHOOD")));
            priceStatement.setString(1, offer.getId());
            ResultSet priceResultSet = priceStatement.executeQuery();
            priceResultSet.next();
            offer.setPrice(priceResultSet.getInt("PRICE"));

            offers.add(offer);
        }
        connection.close();
        return offers;
    }
}
