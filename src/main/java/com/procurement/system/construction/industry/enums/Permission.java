package com.procurement.system.construction.industry.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    PROCUREMENT_MANAGER_READ("procurement_manager:read"),
    PROCUREMENT_MANAGER_UPDATE("procurement_manager:update"),
    PROCUREMENT_MANAGER_CREATE("procurement_manager:create"),
    PROCUREMENT_MANAGER_DELETE("procurement_manager:delete"),

    SITE_MANAGER_READ("site_manager:read"),
    SITE_MANAGER_UPDATE("site_manager:update"),
    SITE_MANAGER_CREATE("site_manager:create"),
    SITE_MANAGER_DELETE("site_manager:delete"),

    SUPPLIER_READ("supplier:read"),
    SUPPLIER_UPDATE("supplier:update"),
    SUPPLIER_CREATE("supplier:create"),
    SUPPLIER_DELETE("supplier:delete"),

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete");

    @Getter
    private final String permission;
}
