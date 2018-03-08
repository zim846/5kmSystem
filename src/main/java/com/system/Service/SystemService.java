package com.system.Service;

import com.system.Enum.ResultEnum;
import com.system.Exception.LabsException;
import com.system.Model.Admin;
import com.system.Model.Facility;
import com.system.Properties.FileProperties;
import com.system.Repository.AdminRepository;
import com.system.Repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by arlex on 2017/12/1.
 */
@Service
public class SystemService {

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FileProperties fileProperties;


    public void slim() throws Exception{
        cleanUserImages(fileProperties.getUserPath());
        cleanFacilityImages(fileProperties.getFacilityPath());
    }

    /**
     * 清理冗余文件
     * @throws Exception
     */

    public void cleanUserImages(String imagePath) throws Exception{
        File folder = new File(imagePath);
        if(!folder.exists()){
            throw new LabsException(ResultEnum.FILE_FOLDER_NOT_FOUND.getCode(),ResultEnum.FILE_FOLDER_NOT_FOUND.getMsg());
        }
        File images[] = folder.listFiles();
        Admin admin = null;
        for(int i=0;i<images.length;i++){
            File image = images[i];
            String name = image.getName();
            if(name.equals("default.png")){
                continue;
            }
            admin= adminRepository.findOneByUrl(name);
            if(admin == null){
                image.delete();
            }
        }
    }

    public void cleanFacilityImages(String imagePath) throws Exception{
        File folder = new File(imagePath);
        if(!folder.exists()){
            throw new LabsException(ResultEnum.FILE_FOLDER_NOT_FOUND.getCode(),ResultEnum.FILE_FOLDER_NOT_FOUND.getMsg());
        }
        File images[] = folder.listFiles();
        Facility facility = null;
        for(int i=0;i<images.length;i++){
            File image = images[i];
            String name = image.getName();
            facility= facilityRepository.findOneByUrl(name);
            if(facility == null){
                image.delete();
            }
        }
    }

}
