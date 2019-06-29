package ml131.de.hdm_stuttgart.mi;

import javafx.scene.control.Hyperlink;
import javafx.event.ActionEvent;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import java.awt.Desktop;
import java.net.URI;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;


import java.io.IOException;
import java.net.URISyntaxException;

import javafx.scene.control.Tooltip;

import javax.imageio.ImageIO;

public class Card {
    HashMap<String,Object> cardFeature = new HashMap<>();

    private String name;
    private String rawURL;
    private String convertedManaCost;
    private String type;
    private String effect;
    private String format;
    private String rarity;
    private Hyperlink pictureLink;
    private String cardmarketLink;
    private ArrayList<String> colors;
    private String language;
    private String manaCost;

    public Card(String name,String type,String manaCost){
        this.name=name;
        this.type=type;
        this.manaCost=manaCost;
    }

    public Card(String name,
                String convertedManaCost,
                String type,
                String effect,
                String format,
                String rarity,
                Hyperlink pictureLink,
                String cardmarketLink,
                ArrayList<String> colors,
                String language,
                String manaCost,
                String multiverseId){

        cardFeature.put("name",name);
        this.name=name;
        cardFeature.put("convertedManaCost",convertedManaCost);
        cardFeature.put("type",type);
        this.type=type;
        cardFeature.put("effect",effect);
        cardFeature.put("format",format);
        cardFeature.put("rarity",rarity);

        this.rawURL = pictureLink.getText();
        pictureLink.setText(name);
        pictureLink.setOnAction((ActionEvent e) -> {
            if( Desktop.isDesktopSupported() )
            {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().browse( new URI( rawURL) );
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
//        view.setFitHeight(400);
//        view.setFitWidth(400);
//        view.setPreserveRatio(true);
        Tooltip tooltip = new Tooltip();
        tooltip.setGraphic(view);
        pictureLink.setTooltip(tooltip);

        cardFeature.put("pictureLink",pictureLink);
        this.pictureLink=pictureLink;

        cardFeature.put("cardmarketLink",cardmarketLink);
        cardFeature.put("colors",colors);
        cardFeature.put("language",language);
        cardFeature.put("manaCost",manaCost);
        this.manaCost=manaCost;
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
}
