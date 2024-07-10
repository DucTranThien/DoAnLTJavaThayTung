package com.hutech.tranthienducpro;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ADMIN("ADMIN", 1), // Vai trò quản trị viên, có quyền quản lý người dùng trong hệ thống.
    USER("USER", 2), // Vai trò người dùng bình thường, có quyền hạn giới hạn.
    MASTER("MASTER", 3); // Vai trò Master, có quyền quản lý các admin và user.

    private final String name;
    private final long value;
}
