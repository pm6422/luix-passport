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
import { IconInfoCircle } from "@tabler/icons-react"

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
      // Close connection on component unmount
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
          toast(
            <div className="flex flex-col">
              <div className="flex">
                <IconInfoCircle className="size-4 mr-2"/>
                <span className="font-bold mb-2">{event.data}</span>
              </div>
              <span>
                    Please go to{' '}
                <a
                  href="/console/notifications"
                  className="text-blue-500 hover:text-blue-700 underline underline-offset-4 decoration-blue-300 hover:decoration-blue-500 transition-all font-bold"
                >
                  notification center
                </a>{' '}
                to check.
              </span>
            </div>,
            { duration: 5000 })
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
              <AccountNav />
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

