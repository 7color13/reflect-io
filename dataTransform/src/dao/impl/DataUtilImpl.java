package dao.impl;

import dao.IDataUtil;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.lang.Boolean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class DataUtilImpl implements IDataUtil {
    public static Object convertObject(String data, String type) {  //类型转换函数
        Object obj = null;
        switch (type) {
            case "String":
                obj = new String(data);
                break;
            case "double":
            case "Double":
                obj = new Double(data);
                break;
            case "float":
            case "Float":
                obj = new Float(data);
                break;
            case "int":
            case "Integer":
                obj = new Integer(data);
                break;
            //后面可以扩充转换为其他数据类型
        }
        return obj;
    }

    public static boolean isBaseDefaultValue(Object object) {   //判断int,float等值是否为默认值（即判断是默认的0还是人为设定的0）
        Class className = object.getClass();
        String strClassName = "" + className;
        if (className.equals(java.lang.Integer.class)) {
            return (int) object == 0;
        } else if (className.equals(java.lang.Byte.class)) {
            return (byte) object == 0;
        } else if (className.equals(java.lang.Long.class)) {
            return (long) object == 0L;
        } else if (className.equals(java.lang.Double.class)) {
            return (double) object == 0.0d;
        } else if (className.equals(java.lang.Float.class)) {
            return (float) object == 0.0f;
        } else if (className.equals(java.lang.Character.class)) {
            return (char) object == '\u0000';
        } else if (className.equals(java.lang.Short.class)) {
            return (short) object == 0;
        } else if (className.equals(java.lang.Boolean.class)) {
            return (boolean) object == false;
        }
        return false;
    }


    @Override
    public Object getXMLAsObject(String path, String className) {   //将xml转为对象
        Object obj = null;
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(path));
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            Class aClass = Class.forName(className);
            obj = aClass.newInstance();
            Map<String, String> map = new HashMap<String, String>();
            for (Element child : childElements) {
                List<Element> elementList = child.elements();
                for (Element ele : elementList) {
                    map.put(ele.getName(), ele.getText());
                }
            }
            for (String key : map.keySet()) {
                String data = map.get(key); // 获取参数值
                Field field = aClass.getDeclaredField(key);  //获取该参数对应的Field对象
                Object value = convertObject(data, field.getType().getSimpleName()); // 将字符串形式的参数值转换为vo中定义的类型
                String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1); // 获取setXxx方法名
                Method method = aClass.getDeclaredMethod(methodName, field.getType()); // 获取setXxxx方法对应的Method对象
                method.invoke(obj, value); // 执行对应的setXxxx方法
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public Object getURLAsObject(String url, String className) {
        Object obj = null;
        try {
            Class cla = Class.forName(className);
            obj = cla.newInstance();
            String urlArray[] = url.split("\\?");
            if (urlArray.length == 1)
                return null;
            String params = urlArray[1]; // 从url地址中取出所有的参数

            String paramsArray[] = params.split("&");
            Map<String, String> map = new HashMap<String, String>();
            // 把每一个参数存放为map中，以key-value形式进行存放
            for (String str : paramsArray) {
                String kvArray[] = str.split("=");
                map.put(kvArray[0], kvArray[1]);
            }
            // 遍历map中的所有数据
            for (String key : map.keySet()) {
                String data = map.get(key); // 获取参数值
                Field field = cla.getDeclaredField(key);  //获取该参数对应的Field对象
                Object value = convertObject(data, field.getType().getSimpleName()); // 将字符串形式的参数值转换为vo中定义的类型
                String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1); // 获取setXxx方法名
                Method method = cla.getDeclaredMethod(methodName, field.getType()); // 获取setXxxx方法对应的Method对象
                method.invoke(obj, value); // 执行对应的setXxxx方法
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public List<Object> getXMLAsArray(String path, String className) {
        List<Object> resultList = new ArrayList<Object>();
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(path));
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            Class aClass = Class.forName(className);
            
            for (Element child : childElements) {
                List<Element> elementList = child.elements();
                Object obj = aClass.newInstance();
                for (Element ele : elementList) {
                    String fieldName = ele.getName();
                    Field field = aClass.getDeclaredField(fieldName);
                    String getMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method method = aClass.getMethod(getMethodName, field.getType());
                    method.invoke(obj, convertObject(ele.getText(), field.getType().getSimpleName()));
                }
                resultList.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public List<Object> getTXTAsArray(String path, String className) {
        List<Object> resultList = new ArrayList<Object>();
        File file = new File(path);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String title = br.readLine();
            String titles[] = title.split("\\s+");
            String aLine = "";
            Class aClass = Class.forName(className);
            while ((aLine = br.readLine()) != null) {
                String datas[] = aLine.split("\\s+");
                Object obj = aClass.newInstance();
                for (int i = 0; i < titles.length; i++) {
                    String fieldName = titles[i];
                    Field field = aClass.getDeclaredField(fieldName);
                    String getMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method method = aClass.getMethod(getMethodName, field.getType());
                    method.invoke(obj, convertObject(datas[i], field.getType().getSimpleName()));
                }
                resultList.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultList;
    }

    @Override
    public List<Object> getXLSAsArray(String path, String className) {
        List<Object> resultList = new ArrayList<Object>();
        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(new File(path));
            Sheet sheet = wb.getSheet(0);
            Class aClass = Class.forName(className);
            for (int i = 1; i < sheet.getRows(); i++) {
                Object obj = aClass.newInstance();
                for (int j = 0; j < sheet.getColumns(); j++) {
                    String fieldName = sheet.getCell(j, 0).getContents();
                    Field field = aClass.getDeclaredField(fieldName);
                    String getMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method method = aClass.getMethod(getMethodName, field.getType());
                    method.invoke(obj, convertObject(sheet.getCell(j, i).getContents(), field.getType().getSimpleName()));
                }
                resultList.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (wb != null) {
                wb.close();
            }
        }
        return resultList;
    }

    @Override
    public void objectToXML(Object obj, String path) {
        Document document = DocumentHelper.createDocument();
        XMLWriter writer = null;
        try {
            Element root = document.addElement(obj.getClass().getSimpleName().substring(0, 1).toLowerCase() + obj.getClass().getSimpleName().substring(1) + "s");
            Element childElement = root.addElement(obj.getClass().getSimpleName().substring(0, 1).toLowerCase() + obj.getClass().getSimpleName().substring(1));
            Field[] field = obj.getClass().getDeclaredFields();

            for (int i = 0; i < field.length; i++) {
                field[i].setAccessible(true);
                String name = field[i].getName();
//               Method method = obj.getClass().getMethod("get"+name.substring(0,1).toUpperCase()+name.substring(1));
//               String keyValue =  String.valueOf(method.invoke(obj)) ;
                if (field[i].get(obj) != null && !isBaseDefaultValue(field[i].get(obj))) {
                    String keyValue = field[i].get(obj).toString();
                    Element key = childElement.addElement(name);
                    key.setText(keyValue);
                }
            }
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            File file = new File(path);
            writer = new XMLWriter(new FileOutputStream(file), format);
            writer.write(document);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public String objectToURL(Object obj, String baseURL) {   //对象转换为链接
        Field[] fields = obj.getClass().getDeclaredFields();
        StringBuilder url = new StringBuilder();
        if (url.indexOf("?")==-1){
        url.append(baseURL + "?");
        }
        try {
            String key, value;
            boolean flag = true;  //用于剔除最后一个&
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                /*判断所属对象是否为空或者默认0，如果是则剔除*/
                if (field.get(obj) != null && !isBaseDefaultValue(field.get(obj))) {
                    key = field.getName();
                    value = field.get(obj).toString();
                    String params = key + "=" + value;
                    if (flag) {
                        url.append(params);
                        flag = false;
                    } else {
                        url.append("&" + params);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url.toString();
    }

    @Override
    public void arrayToXML(List<Object> list, String path) {   //list转XML
        Document document = DocumentHelper.createDocument();
        XMLWriter writer = null;
        Object obj = null;
        try {
            Object obj1 = list.get(0).getClass().newInstance();
            if (list != null && list.size() != 0) {
                Element root = document.addElement(obj1.getClass().getSimpleName().substring(0, 1).toLowerCase() + obj1.getClass().getSimpleName().substring(1) + "s");
                for (int i = 0; i < list.size(); i++) {

                    obj = list.get(i);

                    Element childElement = root.addElement(obj.getClass().getSimpleName().substring(0, 1).toLowerCase() + obj.getClass().getSimpleName().substring(1));
                    Field[] field = obj.getClass().getDeclaredFields();


                    System.out.println(obj.toString());

                    for (int j = 0; j < field.length; j++) {
                        field[j].setAccessible(true);
                        String fieldName = field[j].getName();
                        /*判断所属对象是否为空或者默认0，如果是则剔除*/
                        if (field[j].get(obj) != null && !isBaseDefaultValue(field[j].get(obj))) {
                            String keyValue = field[j].get(obj).toString();
                            Element key = childElement.addElement(fieldName);
                            key.setText(keyValue);
                        }
                    }
                }
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding("UTF-8");
                File file = new File(path);
                writer = new XMLWriter(new FileOutputStream(file), format);
                writer.write(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void arrayToTXT(List<Object> list, String path) {   //list转TXT
        File file = new File(path);
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            if (list != null && list.size() != 0) {
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                String str = "";
                String keyValue = "";
                Object obj = list.get(0);
                Field[] fields = obj.getClass().getDeclaredFields();
                for (int j = 0; j < fields.length; j++) {
                    str += fields[j].getName() + "\t";
                }
                str += "\n";
                bw.write(str);
                for (int i = 0; i < list.size(); i++) {

                    Object obj1 = list.get(i);
                    for (int j = 0; j < fields.length; j++) {
                        fields[j].setAccessible(true);
                        keyValue += fields[j].get(obj1).toString() + "\t";
                    }
                    keyValue += "\n";
                }
                bw.write(keyValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void arrayToXLS(List<Object> list, String path) {   //List转XLS
        WritableWorkbook wwb = null;
        WritableSheet ws = null;
        try {
            if (list != null && list.size() != 0) {
                wwb = Workbook.createWorkbook(new File(path));
                ws = wwb.createSheet(list.get(0).getClass().getSimpleName(), 0);
                Object obj = list.get(0);
                Field[] fields = obj.getClass().getDeclaredFields();
                Label label = null;
                for (int j = 0; j < fields.length; j++) {
                    String fieldName = fields[j].getName();
                    label = new Label(j, 0, fieldName);
                    ws.addCell(label);
                }
                for (int i = 0; i < list.size(); i++) {
                    Object object = list.get(i);
                    for (int j = 0; j < fields.length; j++) {
                        fields[j].setAccessible(true);
                        String keyValue = fields[j].get(object).toString();
                        label = new Label(j, i + 1, keyValue);
                        ws.addCell(label);
                    }
                }
                wwb.write();
            }
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
           if (wwb!=null){
               try {
                   wwb.close();
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (WriteException e) {
                   e.printStackTrace();
               }
           }
        }
    }
}
