import { useEffect } from "react"
import { Outlet } from "react-router-dom"
import Sidebar from "@/components/sidebar"
import useIsCollapsed from "@/hooks/use-is-collapsed"
import { useStore } from "exome/react"
import { appInfoStore } from "@/stores/app-info-store"
import { authUserStore } from "@/stores/auth-user-store"
import { RoleAdmin } from "@/components/custom/role/role-admin"
import { RoleOnlyUser } from "@/components/custom/role/role-only-user"
import { useLocation } from "react-router-dom"
import { CentralTopNav } from "@/components/central-top-nav"
// import { NotificationNav } from "@/components/notification-nav"
import { AccountNav } from "@/components/account-nav"
import { Layout, LayoutHeader } from "@/layouts/layout-definitions"
// import { Search } from "@/components/custom/search"
import { isEmpty } from "lodash"
import { AccountService } from "@/services/account-service"
import { toast } from "sonner"
import { IconInfoCircle } from "@tabler/icons-react"

export default function AuthLayout() {
  const { appInfo } = useStore(appInfoStore)
  const { authUser, setAuthUser } = useStore(authUserStore)
  const [isCollapsed, setIsCollapsed] = useIsCollapsed()
  const location = useLocation()
  const topNav = [
    {
      title: "Notifications",
      href: "notifications",
      isActive: true,
    },
    {
      title: "Account",
      href: "account-settings",
      isActive: false,
    },
  ]

  useEffect(() => {
    AccountService.getCurrentAccount().then(u => {
      if(isEmpty(u)) {
        console.log("Redirecting to login for empty user")
        window.location.href = "/login"
      } else {
        setAuthUser(u);
      }
    })
  }, [location]);

  useEffect(() => {
    if(isEmpty(authUser)) {
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
  }, [authUser]);

  function setupSse(): EventSource | undefined {
    if (!window.EventSource) {
      toast.error("Your browser does NOT support Server-Sent Event!");
      return undefined;
    } else {
      const eventSource = new EventSource("api/sse/connect");
      eventSource.onopen = function () {
        console.log("Opened SSE connection to the server");
      }
      eventSource.onerror = () => {
        eventSource.close();
        // reconnect after 5 seconds
        setTimeout(setupSse, 5000);
      };
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
                  href="/#/notifications"
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
    }
  }

  return (
    <div className="relative h-full overflow-hidden bg-background">
      <RoleAdmin>
        <Sidebar isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />
      </RoleAdmin>
      <main
        id="content"
        className={`overflow-x-hidden pt-16 transition-[margin] md:overflow-y-hidden md:pt-0 ${!authUser.isAdmin ? "" : isCollapsed ? "md:ml-14" : "md:ml-64"} h-full`}
      >
        <Layout>
          <LayoutHeader>
            <RoleOnlyUser>
              <CentralTopNav links={topNav}/>
            </RoleOnlyUser>
            <div className="ml-auto flex items-center space-x-4">
              {/* <Search /> */}
              {/*<NotificationNav/>*/}
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

