import { useParams, Link } from "react-router-dom"
import { useState, useEffect, useRef, useCallback } from "react"
import { Button } from "@/components/ui/button"
import { LayoutBody } from "@/layouts/layout-definitions"
import { IconUsers, IconCamera } from "@tabler/icons-react"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { TeamService, Team } from "@/services/team-service"
import { toast } from "sonner"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"

export default function TeamDashboard() {
  const { teamId } = useParams()
  const [team, setTeam] = useState<Team | null>(null)
  const [avatarUrl, setAvatarUrl] = useState<string>("")
  const fileInputRef = useRef<HTMLInputElement>(null)
  const { loginUser } = useStore(loginUserStore)
  const [isOwner, setIsOwner] = useState<boolean>(false)

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

  const loadRole = useCallback(async () => {
    if (!teamId || !loginUser?.username) return
    try {
      const { data } = await TeamService.getMembers(teamId)
      const me = data.find(m => m.userId === loginUser.username)
      setIsOwner(me?.role === "OWNER")
    } catch (error) {
      console.error(error)
    }
  }, [teamId, loginUser?.username])

  useEffect(() => {
    if (teamId) {
      loadRole()
    }
  }, [teamId, loadRole])
  const handleAvatarClick = () => {
    if (!isOwner) return
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

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      {/* Header with Team Info */}
      <div className="flex items-center justify-between px-6 py-4 border-b bg-background">
        <div className="flex items-center gap-4">
          <div className={`relative group ${isOwner ? "cursor-pointer" : "cursor-default"}`} onClick={handleAvatarClick}>
            <Avatar className="size-16">
              <AvatarImage src={avatarUrl} alt={team?.name || "Team"} />
              <AvatarFallback>{team?.name?.[0]?.toUpperCase() || "T"}</AvatarFallback>
            </Avatar>
            {isOwner && (
              <div className="absolute inset-0 flex items-center justify-center bg-black/50 rounded-full opacity-0 group-hover:opacity-100 transition-opacity">
                <IconCamera className="size-6 text-white" />
              </div>
            )}
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">{team?.name || ""}</h1>
            {isOwner && <p className="text-sm text-muted-foreground">Click avatar to change</p>}
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
            <div className="rounded-lg border bg-card text-card-foreground shadow-sm hover:shadow-md transition-shadow">
              <div className="p-6 flex flex-row items-center justify-between space-y-0 pb-3">
                <h3 className="tracking-tight text-sm font-medium">Team Members</h3>
                <IconUsers className="size-4 text-muted-foreground" />
              </div>
              <div className="px-6 pb-6">
                <div className="text-2xl font-bold mb-1">Manage</div>
                <p className="text-xs text-muted-foreground mb-4">
                  View and manage team members
                </p>
                <Link to={`/teams/${teamId}/members`}>
                  <Button variant="outline" className="w-full">
                    Go to Members
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </LayoutBody>
  )
}
