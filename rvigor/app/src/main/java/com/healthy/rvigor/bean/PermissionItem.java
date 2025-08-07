package com.healthy.rvigor.bean;

/**
 * @Description: Rvigor
 * @Author: wb
 * @CreateDate: 2024/5/7 16:31
 * @UpdateRemark:
 */
public class PermissionItem {

    /**
     * @param name        权限名称
     * @param description 权限描述
     */
    public PermissionItem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * 权限名称
     */
    private String name = "";

    /**
     * 获取  权限名称
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 设置  权限名称
     *
     * @param value
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * 权限描述
     */
    private String description = "";

    /**
     * 获取  权限描述
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置  权限描述
     *
     * @param value
     */
    public void setDescription(String value) {
        this.description = value;
    }
}
