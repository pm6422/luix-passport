import { Outlet } from "react-router-dom"
import { Layout, LayoutHeader } from "@/layouts/layout-definitions"
import { SiteNav } from "@/components/site-nav"

export default function NoAuthLayout() {

  return (
    <div className="relative h-full overflow-hidden bg-background">
      <main
        id="content"
        className="h-full"
      >
        <Layout>
          <LayoutHeader>
            <div className="ml-auto flex items-center space-x-4">
              {/*<NotificationNav/>*/}
              <SiteNav />
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