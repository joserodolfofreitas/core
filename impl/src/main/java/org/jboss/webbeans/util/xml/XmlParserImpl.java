package org.jboss.webbeans.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.webbeans.introspector.AnnotatedItem;
import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logging;

public class XmlParserImpl // implements XmlParser
{

   private static Log log = Logging.getLog(XmlParserImpl.class);
   
   public Set<AnnotatedItem<?, ?>> parse(Set<URL> xmls)
   {
      Set<AnnotatedItem<?, ?>> result = new HashSet<AnnotatedItem<?, ?>>();

      for (URL url : xmls)
      {
         try
         {
            InputStream xmlStream;

            xmlStream = url.openStream();
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlStream);
            List<Element> beanElements = findBeans(document);
            result.addAll(ParseXmlHelper.getBeanItems(beanElements));
         }
         catch (IOException e)
         {
            log.debug("Can not open stream for " + url, e);
         }
         catch (DocumentException e)
         {
            log.debug("Error during the processing of a DOM4J document for " + url, e);
         }
      }
      return result;
   }

   private List<Element> findBeans(Document document)
   {
      List<Element> beans = new ArrayList<Element>();

      Element root = document.getRootElement();

      Iterator<?> elIterator = root.elementIterator();
      while (elIterator.hasNext())
      {
         Element element = (Element) elIterator.next();
         if (checkElementName(element) && checkElementChildrenNames(element))
            beans.add(element);
      }

      return beans;
   }   

   private boolean checkElementName(Element element)
   {
      if (ParseXmlHelper.isJavaEeNamespace(element) && 
            (element.getName().equalsIgnoreCase(XmlConstants.DEPLOY) || 
                  element.getName().equalsIgnoreCase(XmlConstants.INTERCEPTORS) || 
                  element.getName().equalsIgnoreCase(XmlConstants.DECORATORS)))
         return false;
      return true;
   }

   private boolean checkElementChildrenNames(Element element)
   {
      Iterator<?> elIterator = element.elementIterator();
      while (elIterator.hasNext())
      {
         Element child = (Element) elIterator.next();
         if (ParseXmlHelper.isJavaEeNamespace(child) && 
               (child.getName().equalsIgnoreCase(XmlConstants.BINDING_TYPE) || 
                     child.getName().equalsIgnoreCase(XmlConstants.INTERCEPTOR_BINDING_TYPE) || 
                     child.getName().equalsIgnoreCase(XmlConstants.STEREOTYPE)))
            return false;
      }
      return true;
   }
}