/**
* The Match class reads two RDF documents and a text file. 
* It also retrieves and match the words from an input text file and the RDFs through SPARQL queries.  
* Prerequisites: Add the Jena API library to the project.
* Output: Matched concepts, and node | queryConcept | url.
* Ex:
* -------------------------------------------------------------------------------------
* | node  | queryProblem       | url   
* =====================================================================================
* |_:b5  | "Nasal irritation" | "http://www.sjweh.fi/show_abstract.php?abstract_id=458"
* 
* Input: rdf_name.rdf; rdf_name2.rdf; text.txt
*
* @author  Jan Beeck
* @version 1.0
* @since   2014-09-23 
*/

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.*;

public class Match {

	
	//Step1: Tokenize
	public static List<String> ReadFile(File input) throws FileNotFoundException
    {
		List<String> token_list = new ArrayList<String>(); 
		List<String> final_token_list = new ArrayList<String>(); 
		
        try {
            Scanner scanner = new Scanner(input);
            scanner.useDelimiter("[\\s]"); //whitespace
 
            while (scanner.hasNext()) {
                //String line = scanner.nextLine();
                String token = scanner.next();
                //remove the last character if its a ',' or '.' or ';'
                if (token.length() > 0 && token.charAt(token.length()-1)==',' || token.charAt(token.length()-1)=='.'
                		|| token.charAt(token.length()-1)==';')
                {
                	token = token.substring(0, token.length()-1);
                	token_list.add(token);	
                }
                else{
                	token_list.add(token);	
                }
               
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
   		//return tokens bigger than 3 characters
        return final_token_list = RemoveTokens(token_list);
   
        
    }//end function
	
	//Step2: Remove tokens with length less than 3 characters.
	public static List<String> RemoveTokens(List<String> list_input) throws FileNotFoundException
	{
		List<String> tokens_remove = new ArrayList<String>(); 
		
		for(int i = 0; i < list_input.size(); i++)
		 {
			 String token = list_input.get(i);
			 if (token.length() > 3)
			 {
				 tokens_remove.add(token);
			 }
		 }//end for
        
        return tokens_remove;
	
	}
	
	//Step3: 
	//Find the concepts related with the class Problem in the Asthma ontology.
	public static List<String> findProblemTokens() throws UnsupportedEncodingException
	{
		String inputRDFfile  = "asthma.rdf";
			 
		// create an empty model
		Model model = ModelFactory.createDefaultModel();
			    
		InputStream in = FileManager.get().open(inputRDFfile);
		if (in == null) {
			throw new IllegalArgumentException( "File: " + inputRDFfile + " not found");
		}
			    
		 // read the RDF/XML file
		model.read(in, null);
			 
		//Find #symptomName in the ontology.
		String queryString = "PREFIX j.0:<http://www.semanticweb.org/jan/ontologies/2014/5/asthma#>" +
				      "PREFIX j.1:<http://purl.org/dc/terms/>" +
				      "SELECT *" +
				       		"WHERE {" +
						          "?x j.0:symptomName ?symptomName" + "." +
						"}";
			 
		Query query = QueryFactory.create(queryString);
		// Execute the query and obtain results
		QueryExecution qe =QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();

		String queryToString = convertSPARQLIntoString(results, query);
		qe.close();
		
		List<String> ontolgy_tokens;
		List<String> problem_ontolgy_tokens = new ArrayList<>();
			
		ontolgy_tokens = parseSPARQLOuput(queryToString);
		
		 for(int i = 0; i < ontolgy_tokens.size(); i++)
	     {
			 problem_ontolgy_tokens.add(ontolgy_tokens.get(i));
	     }
		
		//Find #symptomDescription in the ontology.
		String queryString1 = "PREFIX j.0:<http://www.semanticweb.org/jan/ontologies/2014/5/asthma#>" +
				      "PREFIX j.1:<http://purl.org/dc/terms/>" +
					"SELECT *" +
						"WHERE {" +
							"?x j.0:symptomDescription ?symptomDescription" + "." +
						"}";
					 
		Query query1 = QueryFactory.create(queryString1);
		// Execute the query and obtain results
		QueryExecution qe1 =QueryExecutionFactory.create(query1, model);
		ResultSet results1 = qe1.execSelect();

		String queryToString1 = convertSPARQLIntoString(results1, query1);
		qe1.close();
		ontolgy_tokens = null;
		ontolgy_tokens = parseSPARQLOuput(queryToString1);
		
		for(int i = 0; i < ontolgy_tokens.size(); i++)
	     {
			problem_ontolgy_tokens.add(ontolgy_tokens.get(i));
	     }
		
		//Find #allergyName in the ontology.
		String queryString2 = "PREFIX j.0:<http://www.semanticweb.org/jan/ontologies/2014/5/asthma#>" +
				       "PREFIX j.1:<http://purl.org/dc/terms/>" +
				         "SELECT *" +
				   		"WHERE {" +
					           	"?x j.0:allergyName ?allergyName" + "." +
					         "}";
							 
	    Query query2 = QueryFactory.create(queryString2);
		// Execute the query and obtain results
		QueryExecution qe2 =QueryExecutionFactory.create(query2, model);
		ResultSet results2 = qe2.execSelect();

		String queryToString2 = convertSPARQLIntoString(results2, query2);
		qe2.close();
		ontolgy_tokens = null;
		ontolgy_tokens = parseSPARQLOuput(queryToString2);
				
		for(int i = 0; i < ontolgy_tokens.size(); i++)
		{
			problem_ontolgy_tokens.add(ontolgy_tokens.get(i));
	    }
		
	
		return problem_ontolgy_tokens = removeDuplicates(problem_ontolgy_tokens);
		
			
	}//end function
	
	//Step4: 
	//Find the concepts related with the class Intervention in the Asthma ontology.
	public static List<String> findInterventionTokens() throws UnsupportedEncodingException
	{
		String inputRDFfile  = "asthma.rdf";
				 
		// create an empty model
		Model model = ModelFactory.createDefaultModel();
				    
		InputStream in = FileManager.get().open(inputRDFfile);
		if (in == null) {
			throw new IllegalArgumentException( "File: " + inputRDFfile + " not found");
		}
				    
		// read the RDF/XML file
		model.read(in, null);
				 
		//Find #interventionName in the ontology.
		String queryString = "PREFIX j.0:<http://www.semanticweb.org/jan/ontologies/2014/5/asthma#>" +
				     "PREFIX j.1:<http://purl.org/dc/terms/>" +
				     	"SELECT *" +
				        	"WHERE {" +
							 "?x j.0:interventionName ?interventionName" + "." +
						"}";
				 
		Query query = QueryFactory.create(queryString);
		// Execute the query and obtain results
		QueryExecution qe =QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();

		String queryToString = convertSPARQLIntoString(results, query);
		qe.close();
			
		List<String> ontolgy_tokens;
		List<String> intervention_ontolgy_tokens = new ArrayList<>();
				
		ontolgy_tokens = parseSPARQLOuput(queryToString);
			
		for(int i = 0; i < ontolgy_tokens.size(); i++)
		{
			intervention_ontolgy_tokens.add(ontolgy_tokens.get(i));
		}
			
		//Find #interventionDescription in the ontology.
		String queryString1 = "PREFIX j.0:<http://www.semanticweb.org/jan/ontologies/2014/5/asthma#>" +
	                              "PREFIX j.1:<http://purl.org/dc/terms/>" +
				       "SELECT *" +
					   "WHERE {" +
						 "?x j.0:interventionDescription ?interventionDescription" + "." +
				           "}";
						 
		Query query1 = QueryFactory.create(queryString1);
		// Execute the query and obtain results
		QueryExecution qe1 =QueryExecutionFactory.create(query1, model);
		ResultSet results1 = qe1.execSelect();

		String queryToString1 = convertSPARQLIntoString(results1, query1);
		qe1.close();
		ontolgy_tokens = null;
		ontolgy_tokens = parseSPARQLOuput(queryToString1);
			
		for(int i = 0; i < ontolgy_tokens.size(); i++)
		{
			intervention_ontolgy_tokens.add(ontolgy_tokens.get(i));
		}
			
		//Find #testName in the ontology.
		String queryString2 = "PREFIX j.0:<http://www.semanticweb.org/jan/ontologies/2014/5/asthma#>" +
				       "PREFIX j.1:<http://purl.org/dc/terms/>" +
					"SELECT *" +
					 	"WHERE {" +
						 	"?x j.0:testName ?testName" + "." +
						 "}";
								 
		 Query query2 = QueryFactory.create(queryString2);
		// Execute the query and obtain results
		QueryExecution qe2 =QueryExecutionFactory.create(query2, model);
		ResultSet results2 = qe2.execSelect();

		String queryToString2 = convertSPARQLIntoString(results2, query2);
		qe2.close();
		ontolgy_tokens = null;
		ontolgy_tokens = parseSPARQLOuput(queryToString2);
					
		for(int i = 0; i < ontolgy_tokens.size(); i++)
		{
			intervention_ontolgy_tokens.add(ontolgy_tokens.get(i));
		}
			
		
		return intervention_ontolgy_tokens = removeDuplicates(intervention_ontolgy_tokens);
			
				
	}//end function
	
	//Step5: do the matching between the input tokens with problems.
    //       do the matching between the input tokens with interventions.
	public static List<String> matchInputWithOntology(List<String> inputTokens, List<String> ontologyTokens)
	{
		List<String> matchedTokens = new ArrayList<>();
		
		for(int i= 0; i < inputTokens.size(); i++)
		{
			for(int j= 0; j < ontologyTokens.size(); j++)
			{
			   boolean match = ontologyTokens.get(j).toLowerCase().contains(inputTokens.get(i).toLowerCase());
				if (match) {
					matchedTokens.add(ontologyTokens.get(j));
				}
			}
		}
		
		return matchedTokens = removeDuplicates(matchedTokens);
	}
	
	//Step6: find the matched words of Problems and Interventions for the queryString for PEDRO database.
	public static List<String> findMatchedforPEDRO(List<String> matchedProblems, List<String> matchedInterventions)
	{
		List<String> queryString_list = new ArrayList<String>();
		queryString_list.addAll(matchedProblems);
		queryString_list.addAll(matchedInterventions);
		
		return queryString_list = removeDuplicates(queryString_list);
		
	}
	
	//Step7: 
	//Match the queryProblems and queryIntervention with a PARQL query; and show related URLs.
	public static List<String> retrieveRDFInformation(List<String> problems, List<String> interventions, 
						          List<String> queryPedro) throws IOException
	{
		String inputRDFfile  = "evidences.rdf";
		
		List<String> retrieved_list = new ArrayList<String>(); 
		 
		// create an empty model
		Model model = ModelFactory.createDefaultModel();
				    
		InputStream in = FileManager.get().open(inputRDFfile);
		if (in == null) {
			throw new IllegalArgumentException( "File: " + inputRDFfile + " not found");
		}
				    
		// read the RDF/XML file
		model.read(in, null);
				 
		
		for (int i=0; i<problems.size(); i++)
		{
			String term_to_find = problems.get(i);
			//Retrieve throw a SPARQL query
			final ParameterizedSparqlString queryString = new ParameterizedSparqlString(
		    		"PREFIX j.1:<http://www.semanticweb.org/jan/ontologies/2014/5/evidences#>" +
		   	        "PREFIX j.0:<http://purl.org/dc/terms/>" +
		   	        	"SELECT *" +
		                  		"WHERE {" +
					                  "?node j.1:queryProblem ?value." +
			                          	  "?node j.1:queryProblem ?queryProblem." + 
			                                   //"?node j.0:title ?title." + 
			                                   "?node j.1:evidenceURL ?url ." + 
		                           "}" );
		    queryString.setIri("?value", term_to_find);
		    
		    String newStringQuery = bindVariableInQuery(queryString, term_to_find);
					 
			Query query = QueryFactory.create(newStringQuery);
			 // Execute the query and obtain results
			QueryExecution qe =QueryExecutionFactory.create(query, model);
			ResultSet results = qe.execSelect();
		
			String queryToString = convertSPARQLIntoString(results, query);
			qe.close();
			retrieved_list.add(queryToString);				
		}//end fisrt for
		
		for (int j=0; j<interventions.size(); j++)
		{
			String term_to_find = interventions.get(j);
			//Retrieve throw a SPARQL query
			final ParameterizedSparqlString queryString = new ParameterizedSparqlString(
		    		"PREFIX j.1:<http://www.semanticweb.org/jan/ontologies/2014/5/evidences#>" +
		   	        "PREFIX j.0:<http://purl.org/dc/terms/>" +
		   	        	"SELECT *" +
		                  		"WHERE {" +
		                             		"?node j.1:queryIntervention ?value." +
		                             		"?node j.1:queryIntervention ?queryIntervention." + 
		                        		 //"?node j.0:title ?title." + 
		                             		"?node j.1:evidenceURL ?url ." + 
		                        "}" );
		    queryString.setIri("?value", term_to_find);
		    
		    String newStringQuery = bindVariableInQuery(queryString, term_to_find);
					 
			Query query = QueryFactory.create(newStringQuery);
			 // Execute the query and obtain results
			QueryExecution qe =QueryExecutionFactory.create(query, model);
			ResultSet results = qe.execSelect();
		
			String queryToString = convertSPARQLIntoString(results, query);
			qe.close();
			retrieved_list.add(queryToString);				
		}//end interventions For
		
		for (int k=0; k<queryPedro.size(); k++)
		{
			String term_to_find = queryPedro.get(k);
			//Retrieve throw a SPARQL query
			final ParameterizedSparqlString queryString = new ParameterizedSparqlString(
		    		"PREFIX j.1:<http://www.semanticweb.org/jan/ontologies/2014/5/evidences#>" +
		   	        "PREFIX j.0:<http://purl.org/dc/terms/>" +
		   	        	"SELECT *" +
		                  		"WHERE {" +
		                        		 "?node j.1:queryString ?value." +
		                             		 "?node j.1:queryString ?queryString." + 
		                        	          //"?node j.0:title ?title." + 
		                                         "?node j.1:evidenceURL ?url ." + 
		                        "}" );
		    queryString.setIri("?value", term_to_find);
		    
		    String newStringQuery = bindVariableInQuery(queryString, term_to_find);
					 
			Query query = QueryFactory.create(newStringQuery);
			 // Execute the query and obtain results
			QueryExecution qe =QueryExecutionFactory.create(query, model);
			ResultSet results = qe.execSelect();
		
			String queryToString = convertSPARQLIntoString(results, query);
			qe.close();
			retrieved_list.add(queryToString);				
		}//end interventions For
		
		return retrieved_list;
	
	}//end function
		
	//Convert SPARQL query results into string array
	public static String convertSPARQLIntoString(ResultSet results, Query query) throws UnsupportedEncodingException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		final ResultSetRewindable r = ResultSetFactory.copyResults(results);
		r.reset();
		ResultSetFormatter.out(baos, r, query);
		ps.flush();
		String queryOutput = new String(baos.toByteArray(), "UTF-8");
			
		return queryOutput;
			
	}
		
	//Parse the SPARQL query output and add the tokens to a list.
	public static List<String> parseSPARQLOuput(String queryOutput)
	{
		Scanner scanner = new Scanner(queryOutput);
		List<String> stringQuery_tokens = new ArrayList<String>(); 
		List<Character> pattern_in_query = new ArrayList<Character>();
			
		for (int temp_line = 0; scanner.hasNextLine(); temp_line++) {
			String line = scanner.nextLine();
				if (temp_line > 2){ //sparql format results
					int pos_p = line.indexOf('"');
						if (pos_p > 0){
							for (pos_p = pos_p+1; line.charAt(pos_p) != '"'; pos_p++ )
							{
								char a_char = line.charAt(pos_p);
								pattern_in_query.add(a_char);
							}
							String temp_token_pattern = getStringRepresentation(pattern_in_query);
							pattern_in_query.clear();
							stringQuery_tokens.add(temp_token_pattern);		
					   }
				 } 
		}//end for
		
		scanner.close();
			
		return stringQuery_tokens;
			
	}
	
	public static List<String> removeDuplicates(List<String> duplicateList)
	{
	    List<String> correctedList = new ArrayList<String>();
	    Set<String> a = new HashSet<String>();
	    a.addAll(duplicateList);
	    correctedList.addAll(a);
	    return correctedList;
	}
	
	public static String getStringRepresentation(List<Character> pattern)
	{    
	    StringBuilder builder = new StringBuilder(pattern.size());
	    for(Character ch: pattern)
	    {
	        builder.append(ch);
	    }
	    return builder.toString();
	}
	
	//Create the SPARQL query biding a variable.
	public static String bindVariableInQuery(ParameterizedSparqlString queryInput, String concept) throws IOException
	{
		String patternQuery = queryInput.toString();
		String first = null;
		String last = null;
		
		int lastpos = patternQuery.lastIndexOf('>',patternQuery.length());
		if (lastpos > 0){
			last = patternQuery.substring(lastpos + 1,patternQuery.length());
		}
		
		int firstpos = patternQuery.lastIndexOf('<',patternQuery.length());
		if ( firstpos > 0){
			first = patternQuery.substring(0,firstpos - 1);
		}
			
		String finalQuery = first + "  " + '"' + concept + '"' + "  " + last;
		
		return finalQuery;
		
	}//end function
	
	public static void main(String[] args) throws IOException
	{
		
		 File file = new File("case1.txt");
		 List<String> final_input_tokens_list = ReadFile(file);
		 //List<String> final_ontology_tokens = FindPatternInRFDFile();
		 //List<String> final_matched_tokens = matchWithOntology(final_ontology_tokens, final_tokens_list);
		 List<String> final_string_problems_tokens = findProblemTokens();
		 List<String> final_string_intervention_tokens = findInterventionTokens();
		 List<String> final_matched_problems_tokens = matchInputWithOntology(final_input_tokens_list, 
		                                              final_string_problems_tokens);
		 List<String> final_matched_interventions_tokens = matchInputWithOntology(final_input_tokens_list, 
		                                                   final_string_intervention_tokens);
		 List<String> final_queryString_tokens = findMatchedforPEDRO(final_matched_problems_tokens,
		                                         final_matched_interventions_tokens);
		 List<String> final_retrieved_list = retrieveRDFInformation(final_matched_problems_tokens, 
		                                     final_matched_interventions_tokens, final_queryString_tokens);
		
		 for(int i = 0; i < final_input_tokens_list.size(); i++)
	     {
			 System.out.println(final_input_tokens_list.get(i));
	     }
		 
		 System.out.println("-----------Problems---------");
		 
		 for(int j = 0; j <final_string_problems_tokens.size(); j++)
	     {
			System.out.println(final_string_problems_tokens.get(j));
	     }
		 
		 System.out.println("-----------Intervention---------");
		 
		 for(int j = 0; j <final_string_intervention_tokens.size(); j++)
	     {
			System.out.println(final_string_intervention_tokens.get(j));
	     }
		 
		 System.out.println("-----------Matched problems---------");
		 
		 for(int j = 0; j <final_matched_problems_tokens.size(); j++)
	     {
			System.out.println(final_matched_problems_tokens.get(j));
	     }
		 
		 System.out.println("-----------Matched interventions---------");
		 
		 for(int j = 0; j <final_matched_interventions_tokens.size(); j++)
	     {
			System.out.println(final_matched_interventions_tokens.get(j));
	     }
		 
		 System.out.println("-----------RETRIEVED---------");
		 
		 for(int j = 0; j <final_retrieved_list.size(); j++)
	     {
			System.out.println(final_retrieved_list.get(j));
	     }
		 
	  
		 
	}//end main function

	
}//end class
