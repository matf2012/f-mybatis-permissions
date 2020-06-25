package com.f.permissions.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

/**
 * Created by matf on 2020-06-11.
 */
@Configuration
@ConditionalOnClass({SqlSessionFactory.class,MyBatisPermissionsIntercetor.class,MybatisParamIntercetor.class})
@AutoConfigureAfter({MybatisAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class MyBatisPermissionsAutoConfiguration {

        @Autowired
        private List<SqlSessionFactory> sqlSessionFactoryList;
        @Autowired
        private MyBatisPermissionsIntercetor interceptor;
        @Autowired
        private MybatisParamIntercetor paramIntercetor;

        public MyBatisPermissionsAutoConfiguration() {

        }

        @PostConstruct
        public void addPageInterceptor() {

            Iterator var3 = this.sqlSessionFactoryList.iterator();

            while(var3.hasNext()) {
                SqlSessionFactory sqlSessionFactory = (SqlSessionFactory)var3.next();
                sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
                sqlSessionFactory.getConfiguration().addInterceptor(paramIntercetor);
            }

        }

}
