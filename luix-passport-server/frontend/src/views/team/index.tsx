import { useParams } from "react-router-dom"
import { useState } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { RoleTeamOwner } from "@/layouts/permission/role-control"
import { InviteMembersDialog } from "./dialog/invite-dialog"
import { TeamHeader } from "./components/team-header"
import { TeamMembersTable } from "./components/team-members-table"
import { PendingInvitationsTable } from "./components/pending-invitations-table"
import { DangerZone } from "./components/danger-zone"

export default function TeamDashboard() {
  const { teamId } = useParams()
  const [inviteOpen, setInviteOpen] = useState(false)

  if (!teamId) return null

  return (
    <LayoutBody className="flex flex-col space-y-6 pb-10" fixedHeight={false}>
      <TeamHeader
        teamId={teamId}
        onInviteClick={() => setInviteOpen(true)}
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 flex flex-col gap-y-6">
          <TeamMembersTable teamId={teamId} />

          <RoleTeamOwner>
            <PendingInvitationsTable teamId={teamId} />
          </RoleTeamOwner>
        </div>

        <div className="flex flex-col gap-y-6">
          <RoleTeamOwner>
            <DangerZone teamId={teamId} />
          </RoleTeamOwner>
        </div>
      </div>

      <RoleTeamOwner>
        <InviteMembersDialog
          open={inviteOpen}
          onOpenChange={setInviteOpen}
          teamId={teamId}
          onSuccess={() => setInviteOpen(false)}
        />
      </RoleTeamOwner>
    </LayoutBody>
  )
}
