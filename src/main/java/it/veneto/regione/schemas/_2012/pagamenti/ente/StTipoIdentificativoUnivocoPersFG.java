
package it.veneto.regione.schemas._2012.pagamenti.ente;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for stTipoIdentificativoUnivocoPersFG.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="stTipoIdentificativoUnivocoPersFG">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="F"/>
 *     &lt;enumeration value="G"/>
 *     &lt;length value="1"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "stTipoIdentificativoUnivocoPersFG")
@XmlEnum
public enum StTipoIdentificativoUnivocoPersFG {

    F,
    G;

    public String value() {
        return name();
    }

    public static StTipoIdentificativoUnivocoPersFG fromValue(String v) {
        return valueOf(v);
    }

}
