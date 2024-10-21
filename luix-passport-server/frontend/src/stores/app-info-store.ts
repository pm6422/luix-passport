import { Exome } from "exome"

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

class AppInfoStore extends Exome {
    public appInfo = {} as AppInfo

    public setAppInfo(appInfo: AppInfo) {
        this.appInfo = appInfo
    }
}

export const appInfoStore = new AppInfoStore()