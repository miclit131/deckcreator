package ml131.de.hdm_stuttgart.mi.datamodel;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import ml131.de.hdm_stuttgart.mi.interfaces.Color;
import ml131.de.hdm_stuttgart.mi.util.InternetUtil;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class Card implements Comparable<Card> {
    private String name;
    private String rawURL;
    private String convertedManaCost;
    private String type;
    private String effect;
    private String format;
    private String rarity;
    private Hyperlink pictureLink;
    private String cardmarketLink;
    private List<Color> colors;
    private String language;
    private String manaCost;
    private SimpleIntegerProperty count; // only relevant for decks


    public Card(String name,
                String convertedManaCost,
                String type,
                String effect,
                String format,
                String rarity,
                Hyperlink pictureLink,
                List<Color> colors,
                String language,
                String manaCost,
                String multiverseId,
                Integer count){

        this.effect = effect;
        this.count = new SimpleIntegerProperty(count);
        this.name=name;
        this.type=type;
        this.rawURL = pictureLink.getText();
        this.colors = colors;
        this.manaCost=manaCost;
        this.language = language;
        this.rarity = rarity;
        this.format = format;
        this.convertedManaCost = convertedManaCost;

        pictureLink.setText(name);
        pictureLink.setOnAction((ActionEvent e) -> {
            if( Desktop.isDesktopSupported() )
            {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().browse( new URI( rawURL+ InternetUtil.encodeValue(this.name)) );
                    } catch (IOException | URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }).start();
            }

            System.out.println(this.rawURL);
        });
        String urlString = "https://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+multiverseId+"&type=card";
        Image image = new Image(urlString);
        ImageView view = new ImageView(image);
        view.setPreserveRatio(true);
        view.setFitHeight(450);
        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.seconds(0.1));
        tooltip.setGraphic(view);
        tooltip.setShowDuration(Duration.seconds(60));
        pictureLink.setTooltip(tooltip);
        this.pictureLink=pictureLink;


    }


    public String getRawURL() { return this.rawURL; }


    public void setRawURL(String rawURL) { this.rawURL = rawURL; }


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }


    public String getManaCost() {
        return manaCost;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getType() {
        return type;
    }


    public void setPictureLink(Hyperlink pictureLink) {
        this.pictureLink = pictureLink;
    }


    public Hyperlink getPictureLink() {
        return pictureLink;
    }


    public Integer getCount() {
        return count.get();
    }


    public void setCount(Integer count) {
        this.count.set(count);
    }


    public List<Color> getColors() {
        return colors;
    }


    public void setColors(ArrayList<Color> colors) {
        this.colors = colors;
    }


    public String getConvertedManaCost() {
        return convertedManaCost;
    }


    public void setConvertedManaCost(String convertedManaCost) {
        this.convertedManaCost = convertedManaCost;
    }


    public String getEffect() {
        return effect;
    }


    public void setEffect(String effect) {
        this.effect = effect;
    }


    public String getFormat() {
        return format;
    }


    public void setFormat(String format) {
        this.format = format;
    }


    public String getRarity() {
        return rarity;
    }


    public void setRarity(String rarity) {
        this.rarity = rarity;
    }


    public String getCardmarketLink() {
        return cardmarketLink;
    }


    public void setCardmarketLink(String cardmarketLink) {
        this.cardmarketLink = cardmarketLink;
    }


    public String getLanguage() {
        return language;
    }


    public void setLanguage(String language) {
        this.language = language;
    }


//    public SimpleIntegerProperty countProperty() {
//        return count;
//    }


    public void setCount(int count) {
        this.count.set(count);
    }

    /***
     * Card names are unique, with comparing by name we can easily sort them alphabetically.
     * @param o: card to compare to this instance
     * @return signed integer representing the result
     */
    @Override
    public int compareTo(Card o) {
        return this.getName().compareTo(o.getName());
    }
}
