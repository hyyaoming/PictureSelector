package org.lym.image.select.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageFolder implements Serializable {
    public String name;
    public ArrayList<ImageItem> images;

    public ImageFolder(String name) {
        this.name = name;
    }

    public ImageFolder(String name, ArrayList<ImageItem> images) {
        this.name = name;
        this.images = images;
    }

    public void addImage(ImageItem image) {
        if (image != null && image.path.length() > 0) {
            if (images == null) {
                images = new ArrayList<>();
            }
            images.add(image);
        }
    }

    @Override
    public String toString() {
        return "Folder{" +
                "name='" + name + '\'' +
                ", images=" + images +
                '}';
    }
}
