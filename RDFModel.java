/**
* The RDFModel creates an RDF document using two tables in MySQL. 
* Prerequisites: add the Jena API library and the MySQL connector to the project.
* Output: rdf_name.rdf
* Input: Two tables with the IRIs from an ontology and its properties.
* 
* rdf_node:Table
* node_id INT PK
* model_name VARCHAR

* rdf_property:Table
* property_id INT PK
* id_node INT FK
* property VARCHAR
* property_value VARCHAR
*
* @author  Jan Beeck
* @version 1.0
* @since   2014-09-23 
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//http://jena.apache.org/documentation/javadoc/jena/com/hp/hpl/jena/rdf/model/Model.html

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class RDFModel 
{
	public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
		Connection con = null;
		  
		  //Create a model
		 Model model = ModelFactory.createDefaultModel();
		 model.setNsPrefix("owl", RDFS.getURI());
		 //model.setNsPrefix("rdfs",RDFS.getURI());
		 //String modelName = "evidences"; //model evidences table rdf_node
		 //Name of the RDF to create.
		 String modelName = "asthma"; //model evidences table rdf_node
		 String property = null;
		 String subject = null;
		 
		Resource blankSubject = model.createResource(com.hp.hpl.jena.rdf.model.AnonId.create("1"));
		 //Resource blankSubject = model.createResource(com.hp.hpl.jena.rdf.model.AnonId.create("100"));
		 
		 try {
		   Class.forName("com.mysql.jdbc.Driver");
		   String url = "jdbc:mysql://localhost:3306/rdf_model";
		   con = DriverManager.getConnection(url, "root","root");
		   Statement st = con.createStatement();
		   Statement st1 = con.createStatement();
		   Statement st2 = con.createStatement();
		   int iter;
		   int i;
		   
		   //Find the number of nodes per model
		   ResultSet rs = st.executeQuery("select count(*) from rdf_node where model_name ='"+ modelName+"'");
		   rs.next();
		   int total_nodes = rs.getInt(1);
		   
		   //first loop. 
		   //It is 1000 because is the second model
		   for (iter = 1000 ; iter <= total_nodes  + 10000  ; iter++)
		   {
			   String id = Integer.toString(iter); //node id
			   //It is better to only have one declaration of blankSubject
			   if (iter != 1000) {
				   blankSubject = model.createResource(com.hp.hpl.jena.rdf.model.AnonId.create(id)); }
				     
			   //Find the number of statements per node
			   ResultSet rs1 = st1.executeQuery("select count(*) from rdf_property where id_node =" + iter);
			   rs1.next();
			   int facts = rs1.getInt(1);
		   
			   //Retrieve all the facts of a node
			   ResultSet rs2 = st2.executeQuery("select * from rdf_property where id_node =" + iter);
			   
			   for (i = 1; rs2.next() && i <= facts; i++){		   
					   property = rs2.getString(3);
					   subject = rs2.getString(4);
					   blankSubject.addProperty(model.createProperty(property),subject);
					   //System.out.println("creating node " + model.toString());	
					   property = null;
					   subject = null;
				   }//end for
		   }//end while
		 }//try
		   
		catch (Exception e) {
		   e.printStackTrace();
		   System.out.println("Exception: " + e.getMessage());
		  } finally {
		   try {
		    if (con != null)
		     con.close();
		   } catch (SQLException e) {
		    
		   }
		  }
		
		 model.write(System.out);
		 
		//Write into a file
	    //PrintWriter writer = new PrintWriter("evidences.rdf", "UTF-8");
	    PrintWriter writer = new PrintWriter("asthma.rdf", "UTF-8");
	    model.write(writer);
	   writer.close();
	}//end main
	
}//end class
