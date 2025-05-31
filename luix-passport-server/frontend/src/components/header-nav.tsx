import { Button } from "@/components/custom/button"
import { Link } from "react-router-dom"

export function HeaderNav() {

  return (
    <header className="flex justify-between items-center mt-5">
      <nav className="flex items-center space-x-6">
        <Link to="/features" className="text-sm font-medium hover:text-primary">
          Features
        </Link>
        <Link to="/pricing" className="text-sm font-medium hover:text-primary">
          Pricing
        </Link>
        <Link to="/docs" className="text-sm font-medium hover:text-primary">
          Docs
        </Link>
        <Button variant="outline" className="rounded-2xl">
          <Link to="/login">Sign In</Link>
        </Button>
      </nav>
    </header>
  )
}
