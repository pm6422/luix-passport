import React, { createContext, useContext, useState, useEffect } from "react"
import { DateTimeFormat } from "@/data/date-time-formats"
import { isEmpty } from "lodash"

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

type AuthUserProviderProps = {
  children: React.ReactNode
  defaultValue?: AuthUser
}

type AuthUserProviderState = {
  authUser: AuthUser
  clearAuthUser: () => void
  isAdmin: () => boolean
  isDeveloper: () => boolean
  isOnlyUser: () => boolean
}

const initialState: AuthUserProviderState = {
  authUser: {} as AuthUser,
  clearAuthUser: () => {},
  isAdmin: () => false,
  isDeveloper: () => false,
  isOnlyUser: () => false
}

const AuthUserProviderContext = createContext<AuthUserProviderState>(initialState)

export function AuthUserProvider({
  children,
  defaultValue = {} as AuthUser,
  ...props
}: AuthUserProviderProps) {
  const [authUser, setAuthUser] = useState<AuthUser>(defaultValue)

  // watch auth user whether is null
  useEffect(() => {
    if(isEmpty(authUser)) {
      // user is null
    }
  }, [authUser])

  const value = {
    authUser,
    clearAuthUser: () => setAuthUser({} as AuthUser),
    isAdmin: () => {
      return authUser.roles && authUser.roles.includes("ROLE_ADMIN")
    },
    isDeveloper: () => {
      return authUser.roles && authUser.roles.includes("ROLE_DEVELOPER")
    },
    isOnlyUser: () => {
      return authUser.roles && authUser.roles.length ===2 && authUser.roles.includes("ROLE_USER") && authUser.roles.includes("ROLE_ANONYMOUS")
    }
  }

  return (
    <AuthUserProviderContext.Provider {...props} value={value}>
      {children}
    </AuthUserProviderContext.Provider>
  )
}

export const useAuthUser = () => {
  const context = useContext(AuthUserProviderContext)

  if (context === undefined) {
    throw new Error("useAuthUser must be used within a AuthUserProvider")
  }

  return context
}