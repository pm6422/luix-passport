import { create } from "zustand"
import { isEmpty } from "lodash"
import { createJSONStorage, persist } from "zustand/middleware"

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
    membershipType?: string
    membershipExpiresAt?: string
}

interface LoginUserState {
    loginUser: LoginUser
    setLoginUser: (loginUser: LoginUser) => void
    hasRole: (roleId: string) => boolean
    hasRoles: (roleIds: string[], matchAll?: boolean) => boolean
}

export const useLoginUserStore = create<LoginUserState>()(
    persist(
        (set, get) => ({
            loginUser: {} as LoginUser,

            setLoginUser: (loginUser: LoginUser) => {
                const authenticated = !isEmpty(loginUser)
                set({
                    loginUser: { ...loginUser, isAuthenticated: authenticated } as LoginUser,
                })
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
        }),
        {
            name: "login-user",
            storage: createJSONStorage(() => sessionStorage),
            partialize: (state) => ({
                loginUser: state.loginUser,
            }),
        }
    )
)
