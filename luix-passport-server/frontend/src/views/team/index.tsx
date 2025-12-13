import { useParams, Link } from "react-router-dom"
import { useState, useEffect, useRef } from "react"
import { Button } from "@/components/ui/button"
import { LayoutBody } from "@/layouts/layout-definitions"
import { IconUsers, IconCamera } from "@tabler/icons-react"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { TeamService, Team } from "@/services/team-service"
import { toast } from "sonner"

export default function TeamDashboard() {
  const { teamId } = useParams()
  const [team, setTeam] = useState<Team | null>(null)
  const [avatarUrl, setAvatarUrl] = useState<string>("")
  const fileInputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    if (teamId) {
      loadTeam()
    }
  }, [teamId])

  const loadTeam = async () => {
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
  }

  const handleAvatarClick = () => {
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
      <div className="flex items-center justify-between p-4 border-b bg-background">
        <div className="flex items-center gap-4">
          <div className="relative group cursor-pointer" onClick={handleAvatarClick}>
            <Avatar className="size-16">
              <AvatarImage src={avatarUrl} alt={team?.name || "Team"} />
              <AvatarFallback>{team?.name?.[0]?.toUpperCase() || "T"}</AvatarFallback>
            </Avatar>
            <div className="absolute inset-0 flex items-center justify-center bg-black/50 rounded-full opacity-0 group-hover:opacity-100 transition-opacity">
              <IconCamera className="size-6 text-white" />
            </div>
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">{team?.name || "Team Dashboard"}</h1>
            <p className="text-sm text-muted-foreground">Click avatar to change</p>
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

      <div className="flex-1 p-4">
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <div className="rounded-xl border bg-card text-card-foreground shadow">
            <div className="p-6 flex flex-row items-center justify-between space-y-0 pb-2">
              <h3 className="tracking-tight text-sm font-medium">Team Members</h3>
              <IconUsers className="size-4 text-muted-foreground" />
            </div>
            <div className="p-6 pt-0">
              <div className="text-2xl font-bold">Manage</div>
              <p className="text-xs text-muted-foreground">
                View and manage team members
              </p>
              <div className="mt-4">
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
