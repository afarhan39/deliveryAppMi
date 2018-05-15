package my.fallacy.deliveryappmi;

import com.google.gson.annotations.SerializedName;

public class Delivery {

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("location")
    private Location location;

    public Delivery(String description, String imageUrl, Location location) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}


