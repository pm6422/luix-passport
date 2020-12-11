package org.infinity.passport.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.infinity.passport.dto.AdminMenuDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MenuTreeNode extends AdminMenuDTO implements Serializable {

    private static final long               serialVersionUID = 1L;
    private final        List<MenuTreeNode> children         = new ArrayList<>();

    public MenuTreeNode() {
        super();
    }

    public MenuTreeNode addChild(MenuTreeNode n) {
        children.add(n);
        return this;
    }
}
