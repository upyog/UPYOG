package org.egov.pg.service.gateways.nttdata;



import javax.net.ssl.SSLException;

import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;




//import com.nttdatapay.aipayclient.utils.Utility;

public class AipayService {
	
	  public static String getAtomTokenId(String merchantId, String serverResp, String atomUrl)
	  {
	    String result = "";
	    try
	    {
	    	System.out.println("in get atomtokenID method Merchant Id ="+merchantId+ "&&&&&& Encypted text ="+ serverResp);
//	      result = Utility.httpPostCaller("merchId=" + merchantId + "&encData=" + serverResp, "https://caller.atomtech.in/ots/aipay/auth");
	      WebClient.Builder webClientBuilder=WebClient.builder();
	      webClientBuilder= getCertificateSkippedRestObject();
	      result=webClientBuilder.build().post()
	    		  .uri(atomUrl+"merchId=" + merchantId + "&encData=" + serverResp)
	    		  .contentType(MediaType.APPLICATION_JSON)
	    		  .retrieve()
	    		  .bodyToMono(String.class)
	    		  .block();
	    	System.out.println("Server result----------: " + result);
	    } catch (Exception e) {
	      e.getStackTrace();
	    }

	    return result;
	  }
	  public static Builder getCertificateSkippedRestObject() throws SSLException {

			SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
			HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

			return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
		}
	  
	  public static String getTransactionStatus(String merchantId, String serverResp, String statusUrl)
	  {
	    String result = "";
	    try
	    {
	    	System.out.println("in fetch Status method Merchant Id ="+merchantId+ "&&&&&& Encypted text ="+ serverResp);
//	      result = Utility.httpPostCaller("merchId=" + merchantId + "&encData=" + serverResp, "https://caller.atomtech.in/ots/aipay/auth");
	      WebClient.Builder webClientBuilder=WebClient.builder();
	      webClientBuilder=getCertificateSkippedRestObject();
	      result=webClientBuilder.build().post()
	    		  .uri(statusUrl+"merchId=" + merchantId + "&encData=" + serverResp)
	    		  .contentType(MediaType.APPLICATION_JSON)
	    		  .retrieve()
	    		  .bodyToMono(String.class)
	    		  .block();
	    	System.out.println("Server result----------: " + result);
	    } catch (Exception e) {
	      e.getStackTrace();
	    }

	    return result;
	  }
	  
}
