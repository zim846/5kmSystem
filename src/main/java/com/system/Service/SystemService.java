package com.system.Service;

import com.system.Enum.ResultEnum;
import com.system.Exception.SystemException;
import com.system.Model.Advertisement;
import com.system.Properties.FileProperties;
import com.system.Repository.AdminRepository;
import com.system.Repository.AdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class SystemService {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private FileProperties fileProperties;


    public void slim() throws Exception{
        cleanAdvertisementImages(fileProperties.getAdvertisementPath());
    }

    /**
     * 清理冗余文件
     * @throws Exception
     */

    public void cleanAdvertisementImages(String imagePath) throws Exception{
        File folder = new File(imagePath);
        if(!folder.exists()){
            throw new SystemException(ResultEnum.FILE_FOLDER_NOT_FOUND.getCode(),ResultEnum.FILE_FOLDER_NOT_FOUND.getMsg());
        }
        File images[] = folder.listFiles();
        Advertisement advertisement = null;
        for(int i=0;i<images.length;i++){
            File image = images[i];
            String name = image.getName();
            advertisement= advertisementRepository.findOneByPics("%"+name+"%");
            if(advertisement == null){
                image.delete();
            }
        }
    }

}
