package com.xzit.train.generator.gen;

import com.xzit.train.generator.util.FreemarkerUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ServerGenerator {
    static String servicePath = "train-member\\src\\main\\java\\com\\xzit\\train\\[module]\\service\\impl\\";
    static String pomPath ="generator\\pom.xml";
//    static {
//        new File(servicePath).mkdirs();
//    }

    public static void main(String[] args) throws Exception {
        String generatorPath = getGeneratorPath();

        String module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        String ModuleServicePath = servicePath.replace("[module]", module);
        System.out.println(ModuleServicePath);
        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText()+"/"+domainObjectName.getText());
        String Domain = domainObjectName.getText();
        String domain = Domain.substring(0,1).toLowerCase()+Domain.substring(1);
        String do_main= tableName.getText().replaceAll("_","-");
        Map<String,Object> param=new HashMap<>();
        param.put("domain",domain);
        param.put("do_main",do_main);
        param.put("Domain",Domain);
        System.out.println(param);
        FreemarkerUtil.initConfig("service.ftl");
        FreemarkerUtil.generator(ModuleServicePath+Domain+"ServiceImpl.java",param);
    }

    private static String getGeneratorPath() throws DocumentException {
        SAXReader reader = new SAXReader();
        Map<String,String> map = new HashMap<>();
        map.put("pom","http://maven.apache.org/POM/4.0.0");
        reader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document=reader.read(pomPath);
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }
}























