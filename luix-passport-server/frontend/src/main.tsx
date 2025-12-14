import { ThemeProvider } from "@/stores/theme-provider"
import { RouterProvider } from "react-router-dom"
import { Toaster } from "@/components/custom/toaster"
import router from "@/router"
import "@/main.css"

export default function Main() {
    return <ThemeProvider>
        <RouterProvider router={router} future={{ v7_startTransition: true }} />
        <Toaster />
    </ThemeProvider>;
}