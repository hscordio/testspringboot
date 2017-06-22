package hello;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.web.client.RestTemplate;
import com.sun.net.ssl.SSLContext;
import javax.net.ssl.HttpsURLConnection;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import com.sun.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;



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
    public String helloFacebook(Model model) {
//        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
//            return "redirect:/connect/facebook";
//        }
//
//        model.addAttribute("facebookProfile", facebook.userOperations().getUserProfile());
//        PagedList<Post> feed = facebook.feedOperations().getFeed();
//        model.addAttribute("feed", feed);
//        return "hello";
    	
    	try {
    	InputStream inputStream = companiesXml.getInputStream();
    	KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
    	keystore.load(inputStream, "changeit".toCharArray());


    	SSLContext context = SSLContext.getInstance("TLSv1.2");

    	final TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
    	tmf.init(keystore);
    	final TrustManager[] trustManagers = tmf.getTrustManagers();

    	context.init(null, trustManagers, new SecureRandom());

    	HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    	
    	
    	} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error("Errore nel settaggio del certificato"+e1);
		}
    	
    	//System.setProperty("javax.net.ssl.trustStrore", "applepaykeystore.jks");
    	
    	String validationURL = "https://apple-pay-gateway-cert.apple.com/paymentservices/startSession";

    	//HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
    	//  public boolean verify(String hostname, SSLSession session) {
//    	    return true;
    	//  }
    	//});

    	ApplePayMerchantValidationRequestData merchantValidationRequest = new ApplePayMerchantValidationRequestData();
    	merchantValidationRequest.setDisplayName("Oakley US");
    	merchantValidationRequest.setDomainName("testapplepayoakley.herokuapp.com");
    	merchantValidationRequest.setMerchantIdentifier("merchant.us.oakley.com.heroku");


    	RestTemplate restTemplate = new RestTemplate();
    	try{
	    	String response = restTemplate.postForObject(validationURL, merchantValidationRequest, String.class);
	    	System.out.println(response);
    	}catch(Exception e ){
    		System.out.println("KO");
    		logger.info(""+e);
    		return "KO";
    	}
    	
    	return "OK";
//    	log.info(response);

    	
    	
    }

}
