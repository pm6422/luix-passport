import { LayoutBody } from "@/layouts/layout-definitions"
import { useState } from "react"
import { useLoginUserStore } from "@/stores/login-user-store"
import { useNavigate } from "react-router-dom"
import { RoleAdmin } from "@/layouts/permission/role-control"
import { SystemStatusBanner } from "./components/system-status-banner"
import { KpiGrid } from "./components/kpi-grid"
import { LoginActivityChart } from "./components/login-activity-chart"
import { MembershipStats } from "./components/membership-stats"
import { AdminTabsSection } from "./components/admin-tabs-section"
import { RecentNotifications } from "./components/recent-notifications"
import { PendingInvitations } from "./components/pending-invitations"

export default function ConsolePage() {
  const navigate = useNavigate()
  const { loginUser } = useLoginUserStore()
  const [apiStatus] = useState<'operational' | 'degraded' | 'offline'>('operational')
  const [lastSync] = useState<string>(new Date().toISOString())

  return (
    <LayoutBody className="flex flex-col space-y-6 pb-10" fixedHeight={false}>
      {/* System Health Banner */}
      <SystemStatusBanner
        apiStatus={apiStatus}
        lastSync={lastSync}
        userTimeFormat={loginUser.timeFormat}
        userTimeZoneId={loginUser.timeZoneId}
        onCreateTeam={() => navigate("/teams")}
      />

      {/* Admin Only Sections */}
      <RoleAdmin>
        {/* KPI Grid */}
        <KpiGrid />

        {/* Analytics Section */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <LoginActivityChart />
          <MembershipStats />
        </div>
      </RoleAdmin>

      {/* Tabs Section for Deep Data - Admin Only + Recent Notifications */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <RoleAdmin>
          <AdminTabsSection />
        </RoleAdmin>

        {/* Recent Notifications Widget */}
        <div className="space-y-6">
          <RecentNotifications />

          <PendingInvitations />
        </div>
      </div>
    </LayoutBody>
  )
}
