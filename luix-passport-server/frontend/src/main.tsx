// import React from "react"
import { ThemeProvider } from "@/stores/theme-provider"
import { RouterProvider } from "react-router-dom"
import { Toaster } from "@/components/custom/toaster"
import dayjs from "dayjs"
import utc from "dayjs/plugin/utc"
import timezone from "dayjs/plugin/timezone"
import type { AppInfo } from "@/stores/app-info-store"
import type { LoginUser } from "@/stores/login-user-store"
import { appInfoStore } from "@/stores/app-info-store"
import { loginUserStore } from "@/stores/login-user-store"
import { useStore } from "exome/react"
import router from "@/router"
import "@/main.css"
// import "@/assets/sass/intro.scss"

dayjs.extend(utc)
dayjs.extend(timezone)

type Props = {
    appInfo: AppInfo,
    loginUser: LoginUser
}

export default function Main({ appInfo, loginUser }: Props) {
    const { setAppInfo } = useStore(appInfoStore)
    setAppInfo(appInfo)

    const { setLoginUser } = useStore(loginUserStore)
    setLoginUser(loginUser)

    return <ThemeProvider>
                <RouterProvider router={router}/>
                <Toaster/>
            </ThemeProvider>;

    // return <React.StrictMode>
    // <ThemeProvider>
    //     <RouterProvider router={router}/>
    //     <Toaster/>
    // </ThemeProvider>
    // </React.StrictMode>;
}