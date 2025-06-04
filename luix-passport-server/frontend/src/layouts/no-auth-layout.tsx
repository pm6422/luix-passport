import { Outlet } from "react-router-dom"
import { Layout, LayoutHeader } from "@/layouts/layout-definitions"
import { CentralTopNav } from "@/components/central-top-nav"
import { AccountNav } from "@/components/account-nav"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { Button } from "@/components/custom/button"
import { Link } from "react-router-dom"

export default function NoAuthLayout() {
  const { loginUser } = useStore(loginUserStore)

  const topNav = [
    {
      title: "Home",
      href: "/",
      isActive: true,
    },
    ...(loginUser.isAuthenticated ? [{
      title: "Console",
      href: "/console",
      isActive: false,
    }] : []),
    {
      title: "Features",
      href: "/features",
      isActive: false,
    },
    {
      title: "Pricing",
      href: "/pricing",
      isActive: false,
    },
    {
      title: "Docs",
      href: "/docs",
      isActive: false,
    },
    ...(!loginUser.isAuthenticated ? [{
      title: "Contact",
      href: "/contact-us",
      isActive: false,
    }] : [])
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
              <CentralTopNav links={topNav}/>
              {loginUser.isAuthenticated ? (
                <AccountNav />
              ) : (
                <Link to="/sign-in">
                  <Button
                    variant="outline"
                    className="rounded-2xl"
                  >
                    Sign In
                  </Button>
                </Link>
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