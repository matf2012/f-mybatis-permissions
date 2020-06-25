package com.f.permissions.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限控制注解
 * 标注在方法上。MYBATIS 插件会通过此注解判断是否需要动态修改SQL
 * Created by matf on 2019-09-30.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermissions {
    /**
     * 权限控制信息
     * [{table_name:column_name[:role_type]}]
     * role_type 与 列名相同时，可以不写
     * 例如：
     * @DataPermissions(
     *  {
     *      'bcs_record_info:insre_type_code:insur_type',
     *      'bcs_record_info:org_code'
     *   }
     * )
     * @return
     */
    String[] value() ;
}
