package hello;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;



@Controller
@RequestMapping("/")
public class HelloController {

	
	@Value(value = "classpath:/applepaykeystore.jks")
	private Resource companiesXml;
	
    private Facebook facebook;
    private ConnectionRepository connectionRepository;

    Logger logger = LoggerFactory.getLogger(HelloController.class);
    
    public HelloController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = facebook;
        this.connectionRepository = connectionRepository;
    }

    @GetMapping
    public @ResponseBody ResponseEntity<String> helloFacebook(Model model) {
//        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
//            return "redirect:/connect/facebook";
//        }
//
//        model.addAttribute("facebookProfile", facebook.userOperations().getUserProfile());
//        PagedList<Post> feed = facebook.feedOperations().getFeed();
//        model.addAttribute("feed", feed);
//        return "hello";
//    	String absolutePath = companiesXml.getFile().getAbsolutePath();
//    	System.setProperty("javax.net.ssl.trustStore", absolutePath);
//	    System.setProperty("javax.net.ssl.trustStorePassword","changeit");

    	
    	String filename = System.getProperty("java.home") + "/lib/security/cacerts".replace('/', File.separatorChar);
//		System.setProperty("javax.net.ssl.keyStore", filename);
//	    System.setProperty("javax.net.ssl.keyStorePassword","changeit");
	    
    	try {
	    
    	InputStream inputStream = companiesXml.getInputStream();
    	KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    	trustStore.load(inputStream, "changeit".toCharArray());
    	logger.info("number of certificate found in trustStore: " + trustStore.size());
    	final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    	tmf.init(trustStore);
    	final TrustManager[] trustManagers = tmf.getTrustManagers();

    	FileInputStream fis = new FileInputStream(filename);
    	KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    	keyStore.load(fis, "changeit".toCharArray());
    	logger.info("number of certificate found in keystore: " + keyStore.size());
    	KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    	kmf.init(keyStore,"changeit".toCharArray());
    	final KeyManager[] keyManagers = kmf.getKeyManagers();
    	
    	SSLContext context = SSLContext.getInstance("TLSv1.2");
    	context.init(keyManagers, trustManagers, new SecureRandom());

    	HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    	
    	} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error("Errore nel settaggio del certificato"+e1);
			return new ResponseEntity<String>("Errore nel settaggio del certificato: "+ e1, HttpStatus.OK);
		}
    	
    	//System.setProperty("javax.net.ssl.trustStrore", "applepaykeystore.jks");
    	
    	String validationURL = "https://apple-pay-gateway-cert.apple.com/paymentservices/startSession";

    	//HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
    	//  public boolean verify(String hostname, SSLSession session) {
//    	    return true;
    	//  }
    	//});

    	ApplePayMerchantValidationRequestData merchantValidationRequest = new ApplePayMerchantValidationRequestData();
    	merchantValidationRequest.setDisplayName("Heroku Oakley Us Test Account");
    	merchantValidationRequest.setDomainName("testapplepayoakley.herokuapp.com");
    	merchantValidationRequest.setMerchantIdentifier("merchant.us.oakley.com.heroku");


    	RestTemplate restTemplate = new RestTemplate();
    	try{
	    	String response = restTemplate.postForObject(validationURL, merchantValidationRequest, String.class);
	    	System.out.println(response);
	    	return new ResponseEntity<String>(response, HttpStatus.OK);
    	}catch(Exception e ){
    		logger.info(""+e);
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
    }

}
