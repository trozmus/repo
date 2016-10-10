package com.ibm.pl;


	import java.io.*;
	import java.util.ArrayList;
	import java.util.List;
	import org.jsoup.Jsoup;
	import org.jsoup.nodes.*;
	import org.jsoup.parser.Parser;
	import org.jsoup.select.Elements;
	import org.jsoup.nodes.Document;
	    
	public class NlpExtAnn2 {      
		
		static int index = 0;
		 int chunk = 0;
	     String chunkid="";
	     int sentence=0;
	     String sentenceid="";
	 
	     String disamb = "";
	     String orthtext = "";	//orth
	     String orthbase="";	//base
	     String ctag="";		//ctag
	     String ann="";			// tag ann parametr chan wtedy gdy wartosc tag =1
	     String anntemp="";
	     
	     int orthStart;
	     int orthEnd;
	     String chan="";			
	     int clear;			// flaga mowi�ca czy warto�ci zosta�y skasowane
	     String documentid="";	//nazwa pliku lub dokumentu
	     
	     //List<orth> lista = new ArrayList<orth>();
    
	 public static void main (String args []) throws IOException {
	 try {
	//String[] file={"/Users/trozmus/Documents/workspace/NLPPL/data/dokument1.txt.xml","C:/Users/IBM_ADMIN/workspace/NLPPL/data/test2.xml","C:/Users/IBM_ADMIN/workspace/NLPPL/data/test2.txt"};

		//System.out.print("Start Main \n"); 
		
		//String source = "Czy Wielka Brytania może wyjść z UE od razu? A może za rok Polska?";	
		//String xmlResource = NlpExtAnn2.readFile(file[0]);
		 
		 //NlpExtAnn2.parseNLPXML(xmlResource);
		//System.out.println(xmlResource+"\n");
	//	 NlpExtAnn2.NlpExtAnnGet(xmlResource , source , "bababab"); 
		     
		
		//list = nlpExtAnn.NlpExtAnnGet("C:/Users/IBM_ADMIN/workspace/NLPPL/data/dokument1.txt");
		
	 }
	   catch(Exception e) {e.printStackTrace();}       
	    
	 }
	 
	 private static String readFile(String file) throws IOException {
		    BufferedReader reader = new BufferedReader(new FileReader (file));
		    String         line = null;
		    StringBuilder  stringBuilder = new StringBuilder();
		    String         ls = System.getProperty("line.separator");

		    try {
		        while((line = reader.readLine()) != null) {
		            stringBuilder.append(line);
		            stringBuilder.append(ls);
		        }

		        return stringBuilder.toString();
		    } finally {
		        reader.close();
		    }
		}
	          
	 
	 
	 public  static List<OrthPl> parseNLPXML(String AnnotatorText){
		 List<OrthPl> list = new ArrayList<OrthPl>(); 
			
		 Document doc = (Document) Jsoup.parse(AnnotatorText,"", Parser.xmlParser()); //,"",Parser.xmlParser());
			
			//System.out.println(doc.toString());
			String chunkId = "";
			String sentenceId = "";
			Elements chunkList = doc.select("chunk");
			for(Element chunk: chunkList){
				
				chunkId = chunk.attr("id");
				
				//System.out.println(chunk.toString()+"\n");
				Elements sentenceList = chunk.select("sentence");
				for(Element sentence: sentenceList) {
					sentenceId = sentence.attr("id");
					
					Elements tokList = sentence.select("tok");
					for(Element tok: tokList) {
						String orth = tok.select("orth").text();
						String base = tok.select("lex").select("base").text();
						String ctag = tok.select("lex").select("ctag").text();
						String ann ="";
						String chan="0";
						
						Elements annList = tok.select("ann");
						for(Element annElement: annList) {
							//System.out.print("ann element "+annElement.text()+"\n");
						if (!annElement.text().contains("0")) 
						{
								ann = annElement.attr("chan");
								chan = annElement.text();
								//System.out.println("nie zero "+chan+" \n");
						}
						
						
					}
						
						//System.out.print(chunkId+" "+sentenceId+" "+orth+" "+base+" "+ctag+" "+ann+" "+chan+"\n");
						OrthPl orthC = new OrthPl();
				    	orthC.ann=ann;
				    	orthC.chan=chan;
				    	orthC.chunkid=chunkId;
				    	orthC.clear=1;
				    	orthC.ctag=ctag;
				    	//orthC.documentid=FileName;
				    	orthC.orthbase=base;
				    	orthC.orthEnd=0;
				    	orthC.orthStart=0;
				    	orthC.orthtext=orth;
				    	orthC.sentenceid=sentenceId;
				    	ann="";
				    	chan="";
				    	list.add(orthC);
					
				}
					
				
				
					
				
			}
			}
		 
		 return list;
	 }
	 
	 public  List<OrthPl> NlpExtAnnGet(String AnnotatorText, String InputText, String InputTextFilename) throws IOException{
		 
		 
		 // Annotators from NLP as String XML
		 // InputText - original text to map begin and end Anotators
		 //InputTextFilename - file name of analyzed tex/doc/web
		 
		 
		 List<OrthPl> list = new ArrayList<OrthPl>(); 
		 List<OrthPl> finalList = new ArrayList<OrthPl>(); 
		 
		 //System.out.println("Przeszukiwany text: "+InputText+"\n");
		 
         try {        
     	    
        	 list = NlpExtAnn2.parseNLPXML(AnnotatorText);
            int allFileIndex = 0;
            
     	    // dla Adnotacji przypisywane są pozycje w analizowanyym tekście
     	    int listSize = list.size();
     	    int i = 0 ;
     	    while  ( i<listSize ) {
     	    	
     	    	OrthPl orthTemp = list.get(i);
     	    	String search=orthTemp.orthtext;
     	    	int startIndex = InputText.indexOf(search,allFileIndex);
     	    	if(startIndex!=-1){
     	    		allFileIndex=startIndex;
     	    		int endIndex=startIndex+search.length();
     	    		orthTemp.orthStart = startIndex;
     	    		orthTemp.orthEnd = endIndex;
     	    		list.set(i, orthTemp);
     	   // 		System.out.print("znalazl "+search+" "+orthTemp.getAnn()+" "+startIndex+ " "+endIndex+" \n");
     	    	}
     	    	i++;
     	    }
     	    
     	    
     	    i = 0;
     	    
 	    	while ( i < listSize) {
 	    	
 	    		OrthPl orthTemp = list.get(i);
 	 	    	if ( !orthTemp.ann.isEmpty())
 	 	    	{
 	 	    		boolean findNext = Boolean.TRUE;
 	 	    		int index = i+1;
 	 	    		
 	 	    		while ( index < listSize & findNext) {
 	 	    			OrthPl orthNext = list.get(index);
 	 	    			//System.out.println("sklejanie "+orthTemp.ann+ " == "+ orthNext.ann + " && " + orthTemp.chan + " ==" + orthNext.chan);
 	 	    			
 	 	    			if ( orthTemp.ann.equals(orthNext.ann) && orthTemp.chan.equals(orthNext.chan)) {  
 	 	    				//System.out.println(" \n TRUE \n");
 	 	    				orthTemp.orthEnd = orthNext.orthEnd;
 	 	    				if (orthTemp.orthEnd == orthNext.orthStart){
 	 	    					orthTemp.orthtext += orthNext.orthtext;
 	 	    				} else {
 	 	    					orthTemp.orthtext += " "+orthNext.orthtext;
 	 	    				}
 	 	    				orthTemp.orthbase += " "+orthNext.orthbase;
 	 	    				
 	 	    				i=index;
 	 	    				index++;
 	 	    				
 	 	    			} else
 	 	    				findNext = Boolean.FALSE; // end if
 	 	    			

 	 	    		} 
 	 	    		
 	 	    		// dodać do finalList nowy ewentualnie połączony annotator
 	 	    		finalList.add(orthTemp);	
 	    		} // end if orthTemp.ann != ""
	 	    	
 	 	    	i++;
 	    		
 	    	}
 	    	
     	    
     	   // list.forEach(a->a.printall());
     	   // System.out.print("\n po połączeniu \n");
     	   // finalList.forEach(a->a.printall());
     	   }
     	   catch(Exception e) {e.printStackTrace();}  
		 
		 
		 
		 return finalList ; 
	 }
	} //class DOM 
