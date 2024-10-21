import { Exome } from "exome"
import { DateTimeFormat } from "@/data/date-time-formats.tsx"

export type AuthUser = {
    id: string
    username: string
    email: string
    firstName: string
    lastName: string
    locale: string
    timeZone: string
    dateTimeFormat: string
    dateTimeFormatInstance: DateTimeFormat
    activated: boolean
    enabled: boolean
    roles: Array<string>
}

class AuthUserStore extends Exome {
    public id = ""
    public username = ""
    public email = ""
    public firstName = ""
    public lastName = ""
    public locale = "en_US"
    public timeZone = "Asia/Shanghai"
    public dateTimeFormat = ""
    public dateTimeFormatInstance = {}
    public activated = false
    public enabled = false
    public roles: Array<string> = []


    public setAuthUser(authUser: AuthUser) {
        this.id = authUser.id
        this.username = authUser.username
        this.email = authUser.email
        this.firstName = authUser.firstName
        this.lastName = authUser.lastName
        this.locale = authUser.locale
        this.timeZone = authUser.timeZone
        this.dateTimeFormat = authUser.dateTimeFormat
        this.dateTimeFormatInstance = authUser.dateTimeFormatInstance
        this.activated = authUser.activated
        this.enabled = authUser.enabled
        this.roles = authUser.roles
    }

    public getAuthUser(): AuthUser {
        return {
            id: this.id,
            username: this.username,
            email: this.email,
            firstName: this.firstName,
            lastName: this.lastName,
            locale: this.locale,
            timeZone: this.timeZone,
            dateTimeFormat: this.dateTimeFormat,
            dateTimeFormatInstance: this.dateTimeFormatInstance,
            activated: this.activated,
            enabled: this.enabled,
            roles: this.roles
        }
    }

    public isAdmin(): boolean {
        return this.roles && this.roles.includes("ROLE_ADMIN")
    }

    public isDeveloper(): boolean{
        return this.roles && this.roles.includes("ROLE_DEVELOPER")
    }

    public isOnlyUser(): boolean {
        return this.roles && this.roles.length === 2 && this.roles.includes("ROLE_USER") && this.roles.includes("ROLE_ANONYMOUS")
    }
}

export const authUserStore = new AuthUserStore()