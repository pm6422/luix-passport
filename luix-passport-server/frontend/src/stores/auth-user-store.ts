import { Exome } from "exome"
import { DateTimeFormat } from "@/data/date-time-formats.tsx"
import { isEmpty } from "lodash"

export type AuthUser = {
    id: string
    username: string
    email: string
    mobileNo: string
    firstName: string
    lastName: string
    locale: string
    timeZone: string
    dateTimeFormat: string
    dateTimeFormatInstance: DateTimeFormat
    activated: boolean
    enabled: boolean
    roles: Array<string>

    isAdmin: boolean
    isDeveloper: boolean
    isOnlyUser: boolean
}

class AuthUserStore extends Exome {
    public authUser = {} as AuthUser

    public setAuthUser(authUser: AuthUser) {
        this.authUser = authUser
        if (!isEmpty(authUser)) {
            this.authUser.isAdmin = authUser.roles && authUser.roles.includes("ROLE_ADMIN")
            this.authUser.isDeveloper = authUser.roles && authUser.roles.includes("ROLE_DEVELOPER")
            this.authUser.isOnlyUser = authUser.roles && authUser.roles.length === 2
                && authUser.roles.includes("ROLE_USER") && authUser.roles.includes("ROLE_ANONYMOUS")
        }
    }
}

export const authUserStore = new AuthUserStore()