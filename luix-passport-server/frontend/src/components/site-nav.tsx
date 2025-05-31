import { Button } from "@/components/custom/button"
import { Link } from "react-router-dom"
import { AccountNav } from "@/components/account-nav"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"

export function SiteNav() {
  const { loginUser } = useStore(loginUserStore)

  return (
    <header className="flex justify-between items-center mt-5">
      <nav className="flex items-center space-x-6">
        <Link to="/" className="text-sm font-medium hover:text-primary">
          Home
        </Link>
        <Link to="/features" className="text-sm font-medium hover:text-primary">
          Features
        </Link>
        <Link to="/pricing" className="text-sm font-medium hover:text-primary">
          Pricing
        </Link>
        <Link to="/docs" className="text-sm font-medium hover:text-primary">
          Docs
        </Link>
        {loginUser.isAuthenticated ? (
          <AccountNav />
        ) : (
          <Button variant="outline" className="rounded-2xl">
          <a href="/login">Sign In</a>
          </Button>
        )}
      </nav>
    </header>
  )
}
