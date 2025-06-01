import { LayoutBody } from "@/layouts/layout-definitions"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"

export default function SiteHome() {
  const { loginUser } = useStore(loginUserStore)

  return (
    <LayoutBody className="space-y-4">
      <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-2">
        {/* Left side - hidden on mobile */}
        <div className="lg:flex relative hidden lg:px-28 ms-10 mb-36">
          <div className="max-md:text-center">
            <img
              src="/assets/images/logos/logo-with-text.svg"
              alt="Logo"
              className="h-14 my-12"
            />
            {loginUser.isAuthenticated ? (
              <div>
                <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px]">
                  Welcome back
                </h2>
              </div>
            ) : (
              <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px]">
                Welcome to Our Platform
              </h2>
            )}
            <h4 className="mt-6 text-lg">
              {loginUser.isAuthenticated
                ? 'Continue your journey with our powerful tools'
                : 'Discover amazing features designed to help you succeed'}
            </h4>
            <div className="mt-10 flex space-x-4">
              {loginUser.isAuthenticated ? (
                <Button asChild className="px-8 py-4 h-14 text-base rounded-2xl">
                  <Link to="/console">Go to Dashboard</Link>
                </Button>
              ) : (
                <>
                  <Button asChild className="px-8 py-4 h-14 text-base rounded-2xl">
                    <Link to="/sign-up">Get Started</Link>
                  </Button>
                  <Button asChild variant="outline" className="px-8 py-4 h-14 text-base rounded-2xl">
                    <Link to="/features">Learn More</Link>
                  </Button>
                </>
              )}
            </div>
          </div>
        </div>

        {/* Right side - content */}
        <div className="flex h-full w-full flex-col rounded-3xl px-5 py-10 lg:px-28 xl:px-48">
          {/* Main content */}
          <main className="flex-1 flex flex-col items-center justify-center text-center">
            {loginUser.isAuthenticated ? (
              <>
                <h1 className="text-4xl font-bold mb-6">Welcome to Your Dashboard</h1>
                <p className="text-lg text-muted-foreground max-w-md">
                  Hi {loginUser.username}<br></br> You have 3 new notifications.
                </p>
                <div className="w-full max-w-sm space-y-6">
                  <Button asChild variant="link" className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                    <Link to="/console/notifications">View Notifications</Link>
                  </Button>
                  <Button asChild className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                    <Link to="/console">Go to Dashboard</Link>
                  </Button>
                </div>
              </>
            ) : (
              <>
                <h1 className="text-4xl font-bold mb-6">Ready to Continue Your Journey?</h1>
                <p className="text-lg text-muted-foreground mb-10 max-w-md">
                  Sign in to access your personalized dashboard and features.
                </p>
                <div className="w-full max-w-sm space-y-6">
                  <Button asChild className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                    <a href="/login">Sign In</a>
                  </Button>
                  <Button asChild variant="outline" className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                    <Link to="/sign-up">Create Account</Link>
                  </Button>
                </div>
              </>
            )}
          </main>
          {/* Footer */}
          <footer className="mt-auto pt-2">
            <div className="flex items-center justify-between">
              <p className="text-sm text-muted-foreground">
                © 2025 LUIX Universe. All rights reserved.
              </p>
              <div className="flex space-x-6">
                <Link
                  to="/terms-of-service"
                  className="text-sm text-muted-foreground hover:text-primary"
                >
                  Terms
                </Link>
                <Link
                  to="/privacy-policy"
                  className="text-sm text-muted-foreground hover:text-primary"
                >
                  Privacy
                </Link>
              </div>
            </div>
          </footer>
        </div>
      </div>
    </LayoutBody>
  )
}
