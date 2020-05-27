package test;

import dao.IDataUtil;
import factory.DAOFactory;
import resources.Path;
import vo.Student;

import java.util.ArrayList;
import java.util.List;

public class ArrayToXls {
    public static void main(String[] args) {
        List<Object> studentList = new ArrayList<>();
        Student s1 = new Student("sd", 23, "女", 90);
        studentList.add(s1);
        Student s2 = new Student("刘锐",18,"男",95);
        studentList.add(s2);
        Student s3 = new Student("taylor", 30, "女", 100);
        studentList.add(s3);
        IDataUtil dao = DAOFactory.getDataInstance();
        dao.arrayToXLS(studentList, Path.normalPath+"ArrayToXls.xls");
    }
}
