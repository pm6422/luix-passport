import { useEffect } from "react"
import { Outlet } from "react-router-dom"
import Sidebar from "@/components/sidebar"
import useIsCollapsed from "@/hooks/use-is-collapsed"
import { appInfoStore } from "@/stores/app-info-store"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { RoleAdmin } from "@/components/custom/role/role-admin"
import { useLocation } from "react-router-dom"
import { useNavigate } from "react-router-dom"
import { CentralTopNav } from "@/components/central-top-nav"
// import { NotificationNav } from "@/components/notification-nav"
import { AccountNav } from "@/components/account-nav"
import { Layout, LayoutHeader } from "@/layouts/layout-definitions"
// import { Search } from "@/components/custom/search"
import { isEmpty } from "lodash"
import { toast } from "sonner"
import { IconBellBolt } from "@tabler/icons-react"

export default function AuthLayout() {
  const navigate = useNavigate()
  const { appInfo } = useStore(appInfoStore)
  const { loginUser } = useStore(loginUserStore)
  const [isCollapsed, setIsCollapsed] = useIsCollapsed()
  const location = useLocation()
  const topNav = [
    {
      title: "Home",
      href: "/",
      isActive: false,
    },
    ...(loginUser.isOnlyUser ? [
      {
        title: "Console",
        href: "/console",
        isActive: true,
      },
      {
        title: "Personal Account",
        href: "/account",
        isActive: false,
      },
      {
        title: "Notifications",
        href: "/notifications",
        isActive: false,
      }
    ] : [])
  ]
  let reconnectAttempts = 0;
  const maxReconnectAttempts = 5;
  const initialReconnectDelay = 1000;

  useEffect(() => {
    if(!loginUser.isAuthenticated) {
      navigate("/sign-in")
    }
  }, [location, loginUser]);

  useEffect(() => {
    if(isEmpty(loginUser)) {
      return;
    }

    if(appInfo.ribbonProfile === "dev") {
      return;
    }

    const eventSource = setupSse();

    return () => {
      // Close connection on part unmount
      if (eventSource) {
        eventSource.close();
      }
    };
  }, [loginUser]);

  function setupSse(): EventSource | undefined {
    if (!window.EventSource) {
      toast.error("Your browser does NOT support Server-Sent Event!");
      return undefined;
    } else {
      const eventSource = new EventSource("/api/sse/connect");
      eventSource.onopen = function () {
        // reset reconnect attempts
        reconnectAttempts = 0;
      }
      eventSource.onmessage = function (event) {
        // const data = JSON.parse(event.data);
        // console.log("Received message from the server:", data);
        setTimeout(() => {
          toast(
            <div className="flex items-start gap-2">
              <IconBellBolt className="size-8 flex-shrink-0 self-center mr-2 text-primary" />
              <div className="flex flex-col gap-1">
                <span className="font-bold">{event.data}</span>
                  <span>
                    Please go to{' '}
                      <a
                        href="/notifications"
                        className="font-medium text-primary underline-offset-4 hover:underline"
                      >
                        notification center
                      </a>{' '}
                        to check.
                  </span>
                </div>
              </div>,
            { duration: 5000 }
          )
        }, 2000)

        return eventSource;
      }
      eventSource.onerror = () => {
        eventSource.close();

        if (reconnectAttempts < maxReconnectAttempts) {
          const delay = initialReconnectDelay * Math.pow(2, reconnectAttempts);
          reconnectAttempts++;
          setTimeout(setupSse, delay);
        }
      }
    }
  }

  return (
    <div className="relative h-full overflow-hidden bg-background">
      <RoleAdmin>
        <Sidebar isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />
      </RoleAdmin>
      <main
        id="content"
        className={`overflow-x-hidden pt-16 transition-[margin] md:overflow-y-hidden md:pt-0 ${!loginUser.isAdmin ? "" : isCollapsed ? "md:ml-14" : "md:ml-64"} h-full`}
      >
        <Layout>
          <LayoutHeader>
            <div className="ml-auto flex items-center space-x-4">
              <CentralTopNav links={topNav}/>
              {loginUser.isAuthenticated && (
                <AccountNav />
              )}
            </div>
          </LayoutHeader>
          {/* ===== View Content ===== */}
          <Outlet />
          {/* ===== View Content ===== */}
        </Layout>
      </main>
    </div>
  )
}

