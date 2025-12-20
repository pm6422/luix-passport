import { LayoutBody } from "@/layouts/layout-definitions"
import { RoleAdmin, RoleUser } from "@/layouts/permission/role-control"
import { SystemStatusBanner } from "./components/system-status-banner"
import { KpiGrid } from "./components/kpi-grid"
import { LoginActivityChart } from "./components/login-activity-chart"
import { MembershipStats } from "./components/membership-stats"
import { SecurityLogsSection } from "./components/security-logs-section"
import { RecentNotifications } from "./components/recent-notifications"
import { PendingInvitations } from "./components/pending-invitations"

export default function ConsolePage() {
  return (
    <LayoutBody className="flex flex-col space-y-6 pb-10" fixedHeight={false}>
      <SystemStatusBanner />

      {/* Admin Role View */}
      <RoleAdmin>
        <KpiGrid />
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <LoginActivityChart />
          <MembershipStats />
        </div>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <SecurityLogsSection />
          <div className="space-y-6">
            <RecentNotifications />
            <PendingInvitations />
          </div>
        </div>
      </RoleAdmin>

      {/* User Role View */}
      <RoleUser>
        <div className="space-y-6">
          <RecentNotifications />
          <PendingInvitations />
        </div>
      </RoleUser>
    </LayoutBody>
  )
}
