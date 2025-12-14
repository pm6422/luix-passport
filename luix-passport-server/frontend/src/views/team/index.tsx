import { useParams, Link } from "react-router-dom"
import { useState, useEffect, useRef, useCallback } from "react"
import { Button } from "@/components/ui/button"
import { LayoutBody } from "@/layouts/layout-definitions"
import { IconUsers, IconCamera, IconAlertCircle } from "@tabler/icons-react"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { TeamService, Team } from "@/services/team-service"
import { toast } from "sonner"
import { useLoginUserStore } from "@/stores/login-user-store"
import { RoleTeamOwner } from "@/permission/role-control"
import { TEAM_OWNER } from "@/permission/roles"
import { ConfirmDialog } from "@/components/custom/dialog/confirm-dialog"
import { useNavigate } from "react-router-dom"
export default function TeamDashboard() {
  const { teamId } = useParams()
  const [team, setTeam] = useState<Team | null>(null)
  const [avatarUrl, setAvatarUrl] = useState<string>("")
  const fileInputRef = useRef<HTMLInputElement>(null)
  const { hasRole } = useLoginUserStore()
  const navigate = useNavigate()
  const [deleteOpen, setDeleteOpen] = useState(false)

  const loadTeam = useCallback(async () => {
    if (!teamId) return
    try {
      const { data } = await TeamService.findUserTeams()
      const currentTeam = data.find(t => t.id === teamId)
      if (currentTeam) {
        setTeam(currentTeam)
        setAvatarUrl(TeamService.getTeamPhoto(teamId) + `?t=${Date.now()}`)
      }
    } catch (error) {
      console.error(error)
    }
  }, [teamId])

  useEffect(() => {
    if (teamId) {
      loadTeam()
    }
  }, [loadTeam, teamId])

  const handleAvatarClick = () => {
    if (!hasRole(TEAM_OWNER)) return
    fileInputRef.current?.click()
  }

  const handleFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file || !teamId) return

    try {
      await TeamService.updateTeamPhoto(teamId, file)
      toast.success("Team avatar updated successfully")
      setAvatarUrl(TeamService.getTeamPhoto(teamId) + `?t=${Date.now()}`)
    } catch (error) {
      toast.error("Failed to update team avatar")
      console.error(error)
    }
  }

  const handleDeleteTeam = async () => {
    if (!teamId) return
    try {
      await TeamService.delete(teamId)
      toast.success("Team deleted")
      navigate("/console")
      setTimeout(() => window.location.reload(), 0)
    } catch (error) {
      toast.error("Failed to delete team")
      console.error(error)
    }
  }

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      {/* Header with Team Info */}
      <div className="flex items-center justify-between px-6 py-4 border-b bg-background">
        <div className="flex items-center gap-4">
          <div className={`relative group ${hasRole(TEAM_OWNER) ? "cursor-pointer" : "cursor-default"}`} onClick={handleAvatarClick}>
            <Avatar className="size-16">
              <AvatarImage src={avatarUrl} alt={team?.name || "Team"} />
              <AvatarFallback>{team?.name?.[0]?.toUpperCase() || "T"}</AvatarFallback>
            </Avatar>
            <RoleTeamOwner>
              <div className="absolute inset-0 flex items-center justify-center bg-black/50 rounded-full opacity-0 group-hover:opacity-100 transition-opacity">
                <IconCamera className="size-6 text-white" />
              </div>
            </RoleTeamOwner>
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">{team?.name || ""}</h1>
            <RoleTeamOwner>
              <p className="text-sm text-muted-foreground">Click avatar to change</p>
            </RoleTeamOwner>
          </div>
        </div>
        <input
          ref={fileInputRef}
          type="file"
          accept="image/*"
          className="hidden"
          onChange={handleFileChange}
        />
      </div>

      {/* Main Content */}
      <div className="flex-1 overflow-auto">
        <div className="p-6">
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {/* Team Members Card */}
            <div className="rounded-lg border bg-card text-card-foreground shadow-sm hover:shadow-md transition-shadow h-full flex flex-col">
              <div className="p-6 flex flex-row items-center justify-between space-y-0 pb-3">
                <h3 className="tracking-tight text-sm font-medium">Manage Team Members</h3>
                <IconUsers className="size-4 text-muted-foreground" />
              </div>
              <div className="px-6 pb-6 flex-1 flex flex-col">
                <p className="text-xs text-muted-foreground mb-4">
                  View and manage team members
                </p>
                <Link to={`/teams/${teamId}/members`} className="mt-auto block">
                  <Button variant="outline" className="w-full">
                    Go to Members
                  </Button>
                </Link>
              </div>
            </div>
            <RoleTeamOwner>
              {/* Delete Team Card */}
              <div className="rounded-lg border bg-card text-card-foreground shadow-sm hover:shadow-md transition-shadow h-full flex flex-col">
                <div className="p-6 flex flex-row items-center justify-between space-y-0 pb-3">
                  <h3 className="tracking-tight text-sm font-medium">Danger Zone</h3>
                  <IconAlertCircle className="size-4 text-muted-foreground" />
                </div>
                <div className="px-6 pb-6 flex-1 flex flex-col">
                  <p className="text-xs text-muted-foreground mb-4">
                    Delete this team and all related data
                  </p>
                  <Button variant="destructive" className="w-full mt-auto" onClick={() => setDeleteOpen(true)}>
                    Delete Team
                  </Button>
                </div>
              </div>
            </RoleTeamOwner>
          </div>
        </div>
      </div>
      <ConfirmDialog
        open={deleteOpen}
        onOpenChange={setDeleteOpen}
        title="Delete Team"
        description="This action cannot be undone. Do you want to proceed?"
        onConfirm={handleDeleteTeam}
      />
    </LayoutBody>
  )
}
