import { useParams, useNavigate } from "react-router-dom"
import { useState, useEffect, useRef, useCallback, useMemo } from "react"
import { Button } from "@/components/ui/button"
import { LayoutBody } from "@/layouts/layout-definitions"
import { IconCamera, IconCopy, IconCheck, IconTrash, IconUsers, IconMail, IconPencil, IconX, IconLoader } from "@tabler/icons-react"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { TeamService } from "@/services/team-service"
import { type Team, type TeamMember, type TeamInvitation } from "@/domains/team"
import { toast } from "sonner"
import { useLoginUserStore } from "@/stores/login-user-store"
import { RoleTeamOwner } from "@/layouts/permission/role-control"
import { ROLE_TEAM_OWNER } from "@/domains/role"
import { ConfirmDialog } from "@/components/custom/dialog/confirm-dialog"
import { InviteMembersDialog } from "./dialog/invite-dialog"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
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
  const [editingName, setEditingName] = useState(false)
  const [newName, setNewName] = useState("")

  const loadTeamData = useCallback(async () => {
    if (!teamId) return
    try {
      // Load Team Info
      const { data: teams } = await TeamService.findUserTeams()
      const currentTeam = teams.find(t => t.id === teamId)
      if (currentTeam) {
        setTeam(currentTeam)
        setAvatarUrl(TeamService.getTeamPhoto(teamId) + `?t=${Date.now()}`)
        setNewName(currentTeam.name || "")
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
    if (teamId && hasRole(ROLE_TEAM_OWNER)) {
      loadPendingInvitations()
      TeamService.findAllRoleIds().then(({ data }) => setAllRoles(data))
    }
  }, [loadTeamData, teamId, loadPendingInvitations, hasRole])

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
    <LayoutBody className="flex flex-col space-y-6 pb-10" fixedHeight={false}>
      {/* Premium Header / Banner Style */}
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
            <Button onClick={() => setInviteOpen(true)} size="sm" className="shadow-lg shadow-primary/20">
              <IconPlus className="mr-2 size-4" />
              Invite Member
            </Button>
          </RoleTeamOwner>
        </div>
      </div>

      {/* Main Dashboard Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column (Main Content) */}
        <div className="lg:col-span-2 flex flex-col gap-y-6">
          <Card className="border-none shadow-sm bg-secondary/50 dark:bg-sky-500/5 backdrop-blur-xl">
            <CardHeader className="pb-3 border-b border-white/5">
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle className="text-lg font-bold flex items-center gap-2">
                    <IconUsers className="size-5 text-sky-600 dark:text-sky-400" />
                    Team Members
                  </CardTitle>
                  <p className="text-xs text-muted-foreground mt-0.5">
                    Manage the members of your team.
                  </p>
                </div>
                <div className="w-1/3 max-w-[220px]">
                  <Input
                    placeholder="Search members..."
                    className="h-8 text-xs bg-background/30 border-sky-500/10"
                    value={memberQuery}
                    onChange={(e) => setMemberQuery(e.target.value)}
                  />
                </div>
              </div>
            </CardHeader>
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow className="hover:bg-transparent border-sky-500/10">
                    <TableHead className="pl-6 h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Member</TableHead>
                    <TableHead className="h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Email</TableHead>
                    <TableHead className="h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Authorized Roles</TableHead>
                    <TableHead className="pr-6 h-10 text-right uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Joined at</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredMembers.map((member) => (
                    <TableRow key={member.userId} className="group hover:bg-white/5 border-sky-500/10">
                      <TableCell className="pl-6 py-4">
                        <div className="flex items-center gap-3">
                          <div className="relative">
                            <Avatar className="size-9 border border-sky-500/20 shadow-sm">
                              {member.avatar && (
                                <AvatarImage src={`data:image/png;base64,${member.avatar}`} />
                              )}
                              <AvatarFallback className="bg-sky-500/10 text-sky-600 font-bold">{member.firstName?.[0] || member.userId?.[0]?.toUpperCase()}</AvatarFallback>
                            </Avatar>
                            {member.userId === loginUser.username && (
                              <span className="absolute -right-0.5 -bottom-0.5 shadow-sm border-2 border-background flex h-3 w-3 bg-emerald-500 rounded-full" />
                            )}
                          </div>
                          <span className="text-sm font-semibold text-foreground/90">{member.userId}</span>
                        </div>
                      </TableCell>
                      <TableCell className="py-4 text-xs font-medium text-muted-foreground">{member.email}</TableCell>
                      <TableCell className="py-4">
                        <RoleTeamOwner>
                          <TeamRoleSelector
                            value={member.roles || [member.role]}
                            allRoles={allRoles}
                            disabled={member.userId === loginUser.username}
                            onChange={(newRoles) => {
                              setPendingRoleChange({ userId: member.userId, newRoles })
                              setRoleConfirmOpen(true)
                            }}
                            className="h-8 max-w-[180px] bg-background/50 border-sky-500/20"
                          />
                        </RoleTeamOwner>
                        {!hasRole(ROLE_TEAM_OWNER) && (
                          <div className="flex flex-wrap gap-1.5">
                            {(member.roles || [member.role]).map(r => (
                              <Badge key={r} variant="outline" className="capitalize text-[10px] font-bold bg-sky-500/5 text-sky-600 border-sky-500/20">{r}</Badge>
                            ))}
                          </div>
                        )}
                      </TableCell>
                      <TableCell className="pr-6 py-4 text-right">
                        {member.joinedAt && <UserDateTime value={member.joinedAt} twoLines={true} align="end" className="text-[10px] font-medium opacity-80" />}
                      </TableCell>
                    </TableRow>
                  ))}
                  {filteredMembers.length === 0 && (
                    <TableRow>
                      <TableCell colSpan={4} className="py-12 text-center text-muted-foreground italic text-sm">
                        No team members found.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </CardContent>
          </Card>

          <RoleTeamOwner>
            <Card className="border-none shadow-sm bg-secondary/50 dark:bg-indigo-500/5 backdrop-blur-xl">
              <CardHeader className="pb-3 border-b border-white/5">
                <div className="flex items-center justify-between">
                  <div>
                    <CardTitle className="text-lg font-bold flex items-center gap-2">
                      <IconMail className="size-5 text-indigo-600 dark:text-indigo-400" />
                      Pending Invitations
                    </CardTitle>
                    <p className="text-xs text-muted-foreground mt-0.5">
                      Manage pending team invitations.
                    </p>
                  </div>
                  <div className="w-1/3 max-w-[220px]">
                    <Input
                      placeholder="Search invitations..."
                      className="h-8 text-xs bg-background/30 border-indigo-500/10"
                      value={inviteQuery}
                      onChange={(e) => setInviteQuery(e.target.value)}
                    />
                  </div>
                </div>
              </CardHeader>
              <CardContent className="p-0">
                <Table>
                  <TableHeader>
                    <TableRow className="hover:bg-transparent border-indigo-500/10">
                      <TableHead className="pl-6 h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Recipient</TableHead>
                      <TableHead className="h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Roles</TableHead>
                      <TableHead className="h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Status</TableHead>
                      <TableHead className="pr-6 h-10 text-right uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredInvitations.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={4} className="py-12 text-center text-muted-foreground italic text-sm">
                          No pending invitations found.
                        </TableCell>
                      </TableRow>
                    ) : (
                      filteredInvitations.map((invite) => (
                        <TableRow key={invite.id} className="hover:bg-white/5 border-indigo-500/10">
                          <TableCell className="pl-6 py-4">
                            <span className="text-sm font-semibold">{invite.inviteeEmail}</span>
                          </TableCell>
                          <TableCell className="py-4">
                            <OverflowBadges
                              items={(invite.inviteeRoles || "").split(",").filter(Boolean)}
                              visibleCount={2}
                              badgeClassName="capitalize text-[10px] font-bold bg-indigo-500/5 text-indigo-600 border-indigo-500/10"
                              popoverBadgeClassName="capitalize"
                            />
                          </TableCell>
                          <TableCell className="py-4">
                            <div className="flex flex-col items-start gap-1.5">
                              <Badge
                                variant="secondary"
                                className={`text-[10px] font-bold px-2 py-0.5 uppercase tracking-wide rounded-full ${invite.status === "EXPIRED"
                                  ? "bg-rose-500/10 text-rose-600 border-rose-500/20"
                                  : "bg-emerald-500/10 text-emerald-600 border-emerald-500/20"
                                  }`}
                              >
                                {invite.status !== "EXPIRED" && <IconLoader className="me-1 size-3 animate-spin" />}
                                {getInviteProgress(invite)}
                              </Badge>
                              <span className="text-[10px] text-muted-foreground flex items-center gap-1 opacity-80">
                                Expires at <UserDateTime value={invite.expiresAt} type="datetime" />
                              </span>
                            </div>
                          </TableCell>
                          <TableCell className="pr-6 py-4 text-right">
                            {invite.status === "EXPIRED" && (
                              <Button
                                variant="ghost"
                                size="sm"
                                className="h-7 text-[10px] font-bold text-indigo-600 hover:text-indigo-700 hover:bg-indigo-500/10"
                                onClick={async () => {
                                  try {
                                    await TeamService.resendInvitation(invite.id)
                                    toast.success("Invitation resent successfully")
                                    loadPendingInvitations()
                                  } catch (error) {
                                    toast.error("Failed to resend invitation")
                                  }
                                }}
                              >
                                RESEND
                              </Button>
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
        <div className="flex flex-col gap-y-6">
          {/* Danger Zone */}
          <RoleTeamOwner>
            <Card className="border-none shadow-sm shadow-rose-500/10 bg-secondary/50 dark:bg-rose-500/5 backdrop-blur-xl">
              <CardHeader className="pb-3 border-b border-rose-500/10">
                <CardTitle className="text-sm font-bold text-rose-600 dark:text-rose-400 flex items-center gap-2">
                  <IconTrash className="size-4" />
                  Danger Zone
                </CardTitle>
              </CardHeader>
              <CardContent className="pt-4">
                <p className="text-[10px] font-medium text-rose-600/70 mb-4 leading-relaxed italic">
                  Deleting this identity organization will dissolve the organization and remove all member associations and invitations. Member user accounts and their personal data will remain unaffected.
                </p>
                <Button variant="destructive" className="w-full text-xs font-bold shadow-lg shadow-rose-500/20" onClick={() => setDeleteOpen(true)}>
                  DELETE TEAM
                </Button>
              </CardContent>
            </Card>
          </RoleTeamOwner>
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
            teamId={teamId!}
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
