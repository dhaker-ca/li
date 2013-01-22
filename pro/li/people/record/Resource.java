package li.people.record;

import java.util.ArrayList;
import java.util.List;

import li.annotation.Bean;
import li.annotation.Table;
import li.dao.Page;
import li.dao.Record;
import li.people.Const;

@Bean
@Table("t_resource")
public class Resource extends Record<Resource> implements Const {
    private static final long serialVersionUID = 7989017780707672816L;

    public List<Resource> listByRoleId(Page page, Integer roleId) {
        String sql = "WHERE id IN (SELECT resource_id from r_role_resource WHERE role_id=?) ORDER BY description ASC";
        return list(page, sql, roleId);
    }

    public List<Resource> listNotHaveByRoleId(Page page, Integer roleId) {
        String sql = "WHERE id NOT IN (SELECT resource_id from r_role_resource WHERE role_id=?) ORDER BY description ASC";
        return list(page, sql, roleId);
    }

    public List<String> listNameByRoleId(Page page, Integer roleId) {
        List<String> list = new ArrayList<String>();
        List<Resource> resources = listByRoleId(page, roleId);
        for (Resource resource : resources) {
            list.add(resource.get(String.class, "name"));
        }
        return list;
    }

    public List<Resource> list(Page page) {
        return super.list(page, "ORDER BY description ASC");
    }
}