import { useParams, useNavigate } from "react-router-dom"
import { useState, useEffect, useRef, useCallback, useMemo } from "react"
import { Button } from "@/components/ui/button"
import { LayoutBody } from "@/layouts/layout-definitions"
import { IconCamera, IconAlertCircle, IconCopy, IconCheck, IconTrash, IconSettings } from "@tabler/icons-react"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { TeamService, Team, TeamMember } from "@/services/team-service"
import { toast } from "sonner"
import { useLoginUserStore } from "@/stores/login-user-store"
import { RoleTeamOwner } from "@/layouts/permission/role-control"
import { TEAM_OWNER } from "@/domains/role"
import { ConfirmDialog } from "@/components/custom/dialog/confirm-dialog"
import { InviteMembersDialog } from "./dialog/invite-dialog"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import AnimatedPathsBackground from "@/components/custom/background/animated-paths-background"
import { UserDateTime } from "@/components/custom/display/user-date-time"
import { Input } from "@/components/ui/input"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { TeamInvitation } from "@/services/team-service"
import { TeamRoleSelector } from "@/components/custom/form-field/team-role-selector"
import { OverflowBadges } from "@/components/custom/display/overflow-badges"
import { IconPlus } from "@tabler/icons-react"

export default function TeamDashboard() {
  const { teamId } = useParams()
  const [team, setTeam] = useState<Team | null>(null)
  const navigate = useNavigate()
  const [avatarUrl, setAvatarUrl] = useState<string>("")
  const fileInputRef = useRef<HTMLInputElement>(null)
  const { loginUser, hasRole } = useLoginUserStore()

  const [deleteOpen, setDeleteOpen] = useState(false)
  const [inviteOpen, setInviteOpen] = useState(false)
  const [members, setMembers] = useState<TeamMember[]>([])

  // Members & Invitations State
  const [invitations, setInvitations] = useState<TeamInvitation[]>([])
  const [memberQuery, setMemberQuery] = useState("")
  const [inviteQuery, setInviteQuery] = useState("")
  const [allRoles, setAllRoles] = useState<string[]>([])
  const [roleConfirmOpen, setRoleConfirmOpen] = useState(false)
  const [pendingRoleChange, setPendingRoleChange] = useState<{ userId: string, newRoles: string[] } | null>(null)

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

      // Load Members
      const { data: teamMembers } = await TeamService.getMembers(teamId)
      setMembers(teamMembers)

    } catch (error) {
      console.error(error)
    }
  }, [teamId])

  const loadPendingInvitations = useCallback(async () => {
    if (!teamId) return
    try {
      const { data } = await TeamService.getPendingInvitations(teamId);
      setInvitations(data);
    } catch (error) {
      console.error(error)
    }
  }, [teamId])

  useEffect(() => {
    if (teamId) {
      loadTeamData()
    }
    if (teamId && hasRole(TEAM_OWNER)) {
      loadPendingInvitations()
      TeamService.findAllRoleIds().then(({ data }) => setAllRoles(data))
    }
  }, [loadTeamData, teamId, loadPendingInvitations, hasRole])

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
      setTimeout(() => window.location.reload(), 100)
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

  // Members Logic
  const filteredMembers = useMemo(() => {
    const q = memberQuery.trim().toLowerCase()
    return q
      ? members.filter((m) => {
        const values = [m.userId, m.email, m.firstName, m.lastName, m.role]
        return values.filter(Boolean).some((v) => String(v).toLowerCase().includes(q))
      })
      : members
  }, [members, memberQuery])


  const confirmRoleChange = async () => {
    if (!pendingRoleChange || !teamId) return
    try {
      await TeamService.updateMemberRole(teamId, pendingRoleChange.userId, pendingRoleChange.newRoles)
      toast.success("Role updated")
      loadTeamData()
    } catch (error) {
      toast.error("Role update failed")
      console.error(error)
    } finally {
      setRoleConfirmOpen(false)
      setPendingRoleChange(null)
    }
  }

  // Invitation Logic
  const getInviteProgress = (i: TeamInvitation) => {
    if (i.status === "ACCEPTED") return "Accepted"
    if (i.status === "REJECTED") return "Rejected"
    if (i.status === "EXPIRED") return "Expired"
    if (i.status === "REGISTERED") return "Registered"
    return "Unregistered"
  }

  const filteredInvitations = useMemo(() => {
    const q = inviteQuery.trim().toLowerCase()
    return q
      ? invitations.filter((i) => {
        const values = [i.inviteeEmail, i.inviteeRoles, getInviteProgress(i)]
        return values.filter(Boolean).some((v) => String(v).toLowerCase().includes(q))
      })
      : invitations
  }, [invitations, inviteQuery])

  return (
    <LayoutBody className="flex flex-col bg-muted/5" fixedHeight>
      {/* Premium Header */}
      <AnimatedPathsBackground className="bg-background border-b" fullScreen={false}>
        <div className="relative pb-6 pt-8 px-3 md:px-4 shadow-sm">
          <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-primary/50 to-primary/10" />
          <div className="flex flex-col md:flex-row items-center md:items-start gap-6 max-w-[1800px] mx-auto">
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

            {/* <div className="flex gap-3 mt-4 md:mt-0">
              <Link to={`/teams/${teamId}/members`}>
                <Button variant="outline">Manage Members</Button>
              </Link>
            </div> */}
          </div>
        </div>
      </AnimatedPathsBackground>

      {/* Main Dashboard Grid */}
      <div className="flex-1 overflow-auto py-4 md:py-6 px-3 md:px-4">
        <div className="max-w-[1800px] mx-auto grid gap-6 md:grid-cols-3">

          {/* Left Column (Main Content) */}
          <div className="md:col-span-2 space-y-8">

            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div className="space-y-1">
                    <CardTitle>Team Members</CardTitle>
                    <p className="text-sm text-muted-foreground">
                      Manage the members of your team.
                    </p>
                  </div>
                  <div className="flex items-center gap-2">
                    <RoleTeamOwner>
                      <Button onClick={() => setInviteOpen(true)} size="sm">
                        <IconPlus className="mr-2 size-4" />
                        Invite Members
                      </Button>
                    </RoleTeamOwner>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center space-x-2">
                  <Input
                    placeholder="Search members"
                    className="h-9 w-full"
                    value={memberQuery}
                    onChange={(e) => setMemberQuery(e.target.value)}
                  />
                </div>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead className="w-[300px]">Name</TableHead>
                      <TableHead>Email</TableHead>
                      <TableHead>Roles</TableHead>
                      <TableHead className="text-right">Joined at</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredMembers.map((member) => (
                      <TableRow key={member.userId}>
                        <TableCell className="font-medium">
                          <div className="flex items-center space-x-3">
                            <div className="relative">
                              <Avatar className="size-8">
                                {member.avatar && (
                                  <AvatarImage src={`data:image/png;base64,${member.avatar}`} />
                                )}
                                <AvatarFallback>{member.firstName?.[0]}{member.lastName?.[0]}</AvatarFallback>
                              </Avatar>
                              {member.userId === loginUser.username && (
                                <span className="animate-pulse border-background absolute -end-0.5 -bottom-0.5 size-3 rounded-full border-2 bg-emerald-500">
                                  <span className="sr-only">You</span>
                                </span>
                              )}
                            </div>
                            <span>{member.userId}</span>
                          </div>
                        </TableCell>
                        <TableCell>{member.email}</TableCell>
                        <TableCell>
                          <RoleTeamOwner>
                            <div className="flex items-center gap-2">
                              <TeamRoleSelector
                                value={member.roles || [member.role]}
                                allRoles={allRoles}
                                disabled={member.userId === loginUser.username}
                                onChange={(newRoles) => {
                                  setPendingRoleChange({ userId: member.userId, newRoles })
                                  setRoleConfirmOpen(true)
                                }}
                                className="h-8 max-w-[200px]"
                              />
                            </div>
                          </RoleTeamOwner>
                          {!hasRole(TEAM_OWNER) && (
                            <div className="flex gap-2">
                              <Badge variant="outline" className="capitalize">{member.role}</Badge>
                            </div>
                          )}
                        </TableCell>
                        <TableCell className="text-right">
                          {member.joinedAt && <UserDateTime value={member.joinedAt} />}
                        </TableCell>
                      </TableRow>
                    ))}
                    {filteredMembers.length === 0 && (
                      <TableRow>
                        <TableCell colSpan={4} className="h-24 text-center">
                          No members found
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </CardContent>
            </Card>

            <RoleTeamOwner>
              <Card>
                <CardHeader>
                  <div className="space-y-1">
                    <CardTitle>Pending Invites</CardTitle>
                    <p className="text-sm text-muted-foreground">
                      Manage pending invitations.
                    </p>
                  </div>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex items-center space-x-2">
                    <Input
                      placeholder="Search Invitations"
                      className="h-9 w-full"
                      value={inviteQuery}
                      onChange={(e) => setInviteQuery(e.target.value)}
                    />
                  </div>
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Email</TableHead>
                        <TableHead>Roles</TableHead>
                        <TableHead>Invited at</TableHead>
                        <TableHead>Expires at</TableHead>
                        <TableHead>Progress</TableHead>
                        <TableHead className="text-right">Actions</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {filteredInvitations.length === 0 ? (
                        <TableRow>
                          <TableCell colSpan={6} className="h-24 text-center">
                            No pending invitations
                          </TableCell>
                        </TableRow>
                      ) : (
                        filteredInvitations.map((invite) => (
                          <TableRow key={invite.id}>
                            <TableCell>{invite.inviteeEmail}</TableCell>
                            <TableCell>
                              <OverflowBadges
                                items={(invite.inviteeRoles || "").split(",").filter(Boolean)}
                                visibleCount={2}
                                className=""
                                badgeClassName="capitalize"
                                popoverBadgeClassName="capitalize"
                              />
                            </TableCell>
                            <TableCell>
                              <UserDateTime value={invite.createdAt} />
                            </TableCell>
                            <TableCell>
                              <UserDateTime value={invite.expiresAt} />
                            </TableCell>
                            <TableCell>
                              <Badge
                                className={`capitalize ${invite.status === "EXPIRED"
                                  ? "bg-red-600/10 text-red-600 shadow-none"
                                  : ""
                                  }`}
                              >
                                {getInviteProgress(invite)}
                              </Badge>
                            </TableCell>
                            <TableCell className="text-right">
                              {invite.status === "EXPIRED" ? (
                                <Button
                                  variant="outline"
                                  size="sm"
                                  onClick={async () => {
                                    try {
                                      await TeamService.resendInvitation(invite.id)
                                      toast.success("Invitation resent")
                                      loadPendingInvitations()
                                    } catch (error) {
                                      toast.error("Failed to resend invitation")
                                      console.error(error)
                                    }
                                  }}
                                >
                                  Resend
                                </Button>
                              ) : (
                                <span className="text-muted-foreground">—</span>
                              )}
                            </TableCell>
                          </TableRow>
                        ))
                      )}
                    </TableBody>
                  </Table>
                </CardContent>
              </Card>
            </RoleTeamOwner>

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
      )}

      <ConfirmDialog
        open={roleConfirmOpen}
        onOpenChange={setRoleConfirmOpen}
        title="Change Role"
        description="Are you sure you want to change the role for this member?"
        onConfirm={confirmRoleChange}
        onCancel={() => setPendingRoleChange(null)}
      />
    </LayoutBody>
  )
}
