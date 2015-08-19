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
    public static final int AVERAGE_PRICE_PER_SQUARE_METER = 1022;
    private static final Logger logger = LogManager.getLogger(RealEstateFinder.class.getName());

    public static void main(String[] args) throws IOException, SQLException {
        List<Offer> offers = getOffersFromDB();
        MailSender.sendMail("Oferte 19.08.2015 +2 camere < 50000 (smart sort)", offers.stream().filter(o -> o.getPrice() <= 50000 && o.getRooms() > 1).sorted((o1, o2) -> (int) (Math.abs(AVERAGE_PRICE_PER_SQUARE_METER - o1.getPricePerSquareMeter()) - Math.abs(AVERAGE_PRICE_PER_SQUARE_METER - o2.getPricePerSquareMeter()))).map(Offer::toString).collect(Collectors.joining("<br/>")));
//        Map<ClujNeighbourhood, List<Offer>> offersByNeighbourhood = offers.stream().filter(o -> o.getPrice() <= 50000 && o.getRooms() > 1).sorted((o1, o2) -> (int) (Math.abs(AVERAGE_PRICE_PER_SQUARE_METER - o1.getPricePerSquareMeter()) - Math.abs(AVERAGE_PRICE_PER_SQUARE_METER - o2.getPricePerSquareMeter()))).collect(Collectors.groupingBy(Offer::getNeighbourhood)).collect((Collectors.joining("<br/><br/>")))).
//        MailSender.sendMail("Oferte 19.08.2015 +2 camere < 50000 (smart sort)", offersByNeighbourhood.entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey())).map(entry -> "<p><strong>" + entry.getKey() + "</strong> (" + entry.getValue().size() + " oferte)</p><p>--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</p>" + entry.getValue().stream().map(Offer::toString).collect(Collectors.joining("<br/>"))).collect((Collectors.joining("<br/><br/>"))));

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

    public static void insertOffersInDB(List<Offer> offers) throws SQLException {
        Connection c = DriverManager.getConnection(DATABASE_CONNECTION_STRING);
        PreparedStatement insertOffer = c.prepareStatement("INSERT OR REPLACE INTO OFFER VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement insertPrice = c.prepareStatement("INSERT INTO OFFER_PRICE(OFFER_ID, PRICE, DATE) VALUES(?, ?, ?)");
        offers.stream().forEach(o -> {
            try {
                insertOffer.setString(1, o.getId());
                insertOffer.setString(2, o.getLink());
                insertOffer.setString(3, o.getTitle());
                insertOffer.setDouble(4, o.getPrice());
                insertOffer.setDouble(5, o.getSurface());
                insertOffer.setInt(6, o.getRooms());
                insertOffer.setString(7, o.getFloor());
                insertOffer.setString(8, o.getRoomStructure());
                insertOffer.setString(9, o.getNeighbourhood().getNeighbourhoodName());
                insertOffer.executeUpdate();

                insertPrice.setString(1, o.getId());
                insertPrice.setInt(2, o.getPrice());
                insertPrice.setString(3, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                insertPrice.executeUpdate();

            } catch (SQLException e) {
                logger.error("An error has occurred while trying to insert offers in the database", e);
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
        while (resultSet.next()) {
            Offer offer = new Offer();
            offer.setId(resultSet.getString("ID"));
            offer.setLink(resultSet.getString("LINK"));
            offer.setTitle(resultSet.getString("TITLE"));
            offer.setPrice(resultSet.getInt("PRICE"));
            offer.setRooms(resultSet.getInt("ROOMS"));
            offer.setSurface(resultSet.getDouble("SURFACE"));
            offer.setFloor(resultSet.getString("FLOOR"));
            offer.setRoomStructure(resultSet.getString("ROOM_STRUCTURE"));
            offer.setNeighbourhood(ClujNeighbourhood.getNeighbourhood(resultSet.getString("NEIGHBOURHOOD")));
            offers.add(offer);
        }
        connection.close();
        return offers;
    }
}
