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
    isAdmin: boolean
    isDeveloper: boolean
    isOnlyUser: boolean
}

class LoginUserStore extends Exome {
    public loginUser = {} as LoginUser

    public setLoginUser(loginUser: LoginUser) {
        this.loginUser = loginUser
        if (!isEmpty(loginUser)) {
            this.loginUser.isAuthenticated = true
            this.loginUser.isAdmin = loginUser.roleIds && loginUser.roleIds.includes("ROLE_ADMIN")
            this.loginUser.isDeveloper = loginUser.roleIds && loginUser.roleIds.includes("ROLE_DEVELOPER")
            this.loginUser.isOnlyUser = loginUser.roleIds && loginUser.roleIds.length === 2
                && loginUser.roleIds.includes("ROLE_USER") && loginUser.roleIds.includes("ROLE_ANONYMOUS")
        }
    }
}

export const loginUserStore = new LoginUserStore()