import { useRef, useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { IconCamera, IconCopy, IconCheck, IconPencil, IconX, IconPlus } from "@tabler/icons-react"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { TeamService } from "@/services/team-service"
import { type Team } from "@/domains/team"
import { toast } from "sonner"
import { useLoginUserStore } from "@/stores/login-user-store"
import { RoleTeamOwner } from "@/layouts/permission/role-control"
import { ROLE_TEAM_OWNER } from "@/domains/role"
import { Input } from "@/components/ui/input"

interface TeamHeaderProps {
  teamId: string
  onInviteClick: () => void
}

export function TeamHeader({ teamId, onInviteClick }: TeamHeaderProps) {
  const { hasRole } = useLoginUserStore()
  const [team, setTeam] = useState<Team | null>(null)
  const [avatarUrl, setAvatarUrl] = useState<string>(TeamService.getTeamPhoto(teamId) + `?t=${Date.now()}`)
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [copiedId, setCopiedId] = useState(false)
  const [editingName, setEditingName] = useState(false)
  const [newName, setNewName] = useState("")

  useEffect(() => {
    const loadTeam = async () => {
      try {
        const { data: teams } = await TeamService.findUserTeams()
        const currentTeam = teams.find(t => t.id === teamId)
        if (currentTeam) {
          setTeam(currentTeam)
          setNewName(currentTeam.name || "")
        }
      } catch (error) {
        console.error(error)
      }
    }
    loadTeam()
  }, [teamId])

  const handleAvatarClick = () => {
    if (!hasRole(ROLE_TEAM_OWNER)) return
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

  const copyTeamId = () => {
    if (!teamId) return
    navigator.clipboard.writeText(teamId)
    setCopiedId(true)
    setTimeout(() => setCopiedId(false), 2000)
    toast.success("Team ID copied to clipboard")
  }

  const startEditName = () => {
    if (!hasRole(ROLE_TEAM_OWNER)) return
    setNewName(team?.name || "")
    setEditingName(true)
  }

  const cancelEditName = () => {
    setEditingName(false)
    setNewName(team?.name || "")
  }

  const saveTeamName = async () => {
    if (!teamId) return
    try {
      await TeamService.updateTeamName(teamId, (newName || "").trim())
      toast.success("Team name updated")
      setTeam(prev => (prev ? { ...prev, name: (newName || "").trim() } : prev))
      setEditingName(false)
    } catch (error) {
      toast.error("Failed to update team name")
      console.error(error)
    }
  }

  return (
    <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6 bg-secondary/50 dark:bg-gradient-to-r dark:from-primary/10 dark:to-transparent p-6 rounded-2xl border border-border/50 dark:border-primary/10">
      <div className="flex flex-col md:flex-row items-center md:items-start gap-6">
        <div className={`relative group shrink-0 ${hasRole(ROLE_TEAM_OWNER) ? "cursor-pointer" : "cursor-default"}`} onClick={handleAvatarClick}>
          <Avatar className="size-24 border-4 border-background shadow-xl">
            <AvatarImage src={avatarUrl} alt={team?.name || "Team"} />
            <AvatarFallback className="text-2xl bg-primary/10 text-primary">{team?.name?.[0]?.toUpperCase() || "T"}</AvatarFallback>
          </Avatar>
          <RoleTeamOwner>
            <div className="absolute inset-0 flex items-center justify-center bg-black/40 rounded-full opacity-0 group-hover:opacity-100 transition-all duration-200">
              <IconCamera className="size-8 text-white drop-shadow-md" />
            </div>
          </RoleTeamOwner>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            className="hidden"
            onChange={handleFileChange}
          />
        </div>

        <div className="flex-1 text-center md:text-start space-y-2">
          {!editingName ? (
            <div className="flex items-center justify-center md:justify-start gap-2">
              <h1 className="text-3xl font-extrabold tracking-tight text-foreground">{team?.name}</h1>
              <RoleTeamOwner>
                <Button variant="ghost" size="icon" className="size-7" onClick={startEditName}>
                  <IconPencil className="size-4" />
                </Button>
              </RoleTeamOwner>
            </div>
          ) : (
            <div className="flex items-center justify-center md:justify-start gap-2">
              <Input
                value={newName}
                onChange={(e) => setNewName(e.target.value)}
                className="h-9 w-[200px] bg-background/50"
                placeholder="Team name"
              />
              <Button size="sm" className="px-3" onClick={saveTeamName}>
                <IconCheck className="size-4 me-1" />
                Save
              </Button>
              <Button size="sm" variant="outline" className="px-3" onClick={cancelEditName}>
                <IconX className="size-4 me-1" />
                Cancel
              </Button>
            </div>
          )}
          <div className="flex items-center justify-center md:justify-start gap-2 text-muted-foreground">
            <span className="text-sm font-medium opacity-70 italic">Identity Organization</span>
            <span className="mx-2 text-border">|</span>
            <div className="flex items-center gap-1.5">
              <code className="bg-primary/5 px-2 py-0.5 rounded text-[10px] font-mono border border-primary/10">{teamId}</code>
              <Button variant="ghost" size="icon" className="size-6 hover:bg-primary/5" onClick={copyTeamId}>
                {copiedId ? <IconCheck className="size-3 text-emerald-500" /> : <IconCopy className="size-3" />}
              </Button>
            </div>
          </div>
        </div>
      </div>

      <div className="flex items-center gap-2">
        <RoleTeamOwner>
          <Button onClick={onInviteClick} size="sm" className="shadow-lg shadow-primary/20">
            <IconPlus className="mr-2 size-4" />
            Invite Member
          </Button>
        </RoleTeamOwner>
      </div>
    </div>
  )
}
