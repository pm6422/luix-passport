import { ThemeProvider } from "@/stores/theme-provider"
import { RouterProvider } from "react-router-dom"
import { Toaster } from "@/components/custom/toaster"
import dayjs from "dayjs"
import utc from "dayjs/plugin/utc"
import timezone from "dayjs/plugin/timezone"
import router from "@/router"
import "@/main.css"

dayjs.extend(utc)
dayjs.extend(timezone)

export default function Main() {
    return <ThemeProvider>
                <RouterProvider router={router} future={{ v7_startTransition: true }} />
                <Toaster />
            </ThemeProvider>;
}