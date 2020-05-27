package test;

import dao.IDataUtil;
import factory.DAOFactory;
import resources.Path;
import vo.Student;

public class ObjectToUrl {
    public static void main(String[] args) {
        Student stu = new Student();
        stu.setGrade(80);
        stu.setName("hahah");
        IDataUtil dao = DAOFactory.getDataInstance();
        String url=dao.objectToURL(stu,"www.liurui13.cn");
        System.out.println(url);
    }
}
