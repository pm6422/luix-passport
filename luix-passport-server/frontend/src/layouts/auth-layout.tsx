import { useEffect } from "react"
import { Outlet } from "react-router-dom"
import Sidebar from "@/components/sidebar"
import useIsCollapsed from "@/hooks/use-is-collapsed"
import { appInfoStore } from "@/stores/app-info-store"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { RoleAdmin } from "@/components/custom/role/role-admin"
import { useLocation } from "react-router-dom"
import { CentralTopNav } from "@/components/central-top-nav"
// import { NotificationNav } from "@/components/notification-nav"
import { AccountNav } from "@/components/account-nav"
import { Layout, LayoutHeader } from "@/layouts/layout-definitions"
// import { Search } from "@/components/custom/search"
import { isEmpty } from "lodash"
import { toast } from "sonner"
import { IconBellBolt, IconX } from "@tabler/icons-react"

export default function AuthLayout() {
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
        href: "/console/account",
        isActive: false,
      },
      {
        title: "Notifications",
        href: "/console/notifications",
        isActive: false,
      }
    ] : [])
  ]
  let reconnectAttempts = 0;
  const maxReconnectAttempts = 5;
  const initialReconnectDelay = 1000;

  useEffect(() => {
    if(!loginUser.isAuthenticated) {
      console.log("Redirecting to login for null auth user")
      window.location.href = "/login"
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
        console.log('SSE connection closed');
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
        console.log("Opened SSE connection to the server");
      }
      eventSource.onmessage = function (event) {
        // const data = JSON.parse(event.data);
        // console.log("Received message from the server:", data);
        setTimeout(() => {
          toast.custom(
            (t) => (
              <div className="group w-full max-w-sm rounded-lg border bg-background p-4 shadow-lg">
                <div className="flex items-start gap-3">
                  <IconBellBolt className="mt-0.5 size-5 text-blue-500" />
                  <div className="flex-1">
                    <div className="mb-2 flex items-center justify-between">
                      <h3 className="text-sm font-semibold text-foreground">
                        {event.data}
                      </h3>
                      <button
                        onClick={() => toast.dismiss(t)}
                        className="opacity-0 transition-opacity group-hover:opacity-100 focus:opacity-100 focus:outline-none"
                        aria-label="Close notification"
                      >
                        <IconX className="size-4 text-muted-foreground hover:text-foreground" />
                      </button>
                    </div>
                    <p className="text-sm text-muted-foreground">
                      Please go to{" "}
                      <a
                        href="/console/notifications"
                        className="font-medium text-blue-500 underline-offset-4 hover:text-blue-600 hover:underline"
                      >
                        Notification Center
                      </a>{" "}
                      to check the details.
                    </p>
                  </div>
                </div>
              </div>
            ),
            {
              duration: 5000,
              position: "top-right",
            }
          )
        }, 2000)

        return eventSource;
      }
      eventSource.onerror = () => {
        eventSource.close();

        if (reconnectAttempts < maxReconnectAttempts) {
          const delay = initialReconnectDelay * Math.pow(2, reconnectAttempts);
          reconnectAttempts++;

          console.log(`Disconnected from the server, will try to reconnect in ${delay/1000} seconds with ${reconnectAttempts} attempts...`);

          setTimeout(setupSse, delay);
        } else {
          console.log('Exceeded max reconnect attempts, stop trying to reconnect.');
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

