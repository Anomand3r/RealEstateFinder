package com.realestatefinder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class RealEstateFinder {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("http://www.imobiliare.ro/vanzare-apartamente/cluj-napoca").get();
        System.out.println("size = " + document.select("div.pret").size());
    }
}
