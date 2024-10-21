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
    public apiDocsEnabled = false
    public ribbonProfile = ""
    public build = {
        artifact: "",
        name: "",
        time: "",
        version: "",
        group: ""
    }

    public setAppInfo(appInfo: AppInfo) {
        this.apiDocsEnabled = appInfo.apiDocsEnabled
        this.ribbonProfile = appInfo.ribbonProfile
        this.build = appInfo.build
    }
}

export const appInfoStore = new AppInfoStore()