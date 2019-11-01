package com.demo.camera2.permission;

/**
 * Enum class to handle the different states
 * of permissions since the PackageManager only
 * has a granted and denied state.
 */
enum Permissions {
    GRANTED,//同意
    DENIED, //拒绝
    NOT_FOUND//未找到
}
