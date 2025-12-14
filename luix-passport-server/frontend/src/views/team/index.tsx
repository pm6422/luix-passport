import { useParams, Link, useNavigate } from "react-router-dom"
import { useState, useEffect, useRef, useCallback } from "react"
import { Button } from "@/components/ui/button"
import { LayoutBody } from "@/layouts/layout-definitions"
import { IconUsers, IconCamera, IconAlertCircle, IconCopy, IconCheck, IconTrash, IconSettings } from "@tabler/icons-react"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { TeamService, Team, TeamMember } from "@/services/team-service"
import { toast } from "sonner"
import { useLoginUserStore } from "@/stores/login-user-store"
import { RoleTeamOwner } from "@/permission/role-control"
import { TEAM_OWNER } from "@/permission/roles"
import { ConfirmDialog } from "@/components/custom/dialog/confirm-dialog"
import { InviteMembersDialog } from "./members/dialog/invite-dialog"
import { Card, CardHeader, CardTitle, CardContent, CardDescription, CardFooter } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { Badge } from "@/components/ui/badge"
import AnimatedPathsBackground from "@/components/custom/background/animated-paths-background"

export default function TeamDashboard() {
  const { teamId } = useParams()
  const [team, setTeam] = useState<Team | null>(null)
  const navigate = useNavigate()
  const [avatarUrl, setAvatarUrl] = useState<string>("")
  const fileInputRef = useRef<HTMLInputElement>(null)
  const { hasRole } = useLoginUserStore()

  const [deleteOpen, setDeleteOpen] = useState(false)
  const [inviteOpen, setInviteOpen] = useState(false)
  const [members, setMembers] = useState<TeamMember[]>([])
  const [memberCount, setMemberCount] = useState(0)

  const [copiedId, setCopiedId] = useState(false)

  const loadTeamData = useCallback(async () => {
    if (!teamId) return
    try {
      // Load Team Info
      const { data: teams } = await TeamService.findUserTeams()
      const currentTeam = teams.find(t => t.id === teamId)
      if (currentTeam) {
        setTeam(currentTeam)
        setAvatarUrl(TeamService.getTeamPhoto(teamId) + `?t=${Date.now()}`)
      }

      // Load Members Preview
      const { data: teamMembers } = await TeamService.getMembers(teamId)
      setMembers(teamMembers)
      setMemberCount(teamMembers.length)

    } catch (error) {
      console.error(error)
      // toast.error("Failed to load team data")
    }
  }, [teamId])

  useEffect(() => {
    if (teamId) {
      loadTeamData()
    }
  }, [loadTeamData, teamId])

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

  const copyTeamId = () => {
    if (!teamId) return
    navigator.clipboard.writeText(teamId)
    setCopiedId(true)
    setTimeout(() => setCopiedId(false), 2000)
    toast.success("Team ID copied to clipboard")
  }

  return (
    <LayoutBody className="flex flex-col bg-muted/5" fixedHeight>
      {/* Premium Header */}
      <AnimatedPathsBackground className="bg-background border-b" fullScreen={false}>
        <div className="relative pb-6 pt-8 px-4 md:px-6 shadow-sm">
          <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-primary/50 to-primary/10" />
          <div className="flex flex-col md:flex-row items-center md:items-start gap-6 max-w-screen-2xl mx-auto">
            <div className={`relative group shrink-0 ${hasRole(TEAM_OWNER) ? "cursor-pointer" : "cursor-default"}`} onClick={handleAvatarClick}>
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

            <div className="flex-1 text-center md:text-left space-y-2">
              <h1 className="text-3xl font-bold tracking-tight text-foreground">{team?.name}</h1>
              <div className="flex items-center justify-center md:justify-start gap-2 text-muted-foreground">
                <span className="text-sm">Team ID:</span>
                <code className="bg-muted px-1.5 py-0.5 rounded text-xs font-mono">{teamId}</code>
                <Button variant="ghost" size="icon" className="h-6 w-6" onClick={copyTeamId}>
                  {copiedId ? <IconCheck className="size-3 text-green-500" /> : <IconCopy className="size-3" />}
                </Button>
              </div>
            </div>

            <div className="flex gap-3 mt-4 md:mt-0">
              <Link to={`/teams/${teamId}/members`}>
                <Button variant="outline">Manage Members</Button>
              </Link>
            </div>
          </div>
        </div>
      </AnimatedPathsBackground>

      {/* Main Dashboard Grid */}
      <div className="flex-1 overflow-auto p-4 md:p-6">
        <div className="max-w-screen-2xl mx-auto grid gap-6 md:grid-cols-3">

          {/* Left Column (Main Content) */}
          <div className="md:col-span-2 space-y-6">

            {/* Members Preview Card */}
            <Card>
              <CardHeader className="pb-3 px-4">
                <div className="flex items-center justify-between">
                  <div className="space-y-1">
                    <CardTitle className="text-lg flex items-center gap-2">
                      <IconUsers className="size-5 text-primary" />
                      Team Members <Badge variant="secondary" className="ml-1">{memberCount}</Badge>
                    </CardTitle>
                    <CardDescription>
                      Overview of the team members
                    </CardDescription>
                  </div>
                  <Link to={`/teams/${teamId}/members`} className="text-sm text-primary hover:underline">
                    View all
                  </Link>
                </div>
              </CardHeader>
              <Separator />
              <CardContent className="px-4">
                <div className="grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
                  {members.slice(0, 6).map((member) => (
                    <div key={member.userId} className="flex items-center gap-3 p-2 rounded-lg hover:bg-muted/50 transition-colors">
                      <Avatar className="size-10 border">
                        {member.avatar && <AvatarImage src={`data:image/png;base64,${member.avatar}`} />}
                        <AvatarFallback>{member.firstName?.[0]}{member.lastName?.[0]}</AvatarFallback>
                      </Avatar>
                      <div className="overflow-hidden">
                        <p className="text-sm font-medium truncate">{member.firstName} {member.lastName}</p>
                        <p className="text-xs text-muted-foreground truncate">{member.email}</p>
                      </div>
                    </div>
                  ))}
                  {members.length > 6 && (
                    <div className="flex items-center justify-center p-2 rounded-lg border border-dashed text-muted-foreground text-sm">
                      +{members.length - 6} more members
                    </div>
                  )}
                </div>
                {members.length === 0 && (
                  <div className="text-center py-8 text-muted-foreground">
                    <p>No members found.</p>
                  </div>
                )}
              </CardContent>
              <CardFooter className="bg-muted/20 border-t p-4">
                <RoleTeamOwner>
                  <Button variant="ghost" size="sm" className="w-full sm:w-auto ml-auto" onClick={() => setInviteOpen(true)}>
                    <span className="mr-2">+</span> Add Member
                  </Button>
                </RoleTeamOwner>
              </CardFooter>
            </Card>

            {/* Recent Activity or Stats (Placeholder for future) */}
            {/* You could add a graph or activity log here later */}

          </div>

          {/* Right Column (Sidebar) */}
          <div className="space-y-6">

            {/* Team Settings Card */}
            <Card>
              <CardHeader className="px-4">
                <CardTitle className="text-lg flex items-center gap-2">
                  <IconSettings className="size-5" />
                  Settings
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-4 px-4">
                <div className="space-y-3">
                  <div className="text-sm space-y-1">
                    <p className="font-medium">Visibility</p>
                    <p className="text-muted-foreground text-xs">This team is currently private and visible only to members.</p>
                  </div>
                  <div className="text-sm space-y-1">
                    <p className="font-medium">Role</p>
                    <div className="flex gap-2 flex-wrap">
                      {hasRole(TEAM_OWNER) ? <Badge>Owner</Badge> : <Badge variant="secondary">Member</Badge>}
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* Danger Zone */}
            <RoleTeamOwner>
              <Card className="shadow-none">
                <CardHeader>
                  <CardTitle className="text-lg text-destructive flex items-center gap-2">
                    <IconAlertCircle className="size-5" />
                    Danger Zone
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground mb-4">
                    Deleting the team will permanently remove all data and cannot be undone.
                  </p>
                  <Button variant="destructive" className="w-full" onClick={() => setDeleteOpen(true)}>
                    <IconTrash className="mr-2 size-4" />
                    Delete Team
                  </Button>
                </CardContent>
              </Card>
            </RoleTeamOwner>

          </div>
        </div>
      </div>

      <ConfirmDialog
        open={deleteOpen}
        onOpenChange={setDeleteOpen}
        title="Delete Team"
        description="This action cannot be undone. This will permanently delete the team and remove all member associations."
        onConfirm={handleDeleteTeam}
        confirmText="Delete Team"
      />

      {teamId && (
        <InviteMembersDialog
          open={inviteOpen}
          onOpenChange={setInviteOpen}
          teamId={teamId}
          onSuccess={() => {
            loadTeamData()
          }}
        />
      )}
    </LayoutBody>
  )
}
