package com.ibm.pl;


public class OrthPl {
	
String chunkid;
String sentenceid;
String orthtext;	//orth
String orthbase;	//base
String ctag;		//ctag
String ann;			// tag ann parametr chan wtedy gdy wartosc tag =1
int orthStart;
int orthEnd;
String chan;			
int clear;			// flaga mowiπca czy wartoúci zosta≥y skasowane
String documentid;	//nazwa pliku lub dokumentu

public  OrthPl()
{
	chunkid ="";
	 sentenceid="";
	 orthtext="";
	 orthbase="";
	 ctag="";
	 ann="";
	 orthStart=0;
	 orthEnd=0;
	 chan="";
	 clear=1;
	 documentid="";

	}

public OrthPl(String porthtext){
orthtext=porthtext;

}

public void printall(){
	System.out.println(" chid: "+chunkid+" sentid: "+ sentenceid+" orthtext: "+orthtext+" orthbase: "+orthbase+" ctag: "+ctag
			+" ann: "+ann+" start: "+orthStart+" end: "+orthEnd+" chan: "+chan+" doc: "+documentid);	
};

public void setValue( String chId, String sentId, String orthText, String orthBase, String annot, int Start, int End, String docId)
{
	chunkid=chId;
	sentenceid=sentId;
	 orthtext=orthText;
	 orthbase=orthBase;
	 //ctag="";
	 ann=annot;
	 orthStart=Start;
	 orthEnd=End;
	 documentid=docId;
	
}

public String getChunkId()
{
	return chunkid;
}

public String getSentenceId()
{
	return sentenceid;
}

public String getOrthText()
{
	return orthtext;
}

public String getOrthBase()
{
	return orthbase;
}

public String getAnn()
{
	return ann;
}

public Integer getStart()
{
	return orthStart;
}

public Integer getEnd()
{
	return orthEnd;
}

public void clearAll(){
	chunkid ="";
	 sentenceid="";
	 orthtext="";
	 orthbase="";
	 ctag="";
	 ann="";
	 orthStart=0;
	 orthEnd=0;
	 chan="";
	 clear=1;
	 documentid="";

};

}
