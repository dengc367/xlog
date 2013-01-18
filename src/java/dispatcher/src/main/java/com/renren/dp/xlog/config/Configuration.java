package com.renren.dp.xlog.config;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class Configuration {
  private static Properties paramProps = null;
  private static Properties descriptionProps = null;

  private static Log LOG = LogFactory.getLog(Configuration.class);
  static {
    paramProps = new Properties();
    descriptionProps = new Properties();
    URL url = Configuration.class.getClassLoader()
        .getResource("conf/xlog.xml");
    if (url != null) {
      loadResource(url);
    } else {
      LOG.error("Fail to found conf/xlog.xml at classpath");
    }
  }

  public static String getString(String paramName) {
    return paramProps.getProperty(paramName);
  }

  public static void setString(String key, String value) {
    paramProps.setProperty(key, value);
  }

  public static String getString(String paramName, String defaultValue) {
    String strValue = paramProps.getProperty(paramName);
    if (strValue == null || "".equals(strValue)) {
      return defaultValue;
    }

    return strValue;
  }

  public static int getInt(String paramName, int defaultValue) {
    String strValue = paramProps.getProperty(paramName);
    if (strValue == null || "".equals(strValue)) {
      return defaultValue;
    }
    return Integer.parseInt(strValue);
  }

  public static long getLong(String paramName, long defaultValue) {
    String strValue = paramProps.getProperty(paramName);
    if (strValue == null || "".equals(strValue)) {
      return defaultValue;
    }
    return Long.parseLong(strValue);
  }

  public static Boolean getBoolean(String paramName, boolean defaultValue) {
    String strValue = paramProps.getProperty(paramName);
    if (strValue == null || "".equals(strValue)) {
      return defaultValue;
    }
    return Boolean.parseBoolean(paramName);
  }

  public static Set<?> getParameterNames(){
    return paramProps.keySet();
  }
  public static String getDescription(String key){
    return descriptionProps.getProperty(key);
  }
  private static void loadResource(Object name) {
    try {
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
          .newInstance();
      // ignore all comments inside the xml file
      docBuilderFactory.setIgnoringComments(true);

      // allow includes in the xml file
      docBuilderFactory.setNamespaceAware(true);
      try {
        docBuilderFactory.setXIncludeAware(true);
      } catch (UnsupportedOperationException e) {
        LOG.error("Failed to set setXIncludeAware(true) for parser "
            + docBuilderFactory + ":" + e, e);
      }
      DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
      Document doc = null;
      Element root = null;
      if (name instanceof URL) {
        doc = builder.parse(((URL) name).toString());
      } else if (name instanceof Element) {
        root = (Element) name;
      }
      if (root == null) {
        root = doc.getDocumentElement();
      }
      if (!"configuration".equals(root.getTagName()))
        LOG.fatal("bad conf file: top-level element not <configuration>");
      NodeList props = root.getChildNodes();
      for (int i = 0; i < props.getLength(); i++) {
        Node propNode = props.item(i);
        if (!(propNode instanceof Element))
          continue;
        Element prop = (Element) propNode;
        if ("configuration".equals(prop.getTagName())) {
          loadResource(prop);
          continue;
        }
        if (!"property".equals(prop.getTagName()))
          LOG.warn("bad conf file: element not <property>");
        NodeList fields = prop.getChildNodes();
        String attr = null;
        String value = null;
        String description=null;
        for (int j = 0; j < fields.getLength(); j++) {
          Node fieldNode = fields.item(j);
          if (!(fieldNode instanceof Element))
            continue;
          Element field = (Element) fieldNode;
          if ("name".equals(field.getTagName()) && field.hasChildNodes())
            attr = ((Text) field.getFirstChild()).getData().trim();
          if ("value".equals(field.getTagName()) && field.hasChildNodes())
            value = ((Text) field.getFirstChild()).getData();
          if ("description".equals(field.getTagName()) && field.hasChildNodes())
            description = ((Text) field.getFirstChild()).getData();
          
        }
        if (attr != null && value != null) {
          paramProps.setProperty(attr, value);
        }
        if (description != null && value != null) {
          descriptionProps.setProperty(attr, description);
        }
      }
    } catch (IOException e) {
      LOG.fatal("error parsing conf file: " + e);
      throw new RuntimeException(e);
    } catch (DOMException e) {
      LOG.fatal("error parsing conf file: " + e);
      throw new RuntimeException(e);
    } catch (SAXException e) {
      LOG.fatal("error parsing conf file: " + e);
      throw new RuntimeException(e);
    } catch (ParserConfigurationException e) {
      LOG.fatal("error parsing conf file: " + e);
      throw new RuntimeException(e);
    }
  }
}
