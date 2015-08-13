package com.realestatefinder.imobiliare;

import com.realestatefinder.ClujNeighbourhood;
import com.realestatefinder.Offer;
import com.realestatefinder.RealEstateFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImobiliareOfferRetriever {
    private static final Logger logger = LogManager.getLogger(ImobiliareOfferRetriever.class.getName());

    public static void main(String[] args) {
        //TODO check this and finish it
        try {
            List<Offer> offers = getOffersFromImobiliare();
            RealEstateFinder.insertOffersInDB(offers);
        } catch (IOException | SQLException e) {
            logger.error(e);
        }
    }

    private static List<Offer> getOffersFromImobiliare() throws IOException {
        List<Offer> offers = new ArrayList<>();
        for (int i = 1; i <= 67; i++) {
            Document document = Jsoup.connect("http://www.imobiliare.ro/vanzare-apartamente/cluj-napoca?pagina=" + i).get();
            System.out.println("Processing page " + i + "...");
            int pageOffers = 0;
            for (Element element : document.select("div[itemtype=http://schema.org/Offer]")) {
                Offer offer = new Offer();
                offer.setId(element.id());
                Element nameLink = element.select("a[itemprop=name]").first();
                offer.setLink(nameLink.attr("href"));
                offer.setTitle(nameLink.text());
                Elements priceElement = element.select("div[itemprop=price]");
                if (priceElement.size() == 0) {
                    System.out.println("Ignoring offer " + offer.getLink() + " with no price..");
                    continue;
                }
                String priceText = priceElement.first().text();
                offer.setPrice(Integer.parseInt(priceText.substring(0, priceText.indexOf(" ")).replaceAll("\\.", "")));
                for (Element characteristic : element.select("ul.caracteristici").select("li")) {
                    String characteristicText = characteristic.text().toLowerCase();
                    if (characteristicText.contains("camer")) {
                        if ("o cameră".equals(characteristicText)) {
                            offer.setRooms(1);
                        } else {
                            offer.setRooms(Integer.parseInt(characteristicText.substring(0, characteristicText.indexOf(" "))));
                        }
                    } else if (characteristicText.contains("mp")) {
                        offer.setSurface(Double.parseDouble(characteristicText.substring(0, characteristicText.indexOf(" "))));
                    } else if (characteristicText.contains("parter") || characteristicText.contains("etaj")) {
                        offer.setFloor(characteristicText);
                    } else {
                        offer.setRoomStructure(characteristicText);
                    }
                }
                offer.setNeighbourhood(getNeighbourhoodFromImobiliare(offer));
                offers.add(offer);
                pageOffers++;
            }
            System.out.println("Processed " + pageOffers + " offers");
        }
        return offers;
    }

    private static ClujNeighbourhood getNeighbourhoodFromImobiliare(Offer offer) {
        String link = offer.getLink();
        int prefixEnd = link.indexOf("cluj-napoca/") + "cluj-napoca/".length();
        return ClujNeighbourhood.getNeighbourhood(link.substring(prefixEnd, link.indexOf('/', prefixEnd)).replace("-", " "));
    }
}
