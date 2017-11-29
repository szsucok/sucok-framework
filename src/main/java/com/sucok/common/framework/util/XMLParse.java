package com.sucok.common.framework.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.*;

/**
 * 提供提取消息格式中的密文及生成回复消息格式的接口.
 *
 * @author Tencent
 * @since 2014/11/4
 */
public class XMLParse {


    public static Map<String, Object> xml2Map(String xml){
    	  SAXReader saxReader = new SAXReader();
          Document doc;
		try {
			doc = saxReader.read(new StringReader(xml));
			 return document2Map(doc);
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
         
    }
      public static Properties xml2Properties(String xml){
          Map<String, Object> m=xml2Map(xml);
          Properties p=new Properties();
          for(String key:m.keySet()){
              Object o= m.get(key);
              if(o instanceof  String){
                  p.setProperty(key,m.get(key).toString());
              }else{
                  p.put(key,m.get(key));
              }

          }
          return p;

       }
        public static Map<String, Object> xml2Map(InputStream xml){
        SAXReader saxReader = new SAXReader();
        Document doc;
        try {
            doc = saxReader.read(new InputStreamReader(xml));
            return document2Map(doc);
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }

    }
    public static Map<String, Object> document2Map(Document doc){
        Map<String, Object> map = new HashMap<String, Object>();
        if(doc == null)  
            return map;  
        Element root = doc.getRootElement();  
        for (Iterator<?> iterator = root.elementIterator(); iterator.hasNext();) {  
            Element e = (Element) iterator.next();  
            //System.out.println(e.getName());  
            List<?> list = e.elements();  
            if(list.size() > 0){  
                map.put(e.getName(), element(e));
            }else {
                map.put(e.getName(), e.getText());
            }
        }  
        return map;  
    } 
    
    
    @SuppressWarnings("unchecked")
	public static Map<String,Object> element(Element e){
        Map<String,Object> map = new HashMap<String,Object>();
        List<?> list = e.elements();  
        if(list.size() > 0){  
            for (int i = 0;i < list.size(); i++) {  
                Element iter = (Element) list.get(i);  
                List<Object> mapList = new ArrayList<Object>();  
                  
                if(iter.elements().size()== 1){
                    Map<String,Object> m = element(iter);

                    map.put(iter.getName(), m);
                } else if(iter.elements().size() > 1){
                    Map<String,Object> m = element(iter);
                    map.put(iter.getName()+"_"+i, m);
                }
                else{  

                    map.put(iter.getName(), iter.getText());
                }  
            }  
        }else {
            map.put(e.getName(), e.getText());
        }
        return map;  
    }



    public static Map<String,Object> getMapFromXML(String xmlString) throws ParserConfigurationException, IOException, SAXException {

        return   xml2Map(xmlString);

    }

}
