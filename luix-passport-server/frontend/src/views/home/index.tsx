import { LayoutBody } from "@/layouts/layout-definitions"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"

export default function Home() {
  const { loginUser } = useStore(loginUserStore)

  return (
    <LayoutBody className="space-y-4">
      <div className="container relative grid h-screen flex-col items-center justify-center bg-white lg:max-w-none lg:grid-cols-2">
        {/* Left side - hidden on mobile */}
        <div className="relative mb-36 ms-10 hidden lg:flex lg:px-28">
          <div className="max-md:text-center">
            <img
              src="/assets/images/logos/logo-with-text.svg"
              alt="Logo"
              className="my-12 h-14"
            />
            <h2 className="text-4xl font-extrabold lg:text-5xl lg:leading-[55px]">
              Welcome to Our Platform
            </h2>
            <h4 className="mt-6 text-lg">
              Discover amazing features designed to help you succeed.
            </h4>
            <div className="mt-10 flex space-x-4">
              <Button asChild className="h-14 rounded-2xl px-8 py-4 text-base">
                <Link to="/sign-up">Get Started</Link>
              </Button>
              <Button
                asChild
                variant="outline"
                className="h-14 rounded-2xl px-8 py-4 text-base"
              >
                <Link to="/features">Learn More</Link>
              </Button>
            </div>
          </div>
        </div>

        {/* Right side - content */}
        <div className="flex h-full w-full flex-col rounded-3xl bg-white px-5 py-10 lg:px-28 xl:px-48">
          {/* Main content */}
          <main className="flex flex-1 flex-col items-center justify-center text-center">
            <h1 className="mb-6 text-4xl font-bold">Welcome Back</h1>
            <p className="mb-10 max-w-md text-lg text-muted-foreground">
              Ready to continue your journey? Sign in to access your dashboard.
            </p>

            <div className="w-full max-w-sm space-y-6">
              <Button
                asChild
                className="h-14 w-full rounded-2xl px-5 py-4 text-base font-medium"
              >
                <a href="/login">Sign In</a>
              </Button>
              <Button
                asChild
                variant="outline"
                className="h-14 w-full rounded-2xl px-5 py-4 text-base font-medium"
              >
                <Link to="/sign-up">Create Account</Link>
              </Button>
            </div>
          </main>

          {/* Footer */}
          <footer className="mt-auto border-t pt-10">
            <div className="flex items-center justify-between">
              <p className="text-sm text-muted-foreground">
                © 2023 Your Company. All rights reserved.
              </p>
              <div className="flex space-x-6">
                <Link
                  to="/terms"
                  className="text-sm text-muted-foreground hover:text-primary"
                >
                  Terms
                </Link>
                <Link
                  to="/privacy"
                  className="text-sm text-muted-foreground hover:text-primary"
                >
                  Privacy
                </Link>
                <Link
                  to="/contact"
                  className="text-sm text-muted-foreground hover:text-primary"
                >
                  Contact
                </Link>
              </div>
            </div>
          </footer>
        </div>
      </div>
    </LayoutBody>
  )
}
