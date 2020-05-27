package factory;

import dao.IDataUtil;
import dao.impl.DataUtilImpl;

public class DAOFactory {
    public static IDataUtil getDataInstance(){
        return new DataUtilImpl();
    }
}
