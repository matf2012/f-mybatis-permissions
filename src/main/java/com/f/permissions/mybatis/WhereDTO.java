package com.f.permissions.mybatis;

import com.neusoft.permissions.entity.PermissionsWhereDTO;

/**
 * Created by matf on 2020-06-12.
 */
public class WhereDTO extends PermissionsWhereDTO{
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
