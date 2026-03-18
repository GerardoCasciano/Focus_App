package focusApp.focus.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
public CloudinaryService(
        @Value("${CLOUDINARY_NAME}")
         String cloudName,
        @Value("${CLOUDINARY_KEY}")
         String apiKey,
        @Value("${CLOUDINARY_SECRET}")
        String apiSecret
){
    this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
             "api_key", apiKey,
            "api_secret", apiSecret
    ));
}
public Cloudinary getCloudinary(){
    return this.cloudinary;
}
    public String uploadImage(MultipartFile file)throws IOException{
        //Carica i file e riceve Mappa di risposta
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        //Estrae l'URL pubblico dell'immagine caricata
        return uploadResult.get("url").toString();
    }
}
