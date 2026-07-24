package com.xzit.train.generator.gen;

import com.xzit.train.generator.util.DbUtil;
import com.xzit.train.generator.util.Field;
import com.xzit.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServerGenerator {
    static String servicePath = "[module]\\src\\main\\java\\com\\xzit\\train\\[module]\\";
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

        Node connectionURL = document.selectSingleNode("//@connectionURL");
        Node userId = document.selectSingleNode("//@userId");
        Node password = document.selectSingleNode("//@password");
        DbUtil.url = connectionURL.getText();
        DbUtil.user = userId.getText();
        DbUtil.password = password.getText();

        String Domain = domainObjectName.getText();
        String domain = Domain.substring(0,1).toLowerCase()+Domain.substring(1);
        String do_main= tableName.getText().replaceAll("_","-");
        String tableComment = DbUtil.getTableComment(tableName.getText());
        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
        Set<String> javaTypes = getJavaTypes(fieldList);

//组装参数
        Map<String,Object> param=new HashMap<>();
        param.put("module",module);
        param.put("domain",domain);
        param.put("do_main",do_main);
        param.put("Domain",Domain);
        param.put("tableNameCn",tableComment);
        param.put("fieldList",fieldList);
        param.put("typeSet",javaTypes);
        System.out.println(param);
        gen(ModuleServicePath, Domain, param,"saveReq","req");
        gen(ModuleServicePath, Domain, param,"adminController","controller\\admin","Admin"+Domain+"Controller");
        gen(ModuleServicePath, Domain, param,"service","service");
        gen(ModuleServicePath, Domain, param,"serviceImpl","service\\impl");
        gen(ModuleServicePath, Domain, param,"queryReq","req");
        gen(ModuleServicePath, Domain, param,"queryResp","resp");


    }

    private static void gen(String ModuleServicePath, String Domain, Map<String, Object> param,String target,String packageName) throws IOException, TemplateException {
        gen(ModuleServicePath, Domain, param, target, packageName, null);
    }

    private static void gen(String ModuleServicePath, String Domain, Map<String, Object> param,String target,String packageName, String fileNamePrefix) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target+".ftl");
        String toPath=ModuleServicePath+packageName+"\\";
        new File(toPath).mkdirs();
        System.out.println(toPath);
        String Target= target.substring(0,1).toUpperCase()+target.substring(1);
        String fileName = toPath + (fileNamePrefix != null ? fileNamePrefix : Domain + Target) + ".java";
        System.out.println(fileName);
        FreemarkerUtil.generator(fileName, param);
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
    public static Set<String> getJavaTypes(List<Field> fieldList) {

        Set<String> set = new HashSet<>();
        for (Field field : fieldList) {
            set.add(field.getJavaType());
        }
        return set;
    }
}























