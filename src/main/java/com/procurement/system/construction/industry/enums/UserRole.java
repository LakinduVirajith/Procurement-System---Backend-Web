package com.procurement.system.construction.industry.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum UserRole {

    PROCUREMENT_MANAGER(
            Set.of(
                    Permission.PROCUREMENT_MANAGER_READ,
                    Permission.PROCUREMENT_MANAGER_UPDATE,
                    Permission.PROCUREMENT_MANAGER_CREATE,
                    Permission.PROCUREMENT_MANAGER_DELETE
            )
    ),
    SITE_MANAGER(
            Set.of(
                    Permission.SITE_MANAGER_READ,
                    Permission.SITE_MANAGER_UPDATE,
                    Permission.SITE_MANAGER_CREATE,
                    Permission.SITE_MANAGER_DELETE
            )
    ),

    SUPPLIER(
            Set.of(
                    Permission.SUPPLIER_READ,
                    Permission.SUPPLIER_UPDATE,
                    Permission.SUPPLIER_CREATE,
                    Permission.SUPPLIER_DELETE
            )
    ),
    ADMIN(
            Set.of(
                    Permission.ADMIN_READ,
                    Permission.ADMIN_UPDATE,
                    Permission.ADMIN_CREATE,
                    Permission.ADMIN_DELETE,

                    Permission.PROCUREMENT_MANAGER_READ,
                    Permission.PROCUREMENT_MANAGER_UPDATE,
                    Permission.PROCUREMENT_MANAGER_CREATE,
                    Permission.PROCUREMENT_MANAGER_DELETE,

                    Permission.SITE_MANAGER_READ,
                    Permission.SITE_MANAGER_UPDATE,
                    Permission.SITE_MANAGER_CREATE,
                    Permission.SITE_MANAGER_DELETE,

                    Permission.SUPPLIER_READ,
                    Permission.SUPPLIER_UPDATE,
                    Permission.SUPPLIER_CREATE,
                    Permission.SUPPLIER_DELETE
            )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities(){
        var authorities = getPermissions().stream().map(
                permission -> new SimpleGrantedAuthority(permission.getPermission())
        ).collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
