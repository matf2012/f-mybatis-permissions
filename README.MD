# mybatis 简单数据权限插件
  一个简单的mybatis插件，通过编辑sql的方式，动态添加查询条件，和新增条件。减少重复编写相同处理。
  依赖：https://github.com/matf2012/f-permissions
## 样例参考
https://github.com/matf2012/f-mybatis-permissions-demo

## 使用样例

### 1. 实现接口 IValuesHandler
	int val = 1;
	@Override
    public List<PermissionsWhereDTO> values(String[] permissionsValue) {
        List<PermissionsWhereDTO> list = new ArrayList<>();

        List<String> orgValues = new ArrayList<>();
		// TODO 实现自己的逻辑
        orgValues.add("1");
        if((val++)%2==0){
            orgValues.add("2");
        }
        for(String p :  permissionsValue){
            WhereDTO where = convertPermissionValue(p);
            if(PermissionsConstant.ORG_LIST.equals(where.getType()) && orgValues.size() > 0){
                where.setValues(orgValues);
                if(orgValues.size() == 1){
                    where.setOperator(PermissionsOperator.STARTWITH);
                }else{
                    where.setOperator(PermissionsOperator.IN);
                }
                list.add(where);
            }
        }
        return list;
    }

    @Override
    public String permissionsValue() {
		// 自动添加的值 需要配置属性 mybatis.permissions.column:   例如：org_code
        return "test permissions value";
    }

    protected WhereDTO convertPermissionValue(String permission){
        WhereDTO dto = new WhereDTO();
        if(permission != null){
            dto.setType(PermissionsConstant.ORG_LIST);
            String[] pArray = permission.split(":");
            if(pArray.length > 1){
                dto.setColumn(pArray[1]);
            }
            if(pArray.length > 0){
                dto.setTableName(pArray[0]);
            }
            if(pArray.length > 2){
                // 只有一个 只取默认值 机构 PermissionsType.ORG_CODE
            }
        }
        return dto;
    }
### 2. 标记要处理的mybatis方法 
    @DataPermissions("f_customer:org_code:org_start")
    List<FCustomer> listCust();

    List<FCustomer> listAllCust();
	
	
  <select id="listCust"  resultType="com.f.demo.common.model.entity.FCustomer">

    select
    *
    from f_customer t
  </select>

  <select id="listAllCust"   resultType="com.f.demo.common.model.entity.FCustomer">

    select
    *
    from f_customer t
  </select>
  
	sql输出：
	listCust方法
	SELECT * FROM f_customer t WHERE t.org_code LIKE '1%' 
	listCust方法
	SELECT * FROM f_customer t WHERE t.org_code IN ('1', '2') 
	listAllCust方法
	select * from f_customer t 
==>  Preparing: SELECT * FROM f_customer t WHERE t.org_code LIKE '1%' 
==> Parameters: 
<==    Columns: id, cust_name, cust_type, cust_level, org_code, create_id, create_date
<==        Row: 1, 张三, 1, 1, 1, 1, 2020-06-25 22:08:59
<==        Row: 2, 李四, 1, 2, 1, 1, 2020-06-25 22:09:30
<==      Total: 2

==>  Preparing: SELECT * FROM f_customer t WHERE t.org_code IN ('1', '2') 
==> Parameters: 
<==    Columns: id, cust_name, cust_type, cust_level, org_code, create_id, create_date
<==        Row: 1, 张三, 1, 1, 1, 1, 2020-06-25 22:08:59
<==        Row: 2, 李四, 1, 2, 1, 1, 2020-06-25 22:09:30
<==        Row: 3, 张飞, 2, 1, 2, 2, 2020-06-25 22:09:44
<==        Row: 4, 刘备, 2, 2, 2, 2, 2020-06-25 22:10:04
<==      Total: 4

==>  Preparing: select * from f_customer t 
==> Parameters: 
<==    Columns: id, cust_name, cust_type, cust_level, org_code, create_id, create_date
<==        Row: 1, 张三, 1, 1, 1, 1, 2020-06-25 22:08:59
<==        Row: 2, 李四, 1, 2, 1, 1, 2020-06-25 22:09:30
<==        Row: 3, 张飞, 2, 1, 2, 2, 2020-06-25 22:09:44
<==        Row: 4, 刘备, 2, 2, 2, 2, 2020-06-25 22:10:04
<==      Total: 4
