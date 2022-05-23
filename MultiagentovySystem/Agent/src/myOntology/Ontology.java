/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myOntology;

/**
 *
 * @author patrik.matiasko
 */
public class Ontology {
  
     public static String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

     public static String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
 
     public static String OWL = "http://www.w3.org/2002/07/owl#";
     
     public static String XML_STRING = "^^http://www.w3.org/2001/XMLSchema#string";

     public static String SPARQL_PREFIX = 
             "PREFIX ont: <"+Config.BASE + "#> \nPREFIX rdf: <"+RDF+">\nPREFIX rdfs: <"+RDFS+"> \n";

     public static String SPARQL = "SPARQL";
  
     public static String BASE = Config.BASE;

     public static String AGENT_OWNERSHIP = "IISAS";

     public static String OWL_HEAD =
       "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
       "<rdf:RDF\n"+
       "    xmlns=\""+ Config.BASE + "#\"\n"+
       "    xmlns:rdf=\"" + RDF + "\"\n"+
       "    xmlns:rdfs=\"" + RDFS + "\"\n"+
       "    xmlns:owl=\"" + OWL + "\"\n"+
       "    xml:base=\""+ Config.BASE + "\"\n"+
       "  xml:base=\""+ Config.BASE + "\">\n";

     public static String OWL_FOOT = "\n</rdf:RDF>";
     
     public static String EXIT_CODE = "SaveAndExit^^http://www.w3.org/2001/XMLSchema#string";

}
