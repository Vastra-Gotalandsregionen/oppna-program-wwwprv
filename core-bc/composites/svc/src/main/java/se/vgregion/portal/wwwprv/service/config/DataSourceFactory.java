package se.vgregion.portal.wwwprv.service.config;

import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import org.springframework.beans.factory.FactoryBean;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataSourceFactory implements FactoryBean<Object> {

    private String jndiName;

    @Override
    public Object getObject() throws Exception {
        Thread thread = Thread.currentThread();

        // Get the thread's class loader. You'll reinstate it after using
        // the data source you look up using JNDI

        ClassLoader origLoader = thread.getContextClassLoader();

        // Set Liferay's class loader on the thread

        thread.setContextClassLoader(PortalClassLoaderUtil.getClassLoader());

        try {

            // Look up the data source and connect to it

            InitialContext ctx = new InitialContext();
            DataSource datasource = (DataSource)
                    ctx.lookup(jndiName);

            return datasource;
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        } finally {
            // Switch back to the original context class loader

            thread.setContextClassLoader(origLoader);
        }
    }

    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }
}
