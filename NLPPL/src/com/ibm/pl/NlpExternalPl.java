package com.ibm.pl;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;


public class NlpExternalPl {
	static private final String nlprestURL="http://ws.clarin-pl.eu/nlprest2/base/";

    public String NlpExternalPlProcess(String text) throws IOException, JSONException, InterruptedException
    {
    	String annotators="";
    	NlpExternalPl nlpext = new NlpExternalPl();
    	JSONObject liner2=new JSONObject();
    	

	 	
	 	String id=nlpext.nlpTextUpload(text);
	 	

	 	
	 	liner2.put("model", "n82");
	 	
	 	id=nlpProcess("liner2",id,liner2);
       
	 	
       
	 	annotators = nlpext.nlpAnnotatorsDownload(id);
	 	
	 	 
    	
    	return annotators;
    }
    
    public  String nlpTextUpload(String text) throws IOException
    {   
	
        return ClientBuilder.newClient().target(nlprestURL+"upload").request().
                post(Entity.entity(text, MediaType.APPLICATION_OCTET_STREAM)).
                readEntity(String.class);
     
    }
	
	public  String nlpFileUpload(String fileName) throws IOException
     {   
	
         return ClientBuilder.newClient().target(nlprestURL+"upload").request().
                 post(Entity.entity(new File(fileName), MediaType.APPLICATION_OCTET_STREAM)).
                 readEntity(String.class);
         
     }
	
	public String nlpAnnotatorsDownload(String id) throws IOException
    {   URL url = new URL(nlprestURL+"download"+id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true); 
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
           //InputStream is = conn.getInputStream();
        	BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        	StringBuilder sb = new StringBuilder();
        	String output;
        	while ((output = br.readLine()) != null) 
        	{
        		sb.append(output+"\n");
        		
        	}
        	return sb.toString();  
        }
        else throw new IOException("Error downloading file");
    }

    public void nlpFileDownload(String id, String fileName) throws IOException
     {   URL url = new URL(nlprestURL+"download"+id);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setDoOutput(true); 
         conn.setRequestMethod("GET");
         if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            
            
            Files.copy(is, Paths.get(fileName),StandardCopyOption.REPLACE_EXISTING);
         }
         else throw new IOException("Error downloading file");
     }

    private  static String getRes(Response res) throws IOException   
    {  if (res.getStatus()!=200) 
          throw new IOException("Error in nlprest processing");
       return res.readEntity(String.class);
    }

    public static String nlpProcess(String toolname,String id,JSONObject options) throws IOException, InterruptedException, JSONException   
    {   JSONObject request=new JSONObject();
        Client client = ClientBuilder.newClient();         
        request.put("file", id);
        request.put("tool", toolname);

        request.put("options", options);

        String taskid= client.target(nlprestURL+"startTask").request().
                 post(Entity.entity(request.toString(), MediaType.APPLICATION_JSON)).readEntity(String.class);

         String status="";
         JSONObject jsonres=new JSONObject();
         
         while (!status.equals("DONE"))
         { String res=NlpExternalPl.getRes(client.target(nlprestURL+"getStatus/"+taskid).request().get());

           jsonres=new JSONObject(res); 

           status=jsonres.getString("status");
           if (status.equals("ERROR")) throw new IOException("Error in processing");
           
           if (status.equals("PROCESSING")) { 
        	   //System.out.print(String.format( "%.2f", jsonres.getDouble("value")*100)+"%");
               //System.out.print("\b\b\b\b\b\b\b\b\b\b\b");
        	   //System.out.flush();
        	   }
           if (status.equals("DONE")) System.out.println("100%");
           
           Thread.sleep(500);
         }

         return jsonres.getJSONArray("value").getJSONObject(0).getString("fileID");

     }
    
     public static void main(String[] args) throws IOException, InterruptedException, JSONException {
    	 	
    	 /*
    	 
    	 NlpExternalPl nlpext = new NlpExternalPl();  
    	  
    	 	System.out.print("Start \n");
    	 	

    	 	//String id=nlpext.nlpFileUpload("/Users/trozmus/Documents/workspace/NLPPL/data/dokument1.txt");
    	 	String id=nlpext.nlpTextUpload("Czy Wielka Brytania może wyjść z UE od razu? A może za rok Polska?");
    	 	
    	 	System.out.print("id: "+id+"\n");
    	 	
    	 	JSONObject liner2=new JSONObject();
    	 	liner2.put("model", "top9");
    	 	
    	 	id=nlpProcess("liner2",id,liner2);
           
    	 	System.out.println("Done \n"+id);
           
    	 	nlpext.nlpFileDownload(id,"/Users/trozmus/Documents/workspace/NLPPL/data/dokument1.txt.xml");
    	 	
    	 	System.out.print("koniec");
    	 	*/
 
    	 
    	 String InputTextFilename = "Nazwa pliku.txt";
    	 NlpExternalPl nlp = new NlpExternalPl();
    	 NlpExtAnn2 nlpAnn = new NlpExtAnn2();
    	 List<OrthPl> annotatorList = new ArrayList<OrthPl>(); 
    	 
    	 String inputText = "Polska i Wielka Brytania może opuścić UE i NATO. Polacy mieszkają w Polsce. Donald Tusk mieszka w Warszawie. A proacuje w IBM. Antoni Macierewicz oraz Prezyden Andrzej Duda. Polak i Brytyjczyk.?";
    	 
    	 String output = nlp.NlpExternalPlProcess(inputText);  // send inputText to NLP engine and receive output
    	 
    	 //System.out.print(output);
    	 
    	 annotatorList = nlpAnn.NlpExtAnnGet(output, inputText, InputTextFilename); //adjust annotators in the context of inputText, join multi word annotators, return list of annotators
    	 annotatorList.forEach(a->a.printall());
    	 
    }

}
