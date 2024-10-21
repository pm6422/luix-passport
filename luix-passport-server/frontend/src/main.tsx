// import React from "react"
import { AuthUserProvider } from "./stores/auth-user-provider"
import { ThemeProvider } from "@/stores/theme-provider"
import { RouterProvider } from "react-router-dom"
import { Toaster } from "@/components/custom/toaster"
import dayjs from "dayjs"
import utc from "dayjs/plugin/utc"
import timezone from "dayjs/plugin/timezone"
import type { AppInfo } from "@/stores/app-info-store.ts"
import { appInfoStore } from "@/stores/app-info-store.ts"
import type { AuthUser } from "@/stores/auth-user-provider"
import router from "@/router"
import "@/main.css"
import "@/assets/sass/intro.scss"
import { useStore } from "exome/react"

dayjs.extend(utc)
dayjs.extend(timezone)

type Props = {
    appInfo: AppInfo
    authUser: AuthUser
}

export default function Main({ appInfo, authUser }: Props) {
    const { setAppInfo } = useStore(appInfoStore)
    setAppInfo(appInfo)

    return <AuthUserProvider defaultValue={authUser}>
            <ThemeProvider>
                <RouterProvider router={router}/>
                <Toaster/>
            </ThemeProvider>
        </AuthUserProvider>;

    // return <React.StrictMode>
    //         <AppInfoProvider defaultValue={appInfo}>
    //         <AuthUserProvider defaultValue={authUser}>
    //             <ThemeProvider>
    //                 <RouterProvider router={router}/>
    //                 <Toaster/>
    //             </ThemeProvider>
    //         </AuthUserProvider>
    //     </AppInfoProvider>
    // </React.StrictMode>;
}