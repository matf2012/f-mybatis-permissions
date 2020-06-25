package com.f.permissions.mybatis;

import com.neusoft.permissions.entity.PermissionsWhereDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matf on 2020-06-07.
 */
class ValuesHandler implements IValuesHandler{


    @Override
    public List<PermissionsWhereDTO> values(String[] permissionsValue) {
        List<PermissionsWhereDTO> list = new ArrayList<>();

        return list;
    }


    @Override
    public String permissionsValue() {

        return null;
    }
}
