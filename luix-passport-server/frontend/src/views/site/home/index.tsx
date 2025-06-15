import { LayoutBody } from "@/layouts/layout-definitions"
import { SiteFooter } from "@/components/site-footer"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { useEffect, useState } from "react"
import { UserNotificationService } from "@/services/user-notification-service"
import { Badge } from "@/components/ui/badge.tsx"
import { AccountService } from "@/services/account-service"
import AnimatedBackgroundPaths from "@/components/custom/animated-background-paths"
import ColourfulText from "@/components/custom/colourful-text"

export default function SiteHome() {
  const { loginUser } = useStore(loginUserStore)
  const [unreadNotifications, setUnreadNotifications] = useState(0)

  useEffect(() => {
    if(loginUser.isAuthenticated) {
      UserNotificationService.countUnread().then((r) => {
        setUnreadNotifications(r.data)
      })
    }
  }, [loginUser]);

  return (
    <LayoutBody className="space-y-4">
      <AnimatedBackgroundPaths>
        <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-2">
          {/* Left side - hidden on mobile */}
          <div className="lg:flex relative hidden xl:px-28 lg:px-2 ms-10 mb-36 overflow-hidden">
            <div className="max-md:text-center relative z-10">
              <img
                src="/assets/images/logos/logo-with-text.svg"
                alt="Logo"
                className="h-14 my-12"
              />

              {loginUser.isAuthenticated ? (
                <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px] mt-8">
                  <ColourfulText text="Welcome back" />
                </h2>
              ) : (
                <div className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px] mt-8">
                  Unlock Digital Possibilities
                </div>
              )}

              <div className="mt-6 text-lg max-w-md">
                {loginUser.isAuthenticated
                  ? "Your secure gateway to connected services"
                  : "Your passport to seamless authentication"}
              </div>
            </div>
          </div>


          {/* Right side - content */}
          <div className="flex h-full w-full flex-col rounded-3xl px-5 py-10 lg:px-18 xl:px-48">
            <main className="flex-1 flex flex-col items-center justify-center text-center">
              <img
                src="/assets/images/logos/logo-round.svg"
                alt="Logo"
                className="size-16 mb-2 mx-auto animate-pulse"
              />
              {loginUser.isAuthenticated ? (
                <>
                  <h1 className="text-4xl font-bold mb-6">Welcome to LUIX passport</h1>
                  <div className="text-lg text-muted-foreground">
                    Hi {loginUser.username}, you have{" "}
                    <span className="inline-flex items-center">
                      <Badge className="bg-green-600/10 dark:bg-green-600/20 hover:bg-green-600/10 text-green-500 shadow-none rounded-full mx-1">
                        {unreadNotifications}
                      </Badge>
                    </span>{" "}
                        new notifications.
                  </div>
                  <div className="w-full max-w-sm space-y-6">
                    <Button asChild variant="link" className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                      <Link to="/notifications">View Notifications</Link>
                    </Button>
                    <Button asChild className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                      <Link to="/console">Go to console</Link>
                    </Button>
                    <Button variant="secondary" className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl"
                            onClick={() => AccountService.signOut()}>
                      Sign out
                    </Button>
                  </div>
                </>
              ) : (
                <>
                  <h1 className="text-4xl font-bold mb-6">Ready to Continue Your Journey?</h1>
                  <p className="text-lg text-muted-foreground mb-10 max-w-md">
                    Sign in to access your personalized console and features.
                  </p>
                  <div className="w-full max-w-sm space-y-6">
                    <Button asChild className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                      <Link to="/sign-in">Sign In</Link>
                    </Button>
                    <Button asChild variant="secondary" className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                      <Link to="/sign-up">Create Account</Link>
                    </Button>
                  </div>
                </>
              )}
            </main>
            <SiteFooter />
          </div>
        </div>
      </AnimatedBackgroundPaths>
    </LayoutBody>
  )
}