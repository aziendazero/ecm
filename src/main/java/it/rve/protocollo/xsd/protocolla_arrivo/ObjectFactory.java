//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.10.07 at 02:47:09 PM CEST 
//


package it.rve.protocollo.xsd.protocolla_arrivo;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.rve.protocollo.xsd.protocolla_arrivo package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.rve.protocollo.xsd.protocolla_arrivo
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Allegati }
     * 
     */
    public Allegati createAllegati() {
        return new Allegati();
    }

    /**
     * Create an instance of {@link Files }
     * 
     */
    public Files createFiles() {
        return new Files();
    }

    /**
     * Create an instance of {@link Files.Documento }
     * 
     */
    public Files.Documento createFilesDocumento() {
        return new Files.Documento();
    }

    /**
     * Create an instance of {@link DocumentoPrincipale }
     * 
     */
    public DocumentoPrincipale createDocumentoPrincipale() {
        return new DocumentoPrincipale();
    }

    /**
     * Create an instance of {@link Richiesta }
     * 
     */
    public Richiesta createRichiesta() {
        return new Richiesta();
    }

    /**
     * Create an instance of {@link Destinatari }
     * 
     */
    public Destinatari createDestinatari() {
        return new Destinatari();
    }

    /**
     * Create an instance of {@link Mittente }
     * 
     */
    public Mittente createMittente() {
        return new Mittente();
    }

}
