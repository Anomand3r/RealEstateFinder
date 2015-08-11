package com.realestatefinder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RealEstateFinder {
    public static void main(String[] args) throws IOException {
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
                offers.add(offer);
                pageOffers++;
            }
            System.out.println("Processed " + pageOffers + " offers");
        }
        System.out.println(offers.stream().sorted((o1, o2) -> (int) (o1.getPricePerSquareMeter() - o2.getPricePerSquareMeter())).limit(100).map(Offer::toString).collect(Collectors.joining("\n")));

//        System.out.println("offers = " + offers);
//        System.out.println("offers.size() = " + offers.size());
    }
}
