package dao;


import java.util.List;

public interface IDataUtil {
        //将给定的任意xml文件中的数据转换为对象
        public Object getXMLAsObject(String path,String className);
        //将给定的任意url地址的参数数据转换为对象
        public Object getURLAsObject(String url,String className);
        //将给定的任意xml文件中的数据转换为集合
        public List<Object> getXMLAsArray(String path,String className);
        //将给定的任意txt文件中的数据转换为集合
        public List<Object> getTXTAsArray(String path,String className);
        //将给定的任意xls文件中的数据转换为集合
        public List<Object> getXLSAsArray(String path,String className);
        //将任意的对象写入到指定的XML文件中
        public void objectToXML(Object obj,String path);
        //将任意的对象转换成url地址参数:
        public String objectToURL(Object obj,String baseURL);
        //将任意的集合写入到指定的XML文件中
        public void arrayToXML(List<Object> list,String path);
        //将任意的集合写入到指定的txt文件中
        public void arrayToTXT(List<Object> list,String path);
        //将任意的集合写入到指定的xls文件中
        public void arrayToXLS(List<Object> list, String path);


}
