import { useEffect, useState } from "react";
import { cn } from "@/lib/utils";
import Cookies from "js-cookie";
import { Layout, LayoutHeader } from "@/layouts/layout-definitions";
import { Outlet } from "react-router-dom";
import { useLoginUserStore } from "@/stores/login-user-store";
import { SidebarProvider } from "@/components/ui/sidebar";
import { AppSidebar } from "@/layouts/sidebar/app-sidebar";
import { AuthLayoutHeader } from "./auth-layout-header";
import { SseNotifier } from "./sse-notifier";
import { AccountService } from "@/services/account-service";

export default function AuthLayout() {
  const { loginUser, setLoginUser } = useLoginUserStore()
  const defaultOpen = Cookies.get("sidebar_state") !== "false"
  const [checkingAuth, setCheckingAuth] = useState(true)

  useEffect(() => {
    let active = true

    ;(async () => {
      if (!loginUser.isAuthenticated) {
        const currentUser = await AccountService.getCurrentUser()
        if (!active) return

        setLoginUser(currentUser)
      }

      if (active) {
        setCheckingAuth(false)
      }
    })()

    return () => {
      active = false
    }
  }, [loginUser.isAuthenticated, setLoginUser])

  if (checkingAuth) {
    return null
  }

  return (
    <div className="relative h-full overflow-hidden bg-background">
      <SidebarProvider defaultOpen={defaultOpen}>
        <SseNotifier />
        <AppSidebar />
        <main
          id="content"
          className={cn(
            "ms-auto w-full max-w-full",
            "peer-data-[state=collapsed]:w-[calc(100%-var(--sidebar-width-icon)-1rem)]",
            "peer-data-[state=expanded]:w-[calc(100%-var(--sidebar-width))]",
            "sm:transition-[width] sm:duration-200 sm:ease-linear",
            "group-data-[scroll-locked=1]/body:h-full",
            "has-[main.fixed-main]:group-data-[scroll-locked=1]/body:h-svh"
          )}
        >
          <Layout>
            <LayoutHeader>
              <AuthLayoutHeader />
            </LayoutHeader>
            {/* View Content */}
            <div className="lg:pe-2 pe-0">
              <Outlet />
            </div>
          </Layout>
        </main>
      </SidebarProvider>
    </div>
  )
}
