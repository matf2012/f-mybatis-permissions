package com.f.permissions.mybatis;


import com.neusoft.permissions.entity.PermissionsWhereDTO;

import java.util.List;

/**
 * Created by matf on 2020-06-07.
 */
public interface IValuesHandler {

    List<PermissionsWhereDTO> values(String[] permissionsValue);

    String permissionsValue();

}
