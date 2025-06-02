import { LayoutBody } from "@/layouts/layout-definitions"
import { SiteFooter } from "@/components/site-footer"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { motion } from "framer-motion"
import { useEffect, useState } from 'react'
import { UserNotificationService } from "@/services/user-notification-service"

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
      <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-2">
        {/* Left side - hidden on mobile */}
        <div className="lg:flex relative hidden lg:px-28 ms-10 mb-36 overflow-hidden">
          <div className="max-md:text-center relative z-10">
            <img
              src="/assets/images/logos/logo-with-text.svg"
              alt="Logo"
              className="h-14 my-12"
            />

            {loginUser.isAuthenticated ? (
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5 }}
              >
                <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px] mt-8">
                  Welcome back
                </h2>
              </motion.div>
            ) : (
              <motion.h2
                className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px] mt-8"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.2 }}
              >
                Unlock Digital Possibilities
              </motion.h2>
            )}

            <motion.h4
              className="mt-6 text-lg max-w-md"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.4 }}
            >
              {loginUser.isAuthenticated
                ? 'Your secure gateway to connected services'
                : 'Your passport to seamless authentication'}
            </motion.h4>

            <div className="mt-10 flex space-x-4">
              {/* ... buttons remain the same ... */}
            </div>
          </div>

          {/* Animated background elements */}
          <motion.div
            className="absolute right-0 top-0 w-48 h-48 rounded-full bg-primary/10 blur-xl"
            animate={{
              scale: [1, 1.2, 1],
              opacity: [0.8, 0.5, 0.8]
            }}
            transition={{
              duration: 8,
              repeat: Infinity,
              ease: "easeInOut"
            }}
          />

          {/* Passport stamp animation - moved to top-left */}
          <motion.div
            className="absolute right-0 top-0"
            initial={{ scale: 0, rotate: -15 }}
            animate={{ scale: 0.8, rotate: 0 }}
            transition={{
              type: "spring",
              stiffness: 260,
              damping: 20,
              delay: 0.6
            }}
          >
            <svg width="140" height="140" viewBox="0 0 200 200" className="text-primary">
              <path
                d="M100 0C44.8 0 0 44.8 0 100s44.8 100 100 100 100-44.8 100-100S155.2 0 100 0zm0 20c44.2 0 80 35.8 80 80s-35.8 80-80 80S20 144.2 20 100 55.8 20 100 20z"
                fill="currentColor"
                fillOpacity="0.1"
              />
              <motion.path
                d="M100 40c33.1 0 60 26.9 60 60s-26.9 60-60 60-60-26.9-60-60 26.9-60 60-60z"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeDasharray="0 1"
                initial={{ pathLength: 0 }}
                animate={{ pathLength: 1 }}
                transition={{ duration: 2, delay: 0.8 }}
              />
              <motion.text
                x="100"
                y="110"
                textAnchor="middle"
                fontSize="20"
                fill="currentColor"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 1.2 }}
              >
                {loginUser.isAuthenticated ? 'VERIFIED' : 'SECURE'}
              </motion.text>
            </svg>
          </motion.div>
        </div>

        {/* Right side - content */}
        <div className="flex h-full w-full flex-col rounded-3xl px-5 py-10 lg:px-28 xl:px-48">
          <main className="flex-1 flex flex-col items-center justify-center text-center">
            <img
              src="/assets/images/logos/logo-round.svg"
              alt="Logo"
              className="size-16 mb-2 mx-auto"
            />
            {loginUser.isAuthenticated ? (
              <>
                <h1 className="text-4xl font-bold mb-6">Welcome to Your Console</h1>
                <p className="text-lg text-muted-foreground max-w-md">
                  Hi {loginUser.username}<br></br> You have {unreadNotifications} new notifications.
                </p>
                <div className="w-full max-w-sm space-y-6">
                  <Button asChild variant="link" className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                    <Link to="/console/notifications">View Notifications</Link>
                  </Button>
                  <Button asChild className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                    <Link to="/console">Go to my console</Link>
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
                    <a href="/login">Sign In</a>
                  </Button>
                  <Button asChild variant="outline" className="w-full px-5 py-4 h-14 text-base font-medium rounded-2xl">
                    <Link to="/sign-up">Create Account</Link>
                  </Button>
                </div>
              </>
            )}
          </main>
          <SiteFooter />
        </div>
      </div>
    </LayoutBody>
  )
}