// import React from "react"
import { ThemeProvider } from "@/stores/theme-provider"
import { RouterProvider } from "react-router-dom"
import { Toaster } from "@/components/custom/toaster"
import dayjs from "dayjs"
import utc from "dayjs/plugin/utc"
import timezone from "dayjs/plugin/timezone"
import type { AppInfo } from "@/stores/app-info-store.ts"
import { useStore } from "exome/react"
import { appInfoStore } from "@/stores/app-info-store.ts"
import router from "@/router"
import "@/main.css"
import "@/assets/sass/intro.scss"

dayjs.extend(utc)
dayjs.extend(timezone)

type Props = {
    appInfo: AppInfo
}

export default function Main({ appInfo }: Props) {
    const { setAppInfo } = useStore(appInfoStore)
    setAppInfo(appInfo)

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