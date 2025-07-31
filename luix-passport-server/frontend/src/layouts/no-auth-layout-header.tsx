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
      href: "/"
    },
    ...(loginUser.isAuthenticated ? [{
      title: "Console",
      href: "/console"
    }] : []),
    {
      title: "Features",
      href: "/features"
    },
    {
      title: "Pricing",
      href: "/pricing"
    },
    {
      title: "Docs",
      href: "/docs"
    },
    ...(!loginUser.isAuthenticated ? [{
      title: "Contact",
      href: "/contact-us"
    }] : [])
  ]

  return (
    <div className="ml-auto flex items-center space-x-4 lg:pr-2 pr-0">
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