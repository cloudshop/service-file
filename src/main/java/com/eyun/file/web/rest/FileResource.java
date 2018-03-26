package com.eyun.file.web.rest;

import com.eyun.file.config.FileProperties;
import com.eyun.file.service.FileUploadService;
import com.eyun.file.config.FileProperties;
import com.eyun.file.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * REST controller for file upload.
 *
 * @Description: 文件
 * @author zhusen
 * @date 2015-6-5
 *
 */
@RestController
@RequestMapping(value = "/api", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
public class FileResource {
	private static final Logger logger = LoggerFactory.getLogger(FileResource.class);

	@Resource
	private FileUploadService fileUploadService;
    @Autowired
    FileProperties fileProperties;

	/**
	 * POST /register : register the user.
	 *
	 * @param managedUserVM
	 *            the managed user View Model
	 * @return the ResponseEntity with status 201 (Created) if the user is
	 *         registered or 400 (Bad Request) if the login or email is already
	 *         in use
	 */
	@PostMapping(path = "/uploadAppImg")
	public ResponseEntity<String> uploadAppImg(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return new ResponseEntity<>("上传的图片不能为空", HttpStatus.BAD_REQUEST);
		}
		switch (fileProperties.getLocation()) {
		case "oss":
			return fileUploadService.ossUpload(file)
		              .map(url -> new ResponseEntity<String>(url, HttpStatus.OK))
		              .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
		case "local":
			return fileUploadService.uploadToLocal(file)
		              .map(url -> new ResponseEntity<String>(url, HttpStatus.OK))
		              .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
		case "cloud":
			return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

	}

	/*图片上传
	*@param file
	*@return fileName URL
	* */
	@PostMapping("/ossUpload")
    public ResponseEntity<Map> ossUpload(@RequestParam("file") MultipartFile file)throws Exception{
        Map result=fileUploadService.ossFileUpload(file);
	    return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    /*
    * 获取图片路径
    * */
    @GetMapping("/view")
    public ResponseEntity<String> image(@RequestParam("fileName") String fileName)throws Exception{
        String url=fileUploadService.getImgUrl(fileName);
        return new ResponseEntity<String>(url, HttpStatus.OK);
    }
}
