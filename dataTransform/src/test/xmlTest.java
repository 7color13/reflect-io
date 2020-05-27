package test;

import dao.IDataUtil;
import factory.DAOFactory;
import resources.Path;
import vo.Student;


public class xmlTest {
    public static void main(String[] args) throws Exception {
       // Student stu = new Student("liurui",18,"男",90);
        Student stu = new Student();
        stu.setGrade(80);
        stu.setName("hahah");
        IDataUtil dao = DAOFactory.getDataInstance();
        dao.objectToXML(stu, Path.normalPath+"ObjectToXmlStudent.xml");
    }
}
