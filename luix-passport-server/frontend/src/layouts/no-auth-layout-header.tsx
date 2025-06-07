import { TopNav } from "@/components/top-nav.tsx"
import { AccountNav } from "@/components/account-nav"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { Button } from "@/components/ui/button"
import { Link } from "react-router-dom"

export function NoAuthLayoutHeader() {
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
    <div className="ml-auto flex items-center space-x-4">
      <TopNav links={topNav}/>
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
  )
}