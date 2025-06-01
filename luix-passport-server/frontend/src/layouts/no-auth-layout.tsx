import { Outlet } from "react-router-dom"
import { Layout, LayoutHeader } from "@/layouts/layout-definitions"
import { CentralTopNav } from "@/components/central-top-nav"
import { AccountNav } from "@/components/account-nav"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { Button } from '@/components/custom/button.tsx'

export default function NoAuthLayout() {
  const { loginUser } = useStore(loginUserStore)

  const topNav = [
    {
      title: "Home",
      href: "/",
      isActive: true,
    },
    {
      title: "Features",
      href: "features",
      isActive: false,
    },
    {
      title: "Pricing",
      href: "pricing",
      isActive: false,
    },
    {
      title: "Docs",
      href: "docs",
      isActive: false,
    }
  ]
  const authTopNav = [
    {
      title: "Home",
      href: "/",
      isActive: true,
    },
    {
      title: "Dashboard",
      href: "console",
      isActive: false,
    },
    {
      title: "Features",
      href: "features",
      isActive: false,
    },
    {
      title: "Pricing",
      href: "pricing",
      isActive: false,
    },
    {
      title: "Docs",
      href: "docs",
      isActive: false,
    }
  ]

  return (
    <div className="relative h-full overflow-hidden bg-background">
      <main
        id="content"
        className="h-full"
      >
        <Layout>
          <LayoutHeader>
            <div className="ml-auto flex items-center space-x-4">
              {loginUser.isAuthenticated ? (
                <CentralTopNav links={authTopNav}/>
              ) : (
                <CentralTopNav links={topNav}/>
              )}
              {loginUser.isAuthenticated ? (
                <AccountNav />
              ) : (
                <Button variant="outline" className="rounded-2xl">
                  <a href="/login">Sign In</a>
                </Button>
              )}
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