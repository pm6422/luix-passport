import { Exome } from "exome"
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

class LoginUserStore extends Exome {
    public loginUser = {} as LoginUser

    public setLoginUser(loginUser: LoginUser) {
        this.loginUser = loginUser
        if (!isEmpty(loginUser)) {
            this.loginUser.isAuthenticated = true
        }
    }

    public hasRole(roleId: string): boolean {
        const roleIds = this.loginUser?.roleIds
        return Array.isArray(roleIds) && roleIds.includes(roleId)
    }

    public hasRoles(roleIds: string[], matchAll = false): boolean {
        const userRoleIds = this.loginUser?.roleIds
        if (!Array.isArray(userRoleIds) || !Array.isArray(roleIds) || roleIds.length === 0) {
            return false
        }
        return matchAll
            ? roleIds.every(id => userRoleIds.includes(id))
            : roleIds.some(id => userRoleIds.includes(id))
    }
}

export const loginUserStore = new LoginUserStore()
