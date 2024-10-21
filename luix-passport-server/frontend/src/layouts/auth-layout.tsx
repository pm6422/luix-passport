import { useEffect } from "react"
import { Outlet } from "react-router-dom"
import Sidebar from "@/components/sidebar"
import useIsCollapsed from "@/hooks/use-is-collapsed"
import { useStore } from "exome/react"
import { authUserStore } from "@/stores/auth-user-store.ts"
import { RoleAdmin } from "@/components/custom/role/role-admin"
import { RoleOnlyUser } from "@/components/custom/role/role-only-user"
import { useLocation } from "react-router-dom"
import { CentralTopNav } from "@/components/central-top-nav"
import { NotificationNav } from "@/components/notification-nav.tsx"
import { AccountNav } from "@/components/account-nav.tsx"
import { Layout, LayoutHeader } from "@/layouts/layout-definitions"
// import { Search } from "@/components/custom/search"
import { isEmpty } from "lodash"

export default function AuthLayout() {
  const { authUser } = useStore(authUserStore)
  const [isCollapsed, setIsCollapsed] = useIsCollapsed()
  const location = useLocation()
  const topNav = [
    {
      title: "Overview",
      href: "dashboard/overview",
      isActive: true,
    },
    {
      title: "Customers",
      href: "dashboard/customers",
      isActive: false,
    },
    {
      title: "Products",
      href: "dashboard/products",
      isActive: false,
    },
    {
      title: "Settings",
      href: "dashboard/settings",
      isActive: false,
    },
  ]

  useEffect(() => {
    if(isEmpty(authUser)) {
      console.log("Redirecting to login for null auth user")
      window.location.href = "/login"
    }
  }, [location]);

  return (
    <div className="relative h-full overflow-hidden bg-background">
      <RoleAdmin>
        <Sidebar isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />
      </RoleAdmin>
      <main
        id="content"
        className={`overflow-x-hidden pt-16 transition-[margin] md:overflow-y-hidden md:pt-0 ${!authUser.isAdmin ? "" : isCollapsed ? "md:ml-14" : "md:ml-64"} h-full`}
      >
        <Layout>
          <LayoutHeader>
            <RoleOnlyUser>
              <CentralTopNav links={topNav}/>
            </RoleOnlyUser>
            <div className="ml-auto flex items-center space-x-4">
              {/* <Search /> */}
              <NotificationNav/>
              <AccountNav />
            </div>
          </LayoutHeader>
          {/* ===== View Content ===== */}
          <Outlet />
          {/* ===== View Content ===== */}
        </Layout>
      </main>
    </div>
  )
}

