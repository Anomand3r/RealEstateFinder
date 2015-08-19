package com.realestatefinder.olx;

import com.realestatefinder.ClujNeighbourhood;
import com.realestatefinder.Offer;
import com.realestatefinder.RealEstateFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OlxOfferRetriever {
    private static final Logger logger = LogManager.getLogger(OlxOfferRetriever.class.getName());

    public static void main(String[] args) {
        try {
            List<Offer> olxOffers = getOlxOffers();
            List<Offer> dbOffers = RealEstateFinder.getOffersFromDB();
            RealEstateFinder.removeDuplicateOffers(olxOffers, dbOffers);
            RealEstateFinder.insertOffersInDB(olxOffers);
        } catch (IOException | SQLException e) {
            logger.error("An error has occurred while obtaining the offers from Imobiliare", e);
        }
    }

    private static List<Offer> getOlxOffers() throws IOException {
        List<Offer> olxOffers = new ArrayList<>();
        Document document = Jsoup.connect("http://olx.ro/imobiliare/apartamente-garsoniere-de-vanzare/cluj-napoca/rss/").get();
        for (Element element : document.select("item")) {
            Offer offer = new Offer();
            offer.setId(element.select("guid").text());
            offer.setLink(element.select("guid").text());
            offer.setTitle(element.select("title").text());
            String description = element.select("description").text();
            int priceStart = description.indexOf("Pret:") + "Pret:".length();
            int priceEnd = description.indexOf("€", priceStart);
            if (priceEnd > -1) {
                offer.setPrice(Integer.parseInt(description.substring(priceStart, priceEnd).replaceAll(" ", "")));
            } else {
                logger.info("Ignoring exchange offer " + offer.getLink() + "..");
                continue;
            }
            int surfaceStart = description.indexOf("Suprafata:");
            if (surfaceStart > -1) {
                surfaceStart += "Suprafata:".length();
                int surfaceEnd = description.indexOf("m", surfaceStart);
                offer.setSurface(Double.parseDouble(description.substring(surfaceStart, surfaceEnd).replaceAll(" ", "")));
            } else {
                logger.info("Ignoring offer " + offer.getLink() + " with no surface..");
                continue;
            }
            int roomStructureStart = description.indexOf("Compartimentare: ");
            if (roomStructureStart > -1) {
                roomStructureStart += "Compartimentare: ".length();
                int roomStructureEnd = description.indexOf(",", roomStructureStart);
                offer.setRoomStructure(description.substring(roomStructureStart, roomStructureEnd));
            }
            offer.setRooms(Integer.parseInt(element.select("category").text().substring(0, 1)));
            offer.setNeighbourhood(getNeighbourhoodFromOlx(description));
            olxOffers.add(offer);
        }
        return olxOffers;
    }

    private static ClujNeighbourhood getNeighbourhoodFromOlx(String description) {
        //workaround for GARA neighbourhood
        final String lowerCaseDescription = description.toUpperCase().replaceAll("GARAJ", "");
        return Arrays.stream(ClujNeighbourhood.values()).filter(n -> lowerCaseDescription.contains(n.getNeighbourhoodName())).findFirst().orElse(ClujNeighbourhood.UNKNOWN);
    }

}
