import { Outlet } from "react-router-dom"
import { Layout, LayoutHeader } from "@/layouts/layout-definitions"
import { NoAuthLayoutHeader } from "./no-auth-layout-header"

export default function NoAuthLayout() {
  return (
    <div className="relative h-full overflow-hidden bg-background">
      <main
        id="content"
        className="h-full"
      >
        <Layout>
          <LayoutHeader>
            <NoAuthLayoutHeader />
          </LayoutHeader>
          {/* View Content */}
          <Outlet />
        </Layout>
      </main>
    </div>
  )
}