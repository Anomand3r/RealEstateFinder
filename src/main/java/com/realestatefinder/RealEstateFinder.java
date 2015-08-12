package com.realestatefinder;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RealEstateFinder {
    public static void main(String[] args) throws IOException, SQLException {
        List<Offer> offers = new ArrayList<>();
        Connection connection = DriverManager.getConnection("jdbc:sqlite:RealEstateFinder.db");
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
            priceStatement.setString(1, offer.getId());
            ResultSet priceResultSet = priceStatement.executeQuery();
            priceResultSet.next();
            offer.setPrice(priceResultSet.getInt("PRICE"));
            offers.add(offer);
        }
        connection.close();
        Map<String, List<Offer>> offersByNeighbourhood = offers.stream().sorted((o1, o2) -> (int) (o1.getPricePerSquareMeter() - o2.getPricePerSquareMeter())).collect(Collectors.groupingBy(Offer::getNeighbourhood));
//        offersByNeighbourhood.entrySet().stream().map(entry -> "<p>" + entry.getKey() + "<p><p>----------</p>" + entry.getValue().stream().map(Offer::toString).collect(Collectors.joining("<br/>")));
        MailSender.sendMail("Oferte 12.08.2015", offersByNeighbourhood.entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey())).map(entry -> "<p><strong>" + entry.getKey() + "</strong> (" + entry.getValue().size() + " oferte)</p><p>--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</p>" + entry.getValue().stream().map(Offer::toString).collect(Collectors.joining("<br/>"))).collect((Collectors.joining("<br/><br/>"))));

//        for (int i = 1; i <= 67; i++) {
//            Document document = Jsoup.connect("http://www.imobiliare.ro/vanzare-apartamente/cluj-napoca?pagina=" + i).get();
//            System.out.println("Processing page " + i + "...");
//            int pageOffers = 0;
//            for (Element element : document.select("div[itemtype=http://schema.org/Offer]")) {
//                Offer offer = new Offer();
//                offer.setId(element.id());
//                Element nameLink = element.select("a[itemprop=name]").first();
//                offer.setLink(nameLink.attr("href"));
//                offer.setTitle(nameLink.text());
//                Elements priceElement = element.select("div[itemprop=price]");
//                if (priceElement.size() == 0) {
//                    System.out.println("Ignoring offer " + offer.getLink() + " with no price..");
//                    continue;
//                }
//                String priceText = priceElement.first().text();
//                offer.setPrice(Integer.parseInt(priceText.substring(0, priceText.indexOf(" ")).replaceAll("\\.", "")));
//                for (Element characteristic : element.select("ul.caracteristici").select("li")) {
//                    String characteristicText = characteristic.text().toLowerCase();
//                    if (characteristicText.contains("camer")) {
//                        if ("o cameră".equals(characteristicText)) {
//                            offer.setRooms(1);
//                        } else {
//                            offer.setRooms(Integer.parseInt(characteristicText.substring(0, characteristicText.indexOf(" "))));
//                        }
//                    } else if (characteristicText.contains("mp")) {
//                        offer.setSurface(Double.parseDouble(characteristicText.substring(0, characteristicText.indexOf(" "))));
//                    } else if (characteristicText.contains("parter") || characteristicText.contains("etaj")) {
//                        offer.setFloor(characteristicText);
//                    } else {
//                        offer.setRoomStructure(characteristicText);
//                    }
//                }
//                offers.add(offer);
//                pageOffers++;
//            }
//            System.out.println("Processed " + pageOffers + " offers");
//        }
//        System.out.println(offers.stream().sorted((o1, o2) -> (int) (o1.getPricePerSquareMeter() - o2.getPricePerSquareMeter())).limit(100).map(Offer::toString).collect(Collectors.joining("\n")));


//        Connection c = DriverManager.getConnection("jdbc:sqlite:RealEstateFinder.db");
//        PreparedStatement insertOffer = c.prepareStatement("INSERT INTO OFFER VALUES(?, ?, ?, ?, ?, ?, ?)");
//        PreparedStatement insertPrice = c.prepareStatement("INSERT INTO OFFER_PRICE(OFFER_ID, PRICE, DATE) VALUES(?, ?, ?)");
//        offers.stream().forEach(o -> {
//            try {
//                insertOffer.setString(1, o.getId());
//                insertOffer.setString(2, o.getLink());
//                insertOffer.setString(3, o.getTitle());
//                insertOffer.setInt(4, o.getRooms());
//                insertOffer.setDouble(5, o.getSurface());
//                insertOffer.setString(6, o.getFloor());
//                insertOffer.setString(7, o.getRoomStructure());
//                insertOffer.executeUpdate();
//
//                insertPrice.setString(1, o.getId());
//                insertPrice.setInt(2, o.getPrice());
//                insertPrice.setString(3, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
//                insertPrice.executeUpdate();
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//        c.close();

//        System.out.println("offers = " + offers);
//        System.out.println("offers.size() = " + offers.size());
    }
}
