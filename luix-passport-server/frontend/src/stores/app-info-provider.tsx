import React, { createContext, useContext, useState } from "react"
import appInfoObj from "@/data/app-info.json"

export type AppInfo = {
  apiDocsEnabled: boolean
  ribbonProfile: string
  build: {
    artifact: string
    name: string
    time: string
    version: string
    group: string
  }
}

type AppInfoProviderProps = {
  children: React.ReactNode
  defaultValue?: AppInfo | null
}

type AppInfoProviderState = {
  appInfo: AppInfo
  setAppInfo: (appInfo: AppInfo) => void
}

const initialState: AppInfoProviderState = {
  appInfo: appInfoObj,
  setAppInfo: () => null,
}

const AppInfoProviderContext = createContext<AppInfoProviderState>(initialState)

export function AppInfoProvider({
  children,
  defaultValue,
  ...props
}: AppInfoProviderProps) {
  const [appInfo, setAppInfo] = useState<AppInfo>(defaultValue || initialState.appInfo)

  const value = {
    appInfo,
    setAppInfo
  }

  return (
    <AppInfoProviderContext.Provider {...props} value={value}>
      {children}
    </AppInfoProviderContext.Provider>
  )
}

// eslint-disable-next-line react-hooks/rules-of-hooks
export const useAppInfo = () => {
  const context = useContext(AppInfoProviderContext)

  if (context === undefined) {
    throw new Error("useAppInfo must be used within a AppInfoProvider")
  }

  return context
}
