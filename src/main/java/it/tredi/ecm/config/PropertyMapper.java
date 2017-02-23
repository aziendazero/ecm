package it.tredi.ecm.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

@Component("PropertyMapper")
@PropertySource(name="auditlabelmap", value="classpath:auditlabelmap.properties")
public class PropertyMapper {

    @Autowired
    ApplicationContext applicationContext;

    public HashMap<String, Object> startWith(String qualifier, String startWith) {
        return startWith(qualifier, startWith, false);
    }

    public HashMap<String, Object> startWith(String qualifier, String startWith, boolean removeStartWith) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        MapPropertySource mapPropSource = getMapProperties(qualifier);

        Object obj = mapPropSource.getSource();
        if (obj instanceof Properties) {
            Properties mobileProperties = (Properties)obj;

            if (mobileProperties != null) {
                for (Entry<Object, Object> e : mobileProperties.entrySet()) {
                    Object oKey = e.getKey();
                    if (oKey instanceof String) {
                        String key = (String)oKey;
                        if (((String) oKey).startsWith(startWith)) {
                            if (removeStartWith)
                                key = key.substring(startWith.length());
                            result.put(key, e.getValue());
                        }
                    }
                }
            }
        }

        return result;
    }

    public MapPropertySource getMapProperties(String name) {
        for (Iterator<?> it = ((AbstractEnvironment) applicationContext.getEnvironment()).getPropertySources().iterator(); it.hasNext();) {
            Object propertySource = it.next();
            if (propertySource instanceof MapPropertySource
                    && ((MapPropertySource) propertySource).getName().equals(name)) {
                return (MapPropertySource) propertySource;
            }
        }
        return null;
    }
}