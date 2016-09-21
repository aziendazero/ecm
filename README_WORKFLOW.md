# ECM - (Sviluppo sistema informatico per la gestione dei crediti informativi ECM)

## Workflow
* Si da per scontato che Bonita deve utilizzi postgress e quindi nella cartella lib di tomcat deve essere presente postgresql-9.4.1210.jre7.jar
* Occorre configurare correttamente e copiare nella cartella webapps\bonita\WEB-INF\classes di tomcat i files:
	ecm.properties
	ecm-connectors.properties
* Occorre copiare nella cartella webapps\bonita\WEB-INF\lib di tomcat il jar
	EcmBonitaUtils-1.0.0.jar
