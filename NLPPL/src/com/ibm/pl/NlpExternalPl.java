package com.ibm.pl;

import java.io.BufferedReader;
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
import org.jsoup.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Logger;



public class NlpExternalPl {
	static private final String nlprestURL="http://ws.clarin-pl.eu/nlprest2/base/";
	private static final Logger LOGGER = Logger.getLogger( NlpExternalPl.class.getName() );
	
    public String NlpExternalPlProcess(String text) throws IOException, JSONException, InterruptedException
    {
    	String annotators="";
    	NlpExternalPl nlpext = new NlpExternalPl();
    	JSONObject liner2=new JSONObject();
    	

	 	LOGGER.info("nlpProcess text upload "+text.length()+".\n");

	 	String id=nlpext.nlpTextUpload(text);
	 
	 	liner2.put("model", "top9");
	 	
	 	LOGGER.info("nlpProcess liner2 started.\n");
	 	
	 	id=nlpProcess("liner2",id,liner2);
	 	
	 	LOGGER.info("nlpProcess liner2 finished.\n");

	 	
       
	 	annotators = nlpext.nlpAnnotatorsDownload(id);
	 	LOGGER.info("nlpProcess Download Annotator finished \n");
	 	 
	 	
    	return annotators;
    }
    
    public  String nlpTextUpload(String text) throws IOException
    {   
    	
    	Client client  =  ClientBuilder.newClient();
    	String  response = client.target(nlprestURL+"upload").request().post(Entity.entity(text, MediaType.APPLICATION_OCTET_STREAM)).readEntity(String.class);
    	client.close();
        
    	return response;
    }
	
	public  String nlpFileUpload(String fileName) throws IOException
     {   
	
		//Client client  =  ClientBuilder.newClient();
    	//String  response = client.target(nlprestURL+"upload").request().post(Entity.entity(new File(fileName), MediaType.APPLICATION_OCTET_STREAM)).readEntity(String.class);
    	//client.close();
        
    	//return response;
       return "";
         
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
    {   
    	
    	
    	JSONObject request=new JSONObject();
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
         client.close();
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
    	 System.out.print("Start \n");
    	 String InputTextFilename = "Nazwa pliku.txt";
    	 NlpExternalPl nlp = new NlpExternalPl();
    	 NlpExtAnn2 nlpAnn = new NlpExtAnn2();
    	 List<OrthPl> annotatorList = new ArrayList<OrthPl>(); 
    	 
    	// String inputText = "Polska Polak Wielka Brytania UE i "
    //	 		+ "NATO. Donald Tusk mieszka w Warszawie. A proacuje w IBM. "
    	// 		+ "Antoni Macierewicz oraz Prezyden Anfrzej Duda. Polak i Brytyjczyk.?";
    	 
    	String inputText = " Zakup kilkudziesięciu śmigłowców wielozadaniowych miał być jednym z kluczowych elementów modernizacji sił zbrojnych. W przetargu startowały francuski H225M Caracal (Airbus Helicopters), amerykański Black Hawk (koncern Sikorsky, do którego należą zakłady w Mielcu) oraz brytyjsko-włoski AW-149 (zgłoszony przez zakłady PZL Świdnik należące do koncernu Leonardo)."
    	 +"W kwietniu 2015 r. poprzednie kierownictwo MON wybrało caracale. Planowano zakup 50 śmigłowców za 13,3 mld zł. Pierwsze egzemplarze armia miała dostać już w 2017 r. Warunkiem było podpisanie umowy offsetowej. Negocjacje rozpoczęto we wrześniu ubiegłego roku. Kontynuowała je już ekipa rządu PiS."
    	 +"Macierewicz: "
    	 +"Wiele wskazuje na to, że były to działania pozorowane. Jeszcze przed wyborami Antoni Macierewicz mówił, że ten kontrakt \"to grabież\", a \"13 mld zł powinno pójść na polskie fabryki produkujące najlepsze helikoptery na świecie\". - To świadome działanie na szkodę Polski - oceniał."
    	 +"Po wyborach - gdy stanął na czele MON - w trochę łagodniejszym tonie zapowiedział, że decyzja o podpisaniu kontraktu zależy od negocjacji offsetowych."
    	 +"We wtorek Ministerstwo Rozwoju poinformowało, że kończy negocjacje, bo Airbus \"nie przedstawił oferty zabezpieczającej w należyty sposób interes ekonomiczny i bezpieczeństwo państwa\". \"Rozbieżności w stanowiskach negocjacyjnych uniemożliwiają osiągnięcie kompromisu\" - napisał resort."
    	 +"Eskalacja żądań "
    	 +"Dla branży lotniczej nie jest to zaskoczenie. Podczas niedawnych targów obronnych w Kielcach mówiono, że strona polska stawia coraz to nowe wymagania, a zerwania przetargu domagają się związki zawodowe z zakładów, których oferty przegrały, i politycy PiS z tych regionów."
    	 +"Zaostrzenie wymagań można było wnioskować z informacji przekazywanych przez Airbus - pierwotnie była mowa o stworzeniu 3250 miejsc pracy, a we wrześniu już o 6 tys. Francuzi zapewniali, że oferowane inwestycje nie są zagrożone, nawet jeżeli Polska nie kupi caracali. Chodzi m.in. o utworzenie biura projektów Airbus Helicopters w Łodzi, współpracę z uczelniami, produkcję części w Lublinie i we Wrocławiu oraz otwarcie zakładów Azura Polska w Łodzi (zamiast w Rumunii), które mają produkować części do airbusów A350 XWB."
    	 +"W planach było jeszcze m.in. przekazanie do Wojskowych Zakładów Lotniczych w Łodzi kompetencji do samodzielnej obsługi, napraw i modernizacji caracali oraz zaangażowanie kolejnych zakładów w produkcję elementów śmigłowców i uzbrojenia. A nawet produkcja śmigłowców."
    	 +"W ostatniej chwili strona polska - jak się dowiadujemy - postawiła kolejne wymagania, których Airbus już nie mógł spełnić. Może chodzić o przekazanie przez Francję technologii niezbędnej do produkcji nowoczesnej amunicji artyleryjskiej, co jeszcze wiosną zapowiadał posłom szef Inspektoratu Uzbrojenia MON."
    	 +"PiS zabrakło pieniędzy? "
    	 +"- Spodziewałem się, że oczekiwania wobec Airbusa będą potęgowane i Francuzi powiedzą dość. Wszyscy czekali, kiedy to nastąpi. Moment wybrano fatalnie, przed wizytą w prezydenta François Hollande'a 13 października. W świat idzie sygnał, że jesteśmy niesłowni - mówi Maksymilian Dura z portalu Defence24.pl. Jego zdaniem powód zerwania rozmów jest prozaiczny: - Zabrakło pieniędzy."
    	 +" Podobnego zdania jest Tomasz Siemoniak (PO), b. szef MON, który rozpoczynał negocjacje: - Może chodzić o pieniądze do realizacji obietnic PiS lub na partyzantkę Macierewicza. 13 mld to wielka kwota i Mateusz Morawiecki może spoglądać na to łakomym wzrokiem, bo decyzję podjął parę dni po tym, gdy został ministrem finansów."
    	 +" Według naszych rozmówców \"realnym zagrożeniem\" jest pozew ze strony Airbusa, który poniósł koszty, ponieważ spodziewał się zamówień. Wicepremier Morawiecki kary się nie obawia. - Każdy etap negocjacji dokładnie dokumentowaliśmy. Jesteśmy przekonani, że byliśmy fair wobec naszych partnerów - mówił wczoraj."
    	 +"Bardziej ofensywny jest wiceszef MON Bartosz Kownacki. Jak stwierdził, ma \"wrażenie, że Airbus od początku negocjował w złej wierze i nie chciał zrealizować offsetu na warunkach, jakie wcześniej deklarował\"."
    	 +"Zakup nowoczesnych helikopterów dla armii oddala się na bliżej nieokreślony czas. Wojsko używa przestarzałych śmigłowców. Nie są np. uzbrojone w rakiety przeciwpancerne i wiele jest nieprzystosowanych do lotów w nocy."
    	 +"MON: po co te śmigłowce "
    	 +"Wiceminister Kownacki zapowiedział w Sejmie że \"trzeba dokonać analizy, w jakim zakresie, na jakich zasadach będą potrzebne w polskiej armii śmigłowce, w jaki sposób należy je kupować\"."
    	 +"Z decyzji Ministerstwa Rozwoju cieszą się związkowcy z Mielca i Świdnika. - Ale to połowa drogi. Teraz czekamy na znowelizowany program modernizacji wojska. Mamy nadzieję, że śmigłowce nadal będą priorytetem i liczymy, że nasza firma dostosuje ofertę do tych potrzeb - mówi Andrzej Kuchta, przewodniczący \"Solidarności\" w PZL Świdnik."; 
    	 
    	 
    	 for ( int i=0; i<10;i++){
    	 
    	 String output = nlp.NlpExternalPlProcess(inputText);  // send inputText to NLP engine and receive output
    	 
    	 //System.out.print(output);
    	 
    	  annotatorList = nlpAnn.NlpExtAnnGet(output, inputText, InputTextFilename); //adjust annotators in the context of inputText, join multi word annotators, return list of annotators
    	 }
    	  annotatorList.forEach(a->a.printall());
    	 
    }

}
