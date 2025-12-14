import { create } from "zustand"

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

interface AppInfoState {
    appInfo: AppInfo
    setAppInfo: (appInfo: AppInfo) => void
}

export const useAppInfoStore = create<AppInfoState>((set) => ({
    appInfo: {} as AppInfo,
    setAppInfo: (appInfo) => set({ appInfo }),
}))