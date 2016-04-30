# SematicWeb
Project: Using ontologies to retrieve evidences from clinical notes.

This is the source code of the project. It is based on two ontologies, one is for adult asthma and the other is the structure of two evidence databases.
It has two classes and a MySQL database, this is the order to call them.
(1)RDFModel.java
(2)Match.java

Also we provide a capture knowledge ontology in this case the domain is adult asthma (asthma.owl), and the second is a knowledge ontology repository (evidence.owl).

Notes:
------
 - One of main advantages of using ontologies is the possibility of creating flexible
   models, capable of integrating different domains and heterogeneous sources.
 - The presented implementation can be improved. One limitation in this solution is
   that all the models (RDF documents) raw data are stored into a single database table. As this
   solution may affect the scalability, creating one table per model (domain) in the database could
   overcome this limitation.
 - Link to the related work: 
   http://www.bibliotecadigital.unicamp.br/document/?code=000960914&opt=4
