import { LayoutBody } from "@/layouts/layout-definitions"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button";

export default function Home() {
  return (
    <LayoutBody className="space-y-4">
      <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-2 bg-white">
        {/* Left side - hidden on mobile */}
        <div className="lg:flex relative hidden lg:px-28 ms-10 mb-36">
          <div className="max-md:text-center">
            <img
              src="/assets/images/logos/logo-with-text.svg"
              alt="Logo"
              className="h-14 my-12"
            />
            <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px]">
              Welcome to Our Platform
            </h2>
            <h4 className="mt-6 text-lg">
              Discover amazing features designed to help you succeed.
            </h4>
            <div className="mt-10 flex space-x-4">
              <Button asChild className="px-8 py-4 h-14 text-base rounded-2xl">
                <Link to="/sign-up">Get Started</Link>
              </Button>
              <Button asChild variant="outline" className="px-8 py-4 h-14 text-base rounded-2xl">
                <Link to="/features">Learn More</Link>
              </Button>
            </div>
          </div>
        </div>

        {/* Right side - content */}
        <div className="flex flex-col w-full h-full xl:px-48 lg:px-28 px-5 bg-white rounded-3xl py-10">
          {/* Main content */}
          <main className="flex-1 flex flex-col items-center justify-center text-center">
            <h1 className="text-4xl font-bold mb-6">Welcome Back</h1>
            <p className="text-lg text-muted-foreground mb-10 max-w-md">
              Ready to continue your journey? Sign in to access your dashboard.
            </p>

            <div className="w-full max-w-sm space-y-6">
              <Button asChild className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                <Link to="/login">Sign In</Link>
              </Button>
              <Button asChild variant="outline" className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                <Link to="/sign-up">Create Account</Link>
              </Button>
            </div>
          </main>

          {/* Footer */}
          <footer className="mt-auto pt-10 border-t">
            <div className="flex justify-between items-center">
              <p className="text-sm text-muted-foreground">
                © 2023 Your Company. All rights reserved.
              </p>
              <div className="flex space-x-6">
                <Link to="/terms" className="text-sm text-muted-foreground hover:text-primary">
                  Terms
                </Link>
                <Link to="/privacy" className="text-sm text-muted-foreground hover:text-primary">
                  Privacy
                </Link>
                <Link to="/contact" className="text-sm text-muted-foreground hover:text-primary">
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
