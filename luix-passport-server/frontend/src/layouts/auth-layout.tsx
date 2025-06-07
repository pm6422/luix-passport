import { useEffect } from "react";
import { cn } from "@/lib/utils";
import Cookies from "js-cookie";
import { Layout, LayoutHeader } from "@/layouts/layout-definitions";
import { useStore } from "exome/react";
import { Outlet } from "react-router-dom";
import { useLocation } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { loginUserStore } from "@/stores/login-user-store";
import { SidebarProvider } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/sidebar/app-sidebar";
import { AuthLayoutHeader } from "./auth-layout-header";
import { SseNotification } from "@/components/sse-notification";

export default function AuthLayout() {
  const navigate = useNavigate()
  const { loginUser } = useStore(loginUserStore)
  const defaultOpen = Cookies.get("sidebar_state") !== "false"
  const location = useLocation()

  useEffect(() => {
    if(!loginUser.isAuthenticated) {
      navigate("/sign-in")
    }
  }, [location, loginUser]);

  return (
    <div className="relative h-full overflow-hidden bg-background">
      <SidebarProvider defaultOpen={defaultOpen}>
        <SseNotification />
        <AppSidebar />
        <main
          id="content"
          className={cn(
            "ml-auto w-full max-w-full",
            "peer-data-[state=collapsed]:w-[calc(100%-var(--sidebar-width-icon)-1rem)]",
            "peer-data-[state=expanded]:w-[calc(100%-var(--sidebar-width))]",
            "sm:transition-[width] sm:duration-200 sm:ease-linear",
            // "flex h-svh flex-col",
            "group-data-[scroll-locked=1]/body:h-full",
            "has-[main.fixed-main]:group-data-[scroll-locked=1]/body:h-svh"
          )}
        >
          <Layout>
            <LayoutHeader>
              <AuthLayoutHeader/>
            </LayoutHeader>
            {/* View Content */}
            <Outlet />
          </Layout>
        </main>
      </SidebarProvider>
    </div>
  )
}
