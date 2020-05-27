package test;

import dao.IDataUtil;
import dao.impl.DataUtilImpl;
import factory.DAOFactory;
import resources.Path;
import vo.Student;

import java.util.List;

public class TxtToArray {
    public static void main(String[] args) {
        IDataUtil dao = DAOFactory.getDataInstance();
        List<Object> list1 = dao.getXLSAsArray(Path.normalPath + "ArrayToXls.xls", "vo.Student");
        for (Object object:list1){
            Student stu = (Student)object;
            System.out.println(stu.toString()   );
        }
    }
}
