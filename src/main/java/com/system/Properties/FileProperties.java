package com.system.Properties;

import com.system.utils.UploadUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    private String imagePath;

    private String imageUrl;

    private String advertisement;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(String advertisement) {
        this.advertisement = advertisement;
    }

    public String getAdvertisementPath() {
        return this.imagePath+ UploadUtil.separatar+this.advertisement;
    }

    public String getAdvertisementUrl(){
        return this.imageUrl+this.advertisement;
    }

}
