package com.f.permissions.mybatis;

import com.neusoft.permissions.IPermissions;
import com.neusoft.permissions.core.FPermissionsCore;
import com.neusoft.permissions.entity.PermissionsDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;

/**
 * Created by matf on 2020-06-07.
 */
@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class,Integer.class})})
@Slf4j
public class MyBatisPermissionsIntercetor  implements Interceptor {

    private IPermissions permissions = new FPermissionsCore();

    @Autowired
    private IValuesHandler valuesHandler = new ValuesHandler();

    public MyBatisPermissionsIntercetor(){

    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
            //先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，其实现类是BaseStatementHandler，然后就到BaseStatementHandler的成员变量mappedStatement
            MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
            //id为执行的mapper方法的全路径名，如com.uv.dao.UserMapper.insertUser
            String id = mappedStatement.getId();
            //sql语句类型 select、delete、insert、update
            String sqlCommandType = mappedStatement.getSqlCommandType().toString();
            BoundSql boundSql = statementHandler.getBoundSql();

            //获取到原始sql语句
            String sql = boundSql.getSql();
            String[] permissionsValue = null;

            //注解逻辑判断  添加注解了才拦截
            Class<?> classType = Class.forName(mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf(".")));
            String mName = mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".") + 1, mappedStatement.getId().length());
            for (Method method : classType.getDeclaredMethods()) {
                if (method.isAnnotationPresent(DataPermissions.class) && (mName.equals(method.getName()) || mName.equals(method.getName()+"_COUNT") )) {
                    DataPermissions permissions = method.getAnnotation(DataPermissions.class);
                    permissionsValue = permissions.value();
                }
            }

            if(permissionsValue != null && permissionsValue.length > 0){

                PermissionsDTO dto = new PermissionsDTO();
                dto.setSql(sql);
                dto.setValues(valuesHandler.values(permissionsValue));

                permissions.handle(dto);
                if(dto.isHandleFlag()){
                    Field field = boundSql.getClass().getDeclaredField("sql");
                    field.setAccessible(true);
                    field.set(boundSql, dto.getSql());
                }
            }
        } catch (Exception e) {
            log.debug("mybatis permissions error ! {}",e.getMessage());
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
//        if (target instanceof StatementHandler) {
//            return Plugin.wrap(target, this);
//        } else {
//            return target;
//        }
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
