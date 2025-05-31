import { Outlet } from "react-router-dom"
import { Layout, LayoutHeader } from "@/layouts/layout-definitions"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button.tsx"

export default function NoAuthLayout() {
  return (
    <div className="relative h-full overflow-hidden bg-background">
      <main id="content" className="h-full">
        <Layout>
          <LayoutHeader>
            {/* Header with navigation */}
            <header className="flex justify-between items-center mt-5 w-full">
              <nav className="flex items-center space-x-6 ml-auto">
                <Link to="/features" className="text-sm font-medium hover:text-primary">
                  Features
                </Link>
                <Link to="/pricing" className="text-sm font-medium hover:text-primary">
                  Pricing
                </Link>
                <Link to="/docs" className="text-sm font-medium hover:text-primary">
                  Docs
                </Link>
                <Button asChild variant="outline" className="rounded-2xl">
                  <Link to="/login">Sign In</Link>
                </Button>
              </nav>
            </header>
          </LayoutHeader>
          {/* ===== View Content ===== */}
          <Outlet />
          {/* ===== View Content ===== */}
        </Layout>
      </main>
    </div>
  )
}