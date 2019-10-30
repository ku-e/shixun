package com.zjc.file;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

@Controller
public class FileController {

    /**
     * 上传图片功能
     * @param //file
     * @return
     */

    @RequestMapping("/image")
    @ResponseBody
    public String upload(@RequestParam(value = "file",required = false)MultipartFile file){
        File file1 = new File(file.getOriginalFilename());
        File out = new File("E:/上传/IMAGE/" + file1.getName());
        try {
            FileOutputStream output = new FileOutputStream(out);
            InputStream in = file.getInputStream();
            FileCopyUtils.copy(in,output);
            return "http://image.leyou.com/images/"+out.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
