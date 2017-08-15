package com.tmoncorp.admin.service;

import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Iterator;

public class UploadFileDecodeService {
    private MultipartHttpServletRequest httpRequest;
    private UniversalDetector detector;

    // 파일 인코딩 형식을 참조하여 형식에 맞게 인코딩
    public UploadFileDecodeService(MultipartHttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
        detector = new UniversalDetector(null);
    }

    private String getFileEncodingType(String fileName) throws IOException {
        byte[] buf = new byte[1024];
        int nread;

        while (((nread = httpRequest.getFile(fileName).getInputStream().read(buf)) > 0) && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();

        return detector.getDetectedCharset();
    }

    public String decodeUploadFile() throws Exception {
        Iterator<String> iterator = httpRequest.getFileNames();
        String fileContent = "";

        if (iterator.hasNext()) {
            String fileName = iterator.next();

            MultipartFile mpf = httpRequest.getFile(fileName);
            String encodingType = getFileEncodingType(fileName);

            if (encodingType == null) {
                throw new NullPointerException("Can't figure out encoding type");
            } else if (encodingType.equals("UTF-8")) {
                fileContent = new String(mpf.getBytes(), "utf-8");
            } else if (encodingType.startsWith("EUC")){
                fileContent = new String(mpf.getBytes(), "euc-kr");
            } else {
                throw new Exception("Not supported encoding type");
            }

            detector.reset();
        }
        return fileContent;
    }
}
