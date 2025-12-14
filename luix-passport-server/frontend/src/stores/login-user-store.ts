import { create } from "zustand"
import { isEmpty } from "lodash"

export type LoginUser = {
    username: string
    email: string
    mobileNo: string
    firstName: string
    lastName: string
    locale: string
    timeZoneId: string
    dateTimeFormatId: string
    dateTimeFormat: string
    dateFormat: string
    timeFormat: string
    activated: boolean
    enabled: boolean
    roleIds: Array<string>
    permissionIds: Array<string>
    isAuthenticated: boolean
}

interface LoginUserState {
    loginUser: LoginUser
    setLoginUser: (loginUser: LoginUser) => void
    hasRole: (roleId: string) => boolean
    hasRoles: (roleIds: string[], matchAll?: boolean) => boolean
}

export const useLoginUserStore = create<LoginUserState>((set, get) => ({
    loginUser: {} as LoginUser,

    setLoginUser: (loginUser: LoginUser) => {
        if (!isEmpty(loginUser)) {
            loginUser.isAuthenticated = true
        }
        set({ loginUser })
    },

    hasRole: (roleId: string): boolean => {
        const roleIds = get().loginUser?.roleIds
        return Array.isArray(roleIds) && roleIds.includes(roleId)
    },

    hasRoles: (roleIds: string[], matchAll = false): boolean => {
        const userRoleIds = get().loginUser?.roleIds
        if (!Array.isArray(userRoleIds) || !Array.isArray(roleIds) || roleIds.length === 0) {
            return false
        }
        return matchAll
            ? roleIds.every(id => userRoleIds.includes(id))
            : roleIds.some(id => userRoleIds.includes(id))
    }
}))
