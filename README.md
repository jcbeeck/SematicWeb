# SematicWeb
Project: Using ontologies to retrieve evidences from clinical notes.

Specification: match words/tokens of clinical notes with its related evidences.

This is the source code of the project. It is based on two ontologies, one is for adult asthma and the other is the structure of two evidence databases.
It has two classes and a MySQL database, this is the order to call them.
(1)RDFModel.java
(2)Match.java

We provide a capture knowledge ontology in this case the domain is adult asthma (asthma.owl) and its repository asthma.rdf, and the second is a knowledge ontology repository (evidence.owl) with its repository evidences.rdf, also there is an example of an input in a clinical note (case1.txt).

![asthma_uml](https://cloud.githubusercontent.com/assets/8194215/16206468/3d26c894-36ee-11e6-85b3-34353d32f840.png)

Asthma ontology, knowledge capture.

![evidence_uml](https://cloud.githubusercontent.com/assets/8194215/16206472/3eda832e-36ee-11e6-97a3-bf099fd5da57.png)

Evidence ontology, structure of an evidence database.

![ed](https://cloud.githubusercontent.com/assets/8194215/16206909/3cbc4aee-36f0-11e6-9b96-999686e42448.png)

Model entity-relationship diagram.

Notes:
 - An ontology is an explicit specification of a conceptualization, which is the collection
of objects, concepts, and other entities that are presumed to exist in some area of interest and
the relationships that hold them (GRUBER, 1993). Is a common language, or a vocabulary,
where classes, subclasses, properties, and also the relation between the classes and properties
are defined. Is also domain-specific and it allows the creation of distributed RDF documents. It
also can be seen as the concepts’ structure. Thus, is the model to represent the knowledge in an
easy and readily processed (understood) by machine. In an ontology the facts are expressed as
RDF statements.
 - One of the main advantages of using ontologies is the possibility of creating flexible
   models, capable of integrating different domains and heterogeneous sources.
 - The presented implementation can be improved. One limitation in this solution is
   that all the models (RDF documents) raw data are stored into a single database table. As this
   solution may affect the scalability, creating one table per model (domain) in the database could
   overcome this limitation.
 - Link to the related work: 
   http://www.bibliotecadigital.unicamp.br/document/?code=000960914&opt=4
