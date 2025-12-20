import { useParams } from "react-router-dom"
import { useState, useEffect, useCallback } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { TeamService } from "@/services/team-service"
import { type Team, type TeamMember, type TeamInvitation } from "@/domains/team"
import { useLoginUserStore } from "@/stores/login-user-store"
import { RoleTeamOwner } from "@/layouts/permission/role-control"
import { ROLE_TEAM_OWNER } from "@/domains/role"
import { InviteMembersDialog } from "./dialog/invite-dialog"
import { TeamHeader } from "./components/team-header"
import { TeamMembersTable } from "./components/team-members-table"
import { PendingInvitationsTable } from "./components/pending-invitations-table"
import { DangerZone } from "./components/danger-zone"

export default function TeamDashboard() {
  const { teamId } = useParams()
  const [team, setTeam] = useState<Team | null>(null)
  const { hasRole } = useLoginUserStore()

  const [inviteOpen, setInviteOpen] = useState(false)
  const [members, setMembers] = useState<TeamMember[]>([])
  const [invitations, setInvitations] = useState<TeamInvitation[]>([])
  const [allRoles, setAllRoles] = useState<string[]>([])

  const loadTeamData = useCallback(async () => {
    if (!teamId) return
    try {
      const { data: teams } = await TeamService.findUserTeams()
      const currentTeam = teams.find(t => t.id === teamId)
      if (currentTeam) {
        setTeam(currentTeam)
      }

      const { data: teamMembers } = await TeamService.getMembers(teamId)
      setMembers(teamMembers)
    } catch (error) {
      console.error(error)
    }
  }, [teamId])

  const loadPendingInvitations = useCallback(async () => {
    if (!teamId) return
    try {
      const { data } = await TeamService.getPendingInvitations(teamId)
      setInvitations(data)
    } catch (error) {
      console.error(error)
    }
  }, [teamId])

  useEffect(() => {
    if (teamId) {
      loadTeamData()
    }
    if (teamId && hasRole(ROLE_TEAM_OWNER)) {
      loadPendingInvitations()
      TeamService.findAllRoleIds().then(({ data }) => setAllRoles(data))
    }
  }, [loadTeamData, teamId, loadPendingInvitations, hasRole])

  if (!teamId) return null

  return (
    <LayoutBody className="flex flex-col space-y-6 pb-10" fixedHeight={false}>
      <TeamHeader
        team={team}
        teamId={teamId}
        onTeamUpdate={loadTeamData}
        onInviteClick={() => setInviteOpen(true)}
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 flex flex-col gap-y-6">
          <TeamMembersTable
            teamId={teamId}
            members={members}
            allRoles={allRoles}
            onMemberUpdated={loadTeamData}
          />

          <RoleTeamOwner>
            <PendingInvitationsTable
              invitations={invitations}
              onInvitationResent={loadPendingInvitations}
            />
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
          onSuccess={() => {
            loadTeamData()
            loadPendingInvitations()
          }}
        />
      </RoleTeamOwner>
    </LayoutBody>
  )
}
