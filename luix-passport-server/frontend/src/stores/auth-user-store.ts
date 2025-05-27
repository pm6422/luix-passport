import { Exome } from "exome"
import { isEmpty } from "lodash"

export type AuthUser = {
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

    isAdmin: boolean
    isDeveloper: boolean
    isOnlyUser: boolean
}

class AuthUserStore extends Exome {
    public authUser = {} as AuthUser

    public setAuthUser(authUser: AuthUser) {
        this.authUser = authUser
        if (!isEmpty(authUser)) {
            this.authUser.isAdmin = authUser.roleIds && authUser.roleIds.includes("ROLE_ADMIN")
            this.authUser.isDeveloper = authUser.roleIds && authUser.roleIds.includes("ROLE_DEVELOPER")
            this.authUser.isOnlyUser = authUser.roleIds && authUser.roleIds.length === 2
                && authUser.roleIds.includes("ROLE_USER") && authUser.roleIds.includes("ROLE_ANONYMOUS")
        }
    }
}

export const authUserStore = new AuthUserStore()