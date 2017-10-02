package ie.turfclub.controller;


import ie.turfclub.model.login.User;
import ie.turfclub.model.stableStaff.TeFile;
import ie.turfclub.model.stableStaff.TeTrainers;
import ie.turfclub.service.stableStaff.FileService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping(value = "/trainersEmployeesOnline")
public class FileController {
    
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    
    @Autowired
    private FileService fileService;
   //private String fileUploadDirectory = "/home/FTP-shared/stableread/ftp";
    private String fileUploadDirectory = "C:/eclipse-kepler/TurfClub/TurfClubPrograms/upload/ftp";
 
    
    @RequestMapping(value = "/upload/{id}", method = RequestMethod.GET)
    public @ResponseBody Map list( @PathVariable Integer id) {
        log.debug("uploadGet called");
        List<TeFile> list = fileService.list(id);
        for(TeFile file : list) {
            file.setUrl("/TurfClubOnline/trainersEmployeesOnline/picture/"+file.getId());
            file.setThumbnailUrl("/TurfClubOnline/trainersEmployeesOnline/thumbnail/"+file.getId());
            file.setDeleteUrl("/TurfClubOnline/trainersEmployeesOnline/delete/"+file.getId());
            file.setDeleteType("GET");
        }
        Map<String, Object> files = new HashMap<>();
        files.put("files", list);
        log.debug("Returning: {}", files);
        return files;
    }
    
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody Map upload(MultipartHttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.debug("uploadPost called");
        Object principal = authentication.getPrincipal();
		User user = (User) principal;
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf;
        List<TeFile> list = new LinkedList<>();
        
        while (itr.hasNext()) {
            mpf = request.getFile(itr.next());
            log.debug("Uploading {}", mpf.getOriginalFilename());
            
            String newFilenameBase = UUID.randomUUID().toString();
            String originalFileExtension = mpf.getOriginalFilename().substring(mpf.getOriginalFilename().lastIndexOf("."));
            String newFilename = newFilenameBase + originalFileExtension;
            String storageDirectory = fileUploadDirectory;
            String contentType = mpf.getContentType();
            
            File newFile = new File(storageDirectory + "/" + newFilename);
            try {
                mpf.transferTo(newFile);
                //if the file is not a pdf set the thumbnail preview
                String thumbnailFilename = "";
                File thumbnailFile = null;
                System.out.println(mpf.getOriginalFilename() + " " + originalFileExtension);
                if(!originalFileExtension.equals(".pdf")){
                	BufferedImage thumbnail = Scalr.resize(ImageIO.read(newFile), 290);
                    thumbnailFilename = newFilenameBase + "-thumbnail.png";
                    thumbnailFile = new File(storageDirectory + "/" + thumbnailFilename);
                    ImageIO.write(thumbnail, "png", thumbnailFile);
                }
                //otherwise use pdf icon
                else{
                	thumbnailFilename = "pdfpreview" + "-thumbnail.png";
                	thumbnailFile = new File(storageDirectory + "/" + thumbnailFilename);
                }
                
                
                TeFile file = new TeFile();
                file.setName(mpf.getOriginalFilename());
                file.setThumbnailFilename(thumbnailFilename);
                file.setNewFilename(newFilename);
                file.setContentType(contentType);
                file.setSize(mpf.getSize());
                file.setThumbnailSize(thumbnailFile.length());
                file.setUserId(user.getId());
                fileService.create(file);
                
                file.setUrl("/TurfClubOnline/trainersEmployeesOnline/picture/"+file.getId());
                file.setThumbnailUrl("/TurfClubOnline/trainersEmployeesOnline/thumbnail/"+file.getId());
                file.setDeleteUrl("/TurfClubOnline/trainersEmployeesOnline/delete/"+file.getId());
                file.setDeleteType("GET");
                
                list.add(file);
                
            } catch(IOException e) {
                log.error("Could not upload file "+mpf.getOriginalFilename(), e);
            }
            
        }
        
        Map<String, Object> files = new HashMap<>();
        files.put("files", list);
        return files;
    }
    
    @RequestMapping(value = "/picture/{id}", method = RequestMethod.GET)
    public void picture(HttpServletResponse response, @PathVariable Long id) {
        TeFile file = fileService.get(id);
        File imageFile = new File(fileUploadDirectory+"/"+file.getNewFilename());
        response.setContentType(file.getContentType());
        response.setContentLength(file.getSize().intValue());
        try {
            InputStream is = new FileInputStream(imageFile);
            IOUtils.copy(is, response.getOutputStream());
        } catch(IOException e) {
            log.error("Could not show picture "+id, e);
        }
    }
    
    @RequestMapping(value = "/thumbnail/{id}", method=RequestMethod.GET)
    public void thumbnail(HttpServletResponse response, @PathVariable Long id) {
    	TeFile file = fileService.get(id);
        File imageFile = new File(fileUploadDirectory+"/"+file.getThumbnailFilename());
        response.setContentType(file.getContentType());
        response.setContentLength(file.getThumbnailSize().intValue());
        try {
            InputStream is = new FileInputStream(imageFile);
            IOUtils.copy(is, response.getOutputStream());
        } catch(IOException e) {
            log.error("Could not show thumbnail "+id, e);
        }
    }
    
    @RequestMapping(value = "/delete/{id}", method=RequestMethod.GET)
    public @ResponseBody List<Map<String, Object>> delete(@PathVariable Long id) {
    	System.out.println("DELETE" + id);
    	TeFile file = fileService.get(id);
        File imageFile = new File(fileUploadDirectory+"/"+file.getNewFilename());
        imageFile.delete();
        String originalFileExtension = imageFile.getName().substring(imageFile.getName().lastIndexOf("."));
        if(originalFileExtension.equals(".pdf")){
        	 File thumbnailFile = new File(fileUploadDirectory+"/"+file.getThumbnailFilename());
             thumbnailFile.delete();
        }
       
        fileService.delete(file);
        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Object> success = new HashMap<>();
        success.put("success", true);
        results.add(success);
        return results;
    }
}
